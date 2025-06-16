import request from '@/utils/request'
const { get, post, put, delt: _del } = request
const serve = '/log'

/**
 * 列表
 * @param {*} data
{
  pageNum:
  pageSize:
 }
 */
export function getList(data) {
  return get(`${serve}/findAll`, data)
}

/** 获取类型
 * @param {*} data
 * @returns
 */
export function findOperateLogTypes(data) {
  return get(`${serve}/findOperateLogTypes`, data)
}

// 详情
export function getDetail(id) {
  return get(service + '/get/' + id)
}

// 删除
export function del(id) {
  return _del(`${serve}/removeWeError/${id}`)
}

/**
 * 同步操作日志
 * @param {*} data
 * @returns
 */
export function synchOperateLog(data) {
  return post(`${serve}/synchOperateLog`, data)
}

// 修改
export function update(data) {
  return put(`${serve}/update`, data)
}
