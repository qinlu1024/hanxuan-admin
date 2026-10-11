package com.hanxuan.admin.system.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hanxuan.admin.common.config.FileStorageConfig;
import com.hanxuan.admin.common.enums.ResultCode;
import com.hanxuan.admin.common.exception.BusinessException;
import com.hanxuan.admin.system.entity.SysFile;
import com.hanxuan.admin.system.mapper.SysFileMapper;
import com.hanxuan.admin.system.service.SysFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 文件服务实现（本地磁盘存储）
 *
 * @author hanxuan
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysFileServiceImpl extends ServiceImpl<SysFileMapper, SysFile>
        implements SysFileService {

    private final FileStorageConfig fileConfig;

    @Override
    public IPage<SysFile> page(Integer pageNum, Integer pageSize, String originalName, String fileSuffix) {
        LambdaQueryWrapper<SysFile> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(originalName)) {
            wrapper.like(SysFile::getOriginalName, originalName);
        }
        if (StringUtils.hasText(fileSuffix)) {
            wrapper.eq(SysFile::getFileSuffix, fileSuffix);
        }
        wrapper.orderByDesc(SysFile::getCreateTime);
        return page(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    public SysFile upload(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("文件不能为空");
        }

        // 原文件名
        String originalName = file.getOriginalFilename();
        // 文件后缀
        String suffix = "";
        if (originalName != null && originalName.contains(".")) {
            suffix = originalName.substring(originalName.lastIndexOf("."));
        }
        // 生成新文件名（UUID + 后缀）
        String newFileName = IdUtil.simpleUUID() + suffix;

        // 按日期分目录
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String relativePath = datePath + "/" + newFileName;

        // 完整存储路径（转成绝对路径）
        Path basePath = Paths.get(fileConfig.getLocalPath()).toAbsolutePath().normalize();
        Path storagePath = basePath.resolve(relativePath).normalize();
        File storageFile = storagePath.toFile();

        // 安全检查：确保最终路径在基础目录下（防止路径穿越）
        if (!storagePath.startsWith(basePath)) {
            throw new BusinessException("非法文件路径");
        }

        try {
            // 确保目录存在
            File parentDir = storageFile.getParentFile();
            if (!parentDir.exists()) {
                parentDir.mkdirs();
            }
            // 保存文件（使用流的方式，避免transferTo相对路径问题）
            try (InputStream is = file.getInputStream();
                 FileOutputStream os = new FileOutputStream(storageFile)) {
                byte[] buffer = new byte[4096];
                int len;
                while ((len = is.read(buffer)) > 0) {
                    os.write(buffer, 0, len);
                }
            }
        } catch (IOException e) {
            log.error("文件保存失败: {}", e.getMessage(), e);
            throw new BusinessException("文件保存失败");
        }

        // 保存文件记录
        SysFile sysFile = new SysFile();
        sysFile.setOriginalName(originalName);
        sysFile.setFileName(newFileName);
        sysFile.setFileSuffix(suffix);
        sysFile.setFileSize(file.getSize());
        sysFile.setContentType(file.getContentType());
        sysFile.setFilePath(relativePath);
        sysFile.setStorageType(fileConfig.getStorageType());
        try {
            sysFile.setCreateBy(StpUtil.getLoginIdAsLong());
        } catch (Exception ignored) {
            // 未登录时不记录上传人
        }

        save(sysFile);
        return sysFile;
    }

    @Override
    public void download(Long id, HttpServletResponse response) {
        SysFile sysFile = getById(id);
        if (sysFile == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }

        Path basePath = Paths.get(fileConfig.getLocalPath()).toAbsolutePath().normalize();
        Path filePath = basePath.resolve(sysFile.getFilePath()).normalize();
        // 安全检查
        if (!filePath.startsWith(basePath)) {
            throw new BusinessException("非法文件路径");
        }
        File file = filePath.toFile();
        if (!file.exists()) {
            throw new BusinessException("文件不存在");
        }

        // 设置响应头
        response.setContentType(sysFile.getContentType());
        response.setContentLengthLong(file.length());
        try {
            String encodedName = URLEncoder.encode(sysFile.getOriginalName(), StandardCharsets.UTF_8)
                    .replaceAll("\\+", "%20");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + encodedName + "\"");
        } catch (Exception e) {
            response.setHeader("Content-Disposition", "attachment; filename=\"file" + sysFile.getFileSuffix() + "\"");
        }

        // 流式输出
        try (InputStream is = new FileInputStream(file);
             OutputStream os = response.getOutputStream()) {
            byte[] buffer = new byte[4096];
            int len;
            while ((len = is.read(buffer)) > 0) {
                os.write(buffer, 0, len);
            }
            os.flush();
        } catch (IOException e) {
            log.error("文件下载失败: {}", e.getMessage(), e);
            throw new BusinessException("文件下载失败");
        }
    }

    @Override
    public void deleteFile(Long id) {
        SysFile sysFile = getById(id);
        if (sysFile == null) {
            return;
        }

        // 删除物理文件
        try {
            Path basePath = Paths.get(fileConfig.getLocalPath()).toAbsolutePath().normalize();
            Path filePath = basePath.resolve(sysFile.getFilePath()).normalize();
            // 安全检查
            if (filePath.startsWith(basePath)) {
                Files.deleteIfExists(filePath);
            }
        } catch (IOException e) {
            log.error("物理文件删除失败: {}", e.getMessage(), e);
        }

        // 删除数据库记录（逻辑删除）
        removeById(id);
    }

    @Override
    public void deleteBatch(List<Long> ids) {
        for (Long id : ids) {
            deleteFile(id);
        }
    }
}
