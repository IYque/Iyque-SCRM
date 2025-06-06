import request from '@/utils/request'
const { get, post, put, delt: _del } = request

const service = '/kf'

/** 列表
 * @param {*} params
nickname,string,false,客户名
 */
export const getList = (data) => get(`${service}/findSummaryKfMsgs`, data)

/** 获取圈选客户列表
 * @param data
 * @returns
 */
export const findGroupAll = (data) => get(`${service}/findGroupAll`, data)

/** AI会话总结
 * @returns
 */
export const summaryKfmsgByAi = (data) => post(`${service}/summaryKfmsgByAi`, data)
