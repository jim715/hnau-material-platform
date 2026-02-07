<template>
  <div class="excel-test-container">
    <h1>Excel上传测试</h1>
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
        选择Excel文件
      </el-button>
    </el-upload>
    <el-button type="success" @click="testExcelParse" :disabled="!uploadedFile">
      测试解析
    </el-button>
    <el-button type="warning" @click="uploadUsers" :disabled="!uploadedFile">
      上传用户
    </el-button>
    <el-alert
      v-if="message"
      :title="message"
      :type="messageType"
      show-icon
      class="mt-4"
    />
    <div v-if="parsedData.length > 0" class="parsed-data">
      <h2>解析结果</h2>
      <el-table :data="parsedData" style="width: 100%">
        <el-table-column prop="studentId" label="学号"></el-table-column>
        <el-table-column prop="name" label="姓名"></el-table-column>
        <el-table-column prop="grade" label="年级"></el-table-column>
        <el-table-column prop="major" label="专业"></el-table-column>
        <el-table-column prop="college" label="学院"></el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { Upload } from '@element-plus/icons-vue'
import * as XLSX from 'xlsx'
import api from '../api/index.js'

const uploadedFile = ref(null)
const message = ref('')
const messageType = ref('info')
const parsedData = ref([])

const handleFileChange = (file, fileList) => {
  uploadedFile.value = file.raw
  message.value = ''
  parsedData.value = []
}

const testExcelParse = async () => {
  if (!uploadedFile.value) {
    message.value = '请先选择Excel文件'
    messageType.value = 'warning'
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
      
      const jsonData = XLSX.utils.sheet_to_json(worksheet)
      console.log('转换为JSON数据成功，行数:', jsonData.length)
      console.log('JSON数据:', jsonData)

      // 转换数据格式
      const users = jsonData.map(item => {
        return {
          studentId: item.studentId || item.学号 || '',
          name: item.name || item.姓名 || '',
          grade: item.grade || item.年级 || '',
          major: item.major || item.专业 || '',
          college: item.college || item.学院 || '',
          className: item.className || item.class || item.班级 || ''
        }
      }).filter(user => user.studentId && user.name)

      console.log('转换后的用户数据:', users)
      parsedData.value = users

      message.value = `解析成功，共解析出 ${users.length} 条用户数据`
      messageType.value = 'success'
    } catch (error) {
      console.error('解析Excel文件失败:', error)
      message.value = '解析Excel文件失败，请检查文件格式'
      messageType.value = 'error'
    }
  }
  
  reader.onerror = (error) => {
    console.error('文件读取失败:', error)
    message.value = '文件读取失败，请检查文件'
    messageType.value = 'error'
  }
  
  reader.readAsArrayBuffer(uploadedFile.value)
}

const uploadUsers = async () => {
  if (!uploadedFile.value) {
    message.value = '请先选择Excel文件'
    messageType.value = 'warning'
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
      
      const jsonData = XLSX.utils.sheet_to_json(worksheet)
      console.log('转换为JSON数据成功，行数:', jsonData.length)
      console.log('JSON数据:', jsonData)

      // 转换数据格式
      const users = jsonData.map(item => {
        return {
          studentId: item.studentId || item.学号 || '',
          name: item.name || item.姓名 || '',
          grade: item.grade || item.年级 || '',
          major: item.major || item.专业 || '',
          college: item.college || item.学院 || '',
          className: item.className || item.class || item.班级 || ''
        }
      }).filter(user => user.studentId && user.name)

      console.log('转换后的用户数据:', users)
      parsedData.value = users

      if (users.length === 0) {
        message.value = 'Excel文件中没有有效的学生信息'
        messageType.value = 'warning'
        return
      }

      // 发送批量创建用户请求
      console.log('开始发送批量创建用户请求')
      const response = await api.post('/api/admin/user/batch', users)
      console.log('批量创建用户请求成功，响应:', response)
      
      if (response.code === 200) {
        message.value = '学生信息上传成功'
        messageType.value = 'success'
      } else {
        message.value = '学生信息上传失败: ' + (response.message || '未知错误')
        messageType.value = 'error'
      }
    } catch (error) {
      console.error('解析Excel文件失败:', error)
      message.value = '解析Excel文件失败，请检查文件格式'
      messageType.value = 'error'
    }
  }
  
  reader.onerror = (error) => {
    console.error('文件读取失败:', error)
    message.value = '文件读取失败，请检查文件'
    messageType.value = 'error'
  }
  
  reader.readAsArrayBuffer(uploadedFile.value)
}
</script>

<style scoped>
.excel-test-container {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.excel-test-container h1 {
  color: #409EFF;
  margin-bottom: 30px;
}

.excel-test-container h2 {
  color: #303133;
  margin: 20px 0;
}

.user-upload {
  margin-bottom: 20px;
}

.parsed-data {
  margin-top: 30px;
}
</style>