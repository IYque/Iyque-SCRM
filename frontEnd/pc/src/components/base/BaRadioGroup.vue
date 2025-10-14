<!--     <BaseRadioGroup v-model="form.type" prop="type" label="账户类型" required>
          <el-radio v-for="(item, index) in { 1: '公司账户', 2: '个人账户' }" :key="index" :value="index">
            {{ item }}
          </el-radio>
        </BaseRadioGroup> -->
<script>
import { defineComponent } from 'vue'
export default defineComponent({
  inheritAttrs: false,
  props: {
    options: { type: [Array, Object], default: () => [] },
    // 是否纵向排列
    isCol: { type: Boolean, default: false },
  },
  data() {
    return {}
  },
  computed: {
    isFormItem() {
      return this.$attrs.label || this.$attrs.prop
    },
  },
  watch: {},
  created() {},
  mounted() {},
  methods: {},
})
</script>

<template>
  <component :is="isFormItem ? 'BaFormItem' : 'div'" v-bind="isFormItem && $attrs">
    <template #[item] v-for="(item, index) of Object.keys($slots).filter((e) => !['default'].includes(e))" :key="index">
      <slot :name="item"></slot>
    </template>
    <el-radio-group
      class="BaRadioGroup gap-[30px]"
      :class="[isCol && 'flexCol !items-start --Gap']"
      v-bind="isFormItem ? Object.assign({}, $attrs, { style: '', class: '', id: '' }) : $attrs">
      <div v-for="(item, index) in options" :key="index">
        <el-radio :value="item?.value ?? +index">
          {{ item?.label ?? item }}
        </el-radio>
        <div class="g-tip mt0">{{ item.desc }}</div>
      </div>
    </el-radio-group>
    <slot></slot>
  </component>
</template>

<style scoped>
.el-radio {
  margin-right: 0;
}
</style>
