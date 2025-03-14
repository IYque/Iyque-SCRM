<script>
import { getCosConfig, getVideoPic, upload } from '@/api/common'
import { dateFormat, uuid } from '@/utils/index'

import { ImagePreview } from 'vant'
import { showToast, showLoadingToast, showFailToast, showSuccessToast, closeToast } from 'vant'

export default {
  components: {},
  props: {
    // 单文件上传时使用
    fileUrl: {
      type: String,
      default: '',
    },
    fileName: {
      type: String,
      default: '',
    },
    imgSize: {
      type: String,
      default: '',
    },

    // 多文件上传时使用，例如: [{name: 'food.jpg', url: 'https://xxx.cdn.com/xxx.jpg'}]
    fileList: {
      type: Array,
      default: () => [],
    },

    type: {
      type: String,
      default: 'image',
      validator: function (value) {
        // 这个值必须匹配下列字符串中的一个
        return ['image', 'video', 'file'].includes(value)
      },
    },
    // 上传文件大小不能超过 maxSize MB, 各类型有默认限制 参见: maxSizeDefault
    maxSize: {
      type: Number,
      default: undefined,
    },
    // 图片的宽高像素限制 [width(number), height(number)],默认null不限制
    maxImgPx: {
      type: Array,
      default: null, // () => [100, 100]
    },
    // 允许上传的文件格式后缀名 eg:["jpg", "png"]，['*']为不限制，各类型有默认限制 参见: formatDefault
    format: {
      type: Array,
      default: undefined,
    },
    // 选择上传文件类型, 用于过滤系统选择文件类型, 默认根据类型自动匹配：见 acceptAuto，
    accept: {
      type: String,
      default: '',
    },
    // 是否能上传多个
    multiple: {
      type: Boolean,
      default: false,
    },
    // multiple为true时有效，最多上传几个,默认不限制，最小为1
    limit: {
      type: [Number, String],
      default: undefined,
    },
    // onProgress: {
    //   type: Function,
    //   default: null,
    // },
    // beforeUpload: {
    //   type: Function,
    //   default: function() {
    //     return function() {}
    //   },
    // },

    // 图片操作  目前支持有 view：查看，remove：删除，download：下载
    // action: {
    //   type: Array,
    //   default: () => ["view", "remove"],
    // },
  },
  data() {
    return {
      loading: false,

      fileUrlWatch: this.fileUrl,
      fileNameWatch: this.fileName,
      fileListWatch: this.fileList,

      file: undefined,
      speed: 0, // 上传网速
      percentage: 0, //上传进度
    }
  },
  watch: {
    fileUrl: {
      handler(value) {
        this.fileUrlWatch = value
      },
    },
    fileName: {
      handler(value) {
        this.fileNameWatch = value
      },
    },
    fileList: {
      handler(value) {
        this.fileListWatch = value
      },
      deep: true,
    },
    loading(val) {
      this.$emit('loadingChange', val)
    },
  },
  computed: {
    // 识别选择文件类型
    acceptAuto() {
      return { image: 'image/*', video: 'video/*' }[this.type]
      // ['png','jpg'.'jpeg'] =>  '.png, .jpg, .jpeg'
      let accept = this.format.map((e) => '.' + e).join(', ')
      return accept || ['image/*', 'amr/*', 'video/*'][this.type]
    },
  },
  created() {},
  mounted() {},
  methods: {
    // 上传成功回调
    uploadSuccess(file, location) {
      this.loading = false
      this.$emit('upSuccess', location)

      // 使用本地链接提供预览，避免上传后下载的问题
      let url = location
      // window.URL.createObjectURL(file)

      let name = file.name
      let memorySize = file.size
      let myType = file.myType
      if (!this.multiple) {
        this.fileUrlWatch = url
        this.$emit('update:fileUrl', location)
        this.$emit('update:fileName', (this.fileNameWatch = name))
        this.$emit('update:imgSize', memorySize)
      } else {
        this.fileListWatch = this.fileListWatch.concat({ name, url, memorySize, myType })
        this.$emit(
          'update:fileList',
          this.fileList.concat({ name, url: location, memorySize, myType }),
        )
      }
    },
    upload() {
      this.loading = true
      let file = undefined
      if (!this.multiple || this.limit == 1) {
        file = this.file
      } else {
        // 多选上传是多次调用单传的
        file = this.file.shift()
      }

      let formData = new FormData()
      formData.append('file', file)
      upload(formData).then((dd) => {
        if (dd.code == 200) {
          let location = dd.msg
          this.uploadSuccess(file, location)
        } else {
          this.loading = false
          showFailToast('上传失败，请稍后再试')
        }
      })
    },
    remove(i) {
      this.$dialog
        .confirm({
          title: '提示',
          message: '确定要删除吗?',
        })
        .then(() => {
          let clone = JSON.parse(JSON.stringify(this.fileList))
          this.fileListWatch.splice(i, 1)
          clone.splice(i, 1)
          this.$emit('update:fileList', clone)
          this.$emit('getFileListMove', i)
        })
    },
    handleExceed(file, fileList) {
      showFailToast('最多上传' + this.limit + '张')
      this.loading = false
    },
    getFileType(name) {
      let r = 'image'
      let obj = name.split('.'),
        name_len = obj.length,
        suffix = name_len > 1 ? obj[name_len - 1].toLowerCase() : 'unknow'
      // if (['png', 'jpg', 'jpeg'].includes(suffix)) type = 0
      if (['mp4'].includes(suffix)) r = 'video'
      else if (
        ['xls', 'xlsx', 'doc', 'docx', 'pdf', 'ppt', 'pptx', 'pps', 'pptsx'].includes(suffix)
      )
        r = 'file'
      return r
    },
    async handleBeforeUpload(file, filelist) {
      // debugger
      this.loading = true
      let isFormat = true
      let isSize = true

      // type： 0 图片（image）、1 语音（voice）、2 视频（video），3 普通文件(file)
      file.myType = this.getFileType(file.name)

      // 统一校验文件后缀名格式
      // 如果没有显示配置 format 格式限制，则使用默认下面校验
      let format = this.format
      let tip = ''
      if (this.type && (!format || !format.length)) {
        let formatDefault = {
          image: { tip: 'png/jpg', value: ['png', 'jpg', 'jpeg'] },
          video: { value: ['mp4'] },
          file: {
            tip: 'word/pdf/ppt',
            value: ['doc', 'docx', 'pdf', 'ppt', 'pptx', 'pps', 'pptsx'],
          },
        }
        format = formatDefault[this.type].value
        tip = formatDefault[this.type].tip
      }

      const reg = new RegExp(`\\.(${format.join('|')})$`, 'ig') // /\.xlsx|.$/
      isFormat = format[0] === '*' || reg.test(file.name)
      if (!isFormat) {
        showFailToast('文件格式错误，仅支持 ' + (tip || format.join('，')) + ' 格式!')
        this.loading = false
        return Promise.reject()
      }

      // 统一校验文件体积
      // 如果没有显式配置 maxSize 限制大小，则使用下面默认值，单位 MB，
      let maxSize = this.maxSize
      if (!maxSize) {
        let maxSizeDefault = { image: 2, video: 100, file: 50 }
        maxSize || (maxSize = maxSizeDefault[file.myType])
      }
      isSize = file.size / 1024 / 1024 < maxSize
      if (!isSize) {
        showFailToast('上传文件大小不能超过 ' + maxSize + 'MB!')
        this.loading = false
        return Promise.reject()
      }

      // 各类型独有的校验
      let validate = true
      if (file.myType == 'image') {
        // 图片
        let maxImgPx = this.maxImgPx
        // if (maxImgPx) {
        //   try {
        //     await new Promise((resolve) => {
        //       let width, height
        //       let image = new Image()
        //       //加载图片获取图片真实宽度和高度
        //       image.onload = () => {
        //         width = image.width
        //         height = image.height
        //         if (width > maxImgPx[0]) {
        //           validate = false
        //           showFailToast(`图片“宽”度超过${maxImgPx[0]}像素，请重新选择`)
        //         } else if (height > maxImgPx[1]) {
        //           showFailToast(`图片“高”度超过${maxImgPx[1]}像素，请重新选择`)
        //           validate = false
        //         }
        //         window.URL && window.URL.revokeObjectURL(image.src)
        //         resolve()
        //       }
        //       if (window.URL) {
        //         let url = window.URL.createObjectURL(file)
        //         image.src = url
        //       } else if (window.FileReader) {
        //         let reader = new FileReader()
        //         reader.onload = function (e) {
        //           let data = e.target.result
        //           image.src = data
        //         }
        //         reader.readAsDataURL(file)
        //       }
        //     })
        //   } catch (e) {
        //     console.error(e)
        //   }
        // }
      }
      // else if (file.myType === '1') {
      //   // 语音
      //   let amr = new BenzAMRRecorder()
      //   try {
      //     await amr.initWithBlob(file)
      //     validate = amr.getDuration() <= 60
      //     if (!validate) {
      //       showFailToast('上传文件时长不能超过 60秒!')
      //     }
      //   } catch (error) {
      //     console.log(error)
      //     showFailToast('文件已损坏')
      //   }
      // }
      if (!validate) {
        this.loading = false
      }
      // if (beforeUpload) {
      //   return beforeUpload(file)
      // }
      else if (!this.multiple || this.limit == 1) {
        this.file = file
      } else {
        Array.isArray(this.file) || (this.file = []) // 多选
        this.file.push(file)
      }
      return validate ? file : Promise.reject()
    },
    onError(err, file, fileList) {
      this.loading = false
      showFailToast('上传文件失败')
    },
    checkVideoCode(file) {
      return new Promise((resolve, reject) => {
        const mp4boxFile = MP4Box.createFile()
        const reader = new FileReader()
        reader.readAsArrayBuffer(file)
        reader.onload = function (e) {
          const arrayBuffer = e.target.result
          arrayBuffer.fileStart = 0
          mp4boxFile.appendBuffer(arrayBuffer)
        }
        mp4boxFile.onReady = function (info) {
          resolve(info)
        }
        mp4boxFile.onError = function (info) {
          reject(info)
        }
      })
    },
    showView(index) {
      ImagePreview({ images: [this.fileUrlWatch], startPosition: index })

      // let imager = index !== undefined ? this.$refs.image[index] : this.$refs.image
      // imager.$el.firstElementChild.click()
      // this.$nextTick(() => {
      //   // 为遮罩层添加关闭事件
      //   let maskEl = imager.$children[0].$refs['van-image-viewer__wrapper'].firstChild
      //   maskEl.addEventListener('click', () => {
      //     event.stopPropagation()
      //     imager.closeViewer()
      //   })
      // })
      // this.$refs.image.$refs.closeViewer();
    },
  },
}
</script>

<template>
  <div>
    <van-uploader
      v-if="!multiple || !limit || fileListWatch.length < limit"
      class="uploader"
      :accept="accept || acceptAuto"
      v-model="fileListWatch"
      :disabled="loading"
      :multiple="multiple && limit != 1"
      :max-count="limit"
      :before-read="handleBeforeUpload"
      :after-read="upload"
    >
      <!--
      :preview-image="false"
      :max-size="maxSize * 1024 * 1024"
      element-loading-text="正在上传..."
        :on-success="onSuccess"
       -->

      <slot>
        <div v-if="!loading && !fileUrlWatch" class="uploader-icon upload-action uploader-size">
          <van-icon name="plus" />
        </div>

        <TransitionGroup>
          <!-- 上传进度条 -->
          <div class="upload-action uploader-size" v-if="loading" :key="1">
            <van-progress
              v-if="percentage"
              class="progress cc"
              type="circle"
              :percentage="percentage"
            ></van-progress>
            <div class="el-loading-spinner">
              <svg viewBox="25 25 50 50" class="circular">
                <circle cx="50" cy="50" r="20" fill="none" class="path"></circle>
              </svg>
            </div>
            <div class="cc" style="margin-top: 35px" v-if="speed">
              {{ speed + 'M/s' }}
            </div>
          </div>

          <!-- 单文件上传的文件展示 -->
          <div v-if="!loading && fileUrlWatch && !multiple" class="upload-item" :key="2">
            <template v-if="file.myType == 'image'">
              <van-image
                v-if="fileUrlWatch"
                ref="image"
                :src="fileUrlWatch"
                class="upload-img upload-img-single uploader-size"
                :preview-src-list="[fileUrlWatch]"
                preview-teleported
                fit="contain"
                @click.stop
              />

              <div class="action-mask" @click.self.stop>
                <van-icon name="search" @click.stop="showView(0)"></van-icon>
                <van-icon name="edit" />
                <!-- <span v-if="action.includes('download')" @click.prevent.stop="download(item)">
              <el-icon-download class="el-icon-download"></el-icon-download>
            </span> -->
                <!-- <span @click.prevent.stop="remove()">
                  <el-icon-delete class="el-icon-delete"></el-icon-delete>
                </span> -->
              </div>
            </template>
            <template v-else-if="file.myType == 'video'">
              <video
                ref="video"
                id="myVideo"
                class="upload-video"
                width="100%"
                controls
                webkit-playsinline="true"
                playsinline="true"
                :autoplay="false"
                :key="fileUrlWatch"
                preload="auto"
              >
                <source :src="fileUrlWatch" type="video/mp4" />
              </video>
              <div class="action-mask" style="height: 30%" @click.self.stop>
                <van-icon name="edit" />
              </div>
            </template>
            <template v-else class="al">
              {{ fileNameWatch || fileUrlWatch }}
              <van-icon name="edit" />
              <!-- a链接用本地视频打不开，视频地址使用远程地址 -->
              <!-- <a @click.stop :href="/\.mp4$/.test(fileNameWatch) ? fileUrl : fileUrlWatch" target="_blank">
                <el-icon-view class="el-icon-view ml10" style="vertical-align: middle"></el-icon-view>
              </a> -->
            </template>
          </div>
        </TransitionGroup>
      </slot>
    </van-uploader>

    <!-- 上传格式，大小等提示语 -->
    <div class="tip">
      <slot name="tip"></slot>
    </div>
  </div>
</template>

<style lang="scss" scoped>
:deep().uploader {
  display: inline-block;
  vertical-align: middle;
  .van-upload {
    display: block;
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
  }
}
// 默认组件大小，如需修改，请通过外部重写该类样式
.uploader-size {
  width: 80px;
  height: 80px;
}
.upload-action {
  position: relative;
  text-align: center;
}
.uploader-icon {
  display: flex;
  font-size: 28px;
  align-items: center;
  justify-content: center;
  color: var(--FontBlack6, #666);
  border-radius: 6px;
  border: 1px dashed var(--BorderBlack10, #666);
  transition: all 0.3s;
  &:hover {
    border-color: var(--Color, #666);
    color: var(--Color, #666);
  }
}
.progress {
  overflow: hidden;
}
.upload-img-single {
  border: 1px dashed var(--BorderBlack9);
}
.upload-img {
  display: block;
  border: 1px dashed var(--BorderBlack9, #eee);
  border-radius: 6px;
}
.upload-video {
  display: block;
  width: 300px;
  height: 150px;
  box-sizing: border-box;
  color: var(--FontWhite, #fff);
  background-color: var(--BgBlack);
  position: relative;
  padding: 0;
  font-size: 10px;
  line-height: 1;
  font-weight: normal;
  font-style: normal;
  word-break: initial;
}
.tip {
  margin-top: 10px;
  color: var(--FontBlack7);
  font-size: 12px;
}
.img-item {
  position: relative;
  display: inline-block;
  vertical-align: top;
  margin: 0 10px 10px 0;
  transition: all 0.3s;
}
.action-mask {
  position: absolute;
  display: flex;
  justify-content: space-around;
  // align-items: center;
  padding: 0 30px;
  width: 100%;
  height: 100%;
  left: 0;
  top: 0;
  cursor: pointer;
  text-align: center;
  // opacity: 0;
  font-size: 20px;
  transition: opacity 0.3s;
  // background-color: var(--bg-black-5);
  z-index: 1;
}
.icon-img-delete {
  position: absolute;
  top: 0;
  right: 0;
  width: 14px;
  height: 14px;
  background-color: rgba(0, 0, 0, 0.7);
  border-radius: 0 0 0 12px;
  .van-icon-cross {
    position: absolute;
    top: -2px;
    right: -2px;
    color: #fff;
    font-size: 16px;
    transform: scale(0.5);
  }
}
.upload-item {
  &:hover {
    .action-mask {
      opacity: 1;
    }
  }
}

.file-item {
  width: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin: 0 0 10px;
  font-size: 12px;
  .icon {
    font-size: 20px;
    margin-left: 5px;
  }
}
</style>
