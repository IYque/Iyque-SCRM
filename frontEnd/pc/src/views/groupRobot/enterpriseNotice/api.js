import request from '@/utils/request'
const service = '/agent'

// 应用管理
/**
 * 列表
 * @param {*} params
 * {
  endTime:'',	query	string 否 结束时间
  groupName:'',	query	string 否 群名称
  startTime:'',	query	string 否 开始时间
 * }
 */
export function getList() {
  return request({
    url: service + '/findAll',
  })
}

/**
 * 删除
 * @param {*} params
 */
export function remove(id) {
  return request({
    url: service + `/${id}`,
    method: 'delete',
  })
}

/**
 * 添加
 * @param {*} data
{
  groupName: string 群名称
  webHookUrl: string 群机器人链接
}
 * @returns
 */
export function save(data) {
  return request({
    url: service + '/saveOrUpdate',
    method: 'post',
    data,
  })
}

/**
 * 编辑
 * @param {*} data 同上
 * @returns
 */
export function update(data) {
  return request({
    url: service + `/update/${data.id}`,
    method: 'PUT',
    data,
  })
}

/**
 * 同步应用信息
 * @param {*} params
 */
export function sync(id) {
  return request({
    url: service + `/synchAgent/${id}`,
    method: 'get',
  })
}

// ---- 机器人消息 ------

export const appMsg = {
  /**
   * 获取历史消息列表
   * @param {*} params
   {
    endTime:'', // string date-time 发送结束时间
    startTime:'', // string date-time 发送开始时间
    robotId	query	integer 否 机器人ID
    status:'',// integer int32状态 0-草稿 1-待发送 2-已发送 3-发送失败
    title:'', // string 标题
  }
  */
  getList(params) {
    return request({
      url: service + '/findAgentSubAll/' + params.agentId,
      params,
    })
  },

  // /**
  //  * 应用消息详情
  //  * @param {*} id
  //  */
  // getDetail(id) {
  //   return request({
  //     url: serviceMsg + `/get/${id}`,
  //   })
  // },

  // /**
  //  * 撤销应用消息
  //  * @param {*} id
  //  */
  // revoke(id) {
  //   return request({
  //     url: serviceMsg + `/revoke/${id}`,
  //   })
  // },

  /**
 * 新增消息
 * @param {*} data
    {
      robotId:'', // integer 机器人ID
      msgTitle: '', // string 消息标题
      WeMessageTemplate:{
        appId: '', // string 小程序appid（可以在微信公众平台上查询），必须是关联到企业的小程序应用
        content: '', // string 文本内容（文本消息必传）
        description: '', // string 消息的描述（视频、文本卡片、图文 必传）
        linkUrl: '', // string 点击后跳转的链接。最长2048字节，请确保包含了协议头(http/https) （文本卡片、图文 必传）
        mediaId: '', // string 素材id（语音、视频、文件 必传）
        msgType: '', // string 消息类型 文本:text, 图片:image, 语音:voice, 视频:video, 文件:file, 文本卡片:textcard, 图文:news, 图文消息:link, 小程序：miniprogram
        picUrl: '', // string 图文消息的图片链接，支持JPG、PNG格式，较好的效果为大图 1068*455，小图150*150。（文本卡片、图文 必传）
        title: '', // string 消息的标题（视频、文本卡片、图文 必传）
      }
    }
 */
  add(data) {
    return request({
      url: service + '/sendAgentMsg',
      method: 'post',
      data,
    })
  },

  // // 修改应用消息 data同上
  // update(data) {
  //   return request({
  //     url: serviceMsg + `/update/${data.id}`,
  //     method: 'PUT',
  //     data,
  //   })
  // },

  // /**
  //  * 删除
  //  * @param {*} id
  //  */
  // remove(id) {
  //   return request({
  //     url: serviceMsg + `/delete/${id}`,
  //     method: 'delete',
  //   })
  // },
}
