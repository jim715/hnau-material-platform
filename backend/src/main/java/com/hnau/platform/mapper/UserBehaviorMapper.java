package com.hnau.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hnau.platform.entity.UserBehavior;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Delete;

/**
 * 用户行为Mapper
 */
@Mapper
public interface UserBehaviorMapper extends BaseMapper<UserBehavior> {
    /**
     * 检查用户是否已执行过指定行为
     * 
     * @param userId       用户ID
     * @param behaviorType 行为类型
     * @param relId        关联ID
     * @return 用户行为记录
     */
    @Select("SELECT * FROM user_behavior WHERE user_id = #{userId} AND behavior_type = #{behaviorType} AND rel_id = #{relId} LIMIT 1")
    UserBehavior checkBehaviorExists(Long userId, Integer behaviorType, Long relId);

    /**
     * 根据关联ID删除用户行为记录
     * 
     * @param relId 关联ID
     * @return 影响行数
     */
    @Delete("DELETE FROM user_behavior WHERE rel_id = #{relId}")
    int deleteByRelId(Long relId);
}