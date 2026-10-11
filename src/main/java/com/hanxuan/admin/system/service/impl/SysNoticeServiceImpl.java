package com.hanxuan.admin.system.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hanxuan.admin.common.enums.ResultCode;
import com.hanxuan.admin.common.exception.BusinessException;
import com.hanxuan.admin.system.entity.SysNotice;
import com.hanxuan.admin.system.entity.SysUser;
import com.hanxuan.admin.system.mapper.SysNoticeMapper;
import com.hanxuan.admin.system.mapper.SysUserMapper;
import com.hanxuan.admin.system.service.SysNoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 通知公告服务实现
 *
 * @author hanxuan
 */
@Service
@RequiredArgsConstructor
public class SysNoticeServiceImpl extends ServiceImpl<SysNoticeMapper, SysNotice>
        implements SysNoticeService {

    private final SysUserMapper userMapper;

    @Override
    public IPage<SysNotice> page(Integer pageNum, Integer pageSize, String noticeTitle,
                                 Integer noticeType, Integer status) {
        LambdaQueryWrapper<SysNotice> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(noticeTitle)) {
            wrapper.like(SysNotice::getNoticeTitle, noticeTitle);
        }
        if (noticeType != null) {
            wrapper.eq(SysNotice::getNoticeType, noticeType);
        }
        if (status != null) {
            wrapper.eq(SysNotice::getStatus, status);
        }
        wrapper.orderByDesc(SysNotice::getCreateTime);
        IPage<SysNotice> result = page(new Page<>(pageNum, pageSize), wrapper);
        fillCreatorName(result.getRecords());
        return result;
    }

    @Override
    public void addNotice(SysNotice notice) {
        if (notice.getStatus() == null) {
            // 默认关闭（草稿），发布由专门操作控制
            notice.setStatus(0);
        }
        notice.setCreateBy(StpUtil.getLoginIdAsLong());
        save(notice);
    }

    @Override
    public void updateNotice(SysNotice notice) {
        SysNotice old = getById(notice.getId());
        if (old == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        // 不允许通过修改接口改动发布状态
        notice.setStatus(null);
        notice.setCreateBy(null);
        updateById(notice);
    }

    @Override
    public void deleteNotice(Long id) {
        removeById(id);
    }

    @Override
    public void deleteBatch(List<Long> ids) {
        removeByIds(ids);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        SysNotice old = getById(id);
        if (old == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }
        SysNotice update = new SysNotice();
        update.setId(id);
        update.setStatus(status);
        updateById(update);
    }

    @Override
    public List<SysNotice> listPublished(Integer noticeType) {
        LambdaQueryWrapper<SysNotice> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysNotice::getStatus, 1);
        if (noticeType != null) {
            wrapper.eq(SysNotice::getNoticeType, noticeType);
        }
        wrapper.orderByDesc(SysNotice::getCreateTime);
        List<SysNotice> list = list(wrapper);
        fillCreatorName(list);
        return list;
    }

    /**
     * 回填创建人名称
     */
    private void fillCreatorName(List<SysNotice> records) {
        if (records == null || records.isEmpty()) {
            return;
        }
        Set<Long> userIds = records.stream()
                .map(SysNotice::getCreateBy)
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toSet());
        if (userIds.isEmpty()) {
            return;
        }
        Map<Long, String> userMap = userMapper.selectBatchIds(userIds).stream()
                .collect(Collectors.toMap(SysUser::getId,
                        u -> u.getNickname() != null ? u.getNickname() : u.getUsername(),
                        (a, b) -> a));
        records.forEach(n -> n.setCreateByName(userMap.get(n.getCreateBy())));
    }
}
