package com.hanxuan.admin.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 路由 VO（前端动态路由用）
 *
 * @author hanxuan
 */
@Data
@Schema(description = "路由信息")
public class RouterVO {

    @Schema(description = "路由名称")
    private String name;

    @Schema(description = "路由路径")
    private String path;

    @Schema(description = "组件路径")
    private String component;

    @Schema(description = "路由元信息")
    private Meta meta;

    @Schema(description = "子路由")
    private List<RouterVO> children;

    @Data
    @Schema(description = "路由元信息")
    public static class Meta {
        @Schema(description = "菜单标题")
        private String title;

        @Schema(description = "菜单图标")
        private String icon;

        @Schema(description = "权限标识")
        private List<String> roles;

        @Schema(description = "是否隐藏")
        private Boolean hidden;

        @Schema(description = "是否缓存")
        private Boolean keepAlive;
    }
}
