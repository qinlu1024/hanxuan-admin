package com.hanxuan.admin.common.annotation;

import java.lang.annotation.*;

/**
 * 操作日志注解
 * 在需要记录日志的 Controller 方法上使用
 *
 * @author hanxuan
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperationLog {

    /**
     * 操作模块
     */
    String module() default "";

    /**
     * 操作类型（如：新增、修改、删除、查询等）
     */
    String type() default "";

    /**
     * 操作描述
     */
    String description() default "";

    /**
     * 是否保存请求参数
     */
    boolean saveRequestParams() default true;

    /**
     * 是否保存响应结果
     */
    boolean saveResponseResult() default false;
}
