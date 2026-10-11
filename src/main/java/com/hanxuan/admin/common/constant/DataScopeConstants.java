package com.hanxuan.admin.common.constant;

/**
 * 数据权限常量
 *
 * @author hanxuan
 */
public interface DataScopeConstants {

    /**
     * 全部数据权限
     */
    Integer DATA_SCOPE_ALL = 1;

    /**
     * 本部门及以下数据权限
     */
    Integer DATA_SCOPE_DEPT_AND_CHILD = 2;

    /**
     * 本部门数据权限
     */
    Integer DATA_SCOPE_DEPT = 3;

    /**
     * 仅本人数据权限
     */
    Integer DATA_SCOPE_SELF = 4;

    /**
     * 部门ID字段名（默认）
     */
    String DEPT_ID = "dept_id";

    /**
     * 创建人字段名（默认）
     */
    String CREATE_BY = "create_by";
}
