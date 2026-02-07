package com.hnau.platform.service;

import com.hnau.platform.entity.LoginRecord;

import java.util.List;
import java.util.Map;

/**
 * 登录记录Service接口
 */
public interface LoginRecordService {

    /**
     * 保存登录记录
     * 
     * @param loginRecord 登录记录
     * @return 是否保存成功
     */
    boolean saveLoginRecord(LoginRecord loginRecord);

    /**
     * 更新登出时间
     * 
     * @param userId     用户ID
     * @param logoutTime 登出时间
     * @return 是否更新成功
     */
    boolean updateLogoutTime(Long userId, java.time.LocalDateTime logoutTime);

    /**
     * 获取登录记录列表（分页）
     * 
     * @param page     页码
     * @param pageSize 每页大小
     * @return 登录记录列表和总数
     */
    Map<String, Object> getLoginRecords(int page, int pageSize);

    /**
     * 获取指定用户的登录记录（分页）
     * 
     * @param userId   用户ID
     * @param page     页码
     * @param pageSize 每页大小
     * @return 登录记录列表和总数
     */
    Map<String, Object> getLoginRecordsByUserId(Long userId, int page, int pageSize);

    /**
     * 获取用户的最新登录记录
     * 
     * @param userId 用户ID
     * @return 最新登录记录
     */
    LoginRecord getLatestLoginRecordByUserId(Long userId);

    /**
     * 强制所有用户退出
     * 
     * @return 是否强制退出成功
     */
    boolean forceAllUsersLogout();
}
