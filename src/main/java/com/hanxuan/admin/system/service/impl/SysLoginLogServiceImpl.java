package com.hanxuan.admin.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hanxuan.admin.common.utils.IpUtils;
import com.hanxuan.admin.system.entity.SysLoginLog;
import com.hanxuan.admin.system.mapper.SysLoginLogMapper;
import com.hanxuan.admin.system.service.SysLoginLogService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 登录日志服务实现
 *
 * @author hanxuan
 */
@Service
public class SysLoginLogServiceImpl extends ServiceImpl<SysLoginLogMapper, SysLoginLog>
        implements SysLoginLogService {

    @Override
    @Async
    public void recordLoginLog(String username, Integer status, String failReason, HttpServletRequest request) {
        SysLoginLog log = new SysLoginLog();
        log.setUsername(username);
        log.setStatus(status);
        log.setFailReason(failReason);
        log.setLoginTime(LocalDateTime.now());

        if (request != null) {
            log.setLoginIp(IpUtils.getIpAddr(request));
            log.setBrowser(IpUtils.getBrowser(request));
            log.setOs(IpUtils.getOs(request));
        }

        save(log);
    }

    @Override
    public IPage<SysLoginLog> page(Integer pageNum, Integer pageSize, String username, Integer status) {
        return page(new Page<>(pageNum, pageSize), buildQueryWrapper(username, status));
    }

    @Override
    public List<SysLoginLog> listForExport(String username, Integer status) {
        return list(buildQueryWrapper(username, status));
    }

    /**
     * 构建查询条件（分页与导出共用）
     */
    private LambdaQueryWrapper<SysLoginLog> buildQueryWrapper(String username, Integer status) {
        LambdaQueryWrapper<SysLoginLog> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(username)) {
            wrapper.like(SysLoginLog::getUsername, username);
        }
        if (status != null) {
            wrapper.eq(SysLoginLog::getStatus, status);
        }
        wrapper.orderByDesc(SysLoginLog::getLoginTime);
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
