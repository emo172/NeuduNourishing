<template>
	<div class="main">
		<div class="query">
			<table class="query-table">
				<tbody>
					<tr>
						<td>
							<div class="query-form">
								<el-form :inline="true">
									<el-form-item label="楼层：">
										<el-select v-model="form.floor" @change="findCwsyBedVo" style="width: 100px;">
											<el-option label="一楼" value="一楼" />
											<el-option label="二楼" value="二楼" />
										</el-select>
									</el-form-item>
								</el-form>
							</div>
						</td>
						<td style="width:120px">
							<div>
								<img src="@/assets/bed/all.png" />
								<span class="pic-text">总床数:</span>{{count.zcw}}
							</div>
						</td>
						<td style="width:120px">
							<div>
								<img src="@/assets/bed/kx.png" />
								<span class="pic-text">空闲:</span>{{count.kx}}
							</div>
						</td>
						<td style="width:120px">
							<div>
								<img src="@/assets/bed/yr.png" />
								<span class="pic-text">有人:</span>{{count.yr}}
							</div>
						</td>
						<td style="width:120px">
							<div>
								<img src="@/assets/bed/wc.png" />
								<span class="pic-text">外出:</span>{{count.wc}}
							</div>
						</td>
					</tr>
				</tbody>
			</table>
		</div>
		<div class="map">
			<table border="1px" cellspacing="20px" style=" border-collapse: collapse; width:1200px">
				<tbody>
					<tr class="cwsyt_thead">
						<td v-for="(item,index) in listRoom" :key="item.roomNo" v-show="index <1 ">{{item.roomNo}}</td>
						<td class="room_id" rowspan="2" style="background-color:#AEEEEE">电梯厅</td>
						<td class="room_id" rowspan="2" colspan="2" style="background-color:#9AC0CD">洗衣房</td>
						<td class="room_id" rowspan="2" colspan="2" style="background-color:#4EEE94">活动中心</td>
						<td v-for="(item,index) in listRoom" :key="item.roomNo" v-show="index>0 && index<4">
							{{item.roomNo}}
						</td>
						<td class="room_id" rowspan="2" style="background-color:#CDAA7D">走道</td>
						<td v-for="(item,index) in listRoom" :key="item.roomNo" v-show="index>=4 && index<6">
							{{item.roomNo}}
						</td>
					</tr>
					<tr class="cwsyt_tbody">
						<td v-for="(item,index) in listRoom" :key="item.roomNo" v-show="index <6 ">
							<a href="javascript:void(0)" v-for="bedItem in item.bedList" :key="bedItem.bedNo">
								<div>
									<img v-if="bedItem.bedStatus==1" src="@/assets/bed/kx.png" />
									<img v-if="bedItem.bedStatus==2" src="@/assets/bed/yr.png" />
									<img v-if="bedItem.bedStatus==3" src="@/assets/bed/wc.png" />
								</div>{{bedItem.bedNo}}
							</a>
						</td>

					</tr>
					<tr></tr>
					<tr class="cwsyt_thead">
						<td v-for="(item,index) in listRoom" :key="item.roomNo" v-show="index>=6">{{item.roomNo}}</td>
					</tr>
					<tr class="cwsyt_tbody">
						<td v-for="(item,index) in listRoom" :key="item.roomNo" v-show="index >=6 ">
							<a href="javascript:void(0)" v-for="bedItem in item.bedList" :key="bedItem.bedNo">
								<div>
									<img v-if="bedItem.bedStatus==1" src="@/assets/bed/kx.png" />
									<img v-if="bedItem.bedStatus==2" src="@/assets/bed/yr.png" />
									<img v-if="bedItem.bedStatus==3" src="@/assets/bed/wc.png" />
								</div>{{bedItem.bedNo}}
							</a>
						</td>
					</tr>
				</tbody>
			</table>
		</div>
	</div>
</template>

<script>
	import {
		findCwsyBedVo
	} from '@/api/bedApi.js'
	export default {
		data() {
			return {
				//查询条件数据
				form: {
					floor: "一楼"
				},
				count: {
					zcw: '',
					kx: '',
					yr: '',
					wc: ''
				},
				listRoom: []
			};
		},
		mounted() {
			//初始化床位示意图
			this.findCwsyBedVo()
		},
		methods: {
			//查询房间和床位信息列表 、床位统计信息
			findCwsyBedVo() {
				findCwsyBedVo(this.form).then(res => {
					this.count.zcw = res.data.zcw;
					this.count.kx = res.data.kx;
					this.count.yr = res.data.yr;
					this.count.wc = res.data.wc;
					this.listRoom = res.data.roomList
				})
			}
		}
	};
</script>

<style scoped>
	/* 基础布局 */
	.main {
		padding: 20px;
		box-sizing: border-box;
	}

	/* 查询区域样式 */
	.query {
		background-color: #fff;
		border-radius: 8px;
		box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
		padding: 15px 20px;
		margin-bottom: 20px;
		display: flex;
		align-items: center;
		justify-content: space-between;
	}

	.query-table {
		width: 100%;
		white-space: nowrap;
		/* 防止内容换行 */
	}

	.query-form {
		display: flex;
		align-items: center;
	}

	.el-form-item {
		margin-bottom: 0;
	}

	/* 统计信息样式 */
	.bed-stats {
		display: flex;
		align-items: center;
		margin-left: 20px;
	}

	.bed-stat {
		display: flex;
		align-items: center;
		margin-right: 20px;
		/* 增加床位信息之间的间距 */
	}

	.bed-stat img {
		width: 24px;
		height: 24px;
		margin-right: 8px;
		vertical-align: middle;
		/* 图片垂直居中 */
	}

	.pic-text {
		font-weight: 500;
		margin-right: 4px;
		display: inline-block;
		vertical-align: middle;
		/* 文本垂直居中 */
	}

	/* 地图区域样式 */
	.map {
		background-color: #fff;
		border-radius: 8px;
		box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
		padding: 20px;
		overflow-x: auto;
	}

	/* 表格样式优化 */
	.bed-map {
		width: 100%;
		border-collapse: separate;
		border-spacing: 10px;
	}

	.bed-map th,
	.bed-map td {
		border: none;
		padding: 10px;
		text-align: center;
		border-radius: 6px;
		transition: all 0.2s ease;
	}

	/* 表头样式 */
	.cwsyt_thead {
		background-color: #0168b7;
		color: white;
		font-weight: 500;
	}

	/* 床位行样式 */
	.cwsyt_tbody {
		vertical-align: top;
	}

	/* 房间和床位单元格样式 */
	.room-cell {
		background-color: #f5f7fa;
		padding: 8px;
		position: relative;
	}

	.room-id {
		background-color: #e6f7ff;
		color: #0050b3;
		font-weight: bold;
		display: flex;
		align-items: center;
		justify-content: center;
	}

	/* 床位状态样式 */
	.bed-item {
		display: flex;
		flex-direction: column;
		align-items: center;
		cursor: pointer;
		transition: transform 0.2s ease;
	}

	.bed-item:hover {
		transform: translateY(-2px);
	}

	.bed-item img {
		width: 36px;
		height: 36px;
		margin-bottom: 6px;
	}

	.bed-no {
		font-size: 14px;
		font-weight: 500;
	}

	/* 床位状态颜色 */
	.bed-kx img {
		filter: invert(59%) sepia(98%) saturate(562%) hue-rotate(187deg) brightness(103%) contrast(95%);
		/* 绿色 - 空闲 */
	}

	.bed-yr img {
		filter: invert(33%) sepia(94%) saturate(692%) hue-rotate(340deg) brightness(99%) contrast(97%);
		/* 红色 - 有人 */
	}

	.bed-wc img {
		filter: invert(83%) sepia(38%) saturate(780%) hue-rotate(351deg) brightness(98%) contrast(89%);
		/* 黄色 - 外出 */
	}

	/* 特殊区域样式 */
	.elevator {
		background-color: #AEEEEE;
	}

	.laundry {
		background-color: #9AC0CD;
	}

	.activity {
		background-color: #4EEE94;
	}

	.corridor {
		background-color: #CDAA7D;
	}

	/* 响应式设计 - 调整小屏幕处理方式 */
	@media (max-width: 1200px) {
		.map {
			overflow-x: auto;
		}

		.bed-map {
			min-width: 1000px;
		}

		.query {
			flex-wrap: wrap;
			/* 允许查询区域换行 */
		}

		.query-table td {
			padding: 8px;
			/* 调整单元格内边距 */
		}

		.bed-stat {
			margin-right: 15px;
			/* 小屏幕减少间距 */
		}
	}

	@media (max-width: 800px) {
		.main {
			padding: 10px;
		}

		.query {
			flex-direction: column;
			text-align: center;
		}

		.query-table {
			white-space: normal;
			/* 小屏幕允许换行 */
		}

		.bed-stats {
			margin-left: 0;
			margin-top: 10px;
			flex-wrap: wrap;
		}

		.bed-stat {
			margin-right: 15px;
			margin-bottom: 10px;
		}
	}

	/* 确保统计信息不换行并留出空间 */
	.query-table td {
		white-space: nowrap;
		/* 防止统计信息单元格内换行 */
		vertical-align: middle;
		/* 垂直居中对齐 */
		padding: 0 20px;
		/* 单元格左右内边距，增加列间距 */
	}

	.query-table tr {
		height: 50px;
		/* 统一行高，使布局更整齐 */
	}
</style>