import Home from '../views/Home.vue'
let noAuth = process.env.NODE_ENV == 'development' // 设置页面是否关闭授权登录鉴权，用于开发静态页面统一配置
/**
 * 路由字段说明
 * meta:{
 *   title: 页面标题
 *   noAuth: 设置页面是否关闭授权登录鉴权，用于本地开发静态页面调试和某些不要登录的页面
 *   authType: 登录类型： 企微登录（默认）， wechat(微信登录)
 *   wxSDKConfigType:  wx JS SDK config类型; 取值: 无值默认(企微端), wechat(微信端), no(不调用sdk config配置)
 * }
 */

export default [
  {
    path: '/',
    name: 'Home',
    component: Home,
    redirect: process.env.NODE_ENV === 'development' ? '' : '/' + window.location.pathname.split('/').pop(),
    meta: {
      title: '企微助手',
      noAuth: true,
    },
  },

  //  客户投诉
  {
    path: '/customerComplaint',
    name: 'customerComplaint',
    component: () => import('../views/customerComplaint/index.vue'),
    meta: {
      title: '客户投诉',
      // noAuth
      wxSDKConfigType: 'no',
    },
  },
  //  客户投诉详情
  {
    path: '/customerComplaintDetail',
    name: 'customerComplaintDetail',
    component: () => import('../views/customerComplaint/detail.vue'),
    meta: {
      title: '投诉详情',
      // noAuth
      wxSDKConfigType: 'no',
    },
  },
  //  客户投诉表单
  {
    path: '/customerComplaintForm',
    name: 'customerComplaintForm',
    component: () => import('../views/customerComplaint/form.vue'),
    meta: {
      title: '1',
      noAuth: true,
      wxSDKConfigType: 'no',
    },
  },

  // 聊天素材
  {
    path: '/chat',
    name: 'chat',
    component: () => import('../views/chat/index.vue'),
    meta: {
      title: '聊天素材',
      noAuth,
    },
  },
  // 组合话术
  {
    path: '/combinedRhetoric',
    name: 'combinedRhetoric',
    component: () => import('../views/combinedRhetoric/index.vue'),
    meta: {
      title: '待办',
      noAuth,
    },
  },

  // 客户公海待办
  {
    path: '/customerSea',
    name: 'customerSea',
    component: () => import('../views/customerSea/index.vue'),
    meta: {
      title: '待办',
      noAuth,
    },
  },

  // 解决授权重定向返回问题
  { path: '/:path(.*)*', redirect: '/', hidden: true },
]
