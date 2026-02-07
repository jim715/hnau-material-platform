package com.hnau.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hnau.platform.entity.DownloadRecord;
import com.hnau.platform.mapper.DownloadRecordMapper;
import com.hnau.platform.service.DownloadRecordService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 下载记录服务实现类
 */
@Service
public class DownloadRecordServiceImpl extends ServiceImpl<DownloadRecordMapper, DownloadRecord> implements DownloadRecordService {
    
    @Override
    public List<DownloadRecord> getByUserId(Long userId) {
        QueryWrapper<DownloadRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        wrapper.orderByDesc("download_time");
        return list(wrapper);
    }
    
    @Override
    public List<DownloadRecord> getByMaterialId(Long materialId) {
        QueryWrapper<DownloadRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("material_id", materialId);
        wrapper.orderByDesc("download_time");
        return list(wrapper);
    }
    
    @Override
    public void clearUserDownloadRecords(Long userId) {
        QueryWrapper<DownloadRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        remove(wrapper);
    }
}