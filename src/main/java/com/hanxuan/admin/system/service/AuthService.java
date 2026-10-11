package com.hanxuan.admin.system.service;

import com.hanxuan.admin.system.dto.LoginDTO;
import com.hanxuan.admin.system.vo.LoginVO;
import com.hanxuan.admin.system.vo.TokenInfoVO;

/**
 * 认证服务
 *
 * @author hanxuan
 */
public interface AuthService {

    /**
     * 登录
     */
    LoginVO login(LoginDTO dto, jakarta.servlet.http.HttpServletRequest request);

    /**
     * 登出
     */
    void logout();

    /**
     * 获取当前登录用户信息
     */
    LoginVO.UserInfo getUserInfo();

    /**
     * 获取当前 Token 的有效期信息（剩余绝对有效期、剩余活跃有效期）
     */
    TokenInfoVO getTokenInfo();

    /**
     * 主动续签：刷新当前 Token 的活跃时间（滑动会话）
     */
    TokenInfoVO renewToken();
}
