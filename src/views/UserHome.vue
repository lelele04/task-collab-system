<template>
  <div class="user-home">
    <el-card class="welcome-card">
      <h2>欢迎回来，{{ currentUser.username }}！</h2>
      <p class="description">这是您的个人工作台，管理您的任务和团队。</p>
    </el-card>

    <div class="stats-row">
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
              <div class="stat-value">{{ taskStats.total }}</div>
              <div class="stat-label">我的任务</div>
            </div>
          </el-card>
        </template>
        <div class="popover-content">
          <h4>我的任务列表</h4>
          <div class="popover-list">
            <div v-for="task in myTasks" :key="task.id" class="popover-item">
              <div class="item-main">{{ task.taskName }}</div>
              <div class="item-secondary">
                <el-tag :type="getStatusType(task.status)" size="small">{{ getStatusText(task.status) }}</el-tag>
                <span>{{ formatDate(task.deadline) }}</span>
              </div>
            </div>
          </div>
        </div>
      </el-popover>

      <el-popover
        v-model:visible="teamPopoverVisible"
        placement="top-start"
        width="300"
        trigger="hover"
      >
        <template #reference>
          <el-card class="stat-card stat-hoverable">
            <div class="stat-icon team-icon">
              <el-icon :icon="OfficeBuildingIcon" />
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ teamCount }}</div>
              <div class="stat-label">加入的团队</div>
            </div>
          </el-card>
        </template>
        <div class="popover-content">
          <h4>我的团队</h4>
          <div class="popover-list">
            <div v-for="team in myTeams" :key="team.id" class="popover-item">
              <div class="item-main">{{ team.teamName }}</div>
              <div class="item-secondary">
                <span>负责人: {{ team.leader?.username || '未设置' }}</span>
              </div>
              <div class="team-members">
                <span class="members-label">成员:</span>
                <span v-for="member in (team.members || []).slice(0, 4)" :key="member.id" class="member-tag">
                  {{ member.username }}
                </span>
                <span v-if="(team.members || []).length > 4" class="member-tag">
                  +{{ (team.members || []).length - 4 }}
                </span>
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
              <div class="stat-value">{{ taskStats.pending }}</div>
              <div class="stat-label">待处理</div>
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

      <el-popover
        v-model:visible="progressPopoverVisible"
        placement="bottom-start"
        width="300"
        trigger="hover"
      >
        <template #reference>
          <el-card class="stat-card stat-hoverable">
            <div class="stat-icon progress-icon">
              <el-icon :icon="BriefcaseIcon" />
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ taskStats.inProgress }}</div>
              <div class="stat-label">进行中</div>
            </div>
          </el-card>
        </template>
        <div class="popover-content">
          <h4>进行中任务</h4>
          <div class="popover-list">
            <div v-for="task in inProgressTasks" :key="task.id" class="popover-item">
              <div class="item-main">{{ task.taskName }}</div>
              <div class="item-secondary">
                <el-tag type="primary" size="small">{{ getPriorityText(task.priority) }}优先级</el-tag>
                <span>{{ formatDate(task.deadline) }}</span>
              </div>
            </div>
          </div>
        </div>
      </el-popover>
    </div>

    <el-card class="quick-actions">
      <h3>快捷操作</h3>
      <div class="actions-row">
        <el-button type="primary" :icon="ListIcon" @click="navigateTo('/user/tasks')">我的任务</el-button>
        <el-button type="success" :icon="OfficeBuildingIcon" @click="navigateTo('/user/teams')">我的团队</el-button>
      </div>
    </el-card>

    <div class="charts-row">
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
      
      <el-card class="team-members-card">
        <h3>我的团队成员</h3>
        <div v-if="myTeams.length > 0">
          <div v-for="team in myTeams" :key="team.id" class="team-section">
            <div class="team-header">
              <span class="team-name">{{ team.teamName }}</span>
              <span class="team-leader">负责人: {{ team.leader?.username || '未设置' }}</span>
            </div>
            <div class="members-list">
              <span v-for="member in team.members" :key="member.id" class="member-chip">
                {{ member.username }}
              </span>
            </div>
          </div>
        </div>
        <div v-else class="empty-message">
          <p>您还没有加入任何团队</p>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted, markRaw } from 'vue'
import { useRouter } from 'vue-router'
import { Document, OfficeBuilding, Clock, Briefcase, List, Plus, Message } from '@element-plus/icons-vue'
import { taskApi, teamApi } from '../api/services'
import ChartCard from '../components/ChartCard.vue'

export default {
  name: 'UserHome',
  components: {
    ChartCard
  },
  setup() {
    const router = useRouter()
    const DocumentIcon = markRaw(Document)
    const OfficeBuildingIcon = markRaw(OfficeBuilding)
    const ClockIcon = markRaw(Clock)
    const BriefcaseIcon = markRaw(Briefcase)
    const ListIcon = markRaw(List)
    const AddIcon = markRaw(Plus)
    const MessageIcon = markRaw(Message)
    
    const currentUser = computed(() => {
      const user = localStorage.getItem('user')
      return user ? JSON.parse(user) : { username: '未知用户', id: 0 }
    })

    const taskStats = ref({
      total: 0,
      pending: 0,
      inProgress: 0,
      completed: 0
    })

    const teamCount = ref(0)
    const recentTasks = ref([])
    
    // popover状态
    const taskPopoverVisible = ref(false)
    const teamPopoverVisible = ref(false)
    const pendingPopoverVisible = ref(false)
    const progressPopoverVisible = ref(false)
    
    // popover数据
    const myTasks = ref([])
    const myTeams = ref([])
    const pendingTasks = ref([])
    const inProgressTasks = ref([])
    
    // 图表数据
    const chartTasks = ref([])
    const chartTeams = ref([])

    const taskStatusChartData = computed(() => {
      const pendingCount = chartTasks.value.filter(t => t.status === 'PENDING').length
      const inProgressCount = chartTasks.value.filter(t => t.status === 'IN_PROGRESS').length
      const pendingReviewCount = chartTasks.value.filter(t => t.status === 'PENDING_REVIEW').length
      const completedCount = chartTasks.value.filter(t => t.status === 'COMPLETED').length
      return {
        items: [
          { value: pendingCount, name: '待处理', itemStyle: { color: '#e6a23c' } },
          { value: inProgressCount, name: '进行中', itemStyle: { color: '#409eff' } },
          { value: pendingReviewCount, name: '待审核', itemStyle: { color: '#909399' } },
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

    const getStatusType = (status) => {
      const types = {
        'PENDING': 'warning',
        'IN_PROGRESS': 'primary',
        'PENDING_REVIEW': 'info',
        'COMPLETED': 'success'
      }
      return types[status] || 'info'
    }

    const getStatusText = (status) => {
      const texts = {
        'PENDING': '待处理',
        'IN_PROGRESS': '进行中',
        'PENDING_REVIEW': '待审核',
        'COMPLETED': '已完成'
      }
      return texts[status] || status
    }

    const getPriorityText = (priority) => {
      const texts = {
        'LOW': '低',
        'MEDIUM': '中',
        'HIGH': '高'
      }
      return texts[priority] || priority
    }

    const formatDate = (dateStr) => {
      if (!dateStr) return ''
      const date = new Date(dateStr)
      const year = date.getFullYear()
      const month = String(date.getMonth() + 1).padStart(2, '0')
      const day = String(date.getDate()).padStart(2, '0')
      const hours = String(date.getHours()).padStart(2, '0')
      const minutes = String(date.getMinutes()).padStart(2, '0')
      return `${year}-${month}-${day} ${hours}:${minutes}`
    }

    const navigateTo = (path) => {
      router.push(path)
    }

    const loadStats = async () => {
      try {
        const tasks = await taskApi.getTasksByAssigneeId(currentUser.value.id)
        if (tasks.success) {
          const data = tasks.data
          taskStats.value = {
            total: data.length,
            pending: data.filter(t => t.status === 'PENDING').length,
            inProgress: data.filter(t => t.status === 'IN_PROGRESS' || t.status === 'PENDING_REVIEW').length,
            completed: data.filter(t => t.status === 'COMPLETED').length
          }
          myTasks.value = data.slice(0, 8)
          pendingTasks.value = data.filter(t => t.status === 'PENDING').slice(0, 8)
          inProgressTasks.value = data.filter(t => t.status === 'IN_PROGRESS' || t.status === 'PENDING_REVIEW').slice(0, 8)
          chartTasks.value = data
        }

        // 获取最近发布的任务（所有任务按创建时间排序）
        const allTasks = await taskApi.getAllTasks()
        if (allTasks.success) {
          // 按创建时间倒序排序，取最近的5条
          const sortedTasks = allTasks.data.sort((a, b) => 
            new Date(b.createTime) - new Date(a.createTime)
          )
          recentTasks.value = sortedTasks.slice(0, 5).map(task => ({
            ...task,
            teamName: task.team?.teamName || '未分配'
          }))
        }

        const teams = await teamApi.getAllTeams()
        if (teams.success) {
          // 只显示用户加入的团队
          const userTeams = teams.data.filter(team => 
            team.members && team.members.some(m => m.id === currentUser.value.id)
          )
          teamCount.value = userTeams.length
          myTeams.value = userTeams.slice(0, 8)
          chartTeams.value = userTeams
        }
      } catch (error) {
        console.error('加载数据失败:', error)
      }
    }

    onMounted(() => {
      loadStats()
    })

    return {
      DocumentIcon,
      OfficeBuildingIcon,
      ClockIcon,
      BriefcaseIcon,
      ListIcon,
      AddIcon,
      MessageIcon,
      currentUser,
      taskStats,
      teamCount,
      recentTasks,
      taskPopoverVisible,
      teamPopoverVisible,
      pendingPopoverVisible,
      progressPopoverVisible,
      myTasks,
      myTeams,
      pendingTasks,
      inProgressTasks,
      taskStatusChartData,
      teamMemberChartData,
      getStatusType,
      getStatusText,
      getPriorityText,
      formatDate,
      navigateTo
    }
  }
}
</script>

<style scoped>
.user-home {
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

.actions-row {
  display: flex;
  gap: 15px;
  flex-wrap: wrap;
}

.actions-row .el-button {
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
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

.task-icon {
  background-color: #e3f2fd;
  color: #1976d2;
}

.team-icon {
  background-color: #e8f5e9;
  color: #388e3c;
}

.pending-icon {
  background-color: #fff8e1;
  color: #f57c00;
}

.progress-icon {
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
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
  margin-bottom: 20px;
}

.section-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.recent-tasks h3 {
  margin-bottom: 15px;
  color: #303133;
}

.task-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
  border-bottom: 1px solid #f0f0f0;
}

.task-item:last-child {
  border-bottom: none;
}

.task-name {
  font-weight: 500;
  color: #303133;
}

.task-team {
  margin-left: 10px;
  color: #909399;
  font-size: 12px;
}

.team-members {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 6px;
  margin-top: 4px;
}

.members-label {
  font-size: 12px;
  color: #909399;
}

.member-tag {
  font-size: 11px;
  padding: 2px 6px;
  background-color: #e4e7ed;
  color: #606266;
  border-radius: 4px;
}

.team-members-card h3 {
  margin-bottom: 15px;
  color: #303133;
}

.team-section {
  margin-bottom: 20px;
  padding-bottom: 20px;
  border-bottom: 1px solid #f0f0f0;
}

.team-section:last-child {
  margin-bottom: 0;
  padding-bottom: 0;
  border-bottom: none;
}

.team-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.team-header .team-name {
  font-weight: 600;
  color: #303133;
}

.team-header .team-leader {
  font-size: 12px;
  color: #909399;
}

.members-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.member-chip {
  padding: 4px 10px;
  background-color: #e8f5e9;
  color: #388e3c;
  border-radius: 12px;
  font-size: 12px;
}

.empty-message {
  text-align: center;
  padding: 20px;
  color: #909399;
}
</style>