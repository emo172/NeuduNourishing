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
									<el-input placeholder="请输入客户姓名" v-model="condition.customerName" clearable
										@clear="query" size="large" class="full-width">
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
							<!-- 添加退住申请按钮 -->
							<el-col :span="3" class="btn-col" v-if="roleId === 2">
								<el-form-item>
									<el-button type="primary" @click="addItem" color="#337ab7" class="operation-btn">
										<el-icon>
											<Plus />
										</el-icon>
										<span>添加退住申请</span>
									</el-button>
								</el-form-item>
							</el-col>
						</el-row>
					</el-form>
				</div>
			</el-header>

			<el-divider style="margin:0"></el-divider>

			<!-- 页面主体：包含客户信息和退住登记表格 -->
			<el-main>
				<el-row :gutter="20">
					<el-col :span="9">
						<div class="card-container">
							<div class="card-header">
								<span class="card-title">客户信息</span>
							</div>
							<!-- 客户信息表格 -->
							<el-table :data="khxxList" highlight-current-row @current-change="handleChangeCustomer"
								size="small" style="width: 100%; color:black;" stripe>
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
							<!-- 分页组件 -->
							<div style="margin-top:15px">
								<el-pagination :page-size="page.pageSize" background :current-page="page.currentPage"
									layout=" prev, pager, next" :total="page.total"
									@current-change="handleCurrentChange" />
							</div>
						</div>
					</el-col>
					<el-col :span="15">
						<div class="card-container">
							<div class="card-header">
								<span class="card-title">退住登记</span>
							</div>
							<!-- 退住登记表格 -->
							<el-table :data="backdownList" size="small" style="width: 100%; color:black;" stripe>
								<el-table-column align="center" :index="indexMethodRecord" type="index" label="序号"
									width="40" />
								<el-table-column align="center" prop="customerName" label="客户名称" width="100" />
								<el-table-column align="center" prop="checkinDate" label="入住时间" width="100" />
								<el-table-column align="center" prop="retreattime" label="退住时间" width="70" />
								<el-table-column align="center" prop="retreattype" label="退住类型" width="100" />
								<el-table-column align="center" prop="retreatreason" label="退住原因" width="100" />
								<el-table-column align="center" prop="audittime" label="审批时间" width="100" />
								<el-table-column align="center" prop="auditstatus" label="审批状态" width="100">
									<template #default="{row}">
										{{ row.auditstatus === 0 ? '已提交' : row.auditstatus === 1 ? '同意' : '拒绝' }}
									</template>
								</el-table-column>
								<el-table-column align="center" prop="bedId" label="床位" width="100" />
								<el-table-column align="center" label="操作" width="100">
									<template #default="scope">
										<el-button v-if="scope.row.auditstatus === 0 && dialog.item.roleId === 2"
											type="danger" size="small" round plain
											@click="del(scope.row.id)">撤销申请</el-button>
										<el-button v-if="scope.row.auditstatus === 0 && dialog.item.roleId !== 2"
											type="primary" size="small" round plain
											@click="examine(scope.row.id)">审批</el-button>
									</template>
								</el-table-column>
							</el-table>
							<!-- 分页组件 -->
							<div style="margin-top:15px" v-if="backdownList.length > 0">
								<el-pagination :page-size="pageRecord.pageSize" background
									:current-page="pageRecord.currentPage" layout=" prev, pager, next"
									:total="pageRecord.total" @current-change="handleRecordChange" />
							</div>
						</div>
					</el-col>
				</el-row>
			</el-main>
		</el-container>

		<!-- 审批对话框 -->
		<el-dialog v-model="dialog.dialogExamineVisible" :title="dialog.tops" width="40%" align-center draggable
			before-close="handleExamineClose">
			<el-divider border-style="double" style="margin:0;" />
			<el-form label-position="right" label-width="auto" style="max-width:380px;margin:20px auto"
				class="demo-form-inline" ref="itemExamineForm" :model="dialog.item" :rules="rules">

				<el-form-item label="审批：" prop="auditstatus">
					<el-radio-group v-model="dialog.item.auditstatus" class="ml-4">
						<el-radio label="1">同意</el-radio>
						<el-radio label="2">拒绝</el-radio>
					</el-radio-group>
				</el-form-item>

			</el-form>
			<el-divider border-style="double" style="margin:0;" />
			<template #footer>
				<span class="dialog-footer" style="padding-top: 0px">
					<el-button type="primary" @click="examineBackdown('itemExamineForm')"
						class="operation-btn">保存</el-button>
					<el-button @click="cancelExamine" class="operation-btn">取消</el-button>
				</span>
			</template>
		</el-dialog>

		<!-- 添加退住申请对话框 -->
		<el-dialog v-model="dialog.dialogVisible" :title="dialog.tops" width="40%" align-center draggable
			before-close="handleClose">
			<el-divider border-style="double" style="margin:0;" />
			<el-form label-position="right" label-width="auto" style="max-width:380px;margin:20px auto"
				class="demo-form-inline" ref="itemForm" :model="dialog.item" :rules="rules">
				<el-form-item label="客户姓名：" prop="customerId">
					<el-select v-model="dialog.item.customerId" style="width:320px" placeholder="客户姓名：">
						<el-option v-for="item in customerList" :label="item.customerName" :value="item.id" />
					</el-select>
				</el-form-item>
				<el-form-item label="退住时间：" prop="retreattime">
					<el-date-picker style="width:320px" v-model="dialog.item.retreattime" type="date"
						placeholder="退住时间:" value-format="YYYY-MM-DD" />
				</el-form-item>
				<el-form-item label="退住类型：" prop="retreattype">
					<el-select v-model="dialog.item.retreattype" style="width:320px" placeholder="退住类型：">
						<el-option label="正常退住" value="0" />
						<el-option label="死亡退住" value="1" />
						<el-option label="保留床位" value="2" />
					</el-select>
				</el-form-item>
				<el-form-item label="退住原因： " prop="retreatreason">
					<el-input v-model="dialog.item.retreatreason" />
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
		listKhxxPage
	} from "@/api/customerApi.js";
	import {
		addBackdown,
		examineBackdown,
		delBackdown,
		listBackdown
	} from "@/api/backdownApi.js";
	import {
		getSessionStorage,
		getCurDate
	} from '@/utils/common.js'
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
			// 分页序号计算 - 客户
			indexMethod() {
				return (this.page.currentPage - 1) * this.page.pageSize + 1;
			},
			// 分页序号计算 - 退住记录
			indexMethodRecord() {
				return (this.pageRecord.currentPage - 1) * this.pageRecord.pageSize + 1;
			},
		},
		data() {
			return {
				// 模态框数据
				dialog: {
					dialogVisible: false, // 模态框状态
					dialogTimeVisible: false, // 模态框状态
					dialogExamineVisible: false,
					tops: "", // 模态框标题
					item: {
						id: "",
						username: "",
						customerName: "",
						checkinDate: "",
						retreattime: "",
						retreattype: "",
						retreatreason: "",
						audittime: "",
						auditstatus: 0,
						bedId: "",
						roleId: ""
					},
					statusArr: [{
							value: 1,
							label: "停用"
						},
						{
							value: 2,
							label: "启用"
						}
					]
				},
				roleId: '',
				// 分页属性封装 -- 客户
				page: {
					total: 0,
					pageSize: 10,
					currentPage: 1,
					pagCount: 0
				},
				// 分页属性封装 -- 退住记录
				pageRecord: {
					total: 0,
					pageSize: 10,
					currentPage: 1,
					pagCount: 0
				},
				backdownList: [],
				khxxList: [],
				customerList: [],
				// 查询条件封装 -- 客户
				condition: {
					customerName: "",
					userId: "",
					pageNum: 1, // 当前页码
					pageSize: 10 // 每页显示条数
				},
				// 查询条件封装 -- 退住记录
				conditionRecord: {
					userId: "",
					customerId: "", // 添加客户ID查询条件
					pageNum: 1, // 当前页码
					pageSize: 10 // 每页显示条数
				},
				// 表单校验规则
				rules: {
					auditstatus: [{
						required: true,
						message: "请选择审批状态",
						trigger: "change"
					}],
					customerId: [{
						required: true,
						message: "请选择客户",
						trigger: "change"
					}],
					retreattime: [{
						required: true,
						message: "请选择退住时间",
						trigger: "change"
					}],
					retreattype: [{
						required: true,
						message: "请选择退住类型",
						trigger: "change"
					}],
					retreatreason: [{
						required: true,
						message: "请输入退住原因",
						trigger: "blur"
					}]
				}
			};
		},
		methods: {
			// 点击查询
			query() {
				// 清空退住记录表
				this.backdownList = []

				// 重置到第一页
				this.condition.pageNum = 1;
				this.conditionRecord.pageNum = 1;
				this.conditionRecord.customerId = ""; // 清空客户ID

				this.listKhxxPage();
				this.listBackdown();
			},
			// 选中页码 - 客户
			handleCurrentChange(curPage) {
				this.page.currentPage = curPage;
				this.condition.pageNum = curPage; // 设置当前页码
				// 重新渲染表格
				this.listKhxxPage();
				// 清空退住记录表
				this.backdownList = []
				this.conditionRecord.customerId = "";
			},

			// 选中某客户行:获取用户的退住记录列表
			handleChangeCustomer(row) {
				if (row) {
					console.log('选中客户:', row); // 添加日志以便调试
					// 构建查询条件
					this.conditionRecord.customerId = row.id;
					this.conditionRecord.pageNum = 1; // 重置到第一页
					this.listBackdown();
				} else {
					// 如果没有选中行，清空退住记录
					this.backdownList = [];
					this.conditionRecord.customerId = "";
				}
			},
			// 选中页码 - 退住记录
			handleRecordChange(curPage) {
				this.pageRecord.currentPage = curPage;
				this.conditionRecord.pageNum = curPage; // 设置当前页码
				// 重新渲染表格:
				this.listBackdown();
			},

			// api-查询客户信息列表-分页
			listKhxxPage() {
				listKhxxPage(this.condition).then(res => {
					this.khxxList = res.data.records;
					this.customerList = res.data.records;
					this.page.total = res.data.total; // 总记录数
					this.page.pageSize = this.condition.pageSize; // 保持设置的pageSize
					this.page.currentPage = res.data.current; // 当前页码
					this.page.pagCount = res.data.pages; // 总页数
				});
			},
			// api-查询退住申请记录
			listBackdown() {
				console.log('查询退住记录参数:', this.conditionRecord); // 添加日志以便调试
				listBackdown(this.conditionRecord).then(res => {
					this.backdownList = res.data.records;
					this.pageRecord.total = res.data.total; // 总记录数
					this.pageRecord.pageSize = this.conditionRecord.pageSize; // 保持设置的pageSize
					this.pageRecord.currentPage = res.data.current; // 当前页码
					this.pageRecord.pagCount = res.data.pages; // 总页数
				}).catch(error => {
					console.error('获取退住记录失败:', error);
					this.$message.error('获取退住记录失败，请检查网络连接');
				});
			},
			// 审批退住申请
			examine(id) {
				this.dialog.tops = "审批";
				this.dialog.dialogExamineVisible = true;
				this.dialog.item.id = id;
			},
			// 关闭审批对话框
			handleExamineClose() {
				this.dialog.dialogExamineVisible = false;
				this.resetForm("itemExamineForm"); // 重置表单
			},
			// 取消审批
			cancelExamine() {
				this.handleExamineClose();
			},

			// 点击添加按钮
			addItem() {
				this.dialog.tops = "添加退住申请";
				this.dialog.dialogVisible = true;
			},
			// 关闭添加对话框
			handleClose() {
				this.dialog.dialogVisible = false;
				this.resetForm("itemForm"); // 重置表单
			},
			// 取消添加
			cancel() {
				this.handleClose();
			},
			// 重置表单
			resetForm(formName) {
				if (this.$refs[formName]) {
					this.$refs[formName].resetFields();
				}
			},
			// 保存审批结果
			examineBackdown(formName) {
				this.$refs[formName].validate(valid => {
					if (valid) {
						this.dialog.item.audittime = getCurDate();
						this.dialog.item.auditperson = getSessionStorage('user').id;
						examineBackdown(this.dialog.item).then(res => {
							if (res.flag) {
								this.$message.success(res.message);
								// 刷新数据表格(回到最初查询状态)
								this.listBackdown();
								this.handleExamineClose(); // 关闭模态框
							} else {
								this.$message.error(res.message);
							}
						});
					} else {
						return false;
					}
				});
			},

			// 保存(新增)退住申请
			save(formName) {
				this.$refs[formName].validate(valid => {
					if (valid) {
						// 通过id判断是添加还是编辑
						if (this.dialog.item.id == null || this.dialog.item.id == "") {
							this.dialog.item.createTime = getCurDate();
							this.dialog.item.createBy = getSessionStorage("user").id;
							this.dialog.item.password = "000000";
							this.dialog.item.roleId = 2;
							this.dialog.item.isDeleted = 0;
							addBackdown(this.dialog.item).then(res => {
								if (res.flag) {
									this.$message.success(res.message);
									// 刷新数据表格(回到最初查询状态)
									this.listBackdown();
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
			// 删除退住申请
			del(id) {
				this.$confirm("确定删除?", "提示", {
						confirmButtonText: "确定",
						cancelButtonText: "取消",
						type: "warning"
					})
					.then(() => {
						delBackdown({
							id: id
						}).then(res => {
							if (res.flag) {
								this.$message({
									type: "success",
									message: res.message
								});
								// 重载表格
								this.listBackdown();
							}
						});
					})
					.catch(() => {});
			},
		},
		mounted() {
			this.roleId = getSessionStorage('user').roleId;
			this.condition.userId = this.roleId === 1 ? '' : getSessionStorage('user').id;
			this.conditionRecord.userId = this.roleId === 1 ? '' : getSessionStorage('user').id;
			this.listKhxxPage();
			// 初始状态不加载退住记录，等用户选择客户后再加载
			// this.listBackdown();
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
		.filter-btn,
		.el-button--primary,
		.el-button--danger,
		.el-button[type="primary"][icon="Search"] {
			padding: 6px 12px;
			font-size: 12px;
			height: 36px;
		}
	}
</style>