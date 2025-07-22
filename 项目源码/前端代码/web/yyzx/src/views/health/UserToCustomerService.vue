<template>
	<div class="common-layout">
		<el-container>
			<el-header>
				<div class="header-container">
					<el-form :inline="true" class="search-form">
						<el-row :gutter="15" class="search-row">
							<!-- 客户姓名输入框 -->
							<el-col :span="6" class="input-col">
								<el-form-item label="客户姓名: ">
									<el-input placeholder="请输入客户姓名" v-model="queryParams.customerName" @clear="query"
										clearable size="large" class="full-width">
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
							<!-- 返回按钮 -->
							<el-col :span="3" class="btn-col">
								<el-form-item>
									<el-button type="primary" @click="goBack" color="#337ab7" class="operation-btn">
										<el-icon>
											<ArrowLeft />
										</el-icon>
										<span>返回</span>
									</el-button>
								</el-form-item>
							</el-col>
						</el-row>
					</el-form>
				</div>
			</el-header>
			<el-divider style="margin:0"></el-divider>
			<el-main>
				<el-row :gutter="20" class="main-content">
					<!-- 左侧：无管家客户信息 -->
					<el-col :span="11" class="table-column">
						<div class="card-container">
							<div class="card-header">
								<span class="card-title">无管家客户信息</span>
							</div>
							<!-- 表格 -->
							<el-table :data="notManagerKhxxList" @row-click="changeRow"
								style="width: 100%; color:black;" stripe :max-height="tableMaxHeight" size="small">
								<el-table-column align="center" type="index" :index="indexMethod" label="序号"
									width="60" />
								<el-table-column align="center" fixed prop="customerName" label="客户姓名" width="120" />
								<el-table-column align="center" prop="customerAge" label="年龄" width="80" />
								<el-table-column align="center" prop="customerSex" label="性别" width="80">
									<template #default="scope">{{ scope.row.customerSex==0?'男':'女'}}</template>
								</el-table-column>
								<el-table-column align="center" prop="roomNo" label="房间号" width="80" />
								<el-table-column align="center" prop="bedNo" label="床号" width="80" />
								<el-table-column align="center" prop="levelName" label="护理级别" width="100" />
							</el-table>

							<!-- 空数据提示 -->
							<div v-if="notManagerKhxxList.length === 0" class="empty-data">
								<el-empty description="暂无无管家客户信息" />
							</div>

							<!-- 分页插件 - 移除sizes选择框 -->
							<div style="margin-top:15px" v-if="page.total > 0">
								<el-pagination :page-size="page.pageSize" background :current-page="page.currentPage"
									layout="prev, pager, next" :total="page.total"
									@current-change="handleCurrentChange" />
							</div>
						</div>
					</el-col>

					<!-- 右侧：当前管家服务的客户 -->
					<el-col :span="13" class="table-column">
						<div class="card-container">
							<div class="card-header">
								<span class="card-title">{{nickName}}--服务的客户</span>
							</div>
							<!-- 表格 -->
							<el-table :data="managerKhxxList" style="width: 100%; color:black;" stripe
								:max-height="tableMaxHeight" size="small">
								<el-table-column align="center" type="index" :index="managerKhxxIndexMethod" label="序号"
									width="60" />
								<el-table-column align="center" fixed prop="customerName" label="客户姓名" width="120" />
								<el-table-column align="center" prop="customerAge" label="年龄" width="80" />
								<el-table-column align="center" prop="customerSex" label="性别" width="80">
									<template #default="scope">{{ scope.row.customerSex==0?'男':'女'}}</template>
								</el-table-column>
								<el-table-column align="center" prop="roomNo" label="房间号" width="80" />
								<el-table-column align="center" prop="bedNo" label="床号" width="80" />
								<el-table-column align="center" prop="levelName" label="护理级别" width="100" />
								<el-table-column align="center" fixed="right" label="操作" width="100">
									<template #default="scope">
										<el-button type="danger" icon="Delete" size="small" class="operation-btn"
											@click="del(scope.row.id)">删除</el-button>
									</template>
								</el-table-column>
							</el-table>

							<!-- 空数据提示 -->
							<div v-if="managerKhxxList.length === 0" class="empty-data">
								<el-empty description="暂无服务的客户信息" />
							</div>

							<!-- 分页插件 - 移除sizes选择框 -->
							<div style="margin-top:15px" v-if="manager.page.total > 0">
								<el-pagination :page-size="manager.page.pageSize" background
									:current-page="manager.page.currentPage" layout="prev, pager, next"
									:total="manager.page.total" @current-change="handleCurrentChangeManager" />
							</div>
						</div>
					</el-col>
				</el-row>
			</el-main>
		</el-container>
	</div>
</template>

<script>
	import {
		listKhxxPage,
		editKhxx
	} from "@/api/customerApi.js";
	import {
		Search,
		Delete,
		ArrowLeft
	} from '@element-plus/icons-vue'

	export default {
		components: {
			Search,
			Delete,
			ArrowLeft
		},
		data() {
			return {
				nickName: this.$route.query.nickName,
				tableMaxHeight: 0, // 表格最大高度
				// 分页属性封装-无管家客户
				page: {
					total: 0,
					pageSize: 10, // 每页显示10条
					currentPage: 1,
					pageCount: 0
				},
				queryParams: {
					customerName: "",
					pageNum: 1, // 当前页码
					pageSize: 10, // 每页记录数
					manType: "3" // 无管家客户
				},
				// 当前管家客户信息模型数据
				manager: {
					queryParams: {
						pageNum: 1, // 当前页码
						pageSize: 10, // 每页记录数
						userId: this.$route.query.userId
					},
					page: {
						total: 0,
						pageSize: 10, // 每页显示10条
						currentPage: 1,
						pageCount: 0
					}
				},
				notManagerKhxxList: [], // 无管家客户信息列表
				managerKhxxList: [] // 当前管家的客户信息列表
			};
		},
		mounted() {
			// 计算表格高度
			this.calcTableHeight();
			// 监听窗口大小变化
			window.addEventListener('resize', this.calcTableHeight);

			this.getNotManagerKhxxList();
			this.getManagerKhxxList();
		},
		beforeUnmount() {
			// 移除事件监听
			window.removeEventListener('resize', this.calcTableHeight);
		},
		computed: {
			// 无管家客户序号计算
			indexMethod() {
				return (index) => (this.page.currentPage - 1) * this.page.pageSize + index + 1;
			},
			// 管家客户序号计算
			managerKhxxIndexMethod() {
				return (index) => (this.manager.page.currentPage - 1) * this.manager.page.pageSize + index + 1;
			}
		},
		methods: {
			// 计算表格最大高度
			calcTableHeight() {
				// 窗口高度 - 头部高度 - 边距（根据实际布局调整）
				this.tableMaxHeight = window.innerHeight - 220;
			},

			// 点击查询
			query() {
				this.page.currentPage = 1;
				this.queryParams.pageNum = 1;
				this.getNotManagerKhxxList();
			},

			// 返回按钮
			goBack() {
				// 导航到健康管家管理页面
				this.$router.push("/health/serviceCustomerSetting").then(() => {
					// 关闭当前标签页
					this.closeCurrentTab();
				}).catch(error => {
					console.error("导航失败:", error);
				});
			},

			// 关闭当前标签页
			closeCurrentTab() {
				const currentTabIndex = this.$store.getters.tabs.findIndex(
					tab => tab.path === this.$route.path
				);

				if (currentTabIndex > -1) {
					this.$store.commit("deleteTabByIndex", currentTabIndex);
				} else {
					console.warn("未找到当前标签页，无法关闭");
				}
			},

			// 无管家客户页码改变
			handleCurrentChange(curPage) {
				this.page.currentPage = curPage;
				this.queryParams.pageNum = curPage;
				this.getNotManagerKhxxList();
			},

			// 无管家客户每页条数改变（由于移除了选择框，此方法可保留也可删除，不影响功能）
			handleSizeChange(size) {
				this.page.pageSize = size;
				this.queryParams.pageSize = size;
				this.page.currentPage = 1;
				this.queryParams.pageNum = 1;
				this.getNotManagerKhxxList();
			},

			// 管家客户页码改变
			handleCurrentChangeManager(curPage) {
				this.manager.page.currentPage = curPage;
				this.manager.queryParams.pageNum = curPage;
				this.getManagerKhxxList();
			},

			// 管家客户每页条数改变（由于移除了选择框，此方法可保留也可删除，不影响功能）
			handleManagerSizeChange(size) {
				this.manager.page.pageSize = size;
				this.manager.queryParams.pageSize = size;
				this.manager.page.currentPage = 1;
				this.manager.queryParams.pageNum = 1;
				this.getManagerKhxxList();
			},

			// 设置为当前管家的服务对象
			changeRow(row) {
				editKhxx({
					id: row.id,
					userId: this.manager.queryParams.userId
				}).then(res => {
					if (res.flag) {
						// 刷新表格
						this.getManagerKhxxList();
						this.getNotManagerKhxxList();
					} else {
						this.$message.error(res.message);
					}
				});
			},

			// 查询无管家客户信息列表
			getNotManagerKhxxList() {
				this.queryParams.pageNum = this.page.currentPage;
				this.queryParams.pageSize = this.page.pageSize;

				listKhxxPage(this.queryParams).then(res => {
					if (res.flag) {
						this.notManagerKhxxList = res.data.records || [];
						this.page.total = res.data.total || 0;
						this.page.pageSize = res.data.size || this.page.pageSize;
						this.page.currentPage = res.data.current || this.page.currentPage;
						this.page.pageCount = res.data.pages || 0;
					} else {
						this.$message.error(res.message || "查询失败");
						this.notManagerKhxxList = [];
					}
				}).catch(error => {
					console.error("查询无管家客户失败:", error);
					this.$message.error("查询数据失败，请检查网络连接");
					this.notManagerKhxxList = [];
				});
			},

			// 查询当前管家护理客户
			getManagerKhxxList() {
				this.manager.queryParams.pageNum = this.manager.page.currentPage;
				this.manager.queryParams.pageSize = this.manager.page.pageSize;

				listKhxxPage(this.manager.queryParams).then(res => {
					if (res.flag) {
						this.managerKhxxList = res.data.records || [];
						this.manager.page.total = res.data.total || 0;
						this.manager.page.pageSize = res.data.size || this.manager.page.pageSize;
						this.manager.page.currentPage = res.data.current || this.manager.page.currentPage;
						this.manager.page.pageCount = res.data.pages || 0;
					} else {
						this.$message.error(res.message || "查询失败");
						this.managerKhxxList = [];
					}
				}).catch(error => {
					console.error("查询管家客户失败:", error);
					this.$message.error("查询数据失败，请检查网络连接");
					this.managerKhxxList = [];
				});
			},

			// 移除当前管家服务的客户
			del(id) {
				this.$confirm("此操作将移除该客户的服务关系，是否继续?", "提示", {
						confirmButtonText: "确定",
						cancelButtonText: "取消",
						type: "warning"
					})
					.then(() => {
						editKhxx({
							id: id,
							userId: -1
						}).then(res => {
							if (res.flag) {
								this.$message.success(res.message);
								// 刷新表格
								this.getManagerKhxxList();
								this.getNotManagerKhxxList();
							} else {
								this.$message.error(res.message || "操作失败");
							}
						}).catch(error => {
							console.error("移除客户失败:", error);
							this.$message.error("操作失败，请检查网络连接");
						});
					})
					.catch(() => {});
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

	.main-content {
		width: 100%;
	}

	.table-column {
		display: flex;
	}

	/* 卡片容器样式 */
	.card-container {
		width: 100%;
		background-color: #fff;
		border-radius: 8px;
		box-shadow: 0 1px 3px rgba(0, 0, 0, 0.12), 0 1px 2px rgba(0, 0, 0, 0.24);
		overflow: hidden;
		display: flex;
		flex-direction: column;
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
		flex: 1;
	}

	.el-table th {
		background-color: #f5f7fa;
		color: #606266;
		font-weight: 500;
	}

	.el-table__row:hover>td {
		background-color: #ecf5ff !important;
		transition: background-color 0.3s ease;
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

	.el-button--primary {
		color: #0050B3;
		border-color: #409EFF;
		background-color: #E6F7FF;
	}

	.el-button--primary:hover {
		color: #0039A6;
		border-color: #3A8EE6;
		background-color: #BAE1FF;
		transform: translateY(-2px);
		box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
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
		transform: translateY(-2px);
		box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
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

	/* 响应式样式 */
	@media (max-width: 1024px) {
		.el-row {
			display: flex;
			flex-direction: column;
		}

		.el-col {
			width: 100% !important;
			margin-bottom: 20px;
		}

		.el-table {
			margin: 0 10px 10px;
		}
	}

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