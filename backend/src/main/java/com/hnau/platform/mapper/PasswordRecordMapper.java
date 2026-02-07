package com.hnau.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hnau.platform.entity.PasswordRecord;

import java.util.List;

/**
 * 密码修改记录Mapper
 */
public interface PasswordRecordMapper extends BaseMapper<PasswordRecord> {

    /**
     * 获取用户最近的密码修改记录
     * @param userId 用户ID
     * @param limit 限制数量
     * @return 密码修改记录列表
     */
    List<PasswordRecord> selectRecentByUserId(Long userId, Integer limit);
}
