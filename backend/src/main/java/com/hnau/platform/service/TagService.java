package com.hnau.platform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hnau.platform.entity.Tag;

import java.util.List;
import java.util.Map;

/**
 * 标签服务接口
 */
public interface TagService extends IService<Tag> {
    /**
     * 获取标签树结构
     */
    List<Map<String, Object>> getTagTree();
}