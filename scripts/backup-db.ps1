# 编码要求：本文件必须保存为 UTF-8 with BOM（Windows PowerShell 5.1 按 BOM 识别编码，否则中文会乱码）
<#
.SYNOPSIS
    晗煊后台管理系统 PostgreSQL 数据库定时备份脚本（Windows / Docker 环境）

.DESCRIPTION
    1. 在 postgres 容器内用 pg_dump 自定义格式(-F c)导出数据库到容器 /tmp
    2. 通过 docker cp 取回宿主机备份目录（避免 PowerShell 重定向损坏二进制）
    3. 删除容器内临时文件
    4. 自动清理超过保留天数的旧备份
    5. 全程写入日志文件

    由 Windows 计划任务 HanxuanAdminDbBackup 每日调用（S4U，注销后也可运行）：
    powershell -NoProfile -ExecutionPolicy Bypass -File 本脚本路径

.PARAMETER RetainDays
    备份保留天数，默认 14 天

.PARAMETER BackupDir
    宿主机备份目录，默认位于 OneDrive 同步盘：E:\Documents\OneDrive\HanxuanAdminBackups
#>

param(
    [int]$RetainDays = 14,
    [string]$BackupDir = 'E:\Documents\OneDrive\HanxuanAdminBackups'
)

# ---------- 基本配置 ----------
$ContainerName = 'hanxuan-postgres'
$DbUser        = 'postgres'
$DbName        = 'hanxuan_admin'
$LogDir        = Join-Path $BackupDir 'logs'

# ---------- 初始化目录 ----------
if (-not (Test-Path $BackupDir)) {
    New-Item -ItemType Directory -Path $BackupDir -Force | Out-Null
}
if (-not (Test-Path $LogDir)) {
    New-Item -ItemType Directory -Path $LogDir -Force | Out-Null
}

$timestamp = Get-Date -Format 'yyyyMMdd-HHmmss'
$backupName = "hanxuan_admin-$timestamp.dump"
$containerTmp = "/tmp/$backupName"
$hostFile = Join-Path $BackupDir $backupName
$logFile = Join-Path $LogDir "backup-$timestamp.log"

function Write-Log {
    param([string]$Message)
    $line = "[{0}] {1}" -f (Get-Date -Format 'yyyy-MM-dd HH:mm:ss'), $Message
    Write-Host $line
    Add-Content -Path $logFile -Value $line -Encoding UTF8
}

try {
    Write-Log '====== 数据库备份开始 ======'
    Write-Log "备份文件: $hostFile"

    # 0. 检查容器是否运行
    $running = docker inspect -f '{{.State.Running}}' $ContainerName 2>$null
    if ($running -ne 'true') {
        throw "容器 $ContainerName 未运行（状态: $running），备份中止"
    }

    # 1. 容器内导出（自定义格式，支持 pg_restore 选择性恢复）
    Write-Log '步骤 1/4：容器内 pg_dump 导出 ...'
    docker exec $ContainerName pg_dump -U $DbUser -d $DbName -F c -f $containerTmp
    if ($LASTEXITCODE -ne 0) {
        throw 'pg_dump 执行失败'
    }

    # 2. 复制到宿主机
    Write-Log '步骤 2/4：docker cp 取回备份 ...'
    docker cp "${ContainerName}:$containerTmp" $hostFile
    if ($LASTEXITCODE -ne 0 -or -not (Test-Path $hostFile)) {
        throw 'docker cp 失败，宿主机上未生成备份文件'
    }

    # 3. 清理容器内临时文件
    Write-Log '步骤 3/4：清理容器临时文件 ...'
    docker exec $ContainerName rm -f $containerTmp | Out-Null

    # 4. 校验文件大小并清理过期备份
    $size = (Get-Item $hostFile).Length
    if ($size -lt 1024) {
        throw "备份文件异常过小（$size 字节），疑似导出失败"
    }
    Write-Log ('备份成功，文件大小: {0:N2} MB' -f ($size / 1MB))

    Write-Log "步骤 4/4：清理 $RetainDays 天前的旧备份 ..."
    $cutoff = (Get-Date).AddDays(-$RetainDays)
    $oldFiles = Get-ChildItem -Path $BackupDir -Filter 'hanxuan_admin-*.dump' -File |
        Where-Object { $_.LastWriteTime -lt $cutoff }
    if ($oldFiles) {
        $oldFiles | ForEach-Object {
            Remove-Item $_.FullName -Force
            Write-Log "已删除过期备份: $($_.Name)"
        }
    } else {
        Write-Log '无过期备份需要清理'
    }

    Write-Log '====== 数据库备份完成 ======'
    exit 0
}
catch {
    Write-Log "备份失败: $($_.Exception.Message)"
    # 失败时尽量清理宿主机残留半成品
    if (Test-Path $hostFile) {
        Remove-Item $hostFile -Force -ErrorAction SilentlyContinue
    }
    exit 1
}
