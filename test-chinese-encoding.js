// 测试中文编码的脚本
const axios = require('axios');

// 创建axios实例
const api = axios.create({
  baseURL: 'http://localhost:8082',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json;charset=UTF-8'
  }
});

// 测试用户列表API
async function testUserList() {
  console.log('开始测试用户列表API...');
  try {
    // 首先尝试登录获取token
    const loginResponse = await api.post('/api/user/login', {
      studentId: 'admin',
      password: '123456'
    });

    console.log('登录响应:', loginResponse.data);

    if (loginResponse.data.code === 200 && loginResponse.data.data && loginResponse.data.data.token) {
      const token = loginResponse.data.data.token;
      console.log('获取到token:', token);

      // 使用token调用用户列表API
      api.defaults.headers.common['Authorization'] = `Bearer ${token}`;

      const userListResponse = await api.get('/api/admin/user/list');
      console.log('用户列表响应:', JSON.stringify(userListResponse.data, null, 2));

      if (userListResponse.data.code === 200 && userListResponse.data.data) {
        const users = userListResponse.data.data;
        console.log('\n=== 测试结果 ===');
        console.log(`共获取到 ${users.length} 个用户`);

        users.forEach((user, index) => {
          console.log(`\n用户 ${index + 1}:`);
          console.log(`学号: ${user.studentId}`);
          console.log(`姓名: ${user.name}`);
          console.log(`学院: ${user.college}`);
          console.log(`专业: ${user.major}`);
          console.log(`班级: ${user.className}`);
          console.log(`年级: ${user.grade}`);

          // 检查中文字符是否正确显示
          if (user.name && user.name.includes('?')) {
            console.log('❌ 姓名显示异常（包含问号）');
          } else {
            console.log('✅ 姓名显示正常');
          }

          if (user.college && user.college.includes('?')) {
            console.log('❌ 学院显示异常（包含问号）');
          } else {
            console.log('✅ 学院显示正常');
          }

          if (user.major && user.major.includes('?')) {
            console.log('❌ 专业显示异常（包含问号）');
          } else {
            console.log('✅ 专业显示正常');
          }
        });

        console.log('\n=== 测试完成 ===');
      } else {
        console.error('获取用户列表失败:', userListResponse.data);
      }
    } else {
      console.error('登录失败:', loginResponse.data);
    }
  } catch (error) {
    console.error('测试过程中发生错误:', error.message);
    if (error.response) {
      console.error('错误响应:', error.response.data);
    }
  }
}

// 运行测试
testUserList();
