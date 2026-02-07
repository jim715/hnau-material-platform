package com.hnau.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hnau.platform.entity.LoginRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 登录记录Mapper接口
 */
@Mapper
public interface LoginRecordMapper extends BaseMapper<LoginRecord> {

    /**
     * 获取所有登录记录（分页）
     * 
     * @param offset 偏移量
     * @param limit  限制数量
     * @return 登录记录列表
     */
    List<LoginRecord> getLoginRecords(@Param("offset") int offset, @Param("limit") int limit);

    /**
     * 获取指定用户的登录记录（分页）
     * 
     * @param userId 用户ID
     * @param offset 偏移量
     * @param limit  限制数量
     * @return 登录记录列表
     */
    List<LoginRecord> getLoginRecordsByUserId(@Param("userId") Long userId, @Param("offset") int offset,
            @Param("limit") int limit);

    /**
     * 获取登录记录总数
     * 
     * @return 登录记录总数
     */
    int getLoginRecordsCount();

    /**
     * 获取指定用户的登录记录总数
     * 
     * @param userId 用户ID
     * @return 登录记录总数
     */
    int getLoginRecordsCountByUserId(@Param("userId") Long userId);

    /**
     * 获取用户的最新登录记录
     * 
     * @param userId 用户ID
     * @return 最新登录记录
     */
    LoginRecord getLatestLoginRecordByUserId(@Param("userId") Long userId);

    /**
     * 获取用户所有状态为在线的登录记录
     * 
     * @param userId 用户ID
     * @return 在线登录记录列表
     */
    List<LoginRecord> getOnlineLoginRecordsByUserId(@Param("userId") Long userId);

    /**
     * 获取所有用户所有状态为在线的登录记录
     * 
     * @return 在线登录记录列表
     */
    List<LoginRecord> getAllOnlineLoginRecords();
}
