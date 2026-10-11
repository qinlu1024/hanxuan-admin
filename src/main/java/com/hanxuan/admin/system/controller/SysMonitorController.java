package com.hanxuan.admin.system.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.hanxuan.admin.common.annotation.OperationLog;
import com.hanxuan.admin.common.result.Result;
import com.hanxuan.admin.system.service.SysMonitorService;
import com.hanxuan.admin.system.vo.OnlineUserVO;
import com.hanxuan.admin.system.vo.ServerInfoVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 服务监控控制器
 *
 * @author hanxuan
 */
@Tag(name = "服务监控", description = "服务器运行信息、在线用户管理")
@RestController
@RequestMapping("/monitor")
@RequiredArgsConstructor
public class SysMonitorController {

    private final SysMonitorService monitorService;

    @GetMapping("/server")
    @SaCheckPermission("monitor:server:list")
    @Operation(summary = "获取服务器运行信息")
    public Result<ServerInfoVO> serverInfo() {
        return Result.success(monitorService.getServerInfo());
    }

    @GetMapping("/online")
    @SaCheckPermission("monitor:online:list")
    @Operation(summary = "获取在线用户列表")
    public Result<List<OnlineUserVO>> onlineUsers() {
        return Result.success(monitorService.listOnlineUsers());
    }

    @DeleteMapping("/online/{tokenValue}")
    @SaCheckPermission("monitor:online:delete")
    @OperationLog(module = "在线用户", type = "删除", description = "强退在线用户")
    @Operation(summary = "强退在线用户")
    public Result<Void> forceLogout(@PathVariable String tokenValue) {
        monitorService.forceLogout(tokenValue);
        return Result.success();
    }
}
