package com.example.service;

import cn.hutool.core.util.ObjectUtil;
import com.example.common.enums.ApplyEnum;
import com.example.common.enums.LevelEnum;
import com.example.common.enums.ResultCodeEnum;
import com.example.common.enums.RoleEnum;
import com.example.entity.Account;
import com.example.entity.Apply;
import com.example.entity.Department;
import com.example.entity.NewDepartmentApply;
import com.example.entity.User;
import com.example.exception.CustomException;
import com.example.mapper.ApplyMapper;
import com.example.mapper.DepartmentMapper;
import com.example.mapper.NewDepartmentApplyMapper;
import com.example.mapper.UserMapper;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 新建社团申请业务处理
 */
@Service
public class NewDepartmentApplyService {

    @Resource
    private NewDepartmentApplyMapper newDepartmentApplyMapper;

    @Resource
    private ApplyMapper applyMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private DepartmentMapper departmentMapper;

    /**
     * 新增
     */
    public void add(NewDepartmentApply newDepartmentApply) {
        if (checkIsInClub(newDepartmentApply.getApplicantId())) {
            throw new CustomException(ResultCodeEnum.CLUB_ALREADY_ERROR);
        }
        newDepartmentApplyMapper.insert(newDepartmentApply);
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
        newDepartmentApplyMapper.deleteById(id);
    }

    /**
     * 批量删除
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            newDepartmentApplyMapper.deleteById(id);
        }
    }

    /**
     * 修改
     */
    public void updateById(NewDepartmentApply newDepartmentApply) {
        newDepartmentApplyMapper.updateById(newDepartmentApply);
        // 如果是已批准，则自动添加该社团，该申请的学生自动成为社长
        if ("已批准".equals(newDepartmentApply.getStatus())) {
            NewDepartmentApply dbApply = newDepartmentApplyMapper.selectById(newDepartmentApply.getId());
            Department department = new Department();
            department.setName(dbApply.getName());
            department.setDescription(dbApply.getDescription());
            department.setImg(dbApply.getCover());
            department.setUserId(dbApply.getApplicantId());
            department.setTeacherId(dbApply.getTeacherId());
            department.setTime(dbApply.getApplyTime());
            departmentMapper.insert(department);

            // 更新用户等级为社长
            User user = userMapper.selectById(dbApply.getApplicantId());
            if (ObjectUtil.isNotEmpty(user)) {
                user.setLevel(LevelEnum.HEADER.level);
                userMapper.updateById(user);
            }
        }
    }

    /**
     * 根据ID查询
     */
    public NewDepartmentApply selectById(Integer id) {
        return newDepartmentApplyMapper.selectById(id);
    }

    /**
     * 查询所有
     */
    public List<NewDepartmentApply> selectAll(NewDepartmentApply newDepartmentApply) {
        return newDepartmentApplyMapper.selectAll(newDepartmentApply);
    }

    /**
     * 分页查询
     */
    public PageInfo<NewDepartmentApply> selectPage(NewDepartmentApply newDepartmentApply, Integer pageNum, Integer pageSize) {
        Account currentUser = TokenUtils.getCurrentUser();
        if (RoleEnum.TEACHER.name().equals(currentUser.getRole())) {
            newDepartmentApply.setTeacherId(currentUser.getId());
        }
        PageHelper.startPage(pageNum, pageSize);
        List<NewDepartmentApply> list = newDepartmentApplyMapper.selectAll(newDepartmentApply);
        return PageInfo.of(list);
    }
}
