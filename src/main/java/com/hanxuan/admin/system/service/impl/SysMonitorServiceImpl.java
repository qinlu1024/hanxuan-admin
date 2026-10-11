package com.hanxuan.admin.system.service.impl;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.date.DateUtil;
import com.hanxuan.admin.system.service.SysMonitorService;
import com.hanxuan.admin.system.vo.OnlineUserVO;
import com.hanxuan.admin.system.vo.ServerInfoVO;
import org.springframework.stereotype.Service;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * 服务监控服务实现
 *
 * @author hanxuan
 */
@Service
public class SysMonitorServiceImpl implements SysMonitorService {

    private static final double GB = 1024D * 1024 * 1024;
    private static final double MB = 1024D * 1024;

    @Override
    public ServerInfoVO getServerInfo() {
        ServerInfoVO vo = new ServerInfoVO();
        vo.setCpu(buildCpuInfo());
        vo.setMem(buildMemInfo());
        vo.setJvm(buildJvmInfo());
        vo.setSys(buildSysInfo());
        vo.setDisks(buildDiskInfos());
        return vo;
    }

    @Override
    public List<OnlineUserVO> listOnlineUsers() {
        List<OnlineUserVO> list = new ArrayList<>();
        // 以拼接 key 的方式获取 token 存储前缀，避免硬编码
        String prefix = StpUtil.stpLogic.splicingKeyTokenValue("");
        List<String> keys = SaManager.getSaTokenDao().searchData(prefix, "", 0, 1000, true);
        for (String key : keys) {
            String token = key.substring(prefix.length());
            Object loginId = StpUtil.getLoginIdByToken(token);
            if (loginId == null) {
                continue;
            }
            SaSession session = StpUtil.getSessionByLoginId(loginId);
            OnlineUserVO user = new OnlineUserVO();
            user.setTokenValue(token);
            user.setUserId(Long.valueOf(loginId.toString()));
            user.setUsername(session.getString("username"));
            user.setNickname(session.getString("nickname"));
            user.setLoginIp(session.getString("loginIp"));
            user.setBrowser(session.getString("browser"));
            user.setOs(session.getString("os"));
            user.setLoginTime(session.getString("loginTime"));
            user.setActiveTimeout(StpUtil.stpLogic.getTokenActiveTimeoutByToken(token));
            list.add(user);
        }
        // 按登录时间倒序
        list.sort(Comparator.comparing(OnlineUserVO::getLoginTime, Comparator.nullsLast(Comparator.reverseOrder())));
        return list;
    }

    @Override
    public void forceLogout(String tokenValue) {
        StpUtil.logoutByTokenValue(tokenValue);
    }

    /**
     * CPU 信息（使用率采样两次，首次调用返回值不准确）
     */
    private ServerInfoVO.CpuInfo buildCpuInfo() {
        ServerInfoVO.CpuInfo cpu = new ServerInfoVO.CpuInfo();
        cpu.setCpuNum(Runtime.getRuntime().availableProcessors());
        java.lang.management.OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        if (osBean instanceof com.sun.management.OperatingSystemMXBean sunOsBean) {
            double load = sunOsBean.getCpuLoad();
            // 首次采样常返回 -1 或 0，间隔后重新采样
            if (load <= 0) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException ignored) {
                    Thread.currentThread().interrupt();
                }
                load = sunOsBean.getCpuLoad();
            }
            cpu.setUsed(round(Math.max(load, 0) * 100));
        }
        return cpu;
    }

    /**
     * 物理内存信息
     */
    private ServerInfoVO.MemInfo buildMemInfo() {
        ServerInfoVO.MemInfo mem = new ServerInfoVO.MemInfo();
        java.lang.management.OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        if (osBean instanceof com.sun.management.OperatingSystemMXBean sunOsBean) {
            double total = sunOsBean.getTotalMemorySize();
            double free = sunOsBean.getFreeMemorySize();
            mem.setTotal(round(total / GB));
            mem.setFree(round(free / GB));
            mem.setUsed(round((total - free) / GB));
            mem.setUsage(total > 0 ? round((total - free) / total * 100) : 0);
        }
        return mem;
    }

    /**
     * JVM 信息
     */
    private ServerInfoVO.JvmInfo buildJvmInfo() {
        ServerInfoVO.JvmInfo jvm = new ServerInfoVO.JvmInfo();
        Runtime runtime = Runtime.getRuntime();
        double total = runtime.totalMemory();
        double max = runtime.maxMemory();
        double free = runtime.freeMemory();
        jvm.setTotal(round(total / MB));
        jvm.setMax(round(max / MB));
        jvm.setUsed(round((total - free) / MB));
        jvm.setUsage(total > 0 ? round((total - free) / total * 100) : 0);
        jvm.setName(System.getProperty("java.vm.name"));
        jvm.setVersion(System.getProperty("java.version"));

        java.lang.management.RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        Date startTime = new Date(runtimeMXBean.getStartTime());
        jvm.setStartTime(DateUtil.formatDateTime(startTime));
        jvm.setRunTime(formatDuration(runtimeMXBean.getUptime()));
        return jvm;
    }

    /**
     * 服务器系统信息
     */
    private ServerInfoVO.SysInfo buildSysInfo() {
        ServerInfoVO.SysInfo sys = new ServerInfoVO.SysInfo();
        try {
            sys.setComputerName(InetAddress.getLocalHost().getHostName());
        } catch (Exception e) {
            sys.setComputerName("未知");
        }
        sys.setOsName(System.getProperty("os.name"));
        sys.setOsArch(System.getProperty("os.arch"));
        sys.setUserDir(System.getProperty("user.dir"));
        return sys;
    }

    /**
     * 磁盘信息（Windows 遍历盘符，Linux 为挂载根目录）
     */
    private List<ServerInfoVO.DiskInfo> buildDiskInfos() {
        List<ServerInfoVO.DiskInfo> disks = new ArrayList<>();
        for (File root : File.listRoots()) {
            long total = root.getTotalSpace();
            if (total <= 0) {
                continue;
            }
            long free = root.getFreeSpace();
            ServerInfoVO.DiskInfo disk = new ServerInfoVO.DiskInfo();
            disk.setDirName(root.getPath());
            disk.setTotal(round(total / GB));
            disk.setFree(round(free / GB));
            disk.setUsed(round((total - free) / GB));
            disk.setUsage(round((double) (total - free) / total * 100));
            disks.add(disk);
        }
        return disks;
    }

    private double round(double value) {
        return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    /**
     * 毫秒时长格式化为 "x天x小时x分钟"
     */
    private String formatDuration(long millis) {
        long days = millis / (24 * 3600_000L);
        long hours = millis % (24 * 3600_000L) / 3600_000L;
        long minutes = millis % 3600_000L / 60_000L;
        StringBuilder sb = new StringBuilder();
        if (days > 0) {
            sb.append(days).append("天");
        }
        if (hours > 0 || days > 0) {
            sb.append(hours).append("小时");
        }
        sb.append(minutes).append("分钟");
        return sb.toString();
    }
}
