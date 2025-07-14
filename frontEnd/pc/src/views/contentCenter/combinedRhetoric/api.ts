import request from '@/utils/request'
const { get, post, put, delt: _del } = request

const service = '/script'

/** 列表
 * @param {*} params
tplName,string,true,,false,模版名称
status,string,true,,false,状态(1:启用；0:停用)
 */
export const getList = (data) => get(`${service}/findAll`, data)

/** 详情
 * @param {*} params
 */
export const getDetail = (id) => get(`${service}/findSvipGroupTplById/${id}`)

/** 添加更新
 * @param {Object} data
{
    "id": "string",
    "categoryId": "string",
    "hotWord": "string",
    "nearHotWord": "string"
}
 */
export const save = (data) => post(`${service}/saveOrUpdate`, data)

/** 删除
 * @param {*} ids
 */
export const del = (ids) => _del(`${service}/${ids}`)
