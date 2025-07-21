import request from '@/utils/request'
const { get, post, put, delt: _del } = request

const service = '/seas'

/** 列表
 * @param {*} params
tplName,string,true,,false,模版名称
status,string,true,,false,状态(1:启用；0:停用)
 */
export const getList = (data) => get(`${service}/findAll`, data)

/** 通过id批量提醒
 * @param {*} params
 */
export const distribute = (id) => get(`${service}/distribute/${id}`)

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
export const del = (ids) => _del(`${service}/batchDelete/${ids}`)

/** 公海导入
{
    "allocateUsers": [
        {
            "userId": "string"
        }
    ],
    "file": {}
}
 */
export const importData = (data) => {
  const formdata = new FormData()
  formdata.append('allocateUsers', JSON.stringify(data.allocateUsers))
  formdata.append('file', data.file)

  post(`${service}/importData`, formdata)
}

// 公海下载模版
export const getExport = (data) => get(`${service}/export`, data, { responseType: 'blob' })
