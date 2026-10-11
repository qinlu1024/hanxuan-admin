package com.hanxuan.admin.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hanxuan.admin.common.enums.ResultCode;
import com.hanxuan.admin.common.exception.BusinessException;
import com.hanxuan.admin.system.entity.SysDept;
import com.hanxuan.admin.system.entity.SysUser;
import com.hanxuan.admin.system.mapper.SysDeptMapper;
import com.hanxuan.admin.system.mapper.SysUserMapper;
import com.hanxuan.admin.system.service.SysDeptService;
import com.hanxuan.admin.system.vo.DeptTreeVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 部门服务实现
 *
 * @author hanxuan
 */
@Service
@RequiredArgsConstructor
public class SysDeptServiceImpl extends ServiceImpl<SysDeptMapper, SysDept>
        implements SysDeptService {

    private final SysUserMapper userMapper;

    @Override
    public List<DeptTreeVO> tree(String deptName, Integer status) {
        LambdaQueryWrapper<SysDept> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(deptName)) {
            wrapper.like(SysDept::getDeptName, deptName);
        }
        if (status != null) {
            wrapper.eq(SysDept::getStatus, status);
        }
        wrapper.orderByAsc(SysDept::getSort).orderByAsc(SysDept::getId);
        List<SysDept> deptList = list(wrapper);

        // 构建树结构
        List<DeptTreeVO> allVos = deptList.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        return buildTree(allVos, 0L);
    }

    private DeptTreeVO convertToVO(SysDept dept) {
        DeptTreeVO vo = new DeptTreeVO();
        BeanUtils.copyProperties(dept, vo);
        return vo;
    }

    /**
     * 递归构建树结构
     */
    private List<DeptTreeVO> buildTree(List<DeptTreeVO> allVos, Long parentId) {
        List<DeptTreeVO> children = new ArrayList<>();
        for (DeptTreeVO vo : allVos) {
            if (parentId.equals(vo.getParentId())) {
                vo.setChildren(buildTree(allVos, vo.getId()));
                children.add(vo);
            }
        }
        return children;
    }

    @Override
    public void addDept(SysDept dept) {
        if (dept.getParentId() == null) {
            dept.setParentId(0L);
        }
        if (dept.getSort() == null) {
            dept.setSort(0);
        }
        // 计算祖级列表
        String ancestors = calculateAncestors(dept.getParentId());
        dept.setAncestors(ancestors);

        save(dept);
    }

    /**
     * 计算祖级列表
     */
    private String calculateAncestors(Long parentId) {
        if (parentId == null || parentId == 0) {
            return "0";
        }
        SysDept parent = getById(parentId);
        if (parent == null) {
            return "0";
        }
        return parent.getAncestors() + "," + parent.getId();
    }

    @Override
    public void updateDept(SysDept dept) {
        SysDept oldDept = getById(dept.getId());
        if (oldDept == null) {
            throw new BusinessException(ResultCode.NOT_FOUND);
        }

        Long oldParentId = oldDept.getParentId();
        Long newParentId = dept.getParentId() != null ? dept.getParentId() : 0L;

        // 如果父级变了，重新计算祖级列表，并更新所有子部门的祖级
        if (!oldParentId.equals(newParentId)) {
            // 不能把自己的子部门设为父级
            List<Long> childIds = getChildDeptIds(dept.getId());
            if (childIds.contains(newParentId)) {
                throw new BusinessException("不能将子部门设为父级部门");
            }
            String newAncestors = calculateAncestors(newParentId);
            dept.setAncestors(newAncestors);

            // 更新子部门的祖级列表
            updateChildAncestors(dept.getId(), oldDept.getAncestors(), newAncestors);
        }

        updateById(dept);
    }

    /**
     * 更新子部门的祖级列表
     */
    private void updateChildAncestors(Long deptId, String oldAncestors, String newAncestors) {
        List<Long> childIds = getChildDeptIds(deptId);
        if (childIds.isEmpty()) {
            return;
        }
        for (Long childId : childIds) {
            SysDept child = getById(childId);
            if (child != null) {
                String newChildAncestors = newAncestors + "," + deptId
                        + child.getAncestors().substring((oldAncestors + "," + deptId).length());
                SysDept update = new SysDept();
                update.setId(childId);
                update.setAncestors(newChildAncestors);
                updateById(update);
            }
        }
    }

    @Override
    public void deleteDept(Long id) {
        // 检查是否有子部门
        Long childCount = lambdaQuery().eq(SysDept::getParentId, id).count();
        if (childCount > 0) {
            throw new BusinessException("存在子部门，不允许删除");
        }
        // 检查是否有用户
        Long userCount = userMapper.selectCount(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getDeptId, id));
        if (userCount != null && userCount > 0) {
            throw new BusinessException("部门下存在用户，不允许删除");
        }
        removeById(id);
    }

    @Override
    public SysDept getDeptById(Long id) {
        return getById(id);
    }

    @Override
    public List<Long> getChildDeptIds(Long deptId) {
        List<Long> result = new ArrayList<>();
        collectChildIds(deptId, result);
        return result;
    }

    private void collectChildIds(Long parentId, List<Long> result) {
        List<SysDept> children = lambdaQuery().eq(SysDept::getParentId, parentId).list();
        for (SysDept child : children) {
            result.add(child.getId());
            collectChildIds(child.getId(), result);
        }
    }
}
