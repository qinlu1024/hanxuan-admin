package com.hanxuan.admin.common.aspect;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import com.hanxuan.admin.common.annotation.RateLimiter;
import com.hanxuan.admin.common.enums.ResultCode;
import com.hanxuan.admin.common.exception.BusinessException;
import com.hanxuan.admin.common.utils.IpUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 接口限流切面
 * 单机内存固定窗口计数实现，按 IP 或登录用户 + 接口方法 维度统计
 *
 * @author hanxuan
 */
@Slf4j
@Aspect
@Component
public class RateLimiterAspect {

    /**
     * 窗口计数器缓存，key = 维度值:类名.方法名
     */
    private final Map<String, WindowCounter> counterMap = new ConcurrentHashMap<>();

    /**
     * 缓存上限，防止极端情况下内存膨胀
     */
    private static final int MAX_CACHE_SIZE = 10000;

    @Around("@annotation(rateLimiter)")
    public Object around(ProceedingJoinPoint point, RateLimiter rateLimiter) throws Throwable {
        HttpServletRequest request = getRequest();
        String key = buildKey(point, rateLimiter, request);
        long windowMillis = rateLimiter.seconds() * 1000L;

        // 缓存过大时清理过期窗口，避免内存膨胀
        if (counterMap.size() > MAX_CACHE_SIZE) {
            long now = System.currentTimeMillis();
            counterMap.entrySet().removeIf(e -> now - e.getValue().getWindowStart() > windowMillis);
        }

        WindowCounter counter = counterMap.computeIfAbsent(key, k -> new WindowCounter());
        long current = counter.incrementAndGet(windowMillis);
        if (current > rateLimiter.count()) {
            String message = StrUtil.isNotBlank(rateLimiter.message())
                    ? rateLimiter.message()
                    : ResultCode.TOO_MANY_REQUESTS.getMessage();
            log.warn("接口限流触发: key={}, 窗口{}秒内第{}次请求（上限{}次）",
                    key, rateLimiter.seconds(), current, rateLimiter.count());
            throw new BusinessException(ResultCode.TOO_MANY_REQUESTS.getCode(), message);
        }
        return point.proceed();
    }

    /**
     * 构建限流 key：维度值:全限定方法名
     */
    private String buildKey(ProceedingJoinPoint point, RateLimiter rateLimiter, HttpServletRequest request) {
        String dimensionValue;
        if (rateLimiter.dimension() == RateLimiter.LimitDimension.USER) {
            dimensionValue = getLoginIdOrIp(request);
        } else {
            dimensionValue = IpUtils.getIpAddr(request);
        }
        MethodSignature signature = (MethodSignature) point.getSignature();
        String methodName = signature.getDeclaringTypeName() + "." + signature.getName();
        return dimensionValue + ":" + methodName;
    }

    /**
     * 获取登录用户ID，未登录时退化为 IP
     */
    private String getLoginIdOrIp(HttpServletRequest request) {
        try {
            if (StpUtil.isLogin()) {
                return "user:" + StpUtil.getLoginIdAsLong();
            }
        } catch (Exception ignored) {
        }
        return "ip:" + IpUtils.getIpAddr(request);
    }

    private HttpServletRequest getRequest() {
        try {
            ServletRequestAttributes attributes =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            return attributes != null ? attributes.getRequest() : null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 固定窗口计数器
     */
    private static class WindowCounter {

        private volatile long windowStart = System.currentTimeMillis();
        private final AtomicLong count = new AtomicLong(0);

        /**
         * 窗口内计数 +1，过期则重置窗口
         */
        public synchronized long incrementAndGet(long windowMillis) {
            long now = System.currentTimeMillis();
            if (now - windowStart >= windowMillis) {
                windowStart = now;
                count.set(0);
            }
            return count.incrementAndGet();
        }

        public long getWindowStart() {
            return windowStart;
        }
    }
}
