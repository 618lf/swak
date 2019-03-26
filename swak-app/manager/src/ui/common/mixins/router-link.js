/**
 * add vue-router support
 */
export default {
  props: {
    url: String,
    replace: Boolean,
    to: [String, Object]
  },

  methods: {
    routerLink(e) {
      const {to, url, $router, replace} = this
      if (to && $router) {
        $router[replace ? 'replace' : 'push'](to)
      } else if (url) {
        replace ? location.replace(url) : location.href = url
      } else {
        this.$emit('link:click', e)
      }
    }
  }
}
