package com.hanxuan.admin.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hanxuan.admin.system.entity.SysDept;
import com.hanxuan.admin.system.vo.DeptTreeVO;

import java.util.List;

/**
 * 部门服务
 *
 * @author hanxuan
 */
public interface SysDeptService extends IService<SysDept> {

    /**
     * 获取部门树
     */
    List<DeptTreeVO> tree(String deptName, Integer status);

    /**
     * 新增部门
     */
    void addDept(SysDept dept);

    /**
     * 修改部门
     */
    void updateDept(SysDept dept);

    /**
     * 删除部门
     */
    void deleteDept(Long id);

    /**
     * 根据ID获取部门详情
     */
    SysDept getDeptById(Long id);

    /**
     * 获取子部门ID列表
     */
    List<Long> getChildDeptIds(Long deptId);
}
