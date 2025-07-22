<template>
	<div class="common-layout">
		<el-container>
			<el-header>
				<div class="header-container">
					<el-form :inline="true" class="search-form">
						<el-row :gutter="15" class="search-row">
							<!-- 护理项目查询框 -->
							<el-col :span="6" class="input-col">
								<el-form-item label="护理项目: ">
									<el-input placeholder="请输入护理项目名称" v-model="queryParams.itemName" clearable
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
				<div class="card-container">
					<div class="card-header">
						<span class="card-title">护理项目管理</span>
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

					<!-- 表格 -->
					<el-table :data="nurseItemList" size="small" style="width: 100%; color:black;" stripe
						:max-height="tableMaxHeight">
						<el-table-column align="center" type="index" :index="indexMethod" label="序号" width="60" />
						<el-table-column align="center" prop="serialNumber" label="编号" width="120" />
						<el-table-column align="center" prop="nursingName" label="名称" width="120" />
						<el-table-column align="center" prop="servicePrice" label="价格" width="120" />
						<el-table-column align="center" prop="executionCycle" label="执行周次" width="120" />
						<el-table-column align="center" prop="executionTimes" label="执行次数" width="120" />
						<el-table-column align="center" prop="message" label="描述" width="120" />
						<el-table-column align="center" prop="status" label="状态" width="120">
							<template #default="scope">
								<el-tag v-if="scope.row.status === 1" type="success" disable-transitions>
									启用
								</el-tag>
								<el-tag v-if="scope.row.status === 2" type="danger" disable-transitions>
									停用
								</el-tag>
							</template>
						</el-table-column>
						<el-table-column align="center" fixed="right" label="操作" width="220">
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
								</div>
							</template>
						</el-table-column>
					</el-table>

					<!-- 分页插件 -->
					<div style="margin-top:15px" v-if="nurseItemList.length > 0">
						<el-pagination :page-size="page.pageSize" background :current-page="page.currentPage"
							layout="prev, pager, next" :total="page.total" @current-change="handleCurrentChange" />
					</div>

					<!-- 空数据提示 -->
					<div v-if="nurseItemList.length === 0" class="empty-data">
						<el-empty description="暂无护理项目数据" />
					</div>
				</div>
			</el-main>
		</el-container>

		<!-- 修改/添加对话框 -->
		<el-dialog v-model="dialog.dialogVisible" :title="dialog.tops" width="40%" align-center draggable
			:before-close="handleClose">
			<el-divider border-style="double" style="margin:0;" />
			<el-form label-position="right" label-width="auto" style="max-width:380px;margin:20px auto"
				class="demo-form-inline" ref="itemForm" :model="dialog.item" :rules="dialog.rules">
				<el-form-item label="编号:" prop="serialNumber">
					<el-input v-model="dialog.item.serialNumber" />
				</el-form-item>
				<el-form-item label="名称:" prop="nursingName">
					<el-input v-model="dialog.item.nursingName" />
				</el-form-item>
				<el-form-item label="价格:" prop="servicePrice">
					<el-input v-model="dialog.item.servicePrice" />
				</el-form-item>
				<el-form-item label="执行周期:" prop="executionCycle">
					<el-select v-model="dialog.item.executionCycle" style="width:320px" placeholder="执行周期: ">
						<el-option label="每天" value="每天" />
						<el-option label="每周" value="每周" />
						<el-option label="每月" value="每月" />
					</el-select>
				</el-form-item>
				<el-form-item label="执行次数:" prop="executionTimes">
					<el-input v-model="dialog.item.executionTimes" />
				</el-form-item>
				<el-form-item label="描述: " prop="message">
					<el-input v-model="dialog.item.message" />
				</el-form-item>
				<el-form-item label="状态:" prop="status">
					<el-select v-model="dialog.item.status" style="width:320px" placeholder="请选择状态">
						<el-option v-for="item in dialog.statusArr" :key="item.value" :label="item.label"
							:value="item.value" />
					</el-select>
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
		addNurseItem,
		findNurseItemPage,
		updateNurseItem,
		delNurseItem
	} from '@/api/nurseItemApi.js';
	import {
		Plus,
		Edit,
		Delete,
		Search
	} from '@element-plus/icons-vue'

	export default {
		components: {
			Plus,
			Edit,
			Delete,
			Search
		},
		data() {
			return {
				btnFlag: true, // 状态筛选标记：true-启用，false-停用
				tableMaxHeight: 0, // 表格最大高度（用于滚动）
				debouncedCalcHeight: null, // 防抖函数引用
				// 分页属性封装
				page: {
					total: 0,
					pageSize: 10,
					currentPage: 1,
					pagCount: 0
				},
				queryParams: {
					status: "1", // 查询默认状态1-启用
					itemName: "", // 护理项目名称查询条件
					pageNum: 1, // 当前页码
					pageSize: 10 // 每页条数
				},
				// 模态框数据
				dialog: {
					dialogVisible: false, // 模态框状态
					tops: "", // 模态框标题
					item: {
						id: "",
						serialNumber: "",
						nursingName: "",
						servicePrice: "",
						message: "",
						executionCycle: "",
						executionTimes: "",
						status: 1 // 状态1: 启用; 2:停用（默认启用）
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
						serialNumber: [{
							required: true,
							message: "请输入编号",
							trigger: "blur"
						}],
						nursingName: [{
							required: true,
							message: "请输入名称",
							trigger: "blur"
						}],
						servicePrice: [{
							required: true,
							message: "请输入价格",
							trigger: "blur"
						}],
						status: [{
							required: true,
							message: "请选择状态",
							trigger: "change"
						}]
					}
				},
				nurseItemList: []
			};
		},
		mounted() {
			// 初始化防抖函数，延迟100ms执行
			this.debouncedCalcHeight = this.debounce(this.calcTableHeight, 100);
			// 监听窗口大小变化，使用防抖函数处理
			window.addEventListener('resize', this.debouncedCalcHeight);
			// 初始计算表格高度
			this.calcTableHeight();
			// 加载数据
			this.getNurseItemList();
		},
		beforeUnmount() {
			// 移除事件监听（必须使用相同的函数引用）
			window.removeEventListener('resize', this.debouncedCalcHeight);
		},
		methods: {
			// 防抖函数：限制函数执行频率
			debounce(func, delay = 100) {
				let timer = null;
				return (...args) => {
					clearTimeout(timer);
					timer = setTimeout(() => {
						func.apply(this, args);
					}, delay);
				};
			},

			// 计算表格最大高度
			calcTableHeight() {
				// 窗口高度 - 头部高度 - 边距（根据实际布局调整）
				this.tableMaxHeight = window.innerHeight - 220;
			},

			// 序号计算
			indexMethod(index) {
				return (this.page.currentPage - 1) * this.page.pageSize + index + 1;
			},

			// 点击查询
			query() {
				this.page.currentPage = 1;
				this.queryParams.pageNum = 1;
				this.getNurseItemList();
			},

			// 点击启用筛选
			enable() {
				this.btnFlag = true;
				this.queryParams.status = "1"; // 1-启用
				this.page.currentPage = 1;
				this.queryParams.pageNum = 1;
				this.getNurseItemList();
			},

			// 点击停用筛选
			disable() {
				this.btnFlag = false;
				this.queryParams.status = "2"; // 2-停用
				this.page.currentPage = 1;
				this.queryParams.pageNum = 1;
				this.getNurseItemList();
			},

			// 分页切换
			handleCurrentChange(curPage) {
				this.page.currentPage = curPage;
				this.queryParams.pageNum = curPage;
				this.getNurseItemList();
			},

			// 点击修改
			edit(row) {
				this.dialog.tops = "修改护理项目";
				this.dialog.dialogVisible = true;
				// 初始化模态框数据
				this.$nextTick(() => {
					this.dialog.item.id = row.id;
					this.dialog.item.serialNumber = row.serialNumber;
					this.dialog.item.nursingName = row.nursingName;
					this.dialog.item.servicePrice = row.servicePrice;
					this.dialog.item.message = row.message;
					this.dialog.item.executionCycle = row.executionCycle;
					this.dialog.item.executionTimes = row.executionTimes;
					this.dialog.item.status = row.status;
				});
			},

			// 点击添加按钮
			addItem() {
				this.dialog.tops = "添加护理项目";
				this.dialog.dialogVisible = true;
				// 重置表单数据
				this.resetForm("itemForm");
				this.dialog.item = {
					id: "",
					serialNumber: "",
					nursingName: "",
					servicePrice: "",
					message: "",
					executionCycle: "",
					executionTimes: "",
					status: 1
				};
			},

			// 关闭模态框
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
							addNurseItem(this.dialog.item).then(res => {
								if (res.flag) {
									this.$message.success(res.message);
									// 刷新数据表格
									this.getNurseItemList();
									this.handleClose(); // 关闭模态框
								} else {
									this.$message.error(res.message);
								}
							}).catch(error => {
								console.error('添加护理项目失败:', error);
								this.$message.error('添加失败，请检查网络连接');
							});
						} else {
							updateNurseItem(this.dialog.item).then(res => {
								if (res.flag) {
									this.$message.success(res.message);
									// 刷新数据表格
									this.getNurseItemList();
									this.handleClose(); // 关闭模态框
								} else {
									this.$message.error(res.message);
								}
							}).catch(error => {
								console.error('更新护理项目失败:', error);
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
				this.$confirm("此操作将删除该护理项目, 请谨慎操作?", "提示", {
						confirmButtonText: "确定",
						cancelButtonText: "取消",
						type: "warning"
					})
					.then(() => {
						delNurseItem({
							id: id
						}).then(res => {
							if (res.flag) {
								this.$message.success(res.message);
								// 重载表格
								this.getNurseItemList();
							} else {
								this.$message.error(res.message);
							}
						}).catch(error => {
							console.error('删除护理项目失败:', error);
							this.$message.error('删除失败，请检查网络连接');
						});
					})
					.catch(() => {});
			},

			// 查询护理项目列表
			getNurseItemList() {
				// 同步分页参数
				this.queryParams.pageSize = this.page.pageSize;

				findNurseItemPage(this.queryParams).then(res => {
					if (res.flag) {
						const pageData = res.data || {};
						this.nurseItemList = pageData.records || [];
						this.page.total = pageData.total || 0;
						this.page.pageSize = pageData.size || this.page.pageSize;
						this.page.currentPage = pageData.current || this.page.currentPage;
					} else {
						this.nurseItemList = [];
						this.page.total = 0;
						this.$message.error(res.message || '获取数据失败');
					}
				}).catch(error => {
					console.error('获取护理项目列表失败:', error);
					this.$message.error('获取数据失败，请检查网络连接');
					this.nurseItemList = [];
					this.page.total = 0;
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

	/* 表格样式 */
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