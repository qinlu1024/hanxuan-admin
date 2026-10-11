package com.hanxuan.admin.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 部门
 *
 * @author hanxuan
 */
@Data
@Schema(description = "部门")
@TableName("sys_dept")
public class SysDept {

    @Schema(description = "部门ID")
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    @Schema(description = "父部门ID")
    private Long parentId;

    @Schema(description = "祖级列表（逗号分隔）")
    private String ancestors;

    @Schema(description = "部门名称")
    private String deptName;

    @Schema(description = "显示顺序")
    private Integer sort;

    @Schema(description = "负责人")
    private String leader;

    @Schema(description = "联系电话")
    private String phone;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "状态 1-正常 0-停用")
    private Integer status;

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
