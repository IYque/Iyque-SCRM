import request from '@/utils/request'

// 朋友圈列表
export function getFriendCircleList(params: any) {
  return request({
    url: '/iYqueSys/friendCircle/list',
    method: 'get',
    params
  })
}

// 新增朋友圈
export function createFriendCircle(data: any) {
  return request({
    url: '/iYqueSys/friendCircle/create',
    method: 'post',
    data
  })
}

// 获取朋友圈详情
export function getFriendCircleDetail(id: string) {
  return request({
    url: `/iYqueSys/friendCircle/detail/${id}`,
    method: 'get'
  })
}

// 删除朋友圈
export function deleteFriendCircle(id: string) {
  return request({
    url: `/iYqueSys/friendCircle/${id}`,
    method: 'delete'
  })
}

// 更新朋友圈
export function updateFriendCircle(data: any) {
  return request({
    url: '/iYqueSys/friendCircle/update',
    method: 'put',
    data
  })
}

// AI生成朋友圈内容
export function aiGenerateFriendCircle(prompt: string, modelName?: string) {
  return request({
    url: '/iYqueSys/friendCircle/ai/generate',
    method: 'get',
    params: { prompt, modelName }
  })
}