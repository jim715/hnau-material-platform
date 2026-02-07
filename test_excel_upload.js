// Excel上传功能测试脚本
const axios = require('axios');
const fs = require('fs');
const path = require('path');

// 测试配置
const config = {
  baseURL: 'http://localhost:8082/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
};

// 创建axios实例
const api = axios.create(config);

// 测试用户数据
const testUsers = [
  {
    studentId: '2025241889',
    name: '王五',
    grade: '2025',
    major: '软件工程',
    college: '信息科学与工程学院'
  },
  {
    studentId: '2025241890',
    name: '赵六',
    grade: '2025',
    major: '计算机科学与技术',
    college: '信息科学与工程学院'
  }
];

// 测试流程
async function testExcelUpload() {
  console.log('开始测试Excel上传功能...');

  try {
    // 1. 测试批量上传用户API
    console.log('\n1. 测试批量上传用户API:');
    console.log('测试数据:', testUsers);

    const uploadResponse = await api.post('/admin/user/batch', testUsers);
    console.log('上传响应:', uploadResponse.data);

    if (uploadResponse.data.code === 200) {
      console.log('✓ 批量上传用户成功');
    } else {
      console.log('✗ 批量上传用户失败:', uploadResponse.data.message);
      return false;
    }

    // 2. 测试登录功能
    console.log('\n2. 测试登录功能:');
    const loginResponse = await api.post('/user/login', {
      studentId: '2025241889',
      password: '123456'
    });

    console.log('登录响应:', loginResponse.data);

    if (loginResponse.data.code === 200) {
      console.log('✓ 登录成功');
      const token = loginResponse.data.data.token;
      const userInfo = loginResponse.data.data.user;
      console.log('用户信息:', userInfo);

      // 验证用户信息是否正确
      if (userInfo.name === '王五' && userInfo.college === '信息科学与工程学院' && userInfo.major === '软件工程' && userInfo.grade === '2025') {
        console.log('✓ 用户信息正确');
      } else {
        console.log('✗ 用户信息不正确');
        console.log('期望: 姓名=王五, 学院=信息科学与工程学院, 专业=软件工程, 年级=2025');
        console.log('实际: 姓名=' + userInfo.name + ', 学院=' + userInfo.college + ', 专业=' + userInfo.major + ', 年级=' + userInfo.grade);
        return false;
      }

      // 3. 测试获取用户信息API
      console.log('\n3. 测试获取用户信息API:');
      const userInfoResponse = await api.get('/user/info', {
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });

      console.log('获取用户信息响应:', userInfoResponse.data);

      if (userInfoResponse.data.code === 200) {
        console.log('✓ 获取用户信息成功');
        const apiUserInfo = userInfoResponse.data.data;
        console.log('API返回的用户信息:', apiUserInfo);

        // 验证用户信息是否正确
        if (apiUserInfo.name === '王五' && apiUserInfo.college === '信息科学与工程学院' && apiUserInfo.major === '软件工程' && apiUserInfo.grade === '2025') {
          console.log('✓ API返回的用户信息正确');
        } else {
          console.log('✗ API返回的用户信息不正确');
          console.log('期望: 姓名=王五, 学院=信息科学与工程学院, 专业=软件工程, 年级=2025');
          console.log('实际: 姓名=' + apiUserInfo.name + ', 学院=' + apiUserInfo.college + ', 专业=' + apiUserInfo.major + ', 年级=' + apiUserInfo.grade);
          return false;
        }
      } else {
        console.log('✗ 获取用户信息失败:', userInfoResponse.data.message);
        return false;
      }

    } else {
      console.log('✗ 登录失败:', loginResponse.data.message);
      return false;
    }

    console.log('\n🎉 所有测试通过，Excel上传功能正常工作！');
    return true;

  } catch (error) {
    console.error('测试失败:', error.message);
    if (error.response) {
      console.error('错误响应:', error.response.data);
    }
    return false;
  }
}

// 运行测试
testExcelUpload().then(result => {
  if (result) {
    console.log('\n测试完成，Excel上传功能正常。');
  } else {
    console.log('\n测试完成，Excel上传功能存在问题。');
  }
}).catch(error => {
  console.error('测试过程中发生错误:', error);
});