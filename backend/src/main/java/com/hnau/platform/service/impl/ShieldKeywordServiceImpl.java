package com.hnau.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hnau.platform.entity.ShieldKeyword;
import com.hnau.platform.mapper.ShieldKeywordMapper;
import com.hnau.platform.service.ShieldKeywordService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 屏蔽关键词服务实现类
 */
@Service
public class ShieldKeywordServiceImpl extends ServiceImpl<ShieldKeywordMapper, ShieldKeyword> implements ShieldKeywordService {

    @Override
    public boolean isShielded(String keyword) {
        // 查询关键词是否在屏蔽列表中
        Long count = baseMapper.selectCount(new QueryWrapper<ShieldKeyword>().eq("keyword", keyword));
        return count > 0;
    }

    @Override
    public boolean shieldKeyword(String keyword) {
        // 检查关键词是否已经被屏蔽
        if (isShielded(keyword)) {
            return true; // 已经被屏蔽，直接返回成功
        }
        // 创建新的屏蔽关键词记录
        ShieldKeyword shieldKeyword = new ShieldKeyword();
        shieldKeyword.setKeyword(keyword);
        shieldKeyword.setShieldTime(LocalDateTime.now());
        shieldKeyword.setReason("管理员手动屏蔽");
        return save(shieldKeyword);
    }

    @Override
    public boolean unshieldKeyword(String keyword) {
        // 删除屏蔽关键词记录
        int count = baseMapper.delete(new QueryWrapper<ShieldKeyword>().eq("keyword", keyword));
        return count > 0;
    }
}