<template>
  <div class="layout-container">
    <aside class="sidebar">
      <div class="sidebar-header">
        <h1 class="logo">
          用户管理系统
        </h1>
        <p class="subtitle">
          Oatmeal Latte
        </p>
      </div>

      <el-menu
        :default-active="activeMenu"
        class="sidebar-menu"
        router
      >
        <el-menu-item index="/dashboard">
          <el-icon><DataLine /></el-icon>
          <span>数据仪表盘</span>
        </el-menu-item>
        <el-menu-item index="/users">
          <el-icon><User /></el-icon>
          <span>用户管理</span>
        </el-menu-item>
      </el-menu>

      <div class="sidebar-footer">
        <div class="user-info">
          <el-icon :size="20">
            <UserFilled />
          </el-icon>
          <span class="username">{{ username }}</span>
        </div>
        <el-button
          type="danger"
          text
          size="small"
          @click="handleLogout"
        >
          <el-icon><SwitchButton /></el-icon>
          退出登录
        </el-button>
      </div>
    </aside>

    <main class="main-content">
      <slot />
    </main>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { DataLine, User, UserFilled, SwitchButton } from '@element-plus/icons-vue'
import { ElMessageBox } from 'element-plus'

const route = useRoute()
const router = useRouter()

const activeMenu = computed(() => {
  if (route.path.startsWith('/users')) {
    return '/users'
  }
  return route.path
})

const username = computed(() => {
  return localStorage.getItem('realName') || localStorage.getItem('username') || '用户'
})

const handleLogout = async () => {
  try {
    await ElMessageBox.confirm('确定要退出登录吗？', '退出确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    localStorage.removeItem('token')
    localStorage.removeItem('username')
    localStorage.removeItem('realName')
    router.push('/login')
  } catch {
    // 用户取消
  }
}
</script>

<style scoped>
.layout-container {
  display: flex;
  min-height: 100vh;
  background-color: var(--bg-primary);
}

.sidebar {
  width: 240px;
  background-color: var(--bg-surface);
  border-right: 1px solid var(--border-color);
  display: flex;
  flex-direction: column;
  flex-shrink: 0;
}

.sidebar-header {
  padding: var(--spacing-lg);
  border-bottom: 1px solid var(--border-color);
}

.logo {
  font-size: 18px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 4px;
}

.subtitle {
  font-size: 12px;
  color: var(--text-secondary);
}

.sidebar-menu {
  flex: 1;
  border-right: none !important;
  background-color: transparent;
  padding: var(--spacing-sm) 0;
}

.sidebar-menu :deep(.el-menu-item) {
  height: 48px;
  line-height: 48px;
  margin: 4px var(--spacing-sm);
  border-radius: var(--radius-sm);
  color: var(--text-primary);
}

.sidebar-menu :deep(.el-menu-item:hover) {
  background-color: rgba(13, 148, 136, 0.08);
}

.sidebar-menu :deep(.el-menu-item.is-active) {
  background-color: rgba(13, 148, 136, 0.12);
  color: var(--color-interactive);
  font-weight: 500;
}

.sidebar-menu :deep(.el-menu-item .el-icon) {
  font-size: 18px;
  margin-right: 8px;
}

.sidebar-footer {
  padding: var(--spacing-md);
  border-top: 1px solid var(--border-color);
}

.user-info {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  margin-bottom: var(--spacing-sm);
  color: var(--text-primary);
}

.username {
  font-size: 14px;
}

.main-content {
  flex: 1;
  overflow-y: auto;
}
</style>
