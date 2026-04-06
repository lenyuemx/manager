package com.example.controller;

import com.example.common.Result;
import com.example.entity.QuitApply;
import com.example.service.QuitApplyService;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/quitApply")
public class QuitApplyController {

    @Resource
    private QuitApplyService quitApplyService;

    @PostMapping("/add")
    public Result add(@RequestBody QuitApply quitApply) {
        quitApplyService.add(quitApply);
        return Result.success();
    }

    @DeleteMapping("/delete/{id}")
    public Result deleteById(@PathVariable Integer id) {
        quitApplyService.deleteById(id);
        return Result.success();
    }

    @DeleteMapping("/delete/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        quitApplyService.deleteBatch(ids);
        return Result.success();
    }

    @PutMapping("/update")
    public Result updateById(@RequestBody QuitApply quitApply) {
        quitApplyService.updateById(quitApply);
        return Result.success();
    }

    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        QuitApply quitApply = quitApplyService.selectById(id);
        return Result.success(quitApply);
    }

    @GetMapping("/selectAll")
    public Result selectAll(QuitApply quitApply) {
        List<QuitApply> list = quitApplyService.selectAll(quitApply);
        return Result.success(list);
    }

    @GetMapping("/selectPage")
    public Result selectPage(QuitApply quitApply,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<QuitApply> page = quitApplyService.selectPage(quitApply, pageNum, pageSize);
        return Result.success(page);
    }

}
