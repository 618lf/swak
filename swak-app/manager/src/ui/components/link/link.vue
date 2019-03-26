<template>
  <a :class="clazz" @click="onClick">
    <slot/>
  </a>
</template>

<script>
  import RouterLink from '../../common/mixins/router-link'
  
  export default {
    name: 'swak-link',
    mixins: [RouterLink],
    
    props: {
      clazz: {
        type: [String, Object],
        default: ''
      }
    },
    methods: {
      onClick(e) {
        this.routerLink(e)
      }
    }
  }
</script>
