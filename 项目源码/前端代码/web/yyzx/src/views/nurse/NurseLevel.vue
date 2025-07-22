<template>
	<div class="common-layout">
		<el-container>
			<el-header>
				<div class="header-container">
					<el-form :inline="true" class="search-form">
						<el-row :gutter="15" class="search-row">
							<!-- 护理级别查询框 -->
							<el-col :span="6" class="input-col">
								<el-form-item label="护理级别: ">
									<el-input placeholder="请输入护理级别名称" v-model="queryParams.levelName" clearable
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
							<!-- 添加按钮 -->
							<el-col :span="3" class="btn-col">
								<el-form-item>
									<el-button type="primary" @click="addLevel" color="#337ab7" class="operation-btn">
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
				<div class="card-container">
					<div class="card-header">
						<span class="card-title">护理级别管理</span>
					</div>

					<!-- 状态筛选按钮组 -->
					<el-row class="status-filter" :gutter="10">
						<el-col :span="2">
							<el-button :class="[{activeBtn: btnFlag}]" @click="enable" class="status-btn">
								启用
							</el-button>
						</el-col>
						<el-col :span="2">
							<el-button :class="[{activeBtn: !btnFlag}]" @click="disable" class="status-btn">
								停用
							</el-button>
						</el-col>
					</el-row>

					<!-- 表格 - 添加max-height使表格可滚动 -->
					<el-table :data="listLevel" size="small" style="width: 100%; color:black;" stripe
						:max-height="tableMaxHeight">
						<el-table-column align="center" type="index" :index="indexMethod" label="序号" width="60" />
						<el-table-column align="center" prop="levelName" label="护理级别" width="200" />
						<el-table-column align="center" prop="levelStatus" label="状态" width="200">
							<template #default="scope">
								<el-tag v-if="scope.row.levelStatus === 1" type="success" disable-transitions>
									启用
								</el-tag>
								<el-tag v-if="scope.row.levelStatus === 2" type="danger" disable-transitions>
									停用
								</el-tag>
							</template>
						</el-table-column>
						<el-table-column align="center" fixed="right" label="操作" width="300">
							<template #default="scope">
								<div class="operation-group">
									<el-button type="primary" icon="Edit" size="small" class="operation-btn"
										@click="edit(scope.row)">
										修改
									</el-button>
									<el-button type="danger" icon="Delete" size="small" class="operation-btn"
										@click="del(scope.row.id)">
										删除
									</el-button>
									<el-button type="success" icon="Star" size="small" class="operation-btn"
										v-if="scope.row.levelStatus === 1" @click="itemToLevel(scope.row)">
										护理项目配置
									</el-button>
								</div>
							</template>
						</el-table-column>
					</el-table>

					<!-- 空数据提示 -->
					<div v-if="listLevel.length === 0" class="empty-data">
						<el-empty description="暂无护理级别数据" />
					</div>
				</div>
			</el-main>
		</el-container>

		<!-- 修改/添加对话框 -->
		<el-dialog v-model="dialog.dialogVisible" :title="dialog.tops" width="40%" align-center draggable
			:before-close="handleClose">
			<el-divider border-style="double" style="margin:0;" />
			<el-form label-position="right" label-width="auto" style="max-width:380px;margin:20px auto"
				class="demo-form-inline" ref="levelForm" :model="dialog.level" :rules="dialog.rules">
				<el-form-item label="护理级别：" prop="levelName">
					<el-input v-model="dialog.level.levelName" />
				</el-form-item>
				<el-form-item label="状态：" prop="levelStatus">
					<el-select v-model="dialog.level.levelStatus" style="width:320px" placeholder="请选择状态">
						<el-option v-for="item in dialog.statusArr" :key="item.value" :label="item.label"
							:value="item.value" />
					</el-select>
				</el-form-item>
			</el-form>
			<el-divider border-style="double" style="margin:0;" />
			<template #footer>
				<span class="dialog-footer" style="padding-top: 0px">
					<el-button type="primary" @click="save('levelForm')" class="operation-btn">保存</el-button>
					<el-button @click="cancel" class="operation-btn">取消</el-button>
				</span>
			</template>
		</el-dialog>
	</div>
</template>

<script>
	import {
		addNurseLevel,
		updateNurseLevel,
		removeNurseLevel,
		listNurseLevel
	} from "@/api/nurseLevelApi.js";
	import {
		Plus,
		Edit,
		Delete,
		Star,
		Search
	} from '@element-plus/icons-vue'

	export default {
		components: {
			Plus,
			Edit,
			Delete,
			Star,
			Search
		},
		data() {
			return {
				btnFlag: true, // 状态筛选标记：true-启用，false-停用
				tableMaxHeight: 0, // 表格最大高度（用于滚动）
				queryParams: {
					levelStatus: "1", // 查询默认状态1-启用
					levelName: "", // 护理级别名称查询条件
					// 关键：添加参数告诉后端不需要分页，返回全部数据
					allData: true // 自定义参数，需后端配合支持
				},
				dialog: {
					dialogVisible: false,
					tops: "",
					level: {
						id: "",
						levelName: "",
						levelStatus: ""
					},
					statusArr: [{
							value: 1,
							label: "启用"
						},
						{
							value: 2,
							label: "停用"
						}
					],
					rules: {
						levelName: [{
							required: true,
							message: "请输入级别",
							trigger: "blur"
						}],
						levelStatus: [{
							required: true,
							message: "请选择状态",
							trigger: "change"
						}]
					}
				},
				listLevel: []
			};
		},
		mounted() {
			// 计算表格最大高度（留出头部和底部空间）
			this.calcTableHeight();
			// 监听窗口大小变化，动态调整表格高度
			window.addEventListener('resize', this.calcTableHeight);
			// 加载数据
			this.listNurseLevel();
		},
		beforeUnmount() {
			// 移除事件监听
			window.removeEventListener('resize', this.calcTableHeight);
		},
		methods: {
			// 计算表格最大高度，超出则滚动
			calcTableHeight() {
				// 窗口高度 - 头部高度 - 边距（根据实际布局调整数值）
				this.tableMaxHeight = window.innerHeight - 220;
			},

			// 序号计算（基于数组索引）
			indexMethod(index) {
				return index + 1;
			},

			// 点击查询
			query() {
				this.listNurseLevel();
			},

			// 点击启用筛选
			enable() {
				this.btnFlag = true;
				this.queryParams.levelStatus = "1"; // 1-启用
				this.listNurseLevel();
			},

			// 点击停用筛选
			disable() {
				this.btnFlag = false;
				this.queryParams.levelStatus = "2"; // 2-停用
				this.listNurseLevel();
			},

			// 关闭模态框
			handleClose() {
				this.dialog.dialogVisible = false;
				this.resetForm("levelForm"); // 重置表单
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

			// 点击添加
			addLevel() {
				this.dialog.tops = "添加护理级别";
				this.dialog.dialogVisible = true;
				// 重置表单数据
				this.resetForm("levelForm");
				this.dialog.level.id = "";
				this.dialog.level.levelName = "";
				this.dialog.level.levelStatus = 1; // 默认启用
			},

			// 点击编辑
			edit(row) {
				this.dialog.tops = "编辑护理级别";
				this.dialog.dialogVisible = true;
				// 初始化模态框数据
				this.$nextTick(() => {
					this.dialog.level.id = row.id;
					this.dialog.level.levelName = row.levelName;
					this.dialog.level.levelStatus = row.levelStatus;
				});
			},

			// 点击护理项目配置
			itemToLevel(row) {
				this.$router.push({
					path: "/nurse/addItemToLevel",
					query: {
						levelId: row.id,
						levelName: row.levelName
					}
				});
			},

			// 保存(新增/编辑)
			save(formName) {
				this.$refs[formName].validate(valid => {
					if (valid) {
						// 通过id判断是添加还是编辑
						if (!this.dialog.level.id) {
							addNurseLevel(this.dialog.level).then(res => {
								if (res.flag) {
									this.$message.success(res.message);
									// 刷新数据表格
									this.listNurseLevel();
									this.handleClose(); // 关闭模态框
								} else {
									this.$message.error(res.message);
								}
							}).catch(error => {
								console.error('添加护理级别失败:', error);
								this.$message.error('添加失败，请检查网络连接');
							});
						} else {
							updateNurseLevel(this.dialog.level).then(res => {
								if (res.flag) {
									this.$message.success(res.message);
									// 刷新数据表格
									this.listNurseLevel();
									this.handleClose(); // 关闭模态框
								} else {
									this.$message.error(res.message);
								}
							}).catch(error => {
								console.error('更新护理级别失败:', error);
								this.$message.error('更新失败，请检查网络连接');
							});
						}
					} else {
						return false;
					}
				});
			},

			// 删除
			del(id) {
				this.$confirm("此操作将删除该护理级别, 请谨慎操作?", "提示", {
						confirmButtonText: "确定",
						cancelButtonText: "取消",
						type: "warning"
					})
					.then(() => {
						removeNurseLevel({
							id: id
						}).then(res => {
							if (res.flag) {
								this.$message.success(res.message);
								// 重载表格
								this.listNurseLevel();
							} else {
								this.$message.error(res.message);
							}
						}).catch(error => {
							console.error('删除护理级别失败:', error);
							this.$message.error('删除失败，请检查网络连接');
						});
					})
					.catch(() => {});
			},

			// 查询护理级别列表（获取全部数据）
			listNurseLevel() {
				// 打印参数，确认是否携带了allData=true
				console.log('查询参数:', this.queryParams);

				listNurseLevel(this.queryParams).then(res => {
					const data = res.data || {};
					// 兼容两种接口返回格式：
					// 1. 全量数据直接返回数组
					// 2. 分页结构返回（取records数组）
					this.listLevel = Array.isArray(data) ? data : (data.records || []);

					// 打印返回数据长度，确认是否获取全部数据
					console.log('获取到的数据条数:', this.listLevel.length);
				}).catch(error => {
					console.error('获取护理级别列表失败:', error);
					this.$message.error('获取数据失败，请检查网络连接');
					this.listLevel = [];
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
		padding: 6px 12px;
		font-size: 13px;
		height: 36px;
		margin: 0 3px !important;
		border-radius: 4px;
		transition: all 0.3s ease;
		font-weight: 500;
	}

	/* 状态筛选按钮组 */
	.status-filter {
		padding: 15px 20px;
		display: flex;
		align-items: center;
	}

	.status-btn {
		width: 100%;
		border-radius: 4px;
		transition: all 0.3s ease;
	}

	/* 激活状态按钮样式 */
	.activeBtn {
		color: #0050B3;
		border-color: #409EFF;
		background-color: #E6F7FF;
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

	.el-button:hover {
		transform: translateY(-2px);
		box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
	}

	/* 表格样式 - 允许滚动 */
	.el-table {
		width: 100%;
		margin: 0 20px 20px;
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

	/* 操作按钮组样式 */
	.operation-group {
		display: flex;
		align-items: center;
		justify-content: center;
		gap: 5px;
	}

	/* 空数据提示样式 */
	.empty-data {
		text-align: center;
		padding: 50px 0;
		color: #909399;
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

	/* 按钮样式细化 */
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

	.el-button--success {
		color: #0A8F08;
		border-color: #52C41A;
		background-color: #F6FFED;
	}

	.el-button--success:hover {
		color: #076E06;
		border-color: #389E0D;
		background-color: #E6F7E9;
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

		.el-dialog {
			width: 90% !important;
		}

		.operation-btn {
			padding: 4px 8px;
			font-size: 12px;
			height: 32px;
		}
	}
</style>