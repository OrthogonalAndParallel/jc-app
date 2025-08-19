<template>
  <div class="box-index">
    <DockBar></DockBar>
    <component
        v-for="(comp, name) in $options.components"
        :is="name"
        :key="name"
        :ref="name"
        @click.native="bringToFront(name)"
        :style="{ zIndex: appZIndexes[name] || 1 }"
    />
  </div>
</template>

<script>
import DockBar from './DockBar.vue'
import {AppRegistry} from './apps/data/AppRegistry.js'
export default {
  components: {
    DockBar,
    ...Object.fromEntries(
        Object.entries(AppRegistry).map(([name, { component }]) => [name, component])
    )
  },
  code: 'Desktop',
  name: '桌面',
  data () {
    return {
      appZIndexes: {},
      maxZIndex: 10
    }
  },
  methods: {
    // 设置应用最前
    bringToFront(appName) {
      // 增加最大 zIndex 值
      this.maxZIndex += 1;
      // 设置被点击的应用为最前
      this.appZIndexes[appName] = this.maxZIndex;
    }
  }
}
</script>

<style rel="stylesheet/scss" lang="scss">
.box-index {
  width: 100%;
  height: 100%;
  padding: 0;
  margin: 0;
  display: flex;
  justify-content: center;
  align-items: center;
  background-size: cover;
  background-image: url('/assets/bg.jpg');
  overflow: hidden;
  position: fixed;
}
</style>
