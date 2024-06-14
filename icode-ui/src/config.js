import { getToken } from '@/utils/auth'
import { env, common } from '../sys.config'

let config = {
  get headers() {
    return { Authorization: 'Bearer ' + getToken() }
  },
}

window.sysConfig = Object.assign(config, env, common)

// 主题回显
const [h, s, l] = localStorage.hsl?.split(',')?.map((e) => e.trim()) || []
if (h) {
  const style = document.documentElement.style
  style.setProperty('--h', h)
  style.setProperty('--s', s)
  style.setProperty('--l', l)
}

// 阻止表单默认提交行为
document.addEventListener('submit', (event) => {
  event.preventDefault()
})
