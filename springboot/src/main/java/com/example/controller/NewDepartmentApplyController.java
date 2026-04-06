package com.example.controller;

import com.example.common.Result;
import com.example.entity.NewDepartmentApply;
import com.example.service.NewDepartmentApplyService;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 新建社团申请前端操作接口
 */
@RestController
@RequestMapping("/newDepartmentApply")
public class NewDepartmentApplyController {

    @Resource
    private NewDepartmentApplyService newDepartmentApplyService;

    @GetMapping("/checkIsInClub/{userId}")
    public Result checkIsInClub(@PathVariable Integer userId) {
        boolean isInClub = newDepartmentApplyService.checkIsInClub(userId);
        return Result.success(isInClub);
    }

    /**
     * 新增
     */
    @PostMapping("/add")
    public Result add(@RequestBody NewDepartmentApply newDepartmentApply) {
        newDepartmentApplyService.add(newDepartmentApply);
        return Result.success();
    }

    /**
     * 删除
     */
    @DeleteMapping("/delete/{id}")
    public Result deleteById(@PathVariable Integer id) {
        newDepartmentApplyService.deleteById(id);
        return Result.success();
    }

    /**
     * 批量删除
     */
    @DeleteMapping("/delete/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        newDepartmentApplyService.deleteBatch(ids);
        return Result.success();
    }

    /**
     * 修改
     */
    @PutMapping("/update")
    public Result updateById(@RequestBody NewDepartmentApply newDepartmentApply) {
        newDepartmentApplyService.updateById(newDepartmentApply);
        return Result.success();
    }

    /**
     * 根据ID查询
     */
    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        NewDepartmentApply newDepartmentApply = newDepartmentApplyService.selectById(id);
        return Result.success(newDepartmentApply);
    }

    /**
     * 查询所有
     */
    @GetMapping("/selectAll")
    public Result selectAll(NewDepartmentApply newDepartmentApply) {
        List<NewDepartmentApply> list = newDepartmentApplyService.selectAll(newDepartmentApply);
        return Result.success(list);
    }

    /**
     * 分页查询
     */
    @GetMapping("/selectPage")
    public Result selectPage(NewDepartmentApply newDepartmentApply,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<NewDepartmentApply> page = newDepartmentApplyService.selectPage(newDepartmentApply, pageNum, pageSize);
        return Result.success(page);
    }
}
