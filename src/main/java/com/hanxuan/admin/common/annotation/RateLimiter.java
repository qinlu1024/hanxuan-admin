package com.hanxuan.admin.common.annotation;

import java.lang.annotation.*;

/**
 * 接口限流注解
 * 在需要限流的 Controller 方法上使用，基于单机内存固定窗口计数
 *
 * @author hanxuan
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimiter {

    /**
     * 时间窗口内允许的最大请求次数
     */
    int count() default 10;

    /**
     * 时间窗口大小（秒）
     */
    int seconds() default 60;

    /**
     * 限流维度
     */
    LimitDimension dimension() default LimitDimension.IP;

    /**
     * 限流提示信息（为空时使用默认提示）
     */
    String message() default "";

    /**
     * 限流维度
     */
    enum LimitDimension {
        /**
         * 按客户端 IP 限流
         */
        IP,
        /**
         * 按登录用户限流（未登录时退化为按 IP）
         */
        USER
    }
}
