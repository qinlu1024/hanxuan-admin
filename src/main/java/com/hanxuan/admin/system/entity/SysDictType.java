package com.hanxuan.admin.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 字典类型
 *
 * @author hanxuan
 */
@Data
@Schema(description = "字典类型")
@TableName("sys_dict_type")
public class SysDictType {

    @Schema(description = "字典类型ID")
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    @Schema(description = "字典名称")
    private String dictName;

    @Schema(description = "字典类型（唯一标识）")
    private String dictType;

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
