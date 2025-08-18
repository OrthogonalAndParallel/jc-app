import { EventBus } from '../../../utils/event-bus'
import { AppRegistry } from "../data/AppRegistry.js"

export const AppMixin = {
    created() {
        EventBus.on('show-app', (appName) => {
            // 只有当前组件名称匹配时才执行
            if (appName === this.name) {
                const appConfig = AppRegistry[appName]
                if (appConfig && appConfig.initMethod && this.$refs.app && this.$refs.app[appConfig.initMethod]) {
                    this.$refs.app[appConfig.initMethod](this.title)
                }
            }
        })
    },
    beforeDestroy() {
        EventBus.off('show-app')
    }
}