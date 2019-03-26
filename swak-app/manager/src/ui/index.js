import {
  Style,
  Link,
} from './module'

const version = '1.0'
const components = [
  Style,
  Link,
]

function install(Vue) {
  if (install.installed) {
    return
  }
  install.installed = true
  components.forEach((Component) => {
    Component.install(Vue)
  })
}

export {
  install,
  version,
  Style,
  Link,
}

export default {
  install,
  version
}
