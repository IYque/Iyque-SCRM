// 素材类型
export const dictMaterialType = Object.freeze({
  0: { name: '图片', msgtype: 'image', type: '0' },
  image: { name: '图片', msgtype: 'image', type: '0' },
  9: { name: '图文', msgtype: 'link', type: '9' },
  link: { name: '图文', msgtype: 'link', type: '9' },
  4: { name: '文本', msgtype: 'text', type: '4' },
  text: { name: '文本', msgtype: 'text', type: '4' },
})

export const errorCode = {
  401: '认证失败，无法访问系统资源',
  403: '当前操作没有权限',
  404: '访问资源不存在',
  default: '系统未知错误，请反馈给管理员',
}
