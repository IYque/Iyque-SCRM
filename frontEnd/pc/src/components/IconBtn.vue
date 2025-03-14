<script setup>
import { useSlots, useAttrs } from 'vue'

defineProps({
	icon: {
		type: String,
		default: '',
	},
	content: {
		type: String,
		default: '',
	},
	disabled: {
		type: Boolean,
		default: false,
	},
})

const slots = useSlots()
const attrs = useAttrs()
</script>

<template>
	<div class="IconBtn">
		<el-tooltip
			effect="dark"
			popper-class="IconBtnTooltip"
			:content="$slots.default?.()[0].children || $attrs.content"
			placement="top"
			:disabled="disabled"
			:show-after="800"
			:auto-close="0"
			trigger="hover"
			:show-arrow="false"
			:offset="0"
			v-bind="Object.assign({}, $attrs, { class: '', style: '' })">
			<component :is="icon" class="el-icon-" :title="$slots.default?.()[0].children"></component>
		</el-tooltip>
	</div>
</template>

<style lang="scss" scoped>
.IconBtn {
	display: inline-block;
	cursor: pointer;
	font-size: 18px;
	&:hover {
		color: var(--color);
	}
	+ .IconBtn {
		margin-left: 10px;
	}
	.el-icon- {
		// font-size: 18px;
	}
}
</style>
<style>
.IconBtnTooltip {
	background: none !important;
	border: none !important;
	color: var(--font-color) !important;
}
</style>
