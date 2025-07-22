// 引入封装好的axios@等价于/src
import http from "@/request/request.js";

/**
 * 添加护理记录
 * @param {Object} data - 护理记录数据
 * @returns {Promise} - 包含响应数据的Promise
 */
export function addNurseRecord(data) {
	return http.post('/nurserecord/addNurseRecord', data);
}

/**
 * 客户护理记录信息动态查询
 * @param {Object} data - 查询条件，包含分页信息
 * @returns {Promise} - 包含响应数据的Promise
 */
export function listNurseRecordsVo(data) {
	return http.get('/nurserecord/listNurseRecordsVo', {
		params: data
	});
}

/**
 * 移除护理记录（软删除）
 * @param {Object} data - 包含记录ID的对象
 * @returns {Promise} - 包含响应数据的Promise
 */
export function softDeleteCustomerRecord(data) {
	return http.get('/nurserecord/softDeleteCustomerRecord', {
		params: data
	});
}

/**
 * 查询外出记录
 * @param {Object} data - 查询条件
 * @returns {Promise} - 包含响应数据的Promise
 */
export function queryOutwardVo(data) {
	return http.post('/nurserecord/queryOutwardVo', data);
}