import {
	createApp
} from 'vue'

// 导入elementUI的依赖
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import App from './App.vue'
import router from './router'
import store from './store'

const app = createApp(App)

// 注册ElementPlus图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
	app.component(key, component)
}

// 全局注册ElementPlus
app.use(ElementPlus).use(store).use(router)

// 特别注册通知组件
import {
	ElNotification
} from 'element-plus'
app.config.globalProperties.$notify = ElNotification

app.mount('#app')