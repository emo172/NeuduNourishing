import axios from "axios";
import qs from 'qs'; // 用于处理URL编码格式的数据
import router from '../router'; // 用于处理token失效时的路由跳转

/**
 * 创建axios实例并配置基础URL
 * 所有通过此实例发送的请求都会以该URL为前缀
 */
const http = axios.create({
	baseURL: 'http://localhost:9999/yyzx',
	timeout: 10000 // 请求超时时间
});

/**
 * JSON格式API列表
 * 这些API需要使用application/json格式发送数据
 */
const JSON_APIS = [
	// 客户管理相关API
	'/customer/listKhxxPage',

	// 客户护理项相关API
	'/customernurseitem/addItemToCustomer',
	'/customernurseitem/removeCustomerLevelAndItem',

	// 客户偏好相关API
	'/customerpreference/addCustomerpreference',
	'/customerpreference/updateCustomerpreference',
	'/customerpreference/removeCustomerpreference',
	'/customerpreference/listCustomerpreferencePage',

	// 护理内容相关API
	'/nursecontent/addNurseItem',
	'/nursecontent/updateNurseItem',

	// 护理级别相关API
	'/nurselevel/addNurseLevel',
	'/nurselevel/updateNurseLevel',
	'/nurselevel/addItemToLevel',
	'/nurselevel/listNurseLevel',
	'/nurselevel/listNurseItemByLevel',

	// 膳食管理相关API
	'/meal/addMeal',
	'/meal/updateMeal',
	'/meal/removeMeal',
	'/meal/listMealPage',

	// 外出管理相关API
	'/outward/queryOutwardVo',
	'/outward/addOutward',
	'/outward/examineOutward',
	'/outward/delOutward',
	'/outward/updateBackTime',

	// 用户管理相关API
	'/user/findAllUserPage',
	'/user/addUser',
	'/user/updateUser',
];

/**
 * 检查URL是否匹配JSON_API列表中的任何一项
 * @param {string} url - 请求的URL
 * @returns {boolean} - 如果匹配返回true，否则返回false
 */
function shouldUseJson(url) {
	return JSON_APIS.some(api => url.startsWith(api));
}

/**
 * 请求拦截器
 * 处理请求头配置和token注入
 */
http.interceptors.request.use(config => {
	// 注入token
	const token = sessionStorage.getItem('token');
	if (token) {
		config.headers['token'] = token;
	}

	// 处理POST请求的Content-Type
	if (config.method === 'post') {
		// 根据API路径决定使用JSON还是表单格式
		if (shouldUseJson(config.url)) {
			// JSON格式请求
			config.headers['Content-Type'] = 'application/json';
			// axios默认会将data对象转换为JSON字符串
			console.log('使用JSON格式请求:', config.url);
		} else {
			// 表单格式请求
			config.headers['Content-Type'] = 'application/x-www-form-urlencoded';
			config.data = qs.stringify(config.data); // 将对象转换为URL编码格式
			console.log('使用表单格式请求:', config.url);
		}
	}

	return config;
}, error => {
	console.error('请求错误:', error);
	return Promise.reject(error);
});

/**
 * 响应拦截器
 * 处理全局响应逻辑和错误处理
 */
http.interceptors.response.use(response => {
	// 处理token失效的情况
	if (response.data && !response.data.flag && response.data.data === 'token_error') {
		router.push('/login'); // 跳转到登录页
		return Promise.reject(new Error('Token失效，请重新登录'));
	}

	return response.data; // 直接返回响应数据
}, error => {
	console.error('响应错误:', error);

	// 统一错误提示
	if (error.response) {
		const status = error.response.status;
		const message = error.response.data?.message || '请求失败';
		console.error(`HTTP错误 ${status}: ${message}`);

		// 特别处理415错误（不支持的媒体类型）
		if (status === 415) {
			console.error('错误原因：服务器不支持请求的Content-Type，请检查请求头配置');
		}
	}

	return Promise.reject(error);
});

export default http;