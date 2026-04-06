package com.example.mapper;

import com.example.entity.NewDepartmentApply;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 操作new_department_apply相关数据接口
 */
@Mapper
public interface NewDepartmentApplyMapper {

    /**
     * 新增
     */
    int insert(NewDepartmentApply newDepartmentApply);

    /**
     * 删除
     */
    int deleteById(Integer id);

    /**
     * 修改
     */
    int updateById(NewDepartmentApply newDepartmentApply);

    /**
     * 根据ID查询
     */
    NewDepartmentApply selectById(Integer id);

    /**
     * 查询所有
     */
    List<NewDepartmentApply> selectAll(NewDepartmentApply newDepartmentApply);
}
