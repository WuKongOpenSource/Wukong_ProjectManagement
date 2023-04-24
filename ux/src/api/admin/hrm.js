/*
 * @Description: 悟空软件
 * @Author: 悟空
 * @Date: 2020-06-02 11:26:03
 * @LastEditTime: 2020-07-01 10:17:00
 * @LastEditors: yang
 */
import request from '@/utils/request'

/**
 * 根据类型查询配置列表
 * @param {*} data
 */
export function hrmConfigQueryRecruitChannelAPI(data) {
  return request({
    url: 'hrmConfig/queryRecruitChannelList',
    method: 'post',
    data: data
  })
}

/**
 * 保存招聘渠道
 * @param {*} data
 */
export function hrmConfigSaveRecruitChannelAPI(data) {
  return request({
    url: 'hrmConfig/saveRecruitChannel',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 删除招聘渠道
 * @param {*} data
 */
export function hrmConfigDeleteRecruitChannelAPI(data) {
  return request({
    url: 'hrmConfig/deleteRecruitChannel',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 查询后台配置自定义字段列表
 * @param {*} data
 */
export function hrmConfigQueryFieldsAPI(data) {
  return request({
    url: 'hrmConfig/queryFields',
    method: 'post',
    data: data
  })
}

/**
 * 查询后台配置自定义字段列表
 * @param {*} data
 */
export function hrmConfigQueryFieldByLabelAPI(id) {
  return request({
    url: `hrmConfig/queryFieldByLabel/${id}`,
    method: 'post'
  })
}

/**
 * 保存后台自定义字段
 * @param {*} data
 */
export function hrmConfigSaveFieldAPI(data) {
  return request({
    url: 'hrmConfig/saveField',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 查询淘汰原因
 * @param {*} data
 */
export function hrmConfigQueryRecruitEliminateAPI(data) {
  return request({
    url: 'hrmConfig/queryRecruitEliminateList',
    method: 'post',
    data: data
  })
}

/**
 *
保存淘汰原因
 * @param {*} data
 */
export function hrmConfigSaveRecruitEliminateAPI(data) {
  return request({
    url: 'hrmConfig/saveRecruitEliminate',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 查询考核模板列表
 * @param {*} data
 */
export function hrmConfigQueryAchievementListAPI(data) {
  return request({
    url: 'hrmConfig/queryAchievementTableList',
    method: 'post',
    data: data
  })
}

/**
 * 根据类型查询考核模板
 * @param {*} data
 */
export function hrmConfigQueryAchievementAPI(id) {
  return request({
    url: `hrmConfig/queryAchievementTableById/${id}`,
    method: 'post'
  })
}

/**
 * 添加或修改考核模板考核模板
 * @param {*} data
 */
export function hrmConfigSetAchievementAPI(data) {
  return request({
    url: 'hrmConfig/setAchievementTable',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 查询参保方案列表
 * @param {*} data
 */
export function hrmConfigInsuranceSchemListAPI(data) {
  return request({
    url: 'hrmConfig/queryInsuranceSchemePageList',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 查询参保方案详情
 * @param {*} data
 */
export function hrmConfigInsuranceSchemeDetailAPI(id) {
  return request({
    url: `hrmConfig/queryInsuranceSchemeById/${id}`,
    method: 'post'
  })
}

/**
 * 添加社保方案
 * @param {*} data
 */
export function hrmConfigAddInsuranceSchemAPI(data) {
  return request({
    url: 'hrmConfig/addInsuranceScheme',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 删除社保方案
 * @param {*} data
 */
export function hrmConfigDeleteInsuranceSchemeAPI(id) {
  return request({
    url: `hrmConfig/deleteInsuranceScheme/${id}`,
    method: 'post'
  })
}

/**
 * 添加薪资组
 * @param {*} data
 */
export function hrmSalaryGroupAddAPI(data) {
  return request({
    url: 'hrmSalaryGroup/addSalaryGroup',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 查询薪资组列表
 * @param {*} data
 */
export function hrmSalaryGroupListAPI(data) {
  return request({
    url: 'hrmSalaryGroup/querySalaryGroupPageList',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 修改薪资组
 * @param {*} data
 */
export function hrmSalaryGroupUpdateAPI(data) {
  return request({
    url: 'hrmSalaryGroup/setSalaryGroup',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 删除薪资组
 * @param {*} data
 */
export function hrmSalaryGroupDeleteAPI(id) {
  return request({
    url: `hrmSalaryGroup/delete/${id}`,
    method: 'post'
  })
}

/**
 * 查询计税规则列表
 * @param {*} data
 */
export function hrmSalaryTaxRuleListAPI(data) {
  return request({
    url: 'hrmSalaryTaxRule/queryTaxRuleList',
    method: 'post',
    data: data
  })
}

/**
 * 修改计规则
 * @param {*} data
 */
export function hrmSalaryTaxRuleUpdateAPI(data) {
  return request({
    url: 'hrmSalaryTaxRule/setTaxRule',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 查询薪资项详情
 * @param {*} data
 */
export function hrmSalaryOptionDetailAPI(data) {
  return request({
    url: 'hrmSalaryOption/querySalaryOptionDetail',
    method: 'post',
    data: data
  })
}

/**
 * 修改薪资项
 * @param {*} data
 */
export function hrmSalaryOptionUpdateAPI(data) {
  return request({
    url: 'hrmSalaryOption/setSalaryOption',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 查询员工档案设置字段列表
 * @param {*} data
 */
export function hrmEmployeeArchivesQueryFieldAPI() {
  return request({
    url: 'hrmEmployeeArchives/queryEmployeeArchivesField',
    method: 'post'
  })
}

/**
 * 发送填写档案信息
 * @param {*} data
 */
export function hrmEmployeeArchivesSendAPI(data) {
  return request({
    url: 'hrmEmployeeArchives/sendWriteArchives',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 修改员工档案字段
 * @param {*} data
 */
export function hrmEmployeeArchivesSetFieldAPI(data) {
  return request({
    url: 'hrmEmployeeArchives/setEmployeeArchivesField',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}

/**
 * 查询管理可设置员工字段列表
 * @param {*} data
 */
export function hrmEmployeeFieldManageQueryAPI(data) {
  return request({
    url: 'hrmEmployeeFieldManage/queryEmployeeManageField',
    method: 'post',
    data: data
  })
}

/**
 * 修改管理可以设置员工字段
 * @param {*} data
 */
export function hrmEmployeeFieldManageSetAPI(data) {
  return request({
    url: 'hrmEmployeeFieldManage/setEmployeeManageField',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    }
  })
}
