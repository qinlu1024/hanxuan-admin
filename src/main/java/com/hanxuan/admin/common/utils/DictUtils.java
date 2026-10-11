package com.hanxuan.admin.common.utils;

import com.hanxuan.admin.system.entity.SysDictData;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 字典缓存工具类
 * 使用内存缓存，避免每次查库
 *
 * @author hanxuan
 */
@Slf4j
public class DictUtils {

    /**
     * 字典缓存：key = dictType, value = 字典数据列表
     */
    private static final ConcurrentHashMap<String, List<SysDictData>> DICT_CACHE = new ConcurrentHashMap<>();

    /**
     * 缓存过期时间（毫秒）：30 分钟
     */
    private static final long EXPIRE_TIME = 30 * 60 * 1000L;

    /**
     * 缓存时间记录：key = dictType, value = 缓存时间戳
     */
    private static final ConcurrentHashMap<String, Long> CACHE_TIME = new ConcurrentHashMap<>();

    /**
     * 获取字典数据
     */
    public static List<SysDictData> getDict(String dictType) {
        if (dictType == null) {
            return null;
        }
        // 检查缓存是否过期
        Long cacheTime = CACHE_TIME.get(dictType);
        if (cacheTime == null || System.currentTimeMillis() - cacheTime > EXPIRE_TIME) {
            // 过期了，返回 null 让调用方去查库
            return null;
        }
        return DICT_CACHE.get(dictType);
    }

    /**
     * 设置字典缓存
     */
    public static void setDict(String dictType, List<SysDictData> dictDataList) {
        if (dictType == null || dictDataList == null) {
            return;
        }
        DICT_CACHE.put(dictType, dictDataList);
        CACHE_TIME.put(dictType, System.currentTimeMillis());
    }

    /**
     * 清除指定字典缓存
     */
    public static void removeDict(String dictType) {
        if (dictType == null) {
            return;
        }
        DICT_CACHE.remove(dictType);
        CACHE_TIME.remove(dictType);
        log.debug("清除字典缓存: {}", dictType);
    }

    /**
     * 清除所有字典缓存
     */
    public static void clearAll() {
        DICT_CACHE.clear();
        CACHE_TIME.clear();
        log.debug("清除所有字典缓存");
    }

    /**
     * 根据字典值获取字典标签
     */
    public static String getDictLabel(String dictType, String dictValue) {
        List<SysDictData> dictDataList = getDict(dictType);
        if (dictDataList == null || dictDataList.isEmpty()) {
            return dictValue;
        }
        for (SysDictData data : dictDataList) {
            if (dictValue.equals(data.getDictValue())) {
                return data.getDictLabel();
            }
        }
        return dictValue;
    }
}
