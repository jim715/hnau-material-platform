package com.hnau.platform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hnau.platform.entity.PointsRecord;

import java.util.List;

/**
 * 积分记录服务接口
 */
public interface PointsRecordService extends IService<PointsRecord> {
    /**
     * 根据用户查询积分记录
     */
    List<PointsRecord> getByUserId(Long userId);
}