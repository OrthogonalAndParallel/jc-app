import User from './../User.vue'
import Log from './../Log.vue'

export const AppRegistry = {
    User: {
        component: User,
        icon: './assets/user.jpg',
        name: 'User',
        initMethod: 'initAndShow'
    },
    Log: {
        component: Log,
        icon: './assets/log.jpg',
        name: 'Log',
        initMethod: 'initAndShow'
    }
}