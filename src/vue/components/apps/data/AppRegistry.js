import DataFix from './../DataFix.vue'
// 预先导入图标
import gitIcon from '/assets/git.jpg'

export const AppRegistry = {
    DataFix: {
        component: DataFix,
        icon: '/assets/git.jpg',  // 向上三级目录
        name: 'DataFix',
        initMethod: 'initAndShow'
    }
}