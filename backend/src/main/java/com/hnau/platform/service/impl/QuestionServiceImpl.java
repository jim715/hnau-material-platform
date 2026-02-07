package com.hnau.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hnau.platform.entity.Question;
import com.hnau.platform.entity.User;
import com.hnau.platform.entity.UserBehavior;
import com.hnau.platform.mapper.QuestionMapper;
import com.hnau.platform.mapper.UserBehaviorMapper;
import com.hnau.platform.service.QuestionService;
import com.hnau.platform.service.IntegralService;
import com.hnau.platform.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 问题管理服务实现类
 */
@Service
public class QuestionServiceImpl implements QuestionService {

    @Resource
    private QuestionMapper questionMapper;

    @Resource
    private UserBehaviorMapper userBehaviorMapper;

    @Resource
    private IntegralService integralService;

    @Resource
    private UserService userService;

    @Transactional
    @Override
    public boolean addQuestion(Question question) {
        // 设置默认值
        question.setViewCount(0);
        question.setAnswerCount(0);
        question.setLikeCount(0);
        question.setCollectCount(0);
        question.setIsFeatured(0);
        question.setCreateTime(LocalDateTime.now());
        question.setUpdateTime(LocalDateTime.now());

        // 保存问题
        int result = questionMapper.insert(question);
        if (result > 0) {
            // 增加积分（提问+5）
            integralService.updateIntegral(question.getUserId(), 5, "提问获得积分", question.getId(), 1);
            // 同时更新User实体中的points字段
            userService.updatePoints(question.getUserId(), 5);
            return true;
        }
        return false;
    }

    @Override
    public Map<String, Object> getQuestionList(Integer page, Integer size, String orderBy, String keyword,
            Integer isAgri) {
        // 计算偏移量
        Integer offset = (page - 1) * size;

        // 确保isAgri不为null，默认使用0（不是农科专区）
        if (isAgri == null) {
            isAgri = 0;
        }

        // 构建排序方式
        String orderByClause = "create_time DESC";
        if ("hottest".equals(orderBy)) {
            orderByClause = "view_count DESC";
        } else if ("featured".equals(orderBy)) {
            orderByClause = "like_count DESC, is_featured DESC, create_time DESC";
        }

        // 获取问题列表
        List<Question> questionList = questionMapper.getQuestionList(offset, size, orderByClause, keyword, isAgri);

        // 获取问题总数
        Integer total = questionMapper.getQuestionCount(keyword, isAgri);

        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("list", questionList);
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);

        return result;
    }

    @Override
    public Map<String, Object> getQuestionDetail(Long id) {
        try {
            // 更新浏览量
            updateViewCount(id);
            // 获取问题详情
            Question question = questionMapper.selectById(id);
            if (question != null) {
                // 构建返回结果
                Map<String, Object> result = new HashMap<>();
                result.put("id", question.getId());
                result.put("userId", question.getUserId());
                result.put("title", question.getTitle());
                result.put("content", question.getContent());
                result.put("tags", question.getTags());
                result.put("viewCount", question.getViewCount());
                result.put("answerCount", question.getAnswerCount());
                result.put("likeCount", question.getLikeCount());
                result.put("collectCount", question.getCollectCount());
                result.put("isFeatured", question.getIsFeatured());
                result.put("createTime", question.getCreateTime());
                result.put("updateTime", question.getUpdateTime());
                result.put("author", "未知用户");

                // 尝试获取用户信息
                if (question.getUserId() != null && userService != null) {
                    try {
                        com.hnau.platform.entity.User user = userService.getById(question.getUserId());
                        if (user != null) {
                            result.put("author", user.getName());
                        }
                    } catch (Exception e) {
                        // 用户信息获取失败，不影响问题详情返回
                        System.err.println("获取用户信息失败: " + e.getMessage());
                    }
                }

                return result;
            }
        } catch (Exception e) {
            System.err.println("获取问题详情失败: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean updateViewCount(Long id) {
        Question question = questionMapper.selectById(id);
        if (question != null) {
            question.setViewCount(question.getViewCount() + 1);
            question.setUpdateTime(LocalDateTime.now());
            questionMapper.updateById(question);
            return true;
        }
        return false;
    }

    @Override
    public boolean updateAnswerCount(Long id) {
        Question question = questionMapper.selectById(id);
        if (question != null) {
            question.setAnswerCount(question.getAnswerCount() + 1);
            question.setUpdateTime(LocalDateTime.now());
            questionMapper.updateById(question);
            return true;
        }
        return false;
    }

    @Transactional
    @Override
    public Integer likeQuestion(Long userId, Long questionId) {
        // 检查问题是否存在
        Question question = questionMapper.selectById(questionId);
        if (question == null) {
            return -2; // 问题不存在
        }

        // 检查是否已点赞
        UserBehavior behavior = userBehaviorMapper.checkBehaviorExists(userId, 1, questionId);
        if (behavior != null) {
            // 已点赞，取消点赞
            userBehaviorMapper.deleteById(behavior.getId());
            // 检查当前点赞数是否大于0，避免出现负数
            if (question.getLikeCount() > 0) {
                question.setLikeCount(question.getLikeCount() - 1);
                questionMapper.updateById(question);
            }
            // 扣减积分（取消点赞-1）
            integralService.updateIntegral(userId, -1, "取消点赞扣除积分", questionId, 2);
            return 0; // 取消点赞成功
        } else {
            // 未点赞，添加点赞
            UserBehavior newBehavior = new UserBehavior();
            newBehavior.setUserId(userId);
            newBehavior.setBehaviorType(1);
            newBehavior.setRelId(questionId);
            newBehavior.setCreateTime(LocalDateTime.now());
            userBehaviorMapper.insert(newBehavior);
            question.setLikeCount(question.getLikeCount() + 1);
            questionMapper.updateById(question);
            // 增加积分（点赞+1）
            integralService.updateIntegral(userId, 1, "点赞获得积分", questionId, 1);
            return 1; // 点赞成功
        }
    }

    @Transactional
    @Override
    public Integer collectQuestion(Long userId, Long questionId) {
        try {
            System.out.println("=== 收藏问题开始 ===");
            System.out.println("用户ID: " + userId);
            System.out.println("问题ID: " + questionId);

            // 检查问题是否存在
            Question question = questionMapper.selectById(questionId);
            if (question == null) {
                System.out.println("问题不存在");
                return -2; // 问题不存在
            }
            System.out.println("问题存在，当前收藏数: " + question.getCollectCount());

            // 检查是否已收藏
            UserBehavior behavior = userBehaviorMapper.checkBehaviorExists(userId, 2, questionId);
            if (behavior != null) {
                // 已收藏，取消收藏
                System.out.println("已收藏，取消收藏");
                userBehaviorMapper.deleteById(behavior.getId());
                // 检查当前收藏数是否大于0，避免出现负数
                if (question.getCollectCount() > 0) {
                    question.setCollectCount(question.getCollectCount() - 1);
                    questionMapper.updateById(question);
                    System.out.println("取消收藏成功，更新后收藏数: " + question.getCollectCount());
                }
                // 扣减积分（取消收藏-2）
                integralService.updateIntegral(userId, -2, "取消收藏扣除积分", questionId, 2);
                return 0; // 取消收藏成功
            } else {
                // 未收藏，添加收藏
                System.out.println("未收藏，添加收藏");
                UserBehavior newBehavior = new UserBehavior();
                newBehavior.setUserId(userId);
                newBehavior.setBehaviorType(2);
                newBehavior.setRelId(questionId);
                newBehavior.setCreateTime(LocalDateTime.now());
                userBehaviorMapper.insert(newBehavior);
                question.setCollectCount(question.getCollectCount() + 1);
                questionMapper.updateById(question);
                System.out.println("收藏成功，更新后收藏数: " + question.getCollectCount());
                // 增加积分（收藏+2）
                integralService.updateIntegral(userId, 2, "收藏获得积分", questionId, 1);
                return 1; // 收藏成功
            }
        } catch (Exception e) {
            System.out.println("收藏问题时发生异常:");
            e.printStackTrace();
            throw e;
        }
    }

    @Transactional
    @Override
    public boolean deleteQuestion(Long questionId, Long userId) {
        try {
            System.out.println("=== 删除问题开始 ===");
            System.out.println("问题ID: " + questionId);
            System.out.println("用户ID: " + userId);

            // 检查问题是否存在
            Question question = questionMapper.selectById(questionId);
            if (question == null) {
                System.out.println("问题不存在");
                return false; // 问题不存在
            }
            System.out.println("问题存在，创建者ID: " + question.getUserId());

            // 检查是否是问题的创建者或管理员
            boolean hasPermission = false;

            // 检查是否是问题的创建者
            if (question.getUserId() != null && question.getUserId().equals(userId)) {
                System.out.println("是问题的创建者，有权限删除");
                hasPermission = true;
            } else {
                // 检查是否是管理员
                System.out.println("不是问题的创建者，检查是否是管理员");
                User user = userService.getById(userId);
                System.out.println("用户信息: " + user);
                if (user != null) {
                    System.out.println("用户角色: " + user.getRole());
                    if (user.getRole() != null && user.getRole() == 1) {
                        System.out.println("是管理员，有权限删除");
                        hasPermission = true;
                    } else {
                        System.out.println("不是管理员，无权限删除");
                    }
                } else {
                    System.out.println("用户不存在，无权限删除");
                }
            }

            if (!hasPermission) {
                System.out.println("无权限删除问题");
                return false; // 无权限删除
            }
            System.out.println("有权限删除问题");

            // 删除相关的用户行为记录（点赞、收藏）
            try {
                userBehaviorMapper.deleteByRelId(questionId);
            } catch (Exception e) {
                // 记录异常但继续执行
                e.printStackTrace();
            }

            // 删除相关的评论
            // 这里需要添加评论删除的逻辑，假设存在CommentMapper
            // try {
            // commentMapper.deleteByQuestionId(questionId);
            // } catch (Exception e) {
            // // 记录异常但继续执行
            // e.printStackTrace();
            // }

            // 删除问题本身
            return questionMapper.deleteById(questionId) > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Map<String, Object> getCollectedQuestions(Long userId, Integer page, Integer size) {
        // 计算偏移量
        Integer offset = (page - 1) * size;

        // 获取用户收藏的问题列表
        List<Question> questionList = questionMapper.getCollectedQuestions(userId, offset, size);

        // 获取用户收藏的问题总数
        Integer total = questionMapper.getCollectedQuestionCount(userId);

        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("list", questionList);
        result.put("total", total);
        result.put("page", page);
        result.put("size", size);

        return result;
    }
}