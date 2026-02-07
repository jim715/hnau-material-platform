<template>
  <div class="search-result-page">
    <div class="search-header">
      <div class="search-input-wrapper">
        <SearchInput />
      </div>
      <div class="search-info" v-if="keyword">
        搜索 "{{ keyword }}" 的结果
      </div>
    </div>

    <div class="search-content">
      <!-- 筛选条件 -->
      <div class="filter-section">
        <el-collapse v-model="activeFilters">
          <el-collapse-item title="筛选条件" name="1">
            <div class="filter-form">
              <el-form :inline="true" :model="filterForm" class="filter-form">
                <el-form-item label="上传时间">
                  <el-select v-model="filterForm.uploadTime" placeholder="选择时间范围">
                    <el-option label="今天" value="today" />
                    <el-option label="近7天" value="7days" />
                    <el-option label="近30天" value="30days" />
                    <el-option label="本学期" value="semester" />
                  </el-select>
                </el-form-item>
                
                <el-form-item label="文件类型">
                  <el-select v-model="filterForm.fileType" placeholder="选择文件类型">
                    <el-option label="文档" value="document" />
                    <el-option label="图片" value="image" />
                    <el-option label="视频" value="video" />
                    <el-option label="音频" value="audio" />
                  </el-select>
                </el-form-item>
                
                <el-form-item label="积分范围">
                  <el-select v-model="filterForm.integralRange" placeholder="选择积分范围">
                    <el-option label="免费" value="free" />
                    <el-option label="0-10分" value="0-10" />
                    <el-option label="10-20分" value="10-20" />
                    <el-option label="20分以上" value="20+" />
                  </el-select>
                </el-form-item>
                
                <el-form-item>
                  <el-button type="primary" @click="applyFilters">应用筛选</el-button>
                  <el-button @click="resetFilters">重置</el-button>
                </el-form-item>
              </el-form>
            </div>
          </el-collapse-item>
        </el-collapse>
      </div>

      <!-- 搜索结果 -->
      <div class="result-section">
        <el-empty v-if="loading || results.length === 0" description="暂无搜索结果" />
        <div v-else class="result-list">
          <el-card 
            v-for="item in results" 
            :key="item.id"
            class="result-item"
            @click="goToDetail(item.id)"
          >
            <div class="result-item-header">
              <h4 class="result-title">{{ item.title }}</h4>
              <span class="result-integral">{{ item.integral }}分</span>
            </div>
            <div class="result-item-body">
              <p class="result-description">{{ item.description }}</p>
              <div class="result-meta">
                <span class="result-author">{{ item.author }}</span>
                <span class="result-time">{{ formatTime(item.uploadTime) }}</span>
                <span class="result-type">{{ getFileType(item.fileType) }}</span>
                <span class="result-downloads">{{ item.downloadCount }}次下载</span>
              </div>
            </div>
          </el-card>
        </div>

        <!-- 分页 -->
        <div v-if="total > 0" class="pagination">
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
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import SearchInput from '../components/SearchInput.vue'

const route = useRoute()
const router = useRouter()
const keyword = ref('')
const results = ref([])
const total = ref(0)
const loading = ref(false)
const activeFilters = ref(['1'])

// 筛选条件
const filterForm = ref({
  uploadTime: '',
  fileType: '',
  integralRange: ''
})

// 分页参数
const currentPage = ref(1)
const pageSize = ref(20)

// 监听路由变化，更新搜索关键词
watch(() => route.query.keyword, (newKeyword) => {
  if (newKeyword) {
    keyword.value = newKeyword
    resetFilters()
    getSearchResults()
  }
}, { immediate: true })

// 获取搜索结果
const getSearchResults = async () => {
  if (!keyword.value) {
    return
  }

  loading.value = true
  try {
    // 构建查询参数
    const params = new URLSearchParams()
    params.append('keyword', keyword.value)
    params.append('page', currentPage.value)
    params.append('size', pageSize.value)
    
    // 添加筛选条件
    if (filterForm.value.uploadTime) {
      params.append('uploadTime', filterForm.value.uploadTime)
    }
    if (filterForm.value.fileType) {
      params.append('fileType', filterForm.value.fileType)
    }
    if (filterForm.value.integralRange) {
      params.append('integralRange', filterForm.value.integralRange)
    }

    const response = await fetch(`/api/material/search?${params.toString()}`)
    const data = await response.json()
    if (data.code === 200) {
      results.value = data.data.records
      total.value = data.data.total
    } else {
      ElMessage.error('获取搜索结果失败')
    }
  } catch (error) {
    console.error('获取搜索结果失败:', error)
    ElMessage.error('获取搜索结果失败')
  } finally {
    loading.value = false
  }
}

// 应用筛选
const applyFilters = () => {
  currentPage.value = 1
  getSearchResults()
}

// 重置筛选
const resetFilters = () => {
  filterForm.value = {
    uploadTime: '',
    fileType: '',
    integralRange: ''
  }
  currentPage.value = 1
  getSearchResults()
}

// 分页处理
const handleSizeChange = (size) => {
  pageSize.value = size
  getSearchResults()
}

const handleCurrentChange = (current) => {
  currentPage.value = current
  getSearchResults()
}

// 跳转到详情页
const goToDetail = (id) => {
  router.push(`/material/detail/${id}`)
}

// 格式化时间
const formatTime = (timeStr) => {
  const date = new Date(timeStr)
  return date.toLocaleString()
}

// 获取文件类型
const getFileType = (type) => {
  const typeMap = {
    'document': '文档',
    'image': '图片',
    'video': '视频',
    'audio': '音频'
  }
  return typeMap[type] || '其他'
}

// 组件挂载时获取数据
onMounted(() => {
  keyword.value = route.query.keyword || ''
  if (keyword.value) {
    getSearchResults()
  }
})
</script>

<style scoped>
.search-result-page {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.search-header {
  margin-bottom: 30px;
}

.search-input-wrapper {
  margin-bottom: 16px;
}

.search-info {
  font-size: 16px;
  color: #666;
  margin-top: 8px;
}

.filter-section {
  margin-bottom: 20px;
}

.filter-form {
  padding: 16px;
  background-color: #f9f9f9;
  border-radius: 8px;
}

.result-section {
  min-height: 400px;
}

.result-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(380px, 1fr));
  gap: 20px;
}

.result-item {
  cursor: pointer;
  transition: all 0.3s;
}

.result-item:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  transform: translateY(-2px);
}

.result-item-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.result-title {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #333;
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.result-integral {
  font-size: 14px;
  font-weight: 600;
  color: #f56c6c;
  margin-left: 16px;
  white-space: nowrap;
}

.result-description {
  margin: 0 0 16px 0;
  font-size: 14px;
  color: #666;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.result-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  font-size: 12px;
  color: #999;
}

.pagination {
  margin-top: 30px;
  text-align: center;
}

@media (max-width: 768px) {
  .result-list {
    grid-template-columns: 1fr;
  }
}
</style>