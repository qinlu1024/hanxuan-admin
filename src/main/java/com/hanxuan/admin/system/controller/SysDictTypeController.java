package com.hanxuan.admin.system.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hanxuan.admin.common.annotation.OperationLog;
import com.hanxuan.admin.common.result.Result;
import com.hanxuan.admin.system.entity.SysDictType;
import com.hanxuan.admin.system.service.SysDictTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 字典类型控制器
 *
 * @author hanxuan
 */
@Tag(name = "字典管理 - 字典类型")
@RestController
@RequestMapping("/system/dict/type")
@RequiredArgsConstructor
public class SysDictTypeController {

    private final SysDictTypeService dictTypeService;

    @GetMapping("/page")
    @Operation(summary = "分页查询字典类型")
    @SaCheckPermission("system:dict:list")
    public Result<IPage<SysDictType>> page(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "字典名称") @RequestParam(required = false) String dictName,
            @Parameter(description = "字典类型") @RequestParam(required = false) String dictType,
            @Parameter(description = "状态 1-正常 0-停用") @RequestParam(required = false) Integer status) {
        return Result.success(dictTypeService.page(pageNum, pageSize, dictName, dictType, status));
    }

    @GetMapping("/list")
    @Operation(summary = "获取所有字典类型列表")
    @SaCheckPermission("system:dict:list")
    public Result<List<SysDictType>> list() {
        return Result.success(dictTypeService.listAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询字典类型")
    @SaCheckPermission("system:dict:list")
    public Result<SysDictType> getById(@PathVariable Long id) {
        return Result.success(dictTypeService.getById(id));
    }

    @PostMapping
    @Operation(summary = "新增字典类型")
    @SaCheckPermission("system:dict:add")
    @OperationLog(module = "字典管理", type = "新增", description = "新增字典类型")
    public Result<Void> add(@RequestBody SysDictType dictType) {
        dictTypeService.addDictType(dictType);
        return Result.success();
    }

    @PutMapping
    @Operation(summary = "修改字典类型")
    @SaCheckPermission("system:dict:edit")
    @OperationLog(module = "字典管理", type = "修改", description = "修改字典类型")
    public Result<Void> update(@RequestBody SysDictType dictType) {
        dictTypeService.updateDictType(dictType);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除字典类型")
    @SaCheckPermission("system:dict:delete")
    @OperationLog(module = "字典管理", type = "删除", description = "删除字典类型")
    public Result<Void> delete(@PathVariable Long id) {
        dictTypeService.deleteDictType(id);
        return Result.success();
    }

    @DeleteMapping("/batch")
    @Operation(summary = "批量删除字典类型")
    @SaCheckPermission("system:dict:delete")
    @OperationLog(module = "字典管理", type = "删除", description = "批量删除字典类型")
    public Result<Void> deleteBatch(@RequestBody List<Long> ids) {
        dictTypeService.deleteBatch(ids);
        return Result.success();
    }

    @DeleteMapping("/refreshCache")
    @Operation(summary = "刷新字典缓存")
    @SaCheckPermission("system:dict:edit")
    @OperationLog(module = "字典管理", type = "刷新缓存", description = "刷新字典缓存")
    public Result<Void> refreshCache() {
        dictTypeService.refreshCache();
        return Result.success();
    }
}
