package com.hanxuan.admin.security;

import cn.dev33.satoken.stp.StpInterface;
import com.hanxuan.admin.common.constant.SystemConstants;
import com.hanxuan.admin.system.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * Sa-Token 权限数据加载接口
 *
 * @author hanxuan
 */
@Component
@RequiredArgsConstructor
public class StpInterfaceImpl implements StpInterface {

    private final SysUserMapper sysUserMapper;

    /**
     * 返回指定账号拥有的权限码集合
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        Long userId = Long.valueOf(loginId.toString());

        // 超级管理员拥有所有权限
        if (SystemConstants.SUPER_ADMIN_USER_ID.equals(userId)) {
            // 这里返回一个标记，在实际使用时判断是超级管理员直接放行
            // 也可以查询所有权限，视情况而定
            return Collections.singletonList("*:*:*");
        }

        return sysUserMapper.selectPermsByUserId(userId);
    }

    /**
     * 返回指定账号拥有的角色标识集合
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        Long userId = Long.valueOf(loginId.toString());

        // 超级管理员拥有所有角色
        if (SystemConstants.SUPER_ADMIN_USER_ID.equals(userId)) {
            return Collections.singletonList(SystemConstants.SUPER_ADMIN_ROLE);
        }

        return sysUserMapper.selectRoleCodesByUserId(userId);
    }
}
