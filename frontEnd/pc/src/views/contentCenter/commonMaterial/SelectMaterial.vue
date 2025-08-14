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
    width="1000px"
    title="选择素材"
    @confirm="({ visible, loading }) => $emit('confirm', { visible, loading, selected })">
    <index isSelect @selection-change="(val) => (selected = val)" />
  </BaDialog>
</template>

<style lang="scss" scoped></style>
