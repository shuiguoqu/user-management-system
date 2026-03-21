<template>
  <div class="page-container">
    <div class="page-header">
      <h1>订单管理</h1>
      <p>管理系统中的所有订单信息</p>
    </div>

    <el-card class="table-card">
      <el-empty description="订单管理功能开发中..." />
    </el-card>
  </div>
</template>

<script setup lang="ts">
// 订单管理页面 - 占位
</script>

<style scoped>
.page-container {
  padding: var(--spacing-lg);
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  margin-bottom: var(--spacing-lg);
}

.page-header h1 {
  font-size: 28px;
  color: var(--text-primary);
  margin-bottom: 4px;
}

.page-header p {
  font-size: 14px;
  color: var(--text-secondary);
}

.table-card {
  margin-bottom: var(--spacing-md);
  min-height: 400px;
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>
