<!--     <BaseRadioGroup v-model="form.type" prop="type" label="账户类型" required>
          <el-radio v-for="(item, index) in { 1: '公司账户', 2: '个人账户' }" :key="index" :value="index">
            {{ item }}
          </el-radio>
        </BaseRadioGroup> -->
<script>
import { defineComponent } from 'vue'
export default defineComponent({
  props: {
    options: { type: [Array, Object], default: () => [] },
  },
  data() {
    return {}
  },
  computed: {},
  watch: {},
  created() {},
  mounted() {},
  methods: {},
})
</script>

<template>
  <component :is="$attrs.label || $attrs.prop ? 'BaFormItem' : 'div'" class="inline-block">
    <template #[item] v-for="(item, index) of Object.keys($slots).filter((e) => !['default'].includes(e))" :key="index">
      <slot :name="item"></slot>
    </template>
    <el-radio-group class="BaRadioGroup" v-bind="Object.assign({}, $attrs, { style: '', class: '', id: '' })">
      <el-radio v-for="(item, index) in options" :key="index" :value="item.value ?? index">
        {{ item.label ?? item }}
      </el-radio>
    </el-radio-group>
    <slot></slot>
  </component>
</template>

<style scoped></style>
