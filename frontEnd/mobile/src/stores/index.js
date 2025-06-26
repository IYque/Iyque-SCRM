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

      if (!sessionStorage.token) {
        //缓存中没有用户信息，进入授权流程
        let code = getQueryValue('code') //是否存在code
        if (!code) {
          count++
          let url = (await getWcRedirect()).msg

          let t = setInterval(() => {
            if (sessionStorage.token) {
              window.location.reload()
              history.pushState({}, '') // 解决授权重定向返回问题
              clearInterval(t)
            }
          }, 300)

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
          window.history.go(-window.history.length + 1)
          // window.location.reload()
          return Promise.reject()
        }
      }
      if (sessionStorage.token && !sessionStorage.corpId) {
        let dataUser = {}
        try {
          dataUser = await getUserInfo()
        } catch (error) {
          return Promise.reject()
        }
        try {
          sessionStorage.setItem('corpId', dataUser.corpId) // 企业id
          sessionStorage.setItem('agentId', dataUser.agentId) // 自建应用agentId
        } catch (error) {
          alert(JSON.stringify(error))
          return Promise.reject()
        }
      }
    },
  },
  modules: {},
})
