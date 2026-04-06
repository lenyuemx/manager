package com.example.service;

import cn.hutool.core.date.DateUtil;
import com.example.common.enums.LevelEnum;
import com.example.common.enums.ResultCodeEnum;
import com.example.common.enums.RoleEnum;
import com.example.entity.Account;
import com.example.entity.Apply;
import com.example.entity.Department;
import com.example.entity.QuitApply;
import com.example.entity.User;
import com.example.exception.CustomException;
import com.example.mapper.ApplyMapper;
import com.example.mapper.DepartmentMapper;
import com.example.mapper.QuitApplyMapper;
import com.example.mapper.UserMapper;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import cn.hutool.core.util.ObjectUtil;

import javax.annotation.Resource;
import java.util.List;

@Service
public class QuitApplyService {

    @Resource
    private QuitApplyMapper quitApplyMapper;
    
    @Resource
    private ApplyMapper applyMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private DepartmentMapper departmentMapper;

    public void add(QuitApply quitApply) {
        // 先查询该学生有没有正在审批中的退团申请
        QuitApply query = new QuitApply();
        query.setUserId(quitApply.getUserId());
        query.setDepartmentId(quitApply.getDepartmentId());
        query.setStatus("待审批");
        List<QuitApply> list = quitApplyMapper.selectAll(query);
        if (ObjectUtil.isNotEmpty(list)) {
            throw new CustomException(ResultCodeEnum.QUIT_APPLY_ALREADY_ERROR);
        }
        quitApply.setApplyTime(DateUtil.today());
        quitApply.setStatus("待审批");
        quitApplyMapper.insert(quitApply);
    }

    public void deleteById(Integer id) {
        quitApplyMapper.deleteById(id);
    }

    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            this.deleteById(id);
        }
    }

    public void updateById(QuitApply quitApply) {
        quitApplyMapper.updateById(quitApply);
        
        // 如果状态变更为“已批准”，则将此用户从所属社团剥离
        if ("已批准".equals(quitApply.getStatus())) {
            Apply applyQuery = new Apply();
            applyQuery.setUserId(quitApply.getUserId());
            applyQuery.setDepartmentId(quitApply.getDepartmentId());
            List<Apply> applies = applyMapper.selectAll(applyQuery);
            if (applies != null && !applies.isEmpty()) {
                for(Apply apply : applies) {
                    if("审核通过".equals(apply.getStatus())) {
                        applyMapper.deleteById(apply.getId());
                    }
                }
            }
        }
    }

    public QuitApply selectById(Integer id) {
        return quitApplyMapper.selectById(id);
    }

    public List<QuitApply> selectAll(QuitApply quitApply) {
        return quitApplyMapper.selectAll(quitApply);
    }

    public PageInfo<QuitApply> selectPage(QuitApply quitApply, Integer pageNum, Integer pageSize) {
        extracted(quitApply);
        PageHelper.startPage(pageNum, pageSize);
        List<QuitApply> list = this.selectAll(quitApply);
        return PageInfo.of(list);
    }

    private void extracted(QuitApply quitApply) {
        Account currentUser = TokenUtils.getCurrentUser();
        if (RoleEnum.USER.name().equals(currentUser.getRole())) {
            User user = userMapper.selectById(currentUser.getId());
            if (LevelEnum.HEADER.level.equals(user.getLevel())) {
                Department department = departmentMapper.selectByUserId(user.getId());
                if (ObjectUtil.isNotEmpty(department)) {
                    quitApply.setDepartmentId(department.getId());
                }
            }
        } else if (RoleEnum.TEACHER.name().equals(currentUser.getRole())) {
            Department department = departmentMapper.selectByTeacherId(currentUser.getId());
            if (ObjectUtil.isNotEmpty(department)) {
                quitApply.setDepartmentId(department.getId());
            }
        }
    }

}
