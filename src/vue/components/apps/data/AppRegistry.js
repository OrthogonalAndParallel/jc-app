import DataFix from './../DataFix.vue'
import User from './../User.vue'
import Log from './../Log.vue'

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
    },
    Log: {
        component: Log,
        icon: '/assets/user.jpg',
        name: 'Log',
        initMethod: 'initAndShow'
    }
}