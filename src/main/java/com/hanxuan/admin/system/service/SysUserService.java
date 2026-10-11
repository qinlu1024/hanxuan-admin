package com.hanxuan.admin.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hanxuan.admin.system.entity.SysUser;
import com.hanxuan.admin.system.vo.UserVO;

import java.util.List;

/**
 * 用户服务
 *
 * @author hanxuan
 */
public interface SysUserService extends IService<SysUser> {

    /**
     * 分页查询
     */
    IPage<UserVO> page(Integer pageNum, Integer pageSize, String keyword, Long deptId);

    /**
     * 查询导出列表（复用分页查询条件与数据权限）
     */
    List<UserVO> listForExport(String keyword, Long deptId);

    /**
     * 根据ID查询用户信息（脱敏）
     */
    UserVO getUserVOById(Long id);

    /**
     * 新增用户
     */
    void addUser(SysUser user);

    /**
     * 修改用户
     */
    void updateUser(SysUser user);

    /**
     * 删除用户
     */
    void deleteUser(Long id);

    /**
     * 批量删除
     */
    void deleteBatch(List<Long> ids);

    /**
     * 重置密码
     */
    void resetPassword(Long id, String password);

    /**
     * 修改状态
     */
    void updateStatus(Long id, Integer status);

    /**
     * 获取用户角色ID列表
     */
    List<Long> getRoleIdsByUserId(Long userId);

    /**
     * 分配用户角色
     */
    void assignRoles(Long userId, List<Long> roleIds);
}
