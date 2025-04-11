import { config } from '../sys.config'
const BASE_URL = import.meta.env.BASE_URL

// 系统常量
window.sysConfig = {
  ...config,
  ...(globalThis.sysConfig || {}),

  BASE_URL,
  RUN_ENV: config.RUN_ENV,
  TOKEN: '', // 本地开发调试用的token，仅本地开发环境生效

  services: {
    wecom: '/open',
    weChat: '/wx-api',
    ai: '/ai',
  },

  // 以下仅用于系统信息展示，不作为项目变量使用，请勿在代码中使用
  _packDateTime: __PACK_DATETIME__, // 打包时间
  _mode: import.meta.env.MODE, // 前端打包模式
}
