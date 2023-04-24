import request from '@/utils/request'

/**
 * 归档项目列表new
 * @param {*} data
 */
export function archiveProjectListAPI(data) {
  return request({
    url: '/project/archiveProjectList',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}
