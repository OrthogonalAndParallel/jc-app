import DataFix from './../DataFix.vue'
import User from './../User.vue'

export const AppRegistry = {
    DataFix: {
        component: DataFix,
        icon: '/assets/git.jpg',
        name: 'DataFix',
        initMethod: 'initAndShow'
    },
    User: {
        component: User,
        icon: '/assets/user.jpg',
        name: 'User',
        initMethod: 'initAndShow'
    }
}