import { getWxTicket, getAgentTicket } from '@/api/common'
import store from '@/stores'
import { closeToast, showLoadingToast } from 'vant'
import { getWxCode, getQueryValue } from '@/utils/index'

export default function permission(router) {
  router.beforeEach(async (to, from, next) => {
    closeToast()
    /* 路由发生变化修改页面title */
    if (to.meta.title) {
      document.title = to.meta.title
    }
    // 通过config接口注入权限验证配置
    // 所有需要使用JS-SDK的页面必须先注入配置信息，否则将无法调用（同一个url仅需调用一次，对于变化url的SPA（single-page application）的web app可在每次url变化时进行调用）
    const noAuth = to.meta ? to.meta.noAuth : false
    try {
      if (!to.meta?.noAuth) {
        showLoadingToast()
        await (to.meta?.authType === 'wechat' ? getWxCode() : store().login())
        process.env.NODE_ENV !== 'development' &&
          to.meta?.wxSDKConfigType !== 'no' &&
          (await wxConfig(to.meta?.authType))
        closeToast()
      }
      next()
    } catch (error) {
      // error && alert(JSON.stringify(error))
      closeToast()
      next(false)
    }
  })

  // router.afterEach(() => {
  //   document.getElementById('loader-wrapper').className = 'loaded'
  // })
}

function wxConfig(type) {
  try {
    return new Promise((resolve, reject) => {
      if (type === 'wechat') {
        getWxTicket(window.location.href.split('#')[0]).then(({ data }) => {
          let { timestamp, nonceStr, signature } = data
          wx.config({
            beta: true,
            debug: true,
            appId: sessionStorage.getItem('weAppId'),
            timestamp, // 必填，生成签名的时间戳
            nonceStr, // 必填，生成签名的随机串
            signature, // 必填，签名，见附录-JS-SDK使用权限签名算法
            jsApiList: ['getLocation', 'chooseImage', 'previewImage'], //必填
            openTagList: ['wx-open-launch-weapp'],
            success: (res) => {
              resolve()
            },
            fail: (res) => {
              alert('config失败:' + JSON.stringify(res))
              if (res.errMsg.indexOf('function not exist') > -1) {
                alert('版本过低请升级')
              }
              reject()
            },
          })
        })
      } else {
        getAgentTicket(window.location.href.split('#')[0]).then(({ data }) => {
          let { timestamp, nonceStr, signature } = data
          setTimeout(() => {
            wx.agentConfig({
              beta: true, // 必须这么写，否则wx.invoke调用形式的jsapi会有问题
              debug: true,
              corpid: sessionStorage.corpId, // 必填，企业微信的corpid，必须与当前登录的企业一致
              agentid: sessionStorage.agentId, // 必填，企业微信的应用id （e.g. 1000247）
              timestamp, // 必填，生成签名的时间戳
              nonceStr, // 必填，生成签名的随机串
              signature, // 必填，签名，见附录-JS-SDK使用权限签名算法
              jsApiList: [
                'sendChatMessage',
                'getContext',
                'getCurExternalContact',
                'getCurExternalChat',
                'openEnterpriseChat',
                'shareToExternalContact',
                'shareToExternalChat',
                'navigateToAddCustomer',
                'openEnterpriseChat',
                'openExistedChatWithMsg',
                'shareToExternalMoments',
              ], //必填
              success: (res) => {
                // 回调
                // toast('agentId成功:')
                resolve()
              },
              fail: (res) => {
                alert('agent config失败:' + JSON.stringify(res))
                if (res.errMsg.indexOf('function not exist') > -1) {
                  alert('版本过低请升级')
                }
                reject()
              },
            })
          }, 20)
        })
      }
    })
  } catch (error) {
    error && alert(JSON.stringify(error))
    return Promise.reject()
  }
}
