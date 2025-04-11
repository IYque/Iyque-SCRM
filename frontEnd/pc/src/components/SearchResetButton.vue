<!-- 搜索查询、重置按钮 -->
<script>
let dict = {
	button: 'button',
	icon: 'icon',
}
export default {
	props: {
		type: {
			type: String,
			default: dict.button,
			validator(value) {
				// 这个值必须匹配下列字符串中的一个
				return Object.values(dict).includes(value)
			},
		},
	},
	data() {
		return { dict }
	},
	// computed: {},
	// watch: {},
	// created() {},
	// mounted() {},
	// methods: {},
}
</script>

<template>
	<div class="SearchClearButton bfc-d flex aic">
		<template v-if="type === dict.button">
			<el-button type="primary" @click="$emit('search')">查询</el-button>
			<el-button @click="$emit('reset')">重置</el-button>
		</template>
		<template v-else>
			<IconBtn icon="el-icon-Search" @click="$emit('search')" style="font-size: 22px; color: var(--color)">
				查询
			</IconBtn>
			<!-- <IconBtn icon="el-icon-edit" @click="$emit('reset')" style="font-size: 22px">重置</IconBtn> -->
		</template>
	</div>
</template>

<style lang="scss" scoped></style>
