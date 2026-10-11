package com.hanxuan.admin.system.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hanxuan.admin.common.enums.ResultCode;
import com.hanxuan.admin.common.exception.BusinessException;
import com.hanxuan.admin.common.utils.IpUtils;
import com.hanxuan.admin.system.dto.LoginDTO;
import com.hanxuan.admin.system.entity.SysUser;
import com.hanxuan.admin.system.mapper.SysUserMapper;
import com.hanxuan.admin.system.service.AuthService;
import com.hanxuan.admin.system.vo.LoginVO;
import com.hanxuan.admin.system.vo.TokenInfoVO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 认证服务实现
 *
 * @author hanxuan
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final SysUserMapper sysUserMapper;

    @Override
    public LoginVO login(LoginDTO dto, HttpServletRequest request) {
        // 1. 查询用户
        SysUser user = sysUserMapper.selectOne(
                new LambdaQueryWrapper<SysUser>()
                        .eq(SysUser::getUsername, dto.getUsername())
        );
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        // 2. 校验密码
        if (!BCrypt.checkpw(dto.getPassword(), user.getPassword())) {
            throw new BusinessException(ResultCode.USER_PASSWORD_ERROR);
        }

        // 3. 校验状态
        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new BusinessException(ResultCode.USER_DISABLED);
        }

        // 4. Sa-Token 登录
        StpUtil.login(user.getId());
        // 将用户信息存入 session，方便后续使用
        StpUtil.getSession().set("userId", user.getId());
        StpUtil.getSession().set("username", user.getUsername());
        StpUtil.getSession().set("nickname", user.getNickname());
        // 记录登录环境信息，用于在线用户监控
        StpUtil.getSession().set("loginIp", IpUtils.getIpAddr(request));
        StpUtil.getSession().set("browser", IpUtils.getBrowser(request));
        StpUtil.getSession().set("os", IpUtils.getOs(request));
        StpUtil.getSession().set("loginTime", DateUtil.formatDateTime(new Date()));
        String token = StpUtil.getTokenValue();

        // 5. 获取角色和权限
        List<String> roles = StpUtil.getRoleList();
        List<String> permissions = StpUtil.getPermissionList();

        // 6. 组装返回
        LoginVO.UserInfo userInfo = new LoginVO.UserInfo(
                user.getId(),
                user.getUsername(),
                user.getNickname(),
                user.getAvatar()
        );

        return new LoginVO(token, userInfo, roles, permissions);
    }

    @Override
    public void logout() {
        StpUtil.logout();
    }

    @Override
    public LoginVO.UserInfo getUserInfo() {
        Long userId = StpUtil.getLoginIdAsLong();
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        return new LoginVO.UserInfo(
                user.getId(),
                user.getUsername(),
                user.getNickname(),
                user.getAvatar()
        );
    }

    @Override
    public TokenInfoVO getTokenInfo() {
        return buildTokenInfo(false);
    }

    @Override
    public TokenInfoVO renewToken() {
        // 刷新当前 Token 的活跃时间，实现滑动会话续签
        return buildTokenInfo(true);
    }

    /**
     * 组装当前 Token 的有效期信息
     *
     * @param renew 是否在查询前先续签活跃时间
     */
    private TokenInfoVO buildTokenInfo(boolean renew) {
        if (renew) {
            StpUtil.updateLastActiveToNow();
        }
        return new TokenInfoVO(
                StpUtil.getTokenValue(),
                StpUtil.stpLogic.getConfigOrGlobal().getTokenName(),
                StpUtil.getTokenTimeout(),
                StpUtil.getTokenActiveTimeout()
        );
    }
}
