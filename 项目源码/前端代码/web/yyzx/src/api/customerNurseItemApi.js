// customerNurseItemApi.js 文件
import http from "@/request/request.js";

/**
 * 向客户添加护理项目
 * @param {Object} data - 包含customerId和items的对象
 * @returns {Promise} - 包含服务器响应的Promise对象
 */
export function addItemToCustomer(data) {
	return http.post(`/customernurseitem/addItemToCustomer/${data.customerId}`, data.items, {
		headers: {
			'Content-Type': 'application/json'
		}
	});
}

/**
 * 移除客户的护理级别和项目
 * @param {Object} data - 包含级别ID和客户ID的对象
 * @returns {Promise} - 包含服务器响应的Promise对象
 */
export function removeCustomerLevelAndItem(data) {
	return http.get('/customernurseitem/removeCustomerLevelAndItem', {
		params: data
	});
}

/**
 * 获取客户护理项目列表
 * @param {Object} data - 查询参数（如客户ID、护理项目类型等）
 * @returns {Promise} - 包含护理项目列表的Promise对象
 */
export function listCustomerItem(data) {
	return http.get('/customernurseitem/listCustomerItem', {
		params: data
	});
}

/**
 * 更新护理项目信息
 * @param {Object} data - 要更新的护理项目数据
 * @returns {Promise} - 包含更新结果的Promise对象
 */
export function updateNurseItem(data) {
	return http.post('/customernurseitem/updateNurseItem', data);
}

/**
 * 检查客户是否包含特定护理项目
 * @param {Object} data - 查询参数（如客户ID、项目ID等）
 * @returns {Promise} - 包含检查结果的Promise对象
 */
export function isIncludesItemCustomer(data) {
	return http.get('/customernurseitem/isIncludesItemCustomer', {
		params: data
	});
}

/**
 * 移除客户的护理项目
 * @param {Object} data - 请求参数（如客户ID、项目ID等）
 * @returns {Promise} - 包含操作结果的Promise对象
 */
export function removeCustomerItem(data) {
	return http.get('/customernurseitem/removeCustomerItem', {
		params: data
	});
}