<script setup lang="ts">
import index from '@/views/customerServiceManage/list.vue'

defineProps({
  isSigleSelect: { type: Boolean, default: false },
})

const dialogRef = ref()
const selected = ref(undefined)
const $emit = defineEmits(['confirm'])

defineExpose({
  dialogRef,
})
</script>

<template>
  <BaDialog
    append-to-body
    ref="dialogRef"
    title="选择客服"
    size="1000"
    @confirm="
      ({ visible, loading }) => ((visible.value = false), (loading.value = false), $emit('confirm', { selected }))
    ">
    <index
      class="g-card h100 !bg-(--bg-black-11)"
      isSelect
      :isSigleSelect
      @selection-change="(val) => (selected = val)" />
  </BaDialog>
</template>

<style lang="scss" scoped>
:deep() {
  .pagination-container {
    padding-bottom: inherit !important;
  }
}
</style>
