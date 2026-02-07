package com.hnau.platform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hnau.platform.entity.Category;

import java.util.List;

/**
 * 分类服务接口
 */
public interface CategoryService extends IService<Category> {
    /**
     * 获取分类树
     * @return 分类树结构
     */
    List<Category> getCategoryTree();
}