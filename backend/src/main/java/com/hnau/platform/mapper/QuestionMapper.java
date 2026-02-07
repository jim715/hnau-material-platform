package com.hnau.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hnau.platform.entity.Question;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 问题管理Mapper
 */
@Mapper
public interface QuestionMapper extends BaseMapper<Question> {
    /**
     * 获取问题列表（支持排序和搜索）
     * 
     * @param offset  偏移量
     * @param limit   限制数量
     * @param orderBy 排序方式
     * @param keyword 搜索关键词
     * @param isAgri  是否期末专区
     * @return 问题列表
     */
    @Select("SELECT * FROM question WHERE (#{keyword} IS NULL OR title LIKE CONCAT('%', #{keyword}, '%') OR content LIKE CONCAT('%', #{keyword}, '%')) AND (#{isAgri} = 0 OR title LIKE '%考试%' OR content LIKE '%考试%' OR tags LIKE '%考试复习%') ORDER BY create_time DESC LIMIT #{offset}, #{limit}")
    List<Question> getQuestionList(Integer offset, Integer limit, String orderBy, String keyword, Integer isAgri);

    /**
     * 获取问题总数
     * 
     * @param keyword 搜索关键词
     * @param isAgri  是否期末专区
     * @return 问题总数
     */
    @Select("SELECT COUNT(*) FROM question WHERE (#{keyword} IS NULL OR title LIKE CONCAT('%', #{keyword}, '%') OR content LIKE CONCAT('%', #{keyword}, '%')) AND (#{isAgri} = 0 OR title LIKE '%考试%' OR content LIKE '%考试%' OR tags LIKE '%考试复习%')")
    Integer getQuestionCount(String keyword, Integer isAgri);

    /**
     * 获取用户收藏的问题列表
     * 
     * @param userId 用户ID
     * @param offset 偏移量
     * @param limit  限制数量
     * @return 收藏的问题列表
     */
    @Select("SELECT q.* FROM question q JOIN user_behavior ub ON q.id = ub.rel_id WHERE ub.user_id = #{userId} AND ub.behavior_type = 2 ORDER BY q.collect_count DESC LIMIT #{offset}, #{limit}")
    List<Question> getCollectedQuestions(Long userId, Integer offset, Integer limit);

    /**
     * 获取用户收藏的问题总数
     * 
     * @param userId 用户ID
     * @return 收藏的问题总数
     */
    @Select("SELECT COUNT(*) FROM user_behavior WHERE user_id = #{userId} AND behavior_type = 2")
    Integer getCollectedQuestionCount(Long userId);
}