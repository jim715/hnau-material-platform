package com.hnau.platform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hnau.platform.entity.MaterialTag;

import java.util.List;

/**
 * 资料标签关联服务接口
 */
public interface MaterialTagService extends IService<MaterialTag> {
    /**
     * 根据资料ID获取标签ID列表
     * @param materialId 资料ID
     * @return 标签ID列表
     */
    List<Long> getTagIdsByMaterialId(Long materialId);

    /**
     * 根据标签ID获取资料ID列表
     * @param tagId 标签ID
     * @return 资料ID列表
     */
    List<Long> getMaterialIdsByTagId(Long tagId);
}