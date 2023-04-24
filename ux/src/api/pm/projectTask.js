import request from '@/utils/request'

/**
 *
 * 新建事项  0 带规划 1迭代 2需求 3任务 4缺陷 5工时
 * @param {*} data
 */
export function workSaveProjectItemAPI(data) {
  return request({
    url: 'projectTask/saveProjectTask',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 *
 * 更新事项  0 带规划 1迭代 2需求 3任务 4缺陷 5工时
 * @param {*} data
 */
export function workUpdateProjectItemAPI(data) {
  return request({
    url: 'projectTask/updateProjectTask',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 *
 * 删除事项  0 带规划 1迭代 2需求 3任务 4缺陷 5工时
 * @param {*} data
 */
export function workDelProjectItemAPI(data) {
  return request({
    url: `projectTask/deleteTask/${data.taskId}`,
    method: 'post'
  })
}

/**
 *
 * 查询事项列表  待规划
 * @param {*} data
 */
export function workQueryPlanItemListAPI(data) {
  return request({
    url: 'projectTask/queryProjectPlanTaskList',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 *
 * 查询字段包含排序（事项、迭代列表字段）
 * @param {*} data
 */
export function workQueryFieldAndSortAPI(data) {
  return request({
    url: 'projectFieldSort/list',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 *
 * 列表字段宽度设置
 * @param {*} data
 */
export function workListSetFieldWidthAPI(data) {
  return request({
    url: 'projectFieldSort/addOrUpdate',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 *
 * 编辑事项标题
 * @param {*} data
 */
export function setItemNameAPI(data) {
  return request({
    url: 'projectTask/setProjectTaskName',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 *
 * 设置任务时间
 * @param {*} data
 */
export function setItemStartStopTimeAPI(data) {
  return request({
    url: 'projectTask/updateProjectTaskTime',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 *
 * 设置任务描述
 * @param {*} data
 */
export function setItemDesciptionAPI(data) {
  return request({
    url: 'projectTask/setProjectTaskDescription',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 *
 * 查询事项列表  迭代
 * @param {*} data
 */
export function workQueryIterationsItemListAPI(data) {
  return request({
    url: 'projectTask/queryProjectIterationTaskList',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 *
 * 查询事项列表  0 带规划 1迭代 2需求 3任务 4缺陷 5工时
 * @param {*} data
 */
export function workQueryItemListAPI(data) {
  return request({
    url: 'projectTask/queryProjectTaskList',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 *
 * 查询当前用户的事项列表 1迭代 2需求 3任务 4缺陷
 * @param {*} data
 */
export function workQueryUserTaskListAPI(data) {
  return request({
    url: 'projectTask/queryUserTaskList',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 *
 * 事项详情  0 带规划 1迭代 2需求 3任务 4缺陷 5工时
 * @param {*} data
 */
export function workQueryItemDetailAPI(data) {
  return request({
    url: `projectTask/getProjectTaskDetails/${data.taskId}`,
    method: 'post'
  })
}

/**
 *
 * 查询: 1.迭代下所有事项 2.查子任务
 * @param {*} data
 */
export function workQueryIterationAllItemAPI(data) {
  return request({
    url: 'projectTask/queryProjectTaskChildList',
    method: 'post',
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    },
    data
  })
}

/**
 *
 * 设置处理人
 * @param {*} data
 */
export function setHandlerAPI(data) {
  return request({
    url: 'projectTask/setProjectTaskMainUser',
    method: 'post',
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    },
    data
  })
}

/**
 *
 * 开始迭代
 * @param {*} data
 */
export function startIterationAPI(data) {
  return request({
    url: 'projectTask/startBelongIteration',
    method: 'post',
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    },
    data
  })
}

/**
 *
 * 完成迭代
 * @param {*} data
 */
export function finishIterationAPI(data) {
  return request({
    url: 'projectTask/stopBelongIteration',
    method: 'post',
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    },
    data
  })
}

/**
 *
 * 删除迭代
 * @param {*} data
 */
export function delIterationAPI(data) {
  return request({
    url: `projectTask/belongIterationDel/${data.taskId}`,
    method: 'post'
  })
}

/**
 *
 * 迭代详情
 * @param {*} data
 */
export function detailIterationAPI(data) {
  return request({
    url: `projectTask/belongIterationDetails/${data.taskId}`,
    method: 'post'
  })
}

/**
 *
 * 关联迭代
 * @param {*} data
 */
export function relationIterationAPI(data) {
  return request({
    url: 'projectBelongIteration/relevancyBelongIteration',
    method: 'post',
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    },
    data
  })
}

/**
 *
 * 关联内容
 * @param {*} data
 */
export function relationContentAPI(data) {
  return request({
    url: 'projectTaskRelation/saveProjectTaskRelation',
    method: 'post',
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    },
    data
  })
}

/**
 *
 * 工时-新增
 * @param {*} data
 */
export function addWorkTimeAPI(data) {
  return request({
    url: 'projectTaskTime/addProjectTaskTime',
    method: 'post',
    data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

// ======================= 看板视图 start=======================

/**
 *
 * 看板视图列表
 * @param {*} data
 */
export function queryViewBoardListAPI(data) {
  return request({
    url: 'projectBoardTask/queryBoardList',
    method: 'post',
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    },
    data
  })
}

/**
 *
 * 看板视图列表(迭代下)
 * @param {*} data
 */
export function queryViewTaskChildBoardListAPI(data) {
  return request({
    url: 'projectTask/queryProjectTaskChildBoardList',
    method: 'post',
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    },
    data
  })
}

/**
 * 看板视图设置 =》 列表
 * @param {*} data
 */
export function queryViewBoardSetListAPI(data) {
  return request({
    url: 'projectBoard/queryBoardList',
    method: 'post',
    data
  })
}

/**
 * 看板视图设置 =》 保存
 * @param {*} data
 */
export function queryViewBoardSetSaveAPI(data) {
  return request({
    url: 'projectBoard/resetBoard',
    method: 'post',
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    },
    data
  })
}

/**
 * 看板视图设置 =》 未添加看班列表
 * @param {*} data
 * @returns
 */
export function queryNoAddStatusList(data) {
  return request({
    url: 'projectBoard/notAddStatus',
    method: 'post',
    data
  })
}

// ======================= 看板视图 end=======================

// ======================= 评论与活动 start=======================

/**
 * 评论-列表
 * @param {*} data
 */
export function queryCommentActivityListAPI(data) {
  return request({
    url: 'projectTaskComment/queryCommentList',
    method: 'post',
    data
  })
}

/**
 * 评论-删除
 * @param {*} data
 */
export function delCommentAPI(data) {
  return request({
    url: 'projectTaskComment/deleteComment',
    method: 'post',
    data
  })
}

/**
 * 评论-回复
 * @param {*} data
 */
export function replyCommentAPI(data) {
  return request({
    url: 'projectTaskComment/setComment',
    method: 'post',
    data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 事项详情活动日志/概览与统计活动日志
 * @param {*} data
 */
export function queryActivityListAPI(data) {
  return request({
    url: `projectTaskLog/queryTaskLog/${data.taskId}/${data.type}`,
    method: 'post'
  })
}

// ======================= 评论与活动 end=======================

// ======================= 标签 start=======================

/**
 * 新建标签
 * @param {*} data
 */
export function addLabelAPI(data) {
  return request({
    url: 'projectLabel/add',
    method: 'post',
    data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 删除标签
 * @param {*} data
 */
export function delLabelAPI(data) {
  return request({
    url: 'projectLabel/delete',
    method: 'post',
    data
  })
}

/**
 * 查询标签
 * @param {*} data
 */
export function queryLabelAPI(data) {
  return request({
    url: 'projectLabel/queryList',
    method: 'post',
    data
  })
}

/**
 * 更新标签
 * @param {*} data
 */
export function updateLabelAPI(data) {
  return request({
    url: 'projectLabel/update',
    method: 'post',
    data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 设置任务下标签
 * @param {*} data
 */
export function setTaskLabelAPI(data) {
  return request({
    url: 'projectLabel/updateTaskLabel',
    method: 'post',
    data
  })
}
// ======================= 标签 end=======================

/**
 *
 * 关联wiki
 * @param {*} data
 */
export function relativeWikiAPI(data) {
  return request({
    url: 'projectTask/updateTaskWiki',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

// =====================================================================

/**
 *
 * 删除任务
 * @param {*} data
 */
export function workTaskDeleteAPI(taskId) {
  return request({
    url: `workTask/deleteWorkTask/${taskId}`,
    method: 'post'
  })
}

/**
 *
 * 完成任务状态
 * @param {*} data
 */
export function workTaskStatusSetAPI(data) {
  return request({
    url: 'workTask/setWorkTaskStatus',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 *
 * 完成子任务状态
 * @param {*} data
 */
export function workTaskChildStatusSetAPI(data) {
  return request({
    url: 'workTask/setWorkChildTaskStatus',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 *
 * 编辑任务标题
 * @param {*} data
 */
export function workTaskTitleSetAPI(data) {
  return request({
    url: 'workTask/setWorkTaskTitle',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 *
 * 编辑任务描述
 * @param {*} data
 */
export function workTaskDescriptionSetAPI(data) {
  return request({
    url: 'workTask/setWorkTaskDescription',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 *
 * 分配任务（负责人）
 * @param {*} data
 */
export function workTaskMainUserSetAPI(data) {
  return request({
    url: 'workTask/setWorkTaskMainUser',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 *
 * 设置任务时间
 * @param {*} data
 */
export function workTaskTimeSetAPI(data) {
  return request({
    url: 'workTask/setWorkTaskTime',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 *
 * 设置任务标签
 * @param {*} data
 */
export function workTaskLabelSetAPI(data) {
  return request({
    url: 'workTask/setWorkTaskLabel',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 *
 * 添加任务参与人
 * @param {*} data
 */
export function workTaskOwnerUserSetAPI(data) {
  return request({
    url: 'workTask/setWorkTaskOwnerUser',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 *
 * 添加任务参与人
 * @param {*} data
 */
export function workTaskOwnerUserDeleteAPI(data) {
  return request({
    url: 'workTask/deleteWorkTaskOwnerUser',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 *
 * 设置任务优先级
 * @param {*} data
 */
export function workTaskPrioritySetAPI(data) {
  return request({
    url: 'workTask/setWorkTaskPriority',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 *
 * 修改事项优先级
 * @param {*} data
 */
export function workSetPriorityAPI(data) {
  return request({
    url: 'projectTask/setPriority',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 *
 * 更新缺陷
 * @param {*} data
 */
export function workSetProjectWrongTypeAPI(data) {
  return request({
    url: 'projectTask/setProjectWrongType',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 *
 * 上传附件调用日志接口
 * @param {*} data
 */
export function upLoadFileRelationLogAPI(data) {
  return request({
    url: 'projectTask/upLoadFileRelationLog',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 *
 * 新增子工作项
 * @param {*} data
 */
export function workSetProjectChildTaskAPI(data) {
  return request({
    url: 'projectTask/setProjectChildTask',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 *
 * 修改事项进度值
 * @param {*} data
 */
export function workSetProgressAPI(data) {
  return request({
    url: 'projectTask/setProgress',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 *
 * 修改事项进度值
 * @param {*} data
 */
export function workSetEstimatedManHoursAPI(data) {
  return request({
    url: 'projectTask/setEstimatedManHours',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 *
 * 新建子任务
 * @param {*} data
 */
export function workSubTaskAddAPI(data) {
  return request({
    url: 'workTask/addWorkChildTask',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 *
 * 编辑子任务
 * @param {*} data
 */
export function workSubTaskUpdateAPI(data) {
  return request({
    url: 'workTask/updateWorkChildTask',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 *
 * 删除子任务
 * @param {*} data
 */
export function workSubTaskDeleteAPI(taskId) {
  return request({
    url: `workTask/deleteWorkChildTask/${taskId}`,
    method: 'post'
  })
}

/**
 *
 * 任务设置事件状态
 * @param {*} data
 */
export function projectBoardTaskSetStatusAPI(data) {
  return request({
    url: 'projectBoardTask/setTaskStatus',
    method: 'post',
    data
  })
}

/**
 *
 * 项目（趋势图）
 * @param {*} data
 */
export function getProjectByTimeAPI(data) {
  return request({
    url: 'projectTask/getProjectByTime',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 *
 * 迭代（任务趋势图）
 * @param {*} data
 */
export function getTaskByTimeAPI(data) {
  return request({
    url: 'projectTask/getTaskByTime',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 *
 * 迭代（燃尽图）
 * @param {*} data
 */
export function getTaskBurnoutAPI(data) {
  return request({
    url: 'projectTask/getTaskBurnout',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 *
 * 当前迭代（事件分布图）
 * @param {*} data
 */
export function getProjectTaskEventAPI(data) {
  return request({
    url: 'projectTask/getProjectTaskEvent',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 *
 * 关联需求-移除需求
 * @param {*} data
 */
export function projectTaskRelevancyRelatedDemandAPI(data) {
  return request({
    url: 'projectTask/relevancyRelatedDemand',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 *
 * 设置项目子任务状态
 * @param {*} data
 */
export function projectTaskSetChildTaskStatusAPI(data) {
  return request({
    url: 'projectTask/setProjectChildTaskStatus',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 导入模板下载
 * @param {*} data
 */
export function itemImportTemplateAPI(data) {
  return request({
    url: 'projectTask/downloadExcel',
    method: 'post',
    data: data,
    responseType: 'blob'
  })
}

/**
 * 事项导入
 * @param {*} data
 * @returns
 */
export function itemExcelImportAPI(data) {
  var param = new FormData()
  Object.keys(data).forEach(key => {
    param.append(key, data[key])
  })
  return request({
    url: 'projectTask/excelImport',
    method: 'post',
    data: param,
    headers: {
      'Content-Type': 'multipart/form-data'
    },
    timeout: 60000
  })
}

/**
 * 下载事项导入错误数据
 * @param {*} data
 *
 */
export function itemErrorExcelDownAPI(data) {
  return request({
    url: 'projectTask/downloadErrorExcel',
    method: 'post',
    data: data,
    responseType: 'blob'
  })
}

/**
 * 获取事项导出字段
 * @param {*} data
 *
 */
export function itemExportFieldAPI(data) {
  return request({
    url: 'projectTask/projectTaskExportColumn',
    method: 'post',
    data: data
  })
}

/**
 * 获取事项导出
 * @param {*} data
 *
 */
export function itemAllExportAPI(data) {
  return request({
    url: 'projectTask/projectTaskExport',
    method: 'post',
    data: data,
    responseType: 'blob',
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 *
 * 设置任务团队成员
 * @param {*} data
 */
export function relatedProjectTaskUserAPI(data) {
  return request({
    url: 'projectTaskUser/relatedProjectTaskUser',
    method: 'post',
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    },
    data
  })
}

/**
 *
 * backlog排序
 * @param {*} data
 */
export function backLogListSortAPI(data) {
  return request({
    url: 'projectTask/sort/backlog',
    method: 'post',
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    },
    data
  })
}

/**
 *
 * 工作台事项tab数量
 * @param {*} data
 */
export function workbenchItemNumAPI(data) {
  return request({
    url: 'projectTask/queryUserTaskCount',
    method: 'post',
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    },
    data
  })
}

