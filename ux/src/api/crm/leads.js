import request from '@/utils/request'

/**
 * 新建 编辑
 * @param {*} data
 */
export function crmLeadsSaveAPI(data) {
  const url = data.entity && data.entity.leadsId ? 'update' : 'add'
  return request({
    url: 'crmLeads/' + url,
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 列表
 * @param {*} data
 */
export function crmLeadsIndexAPI(data) {
  return request({
    url: 'crmLeads/queryPageList',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 删除
 * @param {*} data
 */
export function crmLeadsDeleteAPI(data) {
  return request({
    url: 'crmLeads/deleteByIds',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 详情
 * @param {*} id
 */
export function crmLeadsReadAPI(id) {
  return request({
    url: `crmLeads/queryById/${id}`,
    method: 'post'
  })
}

/**
 * 线索转移
 * @param {*} data
 */
export function crmLeadsTransferAPI(data) {
  return request({
    url: 'crmLeads/changeOwnerUser',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 线索转换为客户
 * @param {*} data
 */
export function crmLeadsTransformAPI(data) {
  return request({
    url: 'crmLeads/transfer',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 线索导出
 * @param {*} data
 *
 */
export function crmLeadsExcelExportAPI(data) {
  return request({
    url: 'crmLeads/batchExportExcel',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    },
    responseType: 'blob'
  })
}

/**
 * 线索全部导出
 * @param {*} data
 */
export function crmLeadsExcelAllExportAPI(data) {
  return request({
    url: 'crmLeads/allExportExcel',
    method: 'post',
    data: data,
    responseType: 'blob',
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 线索导入
 * @param {*} data
 *
 */
export function crmLeadsExcelImportAPI(data) {
  var param = new FormData()
  Object.keys(data).forEach(key => {
    param.append(key, data[key])
  })
  return request({
    url: 'crmLeads/uploadExcel',
    method: 'post',
    data: param,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 线索导入模板下载
 * @param {*} data
 *
 */
export const crmLeadsExcelDownloadURL = process.env.VUE_APP_BASE_API + 'crmLeads/downloadExcel'
export function crmLeadsDownloadExcelAPI(data) {
  return request({
    url: 'crmLeads/downloadExcel',
    method: 'post',
    data: data,
    responseType: 'blob'
  })
}

/**
 * 线索标记跟进
 * @param {*} data
 * id 客户IDs
 */
export function crmLeadsSetFollowAPI(data) {
  return request({
    url: 'crmBackLog/setLeadsFollowup',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 附件列表
 * @param {*} data
 *
 */
export function crmLeadsFileListAPI(data) {
  return request({
    url: 'crmLeads/queryFileList',
    method: 'post',
    data: data
  })
}

/**
 * tab数量
 * @param {*} data
 *
 */
export function crmLeadsNumAPI(data) {
  return request({
    url: 'crmLeads/num',
    method: 'post',
    data: data
  })
}

