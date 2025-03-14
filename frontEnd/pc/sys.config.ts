// 环境变量
const envs = {
  development: {
    DOMAIN: 'http://127.0.0.1:8085',
    BASE_URL: '/tools/',
    BASE_API: 'http://127.0.0.1:8085',
  },
  production: {
    DOMAIN: 'https://show.iyque.cn',
    BASE_URL: '/tools/',
    BASE_API: 'https://show.iyque.cn/iyque',
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
    '<a href="https://www.iyque.cn?utm_source=iyquecode" target="_blank">源雀Scrm-是基于Java源码交付的企微SCRM,帮助企业构建高度自由安全的私域平台.</a> ', // 系统标语
  COPYRIGHT: 'Copyright © 2024 源雀 All Rights Reserved.', // 版权信息
  LOGO: env.BASE_URL + 'static/logo.png', // 深色logo
}
