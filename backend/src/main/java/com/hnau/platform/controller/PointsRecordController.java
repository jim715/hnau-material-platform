package com.hnau.platform.controller;

import com.hnau.platform.common.Result;
import com.hnau.platform.entity.PointsRecord;
import com.hnau.platform.service.PointsRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 积分记录控制器
 */
@RestController
@RequestMapping("/points")
public class PointsRecordController {
    
    @Autowired
    private PointsRecordService pointsRecordService;
    
    /**
     * 根据用户获取积分记录
     */
    @GetMapping("/list")
    public Result getPointsRecordList(@RequestParam Long userId) {
        List<PointsRecord> records = pointsRecordService.getByUserId(userId);
        return Result.success(records);
    }
    
    /**
     * 创建积分记录
     */
    @PostMapping("/create")
    public Result createPointsRecord(@RequestBody PointsRecord record) {
        boolean success = pointsRecordService.save(record);
        if (success) {
            return Result.success("创建成功");
        }
        return Result.error("创建失败");
    }
}