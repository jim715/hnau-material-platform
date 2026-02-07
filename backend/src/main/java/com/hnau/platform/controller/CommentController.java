package com.hnau.platform.controller;

import com.hnau.platform.entity.Comment;
import com.hnau.platform.service.CommentService;
import com.hnau.platform.common.Result;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 评论管理控制器
 */
@RestController
@RequestMapping("/api/comment")
public class CommentController {

    @Resource
    private CommentService commentService;

    /**
     * 添加评论
     * @param comment 评论信息
     * @return 添加结果
     */
    @PostMapping("/add")
    public Result addComment(@RequestBody Comment comment) {
        boolean result = commentService.addComment(comment);
        return result ? Result.success() : Result.error("评论失败");
    }

    /**
     * 获取评论列表
     * @param relType 关联类型
     * @param relId 关联ID
     * @return 评论列表
     */
    @GetMapping("/list")
    public Result getCommentsByRel(@RequestParam Integer relType, @RequestParam Long relId) {
        List<Comment> comments = commentService.getCommentsByRel(relType, relId);
        return Result.success(comments);
    }

    /**
     * 点赞/取消点赞评论
     * @param userId 用户ID
     * @param commentId 评论ID
     * @return 点赞结果
     */
    @PostMapping("/like")
    public Result likeComment(@RequestParam Long userId, @RequestParam Long commentId) {
        Integer result = commentService.likeComment(userId, commentId);
        switch (result) {
            case 1:
                return Result.success("点赞成功");
            case 0:
                return Result.success("取消点赞成功");
            case -1:
                return Result.error("已点赞");
            case -2:
                return Result.error("评论不存在");
            default:
                return Result.error("操作失败");
        }
    }
}