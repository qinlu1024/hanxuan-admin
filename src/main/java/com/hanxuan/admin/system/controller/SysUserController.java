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
import com.hanxuan.admin.system.entity.SysUser;
import com.hanxuan.admin.system.service.SysUserService;
import com.hanxuan.admin.system.vo.UserVO;
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
 * 用户管理控制器
 *
 * @author hanxuan
 */
@Tag(name = "用户管理")
@RestController
@RequestMapping("/system/user")
@RequiredArgsConstructor
public class SysUserController {

    private final SysUserService sysUserService;

    @GetMapping("/page")
    @Operation(summary = "分页查询用户")
    @SaCheckPermission("system:user:list")
    public Result<IPage<UserVO>> page(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "搜索关键词") @RequestParam(required = false) String keyword,
            @Parameter(description = "部门ID") @RequestParam(required = false) Long deptId) {
        return Result.success(sysUserService.page(pageNum, pageSize, keyword, deptId));
    }

    @GetMapping("/export")
    @Operation(summary = "导出用户列表")
    @SaCheckPermission("system:user:export")
    @RateLimiter(count = 5, seconds = 60, dimension = RateLimiter.LimitDimension.USER)
    @OperationLog(module = "用户管理", type = "导出", description = "导出用户列表")
    public void export(
            @Parameter(description = "搜索关键词") @RequestParam(required = false) String keyword,
            @Parameter(description = "部门ID") @RequestParam(required = false) Long deptId,
            HttpServletResponse response) throws IOException {
        List<UserVO> list = sysUserService.listForExport(keyword, deptId);

        // 组装导出行（中文字段头）
        List<Map<String, Object>> rows = new ArrayList<>(list.size());
        for (UserVO u : list) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("用户名", u.getUsername());
            row.put("昵称", u.getNickname());
            row.put("手机号", u.getPhone());
            row.put("邮箱", u.getEmail());
            row.put("性别", switch (u.getGender() == null ? 0 : u.getGender()) {
                case 1 -> "男";
                case 2 -> "女";
                default -> "未知";
            });
            row.put("部门", u.getDeptName());
            row.put("状态", u.getStatus() != null && u.getStatus() == 1 ? "正常" : "禁用");
            row.put("备注", u.getRemark());
            row.put("创建时间", u.getCreateTime() == null ? "" : DateUtil.formatLocalDateTime(u.getCreateTime()));
            rows.add(row);
        }

        ExcelWriter writer = ExcelUtil.getWriter(true);
        writer.write(rows, true);
        ExcelUtils.writeToResponse(response, "用户列表", writer);
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询用户")
    @SaCheckPermission("system:user:list")
    public Result<UserVO> getById(@PathVariable Long id) {
        return Result.success(sysUserService.getUserVOById(id));
    }

    @PostMapping
    @Operation(summary = "新增用户")
    @SaCheckPermission("system:user:add")
    @OperationLog(module = "用户管理", type = "新增", description = "新增用户")
    public Result<Void> add(@RequestBody SysUser user) {
        sysUserService.addUser(user);
        return Result.success();
    }

    @PutMapping
    @Operation(summary = "修改用户")
    @SaCheckPermission("system:user:edit")
    @OperationLog(module = "用户管理", type = "修改", description = "修改用户")
    public Result<Void> update(@RequestBody SysUser user) {
        sysUserService.updateUser(user);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除用户")
    @SaCheckPermission("system:user:delete")
    @OperationLog(module = "用户管理", type = "删除", description = "删除用户")
    public Result<Void> delete(@PathVariable Long id) {
        sysUserService.deleteUser(id);
        return Result.success();
    }

    @DeleteMapping("/batch")
    @Operation(summary = "批量删除用户")
    @SaCheckPermission("system:user:delete")
    @OperationLog(module = "用户管理", type = "删除", description = "批量删除用户")
    public Result<Void> deleteBatch(@RequestBody List<Long> ids) {
        sysUserService.deleteBatch(ids);
        return Result.success();
    }

    @PutMapping("/resetPassword")
    @Operation(summary = "重置密码")
    @SaCheckPermission("system:user:edit")
    @OperationLog(module = "用户管理", type = "重置密码", description = "重置用户密码")
    public Result<Void> resetPassword(
            @Parameter(description = "用户ID") @RequestParam Long id,
            @Parameter(description = "新密码") @RequestParam String password) {
        sysUserService.resetPassword(id, password);
        return Result.success();
    }

    @PutMapping("/status")
    @Operation(summary = "修改用户状态")
    @SaCheckPermission("system:user:edit")
    @OperationLog(module = "用户管理", type = "修改状态", description = "修改用户状态")
    public Result<Void> updateStatus(
            @Parameter(description = "用户ID") @RequestParam Long id,
            @Parameter(description = "状态 1-正常 0-禁用") @RequestParam Integer status) {
        sysUserService.updateStatus(id, status);
        return Result.success();
    }

    @GetMapping("/{id}/roles")
    @Operation(summary = "获取用户角色ID列表")
    @SaCheckPermission("system:user:list")
    public Result<List<Long>> getRoleIds(@PathVariable Long id) {
        return Result.success(sysUserService.getRoleIdsByUserId(id));
    }

    @PostMapping("/{id}/roles")
    @Operation(summary = "分配用户角色")
    @SaCheckPermission("system:user:edit")
    @OperationLog(module = "用户管理", type = "分配角色", description = "分配用户角色")
    public Result<Void> assignRoles(@PathVariable Long id, @RequestBody List<Long> roleIds) {
        sysUserService.assignRoles(id, roleIds);
        return Result.success();
    }
}
