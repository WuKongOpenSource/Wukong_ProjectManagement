import request from '@/utils/request'

/**
 * 标签左侧列表
 * @param {*} data
 */
export function workTasklableIndexAPI(data) {
  return request({
    url: 'workTaskLabel/getLabelList',
    method: 'post',
    data: data

  })
}

/**
 * 标签删除
 * @param {*} data
 */
export function workTasklableDeleteAPI(data) {
  return request({
    url: 'workTaskLabel/deleteLabel',
    method: 'post',
    data: data
  })
}

/**
 * 创建标签
 * @param {*} data
 */
export function workTasklableSaveAPI(data) {
  return request({
    url: 'workTaskLabel/saveLabel',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 标签编辑
 * @param {*} data
 */
export function workTasklableSetAPI(data) {
  return request({
    url: 'workTaskLabel/setLabel',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}
