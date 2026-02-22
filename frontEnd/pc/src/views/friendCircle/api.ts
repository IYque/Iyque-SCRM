import request from '@/utils/request'

// 朋友圈列表
export function getFriendCircleList(params) {
  return request({
    url: '/friendCircle/list',
    method: 'get',
    params
  })
}

// 朋友圈详情
export function getFriendCircleDetail(id) {
  return request({
    url: `/friendCircle/detail/${id}`,
    method: 'get'
  })
}

// 新建朋友圈
export function createFriendCircle(data) {
  return request({
    url: '/friendCircle/create',
    method: 'post',
    data
  })
}

// 更新朋友圈
export function updateFriendCircle(data) {
  return request({
    url: '/friendCircle/update',
    method: 'post',
    data
  })
}

// 删除朋友圈
export function deleteFriendCircle(id) {
  return request({
    url: `/friendCircle/${id}`,
    method: 'delete'
  })
}

// 朋友圈接口说明
// 朋友圈数据结构:
// {
//   id: string,              // 朋友圈ID
//   name: string,            // 朋友圈名称
//   content: string,         // 文本内容
//   annexLists: [            // 附件列表
//     {
//       msgtype: string,      // 附件类型: image/video/link
//       image: {              // 图片类型
//         picUrl: string      // 图片URL
//       },
//       video: {              // 视频类型
//         videoUrl: string    // 视频URL
//       },
//       link: {               // 链接类型
//         title: string,      // 链接标题
//         desc: string,       // 链接描述
//         picUrl: string,     // 链接封面
//         url: string         // 链接地址
//       }
//     }
//   ],
//   createBy: string,         // 创建人
//   createTime: string,       // 创建时间
//   updateBy: string,         // 更新人
//   updateTime: string        // 更新时间
// }
