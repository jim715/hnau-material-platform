package com.hnau.platform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hnau.platform.entity.ShieldKeyword;

/**
 * 屏蔽关键词服务接口
 */
public interface ShieldKeywordService extends IService<ShieldKeyword> {

    /**
     * 检查关键词是否被屏蔽
     */
    boolean isShielded(String keyword);

    /**
     * 屏蔽关键词
     */
    boolean shieldKeyword(String keyword);

    /**
     * 解除屏蔽关键词
     */
    boolean unshieldKeyword(String keyword);
}