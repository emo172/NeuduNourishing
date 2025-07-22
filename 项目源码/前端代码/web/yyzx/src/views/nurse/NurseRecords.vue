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
				<el-row :gutter="20" class="main-content">
					<!-- 左侧客户信息列表 -->
					<el-col :span="9" class="table-column">
						<div class="card-container">
							<div class="card-header">
								<span class="card-title">客户信息</span>
							</div>
							<!-- 表格 -->
							<el-table :data="khxxList" highlight-current-row @current-change="handleChangeCustomer"
								size="small" style="width: 100%; color:black;" stripe :max-height="tableMaxHeight">
								<el-table-column align="center" type="index" :index="indexMethod" label="序号"
									width="60" />
								<el-table-column align="center" prop="customerName" label="姓名" />
								<el-table-column align="center" prop="customerAge" label="年龄" />
								<el-table-column align="center" prop="customerSex" label="性别">
									<template #default="scope">{{ scope.row.customerSex === 0 ? "男" : "女" }}</template>
								</el-table-column>
								<el-table-column align="center" prop="bedNo" label="床号" width="90" />
								<el-table-column align="center" prop="levelName" label="护理级别" />
							</el-table>

							<!-- 分页插件 -->
							<div style="margin-top:15px" v-if="khxxList.length > 0">
								<el-pagination :page-size="page.pageSize" background :current-page="page.currentPage"
									layout="prev, pager, next" :total="page.total"
									@current-change="handleCurrentChange" />
							</div>

							<!-- 空数据提示 -->
							<div v-if="khxxList.length === 0" class="empty-data">
								<el-empty description="暂无客户信息数据" />
							</div>
						</div>
					</el-col>

					<!-- 右侧护理记录列表 -->
					<el-col :span="15" class="table-column">
						<div class="card-container">
							<div class="card-header">
								<span class="card-title">护理记录</span>
							</div>
							<!-- 表格 -->
							<el-table :data="nurseRecordList" size="small" stripe :max-height="tableMaxHeight">
								<el-table-column align="center" type="index" :index="indexMethodRecord" label="序号"
									width="60" />
								<el-table-column align="center" prop="serialNumber" label="护理项目编号" width="120" />
								<el-table-column align="center" prop="nursingName" label="护理项目名称" width="120" />
								<el-table-column align="center" prop="nursingCount" label="护理数量" width="90" />
								<el-table-column align="center" prop="nursingContent" label="护理内容" width="120" />
								<el-table-column align="center" prop="nickname" label="护理人员" width="100" />
								<el-table-column align="center" prop="phoneNumber" label="护理人员号码" width="120" />
								<el-table-column align="center" prop="nursingTime" label="护理时间" width="140" />
								<el-table-column align="center" label="操作" width="100">
									<template #default="scope">
										<el-button type="danger" size="small" class="operation-btn"
											@click="del(scope.row.id)">
											移除
										</el-button>
									</template>
								</el-table-column>
							</el-table>

							<!-- 分页插件 -->
							<div style="margin-top:15px" v-if="nurseRecordList.length > 0">
								<el-pagination :page-size="pageRecord.pageSize" background
									:current-page="pageRecord.currentPage" layout="prev, pager, next"
									:total="pageRecord.total" @current-change="handleRecordChange" />
							</div>

							<!-- 空数据提示 -->
							<div v-if="nurseRecordList.length === 0 && conditionRecord.customerId" class="empty-data">
								<el-empty description="暂无护理记录数据" />
							</div>
							<div v-if="!conditionRecord.customerId" class="empty-data">
								<el-empty description="请选择客户查看护理记录" />
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
		listKhxxPage
	} from "@/api/customerApi.js";
	import {
		listNurseRecordsVo,
		softDeleteCustomerRecord
	} from "@/api/nurseRecordApi.js";
	import {
		Search
	} from '@element-plus/icons-vue'

	export default {
		components: {
			Search
		},
		computed: {
			// 分页序号不重复-客户
			indexMethod() {
				return (index) => (this.page.currentPage - 1) * this.page.pageSize + index + 1;
			},
			// 分页序号不重复-护理记录
			indexMethodRecord() {
				return (index) => (this.pageRecord.currentPage - 1) * this.pageRecord.pageSize + index + 1;
			}
		},
		data() {
			return {
				tableMaxHeight: 0, // 表格最大高度
				// 分页属性封装--客户
				page: {
					total: 0,
					pageSize: 10,
					currentPage: 1,
					pagCount: 0
				},
				// 分页属性封装--护理记录
				pageRecord: {
					total: 0,
					pageSize: 10,
					currentPage: 1,
					pagCount: 0
				},
				nurseRecordList: [],
				khxxList: [],
				// 查询条件封装--客户
				condition: {
					customerName: "",
					pageNum: 1 // 服务端接收的页码参数名
				},
				// 查询条件封装--护理记录
				conditionRecord: {
					customerId: "", // 客户编号
					pageNum: 1 // 服务端接收的页码参数名
				}
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
				// 清空护理记录数据表
				this.nurseRecordList = [];
				this.conditionRecord.customerId = "";
				this.condition.pageNum = 1; // 回到第一页
				this.listKhxxPage();
			},

			// 选中页码-客户
			handleCurrentChange(curPage) {
				this.page.currentPage = curPage;
				this.condition.pageNum = curPage; // 参数pageNum是服务端接收页码参数名
				// 重新渲染表格
				this.listKhxxPage();
				// 清空护理记录数据表
				this.nurseRecordList = [];
				this.conditionRecord.customerId = "";
			},

			// 选中某客户行：获取用户的服务项目列表
			handleChangeCustomer(row) {
				if (row) {
					// 构建查询条件
					this.conditionRecord.customerId = row.id;
					this.conditionRecord.pageNum = 1; // 重置护理记录页码
					this.pageRecord.currentPage = 1;
					this.listNurseRecordsVo();
				}
			},

			// 选中页码-护理记录
			handleRecordChange(curPage) {
				this.pageRecord.currentPage = curPage;
				this.conditionRecord.pageNum = curPage; // 参数pageNum是服务端接收页码参数名
				// 重新渲染表格
				this.listNurseRecordsVo();
			},

			// api-查询客户信息列表-分页
			listKhxxPage() {
				listKhxxPage(this.condition).then(res => {
					if (res.flag) {
						this.khxxList = res.data?.records || [];
						this.page.total = res.data?.total || 0;
						this.page.pageSize = res.data?.size || this.page.pageSize;
						this.page.currentPage = res.data?.current || this.page.currentPage;
						this.page.pagCount = res.data?.pages || 0;
					} else {
						this.$message.error(res.message || "查询客户信息失败");
						this.khxxList = [];
					}
				}).catch(error => {
					console.error('查询客户信息失败:', error);
					this.$message.error('查询失败，请检查网络连接');
					this.khxxList = [];
				});
			},

			// api-查询客户护理记录
			listNurseRecordsVo() {
				listNurseRecordsVo(this.conditionRecord).then(res => {
					if (res.flag) {
						this.nurseRecordList = res.data?.records || [];
						this.pageRecord.total = res.data?.total || 0;
						this.pageRecord.pageSize = res.data?.size || this.pageRecord.pageSize;
						this.pageRecord.currentPage = res.data?.current || this.pageRecord.currentPage;
						this.pageRecord.pagCount = res.data?.pages || 0;
					} else {
						this.$message.error(res.message || "查询护理记录失败");
						this.nurseRecordList = [];
					}
				}).catch(error => {
					console.error('查询护理记录失败:', error);
					this.$message.error('查询失败，请检查网络连接');
					this.nurseRecordList = [];
				});
			},

			// 删除护理记录
			del(id) {
				this.$confirm("确定删除该护理记录?", "提示", {
						confirmButtonText: "确定",
						cancelButtonText: "取消",
						type: "warning"
					})
					.then(() => {
						softDeleteCustomerRecord({
							id: id
						}).then(res => {
							if (res.flag) {
								this.$message.success(res.message);
								// 重载表格
								this.listNurseRecordsVo();
							} else {
								this.$message.error(res.message || "删除失败");
							}
						}).catch(error => {
							console.error('删除护理记录失败:', error);
							this.$message.error('删除失败，请检查网络连接');
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
		margin: 0 auto;
		border-radius: 0;
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