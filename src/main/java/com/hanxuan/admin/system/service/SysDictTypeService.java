package com.hanxuan.admin.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hanxuan.admin.system.entity.SysDictType;

import java.util.List;

/**
 * 字典类型服务
 *
 * @author hanxuan
 */
public interface SysDictTypeService extends IService<SysDictType> {

    /**
     * 分页查询
     */
    IPage<SysDictType> page(Integer pageNum, Integer pageSize, String dictName, String dictType, Integer status);

    /**
     * 新增字典类型
     */
    void addDictType(SysDictType dictType);

    /**
     * 修改字典类型
     */
    void updateDictType(SysDictType dictType);

    /**
     * 删除字典类型
     */
    void deleteDictType(Long id);

    /**
     * 批量删除字典类型
     */
    void deleteBatch(List<Long> ids);

    /**
     * 获取所有字典类型列表
     */
    List<SysDictType> listAll();

    /**
     * 刷新字典缓存
     */
    void refreshCache();
}
