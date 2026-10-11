package com.hanxuan.admin.system.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.hanxuan.admin.common.annotation.OperationLog;
import com.hanxuan.admin.common.result.Result;
import com.hanxuan.admin.system.entity.SysDept;
import com.hanxuan.admin.system.service.SysDeptService;
import com.hanxuan.admin.system.vo.DeptTreeVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 部门管理控制器
 *
 * @author hanxuan
 */
@Tag(name = "部门管理")
@RestController
@RequestMapping("/system/dept")
@RequiredArgsConstructor
public class SysDeptController {

    private final SysDeptService deptService;

    @GetMapping("/tree")
    @Operation(summary = "获取部门树")
    @SaCheckPermission("system:dept:list")
    public Result<List<DeptTreeVO>> tree(
            @Parameter(description = "部门名称") @RequestParam(required = false) String deptName,
            @Parameter(description = "状态 1-正常 0-停用") @RequestParam(required = false) Integer status) {
        return Result.success(deptService.tree(deptName, status));
    }

    @GetMapping("/list")
    @Operation(summary = "获取部门列表（平铺，用于下拉选择）")
    @SaCheckPermission("system:dept:list")
    public Result<List<SysDept>> list() {
        return Result.success(deptService.list());
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询部门")
    @SaCheckPermission("system:dept:list")
    public Result<SysDept> getById(@PathVariable Long id) {
        return Result.success(deptService.getDeptById(id));
    }

    @PostMapping
    @Operation(summary = "新增部门")
    @SaCheckPermission("system:dept:add")
    @OperationLog(module = "部门管理", type = "新增", description = "新增部门")
    public Result<Void> add(@RequestBody SysDept dept) {
        deptService.addDept(dept);
        return Result.success();
    }

    @PutMapping
    @Operation(summary = "修改部门")
    @SaCheckPermission("system:dept:edit")
    @OperationLog(module = "部门管理", type = "修改", description = "修改部门")
    public Result<Void> update(@RequestBody SysDept dept) {
        deptService.updateDept(dept);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除部门")
    @SaCheckPermission("system:dept:delete")
    @OperationLog(module = "部门管理", type = "删除", description = "删除部门")
    public Result<Void> delete(@PathVariable Long id) {
        deptService.deleteDept(id);
        return Result.success();
    }
}
