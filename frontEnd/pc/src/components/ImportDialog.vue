<template>
  <!--
    导入表格模板弹窗 组件
    由透传属性，可直接使用 el-dialog 的所有props，
    用法示例：<ImportDialog v-model="dialogVisible" title="title"/>
  -->
  <BaseDialog ref="dialogRef" width="600px">
    <!-- 无效 :formProps="{
      ...$attrs.formProps,
      rules: {
        ...$attrs.formProps?.rules,
        file: [$sdk.ruleRequiredChange],
      },
    }" -->
    <template #form="{ form, formRef, refData }">
      <!-- <el-form v-loading="loading" ref="ruleForm" label-width="100px"> -->
      <el-form-item label="选择表格" prop="file" :rules="[$sdk.ruleRequiredChange]">
        <div class="flex justify-between items-center">
          <div>
            <input class="hidden" ref="input" type="file" @change="(event) => changeFile(event, form, formRef)" />
            <span class="mr15" v-if="form.file?.name" @click="$refs.input.click()">
              {{ form.file?.name }}
            </span>
            <el-button type="default" plain @click="$refs.input.click()">
              {{ form.file?.name ? '重新上传' : '上传表格' }}
            </el-button>
          </div>
          <el-button v-if="downloadTemplateRequest" text @click="downloadTemplate(refData)">模版下载</el-button>
        </div>
      </el-form-item>

      <slot v-bind="{ form, refData }"></slot>
      <!-- </el-form> -->
    </template>
  </BaseDialog>
</template>

<script>
export default {
  props: {
    // 导入文件格式, eg：xlsx, xls
    format: {
      type: Array,
      default: () => ['xlsx', 'xls'], // xlsx
    },
    // 文件体积限制大小
    maxSize: {
      type: Number,
      default: 50, // 50M
    },
    // 下载模板api
    downloadTemplateRequest: {
      type: Function,
      default: undefined,
    },
    // 导出文件名(需包含后缀名)
    downloadFileName: {
      type: String,
      default: '',
    },
  },
  data() {
    return {
      file: {},
    }
  },
  mounted() {},
  methods: {
    // 下载模板
    downloadTemplate(refData) {
      refData.loading = true
      this.downloadTemplateRequest()
        .then((res) => {
          res && this.downloadBlob(res, this.downloadFileName)
        })
        .finally(() => {
          refData.loading = false
        })
    },
    changeFile(event, form, formRef) {
      let file = event.target.files[0]
      const isOk = this.checkFile(file)
      if (!isOk) return
      form.file = file
      formRef.validateField('file')
    },
    // 文件校验
    checkFile(file) {
      const { size, name } = file
      const reg = new RegExp(`\\.(${this.format.join('|')})$`, 'ig') // /\.(xlsx|xls)$/ig
      const isXlsx = reg.test(name)
      const isLimit = size / 1024 / 1024 < this.maxSize

      if (!isXlsx) {
        this.msgError(`上传文件只能是 ${this.format.join(',')} 格式!`)
      }
      if (!isLimit) {
        this.msgError(`上传文件大小不能超过 ${this.maxSize}MB!`)
      }
      return isXlsx && isLimit
    },
    // 确认导入事件
    importFile({ form, refData }) {
      const isOk = this.checkFile(this.file)
      if (!isOk) return
      this.$emit('confirm', { form, refData, ...this.$data })
    },
  },
}
</script>

<style lang="scss" scoped></style>
