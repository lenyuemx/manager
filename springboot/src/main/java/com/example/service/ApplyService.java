package com.example.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.example.common.enums.ApplyEnum;
import com.example.common.enums.LevelEnum;
import com.example.common.enums.ResultCodeEnum;
import com.example.common.enums.RoleEnum;
import com.example.entity.Account;
import com.example.entity.Apply;
import com.example.entity.Department;
import com.example.entity.User;
import com.example.exception.CustomException;
import com.example.mapper.ApplyMapper;
import com.example.mapper.DepartmentMapper;
import com.example.mapper.UserMapper;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * 申请审批表业务处理
 **/
@Service
public class ApplyService {

    @Resource
    private ApplyMapper applyMapper;
    @Resource
    private UserMapper userMapper;
    @Resource
    private DepartmentMapper departmentMapper;

    /**
     * 新增
     */
    public void add(Apply apply) {
        // 校验用户是否已经在社团中
        if (checkIsInClub(apply.getUserId())) {
            throw new CustomException(ResultCodeEnum.CLUB_ALREADY_ERROR);
        }
        // 先去查询一下该学生有没有申请过该社团
        List<Apply> list = applyMapper.selectByStatus(apply.getUserId(), apply.getDepartmentId());
        if (CollectionUtil.isNotEmpty(list)) {
            throw new CustomException(ResultCodeEnum.APPLY_ALREADY_ERROR);
        }
        apply.setProcess(ApplyEnum.PROCESS_HEADER_APPLYING.status);
        apply.setStatus(ApplyEnum.STATUS_APPLYING.status);
        applyMapper.insert(apply);
    }

    /**
     * 校验用户是否已经在社团中
     */
    public boolean checkIsInClub(Integer userId) {
        // 1. 校验是否是社长
        User user = userMapper.selectById(userId);
        if (ObjectUtil.isNotEmpty(user) && LevelEnum.HEADER.level.equals(user.getLevel())) {
            return true;
        }
        // 2. 校验是否有已通过的入团申请
        Apply apply = new Apply();
        apply.setUserId(userId);
        apply.setStatus(ApplyEnum.STATUS_APPLY_OK.status);
        List<Apply> list = applyMapper.selectAll(apply);
        return ObjectUtil.isNotEmpty(list) && list.size() > 0;
    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        applyMapper.deleteById(id);
    }

    /**
     * 批量删除
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            applyMapper.deleteById(id);
        }
    }

    /**
     * 修改
     */
    public void updateById(Apply apply) {
        applyMapper.updateById(apply);
    }

    /**
     * 根据ID查询
     */
    public Apply selectById(Integer id) {
        return applyMapper.selectById(id);
    }

    /**
     * 查询所有
     */
    public List<Apply> selectAll(Apply apply) {
        return applyMapper.selectAll(apply);
    }

    /**
     * 分页查询
     */
    public PageInfo<Apply> selectPage(Apply apply, Integer pageNum, Integer pageSize) {
        extracted(apply);
        PageHelper.startPage(pageNum, pageSize);
        List<Apply> list = applyMapper.selectAll(apply);
        return PageInfo.of(list);
    }

    public PageInfo<Apply> selectPage2(Apply apply, Integer pageNum, Integer pageSize) {
        extracted(apply);
        apply.setStatus(ApplyEnum.STATUS_APPLY_OK.status);
        PageHelper.startPage(pageNum, pageSize);
        List<Apply> list = applyMapper.selectAll(apply);
        return PageInfo.of(list);
    }

    private void extracted(Apply apply) {
        Account currentUser = TokenUtils.getCurrentUser();
        if (RoleEnum.USER.name().equals(currentUser.getRole())) {
            User user = userMapper.selectById(currentUser.getId());
            if (LevelEnum.HEADER.level.equals(user.getLevel())) {
                Department department = departmentMapper.selectByUserId(user.getId());
                if (ObjectUtil.isNotEmpty(department)) {
                    apply.setDepartmentId(department.getId());
                } else {
                    apply.setDepartmentId(-1);
                }
            }
        } else if (RoleEnum.TEACHER.name().equals(currentUser.getRole())) {
            Department department = departmentMapper.selectByTeacherId(currentUser.getId());
            if (ObjectUtil.isNotEmpty(department)) {
                apply.setDepartmentId(department.getId());
            } else {
                apply.setDepartmentId(-1);
            }
        }
    }

    public List<Apply> selectMyApply(Apply apply) {
        Account currentUser = TokenUtils.getCurrentUser();
        apply.setUserId(currentUser.getId());
        return applyMapper.selectAll(apply);
    }

}