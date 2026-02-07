package com.hnau.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hnau.platform.entity.UploadRecord;
import com.hnau.platform.mapper.UploadRecordMapper;
import com.hnau.platform.service.UploadRecordService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 上传记录服务实现类
 */
@Service
public class UploadRecordServiceImpl extends ServiceImpl<UploadRecordMapper, UploadRecord> implements UploadRecordService {
    
    @Override
    public List<UploadRecord> getByUserId(Long userId) {
        QueryWrapper<UploadRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        wrapper.orderByDesc("upload_time");
        return list(wrapper);
    }
    
    @Override
    public void clearUserUploadRecords(Long userId) {
        QueryWrapper<UploadRecord> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        remove(wrapper);
    }
}
