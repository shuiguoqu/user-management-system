import { createRouter, createWebHistory } from 'vue-router'
import MainLayout from '@/layouts/MainLayout.vue'

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
      component: MainLayout,
      redirect: '/dashboard',
      children: [
        {
          path: 'dashboard',
          name: 'Dashboard',
          component: () => import('@/pages/DashboardPage.vue'),
          meta: { requiresAuth: true }
        },
        {
          path: 'users',
          name: 'UserList',
          component: () => import('@/pages/UserListPage.vue'),
          meta: { requiresAuth: true }
        },
        {
          path: 'users/create',
          name: 'UserCreate',
          component: () => import('@/pages/UserFormPage.vue'),
          meta: { requiresAuth: true }
        },
        {
          path: 'users/:id/edit',
          name: 'UserEdit',
          component: () => import('@/pages/UserFormPage.vue'),
          meta: { requiresAuth: true }
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
