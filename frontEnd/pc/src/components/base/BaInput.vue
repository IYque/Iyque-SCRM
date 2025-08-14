<!-- <BaseInput v-model.trim="dataUser.username" label="分支" prop="username"></BaseInput> -->

<!-- <script setup lang="ts">
import { ref } from 'vue'
const $props = defineProps({
	placeholder: { type: String, default: '' },
})
const input = ref()
const placeholderTransfer = computed(() => {
	if (!$props.placeholder) {
		console.log(input)

		return '请输入'
	} else {
		return $props.placeholder
	}
})
</script> -->
<script>
import { defineComponent, defineAsyncComponent } from 'vue'
import { ElFormItem } from 'element-plus'
export default defineComponent({
  props: {
    placeholder: { type: String },
  },
  // components: { ElFormItem: defineAsyncComponent(() => import('element-plus')['ElFormItem']) },
  components: { ElFormItem },
  computed: {
    placeholderTransfer() {
      return this.placeholder ?? '请输入' + (this.$attrs.label || this.$.parent?.props?.label || '')
    },
  },
})
</script>
<template>
  <component :is="$attrs.label || $attrs.prop ? 'BaFormItem' : 'div'" class="inline-block">
    <template #[item] v-for="(item, index) of Object.keys($slots).filter((e) => !['default'].includes(e))" :key="index">
      <slot :name="item"></slot>
    </template>
    <el-input
      class="BaInput"
      clearable
      show-word-limit
      :placeholder="placeholderTransfer"
      v-bind="Object.assign({}, $attrs, { style: '', class: '', id: '' })">
      <template
        #[item]
        v-for="(item, index) of Object.keys($slots).filter((e) =>
          ['prefix', 'suffix', 'prepend', 'append'].includes(e),
        )"
        :key="index">
        <slot :name="item"></slot>
      </template>
    </el-input>
    <slot></slot>
  </component>
</template>

<style lang="scss" scoped>
:deep() .el-input__wrapper {
  border: 1px solid var(--BorderBlack10);
  background-color: transparent;
  box-shadow: none;
  // --el-input-height: var(--heightBaseInput, 72px);
  height: var(--heightBaseInput);
  overflow: hidden;
}
</style>
