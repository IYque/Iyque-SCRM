import request from '@/utils/request'
const { get, post, put, delt: _del } = request

const service = '/kf'

/** 列表
 * @param {*} params
kfName,string,false,,,知识库名称
 */
export const getList = (data) => get(`${service}/findAll`, data)

/** 删除
 * @param {*} ids
 */
export const del = (ids) => _del(`${service}/${ids}`)

/** 启用或者停用
{
    "svipGroupIds": "", //一客一群模版id，多个使用逗号隔开
    "status": 0, //状态(1:启用；0:停用)
    "chatId": "", //群id
    "weUserId": "", //成员id
    "externalUserid": "" //客户id
}
 * @returns
 */
export const save = (data) => post(`${service}/saveOrUpdateKf`, data)

// 建群统计-头部tab
export const getStatistic = (svipGroupId) => get(`${service}/countTotalTab`, { svipGroupId })

// 建群统计-折线图
export const getDataTrend = (data) => get(`${service}/countTrend`, data)
