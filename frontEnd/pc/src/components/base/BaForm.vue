<script>
export default defineComponent({
  props: {
    modelValue: { type: [Object, Array], default: () => ({}) },
    data: { type: Object, default: () => ({}) },
    cancelButtonText: { type: String, default: '取 消' },
    confirmButtonText: { type: String, default: '确 定' },
    isOperate: { type: Boolean, default: true },
  },
  components: {},
  data() {
    return {
      loading: false,
      update: false,
      formRef: {},
      form: {},
    }
  },
  computed: {
    isDetail() {
      return this.$route.path.endsWith('detail')
    },
    // form: {
    //   get() {
    //     return this.modelValue
    //   },
    //   set(val) {
    //     this.$emit('update:modelValue', val)
    //   },
    // },
  },
  watch: {
    modelValue: {
      handler(newVal) {
        this.update = false
        // if (Array.isArray(newVal)) {
        //   this.form = JSON.parse(JSON.stringify(newVal))
        //   // Object.assign(this.form, JSON.parse(JSON.stringify(newVal)))
        // } else {
        //   // Object.assign(this.form, JSON.parse(JSON.stringify(newVal)))
        //   this.form = { ...this.form, ...JSON.parse(JSON.stringify(newVal)) }
        // }
        this.form = JSON.parse(JSON.stringify(newVal))
      },
      immediate: true,
      deep: true,
    },
    form: {
      handler(newVal, oldVal) {
        // modelValue改变使form改变时不更新，避免循环调用，deep内部属性改变时更新
        if (newVal == oldVal) {
          console.log('form change')
          this.$emit('update:modelValue', newVal)
        }
        this.update = true
        // if (JSON.stringify(newVal) == JSON.stringify(oldVal)) {
        // }
      },
      deep: true,
    },
    data: {
      handler(newVal) {
        if (JSON.stringify(newVal) != '{}') {
          this.form = { ...this.form, ...JSON.parse(JSON.stringify(newVal)) }
        }
      },
      immediate: true,
      deep: true,
    },
  },
  created() {},
  mounted() {
    // console.log(this.$options, this.$attrs)
  },
  methods: {
    async submit() {
      await this.formRef.validate()
      this.$store.loading = true
      this.$emit('submit', { form: this.form, loading: this.loading })
    },
    submitFn(add, callback, update, idKey = 'id') {
      this.$store.loading = true
      ;(this.form[idKey] ? update || add : add)(this.form)
        .then(() => {
          callback?.()
          $sdk.msgSuccess()
        })
        .finally(() => (this.$store.loading = false))
    },
  },
})
</script>

<template>
  <el-form
    class="BaForm"
    :model="form"
    :show-message="!isDetail"
    scroll-to-error
    :class="isDetail && 'form-detail'"
    :disabled="isDetail"
    :ref="(v) => (formRef = v)"
    v-loading="$store.loading"
    label-width="auto">
    <!-- v-bind="$attrs" -->
    <slot v-bind="{ loading, form, formRef, isDetail, submitFn }"></slot>

    <slot name="operate" v-bind="{ loading, form, formRef, isDetail, submitFn }" v-if="!isDetail && isOperate">
      <BaFormBar>
        <ElButton type="primary" plain @click="$attrs.onCancel ? $emit('cancel') : $router.back()">
          {{ cancelButtonText }}
        </ElButton>
        <ElButton type="primary" v-loading="$store.loading" :disabled="$store.loading" @click="submit()">
          {{ confirmButtonText }}
        </ElButton>
      </BaFormBar>
    </slot>
  </el-form>
</template>

<style lang="scss" scoped></style>
