package com.example.service;

import cn.hutool.core.date.DateUtil;
import com.example.entity.Likes;
import com.example.mapper.LikesMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class LikesService {

    @Resource
    private LikesMapper likesMapper;

    /**
     * 点赞 / 取消点赞
     */
    public void set(Likes likes) {
        Likes dbLikes = likesMapper.selectUserLike(likes);
        if (dbLikes == null) {
            // 没有点赞过，则新增点赞
            likes.setTime(DateUtil.now());
            likesMapper.insert(likes);
        } else {
            // 已经点赞过，则取消点赞（删除记录）
            likesMapper.deleteById(dbLikes.getId());
        }
    }

    /**
     * 检查当前用户是否已经点赞
     */
    public boolean checkUserLike(Likes likes) {
        Likes dbLikes = likesMapper.selectUserLike(likes);
        return dbLikes != null;
    }

    /**
     * 获取总点赞数
     */
    public int selectCount(Likes likes) {
        return likesMapper.selectCountByForeignIdAndModule(likes);
    }
}
