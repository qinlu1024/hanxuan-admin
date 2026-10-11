package com.hanxuan.admin.system.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import com.hanxuan.admin.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 健康检查
 *
 * @author hanxuan
 */
@Tag(name = "系统健康检查")
@RestController
public class HealthController {

    @SaIgnore
    @GetMapping("/health")
    @Operation(summary = "健康检查")
    public Result<Map<String, Object>> health() {
        Map<String, Object> data = new HashMap<>();
        data.put("status", "UP");
        data.put("app", "hanxuan-admin");
        data.put("time", LocalDateTime.now());
        return Result.success(data);
    }
}
