/**
 * 项目列表管理
 * @author yxk
 * @date 2022/9/13
 */
import request from '@/utils/request'

/**
 * 查询全部项目
 */
export function projectQueryAllAPI(data) {
  return request({
    url: '/project/allProjectList',
    method: 'post',
    headers: {
      'Content-Type': 'application/json;charset=utf-8;'
    },
    data
  })
}

/**
 * 查询属于我的全部项目
 */
export function projectQueryMyAPI(data) {
  return request({
    url: '/project/myProjectList',
    method: 'post',
    headers: {
      'Content-Type': 'application/json;charset=utf-8;'
    },
    data
  })
}

/**
 * 查询我管理的项目
 */
export function projectOwnerListAPI(data) {
  return request({
    url: '/project/iManagementProjectList',
    method: 'post',
    headers: {
      'Content-Type': 'application/json;charset=utf-8;'
    },
    data
  })
}

/**
 * 查询我参与的项目
 */
export function projectParticipateListAPI(data) {
  return request({
    url: '/project/iParticipateProjectList',
    method: 'post',
    headers: {
      'Content-Type': 'application/json;charset=utf-8;'
    },
    data
  })
}

/**
 * 归档/删除/恢复项目
 */
export function projectArchiveAPI(data) {
  return request({
    url: '/project/archiveProject',
    method: 'post',
    data
  })
}

/**
 * 删除项目
 */
export function projectDeleteAPI(projectId) {
  return request({
    url: `/project/deleteProject/${projectId}`,
    method: 'post'
  })
}

/**
 * 项目详情查询
 */
export function projectQueryByIdAPI(data) {
  return request({
    url: '/project/queryProjectById',
    method: 'post',
    data
  })
}

/**
 * 项目权限查询
 */
export function projectAuthAPI(data) {
  return request({
    url: `/projectUser/projectAuth/${data.projectId}`,
    method: 'post'
  })
}

/**
 * 根据项目ID查询项目权限
 */
export function projectAuthListAPI(data) {
  return request({
    url: 'projectUser/projectAuthList',
    method: 'post',
    data: data
  })
}

/**
 * 新建项目
 */
export function projectAddAPI(data) {
  return request({
    url: '/project/addProject',
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
export function projectUpdateAPI(data) {
  return request({
    url: '/project/updateProject',
    method: 'post',
    headers: {
      'Content-Type': 'application/json;charset=utf-8;'
    },
    data
  })
}

/**
 * 新建分组
 */
export function groupAddAPI(data) {
  return request({
    url: '/projectGroup/addGroup',
    method: 'post',
    headers: {
      'Content-Type': 'application/json;charset=utf-8;'
    },
    data
  })
}

/**
 * 移除分组
 */
export function groupDeleteAPI(data) {
  return request({
    url: '/projectGroup/removeGroupById',
    method: 'post',
    data
  })
}

/**
 * 更新项目分组
 */
export function groupUpdateAPI(data) {
  return request({
    url: '/projectGroup/updateGroup',
    method: 'post',
    headers: {
      'Content-Type': 'application/json;charset=utf-8;'
    },
    data
  })
}

/**
 * 更新项目分组顺序
 */
export function updateGroupBatchAPI(data) {
  return request({
    url: '/projectGroup/updateGroupBatch',
    method: 'post',
    headers: {
      'Content-Type': 'application/json;charset=utf-8;'
    },
    data
  })
}

/**
 * 移动-增加项目到分组
 */
export function moveToGroupAPI(data) {
  return request({
    url: '/projectGroupManagement/moveToGroup',
    method: 'post',
    headers: {
      'Content-Type': 'application/json;charset=utf-8;'
    },
    data
  })
}

/**
 * 查询分组下的项目
 */
export function searchGroupListAPI(data) {
  return request({
    url: '/projectGroup/searchGroupList',
    method: 'post',
    data
  })
}

/**
 * 我的收藏项目列表
 */
export function projectCollectListAPI(data) {
  return request({
    // url: `/projectCollect/queryCollect/${data.projectId}`,
    url: '/projectCollect/myCollectByProjectList',
    method: 'post',
    data,
    headers: {
      'Content-Type': 'application/json;charset=utf-8;'
    }
  })
}

/**
 * 收藏项目-取消收藏
 */
export function projectCollectAPI(data) {
  return request({
    url: `/projectCollect/collect/${data.projectId}`,
    method: 'post',
    data
  })
}

/**
 * 查询项目成员列表
 */
export function queryProjectUserListAPI(data) {
  return request({
    url: `/projectUser/queryProjectUser`,
    method: 'post',
    data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 添加成员
 * @param {*} data
 */
export function relatedProjectUserAPI(data) {
  return request({
    url: '/projectUser/relatedProjectUser',
    method: 'post',
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    },
    data: data
  })
}

/**
 * 编辑角色
 * @param {*} data
 */
export function editProjectUserAPI(data) {
  return request({
    url: '/projectUser/editProjectUser',
    method: 'post',
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    },
    data: data
  })
}

/**
 * 获取角色列表
 * @param {*} roleType
 */
export function getProjectRoleByTypeAPI(data) {
  return request({
    url: `/projectUser/getProjectRoles/${data.projectId}`,
    method: 'post',
    data
  })
}

/**
 * 退出项目
 */
export function deleteProjectRolesAPI(data) {
  return request({
    url: `/projectUser/deleteProjectRoles`,
    method: 'post',
    data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 查看项目角色权限
 * @param {*} roleType
 */
export function getAllRoleMenuAPI(data) {
  return request({
    url: `/projectUser/getAllRoleMenu/${data.projectId}/${data.userId}`,
    method: 'post',
    data
  })
}
