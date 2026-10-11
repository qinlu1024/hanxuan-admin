package com.hanxuan.admin.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色表
 *
 * @author hanxuan
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_role")
public class SysRole extends BaseEntity {

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 角色标识
     */
    private String roleCode;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 状态 1-正常 0-禁用
     */
    private Integer status;

    /**
     * 数据权限范围：1-全部数据 2-本部门及以下 3-本部门 4-仅本人
     */
    private Integer dataScope;

    /**
     * 备注
     */
    private String remark;
}
