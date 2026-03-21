import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/login',
      name: 'Login',
      component: () => import('@/pages/LoginPage.vue'),
      meta: { requiresAuth: false }
    },
    {
      path: '/',
      component: () => import('@/components/Layout.vue'),
      redirect: '/dashboard',
      meta: { requiresAuth: true },
      children: [
        {
          path: '/dashboard',
          name: 'Dashboard',
          component: () => import('@/pages/DashboardPage.vue'),
          meta: { requiresAuth: true, title: '数据仪表盘' }
        },
        {
          path: '/users',
          name: 'UserList',
          component: () => import('@/pages/UserListPage.vue'),
          meta: { requiresAuth: true, title: '用户管理' }
        },
        {
          path: '/users/create',
          name: 'UserCreate',
          component: () => import('@/pages/UserFormPage.vue'),
          meta: { requiresAuth: true, title: '新增用户' }
        },
        {
          path: '/users/:id/edit',
          name: 'UserEdit',
          component: () => import('@/pages/UserFormPage.vue'),
          meta: { requiresAuth: true, title: '编辑用户' }
        },
        {
          path: '/orders',
          name: 'OrderList',
          component: () => import('@/pages/OrderListPage.vue'),
          meta: { requiresAuth: true, title: '订单管理' }
        }
      ]
    }
  ]
})

// 路由守卫 - 鉴权检查
router.beforeEach((to, _from, next) => {
  const token = localStorage.getItem('token')
  if (to.meta.requiresAuth && !token) {
    next('/login')
  } else if (to.path === '/login' && token) {
    next('/dashboard')
  } else {
    next()
  }
})

export default router
