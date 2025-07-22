import {
	createStore
} from 'vuex'
import {
	getSessionStorage,
	setSessionStorage
} from '@/utils/common.js'

export default createStore({
	state: {
		tabs: [],
		menus: getSessionStorage('menuList') || []
	},
	getters: {
		tabs(state) {
			return state.tabs;
		},
		menus(state) {
			return state.menus;
		}
	},
	mutations: {
		addMenus(state, param) {
			state.menus = param;
			setSessionStorage('menuList', param);
		},
		deleteTabByIndex(state, index) {
			state.tabs.splice(index, 1);
		},
		clearTab(state, param) {
			state.tabs = param;
		},
		addTab(state, payload) {
			let path = payload.path;
			if (path) {
				let result = state.tabs.filter((item) => {
					return item.path == path;
				});
				if (result.length == 0) {
					if (state.tabs.length == 10) {
						state.tabs.splice(1, 1);
					}
					state.tabs.push(payload);
				}
			}
		},
	},
	actions: {
		// 添加setMenu action
		setMenu({
			commit
		}, menu) {
			commit('addMenus', menu);
		}
	},
	modules: {}
})