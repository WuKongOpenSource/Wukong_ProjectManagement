import request from '@/utils/request'

/**
 * 项目公告列表
 * @param {*} data
 */
export function getProjectAnnouncementAPI(data) {
  return request({
    url: 'projectAnnouncement/getProjectAnnouncement',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=utf-8;'
    }
  })
}

/**
 * 新建项目公告
 */
export function addProjectAnnouncementAPI(data) {
  return request({
    url: 'projectAnnouncement/addProjectAnnouncement',
    method: 'post',
    headers: {
      'Content-Type': 'application/json;charset=utf-8;'
    },
    data
  })
}

/**
 * 编辑项目
 */
export function setProjectAnnouncementAPI(data) {
  return request({
    url: 'projectAnnouncement/setProjectAnnouncement',
    method: 'post',
    headers: {
      'Content-Type': 'application/json;charset=utf-8;'
    },
    data
  })
}

/**
 * 删除项目
 */
export function delProjectAnnouncementAPI(data) {
  return request({
    url: `projectAnnouncement/delProjectAnnouncement`,
    method: 'post',
    headers: {
      'Content-Type': 'application/json;charset=utf-8;'
    },
    data
  })
}
