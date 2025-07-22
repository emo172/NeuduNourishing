// @/api/nurseLevelApi.js
import http from "@/request/request.js";

/**
 * 添加护理级别
 * @param {Object} data - 请求体数据，包含护理级别信息
 * @returns {Promise} - 包含响应数据的Promise对象
 */
export function addNurseLevel(data) {
	return http.post('/nurselevel/addNurseLevel', data, {
		headers: {
			'Content-Type': 'application/json'
		}
	});
}

/**
 * 更新护理级别信息
 * @param {Object} data - 请求体数据，包含更新的护理级别信息
 * @returns {Promise} - 包含响应数据的Promise对象
 */
export function updateNurseLevel(data) {
	return http.post('/nurselevel/updateNurseLevel', data, {
		headers: {
			'Content-Type': 'application/json'
		}
	});
}

/**
 * 删除护理级别
 * @param {Object} data - 请求参数，包含要删除的护理级别ID
 * @returns {Promise} - 包含响应数据的Promise对象
 */
export function removeNurseLevel(data) {
	return http.get('/nurselevel/removeNurseLevel', {
		params: data
	});
}

/**
 * 查询护理级别列表
 * @param {Object} data - 请求参数，可包含状态过滤条件
 * @returns {Promise} - 包含护理级别列表的Promise对象
 */
export function listNurseLevel(data) {
	return http.get('/nurselevel/listNurseLevel', {
		params: data,
		headers: {
			'Content-Type': 'application/json'
		}
	});
}

/**
 * 根据护理级别查询关联的护理项目
 * @param {Object} data - 请求参数，包含护理级别ID
 * @returns {Promise} - 包含护理项目列表的Promise对象
 */
export function listNurseItemByLevel(data) {
	return http.get('/nurselevel/listNurseItemByLevel', {
		params: data,
		headers: {
			'Content-Type': 'application/json'
		}
	});
}

/**
 * 添加护理项目到指定级别
 * @param {Object} data - 请求体数据，包含级别ID和项目ID
 * @returns {Promise} - 包含响应数据的Promise对象
 */
export function addItemToLevel(data) {
	return http.post('/nurselevel/addItemToLevel', data, {
		headers: {
			'Content-Type': 'application/json'
		}
	});
}

/**
 * 从护理级别中移除护理项目
 * @param {Object} data - 请求参数，包含级别ID和项目ID
 * @returns {Promise} - 包含响应数据的Promise对象
 */
export function removeNurseLevelItem(data) {
	return http.get('/nurselevel/removeNurseLevelItem', {
		params: data
	});
}