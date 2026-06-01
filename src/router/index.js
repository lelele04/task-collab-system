import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/Login.vue'
import Register from '../views/Register.vue'
import AdminDashboard from '../views/AdminDashboard.vue'
import UserDashboard from '../views/UserDashboard.vue'
import AdminUserManage from '../views/UserManage.vue'
import AdminTeamManage from '../views/TeamManage.vue'
import AdminTaskManage from '../views/TaskManage.vue'
import UserTeamList from '../views/UserTeamList.vue'
import UserTaskList from '../views/UserTaskList.vue'
import AdminHome from '../views/AdminHome.vue'
import UserHome from '../views/UserHome.vue'
import Profile from '../views/Profile.vue'

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: Login
  },
  {
    path: '/register',
    name: 'Register',
    component: Register
  },
  {
    path: '/admin',
    component: AdminDashboard,
    meta: { requiresAuth: true, requiresRole: 'ADMIN' },
    children: [
      {
        path: 'dashboard',
        name: 'AdminDashboard',
        component: AdminHome
      },
      {
        path: 'users',
        name: 'AdminUserManage',
        component: AdminUserManage
      },
      {
        path: 'teams',
        name: 'AdminTeamManage',
        component: AdminTeamManage
      },
      {
        path: 'tasks',
        name: 'AdminTaskManage',
        component: AdminTaskManage
      },
      {
        path: 'profile',
        name: 'AdminProfile',
        component: Profile
      }
    ]
  },
  {
    path: '/user',
    component: UserDashboard,
    meta: { requiresAuth: true, requiresRole: 'USER' },
    children: [
      {
        path: 'dashboard',
        name: 'UserDashboard',
        component: UserHome
      },
      {
        path: 'teams',
        name: 'UserTeamList',
        component: UserTeamList
      },
      {
        path: 'tasks',
        name: 'UserTaskList',
        component: UserTaskList
      },
      {
        path: 'profile',
        name: 'UserProfile',
        component: Profile
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const user = localStorage.getItem('user') ? JSON.parse(localStorage.getItem('user')) : null
  
  if (to.meta.requiresAuth && !token) {
    next('/login')
    return
  }
  
  if (to.meta.requiresAuth && user) {
    const requiredRole = to.meta.requiresRole
    if (requiredRole && user.role !== requiredRole) {
      if (user.role === 'ADMIN') {
        next('/admin/dashboard')
      } else {
        next('/user/dashboard')
      }
      return
    }
  }
  
  next()
})

export default router