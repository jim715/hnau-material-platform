<template>
  <div class="question-list">
    <el-card class="list-card">
      <template #header>
        <div class="card-header">
          <span>问题列表</span>
        </div>
      </template>
      
      <div class="list-content">
        <!-- 搜索和排序 -->
        <div class="search-sort-container">
          <div class="search-box">
            <el-input
              v-model="searchKeyword"
              placeholder="请输入关键词搜索问题"
              style="width: 300px"
              clearable
              @keyup.enter="loadQuestionList"
            >
              <template #append>
                <el-button @click="loadQuestionList"><el-icon><Search /></el-icon></el-button>
              </template>
            </el-input>
          </div>
          
          <div class="sort-box">
            <el-radio-group v-model="sortType" @change="handleSortChange">
              <el-radio-button label="latest">最新</el-radio-button>
              <el-radio-button label="hottest">最热</el-radio-button>
              <el-radio-button label="featured">精选</el-radio-button>
              <el-radio-button label="collected">收藏</el-radio-button>
            </el-radio-group>
          </div>
          
          <div class="filter-box">
            <el-checkbox v-model="isFinalExamZone" @change="sortType === 'collected' ? loadCollectedQuestions() : loadQuestionList()">期末专区</el-checkbox>
          </div>
        </div>
        
        <!-- 问题列表 -->
        <div class="question-items">
          <el-empty v-if="questionData.length === 0" description="暂无问题" />
          <el-card
            v-for="item in questionData"
            :key="item.id"
            class="question-item"
            @click="goToDetail(item.id)"
          >
            <div class="question-header">
              <h3 class="question-title">{{ item.title }}</h3>
              <div class="question-meta">
                <span class="author">{{ item.author }}</span>
                <span class="time">{{ item.createTime }}</span>
              </div>
            </div>
            <div class="question-content">{{ item.content }}</div>
            <div class="question-footer">
             <div class="question-tags">
               <el-tag v-for="tag in item.tags" :key="tag" size="small">{{ tag }}</el-tag>
             </div>
             <div class="question-stats">
               <span class="stat-item">
                 <el-icon><View /></el-icon> {{ item.viewCount }}
               </span>
               <span class="stat-item">
                 <el-icon><ChatLineRound /></el-icon> {{ item.commentCount }}
               </span>
               <span class="stat-item like-btn" @click.stop="likeQuestion(item)"
                     :class="{ 'liked': likedQuestions.includes(String(item.id)) }">
                 <el-icon v-if="likedQuestions.includes(String(item.id))"><StarFilled /></el-icon>
                 <el-icon v-else><Star /></el-icon> {{ Math.max(0, item.likeCount || 0) }}
               </span>
               <span class="stat-item collect-btn" @click.stop="collectQuestion(item)"
                     :class="{ 'collected': collectedQuestions.includes(String(item.id)) }">
                 <el-icon v-if="collectedQuestions.includes(String(item.id))"><Collection /></el-icon>
                 <el-icon v-else><Collection /></el-icon>
                 <span v-if="sortType === 'collected' && collectedQuestions.includes(String(item.id))">已收藏</span>
                 <span v-else-if="sortType === 'collected' && !collectedQuestions.includes(String(item.id))">未收藏</span>
                 <span v-else>{{ item.collectCount || 0 }}</span>
               </span>
             </div>
            </div>
          </el-card>
        </div>
        
        <!-- 分页 -->
        <div class="pagination">
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
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Search, View, ChatLineRound, Star, StarFilled, Collection } from '@element-plus/icons-vue'
import api from '../api/index.js'
import { ElMessage } from 'element-plus'

// 路由
const router = useRouter()

// 搜索和排序
const searchKeyword = ref('')
const sortType = ref('latest')
const isFinalExamZone = ref(false)

// 分页
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

// 问题数据
const questionData = ref([])
// 已点赞的问题ID列表
const likedQuestions = ref([])
// 已收藏的问题ID列表
const collectedQuestions = ref([])

// 加载问题列表
const loadQuestionList = async () => {
  try {
    const response = await api.get('/question/list', {
      params: {
        page: currentPage.value,
        size: pageSize.value,
        orderBy: 'latest', // 始终使用最新排序获取数据
        keyword: searchKeyword.value,
        isAgri: isFinalExamZone.value ? 1 : 0
      }
    })
    
    if (response.code === 200) {
      // 处理后端返回的数据
      let questions = response.data.list.map(item => {
        // 处理tags字段，将逗号分隔的字符串转换为数组
        const tags = item.tags ? item.tags.split(',').map(tag => tag.trim()) : []
        return {
          ...item,
          tags: tags,
          // 格式化时间
          createTime: new Date(item.createTime).toLocaleString(),
          // 初始化评论数
          commentCount: 0,
          // 转换时间为可排序的时间戳
          createTimeStamp: new Date(item.createTime).getTime()
        }
      })
      
      // 为每个问题加载评论数量
      for (const question of questions) {
        try {
          const commentResponse = await api.get('/comment/list', {
            params: {
              relType: 1, // 1表示问题评论
              relId: question.id
            }
          })
          
          if (commentResponse.code === 200 && Array.isArray(commentResponse.data)) {
            question.commentCount = commentResponse.data.length
          }
        } catch (error) {
          console.error(`获取问题 ${question.id} 的评论数量失败:`, error)
        }
      }
      
      // 在前端进行排序
      questions = sortQuestions(questions, sortType.value)
      
      // 在收藏模式下，只显示已点赞的问题
      if (sortType.value === 'collected') {
        questions = questions.filter(question => likedQuestions.value.includes(question.id))
        total.value = questions.length
      } else {
        total.value = response.data.total
      }
      
      questionData.value = questions
      
      // 初始化用户的点赞和收藏状态
      await initUserBehaviorStatus()
    } else {
      ElMessage.error('获取问题列表失败')
    }
  } catch (error) {
    console.error('获取问题列表失败:', error)
    ElMessage.error('获取问题列表失败')
  }
}

// 排序问题列表
const sortQuestions = (questions, sortType) => {
  const sorted = [...questions]
  
  switch (sortType) {
    case 'latest':
      // 按创建时间降序
      return sorted.sort((a, b) => b.createTimeStamp - a.createTimeStamp)
    case 'hottest':
      // 按浏览量降序
      return sorted.sort((a, b) => (b.viewCount || 0) - (a.viewCount || 0))
    case 'featured':
      // 按点赞量降序，然后按创建时间降序
      return sorted.sort((a, b) => {
        const likeDiff = (b.likeCount || 0) - (a.likeCount || 0)
        if (likeDiff !== 0) {
          return likeDiff
        }
        return b.createTimeStamp - a.createTimeStamp
      })
    default:
      return sorted
  }
}

// 初始化用户的点赞和收藏状态
const initUserBehaviorStatus = async () => {
  // 清空现有状态
  likedQuestions.value = []
  collectedQuestions.value = []
  
  // 检查用户是否登录
  const userInfo = localStorage.getItem('user')
  if (!userInfo) {
    return
  }
  
  const user = JSON.parse(userInfo)
  
  // 为每个问题检查点赞和收藏状态
  for (const question of questionData.value) {
    try {
      // 确保使用字符串类型的问题ID
      const questionId = String(question.id)
      // 检查点赞状态
      const likedKey = `question_liked_${user.id}_${questionId}`
      // 检查收藏状态
      const collectedKey = `question_collected_${user.id}_${questionId}`
      
      // 从本地存储获取状态
      const isLiked = localStorage.getItem(likedKey) === 'true'
      const isCollected = localStorage.getItem(collectedKey) === 'true'
      
      // 更新本地状态
      if (isLiked) {
        likedQuestions.value.push(questionId)
      }
      
      if (isCollected) {
        collectedQuestions.value.push(questionId)
      }
      
      console.log('初始化问题状态:', questionId, 'isLiked:', isLiked, 'isCollected:', isCollected)
    } catch (error) {
      console.error('初始化行为状态失败:', error)
    }
  }
}

// 排序类型变化处理
const handleSortChange = (value) => {
  if (value === 'collected') {
    loadCollectedQuestions()
  } else {
    loadQuestionList()
  }
}

// 分页处理
const handleSizeChange = (size) => {
  pageSize.value = size
  if (sortType.value === 'collected') {
    loadCollectedQuestions()
  } else {
    loadQuestionList()
  }
}

const handleCurrentChange = (current) => {
  currentPage.value = current
  if (sortType.value === 'collected') {
    loadCollectedQuestions()
  } else {
    loadQuestionList()
  }
}

// 加载用户收藏的问题列表
const loadCollectedQuestions = async () => {
  try {
    console.log('=== 开始加载收藏问题 ===')
    // 检查用户是否登录
    const userInfo = localStorage.getItem('user')
    if (!userInfo) {
      ElMessage.error('请先登录')
      sortType.value = 'latest'
      return
    }
    const user = JSON.parse(userInfo)
    console.log('用户信息:', user)
    
    console.log('调用API获取收藏问题:', {
      userId: user.id,
      page: currentPage.value,
      size: pageSize.value
    })
    
    const response = await api.get('/question/collected', {
      params: {
        userId: user.id,
        page: currentPage.value,
        size: pageSize.value
      }
    })
    
    console.log('API返回结果:', response)
    
    if (response.code === 200) {
      console.log('收藏问题列表:', response.data.list)
      console.log('收藏问题总数:', response.data.total)
      // 处理后端返回的数据
      let questions = response.data.list.map(item => {
        // 处理tags字段，将逗号分隔的字符串转换为数组
        const tags = item.tags ? item.tags.split(',').map(tag => tag.trim()) : []
        return {
          ...item,
          tags: tags,
          // 格式化时间
          createTime: new Date(item.createTime).toLocaleString(),
          // 初始化评论数
          commentCount: 0,
          // 转换时间为可排序的时间戳
          createTimeStamp: new Date(item.createTime).getTime()
        }
      })
      
      console.log('处理后的问题列表:', questions)
      
      // 为每个问题加载评论数量
      for (const question of questions) {
        try {
          const commentResponse = await api.get('/comment/list', {
            params: {
              relType: 1, // 1表示问题评论
              relId: question.id
            }
          })
          
          if (commentResponse.code === 200 && Array.isArray(commentResponse.data)) {
            question.commentCount = commentResponse.data.length
          }
        } catch (error) {
          console.error(`获取问题 ${question.id} 的评论数量失败:`, error)
        }
      }
      
      // 排序后的问题列表:
      questions = questions.sort((a, b) => {
        return (b.collectCount || 0) - (a.collectCount || 0)
      })
      
      console.log('排序后的问题列表:', questions)
      
      // 更新问题数据
      questionData.value = questions
      total.value = response.data.total
      
      console.log('更新后questionData:', questionData.value)
      console.log('更新后total:', total.value)
      
      // 在收藏模式下，后端只返回用户收藏的问题，所以所有显示的问题都应该被标记为已收藏
      const user = JSON.parse(localStorage.getItem('user'))
      if (user) {
        // 清空现有收藏状态
        collectedQuestions.value = []
        // 为每个问题添加到收藏状态
        for (const question of questions) {
          const questionId = String(question.id)
          collectedQuestions.value.push(questionId)
          // 更新本地存储
          const collectedKey = `question_collected_${user.id}_${questionId}`
          localStorage.setItem(collectedKey, 'true')
        }
        console.log('收藏模式下的收藏状态:', collectedQuestions.value)
      }
      
      // 初始化用户的点赞状态
      likedQuestions.value = []
      if (user) {
        for (const question of questions) {
          const questionId = String(question.id)
          const likedKey = `question_liked_${user.id}_${questionId}`
          const isLiked = localStorage.getItem(likedKey) === 'true'
          if (isLiked) {
            likedQuestions.value.push(questionId)
          }
        }
      }
      console.log('=== 加载收藏问题结束 ===')
    } else {
      console.error('获取收藏问题失败，响应码:', response.code)
      ElMessage.error('获取收藏问题失败')
      sortType.value = 'latest'
    }
  } catch (error) {
    console.error('获取收藏问题失败:', error)
    ElMessage.error('获取收藏问题失败')
    sortType.value = 'latest'
  }
}

// 跳转到问题详情页
const goToDetail = (id) => {
  router.push(`/question/${id}`)
}

// 点赞问题
const likeQuestion = async (question) => {
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
        questionId: question.id
      }
    })
    
    if (response.code === 200) {
      // 点赞成功，更新本地状态
      const questionId = String(question.id) // 确保使用字符串类型
      const likedKey = `question_liked_${user.id}_${questionId}`
      
      // 根据后端返回的消息更新本地状态
      if (response.message === '取消点赞成功') {
        // 取消点赞成功，从数组中移除问题ID
        const index = likedQuestions.value.indexOf(questionId)
        if (index > -1) {
          likedQuestions.value.splice(index, 1)
        }
        localStorage.setItem(likedKey, 'false')
        console.log('取消点赞，从数组中移除问题ID:', questionId)
        console.log('更新后likedQuestions数组:', likedQuestions.value)
        ElMessage.success('取消点赞成功，扣除1积分')
      } else if (response.message === '点赞成功') {
        // 点赞成功，向数组中添加问题ID
        if (!likedQuestions.value.includes(questionId)) {
          likedQuestions.value.push(questionId)
        }
        localStorage.setItem(likedKey, 'true')
        console.log('点赞，向数组中添加问题ID:', questionId)
        console.log('更新后likedQuestions数组:', likedQuestions.value)
        ElMessage.success('点赞成功，获得1积分')
      }
      
      // 重新加载问题列表，确保计数与后端同步
      await loadQuestionList()
    } else {
      ElMessage.error(response.message)
    }
  } catch (error) {
    console.error('点赞失败:', error)
    ElMessage.error('点赞失败，请检查网络连接')
  }
}

// 收藏问题
const collectQuestion = async (question) => {
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
        questionId: question.id
      }
    })
    
    if (response.code === 200) {
      // 收藏成功，更新本地状态
      const questionId = String(question.id) // 确保使用字符串类型
      const collectedKey = `question_collected_${user.id}_${questionId}`
      
      // 根据后端返回的消息更新本地状态
      if (response.message === '取消收藏成功') {
        // 取消收藏成功，从数组中移除问题ID
        const index = collectedQuestions.value.indexOf(questionId)
        if (index > -1) {
          collectedQuestions.value.splice(index, 1)
        }
        localStorage.setItem(collectedKey, 'false')
        console.log('取消收藏，从数组中移除问题ID:', questionId)
        console.log('更新后collectedQuestions数组:', collectedQuestions.value)
        ElMessage.success('取消收藏成功，扣除2积分')
      } else if (response.message === '收藏成功') {
        // 收藏成功，向数组中添加问题ID
        if (!collectedQuestions.value.includes(questionId)) {
          collectedQuestions.value.push(questionId)
        }
        localStorage.setItem(collectedKey, 'true')
        console.log('收藏，向数组中添加问题ID:', questionId)
        console.log('更新后collectedQuestions数组:', collectedQuestions.value)
        ElMessage.success('收藏成功，获得2积分')
      }
      
      // 重新加载问题列表，确保计数与后端同步
      if (sortType.value === 'collected') {
        await loadCollectedQuestions()
      } else {
        await loadQuestionList()
      }
    } else {
      ElMessage.error(response.message)
    }
  } catch (error) {
    console.error('收藏失败:', error)
    ElMessage.error('收藏失败，请检查网络连接')
  }
}

onMounted(() => {
  loadQuestionList()
})
</script>

<style scoped>
.question-list {
  max-width: 1000px;
  margin: 20px auto;
  padding: 0 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 18px;
  font-weight: bold;
}

.list-content {
  padding: 20px 0;
}

.search-sort-container {
  display: flex;
  align-items: center;
  margin-bottom: 30px;
  gap: 20px;
}

.search-box {
  flex: 1;
}

.sort-box {
  margin-left: 20px;
}

.filter-box {
  margin-left: 20px;
}

.question-items {
  margin-top: 20px;
}

.question-item {
  margin-bottom: 20px;
  cursor: pointer;
  transition: all 0.3s;
}

.question-item:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  transform: translateY(-2px);
}

.question-header {
  margin-bottom: 10px;
}

.question-title {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 5px;
  color: #303133;
}

.question-meta {
  font-size: 12px;
  color: #909399;
}

.question-content {
  font-size: 14px;
  color: #606266;
  margin-bottom: 15px;
  line-height: 1.5;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.question-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.question-tags {
  display: flex;
  gap: 5px;
}

.question-stats {
  display: flex;
  gap: 15px;
  font-size: 12px;
  color: #909399;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 2px;
}

.like-btn:hover,
.collect-btn:hover {
  color: #409EFF;
  cursor: pointer;
  transition: all 0.3s ease;
}

.like-btn.liked {
  color: #f56c6c;
  text-shadow: 0 0 5px rgba(245, 108, 108, 0.5);
  transition: all 0.3s ease;
}

.collect-btn.collected {
  color: #67c23a;
  text-shadow: 0 0 5px rgba(103, 194, 58, 0.5);
  transition: all 0.3s ease;
}

.pagination {
  margin-top: 30px;
  display: flex;
  justify-content: flex-end;
}
</style>