import {
  adminSystemIndexAPI
} from '@/api/admin/config'
import {
  crmSettingConfigDataAPI
} from '@/api/admin/crm'
import {
  adminConfigsetIndexAPI
} from '@/api/admin/application'

import Lockr from 'lockr'
import merge from '@/utils/merge'

const DefaultSystemAlertProps = {
  title: '',
  type: 'warning',
  description: '',
  closable: true,
  center: false,
  closeText: '',
  showIcon: true,
  effect: 'light'
}

/** 记录 侧边索引 */
const app = {
  state: {
    logo: '',
    name: '',
    lang: localStorage.lang || 'cn',
    sidebar: {
      collapse: Lockr.get('sideBarCollapse') || false
    },
    // CRM配置信息
    CRMConfig: {},
    // 图片缓存
    imageCache: {},
    // 活动咨询是否开启
    marketingEnable: false,
    // 导航栏系统提示
    systemAlertShow: false,
    systemAlertProps: DefaultSystemAlertProps,
    // 模块权限
    moduleAuth: null,
    // 当前账套的月份
    nowMounth: '',
    cancelTokenArr: [], // 取消请求token数组

    moduleData: [] // 各模块状态 包含 时间 次数等信息
  },

  mutations: {
    SET_NOWMOUNTH: (state, num) => {
      state.nowMounth = num
    },
    SET_COLLAPSE: (state, collapse) => {
      state.sidebar.collapse = collapse
      Lockr.set('sideBarCollapse', collapse)
    },
    SET_APPLOGO: (state, logo) => {
      state.logo = logo
    },
    SET_APPNAME: (state, name) => {
      state.name = name
    },
    SET_LANG: (state, lang) => {
      state.lang = lang
      window.app.$i18n.locale = lang
      localStorage.setItem('lang', lang)
      window.location.reload()
    },
    SET_CRMCONFIG: (state, config) => {
      state.CRMConfig = config
    },
    SET_IMAGECACHE: (state, value) => {
      state.imageCache = value
    },
    SET_MARKETINGENABLE: (state, value) => {
      state.marketingEnable = value
    },
    SET_SYSTEMALERTSHOW: (state, value) => {
      state.systemAlertShow = value
    },
    SET_SYSTEMALERTPROPS: (state, value) => {
      state.systemAlertProps = merge({ ...DefaultSystemAlertProps }, value || {})
    },
    SET_MODULEAUTH: (state, value) => {
      state.moduleAuth = value
    },
    SET_CANCELTOKENARR(state, payload) {
      state.cancelTokenArr.push(payload.cancelToken)
    },
    CLEAR_CANCELTOKENARR({ cancelTokenArr }) {
      cancelTokenArr.forEach(item => {
        item('路由跳转取消请求')
      })
      cancelTokenArr = []
    },
    SET_MODULE_DATA(state, data) {
      state.moduleData = data
    }
  },

  actions: {
    // 登录
    SystemLogoAndName({
      commit
    }) {
      return new Promise((resolve, reject) => {
        adminSystemIndexAPI().then(response => {
          const resData = response.data || {}
          commit('SET_APPNAME', resData.companyName)
          commit('SET_APPLOGO', resData.companyLogo)
          Lockr.set('systemLogo', resData.companyLogo)
          Lockr.set('systemName', resData.companyName)

          // 注释掉缓存头像逻辑
          // getImageData(resData.url)
          //   .then((data) => {
          //   }).catch(() => {})

          resolve(response)
        }).catch(error => {
          reject(error)
        })
      })
    },

    /**
     * 获取客户管理配置
     */
    CRMSettingConfig({
      commit
    }) {
      return new Promise((resolve, reject) => {
        crmSettingConfigDataAPI().then(response => {
          commit('SET_CRMCONFIG', response.data)
          resolve(response)
        }).catch(error => {
          reject(error)
        })
      })
    },

    // // 查询活动配置
    // QueryMarketing({
    //   commit,
    //   state
    // }) {
    //   return new Promise((resolve, reject) => {
    //     configQueryMarketingAPI().then(res => {
    //       state.marketingEnable = res.data == 1
    //       resolve(res)
    //     }).catch(error => {
    //       reject(error)
    //     })
    //   })
    // },

    // 查询模块权限
    QueryModules({
      commit,
      state
    }) {
      return new Promise((resolve, reject) => {
        adminConfigsetIndexAPI().then(res => {
          const resData = res.data || []
          // status 状态 1:启用 0:停用 2:试用中 3:已过期,可用值:0,1,2,3
          const auth = {}
          resData.forEach(item => {
            auth[item.module] = item.status === 1 || item.status === 2
          })
          commit('SET_MODULEAUTH', auth)
          commit('SET_MODULE_DATA', resData)
          resolve(res)
        }).catch(error => {
          reject(error)
        })
      })
    }
  },

  getters: {
    lang: state => state.lang,
    app: state => state,
    logo: state => {
      if (state.logo) {
        return state.logo
      }
      return require('@/assets/img/logo.png')
    },
    name: state => {
      if (state.name) {
        return state.name
      }
      return window.WKConfig.companyName
    },
    nowMounth: state => state.nowMounth,
    moduleData: state => state.moduleData,

    collapse: state => state.sidebar.collapse,
    systemAlertShow: state => state.systemAlertShow,
    systemAlertProps: state => state.systemAlertProps,
    moduleAuth: state => state.moduleAuth,

    // 配置信息
    CRMConfig: state => state.CRMConfig,
    imageCache: state => state.imageCache
  }
}

export default app
