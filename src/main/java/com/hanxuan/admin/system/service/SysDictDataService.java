package com.hanxuan.admin.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hanxuan.admin.system.entity.SysDictData;

import java.util.List;

/**
 * 字典数据服务
 *
 * @author hanxuan
 */
public interface SysDictDataService extends IService<SysDictData> {

    /**
     * 分页查询
     */
    IPage<SysDictData> page(Integer pageNum, Integer pageSize, String dictType, String dictLabel, Integer status);

    /**
     * 根据字典类型获取字典数据列表
     */
    List<SysDictData> listByDictType(String dictType);

    /**
     * 新增字典数据
     */
    void addDictData(SysDictData dictData);

    /**
     * 修改字典数据
     */
    void updateDictData(SysDictData dictData);

    /**
     * 删除字典数据
     */
    void deleteDictData(Long id);

    /**
     * 批量删除字典数据
     */
    void deleteBatch(List<Long> ids);
}
