<template>
  <div class="layout-container">
    <!-- 左侧菜单 -->
    <div class="sidebar">
      <div class="logo">
        <h1>资料平台</h1>
      </div>
      <el-menu
        :default-active="activeMenu"
        class="el-menu-vertical-demo"
        @select="handleMenuSelect"
        background-color="#304156"
        text-color="#fff"
        active-text-color="#ffd04b"
      >
        <el-menu-item index="/home">
          <el-icon><House /></el-icon>
          <span>首页</span>
        </el-menu-item>
        <el-menu-item index="/upload">
          <el-icon><Upload /></el-icon>
          <span>上传资料</span>
        </el-menu-item>
        <el-menu-item index="/user">
          <el-icon><User /></el-icon>
          <span>个人中心</span>
        </el-menu-item>
        <el-sub-menu index="integral">
          <template #title>
            <el-icon><Coin /></el-icon>
            <span>积分管理</span>
          </template>
          <el-menu-item index="/integral/rules">积分规则</el-menu-item>
          <el-menu-item index="/integral/center">积分中心</el-menu-item>
          <el-menu-item index="/integral/rank">积分排行</el-menu-item>
        </el-sub-menu>
        <el-sub-menu index="question">
          <template #title>
            <el-icon><ChatDotRound /></el-icon>
            <span>互动交流</span>
          </template>
          <el-menu-item index="/question/add">发布问题</el-menu-item>
          <el-menu-item index="/question/list">问题列表</el-menu-item>
        </el-sub-menu>
        <el-menu-item v-if="userRole === 1 || (user && user.studentId === 'admin')" index="/admin">
          <el-icon><Setting /></el-icon>
          <span>管理员</span>
        </el-menu-item>
      </el-menu>
    </div>
    <!-- 右侧内容区域 -->
    <div class="main-content">
      <!-- 顶部导航栏 -->
      <div class="top-nav">
        <div class="nav-left">
          <span class="welcome">欢迎，{{ userName }}</span>
        </div>
        <div class="nav-right">
          <el-button type="text" @click="handleLogout">退出登录</el-button>
        </div>
      </div>
      <!-- 页面内容 -->
      <div class="page-content">
        <router-view />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { House, Upload, User, Coin, ChatDotRound, Setting } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import api from '../api/index.js'

const router = useRouter()
const route = useRoute()

// 计算当前活跃的菜单
const activeMenu = computed(() => {
  const path = route.path
  // 处理子路由，确保菜单高亮正确
  if (path.startsWith('/integral/')) {
    return '/integral/rules'
  }
  if (path.startsWith('/question/')) {
    return '/question/list'
  }
  return path
})

// 获取用户信息
const userName = ref('用户')
const userRole = ref(0)
const user = ref(null)

const getUserInfo = () => {
  const userInfo = localStorage.getItem('user')
  console.log('LocalStorage user info:', userInfo)
  if (userInfo) {
    try {
      const parsedUser = JSON.parse(userInfo)
      console.log('Parsed user:', parsedUser)
      console.log('User role:', parsedUser.role)
      console.log('User studentId:', parsedUser.studentId)
      user.value = parsedUser
      userName.value = parsedUser.name || parsedUser.username || '用户'
      userRole.value = parsedUser.role || 0
      console.log('Set userRole to:', userRole.value)
    } catch (error) {
      console.error('解析用户信息失败:', error)
    }
  }
  console.log('Final userRole:', userRole.value)
  console.log('Final user:', user.value)
}

// 处理菜单选择
const handleMenuSelect = (key) => {
  router.push(key)
}

// 处理退出登录
const handleLogout = async () => {
  try {
    console.log('开始退出登录...')
    console.log('localStorage中的token:', localStorage.getItem('token'))
    console.log('localStorage中的user:', localStorage.getItem('user'))
    
    // 先调用后端退出接口，确保退出时间被记录
    const response = await api.post('/user/logout')
    console.log('退出登录响应:', response)
    
    // 检测当前路由，如果在Admin页面，调用Admin页面的退出登录处理函数
    console.log('当前路由:', route.path)
    if (route.path.startsWith('/admin')) {
      try {
        console.log('当前在Admin页面，尝试调用Admin页面的退出登录处理函数...')
        // 检查是否存在全局的Admin退出登录处理函数
        if (window.handleAdminLogout) {
          console.log('找到全局的handleAdminLogout函数，调用它...')
          // 调用Admin页面的退出登录处理函数，它会处理刷新和跳转
          await window.handleAdminLogout()
          console.log('Admin页面的退出登录处理函数调用完成')
          return // 退出函数，因为Admin页面的处理函数已经完成了所有工作
        } else {
          console.log('未找到全局的handleAdminLogout函数，执行默认退出流程...')
          // 如果没有找到全局函数，执行默认的退出流程
          // 强制刷新登录记录
          const loginRecordsResponse = await api.get('/admin/login/records', {
            params: { page: 1, pageSize: 10 }
          })
          console.log('强制刷新登录记录成功:', loginRecordsResponse)
          
          // 等待更长时间，确保Admin页面有足够的时间更新显示
          console.log('等待2.5秒，确保Admin页面更新...')
          await new Promise(resolve => setTimeout(resolve, 2500))
          console.log('等待完成，准备清除token并跳转...')
        }
      } catch (error) {
        console.error('Admin页面退出登录处理失败:', error)
        // 即使失败，也要执行默认的退出流程
      }
    } else {
      // 如果不在Admin页面，等待一小段时间
      await new Promise(resolve => setTimeout(resolve, 500))
    }
    
    // 清除本地存储的用户信息和token
    localStorage.removeItem('user')
    localStorage.removeItem('token')
    
    ElMessage.success('退出登录成功')
    // 跳转到登录页面
    router.push('/login')
  } catch (error) {
    console.error('退出登录失败:', error)
    console.error('错误详情:', error.response)
    console.error('错误消息:', error.message)
    
    // 即使后端调用失败，也要清除本地存储并跳转
    localStorage.removeItem('user')
    localStorage.removeItem('token')
    ElMessage.success('退出登录成功')
    router.push('/login')
  }
}

// 初始化
onMounted(() => {
  getUserInfo()
})
</script>

<style scoped>
.layout-container {
  display: flex;
  height: 100vh;
  overflow: hidden;
}

.sidebar {
  width: 200px;
  background-color: #304156;
  color: #fff;
  height: 100%;
  overflow-y: auto;
}

.logo {
  padding: 20px;
  text-align: center;
  border-bottom: 1px solid #405467;
}

.logo h1 {
  font-size: 18px;
  margin: 0;
  color: #ffd04b;
}

.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  height: 100%;
  overflow: hidden;
}

.top-nav {
  height: 60px;
  background-color: #fff;
  border-bottom: 1px solid #e6e6e6;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.nav-left {
  display: flex;
  align-items: center;
}

.welcome {
  font-size: 14px;
  color: #606266;
}

.page-content {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  background-color: #f5f7fa;
}

/* 菜单样式调整 */
:deep(.el-menu-vertical-demo) {
  border-right: none;
}

:deep(.el-menu-item) {
  height: 50px;
  line-height: 50px;
}

:deep(.el-sub-menu__title) {
  height: 50px;
  line-height: 50px;
}
</style>