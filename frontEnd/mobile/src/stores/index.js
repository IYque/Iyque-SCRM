import { ref, computed } from 'vue'
import { defineStore } from 'pinia'
import router from '@/router/index'
import { getUserInfo, login, getWcRedirect } from '@/api/common'
import { param2Obj, getQueryValue } from '@/utils/index'
import { useDark, useToggle } from '@vueuse/core'

let count = 0
export default defineStore('app', {
  state: () => ({
    userId: sessionStorage.userId,
    reload: true,
    isDark: useDark(),
  }),
  actions: {
    // 企业微信端授权登录
    async login() {
      //取缓存中的用户信息
      process.env.NODE_ENV === 'development' &&
        window.sysConfig.TOKEN &&
        (sessionStorage.token = window.sysConfig.TOKEN)
      if (!sessionStorage.token) {
        //缓存中没有用户信息，进入授权流程
        let code = getQueryValue('code') //是否存在code
        if (!code) {
          count++
          let url = (await getWcRedirect()).msg
          window.location.replace(url)
          return Promise.reject()
        }
        // 第三方授权重定向回来手动刷新页面
        if (code && count === 1) {
          // this.('reload') =
        }
        if (code) {
          let dataLogin = await login(code)
          // 存入sessionStorage，解决刷新重复code获取用户问题
          dataLogin.data && sessionStorage.setItem('token', dataLogin.data.token)
          // 解决授权重定向返回问题
          // window.history.go(-window.history.length + 1)
          window.location.reload()
          return Promise.reject()
        }
      }
      return
      if (sessionStorage.token && !sessionStorage.userId) {
        history.pushState({}, '') // 解决授权重定向返回问题，配合上面使用
        let dataUser = {}
        try {
          dataUser = await getUserInfo()
        } catch (error) {
          return Promise.reject()
        }
        try {
          let corpInfo = dataUser.corpInfo
          dataUser.user.weUserId && sessionStorage.setItem('userId', dataUser.user.weUserId) // 当前 登录/使用 企业员工真实姓名大驼峰 eg：QinShiHuang
          dataUser.user.userId && sessionStorage.setItem('sysId', dataUser.user.userId) // 当前系统的userId
          this.userId = sessionStorage.userId
          corpInfo.appId && sessionStorage.setItem('appId', corpInfo.appId) // 微信公众号appid
          sessionStorage.setItem('corpId', corpInfo.corpId) // 企业id
          sessionStorage.setItem('agentId', corpInfo.agentId) // 自建应用agentId
          dataUser.user.companyName &&
            sessionStorage.setItem('companyName', dataUser.user.companyName) // 公司名称
          dataUser.user.avatar && sessionStorage.setItem('avatar', dataUser.user.avatar) // 用户头像
          dataUser.user.userName && sessionStorage.setItem('userName', dataUser.user.userName) // 用户姓名
          dataUser.user.nickName && sessionStorage.setItem('nickName', dataUser.user.nickName) // 用户昵称
          dataUser.user.position && sessionStorage.setItem('position', dataUser.user.position) // 用户部门
        } catch (error) {
          alert(JSON.stringify(error))
          return Promise.reject()
        }
      }
    },
  },
  modules: {},
})
