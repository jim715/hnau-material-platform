<template>
  <div class="user-container">
    <el-card class="user-info-card">
      <template #header>
        <div class="card-header">
          <span>用户信息</span>
          <el-button type="primary" @click="openPasswordDialog">修改密码</el-button>
        </div>
      </template>
      <div class="user-info">
        <div class="avatar-section">
          <div style="display: inline-block; padding: 10px;">
            <el-avatar :size="120" :src="defaultAvatar">
                <img :src="defaultAvatar" alt="头像">
            </el-avatar>
            <div class="avatar-text">
              {{ getUserInfoText }}
            </div>
            <el-button type="default" @click="handleLogout" style="margin-top: 10px;">
              账户切换
            </el-button>
          </div>
        </div>
        <div class="info-content">
          <div class="info-item">
            <span class="label">学号：</span>
            <span>{{ userInfo.studentId }}</span>
          </div>
          <div class="info-item">
            <span class="label">姓名：</span>
            <span>{{ userInfo.name }}</span>
          </div>
          <div class="info-item">
            <span class="label">学院：</span>
            <span>{{ userInfo.college || '未设置' }}</span>
          </div>
          <div class="info-item">
            <span class="label">专业：</span>
            <span>{{ userInfo.major || '未设置' }}</span>
          </div>
          <div class="info-item">
            <span class="label">年级：</span>
            <span>{{ userInfo.grade || '未设置' }}</span>
          </div>
          <div class="info-item">
            <span class="label">班级：</span>
            <span>{{ userInfo.className || '未设置' }}</span>
          </div>
          <div class="info-item">
            <span class="label">积分：</span>
            <span class="points">{{ userInfo.points }}</span>
          </div>
          <div class="info-item">
            <span class="label">上传资料数：</span>
            <span>{{ userInfo.uploadCount }}</span>
          </div>
          <div class="info-item">
            <span class="label">下载资料数：</span>
            <span>{{ userInfo.downloadCount }}</span>
          </div>
        </div>
      </div>
    </el-card>
    
    <el-card class="upload-history-card">
      <template #header>
        <div class="card-header">
          <span>上传历史</span>
        </div>
      </template>
      <el-table :data="uploadHistory" style="width: 100%">
        <el-table-column prop="name" label="资料名称" min-width="200"></el-table-column>
        <el-table-column prop="categoryName" label="分类" width="100"></el-table-column>
        <el-table-column prop="uploadTime" label="上传时间" width="180">
          <template #default="scope">
            {{ formatDate(scope.row.uploadTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="downloadCount" label="下载次数" width="100"></el-table-column>
        <el-table-column prop="points" label="获得积分" width="100"></el-table-column>
      </el-table>
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="uploadCurrentPage"
          v-model:page-size="pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="uploadTotal"
          @size-change="handleUploadSizeChange"
          @current-change="handleUploadCurrentChange"
        />
      </div>
    </el-card>
    
    <el-card class="download-history-card">
      <template #header>
        <div class="card-header">
          <span>下载历史</span>
        </div>
      </template>
      <el-table :data="downloadHistory" style="width: 100%">
        <el-table-column prop="name" label="资料名称" min-width="200"></el-table-column>
        <el-table-column prop="categoryName" label="分类" width="100"></el-table-column>
        <el-table-column prop="downloadTime" label="下载时间" width="180">
          <template #default="scope">
            {{ formatDate(scope.row.downloadTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="points" label="消耗积分" width="100"></el-table-column>
      </el-table>
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="downloadCurrentPage"
          v-model:page-size="pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="downloadTotal"
          @size-change="handleDownloadSizeChange"
          @current-change="handleDownloadCurrentChange"
        />
      </div>
    </el-card>



    <!-- 密码修改对话框 -->
    <el-dialog v-model="passwordDialogVisible" title="修改密码" width="400px">
      <el-form :model="passwordForm" :rules="passwordRules" ref="passwordFormRef">
        <el-form-item label="原密码" prop="oldPassword">
          <el-input v-model="passwordForm.oldPassword" type="password" placeholder="请输入原密码"></el-input>
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="passwordForm.newPassword" type="password" placeholder="请输入新密码"></el-input>
        </el-form-item>
        <el-form-item label="确认新密码" prop="confirmPassword">
          <el-input v-model="passwordForm.confirmPassword" type="password" placeholder="请确认新密码"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="passwordDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handlePasswordSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 密码修改记录 -->
    <el-card class="password-history-card">
      <template #header>
        <div class="card-header">
          <span>最近修改密码记录</span>
        </div>
      </template>
      <el-table :data="passwordHistory" style="width: 100%" v-if="passwordHistory.length > 0">
        <el-table-column prop="modifyTime" label="修改时间" width="200">
          <template #default="scope">
            {{ formatDate(scope.row.modifyTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="ipAddress" label="修改IP" min-width="150"></el-table-column>
        <el-table-column prop="device" label="设备信息" min-width="300"></el-table-column>
      </el-table>
      <div v-else class="no-data">
        <el-empty description="暂无密码修改记录"></el-empty>
      </div>
    </el-card>

    <!-- 学院修改对话框 -->
    <el-dialog v-model="collegeDialogVisible" title="修改学院" width="400px">
      <el-form :model="collegeForm" :rules="collegeRules" ref="collegeFormRef">
        <el-form-item label="学院" prop="college">
          <el-input v-model="collegeForm.college" placeholder="请输入学院名称"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="collegeDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleCollegeSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 专业和年级修改对话框 -->
    <el-dialog v-model="majorGradeDialogVisible" title="修改专业和年级" width="400px">
      <el-form :model="majorGradeForm" :rules="majorGradeRules" ref="majorGradeFormRef">
        <el-form-item label="专业" prop="major">
          <el-input v-model="majorGradeForm.major" placeholder="请输入专业名称"></el-input>
        </el-form-item>
        <el-form-item label="年级" prop="grade">
          <el-input v-model="majorGradeForm.grade" placeholder="请输入年级，例如：2025"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="majorGradeDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleMajorGradeSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, onActivated, computed } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import api from '../api/index.js'

const router = useRouter()
const route = useRoute()
const userInfo = ref({
  studentId: '',
  name: '',
  college: '',
  major: '',
  grade: '',
  className: '',
  points: 0,
  uploadCount: 0,
  downloadCount: 0,
  avatar: ''
})

const defaultAvatar = 'https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=default%20user%20avatar%20simple%20gray%20portrait&image_size=square'

// 计算用户信息文本
const getUserInfoText = computed(() => {
  const major = userInfo.value.major || '未设置'
  const grade = userInfo.value.grade || ''
  const gradeText = grade ? `${grade.slice(-2)}级` : '未设置'
  const className = userInfo.value.className || '未设置'
  const classNameText = className ? `${className}班` : '未设置'
  const name = userInfo.value.name || '未设置'
  return `${major}${gradeText}${classNameText}${name}`
})
const passwordDialogVisible = ref(false)
const passwordForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})
const passwordFormRef = ref(null)
const passwordRules = ref({
  oldPassword: [
    { required: true, message: '请输入原密码', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, message: '新密码长度至少为6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    {
      validator: (rule, value, callback) => {
        if (value !== passwordForm.value.newPassword) {
          callback(new Error('两次输入的密码不一致'))
        } else {
          callback()
        }
      },
      trigger: 'blur'
    }
  ]
})

// 学院编辑相关
const collegeDialogVisible = ref(false)
const collegeForm = ref({
  college: ''
})
const collegeFormRef = ref(null)
const collegeRules = ref({
  college: [
    {
      required: true,
      message: '请输入学院名称',
      trigger: 'blur'
    }
  ]
})

// 专业和年级修改表单
const majorGradeDialogVisible = ref(false)
const majorGradeForm = ref({
  major: '',
  grade: ''
})
const majorGradeFormRef = ref(null)
const majorGradeRules = ref({
  major: [
    {
      required: true,
      message: '请输入专业名称',
      trigger: 'blur'
    }
  ],
  grade: [
    {
      required: true,
      message: '请输入年级',
      trigger: 'blur'
    }
  ]
})

const uploadHistory = ref([])
const downloadHistory = ref([])
const passwordHistory = ref([])
const uploadCurrentPage = ref(1)
const downloadCurrentPage = ref(1)
const pageSize = ref(10)
const uploadTotal = ref(0)
const downloadTotal = ref(0)

// 格式化日期
const formatDate = (date) => {
  if (!date) return ''
  const d = new Date(date)
  return d.toLocaleString()
}

// 获取用户信息
const getUserInfo = async () => {
  try {
    const token = typeof localStorage !== 'undefined' ? localStorage.getItem('token') : ''
    if (!token) {
      router.push('/login')
      return
    }
    
    // 先设置默认值，避免使用旧的缓存数据
    userInfo.value = {
      studentId: '',
      name: '',
      major: '',
      grade: '',
      college: '',
      className: '',
      points: 0,
      uploadCount: 0,
      downloadCount: 0,
      avatar: ''
    }
    
    // 然后调用API获取最新的用户信息
    console.log('开始获取用户信息，token:', token)
    try {
      // 确保API调用时携带token
      const config = {
        headers: {
          'Authorization': `Bearer ${token}`
        }
      }
      const response = await api.get('/user/info', config)
      console.log('获取用户信息成功，响应:', response)
      // 确保response.data存在
      if (response.data) {
        console.log('用户信息:', response.data)
        console.log('用户信息中是否包含className字段:', 'className' in response.data)
        console.log('className字段的值:', response.data.className)
        console.log('className字段的类型:', typeof response.data.className)
        // 直接使用API返回的用户信息
        userInfo.value = response.data
        console.log('使用API返回的用户信息:', userInfo.value)
        console.log('赋值后userInfo.className的值:', userInfo.value.className)
        
        // 确保下载次数从API返回的用户信息中获取
        if (response.data.downloadCount !== undefined) {
          userInfo.value.downloadCount = response.data.downloadCount
        }
          
          // 调用积分API获取最新的积分数据，更新积分和上传次数
          try {
            const integralResponse = await api.get('/integral/user', config)
            console.log('获取积分信息成功:', integralResponse)
            // 确保integralResponse.data存在
            if (integralResponse.data) {
              // 更新积分和上传次数，确保与积分管理页面一致
              userInfo.value.points = integralResponse.data.totalPoints || userInfo.value.points
              userInfo.value.uploadCount = integralResponse.data.uploadCount || 0
              console.log('已更新积分:', userInfo.value.points)
              console.log('已更新上传次数:', userInfo.value.uploadCount)
            }
          } catch (integralError) {
            console.error('获取积分信息失败:', integralError)
          }
          
          // 更新localStorage中的用户信息，确保刷新页面后显示最新信息
          if (typeof localStorage !== 'undefined') {
            localStorage.setItem('user', JSON.stringify(userInfo.value))
            console.log('已更新localStorage中的用户信息')
          }
      }

    } catch (apiError) {
      console.error('API调用失败:', apiError)
      // API调用失败时，清除localStorage中的用户信息
      if (typeof localStorage !== 'undefined') {
        localStorage.removeItem('user')
      }
    }
  } catch (error) {
    console.error('获取用户信息失败:', error)
    router.push('/login')
  }
}

// 获取上传历史
const getUploadHistory = async () => {
  try {
    const token = typeof localStorage !== 'undefined' ? localStorage.getItem('token') : ''
    console.log('获取上传历史，token:', token)
    const params = {
      page: uploadCurrentPage.value,
      pageSize: pageSize.value
    }
    console.log('获取上传历史，参数:', params)
    const config = {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    }
    console.log('获取上传历史，配置:', config)
    console.log('开始调用API...')
    const response = await api.get('/user/uploadHistory', { params })
    console.log('获取上传历史成功:', response)
    // 打印列表数据，查看每个item的categoryName
    console.log('资料列表:', response.data.list)
    console.log('资料总数:', response.data.total)
    // 检查response.data.list是否存在
    if (response.data && response.data.list && Array.isArray(response.data.list)) {
      response.data.list.forEach((item, index) => {
        console.log(`资料${index + 1}:`, item)
        console.log(`资料${index + 1}分类名称:`, item.categoryName)
      })
      // 处理上传历史数据
      uploadHistory.value = response.data.list.map(item => ({
        id: item.id,
        name: item.name,
        categoryName: item.categoryName || '未分类', // 使用后端返回的分类名称
        uploadTime: item.createTime,
        downloadCount: item.downloadCount,
        points: item.points || 20 // 使用后端返回的获得积分，默认为20
      }))
      uploadTotal.value = response.data.total
    } else {
      console.error('获取上传历史失败: response.data.list 不存在或不是数组')
      uploadHistory.value = []
      uploadTotal.value = 0
    }
    console.log('上传历史数据处理完成:', uploadHistory.value)
  } catch (error) {
    console.error('获取上传历史失败:', error)
  }
}

// 获取下载历史
const getDownloadHistory = async () => {
  try {
    const token = typeof localStorage !== 'undefined' ? localStorage.getItem('token') : ''
    console.log('获取下载历史，token:', token)
    const params = {
      page: downloadCurrentPage.value,
      pageSize: pageSize.value
    }
    console.log('获取下载历史，参数:', params)
    const config = {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    }
    console.log('获取下载历史，配置:', config)
    console.log('开始调用API...')
    const response = await api.get('/user/downloadHistory', { params })
    console.log('获取下载历史成功:', response)
    // 打印下载历史列表数据
    console.log('下载历史列表:', response.data.list)
    console.log('下载历史总数:', response.data.total)
    // 检查response.data.list是否存在
    if (response.data && response.data.list && Array.isArray(response.data.list)) {
      downloadHistory.value = response.data.list
      downloadTotal.value = response.data.total
    } else {
      console.error('获取下载历史失败: response.data.list 不存在或不是数组')
      downloadHistory.value = []
      downloadTotal.value = 0
    }
    console.log('下载历史数据处理完成:', downloadHistory.value)
  } catch (error) {
    console.error('获取下载历史失败:', error)
  }
}

// 获取密码修改记录
const getPasswordHistory = async () => {
  try {
    const token = typeof localStorage !== 'undefined' ? localStorage.getItem('token') : ''
    console.log('获取密码修改记录，token:', token)
    const config = {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    }
    console.log('获取密码修改记录，配置:', config)
    console.log('开始调用API...')
    const response = await api.get('/user/password/history')
    console.log('获取密码修改记录成功:', response)
    // 检查response.data是否存在
    if (response.data && Array.isArray(response.data)) {
      passwordHistory.value = response.data
      console.log('密码修改记录数据:', passwordHistory.value)
    } else {
      console.error('获取密码修改记录失败: response.data 不存在或不是数组')
      passwordHistory.value = []
    }
  } catch (error) {
    console.error('获取密码修改记录失败:', error)
    passwordHistory.value = []
  }
}

// 上传历史分页处理
const handleUploadSizeChange = (size) => {
  pageSize.value = size
  uploadCurrentPage.value = 1
  getUploadHistory()
}

const handleUploadCurrentChange = (current) => {
  uploadCurrentPage.value = current
  getUploadHistory()
}

// 下载历史分页处理
const handleDownloadSizeChange = (size) => {
  pageSize.value = size
  downloadCurrentPage.value = 1
  getDownloadHistory()
}

const handleDownloadCurrentChange = (current) => {
  downloadCurrentPage.value = current
  getDownloadHistory()
}



// 打开密码修改对话框
const openPasswordDialog = () => {
  passwordDialogVisible.value = true
}

// 打开学院编辑对话框
const openCollegeDialog = () => {
  collegeForm.value.college = userInfo.value.college || ''
  collegeDialogVisible.value = true
}

// 打开专业和年级修改对话框
const openMajorGradeDialog = () => {
  majorGradeForm.value.major = userInfo.value.major || ''
  majorGradeForm.value.grade = userInfo.value.grade || ''
  majorGradeDialogVisible.value = true
}

// 学院修改提交
const handleCollegeSubmit = async () => {
  if (!collegeFormRef.value) return
  await collegeFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const response = await api.post('/user/updateCollege', {
          college: collegeForm.value.college
        })
        if (response.code === 200) {
          userInfo.value.college = collegeForm.value.college
          // 更新localStorage中的用户信息
          if (typeof localStorage !== 'undefined') {
            localStorage.setItem('user', JSON.stringify(userInfo.value))
          }
          ElMessage.success('学院修改成功!')
          collegeDialogVisible.value = false
        } else {
          ElMessage.error(response.message || '学院修改失败!')
        }
      } catch (error) {
        console.error('学院修改失败:', error)
        ElMessage.error('学院修改失败!')
      }
    }
  })
}

// 专业和年级修改提交
const handleMajorGradeSubmit = async () => {
  if (!majorGradeFormRef.value) return
  
  await majorGradeFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const response = await api.post('/user/updateMajorGrade', {
          major: majorGradeForm.value.major,
          grade: majorGradeForm.value.grade
        })
        if (response.code === 200) {
          userInfo.value.major = majorGradeForm.value.major
          userInfo.value.grade = majorGradeForm.value.grade
          // 更新localStorage中的用户信息
          if (typeof localStorage !== 'undefined') {
            localStorage.setItem('user', JSON.stringify(userInfo.value))
          }
          ElMessage.success('专业和年级修改成功!')
          majorGradeDialogVisible.value = false
        } else {
          // 不要处理token无效的情况，直接显示修改失败的错误信息
          ElMessage.error(response.message || '专业和年级修改失败!')
        }
      } catch (error) {
        console.error('专业和年级修改失败:', error)
        ElMessage.error('专业和年级修改失败!')
      }
    }
  })
}



// 密码修改提交
const handlePasswordSubmit = async () => {
  if (!passwordFormRef.value) return
  await passwordFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        const response = await api.post('/user/updatePwd', {
          oldPassword: passwordForm.value.oldPassword,
          newPassword: passwordForm.value.newPassword
        })
        if (response.code === 200) {
          ElMessage.success('密码修改成功!')
          passwordDialogVisible.value = false
          passwordForm.value = {
            oldPassword: '',
            newPassword: '',
            confirmPassword: ''
          }
        } else {
          ElMessage.error(response.message || '密码修改失败!')
        }
      } catch (error) {
        console.error('密码修改失败:', error)
        ElMessage.error('密码修改失败!')
      }
    }
  })
}

// 账户切换（退出登录）
const handleLogout = async () => {
  try {
    console.log('开始退出登录...')
    console.log('localStorage中的token:', localStorage.getItem('token'))
    console.log('localStorage中的user:', localStorage.getItem('user'))
    
    // 先调用后端退出接口，确保退出时间被记录
    const response = await api.post('/user/logout')
    console.log('退出登录响应:', response)
    
    // 检测当前路由，如果在Admin页面，先刷新登录记录
    if (route.path.startsWith('/admin')) {
      try {
        console.log('当前在Admin页面，强制触发登录记录刷新...')
        // 直接调用API获取登录记录，不依赖定时器
        const loginRecordsResponse = await api.get('/admin/login/records', {
          params: { page: 1, pageSize: 10 }
        })
        console.log('强制刷新登录记录成功:', loginRecordsResponse)
        // 等待更长时间，确保Admin页面有足够的时间更新显示
        await new Promise(resolve => setTimeout(resolve, 2500))
      } catch (refreshError) {
        console.error('强制刷新登录记录失败:', refreshError)
        // 即使刷新失败，也要等待一小段时间
        await new Promise(resolve => setTimeout(resolve, 1500))
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

// 初始化数据
onMounted(() => {
  refreshData()
})

// 当页面被激活时重新获取数据
onActivated(() => {
  refreshData()
})

// 刷新所有数据
const refreshData = async () => {
  await getUserInfo()
  await getUploadHistory()
  await getDownloadHistory()
  await getPasswordHistory()
}
</script>

<style scoped>
.user-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.user-info-card {
  margin-bottom: 20px;
}

.upload-history-card {
  margin-bottom: 20px;
}

.download-history-card {
  margin-bottom: 20px;
}

.password-history-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.user-info {
  padding: 20px 0;
  display: flex;
  align-items: center;
  gap: 40px;
}

.avatar-section {
  text-align: center;
  min-width: 140px;
}

.avatar-section .el-avatar {
  cursor: pointer;
  transition: all 0.3s;
  margin-bottom: 15px;
}

.avatar-section .el-avatar:hover {
  transform: scale(1.05);
}

.avatar-text {
  margin-top: 10px;
  font-size: 14px;
  color: #606266;
  line-height: 1.4;
  text-align: center;
  word-break: break-all;
}

.info-content {
  flex: 1;
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 15px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.info-item .label {
  font-weight: 600;
  color: #303133;
  font-size: 14px;
}

.info-item span {
  color: #606266;
  font-size: 14px;
}

.points {
  color: #409EFF;
  font-weight: 600;
  font-size: 16px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

/* 头像上传样式 */
.avatar-uploader .avatar {
  width: 178px;
  height: 178px;
  display: block;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  line-height: 178px;
  text-align: center;
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
}

.dialog-footer {
  width: 100%;
  display: flex;
  justify-content: flex-end;
}
</style>