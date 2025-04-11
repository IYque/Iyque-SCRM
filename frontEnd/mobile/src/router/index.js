import { createRouter, createWebHistory, createWebHashHistory } from 'vue-router'
import routes from './routes'
import permission from './permission'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
})
permission(router)

export default router
