import request from '@/utils/request'
const { get, post, put, delt } = request
const serve = '/iYqueTag'

/**
 * 列表
 * @param {*} data
 */
export const getList = (data) => get(`${serve}/findIYqueTagGroups`, data)

// 详情
export const getDetail = (id) => get(`${serve}/getKeyWordGroupBaseInfo/${id}`)

// 删除
export const del = (ids) => delt(`${serve}/${ids}`)

/**
 * 新增
 * @param {*} data
 * @returns
 */
export const add = (data) => post(`${serve}`, data)

// 修改
export function update(data) {
  return put(`${serve}`, data)
}

// 同步
export const sync = (data) => post(`${serve}/synchTags`, data)
