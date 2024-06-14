import request from '@/utils/request'
const { get, post, put, delt } = request
const serve = '/iYqueConfig'

export const getDetail = (id) => get(`${serve}/findIYqueConfig`)

export const addOrUpdate = (data) => post(`${serve}/saveOrUpdate`, data)

const serveWel = '/iYqueDefaultMsg'
export const getDetailWel = (id) => get(`${serveWel}/findIYqueConfig`)

export const addOrUpdateWel = (data) => post(`${serveWel}/saveOrUpdate`, data)
