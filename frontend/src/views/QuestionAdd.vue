<template>
  <div class="question-add">
    <el-card class="add-card">
      <template #header>
        <div class="card-header">
          <span>发布问题</span>
        </div>
      </template>
      
      <div class="add-content">
        <el-form :model="questionForm" :rules="rules" ref="questionFormRef" label-width="80px">
          <el-form-item label="问题标题" prop="title">
            <el-input v-model="questionForm.title" placeholder="请输入问题标题" maxlength="100" show-word-limit />
          </el-form-item>
          
          <el-form-item label="问题内容" prop="content">
            <el-input
              v-model="questionForm.content"
              type="textarea"
              :rows="6"
              placeholder="请详细描述您的问题"
              maxlength="1000"
              show-word-limit
            />
          </el-form-item>
          
          <el-form-item label="标签" prop="tags">
         <el-select
           v-model="questionForm.tags"
           multiple
           placeholder="请选择标签"
           style="width: 100%"
         >
           <el-option label="课程学习" value="课程学习" />
           <el-option label="考试复习" value="考试复习" />
           <el-option label="编程开发" value="编程开发" />
           <el-option label="算法数据结构" value="算法数据结构" />
           <el-option label="前端技术" value="前端技术" />
           <el-option label="后端技术" value="后端技术" />
           <el-option label="数据库" value="数据库" />
           <el-option label="计算机网络" value="计算机网络" />
           <el-option label="操作系统" value="操作系统" />
           <el-option label="软件工程" value="软件工程" />
         </el-select>
       </el-form-item>
          
          <el-form-item>
            <el-button type="primary" @click="submitForm" :loading="loading">发布问题</el-button>
            <el-button @click="resetForm">重置</el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import api from '../api/index.js'

// 表单数据
const questionForm = reactive({
  title: '',
  content: '',
  tags: []
})

// 表单验证规则
const rules = {
  title: [
    { required: true, message: '请输入问题标题', trigger: 'blur' },
    { min: 5, max: 100, message: '标题长度应在5-100个字符之间', trigger: 'blur' }
  ],
  content: [
    { required: true, message: '请输入问题内容', trigger: 'blur' },
    { min: 10, max: 1000, message: '内容长度应在10-1000个字符之间', trigger: 'blur' }
  ],
  tags: [
    { required: true, message: '请至少选择一个标签', trigger: 'blur' }
  ]
}

// 表单引用
const questionFormRef = ref(null)

// 加载状态
const loading = ref(false)

// 提交表单
const submitForm = async () => {
  await questionFormRef.value.validate(async (valid, fields) => {
    if (valid) {
      loading.value = true
      
      try {
        // 获取用户信息和token
        const userInfo = localStorage.getItem('user')
        if (!userInfo) {
          ElMessage.error('请先登录')
          loading.value = false
          return
        }
        const user = JSON.parse(userInfo)
        const userId = user.id
        const token = localStorage.getItem('token')
        if (!token) {
          ElMessage.error('请先登录')
          loading.value = false
          return
        }
        
        // 构建请求数据
        const requestData = {
          title: questionForm.title,
          content: questionForm.content,
          tags: questionForm.tags.join(','), // 将tags数组转换为逗号分隔的字符串
          userId: userId
        }
        
        // 调用后端API
        const response = await api.post('/question/add', requestData, {
          headers: {
            'Authorization': `Bearer ${token}`
          }
        })
        
        if (response.code === 200) {
          ElMessage.success('问题发布成功，获得5积分')
          questionFormRef.value.resetFields()
        } else {
          ElMessage.error(response.message || '问题发布失败')
        }
      } catch (error) {
        console.error('问题发布失败:', error)
        ElMessage.error('问题发布失败，请检查网络连接')
      } finally {
        loading.value = false
      }
    } else {
      console.log('验证失败:', fields)
    }
  })
}

// 重置表单
const resetForm = () => {
  questionFormRef.value.resetFields()
}
</script>

<style scoped>
.question-add {
  max-width: 800px;
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

.add-content {
  padding: 20px 0;
}

.el-form {
  margin-top: 20px;
}

.el-form-item {
  margin-bottom: 20px;
}

.el-button {
  margin-right: 10px;
}
</style>