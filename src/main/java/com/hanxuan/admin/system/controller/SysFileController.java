package com.hanxuan.admin.system.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hanxuan.admin.common.annotation.OperationLog;
import com.hanxuan.admin.common.result.Result;
import com.hanxuan.admin.system.entity.SysFile;
import com.hanxuan.admin.system.service.SysFileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 文件管理控制器
 *
 * @author hanxuan
 */
@Tag(name = "文件管理")
@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
public class SysFileController {

    private final SysFileService fileService;

    @GetMapping("/page")
    @Operation(summary = "分页查询文件")
    @SaCheckPermission("system:file:list")
    public Result<IPage<SysFile>> page(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") Integer pageSize,
            @Parameter(description = "文件名") @RequestParam(required = false) String originalName,
            @Parameter(description = "文件后缀") @RequestParam(required = false) String fileSuffix) {
        return Result.success(fileService.page(pageNum, pageSize, originalName, fileSuffix));
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询文件")
    @SaCheckPermission("system:file:list")
    public Result<SysFile> getById(@PathVariable Long id) {
        return Result.success(fileService.getById(id));
    }

    @PostMapping("/upload")
    @Operation(summary = "上传文件")
    @SaCheckPermission("system:file:upload")
    @OperationLog(module = "文件管理", type = "上传", description = "上传文件")
    public Result<SysFile> upload(@RequestParam("file") MultipartFile file) {
        return Result.success(fileService.upload(file));
    }

    @GetMapping("/download/{id}")
    @Operation(summary = "下载文件")
    public void download(@PathVariable Long id, HttpServletResponse response) {
        fileService.download(id, response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除文件")
    @SaCheckPermission("system:file:delete")
    @OperationLog(module = "文件管理", type = "删除", description = "删除文件")
    public Result<Void> delete(@PathVariable Long id) {
        fileService.deleteFile(id);
        return Result.success();
    }

    @DeleteMapping("/batch")
    @Operation(summary = "批量删除文件")
    @SaCheckPermission("system:file:delete")
    @OperationLog(module = "文件管理", type = "删除", description = "批量删除文件")
    public Result<Void> deleteBatch(@RequestBody List<Long> ids) {
        fileService.deleteBatch(ids);
        return Result.success();
    }
}
