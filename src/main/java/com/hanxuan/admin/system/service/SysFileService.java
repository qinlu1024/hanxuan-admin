package com.hanxuan.admin.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hanxuan.admin.system.entity.SysFile;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletResponse;

/**
 * 文件服务
 *
 * @author hanxuan
 */
public interface SysFileService extends IService<SysFile> {

    /**
     * 分页查询
     */
    IPage<SysFile> page(Integer pageNum, Integer pageSize, String originalName, String fileSuffix);

    /**
     * 上传文件
     */
    SysFile upload(MultipartFile file);

    /**
     * 下载文件
     */
    void download(Long id, HttpServletResponse response);

    /**
     * 删除文件
     */
    void deleteFile(Long id);

    /**
     * 批量删除
     */
    void deleteBatch(java.util.List<Long> ids);
}
