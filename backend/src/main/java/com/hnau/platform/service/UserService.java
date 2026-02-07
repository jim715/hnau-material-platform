package com.hnau.platform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hnau.platform.entity.User;
import java.util.List;

/**
 * 用户服务接口
 */
public interface UserService extends IService<User> {
    /**
     * 根据学号查询用户
     */
    User getByStudentId(String studentId);
    
    /**
     * 用户登录
     */
    User login(String studentId, String password);
    
    /**
     * 用户注册
     */
    boolean register(User user);
    
    /**
     * 更新用户积分
     */
    boolean updatePoints(Long userId, int points);
    
    /**
     * 扣减用户积分
     */
    boolean deductPoints(Long userId, int points);
    
    /**
     * 批量创建用户
     */
    boolean batchCreateUsers(List<User> users);
}