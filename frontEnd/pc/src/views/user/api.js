import request from '@/utils/request'
const { get, post, put, delt } = request
const serve = '/iYqueUser'

/**
 * 列表
 * @param {*} data
{
  pageNum:
  pageSize:
  type:''
 }
 */
export const getList = (data) => get(`${serve}/findIYqueUserPage`, data)



/**
 * 成员同步
 * @returns 
 */
export const synchIyqueUser = () => post(`${serve}/synchIyqueUser`)

