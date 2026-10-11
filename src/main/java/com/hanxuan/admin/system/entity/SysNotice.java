package com.hanxuan.admin.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 通知公告
 *
 * @author hanxuan
 */
@Data
@Schema(description = "通知公告")
@TableName("sys_notice")
public class SysNotice {

    @Schema(description = "公告ID")
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    @Schema(description = "公告标题")
    private String noticeTitle;

    @Schema(description = "公告类型 1-通知 2-公告")
    private Integer noticeType;

    @Schema(description = "公告内容")
    private String noticeContent;

    @Schema(description = "状态 1-已发布 0-关闭")
    private Integer status;

    @Schema(description = "创建人ID")
    private Long createBy;

    @Schema(description = "创建人名称（回填，非数据库字段）")
    @TableField(exist = false)
    private String createByName;

    @Schema(description = "备注")
    private String remark;

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
