import axios from 'axios'

const instance = axios.create({
  baseURL: 'https://www.5kcrm.com/center/index.php/center/api/',
  method: 'post'
})

instance.interceptors.request.use(
  config => {
    delete config.headers['Admin-Token']
    return config
  },
  err => {
    return Promise.reject(err)
  }
)

export default instance
