package com.hanxuan.admin.system.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.stp.StpUtil;
import com.hanxuan.admin.common.annotation.RateLimiter;
import com.hanxuan.admin.common.result.Result;
import com.hanxuan.admin.system.dto.LoginDTO;
import com.hanxuan.admin.system.service.AuthService;
import com.hanxuan.admin.system.service.SysLoginLogService;
import com.hanxuan.admin.system.vo.LoginVO;
import com.hanxuan.admin.system.vo.TokenInfoVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 *
 * @author hanxuan
 */
@Tag(name = "认证管理", description = "登录、登出、获取用户信息")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final SysLoginLogService loginLogService;

    @SaIgnore
    @PostMapping("/login")
    @Operation(summary = "用户登录")
    @RateLimiter(count = 5, seconds = 60, message = "登录尝试过于频繁，请1分钟后再试")
    public Result<LoginVO> login(@Valid @RequestBody LoginDTO dto, HttpServletRequest request) {
        String username = dto.getUsername();
        try {
            LoginVO result = authService.login(dto, request);
            // 记录登录成功日志
            loginLogService.recordLoginLog(username, 1, null, request);
            return Result.success(result);
        } catch (Exception e) {
            // 记录登录失败日志
            loginLogService.recordLoginLog(username, 0, e.getMessage(), request);
            throw e;
        }
    }

    @PostMapping("/logout")
    @Operation(summary = "用户登出")
    public Result<Void> logout(HttpServletRequest request) {
        // 记录登出日志
        try {
            String username = (String) StpUtil.getSession().get("username");
            loginLogService.recordLoginLog(username, 1, "登出", request);
        } catch (Exception ignored) {
        }
        authService.logout();
        return Result.success();
    }

    @GetMapping("/userInfo")
    @Operation(summary = "获取当前登录用户信息")
    public Result<LoginVO.UserInfo> getUserInfo() {
        return Result.success(authService.getUserInfo());
    }

    @GetMapping("/tokenInfo")
    @Operation(summary = "获取当前 Token 的有效期信息")
    public Result<TokenInfoVO> getTokenInfo() {
        return Result.success(authService.getTokenInfo());
    }

    @PostMapping("/renew")
    @Operation(summary = "主动续签当前 Token（刷新活跃时间）")
    public Result<TokenInfoVO> renew() {
        return Result.success(authService.renewToken());
    }
}
