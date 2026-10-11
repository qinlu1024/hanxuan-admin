package com.hanxuan.admin.system.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hanxuan.admin.common.annotation.OperationLog;
import com.hanxuan.admin.common.result.Result;
import com.hanxuan.admin.system.entity.SysRole;
import com.hanxuan.admin.system.service.SysRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色管理控制器
 *
 * @author hanxuan
 */
@Tag(name = "角色管理")
@RestController
@RequestMapping("/system/role")
@RequiredArgsConstructor
public class SysRoleController {

    private final SysRoleService sysRoleService;

    @GetMapping("/page")
    @Operation(summary = "分页查询角色")
    @SaCheckPermission("system:role:list")
    public Result<IPage<SysRole>> page(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "搜索关键词") @RequestParam(required = false) String keyword) {
        return Result.success(sysRoleService.page(pageNum, pageSize, keyword));
    }

    @GetMapping("/list")
    @Operation(summary = "获取所有角色列表")
    @SaCheckPermission("system:role:list")
    public Result<List<SysRole>> list() {
        return Result.success(sysRoleService.list());
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询角色")
    @SaCheckPermission("system:role:list")
    public Result<SysRole> getById(@PathVariable Long id) {
        return Result.success(sysRoleService.getById(id));
    }

    @PostMapping
    @Operation(summary = "新增角色")
    @SaCheckPermission("system:role:add")
    @OperationLog(module = "角色管理", type = "新增", description = "新增角色")
    public Result<Void> add(@RequestBody SysRole role) {
        sysRoleService.addRole(role);
        return Result.success();
    }

    @PutMapping
    @Operation(summary = "修改角色")
    @SaCheckPermission("system:role:edit")
    @OperationLog(module = "角色管理", type = "修改", description = "修改角色")
    public Result<Void> update(@RequestBody SysRole role) {
        sysRoleService.updateRole(role);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除角色")
    @SaCheckPermission("system:role:delete")
    @OperationLog(module = "角色管理", type = "删除", description = "删除角色")
    public Result<Void> delete(@PathVariable Long id) {
        sysRoleService.deleteRole(id);
        return Result.success();
    }

    @DeleteMapping("/batch")
    @Operation(summary = "批量删除角色")
    @SaCheckPermission("system:role:delete")
    @OperationLog(module = "角色管理", type = "删除", description = "批量删除角色")
    public Result<Void> deleteBatch(@RequestBody List<Long> ids) {
        sysRoleService.deleteBatch(ids);
        return Result.success();
    }

    @GetMapping("/{id}/menuIds")
    @Operation(summary = "获取角色菜单ID列表")
    @SaCheckPermission("system:role:list")
    public Result<List<Long>> getMenuIdsByRoleId(@PathVariable Long id) {
        return Result.success(sysRoleService.getMenuIdsByRoleId(id));
    }

    @PostMapping("/{id}/menus")
    @Operation(summary = "分配角色菜单")
    @SaCheckPermission("system:role:edit")
    @OperationLog(module = "角色管理", type = "分配权限", description = "分配角色菜单权限")
    public Result<Void> assignMenus(@PathVariable Long id, @RequestBody List<Long> menuIds) {
        sysRoleService.assignMenus(id, menuIds);
        return Result.success();
    }
}
