package com.hnau.platform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hnau.platform.entity.UploadRecord;

import java.util.List;

/**
 * 上传记录服务接口
 */
public interface UploadRecordService extends IService<UploadRecord> {
    /**
     * 根据用户查询上传记录
     */
    List<UploadRecord> getByUserId(Long userId);
    
    /**
     * 清除用户上传记录
     */
    void clearUserUploadRecords(Long userId);
}
