<template>
  <div class="admin-container">
    <div class="admin-menu">
      <el-menu :default-active="activeMenu" class="el-menu-vertical-demo" @select="handleMenuSelect">
        <el-menu-item index="user-manage">
          <el-icon><User /></el-icon>
          <span>用户管理</span>
        </el-menu-item>
        <el-menu-item index="material-manage">
          <el-icon><Document /></el-icon>
          <span>资料管理</span>
        </el-menu-item>
        <el-menu-item index="category-manage">
          <el-icon><Folder /></el-icon>
          <span>分类管理</span>
        </el-menu-item>
        <el-menu-item index="integral-manage">
          <el-icon><Coin /></el-icon>
          <span>积分管理</span>
        </el-menu-item>
        <el-menu-item index="system-setting">
          <el-icon><Setting /></el-icon>
          <span>系统设置</span>
        </el-menu-item>
      </el-menu>
    </div>
    <div class="admin-content">
      <div v-if="activeMenu === 'user-manage'">
        <h2>用户管理</h2>
        <div class="user-upload-section">
          <el-upload
            class="user-upload"
            action=""
            :on-change="handleFileChange"
            :auto-upload="false"
            accept=".xlsx, .xls"
            multiple
          >
            <el-button type="primary">
              <el-icon><Upload /></el-icon>
              上传学生信息Excel文件
            </el-button>
            <template #tip>
              <div class="el-upload__tip">
                请上传包含学生信息的Excel文件，支持.xlsx和.xls格式
              </div>
            </template>
          </el-upload>
          <el-button type="success" @click="uploadUsers" :disabled="!uploadedFile" class="ml-4">
            确认上传
          </el-button>
        </div>
        <el-alert
          v-if="uploadMessage"
          :title="uploadMessage"
          :type="uploadMessageType"
          show-icon
          class="mt-4"
        />

        <!-- 登录记录 -->
        <div class="login-records mt-6">
          <h3>登录记录</h3>
          <el-button type="primary" @click="getLoginRecords" class="mb-4">
            <el-icon><Refresh /></el-icon>
            刷新登录记录
          </el-button>
          <el-table :data="loginRecords" style="width: 100%" v-loading="loadingLoginRecords">
            <el-table-column prop="id" label="ID" width="80"></el-table-column>
            <el-table-column prop="username" label="用户名" width="120"></el-table-column>
            <el-table-column prop="studentId" label="学号" width="150"></el-table-column>
            <el-table-column prop="loginTime" label="登录时间" width="180">
              <template #default="scope">
                {{ formatDate(scope.row.loginTime) }}
              </template>
            </el-table-column>
            <el-table-column prop="device" label="登录设备" min-width="200"></el-table-column>
            <el-table-column prop="ipAddress" label="IP地址" width="150"></el-table-column>
            <el-table-column prop="logoutTime" label="退出时间" width="180">
              <template #default="scope">
                {{ scope.row.logoutTime ? formatDate(scope.row.logoutTime) : '-' }}
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="100">
              <template #default="scope">
                {{ scope.row.status === 0 ? '在线' : '已退出' }}
              </template>
            </el-table-column>
          </el-table>
          <el-pagination
            v-if="loginRecords && loginRecords.length > 0"
            layout="prev, pager, next"
            :total="loginRecordsTotal"
            :page-size="10"
            :current-page="loginRecordsPage"
            @current-change="handleLoginRecordsPageChange"
            class="mt-4"
          />
        </div>
      </div>
      <div v-else-if="activeMenu === 'material-manage'">
        <h2>资料管理</h2>
        <el-button type="primary" @click="getAuditMaterials">
          <el-icon><Refresh /></el-icon>
          刷新待审核列表
        </el-button>
        <el-table :data="auditMaterials" style="width: 100%" v-loading="loading">
          <el-table-column prop="id" label="ID" width="80"></el-table-column>
          <el-table-column prop="name" label="资料名称" min-width="200">
            <template #default="scope">
              <a href="#" @click.prevent="viewMaterialDetail(scope.row)">{{ scope.row.name }}</a>
            </template>
          </el-table-column>
          <el-table-column prop="categoryId" label="分类ID" width="100"></el-table-column>
          <el-table-column prop="userId" label="上传用户ID" width="120"></el-table-column>
          <el-table-column prop="createTime" label="上传时间" width="180">
            <template #default="scope">
              {{ formatDate(scope.row.createTime) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="180">
            <template #default="scope">
              <el-button type="success" size="small" @click="auditMaterial(scope.row.id, 1)">
                通过
              </el-button>
              <el-button type="danger" size="small" @click="auditMaterial(scope.row.id, 2)">
                驳回
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        <el-pagination
          v-if="auditMaterials.length > 0"
          layout="prev, pager, next"
          :total="auditMaterials.length"
          :page-size="10"
          :current-page="currentPage"
          @current-change="handleCurrentChange"
          class="mt-4"
        />
        <div v-if="auditMaterials.length === 0 && !loading" class="no-data">
          <el-empty description="暂无待审核资料"></el-empty>
        </div>
      </div>
      <div v-else-if="activeMenu === 'category-manage'">
        <h2>分类管理</h2>
        <el-button type="primary" @click="openAddCategoryDialog">
          <el-icon><Plus /></el-icon>
          添加分类
        </el-button>
        <el-table :data="categoryList" style="width: 100%" :key="categoryList.length">
          <el-table-column prop="id" label="分类ID" width="80"></el-table-column>
          <el-table-column prop="name" label="分类名称"></el-table-column>
          <el-table-column prop="description" label="分类描述"></el-table-column>
          <el-table-column prop="parentId" label="父分类ID" width="120"></el-table-column>
          <el-table-column prop="sort" label="排序" width="80"></el-table-column>
          <el-table-column label="操作" width="150">
            <template #default="scope">
              <el-button type="primary" size="small" @click="openEditCategoryDialog(scope.row)">
                <el-icon><Edit /></el-icon>
                编辑
              </el-button>
              <el-button type="danger" size="small" @click="handleDeleteCategory(scope.row.id)">
                <el-icon><Delete /></el-icon>
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <div v-else-if="activeMenu === 'integral-manage'">
        <h2>积分管理</h2>
        <el-button type="primary" @click="getUsers">
          <el-icon><Refresh /></el-icon>
          刷新用户列表
        </el-button>
        <el-button type="success" @click="testApi">
          测试API
        </el-button>
        <el-table :data="userList" style="width: 100%" v-loading="loadingUser">
          <el-table-column prop="id" label="用户ID" width="100"></el-table-column>
          <el-table-column prop="studentId" label="学号" width="150"></el-table-column>
          <el-table-column prop="name" label="姓名" width="120"></el-table-column>
          <el-table-column prop="college" label="学院"></el-table-column>
          <el-table-column prop="major" label="专业"></el-table-column>
          <el-table-column label="班级" width="120">
            <template #default="scope">
              {{ scope.row.className || '未设置' }}
            </template>
          </el-table-column>
          <el-table-column prop="points" label="当前积分" width="120"></el-table-column>
          <el-table-column label="操作" width="180">
            <template #default="scope">
              <el-button type="primary" size="small" @click="openIntegralModal(scope.row)">
                操作积分
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        <div v-if="userList.length === 0 && !loadingUser" class="no-data">
          <el-empty description="暂无用户数据"></el-empty>
        </div>
      </div>
      <div v-else-if="activeMenu === 'system-setting'">
        <h2>系统设置</h2>
        
        <!-- 系统更新设置 -->
        <el-card class="system-update-card">
          <template #header>
            <div class="card-header">
              <span>系统更新设置</span>
            </div>
          </template>
          
          <div class="update-settings">
            <el-form :model="updateForm" :rules="updateRules" ref="updateFormRef">
              <el-form-item label="更新日期" prop="updateDate">
                <el-date-picker
                  v-model="updateForm.updateDate"
                  type="date"
                  placeholder="选择更新日期"
                  style="width: 100%"
                />
              </el-form-item>
              
              <el-form-item label="开始时间" prop="startTime">
                <el-time-picker
                  v-model="updateForm.startTime"
                  placeholder="选择开始时间"
                  style="width: 100%"
                />
              </el-form-item>
              
              <el-form-item label="结束时间" prop="endTime">
                <el-time-picker
                  v-model="updateForm.endTime"
                  placeholder="选择结束时间"
                  style="width: 100%"
                />
              </el-form-item>
            </el-form>
            
            <div class="update-buttons">
              <el-button 
                type="danger" 
                @click="handleSystemUpdate"
                :disabled="isUpdating"
              >
                {{ isUpdating ? '更新中...' : '开始更新' }}
              </el-button>
              <el-button 
                type="success" 
                @click="handleUpdateComplete"
                :disabled="!isUpdating"
              >
                更新完成
              </el-button>
            </div>
            
            <div v-if="updateMessage" class="update-message" :class="updateMessageType">
              {{ updateMessage }}
            </div>
          </div>
        </el-card>
      </div>
      <div v-else>
        <h2>欢迎</h2>
        <p>请选择左侧菜单进行操作</p>
      </div>

      <!-- 添加分类对话框 -->
      <el-dialog v-model="addCategoryDialogVisible" title="添加分类" width="500px">
        <el-form :model="addCategoryForm" :rules="addCategoryRules" ref="addCategoryFormRef">
          <el-form-item label="分类名称" prop="name">
            <el-input v-model="addCategoryForm.name" placeholder="请输入分类名称"></el-input>
          </el-form-item>
          <el-form-item label="分类描述" prop="description">
            <el-input v-model="addCategoryForm.description" placeholder="请输入分类描述" type="textarea"></el-input>
          </el-form-item>
          <el-form-item label="父分类ID" prop="parentId">
            <el-input v-model="addCategoryForm.parentId" placeholder="请输入父分类ID，根分类为0" type="number"></el-input>
          </el-form-item>
          <el-form-item label="排序" prop="sort">
            <el-input v-model="addCategoryForm.sort" placeholder="请输入排序" type="number"></el-input>
          </el-form-item>
        </el-form>
        <template #footer>
          <span class="dialog-footer">
            <el-button @click="addCategoryDialogVisible = false">取消</el-button>
            <el-button type="primary" @click="handleAddCategorySubmit">确定</el-button>
          </span>
        </template>
      </el-dialog>

      <!-- 编辑分类对话框 -->
      <el-dialog v-model="editCategoryDialogVisible" title="编辑分类" width="500px">
        <el-form :model="editCategoryForm" :rules="editCategoryRules" ref="editCategoryFormRef">
          <el-form-item label="分类名称" prop="name">
            <el-input v-model="editCategoryForm.name" placeholder="请输入分类名称"></el-input>
          </el-form-item>
          <el-form-item label="分类描述" prop="description">
            <el-input v-model="editCategoryForm.description" placeholder="请输入分类描述" type="textarea"></el-input>
          </el-form-item>
          <el-form-item label="父分类ID" prop="parentId">
            <el-input v-model="editCategoryForm.parentId" placeholder="请输入父分类ID，根分类为0" type="number"></el-input>
          </el-form-item>
          <el-form-item label="排序" prop="sort">
            <el-input v-model="editCategoryForm.sort" placeholder="请输入排序" type="number"></el-input>
          </el-form-item>
        </el-form>
        <template #footer>
          <span class="dialog-footer">
            <el-button @click="editCategoryDialogVisible = false">取消</el-button>
            <el-button type="primary" @click="handleEditCategorySubmit">确定</el-button>
          </span>
        </template>
      </el-dialog>

      <!-- 资料详情对话框 -->
      <el-dialog v-model="dialogVisible" :title="materialDetail.name || '资料详情'" width="80%">
        <div class="material-detail">
          <el-descriptions :column="2" border>
            <el-descriptions-item label="资料ID">{{ materialDetail.id }}</el-descriptions-item>
            <el-descriptions-item label="上传用户ID">{{ materialDetail.userId }}</el-descriptions-item>
            <el-descriptions-item label="分类ID">{{ materialDetail.categoryId }}</el-descriptions-item>
            <el-descriptions-item label="上传时间">{{ formatDate(materialDetail.createTime) }}</el-descriptions-item>
            <el-descriptions-item label="文件大小">{{ formatFileSize(materialDetail.fileSize) }}</el-descriptions-item>
            <el-descriptions-item label="审核状态">{{ materialDetail.auditStatus === 0 ? '待审核' : materialDetail.auditStatus === 1 ? '已通过' : '已驳回' }}</el-descriptions-item>
          </el-descriptions>
          <div class="description">
            <h3>资料描述</h3>
            <p>{{ materialDetail.description || '无描述' }}</p>
          </div>
          <div class="file-info">
            <h3>文件信息</h3>
            <p>文件路径: {{ materialDetail.filePath }}</p>
            <p>文件名: {{ materialDetail.fileName }}</p>
          </div>
        </div>
        <template #footer>
          <span class="dialog-footer">
            <el-button @click="dialogVisible = false">关闭</el-button>
          </span>
        </template>
      </el-dialog>

      <!-- 积分操作对话框 -->
      <el-dialog v-model="integralDialogVisible" :title="`积分操作 - ${selectedUser.name || ''}`" width="500px">
        <div class="integral-dialog">
          <el-descriptions :column="2" border>
            <el-descriptions-item label="用户ID">{{ selectedUser.id }}</el-descriptions-item>
            <el-descriptions-item label="学号">{{ selectedUser.studentId }}</el-descriptions-item>
            <el-descriptions-item label="姓名">{{ selectedUser.name }}</el-descriptions-item>
            <el-descriptions-item label="当前积分">{{ selectedUser.points || 0 }}</el-descriptions-item>
          </el-descriptions>
          <div class="integral-actions">
            <h3>操作选项</h3>
            <el-form :model="integralForm" :rules="integralRules" ref="integralFormRef">
              <el-form-item label="改变积分" prop="points">
                <el-input v-model="integralForm.points" placeholder="请输入积分变动值（正数增加，负数减少）" type="number"></el-input>
              </el-form-item>
              <el-form-item label="变动原因" prop="reason">
                <el-input v-model="integralForm.reason" placeholder="请输入积分变动原因"></el-input>
              </el-form-item>
            </el-form>
          </div>
        </div>
        <template #footer>
          <span class="dialog-footer">
            <el-button @click="integralDialogVisible = false">取消</el-button>
            <el-button type="primary" @click="changePoints">改变积分</el-button>
            <el-button type="danger" @click="clearPoints">清除积分</el-button>
          </span>
        </template>
      </el-dialog>
    </div>
  </div>
</template>

<script setup>
import { ref, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Document, Folder, Coin, Setting, Upload, Plus, Edit, Delete, Refresh } from '@element-plus/icons-vue'
import api from '../api/index.js'
import * as XLSX from 'xlsx'

const router = useRouter()

// 打印api模块信息
console.log('API模块:', api)
console.log('API基础URL:', api.defaults.baseURL)

const activeMenu = ref('')
const uploadedFile = ref(null)
const uploadMessage = ref('')
const uploadMessageType = ref('info')

// 资料管理相关
const auditMaterials = ref([])
const loading = ref(false)
const currentPage = ref(1)
const dialogVisible = ref(false)
const materialDetail = ref({})

// 分类管理相关
const categoryList = ref([])
const addCategoryDialogVisible = ref(false)
const editCategoryDialogVisible = ref(false)
const addCategoryForm = ref({
  name: '',
  description: '',
  parentId: 0,
  sort: 0
})
const editCategoryForm = ref({
  id: '',
  name: '',
  description: '',
  parentId: 0,
  sort: 0
})
const addCategoryRules = ref({
  name: [
    { required: true, message: '请输入分类名称', trigger: 'blur' }
  ]
})
const editCategoryRules = ref({
  name: [
    { required: true, message: '请输入分类名称', trigger: 'blur' }
  ]
})

// 积分管理相关
const userList = ref([])
const loadingUser = ref(false)
const integralDialogVisible = ref(false)
const selectedUser = ref({})
const integralForm = ref({
  points: 0,
  reason: ''
})
const integralRules = ref({
  points: [
    { required: true, message: '请输入积分变动值', trigger: 'blur' }
  ],
  reason: [
    { required: true, message: '请输入变动原因', trigger: 'blur' }
  ]
})

// 登录记录相关
const loginRecords = ref([])
const loadingLoginRecords = ref(false)
const loginRecordsTotal = ref(0)
const loginRecordsPage = ref(1)
const loginRecordTimer = ref(null)

// 系统更新相关
const updateForm = ref({
  updateDate: new Date(),
  startTime: new Date(),
  endTime: new Date()
})
const updateRules = ref({
  updateDate: [
    { required: true, message: '请选择更新日期', trigger: 'change' }
  ],
  startTime: [
    { required: true, message: '请选择开始时间', trigger: 'change' }
  ],
  endTime: [
    { required: true, message: '请选择结束时间', trigger: 'change' }
  ]
})
const updateFormRef = ref(null)
const isUpdating = ref(false)
const updateMessage = ref('')
const updateMessageType = ref('info')
const updateCompleteTimer = ref(null)
const updateStartTimer = ref(null)

const handleMenuSelect = (key, keyPath) => {
  console.log('菜单选择:', key)
  activeMenu.value = key
  if (key === 'category-manage') {
    getCategoryList()
  } else if (key === 'material-manage') {
    getAuditMaterials()
  } else if (key === 'integral-manage') {
    console.log('选择了积分管理，准备获取用户列表...')
    console.log('localStorage中的token:', localStorage.getItem('token'))
    console.log('localStorage中的user:', localStorage.getItem('user'))
    getUsers()
  } else if (key === 'user-manage') {
    // 开始登录记录自动刷新
    startLoginRecordRefresh()
  }
}

// 处理退出登录（Admin页面专用）
const handleAdminLogout = async () => {
  try {
    console.log('Admin页面开始退出登录...')
    
    // 先调用后端退出接口，确保退出时间被记录
    const response = await api.post('/user/logout')
    console.log('退出登录响应:', response)
    
    // 强制刷新登录记录，确保获取到最新的退出时间
    console.log('强制刷新登录记录...')
    const refreshResponse = await api.get('/admin/login/records', {
      params: { page: loginRecordsPage.value, pageSize: 10 }
    })
    console.log('强制刷新登录记录成功:', refreshResponse)
    
    // 更新本地状态
    if (refreshResponse.code === 200 && refreshResponse.data && refreshResponse.data.list) {
      loginRecords.value = refreshResponse.data.list
      loginRecordsTotal.value = refreshResponse.data.total || 0
      console.log('Admin页面登录记录已更新')
    }
    
    // 等待一小段时间，让用户看到更新后的退出时间
    console.log('等待2秒，让用户看到更新后的退出时间...')
    await new Promise(resolve => setTimeout(resolve, 2000))
    console.log('等待完成，准备清除token并跳转...')
    
    // 清除本地存储的用户信息和token
    localStorage.removeItem('user')
    localStorage.removeItem('token')
    
    ElMessage.success('退出登录成功')
    // 跳转到登录页面
    router.push('/login')
  } catch (error) {
    console.error('退出登录失败:', error)
    
    // 即使后端调用失败，也要清除本地存储并跳转
    localStorage.removeItem('user')
    localStorage.removeItem('token')
    ElMessage.success('退出登录成功')
    router.push('/login')
  }
}

// 全局退出登录处理函数，供Layout组件调用
window.handleAdminLogout = handleAdminLogout

// 资料管理相关方法
const getAuditMaterials = async () => {
  loading.value = true
  try {
    const response = await api.get('/admin/material/audit/list')
    if (response.code === 200) {
      auditMaterials.value = response.data
    } else {
      uploadMessage.value = '获取待审核资料失败: ' + (response.message || '未知错误')
      uploadMessageType.value = 'error'
    }
  } catch (error) {
    console.error('获取待审核资料失败:', error)
    uploadMessage.value = '获取待审核资料失败，请检查网络连接'
    uploadMessageType.value = 'error'
  } finally {
    loading.value = false
  }
}

const auditMaterial = async (materialId, auditStatus) => {
  try {
    const response = await api.post('/admin/material/audit', {
      materialId: materialId,
      auditStatus: auditStatus
    })
    if (response.code === 200) {
      uploadMessage.value = '审核成功'
      uploadMessageType.value = 'success'
      getAuditMaterials()
    } else {
      uploadMessage.value = '审核失败: ' + (response.message || '未知错误')
      uploadMessageType.value = 'error'
    }
  } catch (error) {
    console.error('审核资料失败:', error)
    uploadMessage.value = '审核资料失败，请检查网络连接'
    uploadMessageType.value = 'error'
  }
}

const viewMaterialDetail = async (material) => {
  try {
    const response = await api.get(`/material/info/${material.id}`)
    if (response.code === 200) {
      materialDetail.value = response.data
      dialogVisible.value = true
    } else {
      uploadMessage.value = '获取资料详情失败: ' + (response.message || '未知错误')
      uploadMessageType.value = 'error'
    }
  } catch (error) {
    console.error('获取资料详情失败:', error)
    uploadMessage.value = '获取资料详情失败，请检查网络连接'
    uploadMessageType.value = 'error'
  }
}

const formatDate = (date) => {
  if (!date) return ''
  const d = new Date(date)
  return d.toLocaleString()
}

const formatFileSize = (bytes) => {
  if (!bytes) return '0 B'
  const k = 1024
  const sizes = ['B', 'KB', 'MB', 'GB', 'TB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

const handleCurrentChange = (current) => {
  currentPage.value = current
}

// 获取分类列表
const getCategoryList = async () => {
  try {
    const response = await api.get('/category/list')
    if (response.code === 200) {
      categoryList.value = response.data
    } else {
      uploadMessage.value = '获取分类列表失败'
      uploadMessageType.value = 'error'
    }
  } catch (error) {
    console.error('获取分类列表失败:', error)
    uploadMessage.value = '获取分类列表失败'
    uploadMessageType.value = 'error'
  }
}

// 打开添加分类对话框
const openAddCategoryDialog = () => {
  addCategoryForm.value = {
    name: '',
    description: '',
    parentId: 0,
    sort: 0
  }
  addCategoryDialogVisible.value = true
}

// 打开编辑分类对话框
const openEditCategoryDialog = (row) => {
  editCategoryForm.value = {
    id: row.id,
    name: row.name,
    description: row.description,
    parentId: row.parentId,
    sort: row.sort
  }
  editCategoryDialogVisible.value = true
}

// 处理添加分类提交
const handleAddCategorySubmit = async () => {
  try {
    const response = await api.post('/category/create', addCategoryForm.value)
    if (response.code === 200) {
      uploadMessage.value = '添加分类成功'
      uploadMessageType.value = 'success'
      addCategoryDialogVisible.value = false
      getCategoryList()
    } else {
      uploadMessage.value = '添加分类失败: ' + (response.message || '未知错误')
      uploadMessageType.value = 'error'
    }
  } catch (error) {
    console.error('添加分类失败:', error)
    uploadMessage.value = '添加分类失败'
    uploadMessageType.value = 'error'
  }
}

// 处理编辑分类提交
const handleEditCategorySubmit = async () => {
  try {
    const response = await api.put('/category/update', editCategoryForm.value)
    if (response.code === 200) {
      uploadMessage.value = '编辑分类成功'
      uploadMessageType.value = 'success'
      editCategoryDialogVisible.value = false
      getCategoryList()
    } else {
      uploadMessage.value = '编辑分类失败: ' + (response.message || '未知错误')
      uploadMessageType.value = 'error'
    }
  } catch (error) {
    console.error('编辑分类失败:', error)
    uploadMessage.value = '编辑分类失败'
    uploadMessageType.value = 'error'
  }
}

// 处理删除分类
const handleDeleteCategory = async (id) => {
  try {
    const response = await api.delete(`/category/delete/${id}`)
    if (response.code === 200) {
      uploadMessage.value = '删除分类成功'
      uploadMessageType.value = 'success'
      getCategoryList()
    } else {
      uploadMessage.value = '删除分类失败: ' + (response.message || '未知错误')
      uploadMessageType.value = 'error'
    }
  } catch (error) {
    console.error('删除分类失败:', error)
    uploadMessage.value = '删除分类失败'
    uploadMessageType.value = 'error'
  }
}

const handleFileChange = (file, fileList) => {
  uploadedFile.value = file.raw
  uploadMessage.value = ''
}

const uploadUsers = async () => {
  if (!uploadedFile.value) {
    uploadMessage.value = '请先选择Excel文件'
    uploadMessageType.value = 'warning'
    return
  }

  // 解析Excel文件
  const reader = new FileReader()
  reader.onload = async (e) => {
    try {
      console.log('开始解析Excel文件')
      const data = new Uint8Array(e.target.result)
      console.log('读取文件数据成功，长度:', data.length)
      
      // 尝试解析Excel文件
      const workbook = XLSX.read(data, { type: 'array' })
      console.log('解析工作簿成功，工作表数量:', workbook.SheetNames.length)
      
      const sheetName = workbook.SheetNames[0]
      console.log('使用工作表:', sheetName)
      
      const worksheet = workbook.Sheets[sheetName]
      console.log('获取工作表成功')
      
      // 方法1：使用range参数限制解析范围
      // 首先获取工作表的范围
      const range = XLSX.utils.decode_range(worksheet['!ref'] || 'A1:A1')
      console.log('工作表原始范围:', worksheet['!ref'], '解析后范围:', range)
      
      // 方法2：直接使用原始解析，获取数组格式数据
      const rawJsonData = XLSX.utils.sheet_to_json(worksheet, { header: 1 })
      console.log('原始JSON数据（数组格式）行数:', rawJsonData.length)
      console.log('原始JSON数据（数组格式）:', rawJsonData)
      
      // 宽松过滤：保留所有可能包含用户信息的行
      const finalJsonData = rawJsonData.filter(row => {
        // 检查是否为空数组
        if (!row || row.length === 0) {
          return false
        }
        
        // 检查是否有至少一个非空单元格
        const hasNonEmptyCell = row.some(cell => {
          return cell !== null && cell !== undefined && String(cell).trim() !== ''
        })
        
        const result = hasNonEmptyCell
        console.log(`过滤检查 - 原始数据:`, row, `结果:`, result)
        return result
      })
      console.log('过滤后的数据行数:', finalJsonData.length)
      console.log('过滤后的数据:', finalJsonData)

      // 转换数据格式
      const users = finalJsonData.map((row, index) => {
        // 提取关键字段
        let studentId = ''
        let name = ''
        let grade = ''
        let major = ''
        let college = ''
        let className = ''
        
        console.log(`=== 开始解析第${index + 2}行数据 ===`)
        console.log(`原始数据:`, row)
        
        // 跳过标题行
        const firstCell = String(row[0] || '').trim()
        const commonHeaders = ['学号', '姓名', '年级', '专业', '学院', '班级', 'class', 'grade', 'major', 'college', 'student_id', 'studentid']
        if (commonHeaders.includes(firstCell)) {
          console.log(`跳过标题行:`, row)
          return { studentId: '', name: '', grade: '', major: '', college: '', className: '' }
        }
        
        // 直接从数组中提取数据
        for (let i = 0; i < row.length; i++) {
          const value = String(row[i] || '').trim()
          console.log(`处理单元格[${i}]: "${value}"`)
          
          if (!studentId || !name) {
            // 处理学号和姓名在同一单元格的情况，如"54455张三"
            if (value) {
              const valueStr = String(value || '').trim()
              // 尝试匹配数字开头后跟中文的模式，支持数字和中文之间有空格
              const combinedMatch = valueStr.match(/^(\d+)\s*([\u4e00-\u9fa5]+)$/)
              if (combinedMatch && combinedMatch.length === 3) {
                console.log(`检测到学号和姓名在同一单元格: "${valueStr}"`)
                studentId = combinedMatch[1]
                name = combinedMatch[2]
                console.log(`从同一单元格分离 - 学号: "${studentId}", 姓名: "${name}"`)
              } else if (!studentId && /^\d+$/.test(valueStr)) {
                studentId = valueStr
                console.log(`提取学号: ${studentId}`)
              } else if (!name && valueStr && !/^\d+$/.test(valueStr)) {
                name = valueStr
                console.log(`提取姓名: ${name}`)
              }
            }
          } else {
            // 提取其他信息
            if (!grade && value) {
              grade = value
              console.log(`提取年级: ${grade}`)
            } else if (!major && value) {
              major = value
              console.log(`提取专业: ${major}`)
            } else if (!college && value) {
              college = value
              console.log(`提取学院: ${college}`)
            } else if (!className && value) {
              className = value
              console.log(`提取班级: ${className}`)
            }
          }
        }
        
        // 尝试从后续单元格提取其他信息
        if (studentId && name) {
          for (let i = 0; i < row.length; i++) {
            const value = String(row[i] || '').trim()
            if (value && !/^\d+[\u4e00-\u9fa5]+$/.test(value) && !/^\d+$/.test(value) && !/^[\u4e00-\u9fa5]+$/.test(value)) {
              if (!grade) {
                grade = value
                console.log(`补充提取年级: ${grade}`)
              } else if (!major) {
                major = value
                console.log(`补充提取专业: ${major}`)
              } else if (!college) {
                college = value
                console.log(`补充提取学院: ${college}`)
              } else if (!className) {
                className = value
                console.log(`补充提取班级: ${className}`)
              }
            }
          }
        }
        
        // 处理没有标题的Excel表格（图一类型）或标题不匹配的情况
        if (!studentId || !name) {
          console.log(`需要从值中提取数据，当前学号: "${studentId}", 姓名: "${name}"`)
          // 尝试直接从数组中提取（适用于没有标题的表格）
          const values = row
          console.log(`尝试从值中提取数据，值数量: ${values.length}`, values)
          
          // 尝试不同的数据排列顺序
          const possibleOrders = [
            [0, 1, 2, 3, 4, 5], // 学号、姓名、年级、专业、学院、班级
            [1, 0, 2, 3, 4, 5], // 姓名、学号、年级、专业、学院、班级
            [0, 1, 3, 2, 4, 5], // 学号、姓名、专业、年级、学院、班级
            [0, 1, 2, 4, 3, 5], // 学号、姓名、年级、学院、专业、班级
            [0, 1, 4, 2, 3, 5], // 学号、姓名、学院、年级、专业、班级
            [0, 1, 5, 2, 3, 4], // 学号、姓名、班级、年级、专业、学院
          ]
          
          for (const order of possibleOrders) {
            if (values.length > Math.max(...order)) {
              const tempStudentId = String(values[order[0]] || '').trim()
              const tempName = String(values[order[1]] || '').trim()
              
              console.log(`尝试顺序 ${order}: 学号="${tempStudentId}", 姓名="${tempName}"`)
              
              // 检查是否可能是有效的学号和姓名
              if (tempStudentId && tempName && /^\d+$/.test(tempStudentId)) {
                studentId = tempStudentId
                name = tempName
                grade = String(values[order[2]] || '').trim()
                major = String(values[order[3]] || '').trim()
                college = String(values[order[4]] || '').trim()
                className = String(values[order[5]] || '').trim()
                console.log(`从值中提取数据成功，使用顺序: ${order}`, {
                  studentId,
                  name,
                  grade,
                  major,
                  college,
                  className
                })
                break
              }
            }
          }
        }
        
        // 特殊处理：直接从原始数据中提取可能的学号和姓名
        if (!studentId || !name) {
          console.log(`尝试特殊处理，直接从原始数据中提取学号和姓名`)
          const values = row
          
          // 尝试找到第一个数字作为学号，第一个非数字作为姓名
          for (let i = 0; i < values.length; i++) {
            const value = String(values[i] || '').trim()
            console.log(`处理值[${i}]: "${value}"`)
            
            if (!studentId || !name) {
              // 处理学号和姓名在同一单元格的情况，如"54455张三"
              if (value && /^\s*\d+\s*[\u4e00-\u9fa5]+\s*$/.test(value)) {
                console.log(`检测到学号和姓名在同一单元格: "${value}"`)
                // 分离数字和中文
                const numberMatch = value.match(/\d+/)
                const chineseMatch = value.match(/[\u4e00-\u9fa5]+/)
                if (numberMatch && chineseMatch) {
                  studentId = numberMatch[0]
                  name = chineseMatch[0]
                  console.log(`从同一单元格分离 - 学号: "${studentId}", 姓名: "${name}"`)
                }
              } else if (!studentId && /^\d+$/.test(value)) {
                studentId = value
                console.log(`特殊处理：提取学号: ${studentId}`)
              } else if (!name && value && !/^\d+$/.test(value)) {
                name = value
                console.log(`特殊处理：提取姓名: ${name}`)
              }
            }
            
            if (studentId && name) {
              // 尝试提取其他信息
              if (i + 1 < values.length) grade = String(values[i + 1] || '').trim()
              if (i + 2 < values.length) major = String(values[i + 2] || '').trim()
              if (i + 3 < values.length) college = String(values[i + 3] || '').trim()
              if (i + 4 < values.length) className = String(values[i + 4] || '').trim()
              console.log(`特殊处理成功，提取到完整信息:`, {
                studentId,
                name,
                grade,
                major,
                college,
                className
              })
              break
            }
          }
        }
        
        // 验证数据
        const user = {
          studentId,
          name,
          grade,
          major,
          college,
          className
        }
        
        // 打印解析结果，方便调试
        console.log(`解析第${index + 2}行数据完成:`, {
          original: row,
          parsed: user
        })
        console.log(`=== 解析第${index + 2}行数据结束 ===`)
        
        return user
      }).filter(user => {
        // 增强数据验证：过滤掉无效用户
        const hasStudentId = user.studentId && String(user.studentId).trim() !== '' && String(user.studentId).trim() !== '0'
        const hasName = user.name && String(user.name).trim() !== ''
        const isStudentIdValid = hasStudentId && /^\d+$/.test(String(user.studentId).trim())
        
        const valid = hasStudentId && hasName && isStudentIdValid
        if (!valid) {
          console.log('过滤掉无效用户:', user)
          console.log('验证结果 - 有学号:', hasStudentId, '有姓名:', hasName, '学号有效:', isStudentIdValid)
          console.log('学号详细信息:', {
            value: user.studentId,
            type: typeof user.studentId,
            trimmed: user.studentId ? String(user.studentId).trim() : '',
            isEmpty: !user.studentId || String(user.studentId).trim() === '',
            isZero: user.studentId && String(user.studentId).trim() === '0',
            isNumber: user.studentId && /^\d+$/.test(String(user.studentId).trim())
          })
          console.log('姓名详细信息:', {
            value: user.name,
            type: typeof user.name,
            trimmed: user.name ? String(user.name).trim() : '',
            isEmpty: !user.name || String(user.name).trim() === ''
          })
        } else {
          console.log('验证通过用户:', user)
        }
        return valid
      }) // 过滤掉缺少关键信息或格式错误的行

      console.log('转换后的用户数据:', users)
      console.log('最终处理用户数量:', users.length)

      if (users.length === 0) {
        uploadMessage.value = 'Excel文件中没有有效的学生信息'
        uploadMessageType.value = 'warning'
        return
      }

      // 先获取已有的用户列表，用于比较
      console.log('开始获取已有的用户列表...')
      let existingUsers = []
      try {
        const existingUsersResponse = await api.get('/admin/user/list')
        console.log('获取已有的用户列表响应:', existingUsersResponse)
        if (existingUsersResponse.code === 200) {
          existingUsers = existingUsersResponse.data
        }
      } catch (error) {
        console.error('获取已有的用户列表失败:', error)
        console.error('错误消息:', error.message)
        uploadMessage.value = '获取用户列表失败: ' + error.message
        uploadMessageType.value = 'error'
        return
      }

      // 再次严格过滤用户数据，确保只发送有效的用户
      const validUsers = users.filter(user => {
        const hasValidStudentId = user.studentId && String(user.studentId).trim() !== '' && /^\d+$/.test(String(user.studentId).trim())
        const hasValidName = user.name && String(user.name).trim() !== ''
        return hasValidStudentId && hasValidName
      })
      console.log('最终发送的有效用户数量:', validUsers.length)
      console.log('最终发送的用户数据:', validUsers)

      // 发送批量创建用户请求（只发送有效的用户）
      console.log('开始发送批量创建用户请求')
      console.log('用户数量:', validUsers.length)
      console.log('准备发送的用户数据:', validUsers)
      let response
      if (validUsers.length > 0) {
        try {
          console.log('发送POST请求到 /admin/user/batch')
          console.log('确认发送的用户数量:', validUsers.length)
          response = await api.post('/admin/user/batch', validUsers)
          console.log('批量创建用户请求响应:', response)
        } catch (error) {
          console.error('批量创建用户请求失败:', error)
          console.error('错误消息:', error.message)
          console.error('错误响应:', error.response)
          uploadMessage.value = '批量上传失败: ' + error.message
          uploadMessageType.value = 'error'
          return
        }
      } else {
        // 没有用户，模拟成功响应
        response = { code: 200 }
      }
      
      if (response.code === 200) {
        let message = '学生信息上传成功'
        message += `，处理了 ${validUsers.length} 个用户`
        uploadMessage.value = message
        uploadMessageType.value = 'success'
        uploadedFile.value = null

        // 显示成功提示
        setTimeout(() => {
          ElMessage.success(`学生信息上传成功！\n` +
            `系统已处理 ${validUsers.length} 个用户\n` +
            `- 新用户将被创建，默认密码为123456\n` +
            `- 已存在的用户信息将被更新为最新信息\n` +
            `- 信息有变动的用户密码将重置为123456\n` +
            `- 信息无变动的用户保持原有密码不变`)
        }, 1000)
      } else {
        uploadMessage.value = '学生信息上传失败: ' + (response.message || '未知错误')
        uploadMessageType.value = 'error'
      }
    } catch (error) {
      console.error('解析Excel文件失败:', error)
      console.error('错误堆栈:', error.stack)
      uploadMessage.value = '解析Excel文件失败，请检查文件格式: ' + error.message + '\n错误堆栈: ' + error.stack
      uploadMessageType.value = 'error'
    }
  }
  
  reader.onerror = (error) => {
    console.error('文件读取失败:', error)
    console.error('错误堆栈:', error.stack)
    uploadMessage.value = '文件读取失败，请检查文件: ' + error.message
    uploadMessageType.value = 'error'
  }
  
  reader.readAsArrayBuffer(uploadedFile.value)
}

// 积分管理相关方法
const getUsers = async () => {
  loadingUser.value = true
  try {
    console.log('开始获取用户列表...')
    console.log('API请求URL:', '/admin/user/list')
    console.log('API基础URL:', api.defaults.baseURL)
    // 手动构建完整的请求URL
    const fullUrl = api.defaults.baseURL + '/admin/user/list'
    console.log('完整请求URL:', fullUrl)
    console.log('localStorage中的token:', localStorage.getItem('token'))
    console.log('localStorage中的user:', localStorage.getItem('user'))
    const response = await api.get('/admin/user/list')
    console.log('获取用户列表响应:', response)
    if (response.code === 200) {
      console.log('获取用户列表成功，数据:', response.data)
      // 检查每个用户的className字段
      response.data.forEach((user, index) => {
        console.log(`用户 ${index + 1}: 学号=${user.studentId}, 姓名=${user.name}, className=${user.className}`)
        console.log(`用户 ${index + 1} 所有字段:`, user)
      })
      // 按学号排序，方便与Excel表格对比
      const sortedUsers = response.data.sort((a, b) => {
        return a.studentId.localeCompare(b.studentId)
      })
      console.log('排序后的用户列表:', sortedUsers)
      // 检查排序后用户的className字段
      sortedUsers.forEach((user, index) => {
        console.log(`排序后用户 ${index + 1}: className=${user.className}, 类型=${typeof user.className}`)
      })
      userList.value = sortedUsers
    } else {
      console.error('获取用户列表失败:', response.message || '未知错误')
      uploadMessage.value = '获取用户列表失败: ' + (response.message || '未知错误')
      uploadMessageType.value = 'error'
    }
  } catch (error) {
    console.error('获取用户列表异常:', error)
    console.error('异常详情:', error.response)
    console.error('异常消息:', error.message)
    uploadMessage.value = '获取用户列表失败，请检查网络连接'
    uploadMessageType.value = 'error'
  } finally {
    loadingUser.value = false
  }
}

const openIntegralModal = (user) => {
  selectedUser.value = user
  integralForm.value = {
    points: 0,
    reason: ''
  }
  integralDialogVisible.value = true
}

const changePoints = async () => {
  try {
    const response = await api.post('/admin/integral/update', {
      userId: selectedUser.value.id,
      points: parseInt(integralForm.value.points),
      reason: integralForm.value.reason
    })
    if (response.code === 200) {
      uploadMessage.value = '积分修改成功'
      uploadMessageType.value = 'success'
      integralDialogVisible.value = false
      getUsers()
    } else {
      uploadMessage.value = '积分修改失败: ' + (response.message || '未知错误')
      uploadMessageType.value = 'error'
    }
  } catch (error) {
    console.error('修改积分失败:', error)
    uploadMessage.value = '修改积分失败，请检查网络连接'
    uploadMessageType.value = 'error'
  }
}

const clearPoints = async () => {
  try {
    const response = await api.post('/admin/integral/clear', {
      userId: selectedUser.value.id
    })
    if (response.code === 200) {
      uploadMessage.value = '积分清除成功'
      uploadMessageType.value = 'success'
      integralDialogVisible.value = false
      getUsers()
    } else {
      uploadMessage.value = '积分清除失败: ' + (response.message || '未知错误')
      uploadMessageType.value = 'error'
    }
  } catch (error) {
    console.error('清除积分失败:', error)
    uploadMessage.value = '清除积分失败，请检查网络连接'
    uploadMessageType.value = 'error'
  }
}

const testApi = async () => {
  try {
    console.log('开始测试API...')
    const response = await api.get('/admin/test')
    console.log('测试API响应:', response)
    if (response.code === 200) {
      uploadMessage.value = '测试API调用成功'
      uploadMessageType.value = 'success'
    } else {
      uploadMessage.value = '测试API调用失败: ' + (response.message || '未知错误')
      uploadMessageType.value = 'error'
    }
  } catch (error) {
    console.error('测试API异常:', error)
    uploadMessage.value = '测试API调用失败，请检查网络连接'
    uploadMessageType.value = 'error'
  }
}

// 登录记录相关方法
const getLoginRecords = async () => {
  loadingLoginRecords.value = true
  try {
    console.log('开始获取登录记录...')
    console.log('请求参数:', { page: loginRecordsPage.value, pageSize: 10 })
    
    // 确保token存在
    const token = localStorage.getItem('token')
    console.log('使用的token:', token)
    
    if (!token) {
      console.error('未登录，token为空')
      uploadMessage.value = '获取登录记录失败: 未登录'
      uploadMessageType.value = 'error'
      loginRecords.value = []
      loginRecordsTotal.value = 0
      // 停止自动刷新
      stopLoginRecordRefresh()
      return
    }
    
    // 使用api实例发送请求
    const response = await api.get('/admin/login/records', {
      params: {
        page: loginRecordsPage.value,
        pageSize: 10
      }
    })
    
    console.log('登录记录响应:', response)
    console.log('响应类型:', typeof response)
    
    // 处理响应数据
    if (response && response.code === 200) {
      if (response.data) {
        console.log('响应data:', response.data)
        
        if (response.data.list) {
          console.log('获取登录记录成功，记录数:', response.data.list.length)
          loginRecords.value = response.data.list
          loginRecordsTotal.value = response.data.total || 0
          uploadMessage.value = '' // 清除错误信息
        } else {
          console.error('响应data中没有list字段')
          uploadMessage.value = '获取登录记录失败: 数据结构不正确'
          uploadMessageType.value = 'error'
          loginRecords.value = []
          loginRecordsTotal.value = 0
        }
      } else {
        console.error('响应中没有data字段')
        uploadMessage.value = '获取登录记录失败: 数据结构不正确'
        uploadMessageType.value = 'error'
        loginRecords.value = []
        loginRecordsTotal.value = 0
      }
    } else {
      console.error('响应code不是200:', response?.code)
      uploadMessage.value = '获取登录记录失败: ' + (response?.message || '未知错误')
      uploadMessageType.value = 'error'
      loginRecords.value = []
      loginRecordsTotal.value = 0
    }
  } catch (error) {
    console.error('获取登录记录失败:', error)
    console.error('错误消息:', error.message)
    console.error('错误堆栈:', error.stack)
    uploadMessage.value = '获取登录记录失败，请检查网络连接'
    uploadMessageType.value = 'error'
    loginRecords.value = []
    loginRecordsTotal.value = 0
  } finally {
    loadingLoginRecords.value = false
  }
}

const handleLoginRecordsPageChange = (current) => {
  loginRecordsPage.value = current
  getLoginRecords()
}

// 开始登录记录自动刷新
const startLoginRecordRefresh = () => {
  // 清除之前的定时器（如果存在）
  if (loginRecordTimer.value) {
    clearInterval(loginRecordTimer.value)
  }
  
  // 立即获取一次登录记录
  getLoginRecords()
  
  // 设置定时器，每5秒刷新一次登录记录
  loginRecordTimer.value = setInterval(() => {
    getLoginRecords()
  }, 5000)
  console.log('登录记录自动刷新已启动')
}

// 停止登录记录自动刷新
const stopLoginRecordRefresh = () => {
  if (loginRecordTimer.value) {
    clearInterval(loginRecordTimer.value)
    loginRecordTimer.value = null
    console.log('登录记录自动刷新已停止')
  }
}

// 系统更新相关方法
const handleSystemUpdate = async () => {
  if (!updateFormRef.value) return
  
  await updateFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        // 检查当前时间是否已达到设定的开始时间
        const startTime = new Date(updateForm.value.startTime)
        const now = new Date()
        const startDiff = startTime.getTime() - now.getTime()
        
        console.log('开始时间:', startTime)
        console.log('当前时间:', now)
        console.log('开始时间差(毫秒):', startDiff)
        
        if (startDiff > 0) {
          // 未到开始时间，设置定时器在到达开始时间时执行更新
          updateMessage.value = `系统将在 ${startTime.toLocaleString()} 开始更新`
          updateMessageType.value = 'info'
          
          // 清除之前的定时器（如果存在）
          if (updateStartTimer.value) {
            clearTimeout(updateStartTimer.value)
          }
          
          // 设置开始更新定时器
          updateStartTimer.value = setTimeout(async () => {
            console.log('到达开始时间，自动执行开始更新...')
            await executeSystemUpdate()
          }, startDiff)
          
          console.log('开始更新定时器已设置，将在', startTime, '自动执行')
        } else {
          // 已到开始时间，立即执行更新
          await executeSystemUpdate()
        }
      } catch (error) {
        console.error('系统更新开始失败:', error)
        updateMessage.value = '系统更新开始失败，请检查网络连接'
        updateMessageType.value = 'error'
        isUpdating.value = false
      }
    }
  })
}

// 执行系统更新
const executeSystemUpdate = async () => {
  try {
    // 显示更新中状态
    isUpdating.value = true
    updateMessage.value = '系统开始更新，正在强制所有用户退出...'
    updateMessageType.value = 'warning'
    
    // 调用后端接口强制所有用户退出
    console.log('开始强制所有用户退出...')
    const response = await api.post('/admin/system/update/start', {
      updateDate: updateForm.value.updateDate,
      startTime: updateForm.value.startTime,
      endTime: updateForm.value.endTime
    })
    
    if (response.code === 200) {
      updateMessage.value = '系统更新开始成功，所有用户已被强制退出'
      updateMessageType.value = 'success'
      
      // 管理员不需要被强制退出，只有普通用户需要
      console.log('系统更新开始成功，管理员可以继续操作')
      
      // 设置定时器，在到达结束时间时自动调用更新完成方法
      console.log('设置更新完成定时器...')
      const endTime = new Date(updateForm.value.endTime)
      const now = new Date()
      const endDiff = endTime.getTime() - now.getTime()
      
      if (endDiff > 0) {
        console.log('结束时间:', endTime)
        console.log('当前时间:', now)
        console.log('结束时间差(毫秒):', endDiff)
        
        // 清除之前的定时器（如果存在）
        if (updateCompleteTimer.value) {
          clearTimeout(updateCompleteTimer.value)
        }
        
        // 设置新的定时器
        updateCompleteTimer.value = setTimeout(async () => {
          console.log('到达结束时间，自动调用更新完成方法...')
          await handleUpdateComplete()
        }, endDiff)
        
        console.log('更新完成定时器已设置，将在', endTime, '自动执行')
      } else {
        console.log('结束时间已过，立即调用更新完成方法...')
        await handleUpdateComplete()
      }
    } else {
      updateMessage.value = '系统更新开始失败: ' + (response.message || '未知错误')
      updateMessageType.value = 'error'
      isUpdating.value = false
    }
  } catch (error) {
    console.error('系统更新执行失败:', error)
    updateMessage.value = '系统更新执行失败，请检查网络连接'
    updateMessageType.value = 'error'
    isUpdating.value = false
  }
}

const handleUpdateComplete = async () => {
  try {
    updateMessage.value = '正在完成系统更新...'
    updateMessageType.value = 'info'
    
    // 清除更新完成定时器
    if (updateCompleteTimer.value) {
      clearTimeout(updateCompleteTimer.value)
      updateCompleteTimer.value = null
      console.log('更新完成定时器已清除')
    }
    
    // 调用后端接口完成系统更新
    console.log('完成系统更新...')
    const response = await api.post('/admin/system/update/complete')
    
    if (response.code === 200) {
      updateMessage.value = '系统更新完成，所有功能已恢复正常'
      updateMessageType.value = 'success'
      isUpdating.value = false
    } else {
      updateMessage.value = '系统更新完成失败: ' + (response.message || '未知错误')
      updateMessageType.value = 'error'
    }
  } catch (error) {
    console.error('系统更新完成失败:', error)
    updateMessage.value = '系统更新完成失败，请检查网络连接'
    updateMessageType.value = 'error'
  }
}

// 组件卸载时停止定时器
onUnmounted(() => {
  stopLoginRecordRefresh()
  // 清除更新完成定时器
  if (updateCompleteTimer.value) {
    clearTimeout(updateCompleteTimer.value)
    updateCompleteTimer.value = null
    console.log('更新完成定时器已清除')
  }
  // 清除开始更新定时器
  if (updateStartTimer.value) {
    clearTimeout(updateStartTimer.value)
    updateStartTimer.value = null
    console.log('开始更新定时器已清除')
  }
  console.log('Admin组件已卸载，定时器已清理')
})
</script>

<style scoped>
.admin-container {
  display: flex;
  min-height: 100vh;
  background-color: #f5f7fa;
}

.admin-menu {
  width: 200px;
  background-color: #fff;
  border-right: 1px solid #e6e8eb;
  padding: 20px 0;
}

.admin-content {
  flex: 1;
  padding: 30px;
}

.admin-content h1 {
  color: #409EFF;
  margin-bottom: 30px;
}

.admin-content h2 {
  color: #303133;
  margin-bottom: 20px;
}

.admin-content p {
  color: #606266;
  line-height: 1.6;
}

/* 系统更新设置样式 */
.system-update-card {
  margin-bottom: 20px;
}

.update-settings {
  padding: 20px 0;
}

.update-form {
  margin-bottom: 20px;
}

.update-buttons {
  margin-top: 20px;
  display: flex;
  gap: 10px;
}

.update-message {
  margin-top: 20px;
  padding: 10px;
  border-radius: 4px;
  font-size: 14px;
}

.update-message.success {
  background-color: #f0f9eb;
  color: #67c23a;
  border: 1px solid #e1f5c4;
}

.update-message.warning {
  background-color: #fdf6ec;
  color: #e6a23c;
  border: 1px solid #faecd8;
}

.update-message.error {
  background-color: #fef0f0;
  color: #f56c6c;
  border: 1px solid #fbc4c4;
}
</style>