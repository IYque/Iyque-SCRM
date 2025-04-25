import request from '@/utils/request'
const { get, post, put, delt: _del } = request

const service = '/knowledge'

/** 列表
 * @param {*} params
kname,string,false,,,知识库名称
 */
export const getList = (data) => get(`${service}/findKnowledgeByPage`, data)

/**
 *
 * @returns 获取所有知识库
 */

export const getKnowledgeAll = () => get(`${service}/findAll`)

/** 删除
 * @param {*} ids
 */
export const del = (ids) => _del(`${service}/remove/${ids}`)

/** 新增
{
    "kname": "string",
    "description": "string",
    "knowledgeSeparator": "string",
    "questionSeparator": "string",
    "overlapChar": "string",
    "retrieveLimit": "string",
    "textBlockSize": "string"
}
 * @returns
 */
export const save = (data) => post(`${service}/save`, data)

/** 查询知识附件信息列表
 * @param {*} params
kid,string,知识库主键id
 */
export const getListDetail = (data) => get(`${service}/detail/${data.kid}`, data)

/** 删除知识库附件
 * @param {*} docId 知识库附件主键id
 */
export const delAttach = (docId) => _del(`${service}/attach/remove/${docId}`)

/** 查询知识附件片段
 * @param {*} params
docId string  知识库附件文档id
 */
export const getListFragment = (data) => get(`${service}/fragment/list/${data.docId}`, data)
