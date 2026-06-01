<template>
  <div class="user-team-list">
    <div class="header">
      <h3>我的团队</h3>
      <div class="header-actions">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索团队名称"
          style="width: 200px"
          @keyup.enter="handleSearch"
        >
          <template #append>
            <el-button :icon="SearchIcon" @click="handleSearch" />
          </template>
        </el-input>
        <el-button type="primary" :icon="PlusIcon" @click="openCreateDialog">申请创建团队</el-button>
      </div>
    </div>

    <div v-if="myTeams.length > 0" class="my-teams-section">
      <h4>我加入的团队</h4>
      <el-table :data="myTeams" border stripe style="width: 100%">
        <el-table-column prop="id" label="团队ID" width="100">
          <template #default="scope">{{ formatTeamId(scope.row.id) }}</template>
        </el-table-column>
        <el-table-column prop="teamName" label="团队名称" />
        <el-table-column prop="description" label="团队描述" show-overflow-tooltip />
        <el-table-column prop="leaderName" label="负责人" />
        <el-table-column label="团队成员" width="180">
          <template #default="scope">
            <el-popover
              v-model:visible="memberPopovers[scope.row.id]"
              placement="bottom-start"
              width="250"
              trigger="hover"
            >
              <template #reference>
                <span class="member-count">{{ scope.row.memberCount }} 名成员</span>
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
        <el-table-column label="我的角色" width="120">
          <template #default="scope">
            <el-tag :type="getMembershipStatusType(scope.row.id)">
              {{ getMembershipStatusText(scope.row.id) }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <div v-if="myTeams.length === 0" class="empty-state">
      <el-empty description="您还没有加入任何团队" />
    </div>

    <div class="available-teams-section">
      <h4>可加入的团队</h4>
      <el-table :data="availableTeams" border stripe style="width: 100%">
        <el-table-column prop="id" label="团队ID" width="100">
          <template #default="scope">{{ formatTeamId(scope.row.id) }}</template>
        </el-table-column>
        <el-table-column prop="teamName" label="团队名称" />
        <el-table-column prop="description" label="团队描述" show-overflow-tooltip />
        <el-table-column prop="leaderName" label="负责人" />
        <el-table-column prop="memberCount" label="成员数" width="100" />
        <el-table-column prop="createTime" label="创建时间" width="180">
          <template #default="scope">
            {{ formatDate(scope.row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120">
          <template #default="scope">
            <el-button 
              size="small" 
              type="primary" 
              @click="handleJoin(scope.row)"
              :disabled="myTeams.length >= 3"
            >
              {{ myTeams.length >= 3 ? '已达上限' : '加入团队' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div v-if="availableTeams.length === 0" class="empty-state">
        <el-empty description="暂无可加入的团队" />
      </div>
    </div>

    <el-dialog
      v-model="createDialogVisible"
      title="申请创建团队"
      width="500px"
    >
      <el-form :model="createForm" ref="createFormRef" label-width="80px">
        <el-form-item label="团队名称" prop="teamName">
          <el-input
            v-model="createForm.teamName"
            placeholder="请输入团队名称"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="团队描述" prop="description">
          <el-input
            v-model="createForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入团队描述"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="createDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitCreate">创建团队</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, computed, reactive, onMounted, markRaw } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, Plus } from '@element-plus/icons-vue'
import { teamApi } from '../api/services'
import { formatTeamId } from '../utils/idFormatter'

export default {
  name: 'UserTeamList',
  setup() {
    const SearchIcon = markRaw(Search)
    const PlusIcon = markRaw(Plus)
    const searchKeyword = ref('')
    const teamList = ref([])
    const createDialogVisible = ref(false)
    const createFormRef = ref(null)
    const memberPopovers = reactive({})

    const currentUser = ref(JSON.parse(localStorage.getItem('user') || '{}'))

    const createForm = reactive({
      teamName: '',
      description: ''
    })

    const isMemberOfTeam = (team) => {
      if (team.members) {
        return team.members.some(m => m.id === currentUser.value.id)
      }
      return false
    }

    const myTeams = computed(() => {
      let result = teamList.value.filter(team => isMemberOfTeam(team))
      if (searchKeyword.value) {
        result = result.filter(team =>
          team.teamName.toLowerCase().includes(searchKeyword.value.toLowerCase())
        )
      }
      return result
    })

    const availableTeams = computed(() => {
      let result = teamList.value.filter(team => !isMemberOfTeam(team))
      if (searchKeyword.value) {
        result = result.filter(team =>
          team.teamName.toLowerCase().includes(searchKeyword.value.toLowerCase())
        )
      }
      return result
    })

    const formatDate = (dateStr) => {
      if (!dateStr) return ''
      const date = new Date(dateStr)
      return date.toLocaleString('zh-CN')
    }

    const getMembershipStatus = (teamId) => {
      const team = teamList.value.find(t => t.id === teamId)
      if (team && team.members) {
        const isMember = team.members.some(m => m.id === currentUser.value.id)
        if (isMember) {
          return team.leaderId === currentUser.value.id ? 'OWNER' : 'MEMBER'
        }
      }
      return 'MEMBER'
    }

    const getMembershipStatusType = (teamId) => {
      const status = getMembershipStatus(teamId)
      const types = {
        'OWNER': 'success',
        'MEMBER': 'primary'
      }
      return types[status] || 'info'
    }

    const getMembershipStatusText = (teamId) => {
      const status = getMembershipStatus(teamId)
      const texts = {
        'OWNER': '负责人',
        'MEMBER': '成员'
      }
      return texts[status] || status
    }

    const loadTeams = async () => {
      try {
        teamList.value = []
        const response = await teamApi.getAllTeams()
        if (response.success) {
          // 去重处理
          const uniqueTeams = response.data.reduce((acc, team) => {
            if (!acc.find(t => t.id === team.id)) {
              acc.push({
                ...team,
                leaderName: team.leaderName || team.leader?.username || '未知',
                memberCount: team.members?.length || 0
              })
            }
            return acc
          }, [])
          teamList.value = uniqueTeams
        }
      } catch (error) {
        console.error('加载团队列表失败:', error)
      }
    }

    const handleJoin = async (team) => {
      if (myTeams.value.length >= 3) {
        ElMessage.warning('您已加入的团队数量已达上限（最多3个），无法再申请加入新团队')
        return
      }
      
      try {
        await ElMessageBox.confirm(
          `确定要申请加入团队"${team.teamName}"吗？`,
          '申请加入',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'info'
          }
        )
        
        const response = await teamApi.joinTeam(team.id, currentUser.value.id)
        if (response.success) {
          ElMessage.success('申请提交成功，请等待管理员审批')
          loadTeams()
        } else {
          ElMessage.error(response.message || '申请失败')
        }
      } catch (error) {
        if (error !== 'cancel') {
          ElMessage.error('申请失败')
        }
      }
    }

    const openCreateDialog = () => {
      createForm.teamName = ''
      createForm.description = ''
      createDialogVisible.value = true
    }

    const submitCreate = async () => {
      if (!createForm.teamName.trim()) {
        ElMessage.warning('请输入团队名称')
        return
      }
      try {
        const response = await teamApi.applyCreateTeam({
          userId: currentUser.value.id,
          teamName: createForm.teamName.trim(),
          description: createForm.description.trim(),
          reason: '申请创建团队'
        })
        if (response.success) {
          ElMessage.success('申请提交成功，请等待管理员审批')
          createDialogVisible.value = false
          loadTeams()
        } else {
          ElMessage.error(response.message || '申请失败')
        }
      } catch (error) {
        ElMessage.error('申请失败')
      }
    }

    const handleSearch = () => {
      console.log('搜索:', searchKeyword.value)
    }

    onMounted(() => {
      loadTeams()
    })

    return {
      SearchIcon,
      PlusIcon,
      searchKeyword,
      teamList,
      createDialogVisible,
      createForm,
      createFormRef,
      memberPopovers,
      myTeams,
      availableTeams,
      formatDate,
      formatTeamId,
      getMembershipStatus,
      getMembershipStatusType,
      getMembershipStatusText,
      handleJoin,
      openCreateDialog,
      submitCreate,
      handleSearch
    }
  }
}
</script>

<style scoped>
.user-team-list {
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

.header-actions {
  display: flex;
  gap: 10px;
  align-items: center;
}

.my-teams-section h4,
.available-teams-section h4 {
  margin-bottom: 10px;
  color: #409eff;
}

.empty-state {
  margin-top: 50px;
}

.member-count {
  color: #409eff;
  cursor: pointer;
}

.member-count:hover {
  text-decoration: underline;
}

.popover-content h4 {
  margin: 0 0 10px 0;
  font-size: 14px;
  font-weight: 600;
}

.popover-list {
  max-height: 200px;
  overflow-y: auto;
}

.popover-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 6px 0;
  border-bottom: 1px solid #f0f0f0;
}

.popover-item:last-child {
  border-bottom: none;
}

.item-main {
  font-weight: 500;
}

.item-secondary {
  margin-left: 10px;
}
</style>