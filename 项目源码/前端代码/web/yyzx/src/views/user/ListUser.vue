<template>
	<div class="common-layout">
		<el-container>
			<el-header>
				<div class="header-container">
					<el-form :inline="true" class="search-form">
						<el-row :gutter="15" class="search-row">
							<!-- 角色选择下拉框 -->
							<el-col :span="6" class="input-col">
								<el-form-item label="用户身份: ">
									<el-select v-model="queryParams.roleId" placeholder="请选择角色" clearable size="large"
										@clear="query" class="full-width" style="width: 200px;">
										<el-option label="管理员" value="1" />
										<el-option label="健康管家" value="2" />
									</el-select>
								</el-form-item>
							</el-col>
							<!-- 真实姓名输入框 -->
							<el-col :span="6" class="input-col">
								<el-form-item label="用户姓名: ">
									<el-input placeholder="请输入真实姓名" v-model="queryParams.nickname" @clear="query"
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
							<!-- 添加按钮 -->
							<el-col :span="3" class="btn-col">
								<el-form-item>
									<el-button type="primary" @click="addItem" color="#337ab7" class="operation-btn">
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
			<el-main>
				<div>
					<!-- 表格 -->
					<el-table :data="userList" style="width: 100% ;color:black;" stripe>
						<el-table-column align="center" type="index" :index="indexMethod" label="序号" width="60" />
						<el-table-column align="center" prop="id" label="编号" width="120" />
						<el-table-column align="center" prop="createTime" label="创建时间" width="180" />
						<el-table-column align="center" prop="username" label="用户名" width="120" />
						<el-table-column align="center" prop="nickname" label="真实姓名" width="120" />
						<el-table-column align="center" prop="sex" label="性别" width="120">
							<template #default="scope">{{ scope.row.sex==1?'男':'女'}}</template>
						</el-table-column>
						<el-table-column align="center" prop="email" label="邮箱" width="180" />
						<el-table-column align="center" prop="phoneNumber" label="电话号码" width="120" />
						<el-table-column align="center" prop="roleId" label="权限" width="120">
							<template #default="scope">{{ scope.row.roleId==1?'管理员':'健康管家'}}</template>
						</el-table-column>
						<!-- 状态列显示 -->
						<el-table-column align="center" prop="isDeleted" label="状态" width="120">
							<template #default="scope">
								<el-tag :type="scope.row.isDeleted === 0 ? 'success' : 'danger'">
									{{ scope.row.isDeleted === 0 ? '启用' : '停用' }}
								</el-tag>
							</template>
						</el-table-column>
						<el-table-column align="center" fixed="right" label="操作" width="220">
							<template #default="scope">
								<!-- 修改按钮 -->
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
					<el-pagination :page-size="page.pageSize" background :current-page="page.currentPage"
						layout="prev, pager, next" :total="page.total" @current-change="handleCurrentChange"
						@size-change="handleSizeChange" />
				</div>
			</el-main>
		</el-container>
		<!-- 修改/添加对话框 -->
		<el-dialog v-model="dialog.dialogVisible" :title="dialog.tops" width="40%" align-center draggable
			:before-close="handleClose">
			<el-divider border-style="double" style="margin:0;" />
			<el-form label-position="right" label-width="auto" style="max-width:380px;margin:20px auto"
				class="demo-form-inline" ref="itemForm" :model="dialog.item" :rules="rules">
				<el-form-item label="用户名：" prop="username">
					<el-input v-model="dialog.item.username" />
				</el-form-item>
				<el-form-item label="真实姓名：" prop="nickname">
					<el-input v-model="dialog.item.nickname" />
				</el-form-item>
				<el-form-item label="性别：" prop="sex">
					<el-select v-model="dialog.item.sex" style="width:320px" placeholder="请选择性别">
						<el-option label="男" value="1" />
						<el-option label="女" value="2" />
					</el-select>
				</el-form-item>
				<el-form-item label="邮箱：" prop="email">
					<el-input v-model="dialog.item.email" />
				</el-form-item>
				<el-form-item label="电话号码： " prop="phoneNumber">
					<el-input v-model="dialog.item.phoneNumber" />
				</el-form-item>
				<el-form-item label="角色：" prop="roleId">
					<el-select v-model="dialog.item.roleId" style="width:320px" placeholder="请选择角色">
						<el-option label="管理员" value="1" />
						<el-option label="健康管家" value="2" />
					</el-select>
				</el-form-item>
				<el-form-item label="状态：" prop="isDeleted">
					<el-select v-model="dialog.item.isDeleted" style="width:320px" placeholder="请选择状态">
						<el-option v-for="item in dialog.statusArr" :key="item.value" :label="item.label"
							:value="item.value" />
					</el-select>
				</el-form-item>
			</el-form>
			<el-divider border-style="double" style="margin:0;" />
			<template #footer>
				<span class="dialog-footer" style="padding-top: 0px">
					<el-button type="primary" @click="save('itemForm')">保存</el-button>
					<el-button @click="cancel">取消</el-button>
				</span>
			</template>
		</el-dialog>
	</div>
</template>

<script>
	import {
		addUser,
		updateUser,
		delUser,
		findUserPage,
		findAllUserPage
	} from "@/api/userApi.js";
	import {
		getSessionStorage,
		getCurDate
	} from "@/utils/common.js"
	import {
		Plus,
		Search
	} from '@element-plus/icons-vue'

	export default {
		components: {
			Plus,
			Search
		},
		computed: {
			indexMethod() {
				return (this.page.currentPage - 1) * this.page.pageSize + 1;
			}
		},
		data() {
			return {
				// 模态框数据
				dialog: {
					dialogVisible: false,
					tops: "",
					item: {
						id: "",
						username: "",
						nickname: "", // 真实姓名
						sex: "",
						email: "",
						phoneNumber: "",
						isDeleted: 0, // 0：启用；1：停用
						createTime: "",
						createBy: "",
						password: "",
						roleId: 2 // 默认健康管家角色
					},
					statusArr: [{
							value: 0,
							label: "启用"
						},
						{
							value: 1,
							label: "停用"
						}
					]
				},
				// 校验规则
				rules: {
					username: [{
						required: true,
						message: "请输入用户名",
						trigger: "blur"
					}],
					nickname: [{
						required: true,
						message: "请输入真实姓名",
						trigger: "blur"
					}],
					email: [{
						required: true,
						message: "请输入邮箱",
						trigger: "blur"
					}, {
						type: "email",
						message: "请输入正确的邮箱格式",
						trigger: ["blur", "change"]
					}],
					roleId: [{
						required: true,
						message: "请选择角色",
						trigger: "change"
					}],
					isDeleted: [{
						required: true,
						message: "请选择状态",
						trigger: "change"
					}]
				},
				// 分页属性
				page: {
					total: 0,
					pageSize: 10,
					currentPage: 1,
					pageCount: 0
				},
				// 查询参数
				queryParams: {
					nickname: "", // 真实姓名
					roleId: null, // 角色ID（1=管理员，2=健康管家）
					pageNum: 1,
					pageSize: 10
				},
				userList: []
			};
		},
		mounted() {
			// 页面加载时查询所有用户
			this.getAllUserList();
		},
		methods: {
			// 点击查询
			query() {
				this.page.currentPage = 1;
				this.queryParams.pageNum = 1;
				if (this.queryParams.nickname || this.queryParams.roleId !== null) {
					this.getUserByQuery();
				} else {
					this.getAllUserList();
				}
			},
			// 页码改变事件
			handleCurrentChange(curPage) {
				this.page.currentPage = curPage;
				this.queryParams.pageNum = curPage;
				if (this.queryParams.nickname || this.queryParams.roleId !== null) {
					this.getUserByQuery();
				} else {
					this.getAllUserList();
				}
			},
			// 每页条数改变事件
			handleSizeChange(size) {
				this.page.pageSize = size;
				this.queryParams.pageSize = size;
				this.page.currentPage = 1;
				this.queryParams.pageNum = 1;
				if (this.queryParams.nickname || this.queryParams.roleId !== null) {
					this.getUserByQuery();
				} else {
					this.getAllUserList();
				}
			},
			// 点击修改（核心修改：直接使用当前行数据，无需额外查询接口）
			edit(row) {
				// 直接使用当前行数据赋值，避免接口查询异常导致的问题
				this.dialog.tops = "修改用户";
				this.dialog.dialogVisible = true;
				this.$nextTick(() => {
					// 重置表单避免缓存旧数据
					this.$refs.itemForm?.resetFields();
					// 直接复制当前行数据到编辑表单
					this.dialog.item = {
						...row,
						sex: row.sex.toString() // 确保性别为字符串（适配select组件）
					};
				});
			},
			// 点击添加
			addItem() {
				this.dialog.tops = "添加用户";
				this.dialog.dialogVisible = true;
				this.$nextTick(() => {
					if (this.$refs.itemForm) {
						this.$refs.itemForm.resetFields();
					}
					this.dialog.item = {
						id: "",
						username: "",
						nickname: "",
						sex: "",
						email: "",
						phoneNumber: "",
						isDeleted: 0,
						createTime: getCurDate(),
						createBy: getSessionStorage("user").id,
						password: "000000",
						roleId: 2
					};
				});
			},
			handleClose() {
				this.dialog.dialogVisible = false;
				this.resetForm("itemForm");
			},
			cancel() {
				this.handleClose();
			},
			resetForm(formName) {
				if (this.$refs[formName]) {
					this.$refs[formName].resetFields();
				}
			},
			// 保存(新增/编辑)
			save(formName) {
				this.$refs[formName].validate(valid => {
					if (valid) {
						this.dialog.item.sex = Number(this.dialog.item.sex);

						if (!this.dialog.item.id) {
							// 新增
							addUser(this.dialog.item).then(res => {
								if (res.flag) {
									this.$message.success(res.message);
									this.query();
									this.handleClose();
								} else {
									this.$message.error(res.message);
								}
							});
						} else {
							// 编辑
							updateUser(this.dialog.item).then(res => {
								if (res.flag) {
									this.$message.success(res.message);
									this.query();
									this.handleClose();
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
			// 删除用户
			del(id) {
				this.$confirm("此操作将删除该记录, 是否继续?", "提示", {
						confirmButtonText: "确定",
						cancelButtonText: "取消",
						type: "warning"
					})
					.then(() => {
						delUser({
							id
						}).then(res => {
							if (res.flag) {
								this.$message.success(res.message);
								this.query();
							} else {
								this.$message.error(res.message);
							}
						});
					})
					.catch(() => {});
			},
			// 查询所有用户
			getAllUserList() {
				this.queryParams.pageNum = this.page.currentPage;
				this.queryParams.pageSize = this.page.pageSize;

				findAllUserPage(this.queryParams).then(res => {
					if (res.flag) {
						this.userList = res.data.records;
						this.page.total = res.data.total;
						this.page.pageSize = res.data.size;
						this.page.currentPage = res.data.current;
						this.page.pageCount = res.data.pages;
					} else {
						this.$message.error(res.message);
					}
				}).catch(error => {
					console.error("查询所有用户失败:", error);
					this.$message.error("查询数据失败，请稍后重试");
				});
			},
			// 根据用户输入条件查询
			getUserByQuery() {
				this.queryParams.pageNum = this.page.currentPage;
				this.queryParams.pageSize = this.page.pageSize;

				findUserPage(this.queryParams).then(res => {
					if (res.flag) {
						this.userList = res.data.records;
						this.page.total = res.data.total;
						this.page.pageSize = res.data.size;
						this.page.currentPage = res.data.current;
						this.page.pageCount = res.data.pages;
					} else {
						this.$message.error(res.message);
					}
				}).catch(error => {
					console.error("按条件查询用户失败:", error);
					this.$message.error("查询数据失败，请稍后重试");
				});
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