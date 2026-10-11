package com.hanxuan.admin.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hanxuan.admin.system.entity.SysOperationLog;
import com.hanxuan.admin.system.mapper.SysOperationLogMapper;
import com.hanxuan.admin.system.service.SysOperationLogService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 操作日志服务实现
 *
 * @author hanxuan
 */
@Service
public class SysOperationLogServiceImpl extends ServiceImpl<SysOperationLogMapper, SysOperationLog>
        implements SysOperationLogService {

    @Override
    @Async
    public void saveLog(SysOperationLog log) {
        save(log);
    }

    @Override
    public IPage<SysOperationLog> page(Integer pageNum, Integer pageSize, String module,
                                        String operationType, Integer status, String username) {
        return page(new Page<>(pageNum, pageSize),
                buildQueryWrapper(module, operationType, status, username));
    }

    @Override
    public List<SysOperationLog> listForExport(String module, String operationType, Integer status, String username) {
        return list(buildQueryWrapper(module, operationType, status, username));
    }

    /**
     * 构建查询条件（分页与导出共用）
     */
    private LambdaQueryWrapper<SysOperationLog> buildQueryWrapper(String module, String operationType,
                                                                  Integer status, String username) {
        LambdaQueryWrapper<SysOperationLog> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(module)) {
            wrapper.like(SysOperationLog::getModule, module);
        }
        if (StringUtils.hasText(operationType)) {
            wrapper.eq(SysOperationLog::getOperationType, operationType);
        }
        if (status != null) {
            wrapper.eq(SysOperationLog::getStatus, status);
        }
        if (StringUtils.hasText(username)) {
            wrapper.like(SysOperationLog::getUsername, username);
        }
        wrapper.orderByDesc(SysOperationLog::getOperationTime);
        return wrapper;
    }

    @Override
    public void deleteBatch(List<Long> ids) {
        removeByIds(ids);
    }

    @Override
    public void clean() {
        remove(null);
    }
}
