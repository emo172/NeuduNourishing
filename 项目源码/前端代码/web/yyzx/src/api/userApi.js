// 引入封装好的axios实例，@等价于/src目录
import http from "@/request/request.js";

/**
 * 用户登录接口
 * @param {Object} data - 登录参数，通常包含用户名和密码
 * @returns {Promise} - 返回一个Promise对象，包含服务器响应数据
 */
export function login(data) {
	return http.get('/user/login', {
		params: data,
		headers: {
			'Content-Type': 'application/json'
		}
	});
}

/**
 * 分页查询用户列表
 * @param {Object} data - 查询参数，通常包含分页信息（页码、每页数量等）
 * @returns {Promise} - 返回一个Promise对象，包含服务器响应数据
 */
export function findUserPage(data) {
	return http.get('/user/findUserPage', {
		params: data,
		headers: {
			'Content-Type': 'application/json'
		}
	});
}

/**
 * 分页查询所有用户列表
 * @param {Object} data - 查询参数，通常包含分页信息（页码、每页数量等）
 * @returns {Promise} - 返回一个Promise对象，包含服务器响应数据
 */
export function findAllUserPage(data) {
	return http.get('/user/findAllUserPage', {
		params: data,
		headers: {
			'Content-Type': 'application/json'
		}
	});
}

/**
 * 添加新用户
 * @param {Object} data - 用户信息，包含用户名、密码等必要字段
 * @returns {Promise} - 返回一个Promise对象，包含服务器响应数据
 */
export function addUser(data) {
	return http.post('/user/addUser', data);
}

/**
 * 更新用户信息
 * @param {Object} data - 需要更新的用户信息，包含用户ID和要更新的字段
 * @returns {Promise} - 返回一个Promise对象，包含服务器响应数据
 */
export function updateUser(data) {
	return http.post('/user/updateUser', data);
}

/**
 * 删除用户
 * @param {Object} data - 删除参数，通常包含要删除的用户ID
 * @returns {Promise} - 返回一个Promise对象，包含服务器响应数据
 */
export function delUser(data) {
	return http.get('/user/delUser', {
		params: data,
		headers: {
			'Content-Type': 'application/json'
		}
	});
}