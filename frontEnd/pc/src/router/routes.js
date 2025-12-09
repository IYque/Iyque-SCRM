/* Layout */
import { RouterView } from 'vue-router'
import { h } from 'vue'
import Layout from '@/layout/index.vue'
const routerView = {
  render: () => h(RouterView),
}
/**
 * Note: 路由配置项
 *
 * hidden: true                   // 当设置 true 的时候该路由不会再侧边栏出现 如401，login等页面，或者如一些编辑页面/edit/1
 * alwaysShow: true               // 当你一个路由下面的 children 声明的路由大于1个时，自动会变成嵌套的模式--如组件页面
 *                                // 只有一个时，会将那个子路由当做根路由显示在侧边栏--如引导页面
 *                                // 若你想不管路由下面的 children 声明的个数都显示你的根路由
 *                                // 你可以设置 alwaysShow: true，这样它就会忽略之前定义的规则，一直显示根路由
 * redirect: noRedirect           // 当设置 noRedirect 的时候该路由在面包屑导航中不可被点击
 * name:'router-name'             // 设定路由的名字，一定要填写不然使用<keep-alive>时会出现各种问题
 * meta : {
 roles: ['admin','editor']    // 设置该路由进入的权限，支持多个权限叠加
 title: 'title'               // 设置该路由在侧边栏和面包屑中展示的名字
 icon: 'svg-name'             // 设置该路由的图标，对应路径src/icons/svg
 breadcrumb: false            // 如果设置为false，则不会在breadcrumb面包屑中显示
 }
 */

export const navRoutes = [
  {
    path: '/customerLink',
    component: Layout,
    redirect: '/customerLink/index',
    meta: { title: '获客-私域获客-快速构建企业专属客户池', icon: 'customer' },
    children: [
      {
        path: '/customerLink',
        component: () => import('@/views/liveCode/index'),
        meta: { title: '获客外链' },
      },
      {
        path: '/index',
        component: () => import('@/views/liveCode/index'),
        meta: { title: '员工活码' },
      },
      {
        path: '/groupCode',
        component: () => import('@/views/groupCode/index'),
        meta: { title: '客群活码' },
      },
      {
        path: 'customerSea',
        component: () => import('@/views/customerSea/index'),
        meta: { title: '客户公海' },
      },
    ],
  },

  {
    path: '/manage',
    component: Layout,
    redirect: '/manage/customer',
    meta: { title: '管理-私域管理-高效维护企业客户/客群', icon: 'system' },
    children: [
      {
        path: 'customer',
        component: () => import('@/views/customer/index'),
        meta: { title: '客户列表' },
      },
      {
        path: 'user',
        component: () => import('@/views/user/index'),
        meta: { title: '员工列表' },
      },
      {
        path: 'groupChat',
        component: () => import('@/views/groupChat/index'),
        meta: { title: '客群列表' },
      },
      {
        path: 'customerTag',
        component: () => import('@/views/customerTag/index'),
        meta: { title: '客户标签' },
      },
       {
        path: 'groupTag',
        component: () => import('@/views/groupTag/index'),
        meta: { title: '客群标签' },
      },
    ],
  },

  {
    path: '/marketing',
    component: Layout,
    redirect: '/marketing/massMarketing/group',
    meta: { title: '营销-私域营销-高效触达私域客户', icon: 'marketing' },
    children: [
      {
        path: 'massMarketing',
        // component: () => import('@/views/massMarketing/index'),
        meta: { title: '群发营销' },
        children: [
          {
            path: 'customer',
            component: () => import('@/views/massMarketing/list'),
            meta: { title: '客户群发' },
          },
          {
            path: 'group',
            component: () => import('@/views/massMarketing/list'),
            meta: { title: '客群群发' },
          },
        ],
      },
      {
        path: 'H5Marketing',
        meta: { title: 'H5营销' },
        redirect: '/marketing/H5Marketing/list',
        children: [
          {
            path: 'list',
            component: () => import('@/views/H5Marketing/list'),
            meta: { title: '列表', hidden: true },
          },
          {
            path: 'aev',
            component: () => import('@/views/H5Marketing/aev'),
            meta: { title: '{新建}', hidden: true },
          },
          {
            path: 'detail',
            component: () => import('@/views/H5Marketing/detail'),
            meta: { title: '详情', hidden: true },
          },
        ],
      },
    ],
  },

  {
    path: '/content',
    component: Layout,
    redirect: '/content/commonMaterial',
    meta: { title: '内容-内容中心-企业私有知识库', icon: 'content' },
    children: [
      {
        path: 'commonMaterial',
        component: () => import('@/views/contentCenter/commonMaterial/index'),
        meta: { title: '普通素材' },
      },
      {
        path: 'combinedRhetoric',
        redirect: '/content/combinedRhetoric/index',
        meta: { title: '组合话术' },
        children: [
          {
            path: 'index',
            component: () => import('@/views/contentCenter/combinedRhetoric/index'),
            hidden: true,
            meta: { title: '组合话术', hidden: true },
          },
          {
            path: 'aev',
            component: () => import('@/views/contentCenter/combinedRhetoric/aev'),
            hidden: true,
            meta: { title: '{}组合话术', hidden: true },
          },
        ],
      },
    ],
  },

  {
    path: '/customerService',
    component: Layout,
    redirect: '/customerService/corpCustomerService/customerServiceManage',
    meta: { title: '客服-AI会话存档-企微会话内容存档', icon: 'customerService' },
    children: [
      {
        path: 'corpCustomerService',
        meta: { title: '企微客服' },
        children: [
          {
            path: 'customerServiceManage',
            component: () => import('@/views/customerServiceManage/list'),
            meta: { title: '基础客服' },
          },
          {
            path: 'customerServiceManageScheduled',
            component: () => import('@/views/customerServiceManageScheduled/list'),
            meta: { title: '排班客服' },
          },
          {
            path: 'KBM',
            component: () => import('@/views/KBM/index'),
            meta: { title: 'AI知识库管理' },
          },
          {
            path: 'serviceRecord',
            component: () => import('@/views/serviceRecord/index'),
            meta: { title: '服务记录' },
          },
          {
            path: 'sessionSummary',
            component: () => import('@/views/sessionSummary/index'),
            meta: { title: 'AI会话总结' },
          },
        ],
      },

      {
        path: 'complaint',
        meta: { title: '客户投诉' },
        children: [
          {
            path: 'complaintManage',
            component: () => import('@/views/complaintManage/index'),
            meta: { title: '投诉管理' },
          },
        ],
      },

      // {
      //   path: 'customerServiceManage',
      //   component: () => import('@/views/customerServiceManage/list'),
      //   meta: { title: '客服管理' },
      // },
      // {
      //   path: 'serviceRecord',
      //   component: () => import('@/views/serviceRecord/index'),
      //   meta: { title: '服务记录' },
      // },
      // {
      //   path: 'KBM',
      //   component: () => import('@/views/KBM/index'),
      //   meta: { title: '知识库管理' },
      // },
      // {
      //   path: 'complaintManage',
      //   component: () => import('@/views/complaintManage/index'),
      //   meta: { title: '投诉管理' },
      // },
    ],
  },
  {
    path: '/chat',
    component: Layout,
    redirect: '/chat/khchat/index',
    meta: { title: 'AI会话-AI会话存档-企微会话内容存档', icon: 'conversation' },
    children: [
      {
        path: 'khchat',
        meta: { title: '客户会话' },
        children: [
          {
            path: 'index',
            component: () => import('@/views/chat/index'),
            meta: { title: '会话内容' },
          },
          {
            path: 'inquiryCustomer',
            component: () => import('@/views/inquiryCustomer/index'),
            meta: { title: 'AI会话预审' },
          },

          {
            path: 'intentionCustomer',
            component: () => import('@/views/intentionCustomer/index'),
            meta: { title: 'AI意向客户' },
          },
        ],
      },

      {
        path: 'kqchat',
        meta: { title: '客群会话' },
        children: [
          {
            path: 'kqchat',
            component: () => import('@/views/kqchat/index'),
            meta: { title: '会话内容' },
          },
          {
            path: 'inquiryKqChat',
            component: () => import('@/views/inquiryKqChat/index'),
            meta: { title: 'AI会话预审' },
          },
          {
            path: 'intentionGroupFriend',
            component: () => import('@/views/intentionGroupFriend/index'),
            meta: { title: 'AI意向群友' },
          },
        ],
      },

      {
        path: 'conversationalInsights',
        redirect: '/chat/conversationalInsights/hotWordManage',
        meta: { title: '会话洞察' },
        children: [
          {
            path: 'hotWordManage',
            component: () => import('@/views/hotWordManage/index'),
            meta: { title: '热词管理' },
          },
          {
            path: 'hotWordAnalysis',
            component: () => import('@/views/hotWordManage/statistics'),
            meta: { title: '热词分析' },
          },
        ],
      },
    ],
  },
  // {
  //   path: '/chat',
  //   component: Layout,
  //   redirect: '/chat/index',
  //   meta: { title: 'AI会话-AI会话存档-企微会话内容存档', icon: 'conversation' },
  //   children: [
  //     {
  //       path: 'index',
  //       component: () => import('@/views/chat/index'),
  //       meta: { title: 'AI客户会话' },
  //     },
  //     {
  //       path: 'kqchat',
  //       component: () => import('@/views/kqchat/index'),
  //       meta: { title: 'AI客群会话' },
  //     }
  //   ],
  // },
  {
    path: '/config',
    component: Layout,
    redirect: '/config/index',
    meta: { title: '配置-配置中心-企微&系统配置', icon: 'config' },
    children: [
      {
        path: 'index',
        component: () => import('@/views/config/index'),
        meta: { title: '系统配置' },
      },
      {
        path: 'operateLog',
        component: () => import('@/views/operateLog/index'),
        meta: { title: '操作日志' },
      },
      {
        path: 'groupRobot',
        component: () => import('@/views/groupRobot/index'),
        meta: { title: '群机器人' },
      },
      {
        path: 'enterpriseNotice',
        component: () => import('@/views/groupRobot/enterpriseNotice/index'),
        meta: { title: '企业公告' },
      },
      {
        path: 'fileSecurity',
        component: () => import('@/views/fileSecurity/index'),
        meta: { title: '文件安全' },
      },
      {
        path: 'screenShot',
        component: () => import('@/views/screenShot/index'),
        meta: { title: '截屏安全' },
      },
    ],
  },
]

// 公共路由
export const constantRoutes = navRoutes.concat([
  {
    path: '/',
    component: () => import('@/layout/visitor'),
    redirect: navRoutes[0].path,
    hidden: true,
    children: [
      {
        path: '/login',
        component: () => import('@/views/system/login'),
      },
      {
        path: '/register',
        component: () => import('@/views/system/register'),
      },
    ],
  },
  {
    path: '/authRedirect',
    component: () => import('@/views/system/login/authRedirect'),
    hidden: true,
  },
  {
    path: '/404',
    component: () => import('@/views/system/error/404'),
    hidden: true,
  },
  {
    path: '/401',
    component: () => import('@/views/system/error/401'),
    hidden: true,
  },
])
