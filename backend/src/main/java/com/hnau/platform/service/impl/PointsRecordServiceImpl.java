package com.hnau.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hnau.platform.entity.PointsRecord;
import com.hnau.platform.mapper.PointsRecordMapper;
import com.hnau.platform.service.PointsRecordService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 积分记录服务实现类
 */
@Service
public class PointsRecordServiceImpl extends ServiceImpl<PointsRecordMapper, PointsRecord> implements PointsRecordService {
    
    @Override
    public List<PointsRecord> getByUserId(Long userId) {
        QueryWrapper<PointsRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        wrapper.orderByDesc("create_time");
        return list(wrapper);
    }
}