<template>
  <div class="content-box">
    <van-button style="margin: 15px" type="primary" v-if="$route.query.isBack" @click="$router.back()">返回</van-button>
    <!-- 图文 -->
    <div v-if="contentObj.mediaType == '9'">
      <div class="title">{{ contentObj.materialName }}</div>
      <!-- <div class="pic">{{ contentObj.coverUrl }}</div> -->
      <van-image class="pic" :src="contentObj.coverUrl" v-if="contentObj.coverUrl" />
      <div class="content">{{ contentObj.content }}</div>
    </div>
    <!-- 文件，外链 -->
    <iframe :src="fileUrl" class="iframeClass" v-else-if="['3', 'file', '19'].includes(contentObj.mediaType)"></iframe>
    <!-- 文章 -->
    <div v-else-if="contentObj.mediaType == '12'">
      <div class="title">{{ contentObj.materialName }}</div>
      <p v-html="contentObj.content"></p>
    </div>
    <!-- 视频 -->
    <div v-else-if="['2', 'video'].includes(contentObj.mediaType)">
      <video class="video" controls webkit-playsinline="true" playsinline="true" :autoplay="false" preload="auto">
        <!-- <video
        class="video"
        controls
        webkit-playsinline="true"
        playsinline="true"
        :autoplay="false"
        preload="auto"
        :poster="contentObj.coverUrl"
      > -->
        <source :src="contentObj.materialUrl" type="video/mp4" />
      </video>
    </div>
    <!-- 海报 -->
    <div v-else-if="contentObj.mediaType == '5'">
      <van-image class="pic" :src="contentObj.materialUrl" v-if="contentObj.materialUrl" />
    </div>

    <!-- <van-popup v-model="show" round :close-on-click-overlay="false" class="show-pop">
      <h2>提示</h2>
      <p class="show-content">
        <span>
          我们代表企业用户申请获取您的查阅素材的查看次数、时长等信息。您可通过访问
          <a target="_blank" :href="privacyPolicy" class="light">《隐私政策》</a>
          查阅完整隐私政策内容。
        </span>
      </p>

      <p class="show-content">
        <span>
          您点击”同意“即表示您已阅读并同意我们
          <a target="_blank" :href="privacyPolicy" class="light">《隐私政策》</a>
          处理您的个人信息，我们承诺将全力保护您的个人信息；如您点击不同意，将可能导致后续无法继续使用我们的产品和服务。
        </span>
      </p>
      <div class="footer">
        <span @click="show = false">不同意</span>
        <span @click="agree">同意</span>
      </div>
    </van-popup> -->
  </div>
</template>
<script>
import { getWxCode } from '@/utils/index.js'
import { getList } from './api'

export default {
  data() {
    return {
      contentObj: {},
      show: false,
      materiaId: '', // 前端传过来的素材id
      // startTime: 0,
      // endTime: 0,
      query: {
        contentId: '',
        openid: '',
        unionid: '',
        sendUserId: '',
        viewWatchTime: null,
        resourceType: '',
      },
      fileUrl: '',
      url: '', //H5链接传来的url
      type: '', // H5链接传来的类型
      otherModle: false,
      privacyPolicy: '',
      ws: '',
    }
  },
  created() {
    this.materiaId = this.$route.query.materiaId
    this.otherModle = this.$route.query.otherModle
    this.query.sendUserId = this.$route.query.userId
    this.query.talkId = this.$route.query.talkId

    this.url = this.$route.query.materialUrl
    this.type = this.$route.query.mediaType

    // this.getMaterialId(this.materiaId)
    if (this.url && this.type) {
      this.contentObj = {
        mediaType: this.type,
        materialUrl: this.url,
      }
      // 文件
      if (this.type === 'file') {
        document.querySelector('meta[name=viewport]').content = ''
        this.fileUrl = window.sysConfig.PRIVIEW_URL
          ? window.sysConfig.PRIVIEW_URL + encodeURIComponent(this.base64Encode(this.url))
          : this.url
      }
    } else if (
      !this.otherModle &&
      navigator.userAgent.match(/micromessenger/i) &&
      !navigator.userAgent.includes('wxwork')
    ) {
      // 非其他模块且是微信环境
      getWxCode()
        .then(({ openId, unionId, nickName, avatar }) => {
          try {
            this.query.openid = openId
            this.query.unionid = unionId
            this.query.nickName = nickName
            this.query.avatar = avatar
            if (openId) {
              let obj = { openid: openId }
              getOpenId(obj).then((res) => {
                this.getMaterialId(this.materiaId).then(() => this.addView())
                // // 0未授权，1已授权
                // if (res.data === 0) {
                //   // 弹出隐私政策弹框
                //   this.show = true
                // } else if (res.data == 1) {
                //   this.getMaterialId(this.materiaId)
                // }
              })
            } else {
              this.$toast.clear()
            }
          } catch (error) {
            this.$toast(JSON.stringify(error))
          }
        })
        .catch((err) => {
          // this.$toast('授权异常，请刷新重试')
        })
    } else {
      // 其他模块和环境不需要统计
      this.getMaterialId(this.materiaId)
    }
  },
  destroyed() {
    this.pageCloseEventListener('removeEventListener')
  },
  mounted() {
    // this.dealPolicy()
  },
  methods: {
    // dealPolicy() {
    //   this.privacyPolicy =
    //     window.sysConfig.PRIVIEW_URL + encodeURIComponent(this.base64Encode(window.sysConfig.PRIVACY_POLICY))
    // },
    // agree() {
    //   this.show = false
    //   this.getMaterialId(this.materiaId)
    //   this.saveInfo()
    // },
    // // 保存客户授权信息
    // saveInfo() {
    //   let obj = { openid: this.query.openid, unionid: this.query.unionid }
    //   saveInfo(obj).then((res) => {
    //     console.log(res)
    //   })
    // },
    getMaterialId(id) {
      return getMaterialId(id).then(({ data }) => {
        this.contentObj = data
        if (data.materialName) {
          document.title = data.materialName
        }
        this.query.resourceType = data.resourceType
        this.query.contentId = data.id
        if (data.mediaType === '3') {
          // 文件
          document.querySelector('meta[name=viewport]').content = ''
          this.fileUrl = window.sysConfig.PRIVIEW_URL + encodeURIComponent(this.base64Encode(data.materialUrl))
        } else if (['19'].includes(data.mediaType)) {
          this.fileUrl = data.materialUrl
        }
      })
    },
    base64Encode(str) {
      if (!str) return
      return btoa(
        encodeURIComponent(str).replace(/%([0-9A-F]{2})/g, function toSolidBytes(match, p1) {
          return String.fromCharCode('0x' + p1)
        }),
      )
    },
    addView() {
      this.pageCloseEventListener('addEventListener')
      this.ws = new WebSocket(
        'wss://' + window.sysConfig.BASE_API.match(/\/\/([^\/]+)/)[1] + '/ws/wx-api/material/websocket/action/addView',
        [sessionStorage.token],
      )
      this.ws.onopen = () => {
        // type:0心跳 1数据
        this.ws.send(JSON.stringify({ type: 1, ...this.query }))
      }

      // 每15秒发送心跳，预防nginx超时
      setInterval(() => {
        this.ws.readyState == 1 && this.ws.send(JSON.stringify({ type: 0 }))
      }, 15000)
    },
    pageCloseEventListener(type = 'addEventListener') {
      document[type](document.visibilityState ? 'visibilitychange' : 'pagehide', this.close)
    },
    close() {
      document.visibilityState || this.ws.close()
      document.visibilityState === 'hidden' && this.ws.close()
    },
  },
}
</script>

<style lang="scss" scoped>
// .show-pop {
//   padding: 10px;
//   h2 {
//     margin-bottom: 20px;
//   }
//   p {
//     margin-bottom: 20px;
//   }
// }
// .show-content {
//   width: 220px;
//   font-size: 14px;
//   span {
//     line-height: 20px;
//   }
// }
// .light {
//   color: #2c8cf0;
// }
// .footer {
//   margin-top: 20px;
//   display: flex;
//   justify-content: flex-end;
//   span {
//     cursor: pointer;
//     font-size: 14px;
//   }
//   span:nth-child(1) {
//     color: #aaa;
//     margin-right: 20px;
//   }
//   span:nth-child(2) {
//     color: #2c8cf0;
//   }
// }
// .content-box {
//   padding: 10px;
//   padding-top: 20px;
// }
.title {
  width: 100%;
  text-align: center;
  color: #000;
  margin-bottom: 20px;
}
.pic {
  width: 100%;
  margin: 20px 0;
}
.content {
  color: #333;
  font-size: 12px;
  line-height: 16px;
}
.iframeClass {
  width: 100%;
  height: 100%;
  border: 0;
}
</style>
