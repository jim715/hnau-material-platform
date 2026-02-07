<template>
  <div class="material-detail-container">
    <el-card class="detail-card" v-loading="loading">
      <template #header>
        <div class="card-header">
          <span>资料详情</span>
        </div>
      </template>
      
      <div class="detail-content">
        <h2 class="material-title">{{ material.name }}</h2>
        
        <div class="material-info">
          <el-tag>{{ material.categoryName }}</el-tag>
          <span class="info-item">上传时间：{{ formatDate(material.uploadTime) }}</span>
          <span class="info-item">浏览次数：{{ material.viewCount }}</span>
          <span class="info-item">下载次数：{{ material.downloadCount }}</span>
        </div>
        
        <div class="material-description">
          <h3>资料描述</h3>
          <p>{{ material.description }}</p>
        </div>
        
        <div class="material-tags">
          <h3>标签</h3>
          <el-tag
            v-for="tag in material.tags"
            :key="tag.id"
            effect="plain"
          >
            {{ tag.name }}
          </el-tag>
        </div>
        
        <div class="material-actions">
          <el-button type="primary" size="large" @click="downloadMaterial" :disabled="downloading">
            <el-icon v-if="!downloading"><download></download></el-icon>
            <el-icon v-else><loading></loading></el-icon>
            {{ downloading ? '下载中...' : '下载资料' }}
          </el-button>
          <el-tag type="warning">下载需要消耗5积分</el-tag>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Download, Loading } from '@element-plus/icons-vue'
import axios from 'axios'

const route = useRoute()
const router = useRouter()
const material = ref({})
const loading = ref(true)
const downloading = ref(false)

// 格式化日期
const formatDate = (date) => {
  if (!date) return ''
  const d = new Date(date)
  return d.toLocaleString()
}

// 获取资料详情
const getMaterialDetail = async () => {
  loading.value = true
  try {
    const materialId = route.params.id
    const response = await axios.get(`/api/material/${materialId}`)
    
    if (response.data.code === 200) {
      material.value = response.data.data
    } else {
      ElMessage.error(response.data.message)
      router.push('/home')
    }
  } catch (error) {
    console.error('获取资料详情失败:', error)
    ElMessage.error('获取资料详情失败，请检查网络连接')
    router.push('/home')
  } finally {
    loading.value = false
  }
}

// 下载资料
const downloadMaterial = async () => {
  // 检查用户是否登录
  const userInfo = localStorage.getItem('user')
  if (!userInfo) {
    ElMessage.error('请先登录')
    router.push('/login')
    return
  }
  
  downloading.value = true
  try {
    const materialId = route.params.id
    const user = JSON.parse(userInfo)
    
    const response = await axios.get(`/api/material/download/${materialId}`, {
      params: {
        userId: user.id
      },
      responseType: 'blob'
    })
    
    // 处理文件下载
    const url = window.URL.createObjectURL(new Blob([response.data]))
    const link = document.createElement('a')
    link.href = url
    link.setAttribute('download', material.value.name)
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    
    ElMessage.success('下载成功')
    
    // 更新下载次数
    material.value.downloadCount++
  } catch (error) {
    console.error('下载资料失败:', error)
    if (error.response) {
      // 尝试解析错误响应
      try {
        const errorData = JSON.parse(error.response.data)
        ElMessage.error(errorData.message)
      } catch {
        ElMessage.error('下载失败，请检查网络连接')
      }
    } else {
      ElMessage.error('下载失败，请检查网络连接')
    }
  } finally {
    downloading.value = false
  }
}

// 初始化数据
onMounted(() => {
  getMaterialDetail()
})
</script>

<style scoped>
.material-detail-container {
  padding: 20px;
  max-width: 1000px;
  margin: 0 auto;
}

.detail-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.detail-content {
  padding: 20px 0;
}

.material-title {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 20px;
  color: #333;
}

.material-info {
  margin-bottom: 30px;
  padding: 15px;
  background-color: #f5f7fa;
  border-radius: 8px;
}

.info-item {
  margin-left: 20px;
  color: #666;
}

.material-description {
  margin-bottom: 30px;
}

.material-description h3,
.material-tags h3 {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 10px;
  color: #333;
}

.material-description p {
  line-height: 1.6;
  color: #666;
  white-space: pre-wrap;
}

.material-tags {
  margin-bottom: 30px;
}

.material-actions {
  margin-top: 40px;
  display: flex;
  align-items: center;
  gap: 20px;
}
</style>