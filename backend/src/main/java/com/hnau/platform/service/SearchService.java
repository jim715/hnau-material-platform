package com.hnau.platform.service;

import com.hnau.platform.entity.SearchLog;
import java.util.List;
import java.util.Map;

/**
 * 搜索服务接口
 * 提供热搜、搜索历史等功能
 */
public interface SearchService {

    /**
     * 记录搜索日志
     * @param keyword 搜索关键词
     * @param userId 用户ID
     * @param ipAddress IP地址
     * @param resultCount 搜索结果数量
     * @return 是否记录成功
     */
    boolean recordSearchLog(String keyword, Long userId, String ipAddress, Integer resultCount);

    /**
     * 获取热搜排行榜
     * @param period 时间周期：24h或7d
     * @return 热搜关键词列表
     */
    List<Map<String, Object>> getHotKeywords(String period);

    /**
     * 获取用户搜索历史
     * @param userId 用户ID
     * @return 搜索历史列表
     */
    List<SearchLog> getUserSearchHistory(Long userId);

    /**
     * 清空用户搜索历史
     * @param userId 用户ID
     * @return 是否清空成功
     */
    boolean clearUserSearchHistory(Long userId);

    /**
     * 获取关键词联想
     * @param prefix 关键词前缀
     * @param limit 限制返回数量
     * @return 联想关键词列表
     */
    List<String> getKeywordSuggestions(String prefix, Integer limit);

    /**
     * 屏蔽关键词
     * @param keyword 关键词
     * @return 是否屏蔽成功
     */
    boolean shieldKeyword(String keyword);

    /**
     * 解除屏蔽关键词
     * @param keyword 关键词
     * @return 是否解除屏蔽成功
     */
    boolean unshieldKeyword(String keyword);
}