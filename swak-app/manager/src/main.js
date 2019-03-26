import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import ElementUI from 'element-ui';
import UI from './ui'
import '@/permission'

Vue.use(ElementUI)
Vue.use(UI)

new Vue({
  store,
  render: h => h(App),
  router,
}).$mount('#app')
