package com.hanxuan.admin.common.annotation;

import java.lang.annotation.*;

/**
 * 数据权限注解
 * 在查询方法上使用，自动根据当前用户角色的数据权限范围注入过滤条件
 *
 * @author hanxuan
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataScope {

    /**
     * 部门ID字段名（数据库表字段名）
     */
    String deptId() default "dept_id";

    /**
     * 用户ID字段名（数据库表字段名，用于"仅本人"）
     */
    String userId() default "create_by";
}
