import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: '/',
            redirect: '/login'
        },
        {
            path: '/login',
            name: 'login',
            component: () => import('../views/Login.vue')
        },
        {
            path: '/web',
            name: 'webLayout',
            component: () => import('../views/web/Layout.vue'),
            children: [
                { path: 'home', component: () => import('../views/web/Home.vue') },
                { path: 'order', component: () => import('../views/web/Order.vue') },
                { path: 'health', component: () => import('../views/web/Health.vue') },
                { path: 'activity', component: () => import('../views/web/Activity.vue') },
                { path: 'mall', component: () => import('../views/web/PointsMall.vue') },
                { path: 'treehole', component: () => import('../views/web/TreeHole.vue') }
            ]
        },
        {
            path: '/app',
            name: 'appLayout',
            component: () => import('../views/app/Layout.vue'),
            children: [
                { path: 'home', component: () => import('../views/app/Home.vue') },
                { path: 'order', component: () => import('../views/app/Order.vue') }
            ]
        },
        {
            path: '/org',
            name: 'orgLayout',
            component: () => import('../views/org/Layout.vue'),
            children: [
                { path: 'home', component: () => import('../views/org/Home.vue') },
                { path: 'order', component: () => import('../views/org/Order.vue') }
            ]
        },
        {
            path: '/platform',
            name: 'platformLayout',
            component: () => import('../views/platform/Layout.vue'),
            children: [
                { path: 'home', component: () => import('../views/platform/Home.vue') },
                { path: 'users', component: () => import('../views/platform/Users.vue') },
                { path: 'complaints', component: () => import('../views/platform/Complaints.vue') },
                { path: 'logs', component: () => import('../views/platform/Logs.vue') },
                { path: 'articles', component: () => import('../views/platform/Articles.vue') }
            ]
        }
    ]
})

router.beforeEach((to, from, next) => {
    const token = localStorage.getItem('token');
    if (to.path !== '/login' && !token) {
        next('/login');
    } else {
        next();
    }
});

export default router
