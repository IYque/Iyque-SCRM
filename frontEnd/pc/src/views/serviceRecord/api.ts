import request from '@/utils/request'
const { get, post, put, delt: _del } = request

const service = '/kf'

/** 列表
 * @param {*} params
kfName,string,false,客服名
nickname,string,false,客户名
 */
export const getList = (data) => get(`${service}/findKfMsgAll`, data)
