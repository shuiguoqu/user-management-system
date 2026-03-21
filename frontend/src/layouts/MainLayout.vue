<template>
  <div class="layout-container">
    <!-- 侧边栏 -->
    <el-aside class="sidebar" :class="{ 'is-collapsed': isCollapsed }">
      <div class="sidebar-header">
        <el-icon class="logo-icon"><DataLine /></el-icon>
        <span v-if="!isCollapsed" class="logo-text">管理系统</span>
      </div>
      <el-menu
        :default-active="activeMenu"
        class="sidebar-menu"
        :collapse="isCollapsed"
        router
      >
        <el-menu-item index="/dashboard">
          <el-icon><DataLine /></el-icon>
          <template #title>仪表盘</template>
        </el-menu-item>
        <el-menu-item index="/users">
          <el-icon><User /></el-icon>
          <template #title>用户管理</template>
        </el-menu-item>
      </el-menu>
      <div class="sidebar-footer">
        <el-button
          :icon="isCollapsed ? 'Expand' : 'Fold'"
          @click="isCollapsed = !isCollapsed"
          circle
          size="small"
        />
      </div>
    </el-aside>

    <!-- 主内容区 -->
    <el-container class="main-container">
      <!-- 顶部导航栏 -->
      <el-header class="header">
        <div class="header-left">
          <h2 class="page-title">{{ currentPageTitle }}</h2>
        </div>
        <div class="header-right">
          <el-dropdown @command="handleDropdownCommand">
            <div class="user-info">
              <el-icon class="user-avatar"><User /></el-icon>
              <span class="username">{{ realName }}</span>
              <el-icon class="arrow-icon"><CaretBottom /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人信息</el-dropdown-item>
                <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <!-- 页面内容 -->
      <el-main class="content-area">
        <router-view />
      </el-main>
    </el-container>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  DataLine,
  User,
  Fold,
  Expand,
  CaretBottom
} from '@element-plus/icons-vue'

const router = useRouter()
const route = useRoute()

const isCollapsed = ref(false)
const realName = ref(localStorage.getItem('realName') || '管理员')

// 当前激活的菜单
const activeMenu = computed(() => {
  return route.path
})

// 当前页面标题
const currentPageTitle = computed(() => {
  const path = route.path
  if (path === '/dashboard' || path === '/') return '数据仪表盘'
  if (path.startsWith('/users')) return '用户管理'
  return '管理系统'
})

// 处理下拉菜单命令
const handleDropdownCommand = async (command: string) => {
  if (command === 'logout') {
    try {
      await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
      localStorage.removeItem('token')
      localStorage.removeItem('username')
      localStorage.removeItem('realName')
      ElMessage.success('退出成功')
      router.push('/login')
    } catch {
      // 用户取消操作
    }
  } else if (command === 'profile') {
    ElMessage.info('个人信息功能开发中...')
  }
}
</script>

<style scoped>
.layout-container {
  display: flex;
  height: 100vh;
  overflow: hidden;
}

/* 侧边栏样式 */
.sidebar {
  width: 240px;
  background: linear-gradient(180deg, #1e293b 0%, #0f172a 100%);
  display: flex;
  flex-direction: column;
  transition: width 0.3s ease;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.1);
}

.sidebar.is-collapsed {
  width: 64px;
}

.sidebar-header {
  display: flex;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.logo-icon {
  font-size: 28px;
  color: #409eff;
}

.logo-text {
  margin-left: 12px;
  font-size: 18px;
  font-weight: 600;
  color: #fff;
  white-space: nowrap;
}

.sidebar-menu {
  flex: 1;
  border-right: none;
  background: transparent;
  padding-top: 12px;
}

.sidebar-menu :deep(.el-menu-item) {
  height: 50px;
  line-height: 50px;
  margin: 4px 12px;
  border-radius: 8px;
  color: rgba(255, 255, 255, 0.7);
  transition: all 0.2s;
}

.sidebar-menu :deep(.el-menu-item:hover) {
  background-color: rgba(64, 158, 255, 0.2);
  color: #fff;
}

.sidebar-menu :deep(.el-menu-item.is-active) {
  background-color: #409eff;
  color: #fff;
}

.sidebar-menu :deep(.el-icon) {
  font-size: 20px;
}

.sidebar-footer {
  padding: 16px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  display: flex;
  justify-content: center;
}

.sidebar-footer :deep(.el-button) {
  background: rgba(255, 255, 255, 0.1);
  border: none;
  color: rgba(255, 255, 255, 0.7);
}

.sidebar-footer :deep(.el-button:hover) {
  background: rgba(255, 255, 255, 0.2);
  color: #fff;
}

/* 主内容区 */
.main-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  background-color: #f5f7fa;
}

/* 顶部导航栏 */
.header {
  height: 64px;
  background: #fff;
  border-bottom: 1px solid #e5e7eb;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);
}

.page-title {
  font-size: 18px;
  font-weight: 600;
  color: #1f2937;
  margin: 0;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  border-radius: 8px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.user-info:hover {
  background-color: #f3f4f6;
}

.user-avatar {
  font-size: 20px;
  color: #6b7280;
}

.username {
  font-size: 14px;
  color: #374151;
  font-weight: 500;
}

.arrow-icon {
  font-size: 12px;
  color: #9ca3af;
}

/* 内容区域 */
.content-area {
  flex: 1;
  overflow: auto;
  padding: 0;
  background-color: #f5f7fa;
}
</style>