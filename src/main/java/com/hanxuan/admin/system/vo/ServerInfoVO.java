package com.hanxuan.admin.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 服务器监控信息 VO
 *
 * @author hanxuan
 */
@Data
@Schema(description = "服务器监控信息")
public class ServerInfoVO {

    @Schema(description = "CPU 信息")
    private CpuInfo cpu;

    @Schema(description = "内存信息")
    private MemInfo mem;

    @Schema(description = "JVM 信息")
    private JvmInfo jvm;

    @Schema(description = "服务器系统信息")
    private SysInfo sys;

    @Schema(description = "磁盘信息列表")
    private List<DiskInfo> disks;

    @Data
    @Schema(description = "CPU 信息")
    public static class CpuInfo {
        @Schema(description = "核心数")
        private int cpuNum;
        @Schema(description = "CPU 总使用率（%）")
        private double used;
    }

    @Data
    @Schema(description = "内存信息")
    public static class MemInfo {
        @Schema(description = "总内存（GB）")
        private double total;
        @Schema(description = "已使用（GB）")
        private double used;
        @Schema(description = "剩余（GB）")
        private double free;
        @Schema(description = "使用率（%）")
        private double usage;
    }

    @Data
    @Schema(description = "JVM 信息")
    public static class JvmInfo {
        @Schema(description = "JVM 总内存（MB）")
        private double total;
        @Schema(description = "JVM 最大可用内存（MB）")
        private double max;
        @Schema(description = "JVM 已使用（MB）")
        private double used;
        @Schema(description = "JVM 使用率（%）")
        private double usage;
        @Schema(description = "JDK 名称")
        private String name;
        @Schema(description = "JDK 版本")
        private String version;
        @Schema(description = "启动时间")
        private String startTime;
        @Schema(description = "运行时长")
        private String runTime;
    }

    @Data
    @Schema(description = "服务器系统信息")
    public static class SysInfo {
        @Schema(description = "主机名")
        private String computerName;
        @Schema(description = "操作系统")
        private String osName;
        @Schema(description = "系统架构")
        private String osArch;
        @Schema(description = "项目路径")
        private String userDir;
    }

    @Data
    @Schema(description = "磁盘信息")
    public static class DiskInfo {
        @Schema(description = "盘符/挂载点")
        private String dirName;
        @Schema(description = "总容量（GB）")
        private double total;
        @Schema(description = "已使用（GB）")
        private double used;
        @Schema(description = "剩余（GB）")
        private double free;
        @Schema(description = "使用率（%）")
        private double usage;
    }
}
