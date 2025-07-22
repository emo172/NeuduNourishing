// @/api/nurseItemApi.js
import http from "@/request/request.js";

/**
 * 添加护理项目
 * @param {Object} data - 请求体数据，包含护理项目信息
 * @returns {Promise} - 包含响应数据的Promise对象
 */
export function addNurseItem(data) {
	return http.post('/nursecontent/addNurseItem', data);
}

/**
 * 分页查询护理项目列表
 * @param {Object} data - 请求参数，包含分页和过滤条件
 * @returns {Promise} - 包含分页数据的Promise对象
 */
export function findNurseItemPage(data) {
	// GET请求不需要设置Content-Type
	return http.get('/nursecontent/findNurseItemPage', {
		params: data
	});
}

/**
 * 更新护理项目信息
 * @param {Object} data - 请求体数据，包含更新的护理项目信息
 * @returns {Promise} - 包含响应数据的Promise对象
 */
export function updateNurseItem(data) {
	return http.post('/nursecontent/updateNurseItem', data);
}

/**
 * 删除护理项目
 * @param {Object} data - 请求参数，包含要删除的护理项目ID
 * @returns {Promise} - 包含响应数据的Promise对象
 */
export function delNurseItem(data) {
	// 建议使用DELETE方法
	return http.delete('/nursecontent/delNurseItem', {
		params: data
	});
}