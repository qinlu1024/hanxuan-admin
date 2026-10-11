package com.hanxuan.admin.system.service.impl;

import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hanxuan.admin.common.constant.SystemConstants;
import com.hanxuan.admin.common.enums.ResultCode;
import com.hanxuan.admin.common.exception.BusinessException;
import com.hanxuan.admin.common.utils.DataScopeUtils;
import com.hanxuan.admin.system.entity.SysDept;
import com.hanxuan.admin.system.entity.SysUser;
import com.hanxuan.admin.system.entity.SysUserRole;
import com.hanxuan.admin.system.mapper.SysUserMapper;
import com.hanxuan.admin.system.mapper.SysUserRoleMapper;
import com.hanxuan.admin.system.service.SysDeptService;
import com.hanxuan.admin.system.service.SysUserService;
import com.hanxuan.admin.system.vo.UserVO;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 用户服务实现
 *
 * @author hanxuan
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Resource
    @Lazy
    private SysDeptService deptService;

    @Resource
    private SysUserRoleMapper sysUserRoleMapper;

    @Override
    public IPage<UserVO> page(Integer pageNum, Integer pageSize, String keyword, Long deptId) {
        IPage<SysUser> userPage = page(new Page<>(pageNum, pageSize), buildQueryWrapper(keyword, deptId));

        // 转换为 VO（脱敏）
        Page<UserVO> voPage = new Page<>(userPage.getCurrent(), userPage.getSize(), userPage.getTotal());
        List<UserVO> voList = userPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        // 批量填充部门名称
        fillDeptName(voList);

        voPage.setRecords(voList);
        return voPage;
    }

    @Override
    public List<UserVO> listForExport(String keyword, Long deptId) {
        List<SysUser> userList = list(buildQueryWrapper(keyword, deptId));
        List<UserVO> voList = userList.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        fillDeptName(voList);
        return voList;
    }

    /**
     * 构建查询条件（含数据权限过滤，分页与导出共用）
     */
    private LambdaQueryWrapper<SysUser> buildQueryWrapper(String keyword, Long deptId) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(SysUser::getUsername, keyword)
                    .or().like(SysUser::getNickname, keyword)
                    .or().like(SysUser::getPhone, keyword));
        }
        if (deptId != null) {
            wrapper.eq(SysUser::getDeptId, deptId);
        }
        // 数据权限过滤
        DataScopeUtils.addCondition(wrapper, SysUser::getDeptId, SysUser::getId);
        wrapper.orderByDesc(SysUser::getCreateTime);
        return wrapper;
    }

    @Override
    public UserVO getUserVOById(Long id) {
        SysUser user = getById(id);
        if (user == null) {
            return null;
        }
        UserVO vo = convertToVO(user);
        fillDeptName(vo);
        return vo;
    }

    /**
     * 实体转 VO（脱敏，移除密码等敏感字段）
     */
    private UserVO convertToVO(SysUser user) {
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        return vo;
    }

    /**
     * 批量填充部门名称
     */
    private void fillDeptName(List<UserVO> voList) {
        if (voList == null || voList.isEmpty() || deptService == null) {
            return;
        }
        List<Long> deptIds = voList.stream()
                .map(UserVO::getDeptId)
                .filter(id -> id != null && id != 0)
                .distinct()
                .collect(Collectors.toList());
        if (deptIds.isEmpty()) {
            return;
        }
        List<SysDept> depts = deptService.listByIds(deptIds);
        Map<Long, String> deptNameMap = new HashMap<>();
        for (SysDept dept : depts) {
            deptNameMap.put(dept.getId(), dept.getDeptName());
        }
        for (UserVO vo : voList) {
            if (vo.getDeptId() != null) {
                vo.setDeptName(deptNameMap.get(vo.getDeptId()));
            }
        }
    }

    /**
     * 单个填充部门名称
     */
    private void fillDeptName(UserVO vo) {
        if (vo == null || vo.getDeptId() == null || deptService == null) {
            return;
        }
        SysDept dept = deptService.getById(vo.getDeptId());
        if (dept != null) {
            vo.setDeptName(dept.getDeptName());
        }
    }

    @Override
    public void addUser(SysUser user) {
        // 校验用户名是否存在
        Long count = lambdaQuery().eq(SysUser::getUsername, user.getUsername()).count();
        if (count > 0) {
            throw new BusinessException(ResultCode.USERNAME_EXISTS);
        }
        // 默认密码
        if (!StringUtils.hasText(user.getPassword())) {
            user.setPassword(BCrypt.hashpw(SystemConstants.DEFAULT_PASSWORD));
        } else {
            user.setPassword(BCrypt.hashpw(user.getPassword()));
        }
        // 默认状态
        if (user.getStatus() == null) {
            user.setStatus(1);
        }
        save(user);
    }

    @Override
    public void updateUser(SysUser user) {
        // 不允许修改用户名和密码
        user.setUsername(null);
        user.setPassword(null);
        updateById(user);
    }

    @Override
    public void deleteUser(Long id) {
        // 超级管理员不能删除
        if (SystemConstants.SUPER_ADMIN_USER_ID.equals(id)) {
            throw new BusinessException("超级管理员不能删除");
        }
        removeById(id);
    }

    @Override
    public void deleteBatch(List<Long> ids) {
        if (ids.contains(SystemConstants.SUPER_ADMIN_USER_ID)) {
            throw new BusinessException("超级管理员不能删除");
        }
        removeByIds(ids);
    }

    @Override
    public void resetPassword(Long id, String password) {
        SysUser user = new SysUser();
        user.setId(id);
        user.setPassword(BCrypt.hashpw(password));
        updateById(user);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        SysUser user = new SysUser();
        user.setId(id);
        user.setStatus(status);
        updateById(user);
    }

    @Override
    public List<Long> getRoleIdsByUserId(Long userId) {
        List<SysUserRole> list = sysUserRoleMapper.selectList(
                new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, userId)
        );
        return list.stream().map(SysUserRole::getRoleId).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignRoles(Long userId, List<Long> roleIds) {
        // 先删除原有角色
        sysUserRoleMapper.delete(
                new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, userId)
        );
        // 再新增
        if (roleIds != null && !roleIds.isEmpty()) {
            for (Long roleId : roleIds) {
                SysUserRole userRole = new SysUserRole();
                userRole.setUserId(userId);
                userRole.setRoleId(roleId);
                sysUserRoleMapper.insert(userRole);
            }
        }
    }
}
