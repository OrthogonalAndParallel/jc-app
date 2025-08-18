import { createRouter, createWebHashHistory } from 'vue-router'


const routes = [
    {
        path: "/",
        redirect: '/Desktop'
    },
    {
        path: "/Desktop",
        name: "桌面",
        component:  () => import('../components/Desktop.vue'),
    }
]

const router = createRouter({
    history: createWebHashHistory(),
    routes
})

export default router
