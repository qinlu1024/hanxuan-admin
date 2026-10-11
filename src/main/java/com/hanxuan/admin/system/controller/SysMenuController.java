package com.hanxuan.admin.system.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import com.hanxuan.admin.common.result.Result;
import com.hanxuan.admin.system.entity.SysMenu;
import com.hanxuan.admin.system.service.SysMenuService;
import com.hanxuan.admin.system.vo.MenuTreeVO;
import com.hanxuan.admin.system.vo.RouterVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜单管理控制器
 *
 * @author hanxuan
 */
@Tag(name = "菜单管理")
@RestController
@RequestMapping("/system/menu")
@RequiredArgsConstructor
public class SysMenuController {

    private final SysMenuService sysMenuService;

    @GetMapping("/tree")
    @Operation(summary = "查询菜单树")
    @SaCheckPermission("system:menu:list")
    public Result<List<MenuTreeVO>> tree(
            @Parameter(description = "搜索关键词") @RequestParam(required = false) String keyword) {
        return Result.success(sysMenuService.tree(keyword));
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询菜单")
    @SaCheckPermission("system:menu:list")
    public Result<SysMenu> getById(@PathVariable Long id) {
        return Result.success(sysMenuService.getById(id));
    }

    @PostMapping
    @Operation(summary = "新增菜单")
    @SaCheckPermission("system:menu:add")
    public Result<Void> add(@RequestBody SysMenu menu) {
        sysMenuService.addMenu(menu);
        return Result.success();
    }

    @PutMapping
    @Operation(summary = "修改菜单")
    @SaCheckPermission("system:menu:edit")
    public Result<Void> update(@RequestBody SysMenu menu) {
        sysMenuService.updateMenu(menu);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除菜单")
    @SaCheckPermission("system:menu:delete")
    public Result<Void> delete(@PathVariable Long id) {
        sysMenuService.deleteMenu(id);
        return Result.success();
    }

    @GetMapping("/routers")
    @Operation(summary = "获取当前用户的路由")
    public Result<List<RouterVO>> getRouters() {
        Long userId = StpUtil.getLoginIdAsLong();
        return Result.success(sysMenuService.getUserRouters(userId));
    }
}
