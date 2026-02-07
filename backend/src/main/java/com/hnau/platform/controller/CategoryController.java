package com.hnau.platform.controller;

import com.hnau.platform.common.Result;
import com.hnau.platform.entity.Category;
import com.hnau.platform.service.CategoryService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 分类控制器
 */
@RestController
@RequestMapping("/api/category")
public class CategoryController {

    private static final Logger logger = LoggerFactory.getLogger(CategoryController.class);

    @Autowired
    private CategoryService categoryService;

    /**
     * 获取分类列表
     */
    @GetMapping("/list")
    public Result list() {
        try {
            logger.info("开始获取分类列表");
            List<Category> list = categoryService.list();
            logger.info("获取到 {} 条分类记录", list.size());
            return Result.success(list);
        } catch (Exception e) {
            logger.error("获取分类列表失败", e);
            return Result.error("获取分类列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取分类详情
     */
    @GetMapping("/info/{id}")
    public Result getById(@PathVariable Long id) {
        Category category = categoryService.getById(id);
        return Result.success(category);
    }

    /**
     * 创建分类
     */
    @PostMapping("/create")
    public Result create(@RequestBody Category category) {
        categoryService.save(category);
        return Result.success();
    }

    /**
     * 更新分类
     */
    @PutMapping("/update")
    public Result update(@RequestBody Category category) {
        categoryService.updateById(category);
        return Result.success();
    }

    /**
     * 删除分类
     */
    @DeleteMapping("/delete/{id}")
    public Result delete(@PathVariable Long id) {
        categoryService.removeById(id);
        return Result.success();
    }

    /**
     * 获取分类树
     */
    @GetMapping("/tree")
    public Result getCategoryTree() {
        List<Category> categoryTree = categoryService.getCategoryTree();
        return Result.success(categoryTree);
    }
}