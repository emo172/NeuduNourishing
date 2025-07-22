// 引入封装好的axios实例
import http from "@/request/request.js";

/**
 * 添加客户偏好信息
 * @param {Object} data - 客户偏好数据（如客户ID、偏好类型、偏好描述等）
 * @returns {Promise} - 包含添加结果的Promise对象
 */
export function addCustomerpreference(data) {
	return http.post('/customerpreference/addCustomerpreference', data, {
		headers: {
			'Content-Type': 'application/json'
		}
	});
};

/**
 * 更新客户偏好信息
 * @param {Object} data - 要更新的偏好数据（需包含ID）
 * @returns {Promise} - 包含更新结果的Promise对象
 */
export function updateCustomerpreference(data) {
	return http.post('/customerpreference/updateCustomerpreference', data, {
		headers: {
			'Content-Type': 'application/json'
		}
	});
};

/**
 * 删除客户偏好信息
 * @param {Object} data - 删除参数（如偏好ID）
 * @returns {Promise} - 包含删除结果的Promise对象
 */
export function removeCustomerpreference(data) {
	return http.get('/customerpreference/removeCustomerpreference', {
		params: data,
		headers: {
			'Content-Type': 'application/json'
		}
	});
};

/**
 * 分页查询客户偏好列表
 * @param {Object} data - 查询条件（如客户ID、偏好类型、分页信息等）
 * @returns {Promise} - 包含偏好列表的Promise对象
 */
export function listCustomerpreferencePage(data) {
	return http.get('/customerpreference/listCustomerpreferencePage', {
		params: data,
		headers: {
			'Content-Type': 'application/json'
		}
	});
};