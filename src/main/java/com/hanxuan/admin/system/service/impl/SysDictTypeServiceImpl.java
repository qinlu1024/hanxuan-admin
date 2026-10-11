package com.hanxuan.admin.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hanxuan.admin.common.enums.ResultCode;
import com.hanxuan.admin.common.exception.BusinessException;
import com.hanxuan.admin.common.utils.DictUtils;
import com.hanxuan.admin.system.entity.SysDictData;
import com.hanxuan.admin.system.entity.SysDictType;
import com.hanxuan.admin.system.mapper.SysDictDataMapper;
import com.hanxuan.admin.system.mapper.SysDictTypeMapper;
import com.hanxuan.admin.system.service.SysDictTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 字典类型服务实现
 *
 * @author hanxuan
 */
@Service
@RequiredArgsConstructor
public class SysDictTypeServiceImpl extends ServiceImpl<SysDictTypeMapper, SysDictType>
        implements SysDictTypeService {

    private final SysDictDataMapper dictDataMapper;

    @Override
    public IPage<SysDictType> page(Integer pageNum, Integer pageSize, String dictName,
                                    String dictType, Integer status) {
        LambdaQueryWrapper<SysDictType> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(dictName)) {
            wrapper.like(SysDictType::getDictName, dictName);
        }
        if (StringUtils.hasText(dictType)) {
            wrapper.like(SysDictType::getDictType, dictType);
        }
        if (status != null) {
            wrapper.eq(SysDictType::getStatus, status);
        }
        wrapper.orderByAsc(SysDictType::getId);
        return page(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    public void addDictType(SysDictType dictType) {
        // 校验字典类型是否已存在
        Long count = lambdaQuery().eq(SysDictType::getDictType, dictType.getDictType()).count();
        if (count > 0) {
            throw new BusinessException(ResultCode.DICT_TYPE_EXISTS);
        }
        save(dictType);
        // 清除缓存
        DictUtils.removeDict(dictType.getDictType());
    }

    @Override
    public void updateDictType(SysDictType dictType) {
        SysDictType oldDict = getById(dictType.getId());
        if (oldDict == null) {
            throw new BusinessException(ResultCode.DICT_TYPE_NOT_FOUND);
        }
        // 如果字典类型变了，校验唯一性，并清除旧缓存
        if (!oldDict.getDictType().equals(dictType.getDictType())) {
            Long count = lambdaQuery().eq(SysDictType::getDictType, dictType.getDictType()).count();
            if (count > 0) {
                throw new BusinessException(ResultCode.DICT_TYPE_EXISTS);
            }
            DictUtils.removeDict(oldDict.getDictType());
            // 同时更新字典数据里的 dictType
            LambdaQueryWrapper<SysDictData> dataWrapper = new LambdaQueryWrapper<>();
            dataWrapper.eq(SysDictData::getDictType, oldDict.getDictType());
            SysDictData updateData = new SysDictData();
            updateData.setDictType(dictType.getDictType());
            dictDataMapper.update(updateData, dataWrapper);
        }
        updateById(dictType);
        DictUtils.removeDict(dictType.getDictType());
    }

    @Override
    public void deleteDictType(Long id) {
        SysDictType dictType = getById(id);
        if (dictType == null) {
            return;
        }
        // 删除字典数据
        LambdaQueryWrapper<SysDictData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDictData::getDictType, dictType.getDictType());
        dictDataMapper.delete(wrapper);

        removeById(id);
        DictUtils.removeDict(dictType.getDictType());
    }

    @Override
    public void deleteBatch(List<Long> ids) {
        for (Long id : ids) {
            deleteDictType(id);
        }
    }

    @Override
    public List<SysDictType> listAll() {
        return list();
    }

    @Override
    public void refreshCache() {
        DictUtils.clearAll();
    }
}
