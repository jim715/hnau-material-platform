package com.hnau.platform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hnau.platform.entity.Material;
import com.hnau.platform.vo.MaterialDetailVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 资料服务接口
 */
public interface MaterialService extends IService<Material> {
    /**
     * 根据分类查询资料
     */
    List<Material> getByCategoryId(Long categoryId);
    
    /**
     * 根据用户查询资料
     */
    List<Material> getByUserId(Long userId);
    
    /**
     * 搜索资料
     */
    List<Material> search(String keyword);
    
    /**
     * 增加资料下载次数
     */
    boolean incrementDownloadCount(Long materialId);
    
    /**
     * 增加资料浏览次数
     */
    boolean incrementViewCount(Long materialId);
    
    /**
     * 上传资料
     */
    Material uploadMaterial(MultipartFile file, String name, String description, Long categoryId, String tags, Long userId);
    
    /**
     * 获取资料列表（带筛选和排序）
     */
    List<Material> getMaterialList(String keyword, Long categoryId, String tag, String sortBy, String order, Integer page, Integer pageSize);
    
    long getMaterialCount(String keyword, Long categoryId, String tag);
    
    /**
     * 获取待审核资料列表
     */
    List<Material> getAuditList();

    /**
     * 审核资料
     */
    boolean auditMaterial(Long materialId, Integer auditStatus);

    /**
     * 标记农科精选资料
     */
    boolean markAgriMaterial(Long materialId, Integer agriTag);
}