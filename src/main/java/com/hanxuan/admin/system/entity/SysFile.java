package com.hanxuan.admin.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文件管理
 *
 * @author hanxuan
 */
@Data
@Schema(description = "文件")
@TableName("sys_file")
public class SysFile {

    @Schema(description = "文件ID")
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    @Schema(description = "原文件名")
    private String originalName;

    @Schema(description = "存储文件名（UUID命名）")
    private String fileName;

    @Schema(description = "文件后缀")
    private String fileSuffix;

    @Schema(description = "文件大小（字节）")
    private Long fileSize;

    @Schema(description = "文件类型（MIME）")
    private String contentType;

    @Schema(description = "存储路径（相对路径）")
    private String filePath;

    @Schema(description = "存储类型：local-本地")
    private String storageType;

    @Schema(description = "上传人ID")
    private Long createBy;

    @Schema(description = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @Schema(description = "逻辑删除")
    @TableLogic
    private Integer deleted;
}
