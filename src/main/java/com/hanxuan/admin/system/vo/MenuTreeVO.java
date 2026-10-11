package com.hanxuan.admin.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 菜单树 VO
 *
 * @author hanxuan
 */
@Data
@Schema(description = "菜单树")
public class MenuTreeVO {

    @Schema(description = "菜单ID")
    private Long id;

    @Schema(description = "父级ID")
    private Long parentId;

    @Schema(description = "菜单名称")
    private String menuName;

    @Schema(description = "菜单类型 1-目录 2-菜单 3-按钮")
    private Integer menuType;

    @Schema(description = "路由地址")
    private String path;

    @Schema(description = "组件路径")
    private String component;

    @Schema(description = "权限标识")
    private String perms;

    @Schema(description = "菜单图标")
    private String icon;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "是否可见 1-显示 0-隐藏")
    private Integer visible;

    @Schema(description = "状态 1-正常 0-禁用")
    private Integer status;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "子菜单")
    private List<MenuTreeVO> children;
}
