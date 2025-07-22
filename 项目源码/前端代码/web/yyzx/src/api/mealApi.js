// 引入封装好的axios实例
import http from "@/request/request.js";

/**
 * 添加膳食信息
 * @param {Object} data - 膳食数据
 * @returns {Promise} - 包含操作结果的Promise对象
 */
export function addMeal(data) {
	return http.post('/meal/addMeal', data, {
		headers: {
			'Content-Type': 'application/json'
		}
	});
};

/**
 * 更新膳食信息
 * @param {Object} data - 要更新的膳食数据（需包含ID）
 * @returns {Promise} - 包含操作结果的Promise对象
 */
export function updateMeal(data) {
	return http.post('/meal/updateMeal', data, {
		headers: {
			'Content-Type': 'application/json'
		}
	});
};

/**
 * 删除膳食信息
 * @param {Object} data - 删除参数（如膳食ID）
 * @returns {Promise} - 包含操作结果的Promise对象
 */
export function removeMeal(data) {
	return http.get('/meal/removeMeal', {
		params: data,
		headers: {
			'Content-Type': 'application/json'
		}
	});
}

/**
 * 分页查询膳食列表
 * @param {Object} data - 查询条件
 * @returns {Promise} - 包含膳食列表的Promise对象
 */
export function listMealPage(data) {
	return http.post('/meal/listMealPage', data, {
		headers: {
			'Content-Type': 'application/json'
		}
	});
};