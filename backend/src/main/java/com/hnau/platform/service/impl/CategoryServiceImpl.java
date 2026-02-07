package com.hnau.platform.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hnau.platform.entity.Category;
import com.hnau.platform.mapper.CategoryMapper;
import com.hnau.platform.service.CategoryService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 分类服务实现类
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    
    @Override
    public List<Category> getCategoryTree() {
        // 获取所有分类
        List<Category> allCategories = list();
        
        // 构建分类树
        List<Category> rootCategories = allCategories.stream()
                .filter(category -> category.getParentId() == null || category.getParentId() == 0)
                .collect(Collectors.toList());
        
        // 为每个根分类设置子分类
        for (Category rootCategory : rootCategories) {
            rootCategory.setChildren(getChildren(rootCategory.getId(), allCategories));
        }
        
        return rootCategories;
    }
    
    /**
     * 递归获取子分类
     * @param parentId 父分类ID
     * @param allCategories 所有分类
     * @return 子分类列表
     */
    private List<Category> getChildren(Long parentId, List<Category> allCategories) {
        return allCategories.stream()
                .filter(category -> category.getParentId() != null && category.getParentId().equals(parentId))
                .map(category -> {
                    category.setChildren(getChildren(category.getId(), allCategories));
                    return category;
                })
                .collect(Collectors.toList());
    }
}