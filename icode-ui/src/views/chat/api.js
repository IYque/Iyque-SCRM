import request from '@/utils/request'
const { get, post, put, delt } = request
const serve = '/msg'

/**
 * 列表
 * @param {*} data
{
  pageNum:
  pageSize:
  type:''
 }
 */
export const getList = (data) => get(`${serve}/findMsgAuditByPage`, data)


/**
 * AI预审列表
 * @param {*} data 
 * @returns 
 */
export const findAiAnalysisMsgAudits = (data) => get(`${serve}/findAiAnalysisMsgAudits`, data)


/**
 * 会话同步
 * @returns 
 */
export const synchMsg = () => get(`${serve}/synchMsg`)



/**
 * 
 * @returns ai会话预审
 */
export const buildAISessionWarning = () => get(`${serve}/buildAISessionWarning`)