<template>
  <div class="upload-file">
    <el-upload
      class="upload-demo"
      :action="uploadUrl"
      :headers="headers"
      :on-success="handleSuccess"
      :on-error="handleError"
      :before-upload="beforeUpload"
      :file-list="fileList"
      :multiple="multiple"
      :limit="limit"
      :on-exceed="handleExceed"
      :auto-upload="autoUpload"
    >
      <template v-if="!autoUpload">
        <el-button type="primary">
          <el-icon><Upload /></el-icon>
          {{ buttonText }}
        </el-button>
      </template>
      <template v-else>
        <el-button type="primary">
          <el-icon><Upload /></el-icon>
          {{ buttonText }}
        </el-button>
      </template>
      <template #tip>
        <div class="el-upload__tip">
          {{ fileTypes.join('、') }}，单个文件不超过 {{ maxSize }}MB，最多上传 {{ limit }} 个文件
        </div>
      </template>
    </el-upload>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { Upload } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const props = defineProps({
  modelValue: {
    type: Array,
    default: () => []
  },
  uploadUrl: {
    type: String,
    default: '/api/material/upload'
  },
  multiple: {
    type: Boolean,
    default: false
  },
  limit: {
    type: Number,
    default: 1
  },
  maxSize: {
    type: Number,
    default: 50
  },
  fileTypes: {
    type: Array,
    default: () => ['PDF', 'Word', 'Excel', 'PPT', '图片']
  },
  buttonText: {
    type: String,
    default: '点击上传'
  },
  autoUpload: {
    type: Boolean,
    default: true
  }
})

const emit = defineEmits(['update:modelValue'])
const fileList = ref([...props.modelValue])

// 获取认证头
const headers = computed(() => {
  const token = localStorage.getItem('token')
  return {
    Authorization: `Bearer ${token}`
  }
})

// 上传前验证
const beforeUpload = (file) => {
  const isAccept = validateFileType(file.name)
  const isLtSize = file.size / 1024 / 1024 < props.maxSize

  if (!isAccept) {
    ElMessage.error(`只支持 ${props.fileTypes.join('、')} 格式的文件!`)
    return false
  }
  if (!isLtSize) {
    ElMessage.error(`文件大小不能超过 ${props.maxSize}MB!`)
    return false
  }

  return true
}

// 验证文件类型
const validateFileType = (fileName) => {
  const ext = fileName.split('.').pop().toLowerCase()
  const allowedExts = {
    'PDF': ['pdf'],
    'Word': ['doc', 'docx'],
    'Excel': ['xls', 'xlsx'],
    'PPT': ['ppt', 'pptx'],
    '图片': ['jpg', 'jpeg', 'png', 'gif']
  }

  return props.fileTypes.some(type => {
    return allowedExts[type]?.includes(ext)
  })
}

// 上传成功处理
const handleSuccess = (response, file, fileList) => {
  if (response.code === 200) {
    const newFile = {
      name: file.name,
      url: response.data,
      size: file.size
    }
    fileList.value = [...fileList.value, newFile]
    emit('update:modelValue', fileList.value)
    ElMessage.success('文件上传成功')
  } else {
    ElMessage.error('文件上传失败')
  }
}

// 上传失败处理
const handleError = () => {
  ElMessage.error('文件上传失败')
}

// 文件超出限制处理
const handleExceed = (files, fileList) => {
  ElMessage.warning(`最多只能上传 ${props.limit} 个文件`)
}
</script>

<style scoped>
.upload-file {
  margin: 10px 0;
}

.upload-demo {
  width: 100%;
}

.el-upload__tip {
  font-size: 12px;
  color: #999;
}
</style>