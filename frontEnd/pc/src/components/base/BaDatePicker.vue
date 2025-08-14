<script>
import { defineComponent } from 'vue'
import { pickerOptions } from '@/utils/index'
export default defineComponent({
  props: {
    placeholder: { type: String },
    disabledDate: { type: [String, Function] },
  },
  data() {
    return {
      pickerOptions,
    }
  },
  computed: {
    placeholderTransfer() {
      return this.placeholder ?? '请选择' + (this.$attrs.label || this.$.parent?.props?.label || '日期时间')
    },
    _disabledDate() {
      if (this.disabledDate === 'disabledDateFuture') {
        return (time) => time.getTime() > new Date(new Date().setDate(new Date().getDate() - 1)).getTime()
      } else if (this.disabledDate === 'disabledDatePast') {
        return (time) => time.getTime() < new Date(new Date().setDate(new Date().getDate() - 1)).getTime()
      } else if (typeof this.disabledDate === 'function') {
        return this.disabledDate
      }
      return null
    },
  },
})
</script>
<template>
  <component :is="$attrs.label || $attrs.prop ? 'BaFormItem' : 'div'" class="inline-block">
    <template #[item] v-for="(item, index) of Object.keys($slots).filter((e) => !['default'].includes(e))" :key="index">
      <slot :name="item"></slot>
    </template>
    <el-date-picker
      class="BaDatePicker"
      value-format="YYYY-MM-DD HH:mm:ss"
      type="datetimerange"
      :placeholder="placeholderTransfer"
      range-separator="至"
      start-placeholder="开始日期"
      end-placeholder="结束日期"
      clearable
      v-bind="Object.assign({}, pickerOptions, $attrs, { style: '', class: '', id: '' })"
      :disabledDate="_disabledDate">
      <template #[item] v-for="(item, index) of Object.keys($slots)" :key="index">
        <slot :name="item"></slot>
      </template>
    </el-date-picker>
    <!-- v-bind="{ ...pickerOptions, ...$attrs }"></el-date-picker> -->
    <slot></slot>
  </component>
</template>

<style lang="scss" scoped>
:deep() .el-date-editor {
  border: 1px solid var(--BorderBlack10);
  background-color: transparent;
  height: var(--heightBaseDate);
}
</style>
