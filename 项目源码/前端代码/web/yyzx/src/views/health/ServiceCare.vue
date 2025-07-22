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
									<el-input placeholder="请输入客户姓名" v-model="condition.customerName" @clear="query"
										clearable size="large" class="full-width">
									</el-input>
								</el-form-item>
							</el-col>
							<!-- 查询按钮 -->
							<el-col :span="3" class="btn-col">
								<el-form-item>
									<el-button type="primary" @click="query" color="#337ab7" icon="Search"
										class="operation-btn">
										<span>查询</span>
									</el-button>
								</el-form-item>
							</el-col>
							<!-- 购买护理服务按钮 -->
							<el-col :span="4" class="btn-col">
								<el-form-item>
									<el-button type="primary" @click="buyService" color="#337ab7" class="operation-btn">
										<el-icon size="20">
											<ShoppingCart />
										</el-icon>
										<span>购买护理服务</span>
									</el-button>
								</el-form-item>
							</el-col>
						</el-row>
					</el-form>
				</div>
			</el-header>
			<el-divider style="margin:0"></el-divider>
			<el-main>
				<el-row :gutter="20">
					<el-col :span="10">
						<div class="card-container">
							<div class="card-header">
								<span class="card-title">客户信息</span>
							</div>
							<!-- 表格 -->
							<el-table :data="khxxList" highlight-current-row @current-change="handleChangeCustomer"
								size="small" style="width: 100% ;color:black;" stripe>
								<el-table-column align="center" fixed type="index" :index="indexMethod" label="序号"
									width="40" />
								<el-table-column align="center" prop="customerName" label="姓名" />
								<el-table-column align="center" prop="customerAge" label="年龄" />
								<el-table-column align="center" prop="customerSex" label="性别">
									<template #default="scope">{{ scope.row.customerSex === 0 ? '男' : '女' }}</template>
								</el-table-column>
								<el-table-column align="center" prop="bedNo" label="床号" />
								<el-table-column align="center" prop="levelName" label="护理级别" />
							</el-table>
							<!-- 分页插件 -->
							<div style="margin-top:15px">
								<el-pagination :page-size="page.pageSize" background :current-page="page.currentPag"
									layout="prev, pager, next" :total="page.total"
									@current-change="handleCurrentChange" />
							</div>
						</div>
					</el-col>
					<el-col :span="14">
						<div class="card-container">
							<div class="card-header">
								<span class="card-title">已购护理服务</span>
							</div>
							<!-- 表格 -->
							<el-table :data="customerItemList" size="small" style="width: 100% ;color:black;" stripe>
								<el-table-column align="center" :index="indexMethodItem" type="index" label="序号"
									width="40" />
								<el-table-column align="center" prop="customerName" label="客户" />
								<el-table-column align="center" prop="serialNumber" label="编号" />
								<el-table-column align="center" prop="nursingName" label="名称" />
								<el-table-column align="center" prop="servicePrice" label="价格" />
								<el-table-column align="center" prop="nurseNumber" label="余量" />
								<el-table-column align="center" prop="maturityTime" label="服务到期日期" />
								<el-table-column align="center" prop label="状态">
									<template #default="scope">
										<el-tag v-if="0 < scope.row.nurseNumber && scope.row.nurseNumber < 10"
											type="warning" disable-transitions>即将用完</el-tag>
										<el-tag v-if="10 < scope.row.nurseNumber" type="success"
											disable-transitions>数量正常</el-tag>
										<el-tag v-if="scope.row.nurseNumber < 0" type="danger"
											disable-transitions>已欠费</el-tag>
										<el-tag v-if="(new Date(scope.row.maturityTime) - new Date()) / 86400000 > 10"
											type="success" disable-transitions>未到期</el-tag>
										<el-tag
											v-if="(new Date(scope.row.maturityTime) - new Date()) / 86400000 > 0 && (new Date(scope.row.maturityTime) - new Date()) / 86400000 < 10"
											type="warning" disable-transitions>即将到期</el-tag>
										<el-tag v-if="(new Date(scope.row.maturityTime) - new Date()) / 86400000 < 0"
											type="danger" disable-transitions>已到期</el-tag>
									</template>
								</el-table-column>
								<el-table-column align="center" label="操作" width="160">
									<template #default="scope">
										<div class="operation-group">
											<el-button type="primary" icon="Edit" link
												style="color: #337ab7; border-color: #337ab7; padding: 0 5px;"
												@click="enew(scope.row)">续费</el-button>
											<el-button type="danger" icon="Delete" link
												style="color: #C12E2A; border-color: #F56C6C; padding: 0 5px;"
												@click="del(scope.row.id)">移除</el-button>
										</div>
									</template>
								</el-table-column>
							</el-table>
							<!-- 分页插件 -->
							<div style="margin-top:15px" v-if="customerItemList.length > 0">
								<el-pagination :page-size="pageItem.pageSize" background
									:current-page="pageItem.currentPag" layout="prev, pager, next"
									:total="pageItem.total" @current-change="handleItemChange" />
							</div>
						</div>
					</el-col>
				</el-row>
			</el-main>
		</el-container>
		<!-- 修改/添加对话框 -->
		<el-dialog v-model="dialog.dialogVisible" :title="dialog.tops" width="40%" align-center draggable
			:before-close="handleClose">
			<el-divider border-style="double" style="margin:0;" />
			<el-form label-position="right" label-width="auto" style="max-width:380px;margin:20px auto"
				class="demo-form-inline" ref="customerItemForm" :model="dialog.customerNurseItem" :rules="dialog.rules">
				<el-form-item label="客户姓名：" prop="customerName">
					<el-input readonly v-model="dialog.customerNurseItem.customerName" />
				</el-form-item>
				<el-form-item label="护理项目编号：" prop="serialNumber">
					<el-input readonly v-model="dialog.customerNurseItem.serialNumber" />
				</el-form-item>
				<el-form-item label="护理项目名称" prop="nursingName">
					<el-input readonly v-model="dialog.customerNurseItem.nursingName" />
				</el-form-item>
				<el-form-item label="价格：" prop="servicePrice">
					<el-input readonly v-model="dialog.customerNurseItem.servicePrice" />
				</el-form-item>
				<el-form-item label="原有数量：" prop="oldNumber">
					<el-input readonly v-model="dialog.customerNurseItem.oldNumber" />
				</el-form-item>
				<el-form-item label="新增数量：" prop="addNumber">
					<el-input v-model="dialog.customerNurseItem.addNumber" />
				</el-form-item>
				<el-form-item label="总数量：" prop="nurseNumber">
					<el-input readonly v-model="nurseNumber" />
				</el-form-item>
				<el-form-item label="服务到期日期：" prop="maturityTime">
					<el-date-picker style="width:300px" format="YYYY/MM/DD" value-format="YYYY-MM-DD"
						v-model="dialog.customerNurseItem.maturityTime" type="date" placeholder="选择日期" />
				</el-form-item>
			</el-form>
			<el-divider border-style="double" style="margin:0;" />
			<template #footer>
				<span class="dialog-footer" style="padding-top: 0px">
					<el-button type="primary" @click="save('customerItemForm')" class="operation-btn">保存</el-button>
					<el-button @click="cancel" class="operation-btn">取消</el-button>
				</span>
			</template>
		</el-dialog>
	</div>
</template>

<script>
	import {
		listKhxxPage
	} from "@/api/customerApi.js";
	import {
		listCustomerItem,
		updateNurseItem,
		removeCustomerItem
	} from "@/api/customerNurseItemApi.js";
	import {
		ShoppingCart,
		Search,
		Edit,
		Delete
	} from '@element-plus/icons-vue'

	export default {
		components: {
			ShoppingCart,
			Search,
			Edit,
			Delete
		},
		computed: {
			// 客户表格分页序号计算
			indexMethod() {
				return (this.page.currentPag - 1) * this.page.pageSize + 1;
			},
			// 护理项目表格分页序号计算
			indexMethodItem() {
				return (this.pageItem.currentPag - 1) * this.pageItem.pageSize + 1;
			},
			// 计算总数量
			nurseNumber() {
				if (
					this.dialog.customerNurseItem.addNumber === null ||
					this.dialog.customerNurseItem.addNumber === "" ||
					this.dialog.customerNurseItem.addNumber === "undifine"
				) {
					return this.dialog.customerNurseItem.oldNumber;
				}
				return (
					parseInt(this.dialog.customerNurseItem.oldNumber) +
					parseInt(this.dialog.customerNurseItem.addNumber)
				);
			}
		},
		data() {
			return {
				// 护理项目分页配置
				pageItem: {
					total: 0,
					pageSize: 10,
					currentPag: 1,
					pagCount: 0
				},
				// 护理项目查询条件
				conditionItem: {
					customerId: "",
					pageSize: 1
				},
				// 客户信息分页配置
				page: {
					total: 0,
					pageSize: 10,
					currentPag: 1,
					pagCount: 0
				},
				customerItemList: [],
				khxxList: [],
				// 客户信息查询条件
				condition: {
					customerName: "",
					pageSize: 1,
					pageNum: 1
				},
				// 续费模态框数据
				dialog: {
					dialogVisible: false,
					tops: "护理项目续费",
					rules: {
						maturityTime: [{
							required: true,
							message: "请选择日期",
							trigger: "change"
						}]
					},
					customerNurseItem: {
						id: "",
						customerName: "",
						maturityTime: "",
						oldNumber: 0,
						newNumber: 0,
						nursingName: "",
						serialNumber: "",
						servicePrice: "",
						addNumber: ""
					}
				}
			};
		},
		methods: {
			// 点击查询
			query() {
				// 重置页码为第一页
				this.condition.pageSize = 1;
				this.condition.pageNum = 1;
				this.page.currentPag = 1;

				// 清空护理项目数据
				this.conditionItem.customerId = "";
				this.customerItemList = [];

				// 清空客户数据并重新加载
				this.khxxList = [];
				this.listKhxxPage();
			},
			// 客户信息分页切换
			handleCurrentChange(curPage) {
				// 确保页码为数字类型
				curPage = parseInt(curPage);

				this.page.currentPag = curPage;
				this.condition.pageSize = curPage;
				this.condition.pageNum = curPage;

				// 清空原有数据并加载新页数据
				this.khxxList = [];
				this.listKhxxPage();

				// 清空护理项目数据
				this.conditionItem.customerId = "";
				this.customerItemList = [];
			},
			// 护理项目分页切换
			handleItemChange(curPage) {
				// 确保页码为数字类型
				curPage = parseInt(curPage);

				this.pageItem.currentPag = curPage;
				this.conditionItem.pageSize = curPage;

				// 清空原有数据并加载新页数据
				this.customerItemList = [];
				this.listCustomerItem();
			},
			// 选中客户行
			handleChangeCustomer(row) {
				if (row !== null) {
					// 构建查询条件
					this.conditionItem.customerId = row.id;
					this.conditionItem.pageSize = 1;
					this.pageItem.currentPag = 1;

					// 清空护理项目数据并重新加载
					this.customerItemList = [];
					this.listCustomerItem();
				}
			},
			// 关闭模态框
			handleClose() {
				this.dialog.dialogVisible = false;
				this.resetForm("customerItemForm");
			},
			// 重置表单
			resetForm(formName) {
				if (this.$refs[formName]) {
					this.$refs[formName].resetFields();
				}
			},
			cancel() {
				this.handleClose();
			},
			// 点击续费
			enew(row) {
				this.dialog.dialogVisible = true;
				// 初始化模态框数据
				this.$nextTick(() => {
					this.dialog.customerNurseItem.id = row.id;
					this.dialog.customerNurseItem.customerName = row.customerName;
					this.dialog.customerNurseItem.serialNumber = row.serialNumber;
					this.dialog.customerNurseItem.oldNumber = row.nurseNumber;
					this.dialog.customerNurseItem.nursingName = row.nursingName;
					this.dialog.customerNurseItem.servicePrice = row.servicePrice;
					this.dialog.customerNurseItem.maturityTime = row.maturityTime;
				});
			},
			// 点击购买护理服务
			buyService() {
				// 判断是否选中客户
				if (
					this.conditionItem.customerId !== null &&
					this.conditionItem.customerId !== ""
				) {
					this.$router.push({
						path: "/health/serviceBuy",
						query: {
							customerId: this.conditionItem.customerId
						}
					});
				} else {
					this.$message.warning("请选中表格中的客户");
				}
			},
			// 查询客户信息列表
			listKhxxPage() {
				// 构建查询参数
				const params = {
					customerName: this.condition.customerName,
					pageSize: this.page.pageSize,
					pageNum: this.condition.pageNum
				};

				listKhxxPage(params).then(res => {
					this.khxxList = res.data.records;
					this.page.total = res.data.total;
					this.page.pageSize = res.data.size;
					this.page.currentPag = res.data.current;
					this.page.pagCount = res.data.pages;
				}).catch(error => {
					console.error('获取客户信息失败:', error);
					this.$message.error('获取客户信息失败，请检查网络连接');
				});
			},
			// 查询客户护理项目列表
			listCustomerItem() {
				// 构建查询参数
				const params = {
					customerId: this.conditionItem.customerId,
					pageSize: this.conditionItem.pageSize,
					pageNum: this.conditionItem.pageSize
				};

				listCustomerItem(params).then(res => {
					this.customerItemList = res.data.records;
					this.pageItem.total = res.data.total;
					this.pageItem.pageSize = res.data.size;
					this.pageItem.currentPag = res.data.current;
					this.pageItem.pagCount = res.data.pages;
				}).catch(error => {
					console.error('获取护理项目失败:', error);
					this.$message.error('获取护理项目失败，请检查网络连接');
				});
			},
			// 移除客户护理项目
			del(id) {
				this.$confirm("此操作将移除当前护理项目, 请谨慎操作?", "提示", {
						confirmButtonText: "确定",
						cancelButtonText: "取消",
						type: "warning"
					})
					.then(() => {
						removeCustomerItem({
								id
							})
							.then(res => {
								if (res.flag) {
									this.$message({
										type: "success",
										message: res.message
									});
									// 重载表格
									this.listCustomerItem();
								}
							}).catch(error => {
								console.error('删除护理项目失败:', error);
								this.$message.error('删除失败，请重试');
							});
					})
					.catch(() => {});
			},
			// 保存续费信息
			save(formName) {
				this.$refs[formName].validate(valid => {
					if (valid) {
						const params = {
							id: this.dialog.customerNurseItem.id,
							maturityTime: this.dialog.customerNurseItem.maturityTime,
							nurseNumber: this.nurseNumber
						};

						updateNurseItem(params).then(res => {
							if (res.flag) {
								this.$message.success(res.message);
								// 刷新数据表格
								this.listCustomerItem();
								this.handleClose();
							} else {
								this.$message.error(res.message);
							}
						}).catch(error => {
							console.error('更新护理项目失败:', error);
							this.$message.error('更新失败，请重试');
						});
					} else {
						return false;
					}
				});
			}
		},
		mounted() {
			// 初始化时清空数据并加载第一页
			this.khxxList = [];
			this.listKhxxPage();
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

	/* 卡片容器样式 */
	.card-container {
		background-color: #fff;
		border-radius: 8px;
		box-shadow: 0 1px 3px rgba(0, 0, 0, 0.12), 0 1px 2px rgba(0, 0, 0, 0.24);
		margin-bottom: 20px;
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

	/* 按钮样式 */
	.operation-btn {
		padding: 8px 16px;
		font-size: 13px;
		height: 40px;
		min-width: 90px;
		margin: 0 !important;
		border-radius: 4px;
		transition: all 0.3s ease;
		font-weight: 500;
	}

	/* 主体内容样式 */
	.el-main {
		padding: 15px;
		flex: 1;
		overflow: auto;
		background-color: #f5f7fa;
	}

	.el-button {
		border-radius: 4px;
		transition: all 0.3s ease;
		font-weight: 500;
	}

	.el-button[type="primary"][icon="Search"] {
		color: #0050B3;
		border-color: #409EFF;
		background-color: #E6F7FF;
	}

	.el-button[type="primary"][icon="Search"]:hover {
		color: #0039A6;
		border-color: #3A8EE6;
		background-color: #BAE1FF;
		transform: translateY(-2px);
		box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
	}

	.el-button--primary,
	.el-button--danger,
	.el-button[type="primary"] {
		padding: 8px 16px;
		font-size: 13px;
		height: 40px;
	}

	.el-button:hover {
		transform: translateY(-2px);
		box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
	}

	/* 表格样式 */
	.el-table {
		width: 100%;
		margin-top: 15px;
		border-radius: 4px;
		overflow: hidden;
		box-shadow: 0 1px 3px rgba(0, 0, 0, 0.12), 0 1px 2px rgba(0, 0, 0, 0.24);
	}

	.el-table th {
		background-color: #f5f7fa;
		color: #606266;
		font-weight: 500;
	}

	.el-table__row:hover>td {
		background-color: #ecf5ff !important;
	}

	/* 分页样式 */
	.el-pagination {
		margin-top: 15px;
		text-align: right;
	}

	/* 模态框样式 */
	.el-dialog {
		border-radius: 8px;
		box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
	}

	.demo-form-inline {
		padding: 10px 0;
	}

	.el-form-item {
		margin-bottom: 15px;
	}

	.el-form-item__label {
		font-weight: 500;
	}

	/* 主按钮样式 */
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

	/* 危险按钮样式 */
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

	/* 操作按钮组样式 - 确保按钮在同一行并平行对齐 */
	.operation-group {
		display: flex;
		align-items: center;
		justify-content: center;
		gap: 10px;
		/* 按钮之间的间距 */
		width: 100%;
		height: 100%;
	}

	.operation-group .el-button {
		padding: 0 8px;
		height: auto;
		line-height: normal;
		vertical-align: middle;
	}

	/* 响应式样式（屏幕宽度小于768px时） */
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

		.el-dialog {
			width: 90% !important;
		}

		.operation-btn,
		.el-button--primary,
		.el-button--danger,
		.el-button[type="primary"][icon="Search"] {
			padding: 6px 12px;
			font-size: 12px;
			height: 36px;
		}
	}
</style>