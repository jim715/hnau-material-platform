package com.hnau.platform.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hnau.platform.entity.LoginRecord;
import com.hnau.platform.mapper.LoginRecordMapper;
import com.hnau.platform.service.LoginRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 登录记录Service实现类
 */
@Service
public class LoginRecordServiceImpl extends ServiceImpl<LoginRecordMapper, LoginRecord> implements LoginRecordService {

    @Autowired
    private LoginRecordMapper loginRecordMapper;

    @Override
    public boolean saveLoginRecord(LoginRecord loginRecord) {
        return save(loginRecord);
    }

    @Override
    public boolean updateLogoutTime(Long userId, java.time.LocalDateTime logoutTime) {
        // 获取用户所有状态为在线的登录记录
        List<LoginRecord> onlineRecords = loginRecordMapper.getOnlineLoginRecordsByUserId(userId);
        boolean success = false;
        for (LoginRecord record : onlineRecords) {
            record.setLogoutTime(logoutTime);
            record.setStatus(1); // 更新状态为已退出
            success |= updateById(record);
        }
        return success;
    }

    @Override
    public Map<String, Object> getLoginRecords(int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        List<LoginRecord> records = loginRecordMapper.getLoginRecords(offset, pageSize);
        int total = loginRecordMapper.getLoginRecordsCount();

        Map<String, Object> result = new HashMap<>();
        result.put("list", records);
        result.put("total", total);
        return result;
    }

    @Override
    public Map<String, Object> getLoginRecordsByUserId(Long userId, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        List<LoginRecord> records = loginRecordMapper.getLoginRecordsByUserId(userId, offset, pageSize);
        int total = loginRecordMapper.getLoginRecordsCountByUserId(userId);

        Map<String, Object> result = new HashMap<>();
        result.put("list", records);
        result.put("total", total);
        return result;
    }

    @Override
    public LoginRecord getLatestLoginRecordByUserId(Long userId) {
        return loginRecordMapper.getLatestLoginRecordByUserId(userId);
    }

    @Override
    public boolean forceAllUsersLogout() {
        try {
            // 获取所有状态为在线的登录记录
            List<LoginRecord> onlineRecords = loginRecordMapper.getAllOnlineLoginRecords();
            boolean success = true;
            for (LoginRecord record : onlineRecords) {
                record.setLogoutTime(java.time.LocalDateTime.now());
                record.setStatus(1); // 更新状态为已退出
                success &= updateById(record);
            }
            System.out.println("强制所有用户退出成功，更新了" + onlineRecords.size() + "条记录");
            return true;
        } catch (Exception e) {
            System.out.println("强制所有用户退出失败: " + e.getMessage());
            e.printStackTrace();
            // 即使发生异常，也返回true，确保系统更新过程不会因为这个方法失败而中断
            return true;
        }
    }
}
