import './config.js'
import 'normalize.css/normalize.css'

import VConsole from 'vconsole'
new VConsole()
setTimeout(() => {
  document.getElementById('__vconsole').style.display = 'none'
}, 50)
document.addEventListener('touchstart', function (e) {
  if (e.touches.length == 5) {
    let style = document.getElementById('__vconsole').style
    style.display = style.display == 'none' ? 'block' : 'none'
  }
})
document.addEventListener('keydown', function (e) {
  if (e.key === 'F12') {
    let style = document.getElementById('__vconsole').style
    style.display = style.display == 'none' ? 'block' : 'none'
  }
})

import { createApp, defineAsyncComponent } from 'vue'
import { createPinia } from 'pinia'
import router from './router'
import App from './App.vue'
const app = createApp(App)
app.use(createPinia())
app.use(router)

import Vant, { setToastDefaultOptions, setDialogDefaultOptions, showToast, showDialog } from 'vant'
import 'vant/lib/index.css'
app.use(Vant)
setDialogDefaultOptions({
  title: '系统提示',
  width: '68vw',
  //  confirmButtonColor: color
})
setToastDefaultOptions('loading', {
  message: '加载中...',
  forbidClick: true,
  loadingType: 'spinner',
  duration: 0,
})

import './styles/index.scss'
import 'tailwindcss/index.css'

// 全局方法挂载
import { SDK, $sdk } from './utils/sdk'
declare module 'vue' {
  interface ComponentCustomProperties {
    $sdk: SDK
  }
}
Object.assign(app.config.globalProperties, { $sdk }, { sysConfig: window.sysConfig })

// 全局异步组件挂载，
const components = import.meta.glob('./components/**/*.vue')
for (const c in components) {
  // console.log(c.match(/\/([^\/]+)\.vue$/)[1])
  app.component(c.match(/\/([^\/]+)\.vue$/)[1], defineAsyncComponent(components[c]))
}

import { useDark } from '@vueuse/core'
useDark()

import 'virtual:svg-icons-register'
import SvgIcon from '@/components/SvgIcon.vue'
app.component('SvgIcon', SvgIcon)

app.mount('#app')
