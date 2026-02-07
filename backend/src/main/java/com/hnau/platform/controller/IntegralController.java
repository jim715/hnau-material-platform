package com.hnau.platform.controller;

import com.hnau.platform.entity.Integral;
import com.hnau.platform.entity.PointsRecord;
import com.hnau.platform.service.IntegralService;
import com.hnau.platform.common.Result;
import com.hnau.platform.utils.JwtUtil;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 积分管理控制器
 */
@RestController
@RequestMapping("/api/integral")
public class IntegralController {

    @Resource
    private IntegralService integralService;

    @Resource
    private JwtUtil jwtUtil;

    /**
     * 获取用户积分信息
     * 
     * @param request HttpServletRequest
     * @return 积分信息
     */
    @GetMapping("/user")
    public Result getUserIntegral(HttpServletRequest request) {
        try {
            Long userId = getUserIdFromRequest(request);
            if (userId == null) {
                return Result.error("未登录");
            }
            Integral integral = integralService.getUserIntegral(userId);
            return Result.success(integral);
        } catch (Exception e) {
            System.out.println("获取用户积分失败：" + e.getMessage());
            e.printStackTrace();
            return Result.error("获取用户积分失败");
        }
    }

    /**
     * 获取近7天积分数据
     * 
     * @param request HttpServletRequest
     * @return 近7天积分数据
     */
    @GetMapping("/weekly")
    public Result getWeeklyIntegralData(HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        if (userId == null) {
            return Result.error("未登录");
        }
        // 从数据库查询近7天的积分数据
        List<Map<String, Object>> weeklyData = new ArrayList<>();
        java.util.Date today = new java.util.Date();
        // 获取近7天的日期范围
        for (int i = -6; i <= 0; i++) {
            java.util.Calendar calendar = java.util.Calendar.getInstance();
            calendar.setTime(today);
            calendar.add(java.util.Calendar.DAY_OF_YEAR, i);
            java.util.Date date = calendar.getTime();
            int month = calendar.get(java.util.Calendar.MONTH) + 1;
            int day = calendar.get(java.util.Calendar.DAY_OF_MONTH);
            String dateStr = month + "月" + day + "日";

            // 查询当天的积分变动
            int points = integralService.getDailyPoints(userId, date);

            Map<String, Object> data = new java.util.HashMap<>();
            data.put("date", dateStr);
            data.put("points", points);
            weeklyData.add(data);
        }
        return Result.success(weeklyData);
    }

    /**
     * 获取积分排行榜
     * 
     * @param limit   限制数量
     * @param college 学院筛选
     * @param major   专业筛选
     * @return 积分排行榜列表
     */
    @GetMapping("/rank")
    public Result getRankList(@RequestParam(defaultValue = "10") Integer limit,
            @RequestParam(required = false) String college,
            @RequestParam(required = false) String major) {
        List<Map<String, Object>> rankList = integralService.getRankList(limit, college, major);
        return Result.success(rankList);
    }
    
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
    @GetMapping("/rank/date")
    public Result getRankListByDateRange(@RequestParam(defaultValue = "100") Integer limit,
            @RequestParam(required = false) String college,
            @RequestParam(required = false) String major,
            @RequestParam(required = true) String startDate,
            @RequestParam(required = true) String endDate) {
        List<Map<String, Object>> rankList = integralService.getRankListByDateRange(limit, college, major, startDate, endDate);
        return Result.success(rankList);
    }

    /**
     * 获取积分变动记录
     * 
     * @param request HttpServletRequest
     * @param page    页码
     * @param size    每页大小
     * @return 积分变动记录列表
     */
    @GetMapping("/records")
    public Result getPointsRecords(HttpServletRequest request,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        Long userId = getUserIdFromRequest(request);
        if (userId == null) {
            return Result.error("未登录");
        }
        List<PointsRecord> records = integralService.getPointsRecords(userId, page, size);
        return Result.success(records);
    }

    /**
     * 从请求中获取用户ID
     * 
     * @param request HttpServletRequest
     * @return 用户ID
     */
    private Long getUserIdFromRequest(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token == null || token.isEmpty()) {
            return null;
        }

        try {
            // 去除Bearer前缀
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            // 解析token获取用户ID
            Long userId = null;
            try {
                userId = jwtUtil.getUserIdFromToken(token);
            } catch (Exception e) {
                // 如果是mock token，尝试从中提取ID
                if (token.startsWith("mock_token_")) {
                    String[] parts = token.split("_");
                    if (parts.length >= 4) {
                        userId = Long.parseLong(parts[parts.length - 1]);
                    }
                }
            }

            // 如果userId为null，并且是mock token，尝试从中提取ID
            if (userId == null && token.startsWith("mock_token_")) {
                String[] parts = token.split("_");
                if (parts.length >= 4) {
                    userId = Long.parseLong(parts[parts.length - 1]);
                }
            }

            return userId;
        } catch (Exception e) {
            return null;
        }
    }
}