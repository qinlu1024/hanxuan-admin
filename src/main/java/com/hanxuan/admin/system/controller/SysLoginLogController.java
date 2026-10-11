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
import com.hanxuan.admin.system.entity.SysLoginLog;
import com.hanxuan.admin.system.service.SysLoginLogService;
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
 * 登录日志控制器
 *
 * @author hanxuan
 */
@Tag(name = "登录日志")
@RestController
@RequestMapping("/monitor/loginLog")
@RequiredArgsConstructor
public class SysLoginLogController {

    private final SysLoginLogService loginLogService;

    @GetMapping("/page")
    @Operation(summary = "分页查询登录日志")
    @SaCheckPermission("monitor:loginLog:list")
    public Result<IPage<SysLoginLog>> page(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "用户名") @RequestParam(required = false) String username,
            @Parameter(description = "状态 1-成功 0-失败") @RequestParam(required = false) Integer status) {
        return Result.success(loginLogService.page(pageNum, pageSize, username, status));
    }

    @GetMapping("/export")
    @Operation(summary = "导出登录日志")
    @SaCheckPermission("monitor:loginLog:list")
    @RateLimiter(count = 5, seconds = 60, dimension = RateLimiter.LimitDimension.USER)
    @OperationLog(module = "登录日志", type = "导出", description = "导出登录日志")
    public void export(
            @Parameter(description = "用户名") @RequestParam(required = false) String username,
            @Parameter(description = "状态 1-成功 0-失败") @RequestParam(required = false) Integer status,
            HttpServletResponse response) throws IOException {
        List<SysLoginLog> list = loginLogService.listForExport(username, status);

        // 组装导出行（中文字段头）
        List<Map<String, Object>> rows = new ArrayList<>(list.size());
        for (SysLoginLog log : list) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("用户名", log.getUsername());
            row.put("登录IP", log.getLoginIp());
            row.put("浏览器", log.getBrowser());
            row.put("操作系统", log.getOs());
            row.put("登录状态", log.getStatus() != null && log.getStatus() == 1 ? "成功" : "失败");
            row.put("失败原因", log.getFailReason());
            row.put("登录时间", log.getLoginTime() == null ? "" : DateUtil.formatLocalDateTime(log.getLoginTime()));
            rows.add(row);
        }

        ExcelWriter writer = ExcelUtil.getWriter(true);
        writer.write(rows, true);
        ExcelUtils.writeToResponse(response, "登录日志", writer);
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询登录日志详情")
    @SaCheckPermission("monitor:loginLog:list")
    public Result<SysLoginLog> getById(@PathVariable Long id) {
        return Result.success(loginLogService.getById(id));
    }

    @DeleteMapping("/batch")
    @Operation(summary = "批量删除登录日志")
    @SaCheckPermission("monitor:loginLog:delete")
    @OperationLog(module = "登录日志", type = "删除", description = "批量删除登录日志")
    public Result<Void> deleteBatch(@RequestBody List<Long> ids) {
        loginLogService.deleteBatch(ids);
        return Result.success();
    }

    @DeleteMapping("/clean")
    @Operation(summary = "清空登录日志")
    @SaCheckPermission("monitor:loginLog:delete")
    @OperationLog(module = "登录日志", type = "清空", description = "清空登录日志")
    public Result<Void> clean() {
        loginLogService.clean();
        return Result.success();
    }
}
