<!-- <script setup lang="ts">
import { getDetailWel, addOrUpdateWel } from './api'
import { reactive, ref } from 'vue'

const loading = ref(false)
const welcome = ref()
function getDetail() {
  loading.value = true
  getDetailWel()
    .then(({ data }) => {
      welcome.value = data
      loading.value = false
    })
    .catch(() => {
      loading.value = false
    })
}
function submit() {
  this.$refs['form'].validate((validate) => {
    if (validate) {
      addOrUpdateWel(welcome).then(() => {
        this.msgSuccess('操作成功')
        this.getDetail()
      })
    }
  })
}
</script> -->

<script>
import { getDetailWel, addOrUpdateWel } from './api'
export default {
  data() {
    return {
      loading: false,
      form: { defaultContent: '' },
      rules: {
        welcome: [{ required: true, message: '必填项', trigger: 'blur' }],
      },
    }
  },
  created() {
    this.getDetail()
  },
  methods: {
    getDetail() {
      this.loading = true
      getDetailWel()
        .then(({ data }) => {
          this.form = data
          this.loading = false
        })
        .catch(() => {
          this.loading = false
        })
    },
    submit() {
      this.$refs['form'].validate((validate) => {
        if (validate) {
          addOrUpdateWel(this.form).then(() => {
            this.msgSuccess('操作成功')
            this.getDetail()
          })
        }
      })
    },
  },
}
</script>
<template>
  <el-form ref="form" label-position="right" :model="form">
    <div class="g-card">
      <el-form-item label="欢迎语">
        <TextareaExtend
          v-model="form.defaultContent"
          :toolbar="['emoji', 'insertCustomerNickName']"
          maxlength="2000"
          show-word-limit
          placeholder="请输入"
          :autosize="{ minRows: 5, maxRows: 20 }"
          clearable
          :autofocus="false" />
      </el-form-item>
    </div>
  </el-form>
</template>

<style lang="scss" scoped></style>
