<template>
  <div class="home-container">
    <el-card class="search-card">
      <el-input
        v-model="searchKeyword"
        placeholder="搜索资料"
        clearable
        suffix-icon="el-icon-search"
        @keyup.enter="handleSearch"
      >
        <template #append>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
        </template>
      </el-input>
    </el-card>
    
    <el-card class="combined-card">
      <template #header>
        <div class="card-header">
          <el-select v-model="sortBy" @change="handleSort">
            <el-option label="最新上传" value="uploadTime"></el-option>
            <el-option label="最多下载" value="downloadCount"></el-option>
            <el-option label="最多浏览" value="viewCount"></el-option>
          </el-select>
        </div>
      </template>
      
      <!-- 左右布局容器 -->
      <div class="layout-container">
        <!-- 左侧：资料分类 -->
        <div class="category-section">
          <div class="category-header">
            <h3>资料分类</h3>
          </div>
          <div class="category-roadmap">
            <!-- 全部分类 -->
            <div class="roadmap-item">
              <el-tag
                @click="selectCategory(0)"
                :effect="selectedCategory === 0 && !selectedTag ? 'dark' : 'plain'"
                class="category-tag all-category"
              >
                全部
              </el-tag>
            </div>
            
            <!-- 父分类路线图 -->
            <template v-for="(category, index) in categoryTree" :key="category.id">
              <div class="roadmap-item">
                <!-- 父分类标签 -->
                <div 
                  @click="toggleCategory(category.name)"
                  class="parent-category-container"
                  :class="{ 'expanded': activeCategoryNames.includes(category.name) }"
                >
                  <div class="roadmap-dot"></div>
                  <div class="roadmap-line" v-if="index < categoryTree.length - 1"></div>
                  <div class="parent-category-wrapper">
                    <el-tag
                      class="category-tag parent-category"
                      :class="{ 'active': activeCategoryNames.includes(category.name) }"
                    >
                      {{ category.name }}
                      <el-icon v-if="category.children && category.children.length > 0" class="expand-icon">
                        <ArrowDown v-if="activeCategoryNames.includes(category.name)" />
                        <ArrowRight v-else />
                      </el-icon>
                    </el-tag>
                    
                    <!-- 子分类弹出层 -->
                    <div v-if="activeCategoryNames.includes(category.name) && category.children && category.children.length > 0" class="sub-category-popup">
                      <div class="popup-content">
                        <el-tag
                          v-for="subCategory in category.children"
                          :key="subCategory.id"
                          @click="selectCategory(subCategory.id)"
                          :effect="selectedCategory === subCategory.id && !selectedTag ? 'dark' : 'plain'"
                          class="category-tag sub-category"
                        >
                          {{ subCategory.name }}
                        </el-tag>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </template>
          </div>
        </div>
        
        <!-- 右侧：资料列表 -->
        <div class="material-section">
          <div v-if="materials.length === 0 && !loading" class="no-data">
            <el-empty description="暂无资料"></el-empty>
          </div>
          <el-table v-else :data="materials" style="width: 100%" v-loading="loading">
            <el-table-column prop="name" label="资料名称" min-width="300">
              <template #default="scope">
                <a href="#" @click.prevent="viewMaterial(scope.row)">{{ scope.row.name }}</a>
              </template>
            </el-table-column>
            <el-table-column prop="categoryName" label="分类" width="120"></el-table-column>
            <el-table-column prop="uploadTime" label="上传时间" width="180">
              <template #default="scope">
                {{ formatDate(scope.row.uploadTime) }}
              </template>
            </el-table-column>
            <el-table-column prop="viewCount" label="浏览" width="80"></el-table-column>
            <el-table-column prop="downloadCount" label="下载" width="80"></el-table-column>
            <el-table-column label="操作" width="120">
              <template #default="scope">
                <el-button type="primary" size="small" @click="downloadMaterial(scope.row)">
                  下载
                </el-button>
              </template>
            </el-table-column>
          </el-table>
          <div v-if="total > 0" class="pagination-container">
            <el-pagination
              v-model:current-page="currentPage"
              v-model:page-size="pageSize"
              :page-sizes="[10, 20, 50]"
              layout="total, sizes, prev, pager, next, jumper"
              :total="total"
              @size-change="handleSizeChange"
              @current-change="handleCurrentChange"
            />
          </div>
        </div>
      </div>
    </el-card>
    
    <!-- 资料详情模态框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="materialDetail.name"
      width="80%"
      destroy-on-close
    >
      <div class="material-detail">
        <div class="detail-header">
          <el-descriptions :column="2" border>
            <el-descriptions-item label="上传者">{{ materialDetail.uploader || '未知' }}</el-descriptions-item>
            <el-descriptions-item label="上传时间">{{ formatDate(materialDetail.uploadTime) }}</el-descriptions-item>
            <el-descriptions-item label="分类">{{ materialDetail.categoryName }}</el-descriptions-item>
            <el-descriptions-item label="浏览次数">{{ materialDetail.viewCount }}次</el-descriptions-item>
            <el-descriptions-item label="下载次数">{{ materialDetail.downloadCount }}次</el-descriptions-item>
            <el-descriptions-item label="文件大小">{{ formatFileSize(materialDetail.fileSize) }}</el-descriptions-item>
          </el-descriptions>
        </div>
        
        <div class="detail-content">
          <h3>资料介绍</h3>
          <p>{{ materialDetail.description }}</p>
        </div>
        
        <div class="detail-tags">
          <h3>标签</h3>
          <el-tag
            v-for="(tag, index) in materialDetail.tags"
            :key="index"
            size="small"
            class="detail-tag"
            @click="selectTag(tag)"
          >
            {{ tag }}
          </el-tag>
        </div>
        
        <div class="detail-actions">
          <el-button type="primary" size="large" @click="downloadMaterial(materialDetail)">
            <el-icon><Download /></el-icon>
            下载资料
          </el-button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Download, ArrowDown, ArrowRight } from '@element-plus/icons-vue'
import api from '../api/index.js'
import axios from 'axios'

const router = useRouter()
const searchKeyword = ref('')
const categoryTree = ref([])
const allTags = ref([])
const activeCategoryNames = ref([])
const materials = ref([])
const selectedCategory = ref(0)
const selectedTag = ref('')
const sortBy = ref('uploadTime')
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const loading = ref(false)
const dialogVisible = ref(false)
const materialDetail = ref({})

// 格式化日期
const formatDate = (date) => {
  if (!date) return ''
  const d = new Date(date)
  return d.toLocaleString()
}

// 格式化文件大小
const formatFileSize = (bytes) => {
  if (!bytes) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB', 'TB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

// 获取分类树
const getCategoryTree = async () => {
  try {
    const response = await api.get('/category/tree')
    if (response.code === 200) {
      categoryTree.value = response.data
    } else {
      ElMessage.error('获取分类失败')
    }
  } catch (error) {
    console.error('获取分类失败:', error)
    ElMessage.error('获取分类失败，请检查网络连接')
  }
}

// 获取标签树
const getTagTree = async () => {
  try {
    const response = await api.get('/tag/tree')
    if (response.code === 200) {
      // 提取所有标签，包括子标签
      const extractTags = (tags) => {
        let result = []
        for (const tag of tags) {
          result.push(tag)
          if (tag.children && tag.children.length > 0) {
            result = result.concat(extractTags(tag.children))
          }
        }
        return result
      }
      allTags.value = extractTags(response.data)
    } else {
      ElMessage.error('获取标签失败')
    }
  } catch (error) {
    console.error('获取标签失败:', error)
    ElMessage.error('获取标签失败，请检查网络连接')
  }
}

// 获取资料列表
const getMaterials = async () => {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      pageSize: pageSize.value,
      categoryId: selectedCategory.value || undefined,
      keyword: searchKeyword.value || undefined,
      tag: selectedTag.value || undefined,
      sortBy: sortBy.value
    }
    const response = await api.get('/material/list', { params })
    if (response.code === 200) {
      // 转换数据格式
      materials.value = response.data.list.map(item => {
        // 查找分类名称
        let categoryName = '未分类'
        const findCategoryName = (categories, id) => {
          for (const category of categories) {
            if (category.id === id) {
              return category.name
            }
            if (category.children && category.children.length > 0) {
              const name = findCategoryName(category.children, id)
              if (name) {
                return name
              }
            }
          }
          return null
        }
        const foundName = findCategoryName(categoryTree.value, item.categoryId)
        if (foundName) {
          categoryName = foundName
        }
        
        return {
          ...item,
          uploadTime: item.createTime, // 将createTime转换为uploadTime
          categoryName: categoryName // 添加categoryName字段
        }
      })
      total.value = response.data.total
    } else {
      ElMessage.error(response.message)
    }
  } catch (error) {
    console.error('获取资料列表失败:', error)
    ElMessage.error('获取资料列表失败，请检查网络连接')
  } finally {
    loading.value = false
  }
}

// 搜索资料
const handleSearch = () => {
  currentPage.value = 1
  getMaterials()
}

// 选择分类
const selectCategory = (categoryId) => {
  selectedCategory.value = categoryId
  selectedTag.value = '' // 重置标签选择
  currentPage.value = 1
  getMaterials()
}

// 选择标签
const selectTag = (tag) => {
  selectedTag.value = tag
  currentPage.value = 1
  getMaterials()
  dialogVisible.value = false // 关闭详情模态框
}

// 切换分类展开/收起
const toggleCategory = (categoryName) => {
  const index = activeCategoryNames.value.indexOf(categoryName)
  if (index > -1) {
    activeCategoryNames.value.splice(index, 1)
  } else {
    activeCategoryNames.value.push(categoryName)
  }
}

// 排序
const handleSort = () => {
  currentPage.value = 1
  getMaterials()
}

// 分页处理
const handleSizeChange = (size) => {
  pageSize.value = size
  currentPage.value = 1
  getMaterials()
}

const handleCurrentChange = (current) => {
  currentPage.value = current
  getMaterials()
}

// 查看资料详情
const viewMaterial = async (material) => {
  try {
    // 获取资料详细信息
    const response = await api.get(`/material/info/${material.id}`)
    if (response.code === 200) {
      const detail = response.data
      // 查找分类名称
      let categoryName = '未分类'
      const findCategoryName = (categories, id) => {
        for (const category of categories) {
          if (category.id === id) {
            return category.name
          }
          if (category.children && category.children.length > 0) {
            const name = findCategoryName(category.children, id)
            if (name) {
              return name
            }
          }
        }
        return null
      }
      const foundName = findCategoryName(categoryTree.value, detail.categoryId)
      if (foundName) {
        categoryName = foundName
      }
      
      // 构建资料详情对象
      materialDetail.value = {
        ...detail,
        uploadTime: detail.createTime,
        categoryName: categoryName,
        uploader: detail.username || '未知用户', // 使用后端返回的用户名
        tags: detail.tags || [] // 使用后端返回的标签信息
      }
      
      // 显示模态框
      dialogVisible.value = true
    } else {
      ElMessage.error('获取资料详情失败')
    }
  } catch (error) {
    console.error('获取资料详情失败:', error)
    ElMessage.error('获取资料详情失败，请检查网络连接')
  }
}

// 下载资料
const downloadMaterial = async (material) => {
  console.log('开始下载资料:', material)
  
  // 检查用户是否登录
  const userInfo = localStorage.getItem('user')
  if (!userInfo) {
    ElMessage.error('请先登录')
    router.push('/login')
    return
  }
  
  try {
    const user = JSON.parse(userInfo)
    const userId = user.id
    const token = localStorage.getItem('token')
    
    console.log('用户ID:', userId)
    console.log('认证 token:', token)
    
    // 构建完整的API URL
    const apiUrl = `http://localhost:8082/api/material/download/${material.id}`
    console.log('下载API URL:', apiUrl)
    
    // 直接使用fetch API，避免axios拦截器的问题
    const response = await fetch(`${apiUrl}?userId=${userId}`, {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${token}`
      }
    })
    
    console.log('响应状态:', response.status)
    console.log('响应状态文本:', response.statusText)
    
    if (!response.ok) {
      // 尝试解析后端返回的错误信息
      const errorData = await response.json().catch(() => null)
      if (errorData && errorData.message) {
        throw new Error(errorData.message)
      } else {
        throw new Error(`HTTP error! status: ${response.status}`)
      }
    }
    
    // 处理blob响应
    const blob = await response.blob()
    console.log('获取到的blob:', blob)
    
    // 创建下载链接
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = material.fileName || material.name
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    
    ElMessage.success('下载成功')
  } catch (error) {
    console.error('下载资料失败:', error)
    ElMessage.error(error.message || '下载失败，请检查网络连接')
  }
}

// 初始化数据
onMounted(() => {
  getCategoryTree()
  getTagTree()
  getMaterials()
})
</script>

<style scoped>
.home-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.search-card {
  margin-bottom: 20px;
}

.combined-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.layout-container {
  display: flex;
  gap: 20px;
  padding: 20px;
}

.category-section {
  flex: 0 0 280px;
  background-color: #f8f9fa;
  border-radius: 8px;
  padding: 20px;
  max-height: 600px;
  overflow-y: auto;
  position: relative;
}

.category-header {
  margin-bottom: 20px;
}

.category-header h3 {
  margin: 0;
  color: #303133;
  font-size: 16px;
  font-weight: 600;
}

/* 路线图样式 */
.category-roadmap {
  position: relative;
}

.roadmap-item {
  position: relative;
  margin-bottom: 20px;
  display: flex;
  align-items: center;
}

/* 全部分类 */
.all-category {
  background-color: #409EFF;
  color: white;
  font-weight: 600;
  width: 100%;
  text-align: center;
  margin-bottom: 20px;
}

.all-category:hover {
  background-color: #66b1ff;
  transform: none;
}

/* 父分类容器 */
.parent-category-container {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  cursor: pointer;
  position: relative;
  z-index: 1;
}

.parent-category-wrapper {
  position: relative;
  flex: 1;
}

.parent-category-wrapper .sub-category-popup {
  left: calc(100% - 2px);
  top: 0;
  transform: none;
  margin: 0;
  border-left: none;
  border-top-left-radius: 0;
  border-bottom-left-radius: 0;
}

/* 路线图点和线 */
.roadmap-dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background-color: #409EFF;
  flex-shrink: 0;
  margin-top: 6px;
  transition: all 0.3s ease;
}

.parent-category-container.expanded .roadmap-dot {
  background-color: #67c23a;
  box-shadow: 0 0 0 4px rgba(103, 194, 58, 0.2);
}

.roadmap-line {
  position: absolute;
  left: 5px;
  top: 18px;
  bottom: -20px;
  width: 2px;
  background-color: #dcdfe6;
  z-index: 0;
}

/* 父分类标签 */
.parent-category {
  flex: 1;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px 16px;
  border-radius: 8px;
  background-color: white;
  border: 2px solid transparent;
  transition: all 0.3s ease;
  position: relative;
  z-index: 2;
}

.parent-category:hover {
  border-color: #409EFF;
  background-color: #ecf5ff;
  transform: none;
}

.parent-category.active {
  border-color: #409EFF;
  background-color: #ecf5ff;
  color: #409EFF;
}

.expand-icon {
  font-size: 12px;
  transition: transform 0.3s ease;
}

/* 子分类弹出层 */
.sub-category-popup {
  position: absolute;
  left: 100%;
  top: 0;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  padding: 15px;
  min-width: 150px;
  z-index: 100;
  animation: popupFadeIn 0.3s ease;
  margin: 0;
}

.popup-arrow {
  position: absolute;
  right: 100%;
  top: 15px;
  width: 0;
  height: 0;
  border-top: 8px solid transparent;
  border-bottom: 8px solid transparent;
  border-right: 8px solid white;
  transform: translateX(1px);
}

.popup-content {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

/* 子分类标签 */
.sub-category {
  padding: 8px 12px;
  border-radius: 6px;
  font-size: 13px;
  transition: all 0.3s ease;
}

.sub-category:hover {
  background-color: #ecf5ff;
  border-color: #409EFF;
  color: #409EFF;
  transform: none;
}

/* 动画效果 */
@keyframes popupFadeIn {
  from {
    opacity: 0;
    transform: translateX(-10px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

/* 通用分类标签样式 */
.category-tag {
  cursor: pointer;
  transition: all 0.3s ease;
  white-space: nowrap;
}

/* 资料列表区域 */
.material-section {
  flex: 1;
  min-width: 0;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .layout-container {
    flex-direction: column;
  }
  
  .category-section {
    flex: none;
    width: 100%;
    max-height: 400px;
  }
  
  .sub-category-popup {
    position: relative;
    left: 0;
    top: 10px;
    margin-left: 30px;
    margin-bottom: 15px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  }
  
  .popup-arrow {
    display: none;
  }
}

.no-data {
  text-align: center;
  padding: 40px 0;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

/* 资料详情模态框样式 */
.material-detail {
  padding: 20px;
}

.detail-header {
  margin-bottom: 30px;
}

.detail-content {
  margin-bottom: 30px;
}

.detail-content h3 {
  margin-bottom: 10px;
  color: #303133;
}

.detail-content p {
  line-height: 1.8;
  color: #606266;
  white-space: pre-wrap;
}

.detail-tags {
  margin-bottom: 30px;
}

.detail-tags h3 {
  margin-bottom: 10px;
  color: #303133;
}

.detail-tag {
  margin-right: 10px;
  margin-bottom: 10px;
}

.detail-actions {
  margin-top: 40px;
  display: flex;
  justify-content: center;
}

.detail-actions .el-button {
  padding: 12px 40px;
  font-size: 16px;
}
</style>