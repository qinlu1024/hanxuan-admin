package com.hanxuan.admin.system.service;

import com.hanxuan.admin.system.vo.OnlineUserVO;
import com.hanxuan.admin.system.vo.ServerInfoVO;

import java.util.List;

/**
 * 服务监控服务
 *
 * @author hanxuan
 */
public interface SysMonitorService {

    /**
     * 获取服务器运行信息（CPU/内存/JVM/系统/磁盘）
     */
    ServerInfoVO getServerInfo();

    /**
     * 获取当前在线用户会话列表
     */
    List<OnlineUserVO> listOnlineUsers();

    /**
     * 强退指定会话
     *
     * @param tokenValue 会话 Token
     */
    void forceLogout(String tokenValue);
}
