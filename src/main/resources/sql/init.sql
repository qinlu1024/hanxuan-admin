-- ============================================
-- 晗煊后台管理系统 - 数据库初始化脚本
-- 数据库: PostgreSQL
-- 版本: 1.0.0
-- ============================================

-- 创建数据库 (请先手动执行: CREATE DATABASE hanxuan_admin;)
-- \c hanxuan_admin;

-- --------------------------------------------
-- 1. 用户表
-- --------------------------------------------
DROP TABLE IF EXISTS sys_user CASCADE;
CREATE TABLE sys_user (
    id BIGINT NOT NULL,
    username VARCHAR(64) NOT NULL,
    password VARCHAR(128) NOT NULL,
    nickname VARCHAR(64),
    avatar VARCHAR(255),
    phone VARCHAR(20),
    email VARCHAR(64),
    gender INT DEFAULT 0,
    status INT DEFAULT 1,
    remark VARCHAR(255),
    create_time TIMESTAMP,
    update_time TIMESTAMP,
    deleted INT DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE (username)
);
COMMENT ON TABLE sys_user IS '用户表';
COMMENT ON COLUMN sys_user.id IS '主键ID';
COMMENT ON COLUMN sys_user.username IS '用户名';
COMMENT ON COLUMN sys_user.password IS '密码';
COMMENT ON COLUMN sys_user.nickname IS '昵称';
COMMENT ON COLUMN sys_user.avatar IS '头像';
COMMENT ON COLUMN sys_user.phone IS '手机号';
COMMENT ON COLUMN sys_user.email IS '邮箱';
COMMENT ON COLUMN sys_user.gender IS '性别 0-未知 1-男 2-女';
COMMENT ON COLUMN sys_user.status IS '状态 1-正常 0-禁用';
COMMENT ON COLUMN sys_user.remark IS '备注';
COMMENT ON COLUMN sys_user.create_time IS '创建时间';
COMMENT ON COLUMN sys_user.update_time IS '更新时间';
COMMENT ON COLUMN sys_user.deleted IS '逻辑删除 0-未删除 1-已删除';

CREATE INDEX idx_sys_user_username ON sys_user(username);
CREATE INDEX idx_sys_user_status ON sys_user(status);

-- --------------------------------------------
-- 2. 角色表
-- --------------------------------------------
DROP TABLE IF EXISTS sys_role CASCADE;
CREATE TABLE sys_role (
    id BIGINT NOT NULL,
    role_name VARCHAR(64) NOT NULL,
    role_code VARCHAR(64) NOT NULL,
    sort INT DEFAULT 0,
    status INT DEFAULT 1,
    remark VARCHAR(255),
    create_time TIMESTAMP,
    update_time TIMESTAMP,
    deleted INT DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE (role_code)
);
COMMENT ON TABLE sys_role IS '角色表';
COMMENT ON COLUMN sys_role.id IS '主键ID';
COMMENT ON COLUMN sys_role.role_name IS '角色名称';
COMMENT ON COLUMN sys_role.role_code IS '角色标识';
COMMENT ON COLUMN sys_role.sort IS '排序';
COMMENT ON COLUMN sys_role.status IS '状态 1-正常 0-禁用';
COMMENT ON COLUMN sys_role.remark IS '备注';
COMMENT ON COLUMN sys_role.create_time IS '创建时间';
COMMENT ON COLUMN sys_role.update_time IS '更新时间';
COMMENT ON COLUMN sys_role.deleted IS '逻辑删除 0-未删除 1-已删除';

CREATE INDEX idx_sys_role_code ON sys_role(role_code);

-- --------------------------------------------
-- 3. 菜单表
-- --------------------------------------------
DROP TABLE IF EXISTS sys_menu CASCADE;
CREATE TABLE sys_menu (
    id BIGINT NOT NULL,
    parent_id BIGINT DEFAULT 0,
    menu_name VARCHAR(64) NOT NULL,
    menu_type INT NOT NULL,
    path VARCHAR(255),
    component VARCHAR(255),
    perms VARCHAR(128),
    icon VARCHAR(64),
    sort INT DEFAULT 0,
    visible INT DEFAULT 1,
    status INT DEFAULT 1,
    remark VARCHAR(255),
    create_time TIMESTAMP,
    update_time TIMESTAMP,
    deleted INT DEFAULT 0,
    PRIMARY KEY (id)
);
COMMENT ON TABLE sys_menu IS '菜单表';
COMMENT ON COLUMN sys_menu.id IS '主键ID';
COMMENT ON COLUMN sys_menu.parent_id IS '父级ID';
COMMENT ON COLUMN sys_menu.menu_name IS '菜单名称';
COMMENT ON COLUMN sys_menu.menu_type IS '菜单类型 1-目录 2-菜单 3-按钮';
COMMENT ON COLUMN sys_menu.path IS '路由地址';
COMMENT ON COLUMN sys_menu.component IS '组件路径';
COMMENT ON COLUMN sys_menu.perms IS '权限标识';
COMMENT ON COLUMN sys_menu.icon IS '菜单图标';
COMMENT ON COLUMN sys_menu.sort IS '排序';
COMMENT ON COLUMN sys_menu.visible IS '是否可见 1-显示 0-隐藏';
COMMENT ON COLUMN sys_menu.status IS '状态 1-正常 0-禁用';
COMMENT ON COLUMN sys_menu.remark IS '备注';
COMMENT ON COLUMN sys_menu.create_time IS '创建时间';
COMMENT ON COLUMN sys_menu.update_time IS '更新时间';
COMMENT ON COLUMN sys_menu.deleted IS '逻辑删除 0-未删除 1-已删除';

CREATE INDEX idx_sys_menu_parent_id ON sys_menu(parent_id);
CREATE INDEX idx_sys_menu_type ON sys_menu(menu_type);

-- --------------------------------------------
-- 4. 用户角色关联表
-- --------------------------------------------
DROP TABLE IF EXISTS sys_user_role CASCADE;
CREATE TABLE sys_user_role (
    id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (id)
);
COMMENT ON TABLE sys_user_role IS '用户角色关联表';
COMMENT ON COLUMN sys_user_role.id IS '主键ID';
COMMENT ON COLUMN sys_user_role.user_id IS '用户ID';
COMMENT ON COLUMN sys_user_role.role_id IS '角色ID';

CREATE INDEX idx_user_role_user_id ON sys_user_role(user_id);
CREATE INDEX idx_user_role_role_id ON sys_user_role(role_id);

-- --------------------------------------------
-- 5. 角色菜单关联表
-- --------------------------------------------
DROP TABLE IF EXISTS sys_role_menu CASCADE;
CREATE TABLE sys_role_menu (
    id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    menu_id BIGINT NOT NULL,
    PRIMARY KEY (id)
);
COMMENT ON TABLE sys_role_menu IS '角色菜单关联表';
COMMENT ON COLUMN sys_role_menu.id IS '主键ID';
COMMENT ON COLUMN sys_role_menu.role_id IS '角色ID';
COMMENT ON COLUMN sys_role_menu.menu_id IS '菜单ID';

CREATE INDEX idx_role_menu_role_id ON sys_role_menu(role_id);
CREATE INDEX idx_role_menu_menu_id ON sys_role_menu(menu_id);

-- ============================================
-- 初始化数据
-- ============================================

-- 超级管理员用户 (密码: admin123，使用 BCrypt 加密)
INSERT INTO sys_user (id, username, password, nickname, status, create_time, update_time, deleted)
VALUES (1, 'admin', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '超级管理员', 1, NOW(), NOW(), 0);

-- 超级管理员角色
INSERT INTO sys_role (id, role_name, role_code, sort, status, create_time, update_time, deleted)
VALUES (1, '超级管理员', 'super_admin', 1, 1, NOW(), NOW(), 0);

-- 用户角色关联
INSERT INTO sys_user_role (id, user_id, role_id)
VALUES (1, 1, 1);

-- 初始化菜单数据
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, path, component, perms, icon, sort, visible, status, create_time, update_time, deleted) VALUES
-- 一级目录
(1, 0, '系统管理', 1, '/system', 'Layout', NULL, 'setting', 1, 1, 1, NOW(), NOW(), 0),
-- 二级菜单
(11, 1, '用户管理', 2, '/system/user', 'system/user/index', 'system:user:list', 'user', 1, 1, 1, NOW(), NOW(), 0),
(12, 1, '角色管理', 2, '/system/role', 'system/role/index', 'system:role:list', 'team', 2, 1, 1, NOW(), NOW(), 0),
(13, 1, '菜单管理', 2, '/system/menu', 'system/menu/index', 'system:menu:list', 'menu', 3, 1, 1, NOW(), NOW(), 0),
-- 用户管理按钮
(111, 11, '用户新增', 3, NULL, NULL, 'system:user:add', NULL, 1, 1, 1, NOW(), NOW(), 0),
(112, 11, '用户修改', 3, NULL, NULL, 'system:user:edit', NULL, 2, 1, 1, NOW(), NOW(), 0),
(113, 11, '用户删除', 3, NULL, NULL, 'system:user:delete', NULL, 3, 1, 1, NOW(), NOW(), 0),
(114, 11, '用户导出', 3, NULL, NULL, 'system:user:export', NULL, 4, 1, 1, NOW(), NOW(), 0),
-- 角色管理按钮
(121, 12, '角色新增', 3, NULL, NULL, 'system:role:add', NULL, 1, 1, 1, NOW(), NOW(), 0),
(122, 12, '角色修改', 3, NULL, NULL, 'system:role:edit', NULL, 2, 1, 1, NOW(), NOW(), 0),
(123, 12, '角色删除', 3, NULL, NULL, 'system:role:delete', NULL, 3, 1, 1, NOW(), NOW(), 0),
-- 菜单管理按钮
(131, 13, '菜单新增', 3, NULL, NULL, 'system:menu:add', NULL, 1, 1, 1, NOW(), NOW(), 0),
(132, 13, '菜单修改', 3, NULL, NULL, 'system:menu:edit', NULL, 2, 1, 1, NOW(), NOW(), 0),
(133, 13, '菜单删除', 3, NULL, NULL, 'system:menu:delete', NULL, 3, 1, 1, NOW(), NOW(), 0);

-- 超级管理员角色菜单关联 (所有菜单)
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES
(1, 1, 1),
(2, 1, 11),
(3, 1, 12),
(4, 1, 13),
(5, 1, 111),
(6, 1, 112),
(7, 1, 113),
(8, 1, 114),
(9, 1, 121),
(10, 1, 122),
(11, 1, 123),
(12, 1, 131),
(13, 1, 132),
(14, 1, 133),
(15, 1, 2),
(16, 1, 21),
(17, 1, 22),
(18, 1, 211),
(19, 1, 221);

-- --------------------------------------------
-- 6. 操作日志表
-- --------------------------------------------
DROP TABLE IF EXISTS sys_operation_log CASCADE;
CREATE TABLE sys_operation_log (
    id BIGINT NOT NULL,
    module VARCHAR(64),
    operation_type VARCHAR(32),
    description VARCHAR(255),
    request_method VARCHAR(10),
    request_url VARCHAR(255),
    request_params TEXT,
    response_result TEXT,
    user_id BIGINT,
    username VARCHAR(64),
    operation_ip VARCHAR(64),
    operation_location VARCHAR(128),
    browser VARCHAR(64),
    os VARCHAR(64),
    status INT DEFAULT 1,
    error_msg VARCHAR(500),
    cost_time BIGINT,
    operation_time TIMESTAMP,
    deleted INT DEFAULT 0,
    PRIMARY KEY (id)
);
COMMENT ON TABLE sys_operation_log IS '操作日志表';
COMMENT ON COLUMN sys_operation_log.id IS '主键ID';
COMMENT ON COLUMN sys_operation_log.module IS '操作模块';
COMMENT ON COLUMN sys_operation_log.operation_type IS '操作类型';
COMMENT ON COLUMN sys_operation_log.description IS '操作描述';
COMMENT ON COLUMN sys_operation_log.request_method IS '请求方式';
COMMENT ON COLUMN sys_operation_log.request_url IS '请求URL';
COMMENT ON COLUMN sys_operation_log.request_params IS '请求参数';
COMMENT ON COLUMN sys_operation_log.response_result IS '响应结果';
COMMENT ON COLUMN sys_operation_log.user_id IS '操作人ID';
COMMENT ON COLUMN sys_operation_log.username IS '操作人用户名';
COMMENT ON COLUMN sys_operation_log.operation_ip IS '操作IP';
COMMENT ON COLUMN sys_operation_log.operation_location IS '操作地点';
COMMENT ON COLUMN sys_operation_log.browser IS '浏览器';
COMMENT ON COLUMN sys_operation_log.os IS '操作系统';
COMMENT ON COLUMN sys_operation_log.status IS '执行状态 1-成功 0-失败';
COMMENT ON COLUMN sys_operation_log.error_msg IS '错误消息';
COMMENT ON COLUMN sys_operation_log.cost_time IS '执行耗时(毫秒)';
COMMENT ON COLUMN sys_operation_log.operation_time IS '操作时间';
COMMENT ON COLUMN sys_operation_log.deleted IS '逻辑删除 0-未删除 1-已删除';

CREATE INDEX idx_operation_log_user_id ON sys_operation_log(user_id);
CREATE INDEX idx_operation_log_username ON sys_operation_log(username);
CREATE INDEX idx_operation_log_operation_time ON sys_operation_log(operation_time);
CREATE INDEX idx_operation_log_status ON sys_operation_log(status);

-- --------------------------------------------
-- 7. 登录日志表
-- --------------------------------------------
DROP TABLE IF EXISTS sys_login_log CASCADE;
CREATE TABLE sys_login_log (
    id BIGINT NOT NULL,
    username VARCHAR(64),
    login_ip VARCHAR(64),
    login_location VARCHAR(128),
    browser VARCHAR(64),
    os VARCHAR(64),
    status INT DEFAULT 1,
    fail_reason VARCHAR(255),
    login_time TIMESTAMP,
    deleted INT DEFAULT 0,
    PRIMARY KEY (id)
);
COMMENT ON TABLE sys_login_log IS '登录日志表';
COMMENT ON COLUMN sys_login_log.id IS '主键ID';
COMMENT ON COLUMN sys_login_log.username IS '用户名';
COMMENT ON COLUMN sys_login_log.login_ip IS '登录IP';
COMMENT ON COLUMN sys_login_log.login_location IS '登录地点';
COMMENT ON COLUMN sys_login_log.browser IS '浏览器';
COMMENT ON COLUMN sys_login_log.os IS '操作系统';
COMMENT ON COLUMN sys_login_log.status IS '登录状态 1-成功 0-失败';
COMMENT ON COLUMN sys_login_log.fail_reason IS '失败原因';
COMMENT ON COLUMN sys_login_log.login_time IS '登录时间';
COMMENT ON COLUMN sys_login_log.deleted IS '逻辑删除 0-未删除 1-已删除';

CREATE INDEX idx_login_log_username ON sys_login_log(username);
CREATE INDEX idx_login_log_login_time ON sys_login_log(login_time);
CREATE INDEX idx_login_log_status ON sys_login_log(status);

-- --------------------------------------------
-- 8. 字典类型表
-- --------------------------------------------
DROP TABLE IF EXISTS sys_dict_type CASCADE;
CREATE TABLE sys_dict_type (
    id BIGINT NOT NULL,
    dict_name VARCHAR(100) NOT NULL,
    dict_type VARCHAR(100) NOT NULL,
    status INT DEFAULT 1,
    remark VARCHAR(255),
    create_time TIMESTAMP,
    update_time TIMESTAMP,
    deleted INT DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE (dict_type)
);
COMMENT ON TABLE sys_dict_type IS '字典类型表';
COMMENT ON COLUMN sys_dict_type.id IS '字典主键';
COMMENT ON COLUMN sys_dict_type.dict_name IS '字典名称';
COMMENT ON COLUMN sys_dict_type.dict_type IS '字典类型';
COMMENT ON COLUMN sys_dict_type.status IS '状态 1-正常 0-停用';
COMMENT ON COLUMN sys_dict_type.remark IS '备注';
COMMENT ON COLUMN sys_dict_type.create_time IS '创建时间';
COMMENT ON COLUMN sys_dict_type.update_time IS '更新时间';
COMMENT ON COLUMN sys_dict_type.deleted IS '逻辑删除 0-未删除 1-已删除';

CREATE INDEX idx_dict_type ON sys_dict_type(dict_type);

-- --------------------------------------------
-- 9. 字典数据表
-- --------------------------------------------
DROP TABLE IF EXISTS sys_dict_data CASCADE;
CREATE TABLE sys_dict_data (
    id BIGINT NOT NULL,
    sort INT DEFAULT 0,
    dict_label VARCHAR(100) NOT NULL,
    dict_value VARCHAR(100) NOT NULL,
    dict_type VARCHAR(100) NOT NULL,
    css_class VARCHAR(100),
    list_class VARCHAR(100),
    is_default INT DEFAULT 0,
    status INT DEFAULT 1,
    remark VARCHAR(255),
    create_time TIMESTAMP,
    update_time TIMESTAMP,
    deleted INT DEFAULT 0,
    PRIMARY KEY (id)
);
COMMENT ON TABLE sys_dict_data IS '字典数据表';
COMMENT ON COLUMN sys_dict_data.id IS '字典编码';
COMMENT ON COLUMN sys_dict_data.sort IS '字典排序';
COMMENT ON COLUMN sys_dict_data.dict_label IS '字典标签';
COMMENT ON COLUMN sys_dict_data.dict_value IS '字典键值';
COMMENT ON COLUMN sys_dict_data.dict_type IS '字典类型';
COMMENT ON COLUMN sys_dict_data.css_class IS '样式属性';
COMMENT ON COLUMN sys_dict_data.list_class IS '表格回显样式';
COMMENT ON COLUMN sys_dict_data.is_default IS '是否默认 1-是 0-否';
COMMENT ON COLUMN sys_dict_data.status IS '状态 1-正常 0-停用';
COMMENT ON COLUMN sys_dict_data.remark IS '备注';
COMMENT ON COLUMN sys_dict_data.create_time IS '创建时间';
COMMENT ON COLUMN sys_dict_data.update_time IS '更新时间';
COMMENT ON COLUMN sys_dict_data.deleted IS '逻辑删除 0-未删除 1-已删除';

CREATE INDEX idx_dict_data_type ON sys_dict_data(dict_type);

-- --------------------------------------------
-- 初始化字典数据
-- --------------------------------------------
INSERT INTO sys_dict_type (id, dict_name, dict_type, status, remark, create_time, update_time, deleted) VALUES
(1, '用户性别', 'sys_user_sex', 1, '用户性别列表', NOW(), NOW(), 0),
(2, '系统开关', 'sys_normal_disable', 1, '系统开关列表', NOW(), NOW(), 0),
(3, '操作类型', 'sys_operation_type', 1, '操作日志类型', NOW(), NOW(), 0);

INSERT INTO sys_dict_data (id, sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, remark, create_time, update_time, deleted) VALUES
-- 用户性别
(1, 1, '男', '1', 'sys_user_sex', '', 'primary', 0, 1, '性别男', NOW(), NOW(), 0),
(2, 2, '女', '2', 'sys_user_sex', '', 'danger', 0, 1, '性别女', NOW(), NOW(), 0),
(3, 3, '未知', '0', 'sys_user_sex', '', 'info', 1, 1, '性别未知', NOW(), NOW(), 0),
-- 系统开关
(10, 1, '正常', '1', 'sys_normal_disable', '', 'success', 1, 1, '正常状态', NOW(), NOW(), 0),
(11, 2, '停用', '0', 'sys_normal_disable', '', 'danger', 0, 1, '停用状态', NOW(), NOW(), 0),
-- 操作类型
(20, 1, '新增', '新增', 'sys_operation_type', '', '', 0, 1, '新增操作', NOW(), NOW(), 0),
(21, 2, '修改', '修改', 'sys_operation_type', '', '', 0, 1, '修改操作', NOW(), NOW(), 0),
(22, 3, '删除', '删除', 'sys_operation_type', '', '', 0, 1, '删除操作', NOW(), NOW(), 0),
(23, 4, '授权', '授权', 'sys_operation_type', '', '', 0, 1, '授权操作', NOW(), NOW(), 0),
(24, 5, '导出', '导出', 'sys_operation_type', '', '', 0, 1, '导出操作', NOW(), NOW(), 0),
(25, 6, '清空', '清空', 'sys_operation_type', '', '', 0, 1, '清空操作', NOW(), NOW(), 0);

-- --------------------------------------------
-- 追加菜单：系统监控目录 + 操作日志 + 登录日志
-- --------------------------------------------
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, path, component, perms, icon, sort, visible, status, create_time, update_time, deleted) VALUES
(2, 0, '系统监控', 1, '/monitor', 'Layout', NULL, 'monitor', 2, 1, 1, NOW(), NOW(), 0),
(21, 2, '操作日志', 2, '/monitor/operationLog', 'monitor/operationLog/index', 'monitor:operationLog:list', 'form', 1, 1, 1, NOW(), NOW(), 0),
(22, 2, '登录日志', 2, '/monitor/loginLog', 'monitor/loginLog/index', 'monitor:loginLog:list', 'logininfor', 2, 1, 1, NOW(), NOW(), 0),
-- 操作日志按钮
(211, 21, '操作日志删除', 3, NULL, NULL, 'monitor:operationLog:delete', NULL, 1, 1, 1, NOW(), NOW(), 0),
-- 登录日志按钮
(221, 22, '登录日志删除', 3, NULL, NULL, 'monitor:loginLog:delete', NULL, 1, 1, 1, NOW(), NOW(), 0)
ON CONFLICT (id) DO NOTHING;

-- --------------------------------------------
-- 追加菜单：字典管理
-- --------------------------------------------
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, path, component, perms, icon, sort, visible, status, create_time, update_time, deleted) VALUES
(14, 1, '字典管理', 2, '/system/dict', 'system/dict/index', 'system:dict:list', 'dict', 4, 1, 1, NOW(), NOW(), 0),
-- 字典管理按钮
(141, 14, '字典新增', 3, NULL, NULL, 'system:dict:add', NULL, 1, 1, 1, NOW(), NOW(), 0),
(142, 14, '字典修改', 3, NULL, NULL, 'system:dict:edit', NULL, 2, 1, 1, NOW(), NOW(), 0),
(143, 14, '字典删除', 3, NULL, NULL, 'system:dict:delete', NULL, 3, 1, 1, NOW(), NOW(), 0)
ON CONFLICT (id) DO NOTHING;

-- 超级管理员角色菜单关联 (新增的字典管理菜单)
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES
(20, 1, 14),
(21, 1, 141),
(22, 1, 142),
(23, 1, 143)
ON CONFLICT (id) DO NOTHING;

-- --------------------------------------------
-- 10. 部门表
-- --------------------------------------------
DROP TABLE IF EXISTS sys_dept CASCADE;
CREATE TABLE sys_dept (
    id BIGINT NOT NULL,
    parent_id BIGINT DEFAULT 0,
    ancestors VARCHAR(500) DEFAULT '',
    dept_name VARCHAR(50) NOT NULL,
    sort INT DEFAULT 0,
    leader VARCHAR(20),
    phone VARCHAR(20),
    email VARCHAR(50),
    status INT DEFAULT 1,
    remark VARCHAR(255),
    create_time TIMESTAMP,
    update_time TIMESTAMP,
    deleted INT DEFAULT 0,
    PRIMARY KEY (id)
);
COMMENT ON TABLE sys_dept IS '部门表';
COMMENT ON COLUMN sys_dept.id IS '部门ID';
COMMENT ON COLUMN sys_dept.parent_id IS '父部门ID';
COMMENT ON COLUMN sys_dept.ancestors IS '祖级列表';
COMMENT ON COLUMN sys_dept.dept_name IS '部门名称';
COMMENT ON COLUMN sys_dept.sort IS '显示顺序';
COMMENT ON COLUMN sys_dept.leader IS '负责人';
COMMENT ON COLUMN sys_dept.phone IS '联系电话';
COMMENT ON COLUMN sys_dept.email IS '邮箱';
COMMENT ON COLUMN sys_dept.status IS '状态 1-正常 0-停用';
COMMENT ON COLUMN sys_dept.remark IS '备注';
COMMENT ON COLUMN sys_dept.create_time IS '创建时间';
COMMENT ON COLUMN sys_dept.update_time IS '更新时间';
COMMENT ON COLUMN sys_dept.deleted IS '逻辑删除 0-未删除 1-已删除';

CREATE INDEX idx_dept_parent_id ON sys_dept(parent_id);

-- 初始化部门数据
INSERT INTO sys_dept (id, parent_id, ancestors, dept_name, sort, leader, phone, email, status, create_time, update_time, deleted) VALUES
(1, 0, '0', '总公司', 0, '张三', '13800138000', 'admin@example.com', 1, NOW(), NOW(), 0),
(11, 1, '0,1', '研发部', 1, '李四', '13800138001', 'dev@example.com', 1, NOW(), NOW(), 0),
(12, 1, '0,1', '市场部', 2, '王五', '13800138002', 'market@example.com', 1, NOW(), NOW(), 0),
(13, 1, '0,1', '财务部', 3, '赵六', '13800138003', 'finance@example.com', 1, NOW(), NOW(), 0),
(111, 11, '0,1,11', '前端组', 1, NULL, NULL, NULL, 1, NOW(), NOW(), 0),
(112, 11, '0,1,11', '后端组', 2, NULL, NULL, NULL, 1, NOW(), NOW(), 0);

-- 用户表增加部门ID字段
ALTER TABLE sys_user ADD COLUMN IF NOT EXISTS dept_id BIGINT;
COMMENT ON COLUMN sys_user.dept_id IS '部门ID';
CREATE INDEX IF NOT EXISTS idx_user_dept_id ON sys_user(dept_id);

-- 更新admin用户部门
UPDATE sys_user SET dept_id = 1 WHERE id = 1;

-- 追加菜单：部门管理
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, path, component, perms, icon, sort, visible, status, create_time, update_time, deleted) VALUES
(15, 1, '部门管理', 2, '/system/dept', 'system/dept/index', 'system:dept:list', 'dept', 3, 1, 1, NOW(), NOW(), 0),
(151, 15, '部门新增', 3, NULL, NULL, 'system:dept:add', NULL, 1, 1, 1, NOW(), NOW(), 0),
(152, 15, '部门修改', 3, NULL, NULL, 'system:dept:edit', NULL, 2, 1, 1, NOW(), NOW(), 0),
(153, 15, '部门删除', 3, NULL, NULL, 'system:dept:delete', NULL, 3, 1, 1, NOW(), NOW(), 0)
ON CONFLICT (id) DO NOTHING;

-- 超级管理员角色菜单关联 (新增的部门管理菜单)
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES
(24, 1, 15),
(25, 1, 151),
(26, 1, 152),
(27, 1, 153)
ON CONFLICT (id) DO NOTHING;

-- --------------------------------------------
-- 11. 文件表
-- --------------------------------------------
DROP TABLE IF EXISTS sys_file CASCADE;
CREATE TABLE sys_file (
    id BIGINT NOT NULL,
    original_name VARCHAR(255) NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_suffix VARCHAR(20),
    file_size BIGINT,
    content_type VARCHAR(100),
    file_path VARCHAR(500) NOT NULL,
    storage_type VARCHAR(20) DEFAULT 'local',
    create_by BIGINT,
    create_time TIMESTAMP,
    update_time TIMESTAMP,
    deleted INT DEFAULT 0,
    PRIMARY KEY (id)
);
COMMENT ON TABLE sys_file IS '文件表';
COMMENT ON COLUMN sys_file.id IS '文件ID';
COMMENT ON COLUMN sys_file.original_name IS '原文件名';
COMMENT ON COLUMN sys_file.file_name IS '存储文件名';
COMMENT ON COLUMN sys_file.file_suffix IS '文件后缀';
COMMENT ON COLUMN sys_file.file_size IS '文件大小（字节）';
COMMENT ON COLUMN sys_file.content_type IS '文件类型（MIME）';
COMMENT ON COLUMN sys_file.file_path IS '存储路径（相对路径）';
COMMENT ON COLUMN sys_file.storage_type IS '存储类型 local-本地';
COMMENT ON COLUMN sys_file.create_by IS '上传人ID';
COMMENT ON COLUMN sys_file.create_time IS '创建时间';
COMMENT ON COLUMN sys_file.update_time IS '更新时间';
COMMENT ON COLUMN sys_file.deleted IS '逻辑删除 0-未删除 1-已删除';

CREATE INDEX idx_file_create_by ON sys_file(create_by);
CREATE INDEX idx_file_create_time ON sys_file(create_time);

-- 追加菜单：文件管理
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, path, component, perms, icon, sort, visible, status, create_time, update_time, deleted) VALUES
(16, 1, '文件管理', 2, '/system/file', 'system/file/index', 'system:file:list', 'file', 5, 1, 1, NOW(), NOW(), 0),
(161, 16, '文件上传', 3, NULL, NULL, 'system:file:upload', NULL, 1, 1, 1, NOW(), NOW(), 0),
(162, 16, '文件下载', 3, NULL, NULL, 'system:file:download', NULL, 2, 1, 1, NOW(), NOW(), 0),
(163, 16, '文件删除', 3, NULL, NULL, 'system:file:delete', NULL, 3, 1, 1, NOW(), NOW(), 0)
ON CONFLICT (id) DO NOTHING;

-- 超级管理员角色菜单关联 (新增的文件管理菜单)
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES
(28, 1, 16),
(29, 1, 161),
(30, 1, 162),
(31, 1, 163)
ON CONFLICT (id) DO NOTHING;

-- --------------------------------------------
-- 12. 通知公告表
-- --------------------------------------------
DROP TABLE IF EXISTS sys_notice CASCADE;
CREATE TABLE sys_notice (
    id BIGINT NOT NULL,
    notice_title VARCHAR(200) NOT NULL,
    notice_type INT DEFAULT 1,
    notice_content TEXT,
    status INT DEFAULT 0,
    create_by BIGINT,
    remark VARCHAR(500),
    create_time TIMESTAMP,
    update_time TIMESTAMP,
    deleted INT DEFAULT 0,
    PRIMARY KEY (id)
);
COMMENT ON TABLE sys_notice IS '通知公告表';
COMMENT ON COLUMN sys_notice.id IS '公告ID';
COMMENT ON COLUMN sys_notice.notice_title IS '公告标题';
COMMENT ON COLUMN sys_notice.notice_type IS '公告类型 1-通知 2-公告';
COMMENT ON COLUMN sys_notice.notice_content IS '公告内容';
COMMENT ON COLUMN sys_notice.status IS '状态 1-已发布 0-关闭';
COMMENT ON COLUMN sys_notice.create_by IS '创建人ID';
COMMENT ON COLUMN sys_notice.remark IS '备注';
COMMENT ON COLUMN sys_notice.create_time IS '创建时间';
COMMENT ON COLUMN sys_notice.update_time IS '更新时间';
COMMENT ON COLUMN sys_notice.deleted IS '逻辑删除 0-未删除 1-已删除';

CREATE INDEX idx_notice_status ON sys_notice(status);
CREATE INDEX idx_notice_create_time ON sys_notice(create_time);

-- 追加菜单：通知公告
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, path, component, perms, icon, sort, visible, status, create_time, update_time, deleted) VALUES
(17, 1, '通知公告', 2, '/system/notice', 'system/notice/index', 'system:notice:list', 'message', 6, 1, 1, NOW(), NOW(), 0),
(171, 17, '公告新增', 3, NULL, NULL, 'system:notice:add', NULL, 1, 1, 1, NOW(), NOW(), 0),
(172, 17, '公告修改', 3, NULL, NULL, 'system:notice:edit', NULL, 2, 1, 1, NOW(), NOW(), 0),
(173, 17, '公告删除', 3, NULL, NULL, 'system:notice:delete', NULL, 3, 1, 1, NOW(), NOW(), 0)
ON CONFLICT (id) DO NOTHING;

-- 超级管理员角色菜单关联 (新增的通知公告菜单)
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES
(32, 1, 17),
(33, 1, 171),
(34, 1, 172),
(35, 1, 173)
ON CONFLICT (id) DO NOTHING;

-- --------------------------------------------
-- 追加菜单：服务监控 + 在线用户
-- --------------------------------------------
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, path, component, perms, icon, sort, visible, status, create_time, update_time, deleted) VALUES
(23, 2, '服务监控', 2, '/monitor/server', 'monitor/server/index', 'monitor:server:list', 'server', 3, 1, 1, NOW(), NOW(), 0),
(24, 2, '在线用户', 2, '/monitor/online', 'monitor/online/index', 'monitor:online:list', 'online', 4, 1, 1, NOW(), NOW(), 0),
-- 在线用户按钮
(241, 24, '强退用户', 3, NULL, NULL, 'monitor:online:delete', NULL, 1, 1, 1, NOW(), NOW(), 0)
ON CONFLICT (id) DO NOTHING;

-- 超级管理员角色菜单关联 (新增的服务监控菜单)
INSERT INTO sys_role_menu (id, role_id, menu_id) VALUES
(36, 1, 23),
(37, 1, 24),
(38, 1, 241)
ON CONFLICT (id) DO NOTHING;
