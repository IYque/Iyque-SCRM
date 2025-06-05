<script setup lang="ts">
import index from './index.vue'
const dialogRef = ref()
const selected = ref([])
const $emit = defineEmits(['confirm'])

defineExpose({
  dialogRef,
})
</script>

<template>
  <BaseDialog
    ref="dialogRef"
    title="选择客户"
    append-to-body
    width="900"
    @confirm="({ visible, loading }) => $emit('confirm', { visible, loading, selected })">
    <index isSelect @selection-change="(val) => (selected = val)" />
  </BaseDialog>
</template>

<style lang="scss" scoped>
:deep() .g-card {
  --Padding: 0;
}
</style>
