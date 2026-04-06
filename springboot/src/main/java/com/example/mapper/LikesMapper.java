package com.example.mapper;

import com.example.entity.Likes;

public interface LikesMapper {

    void insert(Likes likes);

    void deleteById(Integer id);

    Likes selectUserLike(Likes likes);

    int selectCountByForeignIdAndModule(Likes likes);
}
