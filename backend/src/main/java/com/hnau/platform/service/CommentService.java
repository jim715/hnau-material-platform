package com.hnau.platform.service;

import com.hnau.platform.entity.Comment;
import java.util.List;

/**
 * 评论管理服务接口
 */
public interface CommentService {
    /**
     * 添加评论
     * @param comment 评论信息
     * @return 是否添加成功
     */
    boolean addComment(Comment comment);

    /**
     * 获取评论列表
     * @param relType 关联类型
     * @param relId 关联ID
     * @return 评论列表
     */
    List<Comment> getCommentsByRel(Integer relType, Long relId);

    /**
     * 点赞/取消点赞评论
     * @param userId 用户ID
     * @param commentId 评论ID
     * @return 点赞结果（1：点赞成功，0：取消点赞成功，-1：已点赞，-2：评论不存在）
     */
    Integer likeComment(Long userId, Long commentId);
}