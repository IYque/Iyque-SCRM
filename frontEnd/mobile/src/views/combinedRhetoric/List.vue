<script>
import useStore from '@/stores/index.js'
import * as apiCategory from '@/api/category'
import { getList } from './api'
import { getMaterialMediaId } from '@/api/common.js'
import { closeToast, showLoadingToast, showDialog, showToast } from 'vant'
import { fileUrlBase } from '@/utils/index.js'
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
      groupList: [], // 分组列表
      groupIndex: 0,
    }
  },
  watch: {},
  computed: {},
  created() {
    this.getListCategory()
    // this.query.msgtype = $sdk.dictMaterialType[this.mediaType]?.msgtype
  },
  mounted() {},
  methods: {
    switchGroup(index, data) {
      this.groupIndex = index
      this.query.categoryId = data.id
      this.search(1)
    },
    // 获取当前类型下分组列表
    getListCategory() {
      apiCategory.getList(this.mediaType).then((res) => {
        this.groupList = [{ name: '全部' }]
        res.data && this.groupList.push(...res.data)
        this.groupIndex = 0
      })
    },
    search(pageNum, keyword) {
      keyword ? (this.query.title = keyword) : delete this.query.title
      this.$refs.prsl.getList(pageNum)
    },
    // 一键发送
    sendOnece(data) {
      data.scriptSubs.forEach((el) => {
        this.send(el, data.id)
      })
    },
    send(data) {
      showLoadingToast({
        message: '正在发送...',
        duration: 0,
        forbidClick: true,
      })
      ww.getContext().then(async (res) => {
        if (res.errMsg == 'getContext:ok') {
          let entry = res.entry //返回进入H5页面的入口类型，目前 contact_profile、single_chat_tools、group_chat_tools
          let mes = {}
          try {
            if (!['single_chat_tools', 'group_chat_tools', 'normal'].includes(entry)) {
              showToast('入口错误：' + entry)
              return
            }

            // mediaType 0 图片（image）、1 语音（voice）、2 视频（video），3 普通文件(file) 4 文本 5 海报
            // msgtype 文本(“text”)，图片(“image”)，视频(“video”)，文件(“file”)，H5(“news”）和小程序(“miniprogram”)

            let msgtype = {
              link: 'news',
            }
            mes.msgtype = msgtype[data.msgtype] || data.msgtype

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
                  url: fileUrlBase(data.image?.picUrl),
                  type: mes.msgtype,
                  name: data.title,
                }
                try {
                  let resMaterialId = await getMaterialMediaId(dataMediaId)
                  if (!resMaterialId.data) {
                    showToast('获取素材id失败')
                    return
                  }
                  mes[mes.msgtype] = {
                    mediaid: resMaterialId.data.mediaId, //
                  }
                } catch (error) {
                  return
                }
                break
              // 图文
              case '9':
                mes.news = {
                  link: data.link?.url || ' ', //H5消息页面url 必填
                  title: data.title || ' ', //H5消息标题
                  desc: data.link?.desc || ' ', //H5消息摘要
                  imgUrl: fileUrlBase(data.link?.picUrl), //H5消息封面图片URL
                }
                break
            }
            console.log({ message: 'mes：' + JSON.stringify(mes) })
          } catch (err) {
            showDialog({ message: 'err' + JSON.stringify(err) })
          }
          ww.sendChatMessage(mes)
            .then((resSend) => {
              if (resSend.errMsg == 'sendChatMessage:ok') {
                //发送成功 sdk会自动弹出成功提示，无需再加
                // showToast('发送成功')
              }
              if ('sendChatMessage:cancel,sendChatMessage:ok'.indexOf(resSend.errMsg) < 0) {
                //错误处理
                showDialog({ message: '发送失败：' + JSON.stringify(resSend) })
              }
            })
            .catch((err) => {})
          closeToast()
        } else {
          closeToast()
          //错误处理
          showDialog({ message: '进入失败：' + JSON.stringify(res) })
        }
      })
    },
  },
}
</script>

<template>
  <div class="flex h-[calc(100vh-110px)] mt10">
    <div class="item-list">
      <div
        class="item"
        v-for="(group, key) in groupList"
        :class="{ active: groupIndex == key }"
        :key="group.id"
        @click="switchGroup(key, group)">
        <div class="name">{{ group.name }}</div>
      </div>
    </div>
    <PullRefreshScrollLoadList class="flex-auto" ref="prsl" :request="getList" :params="query">
      <template #list="{ list }">
        <div class="mx-[10px]">
          <div class="talkCard" v-for="(talkitem, index1) in list" :key="index1">
            <div class="talkItem">
              <span>{{ talkitem.title }}</span>
              <div class="actionOnece" @click="sendOnece(talkitem)" v-if="isPC">一键发送</div>
            </div>
            <div v-for="(item, index) in talkitem.scriptSubs" class="itemList overflow-auto" :key="index">
              <div class="content bfc-o" @click="showPopup(item)">
                <div class="title" v-if="item.title">{{ item.title }}</div>
                <!-- 文本 -->
                <p class="text" v-if="item.text?.content">{{ item.text?.content }}</p>
                <!-- 图片 -->
                <template v-if="item.image">
                  <van-image v-if="item.image?.picUrl" :src="item.image?.picUrl" />
                  <div class="icon-style" v-else>
                    <svg-icon class="icon-style" name="pic"></svg-icon>
                  </div>
                </template>
                <!-- 图文 -->
                <div class="centerStyle" v-else-if="item.link">
                  <van-image :src="item.link?.picUrl" v-if="item.link?.picUrl" />
                  <div class="icon-style" v-else>
                    <svg-icon class="icon-style" name="imgText"></svg-icon>
                  </div>
                  <div class="contentStyle">{{ item.link?.desc }}</div>
                </div>
              </div>
              <div class="action float-right" @click="send(item, talkitem.id)">发送</div>
            </div>
          </div>
        </div>
      </template>
    </PullRefreshScrollLoadList>
  </div>
</template>

<style lang="scss" scoped>
.item-list {
  flex: none;
  width: 30%;
  background-color: #fff;
  padding-top: 15px;
  display: flex;
  flex-direction: column;
  overflow-x: hidden;
  overflow-y: auto;
  border-radius: 4px;
  .item {
    cursor: pointer;
    display: flex;
    justify-content: space-between;
    align-items: center;
    color: rgba(0, 0, 0, 0.6);
    height: 40px;
    padding-left: 20px;
    .name {
      overflow: hidden;
      white-space: nowrap;
      text-overflow: ellipsis;
    }
    &:hover,
    &.active {
      border-left: 2px solid var(--Color);
      color: var(--Color);
      background: var(--ColorLight10);
      // opacity: 0.8;
      border-radius: 2px;
    }
  }
}

.talkCard {
  background-color: #fff;
  padding: 10px;
  margin: 10px;
  border-radius: 4px;
  .talkItem {
    display: flex;
    justify-content: space-between;
  }
}
.icon-style {
  width: 50px;
  height: 50px;
  margin-right: 6px;
}
.itemList {
  background-color: #fff;
  padding: 10px;
  border-radius: 4px;
  & + & {
    margin-top: 10px;
  }
}
.content {
  .van-image,
  .video {
    width: 50px;
    height: 50px;
    border-radius: var(--RadiusSmall, 4px);
    overflow: auto;
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
  background-color: var(--Color);
  border-radius: 10px;
  color: #fff;
  line-height: 20px;
  text-align: center;
}
</style>
