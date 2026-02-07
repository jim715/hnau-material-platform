package com.hnau.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hnau.platform.entity.Material;
import com.hnau.platform.entity.MaterialTag;
import com.hnau.platform.entity.Tag;
import com.hnau.platform.entity.User;
import com.hnau.platform.entity.Category;
import com.hnau.platform.mapper.MaterialMapper;
import com.hnau.platform.mapper.MaterialTagMapper;
import com.hnau.platform.mapper.TagMapper;
import com.hnau.platform.mapper.UserMapper;
import com.hnau.platform.service.MaterialService;
import com.hnau.platform.service.IntegralService;
import com.hnau.platform.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 资料服务实现类
 */
@Service
public class MaterialServiceImpl extends ServiceImpl<MaterialMapper, Material> implements MaterialService {

    @Autowired
    private TagMapper tagMapper;

    @Autowired
    private MaterialTagMapper materialTagMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private com.hnau.platform.utils.FileUploadUtil fileUploadUtil;

    @Autowired
    private IntegralService integralService;

    @Autowired
    private CategoryService categoryService;

    @Override
    public List<Material> getByCategoryId(Long categoryId) {
        QueryWrapper<Material> wrapper = new QueryWrapper<>();
        wrapper.eq("category_id", categoryId);
        wrapper.orderByDesc("create_time");
        return list(wrapper);
    }

    @Override
    public List<Material> getByUserId(Long userId) {
        QueryWrapper<Material> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        wrapper.orderByDesc("create_time");
        return list(wrapper);
    }

    @Override
    public List<Material> search(String keyword) {
        QueryWrapper<Material> wrapper = new QueryWrapper<>();
        wrapper.like("name", keyword).or().like("description", keyword);
        wrapper.orderByDesc("create_time");
        return list(wrapper);
    }

    @Override
    public boolean incrementDownloadCount(Long materialId) {
        Material material = getById(materialId);
        if (material != null) {
            material.setDownloadCount(material.getDownloadCount() + 1);
            return updateById(material);
        }
        return false;
    }

    @Override
    public boolean incrementViewCount(Long materialId) {
        Material material = getById(materialId);
        if (material != null) {
            material.setViewCount(material.getViewCount() + 1);
            return updateById(material);
        }
        return false;
    }

    @Override
    public Material uploadMaterial(MultipartFile file, String name, String description, Long categoryId, String tags,
            Long userId) {
        // 处理文件上传
        String fileName = null;
        try {
            // 使用FileUploadUtil上传文件
            String uploadDir = System.getProperty("user.dir") + File.separator + "upload" + File.separator + "material";
            fileName = com.hnau.platform.utils.FileUploadUtil.uploadFile(file, uploadDir);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        // 保存资料信息
        Material material = new Material();
        material.setName(name);
        material.setDescription(description);
        material.setCategoryId(categoryId);
        material.setUserId(userId);
        material.setFilePath("material/" + fileName);
        material.setFileName(file.getOriginalFilename());
        material.setFileSize(file.getSize());
        material.setDownloadCount(0);
        material.setViewCount(0);
        material.setPoints(0);
        material.setStatus(1);
        material.setAuditStatus(0); // 设置默认审核状态：待审核
        material.setAgriTag(0); // 设置默认农科精选标记：否
        material.setCreateTime(LocalDateTime.now());
        material.setUpdateTime(LocalDateTime.now());
        save(material);

        // 处理标签
        if (tags != null && !tags.isEmpty()) {
            String[] tagNames = tags.split(",");
            for (String tagName : tagNames) {
                tagName = tagName.trim();
                if (!tagName.isEmpty()) {
                    // 查找或创建标签
                    QueryWrapper<Tag> tagWrapper = new QueryWrapper<>();
                    tagWrapper.eq("name", tagName);
                    Tag tag = tagMapper.selectOne(tagWrapper);
                    if (tag == null) {
                        tag = new Tag();
                        tag.setName(tagName);
                        tag.setUseCount(1);
                        tagMapper.insert(tag);
                    } else {
                        tag.setUseCount(tag.getUseCount() + 1);
                        tagMapper.updateById(tag);
                    }

                    // 建立资料和标签的关联
                    MaterialTag materialTag = new MaterialTag();
                    materialTag.setMaterialId(material.getId());
                    materialTag.setTagId(tag.getId());
                    materialTagMapper.insert(materialTag);
                }
            }
        }

        // 给用户加20积分
        System.out.println("=== 开始更新用户积分 ===");
        System.out.println("用户ID: " + userId);
        User user = userMapper.selectById(userId);
        if (user != null) {
            System.out.println("更新前用户积分: " + user.getPoints());
            System.out.println("更新前用户上传次数: " + user.getUploadCount());
            user.setPoints(user.getPoints() + 20);
            user.setUploadCount(user.getUploadCount() + 1);
            userMapper.updateById(user);
            System.out.println("更新后用户积分: " + user.getPoints());
            System.out.println("更新后用户上传次数: " + user.getUploadCount());
            // 同时更新Integral实体的积分
            boolean integralResult = integralService.updateIntegral(userId, 20, "上传资料获得积分", material.getId(), 2);
            System.out.println("积分更新结果: " + integralResult);
        } else {
            System.out.println("用户不存在，无法更新积分");
        }
        System.out.println("=== 积分更新结束 ===");

        return material;
    }

    @Override
    public List<Material> getMaterialList(String keyword, Long categoryId, String tag, String sortBy, String order,
            Integer page, Integer pageSize) {
        QueryWrapper<Material> wrapper = new QueryWrapper<>();

        // 只显示审核通过的资料
        wrapper.eq("audit_status", 1);

        // 关键词模糊搜索
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like("name", keyword).or().like("description", keyword);
        }

        // 分类筛选
        if (categoryId != null) {
            // 检查是否是子分类
            Category category = categoryService.getById(categoryId);
            if (category != null && category.getParentId() != null && category.getParentId() != 0) {
                // 如果是子分类，查询父分类下的所有资料，并且根据子分类名称作为标签进行筛选
                wrapper.eq("category_id", category.getParentId());

                // 根据子分类名称作为标签进行筛选
                QueryWrapper<Tag> tagWrapper = new QueryWrapper<>();
                tagWrapper.eq("name", category.getName());
                Tag tagEntity = tagMapper.selectOne(tagWrapper);
                if (tagEntity != null) {
                    QueryWrapper<MaterialTag> materialTagWrapper = new QueryWrapper<>();
                    materialTagWrapper.eq("tag_id", tagEntity.getId());
                    List<MaterialTag> materialTags = materialTagMapper.selectList(materialTagWrapper);
                    if (!materialTags.isEmpty()) {
                        List<Long> materialIds = new ArrayList<>();
                        for (MaterialTag mt : materialTags) {
                            materialIds.add(mt.getMaterialId());
                        }
                        wrapper.in("id", materialIds);
                    } else {
                        // 如果没有找到包含该标签的资料，直接返回空列表
                        return new ArrayList<>();
                    }
                } else {
                    // 如果没有找到对应的标签，直接返回空列表
                    return new ArrayList<>();
                }
            } else {
                // 如果是主分类，直接查询该分类下的所有资料
                wrapper.eq("category_id", categoryId);
            }
        }

        // 标签筛选
        if (tag != null && !tag.isEmpty()) {
            // 通过子查询获取包含指定标签的资料ID
            QueryWrapper<Tag> tagWrapper = new QueryWrapper<>();
            tagWrapper.eq("name", tag);
            Tag tagEntity = tagMapper.selectOne(tagWrapper);
            if (tagEntity != null) {
                QueryWrapper<MaterialTag> materialTagWrapper = new QueryWrapper<>();
                materialTagWrapper.eq("tag_id", tagEntity.getId());
                List<MaterialTag> materialTags = materialTagMapper.selectList(materialTagWrapper);
                if (!materialTags.isEmpty()) {
                    List<Long> materialIds = new ArrayList<>();
                    for (MaterialTag mt : materialTags) {
                        materialIds.add(mt.getMaterialId());
                    }
                    wrapper.in("id", materialIds);
                }
            }
        }

        // 排序
        if (sortBy != null && !sortBy.isEmpty()) {
            // 处理前端传递的字段名映射
            String actualSortField = sortBy;
            if ("uploadTime".equals(sortBy)) {
                actualSortField = "create_time";
            } else if ("downloadCount".equals(sortBy)) {
                actualSortField = "download_count";
            } else if ("viewCount".equals(sortBy)) {
                actualSortField = "view_count";
            }

            if ("desc".equals(order)) {
                wrapper.orderByDesc(actualSortField);
            } else {
                wrapper.orderByAsc(actualSortField);
            }
        } else {
            wrapper.orderByDesc("create_time");
        }

        // 使用MyBatis-Plus的分页功能
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<Material> pageObj = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(
                page, pageSize);
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<Material> resultPage = page(pageObj, wrapper);

        return resultPage.getRecords();
    }

    @Override
    public long getMaterialCount(String keyword, Long categoryId, String tag) {
        QueryWrapper<Material> wrapper = new QueryWrapper<>();

        // 只显示审核通过的资料
        wrapper.eq("audit_status", 1);

        // 关键词模糊搜索
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like("name", keyword).or().like("description", keyword);
        }

        // 分类筛选
        if (categoryId != null) {
            // 检查是否是子分类
            Category category = categoryService.getById(categoryId);
            if (category != null && category.getParentId() != null && category.getParentId() != 0) {
                // 如果是子分类，查询父分类下的所有资料，并且根据子分类名称作为标签进行筛选
                wrapper.eq("category_id", category.getParentId());

                // 根据子分类名称作为标签进行筛选
                QueryWrapper<Tag> tagWrapper = new QueryWrapper<>();
                tagWrapper.eq("name", category.getName());
                Tag tagEntity = tagMapper.selectOne(tagWrapper);
                if (tagEntity != null) {
                    QueryWrapper<MaterialTag> materialTagWrapper = new QueryWrapper<>();
                    materialTagWrapper.eq("tag_id", tagEntity.getId());
                    List<MaterialTag> materialTags = materialTagMapper.selectList(materialTagWrapper);
                    if (!materialTags.isEmpty()) {
                        List<Long> materialIds = new ArrayList<>();
                        for (MaterialTag mt : materialTags) {
                            materialIds.add(mt.getMaterialId());
                        }
                        wrapper.in("id", materialIds);
                    } else {
                        // 如果没有找到包含该标签的资料，返回0
                        return 0;
                    }
                } else {
                    // 如果没有找到对应的标签，返回0
                    return 0;
                }
            } else {
                // 如果是主分类，直接查询该分类下的所有资料
                wrapper.eq("category_id", categoryId);
            }
        }

        // 标签筛选
        if (tag != null && !tag.isEmpty()) {
            // 通过子查询获取包含指定标签的资料ID
            QueryWrapper<Tag> tagWrapper = new QueryWrapper<>();
            tagWrapper.eq("name", tag);
            Tag tagEntity = tagMapper.selectOne(tagWrapper);
            if (tagEntity != null) {
                QueryWrapper<MaterialTag> materialTagWrapper = new QueryWrapper<>();
                materialTagWrapper.eq("tag_id", tagEntity.getId());
                List<MaterialTag> materialTags = materialTagMapper.selectList(materialTagWrapper);
                if (!materialTags.isEmpty()) {
                    List<Long> materialIds = new ArrayList<>();
                    for (MaterialTag mt : materialTags) {
                        materialIds.add(mt.getMaterialId());
                    }
                    wrapper.in("id", materialIds);
                }
            }
        }

        return count(wrapper);
    }

    @Override
    public List<Material> getAuditList() {
        // 查询审核状态为0（待审核）的资料
        return baseMapper.selectList(new QueryWrapper<Material>().eq("audit_status", 0).orderByDesc("create_time"));
    }

    @Override
    public boolean auditMaterial(Long materialId, Integer auditStatus) {
        // 更新审核状态
        Material material = new Material();
        material.setId(materialId);
        material.setAuditStatus(auditStatus);
        return updateById(material);
    }

    @Override
    public boolean markAgriMaterial(Long materialId, Integer agriTag) {
        // 更新农科精选标记
        Material material = new Material();
        material.setId(materialId);
        material.setAgriTag(agriTag);
        return updateById(material);
    }
}