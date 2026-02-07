package com.hnau.platform.service.impl;

import com.hnau.platform.entity.Comment;
import com.hnau.platform.entity.UserBehavior;
import com.hnau.platform.mapper.CommentMapper;
import com.hnau.platform.mapper.UserBehaviorMapper;
import com.hnau.platform.service.CommentService;
import com.hnau.platform.service.IntegralService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 评论管理服务实现类
 */
@Service
public class CommentServiceImpl implements CommentService {

    @Resource
    private CommentMapper commentMapper;

    @Resource
    private UserBehaviorMapper userBehaviorMapper;

    @Resource
    private IntegralService integralService;

    @Transactional
    @Override
    public boolean addComment(Comment comment) {
        // 设置默认值
        comment.setLikeCount(0);
        comment.setCreateTime(LocalDateTime.now());

        // 保存评论
        int result = commentMapper.insert(comment);
        if (result > 0) {
            // 增加积分（评论+3）
            integralService.updateIntegral(comment.getUserId(), 3, "评论获得积分", comment.getId(), 1);
            return true;
        }
        return false;
    }

    @Override
    public List<Comment> getCommentsByRel(Integer relType, Long relId) {
        return commentMapper.getCommentsByRel(relType, relId);
    }

    @Transactional
    @Override
    public Integer likeComment(Long userId, Long commentId) {
        try {
            // 检查评论是否存在
            System.out.println("=== 评论点赞开始 ===");
            System.out.println("用户ID: " + userId);
            System.out.println("评论ID: " + commentId);

            Comment comment = commentMapper.selectById(commentId);
            if (comment == null) {
                System.out.println("评论不存在");
                return -2; // 评论不存在
            }
            System.out.println("评论存在: " + comment.getId());
            System.out.println("当前点赞数: " + comment.getLikeCount());

            // 检查是否已点赞
            UserBehavior behavior = userBehaviorMapper.checkBehaviorExists(userId, 1, commentId);
            System.out.println("已点赞记录: " + (behavior != null ? behavior.getId() : "null"));

            if (behavior != null) {
                // 已点赞，取消点赞
                System.out.println("执行取消点赞操作");
                userBehaviorMapper.deleteById(behavior.getId());
                // 检查当前点赞数是否大于0，避免出现负数
                if (comment.getLikeCount() > 0) {
                    comment.setLikeCount(comment.getLikeCount() - 1);
                    commentMapper.updateById(comment);
                }
                // 扣减积分（取消点赞-1）
                System.out.println("扣减积分");
                integralService.updateIntegral(userId, -1, "取消点赞扣除积分", commentId, 2);
                System.out.println("取消点赞成功");
                return 0; // 取消点赞成功
            } else {
                // 未点赞，添加点赞
                System.out.println("执行添加点赞操作");
                UserBehavior newBehavior = new UserBehavior();
                newBehavior.setUserId(userId);
                newBehavior.setBehaviorType(1);
                newBehavior.setRelId(commentId);
                newBehavior.setCreateTime(LocalDateTime.now());
                userBehaviorMapper.insert(newBehavior);
                comment.setLikeCount(comment.getLikeCount() + 1);
                commentMapper.updateById(comment);
                // 增加积分（点赞+1）
                System.out.println("增加积分");
                integralService.updateIntegral(userId, 1, "点赞获得积分", commentId, 1);
                System.out.println("点赞成功");
                return 1; // 点赞成功
            }
        } catch (Exception e) {
            System.out.println("=== 评论点赞异常 ===");
            System.out.println("错误信息: " + e.getMessage());
            e.printStackTrace();
            throw e;
        } finally {
            System.out.println("=== 评论点赞结束 ===");
        }
    }
}