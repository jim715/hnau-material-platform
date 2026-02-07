package com.hnau.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hnau.platform.entity.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 评论管理Mapper
 */
@Mapper
public interface CommentMapper extends BaseMapper<Comment> {
    /**
     * 根据关联类型和关联ID获取评论列表
     * 
     * @param relType 关联类型
     * @param relId   关联ID
     * @return 评论列表
     */
    @Select("SELECT c.*, u.name AS author FROM comment c LEFT JOIN user u ON c.user_id = u.id WHERE c.rel_type = #{relType} AND c.rel_id = #{relId} ORDER BY c.create_time DESC")
    List<Comment> getCommentsByRel(Integer relType, Long relId);
}