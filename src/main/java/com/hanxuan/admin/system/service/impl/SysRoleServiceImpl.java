package com.hanxuan.admin.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hanxuan.admin.common.constant.DataScopeConstants;
import com.hanxuan.admin.common.constant.SystemConstants;
import com.hanxuan.admin.common.enums.ResultCode;
import com.hanxuan.admin.common.exception.BusinessException;
import com.hanxuan.admin.system.entity.SysRole;
import com.hanxuan.admin.system.entity.SysRoleMenu;
import com.hanxuan.admin.system.entity.SysUserRole;
import com.hanxuan.admin.system.mapper.SysRoleMapper;
import com.hanxuan.admin.system.mapper.SysRoleMenuMapper;
import com.hanxuan.admin.system.mapper.SysUserRoleMapper;
import com.hanxuan.admin.system.service.SysRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色服务实现
 *
 * @author hanxuan
 */
@Service
@RequiredArgsConstructor
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

    private final SysRoleMenuMapper sysRoleMenuMapper;
    private final SysUserRoleMapper sysUserRoleMapper;

    @Override
    public IPage<SysRole> page(Integer pageNum, Integer pageSize, String keyword) {
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(SysRole::getRoleName, keyword)
                    .or().like(SysRole::getRoleCode, keyword));
        }
        wrapper.orderByAsc(SysRole::getSort);
        return page(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    public void addRole(SysRole role) {
        // 校验角色编码是否存在
        Long count = lambdaQuery().eq(SysRole::getRoleCode, role.getRoleCode()).count();
        if (count > 0) {
            throw new BusinessException(ResultCode.ROLE_EXISTS);
        }
        if (role.getStatus() == null) {
            role.setStatus(1);
        }
        if (role.getSort() == null) {
            role.setSort(0);
        }
        if (role.getDataScope() == null) {
            role.setDataScope(DataScopeConstants.DATA_SCOPE_DEPT_AND_CHILD);
        }
        save(role);
    }

    @Override
    public void updateRole(SysRole role) {
        // 超级管理员角色不允许修改编码
        SysRole oldRole = getById(role.getId());
        if (SystemConstants.SUPER_ADMIN_ROLE.equals(oldRole.getRoleCode())) {
            role.setRoleCode(null);
        }
        updateById(role);
    }

    @Override
    public void deleteRole(Long id) {
        SysRole role = getById(id);
        if (role != null && SystemConstants.SUPER_ADMIN_ROLE.equals(role.getRoleCode())) {
            throw new BusinessException("超级管理员角色不能删除");
        }
        removeById(id);
        // 删除角色菜单关联
        sysRoleMenuMapper.delete(new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getRoleId, id));
    }

    @Override
    public void deleteBatch(List<Long> ids) {
        for (Long id : ids) {
            deleteRole(id);
        }
    }

    @Override
    public List<Long> getMenuIdsByRoleId(Long roleId) {
        List<SysRoleMenu> list = sysRoleMenuMapper.selectList(
                new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getRoleId, roleId)
        );
        return list.stream().map(SysRoleMenu::getMenuId).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignMenus(Long roleId, List<Long> menuIds) {
        // 先删除原有关联
        sysRoleMenuMapper.delete(new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getRoleId, roleId));
        // 再新增
        if (menuIds != null && !menuIds.isEmpty()) {
            for (Long menuId : menuIds) {
                SysRoleMenu roleMenu = new SysRoleMenu();
                roleMenu.setRoleId(roleId);
                roleMenu.setMenuId(menuId);
                sysRoleMenuMapper.insert(roleMenu);
            }
        }
    }

    @Override
    public List<SysRole> getRolesByUserId(Long userId) {
        // 查询用户角色关联
        List<SysUserRole> userRoles = sysUserRoleMapper.selectList(
                new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, userId)
        );
        if (userRoles == null || userRoles.isEmpty()) {
            return List.of();
        }
        // 提取角色ID
        List<Long> roleIds = userRoles.stream()
                .map(SysUserRole::getRoleId)
                .collect(Collectors.toList());
        // 查询角色列表（只查正常状态的）
        return listByIds(roleIds).stream()
                .filter(role -> role.getStatus() != null && role.getStatus() == 1)
                .collect(Collectors.toList());
    }
}
