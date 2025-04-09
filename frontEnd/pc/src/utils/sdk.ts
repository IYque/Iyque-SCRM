import { ElMessage, ElMessageBox } from 'element-plus'
import { useRouter } from 'vue-router'
import request from '@/utils/request'

// $sdk 公共方法，挂在到全局 window 和 vue app.config.globalProperties
export class SDK {
  ruleRequiredBlur = { required: true, message: '必填项', trigger: 'blur' }
  ruleRequiredChange = { required: true, message: '必选项', trigger: 'change' }

  request = request

  msgSuccess(msg = '操作成功') {
    ElMessage({ showClose: true, message: msg, type: 'success', grouping: true })
  }
  msgError(msg = '操作失败') {
    ElMessage({ showClose: true, message: msg, type: 'error', grouping: true })
  }
  msgInfo(msg: string) {
    ElMessage.info(msg)
  }
  confirm(message = '确认删除吗？', title?: string, options?: {}) {
    let defaultConfig = {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
      title: '警告',
    }
    if (typeof title === 'string') {
      defaultConfig.title = title
      Object.assign(defaultConfig, options)
    } else {
      Object.assign(defaultConfig, options, title || {})
    }
    return ElMessageBox.confirm(message, defaultConfig)
  }

  /** 通用删除
   * @param {*} removeApi 删除接口
   * @param {*} callback 成功回调
   */
  delConfirm(remove: Function, callback?: Function) {
    this.confirm()
      .then(() => {
        return remove()
      })
      .then(() => {
        callback && callback()
        // this.getList && this.getList()
        this.msgSuccess()
      })
      .catch((error: Error) => {
        console.error(error)
      })
  }

  /** 下载bolb文件
   * @param {*} blob bolo源数据 必需
   * @param {*} downloadName 下载文件名，需含文件后缀名 必需
   * @param {*} type 文件类型 enum: excel,zip,image
   * @param {*} callback 成功回调
   */
  downloadBlob(blob: BlobPart, downloadName: string, type?: 'excel' | 'zip' | 'image', callback?: Function) {
    if (!blob || !downloadName) {
      this?.msgError?.('文件或文件名不存在，请联系系统管理员')
      throw '文件或文件名不存在，请联系系统管理员'
    }
    if (blob instanceof Blob) {
      const typeDict = {
        excel: 'application/vnd.ms-excel',
        zip: 'application/zip',
        image: 'application/image',
      }
      type && (blob = new Blob([blob], { type: typeDict[type] || type }))
      let url = window.URL.createObjectURL(blob)
      const a = document.createElement('a') // 创建a标签
      a.href = url
      a.download = dateFormat(new Date(), 'YYMMDD-HHmmss-') + downloadName // 下载文件名，不能包含英文 : 冒号
      a.click()
      a.remove()
      URL.revokeObjectURL(url) // 释放内存
      callback && callback()
      this?.msgSuccess?.('正在下载，请稍后至浏览器下载栏查看')
    } else if (/^(http|data:image)/.test(blob) && type === 'image') {
      let image = new Image()
      image.setAttribute('crossOrigin', 'anonymous')
      image.src = blob
      image.onload = () => {
        let canvas = document.createElement('canvas')
        canvas.width = image.width
        canvas.height = image.height
        let ctx = canvas.getContext('2d')
        ctx.drawImage(image, 0, 0, image.width, image.height)
        canvas.toBlob((blob) => {
          this.downloadBlob(blob, downloadName, callback)
        })
      }
    } else {
      this?.msgError?.('blob：文件类型错误')
      throw 'blob：文件类型错误'
    }
  }

  /** 通用导出
   * @param {*} exportApi 导出接口 必需
   * @param {*} exportFileName 导出文件名 需含文件后缀名 必需
   * @param {*} type 文件类型 enum: 'excel'|'zip'|'image'
   * @param {*} callback 成功回调
   */
  exportData(exportApi: Function, exportFileName = '导出.xlsx', type?: 'excel' | 'zip' | 'image') {
    this.confirm('是否确认导出吗?', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })
      .then(() => {
        // debugger
        return new Promise((resolve, reject) => {
          ElMessageBox.alert('正在导出，请稍等...', '提示', {
            confirmButtonText: '取消导出',
            confirmButtonClass: 'exportData',
            showClose: false,
          }).then(() => {
            reject()
          })
          exportApi().then((resBlob: BlobPart) => {
            resolve(resBlob)
          })
        })
      })
      .then((resBlob: BlobPart) => {
        this.downloadBlob(resBlob, exportFileName, type, () => {
          document.querySelector('.el-message-box__btns .exportData')?.click?.()
          this.msgSuccess('导出成功，请至浏览器下载栏中查看')
        })
      })
      .catch((error: Error) => {
        console.error(error)
      })
  }

  // goLink(path = '') {
  //   if (/^(https?:|mailto:|tel:)/.test(path)) {
  //     window.open(path, '_blank')
  //   } else if (path?.includes?.('/redirect')) {
  //     // /redirect 路径内部打开
  //     useRouter().push({ path: path.replace('/redirect', '') })
  //   } else {
  //     useRouter().push(path)
  //   }
  // }

  demoDisabled() {
    return process.env.NODE_ENV == 'development' ? true : (this.msgError('演示模式，暂不支持此操作'), false)
  }
}
export const $sdk: SDK = new SDK()
declare global {
  var $sdk: SDK
}
window.$sdk = $sdk
// 下面定义非全局公共方法等
