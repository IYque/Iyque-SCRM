import { config } from '../sys.config'
const BASE_URL = import.meta.env.BASE_URL

// 系统常量
window.sysConfig = {
  ...config,
  ...(globalThis.sysConfig || {}),

  BASE_URL,
  RUN_ENV: config.RUN_ENV,
  TOKEN:
    'eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJOYUxhbiIsImlhdCI6MTc1MDU4ODcxNywiZXhwIjoxNzUxMTkzNTE3fQ.MNXgHa_mgXWSwG9x81s_iUmVADZoS5zbwbcfCYO_Bo-RtuHkMuJdM3VmKv51TckQFssKh2qY_nplvKfDNtKcVA',

  services: {
    wecom: '/open',
    weChat: '/wx-api',
    ai: '/ai',
  },

  // 以下仅用于系统信息展示，不作为项目变量使用，请勿在代码中使用
  _packDateTime: __PACK_DATETIME__, // 打包时间
  _mode: import.meta.env.MODE, // 前端打包模式
}
process.env.NODE_ENV === 'development' && window.sysConfig.TOKEN && (sessionStorage.token = window.sysConfig.TOKEN)
// 统一为img的src不是绝对地址的拼接接口地址
document.addEventListener(
  'error',
  function (e) {
    let target = e.target
    let src = target.attributes.getNamedItem('src').value
    if (target.tagName.toUpperCase() === 'IMG' && src && !src.includes('http')) {
      target.src = window.sysConfig.BASE_API + '/file/fileView/' + src
      e.stopPropagation()
    }
  },
  true,
)
