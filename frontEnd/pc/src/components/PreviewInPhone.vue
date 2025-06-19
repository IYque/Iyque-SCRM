<template>
  <PhoneInterface :title="name">
    <div class="content">
      <!-- 欢迎语 -->
      <div class="item" v-if="templateInfo">
        <div class="avatar fxnone"><svg-icon icon="user" /></div>
      </div>

      <div class="item" v-for="(data, key) in list?.length ? list : data" :key="key">
        <div class="avatar fxnone"><svg-icon icon="user" /></div>

        <div class="msg">
          <!-- 文本 -->
          <div class="word" v-if="data.mediaType === '4'">
            <p style="white-space: pre-line">{{ data.content }}</p>
          </div>
          <!-- 图片 -->
          <div class="image" v-else-if="data.mediaType === '0'">
            <img :src="data.materialUrl" />
          </div>
          <!-- 图文 -->
          <div class="word-and-image" v-else-if="['9'].includes(data.mediaType)">
            <div class="title" v-if="data.materialName">
              {{ data.materialName }}
            </div>
            <div class="sub-content">
              <div v-if="data.content">
                {{ data.content }}
              </div>
              <div class="img-icon">
                <img :src="data.coverUrl" v-if="data.coverUrl && data.coverUrl != ''" />
                <svg-icon class="icon-style" icon="imgText" v-else></svg-icon>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </PhoneInterface>
</template>
<script>
export default {
  name: 'preview-client',
  components: {},
  props: {
    name: {
      type: String,
      default: '客户',
    },
    // 文章详情展示
    article: {
      type: String,
      default: null,
    },
    // 内容
    templateInfo: {
      type: String,
      default: null,
    },
    list: {
      type: Array,
      default: () => [],
    },
    // 直播链接
    liveUrl: {
      type: String,
      default: null,
    },
    // 新客拉新
    pullnewsInfo: {
      type: Object,
      default: null,
    },

    originList: {
      type: Array,
      default: () => [],
    },
  },
  watch: {},
  data() {
    return {}
  },
  computed: {
    data() {
      return this.setEditList(this.originList)
    },
  },
  created() {},
  mounted() {},
  methods: {
    // 处理文件类型
    filType(file) {
      let filecontent = JSON.parse(JSON.stringify(file))
      filecontent = filecontent.split('.')
      let type = filecontent[filecontent.length - 1]
      if (type === 'pdf') {
        return 'pdf'
      } else if (['doc', 'docx'].includes(type)) {
        return 'word'
      } else if (['ppt', 'pptx', 'pps', 'pptsx'].includes(type)) {
        return 'ppt'
      } else {
        return ''
      }
    },
    setEditList(list) {
      let arr = []
      if (list && list.length) {
        list.forEach((dd) => {
          let common = { id: dd.materialId, mediaType: dd.realType + '' }
          let obj = undefined
          if (dd.realType === 0) {
            obj = {
              materialUrl: dd.picUrl,
            }
          } else if (dd.realType === 2) {
            obj = {
              materialUrl: dd.fileUrl,
              coverUrl: dd.picUrl,
              digest: dd.description,
              materialName: dd.title,
            }
          } else if (dd.realType === 3) {
            obj = {
              materialUrl: dd.fileUrl,
              digest: dd.description,
              materialName: dd.title,
            }
          } else if (dd.realType === 4) {
            obj = {
              content: dd.content,
            }
          } else if ([9, 19].includes(dd.realType)) {
            obj = {
              content: dd.content,
              coverUrl: dd.picUrl,
              materialUrl: dd.linkUrl,
              materialName: dd.title,
            }
          } else if (dd.realType === 8) {
            obj = {
              materialName: dd.title,
              materialUrl: dd.linkUrl,
              materialName: dd.title,
            }
          } else if (dd.realType === 11) {
            obj = {
              digest: dd.appId,
              materialName: dd.title,
              coverUrl: dd.picUrl,
              materialUrl: dd.fileUrl,
            }
          } else if (dd.realType === 12) {
            obj = {
              digest: dd.description,
              materialUrl: dd.fileUrl,
              coverUrl: dd.picUrl,
              content: dd.content,
              materialName: dd.title,
            }
          } else if (dd.realType === 5) {
            obj = {
              materialUrl: dd.fileUrl,
              materialName: dd.title,
            }
          }
          obj && arr.push(Object.assign(common, obj))
        })
      }
      return arr
    },
  },
}
</script>
<style lang="scss" scoped>
.redBox {
  min-height: 26px;
  display: inline-block;
  box-sizing: border-box;
  word-break: break-all;
  margin-left: 5px;
  .red-box {
    width: 165px;
    height: 68px;
    background-image: url('../../assets/image/newred.png');
    background-size: 100%;
    background-repeat: no-repeat;
    padding-top: 20px;
    border-radius: 4px;
    .red-text {
      font-size: 14px;
      color: var(--font-white, #fff);
      margin-left: 56px;
      width: 98px;
      white-space: nowrap; // 强制一行
      text-overflow: ellipsis; // 文字溢出显示省略号
      overflow: hidden; // 溢出隐藏
    }
  }
}
.function {
  width: 160px;
  .function-item {
    width: 160px;
    min-height: 76px;
    border-width: 1px;
    border-style: solid;
    border-color: rgba(242, 242, 242, 1);
    border-radius: 5px;
    display: flex;
    padding: 10px;
    padding-top: 6px;
    cursor: pointer;
    .card-style {
      margin-right: 10px;
      margin-top: 8px;
      font-size: 32px;
    }
    .card-name {
      font-size: 14px;
      line-height: 26px;
      width: 98px;
      white-space: nowrap; // 强制一行
      text-overflow: ellipsis; // 文字溢出显示省略号
      overflow: hidden; // 溢出隐藏
    }
    .item-text {
      font-size: 12px;
      color: var(--font-black-7);
      line-height: 14px;
      width: 98px;
    }
  }
}

.content {
  padding: 15px;

  .item {
    font-size: 12px;

    font-weight: 400;
    color: var(--font-black-4);
    display: flex;
    margin-top: 20px;

    &:first-child {
      margin-top: 0;
    }
    .avatar {
      border: 1px solid var(--border-white);
      background: var(--bg-white);
      border-radius: 6px;
      width: 28px;
      height: 28px;
      display: flex;
      align-items: center;
      justify-content: center;
    }

    .msg {
      position: relative;
      min-height: 26px;
      background: var(--bg-white);
      box-shadow: 0px 2px 6px 0px rgba(60, 136, 240, 0.1);
      border: 1px solid var(--bg-white);
      display: inline-block;
      border-radius: 4px;
      box-sizing: border-box;
      word-break: break-all;
      margin-left: 5px;

      &:before {
        content: ' ';
        display: block;
        position: absolute;
        left: -6px;
        box-shadow: 0px 2px 6px 0px rgba(60, 136, 240, 0.1);
        border-width: 6px;
        border-left: 0;
        width: 0;
        border-style: solid;
        border-color: transparent;
        border-right-color: var(--bg-white);
        top: 8px;
        z-index: 888;
      }

      .word {
        padding: 5px 12px;
        line-height: 1.5;
      }

      .image {
        padding: 12px;

        img {
          width: 94px;
          height: 90px;
        }
      }

      .word-and-image {
        padding: 12px;
        .title {
          font-size: 14px;
          font-weight: 500;
          line-height: 1.25;
          margin-bottom: 8px;
        }
        .sub-content {
          display: flex;
          justify-content: space-between;
          font-size: 12px;
          font-weight: 400;
          color: var(--font-black-6);
          gap: 10px;
          .img-icon {
            border-radius: var(--border-radius-small);
            overflow: hidden;
            flex: none;
            line-height: 0;
          }
          img,
          .icon-style {
            height: 40px;
            width: 40px;
          }
        }
      }
      .miniprogram {
        padding: 12px;
        .mini-header {
          display: flex;
          align-items: center;
          img {
            height: 18px;
            width: 18px;
            margin-right: 5px;
          }
        }
        .mini-img {
          margin: 10px 0;
          width: 143px;
          height: 120px;
        }
        .mini-footer {
          display: flex;
          align-items: center;
          img {
            height: 14px;
            width: 14px;
            margin-right: 5px;
          }
        }
      }
    }
  }
}
</style>
