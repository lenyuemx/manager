package com.example.mapper;

import com.example.entity.Comment;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 评论表操作接口
 **/
public interface CommentMapper {

    /**
     * 新增
     */
    int insert(Comment comment);

    /**
     * 删除
     */
    int deleteById(Integer id);

    /**
     * 修改
     */
    int updateById(Comment comment);

    /**
     * 根据ID查询
     */
    Comment selectById(Integer id);

    /**
     * 查询所有
     */
    List<Comment> selectAll(Comment comment);

    /**
     * 根据父ID查询
     */
    List<Comment> selectByParentId(Integer parentId);

    /**
     * 根据关联ID和模块查询
     */
    List<Comment> selectByForeignId(@Param("foreignId") Integer foreignId, @Param("module") String module);
}
