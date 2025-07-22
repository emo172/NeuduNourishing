<template>
	<el-header id="header">
		<div>
			<img style="width:200px;height:40px;margin:20px 0 0 -12px" src="@/assets/welcome.png" />
		</div>

		<el-dropdown split-button type="default">
			{{user.nickname}}
			<template #dropdown>
				<el-dropdown-menu>
					<el-dropdown-item @click="logout">注销</el-dropdown-item>
				</el-dropdown-menu>
			</template>
		</el-dropdown>

	</el-header>
</template>

<script>
	import {
		getSessionStorage,
		setSessionStorage
	} from '@/utils/common.js'
	export default {
		name: "Header",
		data() {
			return {
				user: getSessionStorage('user')
			}
		},
		methods: {
			logout() {
				sessionStorage.setItem('token', null)
				setSessionStorage('user', null)
				this.$store.commit('addMenus', [])
				this.$store.commit('clearTab', [])
				this.$router.push("/")
			}
		},

	}
</script>

<style scoped>
	#header {
		max-height: 50px;
		line-height: 50px;

		display: flex;
		justify-content: space-between;
		align-items: center;
	}

	#header>h2 {
		color: #0b67b8;
	}
</style>