import { ElMessage, ElMessageBox } from 'element-plus'
import router from '../router'

// 错误码映射
const errorMap = {
  400: '请求参数错误',
  401: '未授权，请重新登录',
  403: '拒绝访问',
  404: '请求资源不存在',
  405: '请求方法不允许',
  408: '请求超时',
  500: '服务器内部错误',
  501: '服务未实现',
  502: '网关错误',
  503: '服务不可用',
  504: '网关超时'
}

// 业务错误码映射
const businessErrorMap = {
  10001: '积分不足',
  10002: '文件过大',
  10003: '文件格式错误',
  10004: '上传失败',
  10005: '资源不存在',
  10006: '操作失败',
  10007: '重复操作',
  10008: '权限不足'
}

/**
 * 处理HTTP错误
 * @param {number} status HTTP状态码
 * @param {string} message 错误信息
 */
export const handleHttpError = (status, message) => {
  const errorMessage = errorMap[status] || message || '网络请求失败'
  
  if (status === 401) {
    // 未授权，跳转到登录页
    ElMessageBox.alert('登录已过期，请重新登录', '提示', {
      confirmButtonText: '确定',
      callback: () => {
        localStorage.removeItem('token')
        localStorage.removeItem('user')
        router.push('/login')
      }
    })
  } else {
    ElMessage.error(errorMessage)
  }
}

/**
 * 处理业务错误
 * @param {number} code 业务错误码
 * @param {string} message 错误信息
 */
export const handleBusinessError = (code, message) => {
  const errorMessage = businessErrorMap[code] || message || '操作失败'
  ElMessage.error(errorMessage)
}

/**
 * 处理请求错误
 * @param {Error} error 错误对象
 */
export const handleRequestError = (error) => {
  if (error.response) {
    // 服务器返回错误
    const { status, data } = error.response
    if (data.code && data.code >= 10000) {
      // 业务错误
      handleBusinessError(data.code, data.message)
    } else {
      // HTTP错误
      handleHttpError(status, data.message)
    }
  } else if (error.request) {
    // 请求已发送但没有收到响应
    ElMessage.error('网络连接失败，请检查网络设置')
  } else {
    // 请求配置出错
    ElMessage.error('请求配置错误')
  }
}

/**
 * 处理成功消息
 * @param {string} message 成功消息
 */
export const handleSuccessMessage = (message) => {
  ElMessage.success(message)
}

/**
 * 处理警告消息
 * @param {string} message 警告消息
 */
export const handleWarningMessage = (message) => {
  ElMessage.warning(message)
}

/**
 * 处理确认对话框
 * @param {string} title 对话框标题
 * @param {string} message 对话框内容
 * @param {Function} confirmCallback 确认回调
 * @param {Function} cancelCallback 取消回调
 */
export const handleConfirmDialog = (title, message, confirmCallback, cancelCallback) => {
  ElMessageBox.confirm(message, title, {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  })
    .then(() => {
      if (confirmCallback) confirmCallback()
    })
    .catch(() => {
      if (cancelCallback) cancelCallback()
    })
}