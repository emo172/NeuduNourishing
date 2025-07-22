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
							<!-- 登记按钮 -->
							<el-col :span="3" class="btn-col">
								<el-form-item>
									<el-button type="primary" @click="dj" color="#337ab7" class="operation-btn">
										<el-icon>
											<Plus />
										</el-icon>
										<span>登记</span>
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
					<!-- 老人类型筛选按钮 -->
					<el-row class="mb-4">
						<el-button :class="[{activeBtn: btnFlag1}]" class="filter-btn"
							@click="selfCareMan">自理老人</el-button>
						<el-button :class="[{activeBtn: btnFlag2}]" class="filter-btn" @click="careMan">护理老人</el-button>
					</el-row>
				</div>

				<!-- 客户信息表格 -->
				<div>
					<el-table :data="khxxList" style="width: 100% ;color:black;" stripe>
						<el-table-column align="center" fixed type="index" :index="indexMethod" label="序号" width="60" />
						<el-table-column align="center" prop="customerName" label="客户姓名" width="100" />
						<el-table-column align="center" prop="bloodType" label="血型" width="60" />
						<el-table-column align="center" prop="contactTel" label="联系电话" width="150" />
						<el-table-column align="center" prop="customerAge" label="年龄" width="60" />
						<el-table-column align="center" prop="customerSex" label="性别" width="60">
							<template #default="scope">{{ scope.row.customerSex==0?'男':'女'}}</template>
						</el-table-column>
						<el-table-column align="center" prop="familyMember" label="家属" width="100" />
						<el-table-column align="center" prop="idcard" label="身份证号" width="180" />
						<el-table-column align="center" prop="buildingNo" label="所属楼房" width="80" />
						<el-table-column align="center" prop="roomNo" label="房间号" width="80" />
						<el-table-column align="center" prop="bedNo" label="床号" width="80" />
						<el-table-column align="center" prop="birthday" label="出生日期" width="150" />
						<el-table-column align="center" prop="checkinDate" label="入住时间" width="150" />
						<el-table-column align="center" prop="expirationDate" label="合同到期时间" width="150" />
						<el-table-column align="center" prop="levelName" label="护理级别" width="80" />
						<el-table-column align="center" prop="nickName" label="健康管家(护工)" width="120" />
						<el-table-column align="center" prop="psychosomaticState" label="身心状况" width="200" />
						<el-table-column align="center" fixed="right" label="操作" width="180">
							<template #default="scope">
								<!-- 操作按钮容器，使用flex布局确保按钮在同一行 -->
								<div class="operation-buttons">
									<el-button type="primary" icon="Edit" link v-if="scope.row.isDeleted==0"
										style="color: #337ab7; border-color: #337ab7;"
										@click="edit(scope.row)">修改</el-button>
									<el-button type="danger" link size="small"
										v-if="scope.row.isDeleted==1">逻辑上已删除,只查询不操作</el-button>
									<el-button type="danger" icon="Delete" link v-if="scope.row.isDeleted==0"
										@click="del(scope.row)">删除</el-button>
								</div>
							</template>
						</el-table-column>
					</el-table>
				</div>

				<!-- 分页组件 -->
				<div style="margin-top:15px">
					<el-pagination :page-size="page.pageSize" background :current-page="page.currentPag"
						layout=" prev, pager, next" :total="page.total" @current-change="handleCurrentChange" />
				</div>
			</el-main>

			<!-- 入住登记/编辑模态框 -->
			<el-dialog v-model="djDialog.dialogVisible" :title="djDialog.tops" width="60%" align-center draggable
				:before-close="djhandleClose">
				<el-divider border-style="double" style="margin:0;" />
				<el-form label-position="right" label-width="auto" style="max-width:800px;margin:20px auto"
					class="demo-form-inline" ref="customerForm" :inline="true" :model="djDialog.customerForm"
					:rules="rules">
					<el-form-item label="客户姓名：" prop="customerName">
						<el-input placeholder="输入[客户姓名]" v-model="djDialog.customerForm.customerName" />
					</el-form-item>
					<el-form-item label="楼栋： " prop="buildingNo">
						<el-input readonly v-model="djDialog.customerForm.buildingNo" />
					</el-form-item>
					<el-form-item label="年龄：" prop="customerAge">
						<el-input v-model="djDialog.customerForm.customerAge" />
					</el-form-item>

					<el-form-item label="房间号： " prop="roomNo">
						<el-input readonly v-if="isShow" v-model="djDialog.customerForm.roomNo" />
						<el-select v-if="!isShow" v-model="djDialog.customerForm.roomNo" style="width:200px"
							@change="getBed" placeholder="请选择房间号">
							<el-option-group v-for="group in djDialog.roomList" :key="group.label" :label="group.label">
								<el-option v-for="item in group.options" :key="item.roomNo" :label="item.roomNo"
									:value="item.roomNo"></el-option>
							</el-option-group>
						</el-select>
					</el-form-item>

					<el-form-item label="性别：" prop="customerSex">
						<el-select v-model="djDialog.customerForm.customerSex" style="width:200px" placeholder="性别">
							<el-option v-for="item in djDialog.sex" :key="item.value" :label="item.label"
								:value="item.value" />
						</el-select>
					</el-form-item>
					<el-form-item label="床位号： " prop="bedId">
						<el-input readonly v-if="isShow" v-model="djDialog.customerForm.bedId" />
						<el-select v-if="!isShow" style="width:200px" v-model="djDialog.customerForm.bedId"
							placeholder="请选择床位">
							<el-option v-for="item in djDialog.bedList" :key="item.bedNo" :label="item.bedNo"
								:value="item.id"></el-option>
						</el-select>
					</el-form-item>
					<el-form-item label="出生日期： " prop="birthday">
						<el-date-picker @change="getAge" style="width:200px" format="YYYY/MM/DD"
							value-format="YYYY-MM-DD" v-model="djDialog.customerForm.birthday" type="date"
							placeholder="选择日期"></el-date-picker>
					</el-form-item>
					<el-form-item label="入住时间： " prop="checkinDate">
						<el-date-picker style="width:200px" format="YYYY/MM/DD" value-format="YYYY-MM-DD"
							v-model="djDialog.customerForm.checkinDate" type="date" :disabled-date="disabledDate"
							placeholder="选择日期"></el-date-picker>
					</el-form-item>
					<!-- 身份证号输入项 -->
					<el-form-item label="身份证号：" prop="idcard">
						<el-input v-model="djDialog.customerForm.idcard" placeholder="请输入18位身份证号" />
					</el-form-item>
					<el-form-item label="合同到期时间： " prop="expirationDate">
						<el-date-picker style="width:200px" v-model="djDialog.customerForm.expirationDate" type="date"
							format="YYYY/MM/DD" value-format="YYYY-MM-DD" :disabled-date="disabledDate0"
							placeholder="选择日期"></el-date-picker>
					</el-form-item>
					<el-form-item label="血型：" prop="bloodType">
						<el-input v-model="djDialog.customerForm.bloodType" />
					</el-form-item>
					<el-form-item label="联系电话：" prop="contactTel">
						<el-input v-model="djDialog.customerForm.contactTel" />
					</el-form-item>
					<el-form-item label="家属：" prop="familyMember">
						<el-input v-model="djDialog.customerForm.familyMember" />
					</el-form-item>
					<el-form-item label="身心状况： " prop="psychosomaticState">
						<el-input v-model="djDialog.customerForm.psychosomaticState" />
					</el-form-item>
				</el-form>
				<el-divider border-style="double" style="margin:0;" />
				<template #footer>
					<span class="dialog-footer" style="padding-top: 0px">
						<el-button type="primary" @click="djsave('customerForm')" class="operation-btn">保存</el-button>
						<el-button v-if="!djDialog.customerForm.id" type="primary" @click="resetForm('customerForm')"
							class="operation-btn">重置</el-button>
						<el-button @click="djcancel" class="operation-btn">取消</el-button>
					</span>
				</template>
			</el-dialog>
		</el-container>
	</div>
</template>

<script>
	// 导入接口方法
	import {
		listRoom
	} from "@/api/roomApi.js";
	import {
		findBedByRoom
	} from "@/api/bedApi.js";
	import {
		addCustomer,
		listKhxxPage,
		removeCustomer,
		editKhxx
	} from "@/api/customerApi.js";
	// 导入图标组件
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
		// 计算属性
		computed: {
			// 计算表格序号（分页后正确显示序号）
			indexMethod() {
				return this.page.currentPag * this.page.pageSize - this.page.pageSize + 1
			}
		},
		// 数据定义
		data() {
			return {
				// 控制模态框中房间号/床位号是否只读（编辑时只读，新增时可编辑）
				isShow: false,
				// 模态框相关数据
				djDialog: {
					// 模态框显示状态
					dialogVisible: false,
					// 模态框标题
					tops: "",
					// 表单数据
					customerForm: {
						id: "",
						bedId: "",
						bloodType: "O",
						buildingNo: "606", // 固定楼栋为606
						contactTel: "",
						customerAge: "",
						customerName: "",
						customerSex: "0", // 默认性别为男
						roomNo: "",
						idcard: "", // 身份证号字段（与后端保持一致）
						psychosomaticState: "",
						familyMember: "", // 家属信息
						filepath: "@/assets/tou.png", // 固定头像路径
						birthday: "",
						checkinDate: "",
						expirationDate: ""
					},
					// 房间列表数据（用于下拉选择）
					roomList: [],
					// 床位列表数据（用于下拉选择）
					bedList: [],
					// 性别选项数据
					sex: [{
							value: "0",
							label: "男"
						},
						{
							value: "1",
							label: "女"
						}
					]
				},
				// 查询条件
				condition: {
					customerName: "", // 客户姓名查询条件
					pageNum: 1, // 当前页码
					pageSize: 10, // 每页条数
					manType: "1" // 老人类型（1-自理老人，2-护理老人，3-历史老人）
				},
				// 分页相关属性
				page: {
					total: 0, // 总条数
					pageSize: 10, // 每页条数
					currentPag: 1, // 当前页码
					pagCount: 0 // 总页数（未使用）
				},
				// 客户信息列表数据
				khxxList: [],
				// 表单校验规则
				rules: {
					bedId: [{
						required: true,
						message: "选择床位",
						trigger: "change"
					}],
					bloodType: [{
						required: true,
						message: "请输入血型",
						trigger: "blur"
					}],
					contactTel: [{
						required: true,
						message: "请输入手机号",
						trigger: "blur"
					}],
					customerAge: [{
						required: true,
						message: "请输入年龄",
						trigger: "blur"
					}],
					customerName: [{
						required: true,
						message: "请输入客户姓名",
						trigger: "blur"
					}],
					roomNo: [{
						required: true,
						message: "请选择房间号",
						trigger: "change"
					}],
					// 身份证号校验（必填+格式校验）
					idcard: [{
							required: true,
							message: "请输入身份证号",
							trigger: "blur"
						},
						{
							pattern: /(^\d{18}$)|(^\d{17}(\d|X|x)$)/,
							message: "请输入有效的18位身份证号",
							trigger: "blur"
						}
					],
					familyMember: [{
						required: true,
						message: "请输入家属",
						trigger: "blur"
					}],
					checkinDate: [{
						type: "date",
						required: true,
						message: "请选择时间",
						trigger: "change"
					}],
					expirationDate: [{
						type: "date",
						required: true,
						message: "请选择时间",
						trigger: "change"
					}]
				},
				// 按钮选中状态（自理老人按钮）
				btnFlag1: true,
				// 按钮选中状态（护理老人按钮）
				btnFlag2: false,
				// 预留历史老人按钮状态
				btnFlag3: false,
			};
		},
		// 监听器
		watch: {
			// 监听入住时间变化，控制合同到期时间的有效性
			"djDialog.customerForm": {
				handler(newvalue) {
					// 当入住时间为空或入住时间晚于到期时间时，清空到期时间
					if (!newvalue.checkinDate || newvalue.checkinDate > this.djDialog.customerForm.expirationDate) {
						this.djDialog.customerForm.expirationDate = "";
					}
				},
				// 深度监听对象内部属性变化
				deep: true
			}
		},
		// 方法定义
		methods: {
			// 入住时间限制（只能选择今天及以后的日期）
			disabledDate(time) {
				return time.getTime() < Date.now() - 1000 * 60 * 60 * 24;
			},
			// 合同到期时间限制（只能选择入住时间及以后的日期）
			disabledDate0(time) {
				let start = this.djDialog.customerForm.checkinDate;
				start = new Date(start)
				// 当入住时间有效时，到期时间不能早于入住时间
				if (start && start != "undifine") {
					return time.getTime() < start - 1000 * 60 * 60 * 24;
				}
				// 入住时间未选择时，到期时间只能选择今天及以后
				return time.getTime() < Date.now() - 1000 * 60 * 60 * 24;
			},
			// 打开入住登记模态框
			dj() {
				this.isShow = false; // 新增时房间号和床位号可编辑
				this.getRoomList(); // 获取房间列表
				this.djDialog.dialogVisible = true; // 显示模态框
				this.djDialog.tops = "入住登记"; // 设置标题
			},
			// 打开编辑模态框（传入当前行数据）
			edit(row) {
				this.isShow = true; // 编辑时房间号和床位号只读
				this.djDialog.dialogVisible = true; // 显示模态框
				this.djDialog.tops = "信息编辑"; // 设置标题
				// 等待DOM更新后再赋值（确保表单渲染完成）
				this.$nextTick(() => {
					// 为表单赋值（回显数据）
					this.djDialog.customerForm.id = row.id
					this.djDialog.customerForm.bedId = row.bedId
					this.djDialog.customerForm.birthday = row.birthday
					this.djDialog.customerForm.bloodType = row.bloodType
					this.djDialog.customerForm.buildingNo = row.buildingNo
					this.djDialog.customerForm.contactTel = row.contactTel
					this.djDialog.customerForm.customerAge = row.customerAge
					this.djDialog.customerForm.customerName = row.customerName
					this.djDialog.customerForm.customerSex = row.customerSex + "" // 转为字符串匹配下拉框值
					this.djDialog.customerForm.roomNo = row.roomNo
					this.djDialog.customerForm.idcard = row.idcard // 身份证号赋值
					this.djDialog.customerForm.psychosomaticState = row.psychosomaticState
					this.djDialog.customerForm.familyMember = row.familyMember
					this.djDialog.customerForm.filepath = row.filepath
					this.djDialog.customerForm.checkinDate = row.checkinDate
					this.djDialog.customerForm.expirationDate = row.expirationDate
				})
			},
			// 模态框关闭前的回调（点击右上角X时触发）
			djhandleClose() {
				this.djcancel(); // 调用取消方法
			},
			// 关闭模态框并重置表单
			djcancel() {
				this.djDialog.dialogVisible = false; // 隐藏模态框
				this.resetForm("customerForm"); // 重置表单
				this.djDialog.customerForm.id = ""; // 清空id（区分新增和编辑）
			},
			// 保存表单数据（新增或编辑）
			djsave(formName) {
				// 校验表单
				this.$refs[formName].validate(valid => {
					if (valid) {
						// 表单验证通过
						if (!this.djDialog.customerForm.id) {
							// 新增操作（无id）
							addCustomer(this.djDialog.customerForm).then(res => {
								if (res.flag) {
									this.$message.success(res.message); // 提示成功
									this.query(); // 重新查询数据
									this.djcancel(); // 关闭模态框
								} else {
									this.$message.error(res.message); // 提示失败
								}
							});
						} else {
							// 编辑操作（有id）
							editKhxx(this.djDialog.customerForm).then(res => {
								if (res.flag) {
									this.$message.success(res.message); // 提示成功
									this.query(); // 重新查询数据
									this.djcancel(); // 关闭模态框
								} else {
									this.$message.error(res.message); // 提示失败
								}
							});
						}
					} else {
						// 表单验证失败
						return false;
					}
				});
			},
			// 重置表单
			resetForm(formName) {
				if (this.$refs[formName]) {
					this.$refs[formName].resetFields(); // 调用Element的重置方法
				}
			},
			// 筛选自理老人
			selfCareMan() {
				this.btnFlag1 = true; // 激活自理老人按钮样式
				this.btnFlag2 = false; // 取消护理老人按钮样式
				this.condition.manType = "1"; // 设置查询类型为自理老人
				this.condition.pageNum = 1; // 重置页码为1
				this.listKhxxPage(); // 重新查询
			},
			// 筛选护理老人
			careMan() {
				this.btnFlag1 = false; // 取消自理老人按钮样式
				this.btnFlag2 = true; // 激活护理老人按钮样式
				this.condition.manType = "2"; // 设置查询类型为护理老人
				this.condition.pageNum = 1; // 重置页码为1
				this.listKhxxPage(); // 重新查询
			},
			// 根据出生日期计算年龄
			getAge() {
				let bir = this.djDialog.customerForm.birthday;
				if (bir) {
					// 计算年龄（当前时间-出生时间）/年毫秒数 并取整
					this.djDialog.customerForm.customerAge = Math.round((new Date() - new Date(bir)) / (1000 * 60 * 60 *
						24 * 365));
				}
			},
			// 执行查询操作
			query() {
				this.condition.pageNum = 1; // 重置页码为1
				this.listKhxxPage(); // 调用查询方法
			},
			// 删除操作
			del(row) {
				// 显示确认对话框
				this.$confirm('此操作删除客户, 是否继续?', '提示', {
					confirmButtonText: '确定',
					cancelButtonText: '取消',
					type: 'warning',
				}).then(() => {
					// 点击确定后执行删除
					removeCustomer({
						id: row.id,
						bedId: row.bedId
					}).then(res => {
						if (res.flag) {
							this.$message.success('删除成功!'); // 提示成功
							this.listKhxxPage(); // 重新查询数据
						}
					})
				}).catch(() => {
					// 点击取消时不执行操作
				});
			},
			// 分页页码变化时触发
			handleCurrentChange(curPage) {
				this.page.currentPag = curPage; // 更新当前页码
				this.condition.pageNum = curPage; // 更新查询条件中的页码
				this.listKhxxPage(); // 重新查询
			},
			// 获取房间列表（用于下拉选择）
			getRoomList() {
				listRoom().then(res => {
					// 按楼层分组（一楼和二楼）
					let floor1 = res.data.filter(item => item.roomFloor == "一楼");
					let floor2 = res.data.filter(item => item.roomFloor == "二楼");
					this.djDialog.roomList = [{
							label: "一楼",
							options: floor1
						},
						{
							label: "二楼",
							options: floor2
						}
					];
				});
			},
			// 根据房间号获取可用床位（房间号变化时触发）
			getBed() {
				this.djDialog.bedList = []; // 清空床位列表
				this.djDialog.customerForm.bedId = ""; // 清空选中的床位
				// 调用接口查询可用床位
				findBedByRoom({
					bedStatus: 1, // 查询状态为空闲的床位
					roomNo: this.djDialog.customerForm.roomNo // 当前选择的房间号
				}).then(res => {
					this.djDialog.bedList = res.data; // 赋值床位列表
				});
			},
			// 查询客户列表数据
			listKhxxPage() {
				listKhxxPage(this.condition).then(res => {
					this.khxxList = res.data.records; // 赋值列表数据
					this.page.total = res.data.total; // 赋值总条数
					this.page.currentPag = res.data.current; // 赋值当前页码
				})
			}
		},
		// 页面加载完成后执行
		mounted() {
			this.listKhxxPage(); // 初始加载客户列表
		},
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

	.filter-btn {
		padding: 8px 16px;
		font-size: 13px;
		height: 40px;
		margin-right: 10px;
		border-radius: 4px;
		transition: all 0.3s ease;
		font-weight: 500;
		background-color: #f8f9fa;
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

	/* 按钮选中状态样式 */
	.activeBtn {
		color: #0050B3;
		border-color: #409EFF;
		background-color: #E6F7FF;
	}

	/* 操作按钮容器样式 - 核心修改点 */
	.operation-buttons {
		display: flex;
		align-items: center;
		justify-content: center;
		gap: 10px;
		/* 按钮之间的间距 */
		width: 100%;
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
</style>