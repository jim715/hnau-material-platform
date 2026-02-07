package com.hnau.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hnau.platform.entity.SearchLog;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;

/**
 * 搜索日志Mapper接口
 */
public interface SearchLogMapper extends BaseMapper<SearchLog> {

    /**
     * 统计近24小时搜索关键词热度
     * @param limit 限制返回数量
     * @return 关键词及热度统计
     */
    List<Map<String, Object>> getHotKeywords24h(@Param("limit") Integer limit);

    /**
     * 统计近7天搜索关键词热度
     * @param limit 限制返回数量
     * @return 关键词及热度统计
     */
    List<Map<String, Object>> getHotKeywords7d(@Param("limit") Integer limit);

    /**
     * 获取用户最近搜索历史
     * @param userId 用户ID
     * @param limit 限制返回数量
     * @return 搜索历史记录
     */
    List<SearchLog> getUserSearchHistory(@Param("userId") Long userId, @Param("limit") Integer limit);

    /**
     * 检查用户在指定时间内是否重复搜索同一关键词
     * @param userId 用户ID
     * @param keyword 搜索关键词
     * @param ipAddress IP地址
     * @param minutes 时间范围（分钟）
     * @return 是否存在重复搜索
     */
    Integer checkDuplicateSearch(@Param("userId") Long userId, 
                                 @Param("keyword") String keyword, 
                                 @Param("ipAddress") String ipAddress, 
                                 @Param("minutes") Integer minutes);

    /**
     * 统计关键词在指定时间内的搜索次数
     * @param keyword 搜索关键词
     * @param hours 时间范围（小时）
     * @return 搜索次数
     */
    Integer countKeywordSearch(@Param("keyword") String keyword, @Param("hours") Integer hours);
}