import axios from 'axios'
import {
  Message
} from 'element-ui'
import {
  redirectLogin,
  removeAuth
} from '@/utils/auth'
import qs from 'qs'
import { debounce } from 'throttle-debounce'
import store from '@/store'

//
// axios 0.18 支持不过滤掉自定义参数
// requestProp 额外的一些说明,与 data  method 同级
// disabledMessage 禁用消息弹框

const errorMessage = debounce(500, (message, type = 'error') => {
  Message({
    message: message,
    duration: 1500,
    type: type
  })
})

axios.defaults.headers.post['Content-Type'] = 'application/x-www-form-urlencoded;charset=UTF-8'
// 创建axios实例
const service = axios.create({
  baseURL: process.env.VUE_APP_BASE_API, // api 的 base_url
  timeout: 600000 // 请求超时时间
})

// request拦截器
service.interceptors.request.use(
  config => {
    const flag = config.headers['Content-Type'] && config.headers['Content-Type'].indexOf('application/json') !== -1
    if (!flag) {
      const mult = config.headers['Content-Type'] && config.headers['Content-Type'].indexOf('multipart/form-data') !== -1
      if (!mult) {
        config.data = qs.stringify(config.data)
      }
    } else {
      if (config.data === undefined || config.data === null) {
        // 不传参的情况下 json类型的提交数据，校准为 空对象
        config.data = {}
      }
    }

    const validUrl = config.url ? config.url.toLowerCase() : ''
    if (!validUrl.includes('admin') &&
    !validUrl.includes('financeauth') &&
    !validUrl.includes('queryallemployeelist') &&
    !validUrl.includes('querytreelist') &&
    !validUrl.includes('login')) {
      config.cancelToken = new axios.CancelToken(function(cancel) {
        store.commit('SET_CANCELTOKENARR', { cancelToken: cancel })
      })
    }
    const { customConfig } = config
    if (customConfig?.removeToken) {
      delete config.headers['Admin-Token']
    }
    return config
  },
  error => {
    // Do something with request error
    return Promise.reject(error)
  }
)

// response 拦截器
service.interceptors.response.use(
  response => {
    const requestProp = response.config.requestProp || {} // 请求配置
    /**
     * code为非20000是抛错 可结合自己业务进行修改
     */
    const res = response.data
    if (response.status === 200 && response.config.responseType === 'blob') { // 文件类型特殊处理
      if (response.headers['content-disposition'] || (response.headers['content-type'] && response.headers['content-type'].indexOf('application/pdf') != -1)) {
        return response
      } else if (response.data) {
        const resultBlob = new Blob([response.data], { type: 'application/json' })
        const fr = new FileReader()
        fr.onload = function() {
          if (this.result) {
            const result = JSON.parse(this.result)
            // 附件下载反馈的302 忽略
            if (result.msg && result.code !== 302) {
              errorMessage(result.msg, result.code == 1 ? 'success' : 'error')
            }
          }
        }
        fr.readAsText(resultBlob)
      }
    } else if (res.code !== 0) {
      // 302	登录已失效
      if (res.code === 302) {
        removeAuth({ clearCookies: true })
        redirectLogin()
      } else {
        if (res.msg && !requestProp.disabledMessage) {
          errorMessage(res.msg)
        }
      }
      return Promise.reject(res)
    } else {
      return res
    }
  },
  error => {
    if (!axios.isCancel(error)) { // 取消请求的情况下，终端Promise调用链
      if (error.response) {
        const response = error.response
        if (response.status === 500) {
          errorMessage('网络错误，请检查您的网络')
        } else if (response.data && response.data.msg) {
          errorMessage(response.data.msg)
        }
      }
      return Promise.reject(error)
    }
  }
)

export default service
