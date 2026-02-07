<template>
  <div class="upload-container">
    <el-card class="upload-form-card">
      <template #header>
        <div class="card-header">
          <span>上传资料</span>
          <el-tag type="success">上传成功可获得20积分奖励</el-tag>
        </div>
      </template>
      <el-form :model="uploadForm" :rules="rules" ref="uploadFormRef" label-width="100px">
        <el-form-item label="资料名称" prop="name">
          <el-input v-model="uploadForm.name" placeholder="请输入资料名称"></el-input>
        </el-form-item>
        <el-form-item label="资料描述" prop="description">
          <el-input
            v-model="uploadForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入资料描述"
          ></el-input>
        </el-form-item>
        <el-form-item label="资料分类" prop="categoryId">
          <el-select v-model="uploadForm.categoryId" placeholder="请选择资料分类">
            <el-option
              v-for="category in categoryTree"
              :key="category.id"
              :label="category.name"
              :value="category.id"
            >
              <span>{{ category.name }}</span>
              <el-option
                v-for="subCategory in category.children"
                :key="subCategory.id"
                :label="subCategory.name"
                :value="subCategory.id"
              >
                <span style="margin-left: 20px;">{{ subCategory.name }}</span>
              </el-option>
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="标签" prop="tags">
          <el-select
            v-model="uploadForm.tags"
            multiple
            placeholder="请先选择资料分类"
            style="width: 100%"
            :disabled="!uploadForm.categoryId"
          >
            <template v-if="uploadForm.categoryId">
              <!-- 显示当前选择分类的子分类作为标签选项 -->
              <el-option
                v-for="subCategory in getSelectedCategoryChildren()"
                :key="subCategory.id"
                :label="subCategory.name"
                :value="subCategory.name"
              >
                <span>{{ subCategory.name }}</span>
              </el-option>
            </template>
            <template v-else>
              <el-option label="请先选择资料分类" value="" disabled></el-option>
            </template>
          </el-select>
        </el-form-item>
        <el-form-item label="选择文件" prop="file">
          <el-upload
            class="upload-demo"
            ref="upload"
            :auto-upload="false"
            :on-change="handleFileChange"
            :limit="1"
            :file-list="fileList"
            accept=".pdf,.doc,.docx,.txt,.zip,.rar"
            drag
          >
            <el-icon class="el-icon--upload"><upload-filled></upload-filled></el-icon>
            <div class="el-upload__text">
              拖拽文件到此处，或 <em>点击上传</em>
            </div>
            <template #tip>
              <div class="el-upload__tip">
                支持上传 PDF、Word、TXT、ZIP、RAR 格式文件，单个文件大小不超过 100MB
              </div>
            </template>
          </el-upload>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleUpload" :loading="uploading">上传</el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { UploadFilled } from '@element-plus/icons-vue'
import api from '../api/index.js'

const router = useRouter()

const uploadFormRef = ref(null)
const upload = ref(null)
const uploading = ref(false)
const fileList = ref([])
const categoryTree = ref([])
const uploadForm = reactive({
  name: '',
  description: '',
  categoryId: '',
  tags: []
})

const rules = {
  name: [
    { required: true, message: '请输入资料名称', trigger: 'blur' }
  ],
  description: [
    { required: true, message: '请输入资料描述', trigger: 'blur' }
  ],
  categoryId: [
    { required: true, message: '请选择资料分类', trigger: 'blur' }
  ],
  tags: [
    { 
      required: true, 
      message: '请选择至少一个标签', 
      trigger: 'change',
      validator: (rule, value, callback) => {
        if (value.length === 0) {
          callback(new Error('请选择至少一个标签'))
        } else {
          callback()
        }
      }
    }
  ],
  file: [
    {
      validator: (rule, value, callback) => {
        if (fileList.value.length === 0) {
          callback(new Error('请选择文件'))
        } else {
          callback()
        }
      },
      trigger: 'change'
    }
  ]
}

// 获取分类树
const getCategoryTree = async () => {
  try {
    const response = await api.get('/category/tree')
    if (response.code === 200) {
      categoryTree.value = response.data
    } else {
      ElMessage.error('获取分类失败')
    }
  } catch (error) {
    console.error('获取分类失败:', error)
    ElMessage.error('获取分类失败，请检查网络连接')
  }
}



// 处理文件选择
const handleFileChange = (file) => {
  fileList.value = [file]
}

// 上传文件
const handleUpload = async () => {
  console.log('=== 开始上传 ===')
  if (!uploadFormRef.value) {
    console.log('uploadFormRef不存在')
    return
  }
  
  // 获取用户ID
  const userInfo = localStorage.getItem('user')
  if (!userInfo) {
    ElMessage.error('请先登录')
    return
  }
  const user = JSON.parse(userInfo)
  const userId = user.id
  const token = localStorage.getItem('token')
  if (!token) {
    ElMessage.error('请先登录')
    return
  }
  
  console.log('用户信息:', { userId, token })
  console.log('表单数据:', uploadForm)
  console.log('文件列表:', fileList)
  
  if (fileList.value.length === 0) {
    ElMessage.error('请选择文件')
    return
  }
  
  if (!fileList.value[0].raw) {
    ElMessage.error('文件未正确选择')
    return
  }
  
  await uploadFormRef.value.validate(async (valid) => {
    console.log('表单验证结果:', valid)
    if (valid) {
      uploading.value = true
      try {
        const formData = new FormData()
        formData.append('name', uploadForm.name)
        formData.append('description', uploadForm.description)
        formData.append('categoryId', uploadForm.categoryId)
        formData.append('tags', uploadForm.tags.join(','))
        formData.append('userId', userId)
        formData.append('file', fileList.value[0].raw)
        
        console.log('FormData构建完成')
        
        // 直接使用fetch API上传文件，避免axios的Content-Type设置问题
        console.log('开始上传文件...')
        const response = await fetch('/api/material/upload', {
          method: 'POST',
          headers: {
            'Authorization': `Bearer ${token}`
            // 注意：对于FormData，浏览器会自动设置正确的Content-Type
          },
          body: formData
        })
        
        console.log('上传请求完成，状态码:', response.status)
        
        const responseData = await response.json()
        console.log('上传响应:', responseData)
        
        if (responseData.code === 200) {
          ElMessage.success(responseData.message)
          resetForm()
          // 上传成功后，重新获取用户信息以更新积分
          try {
            const userResponse = await api.get('/user/info', {
              headers: {
                'Authorization': `Bearer ${token}`
              }
            })
            if (userResponse.code === 200) {
              // 更新本地存储的用户信息
              localStorage.setItem('user', JSON.stringify(userResponse.data))
              ElMessage.success('积分已更新，请查看积分中心')
            }
          } catch (error) {
            console.error('获取用户信息失败:', error)
          }
          
          // 上传成功后跳转到首页，确保首页显示最新上传的资料
          setTimeout(() => {
            router.push('/')
          }, 1000)
        } else {
          ElMessage.error(responseData.message)
        }
      } catch (error) {
        console.error('上传失败:', error)
        ElMessage.error('上传失败，请检查网络连接')
      } finally {
        uploading.value = false
      }
    }
  })
}

// 重置表单
const resetForm = () => {
  if (uploadFormRef.value) {
    uploadFormRef.value.resetFields()
  }
  fileList.value = []
  uploadForm.tags = []
}

// 获取当前选择分类的子分类
const getSelectedCategoryChildren = () => {
  // 遍历分类树，找到当前选择的分类
  const findCategory = (categories, categoryId) => {
    for (const category of categories) {
      if (category.id === categoryId) {
        return category
      }
      if (category.children && category.children.length > 0) {
        const found = findCategory(category.children, categoryId)
        if (found) {
          return found
        }
      }
    }
    return null
  }
  
  const selectedCategory = findCategory(categoryTree.value, uploadForm.categoryId)
  return selectedCategory ? selectedCategory.children || [] : []
}

// 监听分类变化，清空标签
watch(() => uploadForm.categoryId, (newValue) => {
  if (newValue) {
    // 分类变化时清空标签
    uploadForm.tags = []
  }
})

// 初始化数据
onMounted(() => {
  getCategoryTree()
})
</script>

<style scoped>
.upload-container {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
}

.upload-form-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>