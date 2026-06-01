<template>
  <div class="profile-page">
    <el-card class="profile-card">
      <div class="profile-header">
        <div class="avatar-section">
          <el-avatar :size="120" :icon="UserIcon" class="profile-avatar" />
          <h2 class="username">{{ userInfo.username }}</h2>
          <el-tag :type="userInfo.role === 'ADMIN' ? 'danger' : 'success'">
            {{ userInfo.role === 'ADMIN' ? '管理员' : '普通用户' }}
          </el-tag>
        </div>
      </div>

      <div class="profile-content">
        <h3>基本信息</h3>
        <el-form :model="userInfo" label-width="120px" class="info-form">
          <el-form-item label="用户ID">
            <span class="form-value">{{ formatUserId(userInfo.id) }}</span>
          </el-form-item>
          <el-form-item label="用户名">
            <span class="form-value">{{ userInfo.username }}</span>
          </el-form-item>
          <el-form-item label="邮箱">
            <span class="form-value">{{ userInfo.email }}</span>
          </el-form-item>
          <el-form-item label="手机号">
            <span class="form-value">{{ userInfo.phone }}</span>
          </el-form-item>
          <el-form-item label="角色">
            <span class="form-value">{{ userInfo.role === 'ADMIN' ? '管理员' : '普通用户' }}</span>
          </el-form-item>
          <el-form-item label="创建时间">
            <span class="form-value">{{ formatDate(userInfo.createTime) }}</span>
          </el-form-item>
        </el-form>

        <div class="actions">
          <el-button type="primary" @click="openEditDialog">修改信息</el-button>
          <el-button type="warning" @click="openChangePwdDialog">修改密码</el-button>
        </div>
      </div>
    </el-card>

    <el-dialog
      v-model="editDialogVisible"
      title="修改个人信息"
      width="450px"
    >
      <el-form :model="editForm" :rules="editRules" ref="editFormRef" label-width="100px">
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="editForm.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="editForm.phone" placeholder="请输入手机号" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitEdit">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog
      v-model="pwdDialogVisible"
      title="修改密码"
      width="450px"
    >
      <el-form :model="pwdForm" :rules="pwdRules" ref="pwdFormRef" label-width="100px">
        <el-form-item label="旧密码" prop="oldPassword">
          <el-input v-model="pwdForm.oldPassword" type="password" placeholder="请输入旧密码" />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="pwdForm.newPassword" type="password" placeholder="请输入新密码" />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="pwdForm.confirmPassword" type="password" placeholder="请确认新密码" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="pwdDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitChangePwd">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script>
import { ref, reactive, onMounted, markRaw, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { User } from '@element-plus/icons-vue'
import { userApi } from '../api/services'
import { formatUserId } from '../utils/idFormatter'

export default {
  name: 'Profile',
  setup() {
    const UserIcon = markRaw(User)
    const userInfo = ref({
      id: 0,
      username: '',
      email: '',
      phone: '',
      role: '',
      createTime: ''
    })

    const editDialogVisible = ref(false)
    const editFormRef = ref(null)
    const editForm = reactive({
      email: '',
      phone: ''
    })

    const editRules = {
      email: [
        { required: true, message: '请输入邮箱', trigger: 'blur' },
        { type: 'email', message: '请输入有效的邮箱地址', trigger: 'blur' }
      ],
      phone: [
        { required: true, message: '请输入手机号', trigger: 'blur' },
        { pattern: /^1[3-9]\d{9}$/, message: '请输入有效的手机号', trigger: 'blur' }
      ]
    }

    const pwdDialogVisible = ref(false)
    const pwdFormRef = ref(null)
    const pwdForm = reactive({
      oldPassword: '',
      newPassword: '',
      confirmPassword: ''
    })

    const pwdRules = {
      oldPassword: [
        { required: true, message: '请输入旧密码', trigger: 'blur' }
      ],
      newPassword: [
        { required: true, message: '请输入新密码', trigger: 'blur' },
        { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
      ],
      confirmPassword: [
        { required: true, message: '请确认新密码', trigger: 'blur' },
        { validator: (rule, value, callback) => {
          if (value !== pwdForm.newPassword) {
            callback(new Error('两次输入的密码不一致'))
          } else {
            callback()
          }
        }, trigger: 'blur' }
      ]
    }

    const formatDate = (dateStr) => {
      if (!dateStr) return ''
      const date = new Date(dateStr)
      return date.toLocaleString('zh-CN')
    }

    const loadUserInfo = async () => {
      try {
        const currentUser = JSON.parse(localStorage.getItem('user') || '{}')
        const response = await userApi.getUserById(currentUser.id)
        if (response.success) {
          userInfo.value = response.data
        }
      } catch (error) {
        console.error('加载用户信息失败:', error)
      }
    }

    const openEditDialog = () => {
      editForm.email = userInfo.value.email
      editForm.phone = userInfo.value.phone
      editDialogVisible.value = true
    }

    const submitEdit = async () => {
      if (!editFormRef.value) return
      editFormRef.value.validate(async (valid) => {
        if (valid) {
          try {
            const response = await userApi.updateUser({
              id: userInfo.value.id,
              email: editForm.email,
              phone: editForm.phone
            })
            if (response.success) {
              ElMessage.success('信息修改成功')
              editDialogVisible.value = false
              loadUserInfo()
            } else {
              ElMessage.error(response.message || '修改失败')
            }
          } catch (error) {
            ElMessage.error('修改失败')
          }
        }
      })
    }

    const openChangePwdDialog = () => {
      pwdForm.oldPassword = ''
      pwdForm.newPassword = ''
      pwdForm.confirmPassword = ''
      pwdDialogVisible.value = true
    }

    const submitChangePwd = async () => {
      if (!pwdFormRef.value) return
      pwdFormRef.value.validate(async (valid) => {
        if (valid) {
          try {
            const response = await userApi.changePassword({
              userId: userInfo.value.id,
              oldPassword: pwdForm.oldPassword,
              newPassword: pwdForm.newPassword
            })
            if (response.success) {
              ElMessage.success('密码修改成功，请重新登录')
              pwdDialogVisible.value = false
              localStorage.removeItem('user')
              localStorage.removeItem('token')
              window.location.href = '/login'
            } else {
              ElMessage.error(response.message || '密码修改失败')
            }
          } catch (error) {
            ElMessage.error('密码修改失败')
          }
        }
      })
    }

    onMounted(() => {
      loadUserInfo()
    })

    return {
      UserIcon,
      userInfo,
      editDialogVisible,
      editForm,
      editFormRef,
      editRules,
      pwdDialogVisible,
      pwdForm,
      pwdFormRef,
      pwdRules,
      formatUserId,
      formatDate,
      openEditDialog,
      submitEdit,
      openChangePwdDialog,
      submitChangePwd
    }
  }
}
</script>

<style scoped>
.profile-page {
  padding: 20px;
  max-width: 600px;
  margin: 0 auto;
}

.profile-card {
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.profile-header {
  text-align: center;
  padding-bottom: 20px;
  border-bottom: 1px solid #eee;
  margin-bottom: 20px;
}

.avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
}

.profile-avatar {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.username {
  margin: 0;
  font-size: 24px;
  font-weight: 600;
  color: #303133;
}

.profile-content h3 {
  margin-bottom: 20px;
  color: #409eff;
  font-size: 16px;
  border-left: 4px solid #409eff;
  padding-left: 10px;
}

.info-form {
  margin-bottom: 24px;
}

.form-value {
  color: #606266;
  font-weight: 500;
}

.actions {
  display: flex;
  gap: 12px;
  justify-content: center;
}
</style>