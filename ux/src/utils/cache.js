import store from '@/store'
import Lockr from 'lockr'
import axios from '@/utils/request'
import Cookies from 'js-cookie'
import { getCookiesDomain } from '@/utils'

const cache = {
  /**
   * 载入全部登陆信息
   */
  loadingCache: function() {
    if (Lockr.get('AUTH-TOKEN') && !axios.defaults.headers.common['AUTH-TOKEN']) {
      /** 将用户信息放入缓存 */
      const userInfo = Lockr.get('loginUserInfo')
      if (userInfo) {
        store.commit('SET_USERINFO', userInfo)
      }
    }
    store.commit('SET_APPNAME', Lockr.get('systemName'))
    store.commit('SET_APPLOGO', Lockr.get('systemLogo'))
  },
  /**
   * 请求和更新登录缓存
   */
  updateAxiosCache: function(token) {
    axios.defaults.headers.common['AUTH-TOKEN'] = token || Lockr.get('AUTH-TOKEN')
    if (token) {
      Lockr.set('AUTH-TOKEN', token)
    }
  },
  updateAxiosHeaders: function(token) {
    const newToken = token || Lockr.get('AUTH-TOKEN')

    if (token) {
      Lockr.set('AUTH-TOKEN', token)
    }

    if (newToken && axios.defaults.headers.common['AUTH-TOKEN'] !== newToken) {
      axios.defaults.headers.common['AUTH-TOKEN'] = newToken
      return true // token 变动
    }
  },
  /**
   * 移除登录信息
   * @param {*}
   */
  rmAxiosCache: function() {
    Cookies.remove('AUTH-TOKEN', { domain: getCookiesDomain() })
    Lockr.rm('AUTH-TOKEN')
  }
}

export default cache
