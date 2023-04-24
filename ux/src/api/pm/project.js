import request from '@/utils/request'

/**
 * 项目 -- 成员列表
 * @param {*} data
 */
export function workWorkOwnerListAPI(data) {
  return request({
    url: 'work/work/queryOwnerRoleList/' + data.workId,
    method: 'post'
  })
}

/**
 * 拖拽改变分类
 * @param {*} data
 */
export function workTaskUpdateOrderAPI(data) {
  return request({
    url: 'work/work/updateOrder',
    method: 'post',
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    },
    data: data
  })
}

/**
 * 拖拽改变分类列表
 * @param {*} data
 */
export function workTaskUpdateClassOrderAPI(data) {
  return request({
    url: 'work/work/updateClassOrder',
    method: 'post',
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    },
    data: data
  })
}

/**
 * 项目（甘特图）
 * @param {*} data
 */
export function projectGetGanttAPI(data) {
  return request({
    url: 'project/getProjectGantt',
    method: 'post',
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    },
    data: data
  })
}

/**
 * 查询项目事项工时列表
 * @param {*} data
 */
export function projectTaskTimeListAPI(data) {
  return request({
    url: 'projectTaskTime/queryProjectTaskTimeList',
    method: 'post',
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    },
    data: data
  })
}
