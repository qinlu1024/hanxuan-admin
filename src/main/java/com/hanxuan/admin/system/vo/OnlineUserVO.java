package com.hanxuan.admin.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 在线用户 VO
 *
 * @author hanxuan
 */
@Data
@Schema(description = "在线用户")
public class OnlineUserVO {

    @Schema(description = "会话 Token（用于强退）")
    private String tokenValue;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "登录 IP")
    private String loginIp;

    @Schema(description = "浏览器")
    private String browser;

    @Schema(description = "操作系统")
    private String os;

    @Schema(description = "登录时间")
    private String loginTime;

    @Schema(description = "剩余活跃有效期（秒）")
    private Long activeTimeout;
}
