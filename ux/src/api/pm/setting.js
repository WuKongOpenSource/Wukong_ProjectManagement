import request from '@/utils/request'

/**
 * 新建项目状态
 */
export function projectStatusAddAPI(data) {
  return request({
    url: 'projectStatus/add',
    method: 'post',
    headers: {
      'Content-Type': 'application/json;charset=utf-8;'
    },
    data
  })
}

/**
 * 更新项目状态
 */
export function projectStatusUpdateAPI(data) {
  return request({
    url: 'projectStatus/update',
    method: 'post',
    headers: {
      'Content-Type': 'application/json;charset=utf-8;'
    },
    data
  })
}

/**
 * 项目列表
 */
export function projectStatusListAPI(data) {
  return request({
    url: 'projectStatus/queryProjectStatusList',
    method: 'post',
    headers: {
      'Content-Type': 'application/json;charset=utf-8;'
    },
    data
  })
}

/**
 * 删除项目状态
 */
export function projectStatusDelAPI(data) {
  return request({
    url: 'projectStatus/delete',
    method: 'post',
    data
  })
}

// ===================== 项目协同 =======================
/**
 * 事件状态列表
 */
export function projectEventTypeListAPI(data) {
  return request({
    url: 'projectEvent/queryEventList',
    method: 'post',
    data
  })
}

/**
 * 新增事件状态
 */
export function projectEventStatusAddAPI(data) {
  return request({
    url: 'projectEventStatus/add',
    method: 'post',
    headers: {
      'Content-Type': 'application/json;charset=utf-8;'
    },
    data
  })
}

/**
 * 删除事件状态
 */
export function projectEventStatusDelAPI(data) {
  return request({
    url: 'projectEventStatus/remove',
    method: 'post',
    data
  })
}

/**
 * 事件状态列表
 */
export function projectEventStatusListAPI(data) {
  return request({
    url: 'projectEventStatus/applicationSchemeStatusList',
    method: 'post',
    data
  })
}

/**
 * 获取状态列表
 */
export function getStatusListAPI(data) {
  return request({
    url: 'projectEventStatus/list',
    method: 'post',
    data
  })
}

/**
 * 状态迁移
 */
export function moveStatusAPI(data) {
  return request({
    url: 'projectEventStatus/transferStatus',
    method: 'post',
    headers: {
      'Content-Type': 'application/json;charset=utf-8;'
    },
    data
  })
}

/**
 * 设置初始状态
 */
export function setInitStatusAPI(data) {
  return request({
    url: 'projectEventStatus/updateInitStatus',
    method: 'post',
    data
  })
}

/**
 * 放弃编辑
 */
export function dropEditStatusAPI(data) {
  return request({
    url: 'projectEventStatus/giveUpApplicationScheme',
    method: 'post',
    data
  })
}

/**
 * 修改状态顺序
 */
export function statusUpdateSortingAPI(data) {
  return request({
    url: 'projectEventStatus/updateSorting',
    method: 'post',
    headers: {
      'Content-Type': 'application/json;charset=utf-8;'
    },
    data
  })
}

