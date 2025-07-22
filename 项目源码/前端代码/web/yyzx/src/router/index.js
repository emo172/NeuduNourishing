import {
	createRouter,
	createWebHistory
} from 'vue-router'
import store from '@/store'
import LoginView from "@/views/Login";

const routes = [{
		path: '/:catchAll(.*)',
		component: () => import('@/views/error/404.vue'),
		name: 'NotFound'
	},
	{
		path: "/login",
		component: LoginView
	},
	{
		path: "/",
		redirect: "/login"
	}
]

const router = createRouter({
	history: createWebHistory(process.env.BASE_URL),
	routes
});

/**
 * 初始路由信息
 */
function initRouter() {
	let appendRoute = {};
	appendRoute.path = "/";
	appendRoute.name = "layout";
	appendRoute.component = () => import(`@/views/layout/Layout.vue`);
	appendRoute.children = [];

	// 从store获取当前的权限信息
	let menus = store.getters.menus;
	for (let menu of menus) {
		menu.children.forEach((temp, index) => {
			let item = {};
			// 将从store中获取的路由路径对应组件名称  /user/list"
			let vueName = temp.path
			let indexN = temp.path.lastIndexOf("/") + 1
			vueName = vueName.substr(0, indexN) + vueName.substring(indexN).charAt(0).toUpperCase() + vueName
				.substring(indexN + 1)
			item.component = () => import(`@/views${vueName}.vue`);
			item.path = temp.path;
			item.name = temp.title;
			appendRoute.children.push(item);
		});
	}

	// 添加额外路由
	appendRoute.children.push({
		path: "/nurse/addItemToLevel",
		component: () => import('@/views/nurse/AddItemToLevel.vue'),
		meta: {
			title: "护理级别"
		}
	});

	appendRoute.children.push({
		path: "/health/userToCustomerService",
		component: () => import('@/views/health/UserToCustomerService.vue'),
		meta: {
			title: "用户服务"
		}
	});

	appendRoute.children.push({
		path: "/health/serviceBuy",
		component: () => import('@/views/health/ServiceBuy.vue'),
		meta: {
			title: "服务购买"
		}
	});

	// 调用setMenu action并传递正确的菜单数据
	// 假设menus是从服务器获取的菜单数据
	if (menus && menus.length > 0) {
		store.dispatch("setMenu", menus);
	}

	router.addRoute(appendRoute);
}

// 路由守卫
router.beforeEach((to, from, next) => {
	// 判断是否登录
	let token = sessionStorage.getItem('token');
	if (to.path != '/login') {
		if (token == null || token == '') {
			next('/login')
		}
	}

	let currentPath = to.fullPath;
	let menuArray = store.getters.menus;

	// 第一次访问登录页面时，并没有菜单列表，此时也动态加载了路由并让length变成了3，导致登录后不会再执行初始化加载路由,加上其他额外条件
	if (router.getRoutes().length == 3 && menuArray != null && menuArray.length > 0 && menuArray !=
		'undefined') {
		// 需要动态加载路由信息
		initRouter();
		next({
			path: to.path
		});
		return;
	}

	let menus = store.getters.menus;
	let currentMenu = null;

	let firstLevelSize = menus.length;
	let secondLevelSize = 0;

	for (let i = 0; i < firstLevelSize && currentMenu == null; i++) {
		secondLevelSize = menus[i].children.length;
		for (let j = 0; j < secondLevelSize; j++) {
			// 说明找到了
			if (menus[i].children[j].path == currentPath) {
				currentMenu = menus[i].children[j];
				break;
			}
		}
	}

	if (currentMenu) {
		// 调用vuex判断是否需要添加到state的tabs中
		store.commit("addTab", currentMenu);
	}
	// 放行
	next();
});

export default router