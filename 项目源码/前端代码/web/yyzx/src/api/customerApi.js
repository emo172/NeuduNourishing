// @/api/customerApi.js
import http from "@/request/request.js";

/**
 * 入住登记
 * @param {Object} data - 客户信息
 * @returns {Promise} - 包含响应数据的Promise
 */
export function addCustomer(data) {
	return http.post('/customer/rzdj', data);
}

/**
 * 客户信息动态查询（分页）
 * @param {Object} data - 查询条件，包含分页信息
 * @returns {Promise} - 包含响应数据的Promise
 */
export function listKhxxPage(data) {
	return http.get('/customer/listKhxxPage', {
		params: data,
		headers: {
			'Content-Type': 'application/json'
		}
	});
}

/**
 * 根据key删除客户
 * @param {Object} data - 包含客户ID和床位ID的对象
 * @returns {Promise} - 包含响应数据的Promise
 */
export function removeCustomer(data) {
	return http.get('/customer/remove', {
		params: data
	});
}

/**
 * 编辑客户信息
 * @param {Object} data - 客户信息
 * @returns {Promise} - 包含响应数据的Promise
 */
export function editKhxx(data) {
	return http.post('/customer/editKhxx', data);
}