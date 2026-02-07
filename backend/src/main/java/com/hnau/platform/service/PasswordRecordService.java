package com.hnau.platform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hnau.platform.entity.PasswordRecord;

import java.util.List;

/**
 * 密码修改记录服务接口
 */
public interface PasswordRecordService extends IService<PasswordRecord> {

    /**
     * 获取用户最近的密码修改记录
     * @param userId 用户ID
     * @param limit 限制数量
     * @return 密码修改记录列表
     */
    List<PasswordRecord> getRecentByUserId(Long userId, Integer limit);

    /**
     * 添加密码修改记录
     * @param userId 用户ID
     * @param ipAddress IP地址
     * @param device 设备信息
     */
    void addPasswordRecord(Long userId, String ipAddress, String device);
}
