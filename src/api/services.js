import api from './index'

export const userApi = {
  register(data) {
    return api.post('/users/register', data)
  },

  login(data) {
    return api.post('/users/login', data)
  },

  getUserById(id) {
    return api.get(`/users/${id}`)
  },

  getAllUsers() {
    return api.get('/users')
  },

  searchUsers(keyword) {
    return api.get('/users/search', { params: { keyword } })
  },

  updateUser(id, data) {
    return api.put(`/users/${id}`, data)
  },

  deleteUser(id) {
    return api.delete(`/users/${id}`)
  },

  changePassword(userId, oldPassword, newPassword) {
    return api.post('/users/password', null, {
      params: { userId, oldPassword, newPassword }
    })
  }
}

export const teamApi = {
  createTeam(data) {
    return api.post('/teams', data)
  },

  updateTeam(id, data) {
    return api.put(`/teams/${id}`, data)
  },

  deleteTeam(id) {
    return api.delete(`/teams/${id}`)
  },

  getTeamById(id) {
    return api.get(`/teams/${id}`)
  },

  getTeamsByLeaderId(leaderId) {
    return api.get(`/teams/leader/${leaderId}`)
  },

  getAllTeams() {
    return api.get('/teams')
  },

  searchTeams(keyword) {
    return api.get('/teams/search', { params: { keyword } })
  },

  joinTeam(teamId, userId) {
    return api.post('/teams/join', null, { params: { teamId, userId } })
  },

  applyTeam(params) {
    return api.post('/team-applications/apply', null, { params })
  },

  applyCreateTeam(params) {
    return api.post('/team-applications/apply-create', null, { params })
  },

  getApplicationsByUserId(userId) {
    return api.get(`/team-applications/user/${userId}`)
  },

  getAllApplications() {
    return api.get('/team-applications')
  },

  approveApplication(id) {
    return api.put(`/team-applications/${id}/approve`)
  },

  rejectApplication(id) {
    return api.put(`/team-applications/${id}/reject`)
  }
}

export const taskApi = {
  createTask(data) {
    return api.post('/tasks', data)
  },

  updateTask(id, data) {
    return api.put(`/tasks/${id}`, data)
  },

  deleteTask(id) {
    return api.delete(`/tasks/${id}`)
  },

  getTaskById(id) {
    return api.get(`/tasks/${id}`)
  },

  getTasksByTeamId(teamId) {
    return api.get(`/tasks/team/${teamId}`)
  },

  getTasksByAssigneeId(assigneeId) {
    return api.get(`/tasks/assignee/${assigneeId}`)
  },

  getTasksByCreatorId(creatorId) {
    return api.get(`/tasks/creator/${creatorId}`)
  },

  getAllTasks() {
    return api.get('/tasks')
  },

  searchTasks(keyword) {
    return api.get('/tasks/search', { params: { keyword } })
  },

  getTasksByStatus(status) {
    return api.get(`/tasks/status/${status}`)
  },

  updateTaskStatus(id, status) {
    return api.patch(`/tasks/${id}/status`, null, { params: { status } })
  },

  claimTask(id, userId) {
    return api.patch(`/tasks/${id}/claim`, null, { params: { userId } })
  }
}