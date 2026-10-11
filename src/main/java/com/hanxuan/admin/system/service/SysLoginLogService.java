package com.hanxuan.admin.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hanxuan.admin.system.entity.SysLoginLog;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * 登录日志服务
 *
 * @author hanxuan
 */
public interface SysLoginLogService extends IService<SysLoginLog> {

    /**
     * 记录登录日志
     */
    void recordLoginLog(String username, Integer status, String failReason, HttpServletRequest request);

    /**
     * 分页查询
     */
    IPage<SysLoginLog> page(Integer pageNum, Integer pageSize, String username, Integer status);

    /**
     * 查询导出列表（复用分页查询条件）
     */
    List<SysLoginLog> listForExport(String username, Integer status);

    /**
     * 批量删除
     */
    void deleteBatch(List<Long> ids);

    /**
     * 清空日志
     */
    void clean();
}
