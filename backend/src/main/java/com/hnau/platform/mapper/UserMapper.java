package com.hnau.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hnau.platform.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * 用户Mapper接口
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
    /**
     * 根据学号查询用户
     * @param studentId 学号
     * @return 用户信息
     */
    @Select("SELECT * FROM user WHERE student_id = #{studentId}")
    User selectByStudentId(String studentId);
}