package com.hanxuan.admin.common.aspect;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.hanxuan.admin.common.annotation.OperationLog;
import com.hanxuan.admin.common.utils.IpUtils;
import com.hanxuan.admin.system.entity.SysOperationLog;
import com.hanxuan.admin.system.service.SysOperationLogService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * 操作日志切面
 *
 * @author hanxuan
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class OperationLogAspect {

    private final SysOperationLogService operationLogService;

    @Around("@annotation(operationLog)")
    public Object around(ProceedingJoinPoint point, OperationLog operationLog) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = null;
        Exception exception = null;

        try {
            result = point.proceed();
            return result;
        } catch (Exception e) {
            exception = e;
            throw e;
        } finally {
            // 异步保存日志
            try {
                saveLog(point, operationLog, result, exception, System.currentTimeMillis() - startTime);
            } catch (Exception e) {
                log.error("保存操作日志失败", e);
            }
        }
    }

    @Async
    public void saveLog(ProceedingJoinPoint point, OperationLog operationLog,
                        Object result, Exception exception, long costTime) {
        try {
            HttpServletRequest request = getRequest();
            if (request == null) {
                return;
            }

            SysOperationLog logEntity = new SysOperationLog();

            // 操作模块/类型/描述
            logEntity.setModule(operationLog.module());
            logEntity.setOperationType(operationLog.type());
            logEntity.setDescription(operationLog.description());

            // 请求信息
            logEntity.setRequestMethod(request.getMethod());
            logEntity.setRequestUrl(request.getRequestURI());

            // 请求参数
            if (operationLog.saveRequestParams()) {
                try {
                    MethodSignature signature = (MethodSignature) point.getSignature();
                    String[] paramNames = signature.getParameterNames();
                    Object[] args = point.getArgs();
                    if (paramNames != null && args != null) {
                        StringBuilder params = new StringBuilder("{");
                        for (int i = 0; i < paramNames.length; i++) {
                            if (isSensitiveParam(paramNames[i])) {
                                params.append("\"").append(paramNames[i]).append("\":\"***\",");
                            } else {
                                try {
                                    String val = args[i] == null ? "null" : JSONUtil.toJsonStr(args[i]);
                                    // 限制参数长度
                                    if (val.length() > 2000) {
                                        val = val.substring(0, 2000) + "...";
                                    }
                                    params.append("\"").append(paramNames[i]).append("\":").append(val).append(",");
                                } catch (Exception e) {
                                    params.append("\"").append(paramNames[i]).append("\":\"[复杂对象]\",");
                                }
                            }
                        }
                        if (params.length() > 1) {
                            params.deleteCharAt(params.length() - 1);
                        }
                        params.append("}");
                        logEntity.setRequestParams(params.toString());
                    }
                } catch (Exception e) {
                    logEntity.setRequestParams("获取参数失败");
                }
            }

            // 响应结果
            if (operationLog.saveResponseResult() && result != null) {
                try {
                    String resp = JSONUtil.toJsonStr(result);
                    if (resp.length() > 2000) {
                        resp = resp.substring(0, 2000) + "...";
                    }
                    logEntity.setResponseResult(resp);
                } catch (Exception e) {
                    logEntity.setResponseResult("获取响应失败");
                }
            }

            // 操作人
            try {
                if (StpUtil.isLogin()) {
                    logEntity.setUserId(StpUtil.getLoginIdAsLong());
                    // 从 StpUtil 的 session 或 token 里拿用户名
                    Object usernameObj = StpUtil.getSession().get("username");
                    if (usernameObj != null) {
                        logEntity.setUsername(usernameObj.toString());
                    }
                }
            } catch (Exception ignored) {
                // 未登录也允许记录（如登录接口）
            }

            // IP 和设备信息
            logEntity.setOperationIp(IpUtils.getIpAddr(request));
            logEntity.setBrowser(IpUtils.getBrowser(request));
            logEntity.setOs(IpUtils.getOs(request));

            // 执行状态
            if (exception != null) {
                logEntity.setStatus(0);
                String errMsg = exception.getMessage();
                if (StrUtil.isNotBlank(errMsg) && errMsg.length() > 500) {
                    errMsg = errMsg.substring(0, 500);
                }
                logEntity.setErrorMsg(errMsg);
            } else {
                logEntity.setStatus(1);
            }

            logEntity.setCostTime(costTime);
            logEntity.setOperationTime(LocalDateTime.now());

            operationLogService.saveLog(logEntity);
        } catch (Exception e) {
            log.error("异步保存操作日志异常", e);
        }
    }

    private boolean isSensitiveParam(String paramName) {
        return paramName.contains("password")
                || paramName.contains("pwd")
                || paramName.contains("secret")
                || paramName.contains("token");
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
}
