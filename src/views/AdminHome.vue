<template>
  <div class="admin-home">
    <el-card class="welcome-card">
      <h2>欢迎回来，管理员！</h2>
      <p class="description">这是系统管理控制台，您可以管理用户、团队和任务。</p>
    </el-card>

    <div class="stats-row">
      <el-popover
        v-model:visible="userPopoverVisible"
        placement="bottom-start"
        width="300"
        trigger="hover"
      >
        <template #reference>
          <el-card class="stat-card stat-hoverable">
            <div class="stat-icon user-icon">
              <el-icon :icon="UserIcon" />
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ userCount }}</div>
              <div class="stat-label">用户总数</div>
            </div>
          </el-card>
        </template>
        <div class="popover-content">
          <h4>用户列表</h4>
          <div class="popover-list">
            <div v-for="user in userList" :key="user.id" class="popover-item">
              <div class="item-main">{{ user.username }}</div>
              <div class="item-secondary">
                <el-tag :type="user.role === 'ADMIN' ? 'danger' : 'success'" size="small">{{ user.role === 'ADMIN' ? '管理员' : '普通用户' }}</el-tag>
                <span>{{ formatDate(user.createTime) }}</span>
              </div>
            </div>
          </div>
        </div>
      </el-popover>

      <el-popover
        v-model:visible="teamPopoverVisible"
        placement="top-start"
        width="200"
        trigger="hover"
      >
        <template #reference>
          <el-card class="stat-card stat-hoverable">
            <div class="stat-icon team-icon">
              <el-icon :icon="OfficeBuildingIcon" />
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ teamCount }}</div>
              <div class="stat-label">团队总数</div>
            </div>
          </el-card>
        </template>
        <div class="popover-content">
          <h4>团队列表</h4>
          <div class="popover-list">
            <div v-for="team in teamList" :key="team.id" class="popover-item">
              <div class="item-main">{{ team.teamName }}</div>
            </div>
          </div>
        </div>
      </el-popover>

      <el-popover
        v-model:visible="taskPopoverVisible"
        placement="bottom-start"
        width="300"
        trigger="hover"
      >
        <template #reference>
          <el-card class="stat-card stat-hoverable">
            <div class="stat-icon task-icon">
              <el-icon :icon="DocumentIcon" />
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ taskCount }}</div>
              <div class="stat-label">任务总数</div>
            </div>
          </el-card>
        </template>
        <div class="popover-content">
          <h4>最近任务</h4>
          <div class="popover-list">
            <div v-for="task in recentTaskList" :key="task.id" class="popover-item">
              <div class="item-main">{{ task.taskName }}</div>
              <div class="item-secondary">
                <el-tag :type="getStatusType(task.status)" size="small">{{ getStatusText(task.status) }}</el-tag>
                <span>{{ formatDate(task.createTime) }}</span>
              </div>
            </div>
          </div>
        </div>
      </el-popover>

      <el-popover
        v-model:visible="pendingPopoverVisible"
        placement="bottom-start"
        width="300"
        trigger="hover"
      >
        <template #reference>
          <el-card class="stat-card stat-hoverable">
            <div class="stat-icon pending-icon">
              <el-icon :icon="ClockIcon" />
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ pendingCount }}</div>
              <div class="stat-label">待处理任务</div>
            </div>
          </el-card>
        </template>
        <div class="popover-content">
          <h4>待处理任务</h4>
          <div class="popover-list">
            <div v-for="task in pendingTasks" :key="task.id" class="popover-item">
              <div class="item-main">{{ task.taskName }}</div>
              <div class="item-secondary">
                <el-tag type="warning" size="small">{{ getPriorityText(task.priority) }}优先级</el-tag>
                <span>{{ formatDate(task.deadline) }}</span>
              </div>
            </div>
          </div>
        </div>
      </el-popover>
    </div>

    <el-card class="quick-actions">
      <h3>快捷操作</h3>
      <div class="actions-grid">
        <el-button class="action-btn" type="primary" :icon="HomeIcon" @click="navigateTo('/admin/dashboard')">管理首页</el-button>
        <el-button class="action-btn" type="success" :icon="UserFilled" @click="navigateTo('/admin/users')">用户管理</el-button>
        <el-button class="action-btn" type="warning" :icon="OfficeBuildingIcon" @click="navigateTo('/admin/teams')">团队管理</el-button>
        <el-button class="action-btn" type="info" :icon="DocumentIcon" @click="navigateTo('/admin/tasks')">任务管理</el-button>
        <el-button class="action-btn" type="danger" :icon="MessageIcon" @click="navigateTo('/admin/teams?tab=applications')">团队申请</el-button>
        <el-button class="action-btn" type="primary" :icon="CheckIcon" @click="navigateTo('/admin/tasks?tab=review')">任务审核</el-button>
        <el-button class="action-btn" type="danger" :icon="UserIcon" @click="navigateTo('/admin/profile')">个人信息</el-button>
      </div>
    </el-card>

    <div class="charts-row">
      <ChartCard
        title="用户角色分布"
        type="pie"
        :data="userRoleChartData"
      />
      <ChartCard
        title="任务状态分布"
        type="pie"
        :data="taskStatusChartData"
      />
      <ChartCard
        title="团队成员数量"
        type="bar"
        :data="teamMemberChartData"
      />
    </div>

    <div class="section-row">
      <el-card class="recent-users">
        <h3>最近注册用户</h3>
        <el-list>
          <el-list-item
            v-for="user in recentUsers"
            :key="user.id"
            class="user-item"
          >
            <template #default>
              <span class="user-name">{{ user.username }}</span>
              <el-tag :type="user.role === 'ADMIN' ? 'danger' : 'success'">{{ user.role }}</el-tag>
            </template>
            <template #suffix>
              {{ formatDate(user.createTime) }}
            </template>
          </el-list-item>
        </el-list>
      </el-card>

      <el-card class="recent-tasks">
        <h3>最近任务</h3>
        <el-list>
          <el-list-item
            v-for="task in recentTasks"
            :key="task.id"
            class="task-item"
          >
            <template #default>
              <span class="task-name">{{ task.taskName }}</span>
              <span class="task-team">{{ task.teamName }}</span>
            </template>
            <template #suffix>
              <el-tag :type="getStatusType(task.status)">{{ getStatusText(task.status) }}</el-tag>
            </template>
          </el-list-item>
        </el-list>
      </el-card>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, markRaw, computed } from 'vue'
import { useRouter } from 'vue-router'
import { User, OfficeBuilding, Document, Clock, UserFilled, Setting, Promotion, Plus, HomeFilled, Message, Check } from '@element-plus/icons-vue'
import { userApi, teamApi, taskApi } from '../api/services'
import ChartCard from '../components/ChartCard.vue'

export default {
  name: 'AdminHome',
  components: {
    ChartCard
  },
  setup() {
    const router = useRouter()
    const UserIcon = markRaw(User)
    const OfficeBuildingIcon = markRaw(OfficeBuilding)
    const DocumentIcon = markRaw(Document)
    const ClockIcon = markRaw(Clock)
    const HomeIcon = markRaw(HomeFilled)
    const MessageIcon = markRaw(Message)
    const CheckIcon = markRaw(Check)
    
    const userCount = ref(0)
    const teamCount = ref(0)
    const taskCount = ref(0)
    const pendingCount = ref(0)
    const recentUsers = ref([])
    const recentTasks = ref([])
    
    // popover状态
    const userPopoverVisible = ref(false)
    const teamPopoverVisible = ref(false)
    const taskPopoverVisible = ref(false)
    const pendingPopoverVisible = ref(false)
    
    // popover数据
    const userList = ref([])
    const teamList = ref([])
    const recentTaskList = ref([])
    const pendingTasks = ref([])
    
    // 图表数据
    const chartUsers = ref([])
    const chartTasks = ref([])
    const chartTeams = ref([])

    const formatDate = (dateStr) => {
      if (!dateStr) return ''
      const date = new Date(dateStr)
      return date.toLocaleDateString('zh-CN')
    }

    const getStatusType = (status) => {
      const types = {
        'PENDING': 'warning',
        'IN_PROGRESS': 'primary',
        'COMPLETED': 'success'
      }
      return types[status] || 'info'
    }

    const getStatusText = (status) => {
      const texts = {
        'PENDING': '待处理',
        'IN_PROGRESS': '进行中',
        'COMPLETED': '已完成'
      }
      return texts[status] || status
    }

    const navigateTo = (path) => {
      router.push(path)
    }

    const getPriorityText = (priority) => {
      const texts = {
        'LOW': '低',
        'MEDIUM': '中',
        'HIGH': '高'
      }
      return texts[priority] || priority
    }

    const userRoleChartData = computed(() => {
      const adminCount = chartUsers.value.filter(u => u.role === 'ADMIN').length
      const userCountVal = chartUsers.value.filter(u => u.role === 'USER').length
      return {
        items: [
          { value: adminCount, name: '管理员', itemStyle: { color: '#f56c6c' } },
          { value: userCountVal, name: '普通用户', itemStyle: { color: '#67c23a' } }
        ]
      }
    })

    const taskStatusChartData = computed(() => {
      const pendingCount = chartTasks.value.filter(t => t.status === 'PENDING').length
      const inProgressCount = chartTasks.value.filter(t => t.status === 'IN_PROGRESS').length
      const completedCount = chartTasks.value.filter(t => t.status === 'COMPLETED').length
      return {
        items: [
          { value: pendingCount, name: '待处理', itemStyle: { color: '#e6a23c' } },
          { value: inProgressCount, name: '进行中', itemStyle: { color: '#409eff' } },
          { value: completedCount, name: '已完成', itemStyle: { color: '#67c23a' } }
        ]
      }
    })

    const teamMemberChartData = computed(() => {
      return {
        xData: chartTeams.value.map(t => t.teamName),
        yData: chartTeams.value.map(t => t.members ? t.members.length : 0)
      }
    })

    const loadStats = async () => {
      try {
        const users = await userApi.getAllUsers()
        if (users.success) {
          userCount.value = users.data.length
          recentUsers.value = users.data.slice(-5).reverse()
          userList.value = users.data.slice(-8).reverse()
          chartUsers.value = users.data
        }

        const teams = await teamApi.getAllTeams()
        if (teams.success) {
          teamCount.value = teams.data.length
          teamList.value = teams.data.slice(-8).reverse()
          chartTeams.value = teams.data
        }

        const tasks = await taskApi.getAllTasks()
        if (tasks.success) {
          taskCount.value = tasks.data.length
          pendingCount.value = tasks.data.filter(t => t.status === 'PENDING').length
          recentTasks.value = tasks.data.slice(-5).reverse().map(task => ({
            ...task,
            teamName: task.team?.teamName || '未分配'
          }))
          recentTaskList.value = tasks.data.slice(-8).reverse()
          pendingTasks.value = tasks.data.filter(t => t.status === 'PENDING' || t.status === 'IN_PROGRESS').slice(-8)
          chartTasks.value = tasks.data
        }
      } catch (error) {
        console.error('加载数据失败:', error)
      }
    }

    onMounted(() => {
      loadStats()
    })

    return {
      UserIcon,
      OfficeBuildingIcon,
      DocumentIcon,
      ClockIcon,
      HomeIcon,
      MessageIcon,
      CheckIcon,
      UserFilled,
      Setting,
      Promotion,
      Plus,
      userCount,
      teamCount,
      taskCount,
      pendingCount,
      recentUsers,
      recentTasks,
      userPopoverVisible,
      teamPopoverVisible,
      taskPopoverVisible,
      pendingPopoverVisible,
      userList,
      teamList,
      recentTaskList,
      pendingTasks,
      userRoleChartData,
      taskStatusChartData,
      teamMemberChartData,
      formatDate,
      getStatusType,
      getStatusText,
      getPriorityText,
      navigateTo
    }
  }
}
</script>

<style scoped>
.admin-home {
  padding: 20px;
}

.welcome-card {
  margin-bottom: 20px;
}

.welcome-card h2 {
  margin-bottom: 10px;
  color: #303133;
}

.description {
  color: #606266;
}

.stats-row {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 20px;
}

.quick-actions {
  margin-bottom: 20px;
}

.quick-actions h3 {
  margin-bottom: 15px;
  color: #303133;
}

.actions-grid {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 10px;
  width: 100%;
}

.actions-grid .el-button {
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
  width: 100%;
  height: 100%;
  min-height: 56px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 4px;
}

.actions-grid .action-btn {
  --el-button-icon-size: 24px;
  padding: 10px 8px;
  font-size: 12px;
}

.actions-grid .action-btn .el-icon {
  width: 24px;
  height: 24px;
  font-size: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.actions-row .el-button::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(
    90deg,
    transparent,
    rgba(255, 255, 255, 0.2),
    transparent
  );
  transition: left 0.5s ease;
}

.actions-row .el-button:hover::before {
  left: 100%;
}

.actions-row .el-button:hover {
  transform: translateY(-3px) scale(1.05);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.15);
}

.actions-row .el-button:active {
  transform: translateY(-1px) scale(1.02);
}

.actions-row .el-button .el-icon {
  transition: transform 0.3s ease;
}

.actions-row .el-button:hover .el-icon {
  transform: rotate(10deg) scale(1.2);
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 15px;
}

.stat-hoverable {
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.stat-hoverable:hover {
  transform: translateY(-3px) scale(1.02);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.15);
}

.stat-hoverable::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(
    90deg,
    transparent,
    rgba(255, 255, 255, 0.3),
    transparent
  );
  transition: left 0.5s ease;
}

.stat-hoverable:hover::before {
  left: 100%;
}

.popover-content h4 {
  margin: 0 0 10px 0;
  padding-bottom: 8px;
  border-bottom: 1px solid #eee;
  color: #303133;
}

.popover-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.popover-item {
  display: flex;
  flex-direction: column;
  padding: 6px 0;
  border-bottom: 1px solid #f0f0f0;
}

.popover-item:last-child {
  border-bottom: none;
}

.item-main {
  font-weight: 500;
  color: #303133;
  margin-bottom: 4px;
}

.item-secondary {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 12px;
  color: #909399;
}

.item-secondary .el-tag {
  margin: 0;
}

.popover-content .el-list-item {
  padding: 8px 0;
}

.popover-content .el-tag {
  margin-left: 8px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
}

.user-icon {
  background-color: #e3f2fd;
  color: #1976d2;
}

.team-icon {
  background-color: #e8f5e9;
  color: #388e3c;
}

.task-icon {
  background-color: #fff8e1;
  color: #f57c00;
}

.pending-icon {
  background-color: #f3e5f5;
  color: #7b1fa2;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
}

.stat-label {
  color: #606266;
  font-size: 14px;
}

.charts-row {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  margin-bottom: 20px;
}

.section-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.recent-users h3,
.recent-tasks h3 {
  margin-bottom: 15px;
  color: #303133;
}

.user-item,
.task-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
  border-bottom: 1px solid #f0f0f0;
}

.user-item:last-child,
.task-item:last-child {
  border-bottom: none;
}

.user-name,
.task-name {
  font-weight: 500;
  color: #303133;
}

.task-team {
  margin-left: 10px;
  color: #909399;
  font-size: 12px;
}
</style>