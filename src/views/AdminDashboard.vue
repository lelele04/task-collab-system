<template>
  <div class="dashboard-container">
    <el-container>
      <el-header class="header">
        <div class="header-left">
          <h2>团队任务协作系统 - 管理员</h2>
        </div>
        <div class="header-right">
          <span class="username">{{ currentUser.username }} (管理员)</span>
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
            <el-menu-item index="/admin/dashboard">
              <el-icon :icon="HomeFilledIcon" />
              <span>管理首页</span>
            </el-menu-item>
            <el-menu-item index="/admin/users">
              <el-icon :icon="UserIcon" />
              <span>用户管理</span>
            </el-menu-item>
            <el-menu-item index="/admin/teams">
              <el-icon :icon="OfficeBuildingIcon" />
              <span>团队管理</span>
            </el-menu-item>
            <el-menu-item index="/admin/tasks">
              <el-icon :icon="DocumentIcon" />
              <span>任务管理</span>
            </el-menu-item>
            <el-menu-item index="/admin/profile">
              <el-icon :icon="UserIcon" />
              <span>个人信息</span>
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
import { ref, computed, markRaw } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { HomeFilled, User, OfficeBuilding, Document } from '@element-plus/icons-vue'

export default {
  name: 'AdminDashboard',
  setup() {
    const HomeFilledIcon = markRaw(HomeFilled)
    const UserIcon = markRaw(User)
    const OfficeBuildingIcon = markRaw(OfficeBuilding)
    const DocumentIcon = markRaw(Document)
    
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
      HomeFilledIcon,
      UserIcon,
      OfficeBuildingIcon,
      DocumentIcon,
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
  background-color: #e74c3c;
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