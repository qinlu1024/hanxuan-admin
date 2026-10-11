package com.hanxuan.admin.system.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hanxuan.admin.common.constant.SystemConstants;
import com.hanxuan.admin.system.entity.SysMenu;
import com.hanxuan.admin.system.mapper.SysMenuMapper;
import com.hanxuan.admin.system.service.SysMenuService;
import com.hanxuan.admin.system.vo.MenuTreeVO;
import com.hanxuan.admin.system.vo.RouterVO;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 菜单服务实现
 *
 * @author hanxuan
 */
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {

    @Override
    public List<MenuTreeVO> tree(String keyword) {
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(SysMenu::getMenuName, keyword);
        }
        wrapper.orderByAsc(SysMenu::getSort);
        List<SysMenu> list = list(wrapper);
        List<MenuTreeVO> voList = list.stream()
                .map(m -> BeanUtil.copyProperties(m, MenuTreeVO.class))
                .collect(Collectors.toList());
        return buildMenuTree(voList, SystemConstants.ROOT_ID);
    }

    @Override
    public void addMenu(SysMenu menu) {
        if (menu.getParentId() == null) {
            menu.setParentId(SystemConstants.ROOT_ID);
        }
        if (menu.getSort() == null) {
            menu.setSort(0);
        }
        if (menu.getStatus() == null) {
            menu.setStatus(1);
        }
        if (menu.getVisible() == null) {
            menu.setVisible(1);
        }
        save(menu);
    }

    @Override
    public void updateMenu(SysMenu menu) {
        updateById(menu);
    }

    @Override
    public void deleteMenu(Long id) {
        // 检查是否有子菜单
        Long count = lambdaQuery().eq(SysMenu::getParentId, id).count();
        if (count > 0) {
            throw new RuntimeException("存在子菜单，不允许删除");
        }
        removeById(id);
    }

    @Override
    public List<SysMenu> getUserMenuTree(Long userId) {
        // 这个方法保留给内部使用，返回扁平列表
        List<SysMenu> menus;
        if (SystemConstants.SUPER_ADMIN_USER_ID.equals(userId)) {
            menus = lambdaQuery()
                    .in(SysMenu::getMenuType, SystemConstants.MENU_TYPE_DIR, SystemConstants.MENU_TYPE_MENU)
                    .eq(SysMenu::getStatus, 1)
                    .orderByAsc(SysMenu::getSort)
                    .list();
        } else {
            menus = baseMapper.selectMenusByUserId(userId);
            menus = menus.stream()
                    .filter(m -> !SystemConstants.MENU_TYPE_BUTTON.equals(m.getMenuType()))
                    .collect(Collectors.toList());
        }
        return menus;
    }

    @Override
    public List<RouterVO> getUserRouters(Long userId) {
        List<SysMenu> menus = getUserMenuTree(userId);
        List<MenuTreeVO> treeVOS = menus.stream()
                .map(m -> BeanUtil.copyProperties(m, MenuTreeVO.class))
                .collect(Collectors.toList());
        List<MenuTreeVO> menuTree = buildMenuTree(treeVOS, SystemConstants.ROOT_ID);
        return buildRouters(menuTree);
    }

    /**
     * 构建菜单树结构
     */
    private List<MenuTreeVO> buildMenuTree(List<MenuTreeVO> list, Long parentId) {
        List<MenuTreeVO> tree = new ArrayList<>();
        for (MenuTreeVO menu : list) {
            if (parentId.equals(menu.getParentId())) {
                List<MenuTreeVO> children = buildMenuTree(list, menu.getId());
                if (CollUtil.isNotEmpty(children)) {
                    menu.setChildren(children);
                }
                tree.add(menu);
            }
        }
        return tree;
    }

    /**
     * 构建前端路由
     */
    private List<RouterVO> buildRouters(List<MenuTreeVO> menuTree) {
        List<RouterVO> routers = new LinkedList<>();
        for (MenuTreeVO menu : menuTree) {
            RouterVO router = new RouterVO();
            router.setName(menu.getMenuName());
            router.setPath(menu.getPath());
            router.setComponent(menu.getComponent());

            RouterVO.Meta meta = new RouterVO.Meta();
            meta.setTitle(menu.getMenuName());
            meta.setIcon(menu.getIcon());
            meta.setHidden(menu.getVisible() != null && menu.getVisible() == 0);
            meta.setKeepAlive(true);
            router.setMeta(meta);

            if (CollUtil.isNotEmpty(menu.getChildren())) {
                router.setChildren(buildRouters(menu.getChildren()));
            }
            routers.add(router);
        }
        return routers;
    }
}
