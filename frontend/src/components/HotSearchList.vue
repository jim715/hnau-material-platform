<template>
  <div class="hot-search-list">
    <div class="hot-search-header">
      <h3>热搜排行榜</h3>
      <div class="period-selector">
        <el-radio-group v-model="period" @change="getHotKeywords">
          <el-radio label="24h">24小时</el-radio>
          <el-radio label="7d">7天</el-radio>
        </el-radio-group>
      </div>
    </div>
    <div class="hot-search-content">
      <el-empty v-if="loading || hotKeywords.length === 0" description="暂无热搜数据" />
      <ul v-else class="hot-search-items">
        <li 
          v-for="(item, index) in hotKeywords" 
          :key="index"
          class="hot-search-item"
          :class="{ 'top-three': index < 3 }"
          @click="goToSearch(item.keyword)"
        >
          <span class="hot-rank">{{ index + 1 }}</span>
          <span class="hot-keyword">{{ item.keyword }}</span>
          <span class="hot-count">{{ item.count }}次</span>
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
const period = ref('24h')
const hotKeywords = ref([])
const loading = ref(false)

// 获取热搜关键词
const getHotKeywords = async () => {
  loading.value = true
  try {
    const data = await searchAPI.getHotKeywords(period.value)
    if (data.code === 200) {
      hotKeywords.value = data.data
    } else {
      ElMessage.error('获取热搜数据失败')
    }
  } catch (error) {
    console.error('获取热搜数据失败:', error)
    ElMessage.error('获取热搜数据失败')
  } finally {
    loading.value = false
  }
}

// 跳转到搜索结果页
const goToSearch = (keyword) => {
  router.push({
    path: '/search',
    query: { keyword }
  })
}

// 组件挂载时获取数据
onMounted(() => {
  getHotKeywords()
})
</script>

<style scoped>
.hot-search-list {
  background: #fff;
  border-radius: 8px;
  padding: 16px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}

.hot-search-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.hot-search-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.period-selector {
  font-size: 14px;
}

.hot-search-content {
  min-height: 200px;
}

.hot-search-items {
  list-style: none;
  padding: 0;
  margin: 0;
}

.hot-search-item {
  display: flex;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
  transition: all 0.3s;
}

.hot-search-item:hover {
  background-color: #f5f7fa;
}

.hot-search-item:last-child {
  border-bottom: none;
}

.hot-rank {
  width: 24px;
  height: 24px;
  line-height: 24px;
  text-align: center;
  font-size: 14px;
  font-weight: 600;
  margin-right: 12px;
  border-radius: 4px;
}

.top-three .hot-rank {
  color: #fff;
}

.top-three:nth-child(1) .hot-rank {
  background-color: #f56c6c;
}

.top-three:nth-child(2) .hot-rank {
  background-color: #e6a23c;
}

.top-three:nth-child(3) .hot-rank {
  background-color: #409eff;
}

.hot-keyword {
  flex: 1;
  font-size: 14px;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.hot-count {
  font-size: 12px;
  color: #999;
  margin-left: 12px;
}

.hot-search-item:hover .hot-keyword {
  color: #409eff;
}
</style>