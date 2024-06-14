import request from '@/utils/request'

export function getUserList(data) {
  return request({
    url: '/iYqueUser/findIYqueUser',
    params: data,
  }).then((res) => {
    if (res.code == 200) {
      res.data?.forEach((element) => {
        element.id = element.userId
      })
    }
    return res
  })
}

export function getTagList(data) {
  return request({
    url: '/iYqueTag/findIYqueTag',
    params: data,
  })
}
