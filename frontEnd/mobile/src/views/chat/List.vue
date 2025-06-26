<script>
import useStore from '@/stores/index.js'
import { getList } from './api'
import { getMaterialMediaId } from '@/api/common.js'
export default {
  components: {},
  props: {
    // mediaType 0 图片（image）、1 语音（voice）、2 视频（video），3 普通文件(file) 4 文本 5 海报
    mediaType: {
      type: String,
      default: null,
    },
  },
  data() {
    return {
      getList,
      // 查询条件
      query: {
        // materialName: '',
        // mediaType: '',
      },
    }
  },
  watch: {},
  computed: {},
  created() {
    this.query.msgtype = $sdk.dictMaterialType[this.mediaType]?.msgtype
  },
  mounted() {},
  methods: {
    search(pageNum, keyword, categoryId) {
      keyword ? (this.query.title = keyword) : delete this.query.title
      if (categoryId) {
        this.query.categoryId = categoryId
      } else {
        delete this.query.categoryId
      }
      this.$refs.prsl.getList(pageNum)
    },
    send(data) {
      this.$toast.loading({
        message: '正在发送...',
        duration: 0,
        forbidClick: true,
      })
      ww.getContext().then(async (res) => {
        if (res.errMsg == 'getContext:ok') {
          let entry = res.entry //返回进入H5页面的入口类型，目前 contact_profile、single_chat_tools、group_chat_tools
          let mes = {}
          mes.msgtype = data.msgtype
          try {
            if (!['single_chat_tools', 'group_chat_tools', 'normal'].includes(entry)) {
              // _this.$toast.clear()
              this.$toast('入口错误：' + entry)
              return
            }

            // mediaType 0 图片（image）、1 语音（voice）、2 视频（video），3 普通文件(file) 4 文本 5 海报
            // msgtype 文本(“text”)，图片(“image”)，视频(“video”)，文件(“file”)，H5(“news”）和小程序(“miniprogram”)

            let msgtype = {
              0: 'image',
              4: 'text',
              9: 'news',
            }
            mes.msgtype = msgtype[this.mediaType]

            let linkUrl =
              window.document.location.origin +
              window.sysConfig.BASE_URL +
              '#/metrialDetail?materiaId=' +
              data.id +
              '&userId=' +
              sessionStorage.getItem('sysId')
            switch (this.mediaType) {
              case '4':
              default:
                mes[mes.msgtype] = data[data.msgtype]
                break
              case '0':
                let dataMediaId = {
                  url: data.picUrl,
                  type: msgtype[this.mediaType],
                  name: data.title,
                }
                try {
                  let resMaterialId = await getMaterialMediaId(dataMediaId)
                  if (!resMaterialId.data) {
                    this.$toast('获取素材id失败')
                    return
                  }
                  mes[msgtype[this.mediaType]] = {
                    mediaid: resMaterialId.data.mediaId, //
                  }
                } catch (error) {
                  return
                }
                break
              // 图文
              case '9':
                mes.news = {
                  link: data.url || ' ', //H5消息页面url 必填
                  title: data.title || ' ', //H5消息标题
                  desc: data.desc || ' ', //H5消息摘要
                  imgUrl: data.picUrl || window.sysConfig.DEFAULT_H5_PIC, //H5消息封面图片URL
                }
                break
            }
            // this.$dialog({ message: 'mes：' + JSON.stringify(mes) })
          } catch (err) {
            this.$dialog({ message: 'err' + JSON.stringify(err) })
          }
          ww.sendChatMessage(mes)
            .then((resSend) => {
              if (resSend.errMsg == 'sendChatMessage:ok') {
                //发送成功 sdk会自动弹出成功提示，无需再加
                // this.$toast('发送成功')
              }
              if ('sendChatMessage:cancel,sendChatMessage:ok'.indexOf(resSend.errMsg) < 0) {
                //错误处理
                this.$dialog({ message: '发送失败：' + JSON.stringify(resSend) })
              }
            })
            .catch((err) => {})
          this.$toast.clear()
        } else {
          this.$toast.clear()
          //错误处理
          this.$dialog({ message: '进入失败：' + JSON.stringify(res) })
        }
      })
    },
  },
}
</script>

<template>
  <div>
    <PullRefreshScrollLoadList ref="prsl" :request="getList" :params="query">
      <template #list="{ list }">
        <div v-for="(item, index) in list" class="itemList overflow-auto" :key="index">
          <div class="content bfc-o" @click="showPopup(item)">
            <div class="title" v-if="mediaType !== '18'">{{ item.title }}</div>
            <!-- 文本 -->
            <p class="text" v-if="mediaType == '4'">{{ item.text?.content }}</p>
            <!-- 图片 -->
            <van-image v-if="mediaType == '0' && item.image?.picUrl" width="50" height="50" :src="item.image?.picUrl" />
            <div class="icon-style" v-if="mediaType == '0' && !item.image?.picUrl">
              <svg-icon class="icon-style" name="pic"></svg-icon>
            </div>
            <!-- 图文 -->
            <div class="centerStyle" v-if="mediaType == 9">
              <van-image width="50" height="50" :src="item.link?.picUrl" v-if="item.link?.picUrl" />
              <div class="icon-style" v-else>
                <svg-icon class="icon-style" name="imgText"></svg-icon>
              </div>
              <div class="contentStyle">{{ item.link?.desc }}</div>
            </div>
          </div>
          <div class="action float-right" @click="send(item)">发送</div>
        </div>
      </template>
    </PullRefreshScrollLoadList>
  </div>
</template>

<style lang="scss" scoped>
.icon-style {
  width: 50px;
  height: 50px;
  margin-right: 6px;
}
.itemList {
  width: 90%;
  margin: 10px;
  background-color: #fff;
  margin-bottom: 0px;
  padding: 10px;
  border-radius: 4px;
}
.content {
  .van-image,
  .video {
    margin: 0 10px 5px 0;
    border: 1px solid #eee;
  }
  .text {
    font-size: 12px;
    color: #000;
    // word-break: break-all;
    width: 100%;
    overflow: hidden;
    text-overflow: ellipsis;
    display: -webkit-box;
    -webkit-line-clamp: 3;
    -webkit-box-orient: vertical;
    margin-bottom: 6px;
    white-space: pre-line;
  }
}
.time {
  margin-left: 5px;
}
.title {
  font-weight: 700;
  font-size: 12px;
  color: #333;
  line-height: 18px;
  width: 100%;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  margin-bottom: 6px;
}
.centerStyle {
  display: flex;
  width: 100%;
  justify-content: space-between;
}
.contentTitle {
  font-weight: 700;
  font-size: 12px;
  color: #333;
  line-height: 18px;
  width: calc(100% - 60px);
  // white-space: nowrap;
  // overflow: hidden;
  // text-overflow: ellipsis;
  // margin-bottom: 6px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
  // margin-top: 20px;
  align-items: top;
}
.contentStyle {
  font-size: 12px;
  color: #aaaaaa;
  width: calc(100% - 60px);
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
  // margin-top: 20px;
  align-items: top;
}
.popText {
  padding: 20px;
  width: 300px;
  .popTitle {
    color: #000;
    font-size: 14px;
    width: 90%;
    // margin-top: 20px;
    margin-bottom: 20px;
    line-height: 18px;
  }
  .popContent {
    width: 100%;
    max-height: 300px;
    min-height: 40px;
    overflow-y: auto;
    color: #333;
    font-size: 14px;
    line-height: 18px;
    white-space: pre-line;
  }
}
.popPic {
  padding: 40px;
}
.action {
  width: 50px;
  height: 20px;
  font-size: 12px;
  background-color: #0079de;
  border-radius: 10px;
  color: #fff;
  line-height: 20px;
  text-align: center;
}
</style>
