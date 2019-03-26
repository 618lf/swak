import axios from 'axios'
import Qs from 'qs'
import store from '../store'
import {env} from '../env'
import {Notification} from 'element-ui';

const service = axios.create({
  baseURL: env.VUE_APP_BASE_URL,
  timeout: 1500000, // ms
  headers: {'X-Requested-With': 'XMLHttpRequest'},
  transformRequest: [function (data) {
    return Qs.stringify(data);
  }],
})

service.interceptors.request.use(config => {
  if (!!store.getters.token) {
    config.headers['X-Token'] = store.getters.token
  }
  return config
}, error => {
  Promise.reject(error)
})

service.interceptors.response.use(
  response => {
    const res = response.data
    if (response.config.responseType != 'blob') {
      if (!res.code || res.code === 50000) {
        Notification({
          title: '提示',
          message: '请求错误，请刷新重试！',
          type: 'error'
        })
        return Promise.reject('error')
      } else if (res.code === 40005) {
        store.dispatch('Logout').then(res => {
          location.href = '/login'
        })
        Notification({
          title: '提醒',
          message: '您还没有登录系统，请登录！',
          type: 'error'
        })
      } else if (res.code === 40004) {
        Notification({
          title: '错误',
          message: '您需要订阅服务，才能进行后续操作',
          type: 'error'
        })
        return Promise.reject('error')
      }
      return res;
    } else {
      var fileName = response.headers['content-disposition'].substring("attachment;filename=".length)
      return {
        fileName: fileName,
        data: res
      }
    }
  },
  error => {
    Notification({
      title: '提示',
      message: '网络链接异常',
      duration: 0,
      type: 'error'
    })
    return Promise.reject(error)
  }
)

export default service
