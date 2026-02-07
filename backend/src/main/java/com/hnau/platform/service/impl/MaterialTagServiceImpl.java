package com.hnau.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hnau.platform.entity.MaterialTag;
import com.hnau.platform.mapper.MaterialTagMapper;
import com.hnau.platform.service.MaterialTagService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 资料标签关联服务实现类
 */
@Service
public class MaterialTagServiceImpl extends ServiceImpl<MaterialTagMapper, MaterialTag> implements MaterialTagService {
    @Override
    public List<Long> getTagIdsByMaterialId(Long materialId) {
        return baseMapper.selectList(
                new LambdaQueryWrapper<MaterialTag>()
                        .eq(MaterialTag::getMaterialId, materialId)
        ).stream().map(MaterialTag::getTagId).collect(Collectors.toList());
    }

    @Override
    public List<Long> getMaterialIdsByTagId(Long tagId) {
        return baseMapper.selectList(
                new LambdaQueryWrapper<MaterialTag>()
                        .eq(MaterialTag::getTagId, tagId)
        ).stream().map(MaterialTag::getMaterialId).collect(Collectors.toList());
    }
}