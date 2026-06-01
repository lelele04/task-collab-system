<template>
  <div class="task-manage">
    <div class="header">
      <h3>任务管理</h3>
      <div class="search-bar">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索任务名称或描述"
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
        <el-select
          v-model="filterStatus"
          placeholder="状态筛选"
          style="width: 150px"
          @change="handleStatusFilter"
        >
          <el-option label="全部" value="" />
          <el-option label="待领取" value="PENDING" />
          <el-option label="进行中" value="IN_PROGRESS" />
          <el-option label="待审核" value="PENDING_REVIEW" />
          <el-option label="已完成" value="COMPLETED" />
        </el-select>
        <el-button v-if="isAdmin" type="primary" :icon="PlusIcon" @click="handleAdd">创建任务</el-button>
      </div>
    </div>

    <el-tabs v-model="activeTab" @tab-change="handleTabChange">
      <el-tab-pane label="任务列表" name="tasks">
        <el-table :data="taskList" border stripe style="width: 100%">
          <el-table-column prop="id" label="任务ID" width="100">
            <template #default="scope">{{ formatTaskId(scope.row.id) }}</template>
          </el-table-column>
          <el-table-column prop="taskName" label="任务名称" />
          <el-table-column prop="description" label="任务描述" show-overflow-tooltip />
          <el-table-column prop="teamName" label="团队名称" />
          <el-table-column prop="assigneeName" label="负责人" />
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
          <el-table-column label="操作" width="450">
            <template #default="scope">
              <el-button size="small" :icon="EditIcon" @click="handleEdit(scope.row)">编辑</el-button>
              
              <el-button
                v-if="scope.row.status !== 'COMPLETED'"
                size="small"
                type="success"
                :icon="CheckIcon"
                @click="handleChangeStatus(scope.row)">
                状态
              </el-button>
              <el-button
                v-if="scope.row.status !== 'COMPLETED' && isDeadlineWarning(scope.row.deadline)"
                size="small"
                type="warning"
                @click="handleEndTask(scope.row)">
                手动结束
              </el-button>
              <el-button
                size="small"
                type="danger"
                :icon="DeleteIcon"
                @click="handleDelete(scope.row.id)">
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="任务审核" name="review">
        <el-table :data="pendingReviewTasks" border stripe style="width: 100%">
          <el-table-column prop="id" label="任务ID" width="100">
            <template #default="scope">{{ formatTaskId(scope.row.id) }}</template>
          </el-table-column>
          <el-table-column prop="taskName" label="任务名称" />
          <el-table-column prop="teamName" label="团队名称" />
          <el-table-column prop="assigneeName" label="提交人" />
          <el-table-column prop="description" label="任务描述" show-overflow-tooltip />
          <el-table-column prop="submitTime" label="提交时间" width="180">
            <template #default="scope">
              {{ formatDate(scope.row.updateTime) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="150">
            <template #default="scope">
              <el-button size="small" type="success" @click="handleApprove(scope.row)">通过</el-button>
              <el-button size="small" type="danger" @click="handleReject(scope.row)">拒绝</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>

    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑任务' : '创建任务'"
      width="600px"
    >
      <el-form :model="taskForm" :rules="rules" ref="taskFormRef" label-width="100px">
        <el-form-item label="任务名称" prop="taskName">
          <el-input v-model="taskForm.taskName" placeholder="请输入任务名称" />
        </el-form-item>
        <el-form-item label="任务描述" prop="description">
          <el-input
            v-model="taskForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入任务描述"
          />
        </el-form-item>
        <el-form-item label="团队ID（可选）" prop="teamId">
          <el-input-number v-model="taskForm.teamId" :min="1" style="width: 100%" />
          <span class="el-form-item__help">不填写则任务不归属任何团队</span>
        </el-form-item>
        <el-form-item label="优先级" prop="priority">
          <el-select v-model="taskForm.priority" style="width: 100%">
            <el-option label="低" value="LOW" />
            <el-option label="中" value="MEDIUM" />
            <el-option label="高" value="HIGH" />
          </el-select>
        </el-form-item>
        <el-form-item label="截止日期" prop="deadline">
          <el-date-picker
            v-model="taskForm.deadline"
            type="datetime"
            placeholder="选择截止日期"
            style="width: 100%"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="statusDialogVisible" title="修改任务状态" width="400px">
      <el-form label-width="80px">
        <el-form-item label="当前任务">
          <span>{{ currentTask.taskName }}</span>
        </el-form-item>
        <el-form-item label="新状态">
          <el-select v-model="newStatus" style="width: 100%">
            <el-option label="待领取" value="PENDING" />
            <el-option label="进行中" value="IN_PROGRESS" />
            <el-option label="待审核" value="PENDING_REVIEW" />
            <el-option label="已完成" value="COMPLETED" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="statusDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitStatus">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, computed, onMounted, markRaw } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Plus, Edit, Delete, Check, ArrowLeft } from '@element-plus/icons-vue'
import { taskApi } from '../api/services'
import { formatTaskId, formatTeamId, formatUserId } from '../utils/idFormatter'

export default {
  name: 'TaskManage',
  setup() {
    const route = useRoute()
    const SearchIcon = markRaw(Search)
    const ArrowLeftIcon = markRaw(ArrowLeft)
    const PlusIcon = markRaw(Plus)
    const EditIcon = markRaw(Edit)
    const DeleteIcon = markRaw(Delete)
    const CheckIcon = markRaw(Check)
    const searchKeyword = ref('')
    const taskList = ref([])
    const dialogVisible = ref(false)
    const statusDialogVisible = ref(false)
    const isEdit = ref(false)
    const taskFormRef = ref(null)
    const currentTask = ref({})
    const newStatus = ref('')
    const filterStatus = ref('')
    const activeTab = ref('tasks')
    const isAdmin = ref(false)

    const taskForm = reactive({
      id: null,
      taskName: '',
      description: '',
      teamId: null,
      assigneeId: null,
      creatorId: null,
      status: 'PENDING',
      priority: 'MEDIUM',
      deadline: null
    })

    const rules = {
      taskName: [
        { required: true, message: '请输入任务名称', trigger: 'blur' }
      ],
      description: [
        { required: true, message: '请输入任务描述', trigger: 'blur' }
      ],
      priority: [
        { required: true, message: '请选择优先级', trigger: 'change' }
      ],
      deadline: [
        { required: true, message: '请选择截止日期', trigger: 'change' }
      ]
    }

    const pendingReviewTasks = computed(() => {
      return taskList.value.filter(task => task.status === 'PENDING_REVIEW')
    })

    const formatDate = (dateStr) => {
      if (!dateStr) return '-'
      return new Date(dateStr).toLocaleString('zh-CN')
    }

    const isDeadlineWarning = (deadline) => {
      if (!deadline) return false
      const deadlineDate = new Date(deadline)
      const now = new Date()
      return deadlineDate < now
    }

    const getStatusType = (status) => {
      const types = {
        'PENDING': 'info',
        'IN_PROGRESS': 'warning',
        'PENDING_REVIEW': 'primary',
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

    const getPriorityType = (priority) => {
      const types = {
        'LOW': 'info',
        'MEDIUM': 'warning',
        'HIGH': 'danger'
      }
      return types[priority] || 'info'
    }

    const getPriorityText = (priority) => {
      const texts = {
        'LOW': '低',
        'MEDIUM': '中',
        'HIGH': '高'
      }
      return texts[priority] || priority
    }

    const loadTasks = async () => {
      try {
        const response = await taskApi.getAllTasks()
        if (response.success) {
          taskList.value = response.data.map(task => ({
            ...task,
            teamName: task.teamName || task.team?.teamName || '未分配',
            assigneeName: task.assigneeName || task.assignee?.username || '未分配'
          }))
        }
      } catch (error) {
        ElMessage.error('加载任务列表失败')
      }
    }

    const handleSearch = async () => {
      if (!searchKeyword.value) {
        loadTasks()
        return
      }
      try {
        const response = await taskApi.searchTasks(searchKeyword.value)
        if (response.success) {
          taskList.value = response.data.map(task => ({
            ...task,
            teamName: task.teamName || task.team?.teamName || '未分配',
            assigneeName: task.assigneeName || task.assignee?.username || '未分配'
          }))
        }
      } catch (error) {
        ElMessage.error('搜索失败')
      }
    }

    const resetSearch = () => {
      searchKeyword.value = ''
      loadTasks()
    }

    const handleStatusFilter = async () => {
      if (!filterStatus.value) {
        loadTasks()
        return
      }
      try {
        const response = await taskApi.getTasksByStatus(filterStatus.value)
        if (response.success) {
          taskList.value = response.data.map(task => ({
            ...task,
            teamName: task.teamName || task.team?.teamName || '未分配',
            assigneeName: task.assigneeName || task.assignee?.username || '未分配'
          }))
        }
      } catch (error) {
        ElMessage.error('筛选失败')
      }
    }

    const handleTabChange = () => {
      if (activeTab.value === 'review') {
        loadTasks()
      }
    }

    const handleAdd = () => {
      isEdit.value = false
      Object.assign(taskForm, {
        id: null,
        taskName: '',
        description: '',
        teamId: null,
        assigneeId: null,
        creatorId: null,
        status: 'PENDING',
        priority: 'MEDIUM',
        deadline: null
      })
      dialogVisible.value = true
    }

    const handleEdit = (row) => {
      isEdit.value = true
      Object.assign(taskForm, {
        id: row.id,
        taskName: row.taskName,
        description: row.description,
        teamId: row.teamId,
        assigneeId: row.assigneeId,
        creatorId: row.creatorId,
        status: row.status,
        priority: row.priority,
        deadline: row.deadline
      })
      dialogVisible.value = true
    }

    const handleSubmit = async () => {
      try {
        await taskFormRef.value.validate()

        if (isEdit.value) {
          const response = await taskApi.updateTask(taskForm.id, taskForm)
          if (response.success) {
            ElMessage.success('更新成功')
            dialogVisible.value = false
            loadTasks()
          }
        } else {
          const response = await taskApi.createTask(taskForm)
          if (response.success) {
            ElMessage.success('创建成功')
            dialogVisible.value = false
            loadTasks()
          }
        }
      } catch (error) {
        ElMessage.error(error.message || '操作失败')
      }
    }

    const handleChangeStatus = (row) => {
      currentTask.value = row
      newStatus.value = row.status
      statusDialogVisible.value = true
    }

    const handleSubmitStatus = async () => {
      try {
        const response = await taskApi.updateTaskStatus(currentTask.value.id, newStatus.value)
        if (response.success) {
          ElMessage.success('状态更新成功')
          statusDialogVisible.value = false
          loadTasks()
        }
      } catch (error) {
        ElMessage.error('状态更新失败')
      }
    }

    const handleDelete = (id) => {
      ElMessageBox.confirm('确定要删除该任务吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          const response = await taskApi.deleteTask(id)
          if (response.success) {
            ElMessage.success('删除成功')
            loadTasks()
          }
        } catch (error) {
          ElMessage.error('删除失败')
        }
      }).catch(() => {})
    }

    const handleApprove = async (task) => {
      try {
        const response = await taskApi.updateTaskStatus(task.id, 'COMPLETED')
        if (response.success) {
          ElMessage.success('审核通过，任务已完成')
          loadTasks()
        }
      } catch (error) {
        ElMessage.error('审核失败')
      }
    }

    const handleReject = async (task) => {
      try {
        const response = await taskApi.updateTaskStatus(task.id, 'IN_PROGRESS')
        if (response.success) {
          ElMessage.success('已拒绝，任务退回修改')
          loadTasks()
        }
      } catch (error) {
        ElMessage.error('操作失败')
      }
    }

    const handleClaim = async (task) => {
      try {
        const userStr = localStorage.getItem('user')
        if (!userStr) {
          ElMessage.error('请先登录')
          return
        }
        const currentUser = JSON.parse(userStr)
        if (!currentUser.id) {
          ElMessage.error('用户信息异常，请重新登录')
          return
        }
        const response = await taskApi.claimTask(task.id, currentUser.id)
        if (response.success) {
          ElMessage.success('任务领取成功')
          loadTasks()
        } else {
          ElMessage.error(response.message || '领取失败')
        }
      } catch (error) {
        ElMessage.error(error.message || '领取失败')
      }
    }

    const handleEndTask = async (task) => {
      try {
        await ElMessageBox.confirm(
          '该任务已过截止日期，确定要手动结束此任务吗？',
          '确认结束',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning'
          }
        )

        const response = await taskApi.updateTaskStatus(task.id, 'COMPLETED')
        if (response.success) {
          ElMessage.success('任务已手动结束')
          loadTasks()
        }
      } catch (error) {
        if (error !== 'cancel') {
          ElMessage.error('操作失败')
        }
      }
    }

    onMounted(() => {
      const userStr = localStorage.getItem('user')
      if (userStr) {
        const user = JSON.parse(userStr)
        isAdmin.value = user.role === 'ADMIN'
      }
      loadTasks()
      const tab = route.query.tab
      if (tab === 'review') {
        activeTab.value = 'review'
      }
    })

    return {
      handleClaim,
      handleEndTask,
      SearchIcon,
      ArrowLeftIcon,
      PlusIcon,
      formatTaskId,
      formatTeamId,
      formatUserId,
      EditIcon,
      DeleteIcon,
      CheckIcon,
      searchKeyword,
      taskList,
      dialogVisible,
      statusDialogVisible,
      isEdit,
      taskForm,
      rules,
      taskFormRef,
      currentTask,
      newStatus,
      filterStatus,
      activeTab,
      isAdmin,
      pendingReviewTasks,
      formatDate,
      isDeadlineWarning,
      getStatusType,
      getStatusText,
      getPriorityType,
      getPriorityText,
      handleSearch,
      handleStatusFilter,
      handleTabChange,
      handleAdd,
      handleEdit,
      handleSubmit,
      handleChangeStatus,
      handleSubmitStatus,
      handleDelete,
      handleApprove,
      handleReject,
      resetSearch
    }
  }
}
</script>

<style scoped>
.task-manage {
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

.search-bar {
  display: flex;
  gap: 10px;
}

.deadline-warning {
  color: #e74c3c;
  font-weight: bold;
}
</style>