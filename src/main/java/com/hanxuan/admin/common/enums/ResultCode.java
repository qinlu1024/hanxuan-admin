package com.hanxuan.admin.common.enums;

import lombok.Getter;

/**
 * 结果码枚举
 *
 * @author hanxuan
 */
@Getter
public enum ResultCode {

    SUCCESS(200, "操作成功"),
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未登录或登录已过期"),
    FORBIDDEN(403, "权限不足"),
    NOT_FOUND(404, "资源不存在"),
    INTERNAL_ERROR(500, "系统内部错误"),

    // 业务错误码 1xxx
    USER_NOT_FOUND(1001, "用户不存在"),
    USER_PASSWORD_ERROR(1002, "密码错误"),
    USER_DISABLED(1003, "账号已被禁用"),
    USERNAME_EXISTS(1004, "用户名已存在"),
    ROLE_EXISTS(1005, "角色标识已存在"),
    MENU_EXISTS(1006, "菜单标识已存在"),
    DICT_TYPE_EXISTS(1007, "字典类型已存在"),
    DICT_TYPE_NOT_FOUND(1008, "字典类型不存在"),
    DICT_DATA_EXISTS(1009, "字典数据已存在"),
    TOO_MANY_REQUESTS(1010, "请求过于频繁，请稍后再试"),
    ;

    private final Integer code;
    private final String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
