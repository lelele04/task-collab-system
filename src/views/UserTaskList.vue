<template>
  <div class="user-task-list">
    <div class="header">
      <h3>我的任务</h3>
      <div class="filter-bar">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索任务名称"
          style="width: 300px"
          @keyup.enter="handleSearch"
        >
          <template #append>
            <el-button :icon="SearchIcon" @click="handleSearch" />
          </template>
        </el-input>
        <el-button
          v-if="searchKeyword"
          :icon="ArrowLeftIcon"
          @click="resetSearch"
          style="margin-left: 10px"
        >
          返回
        </el-button>
      </div>
    </div>

    <el-tabs v-model="activeTab" @tab-change="handleTabChange">
      <el-tab-pane label="我领取的任务" name="claimed">
        <el-table :data="myClaimedTasks" border stripe style="width: 100%">
          <el-table-column prop="id" label="任务ID" width="100">
            <template #default="scope">{{ formatTaskId(scope.row.id) }}</template>
          </el-table-column>
          <el-table-column prop="taskName" label="任务名称" />
          <el-table-column prop="description" label="任务描述" show-overflow-tooltip />
          <el-table-column prop="teamName" label="所属团队" />
          <el-table-column prop="creatorName" label="创建人" />
          <el-table-column prop="priority" label="优先级" width="100">
            <template #default="scope">
              <el-tag :type="getPriorityType(scope.row.priority)">
                {{ getPriorityText(scope.row.priority) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="120">
            <template #default="scope">
              <el-tag :type="getStatusType(scope.row.status)">
                {{ getStatusText(scope.row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="deadline" label="截止日期" width="180">
            <template #default="scope">
              <span :class="{ 'deadline-warning': isDeadlineWarning(scope.row.deadline) }">
                {{ formatDate(scope.row.deadline) }}
              </span>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="180">
            <template #default="scope">
              <template v-if="scope.row.assigneeId === currentUser.id && scope.row.status === 'IN_PROGRESS'">
                <el-button size="small" type="success" @click="handleSubmitForReview(scope.row)">提交审核</el-button>
              </template>
              <template v-else-if="scope.row.assigneeId === currentUser.id && scope.row.status === 'PENDING_REVIEW'">
                <el-button size="small" disabled>待审核</el-button>
              </template>
              <template v-else-if="scope.row.status === 'COMPLETED'">
                <el-button size="small" disabled>已完成</el-button>
              </template>
              <template v-else>
                <el-button size="small" disabled>{{ getStatusText(scope.row.status) }}</el-button>
              </template>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="可领取的任务" name="available">
        <el-table :data="availableTasks" border stripe style="width: 100%">
          <el-table-column prop="id" label="任务ID" width="100">
            <template #default="scope">{{ formatTaskId(scope.row.id) }}</template>
          </el-table-column>
          <el-table-column prop="taskName" label="任务名称" />
          <el-table-column prop="description" label="任务描述" show-overflow-tooltip />
          <el-table-column prop="teamName" label="所属团队" />
          <el-table-column prop="creatorName" label="创建人" />
          <el-table-column prop="priority" label="优先级" width="100">
            <template #default="scope">
              <el-tag :type="getPriorityType(scope.row.priority)">
                {{ getPriorityText(scope.row.priority) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="120">
            <template #default="scope">
              <el-tag :type="getStatusType(scope.row.status)">
                {{ getStatusText(scope.row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="deadline" label="截止日期" width="180">
            <template #default="scope">
              <span :class="{ 'deadline-warning': isDeadlineWarning(scope.row.deadline) }">
                {{ formatDate(scope.row.deadline) }}
              </span>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="180">
            <template #default="scope">
              <el-button size="small" type="primary" @click="handleClaim(scope.row)">领取任务</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
import { ref, computed, onMounted, markRaw } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, ArrowLeft } from '@element-plus/icons-vue'
import { taskApi } from '../api/services'
import { formatTaskId, formatTeamId } from '../utils/idFormatter'

export default {
  name: 'UserTaskList',
  setup() {
    const SearchIcon = markRaw(Search)
    const ArrowLeftIcon = markRaw(ArrowLeft)
    const searchKeyword = ref('')
    const taskList = ref([])
    const activeTab = ref('claimed')

    const currentUser = ref(JSON.parse(localStorage.getItem('user') || '{}'))

    const availableTasks = computed(() => {
      return taskList.value.filter(task => {
        if (task.status === 'PENDING' && !task.assigneeId) {
          if (searchKeyword.value && !task.taskName.toLowerCase().includes(searchKeyword.value.toLowerCase())) {
            return false
          }
          return true
        }
        return false
      })
    })

    const myClaimedTasks = computed(() => {
      return taskList.value.filter(task => {
        if (task.assigneeId === currentUser.value.id && task.status !== 'PENDING') {
          if (searchKeyword.value && !task.taskName.toLowerCase().includes(searchKeyword.value.toLowerCase())) {
            return false
          }
          return true
        }
        return false
      })
    })

    const formatDate = (dateStr) => {
      if (!dateStr) return '-'
      const date = new Date(dateStr)
      return date.toLocaleString('zh-CN')
    }

    const getPriorityType = (priority) => {
      const types = {
        'HIGH': 'danger',
        'MEDIUM': 'warning',
        'LOW': 'success'
      }
      return types[priority] || 'info'
    }

    const getPriorityText = (priority) => {
      const texts = {
        'HIGH': '高',
        'MEDIUM': '中',
        'LOW': '低'
      }
      return texts[priority] || priority
    }

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
        'PENDING': '待领取',
        'IN_PROGRESS': '进行中',
        'PENDING_REVIEW': '待审核',
        'COMPLETED': '已完成'
      }
      return texts[status] || status
    }

    const isDeadlineWarning = (deadline) => {
      if (!deadline) return false
      const deadlineDate = new Date(deadline)
      const now = new Date()
      return deadlineDate < now
    }

    const loadTasks = async () => {
      try {
        const response = await taskApi.getAllTasks()
        if (response.success) {
          taskList.value = response.data.map(task => ({
            ...task,
            teamName: task.team?.teamName || task.teamName || '未分配',
            creatorName: task.creator?.username || task.creatorName || '未知',
            assigneeName: task.assignee?.username || task.assigneeName || '未分配'
          }))
        }
      } catch (error) {
        console.error('加载任务列表失败:', error)
      }
    }

    const handleTabChange = () => {
      loadTasks()
    }

    const handleClaim = async (task) => {
      try {
        const response = await taskApi.claimTask(task.id, currentUser.value.id)
        if (response.success) {
          ElMessage.success('任务领取成功')
          loadTasks()
        } else {
          ElMessage.error(response.message || '领取失败')
        }
      } catch (error) {
        ElMessage.error('领取失败')
      }
    }

    const handleSubmitForReview = async (task) => {
      try {
        const response = await taskApi.updateTaskStatus(task.id, 'PENDING_REVIEW')
        if (response.success) {
          ElMessage.success('任务已提交审核，等待管理者审批')
          loadTasks()
        } else {
          ElMessage.error(response.message || '操作失败')
        }
      } catch (error) {
        ElMessage.error('操作失败')
      }
    }

    const handleSearch = () => {
      console.log('搜索:', searchKeyword.value)
    }

    const resetSearch = () => {
      searchKeyword.value = ''
      loadTasks()
    }

    onMounted(() => {
      loadTasks()
    })

    return {
      SearchIcon,
      ArrowLeftIcon,
      searchKeyword,
      taskList,
      activeTab,
      availableTasks,
      myClaimedTasks,
      currentUser,
      formatDate,
      formatTaskId,
      formatTeamId,
      getPriorityType,
      getPriorityText,
      getStatusType,
      getStatusText,
      isDeadlineWarning,
      handleTabChange,
      handleClaim,
      handleSubmitForReview,
      handleSearch,
      resetSearch
    }
  }
}
</script>

<style scoped>
.user-task-list {
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.header h3 {
  margin: 0;
}

.filter-bar {
  display: flex;
  gap: 10px;
}

.my-claimed-tasks {
  margin-bottom: 40px;
}

.my-claimed-tasks h4,
.available-tasks h4 {
  margin-bottom: 10px;
  color: #409eff;
}

.deadline-warning {
  color: #e74c3c;
  font-weight: bold;
}
</style>