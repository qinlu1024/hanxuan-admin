package com.hanxuan.admin.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 字典数据
 *
 * @author hanxuan
 */
@Data
@Schema(description = "字典数据")
@TableName("sys_dict_data")
public class SysDictData {

    @Schema(description = "字典数据ID")
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    @Schema(description = "字典排序")
    private Integer sort;

    @Schema(description = "字典标签")
    private String dictLabel;

    @Schema(description = "字典键值")
    private String dictValue;

    @Schema(description = "字典类型")
    private String dictType;

    @Schema(description = "样式属性（其他样式扩展）")
    private String cssClass;

    @Schema(description = "表格回显样式")
    private String listClass;

    @Schema(description = "是否默认 1-是 0-否")
    private Integer isDefault;

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
