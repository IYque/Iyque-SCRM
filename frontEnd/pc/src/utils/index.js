export const dictMsgType = Object.freeze({
  image: { name: '图片', type: 'image' },
  link: { name: '链接', type: 'link' },
  miniprogram: { name: '小程序', type: 'miniprogram' },
  video: { name: '视频', type: 'video' },
  file: { name: '文件', type: 'file' },
})
export const echartColors = [
  '#0F68FF',
  '#34D7C3',
  '#FFBF49',
  '#FF5954',
  '#52DDFF',
  '#34D781',
  '#FFE85F',
  '#7F47FF',
  '#A47D79',
  '#7796D7',
  '#89C369',
  '#B595D4',
  '#E4CBCB',
  '#7D889B',
  '#AAAFB7',
  '#DCD7B0',
  '#749E84',
  '#B0BBDC',
]
/**
 * 获取query请求参数中name对应的值
 * @param string name
 */
export function getQueryValue(name) {
  let url = window.location.href

  let reg = new RegExp('(^|&|\\?)' + name + '=([^&#]*)(&|#|$)')
  let r = url.match(reg)
  if (r != null) {
    return decodeURIComponent(r[2])
  }
  return ''
}
