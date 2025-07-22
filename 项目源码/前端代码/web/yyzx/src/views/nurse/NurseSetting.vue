<template>
	<div class="common-layout">
		<el-container>
			<el-header>
				<div class="header-container">
					<el-form :inline="true" class="search-form">
						<el-row :gutter="15" class="search-row">
							<!-- 客户姓名查询框 -->
							<el-col :span="6" class="input-col">
								<el-form-item label="客户姓名: ">
									<el-input placeholder="请输入客户姓名" v-model="condition.customerName" clearable
										@clear="query" size="large" class="full-width">
									</el-input>
								</el-form-item>
							</el-col>
							<!-- 查询按钮 -->
							<el-col :span="3" class="btn-col">
								<el-form-item>
									<el-button type="primary" @click="query" color="#337ab7" class="operation-btn">
										<el-icon>
											<Search />
										</el-icon>
										<span>查询</span>
									</el-button>
								</el-form-item>
							</el-col>
						</el-row>
					</el-form>
				</div>
			</el-header>
			<el-divider style="margin:0"></el-divider>
			<el-main>
				<div class="card-container">
					<div class="card-header">
						<span class="card-title">客户护理级别管理</span>
					</div>

					<!-- 表格 -->
					<el-table :data="khxxList" style="width: 100%; color:black;" stripe :max-height="tableMaxHeight"
						size="small">
						<el-table-column align="center" fixed type="index" :index="indexMethod" label="序号" width="60" />
						<el-table-column align="center" fixed prop="customerName" label="客户姓名" width="120" />
						<el-table-column align="center" prop="customerAge" label="年龄" width="80" />
						<el-table-column align="center" prop="customerSex" label="性别" width="80">
							<template #default="scope">{{ scope.row.customerSex==0?'男':'女'}}</template>
						</el-table-column>
						<el-table-column align="center" prop="roomNo" label="房间号" width="100" />
						<el-table-column align="center" prop="bedNo" label="床号" width="100" />
						<el-table-column align="center" prop="buildingNo" label="所属楼房" width="100" />
						<el-table-column align="center" prop="contactTel" label="联系电话" width="150" />
						<el-table-column align="center" prop="levelName" label="护理级别" width="120" />
						<el-table-column align="center" fixed="right" label="操作" width="180">
							<template #default="scope">
								<div class="operation-group">
									<el-button type="primary" icon="Star" size="small" class="operation-btn"
										v-if="scope.row.levelId==null" @click="opensetting(scope.row)">
										设置护理级别
									</el-button>
									<el-button type="danger" icon="Delete" size="small" class="operation-btn"
										v-if="scope.row.levelId!=null" @click="del(scope.row)">
										移除护理级别
									</el-button>
								</div>
							</template>
						</el-table-column>
					</el-table>

					<!-- 空数据提示 -->
					<div v-if="khxxList.length === 0 && !loading" class="empty-data">
						<el-empty description="没有找到相关数据" />
					</div>

					<!-- 加载中提示 -->
					<div v-if="loading" class="loading">
						<el-loading-spinner />
						<p>加载中...</p>
					</div>

					<!-- 分页插件 -->
					<div style="margin-top:15px" v-if="page.total > 0">
						<el-pagination :page-size="condition.pageSize" background :current-page="page.currentPage"
							layout="prev, pager, next" :total="page.total" @current-change="handleCurrentChange" />
					</div>
				</div>

				<!-- 护理级别设置抽屉 -->
				<el-drawer title="客户护理设置" :before-close="handleClose" v-model="drawer.openFlag" direction="rtl"
					size="70%">
					<el-container>
						<el-header>
							<div class="drawer-header">
								<el-row>
									<el-col :span="8">
										<el-select v-model="drawer.levelId" @change="listNurseItemByLevel"
											style="width:320px" placeholder="选择护理级别">
											<!-- 添加默认选项 -->
											<el-option label="请选择" value="" disabled></el-option>
											<!-- 护理级别选项渲染 -->
											<template v-if="loadingLevel">
												<el-option disabled label="加载中..."></el-option>
											</template>
											<template
												v-else-if="drawer.listLevel && Array.isArray(drawer.listLevel) && drawer.listLevel.length > 0">
												<el-option v-for="item in drawer.listLevel" :key="item.id"
													:label="item.levelName" :value="item.id" />
											</template>
											<template v-else>
												<el-option disabled label="暂无可用护理级别"></el-option>
											</template>
										</el-select>
									</el-col>
								</el-row>
							</div>
						</el-header>
						<el-divider style="margin:0"></el-divider>
						<el-main class="drawer-main">
							<el-table :data="drawer.isInLevelItemList" size="small" stripe>
								<el-table-column align="center" type="index" label="序号" width="60" />
								<el-table-column align="center" prop="serialNumber" label="编号" width="80" />
								<el-table-column align="center" prop="nursingName" label="名称" width="100" />
								<el-table-column align="center" prop="servicePrice" label="价格" width="80" />
								<el-table-column align="center" prop="executionCycle" label="执行周次" width="80" />
								<el-table-column align="center" prop="executionTimes" label="执行次数" width="80" />
								<el-table-column align="center" prop="buyTime" label="服务购买日期" width="150" />
								<el-table-column align="center" prop="nurseNumber" label="购买数量" width="100">
									<template #default="scope">
										<el-input v-model.number="scope.row.nurseNumber" placeholder="购买数量"></el-input>
									</template>
								</el-table-column>
								<el-table-column align="center" prop="maturityTime" label="服务到期日期" width="160">
									<template #default="scope">
										<el-date-picker style="width:130px" format="YYYY/MM/DD"
											value-format="YYYY-MM-DD" v-model="scope.row.maturityTime" type="date"
											placeholder="服务到期日期"></el-date-picker>
									</template>
								</el-table-column>
							</el-table>

							<!-- 抽屉空数据提示 -->
							<div v-if="drawer.isInLevelItemList.length === 0 && !loadingLevel" class="drawer-empty">
								<el-empty description="暂无护理项目数据" />
							</div>
						</el-main>
						<div class="demo-drawer__footer">
							<el-button @click="handleClose" class="operation-btn">取 消</el-button>
							<el-button @click="isOk" type="primary" class="operation-btn">确认</el-button>
						</div>
					</el-container>
				</el-drawer>
			</el-main>
		</el-container>
	</div>
</template>

<script>
	import {
		listKhxxPage
	} from "@/api/customerApi.js";
	import {
		listNurseLevel,
		listNurseItemByLevel
	} from "@/api/nurseLevelApi.js";
	import {
		addItemToCustomer,
		removeCustomerLevelAndItem
	} from "@/api/customerNurseItemApi.js";
	import {
		Search,
		Star,
		Delete,
		WarningFilled,
		WarnTriangleFilled
	} from '@element-plus/icons-vue'

	export default {
		components: {
			Search,
			Star,
			Delete,
			WarningFilled,
			WarnTriangleFilled
		},
		computed: {
			// 分页序号计算
			indexMethod() {
				return (index) => (this.page.currentPage - 1) * this.condition.pageSize + index + 1;
			}
		},
		data() {
			return {
				tableMaxHeight: 0, // 表格最大高度
				// 抽屉数据
				drawer: {
					openFlag: false,
					listLevel: [],
					levelId: null, // 初始化为null，与el-select的默认值匹配
					customerId: "",
					isInLevelItemList: []
				},
				// 查询条件
				condition: {
					customerName: "",
					currentPage: 1, // 当前页码
					pageSize: 10 // 每页显示数量
				},
				khxxList: [],
				// 分页属性
				page: {
					total: 0, // 总记录数
					pageSize: 10, // 每页显示数量
					currentPage: 1, // 当前页码
					pagCount: 0 // 总页数
				},
				loading: false, // 表格加载状态
				loadingLevel: false // 护理级别加载状态
			};
		},
		mounted() {
			// 计算表格高度
			this.calcTableHeight();
			// 监听窗口大小变化
			window.addEventListener('resize', this.calcTableHeight);

			this.listKhxxPage();
		},
		beforeUnmount() {
			// 移除事件监听
			window.removeEventListener('resize', this.calcTableHeight);
		},
		methods: {
			// 计算表格最大高度
			calcTableHeight() {
				// 窗口高度 - 头部高度 - 边距（根据实际布局调整）
				this.tableMaxHeight = window.innerHeight - 200;
			},

			// 点击查询
			query() {
				this.condition.currentPage = 1;
				this.page.currentPage = 1;
				this.listKhxxPage();
			},

			// 页码变更
			handleCurrentChange(curPage) {
				this.page.currentPage = curPage;
				this.condition.currentPage = curPage;
				this.listKhxxPage();
			},

			// 打开抽屉
			opensetting(row) {
				// 添加空值检查，确保row存在且有id属性
				if (!row || !row.id) {
					console.error('无法获取客户ID，row对象可能为空或缺少id属性');
					this.$message.error('操作失败，请检查数据');
					return;
				}

				this.drawer.openFlag = true;
				this.drawer.customerId = row.id;
				this.drawer.levelId = null; // 重置选择的级别
				this.drawer.isInLevelItemList = []; // 清空项目列表
				this.fetchNurseLevels(); // 调用优化后的方法获取护理级别
			},

			// 获取护理级别列表（优化版）
			fetchNurseLevels() {
				this.loadingLevel = true;
				// 构造查询参数，只获取启用状态的护理级别
				const params = {
					levelStatus: 1
				};

				listNurseLevel(params).then(res => {
					if (res && res.data && Array.isArray(res.data.records)) {
						this.drawer.listLevel = res.data.records;
					} else {
						this.drawer.listLevel = [];
						this.$message.warning('获取护理级别失败或没有可用级别');
					}
				}).catch(error => {
					console.error('获取护理级别失败:', error);
					this.$message.error("获取护理级别失败，请稍后再试");
				}).finally(() => {
					this.loadingLevel = false;
				});
			},

			// 关闭抽屉
			handleClose() {
				this.drawer = {
					openFlag: false,
					listLevel: [],
					levelId: null,
					customerId: "",
					isInLevelItemList: []
				};
			},

			// 确认添加护理项目
			isOk() {
				let customerItemList = this.drawer.isInLevelItemList;
				let paramsArr = [];

				// 确保levelId是有效整数
				if (!this.drawer.levelId || isNaN(parseInt(this.drawer.levelId))) {
					this.$message.error("请选择有效的护理级别");
					return;
				}

				if (!Array.isArray(customerItemList) || customerItemList.length === 0) {
					this.$message.error("当前护理级别下没有可添加的项目");
					return;
				}

				// 验证每个项目的必填字段
				for (let i = 0; i < customerItemList.length; i++) {
					let item = customerItemList[i];
					if (!item || !item.nurseNumber || !item.maturityTime) {
						this.$message.error(`请完善第 ${i+1} 项的购买数量和服务到期日期`);
						return;
					}

					let obj = {
						itemId: item.id,
						buyTime: item.buyTime,
						customerId: this.drawer.customerId,
						nurseNumber: item.nurseNumber,
						isDeleted: 0,
						maturityTime: item.maturityTime,
						levelId: parseInt(this.drawer.levelId) // 确保levelId是整数
					};
					paramsArr.push(obj);
				}

				this.$confirm("确定添加此护理级别及其护理项目数据无误?", "提示", {
						confirmButtonText: "确定",
						cancelButtonText: "取消",
						type: "warning"
					})
					.then(() => {
						addItemToCustomer({
							customerId: this.drawer.customerId,
							items: paramsArr
						}).then(res => {
							if (res.flag) {
								this.$message.success(res.message);
								this.listKhxxPage();
								this.handleClose();
							} else {
								this.$message.error(res.message);
							}
						});
					})
					.catch(() => {});
			},

			// 移除护理级别
			del(row) {
				// 同样可以添加对row的空值检查
				if (!row || !row.id || !row.levelId) {
					console.error('无法获取客户或级别ID');
					this.$message.error('操作失败，请检查数据');
					return;
				}

				// 确保levelId是有效整数
				const levelId = parseInt(row.levelId);

				if (isNaN(levelId)) {
					this.$message.error('无效的护理级别ID');
					return;
				}

				this.$confirm("此操作将会移除当前护理级别正在进行中的护理项目?", "提示", {
						confirmButtonText: "确定",
						cancelButtonText: "取消",
						type: "warning"
					})
					.then(() => {
						removeCustomerLevelAndItem({
							levelId: levelId,
							customerId: row.id
						}).then(res => {
							if (res.flag) {
								this.$message.success(res.message);
								this.listKhxxPage();
							} else {
								this.$message.error(res.message);
							}
						});
					})
					.catch(() => {});
			},

			// 查询客户信息列表-分页
			listKhxxPage() {
				this.loading = true;

				// 创建正确的请求参数对象，确保参数为有效整数
				const requestParams = {
					customerName: this.condition.customerName,
					pageNum: parseInt(this.condition.currentPage) || 1, // 确保pageNum是有效整数
					pageSize: parseInt(this.condition.pageSize) || 10 // 确保pageSize是有效整数
				};

				listKhxxPage(requestParams).then(res => {
					if (!res || !res.data) {
						console.error('接口返回格式不正确:', res);
						this.$message.error('获取数据失败');
						this.khxxList = [];
						this.page.total = 0;
						this.page.pagCount = 0;
						this.loading = false;
						return;
					}

					// 确保获取到的records是数组类型
					if (res.data.records && Array.isArray(res.data.records) && res.data.records.length > 0) {
						this.khxxList = res.data.records;
					} else {
						// 如果返回空数据或非数组数据，清空表格
						this.khxxList = [];
						if (this.condition.currentPage > 1) {
							this.$message.warning(`第 ${this.condition.currentPage} 页没有数据`);
							// 自动跳回第一页
							this.condition.currentPage = 1;
							this.page.currentPage = 1;
							this.listKhxxPage();
							return;
						}
					}

					this.page.total = res.data.total || 0;
					this.page.pageSize = res.data.size || this.condition.pageSize;
					this.page.pagCount = res.data.pages || 0;

					// 关键：更新当前页码为后端返回的实际页码
					if (res.data.current !== undefined) {
						this.page.currentPage = res.data.current;
						this.condition.currentPage = res.data.current;
					}

					this.loading = false;
				}).catch(error => {
					console.error('分页查询失败:', error);
					this.$message.error("分页查询失败");
					this.loading = false;
				});
			},

			// 查询当前级别护理项目
			listNurseItemByLevel() {
				if (!this.drawer.levelId) {
					this.drawer.isInLevelItemList = [];
					return;
				}

				// 确保levelId是有效整数
				const levelId = parseInt(this.drawer.levelId);

				if (isNaN(levelId)) {
					this.$message.error('无效的护理级别ID');
					this.drawer.isInLevelItemList = [];
					return;
				}

				listNurseItemByLevel({
					levelId: levelId
				}).then(res => {
					// 从正确的位置提取数据 - 后端数据在 res.data.data 中
					let data = res.data || {};
					let newArr = Array.isArray(data.data) ? data.data : [];

					for (let i = 0; i < newArr.length; i++) {
						const item = newArr[i] || {};
						item.nurseNumber = item.nurseNumber || 1;

						const now = new Date();
						const buyDate = new Date();
						const maturityDate = new Date();
						maturityDate.setMonth(maturityDate.getMonth() + 3);

						item.buyTime = this.formatDate(buyDate);
						item.maturityTime = this.formatDate(maturityDate);
						newArr[i] = item;
					}
					this.drawer.isInLevelItemList = newArr;
				}).catch(error => {
					console.error('获取护理项目失败:', error);
					this.$message.error('获取护理项目失败');
					this.drawer.isInLevelItemList = [];
				});
			},

			// 日期格式化工具方法
			formatDate(date) {
				return `${date.getFullYear()}-${(date.getMonth()+1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')}`;
			}
		}
	};
</script>

<style scoped>
	/* 全局布局样式 */
	.common-layout {
		height: 100%;
		display: flex;
		flex-direction: column;
	}

	.el-container {
		height: 100%;
		display: flex;
		flex-direction: column;
	}

	/* 头部样式 */
	.el-header {
		padding: 15px 20px;
		height: auto !important;
		background-color: #f8f9fa;
		box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
	}

	.header-container {
		width: 100%;
	}

	.search-form {
		width: 100%;
	}

	.search-row {
		width: 100%;
		display: flex;
		align-items: center;
	}

	.input-col {
		display: flex;
		align-items: center;
	}

	.btn-col {
		display: flex;
		align-items: center;
		padding: 0 5px !important;
	}

	.full-width {
		width: 100%;
	}

	/* 主体内容样式 */
	.el-main {
		padding: 15px;
		flex: 1;
		overflow: auto;
		background-color: #f5f7fa;
	}

	/* 卡片容器样式 */
	.card-container {
		width: 100%;
		background-color: #fff;
		border-radius: 8px;
		box-shadow: 0 1px 3px rgba(0, 0, 0, 0.12), 0 1px 2px rgba(0, 0, 0, 0.24);
		overflow: hidden;
	}

	.card-header {
		height: 50px;
		background-color: #3ca2e0;
		color: #fff;
		font-size: 16px;
		line-height: 50px;
		padding: 0 20px;
	}

	.card-title {
		font-weight: 500;
	}

	/* 表格样式 */
	.el-table {
		width: 100%;
		margin: 0 20px 20px;
		border-radius: 4px;
		overflow: hidden;
	}

	.el-table th {
		background-color: #f5f7fa;
		color: #606266;
		font-weight: 500;
	}

	.el-table__row:hover>td {
		background-color: #ecf5ff !important;
	}

	/* 按钮样式 */
	.operation-btn {
		padding: 6px 12px;
		font-size: 13px;
		height: 36px;
		margin: 0 3px !important;
		border-radius: 4px;
		transition: all 0.3s ease;
		font-weight: 500;
	}

	.operation-group {
		display: flex;
		align-items: center;
		justify-content: center;
		gap: 5px;
	}

	.el-button--primary {
		color: #0050B3;
		border-color: #409EFF;
		background-color: #E6F7FF;
	}

	.el-button--primary:hover {
		color: #0039A6;
		border-color: #3A8EE6;
		background-color: #BAE1FF;
	}

	.el-button--danger {
		color: #C12E2A;
		border-color: #F56C6C;
		background-color: transparent;
	}

	.el-button--danger:hover {
		color: #9E1F1B;
		border-color: #E53935;
		background-color: #FFF0F0;
	}

	/* 分页样式 */
	.el-pagination {
		text-align: right;
		padding: 15px 20px 15px 0;
	}

	/* 空数据提示样式 */
	.empty-data {
		text-align: center;
		padding: 50px 0;
		color: #909399;
	}

	/* 加载中样式 */
	.loading {
		text-align: center;
		padding: 50px 0;
		color: #606266;
	}

	/* 抽屉样式 */
	.drawer-header {
		padding: 15px 0;
	}

	.drawer-main {
		padding: 15px;
		overflow: auto;
	}

	.drawer-empty {
		text-align: center;
		padding: 30px 0;
		color: #909399;
	}

	.demo-drawer__footer {
		padding: 15px 20px;
		text-align: right;
		border-top: 1px solid #e8e8e8;
	}

	/* 响应式样式 */
	@media (max-width: 768px) {
		.search-row {
			flex-wrap: wrap;
		}

		.input-col,
		.btn-col {
			flex: 1 0 100%;
			margin-bottom: 10px;
			padding: 0 !important;
		}

		.el-table__header-wrapper,
		.el-table__body-wrapper {
			overflow-x: auto;
		}

		.el-pagination {
			text-align: center;
			padding-right: 0;
		}

		.operation-btn {
			padding: 4px 8px;
			font-size: 12px;
			height: 32px;
		}
	}
</style>