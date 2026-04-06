package com.example.controller;

import com.example.common.Result;
import com.example.entity.Likes;
import com.example.service.LikesService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/likes")
public class LikesController {

    @Resource
    private LikesService likesService;

    /**
     * 点赞或取消点赞
     */
    @PostMapping("/set")
    public Result set(@RequestBody Likes likes) {
        likesService.set(likes);
        return Result.success();
    }

    /**
     * 根据 foreignId 和 module 获取总点赞数，以及当前用户的点赞状态
     */
    @GetMapping("/selectCount")
    public Result selectCount(Likes likes) {
        // likes 对象中此时应该包含 foreignId, module, 以及当前用户的 userId
        int count = likesService.selectCount(likes);
        boolean userLike = false;
        if (likes.getUserId() != null) {
            userLike = likesService.checkUserLike(likes);
        }
        
        Map<String, Object> map = new HashMap<>();
        map.put("count", count);
        map.put("userLike", userLike);
        return Result.success(map);
    }
}
