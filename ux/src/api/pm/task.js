import request from '@/utils/request'

/**
 * 任务归档
 * @param {*} data
 */
export function workTaskArchiveAPI(taskId) {
  return request({
    url: `workTask/archiveByTaskId/${taskId}`,
    method: 'post'
  })
}

/**
 * 归档任务激活
 * @param {*} data
 */
export function workTaskRecoverAPI(taskId) {
  return request({
    url: `work/work/activation/${taskId}`,
    method: 'post'
  })
}
