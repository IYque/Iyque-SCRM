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
  <BaDialog
    ref="dialogRef"
    title="选择热词"
    @confirm="({ visible, loading }) => $emit('confirm', { visible, loading, selected })">
    <index isSelect @selection-change="(val) => (selected = val)" />
  </BaDialog>
</template>

<style lang="scss" scoped></style>
