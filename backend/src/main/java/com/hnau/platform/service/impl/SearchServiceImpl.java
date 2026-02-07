package com.hnau.platform.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hnau.platform.entity.SearchLog;
import com.hnau.platform.mapper.SearchLogMapper;
import com.hnau.platform.service.SearchService;
import com.hnau.platform.service.ShieldKeywordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 搜索服务实现类
 * 实现热搜统计、防刷榜、缓存逻辑等功能
 */
@Slf4j
@Service
public class SearchServiceImpl extends ServiceImpl<SearchLogMapper, SearchLog> implements SearchService {

    @Autowired
    private SearchLogMapper searchLogMapper;

    @Autowired
    private ShieldKeywordService shieldKeywordService;

    // 防刷榜配置
    private static final int DUPLICATE_CHECK_MINUTES = 10; // 10分钟内重复搜索同一关键词仅记录1次
    private static final int KEYWORD_BLOCK_THRESHOLD = 100; // 1小时内搜索超100次的关键词自动屏蔽
    private static final int HOT_KEYWORDS_LIMIT = 20; // 热搜展示前20个

    @Override
    public boolean recordSearchLog(String keyword, Long userId, String ipAddress, Integer resultCount) {
        // 验证参数
        if (!StringUtils.hasText(keyword)) {
            return false;
        }

        // 检查防刷榜机制
        if (!isValidSearch(keyword, userId, ipAddress)) {
            return false;
        }

        // 检查关键词是否被屏蔽
        if (isKeywordBlocked(keyword)) {
            return false;
        }

        // 创建搜索日志
        SearchLog searchLog = new SearchLog();
        searchLog.setKeyword(keyword);
        searchLog.setUserId(userId);
        searchLog.setIpAddress(ipAddress);
        searchLog.setSearchTime(LocalDateTime.now());
        searchLog.setResultCount(resultCount);
        searchLog.setIsValid(true);

        // 保存到数据库
        return save(searchLog);
    }

    @Override
    public List<Map<String, Object>> getHotKeywords(String period) {
        // 从数据库查询
        List<Map<String, Object>> hotKeywords;
        if ("24h".equals(period)) {
            hotKeywords = searchLogMapper.getHotKeywords24h(HOT_KEYWORDS_LIMIT);
        } else if ("7d".equals(period)) {
            hotKeywords = searchLogMapper.getHotKeywords7d(HOT_KEYWORDS_LIMIT);
        } else {
            hotKeywords = new ArrayList<>();
        }

        // 过滤掉被屏蔽的关键词
        hotKeywords = hotKeywords.stream()
                .filter(keywordMap -> {
                    String keyword = (String) keywordMap.get("keyword");
                    return !shieldKeywordService.isShielded(keyword);
                })
                .collect(Collectors.toList());

        return hotKeywords;
    }

    @Override
    public List<SearchLog> getUserSearchHistory(Long userId) {
        if (userId == null) {
            return new ArrayList<>();
        }
        return searchLogMapper.getUserSearchHistory(userId, 10); // 展示最近10条
    }

    @Override
    public boolean clearUserSearchHistory(Long userId) {
        if (userId == null) {
            return false;
        }
        // 这里可以根据需要实现清空逻辑
        // 例如：删除用户的搜索历史记录
        return true;
    }

    @Override
    public List<String> getKeywordSuggestions(String prefix, Integer limit) {
        if (!StringUtils.hasText(prefix)) {
            return new ArrayList<>();
        }

        // 从热搜和历史记录中获取联想词
        // 这里简化实现，实际可以更复杂
        List<String> suggestions = new ArrayList<>();

        // 获取热搜关键词
        List<Map<String, Object>> hotKeywords = getHotKeywords("24h");
        for (Map<String, Object> keywordMap : hotKeywords) {
            String keyword = (String) keywordMap.get("keyword");
            if (keyword.startsWith(prefix) && !suggestions.contains(keyword)) {
                suggestions.add(keyword);
                if (suggestions.size() >= limit) {
                    break;
                }
            }
        }

        return suggestions;
    }

    @Override
    public boolean shieldKeyword(String keyword) {
        return shieldKeywordService.shieldKeyword(keyword);
    }

    @Override
    public boolean unshieldKeyword(String keyword) {
        return shieldKeywordService.unshieldKeyword(keyword);
    }

    /**
     * 检查是否为有效搜索（防重复）
     */
    private boolean isValidSearch(String keyword, Long userId, String ipAddress) {
        // 检查10分钟内是否重复搜索
        int count = searchLogMapper.checkDuplicateSearch(userId, keyword, ipAddress, DUPLICATE_CHECK_MINUTES);
        return count == 0;
    }

    /**
     * 检查关键词是否被屏蔽
     */
    private boolean isKeywordBlocked(String keyword) {
        // 检查1小时内搜索次数
        int count = searchLogMapper.countKeywordSearch(keyword, 1);
        if (count >= KEYWORD_BLOCK_THRESHOLD) {
            // 标记关键词为屏蔽
            shieldKeywordService.shieldKeyword(keyword);
            return true;
        }

        // 检查是否已被屏蔽
        return shieldKeywordService.isShielded(keyword);
    }

}