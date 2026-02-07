package com.hnau.platform.vo;

import com.hnau.platform.entity.Material;
import java.util.List;

/**
 * 资料详情VO
 */
public class MaterialDetailVO {
    private Long id;
    private String name;
    private String description;
    private String fileName;
    private String filePath;
    private Long categoryId;
    private String categoryName;
    private Long userId;
    private String username;
    private Boolean isLiked;
    private Boolean isCollected;
    private List<String> tags;
    private Long fileSize;
    private Integer downloadCount;
    private Integer viewCount;
    private java.time.LocalDateTime createTime;

    // Getter和Setter方法
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Boolean getIsLiked() {
        return isLiked;
    }

    public void setIsLiked(Boolean isLiked) {
        this.isLiked = isLiked;
    }

    public Boolean getIsCollected() {
        return isCollected;
    }

    public void setIsCollected(Boolean isCollected) {
        this.isCollected = isCollected;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public Integer getDownloadCount() {
        return downloadCount;
    }

    public void setDownloadCount(Integer downloadCount) {
        this.downloadCount = downloadCount;
    }

    public Integer getViewCount() {
        return viewCount;
    }

    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount;
    }

    public java.time.LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(java.time.LocalDateTime createTime) {
        this.createTime = createTime;
    }

    // 从Material实体转换
    public static MaterialDetailVO fromMaterial(Material material) {
        MaterialDetailVO vo = new MaterialDetailVO();
        vo.setId(material.getId());
        vo.setName(material.getName());
        vo.setDescription(material.getDescription());
        vo.setFileName(material.getFileName());
        vo.setFilePath(material.getFilePath());
        vo.setCategoryId(material.getCategoryId());
        vo.setUserId(material.getUserId());
        vo.setFileSize(material.getFileSize());
        vo.setDownloadCount(material.getDownloadCount());
        vo.setViewCount(material.getViewCount());
        vo.setCreateTime(material.getCreateTime());
        return vo;
    }
}