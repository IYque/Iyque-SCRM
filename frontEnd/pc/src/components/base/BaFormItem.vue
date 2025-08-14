<script>
export default {
  props: {
    required: { type: Boolean, default: false },
    isPhone: { type: Boolean, default: false },
  },
  components: {},
  data() {
    return {}
  },
  computed: {
    requiredToRule() {
      // console.log(this.$slots)

      // return this.required && $sdk.ruleRequiredChange
      return this.required ? [$sdk.ruleRequiredChange] : []
    },
    isPhoneToRule() {
      return this.isPhone ? [{ pattern: /^1[^012]\d{9}$/, message: '手机号格式错误', trigger: 'blur' }] : []
    },
  },
  watch: {},
  created() {},
  mounted() {},
  methods: {},
}
</script>

<template>
  <el-form-item
    class="BaFormItem"
    :rules="[...requiredToRule, ...isPhoneToRule, ...($attrs.rules || [])]"
    v-bind="{ label: $attrs.label, prop: $attrs.prop }">
    <!-- v-bind="({ label, prop, rules } = $attrs)"> -->
    <template
      #[item]
      v-for="(item, index) of Object.keys($slots).filter((e) => ['default', 'label', 'error'].includes(e))"
      :key="index">
      <slot :name="item"></slot>
    </template>
  </el-form-item>
</template>

<style lang="scss" scoped></style>
