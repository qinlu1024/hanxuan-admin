package com.hanxuan.admin.common.utils;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.hanxuan.admin.common.constant.DataScopeConstants;
import com.hanxuan.admin.system.entity.SysDept;
import com.hanxuan.admin.system.entity.SysRole;
import com.hanxuan.admin.system.entity.SysUser;
import com.hanxuan.admin.system.entity.SysUserRole;
import com.hanxuan.admin.system.mapper.SysDeptMapper;
import com.hanxuan.admin.system.mapper.SysRoleMapper;
import com.hanxuan.admin.system.mapper.SysUserMapper;
import com.hanxuan.admin.system.mapper.SysUserRoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 数据权限工具类
 *
 * 使用方式：
 *   LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
 *   DataScopeUtils.addCondition(wrapper, SysUser::getDeptId, SysUser::getId);
 *   然后正常 page(wrapper) 即可
 *
 * @author hanxuan
 */
@Component
public class DataScopeUtils {

    private static SysUserMapper userMapper;
    private static SysRoleMapper roleMapper;
    private static SysUserRoleMapper userRoleMapper;
    private static SysDeptMapper deptMapper;

    @Autowired
    public void setUserMapper(SysUserMapper userMapper) {
        DataScopeUtils.userMapper = userMapper;
    }

    @Autowired
    public void setRoleMapper(SysRoleMapper roleMapper) {
        DataScopeUtils.roleMapper = roleMapper;
    }

    @Autowired
    public void setUserRoleMapper(SysUserRoleMapper userRoleMapper) {
        DataScopeUtils.userRoleMapper = userRoleMapper;
    }

    @Autowired
    public void setDeptMapper(SysDeptMapper deptMapper) {
        DataScopeUtils.deptMapper = deptMapper;
    }

    /**
     * 注入数据权限条件（部门维度 + 用户维度）
     *
     * @param wrapper     查询条件构造器
     * @param deptColumn  部门ID列（如 SysUser::getDeptId）
     * @param userColumn  用户ID列（如 SysUser::getCreateBy），用于"仅本人"场景
     * @param <T>         实体类型
     */
    public static <T> void addCondition(LambdaQueryWrapper<T> wrapper,
                                        SFunction<T, ?> deptColumn,
                                        SFunction<T, ?> userColumn) {
        if (userMapper == null || roleMapper == null || deptMapper == null) {
            return;
        }
        if (!StpUtil.isLogin()) {
            wrapper.apply("1 = 0");
            return;
        }

        Long userId = StpUtil.getLoginIdAsLong();
        Integer scope = getDataScope(userId);

        // 全部数据：不加条件
        if (DataScopeConstants.DATA_SCOPE_ALL.equals(scope)) {
            return;
        }

        // 仅本人：按用户ID过滤
        if (DataScopeConstants.DATA_SCOPE_SELF.equals(scope)) {
            if (userColumn != null) {
                wrapper.eq(userColumn, userId);
            } else {
                wrapper.apply("1 = 0");
            }
            return;
        }

        // 本部门及以下 / 本部门：按部门ID列表过滤
        List<Long> deptIds = getDeptIds(userId, scope);
        if (deptIds != null && !deptIds.isEmpty()) {
            wrapper.in(deptColumn, deptIds);
        } else {
            wrapper.apply("1 = 0");
        }
    }

    /**
     * 仅注入部门数据权限条件
     */
    public static <T> void addDeptCondition(LambdaQueryWrapper<T> wrapper,
                                            SFunction<T, ?> deptColumn) {
        addCondition(wrapper, deptColumn, null);
    }

    /**
     * 获取用户的数据权限范围（取最高级别，数字越小权限越大）
     */
    public static Integer getDataScope(Long userId) {
        // 查询用户角色关联
        List<SysUserRole> userRoles = userRoleMapper.selectList(
                new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, userId)
        );
        if (userRoles == null || userRoles.isEmpty()) {
            return DataScopeConstants.DATA_SCOPE_SELF;
        }

        List<Long> roleIds = userRoles.stream()
                .map(SysUserRole::getRoleId)
                .collect(Collectors.toList());
        List<SysRole> roles = roleMapper.selectBatchIds(roleIds);
        if (roles == null || roles.isEmpty()) {
            return DataScopeConstants.DATA_SCOPE_SELF;
        }

        // 取最小的 scope 值（1 最大，4 最小）
        return roles.stream()
                .filter(r -> r.getStatus() != null && r.getStatus() == 1 && r.getDataScope() != null)
                .map(SysRole::getDataScope)
                .min(Integer::compareTo)
                .orElse(DataScopeConstants.DATA_SCOPE_SELF);
    }

    /**
     * 获取当前用户可见的部门ID列表
     */
    public static List<Long> getDeptIds(Long userId, Integer scope) {
        // 全部数据
        if (DataScopeConstants.DATA_SCOPE_ALL.equals(scope)) {
            return null;
        }
        // 仅本人
        if (DataScopeConstants.DATA_SCOPE_SELF.equals(scope)) {
            return new ArrayList<>();
        }

        // 查用户部门
        SysUser user = userMapper.selectById(userId);
        if (user == null || user.getDeptId() == null) {
            return new ArrayList<>();
        }

        Set<Long> deptIds = new HashSet<>();
        deptIds.add(user.getDeptId());

        // 本部门及以下：递归查子部门
        if (DataScopeConstants.DATA_SCOPE_DEPT_AND_CHILD.equals(scope)) {
            collectChildDeptIds(user.getDeptId(), deptIds);
        }

        return new ArrayList<>(deptIds);
    }

    /**
     * 递归收集子部门ID
     */
    private static void collectChildDeptIds(Long parentId, Set<Long> result) {
        List<SysDept> children = deptMapper.selectList(
                new LambdaQueryWrapper<SysDept>().eq(SysDept::getParentId, parentId)
        );
        if (children != null && !children.isEmpty()) {
            for (SysDept child : children) {
                if (!result.contains(child.getId())) {
                    result.add(child.getId());
                    collectChildDeptIds(child.getId(), result);
                }
            }
        }
    }

    /**
     * 当前用户是否为全部数据权限
     */
    public static boolean isAllScope() {
        if (!StpUtil.isLogin()) {
            return false;
        }
        return DataScopeConstants.DATA_SCOPE_ALL.equals(getDataScope(StpUtil.getLoginIdAsLong()));
    }
}
