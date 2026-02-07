package com.hnau.platform.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hnau.platform.entity.Tag;
import com.hnau.platform.mapper.TagMapper;
import com.hnau.platform.service.TagService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 标签服务实现类
 */
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagService {

    @Override
    public List<java.util.Map<String, Object>> getTagTree() {
        // 获取所有标签
        List<Tag> allTags = list();

        // 构建标签ID到标签的映射
        Map<Long, Tag> tagMap = allTags.stream()
                .collect(Collectors.toMap(Tag::getId, tag -> tag));

        // 构建树结构
        List<java.util.Map<String, Object>> rootTags = new ArrayList<>();

        // 首先处理所有标签，转换为Map结构并添加children属性
        Map<Long, java.util.Map<String, Object>> tagNodeMap = new java.util.HashMap<>();
        for (Tag tag : allTags) {
            java.util.Map<String, Object> tagNode = new java.util.HashMap<>();
            tagNode.put("id", tag.getId());
            tagNode.put("name", tag.getName());
            tagNode.put("parentId", tag.getParentId());
            tagNode.put("useCount", tag.getUseCount());
            tagNode.put("status", tag.getStatus());
            tagNode.put("children", new ArrayList<>());
            tagNodeMap.put(tag.getId(), tagNode);
        }

        // 构建父子关系
        for (Tag tag : allTags) {
            Long parentId = tag.getParentId();
            java.util.Map<String, Object> tagNode = tagNodeMap.get(tag.getId());

            if (parentId == null || parentId == 0) {
                // 根标签
                rootTags.add(tagNode);
            } else {
                // 子标签，添加到父标签的children中
                java.util.Map<String, Object> parentNode = tagNodeMap.get(parentId);
                if (parentNode != null) {
                    List<java.util.Map<String, Object>> children = (List<java.util.Map<String, Object>>) parentNode
                            .get("children");
                    children.add(tagNode);
                }
            }
        }

        return rootTags;
    }
}