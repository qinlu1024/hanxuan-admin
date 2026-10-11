package com.hanxuan.admin.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hanxuan.admin.common.utils.DictUtils;
import com.hanxuan.admin.system.entity.SysDictData;
import com.hanxuan.admin.system.mapper.SysDictDataMapper;
import com.hanxuan.admin.system.service.SysDictDataService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 字典数据服务实现
 *
 * @author hanxuan
 */
@Service
public class SysDictDataServiceImpl extends ServiceImpl<SysDictDataMapper, SysDictData>
        implements SysDictDataService {

    @Override
    public IPage<SysDictData> page(Integer pageNum, Integer pageSize, String dictType,
                                    String dictLabel, Integer status) {
        LambdaQueryWrapper<SysDictData> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(dictType)) {
            wrapper.eq(SysDictData::getDictType, dictType);
        }
        if (StringUtils.hasText(dictLabel)) {
            wrapper.like(SysDictData::getDictLabel, dictLabel);
        }
        if (status != null) {
            wrapper.eq(SysDictData::getStatus, status);
        }
        wrapper.orderByAsc(SysDictData::getDictType).orderByAsc(SysDictData::getSort);
        return page(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    public List<SysDictData> listByDictType(String dictType) {
        // 先查缓存
        List<SysDictData> cache = DictUtils.getDict(dictType);
        if (cache != null) {
            return cache;
        }
        // 查库
        LambdaQueryWrapper<SysDictData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDictData::getDictType, dictType)
                .eq(SysDictData::getStatus, 1)
                .orderByAsc(SysDictData::getSort);
        List<SysDictData> list = list(wrapper);
        // 放入缓存
        DictUtils.setDict(dictType, list);
        return list;
    }

    @Override
    public void addDictData(SysDictData dictData) {
        // 默认排序
        if (dictData.getSort() == null) {
            dictData.setSort(0);
        }
        save(dictData);
        // 清除缓存
        DictUtils.removeDict(dictData.getDictType());
    }

    @Override
    public void updateDictData(SysDictData dictData) {
        SysDictData oldData = getById(dictData.getId());
        if (oldData != null) {
            // 如果字典类型变了，清除两个类型的缓存
            if (!oldData.getDictType().equals(dictData.getDictType())) {
                DictUtils.removeDict(oldData.getDictType());
            }
        }
        updateById(dictData);
        DictUtils.removeDict(dictData.getDictType());
    }

    @Override
    public void deleteDictData(Long id) {
        SysDictData dictData = getById(id);
        if (dictData != null) {
            removeById(id);
            DictUtils.removeDict(dictData.getDictType());
        }
    }

    @Override
    public void deleteBatch(List<Long> ids) {
        for (Long id : ids) {
            deleteDictData(id);
        }
    }
}
