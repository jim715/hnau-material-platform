<template>
  <div class="search-history">
    <div class="search-history-header">
      <h3>搜索历史</h3>
      <el-button 
        type="text" 
        size="small" 
        @click="clearHistory"
        :disabled="searchHistory.length === 0"
      >
        清空
      </el-button>
    </div>
    <div class="search-history-content">
      <el-empty v-if="loading || searchHistory.length === 0" description="暂无搜索历史" />
      <ul v-else class="history-items">
        <li 
          v-for="(item, index) in searchHistory" 
          :key="item.id || index"
          class="history-item"
        >
          <span class="history-keyword" @click="goToSearch(item.keyword)">{{ item.keyword }}</span>
          <span class="history-time">{{ formatTime(item.searchTime) }}</span>
          <el-button 
            type="text" 
            size="small" 
            class="delete-btn"
            @click.stop="deleteHistoryItem(item.id)"
          >
            删除
          </el-button>
        </li>
      </ul>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { searchAPI } from '../api'

const router = useRouter()
const searchHistory = ref([])
const loading = ref(false)

// 获取搜索历史
const getSearchHistory = async () => {
  loading.value = true
  try {
    const data = await searchAPI.getSearchHistory()
    if (data.code === 200) {
      searchHistory.value = data.data
    } else if (data.code === 401) {
      // 用户未登录，不显示搜索历史
      searchHistory.value = []
    } else {
      ElMessage.error('获取搜索历史失败')
    }
  } catch (error) {
    console.error('获取搜索历史失败:', error)
    ElMessage.error('获取搜索历史失败')
  } finally {
    loading.value = false
  }
}

// 清空搜索历史
const clearHistory = async () => {
  try {
    const data = await searchAPI.clearSearchHistory()
    if (data.code === 200) {
      searchHistory.value = []
      ElMessage.success('搜索历史已清空')
    } else {
      ElMessage.error('清空搜索历史失败')
    }
  } catch (error) {
    console.error('清空搜索历史失败:', error)
    ElMessage.error('清空搜索历史失败')
  }
}

// 删除单个搜索历史
const deleteHistoryItem = (id) => {
  // 这里简化实现，实际可以调用后端接口删除单个记录
  searchHistory.value = searchHistory.value.filter(item => item.id !== id)
  ElMessage.success('删除成功')
}

// 跳转到搜索结果页
const goToSearch = (keyword) => {
  router.push({
    path: '/search',
    query: { keyword }
  })
}

// 格式化时间
const formatTime = (timeStr) => {
  const date = new Date(timeStr)
  const now = new Date()
  const diff = now - date
  
  const minutes = Math.floor(diff / (1000 * 60))
  const hours = Math.floor(diff / (1000 * 60 * 60))
  const days = Math.floor(diff / (1000 * 60 * 60 * 24))
  
  if (minutes < 1) {
    return '刚刚'
  } else if (minutes < 60) {
    return `${minutes}分钟前`
  } else if (hours < 24) {
    return `${hours}小时前`
  } else if (days < 7) {
    return `${days}天前`
  } else {
    return date.toLocaleDateString()
  }
}

// 组件挂载时获取数据
onMounted(() => {
  getSearchHistory()
})
</script>

<style scoped>
.search-history {
  background: #fff;
  border-radius: 8px;
  padding: 16px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
  margin-top: 16px;
}

.search-history-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.search-history-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.search-history-content {
  min-height: 150px;
}

.history-items {
  list-style: none;
  padding: 0;
  margin: 0;
}

.history-item {
  display: flex;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
}

.history-item:last-child {
  border-bottom: none;
}

.history-keyword {
  flex: 1;
  font-size: 14px;
  color: #333;
  cursor: pointer;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.history-keyword:hover {
  color: #409eff;
}

.history-time {
  font-size: 12px;
  color: #999;
  margin: 0 12px;
  white-space: nowrap;
}

.delete-btn {
  font-size: 12px;
  color: #999;
  padding: 0;
}

.delete-btn:hover {
  color: #f56c6c;
}
</style>