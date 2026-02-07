package com.hnau.platform.controller;

import com.hnau.platform.common.Result;
import com.hnau.platform.entity.DownloadRecord;
import com.hnau.platform.service.DownloadRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 下载记录控制器
 */
@RestController
@RequestMapping("/download")
public class DownloadRecordController {
    
    @Autowired
    private DownloadRecordService downloadRecordService;
    
    /**
     * 根据用户获取下载记录
     */
    @GetMapping("/list")
    public Result getDownloadRecordList(@RequestParam Long userId) {
        List<DownloadRecord> records = downloadRecordService.getByUserId(userId);
        return Result.success(records);
    }
    
    /**
     * 根据资料获取下载记录
     */
    @GetMapping("/byMaterial")
    public Result getDownloadRecordByMaterial(@RequestParam Long materialId) {
        List<DownloadRecord> records = downloadRecordService.getByMaterialId(materialId);
        return Result.success(records);
    }
    
    /**
     * 创建下载记录
     */
    @PostMapping("/create")
    public Result createDownloadRecord(@RequestBody DownloadRecord record) {
        boolean success = downloadRecordService.save(record);
        if (success) {
            return Result.success("创建成功");
        }
        return Result.error("创建失败");
    }
}