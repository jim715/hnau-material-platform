package com.hnau.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hnau.platform.entity.Integral;
import com.hnau.platform.entity.PointsRecord;
import com.hnau.platform.mapper.IntegralMapper;
import com.hnau.platform.mapper.PointsRecordMapper;
import com.hnau.platform.service.IntegralService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 积分管理服务实现类
 */
@Service
public class IntegralServiceImpl implements IntegralService {

    @Resource
    private IntegralMapper integralMapper;

    @Resource
    private PointsRecordMapper pointsRecordMapper;

    @Override
    public Integral getUserIntegral(Long userId) {
        Integral integral = integralMapper.getByUserId(userId);
        if (integral == null) {
            integral = initUserIntegral(userId);
        }
        return integral;
    }

    @Override
    public List<Map<String, Object>> getRankList(Integer limit, String college, String major) {
        // 从数据库查询，直接返回包含学院和专业信息的Map列表
        return integralMapper.getRankList(limit, college, major);
    }

    @Override
    public List<PointsRecord> getPointsRecords(Long userId, Integer page, Integer size) {
        QueryWrapper<PointsRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId)
                .orderByDesc("create_time")
                .last("LIMIT " + (page - 1) * size + ", " + size);
        return pointsRecordMapper.selectList(wrapper);
    }

    @Transactional
    @Override
    public boolean updateIntegral(Long userId, Integer points, String reason, Long relatedId, Integer type) {
        // 获取用户积分信息
        Integral integral = integralMapper.getByUserId(userId);
        if (integral == null) {
            integral = initUserIntegral(userId);
        }

        // 更新总积分
        int newTotalPoints = integral.getTotalPoints() + points;
        if (newTotalPoints < 0) {
            return false; // 积分不足
        }
        integral.setTotalPoints(newTotalPoints);

        // 如果是上传资料获得积分，更新上传资料数
        if (type == 2 && points > 0) { // 类型2表示上传资料，points>0表示获得积分
            integral.setUploadCount(integral.getUploadCount() + 1);
        }

        integral.setUpdateTime(LocalDateTime.now());
        integralMapper.updateById(integral);

        // 记录积分变动
        PointsRecord record = new PointsRecord();
        record.setUserId(userId);
        record.setType(type);
        record.setPoints(points);
        record.setRelatedId(relatedId);
        record.setReason(reason);
        record.setCreateTime(LocalDateTime.now());
        pointsRecordMapper.insert(record);

        return true;
    }

    @Override
    public Integral initUserIntegral(Long userId) {
        Integral integral = new Integral();
        integral.setUserId(userId);
        integral.setTotalPoints(0);
        integral.setUploadCount(0);
        integral.setUpdateTime(LocalDateTime.now());
        integral.setCreateTime(LocalDateTime.now());
        integralMapper.insert(integral);
        return integral;
    }

    @Override
    public Integer getUserTotalPoints(Long userId) {
        Integral integral = integralMapper.getByUserId(userId);
        if (integral == null) {
            return 0;
        }
        return integral.getTotalPoints();
    }

    @Transactional
    @Override
    public void clearUserPoints(Long userId) {
        // 清除用户积分记录
        Integral integral = integralMapper.getByUserId(userId);
        if (integral != null) {
            integral.setTotalPoints(0);
            integral.setUploadCount(0);
            integral.setUpdateTime(LocalDateTime.now());
            integralMapper.updateById(integral);
        }

        // 清除积分变动记录
        QueryWrapper<PointsRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        pointsRecordMapper.delete(wrapper);
    }

    @Override
    public int getDailyPoints(Long userId, java.util.Date date) {
        // 构建日期范围
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0);
        calendar.set(java.util.Calendar.MINUTE, 0);
        calendar.set(java.util.Calendar.SECOND, 0);
        calendar.set(java.util.Calendar.MILLISECOND, 0);
        java.util.Date startOfDay = calendar.getTime();

        calendar.add(java.util.Calendar.DAY_OF_YEAR, 1);
        java.util.Date endOfDay = calendar.getTime();

        // 转换为LocalDateTime
        LocalDateTime startDateTime = startOfDay.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime endDateTime = endOfDay.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();

        // 查询当天的积分变动记录
        QueryWrapper<PointsRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId)
                .ge("create_time", startDateTime)
                .lt("create_time", endDateTime);
        List<PointsRecord> records = pointsRecordMapper.selectList(wrapper);

        // 计算当天的积分变动总和
        int totalPoints = 0;
        for (PointsRecord record : records) {
            // 积分变动记录中的points字段已经包含了正负值
            totalPoints += record.getPoints();
        }

        return totalPoints;
    }

    @Override
    public List<Map<String, Object>> getRankListByDateRange(Integer limit, String college, String major,
            String startDate, String endDate) {
        // 从数据库查询，直接返回包含学院和专业信息的Map列表
        return integralMapper.getRankListByDateRange(limit, college, major, startDate, endDate);
    }
}