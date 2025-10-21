<script setup lang="ts">
import { showImagePreview } from 'vant'

defineProps({
  isPreview: { type: Boolean, default: true },
})
</script>

<template>
  <van-image
    class="BaImage"
    @click="isPreview && showImagePreview({ images: [$attrs.src], closeable: true })"></van-image>
</template>

<style lang="scss" scoped></style>
