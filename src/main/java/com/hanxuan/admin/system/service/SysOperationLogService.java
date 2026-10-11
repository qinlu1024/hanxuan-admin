package com.hanxuan.admin.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hanxuan.admin.system.entity.SysOperationLog;

import java.util.List;

/**
 * 操作日志服务
 *
 * @author hanxuan
 */
public interface SysOperationLogService extends IService<SysOperationLog> {

    /**
     * 保存操作日志
     */
    void saveLog(SysOperationLog log);

    /**
     * 分页查询
     */
    IPage<SysOperationLog> page(Integer pageNum, Integer pageSize, String module,
                                String operationType, Integer status, String username);

    /**
     * 查询导出列表（复用分页查询条件）
     */
    List<SysOperationLog> listForExport(String module, String operationType, Integer status, String username);

    /**
     * 批量删除
     */
    void deleteBatch(List<Long> ids);

    /**
     * 清空日志
     */
    void clean();
}
