package com.hnau.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hnau.platform.entity.Integral;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 积分管理Mapper
 */
@Mapper
public interface IntegralMapper extends BaseMapper<Integral> {
    /**
     * 获取积分排行榜
     * 
     * @param limit   限制数量
     * @param college 学院筛选
     * @param major   专业筛选
     * @return 积分排行榜列表
     */
    @Select("SELECT i.*, u.name, u.student_id, u.college, u.major FROM integral i LEFT JOIN user u ON i.user_id = u.id WHERE (#{college} IS NULL OR u.college = #{college}) AND (#{major} IS NULL OR u.major = #{major}) ORDER BY i.total_points DESC LIMIT #{limit}")
    List<Map<String, Object>> getRankList(Integer limit, String college, String major);

    /**
     * 根据用户ID获取积分信息
     * 
     * @param userId 用户ID
     * @return 积分信息
     */
    @Select("SELECT * FROM integral WHERE user_id = #{userId}")
    Integral getByUserId(Long userId);

    /**
     * 获取积分排行榜（按日期范围）
     * 
     * @param limit     限制数量
     * @param college   学院筛选
     * @param major     专业筛选
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 积分排行榜列表
     */
    @Select("SELECT u.id as user_id, u.name, u.student_id, u.college, u.major, COALESCE(SUM(pr.points), 0) as total_points, COALESCE(COUNT(DISTINCT pr.related_id), 0) as upload_count FROM user u LEFT JOIN points_record pr ON u.id = pr.user_id AND pr.create_time >= #{startDate} AND pr.create_time <= #{endDate} WHERE (#{college} IS NULL OR u.college = #{college}) AND (#{major} IS NULL OR u.major = #{major}) GROUP BY u.id ORDER BY total_points DESC LIMIT #{limit}")
    List<Map<String, Object>> getRankListByDateRange(Integer limit, String college, String major, String startDate,
            String endDate);
}