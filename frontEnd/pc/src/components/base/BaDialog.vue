<!-- <BaDialog ref="BaDialogRef" title="重命名" width="500" @confirm="allocateCustomerFn">
  <template #form="{ form }">
    <RemoteSelect
      :request="getUserList"
      class="flex-none w-[100%]"
      v-model="form.userId"
      label="顾问选择"
      :defaultProps="{ id: 'userId', name: 'realName' }"></RemoteSelect>
  </template>
</BaDialog> -->
<script setup lang="ts">
import { reactive, ref, useAttrs, toRef } from 'vue'
defineProps({
  idKey: { type: String, default: 'id' },
  dynamicTitle: { type: String, default: '' },
  rules: { type: Object, default: () => ({}) },
  formProps: { type: Object, default: () => ({}) },
  isFooter: { type: Boolean, default: true },
})
const $emit = defineEmits(['confirm', 'cancel'])

const visible = ref(false)
const loading = ref(false)
const refData = reactive({
  visible,
  loading,
})
const form = ref({})
const formRef = ref()
watch(
  () => visible.value,
  (val) => {
    val || (loading.value = val)
  },
)

function action(data = {}, callback: () => {}) {
  visible.value = true
  form.value = JSON.parse(JSON.stringify(data))
  callback?.()
}

const slots = useSlots()
async function submit() {
  slots.form && (await formRef.value.validate())
  loading.value = true
  $emit('confirm', { form, visible, loading, confirm })
}
function confirm(add, callback, update, idKey = 'id') {
  loading.value = true
  ;(form.value[idKey] ? update || add : add)(form.value)
    .then(() => {
      callback?.()
      // $app.config.globalProperties.msgSuccess()
      $sdk.msgSuccess()
      visible.value = false
    })
    .finally(() => (loading.value = false))
}

defineExpose({
  visible,
  form,
  action,
  confirm,
  formRef,
})
</script>

<template>
  <el-dialog
    class="BaDialog"
    :title="dynamicTitle && (form[idKey] ? '编辑' : '新增') + dynamicTitle"
    v-model="visible"
    align-center
    draggable
    close-on-click-modal
    destroy-on-close>
    <div class="h100" v-loading="loading">
      <slot v-bind="{ form, formRef, refData }"></slot>

      <el-form
        v-if="$slots.form"
        ref="formRef"
        class="dialogForm"
        label-width="auto"
        v-bind="formProps"
        :model="form"
        :rules="rules">
        <slot name="form" v-bind="{ form, formRef, refData }"></slot>
      </el-form>
    </div>

    <template #footer>
      <slot name="footer" v-bind="{ form, formRef, refData }" v-if="isFooter">
        <el-button @click="$emit('cancel'), (visible = false)">取消</el-button>
        <el-button type="primary" :disabled="loading" v-loading="loading" @click="submit">确定</el-button>
      </slot>
    </template>
  </el-dialog>
</template>

<style lang="scss" scoped></style>
