package com.hnau.platform.service;

import com.hnau.platform.entity.Question;
import java.util.List;
import java.util.Map;

/**
 * 问题管理服务接口
 */
public interface QuestionService {
    /**
     * 添加问题
     * 
     * @param question 问题信息
     * @return 是否添加成功
     */
    boolean addQuestion(Question question);

    /**
     * 获取问题列表
     * 
     * @param page    页码
     * @param size    每页大小
     * @param orderBy 排序方式
     * @param keyword 搜索关键词
     * @param isAgri  是否农科专区
     * @return 问题列表和总数
     */
    Map<String, Object> getQuestionList(Integer page, Integer size, String orderBy, String keyword, Integer isAgri);

    /**
     * 获取问题详情
     * 
     * @param id 问题ID
     * @return 问题详情
     */
    Map<String, Object> getQuestionDetail(Long id);

    /**
     * 更新问题浏览量
     * 
     * @param id 问题ID
     * @return 是否更新成功
     */
    boolean updateViewCount(Long id);

    /**
     * 更新问题回答数
     * 
     * @param id 问题ID
     * @return 是否更新成功
     */
    boolean updateAnswerCount(Long id);

    /**
     * 点赞/取消点赞问题
     * 
     * @param userId     用户ID
     * @param questionId 问题ID
     * @return 点赞结果（1：点赞成功，0：取消点赞成功，-1：已点赞，-2：问题不存在）
     */
    Integer likeQuestion(Long userId, Long questionId);

    /**
     * 收藏/取消收藏问题
     * 
     * @param userId     用户ID
     * @param questionId 问题ID
     * @return 收藏结果（1：收藏成功，0：取消收藏成功，-1：已收藏，-2：问题不存在）
     */
    Integer collectQuestion(Long userId, Long questionId);

    /**
     * 删除问题
     * 
     * @param questionId 问题ID
     * @param userId     用户ID
     * @return 是否删除成功
     */
    boolean deleteQuestion(Long questionId, Long userId);

    /**
     * 获取用户收藏的问题列表
     * 
     * @param userId 用户ID
     * @param page   页码
     * @param size   每页大小
     * @return 收藏的问题列表和总数
     */
    Map<String, Object> getCollectedQuestions(Long userId, Integer page, Integer size);
}