// 引入封装好的axios实例
import http from "@/request/request.js";

/**
 * 查询外出申请详情
 * @param {Object} data - 查询参数（如申请ID、客户ID等）
 * @returns {Promise} - 包含外出申请详情的Promise对象
 */
export function queryOutwardVo(data) {
	return http.post('/outward/queryOutwardVo', data, {
		headers: {
			'Content-Type': 'application/json'
		}
	});
}

/**
 * 新增外出申请
 * @param {Object} data - 外出申请数据（如客户ID、外出原因、预计返回时间等）
 * @returns {Promise} - 包含申请结果的Promise对象
 */
export function addOutward(data) {
	return http.post('/outward/addOutward', data, {
		headers: {
			'Content-Type': 'application/json'
		}
	});
}

/**
 * 审核外出申请
 * @param {Object} data - 审核信息（如申请ID、审核状态、审核意见等）
 * @returns {Promise} - 包含审核结果的Promise对象
 */
export function examineOutward(data) {
	return http.post('/outward/examineOutward', data, {
		headers: {
			'Content-Type': 'application/json'
		}
	});
}

/**
 * 删除外出申请
 * @param {Object} data - 删除参数（如申请ID）
 * @returns {Promise} - 包含删除结果的Promise对象
 */
export function delOutward(data) {
	return http.post('/outward/delOutward', data, {
		headers: {
			'Content-Type': 'application/json'
		}
	});
}

/**
 * 更新实际返回时间
 * @param {Object} data - 更新参数（如申请ID、实际返回时间）
 * @returns {Promise} - 包含更新结果的Promise对象
 */
export function updateBackTime(data) {
	return http.post('/outward/updateBackTime', data, {
		headers: {
			'Content-Type': 'application/json'
		}
	});
}