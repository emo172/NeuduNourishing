<template>
	<div class="common-layout">
		<el-container>
			<el-header>
				<div class="header-container">
					<el-form :inline="true" class="search-form">
						<el-row :gutter="15" class="search-row">
							<!-- 护理项目查询框 -->
							<el-col :span="8" class="input-col">
								<el-form-item label="护理项目名称: ">
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
					<!-- 左侧护理项目列表 -->
					<el-col :span="11" class="table-column">
						<div class="card-container">
							<div class="card-header">
								<span class="card-title">护理项目</span>
							</div>
							<!-- 表格 -->
							<el-table :data="nurseItemList" highlight-current-row style="width: 100%; color:black;"
								size="small" @row-click="changeRow" stripe :max-height="tableMaxHeight">
								<el-table-column align="center" type="index" :index="indexMethod" label="序号"
									width="60" />
								<el-table-column align="center" prop="serialNumber" label="编号" width="80" />
								<el-table-column align="center" prop="nursingName" label="名称" width="100" />
								<el-table-column align="center" prop="servicePrice" label="价格" width="80" />
								<el-table-column align="center" prop="executionCycle" label="执行周次" width="80" />
								<el-table-column align="center" prop="executionTimes" label="执行次数" width="80" />
							</el-table>
							<!-- 分页插件 -->
							<div style="margin-top:15px" v-if="nurseItemList.length > 0">
								<el-pagination :page-size="page.pageSize" background :current-page="page.currentPage"
									layout="prev, pager, next" :total="page.total"
									@current-change="handleCurrentChange" />
							</div>
							<!-- 空数据提示 -->
							<div v-if="nurseItemList.length === 0" class="empty-data">
								<el-empty description="暂无护理项目数据" />
							</div>
						</div>
					</el-col>

					<!-- 右侧已选护理项目列表 -->
					<el-col :span="13" class="table-column">
						<div class="card-container">
							<div class="card-header">
								<span class="card-title">护理项目({{levelName}})</span>
							</div>
							<!-- 表格 -->
							<el-table :data="currentLevelItems" style="width: 100%; color:black;" size="small" stripe
								:max-height="tableMaxHeight">
								<!-- 自定义序号列 -->
								<el-table-column align="center" label="序号" width="60">
									<template #default="scope">
										{{ getContinuousIndex(scope.$index) }}
									</template>
								</el-table-column>
								<el-table-column align="center" prop="serialNumber" label="编号" width="80" />
								<el-table-column align="center" prop="nursingName" label="名称" width="100" />
								<el-table-column align="center" prop="servicePrice" label="价格" width="80" />
								<el-table-column align="center" prop="executionCycle" label="执行周次" width="80" />
								<el-table-column align="center" prop="executionTimes" label="执行次数" width="80" />
								<el-table-column align="center" fixed="right" label="操作" width="100">
									<template #default="scope">
										<el-button type="danger" icon="CloseBold" size="small" class="operation-btn"
											@click="del(scope.row.id)" tooltip="删除"></el-button>
									</template>
								</el-table-column>
							</el-table>

							<!-- 空数据提示 -->
							<div v-if="currentLevelItems.length === 0" class="empty-data">
								<el-empty description="暂无配置的护理项目" />
							</div>

							<!-- 右侧表格分页 -->
							<div v-if="levelPage.total > 0" style="margin-top:15px">
								<el-pagination :page-size="levelPage.pageSize" background
									:current-page="levelPage.currentPage" layout="prev, pager, next"
									:total="levelPage.total" @current-change="handleLevelCurrentChange" />
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
		findNurseItemPage
	} from "@/api/nurseItemApi.js";
	import {
		addItemToLevel,
		listNurseItemByLevel,
		removeNurseLevelItem
	} from "@/api/nurseLevelApi.js";
	import {
		Search,
		ArrowLeft,
		CloseBold
	} from '@element-plus/icons-vue'

	export default {
		components: {
			Search,
			ArrowLeft,
			CloseBold
		},
		data() {
			return {
				levelId: this.$route.query.levelId,
				levelName: this.$route.query.levelName,
				tableMaxHeight: 0, // 表格最大高度
				page: {
					total: 0,
					pageSize: 10,
					currentPage: 1,
					pagCount: 0
				},
				// 右侧表格分页数据
				levelPage: {
					total: 0,
					pageSize: 10,
					currentPage: 1,
					pagCount: 0
				},
				queryParams: {
					status: 1,
					itemName: "",
					pageNum: 1
				},
				allLevelItems: [], // 存储所有级别项目数据
				currentLevelItems: [], // 当前页显示的级别项目数据
				nurseItemList: []
			};
		},
		mounted() {
			// 计算表格高度
			this.calcTableHeight();
			// 监听窗口大小变化
			window.addEventListener('resize', this.calcTableHeight);

			// 获取路由参数
			this.levelId = this.$route.query.levelId;
			this.levelName = this.$route.query.levelName;

			if (!this.levelId) {
				this.$message.error("缺少级别ID，无法加载数据");
				this.goBack();
				return;
			}

			// 加载数据
			this.getNurseItemList();
			this.listNurseItemByLevel();
		},
		beforeUnmount() {
			// 移除事件监听
			window.removeEventListener('resize', this.calcTableHeight);
		},
		computed: {
			// 左侧表格序号计算
			indexMethod() {
				return (index) => (this.page.currentPage - 1) * this.page.pageSize + index + 1;
			}
		},
		methods: {
			// 计算表格最大高度
			calcTableHeight() {
				// 窗口高度 - 头部高度 - 边距（根据实际布局调整）
				this.tableMaxHeight = window.innerHeight - 200;
			},

			// 点击查询
			query() {
				this.page.currentPage = 1;
				this.queryParams.pageNum = 1;
				this.queryParams.itemName = this.queryParams.itemName.trim();
				this.getNurseItemList();
			},

			// 返回按钮
			goBack() {
				// 导航到护理级别管理页面
				this.$router.push("/nurse/nurseLevel").then(() => {
					// 关闭当前标签页
					this.closeCurrentTab();
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

			// 左侧表格分页切换
			handleCurrentChange(curPage) {
				this.page.currentPage = curPage;
				this.queryParams.pageNum = curPage;
				this.getNurseItemList();
			},

			// 右侧表格页码切换
			handleLevelCurrentChange(curPage) {
				this.levelPage.currentPage = curPage;
				this.updateCurrentLevelItems();
			},

			// 更新当前页显示的级别项目
			updateCurrentLevelItems() {
				const startIndex = (this.levelPage.currentPage - 1) * this.levelPage.pageSize;
				const endIndex = startIndex + this.levelPage.pageSize;
				this.currentLevelItems = this.allLevelItems.slice(startIndex, endIndex);
			},

			// 计算跨页连续序号
			getContinuousIndex(index) {
				return (this.levelPage.currentPage - 1) * this.levelPage.pageSize + index + 1;
			},

			// 点击添加项目到级别
			changeRow(row) {
				if (!this.levelId) return;

				addItemToLevel({
					levelId: this.levelId,
					itemId: row.id
				}).then(res => {
					if (res.flag) {
						this.$message.success("添加成功");
						this.listNurseItemByLevel();
					} else {
						this.$message.error(res.message);
					}
				}).catch(error => {
					console.error('添加项目失败:', error);
					this.$message.error('添加失败，请检查网络连接');
				});
			},

			// 查询护理项目（分页）
			getNurseItemList() {
				findNurseItemPage({
					status: this.queryParams.status,
					itemName: this.queryParams.itemName,
					pageNo: this.queryParams.pageNum,
					pageSize: this.page.pageSize
				}).then(res => {
					if (res.flag) {
						this.nurseItemList = Array.isArray(res.data?.records) ? res.data.records : [];
						this.page.total = res.data?.total || 0;
						this.page.pageSize = res.data?.size || this.page.pageSize;
						this.page.currentPage = res.data?.current || this.page.currentPage;
					} else {
						this.$message.error(res.message || "查询失败");
						this.nurseItemList = [];
					}
				}).catch(error => {
					console.error('查询护理项目失败:', error);
					this.$message.error('查询失败，请检查网络连接');
					this.nurseItemList = [];
				});
			},

			// 查询当前级别护理项目
			listNurseItemByLevel() {
				if (!this.levelId) return;

				listNurseItemByLevel({
					levelId: this.levelId
				}).then(res => {
					if (res.flag) {
						const items = res.data?.data || [];
						this.allLevelItems = Array.isArray(items) ? items : [];
						// 初始化分页数据
						this.levelPage.total = this.allLevelItems.length;
						this.levelPage.pagCount = Math.ceil(this.levelPage.total / this.levelPage.pageSize);
						// 更新当前页显示的数据
						this.updateCurrentLevelItems();
					} else {
						this.$message.error(res.message || "查询级别项目失败");
						this.allLevelItems = [];
						this.currentLevelItems = [];
					}
				}).catch(error => {
					console.error('查询级别项目失败:', error);
					this.$message.error('查询失败，请检查网络连接');
					this.allLevelItems = [];
					this.currentLevelItems = [];
				});
			},

			// 删除配置项目
			del(id) {
				this.$confirm("此操作将删除该配置项目，是否继续?", "提示", {
					confirmButtonText: "确定",
					cancelButtonText: "取消",
					type: "warning"
				}).then(() => {
					removeNurseLevelItem({
						levelId: this.levelId,
						itemId: id
					}).then(res => {
						if (res.flag) {
							this.$message.success(res.message);
							this.listNurseItemByLevel();
						} else {
							this.$message.error(res.message || "删除失败");
						}
					}).catch(error => {
						console.error('删除项目失败:', error);
						this.$message.error('删除失败，请检查网络连接');
					});
				}).catch(() => {});
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