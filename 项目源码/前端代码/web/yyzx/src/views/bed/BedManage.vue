<template>
	<div class="common-layout">
		<el-container>
			<el-header>
				<div>
					<el-row :gutter="30">
						<el-col :span="5">
							<el-form-item label="姓名: ">
								<el-input placeholder="请输入客户姓名" clearable v-model="queryParams.customerName" @clear="query" />
							</el-form-item>
						</el-col>
						<el-col :span="8">
							<el-form-item label="入住日期: ">
								<el-date-picker value-format="YYYY-MM-DD" format="YYYY/MM/DD" v-model="dateChange"
									type="daterange" unlink-panels range-separator="至" start-placeholder="开始日期"
									end-placeholder="结束日期"></el-date-picker>
							</el-form-item>
						</el-col>
						<el-col :span="6">
							<el-button type="primary" @click="query" color="#337ab7" icon="Search">查询</el-button>
						</el-col>
					</el-row>
				</div>
			</el-header>
			<el-divider style="margin:0"></el-divider>
			<el-main>
				<div>
					<el-row class="mb-4">
						<el-button :class="[{activeBtn: btnFlag}]" style="border: 1px white solid;margin-left: 0px"
							@click="doing">正在使用</el-button>
						<el-button :class="[{activeBtn: !btnFlag}]" style="border: 1px white solid;margin-left: 0px"
							@click="history">使用历史</el-button>
					</el-row>
				</div>
				<div>
					<!-- 表格 -->
					<el-table :data="bedDetailsList" style="width: 100%;color:black;" stripe>
						<el-table-column align="center" type="index" :index="indexMethod" label="序号" width="60">
						</el-table-column>
						<el-table-column align="center" prop="customerName" label="客户姓名" width="120" />
						<el-table-column align="center" prop="customerSex" label="性别" width="100">
							<template #default="scope">{{ scope.row.customerSex === 0 ? '男' : '女' }}</template>
						</el-table-column>
						<el-table-column align="center" prop="bedDetails" label="床位详情" width="120" />
						<!-- <el-table-column align="center" prop="roomNo" label="房间号" width="120" /> -->
						<el-table-column align="center" prop="startDate" label="床位使用起始日期" width="150" />
						<el-table-column align="center" prop="endDate" label="床位使用结束日期" width="150" />
						<el-table-column align="center" fixed="right" label="操作" width="220">
							<template #default="scope">
								<el-button type="warning" link icon="Switch" v-if="btnFlag" size="small"
									@click="exchange(scope.row)">床位调换</el-button>
								<el-button type="primary" icon="Edit" link size="small" v-if="btnFlag"
									@click="editMessage(scope.row)">修改</el-button>
								<el-button type="danger" icon="Delete" size="small" v-if="!btnFlag"
									@click="del(scope.row.id)">删除</el-button>
							</template>
						</el-table-column>
					</el-table>
				</div>
				<!-- 分页插件 -->
				<div style="margin-top:15px">
					<!--
					page-size:每页显示条目个数
					current-page:页码 配合@current-change使用
					disabled:是否禁用分页
					background:是否为分页按钮添加背景色
					layout：组件布局，子组件名用逗号分隔
					total:总条目数
					-->
					<el-pagination :page-size="page.pageSize" background :current-page="page.currentPage"
						layout="prev, pager, next" :total="page.total" @current-change="handleCurrentChange">
					</el-pagination>
				</div>
			</el-main>

			<!--修改对话框 -->
			<el-dialog v-model="edit.dialogVisible" :before-close="editclose" title="信息修改" width="60%" align-center
				draggable>
				<el-divider border-style="double" style="margin:0;" />
				<el-form label-position="right" label-width="auto" style="max-width:800px;margin:20px auto"
					class="demo-form-inline" :inline="true" :model="edit.bedDetalsForm" :rules="rules" ref="editForm">
					<el-form-item label="客户姓名:" prop="customerName">
						<el-input readonly v-model="edit.bedDetalsForm.customerName" />
					</el-form-item>
					<el-form-item label="性别: " prop="customerSex">
						<el-input readonly v-model="edit.bedDetalsForm.customerSex"
							:value="edit.bedDetalsForm.customerSex === 0 ? '男' : '女'" />
					</el-form-item>
					<!-- <el-form-item label="房间号:" prop="roomNo">
						<el-input readonly v-model="edit.bedDetalsForm.roomNo" />
					</el-form-item> -->
					<el-form-item label="床位详情:" prop="bedDetails">
						<el-input readonly v-model="edit.bedDetalsForm.bedDetails" />
					</el-form-item>
					<el-form-item label="床位使用起始日期:" prop="startDate">
						<el-input readonly v-model="edit.bedDetalsForm.startDate" />
					</el-form-item>
					<el-form-item label="床位使用结束日期:" prop="endDate">
						<el-date-picker v-model="edit.bedDetalsForm.endDate" style="width:200px" format="YYYY/MM/DD"
							value-format="YYYY-MM-DD" type="date" placeholder="选择日期"></el-date-picker>
					</el-form-item>
				</el-form>
				<el-divider border-style="double" style="margin:0;" />
				<template #footer>
					<span class="dialog-footer" style="padding-top: 0px">
						<el-button type="primary" @click="editSave('editForm')">保存</el-button>
						<el-button @click="editCancel">取消</el-button>
					</span>
				</template>
			</el-dialog>

			<!-- 床位调换对话框 -->
			<el-dialog v-model="exchangeData.dialogVisible" title="床位调换" width="60%" :before-close="exchangeClose">
				<el-divider border-style="double" style="margin:0;" />
				<el-form label-position="right" label-width="auto" style="max-width:800px;margin:20px auto"
					class="demo-form-inline" :inline="true" :model="exchangeData.exchangeForm" ref="exchangeForm">
					<el-form-item label="客户姓名:" prop="customerName">
						<el-input readonly v-model="exchangeData.exchangeForm.customerName" />
					</el-form-item>
					<el-form-item label="床位详情" prop="bedDetails">
						<el-input readonly v-model="exchangeData.exchangeForm.bedDetails" />
					</el-form-item>
					<el-form-item label="性别: " prop="customerSex">
						<el-input readonly v-model="exchangeData.exchangeForm.customerSex"
							:value="exchangeData.exchangeForm.customerSex === 0 ? '男' : '女'" />
					</el-form-item>
					<el-form-item label="新楼号" prop="buildingNo">
						<el-input readonly v-model="exchangeData.exchangeForm.buildingNo" />
					</el-form-item>

					<!-- <el-form-item label="旧房间号" prop="oldRoomNo">
						<el-input readonly v-model="exchangeData.exchangeForm.oldRoomNo" />
					</el-form-item> -->

					<el-form-item label="新房号" prop="newRoomNo">
						<el-select v-model="exchangeData.exchangeForm.newRoomNo" style="width:200px"
							placeholder="请选择房间号" @change="getBed">
							<el-option-group v-for="group in exchangeData.roomList" :key="group.label"
								:label="group.label">
								<el-option v-for="item in group.options" :key="item.roomNo" :label="item.roomNo"
									:value="item.roomNo">
								</el-option>
							</el-option-group>
						</el-select>
					</el-form-item>

					<el-form-item label="当前床位使用起始日期:" prop="startDate">
						<el-input readonly v-model="exchangeData.exchangeForm.startDate" />
					</el-form-item>

					<el-form-item label="新床号" prop="newBedId">
						<el-select style="width:200px" v-model="exchangeData.exchangeForm.newBedId" placeholder="请选择床位">
							<el-option v-for="item in exchangeData.bedList" :key="item.bedNo" :label="item.bedNo"
								:value="item.id">
							</el-option>
						</el-select>
					</el-form-item>

					<el-form-item label="当前床位使用结束时间:" prop="endDate">
						<el-input readonly v-model="exchangeData.exchangeForm.endDate" />
					</el-form-item>
				</el-form>
				<el-divider border-style="double" style="margin:0;" />
				<template #footer>
					<span class="dialog-footer" style="padding-top: 0px">
						<el-button type="primary" @click="exchangeSave">保存</el-button>
						<el-button @click="exchangeCancel">取消</el-button>
					</span>
				</template>
			</el-dialog>
			<!-- 床位调换对话框 -->
			<el-dialog v-model="exchangeData.dialogVisible" title="床位调换" width="60%" :before-close="exchangeClose">
				<el-divider border-style="double" style="margin:0;" />
				<el-form label-position="right" label-width="auto" style="max-width:800px;margin:20px auto"
					class="demo-form-inline" :inline="true" :model="exchangeData.exchangeForm" ref="exchangeForm">
					<el-form-item label="客户姓名:" prop="customerName">
						<el-input readonly v-model="exchangeData.exchangeForm.customerName" />
					</el-form-item>
					<el-form-item label="床位详情" prop="bedDetails">
						<el-input readonly v-model="exchangeData.exchangeForm.bedDetails" />
					</el-form-item>
					<el-form-item label="性别: " prop="customerSex">
						<el-input readonly v-model="exchangeData.exchangeForm.customerSex"
							:value="exchangeData.exchangeForm.customerSex === 0 ? '男' : '女'" />
					</el-form-item>
					<el-form-item label="新楼号" prop="buildingNo">
						<el-input readonly v-model="exchangeData.exchangeForm.buildingNo" />
					</el-form-item>

					<!-- <el-form-item label="旧房间号" prop="oldRoomNo">
						<el-input readonly v-model="exchangeData.exchangeForm.oldRoomNo" />
					</el-form-item> -->

					<el-form-item label="新房号" prop="newRoomNo">
						<el-select v-model="exchangeData.exchangeForm.newRoomNo" style="width:200px"
							placeholder="请选择房间号" @change="getBed">
							<el-option-group v-for="group in exchangeData.roomList" :key="group.label"
								:label="group.label">
								<el-option v-for="item in group.options" :key="item.roomNo" :label="item.roomNo"
									:value="item.roomNo">
								</el-option>
							</el-option-group>
						</el-select>
					</el-form-item>

					<el-form-item label="当前床位使用起始日期:" prop="startDate">
						<el-input readonly v-model="exchangeData.exchangeForm.startDate" />
					</el-form-item>

					<el-form-item label="新床号" prop="newBedId">
						<el-select style="width:200px" v-model="exchangeData.exchangeForm.newBedId" placeholder="请选择床位">
							<el-option v-for="item in exchangeData.bedList" :key="item.bedNo" :label="item.bedNo"
								:value="item.id">
							</el-option>
						</el-select>
					</el-form-item>

					<el-form-item label="当前床位使用结束时间:" prop="endDate">
						<el-input readonly v-model="exchangeData.exchangeForm.endDate" />
					</el-form-item>
				</el-form>
				<el-divider border-style="double" style="margin:0;" />
				<template #footer>
					<span class="dialog-footer" style="padding-top: 0px">
						<el-button type="primary" @click="exchangeSave">保存</el-button>
						<el-button @click="exchangeCancel">取消</el-button>
					</span>
				</template>
			</el-dialog>
		</el-container>
	</div>
</template>

<script>
	import {
		findBedDetailsList,
		updateDetailsStartDate,
		findBedByRoom,
		exchangeBed,
		delBedDetails
	} from '@/api/bedApi.js'
	import {
		listRoom
	} from '@/api/roomApi.js'

	export default {
		computed: {
			indexMethod() {
				return (this.page.currentPage - 1) * this.page.pageSize + 1;
			}
		},
		mounted() {
			// 获取表格数据
			this.getBedDetailsList();
		},
		data() {
			return {
				queryParams: {
					customerName: '',
					startDate: '',
					endDate: '',
					pageSize: 1, // 默认第一页
					isDeleted: '0' // 显示标志0-生效床位信息1-失效床位信息(历史记录)
				},
				dateChange: [], // 日期区间
				bedDetailsList: [], // 信息列表
				// 分页属性封装
				page: {
					total: 0,
					pageSize: 10,
					currentPage: 1,
					pagCount: 0
				},
				// 编辑模态框model
				edit: {
					dialogVisible: false,
					bedDetalsForm: {
						id: '',
						customerName: '',
						customerSex: '',
						bedDetails: '',
						roomNo: '',
						startDate: '',
						endDate: ''
					}
				},
				// 床位调换模态框model
				exchangeData: {
					dialogVisible: false,
					exchangeForm: {
						// 展示属性
						id: '',
						customerName: '',
						customerSex: '',
						bedDetails: '',
						oldRoomNo: '',
						startDate: '',
						endDate: '',
						// 提交参数属性
						customerId: '',
						buildingNo: '606',
						newRoomNo: '',
						newBedId: '',
						oldBedId: ''
					},
					roomList: [], // 房间列表
					bedList: [] // 床位列表
				},
				rules: {
					endDate: [{
						required: true,
						message: '选择床位使用结束时间',
						trigger: 'change'
					}]
				},
				exchangeRules: {
					newRoomNo: [{
						required: true,
						message: '请选择新房间',
						trigger: 'change'
					}],
					newBedId: [{
						required: true,
						message: '请选择新床位',
						trigger: 'change'
					}],
					endDate: [{
						required: true,
						message: '请选择调换日期',
						trigger: 'change'
					}]
				},
				btnFlag: true,
				loading: false // 加载状态
			};
		},
		methods: {
			// 初始化查询条件
			reset() {
				this.queryParams.startDate = '';
				this.queryParams.endDate = '';
				this.queryParams.customerName = '';
				this.queryParams.pageSize = 1;
				this.queryParams.isDeleted = '0';
			},
			// 点击查询
			query() {
				if (this.dateChange && this.dateChange.length > 0) {
					// 如果输入了日期
					this.queryParams.startDate = this.dateChange[0];
					this.queryParams.endDate = this.dateChange[1];
				} else {
					// 清除日期后要初始化日期条件
					this.queryParams.startDate = '';
					this.queryParams.endDate = '';
				}
				this.queryParams.pageSize = 1; // 回到第一页
				// 重载表格
				this.getBedDetailsList();
			},
			// 选中页码
			handleCurrentChange(curPage) {
				this.page.currentPage = curPage;
				this.queryParams.pageSize = curPage; // 参数pageSize是服务端接收页码参数名
				// 重新渲染表格
				this.getBedDetailsList();
			},
			// 正在使用
			doing() {
				this.btnFlag = true;
				this.queryParams.isDeleted = '0'; // 0-生效床位信息
				this.queryParams.pageSize = 1; // 回到第一页
				this.getBedDetailsList();
			},
			// 使用历史
			history() {
				this.btnFlag = false;
				this.queryParams.isDeleted = '1'; // 1-失效床位信息(历史记录)
				this.queryParams.pageSize = 1; // 回到第一页
				this.getBedDetailsList();
			},
			// 点击编辑
			editMessage(row) {
				// 显示模态框
				this.edit.dialogVisible = true;
				this.$nextTick(() => {
					this.edit.bedDetalsForm.id = row.id;
					this.edit.bedDetalsForm.customerName = row.customerName;
					this.edit.bedDetalsForm.customerSex = row.customerSex;
					this.edit.bedDetalsForm.bedDetails = row.bedDetails;
					this.edit.bedDetalsForm.roomNo = row.roomNo;
					this.edit.bedDetalsForm.startDate = row.startDate;
					this.edit.bedDetalsForm.endDate = row.endDate;
				});
			},
			// 重置表单
			resetForm(formName) {
				if (this.$refs[formName]) {
					this.$refs[formName].resetFields();
				}
			},
			// 编辑模态框关闭
			editclose() {
				this.edit.dialogVisible = false;
				this.resetForm("editForm"); // 重置表单
			},
			// 编辑取消
			editCancel() {
				this.editclose();
			},
			// 床位调换模态框关闭
			exchangeClose() {
				this.exchangeData.dialogVisible = false;
				this.resetForm("exchangeForm"); // 重置表单
				this.exchangeData.bedList = [];
				this.exchangeData.exchangeForm.newBedId = '';
			},
			// 点击床位调换
			exchange(row) {
				console.log('点击床位调换', row);
				this.loading = true;
				this.exchangeData.dialogVisible = true;
				// 初始化表单
				this.$nextTick(() => {
					this.exchangeData.exchangeForm = {
						...this.exchangeData.exchangeForm,
						id: row.id,
						customerName: row.customerName,
						customerSex: row.customerSex,
						bedDetails: row.bedDetails,
						oldRoomNo: row.roomNo,
						startDate: row.startDate,
						endDate: row.endDate,
						customerId: row.customerId,
						oldBedId: row.bedId,
						newRoomNo: '',
						newBedId: ''
					};
				});
				// 获取房间列表
				this.getRoomList().finally(() => {
					this.loading = false;
				});
			},
			// 取消床位调换
			exchangeCancel() {
				this.exchangeClose();
			},
			// api-删除历史记录
			del(id) {
				this.$confirm('此操作删除记录，是否继续?', '提示', {
					confirmButtonText: '确定',
					cancelButtonText: '取消',
					type: 'warning',
				}).then(() => {
					delBedDetails({
						id: id
					}).then(res => {
						if (res.flag) {
							this.$message.success(res.message);
							// 重载表格
							this.getBedDetailsList();
						} else {
							this.$message.error(res.message);
						}
					})
				}).catch(() => {});
			},
			// api点击床位调换保存
			exchangeSave() {
				this.$refs.exchangeForm.validate(valid => {
					if (valid) {
						this.loading = true;
						exchangeBed(this.exchangeData.exchangeForm).then(res => {
							if (res.flag) {
								this.$message.success(res.message);
								this.exchangeClose(); // 关闭模态框
								// 重载表格
								this.getBedDetailsList();
							} else {
								this.$message.error(res.message);
							}
						}).catch(error => {
							console.error('床位调换失败', error);
							this.$message.error('床位调换失败，请稍后再试');
						}).finally(() => {
							this.loading = false;
						});
					} else {
						this.$message.error("请填写完整调换信息");
						return false;
					}
				});
			},
			getRoomList() {
				return listRoom().then(res => {
					if (res.flag) { // 将res.success改为res.flag
						this.exchangeData.roomList = []; // 先清空原有数据

						// 按楼层分组
						const floor1Rooms = res.data.filter(room => room.roomFloor === "一楼");
						const floor2Rooms = res.data.filter(room => room.roomFloor === "二楼");

						// 构建分组数据
						this.exchangeData.roomList = [{
								label: "一楼",
								options: floor1Rooms,
								disabled: true
							},
							{
								label: "二楼",
								options: floor2Rooms,
								disabled: true
							}
						];
					} else {
						this.$message.error(res.message || '获取房间列表失败');
					}
				}).catch(error => {
					console.error('获取房间列表失败', error);
					this.$message.error('获取房间列表失败，请稍后再试');
				});
			},
			// api-根据选择的房间查询床位
			getBed() {
				if (!this.exchangeData.exchangeForm.newRoomNo) {
					this.exchangeData.bedList = [];
					this.exchangeData.exchangeForm.newBedId = '';
					return;
				}

				this.loading = true;
				findBedByRoom({
					bedStatus: 1,
					roomNo: this.exchangeData.exchangeForm.newRoomNo
				}).then(res => {
					this.exchangeData.bedList = res.data || [];
				}).catch(error => {
					console.error('获取床位列表失败', error);
					this.$message.error('获取床位列表失败，请稍后再试');
				}).finally(() => {
					this.loading = false;
				});
			},
			// api-保存修改
			editSave(formName) {
				this.$refs[formName].validate(valid => {
					if (valid) {
						updateDetailsStartDate(this.edit.bedDetalsForm).then(res => {
							if (res.flag) {
								this.$message.success(res.message);
								this.editclose(); // 关闭模态框
								// 重载表格
								this.getBedDetailsList();
							} else {
								this.$message.error(res.message);
							}
						});
					} else {
						return false;
					}
				});
			},
			// api-查询床位详情信息(分页)
			getBedDetailsList() {
				this.loading = true;
				findBedDetailsList(this.queryParams).then(res => {
					this.bedDetailsList = res.data.records;
					this.page.total = res.data.total; // 总记录数
					this.page.pageSize = res.data.size; // 每页显示条数
					this.page.currentPage = res.data.current; // 当前页码
					this.page.pagCount = res.data.pages; // 总页数
				}).catch(error => {
					console.error('获取床位详情失败', error);
					this.$message.error('获取床位详情失败，请稍后再试');
				}).finally(() => {
					this.loading = false;
				});
			}
		}
	};
</script>

<style scoped>
	/* 基础布局样式 */
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

	.el-main {
		padding: 15px;
		flex: 1;
		overflow: auto;
		background-color: #f5f7fa;
	}

	/* 查询区域样式 */
	.el-row {
		margin-bottom: 15px;
	}

	.el-form-item {
		margin-bottom: 0;
	}

	.el-input,
	.el-select {
		border-radius: 4px;
		transition: border-color 0.3s ease, box-shadow 0.3s ease;
	}

	.el-input:focus,
	.el-select:focus {
		border-color: #409EFF;
		box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
	}

	.el-button {
		border-radius: 4px;
		transition: all 0.3s ease;
		font-weight: 500;
		padding: 6px 12px;
		/* 统一按钮内边距 */
	}

	/* 放大床位调换和修改按钮尺寸 */
	.el-button--warning,
	.el-button--primary,
	.el-button--danger {
		padding: 8px 16px;
		/* 增加内边距以放大按钮 */
		font-size: 13px;
		/* 微调字体大小 */
	}

	.el-button:hover {
		transform: translateY(-2px);
		box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
	}

	/* 状态切换按钮样式 */
	.mb-4 {
		margin-bottom: 15px;
	}

	.activeBtn {
		color: #1890ff;
		background: rgba(232, 244, 255, 1);
		border: 1px solid #1890ff !important;
		box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.2);
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

	.el-table td {
		color: #303133;
		padding: 12px 10px;
	}

	.el-table--striped .el-table__body tr.el-table__row--striped {
		background-color: #f9fafc;
	}

	.el-table__row:hover>td {
		background-color: #ecf5ff !important;
	}

	/* 分页样式 */
	.el-pagination {
		margin-top: 15px;
		text-align: right;
	}

	/* 对话框样式 */
	.el-dialog {
		border-radius: 8px;
		box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
	}

	.el-dialog__header {
		padding: 15px 20px;
		background-color: #f5f7fa;
		border-bottom: 1px solid #ebeef5;
	}

	.el-dialog__title {
		font-weight: 600;
		color: #303133;
	}

	.el-dialog__body {
		padding: 20px;
	}

	.el-divider {
		margin: 10px 0;
		color: #ebeef5;
	}

	/* 表单样式 */
	.demo-form-inline {
		padding: 10px 0;
	}

	.el-form-item {
		margin-bottom: 15px;
	}

	.el-form-item__label {
		font-weight: 500;
	}

	/* 响应式设计 */
	@media (max-width: 768px) {
		.el-col {
			width: 100% !important;
			margin-bottom: 10px;
		}

		.el-table__header-wrapper,
		.el-table__body-wrapper {
			overflow-x: auto;
		}

		.el-dialog {
			width: 90% !important;
		}

		/* 小屏幕下调整按钮尺寸 */
		.el-button--warning,
		.el-button--primary,
		.el-button--danger {
			padding: 6px 12px;
			font-size: 12px;
		}
	}

	/* 自定义按钮颜色 - 床位调换按钮（棕色） */
	.el-button--warning {
		color: #8B4513;
		/* 棕色文字 */
		border-color: #D2B48C;
		/* 浅棕色边框 */
		background-color: #FFF8E7;
		/* 浅棕色背景 */
	}

	.el-button--warning:hover {
		color: #6D3B16;
		/* 深棕色悬停文字 */
		border-color: #CD853F;
		/* 深棕色悬停边框 */
		background-color: #F8E8D0;
		/* 略深的棕色背景 */
	}

	/* 自定义按钮颜色 - 修改按钮（蓝色） */
	.el-button--primary {
		color: #0050B3;
		/* 蓝色文字 */
		border-color: #409EFF;
		/* 蓝色边框 */
		background-color: #E6F7FF;
		/* 浅蓝色背景 */
	}

	.el-button--primary:hover {
		color: #0039A6;
		/* 深蓝色悬停文字 */
		border-color: #3A8EE6;
		/* 深蓝色悬停边框 */
		background-color: #BAE1FF;
		/* 中蓝色背景 */
	}

	/* 自定义按钮颜色 - 删除按钮（红色文字，透明背景） */
	.el-button--danger {
		color: #C12E2A;
		/* 红色文字 */
		border-color: #F56C6C;
		/* 浅红色边框 */
		background-color: transparent;
		/* 透明背景 */
	}

	.el-button--danger:hover {
		color: #9E1F1B;
		/* 深红色悬停文字 */
		border-color: #E53935;
		/* 深红色悬停边框 */
		background-color: #FFF0F0;
		/* 极浅的红色背景 */
	}
</style>