package com.hnau.platform.service;

import com.hnau.platform.entity.Integral;
import com.hnau.platform.entity.PointsRecord;
import java.util.List;
import java.util.Map;

/**
 * 积分管理服务接口
 */
public interface IntegralService {
    /**
     * 获取用户积分信息
     * 
     * @param userId 用户ID
     * @return 积分信息
     */
    Integral getUserIntegral(Long userId);

    /**
     * 获取积分排行榜
     * 
     * @param limit   限制数量
     * @param college 学院筛选
     * @param major   专业筛选
     * @return 积分排行榜列表
     */
    List<Map<String, Object>> getRankList(Integer limit, String college, String major);

    /**
     * 获取用户积分变动记录
     * 
     * @param userId 用户ID
     * @param page   页码
     * @param size   每页大小
     * @return 积分变动记录列表
     */
    List<PointsRecord> getPointsRecords(Long userId, Integer page, Integer size);

    /**
     * 更新用户积分
     * 
     * @param userId    用户ID
     * @param points    积分变动数量
     * @param reason    变动原因
     * @param relatedId 关联ID
     * @param type      变动类型（1：获取，2：消耗）
     * @return 是否更新成功
     */
    boolean updateIntegral(Long userId, Integer points, String reason, Long relatedId, Integer type);

    /**
     * 初始化用户积分
     * 
     * @param userId 用户ID
     * @return 积分信息
     */
    Integral initUserIntegral(Long userId);

    /**
     * 获取用户总积分
     * 
     * @param userId 用户ID
     * @return 总积分
     */
    Integer getUserTotalPoints(Long userId);

    /**
     * 清除用户积分及相关记录
     * 
     * @param userId 用户ID
     */
    void clearUserPoints(Long userId);

    /**
     * 获取用户在指定日期的积分变动
     * 
     * @param userId 用户ID
     * @param date   指定日期
     * @return 积分变动值
     */
    int getDailyPoints(Long userId, java.util.Date date);
    
    /**
     * 获取积分排行榜（按日期范围）
     * 
     * @param limit      限制数量
     * @param college    学院筛选
     * @param major      专业筛选
     * @param startDate  开始日期
     * @param endDate    结束日期
     * @return 积分排行榜列表
     */
    List<Map<String, Object>> getRankListByDateRange(Integer limit, String college, String major, String startDate, String endDate);
}