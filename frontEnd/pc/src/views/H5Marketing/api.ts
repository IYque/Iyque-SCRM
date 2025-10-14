import request from '@/utils/request'
const { get, post, put, del: _del } = request

const service = '/h5Market'

/** 列表
 * @param {*} params
 */
export const getList = (data) => get(`${service}/findH5Markets`, data)

/** 详情
 * @param {*} params
 */
export const getDetail = (id) => get(`${service}/findWeH5MarketById/${id}`)

/** 新增或编辑
 * @param {Object} data
 */
export const save = (data) => post(`${service}/addOrUpdate`, data)

// export const add = (data) => post(`${service}/addSop`, data)
// export const update = (data) => post(`${service}/updateSop`, data)

/** 删除
 * @param {*} ids
 */
export const del = (ids) => _del(`${service}/${ids}`)

// 头部tab
export const getStatistic = (data) => get(`${service}/findH5MarketTab`, data)

// 数据统计-折线图
export const getDataTrend = (data) => get(`${service}/findH5MarketTrend`, data)

/** 数据统计-表格
 * @param {*} data
 */
export const getDataDetail = (data) => get(`${service}/findH5MarketTable`, data)

//  数据统计-表格(导出)
export const getDataDetailExpor = (data) => get(`${service}/export`, data, { responseType: 'blob' })
