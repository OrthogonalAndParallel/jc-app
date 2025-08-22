import { EventBus } from '../../../utils/event-bus'
import { AppRegistry } from "../data/AppRegistry.js"

export const AppMixin = {
    data() {
        return {
            currentPanel: '',
            direction: 'forward'
        }
    },
    created() {
        EventBus.on('show-app', (appName) => {
            if (appName === this.name) {
                const appConfig = AppRegistry[appName]
                if (appConfig && appConfig.initMethod && this.$refs.app && this.$refs.app[appConfig.initMethod]) {
                    this.$refs.app[appConfig.initMethod](this.title)
                }
            }
        })
    },
    methods: {
        changePanel(newPanelName) {
            const panels = ['form', 'table']; // 在此处定义面板的顺序，或从 AppRegistry 获取
            const currentIndex = panels.indexOf(this.currentPanel);
            const newIndex = panels.indexOf(newPanelName);

            if (newIndex > currentIndex) {
                this.direction = 'forward';
            } else if (newIndex < currentIndex) {
                this.direction = 'backward';
            }

            this.currentPanel = newPanelName;
        }
    },
    beforeDestroy() {
        EventBus.off('show-app')
    }
}