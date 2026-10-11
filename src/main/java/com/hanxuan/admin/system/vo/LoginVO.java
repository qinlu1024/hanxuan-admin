package com.hanxuan.admin.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 登录返回 VO
 *
 * @author hanxuan
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "登录返回信息")
public class LoginVO {

    @Schema(description = "Token 值")
    private String token;

    @Schema(description = "用户信息")
    private UserInfo userInfo;

    @Schema(description = "角色列表")
    private List<String> roles;

    @Schema(description = "权限标识列表")
    private List<String> permissions;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "用户信息")
    public static class UserInfo {
        @Schema(description = "用户ID")
        private Long id;

        @Schema(description = "用户名")
        private String username;

        @Schema(description = "昵称")
        private String nickname;

        @Schema(description = "头像")
        private String avatar;
    }
}
