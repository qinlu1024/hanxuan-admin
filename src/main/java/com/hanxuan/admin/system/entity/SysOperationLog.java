package com.hanxuan.admin.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 操作日志
 *
 * @author hanxuan
 */
@Data
@Schema(description = "操作日志")
@TableName("sys_operation_log")
public class SysOperationLog {

    @Schema(description = "日志ID")
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    @Schema(description = "操作模块")
    private String module;

    @Schema(description = "操作类型")
    private String operationType;

    @Schema(description = "操作描述")
    private String description;

    @Schema(description = "请求方式")
    private String requestMethod;

    @Schema(description = "请求URL")
    private String requestUrl;

    @Schema(description = "请求参数")
    private String requestParams;

    @Schema(description = "响应结果")
    private String responseResult;

    @Schema(description = "操作人ID")
    private Long userId;

    @Schema(description = "操作人用户名")
    private String username;

    @Schema(description = "操作IP")
    private String operationIp;

    @Schema(description = "操作地点")
    private String operationLocation;

    @Schema(description = "浏览器")
    private String browser;

    @Schema(description = "操作系统")
    private String os;

    @Schema(description = "执行状态 1-成功 0-失败")
    private Integer status;

    @Schema(description = "错误消息")
    private String errorMsg;

    @Schema(description = "执行耗时（毫秒）")
    private Long costTime;

    @Schema(description = "操作时间")
    private LocalDateTime operationTime;

    @Schema(description = "逻辑删除")
    @TableLogic
    private Integer deleted;
}
