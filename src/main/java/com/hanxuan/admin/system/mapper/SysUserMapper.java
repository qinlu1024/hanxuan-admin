package com.hanxuan.admin.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hanxuan.admin.system.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户表 Mapper
 *
 * @author hanxuan
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

    /**
     * 根据用户ID查询角色编码列表
     */
    List<String> selectRoleCodesByUserId(@Param("userId") Long userId);

    /**
     * 根据用户ID查询权限标识列表
     */
    List<String> selectPermsByUserId(@Param("userId") Long userId);
}
