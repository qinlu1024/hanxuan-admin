package com.hanxuan.admin.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 登录日志
 *
 * @author hanxuan
 */
@Data
@Schema(description = "登录日志")
@TableName("sys_login_log")
public class SysLoginLog {

    @Schema(description = "日志ID")
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "登录IP")
    private String loginIp;

    @Schema(description = "登录地点")
    private String loginLocation;

    @Schema(description = "浏览器")
    private String browser;

    @Schema(description = "操作系统")
    private String os;

    @Schema(description = "登录状态 1-成功 0-失败")
    private Integer status;

    @Schema(description = "失败原因")
    private String failReason;

    @Schema(description = "登录时间")
    private LocalDateTime loginTime;

    @Schema(description = "逻辑删除")
    @TableLogic
    private Integer deleted;
}
