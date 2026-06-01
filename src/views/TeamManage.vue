<template>
  <div class="team-manage">
    <div class="header">
      <h3>团队管理</h3>
      <div class="search-bar">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索团队名称"
          style="width: 300px"
          @keyup.enter="handleSearch"
        >
          <template #append>
            <el-button :icon="SearchIcon" @click="handleSearch" />
          </template>
        </el-input>
        <el-button type="primary" :icon="PlusIcon" @click="handleAdd">创建团队</el-button>
      </div>
    </div>

    <el-tabs v-model="activeTab" @tab-change="handleTabChange">
      <el-tab-pane label="团队列表" name="teams">
        <el-table :data="teamList" border stripe style="width: 100%">
          <el-table-column prop="id" label="团队ID" width="100">
            <template #default="scope">{{ formatTeamId(scope.row.id) }}</template>
          </el-table-column>
          <el-table-column prop="teamName" label="团队名称" />
          <el-table-column prop="description" label="团队描述" show-overflow-tooltip />
          <el-table-column prop="leaderId" label="负责人ID" width="120">
            <template #default="scope">{{ formatUserId(scope.row.leaderId) }}</template>
          </el-table-column>
          <el-table-column label="团队成员" width="180">
            <template #default="scope">
              <el-popover
                v-model:visible="memberPopovers[scope.row.id]"
                placement="bottom-start"
                width="250"
                trigger="hover"
              >
                <template #reference>
                  <span class="member-count">{{ scope.row.members?.length || 0 }} 名成员</span>
                </template>
                <div class="popover-content">
                  <h4>{{ scope.row.teamName }} - 成员列表</h4>
                  <div class="popover-list">
                    <div v-for="member in scope.row.members" :key="member.id" class="popover-item">
                      <div class="item-main">{{ member.username }}</div>
                      <div class="item-secondary">
                        <el-tag :type="member.role === 'ADMIN' ? 'danger' : 'success'" size="small">
                          {{ member.role === 'ADMIN' ? '管理员' : '普通用户' }}
                        </el-tag>
                      </div>
                    </div>
                  </div>
                </div>
              </el-popover>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="创建时间" width="180">
            <template #default="scope">
              {{ formatDate(scope.row.createTime) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200">
            <template #default="scope">
              <el-button size="small" :icon="EditIcon" @click="handleEdit(scope.row)">编辑</el-button>
              <el-button
                size="small"
                type="danger"
                :icon="DeleteIcon"
                @click="handleDelete(scope.row.id)"
              >
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>

      <el-tab-pane label="团队申请" name="applications">
        <el-table :data="applicationList" border stripe style="width: 100%">
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="teamName" label="团队名称" />
          <el-table-column prop="userName" label="申请人" />
          <el-table-column prop="reason" label="申请理由" show-overflow-tooltip />
          <el-table-column prop="status" label="状态" width="100">
            <template #default="scope">
              <el-tag :type="getApplicationStatusType(scope.row.status)">
                {{ getApplicationStatusText(scope.row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="applyTime" label="申请时间" width="180">
            <template #default="scope">
              {{ formatDate(scope.row.applyTime) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="150">
            <template #default="scope">
              <template v-if="scope.row.status === 'PENDING'">
                <el-button size="small" type="success" @click="handleApprove(scope.row)">通过</el-button>
                <el-button size="small" type="danger" @click="handleReject(scope.row)">拒绝</el-button>
              </template>
              <template v-else>
                <el-button size="small" disabled>{{ getApplicationStatusText(scope.row.status) }}</el-button>
              </template>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>

    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑团队' : '创建团队'"
      width="500px"
    >
      <el-form :model="teamForm" :rules="rules" ref="teamFormRef" label-width="100px">
        <el-form-item label="团队名称" prop="teamName">
          <el-input v-model="teamForm.teamName" placeholder="请输入团队名称" />
        </el-form-item>
        <el-form-item label="团队描述" prop="description">
          <el-input
            v-model="teamForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入团队描述"
          />
        </el-form-item>
        <el-form-item label="负责人" prop="leaderId">
          <el-select v-model="teamForm.leaderId" placeholder="请选择负责人" style="width: 100%">
            <el-option
              v-for="user in userList"
              :key="user.id"
              :label="user.username"
              :value="user.id"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, onMounted, markRaw } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Plus, Edit, Delete } from '@element-plus/icons-vue'
import { teamApi, userApi } from '../api/services'
import { formatTeamId, formatUserId, formatApplicationId } from '../utils/idFormatter'

export default {
  name: 'TeamManage',
  setup() {
    const route = useRoute()
    const SearchIcon = markRaw(Search)
    const PlusIcon = markRaw(Plus)
    const EditIcon = markRaw(Edit)
    const DeleteIcon = markRaw(Delete)
    const searchKeyword = ref('')
    const teamList = ref([])
    const applicationList = ref([])
    const userList = ref([])
    const dialogVisible = ref(false)
    const isEdit = ref(false)
    const teamFormRef = ref(null)
    const activeTab = ref('teams')
    const memberPopovers = reactive({})

    const teamForm = reactive({
      id: null,
      teamName: '',
      description: '',
      leaderId: null
    })

    const rules = {
      teamName: [
        { required: true, message: '请输入团队名称', trigger: 'blur' }
      ],
      leaderId: [
        { required: true, message: '请输入负责人ID', trigger: 'blur' }
      ]
    }

    const formatDate = (dateStr) => {
      if (!dateStr) return ''
      return new Date(dateStr).toLocaleString('zh-CN')
    }

    const getApplicationStatusType = (status) => {
      const types = {
        'PENDING': 'warning',
        'APPROVED': 'success',
        'REJECTED': 'danger'
      }
      return types[status] || 'info'
    }

    const getApplicationStatusText = (status) => {
      const texts = {
        'PENDING': '待审批',
        'APPROVED': '已通过',
        'REJECTED': '已拒绝'
      }
      return texts[status] || status
    }

    const loadTeams = async () => {
      try {
        const response = await teamApi.getAllTeams()
        if (response.success) {
          teamList.value = response.data
        }
      } catch (error) {
        ElMessage.error('加载团队列表失败')
      }
    }

    const loadApplications = async () => {
      try {
        const response = await teamApi.getAllApplications()
        if (response.success) {
          applicationList.value = response.data
        }
      } catch (error) {
        ElMessage.error('加载申请列表失败')
      }
    }

    const loadUsers = async () => {
      try {
        const response = await userApi.getAllUsers()
        if (response.success) {
          userList.value = response.data
        }
      } catch (error) {
        ElMessage.error('加载用户列表失败')
      }
    }

    const handleSearch = async () => {
      if (!searchKeyword.value) {
        loadTeams()
        return
      }
      try {
        const response = await teamApi.searchTeams(searchKeyword.value)
        if (response.success) {
          teamList.value = response.data
        }
      } catch (error) {
        ElMessage.error('搜索失败')
      }
    }

    const handleTabChange = () => {
      if (activeTab.value === 'applications') {
        loadApplications()
      }
    }

    const handleAdd = () => {
      isEdit.value = false
      Object.assign(teamForm, {
        id: null,
        teamName: '',
        description: '',
        leaderId: null
      })
      dialogVisible.value = true
    }

    const handleEdit = (row) => {
      isEdit.value = true
      Object.assign(teamForm, {
        id: row.id,
        teamName: row.teamName,
        description: row.description,
        leaderId: row.leaderId
      })
      dialogVisible.value = true
    }

    const handleSubmit = async () => {
      try {
        await teamFormRef.value.validate()

        if (isEdit.value) {
          const response = await teamApi.updateTeam(teamForm.id, teamForm)
          if (response.success) {
            ElMessage.success('更新成功')
            dialogVisible.value = false
            loadTeams()
          }
        } else {
          const response = await teamApi.createTeam(teamForm)
          if (response.success) {
            ElMessage.success('创建成功')
            dialogVisible.value = false
            loadTeams()
          }
        }
      } catch (error) {
        ElMessage.error(error.message || '操作失败')
      }
    }

    const handleDelete = (id) => {
      ElMessageBox.confirm('确定要删除该团队吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          const response = await teamApi.deleteTeam(id)
          if (response.success) {
            ElMessage.success('删除成功')
            loadTeams()
          }
        } catch (error) {
          ElMessage.error('删除失败')
        }
      }).catch(() => {})
    }

    const handleApprove = async (application) => {
      try {
        const response = await teamApi.approveApplication(application.id)
        if (response.success) {
          ElMessage.success('审批通过')
          loadApplications()
        }
      } catch (error) {
        ElMessage.error('操作失败')
      }
    }

    const handleReject = async (application) => {
      try {
        const response = await teamApi.rejectApplication(application.id)
        if (response.success) {
          ElMessage.success('已拒绝')
          loadApplications()
        }
      } catch (error) {
        ElMessage.error('操作失败')
      }
    }

    onMounted(() => {
      loadTeams()
      loadUsers()
      const tab = route.query.tab
      if (tab === 'applications') {
        activeTab.value = 'applications'
        loadApplications()
      }
    })

    return {
      SearchIcon,
      PlusIcon,
      EditIcon,
      DeleteIcon,
      searchKeyword,
      teamList,
      applicationList,
      userList,
      dialogVisible,
      formatTeamId,
      formatUserId,
      formatApplicationId,
      isEdit,
      teamForm,
      rules,
      teamFormRef,
      activeTab,
      memberPopovers,
      formatDate,
      getApplicationStatusType,
      getApplicationStatusText,
      handleSearch,
      handleTabChange,
      handleAdd,
      handleEdit,
      handleSubmit,
      handleDelete,
      handleApprove,
      handleReject
    }
  }
}
</script>

<style scoped>
.team-manage {
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

.member-count {
  color: #409eff;
  cursor: pointer;
  text-decoration: underline;
}

.popover-content {
  padding: 8px;
}

.popover-content h4 {
  margin: 0 0 10px 0;
  font-size: 14px;
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
  gap: 4px;
  padding: 6px;
  border-radius: 4px;
  background: #fafafa;
}

.popover-item:hover {
  background: #f0f0f0;
}

.item-main {
  font-weight: 500;
  color: #303133;
}

.item-secondary {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 12px;
  color: #909399;
}
</style>