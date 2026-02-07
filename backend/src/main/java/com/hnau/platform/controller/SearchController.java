package com.hnau.platform.controller;

import com.hnau.platform.entity.SearchLog;
import com.hnau.platform.service.SearchService;
import com.hnau.platform.common.Result;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 搜索控制器
 * 提供热搜、搜索历史、关键词联想等API
 */
@RestController
@RequestMapping("/search")
public class SearchController {

    private static final Logger log = LoggerFactory.getLogger(SearchController.class);

    @Autowired
    private SearchService searchService;

    /**
     * 获取热搜排行榜
     * @param period 时间周期：24h或7d
     * @return 热搜关键词列表
     */
    @GetMapping("/hot")
    public Result getHotKeywords(@RequestParam(defaultValue = "24h") String period) {
        try {
            List<Map<String, Object>> hotKeywords = searchService.getHotKeywords(period);
            return Result.success(hotKeywords);
        } catch (Exception e) {
            log.error("获取热搜排行榜失败", e);
            return Result.error("获取热搜排行榜失败");
        }
    }

    /**
     * 获取用户搜索历史
     * @param request Http请求
     * @return 搜索历史列表
     */
    @GetMapping("/history")
    public Result getUserSearchHistory(HttpServletRequest request) {
        try {
            // 从请求中获取用户ID
            Long userId = (Long) request.getAttribute("userId");
            if (userId == null) {
                return Result.error("用户未登录");
            }

            List<SearchLog> searchHistory = searchService.getUserSearchHistory(userId);
            return Result.success(searchHistory);
        } catch (Exception e) {
            log.error("获取搜索历史失败", e);
            return Result.error("获取搜索历史失败");
        }
    }

    /**
     * 清空用户搜索历史
     * @param request Http请求
     * @return 是否清空成功
     */
    @DeleteMapping("/clear")
    public Result clearUserSearchHistory(HttpServletRequest request) {
        try {
            // 从请求中获取用户ID
            Long userId = (Long) request.getAttribute("userId");
            if (userId == null) {
                return Result.error("用户未登录");
            }

            boolean cleared = searchService.clearUserSearchHistory(userId);
            return Result.success(cleared);
        } catch (Exception e) {
            log.error("清空搜索历史失败", e);
            return Result.error("清空搜索历史失败");
        }
    }

    /**
     * 获取关键词联想
     * @param prefix 关键词前缀
     * @param limit 限制返回数量
     * @return 联想关键词列表
     */
    @GetMapping("/suggest")
    public Result getKeywordSuggestions(@RequestParam String prefix, @RequestParam(defaultValue = "10") Integer limit) {
        try {
            List<String> suggestions = searchService.getKeywordSuggestions(prefix, limit);
            return Result.success(suggestions);
        } catch (Exception e) {
            log.error("获取关键词联想失败", e);
            return Result.error("获取关键词联想失败");
        }
    }

    /**
     * 记录搜索日志
     * @param keyword 搜索关键词
     * @param resultCount 搜索结果数量
     * @param request Http请求
     * @return 是否记录成功
     */
    @PostMapping("/log")
    public Result recordSearchLog(@RequestParam String keyword, 
                                           @RequestParam(defaultValue = "0") Integer resultCount, 
                                           HttpServletRequest request) {
        try {
            // 从请求中获取用户ID
            Long userId = (Long) request.getAttribute("userId");
            // 获取用户IP
            String ipAddress = getClientIp(request);

            boolean recorded = searchService.recordSearchLog(keyword, userId, ipAddress, resultCount);
            return Result.success(recorded);
        } catch (Exception e) {
            log.error("记录搜索日志失败", e);
            return Result.error("记录搜索日志失败");
        }
    }

    /**
     * 获取客户端IP地址
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}