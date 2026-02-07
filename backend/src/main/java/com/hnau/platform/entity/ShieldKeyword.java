package com.hnau.platform.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

/**
 * 屏蔽关键词实体类
 */
@TableName("shield_keyword")
public class ShieldKeyword {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 屏蔽关键词
     */
    private String keyword;

    /**
     * 屏蔽时间
     */
    private LocalDateTime shieldTime;

    /**
     * 屏蔽原因
     */
    private String reason;

    // getter and setter methods
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public LocalDateTime getShieldTime() {
        return shieldTime;
    }

    public void setShieldTime(LocalDateTime shieldTime) {
        this.shieldTime = shieldTime;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}