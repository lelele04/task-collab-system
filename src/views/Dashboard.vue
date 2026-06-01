<template>
  <div class="dashboard-container">
    <el-container>
      <el-header class="header">
        <div class="header-left">
          <h2>团队任务协作系统</h2>
        </div>
        <div class="header-right">
          <span class="username">{{ currentUser.username }}</span>
          <el-button type="danger" size="small" @click="handleLogout">退出登录</el-button>
        </div>
      </el-header>

      <el-container>
        <el-aside width="200px" class="aside">
          <el-menu
            :default-active="activeMenu"
            router
            class="menu"
          >
            <el-menu-item index="/dashboard">
              <el-icon><HomeFilled /></el-icon>
              <span>首页</span>
            </el-menu-item>
            <el-menu-item index="/users">
              <el-icon><User /></el-icon>
              <span>用户管理</span>
            </el-menu-item>
            <el-menu-item index="/teams">
              <el-icon><OfficeBuilding /></el-icon>
              <span>团队管理</span>
            </el-menu-item>
            <el-menu-item index="/tasks">
              <el-icon><Document /></el-icon>
              <span>任务管理</span>
            </el-menu-item>
          </el-menu>
        </el-aside>

        <el-main class="main">
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script>
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { HomeFilled, User, OfficeBuilding, Document } from '@element-plus/icons-vue'

export default {
  name: 'Dashboard',
  components: {
    HomeFilled,
    User,
    OfficeBuilding,
    Document
  },
  setup() {
    const router = useRouter()
    const route = useRoute()

    const currentUser = computed(() => {
      const user = localStorage.getItem('user')
      return user ? JSON.parse(user) : { username: '未知用户' }
    })

    const activeMenu = computed(() => route.path)

    const handleLogout = () => {
      ElMessageBox.confirm('确定要退出登录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        localStorage.removeItem('token')
        localStorage.removeItem('user')
        ElMessage.success('已退出登录')
        router.push('/login')
      }).catch(() => {})
    }

    return {
      currentUser,
      activeMenu,
      handleLogout
    }
  }
}
</script>

<style scoped>
.dashboard-container {
  height: 100vh;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: #409eff;
  color: white;
  padding: 0 20px;
}

.header h2 {
  margin: 0;
  font-size: 20px;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 15px;
}

.username {
  font-size: 14px;
}

.aside {
  background-color: #f5f5f5;
  padding-top: 20px;
}

.menu {
  border: none;
  background-color: transparent;
}

.main {
  background-color: #fff;
  padding: 20px;
}
</style>