<template>
  <div class="search-input-container">
    <el-autocomplete
      v-model="searchKeyword"
      :fetch-suggestions="fetchSuggestions"
      :trigger-on-focus="false"
      :debounce="300"
      placeholder="输入关键词搜索"
      class="search-input"
      @select="handleSelect"
      @keyup.enter="handleSearch"
    >
      <template #prefix>
        <el-icon class="el-input__icon"><Search /></el-icon>
      </template>
      <template #suffix>
        <el-icon 
          class="el-input__icon clear-icon"
          v-if="searchKeyword"
          @click="clearInput"
        >
          <Close />
        </el-icon>
        <el-button 
          type="primary" 
          size="small" 
          class="search-btn"
          @click="handleSearch"
        >
          搜索
        </el-button>
      </template>
    </el-autocomplete>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { Search, Close } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { searchAPI } from '../api'

const router = useRouter()
const searchKeyword = ref('')

// 获取关键词联想
const fetchSuggestions = async (queryString, callback) => {
  if (!queryString) {
    callback([])
    return
  }

  try {
    const data = await searchAPI.getKeywordSuggestions(queryString)
    if (data.code === 200) {
      const suggestions = data.data.map(keyword => ({
        value: keyword
      }))
      callback(suggestions)
    } else {
      callback([])
    }
  } catch (error) {
    console.error('获取关键词联想失败:', error)
    callback([])
  }
}

// 选择联想词
const handleSelect = (value) => {
  searchKeyword.value = value
  handleSearch()
}

// 处理搜索
const handleSearch = async () => {
  if (!searchKeyword.value.trim()) {
    ElMessage.warning('请输入搜索关键词')
    return
  }

  try {
    // 记录搜索日志
    await searchAPI.recordSearchLog(searchKeyword.value)

    // 跳转到搜索结果页
    router.push({
      path: '/search',
      query: { keyword: searchKeyword.value }
    })
  } catch (error) {
    console.error('搜索失败:', error)
    // 即使记录日志失败，也要跳转到搜索结果页
    router.push({
      path: '/search',
      query: { keyword: searchKeyword.value }
    })
  }
}

// 清空输入
const clearInput = () => {
  searchKeyword.value = ''
}
</script>

<style scoped>
.search-input-container {
  width: 100%;
  max-width: 600px;
}

.search-input {
  width: 100%;
}

.search-btn {
  margin-left: 8px;
}

.clear-icon {
  cursor: pointer;
  color: #999;
}

.clear-icon:hover {
  color: #666;
}
</style>