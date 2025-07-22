<template>
	<div class="login-bg">
		<div class="login">
			<div class="message">东软颐养中心</div>
			<div id="darkbannerwrap"></div>
			<form :model="loginForm">
				<input name="username" v-model="loginForm.username" placeholder="用户名" type="text">
				<hr class="hr15">
				<input name="password" v-model="loginForm.password" placeholder="密码" type="password">
				<hr class="hr15">
				<input value="登录" @click="login" style="width:100%;" type="button">
				<hr class="hr20">
			</form>
		</div>
	</div>
</template>


<script>
	import {
		login
	} from '../api/userApi.js'
	import {
		setSessionStorage
	} from '@/utils/common.js'
	export default {
		data() {
			return {
				loginForm: {
					username: "",
					password: ""
				}
			}
		},
		methods: {
			login() {
				login(this.loginForm).then(res => {
					console.log(res);
					if (res.flag) {
						// 将token存入storage
						sessionStorage.setItem('token', res.message);
						// 将当前登录用户信息存入storage中
						setSessionStorage('user', res.data);
						// 将菜单列表存入vuex中
						this.$store.commit('addMenus', res.data.menuList);

						// 路由到第一个子菜单
						this.$router.push(res.data.menuList[0].children[0].path);
					} else {
						this.$message.error(res.message);
					}
				}).catch(error => {
					console.log(error);
				});
			}
		}
	}
</script>

<style scoped>
	canvas {
		display: block;
		margin: 0;
		width: 100%;
		height: 100%;
		position: fixed;
		left: 0;
		right: 0;
		top: 0;
		bottom: 0;
		z-index: -1;
	}

	.login-bg {
		width: 100%;
		height: 100%;
		background: url(@/assets/loginBJ.jpg) no-repeat center;
		background-size: cover;
		overflow: hidden
	}

	.login {
		margin: 200px auto 0 auto;
		min-height: 420px;
		max-width: 420px;
		padding: 40px;
		background-color: #fff;
		border-radius: 4px;
		box-sizing: border-box;
	}

	.login a.logo {
		display: block;
		height: 58px;
		width: 167px;
		margin: 0 auto 30px auto;
		background-size: 167px 42px
	}

	.login.message {
		margin: 10px 0 0 -58px;
		padding: 18px 10px 18px 60px;
		background: #44aff0;
		position: relative;
		color: #fff;
		font-size: 20px
	}

	.login #darkbannerwrap {
		width: 18px;
		height: 10px;
		margin: 0 0 20px -58px;
		position: relative
	}

	.login input[type=password],
	.login input[type=text],
	select {
		border: 1px solid #DCDEE0;
		vertical-align: middle;
		border-radius: 3px;
		height: 50px;
		padding: 0 16px;
		font-size: 14px;
		color: #555;
		outline: 0;
		width: 100%;
		box-sizing: border-box
	}

	.login input[type=email]:focus,
	.login input[type=file]:focus,
	.login input[type=password]:focus,
	.login input[type=text]:focus,
	select:focus {
		border: 1px solid #44aff0
	}

	.login input[type=button],
	.login input[type=submit] {
		display: inline-block;
		vertical-align: middle;
		padding: 12px 24px;
		margin: 0;
		font-size: 18px;
		line-height: 24px;
		text-align: center;
		white-space: nowrap;
		vertical-align: middle;
		cursor: pointer;
		color: #fff;
		background-color: #44aff0;
		border-radius: 3px;
		border: none;
		-webkit-appearance: none;
		outline: 0;
		width: 100%
	}

	.login hr {
		background: #fff;
	}

	.login hr.hr15 {
		height: 15px;
		border: none;
		margin: 0;
		padding: 0;
		width: 100%
	}

	.login hr.hr20 {
		height: 20px;
		border: none;
		margin: 0;
		padding: 0;
		width: 100%
	}
</style>