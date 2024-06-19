// 环境变量
const envs = {
  development: {
    DOMAIN: 'https://show.iyque.cn', // 站点域名，会根据此处域名判断应用环境
    BASE_URL: '/tools/', // 页面路由基础路径 /*/*/，eg：/a/
    BASE_API: 'https://show.iyque.cn/iyque', // 接口基础路径
  },
  production: {
    DOMAIN: 'https://iyque.cn',
    BASE_URL: '/tools/',
    BASE_API: 'https://iyque.cn/qapi',
  },
}

let mode =
  process.env.NODE_ENV == 'development' || !globalThis.document
    ? process.env.VUE_APP_ENV
    : Object.keys(envs).find((e) => envs[e].DOMAIN === window?.location.origin)

export const env = { ...envs[mode], ENV: mode }

// 系统常量配置
export const common = {
  SYSTEM_NAME: '源雀', // 系统简称
  SYSTEM_SLOGAN:
    '<a href="https://www.iyque.cn?utm_source=iyquecode" target="_blank">源雀SCRM -基于SpringCloud+Vue架构,100%开放源码的企微私域营销系统</a> ', // 系统标语
  COPYRIGHT: 'Copyright © 2024 源雀 All Rights Reserved.', // 版权信息
  LOGO: env.BASE_URL + 'static/logo.png', // 深色logo
}
