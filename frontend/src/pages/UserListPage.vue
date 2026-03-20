<template>
  <div class="page-container">
    <div class="page-header">
      <h1>用户管理</h1>
      <p>管理系统中的所有用户信息</p>
    </div>

    <!-- 搜索栏与操作按钮 -->
    <el-card class="search-card">
      <div class="toolbar">
        <div class="search-input-wrapper">
          <el-input
            v-model="keyword"
            placeholder="搜索用户名、姓名、邮箱、手机号..."
            prefix-icon="Search"
            clearable
            style="width: 320px"
            @keyup.enter="handleSearch"
            @clear="handleSearch"
          />
          <el-button
            type="primary"
            icon="Search"
            @click="handleSearch"
          >
            搜索
          </el-button>
        </div>
        <el-button
          type="primary"
          icon="Plus"
          @click="$router.push('/users/create')"
        >
          新增用户
        </el-button>
      </div>
    </el-card>

    <!-- 用户表格 -->
    <el-card class="table-card">
      <el-table
        v-loading="loading"
        :data="userList"
        stripe
        style="width: 100%"
        @row-click="handleRowClick"
      >
        <el-table-column
          prop="id"
          label="ID"
          width="80"
        />
        <el-table-column
          prop="username"
          label="用户名"
          width="120"
        />
        <el-table-column
          prop="realName"
          label="姓名"
          width="120"
        />
        <el-table-column
          prop="email"
          label="邮箱"
        />
        <el-table-column
          prop="phone"
          label="手机号"
          width="140"
        />
        <el-table-column
          prop="status"
          label="状态"
          width="100"
        >
          <template #default="{ row }">
            <el-tag
              :type="row.status === 1 ? 'success' : 'danger'"
              size="small"
            >
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column
          label="操作"
          width="200"
          fixed="right"
        >
          <template #default="{ row }">
            <el-button
              size="small"
              type="primary"
              link
              @click.stop="handleEdit(row)"
            >
              编辑
            </el-button>
            <el-button
              size="small"
              type="danger"
              link
              @click.stop="handleDelete(row)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-wrapper">
        <el-pagination
          v-model:current-page="pagination.current"
          v-model:page-size="pagination.size"
          :total="pagination.total"
          :page-sizes="[5, 10, 20, 50]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="fetchUsers"
          @current-change="fetchUsers"
        />
      </div>
    </el-card>

    <!-- 用户详情抽屉（占位，Kimi 完善） -->
    <UserDetailDrawer
      v-model="drawerVisible"
      :user-id="selectedUserId"
      @close="drawerVisible = false"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getUsers, deleteUser } from '@/api/user'
import UserDetailDrawer from '@/components/UserDetailDrawer.vue'

const router = useRouter()
const loading = ref(false)
const keyword = ref('')
const userList = ref<any[]>([])
const drawerVisible = ref(false)
const selectedUserId = ref<number | null>(null)

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

/** 获取用户列表 */
const fetchUsers = async () => {
  loading.value = true
  try {
    const res: any = await getUsers({
      current: pagination.current,
      size: pagination.size,
      keyword: keyword.value || undefined
    })
    userList.value = res.data.records
    pagination.total = res.data.total
  } catch {
    // 错误已在拦截器中处理
  } finally {
    loading.value = false
  }
}

/** 搜索按钮点击 - 重置页码并查询 */
const handleSearch = () => {
  pagination.current = 1
  fetchUsers()
}

/** 点击行查看详情 */
const handleRowClick = (row: any) => {
  selectedUserId.value = row.id
  drawerVisible.value = true
}

/** 编辑用户 */
const handleEdit = (row: any) => {
  router.push(`/users/${row.id}/edit`)
}

/** 删除用户 */
const handleDelete = async (row: any) => {
  try {
    await ElMessageBox.confirm(`确定要删除用户「${row.realName || row.username}」吗？`, '删除确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteUser(row.id)
    ElMessage.success('删除成功')
    fetchUsers()
  } catch {
    // 用户取消操作
  }
}

onMounted(() => {
  fetchUsers()
})
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

.search-card {
  margin-bottom: var(--spacing-md);
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-input-wrapper {
  display: flex;
  gap: 8px;
}

.table-card {
  margin-bottom: var(--spacing-md);
}

.pagination-wrapper {
  display: flex;
  justify-content: flex-end;
  margin-top: var(--spacing-md);
}
</style>
