package com.hnau.platform.controller;

import com.hnau.platform.entity.Tag;
import com.hnau.platform.service.TagService;
import com.hnau.platform.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 标签控制器
 */
@RestController
@RequestMapping("/api/tag")
public class TagController {

    @Autowired
    private TagService tagService;

    /**
     * 获取标签列表
     */
    @GetMapping("/list")
    public Result getTagList() {
        List<Tag> tags = tagService.list();
        return Result.success(tags);
    }

    /**
     * 创建标签
     */
    @PostMapping("/create")
    public Result createTag(@RequestBody Tag tag) {
        boolean success = tagService.save(tag);
        return success ? Result.success("创建成功") : Result.error("创建失败");
    }

    /**
     * 更新标签
     */
    @PostMapping("/update")
    public Result updateTag(@RequestBody Tag tag) {
        boolean success = tagService.updateById(tag);
        return success ? Result.success("更新成功") : Result.error("更新失败");
    }

    /**
     * 删除标签
     */
    @PostMapping("/delete")
    public Result deleteTag(@RequestParam Long id) {
        boolean success = tagService.removeById(id);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }

    /**
     * 获取标签树结构
     */
    @GetMapping("/tree")
    public Result getTagTree() {
        java.util.List<java.util.Map<String, Object>> tagTree = tagService.getTagTree();
        return Result.success(tagTree);
    }
}
