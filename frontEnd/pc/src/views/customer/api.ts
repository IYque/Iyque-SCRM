import request from '@/utils/request'
const { get, post, put, delt: _del } = request

const service = '/customerInfo'

/** 列表
 * @param {*} params
customerName,string,false,客户名
 */
export const getList = (data) => get(`${service}/findAll`, data)

/** synchCustomer
 * @returns
 */
export const synchCustomer = (data) => post(`${service}/synchCustomer`, data)
