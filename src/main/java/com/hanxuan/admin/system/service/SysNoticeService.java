package com.hanxuan.admin.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hanxuan.admin.system.entity.SysNotice;

import java.util.List;

/**
 * 通知公告服务
 *
 * @author hanxuan
 */
public interface SysNoticeService extends IService<SysNotice> {

    /**
     * 分页查询（后台管理）
     */
    IPage<SysNotice> page(Integer pageNum, Integer pageSize, String noticeTitle,
                          Integer noticeType, Integer status);

    /**
     * 新增公告
     */
    void addNotice(SysNotice notice);

    /**
     * 修改公告
     */
    void updateNotice(SysNotice notice);

    /**
     * 删除公告
     */
    void deleteNotice(Long id);

    /**
     * 批量删除公告
     */
    void deleteBatch(List<Long> ids);

    /**
     * 修改发布状态（发布/关闭）
     */
    void updateStatus(Long id, Integer status);

    /**
     * 获取已发布公告列表（前台展示）
     */
    List<SysNotice> listPublished(Integer noticeType);
}
