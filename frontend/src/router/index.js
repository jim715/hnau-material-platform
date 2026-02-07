import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/Register.vue'),
    meta: { requiresAuth: false }
  },
  // 主布局，包含左侧菜单
  {
    path: '/',
    component: () => import('../components/Layout.vue'),
    meta: { requiresAuth: true },
    children: [
      {
        path: '',
        redirect: '/home'
      },
      {
        path: 'home',
        name: 'Home',
        component: () => import('../views/Home.vue')
      },
      {
        path: 'upload',
        name: 'Upload',
        component: () => import('../views/Upload.vue')
      },
      {
        path: 'user',
        name: 'User',
        component: () => import('../views/User.vue')
      },
      {
        path: 'material/:id',
        name: 'MaterialDetail',
        component: () => import('../views/MaterialDetail.vue')
      },
      // 积分管理模块
      {
        path: 'integral/rules',
        name: 'IntegralRules',
        component: () => import('../views/IntegralRules.vue')
      },
      {
        path: 'integral/center',
        name: 'IntegralCenter',
        component: () => import('../views/IntegralCenter.vue')
      },
      {
        path: 'integral/rank',
        name: 'IntegralRank',
        component: () => import('../views/IntegralRank.vue')
      },
      // 互动模块
      {
        path: 'question/add',
        name: 'QuestionAdd',
        component: () => import('../views/QuestionAdd.vue')
      },
      {
        path: 'question/list',
        name: 'QuestionList',
        component: () => import('../views/QuestionList.vue')
      },
      {
        path: 'question/:id',
        name: 'QuestionDetail',
        component: () => import('../views/QuestionDetail.vue')
      },
      {
        path: 'search',
        name: 'SearchResult',
        component: () => import('../views/SearchResult.vue')
      },
      // 管理员页面
      {
        path: 'admin',
        name: 'Admin',
        component: () => import('../views/Admin.vue')
      },
      // Excel测试页面
      {
        path: 'excel-test',
        name: 'ExcelTest',
        component: () => import('../views/ExcelTest.vue')
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  const requiresAuth = to.matched.some(record => record.meta.requiresAuth)
  const isAuthenticated = localStorage.getItem('token') !== null

  if (requiresAuth && !isAuthenticated) {
    // 重定向到登录页面
    next('/login')
  } else {
    next()
  }
})

export default router