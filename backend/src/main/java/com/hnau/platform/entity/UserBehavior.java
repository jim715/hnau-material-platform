package com.hnau.platform.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

/**
 * 用户行为实体类
 */
@TableName("user_behavior")
public class UserBehavior {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 行为类型（1：点赞评论，2：下载资料，3：浏览资料）
     */
    private Integer behaviorType;

    /**
     * 关联ID（如评论ID、资料ID）
     */
    private Long relId;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    // getter and setter methods
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getBehaviorType() {
        return behaviorType;
    }

    public void setBehaviorType(Integer behaviorType) {
        this.behaviorType = behaviorType;
    }

    public Long getRelId() {
        return relId;
    }

    public void setRelId(Long relId) {
        this.relId = relId;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }
}