<template>
	<div class="common-layout">
		<el-container>
			<el-header>
				<div class="header-container">
					<el-form :inline="true" class="search-form">
						<el-row :gutter="15" class="search-row">
							<!-- 真实姓名输入框 -->
							<el-col :span="6" class="input-col">
								<el-form-item label="管家名称: ">
									<el-input placeholder="请输入管家姓名" v-model="queryParams.nickname" @clear="query"
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
						</el-row>
					</el-form>
				</div>
			</el-header>
			<el-divider style="margin:0"></el-divider>
			<el-main>
				<div class="card-container">
					<div class="card-header">
						<span class="card-title">健康管家管理</span>
					</div>

					<!-- 表格 -->
					<el-table :data="userList" style="width: 100%; color:black;" stripe :max-height="tableMaxHeight"
						size="small">
						<el-table-column align="center" type="index" :index="indexMethod" label="序号" width="80" />
						<el-table-column align="center" prop="nickname" label="姓名" width="140" />
						<el-table-column align="center" prop="phoneNumber" label="电话" width="160" />
						<el-table-column align="center" prop="sex" label="性别" width="100">
							<template #default="scope">{{ scope.row.sex==1?'男':'女'}}</template>
						</el-table-column>
						<el-table-column align="center" prop="email" label="邮箱" width="200" />
						<el-table-column align="center" fixed="right" label="操作" width="260">
							<template #default="scope">
								<div class="operation-group">
									<el-button type="primary" icon="Star" size="small" class="operation-btn"
										@click="userToCustomerService(scope.row)">
										设置服务对象
									</el-button>
									<el-button type="danger" icon="Delete" size="small" class="operation-btn"
										@click="handleDelete(scope.row.id)">
										删除
									</el-button>
								</div>
							</template>
						</el-table-column>
					</el-table>

					<!-- 空数据提示 -->
					<div v-if="userList.length === 0" class="empty-data">
						<el-empty description="暂无健康管家数据" />
					</div>

					<!-- 分页插件 -->
					<div style="margin-top:15px" v-if="page.total > 0">
						<el-pagination :page-size="page.pageSize" background :current-page="page.currentPage"
							layout="prev, pager, next, sizes" :page-sizes="[5, 10, 20, 50]" :total="page.total"
							@current-change="handleCurrentChange" @size-change="handleSizeChange" />
					</div>
				</div>
			</el-main>
		</el-container>
	</div>
</template>

<script>
	import {
		findUserPage,
		delUser
	} from "@/api/userApi.js";
	import {
		Star,
		Delete,
		Search
	} from '@element-plus/icons-vue'

	export default {
		components: {
			Star,
			Delete,
			Search
		},
		computed: {
			indexMethod() {
				return (index) => (this.page.currentPage - 1) * this.page.pageSize + index + 1;
			}
		},
		data() {
			return {
				tableMaxHeight: 0, // 表格最大高度
				// 分页属性封装
				page: {
					total: 0,
					pageSize: 10,
					currentPage: 1,
					pageCount: 0
				},
				queryParams: {
					roleId: "2", // 固定查询健康管家角色
					nickname: "",
					pageNum: 1,
					pageSize: 10
				},
				userList: []
			};
		},
		mounted() {
			// 计算表格高度
			this.calcTableHeight();
			// 监听窗口大小变化
			window.addEventListener('resize', this.calcTableHeight);

			this.findUserPage();
		},
		beforeUnmount() {
			// 移除事件监听
			window.removeEventListener('resize', this.calcTableHeight);
		},
		methods: {
			// 计算表格最大高度
			calcTableHeight() {
				// 窗口高度 - 头部高度 - 边距
				this.tableMaxHeight = window.innerHeight - 200;
			},

			// 点击查询
			query() {
				this.page.currentPage = 1;
				this.queryParams.pageNum = 1;
				this.findUserPage();
			},

			// 页码改变事件
			handleCurrentChange(curPage) {
				this.page.currentPage = curPage;
				this.queryParams.pageNum = curPage;
				this.findUserPage();
			},

			// 每页条数改变事件
			handleSizeChange(size) {
				this.page.pageSize = size;
				this.queryParams.pageSize = size;
				this.page.currentPage = 1;
				this.queryParams.pageNum = 1;
				this.findUserPage();
			},

			// 设置服务对象
			userToCustomerService(row) {
				this.$router.push({
					path: "/health/userToCustomerService",
					query: {
						userId: row.id,
						nickName: row.nickname
					}
				});
			},

			// 删除用户
			handleDelete(userId) {
				this.$confirm("此操作将删除该健康管家记录，是否继续?", "提示", {
					confirmButtonText: "确定",
					cancelButtonText: "取消",
					type: "warning"
				}).then(() => {
					delUser({
						id: userId
					}).then(res => {
						if (res.flag) {
							this.$message.success(res.message);
							this.query();
						} else {
							this.$message.error(res.message);
						}
					}).catch(error => {
						console.error("删除失败:", error);
						this.$message.error("删除失败，请稍后重试");
					});
				}).catch(() => {});
			},

			// 查询用户列表(分页)
			findUserPage() {
				this.queryParams.pageNum = this.page.currentPage;
				this.queryParams.pageSize = this.page.pageSize;

				findUserPage(this.queryParams).then(res => {
					if (res.flag) {
						this.userList = res.data.records || [];
						this.page.total = res.data.total || 0;
						this.page.pageSize = res.data.size || this.page.pageSize;
						this.page.currentPage = res.data.current || this.page.currentPage;
						this.page.pageCount = res.data.pages || 0;
					} else {
						this.$message.error(res.message || "查询失败");
						this.userList = [];
					}
				}).catch(error => {
					console.error("查询管家列表失败:", error);
					this.$message.error("查询数据失败，请检查网络连接");
					this.userList = [];
				});
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