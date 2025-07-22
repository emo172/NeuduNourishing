<template>
	<div class="common-layout">
		<el-container>
			<el-header>
				<div class="header-container">
					<el-form :inline="true" class="search-form">
						<el-row :gutter="15" class="search-row">
							<!-- 护理项目名称搜索框 -->
							<el-col :span="7" class="input-col">
								<el-input placeholder="护理项目名称" v-model="queryParams.itemName" @clear="query" clearable
									size="large" class="full-width">
									<template #append>
										<el-button type="primary" @click="query" class="operation-btn" color="#337ab7">
											<el-icon>
												<Search />
											</el-icon>
											<span>查询</span>
										</el-button>
									</template>
								</el-input>
							</el-col>
							<!-- 返回按钮 -->
							<el-col :span="3" class="btn-col">
								<el-form-item>
									<el-button type="primary" @click="goBack()" color="#337ab7" class="operation-btn"
										round>
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
				<div class="card-container">
					<div class="card-header">
						<span class="card-title">护理项目</span>
					</div>
					<!-- 表格 -->
					<el-table :data="nurseItemList" style="width: 100%; color:black;" size="small"
						@row-click="changeRow" stripe>
						<el-table-column align="center" type="index" :index="indexMethod" label="序号" width="60" />
						<el-table-column align="center" prop="serialNumber" label="编号" width="180" />
						<el-table-column align="center" prop="nursingName" label="名称" width="180" />
						<el-table-column align="center" prop="servicePrice" label="价格" width="180" />
						<el-table-column align="center" prop="executionCycle" label="执行周次" width="180" />
						<el-table-column align="center" prop="executionTimes" label="执行次数" width="180" />
						<el-table-column align="center" prop="message" label="备注" width="180" />
					</el-table>
					<!-- 空数据提示 -->
					<div v-if="nurseItemList.length === 0" class="empty-data">
						<el-empty description="暂无护理项目数据" />
					</div>
					<!-- 分页插件 -->
					<div style="margin-top:15px">
						<el-pagination :page-size="page.pageSize" background :current-page="page.currentPage"
							layout="prev, pager, next" :total="page.total" @current-change="handleCurrentChange" />
					</div>
				</div>

				<el-divider style="margin:20px 0"></el-divider>

				<div class="card-container">
					<div class="card-header">
						<span class="card-title">已选护理项目</span>
					</div>
					<!-- 表格 -->
					<el-table :data="changeNurseItemList" style="width: 100%; color:black;" size="small" stripe>
						<el-table-column align="center" type="index" label="序号" width="60" />
						<el-table-column align="center" prop="serialNumber" label="编号" width="180" />
						<el-table-column align="center" prop="nursingName" label="名称" width="180" />
						<el-table-column align="center" prop="buyTime" label="服务购买日期" width="180" />
						<el-table-column align="center" prop="nurseNumber" label="购买数量" width="180">
							<template #default="scope">
								<el-input v-model="scope.row.nurseNumber" placeholder="购买数量"></el-input>
							</template>
						</el-table-column>
						<el-table-column align="center" prop="maturityTime" label="服务到期日期" width="180">
							<template #default="scope">
								<el-date-picker style="width:130px" format="YYYY/MM/DD" value-format="YYYY-MM-DD"
									v-model="scope.row.maturityTime" type="date" placeholder="服务到期日期"></el-date-picker>
							</template>
						</el-table-column>
						<el-table-column align="center" label="操作" width="100">
							<template #default="scope">
								<el-button type="danger" icon="CloseBold" size="small" class="operation-btn"
									@click="del(scope.$index)">
								</el-button>
							</template>
						</el-table-column>
					</el-table>

					<div class="demo-drawer__footer" style="margin:20px auto; width:200px">
						<el-button @click="clearArr" type="warning" class="operation-btn">清空</el-button>
						<el-button @click="isOk" type="primary" color="#337ab7" class="operation-btn">保存</el-button>
					</div>
				</div>
			</el-main>
		</el-container>
	</div>
</template>


<script>
	import {
		findNurseItemPage
	} from "@/api/nurseItemApi.js";
	import {
		isIncludesItemCustomer,
		addItemToCustomer
	} from "@/api/customerNurseItemApi.js";
	import {
		ElMessage
	} from 'element-plus';
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
		computed: {
			indexMethod() {
				return (this.page.currentPage - 1) * this.page.pageSize + 1;
			}
		},
		data() {
			return {
				customerId: this.$route.query.customerId,
				// 分页属性封装
				page: {
					total: 0,
					pageSize: 10,
					currentPage: 1,
					pageCount: 0
				},
				btnFlag: true,
				queryParams: {
					status: "1", // 查询默认状态1-启用
					itemName: "",
					pageNum: 1 // 默认第一页
				},
				changeNurseItemList: [], //已选择护理项目
				nurseItemList: [], //护理项目列表
				isLoading: false, // 加载状态
				errorMessage: null // 错误信息
			};
		},
		mounted() {
			// 检查customerId是否存在
			if (!this.customerId) {
				console.error('未获取到有效的customerId参数');
				this.$message.error('未获取到客户ID，请重新进入页面');
				// 可以选择返回上一页
				// this.$router.go(-1);
			}
			this.getNurseItemList();
		},
		methods: {
			// 处理Vuex错误的临时方法
			handleVuexError() {
				// 检查store中是否存在setMenu action
				if (!this.$store._actions['setMenu']) {
					console.error('[Vuex] 未找到setMenu action，请在store中定义');
				}
			},
			//点击查询
			query() {
				this.page.currentPage = 1;
				this.queryParams.pageNum = 1;
				this.queryParams.itemName = this.queryParams.itemName.trim();
				this.getNurseItemList();
			},
			// 返回按钮 - 优化导航和标签页关闭
			goBack() {
				// 1. 先导航到护理级别管理页面
				this.$router.push("/health/serviceCare").then(() => {
					// 2. 导航成功后关闭当前标签页
					this.closeCurrentTab();
				});
			},
			// 关闭当前标签页
			closeCurrentTab() {
				// 获取当前标签页在tabs中的索引
				const currentTabIndex = this.$store.getters.tabs.findIndex(
					tab => tab.path === this.$route.path
				);

				if (currentTabIndex > -1) {
					// 调用Vuex方法关闭标签页
					this.$store.commit("deleteTabByIndex", currentTabIndex);
				} else {
					console.warn("未找到当前标签页，无法关闭");
				}
			},
			// 选中页码
			handleCurrentChange(curPage) {
				this.page.currentPage = curPage;
				this.queryParams.pageNum = curPage; // 参数pageNum是服务端接收页码参数名
				// 重新渲染表格
				this.getNurseItemList();
			},
			//鼠标点击某行触发：添加护理项目到已选护理项目列表  api-判断用户是否已经配置了某个指定项目
			changeRow(row) {
				// 检查customerId是否存在
				if (!this.customerId) {
					this.$message.error('客户ID无效，请重新进入页面');
					return;
				}

				//api-判断用户是否已经配置了某个指定项目
				isIncludesItemCustomer({
					customerId: this.customerId,
					itemId: row.id
				}).then(res => {
					if (!res.flag) {
						this.$message.error("当前用户已存在该项目,可前往【服务关注】进行续费")
						return
					}
					//构建已选护理项目列表
					let obj = {
						...row
					}; // 创建对象副本，避免修改原数据
					const formatDate = (date) => {
						const year = date.getFullYear();
						const month = String(date.getMonth() + 1).padStart(2, '0');
						const day = String(date.getDate()).padStart(2, '0');
						return `${year}-${month}-${day}`;
					};

					// 设置购买日期和到期日期
					obj.buyTime = formatDate(new Date());
					obj.maturityTime = formatDate(new Date(new Date().setMonth(new Date().getMonth() + 3)));
					obj.nurseNumber = 1;

					// 判断是否已经添加过
					const exists = this.changeNurseItemList.some(item => item.id === obj.id);
					if (exists) {
						this.$message.warning("所选项目已存在列表中")
						return
					}

					this.changeNurseItemList.push(obj)
				}).catch(error => {
					console.error('检查项目是否已存在失败:', error);
					this.$message.error('检查项目是否已存在失败，请重试');
				})
			},
			//移除已选护理项目列表项
			del(index) {
				this.changeNurseItemList.splice(index, 1)
			},
			//清空已选护理项目列表项
			clearArr() {
				this.changeNurseItemList = []
			},
			//api-查询护理项目(分页)
			async getNurseItemList() {
				this.isLoading = true;
				this.errorMessage = null;

				try {
					// 构建符合后端要求的参数
					const params = {
						status: this.queryParams.status ? parseInt(this.queryParams.status) : null,
						itemName: this.queryParams.itemName,
						pageNo: this.queryParams.pageNum,
						pageSize: this.page.pageSize
					};

					// 修改：直接传递对象，axios会自动序列化为JSON
					const res = await findNurseItemPage(params);

					console.log('API完整响应:', res);

					// 处理Vuex错误
					this.handleVuexError();

					// 1. 检查响应是否存在
					if (!res) {
						throw new Error('API响应为空');
					}

					// 2. 检查请求是否成功
					if (res.flag) {
						// 3. 获取分页数据
						const pageData = res.data || {};

						// 4. 更新前端数据
						this.nurseItemList = pageData.records || [];
						this.page.total = pageData.total || 0;
						this.page.pageSize = pageData.size || this.page.pageSize;
						this.page.currentPage = pageData.current || this.page.currentPage;
						this.page.pageCount = pageData.pages || 0;

						console.log('数据加载成功，列表长度:', this.nurseItemList.length);
					} else {
						throw new Error(res.message || '获取护理项目列表失败');
					}
				} catch (error) {
					console.error('请求异常:', error);
					this.errorMessage = error.message || '请求失败，请检查网络连接';
					this.nurseItemList = [];
					this.page.total = 0;

					// 显示错误通知
					ElMessage.error(this.errorMessage);
				} finally {
					this.isLoading = false;
				}
			},

			//api-点击确认 批量生成客户护理项目信息
			isOk() {
				// 检查customerId是否存在
				if (!this.customerId) {
					this.$message.error('客户ID无效，无法保存数据');
					return;
				}

				let customerItemList = this.changeNurseItemList
				let paramsArr = []; //提交的参数
				if (customerItemList.length == 0) {
					this.$message.error("请选择需要【购买的】护理项目")
					return
				}

				// 验证表单数据
				const validationErrors = [];
				customerItemList.forEach((item, index) => {
					if (!item.nurseNumber || isNaN(Number(item.nurseNumber)) || Number(item.nurseNumber) <= 0) {
						validationErrors.push(`第 ${index + 1} 行的购买数量无效`);
					}

					if (!item.maturityTime) {
						validationErrors.push(`第 ${index + 1} 行的服务到期日期不能为空`);
					}
				});

				if (validationErrors.length > 0) {
					this.$message.error(validationErrors.join('，'));
					return;
				}

				//构建提交的参数列表
				for (let i = 0; i < customerItemList.length; i++) {
					let obj = {}
					obj.itemId = customerItemList[i].id
					obj.buyTime = customerItemList[i].buyTime
					obj.customerId = this.customerId
					obj.nurseNumber = Number(customerItemList[i].nurseNumber)
					obj.isDeleted = 0
					obj.maturityTime = customerItemList[i].maturityTime

					// 添加到参数数组
					paramsArr.push(obj);
				}

				this.$confirm("确定添护理项目数据无误?", "提示", {
						confirmButtonText: "确定",
						cancelButtonText: "取消",
						type: "warning"
					})
					.then(async () => {
						try {
							// 修正：正确构建请求数据
							const requestData = {
								customerId: this.customerId,
								items: paramsArr
							};

							console.log('准备发送数据:', requestData);

							// 确保API调用正确
							const res = await addItemToCustomer(requestData);

							if (res.flag) {
								this.$message.success(res.message);
								//路由会上一级
								this.$router.go(-1)
							} else {
								this.$message.error(res.message || '保存失败，请重试');
							}
						} catch (error) {
							console.error('保存护理项目失败:', error);
							// 显示更具体的错误信息
							if (error.response && error.response.data && error.response.data.message) {
								this.$message.error('服务器错误: ' + error.response.data.message);
							} else {
								this.$message.error('保存护理项目失败，请检查网络连接');
							}
						}
					})
					.catch(() => {});
			},
		}
	}
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
		margin: 0 5px !important;
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

	/* 空数据提示样式 */
	.empty-data {
		text-align: center;
		padding: 80px 0;
		color: #909399;
		font-size: 14px;
	}

	/* 按钮组样式 */
	.demo-drawer__footer {
		display: flex;
		justify-content: center;
		gap: 10px;
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

		.operation-btn,
		.el-button--primary,
		.el-button--danger {
			padding: 6px 12px;
			font-size: 12px;
			height: 36px;
		}
	}
</style>