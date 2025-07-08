/**
 * query请求路径参数转为对象
 * @param {*} url
 */
export function param2Obj(url) {
  try {
    url = url == null ? window.location.href : url
    let search = decodeURIComponent(url).split('?').slice(1)
    if (!search[0]) {
      return
    }
    search = search.filter((e) => e.includes('=')).map((e) => e.split('#')[0])
    search = search.join('&')
    return JSON.parse('{"' + search.replace(/"/g, '\\"').replace(/&/g, '","').replace(/=/g, '":"') + '"}')
  } catch (error) {
    alert(error)
  }
}

/**
 * Obj转为路径query请求参数
 * @param {*} obj
 */
export function obj2query(obj) {
  if (!obj) return ''
  return Object.keys(obj)
    .map((key) => {
      if (obj[key] === undefined) return ''
      return key + '=' + obj[key]
    })
    .filter((e) => !!e)
    .join('&')
}

/**
 * 获取query请求参数中name对应的值
 * @param string name
 */
export function getQueryValue(name, str) {
  try {
    let url = str || window.location.href
    // let search = url.split('?')[1]
    // search = search && search.split('#')[0]
    // if (!search) {
    //   return ''
    // }
    let reg = new RegExp('(^|&|\\?)' + name + '=([^&#]*)(&|#|$)')
    let r = url.match(reg)
    if (r != null) {
      return decodeURIComponent(r[2])
    }
    return ''
  } catch (error) {
    alert(error)
  }
}

export function urlReplaceFirstHistory(url) {
  window.history.go(-(window.history.length - 1))
  window.location.replace(url)
}

import { getWechatUserInfo, getWxRedirect, wxLogin } from '@/api/common'

import { showDialog, showLoadingToast, showSuccessToast, showToast } from 'vant'
// 微信端授权登录
export async function getWxCode() {
  process.env.NODE_ENV == 'development' && window.sysConfig.TOKEN && (sessionStorage.token = window.sysConfig.TOKEN)
  let userinfo = sessionStorage.getItem('userinfo')
  //取缓存中的用户信息
  if (userinfo) {
    try {
      userinfo = JSON.parse(userinfo)
    } catch (error) {
      // alert(error)
    }
    return userinfo
  }
  try {
    if (!sessionStorage.token) {
      let code = getQueryValue('code') //是否存在code
      if (!code) {
        let url = (await getWxRedirect()).data
        let id = getQueryValue('appid', url)
        sessionStorage.setItem('weAppId', id)

        setTimeout(() => {
          history.pushState({}, '') // 解决授权重定向返回问题
          window.location.reload()
        }, 1000)

        window.location.replace(url)
        return Promise.reject()
      } else {
        let { data: dataLogin } = await wxLogin(code)
        sessionStorage.setItem('token', dataLogin.access_token)
        // 解决授权重定向返回问题
        window.history.go(-window.history.length + 1)
        return Promise.reject()
      }
    }

    if (sessionStorage.token && !sessionStorage.openId) {
      let { data: dataUser } = await getWechatUserInfo()
      sessionStorage.setItem('userinfo', JSON.stringify(dataUser))
      sessionStorage.setItem('openId', dataUser.openId)
      sessionStorage.setItem('unionId', dataUser.unionId)
      sessionStorage.setItem('nickName', dataUser.nickName)
      sessionStorage.setItem('avatar', dataUser.avatar)
      return dataUser
    }
  } catch (error) {
    return Promise.reject()
  }
}

// 日期时间格式化
export function dateFormat(dateString, fmt = 'yyyy-MM-dd hh:mm:ss') {
  if (!dateString) {
    return
  }
  var date = dateString
  if (Object.prototype.toString.call(dateString) !== '[object Date]') {
    if (!dateString.includes('T')) {
      dateString = dateString.replace(/-/g, '/')
    }
    date = new Date(dateString)
  }
  var o = {
    'M+': date.getMonth() + 1, //月份
    'd+': date.getDate(), //日
    'h+': date.getHours(), //小时
    'm+': date.getMinutes(), //分
    's+': date.getSeconds(), //秒
    'q+': Math.floor((date.getMonth() + 3) / 3), //季度
    'S+': date.getMilliseconds(), //毫秒
    'w+': '星期' + '日一二三四五六'.charAt(date.getDay()), //星期
  }

  if (/(y+)/.test(fmt)) {
    fmt = fmt.replace(RegExp.$1, (date.getFullYear() + '').substr(4 - RegExp.$1.length))
  }

  for (var k in o) {
    if (new RegExp('(' + k + ')').test(fmt)) {
      fmt = fmt.replace(RegExp.$1, RegExp.$1.length == 1 || String(o[k]).length > 1 ? o[k] : '0' + o[k])
    }
  }

  return fmt
}

export function stringifyError() {
  Error.prototype.toJSON = function () {
    return this.toString()
  }
}

export function baiduMapError(type) {
  let arr = [
    { code: 0, value: '检索成功' },
    { code: 1, value: '城市列表' },
    { code: 2, value: '位置结果未知' },
    { code: 3, value: '导航结果未知' },
    { code: 4, value: '非法密钥AK' },
    { code: 5, value: '非法请求' },
    { code: 6, value: '没有权限, 请确认手机是否打开定位服务！' },
    { code: 7, value: '服务不可用' },
    { code: 8, value: '超时' },
  ]
  let str
  arr.forEach((dd) => {
    if (dd.code === type) {
      str = dd.value
    }
  })
  return str
}

export function splitName(str) {
  if (str) {
    let area = {}
    let index11 = 0
    let index1 = str.indexOf('省')
    if (index1 == -1) {
      index11 = str.indexOf('自治区')
      if (index11 != -1) {
        area.Province = str.substring(0, index11 + 3)
      } else {
        area.Province = str.substring(0, 0)
      }
    } else {
      area.Province = str.substring(0, index1 + 1)
    }

    let index2 = str.indexOf('市')
    if (index11 == -1) {
      area.City = str.substring(index11 + 1, index2 + 1)
    } else {
      if (index11 == 0) {
        area.City = str.substring(index1 + 1, index2 + 1)
      } else {
        area.City = str.substring(index11 + 3, index2 + 1)
      }
    }
    let index3 = str.indexOf('区')
    if (index3 == -1) {
      index3 = str.indexOf('县')
      if (index3 == -1) {
        index3 = str.indexOf('旗')
        area.Country = str.substring(index2 + 1, index3 + 1)
      } else {
        area.Country = str.substring(index2 + 1, index3 + 1)
      }
    } else {
      area.Country = str.substring(index2 + 1, index3 + 1)
    }
    return [area.Province, area.City, area.Country]
  } else {
    return ''
  }
}

// stime 开始时间 etime 结束时间  currentTime指定日期
export function compareTime(stime, etime, currentTime) {
  let thisDate = ''
  if (currentTime) {
    thisDate = new Date(currentTime)
  } else {
    thisDate = new Date()
  }
  let startTime = tranDate(stime)
  let endTime = tranDate(etime)
  let time =
    thisDate.getFullYear() +
    '-' +
    (thisDate.getMonth() + 1) +
    '-' +
    thisDate.getDate() +
    ' ' +
    thisDate.getHours() +
    ':' +
    thisDate.getMinutes()
  let nowTime = tranDate(time)
  if (nowTime < startTime) {
    return 'before'
  } else if (nowTime > endTime) {
    return 'after'
  } else {
    return 'middle'
  }
}
function tranDate(time) {
  return new Date(time.replace(/-/g, '/')).getTime()
}

export function uuid(before = '', after = '') {
  const chars = '0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz'.split('')
  const charsLen = chars.length
  let uuid = []
  const len = 16
  for (let i = 0; i < len; i++) {
    uuid[i] = chars[0 | (Math.random() * charsLen)]
  }
  return before + uuid.join('') + after
}

/**
 * 通用删除
 * @param {*} remove 删除接口
 * @param {*} callback 成功回调
 */
export function $delConfirm(remove, callback) {
  this.$confirm('是否确认删除?', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
  })
    .then(() => {
      return remove()
    })
    .then(() => {
      callback && callback()
      // this.getList && this.getList()
      this.msgSuccess()
    })
    .catch((error) => {
      console.error(error)
    })
}

// 复制文字
export function $copyText(txt) {
  if (!txt) {
    this.msgError('内容为空')
    return
  }
  if (navigator.clipboard) {
    navigator.clipboard
      .writeText(txt)
      .then(() => this.msgSuccess('复制成功'))
      .catch(() => this.msgError('复制失败'))
  } else {
    try {
      const input = document.createElement('input')
      input.style.cssText = 'opacity: 0;'
      input.type = 'text'
      input.value = text // 修改文本框的内容
      document.body.appendChild(input)
      input.select() // 选中文本
      document.execCommand('copy') // 执行浏览器复制命令
      this.msgSuccess('复制成功')
      input.remove()
    } catch (error) {
      this.msgError('复制失败')
    }
  }
}

export function fileUrlBase(url) {
  if (!url) return ''
  return url.startsWith('http') ? url : window.sysConfig.BASE_API + '/file/fileView/' + url
}
