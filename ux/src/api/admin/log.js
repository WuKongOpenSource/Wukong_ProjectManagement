import request from '@/utils/request'

/**
 * 查询系统登录日志列表页接口
 * @param {*} data
 */
export function queryLoginLogListAPI(data) {
  return request({
    url: '/biCustomer/queryLoginLogsData',
    method: 'post',
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    },
    data: data
  })
}

/**
 * 登录日志导出
 * @param {*} data
 */
export function loginLogExportAPI(data) {
  return request({
    url: 'biCustomer/exportLoginLogsExcel',
    method: 'post',
    data: data,
    responseType: 'blob',
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    },
    timeout: 60000
  })
}

/**
 * 查看用户操作日志接口
 * @param {*} data
 */
export function querySystemLogListAPI(data) {
  return request({
    url: '/biCustomer/queryLogsData',
    method: 'post',
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    },
    data: data
  })
}

/**
 * 用户操作日志导出
 * @param {*} data
 */
export function systemLogExportAPI(data) {
  return request({
    url: '/biCustomer/exportLogsExcel',
    method: 'post',
    data: data,
    responseType: 'blob',
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    },
    timeout: 60000
  })
}

/**
 * 查询操作行为参数
 * @param {*} data
 */
export function biCustomerQueryBehaviorListAPI(data) {
  return request({
    url: 'biCustomer/queryBehaviorList',
    method: 'post',
    data
  })
}

/**
 * 查询操作对象参数
 * @param {*} data
 */
export function biCustomerQueryTitleListAPI() {
  return request({
    url: 'biCustomer/queryTitleList',
    method: 'post'
  })
}
