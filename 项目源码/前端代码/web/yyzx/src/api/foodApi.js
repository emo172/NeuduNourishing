// 引入封装好的axios实例
import http from "@/request/request.js";

export function listFood(data) {
	return http.get("/food/listFood", {
		params: data
	});
};