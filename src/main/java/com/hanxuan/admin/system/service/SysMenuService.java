package com.hanxuan.admin.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hanxuan.admin.system.entity.SysMenu;
import com.hanxuan.admin.system.vo.MenuTreeVO;
import com.hanxuan.admin.system.vo.RouterVO;

import java.util.List;

/**
 * 菜单服务
 *
 * @author hanxuan
 */
public interface SysMenuService extends IService<SysMenu> {

    /**
     * 查询菜单树
     */
    List<MenuTreeVO> tree(String keyword);

    /**
     * 新增菜单
     */
    void addMenu(SysMenu menu);

    /**
     * 修改菜单
     */
    void updateMenu(SysMenu menu);

    /**
     * 删除菜单
     */
    void deleteMenu(Long id);

    /**
     * 获取当前用户的菜单树
     */
    List<SysMenu> getUserMenuTree(Long userId);

    /**
     * 获取当前用户的路由（前端用）
     */
    List<RouterVO> getUserRouters(Long userId);
}
