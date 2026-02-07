package com.hnau.platform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hnau.platform.entity.DownloadRecord;

import java.util.List;

/**
 * 下载记录服务接口
 */
public interface DownloadRecordService extends IService<DownloadRecord> {
    /**
     * 根据用户查询下载记录
     */
    List<DownloadRecord> getByUserId(Long userId);
    
    /**
     * 根据资料查询下载记录
     */
    List<DownloadRecord> getByMaterialId(Long materialId);
    
    /**
     * 清除用户下载记录
     */
    void clearUserDownloadRecords(Long userId);
}