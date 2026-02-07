<template>
  <div class="login-container">
    <div class="login-form">
      <h2>河南农业大学期末资料共享平台</h2>
      <el-form :model="loginForm" :rules="rules" ref="loginFormRef" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="loginForm.username" placeholder="请输入学号"></el-input>
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="loginForm.password" type="password" placeholder="请输入密码"></el-input>
        </el-form-item>
        <el-form-item>
          <el-checkbox v-model="loginForm.remember" style="margin-right: 20px;">记住账号</el-checkbox>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleLogin" style="width: 100%;">登录</el-button>
        </el-form-item>
        <el-form-item>
          <el-button type="info" @click="switchToRegister" style="width: 100%;">注册</el-button>
        </el-form-item>
      </el-form>
      <div v-if="errorMessage" class="error-message">{{ errorMessage }}</div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import api from '../api/index.js'

const router = useRouter()
const loginFormRef = ref(null)
const errorMessage = ref('')
const loginForm = reactive({
  username: '',
  password: '',
  remember: false
})

// 验证规则
const rules = {
  username: [
    { required: true, message: '请输入学号', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ]
}

// 登录处理
const handleLogin = async () => {
  if (!loginFormRef.value) return
  
  console.log('开始登录处理...')
  console.log('登录表单数据:', loginForm)
  
  try {
    // 直接验证表单，不使用validate的回调形式
    const valid = await loginFormRef.value.validate();
    console.log('表单验证结果:', valid)
    
    if (valid) {
      console.log('表单验证通过，准备发送登录请求...')
      console.log('登录请求参数:', {
        studentId: loginForm.username,
        password: loginForm.password
      })
      
      try {
        // 使用api实例发送登录请求
        console.log('发送登录请求到 /user/login')
        console.log('API实例:', api)
        console.log('API基础URL:', api.defaults.baseURL)
        
        const response = await api.post('/user/login', {
          studentId: loginForm.username,
          password: loginForm.password
        });
        
        console.log('登录请求成功，响应数据:', response)
        
        if (response.code === 200) {
          console.log('Login response data:', response.data)
          console.log('User object:', response.data.user)
          console.log('User role:', response.data.user.role)
          
          // 登录成功，保存用户信息并跳转到相应页面
          localStorage.setItem('token', response.data.token)
          localStorage.setItem('user', JSON.stringify(response.data.user))
          console.log('Stored user in localStorage:', JSON.stringify(response.data.user))
          if (loginForm.remember) {
            localStorage.setItem('rememberedUsername', loginForm.username)
          } else {
            localStorage.removeItem('rememberedUsername')
          }
          errorMessage.value = ''
          
          // 根据用户角色跳转不同页面
          if (response.data.user.role === 1) {
            console.log('Admin user, redirecting to /admin')
            // 管理员跳转到管理员页面
            router.push('/admin')
          } else {
            console.log('Regular user, redirecting to /home')
            // 普通用户跳转到首页
            router.push('/home')
          }
          
          // 强制刷新用户信息，确保获取最新数据
          setTimeout(async () => {
            try {
              const userInfoResponse = await api.get('/user/info')
              if (userInfoResponse.code === 200 && userInfoResponse.data) {
                console.log('强制刷新用户信息成功:', userInfoResponse.data)
                localStorage.setItem('user', JSON.stringify(userInfoResponse.data))
                console.log('已更新localStorage中的用户信息')
              }
            } catch (error) {
              console.error('强制刷新用户信息失败:', error)
            }
          }, 500)
        } else {
          console.log('登录失败，响应消息:', response.message)
          // 检查是否是系统更新导致的登录失败
          if (response.message && response.message.includes('系统正在更新中')) {
            console.log('系统正在更新中，显示更新提示...')
            errorMessage.value = '登录请求失败，请稍后重试'
          } else {
            errorMessage.value = response.message || '登录失败'
          }
        }
      } catch (error) {
        console.error('登录请求失败:', error)
        console.error('错误信息:', error.message)
        console.error('错误堆栈:', error.stack)
        if (error.response) {
          console.error('错误响应数据:', error.response.data)
          console.error('错误响应状态:', error.response.status)
          console.error('错误响应头:', error.response.headers)
          if (error.response.data && error.response.data.message) {
            // 检查是否是系统更新导致的登录失败
            if (error.response.data.message.includes('系统正在更新中')) {
              console.log('系统正在更新中，显示更新提示...')
              errorMessage.value = '登录请求失败，请稍后重试'
            } else {
              errorMessage.value = error.response.data.message
            }
          } else {
            errorMessage.value = '登录请求失败，请稍后重试'
          }
        } else if (error.request) {
          console.error('错误请求:', error.request)
          errorMessage.value = '登录请求失败，请稍后重试'
        } else {
          errorMessage.value = '登录请求失败，请稍后重试'
        }
      }
    } else {
      console.log('表单验证失败')
    }
  } catch (error) {
    console.error('表单验证失败:', error)
    errorMessage.value = '表单验证失败，请检查输入'
  }
  
  console.log('登录处理完成')
}

// 切换到注册页
const switchToRegister = () => {
  router.push('/register')
}

// 初始化
onMounted(() => {
  // 加载记住的账号
  const rememberedUsername = localStorage.getItem('rememberedUsername')
  if (rememberedUsername) {
    loginForm.username = rememberedUsername
    loginForm.remember = true
  }
})
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #f5f7fa;
}

.login-form {
  width: 400px;
  padding: 30px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.login-form h2 {
  text-align: center;
  margin-bottom: 30px;
  color: #409EFF;
}

.error-message {
  margin-top: 15px;
  color: #F56C6C;
  text-align: center;
}
</style>