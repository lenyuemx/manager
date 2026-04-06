package com.example.service;

import cn.hutool.core.date.DateUtil;
import com.example.entity.Account;
import com.example.entity.Comment;
import com.example.entity.User;
import com.example.mapper.CommentMapper;
import com.example.common.enums.RoleEnum;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 评论表业务处理
 **/
@Service
public class CommentService {

    @Resource
    private CommentMapper commentMapper;

    /**
     * 新增
     */
    public void add(Comment comment) {
        Account currentUser = TokenUtils.getCurrentUser();
        comment.setUserId(currentUser.getId());
        comment.setTime(DateUtil.now());
        commentMapper.insert(comment);
    }

    /**
     * 删除
     */
    public void deleteById(Integer id) {
        commentMapper.deleteById(id);
    }

    /**
     * 批量删除
     */
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            commentMapper.deleteById(id);
        }
    }

    /**
     * 修改
     */
    public void updateById(Comment comment) {
        commentMapper.updateById(comment);
    }

    /**
     * 根据ID查询
     */
    public Comment selectById(Integer id) {
        return commentMapper.selectById(id);
    }

    /**
     * 查询所有
     */
    public List<Comment> selectAll(Comment comment) {
        return commentMapper.selectAll(comment);
    }

    /**
     * 查询树形结构的评论
     */
    public List<Comment> selectTree(Integer foreignId, String module) {
        // 1. 查询所有评论
        List<Comment> commentList = this.commentMapper.selectByForeignId(foreignId, module);

        // 2. 找出所有顶级评论 (parentId is null)
        List<Comment> topComments = commentList.stream().filter(c -> c.getParentId() == null).collect(Collectors.toList());

        // 3. 为每个顶级评论设置子评论
        for (Comment topComment : topComments) {
            List<Comment> children = commentList.stream().filter(c -> topComment.getId().equals(c.getParentId())).collect(Collectors.toList());
            topComment.setChildren(children);
        }
        return topComments;
    }

    /**
     * 分页查询
     */
    public PageInfo<Comment> selectPage(Comment comment, Integer pageNum, Integer pageSize) {
        Account currentUser = TokenUtils.getCurrentUser();
        if (RoleEnum.TEACHER.name().equals(currentUser.getRole())) {
            comment.setTeacherId(currentUser.getId());
        } else if (RoleEnum.USER.name().equals(currentUser.getRole())) {
            comment.setDepartmentUserId(currentUser.getId());
        }
        PageHelper.startPage(pageNum, pageSize);
        List<Comment> list = commentMapper.selectAll(comment);
        for (Comment c : list) {
            if (c.getContent() != null) {
                c.setContent(c.getContent().replaceAll("<.*?>", ""));
            }
        }
        return PageInfo.of(list);
    }
}
