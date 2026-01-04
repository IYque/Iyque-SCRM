import request from '@/utils/request'

/**
 * 获取违规拦截规则列表（分页查询）
 */
export const getList = (params) => {
  return request({
    url: '/iYqueSessionInterceptRule/findAll',
    method: 'get',
    params,
  })
}

/**
 * 新增/更新违规拦截规则
 */
export const saveOrUpdate = (data) => {
  return request({
    url: '/iYqueSessionInterceptRule/saveOrUpdate',
    method: 'post',
    data,
  })
}

/**
 * 删除违规拦截规则
 */
export const del = (id) => {
  return request({
    url: `/iYqueSessionInterceptRule/${id}`,
    method: 'delete',
  })
}

/**
 * 批量删除违规拦截规则
 */
export const batchDelete = (ids) => {
  return request({
    url: `/iYqueSessionInterceptRule/batchDelete/${ids}`,
    method: 'delete',
  })
}

/**
 * 根据ID查询违规拦截规则
 */
export const getById = (id) => {
  return request({
    url: `/iYqueSessionInterceptRule/${id}`,
    method: 'get',
  })
}

/**
 * 获取管理模块员工列表
 */
export const getStaffList = () => {
  return request({
    url: '/iYqueUser/findIYqueUser',
    method: 'get'
  }).then((res) => {
    if (res.code == 200) {
      res.data?.forEach((element) => {
        element.id = element.userId
      })
    }
    return res
  })
}