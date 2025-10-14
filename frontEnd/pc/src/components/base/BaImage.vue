<script setup lang="ts">
defineProps({
  isPreview: { type: Boolean, default: true },
})
</script>

<template>
  <el-image class="BaImage" :preview-src-list="isPreview && [$attrs.src]"></el-image>
</template>

<style lang="scss" scoped></style>
