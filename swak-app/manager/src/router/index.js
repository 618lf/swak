import Vue from 'vue'
import Router from 'vue-router'

const _import = require('./_import')

Vue.use(Router)

/**
 * icon : the icon show in the sidebar
 * hidden : if `hidden:true` will not show in the sidebar
 * redirect : if `redirect:noredirect` will not redirct in the levelbar
 * noDropdown : if `noDropdown:true` will not has submenu in the sidebar
 * meta : `{ role: ['admin'] }`  will control the page role
 **/
export const constantRouterMap = [
  {path: '/', component: _import('index'), hidden: true, meta: {title: '首页'}},
  {path: '/404', component: _import('error/404'), hidden: true, meta: {title: '页面没找到'}},
  {path: '/401', component: _import('error/401'), hidden: true, meta: {title: '没有相关权限'}},
  {path: '*', redirect: '/404', hidden: true}
]

export const asyncRouterMap = []

export default new Router({
  mode: 'hash',
  scrollBehavior: () => ({y: 0}),
  routes: constantRouterMap.concat(asyncRouterMap)
})