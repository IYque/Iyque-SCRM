<script>
import { defineComponent, defineAsyncComponent } from 'vue'
export default defineComponent({
  props: {
    placeholder: { type: String, default: '' },
  },
  computed: {
    placeholderTransfer() {
      return this.placeholder ?? '请输入' + (this.$attrs.label || this.$.parent?.props?.label || '')
    },
  },
})
</script>
<template>
  <component :is="$attrs.label || $attrs.prop ? 'BaFormItem' : 'div'" class="inline-block">
    <template #[item] v-for="(item, index) of Object.keys($slots)" :key="index">
      <slot :name="item"></slot>
    </template>
    <el-input-number
      class="BaInputNumber"
      clearable
      controls-position="right"
      :placeholder="placeholderTransfer"
      v-bind="Object.assign({}, $attrs, { style: '', class: '', id: '' })">
      <template
        #[item]
        v-for="(item, index) of Object.keys($slots).filter((e) =>
          ['prefix', 'suffix', 'decrease-icon', 'increase-icon'].includes(e),
        )"
        :key="index">
        <slot :name="item"></slot>
      </template>
    </el-input-number>
  </component>
</template>

<style lang="scss" scoped></style>
