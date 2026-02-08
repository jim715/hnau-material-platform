// API配置文件
// 统一管理后端接口调用

import axios from 'axios'

// 创建axios实例
const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json;charset=UTF-8'
  }
})

// 请求拦截器
api.interceptors.request.use(
  config => {
    // 从localStorage获取token，先检查localStorage是否存在
    const token = typeof localStorage !== 'undefined' ? localStorage.getItem('token') : ''
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
api.interceptors.response.use(
  response => {
    return response.data
  },
  error => {
    console.error('API请求错误:', error)
    return Promise.reject(error)
  }
)

// 搜索相关API
export const searchAPI = {
  // 获取热搜排行榜
  getHotKeywords: (period) => api.get(`/search/hot?period=${period}`),

  // 获取用户搜索历史
  getSearchHistory: () => api.get('/search/history'),

  // 清空用户搜索历史
  clearSearchHistory: () => api.delete('/search/clear'),

  // 获取关键词联想
  getKeywordSuggestions: (prefix, limit = 10) => api.get(`/search/suggest?prefix=${encodeURIComponent(prefix)}&limit=${limit}`),

  // 记录搜索日志
  recordSearchLog: (keyword, resultCount = 0) => api.post('/search/log', {
    keyword,
    resultCount
  })
}

// 导出api实例
export default api