package com.hanxuan.admin.system.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.hutool.core.date.DateUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hanxuan.admin.common.annotation.OperationLog;
import com.hanxuan.admin.common.annotation.RateLimiter;
import com.hanxuan.admin.common.result.Result;
import com.hanxuan.admin.common.utils.ExcelUtils;
import com.hanxuan.admin.system.entity.SysOperationLog;
import com.hanxuan.admin.system.service.SysOperationLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 操作日志控制器
 *
 * @author hanxuan
 */
@Tag(name = "操作日志")
@RestController
@RequestMapping("/monitor/operationLog")
@RequiredArgsConstructor
public class SysOperationLogController {

    private final SysOperationLogService operationLogService;

    @GetMapping("/page")
    @Operation(summary = "分页查询操作日志")
    @SaCheckPermission("monitor:operationLog:list")
    public Result<IPage<SysOperationLog>> page(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "操作模块") @RequestParam(required = false) String module,
            @Parameter(description = "操作类型") @RequestParam(required = false) String operationType,
            @Parameter(description = "状态 1-成功 0-失败") @RequestParam(required = false) Integer status,
            @Parameter(description = "操作人") @RequestParam(required = false) String username) {
        return Result.success(operationLogService.page(pageNum, pageSize, module, operationType, status, username));
    }

    @GetMapping("/export")
    @Operation(summary = "导出操作日志")
    @SaCheckPermission("monitor:operationLog:list")
    @RateLimiter(count = 5, seconds = 60, dimension = RateLimiter.LimitDimension.USER)
    @OperationLog(module = "操作日志", type = "导出", description = "导出操作日志")
    public void export(
            @Parameter(description = "操作模块") @RequestParam(required = false) String module,
            @Parameter(description = "操作类型") @RequestParam(required = false) String operationType,
            @Parameter(description = "状态 1-成功 0-失败") @RequestParam(required = false) Integer status,
            @Parameter(description = "操作人") @RequestParam(required = false) String username,
            HttpServletResponse response) throws IOException {
        List<SysOperationLog> list = operationLogService.listForExport(module, operationType, status, username);

        // 组装导出行（中文字段头）
        List<Map<String, Object>> rows = new ArrayList<>(list.size());
        for (SysOperationLog log : list) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("操作模块", log.getModule());
            row.put("操作类型", log.getOperationType());
            row.put("操作描述", log.getDescription());
            row.put("请求方式", log.getRequestMethod());
            row.put("请求URL", log.getRequestUrl());
            row.put("操作人", log.getUsername());
            row.put("操作IP", log.getOperationIp());
            row.put("浏览器", log.getBrowser());
            row.put("操作系统", log.getOs());
            row.put("执行状态", log.getStatus() != null && log.getStatus() == 1 ? "成功" : "失败");
            row.put("耗时(ms)", log.getCostTime());
            row.put("操作时间", log.getOperationTime() == null ? "" : DateUtil.formatLocalDateTime(log.getOperationTime()));
            rows.add(row);
        }

        ExcelWriter writer = ExcelUtil.getWriter(true);
        writer.write(rows, true);
        ExcelUtils.writeToResponse(response, "操作日志", writer);
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询操作日志详情")
    @SaCheckPermission("monitor:operationLog:list")
    public Result<SysOperationLog> getById(@PathVariable Long id) {
        return Result.success(operationLogService.getById(id));
    }

    @DeleteMapping("/batch")
    @Operation(summary = "批量删除操作日志")
    @SaCheckPermission("monitor:operationLog:delete")
    @OperationLog(module = "操作日志", type = "删除", description = "批量删除操作日志")
    public Result<Void> deleteBatch(@RequestBody List<Long> ids) {
        operationLogService.deleteBatch(ids);
        return Result.success();
    }

    @DeleteMapping("/clean")
    @Operation(summary = "清空操作日志")
    @SaCheckPermission("monitor:operationLog:delete")
    @OperationLog(module = "操作日志", type = "清空", description = "清空操作日志")
    public Result<Void> clean() {
        operationLogService.clean();
        return Result.success();
    }
}
