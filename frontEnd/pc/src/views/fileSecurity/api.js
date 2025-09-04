import request from '@/utils/request'
const { get, post, put, delt } = request
const serve = '/fileSecurity'

/**
 * 列表
 * @param {*} data
{
  pageNum:
  pageSize:
  type:''
 }
 */
export const getList = (data) => get(`${serve}/findAll`, data)




/**
 * 同步
 * @returns 
 */
export const synchInfo = (data) => post(`${serve}/synchInfo`,data)



/** 
 * 企微文件操作类型
 * @param {*} data
 * @returns
 */
export const getFileSecurityOperations = () => get(`${serve}/getFileSecurityOperations`)



/** 
 * 企微文件操作来源
 * @param {*} data
 * @returns
 */
export const getFileSecuritySources = () => get(`${serve}/getFileSecuritySources`)