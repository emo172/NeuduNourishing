<template>
	<div class="common-layout">
		<el-container>
			<!-- 页面头部：包含搜索框和操作按钮 -->
			<el-header>
				<div class="header-container">
					<el-form :inline="true" class="search-form">
						<el-row :gutter="15" class="search-row">
							<!-- 客户姓名搜索框 -->
							<el-col :span="6" class="input-col">
								<el-form-item label="客户姓名: ">
									<el-input placeholder="请输入客户姓名" v-model="queryParams.customerName" clearable
										@clear="query" size="large" class="full-width">
									</el-input>
								</el-form-item>
							</el-col>
							<!-- 查询按钮 -->
							<el-col :span="3" class="btn-col">
								<el-form-item>
									<el-button type="primary" @click="query" icon="Search" class="operation-btn">
										<span>查询</span>
									</el-button>
								</el-form-item>
							</el-col>
							<!-- 添加按钮 -->
							<el-col :span="3" class="btn-col">
								<el-form-item>
									<el-button type="primary" @click="addItem" class="operation-btn">
										<el-icon>
											<Plus />
										</el-icon>
										<span>添加</span>
									</el-button>
								</el-form-item>
							</el-col>
						</el-row>
					</el-form>
				</div>
			</el-header>
			<el-divider style="margin:0"></el-divider>

			<!-- 页面主体：包含数据表格和分页组件 -->
			<el-main>
				<div>
					<!-- 表格 -->
					<el-table :data="preferenceList" style="width: 100% ;color:black;" stripe>
						<el-table-column align="center" type="index" :index="indexMethod" label="序号" width="60" />
						<el-table-column align="center" prop="customerName" label="客户名称" width="120" />
						<el-table-column align="center" prop="customerSex" label="性别" width="60">
							<template #default="scope">{{ scope.row.customerSex==0?'男':'女'}}</template>
						</el-table-column>
						<el-table-column align="center" prop="customerAge" label="年龄" width="120" />
						<el-table-column align="center" prop="preferences" label="喜好" width="300" />
						<el-table-column align="center" prop="attention" label="注意事项" width="200" />
						<el-table-column align="center" prop="remark" label="备注" width="200" />

						<el-table-column align="center" fixed="right" label="操作" width="220">
							<template #default="scope">
								<el-button type="primary" icon="Edit" link
									style="color: #337ab7; border-color: #337ab7;"
									@click="edit(scope.row)">修改</el-button>
								<el-button type="danger" icon="Delete" link @click="del(scope.row.id)">删除</el-button>
							</template>
						</el-table-column>
					</el-table>
				</div>
				<!-- 分页插件 -->
				<div style="margin-top:15px">
					<el-pagination :page-size="page.pageSize" background :current-page="page.currentPag"
						layout=" prev, pager, next" :total="page.total" @current-change="handleCurrentChange" />
				</div>
			</el-main>
		</el-container>
		<!-- 修改/添加对话框 -->
		<el-dialog v-model="dialog.dialogVisible" :title="dialog.tops" width="40%" align-center draggable
			:before-close="handleClose">
			<el-divider border-style="double" style="margin:0;" />
			<el-form label-position="right" label-width="auto" style="max-width:380px;margin:20px auto"
				class="demo-form-inline" ref="itemForm" :model="dialog.item" :rules="rules">
				<el-form-item label="客户姓名：" prop="customerId">
					<el-select v-model="dialog.item.customerId" style="width:320px" placeholder="请选择客户姓名">
						<!-- 遍历所有客户信息，添加key属性确保唯一性 -->
						<el-option v-for="item in customerList" :key="item.id" :label="item.customerName"
							:value="item.id" />
					</el-select>
				</el-form-item>
				<el-form-item label="喜好：" prop="preferences">
					<el-input v-model="dialog.item.preferences" />
				</el-form-item>
				<el-form-item label="提示：" prop="attention">
					<el-input v-model="dialog.item.attention" />
				</el-form-item>

				<el-form-item label="备注：" prop="remark">
					<el-input v-model="dialog.item.remark" />
				</el-form-item>

			</el-form>
			<el-divider border-style="double" style="margin:0;" />
			<template #footer>
				<span class="dialog-footer" style="padding-top: 0px">
					<el-button type="primary" @click="save('itemForm')" class="operation-btn">保存</el-button>
					<el-button @click="cancel" class="operation-btn">取消</el-button>
				</span>
			</template>
		</el-dialog>
	</div>
</template>

<script>
	import {
		addCustomerpreference,
		listCustomerpreferencePage,
		updateCustomerpreference,
		removeCustomerpreference
	} from "@/api/customerPreferenceApi.js";
	import {
		listKhxxPage
	} from "@/api/customerApi.js";
	import {
		Plus,
		Search
	} from '@element-plus/icons-vue'

	export default {
		// 注册组件
		components: {
			Plus,
			Search
		},
		computed: {
			indexMethod() {
				return this.page.currentPag * this.page.pageSize - this.page.pageSize + 1;
			}
		},
		data() {
			return {
				// 表单验证规则
				rules: {
					customerId: [{
						required: true,
						message: '请选择客户姓名',
						trigger: 'change'
					}],
					preferences: [{
						required: true,
						message: '请输入客户喜好',
						trigger: 'blur'
					}]
				},
				// 模态框数据
				dialog: {
					dialogVisible: false, // 模态框状态
					tops: "", // 模态框标题
					item: {
						id: "",
						customerId: "", // 客户ID（关联客户表）
						customerName: "",
						customerSex: "",
						customerAge: "",
						preferences: "",
						attention: "",
						remark: "",
						isDeleted: 0
					}
				},

				// 分页属性封装
				page: {
					total: 0,
					pageSize: 6,
					currentPag: 1,
					pagCount: 0
				},
				queryParams: {
					customerName: "",
					pageSize: 1 // 默认第一页（数值类型，原字符串类型改为数字）
				},
				preferenceList: [],
				customerList: [], // 存储所有客户信息
				// 查询条件（获取全量客户时不限制分页）
				allCustomerCondition: {
					customerName: "",
					pageSize: 10000, // 设置一个极大值，确保获取所有数据
					currentPage: 1
				},
			};
		},
		mounted() {
			this.loadAllCustomers(); // 加载所有客户信息
			this.getPreferenceList();
		},
		methods: {
			// 加载所有客户信息（核心修改：获取全量数据）
			loadAllCustomers() {
				listKhxxPage(this.allCustomerCondition).then(res => {
					if (res.flag) {
						// 从分页结果中提取所有客户记录
						this.customerList = res.data.records;
					} else {
						this.$message.error('加载客户列表失败：' + res.message);
					}
				}).catch(err => {
					console.error('加载客户列表出错：', err);
					this.$message.error('加载客户列表异常');
				});
			},

			// 点击查询
			query() {
				this.queryParams.currentPage = 1; // 回到第一页
				this.getPreferenceList();
			},

			// 点击修改
			edit(row) {
				this.dialog.tops = "修改膳食管理";
				this.dialog.dialogVisible = true;
				// 初始化模态框数据（关键：绑定客户ID用于下拉框回显）
				this.$nextTick(() => {
					this.dialog.item.id = row.id;
					this.dialog.item.customerId = row.customerId; // 绑定客户ID
					this.dialog.item.customerName = row.customerName;
					this.dialog.item.customerSex = row.customerSex;
					this.dialog.item.customerAge = row.customerAge;
					this.dialog.item.preferences = row.preferences;
					this.dialog.item.attention = row.attention;
					this.dialog.item.remark = row.remark;
				});
			},

			// 点击添加按钮
			addItem() {
				this.dialog.tops = "添加膳食管理";
				this.dialog.dialogVisible = true;
				// 重置表单数据
				this.$nextTick(() => {
					this.resetForm("itemForm");
					this.dialog.item = {
						customerId: "",
						preferences: "",
						attention: "",
						remark: "",
						isDeleted: 0
					};
				});
			},

			handleClose() {
				this.dialog.dialogVisible = false;
				this.resetForm("itemForm"); // 重置表单
			},

			cancel() {
				this.handleClose();
			},

			// 重置表单
			resetForm(formName) {
				if (this.$refs[formName]) {
					this.$refs[formName].resetFields();
				}
			},

			// 保存(新增/编辑)
			save(formName) {
				this.$refs[formName].validate(valid => {
					if (valid) {
						// 通过id判断是添加还是编辑
						if (!this.dialog.item.id) {
							addCustomerpreference(this.dialog.item).then(res => {
								if (res.flag) {
									this.$message.success(res.message);
									// 刷新数据表格
									this.getPreferenceList();
									this.handleClose(); // 关闭模态框
								} else {
									this.$message.error(res.message);
								}
							});
						} else {
							updateCustomerpreference(this.dialog.item).then(res => {
								if (res.flag) {
									this.$message.success(res.message);
									this.getPreferenceList();
									this.handleClose(); // 关闭模态框
								} else {
									this.$message.error(res.message);
								}
							});
						}
					} else {
						return false;
					}
				});
			},

			// 删除
			del(id) {
				this.$confirm("此操作将删除记录，是否继续?", "提示", {
						confirmButtonText: "确定",
						cancelButtonText: "取消",
						type: "warning"
					})
					.then(() => {
						removeCustomerpreference({
							id: id
						}).then(res => {
							if (res.flag) {
								this.$message.success(res.message);
								this.getPreferenceList();
							} else {
								this.$message.error(res.message);
							}
						});
					})
					.catch(() => {});
			},

			// 查询膳食偏好列表
			getPreferenceList() {
				listCustomerpreferencePage(this.queryParams).then(res => {
					this.preferenceList = res.data.records;
					this.page.total = res.data.total;
					this.page.pageSize = res.data.size;
					this.page.currentPag = res.data.current;
					this.page.pagCount = res.data.pages;
				});
			},

			// 分页切换
			handleCurrentChange(val) {
				this.page.currentPag = val;
				this.queryParams.currentPage = val;
				this.getPreferenceList();
			}
		}
	};
</script>

<style scoped>
	/* 样式保持不变 */
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

	.operation-btn {
		padding: 8px 16px;
		font-size: 13px;
		height: 40px;
		min-width: 90px;
		margin: 0 !important;
	}

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

	.el-pagination {
		margin-top: 15px;
		text-align: right;
	}

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
</style>