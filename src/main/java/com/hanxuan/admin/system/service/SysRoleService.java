package com.hanxuan.admin.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hanxuan.admin.system.entity.SysRole;

import java.util.List;

/**
 * 角色服务
 *
 * @author hanxuan
 */
public interface SysRoleService extends IService<SysRole> {

    /**
     * 分页查询
     */
    IPage<SysRole> page(Integer pageNum, Integer pageSize, String keyword);

    /**
     * 新增角色
     */
    void addRole(SysRole role);

    /**
     * 修改角色
     */
    void updateRole(SysRole role);

    /**
     * 删除角色
     */
    void deleteRole(Long id);

    /**
     * 批量删除
     */
    void deleteBatch(List<Long> ids);

    /**
     * 获取角色菜单ID列表
     */
    List<Long> getMenuIdsByRoleId(Long roleId);

    /**
     * 分配角色菜单
     */
    void assignMenus(Long roleId, List<Long> menuIds);

    /**
     * 根据用户ID获取角色列表
     */
    List<SysRole> getRolesByUserId(Long userId);
}
