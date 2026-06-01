<template>
  <div class="user-manage">
    <div class="header">
      <h3>用户管理</h3>
      <div class="search-bar">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索用户名或邮箱"
          style="width: 300px"
          @keyup.enter="handleSearch"
        >
          <template #append>
            <el-button :icon="SearchIcon" @click="handleSearch" />
          </template>
        </el-input>
        <el-button type="primary" :icon="PlusIcon" @click="handleAdd">添加用户</el-button>
      </div>
    </div>

    <el-table :data="userList" border stripe style="width: 100%">
      <el-table-column prop="id" label="ID" width="80">
        <template #default="scope">{{ formatUserId(scope.row.id) }}</template>
      </el-table-column>
      <el-table-column prop="username" label="用户名" />
      <el-table-column prop="email" label="邮箱" />
      <el-table-column prop="phone" label="手机号" />
      <el-table-column prop="role" label="角色" width="100">
        <template #default="scope">
          <el-tag :type="scope.row.role === 'ADMIN' ? 'danger' : 'success'">
            {{ scope.row.role }}
          </el-tag>
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

    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑用户' : '添加用户'"
      width="500px"
    >
      <el-form :model="userForm" :rules="rules" ref="userFormRef" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="userForm.username" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="密码" :prop="isEdit ? '' : 'password'">
          <el-input
            v-model="userForm.password"
            type="password"
            :placeholder="isEdit ? '留空则不修改密码' : '请输入密码'"
          />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="userForm.email" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="userForm.phone" />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="userForm.role" style="width: 100%">
            <el-option label="普通用户" value="USER" />
            <el-option label="管理员" value="ADMIN" />
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
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Plus, Edit, Delete } from '@element-plus/icons-vue'
import { userApi } from '../api/services'
import { formatUserId } from '../utils/idFormatter'

export default {
  name: 'UserManage',
  setup() {
    const SearchIcon = markRaw(Search)
    const PlusIcon = markRaw(Plus)
    const EditIcon = markRaw(Edit)
    const DeleteIcon = markRaw(Delete)
    const searchKeyword = ref('')
    const userList = ref([])
    const dialogVisible = ref(false)
    const isEdit = ref(false)
    const userFormRef = ref(null)

    const userForm = reactive({
      id: null,
      username: '',
      password: '',
      email: '',
      phone: '',
      role: 'USER'
    })

    const rules = {
      username: [
        { required: true, message: '请输入用户名', trigger: 'blur' }
      ],
      password: [
        { required: true, message: '请输入密码', trigger: 'blur' },
        { min: 6, message: '密码长度不能少于 6 位', trigger: 'blur' }
      ],
      email: [
        { required: true, message: '请输入邮箱', trigger: 'blur' },
        { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
      ],
      phone: [
        { required: true, message: '请输入手机号', trigger: 'blur' }
      ],
      role: [
        { required: true, message: '请选择角色', trigger: 'change' }
      ]
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
        loadUsers()
        return
      }
      try {
        const response = await userApi.searchUsers(searchKeyword.value)
        if (response.success) {
          userList.value = response.data
        }
      } catch (error) {
        ElMessage.error('搜索失败')
      }
    }

    const handleAdd = () => {
      isEdit.value = false
      Object.assign(userForm, {
        id: null,
        username: '',
        password: '',
        email: '',
        phone: '',
        role: 'USER'
      })
      dialogVisible.value = true
    }

    const handleEdit = (row) => {
      isEdit.value = true
      Object.assign(userForm, {
        id: row.id,
        username: row.username,
        password: '',
        email: row.email,
        phone: row.phone,
        role: row.role
      })
      dialogVisible.value = true
    }

    const handleSubmit = async () => {
      try {
        await userFormRef.value.validate()

        if (isEdit.value) {
          const response = await userApi.updateUser(userForm.id, {
            username: userForm.username,
            email: userForm.email,
            phone: userForm.phone,
            role: userForm.role
          })
          if (response.success) {
            ElMessage.success('更新成功')
            dialogVisible.value = false
            loadUsers()
          }
        } else {
          const response = await userApi.register(userForm)
          if (response.success) {
            ElMessage.success('添加成功')
            dialogVisible.value = false
            loadUsers()
          }
        }
      } catch (error) {
        ElMessage.error(error.message || '操作失败')
      }
    }

    const handleDelete = (id) => {
      ElMessageBox.confirm('确定要删除该用户吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          const response = await userApi.deleteUser(id)
          if (response.success) {
            ElMessage.success('删除成功')
            loadUsers()
          }
        } catch (error) {
          ElMessage.error('删除失败')
        }
      }).catch(() => {})
    }

    const formatDate = (date) => {
      if (!date) return ''
      return new Date(date).toLocaleString('zh-CN')
    }

    onMounted(() => {
      loadUsers()
    })

    return {
      SearchIcon,
      PlusIcon,
      EditIcon,
      DeleteIcon,
      searchKeyword,
      userList,
      dialogVisible,
      isEdit,
      userForm,
      rules,
      userFormRef,
      handleSearch,
      handleAdd,
      handleEdit,
      handleSubmit,
      handleDelete,
      formatDate,
      formatUserId
    }
  }
}
</script>

<style scoped>
.user-manage {
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
</style>