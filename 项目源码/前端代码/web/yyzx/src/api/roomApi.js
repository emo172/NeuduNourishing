// 引入封装好的axios实例
import http from '@/request/request.js';

/**
 * 查询床位列表
 * @param {Object} data - 查询参数（如房间类型、状态等）
 * @returns {Promise} - 返回床位列表数据的Promise对象
 */
export function listRoom(data) {
	return http.get('/room/listRoom', {
		params: data
	});
};