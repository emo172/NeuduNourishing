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
				<div class="card-container">
					<div class="card-header">
						<span class="card-title">客户护理管理</span>
					</div>

					<!-- 表格 -->
					<el-table :data="khxxList" style="width: 100%; color:black;" stripe :max-height="tableMaxHeight"
						size="small">
						<el-table-column align="center" fixed type="index" :index="indexMethod" label="序号" width="60" />
						<el-table-column align="center" fixed prop="customerName" label="客户姓名" width="120" />
						<el-table-column align="center" prop="customerAge" label="年龄" width="80" />
						<el-table-column align="center" prop="customerSex" label="性别" width="80">
							<template #default="scope">
								{{ scope.row.customerSex === 0 ? '男' : scope.row.customerSex === 1 ? '女' : '未知' }}
							</template>
						</el-table-column>
						<el-table-column align="center" prop="roomNo" label="房间号" width="100" />
						<el-table-column align="center" prop="bedNo" label="床号" width="100" />
						<el-table-column align="center" prop="buildingNo" label="所属楼房" width="100" />
						<el-table-column align="center" prop="contactTel" label="联系电话" width="150" />
						<el-table-column align="center" prop="levelName" label="护理级别" width="120" />
						<el-table-column align="center" fixed="right" label="操作" width="120">
							<template #default="scope">
								<el-button type="primary" icon="Star" size="small" class="operation-btn"
									@click="openNursing(scope.row)">
									日常护理
								</el-button>
							</template>
						</el-table-column>
					</el-table>

					<!-- 空数据提示 -->
					<div v-if="khxxList.length === 0 && !loading" class="empty-data">
						<el-empty description="没有找到相关数据" />
					</div>

					<!-- 加载中提示 -->
					<div v-if="loading" class="loading">
						<el-loading-spinner />
						<p>加载中...</p>
					</div>

					<!-- 分页插件 -->
					<div style="margin-top:15px" v-if="page.filteredTotal > 0">
						<el-pagination :page-size="condition.pageSize" background :current-page="page.currentPage"
							layout="prev, pager, next" :total="page.filteredTotal"
							@current-change="handleCurrentChange" />
					</div>
				</div>

				<!-- 客户护理项目抽屉 -->
				<el-drawer title="客户护理项目" :before-close="handleClose" v-model="drawer.openFlag" direction="rtl"
					size="70%">
					<el-container>
						<el-header class="drawer-header">
							<div class="customer-info">
								<span class="customer-name">客户: {{ drawer.customerName || '未选择' }}</span>
							</div>
						</el-header>
						<el-divider style="margin:0"></el-divider>
						<el-main class="drawer-main">
							<el-table :data="drawer.isInLevelItemList" size="small" border stripe
								:max-height="drawerTableHeight">
								<el-table-column align="center" type="index" label="序号" width="60" />
								<el-table-column align="center" label="客户" width="120">
									<template #default="scope">
										<span>{{ drawer.customerName || '未选择' }}</span>
									</template>
								</el-table-column>
								<el-table-column align="center" prop="serialNumber" label="编号" width="80" />
								<el-table-column align="center" prop="nursingName" label="名称" width="100" />
								<el-table-column align="center" prop="servicePrice" label="价格" width="80" />
								<el-table-column align="center" label="余量" width="100">
									<template #default="scope">
										<span>{{ scope.row.nurseNumber || 0 }}</span>
									</template>
								</el-table-column>
								<el-table-column align="center" prop="maturityTime" label="服务到期时间" width="160">
									<template #default="scope">
										<span>{{ formatDate(scope.row.maturityTime) || '未设置' }}</span>
									</template>
								</el-table-column>
								<el-table-column align="center" label="状态" width="100">
									<template #default="scope">
										<el-tag :type="getStatusType(scope.row.maturityTime)">
											{{ getStatusText(scope.row.maturityTime) }}
										</el-tag>
									</template>
								</el-table-column>
								<el-table-column align="center" label="操作" width="100">
									<template #default="scope">
										<!-- 修改此处：将size="mini"改为size="small" -->
										<el-button type="primary" size="small" class="operation-btn"
											@click="openRecordDrawer(scope.row)">
											护理
										</el-button>
									</template>
								</el-table-column>
							</el-table>

							<!-- 抽屉空数据提示 -->
							<div v-if="drawer.isInLevelItemList.length === 0 && !loadingLevel" class="drawer-empty">
								<el-empty description="暂无护理项目数据" />
							</div>

							<!-- 抽屉内分页控件 -->
							<div style="margin-top:15px" v-if="drawer.allItems.length > 0">
								<el-pagination @current-change="handleDrawerCurrentChange"
									:current-page="drawer.currentPage" layout="prev, pager, next"
									:page-size="drawer.pageSize" :total="drawer.allItems.length">
								</el-pagination>
							</div>
						</el-main>
					</el-container>
					<div class="demo-drawer__footer">
						<el-button @click="handleClose" class="operation-btn">取 消</el-button>
						<el-button @click="isOk" type="primary" class="operation-btn">确认</el-button>
					</div>
				</el-drawer>

				<!-- 添加护理记录抽屉 -->
				<el-drawer title="添加护理记录" :before-close="handleRecordClose" v-model="recordDrawer.visible"
					direction="rtl" size="50%">
					<div class="record-form-wrapper">
						<el-form ref="recordForm" :model="recordForm" :rules="recordRules" label-width="120px"
							class="record-form">
							<el-form-item label="客户姓名:">
								<el-input v-model="recordForm.customerName" readonly placeholder="客户姓名"
									class="uniform-input" />
							</el-form-item>
							<el-form-item label="护理项目编号:">
								<el-input v-model="recordForm.serialNumber" readonly placeholder="护理项目编号"
									class="uniform-input" />
							</el-form-item>
							<el-form-item label="护理项目名称:">
								<el-input v-model="recordForm.nursingName" readonly placeholder="护理项目名称"
									class="uniform-input" />
							</el-form-item>
							<el-form-item label="护理时间:" prop="nursingTime">
								<el-date-picker v-model="recordForm.nursingTime" type="datetime" placeholder="选择护理时间"
									format="YYYY-MM-DD HH:mm" value-format="YYYY-MM-DD HH:mm" class="uniform-input" />
							</el-form-item>
							<el-form-item label="护理数量:" prop="nursingCount">
								<el-input-number v-model="recordForm.nursingCount" :min="1" :max="99"
									controls-position="right" class="uniform-input" />
							</el-form-item>
							<el-form-item label="护理内容:" prop="nursingContent">
								<el-input v-model="recordForm.nursingContent" type="textarea" placeholder="请输入护理内容"
									class="resizable-textarea uniform-input" :autosize="{ minRows: 4, maxRows: 10 }" />
							</el-form-item>
						</el-form>
					</div>
					<div class="record-form-actions">
						<el-button @click="handleRecordClose" class="operation-btn">取消</el-button>
						<el-button type="primary" @click="submitRecord" class="operation-btn">立即创建</el-button>
					</div>
				</el-drawer>
			</el-main>
		</el-container>
	</div>
</template>

<script>
	import {
		listKhxxPage
	} from "@/api/customerApi.js";
	import {
		listNurseLevel,
		listNurseItemByLevel
	} from "@/api/nurseLevelApi.js";
	import {
		addItemToCustomer,
		removeCustomerLevelAndItem,
		updateNurseItem,
		listCustomerItem // 导入获取客户护理项目的API
	} from "@/api/customerNurseItemApi.js";
	import {
		Search,
		Star
	} from '@element-plus/icons-vue'

	export default {
		components: {
			Search,
			Star
		},
		computed: {
			// 分页序号计算
			indexMethod() {
				return (index) => (this.page.currentPage - 1) * this.condition.pageSize + index + 1;
			}
		},
		data() {
			return {
				tableMaxHeight: 0, // 表格最大高度
				drawerTableHeight: 0, // 抽屉表格高度
				// 客户护理项目抽屉数据
				drawer: {
					openFlag: false,
					listLevel: [],
					levelId: null,
					customerId: "",
					customerName: "",
					allItems: [], // 存储所有护理项目（完整列表）
					isInLevelItemList: [], // 存储当前页的护理项目（分页后）
					currentPage: 1, // 抽屉内分页-当前页
					pageSize: 10, // 抽屉内分页-每页数量
				},
				// 护理记录抽屉数据
				recordDrawer: {
					visible: false,
					currentItem: null // 当前选中的护理项目
				},
				// 护理记录表单数据
				recordForm: {
					customerName: '',
					serialNumber: '',
					nursingName: '',
					nursingTime: new Date(),
					nursingCount: 1,
					nursingContent: ''
				},
				// 护理记录表单验证规则
				recordRules: {
					nursingTime: [{
						required: true,
						message: '请选择护理时间',
						trigger: 'change'
					}],
					nursingCount: [{
						required: true,
						message: '请输入护理数量',
						trigger: 'blur'
					}, {
						type: 'number',
						min: 1,
						message: '护理数量必须大于0',
						trigger: 'change'
					}]
				},
				// 查询条件
				condition: {
					customerName: "",
					currentPage: 1,
					pageSize: 10,
					hasLevel: true // 强制只查询有护理级别的客户
				},
				khxxList: [], // 存储过滤后的客户列表（仅含有效护理级别）
				// 分页属性
				page: {
					total: 0, // 后端返回的原始总条数
					filteredTotal: 0, // 过滤后（仅含有效护理级别）的总条数
					currentPage: 1,
					pagCount: 0
				},
				loading: false,
				loadingLevel: false,
				loadingRecord: false
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
				// 窗口高度 - 头部高度 - 边距
				this.tableMaxHeight = window.innerHeight - 200;
				// 抽屉表格高度
				this.drawerTableHeight = window.innerHeight - 300;
			},

			// 点击查询
			query() {
				this.condition.currentPage = 1;
				this.page.currentPage = 1;
				this.listKhxxPage();
			},

			// 页码变更
			handleCurrentChange(curPage) {
				this.page.currentPage = curPage;
				this.condition.currentPage = curPage;
				this.listKhxxPage();
			},

			// 打开日常护理抽屉 - 修改核心方法
			openNursing(row) {
				if (!row || !row.id) {
					return;
				}

				this.drawer.openFlag = true;
				this.drawer.customerId = row.id;
				this.drawer.customerName = row.customerName;
				this.drawer.levelId = row.levelId;
				this.drawer.allItems = [];
				this.drawer.isInLevelItemList = [];
				this.drawer.currentPage = 1; // 重置抽屉内分页
				this.loadingLevel = true;

				// 先获取护理级别列表
				this.fetchNurseLevels().then(() => {
					if (row.levelId) {
						// 关键修改：先获取客户已配置的护理项目
						return this.getCustomerConfiguredItems(row.id);
					}
				}).then(() => {
					// 如果没有从数据库获取到数据，再获取级别默认项目
					if (this.drawer.allItems.length === 0 && row.levelId) {
						return this.listNurseItemByLevel();
					}
				}).finally(() => {
					this.loadingLevel = false;
				});
			},

			// 新增方法：获取客户已配置的护理项目
			getCustomerConfiguredItems(customerId) {
				return listCustomerItem({
					customerId: customerId,
					pageNum: 1,
					pageSize: 1000 // 获取所有项目
				}).then(res => {
					if (res && res.data && res.data.records && Array.isArray(res.data.records)) {
						// 处理从数据库获取的护理项目
						this.drawer.allItems = res.data.records.map(item => ({
							...item,
							// 确保必要字段存在
							serialNumber: item.serialNumber || '',
							nursingName: item.nursingName || '',
							servicePrice: item.servicePrice || 0,
							// 使用数据库中的购买数量作为余量
							nurseNumber: item.nurseNumber || 1,
							// 使用数据库中的服务到期时间
							maturityTime: item.maturityTime || this.formatDate(new Date(new Date()
								.setMonth(new Date().getMonth() + 3)))
						}));

						// 更新分页数据
						this.updatePagedItems();
					}
				}).catch(error => {
					console.error('获取客户已配置的护理项目失败:', error);
				});
			},

			// 打开添加护理记录抽屉
			openRecordDrawer(item) {
				// 初始化护理记录表单数据
				this.recordForm = {
					customerName: this.drawer.customerName || '',
					serialNumber: item.serialNumber || '',
					nursingName: item.nursingName || '',
					nursingTime: new Date(),
					nursingCount: 1,
					nursingContent: ''
				};

				// 保存当前操作的护理项目
				this.recordDrawer.currentItem = {
					...item
				};
				this.recordDrawer.visible = true;
			},

			// 关闭护理记录抽屉
			handleRecordClose() {
				this.recordDrawer.visible = false;
				this.recordDrawer.currentItem = null;
				// 重置表单验证状态
				if (this.$refs.recordForm) {
					this.$refs.recordForm.resetFields();
				}
			},

			// 提交护理记录
			submitRecord() {
				this.$refs.recordForm.validate(valid => {
					if (!valid) {
						return false;
					}

					// 验证护理数量是否充足
					if (this.recordForm.nursingCount > this.recordDrawer.currentItem.nurseNumber) {
						return false;
					}

					// 验证护理时间是否有效
					const nursingTime = new Date(this.recordForm.nursingTime);
					const maturityTime = new Date(this.recordDrawer.currentItem.maturityTime);
					if (nursingTime > new Date()) {
						return false;
					}
					if (nursingTime > maturityTime) {
						return false;
					}

					// 这里可以添加保存护理记录的API调用
					// 模拟API调用
					new Promise(resolve => {
							setTimeout(() => {
								resolve({
									flag: true,
									message: '护理记录创建成功'
								});
							}, 500);
						})
						.then(res => {
							if (res && res.flag) {
								// 关闭抽屉
								this.handleRecordClose();
								// 执行护理服务（减少余量）
								this.executeNursing(this.recordDrawer.currentItem);
							}
						})
						.catch(error => {
							console.error('提交护理记录失败:', error);
						});
				});
			},

			// 获取护理级别列表
			fetchNurseLevels() {
				return listNurseLevel({
						levelStatus: 1
					})
					.then(res => {
						if (res && res.data && Array.isArray(res.data.records)) {
							this.drawer.listLevel = res.data.records;

							if (this.drawer.levelId && !this.drawer.listLevel.some(item => item.id === this.drawer
									.levelId)) {
								this.drawer.levelId = null;
							}
						} else {
							this.drawer.listLevel = [];
						}
					})
					.catch(error => {
						console.error('获取护理级别失败:', error);
					});
			},

			// 关闭客户护理项目抽屉
			handleClose() {
				this.drawer = {
					openFlag: false,
					listLevel: [],
					levelId: null,
					customerId: "",
					customerName: "",
					allItems: [],
					isInLevelItemList: [],
					currentPage: 1,
					pageSize: 10
				};
			},

			// 获取服务状态文本
			getStatusText(maturityTime) {
				if (!maturityTime) return '未设置';

				const today = new Date();
				today.setHours(0, 0, 0, 0);
				const expireDate = new Date(maturityTime);

				if (expireDate < today) {
					return '已过期';
				} else if (expireDate.getTime() === today.getTime()) {
					return '今日到期';
				} else {
					const diffTime = Math.abs(expireDate - today);
					const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));

					if (diffDays <= 7) {
						return `即将到期(${diffDays}天)`;
					} else {
						return '正常';
					}
				}
			},

			// 获取服务状态标签类型
			getStatusType(maturityTime) {
				if (!maturityTime) return 'info';

				const today = new Date();
				today.setHours(0, 0, 0, 0);
				const expireDate = new Date(maturityTime);

				if (expireDate < today) {
					return 'danger';
				} else if (expireDate.getTime() === today.getTime()) {
					return 'warning';
				} else {
					const diffTime = Math.abs(expireDate - today);
					const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));

					if (diffDays <= 7) {
						return 'warning';
					} else {
						return 'success';
					}
				}
			},

			// 执行护理服务
			executeNursing(item) {
				if (item.nurseNumber <= 0) {
					return;
				}

				const maturityDate = new Date(item.maturityTime);
				const today = new Date();
				today.setHours(0, 0, 0, 0);

				if (maturityDate < today) {
					return;
				}

				// 调用更新护理项目余量的API
				updateNurseItem({
						customerId: this.drawer.customerId,
						itemId: item.id,
						levelId: this.drawer.levelId,
						// 传递护理数量，用于批量扣减
						count: this.recordForm.nursingCount || 1
					})
					.then(res => {
						if (res && res.flag) {
							// 更新本地列表中的余量
							const index = this.drawer.allItems.findIndex(i => i.id === item.id);
							if (index !== -1) {
								this.drawer.allItems[index].nurseNumber -= this.recordForm.nursingCount || 1;
							}
							// 重新计算当前页数据
							this.updatePagedItems();
						}
					})
					.catch(error => {
						console.error('执行护理服务失败:', error);
					});
			},

			// 确认添加护理项目
			isOk() {
				const customerItemList = this.drawer.allItems;

				if (!this.drawer.levelId) {
					return;
				}

				if (!Array.isArray(customerItemList) || customerItemList.length === 0) {
					return;
				}

				// 验证每个项目的必填字段
				for (let i = 0; i < customerItemList.length; i++) {
					const item = customerItemList[i];

					if (!item.nurseNumber || item.nurseNumber <= 0) {
						return;
					}

					if (!item.maturityTime) {
						return;
					}

					// 检查到期日期是否在今天之后
					const today = new Date();
					today.setHours(0, 0, 0, 0);
					const maturityDate = new Date(item.maturityTime);

					if (maturityDate <= today) {
						return;
					}
				}

				// 准备提交数据
				const paramsArr = customerItemList.map(item => ({
					itemId: item.id,
					buyTime: item.buyTime || this.formatDate(new Date()),
					customerId: this.drawer.customerId,
					nurseNumber: item.nurseNumber,
					isDeleted: 0,
					maturityTime: item.maturityTime,
					levelId: this.drawer.levelId
				}));

				addItemToCustomer({
						customerId: this.drawer.customerId,
						items: paramsArr
					})
					.then(res => {
						if (res && res.flag) {
							this.listKhxxPage();
							this.handleClose();
						}
					})
					.catch(error => {
						console.error('提交失败:', error);
					});
			},

			// 查询客户信息列表-分页
			listKhxxPage() {
				this.loading = true;

				const requestParams = {
					customerName: this.condition.customerName,
					pageNum: this.condition.currentPage,
					pageSize: this.condition.pageSize,
					hasLevel: true
				};

				listKhxxPage(requestParams)
					.then(res => {
						if (!res || !res.data) {
							console.error('接口返回格式不正确:', res);
							this.khxxList = [];
							this.page.total = 0;
							this.page.filteredTotal = 0;
							return;
						}

						const rawRecords = res.data.records || [];
						const validRecords = rawRecords.filter(item => item.levelId != null && item.levelId !== '');

						this.khxxList = validRecords;
						this.page.total = res.data.total || 0;
						this.page.filteredTotal = validRecords.length;
						this.page.pageSize = res.data.size || this.condition.pageSize;
						this.page.pagCount = Math.ceil(this.page.filteredTotal / this.page.pageSize);

						if (res.data.current !== undefined) {
							this.page.currentPage = res.data.current;
							this.condition.currentPage = res.data.current;
						}

						// 处理过滤后当前页无数据的情况
						if (this.khxxList.length === 0 && this.page.currentPage > 1) {
							this.page.currentPage -= 1;
							this.condition.currentPage = this.page.currentPage;
							this.listKhxxPage();
							return;
						}
					})
					.catch(error => {
						console.error('分页查询失败:', error);
						this.khxxList = [];
						this.page.total = 0;
						this.page.filteredTotal = 0;
					})
					.finally(() => {
						this.loading = false;
					});
			},

			// 查询当前级别护理项目
			listNurseItemByLevel() {
				if (!this.drawer.levelId) {
					this.drawer.allItems = [];
					this.drawer.isInLevelItemList = [];
					return;
				}

				return listNurseItemByLevel({
						levelId: this.drawer.levelId
					})
					.then(res => {
						const data = res.data || {};
						const newArr = Array.isArray(data.data) ? data.data : [];

						// 处理返回的护理项目数据
						this.drawer.allItems = newArr.map(item => {
							const now = new Date();
							const buyDate = new Date();
							const maturityDate = new Date();
							maturityDate.setMonth(maturityDate.getMonth() + 3);

							return {
								...item,
								nurseNumber: item.nurseNumber || 1,
								buyTime: this.formatDate(buyDate),
								maturityTime: this.formatDate(maturityDate)
							};
						});

						// 重置分页并计算当前页数据
						this.updatePagedItems();
					})
					.catch(error => {
						console.error('获取护理项目失败:', error);
						this.drawer.allItems = [];
						this.drawer.isInLevelItemList = [];
					});
			},

			// 抽屉分页切换
			handleDrawerCurrentChange(curPage) {
				this.drawer.currentPage = curPage;
				this.updatePagedItems();
			},

			// 计算当前页数据
			updatePagedItems() {
				const {
					allItems,
					currentPage,
					pageSize
				} = this.drawer;
				const startIndex = (currentPage - 1) * pageSize;
				const endIndex = startIndex + pageSize;
				this.drawer.isInLevelItemList = allItems.slice(startIndex, endIndex);
			},

			// 日期格式化工具方法
			formatDate(date) {
				if (!date) return '';
				if (!(date instanceof Date)) {
					date = new Date(date);
				}
				return `${date.getFullYear()}-${(date.getMonth()+1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')}`;
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
		display: flex;
		flex-direction: column;
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
		flex: 1;
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

	/* 加载中样式 */
	.loading {
		text-align: center;
		padding: 50px 0;
		color: #606266;
	}

	/* 抽屉样式 */
	.drawer-header {
		padding: 15px 20px;
		height: auto !important;
		background-color: #f5f7fa;
	}

	.drawer-main {
		padding: 15px;
		overflow: auto;
	}

	.customer-info {
		font-size: 15px;
		color: #303133;
	}

	.customer-name {
		font-weight: 500;
	}

	.drawer-empty {
		text-align: center;
		padding: 30px 0;
		color: #909399;
	}

	.demo-drawer__footer {
		padding: 15px 20px;
		text-align: right;
		border-top: 1px solid #e8e8e8;
	}

	/* 护理记录表单样式 */
	.record-form-wrapper {
		padding: 20px;
		height: calc(100% - 60px);
		overflow-y: auto;
	}

	.record-form {
		width: 100%;
	}

	.record-form .el-form-item {
		margin-bottom: 20px;
		align-items: center;
	}

	/* 统一输入框长度（与日期选择器保持一致） */
	.uniform-input {
		width: 220px;
		/* 与日期时间选择器默认宽度匹配 */
	}

	/* 可调整大小的文本框 */
	.resizable-textarea {
		resize: vertical;
		min-height: 120px;
	}

	/* 护理记录表单按钮区域 */
	.record-form-actions {
		padding: 15px 20px;
		text-align: right;
		border-top: 1px solid #e8e8e8;
		display: flex;
		justify-content: flex-end;
		gap: 10px;
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

		.operation-btn {
			padding: 4px 8px;
			font-size: 12px;
			height: 32px;
		}

		.record-form-wrapper {
			padding: 10px;
		}

		.record-form .el-form-item {
			margin-bottom: 15px;
		}

		/* 响应式调整输入框宽度 */
		.uniform-input {
			width: 100%;
		}
	}
</style>