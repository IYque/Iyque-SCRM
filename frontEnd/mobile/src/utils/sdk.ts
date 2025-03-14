import request from '@/utils/request'

import Vant, { setToastDefaultOptions, setDialogDefaultOptions, showToast, showDialog } from 'vant'
// $sdk 公共方法，挂在到全局 window 和 vue app.config.globalProperties
export class SDK {
  ruleRequiredBlur = { required: true, message: '必填项', trigger: 'blur' }
  ruleRequiredChange = { required: true, message: '必选项', trigger: 'change' }

  request = request

  msgSuccess(message = '操作成功') {
    showToast({ message, type: 'success' })
  }
  msgError(message = '操作失败') {
    showToast({ message, type: 'fail' })
  }
  msgInfo(message) {
    showToast(message)
  }

  loading(message = '加载中...') {
    showToast({ message, type: 'loading', loadingType: 'spinner' })
  }
  confirm(message = '是否确认删除?', title = '提示', options) {
    return showDialog({ title, message, showCancelButton: true, ...options })
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
}
export const $sdk: SDK = new SDK()
declare global {
  var $sdk: SDK
}
window.$sdk = $sdk
// 下面定义非全局公共方法等
