package com.hanxuan.admin.system.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hanxuan.admin.common.annotation.OperationLog;
import com.hanxuan.admin.common.result.Result;
import com.hanxuan.admin.system.entity.SysNotice;
import com.hanxuan.admin.system.service.SysNoticeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 通知公告控制器
 *
 * @author hanxuan
 */
@Tag(name = "通知公告")
@RestController
@RequestMapping("/system/notice")
@RequiredArgsConstructor
public class SysNoticeController {

    private final SysNoticeService noticeService;

    @GetMapping("/page")
    @Operation(summary = "分页查询通知公告")
    @SaCheckPermission("system:notice:list")
    public Result<IPage<SysNotice>> page(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "公告标题") @RequestParam(required = false) String noticeTitle,
            @Parameter(description = "公告类型 1-通知 2-公告") @RequestParam(required = false) Integer noticeType,
            @Parameter(description = "状态 1-已发布 0-关闭") @RequestParam(required = false) Integer status) {
        return Result.success(noticeService.page(pageNum, pageSize, noticeTitle, noticeType, status));
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询通知公告")
    @SaCheckPermission("system:notice:list")
    public Result<SysNotice> getById(@PathVariable Long id) {
        return Result.success(noticeService.getById(id));
    }

    @PostMapping
    @Operation(summary = "新增通知公告")
    @SaCheckPermission("system:notice:add")
    @OperationLog(module = "通知公告", type = "新增", description = "新增通知公告")
    public Result<Void> add(@RequestBody SysNotice notice) {
        noticeService.addNotice(notice);
        return Result.success();
    }

    @PutMapping
    @Operation(summary = "修改通知公告")
    @SaCheckPermission("system:notice:edit")
    @OperationLog(module = "通知公告", type = "修改", description = "修改通知公告")
    public Result<Void> update(@RequestBody SysNotice notice) {
        noticeService.updateNotice(notice);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除通知公告")
    @SaCheckPermission("system:notice:delete")
    @OperationLog(module = "通知公告", type = "删除", description = "删除通知公告")
    public Result<Void> delete(@PathVariable Long id) {
        noticeService.deleteNotice(id);
        return Result.success();
    }

    @DeleteMapping("/batch")
    @Operation(summary = "批量删除通知公告")
    @SaCheckPermission("system:notice:delete")
    @OperationLog(module = "通知公告", type = "删除", description = "批量删除通知公告")
    public Result<Void> deleteBatch(@RequestBody List<Long> ids) {
        noticeService.deleteBatch(ids);
        return Result.success();
    }

    @PutMapping("/status")
    @Operation(summary = "修改发布状态（发布/关闭）")
    @SaCheckPermission("system:notice:edit")
    @OperationLog(module = "通知公告", type = "修改状态", description = "修改通知公告发布状态")
    public Result<Void> updateStatus(
            @Parameter(description = "公告ID") @RequestParam Long id,
            @Parameter(description = "状态 1-发布 0-关闭") @RequestParam Integer status) {
        noticeService.updateStatus(id, status);
        return Result.success();
    }

    @GetMapping("/published")
    @Operation(summary = "获取已发布通知公告（前台展示，登录即可）")
    public Result<List<SysNotice>> published(
            @Parameter(description = "公告类型 1-通知 2-公告") @RequestParam(required = false) Integer noticeType) {
        return Result.success(noticeService.listPublished(noticeType));
    }
}
