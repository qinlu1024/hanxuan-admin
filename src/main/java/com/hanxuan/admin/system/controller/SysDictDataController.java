package com.hanxuan.admin.system.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hanxuan.admin.common.annotation.OperationLog;
import com.hanxuan.admin.common.result.Result;
import com.hanxuan.admin.system.entity.SysDictData;
import com.hanxuan.admin.system.service.SysDictDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 字典数据控制器
 *
 * @author hanxuan
 */
@Tag(name = "字典管理 - 字典数据")
@RestController
@RequestMapping("/system/dict/data")
@RequiredArgsConstructor
public class SysDictDataController {

    private final SysDictDataService dictDataService;

    @GetMapping("/page")
    @Operation(summary = "分页查询字典数据")
    @SaCheckPermission("system:dict:list")
    public Result<IPage<SysDictData>> page(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "字典类型") @RequestParam(required = false) String dictType,
            @Parameter(description = "字典标签") @RequestParam(required = false) String dictLabel,
            @Parameter(description = "状态 1-正常 0-停用") @RequestParam(required = false) Integer status) {
        return Result.success(dictDataService.page(pageNum, pageSize, dictType, dictLabel, status));
    }

    @GetMapping("/type/{dictType}")
    @Operation(summary = "根据字典类型获取字典数据列表")
    public Result<List<SysDictData>> listByDictType(@PathVariable String dictType) {
        return Result.success(dictDataService.listByDictType(dictType));
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询字典数据")
    @SaCheckPermission("system:dict:list")
    public Result<SysDictData> getById(@PathVariable Long id) {
        return Result.success(dictDataService.getById(id));
    }

    @PostMapping
    @Operation(summary = "新增字典数据")
    @SaCheckPermission("system:dict:add")
    @OperationLog(module = "字典管理", type = "新增", description = "新增字典数据")
    public Result<Void> add(@RequestBody SysDictData dictData) {
        dictDataService.addDictData(dictData);
        return Result.success();
    }

    @PutMapping
    @Operation(summary = "修改字典数据")
    @SaCheckPermission("system:dict:edit")
    @OperationLog(module = "字典管理", type = "修改", description = "修改字典数据")
    public Result<Void> update(@RequestBody SysDictData dictData) {
        dictDataService.updateDictData(dictData);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除字典数据")
    @SaCheckPermission("system:dict:delete")
    @OperationLog(module = "字典管理", type = "删除", description = "删除字典数据")
    public Result<Void> delete(@PathVariable Long id) {
        dictDataService.deleteDictData(id);
        return Result.success();
    }

    @DeleteMapping("/batch")
    @Operation(summary = "批量删除字典数据")
    @SaCheckPermission("system:dict:delete")
    @OperationLog(module = "字典管理", type = "删除", description = "批量删除字典数据")
    public Result<Void> deleteBatch(@RequestBody List<Long> ids) {
        dictDataService.deleteBatch(ids);
        return Result.success();
    }
}
