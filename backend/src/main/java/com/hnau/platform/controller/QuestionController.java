package com.hnau.platform.controller;

import com.hnau.platform.entity.Question;
import com.hnau.platform.service.QuestionService;
import com.hnau.platform.common.Result;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 问题管理控制器
 */
@RestController
@RequestMapping("/api/question")
public class QuestionController {

    @Resource
    private QuestionService questionService;

    /**
     * 添加问题
     * 
     * @param question 问题信息
     * @return 添加结果
     */
    @PostMapping("/add")
    public Result addQuestion(@RequestBody Question question) {
        boolean result = questionService.addQuestion(question);
        return result ? Result.success() : Result.error("提问失败");
    }

    /**
     * 获取问题列表
     * 
     * @param page    页码
     * @param size    每页大小
     * @param orderBy 排序方式
     * @param keyword 关键词
     * @param isAgri  是否农科专区
     * @return 问题列表
     */
    @GetMapping("/list")
    public Result getQuestionList(@RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "latest") String orderBy,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer isAgri) {
        Map<String, Object> result = questionService.getQuestionList(page, size, orderBy, keyword, isAgri);
        return Result.success(result);
    }

    /**
     * 获取问题详情
     * 
     * @param id 问题ID
     * @return 问题详情
     */
    @GetMapping("/detail")
    public Result getQuestionDetail(@RequestParam Long id) {
        java.util.Map<String, Object> question = questionService.getQuestionDetail(id);
        return question != null ? Result.success(question) : Result.error("问题不存在");
    }

    /**
     * 点赞/取消点赞问题
     * 
     * @param userId     用户ID
     * @param questionId 问题ID
     * @return 点赞结果
     */
    @PostMapping("/like")
    public Result likeQuestion(@RequestParam Long userId, @RequestParam Long questionId) {
        Integer result = questionService.likeQuestion(userId, questionId);
        switch (result) {
            case 1:
                return Result.success("点赞成功");
            case 0:
                return Result.success("取消点赞成功");
            case -1:
                return Result.error("已点赞");
            case -2:
                return Result.error("问题不存在");
            default:
                return Result.error("操作失败");
        }
    }

    /**
     * 收藏/取消收藏问题
     * 
     * @param userId     用户ID
     * @param questionId 问题ID
     * @return 收藏结果
     */
    @PostMapping("/collect")
    public Result collectQuestion(@RequestParam Long userId, @RequestParam Long questionId) {
        try {
            System.out.println("=== 收藏问题API被调用 ===");
            System.out.println("用户ID: " + userId);
            System.out.println("问题ID: " + questionId);
            
            Integer result = questionService.collectQuestion(userId, questionId);
            System.out.println("collectQuestion返回结果: " + result);
            
            switch (result) {
                case 1:
                    return Result.success("收藏成功");
                case 0:
                    return Result.success("取消收藏成功");
                case -1:
                    return Result.error(400, "已收藏");
                case -2:
                    return Result.error(404, "问题不存在");
                default:
                    return Result.error(400, "操作失败");
            }
        } catch (Exception e) {
            System.out.println("收藏问题时发生异常:");
            e.printStackTrace();
            return Result.error(500, "操作失败: " + e.getMessage());
        }
    }

    /**
     * 删除问题
     * 
     * @param questionId 问题ID
     * @param userId     用户ID
     * @return 删除结果
     */
    @DeleteMapping("/delete")
    public Result deleteQuestion(@RequestParam Long questionId, @RequestParam Long userId) {
        System.out.println("=== 删除问题API被调用 ===");
        System.out.println("问题ID: " + questionId);
        System.out.println("用户ID: " + userId);
        try {
            System.out.println("开始调用questionService.deleteQuestion");
            boolean success = questionService.deleteQuestion(questionId, userId);
            System.out.println("questionService.deleteQuestion返回: " + success);
            if (success) {
                return Result.success("删除成功");
            } else {
                return Result.error("删除失败，无权限或问题不存在");
            }
        } catch (Exception e) {
            System.out.println("删除问题时发生异常:");
            e.printStackTrace();
            return Result.error("删除失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户收藏的问题列表
     * 
     * @param userId 用户ID
     * @param page   页码
     * @param size   每页大小
     * @return 收藏的问题列表
     */
    @GetMapping("/collected")
    public Result getCollectedQuestions(@RequestParam Long userId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Map<String, Object> result = questionService.getCollectedQuestions(userId, page, size);
        return Result.success(result);
    }
}