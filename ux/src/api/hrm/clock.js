
import request from '@/utils/request'

/**
 * 分页查询打卡记录列表
 * @param {*} data
 */
export function hrmClockQueryPageListAPI(data) {
  return request({
    url: 'hrmAttendanceClock/queryPageList',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 导出
 * @param {*} data
 */
export function hrmAttendanceClockExportAPI(data) {
  return request({
    url: 'hrmAttendanceClock/excelExport',
    method: 'post',
    responseType: 'blob',
    data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}
