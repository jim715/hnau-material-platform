<template>
  <div class="question-detail">
    <div class="post-container">
      <!-- 顶部标题栏 -->
      <div class="post-header">
        <div style="display: flex; align-items: center; gap: 15px;">
          <div class="back-btn" @click="goBack">
            <el-icon><ArrowLeft /></el-icon>
            <span>返回</span>
          </div>
          <div class="logo-section">
            <div class="logo">
              <img src="https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=school%20logo%20with%20book%20and%20mountain%20symbol&image_size=square" alt="Logo" />
            </div>
            <div class="title">学习资料平台</div>
          </div>
        </div>
        <div class="more-options" @click="showMoreOptions">
          <el-icon><More /></el-icon>
          <div v-if="showOptions" class="options-menu">
            <div class="option-item" @click.stop="deleteQuestion">删除问题</div>
          </div>
        </div>
      </div>
      
      <!-- 问题内容区域 -->
      <div class="post-content">
        <h1 class="post-title">{{ questionData.title }}</h1>
        <div class="post-meta">
          <span class="author">{{ questionData.author }}</span>
          <span class="time">{{ questionData.createTime }}</span>
        </div>
        <div class="post-tags">
          <el-tag v-for="tag in questionData.tags" :key="tag" size="small">{{ tag }}</el-tag>
        </div>
        <div class="post-body">{{ questionData.content }}</div>
      </div>
      
      <!-- 互动按钮区域 -->
      <div class="interaction-section">
        <div class="interaction-stats">
          <span class="view-count">
            <el-icon><View /></el-icon> {{ questionData.viewCount }} 浏览
          </span>
          <span class="like-count">
            <el-icon><Star /></el-icon> {{ Math.max(0, questionData.likeCount || 0) }} 点赞
          </span>
          <span class="collect-count">
            <el-icon><Collection /></el-icon> {{ Math.max(0, questionData.collectCount || 0) }} 收藏
          </span>
        </div>
        <div class="interaction-buttons">
          <div 
            class="interaction-btn like-btn"
            :class="{ 'liked': isLiked }"
            @click="likeQuestion"
          >
            <template v-if="isLiked">
              <el-icon class="icon-active"><StarFilled /></el-icon>
              <span class="btn-text">已赞</span>
            </template>
            <template v-else>
              <el-icon><Star /></el-icon>
              <span class="btn-text">点赞</span>
            </template>
          </div>
          <div 
            class="interaction-btn collect-btn"
            :class="{ 'collected': isCollected }"
            @click="collectQuestion"
          >
            <template v-if="isCollected">
              <el-icon class="icon-active"><Collection /></el-icon>
              <span class="btn-text">已收藏</span>
            </template>
            <template v-else>
              <el-icon><Collection /></el-icon>
              <span class="btn-text">收藏</span>
            </template>
          </div>
          <div class="interaction-btn comment-btn">
            <el-icon><ChatDotRound /></el-icon>
            <span class="btn-text">{{ comments.length }} 评论</span>
          </div>

        </div>
        
        <!-- 点赞用户列表 -->
        <div class="likes-list" v-if="Math.max(0, questionData.likeCount || 0) > 0">
          <el-icon><StarFilled /></el-icon>
          <span class="likes-text">
            已有 {{ Math.max(0, questionData.likeCount || 0) }} 人点赞
          </span>
        </div>
      </div>
      
      <!-- 评论区域 -->
      <div class="comment-section">
        <!-- 评论输入框 -->
        <div class="comment-input-section">
          <div class="user-avatar">
            <img src="https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=user%20avatar%20portrait%20simple%20style&image_size=square" alt="Avatar" />
          </div>
          <div class="comment-input-container">
            <el-input
              v-model="commentForm.content"
              type="textarea"
              :rows="2"
              placeholder="说点什么吧..."
              maxlength="500"
              show-word-limit
            />
            <div class="comment-submit">
              <el-button 
                type="primary" 
                @click="submitComment" 
                :loading="commentLoading"
                :disabled="!commentForm.content.trim()"
              >
                发布
              </el-button>
            </div>
          </div>
        </div>
        
        <!-- 评论列表 -->
        <div class="comment-list" v-if="comments.length > 0">
          <el-divider content-position="left">全部评论</el-divider>
          <div 
            v-for="comment in comments" 
            :key="comment.id"
            class="comment-item"
          >
            <div class="comment-avatar">
              <img src="https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=user%20avatar%20portrait%20simple%20style&image_size=square" alt="Avatar" />
            </div>
            <div class="comment-body">
              <div class="comment-header">
                <span class="comment-author">{{ comment.author }}</span>
                <span class="comment-time">{{ comment.createTime }}</span>
              </div>
              <div class="comment-content">{{ comment.content }}</div>
              <div class="comment-footer">
                <div 
                  class="comment-like"
                  :class="{ 'liked': comment.isLiked }"
                  @click="likeComment(comment.id)"
                >
                  <template v-if="comment.isLiked">
                    <el-icon class="icon-active"><StarFilled /></el-icon>
                    <span class="like-text">已赞</span>
                  </template>
                  <template v-else>
                    <el-icon><Star /></el-icon>
                    <span class="like-text">点赞</span>
                  </template>
                  <span class="like-count">{{ Math.max(0, comment.likeCount || 0) }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
        <div class="no-comments" v-else>
          <el-empty description="暂无评论，快来发表第一条评论吧" />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { View, Star, StarFilled, Collection, More, ChatDotRound, ArrowLeft } from '@element-plus/icons-vue'
import api from '../api/index.js'

// 路由
const route = useRoute()
const router = useRouter()
const questionId = ref(route.params.id)

// 返回问题列表页
const goBack = () => {
  router.push('/question/list')
}

// 问题数据
const questionData = ref({
  id: '',
  title: '',
  content: '',
  author: '',
  createTime: '',
  viewCount: 0,
  likeCount: 0,
  collectCount: 0,
  tags: []
})

// 点赞和收藏状态
const isLiked = ref(false)
const isCollected = ref(false)

// 更多选项菜单
const showOptions = ref(false)

// 评论数据
const comments = ref([])

// 评论表单
const commentForm = ref({ content: '' })
const commentRules = {
  content: [
    { required: true, message: '请输入评论内容', trigger: 'blur' },
    { min: 5, max: 500, message: '评论内容长度应在5-500个字符之间', trigger: 'blur' }
  ]
}
const commentFormRef = ref(null)
const commentLoading = ref(false)

// 提交评论
const submitComment = async () => {
  // 直接验证内容，不使用validate方法
  if (!commentForm.value.content.trim()) {
    ElMessage.error('请输入评论内容')
    return
  }
  
  commentLoading.value = true
  
  try {
    // 获取用户信息
    const userInfo = localStorage.getItem('user')
    if (!userInfo) {
      ElMessage.error('请先登录')
      commentLoading.value = false
      return
    }
    const user = JSON.parse(userInfo)
    
    // 提交评论到后端
    const response = await api.post('/comment/add', {
      userId: user.id,
      relType: 1, // 1表示问题评论
      relId: questionId.value,
      content: commentForm.value.content
    })
    
    if (response.code === 200) {
      // 评论成功，重新加载评论列表
      await loadComments()
      commentForm.value.content = ''
      ElMessage.success('评论成功，获得3积分')
    } else {
      ElMessage.error('评论失败')
    }
  } catch (error) {
    console.error('评论失败:', error)
    ElMessage.error('评论失败，请检查网络连接')
  } finally {
    commentLoading.value = false
  }
}

// 点赞评论
const likeComment = async (commentId) => {
  try {
    // 获取用户信息
    const userInfo = localStorage.getItem('user')
    if (!userInfo) {
      ElMessage.error('请先登录')
      return
    }
    const user = JSON.parse(userInfo)
    
    // 调用后端API进行点赞操作
    const response = await api.post('/comment/like', null, {
      params: {
        userId: user.id,
        commentId: commentId
      }
    })
    
    if (response.code === 200) {
      // 更新本地存储中的点赞状态
      const likedKey = `comment_liked_${user.id}_${commentId}`
      if (response.message === '点赞成功') {
        localStorage.setItem(likedKey, 'true')
      } else if (response.message === '取消点赞成功') {
        localStorage.setItem(likedKey, 'false')
      }
      
      // 重新加载评论列表，确保计数与后端同步
      await loadComments()
      ElMessage.success(response.message + '，获得1积分')
    } else {
      ElMessage.error(response.message)
    }
  } catch (error) {
    console.error('点赞失败:', error)
    ElMessage.error('点赞失败，请检查网络连接')
  }
}

// 加载问题详情
const loadQuestionDetail = async () => {
  try {
    const response = await api.get('/question/detail', {
      params: {
        id: questionId.value
      }
    })
    
    if (response.code === 200) {
      const question = response.data
      // 处理tags字段，将逗号分隔的字符串转换为数组
      const tags = question.tags ? question.tags.split(',').map(tag => tag.trim()) : []
      questionData.value = {
        ...question,
        tags: tags,
        // 格式化时间
        createTime: new Date(question.createTime).toLocaleString()
      }
      
      // 加载评论列表
      await loadComments()
      
      // 初始化用户的点赞和收藏状态
      await initUserBehaviorStatus()
    } else {
      ElMessage.error('获取问题详情失败')
    }
  } catch (error) {
    console.error('获取问题详情失败:', error)
    ElMessage.error('获取问题详情失败')
  }
}

// 初始化用户的点赞和收藏状态
const initUserBehaviorStatus = async () => {
  // 检查用户是否登录
  const userInfo = localStorage.getItem('user')
  if (!userInfo) {
    return
  }
  
  const user = JSON.parse(userInfo)
  
  try {
    // 确保使用字符串类型的问题ID
    const questionIdStr = String(questionId.value)
    // 检查点赞状态
    const likedKey = `question_liked_${user.id}_${questionIdStr}`
    // 检查收藏状态
    const collectedKey = `question_collected_${user.id}_${questionIdStr}`
    
    // 从本地存储获取状态
    const likedStatus = localStorage.getItem(likedKey) === 'true'
    const collectedStatus = localStorage.getItem(collectedKey) === 'true'
    
    // 更新本地状态
    isLiked.value = likedStatus
    isCollected.value = collectedStatus
    console.log('初始化问题状态:', questionIdStr, 'isLiked:', likedStatus, 'isCollected:', collectedStatus)
  } catch (error) {
    console.error('初始化行为状态失败:', error)
  }
}

// 加载评论列表
const loadComments = async () => {
  try {
    const response = await api.get('/comment/list', {
      params: {
        relType: 1, // 1表示问题评论
        relId: questionId.value
      }
    })
    
    if (response.code === 200) {
      const commentList = response.data
      // 确保commentList是数组
      if (Array.isArray(commentList)) {
      // 检查用户是否登录
      const userInfo = localStorage.getItem('user')
      const user = userInfo ? JSON.parse(userInfo) : null
      
      // 格式化评论数据
      comments.value = commentList.map(comment => {
        // 从本地存储获取点赞状态
        let isLiked = false
        if (user) {
          const likedKey = `comment_liked_${user.id}_${comment.id}`
          isLiked = localStorage.getItem(likedKey) === 'true'
        }
        
        return {
          ...comment,
          // 确保author字段存在
          author: comment.author || comment.name || '未知用户',
          // 格式化时间
          createTime: comment.createTime ? new Date(comment.createTime).toLocaleString() : new Date().toLocaleString(),
          // 初始化点赞状态
          isLiked: isLiked
        }
      })
      } else {
        comments.value = []
      }
    } else {
      ElMessage.error('获取评论列表失败')
      comments.value = []
    }
  } catch (error) {
    console.error('获取评论列表失败:', error)
    // 后端API错误时，显示空评论列表，不影响页面正常显示
    comments.value = []
  }
}

// 点赞问题
const likeQuestion = async () => {
  try {
    // 获取用户信息
    const userInfo = localStorage.getItem('user')
    if (!userInfo) {
      ElMessage.error('请先登录')
      return
    }
    const user = JSON.parse(userInfo)
    
    // 调用后端API进行点赞操作
    const response = await api.post('/question/like', null, {
      params: {
        userId: user.id,
        questionId: questionId.value
      }
    })
    
    if (response.code === 200) {
      // 更新本地存储
      const questionIdStr = String(questionId.value)
      const likedKey = `question_liked_${user.id}_${questionIdStr}`
      
      // 根据后端返回的消息更新本地存储
      if (response.message === '取消点赞成功') {
        localStorage.setItem(likedKey, 'false')
        ElMessage.success('取消点赞成功，扣除1积分')
      } else if (response.message === '点赞成功') {
        localStorage.setItem(likedKey, 'true')
        ElMessage.success('点赞成功，获得1积分')
      }
      
      // 重新加载问题详情，确保计数与后端同步
      await loadQuestionDetail()
    } else {
      ElMessage.error(response.message)
    }
  } catch (error) {
    console.error('点赞失败:', error)
    ElMessage.error('点赞失败，请检查网络连接')
  }
}

// 收藏问题
const collectQuestion = async () => {
  try {
    // 获取用户信息
    const userInfo = localStorage.getItem('user')
    if (!userInfo) {
      ElMessage.error('请先登录')
      return
    }
    const user = JSON.parse(userInfo)
    
    // 调用后端API进行收藏操作
    const response = await api.post('/question/collect', null, {
      params: {
        userId: user.id,
        questionId: questionId.value
      }
    })
    
    if (response.code === 200) {
      // 更新本地存储
      const questionIdStr = String(questionId.value)
      const collectedKey = `question_collected_${user.id}_${questionIdStr}`
      
      // 根据后端返回的消息更新本地存储
      if (response.message === '取消收藏成功') {
        localStorage.setItem(collectedKey, 'false')
        ElMessage.success('取消收藏成功，扣除2积分')
      } else if (response.message === '收藏成功') {
        localStorage.setItem(collectedKey, 'true')
        ElMessage.success('收藏成功，获得2积分')
      }
      
      // 重新加载问题详情，确保计数与后端同步
      await loadQuestionDetail()
    } else {
      ElMessage.error(response.message)
    }
  } catch (error) {
    console.error('收藏失败:', error)
    ElMessage.error('收藏失败，请检查网络连接')
  }
}

// 显示更多选项菜单
const showMoreOptions = () => {
  showOptions.value = !showOptions.value
}

// 删除问题
const deleteQuestion = async () => {
  try {
    console.log('=== 删除问题开始 ===')
    console.log('问题ID:', questionId.value)
    
    // 获取用户信息
    const userInfo = localStorage.getItem('user')
    console.log('用户信息:', userInfo)
    if (!userInfo) {
      ElMessage.error('请先登录')
      return
    }
    const user = JSON.parse(userInfo)
    console.log('解析后的用户信息:', user)
    console.log('用户ID:', user.id)
    
    // 确认删除
    if (confirm('确定要删除这个问题吗？')) {
      // 调用后端API删除问题
      console.log('开始调用删除API')
      const response = await api.delete('/question/delete', {
        params: {
          questionId: questionId.value,
          userId: user.id
        }
      })
      console.log('删除API调用结果:', response)
      
      if (response.code === 200) {
        ElMessage.success('删除成功')
        // 跳转到问题列表页
        setTimeout(() => {
          window.location.href = '/question/list'
        }, 1000)
      } else {
        ElMessage.error(response.message || '删除失败')
      }
    }
  } catch (error) {
    console.error('删除问题失败:', error)
    ElMessage.error('删除失败，请检查网络连接')
  } finally {
    showOptions.value = false
    console.log('=== 删除问题结束 ===')
  }
}

onMounted(() => {
  console.log('加载问题ID:', questionId.value)
  // 确保questionId有值
  if (questionId.value) {
    loadQuestionDetail()
  } else {
    console.error('问题ID为空')
    ElMessage.error('问题ID不存在')
  }
})
</script>

<style scoped>
.question-detail {
  background-color: #f5f5f5;
  min-height: 100vh;
  padding: 20px 0;
}

.post-container {
  max-width: 800px;
  margin: 0 auto;
  background-color: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

/* 顶部标题栏 */
.post-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid #f0f0f0;
  background-color: #fafafa;
}

.back-btn {
  display: flex;
  align-items: center;
  gap: 5px;
  padding: 8px 12px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 14px;
  color: #606266;
}

.back-btn:hover {
  background-color: #f0f0f0;
  color: #409EFF;
}

.logo-section {
  display: flex;
  align-items: center;
  gap: 10px;
}

.logo img {
  width: 32px;
  height: 32px;
  border-radius: 50%;
}

.title {
  font-size: 18px;
  font-weight: bold;
  color: #303133;
}

.more-options {
  cursor: pointer;
  color: #909399;
  font-size: 18px;
  position: relative;
}

.options-menu {
  position: absolute;
  top: 100%;
  right: 0;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.15);
  padding: 8px 0;
  margin-top: 8px;
  min-width: 120px;
  z-index: 1000;
}

.option-item {
  padding: 8px 16px;
  cursor: pointer;
  font-size: 14px;
  color: #606266;
  transition: all 0.3s ease;
}

.option-item:hover {
  background-color: #f5f7fa;
  color: #409EFF;
}

.option-item:first-child {
  border-top-left-radius: 8px;
  border-top-right-radius: 8px;
}

.option-item:last-child {
  border-bottom-left-radius: 8px;
  border-bottom-right-radius: 8px;
}

/* 问题内容区域 */
.post-content {
  padding: 20px;
}

.post-title {
  font-size: 24px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 16px;
  line-height: 1.3;
}

.post-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 14px;
  color: #909399;
  margin-bottom: 16px;
}

.post-tags {
  display: flex;
  gap: 8px;
  margin-bottom: 20px;
}

.post-body {
  font-size: 16px;
  line-height: 1.6;
  color: #303133;
  white-space: pre-wrap;
  margin-bottom: 20px;
}

/* 互动按钮区域 */
.interaction-section {
  padding: 0 20px 20px;
  border-top: 1px solid #f0f0f0;
  border-bottom: 1px solid #f0f0f0;
}

.interaction-stats {
  display: flex;
  gap: 16px;
  margin-bottom: 16px;
  font-size: 14px;
  color: #909399;
}

.view-count,
.like-count,
.collect-count {
  display: flex;
  align-items: center;
  gap: 4px;
}

.interaction-buttons {
  display: flex;
  gap: 20px;
  margin-bottom: 16px;
}

.interaction-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 12px;
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 14px;
  color: #606266;
}

.interaction-btn:hover {
  background-color: #f0f0f0;
}

.like-btn.liked {
  color: #f56c6c;
  text-shadow: 0 0 5px rgba(245, 108, 108, 0.5);
  transition: all 0.3s ease;
}

.like-btn.liked .icon-active {
  color: #f56c6c;
  text-shadow: 0 0 5px rgba(245, 108, 108, 0.5);
  transition: all 0.3s ease;
}

.collect-btn.collected {
  color: #67c23a;
  text-shadow: 0 0 5px rgba(103, 194, 58, 0.5);
  transition: all 0.3s ease;
}

.collect-btn.collected .icon-active {
  color: #67c23a;
  text-shadow: 0 0 5px rgba(103, 194, 58, 0.5);
  transition: all 0.3s ease;
}

.btn-text {
  font-size: 14px;
}

.likes-list {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #909399;
}

.likes-list .el-icon {
  color: #f56c6c;
}

/* 评论区域 */
.comment-section {
  padding: 20px;
}

.comment-input-section {
  display: flex;
  gap: 12px;
  margin-bottom: 24px;
}

.user-avatar img {
  width: 40px;
  height: 40px;
  border-radius: 50%;
}

.comment-input-container {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.comment-input-container .el-input {
  border-radius: 20px;
  margin-bottom: 8px;
}

.comment-submit {
  align-self: flex-end;
}

.comment-list {
  margin-top: 20px;
}

.comment-item {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
  padding-bottom: 20px;
  border-bottom: 1px solid #f0f0f0;
}

.comment-item:last-child {
  border-bottom: none;
}

.comment-avatar img {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  flex-shrink: 0;
}

.comment-body {
  flex: 1;
}

.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.comment-author {
  font-size: 14px;
  font-weight: bold;
  color: #303133;
}

.comment-time {
  font-size: 12px;
  color: #909399;
}

.comment-content {
  font-size: 14px;
  line-height: 1.5;
  color: #606266;
  margin-bottom: 10px;
  white-space: pre-wrap;
}

.comment-footer {
  display: flex;
  align-items: center;
}

.comment-like {
  display: flex;
  align-items: center;
  gap: 6px;
  cursor: pointer;
  font-size: 12px;
  color: #909399;
  padding: 4px 12px;
  border-radius: 16px;
  transition: all 0.3s ease;
  background-color: #f5f5f5;
}

.comment-like:hover {
  background-color: #ebebeb;
}

.comment-like.liked {
  color: #f56c6c;
  background-color: #fef0f0;
}

.comment-like.liked .icon-active {
  color: #f56c6c;
}

.like-text {
  font-size: 12px;
}

.like-count {
  font-size: 12px;
  font-weight: 500;
  margin-left: 4px;
}

.no-comments {
  margin-top: 40px;
  text-align: center;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .post-container {
    margin: 0 10px;
  }
  
  .post-title {
    font-size: 20px;
  }
  
  .interaction-buttons {
    gap: 12px;
  }
  
  .interaction-btn {
    padding: 6px 10px;
    font-size: 13px;
  }
}
</style>