<template>
	<div class="common-layout">
		<el-container>
			<el-header>
				<div>
					<el-row :gutter="30">
						<el-col :span="17">
							<el-button type="primary" @click="addItem" color="#337ab7" style="margin-top:4px">
								<el-icon>
									<Plus />
								</el-icon>
								<span>添加</span>
							</el-button>
						</el-col>
					</el-row>
				</div>
			</el-header>
			<el-divider style="margin:0"></el-divider>
			<el-main>
				<el-tabs type="border-card" @tab-click="handleTabClick" v-model="activeWeekDay">
					<el-tab-pane v-for="(week, index) in weekdayList" :label="week" :name="week" :key="index" />
				</el-tabs>
				<div v-if="activeWeekDay">
					<!-- 早餐区域 -->
					<el-row :gutter="20" class="row">
						<el-col :span="2" class="mealtype">
							<span>{{mealTypeName(1)}}<br />餐</span>
						</el-col>
						<el-col v-for="(meal, index) in breakfastList" :key="meal.id || index" :span="4" class="food">
							<el-card :body-style="{ padding: '15px' }" class="card" shadow="hover">
								<!-- 口味标签 -->
								<el-tag class="ml-2 taste" v-if="meal.taste === '正常'">{{meal.taste}}</el-tag>
								<el-tag class="ml-2 taste" v-if="meal.taste === '多糖'"
									type="warning">{{meal.taste}}</el-tag>
								<el-tag class="ml-2 taste" v-if="meal.taste === '少糖'"
									type="success">{{meal.taste}}</el-tag>
								<el-tag class="ml-2 taste" v-if="meal.taste === '多盐'"
									type="warning">{{meal.taste}}</el-tag>
								<el-tag class="ml-2 taste" v-if="meal.taste === '少盐'"
									type="success">{{meal.taste}}</el-tag>

								<!-- 编辑/删除按钮 -->
								<el-icon class="edit" @click="edit(meal)">
									<EditPen />
								</el-icon>
								<el-icon class="close" @click="del(meal.id, meal.mealType)">
									<Close />
								</el-icon>

								<!-- 食品图片 -->
								<div class="image-container">
									<img :src="foodFullImg(meal.foodImg)" class="image"
										@error="handleImageError($event, meal)" />
									<div v-if="!meal.foodImg || imageErrors[meal.id]" class="no-image-placeholder">
										<el-icon size="40">
											<Picture />
										</el-icon>
										<p>无图片</p>
									</div>
								</div>

								<!-- 食品信息 -->
								<div style="padding: 14px;">
									<el-row><el-col>类别：{{meal.foodType || '-'}}</el-col></el-row>
									<el-row><el-col>名称：{{meal.foodName || '-'}}</el-col></el-row>
									<el-row><el-col>价格：{{meal.price || 0}}元</el-col></el-row>
									<el-row><el-col>是否清真：<span v-if="meal.isHalal === 1">是</span><span
												v-else>否</span></el-col></el-row>
								</div>
							</el-card>
						</el-col>
						<el-col :span="22" v-if="!breakfastList.length" class="empty-col">
							<div class="nofood">
								<p>暂无早餐数据</p>
							</div>
						</el-col>
					</el-row>

					<!-- 午餐区域 -->
					<el-row :gutter="20" class="row">
						<el-col :span="2" class="mealtype"><span>{{mealTypeName(2)}}<br />餐</span></el-col>
						<el-col v-for="(meal, index) in lunchList" :key="meal.id || index" :span="4" class="food">
							<el-card :body-style="{ padding: '15px' }" class="card" shadow="hover">
								<!-- 口味标签 -->
								<el-tag class="ml-2 taste" v-if="meal.taste === '正常'">{{meal.taste}}</el-tag>
								<el-tag class="ml-2 taste" v-if="meal.taste === '多糖'"
									type="warning">{{meal.taste}}</el-tag>
								<el-tag class="ml-2 taste" v-if="meal.taste === '少糖'"
									type="success">{{meal.taste}}</el-tag>
								<el-tag class="ml-2 taste" v-if="meal.taste === '多盐'"
									type="warning">{{meal.taste}}</el-tag>
								<el-tag class="ml-2 taste" v-if="meal.taste === '少盐'"
									type="success">{{meal.taste}}</el-tag>

								<el-icon class="edit" @click="edit(meal)">
									<EditPen />
								</el-icon>
								<el-icon class="close" @click="del(meal.id, meal.mealType)">
									<Close />
								</el-icon>

								<div class="image-container">
									<img :src="foodFullImg(meal.foodImg)" class="image"
										@error="handleImageError($event, meal)" />
									<div v-if="!meal.foodImg || imageErrors[meal.id]" class="no-image-placeholder">
										<el-icon size="40">
											<Picture />
										</el-icon>
										<p>无图片</p>
									</div>
								</div>

								<div style="padding: 14px;">
									<el-row><el-col>类别：{{meal.foodType || '-'}}</el-col></el-row>
									<el-row><el-col>名称：{{meal.foodName || '-'}}</el-col></el-row>
									<el-row><el-col>价格：{{meal.price || 0}}元</el-col></el-row>
									<el-row><el-col>是否清真：<span v-if="meal.isHalal === 1">是</span><span
												v-else>否</span></el-col></el-row>
								</div>
							</el-card>
						</el-col>
						<el-col :span="22" v-if="!lunchList.length" class="empty-col">
							<div class="nofood">
								<p>暂无午餐数据</p>
							</div>
						</el-col>
					</el-row>

					<!-- 晚餐区域 -->
					<el-row :gutter="20" class="row">
						<el-col :span="2" class="mealtype"><span>{{mealTypeName(3)}}<br />餐</span></el-col>
						<el-col v-for="(meal, index) in dinnerList" :key="meal.id || index" :span="4" class="food">
							<el-card :body-style="{ padding: '15px' }" class="card" shadow="hover">
								<!-- 口味标签 -->
								<el-tag class="ml-2 taste" v-if="meal.taste === '正常'">{{meal.taste}}</el-tag>
								<el-tag class="ml-2 taste" v-if="meal.taste === '多糖'"
									type="warning">{{meal.taste}}</el-tag>
								<el-tag class="ml-2 taste" v-if="meal.taste === '少糖'"
									type="success">{{meal.taste}}</el-tag>
								<el-tag class="ml-2 taste" v-if="meal.taste === '多盐'"
									type="warning">{{meal.taste}}</el-tag>
								<el-tag class="ml-2 taste" v-if="meal.taste === '少盐'"
									type="success">{{meal.taste}}</el-tag>

								<el-icon class="edit" @click="edit(meal)">
									<EditPen />
								</el-icon>
								<el-icon class="close" @click="del(meal.id, meal.mealType)">
									<Close />
								</el-icon>

								<div class="image-container">
									<img :src="foodFullImg(meal.foodImg)" class="image"
										@error="handleImageError($event, meal)" />
									<div v-if="!meal.foodImg || imageErrors[meal.id]" class="no-image-placeholder">
										<el-icon size="40">
											<Picture />
										</el-icon>
										<p>无图片</p>
									</div>
								</div>

								<div style="padding: 14px;">
									<el-row><el-col>类别：{{meal.foodType || '-'}}</el-col></el-row>
									<el-row><el-col>名称：{{meal.foodName || '-'}}</el-col></el-row>
									<el-row><el-col>价格：{{meal.price || 0}}元</el-col></el-row>
									<el-row><el-col>是否清真：<span v-if="meal.isHalal === 1">是</span><span
												v-else>否</span></el-col></el-row>
								</div>
							</el-card>
						</el-col>
						<el-col :span="22" v-if="!dinnerList.length" class="empty-col">
							<div class="nofood">
								<p>暂无晚餐数据</p>
							</div>
						</el-col>
					</el-row>
				</div>
			</el-main>
		</el-container>

		<!-- 修改/添加对话框 -->
		<el-dialog v-model="dialog.dialogVisible" :title="dialog.tops" width="40%" align-center draggable
			:before-close="handleClose">
			<el-divider border-style="double" style="margin:0;" />
			<el-form label-position="right" label-width="auto" style="max-width:380px;margin:20px auto"
				class="demo-form-inline" ref="itemForm" :model="dialog.item" :rules="rules">
				<el-form-item label="膳食类型：" prop="mealType">
					<el-select v-model="dialog.item.mealType" style="width:320px" placeholder="餐饮类型：">
						<el-option v-for="item in mealtypeList" :label="item.mealtypeName" :value="item.typeId" />
					</el-select>
				</el-form-item>
				<el-form-item label="食品名称：" prop="foodId">
					<el-select v-model="dialog.item.foodId" style="width:320px" placeholder="食品名称：" clearable>
						<el-option v-for="item in foodList" :key="item.id" :label="item.foodName" :value="item.id" />
					</el-select>
				</el-form-item>
				<el-form-item label="星期：" prop="weekDay">
					<el-select v-model="dialog.item.weekDay" style="width:320px" placeholder="选择星期：">
						<el-option v-for="(week, index) in weekdayList" :label="week" :value="week" />
					</el-select>
				</el-form-item>
				<el-form-item label="口味：" prop="taste">
					<el-select v-model="dialog.item.taste" style="width:320px" placeholder="选择口味：">
						<el-option label="正常" value="正常" />
						<el-option label="多糖" value="多糖" />
						<el-option label="少糖" value="少糖" />
						<el-option label="多盐" value="多盐" />
						<el-option label="少盐" value="少盐" />
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
	import request from "@/request/request.js";
	import {
		addMeal,
		updateMeal,
		removeMeal,
		listMealPage
	} from "@/api/mealApi.js";
	import {
		listFood
	} from "@/api/foodApi.js";
	import {
		ElMessage,
		ElMessageBox
	} from 'element-plus';
	import {
		Plus,
		EditPen,
		Close,
		Picture
	} from '@element-plus/icons-vue';

	export default {
		components: {
			Plus,
			EditPen,
			Close,
			Picture
		},
		data() {
			return {
				mealtypeList: [{
						typeId: 1,
						mealtypeName: '早餐'
					},
					{
						typeId: 2,
						mealtypeName: '午餐'
					},
					{
						typeId: 3,
						mealtypeName: '晚餐'
					}
				],
				weekdayList: ['周一', '周二', '周三', '周四', '周五', '周六', '周日'],
				activeWeekDay: '周一',
				dialog: {
					dialogVisible: false,
					tops: "",
					item: {
						id: "",
						foodId: null,
						foodName: "",
						foodType: "",
						price: 0,
						isHalal: 0,
						weekDay: "周一",
						mealType: 1,
						taste: "正常",
						isDeleted: 0,
						foodImg: ""
					}
				},
				btnFlag: true,
				queryParams: {
					mealType: 1,
					weekDay: "周一",
					pageNum: 1,
					pageSize: 10000
				},
				breakfastList: [],
				lunchList: [],
				dinnerList: [],
				foodList: [],
				mealList: [],
				path: '',
				hasFood: true,
				rules: {
					mealType: [{
						required: true,
						message: '请选择膳食类型',
						trigger: 'change'
					}],
					foodId: [{
						required: true,
						message: '请选择食品名称',
						trigger: 'change'
					}],
					weekDay: [{
						required: true,
						message: '请选择星期',
						trigger: 'change'
					}],
					taste: [{
						required: true,
						message: '请选择口味',
						trigger: 'change'
					}]
				},
				loading: {
					breakfast: false,
					lunch: false,
					dinner: false
				},
				imageErrors: {},
				retryCount: 0,
				maxRetries: 3
			};
		},
		mounted() {
			this.initData();
		},
		methods: {
			async initData() {
				await this.getFoodList();
				this.activeWeekDay = this.weekdayList[0];
				this.queryParams.weekDay = this.activeWeekDay;
				this.loadMealData();
			},
			foodFullImg(foodImg) {
				return require('@/assets/food/' + foodImg);
			},
			handleImageError(event, meal) {
				this.imageErrors[meal.id] = true;
			},
			mealTypeName(typeId) {
				return typeId === 1 ? '早' : typeId === 2 ? '午' : '晚';
			},
			async addItem() {
				this.dialog.tops = "添加膳食管理";
				await this.getFoodList();
				this.resetForm("itemForm");
				this.dialog.dialogVisible = true;
			},
			handleClose(done) {
				this.resetForm("itemForm");
				done();
				this.dialog.dialogVisible = false;
			},
			cancel() {
				this.handleClose(() => {});
			},
			resetForm(formName) {
				if (this.$refs[formName]) {
					this.$refs[formName].resetFields();
					this.dialog.item = {
						...this.dialog.item,
						weekDay: this.activeWeekDay,
						mealType: 1,
						taste: "正常",
						foodId: null,
						foodName: ""
					};
				}
			},
			save(formName) {
				if (this.$refs[formName]) {
					this.$refs[formName].validate(valid => {
						if (valid) {
							const selectedFood = this.foodList.find(food => food.id === this.dialog.item.foodId);
							if (!selectedFood) {
								ElMessage.error("未找到选中的食品，请重新选择");
								return;
							}
							this.dialog.item.foodName = selectedFood.foodName;
							this.dialog.item.foodType = selectedFood.foodType;
							this.dialog.item.price = selectedFood.price;
							this.dialog.item.isHalal = selectedFood.isHalal;
							this.dialog.item.foodImg = selectedFood.foodImg;
							this.dialog.item.foodId = selectedFood.id;

							if (!this.dialog.item.id) {
								addMeal(this.dialog.item).then(res => {
									if (res && res.flag) {
										ElMessage.success("添加成功");
										this.loadMealData();
										this.handleClose(() => {});
									} else {
										ElMessage.error("添加失败：" + (res?.msg || "未知错误"));
									}
								}).catch(err => {
									ElMessage.error("添加请求失败：" + err.message);
								});
							} else {
								updateMeal(this.dialog.item).then(res => {
									if (res && res.flag) {
										ElMessage.success("修改成功");
										this.loadMealData();
										this.handleClose(() => {});
									} else {
										ElMessage.error("修改失败：" + (res?.msg || "未知错误"));
									}
								}).catch(err => {
									ElMessage.error("修改请求失败：" + err.message);
								});
							}
						}
					});
				}
			},
			del(id, mealType) {
				ElMessageBox.confirm("此操作删除记录, 是否继续?", "提示", {
					confirmButtonText: "确定",
					cancelButtonText: "取消",
					type: "warning"
				}).then(() => {
					removeMeal({
						id: id
					}).then(res => {
						if (res && res.flag) {
							ElMessage.success("删除成功");
							this.loadMealData();
						} else {
							ElMessage.error("删除失败：" + (res?.msg || "未知错误"));
						}
					}).catch(err => {
						ElMessage.error("删除请求失败：" + err.message);
					});
				}).catch(() => {});
			},
			loadMealData() {
				// 关键修改：为每个餐次创建独立参数，避免共享对象导致的参数覆盖
				const baseParams = {
					...this.queryParams
				}; // 复制基础参数

				// 加载早餐数据（使用独立参数）
				this.loading.breakfast = true;
				const breakfastParams = {
					...baseParams,
					mealType: 1
				};
				listMealPage(breakfastParams).then(res => {
					if (res && res.flag && res.data && res.data.records) {
						this.breakfastList = res.data.records.filter(meal => meal.weekDay === this.activeWeekDay);
					} else {
						this.breakfastList = [];
					}
				}).finally(() => {
					this.loading.breakfast = false;
				});

				// 加载午餐数据（使用独立参数）
				this.loading.lunch = true;
				const lunchParams = {
					...baseParams,
					mealType: 2
				};
				listMealPage(lunchParams).then(res => {
					if (res && res.flag && res.data && res.data.records) {
						this.lunchList = res.data.records.filter(meal => meal.weekDay === this.activeWeekDay);
					} else {
						this.lunchList = [];
					}
				}).finally(() => {
					this.loading.lunch = false;
				});

				// 加载晚餐数据（使用独立参数）
				this.loading.dinner = true;
				const dinnerParams = {
					...baseParams,
					mealType: 3
				};
				listMealPage(dinnerParams).then(res => {
					if (res && res.flag && res.data && res.data.records) {
						this.dinnerList = res.data.records.filter(meal => meal.weekDay === this.activeWeekDay);
					} else {
						this.dinnerList = [];
					}
				}).finally(() => {
					this.loading.dinner = false;
				});
			},
			async getFoodList() {
				try {
					const res = await listFood({
						pageNum: 1,
						pageSize: 1000
					});
					console.log('后端返回的食品列表原始数据：', res);

					if (Array.isArray(res)) {
						this.foodList = res.map(food => this.formatFoodItem(food));
					} else if (res) {
						if (res.data?.records && Array.isArray(res.data.records)) {
							this.foodList = res.data.records.map(food => this.formatFoodItem(food));
						} else if (Array.isArray(res.data)) {
							this.foodList = res.data.map(food => this.formatFoodItem(food));
						} else if (typeof res === 'object' && !Array.isArray(res) && !res.data) {
							this.foodList = [this.formatFoodItem(res)];
						} else {
							this.foodList = [];
							console.error('食品列表解析失败：未知数据结构', res);
							ElMessage.warning('食品列表数据格式异常');
						}
					}
				} catch (error) {
					this.foodList = [];
					console.error('获取食品列表请求异常', error);
					ElMessage.error('请求食品列表失败：' + error.message);
				}
			},
			formatFoodItem(food) {
				return {
					id: food.id,
					foodName: food.food_name || food.foodName,
					foodType: food.food_type || food.foodType,
					price: food.price,
					isHalal: food.is_halal || food.isHalal,
					foodImg: food.food_img || food.foodImg
				};
			},
			handleTabClick(tab) {
				this.activeWeekDay = tab.name;
				this.queryParams.weekDay = this.activeWeekDay;
				this.loadMealData();
			},
			edit(meal) {
				this.dialog.tops = "修改膳食日历";
				this.dialog.dialogVisible = true;
				this.$nextTick(() => {
					this.dialog.item = {
						id: meal.id,
						foodId: meal.foodId || meal.food_id,
						foodName: meal.foodName,
						foodType: meal.foodType,
						price: meal.price,
						isHalal: meal.isHalal,
						weekDay: meal.weekDay,
						mealType: meal.mealType,
						taste: meal.taste,
						foodImg: meal.foodImg
					};
				});
			}
		}
	};
</script>

<style scoped>
	.activeBtn {
		color: #1890ff;
		background: rgb(232, 244, 255);
	}

	.time {
		font-size: 12px;
		color: #999;
	}

	.bottom {
		margin-top: 13px;
		line-height: 12px;
		display: flex;
		justify-content: space-between;
		align-items: center;
	}

	.button {
		padding: 0;
		min-height: auto;
	}

	.card {
		height: 80%;
		position: relative;
		cursor: pointer;
		font-size: 14px;
		display: flex;
		flex-direction: column;
		justify-content: center;
		align-items: center;
		transition: all 0.3s;
	}

	.card:hover {
		box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
		transform: translateY(-5px);
	}

	.image {
		width: 100%;
		display: block;
		width: 160px;
		height: 160px;
		margin-top: 20px;
		object-fit: cover;
		border-radius: 4px;
	}

	.image-container {
		position: relative;
		width: 160px;
		height: 160px;
		margin-top: 20px;
	}

	.no-image-placeholder {
		position: absolute;
		top: 0;
		left: 0;
		width: 100%;
		height: 100%;
		background-color: #f5f7fa;
		border-radius: 4px;
		display: flex;
		flex-direction: column;
		justify-content: center;
		align-items: center;
		color: #909399;
	}

	.edit {
		position: absolute;
		right: 20px;
		top: 5px;
		font-size: 20px;
		color: #409EFF;
		transition: color 0.3s;
	}

	.edit:hover {
		color: #66b1ff;
	}

	.close {
		position: absolute;
		right: 5px;
		top: 5px;
		font-size: 20px;
		color: #F56C6C;
		transition: color 0.3s;
	}

	.close:hover {
		color: #ff7e7e;
	}

	.taste {
		position: absolute;
		left: 5px;
		top: 5px;
		font-size: 12px;
		z-index: 1;
	}

	.row {
		border: 1px solid #eee;
		margin-top: 20px;
		box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
		min-height: 300px;
		display: flex;
		align-items: flex-start;
		padding: 15px 0;
	}

	.mealtype {
		color: #fff;
		background-color: #337ab7;
		display: flex;
		justify-content: center;
		align-items: center;
		height: 300px;
		font-size: 18px;
		font-weight: bold;
		flex-shrink: 0;
	}

	.food {
		display: flex;
		justify-content: center;
		align-items: flex-start;
		padding: 20px 0;
		width: 100%;
	}

	.empty-col {
		display: flex;
		justify-content: center;
		align-items: center;
	}

	.nofood {
		text-align: center;
		padding: 20px;
	}

	.el-form-item__error {
		display: block;
		margin-top: 5px;
		color: #F56C6C;
	}
</style>