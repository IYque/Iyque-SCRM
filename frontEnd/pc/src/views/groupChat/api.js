import request from '@/utils/request'
const { get, post, put, delt } = request
const serve = '/iYqueChat'

/**
 * 列表
 * @param {*} data
{
  pageNum:
  pageSize:
  type:''
 }
 */
export const getList = (data) => get(`${serve}/findIYqueChatPage`, data)



/**
 * 客群同步
 * @returns 
 */
export const synchIyqueChat = () => post(`${serve}/synchIyqueChat`)

