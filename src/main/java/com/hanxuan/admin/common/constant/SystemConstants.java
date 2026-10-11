package com.hanxuan.admin.common.constant;

/**
 * 系统常量
 *
 * @author hanxuan
 */
public interface SystemConstants {

    /**
     * 超级管理员角色标识
     */
    String SUPER_ADMIN_ROLE = "super_admin";

    /**
     * 超级管理员用户ID
     */
    Long SUPER_ADMIN_USER_ID = 1L;

    /**
     * 默认密码
     */
    String DEFAULT_PASSWORD = "123456";

    /**
     * 根节点ID
     */
    Long ROOT_ID = 0L;

    /**
     * 菜单类型：目录
     */
    Integer MENU_TYPE_DIR = 1;

    /**
     * 菜单类型：菜单
     */
    Integer MENU_TYPE_MENU = 2;

    /**
     * 菜单类型：按钮
     */
    Integer MENU_TYPE_BUTTON = 3;

    /**
     * 用户状态：正常
     */
    Integer USER_STATUS_NORMAL = 1;

    /**
     * 用户状态：禁用
     */
    Integer USER_STATUS_DISABLED = 0;
}
