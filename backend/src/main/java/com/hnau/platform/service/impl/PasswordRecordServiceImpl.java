package com.hnau.platform.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hnau.platform.entity.PasswordRecord;
import com.hnau.platform.mapper.PasswordRecordMapper;
import com.hnau.platform.service.PasswordRecordService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 密码修改记录服务实现类
 */
@Service
public class PasswordRecordServiceImpl extends ServiceImpl<PasswordRecordMapper, PasswordRecord> implements PasswordRecordService {

    @Override
    public List<PasswordRecord> getRecentByUserId(Long userId, Integer limit) {
        return baseMapper.selectRecentByUserId(userId, limit);
    }

    @Override
    public void addPasswordRecord(Long userId, String ipAddress, String device) {
        PasswordRecord record = new PasswordRecord();
        record.setUserId(userId);
        record.setModifyTime(LocalDateTime.now());
        record.setIpAddress(ipAddress);
        record.setDevice(device);
        save(record);
    }
}
