// 引入封装好的axios实例
import http from "@/request/request.js";

/**
 * 查询退住申请列表
 * @param {Object} data - 查询条件对象（如分页信息、申请状态等）
 * @returns {Promise} - 包含退住申请列表的Promise对象
 */
export function listBackdown(data) {
	return http.post("/backdown/listBackdown", data);
};

/**
 * 新增退住申请
 * @param {Object} data - 退住申请数据（如客户ID、退住原因、申请日期等）
 * @returns {Promise} - 包含操作结果的Promise对象
 */
export function addBackdown(data) {
	return http.post('/backdown/addBackdown', data);
};

/**
 * 审核退住申请
 * 注意：当前使用GET请求传递审核数据可能存在安全风险，建议改为POST
 * @param {Object} data - 审核信息（如申请ID、审核状态、审核意见等）
 * @returns {Promise} - 包含审核结果的Promise对象
 */
export function examineBackdown(data) {
	return http.get('/backdown/examineBackdown', {
		params: data
	});
};

/**
 * 删除退住申请
 * @param {Object} data - 删除参数（如申请ID）
 * @returns {Promise} - 包含删除结果的Promise对象
 */
export function delBackdown(data) {
	return http.get('/backdown/delBackdown', {
		params: data
	});
};