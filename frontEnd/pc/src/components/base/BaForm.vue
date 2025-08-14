<script>
export default defineComponent({
  props: {
    data: { type: Object, default: () => ({}) },
    cancelButtonText: { type: String, default: '取 消' },
    confirmButtonText: { type: String, default: '确 定' },
  },
  components: {},
  data() {
    return {
      loading: false,
      formRef: {},
      form: {},
    }
  },
  computed: {
    isDetail() {
      return this.$route.path.endsWith('detail')
    },
  },
  watch: {
    data: {
      handler(newVal) {
        this.form = { ...this.form, ...newVal }
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
    label-width="auto"
    v-bind="$attrs">
    <slot v-bind="{ loading, form, formRef, isDetail }"></slot>

    <slot name="operate" v-bind="{ loading, form, formRef, isDetail }" v-if="!isDetail">
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
