<script setup lang="ts">
defineProps({
  isPreview: { type: Boolean, default: true },
})
</script>

<template>
  <el-image class="BaImage relative" :preview-src-list="isPreview && [$attrs.src]">
    <template #placeholder>
      <div class="size-[100%]" v-loading="true"></div>
    </template>
  </el-image>
</template>

<style lang="scss" scoped></style>
