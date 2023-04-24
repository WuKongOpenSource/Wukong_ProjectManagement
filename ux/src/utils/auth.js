import axios from '@/utils/request'
import baseAxios from 'axios'
import cache from './cache'
import Lockr from 'lockr'
import store from '@/store'
import Cookies from 'js-cookie'
import { getCookiesDomain } from '@/utils'

/** 移除授权信息 */
export function removeAuth(props = { clearCookies: false }) {
  return new Promise((resolve, reject) => {
    cache.rmAxiosCache()
    store.commit('SET_ALLAUTH', null)
    delete axios.defaults.headers.common['AUTH-TOKEN']
    resolve(true)
  })
}

/** 注入授权信息 */
export function addAuth(token) {
  axios.defaults.headers.common['AUTH-TOKEN'] = token
  Lockr.set('AUTH-TOKEN', token)
  const domain = getCookiesDomain()
  Cookies.set('AUTH-TOKEN', token, { domain: domain, expires: 365 })
}

/** 获取授权信息 */
export function getAuth() {
  return new Promise((resolve, reject) => {
    const token = Cookies.get('AUTH-TOKEN') || Lockr.get('AUTH-TOKEN')
    if (!token) return reject('Not Found Token')

    cache.updateAxiosCache(token)
    if (!store.state.user.userInfo) {
      store.dispatch('GetUserInfo')
        .then(() => {
          resolve()
        })
        .catch(error => {
          reject(error)
        })
    } else {
      resolve()
    }
  })
}

/**
 * 重定向到登录页
 */
export function redirectLogin() {
  baseAxios.get(
    '/APPLICATION_ID.txt',
    {
      transformResponse: [data => {
        data = data ? data.toString() : ''
        data = data.replace(/\r\n/g, '')
        data = data.replace(/\r|\n/g, '')
        return data
      }]
    }
  )
    .then(res => {
      const id = res.data
      const redirect = encodeURIComponent(window.location.origin + window.location.pathname)
      window.location.href = `${process.env.VUE_APP_ID_CENTER_LOGIN_URL}?redirectUrl=${redirect}&appId=${id}`
    })
    .catch(e => {
      window.location.href = process.env.VUE_APP_ID_CENTER_LOGIN_URL
    })
}
