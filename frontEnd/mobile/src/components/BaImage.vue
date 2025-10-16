<script setup lang="ts">
import { showImagePreview } from 'vant'
</script>

<template>
  <van-image class="BaImage" @click="showImagePreview({ images: [$attrs.src], closeable: true })"></van-image>
</template>

<style lang="scss" scoped></style>
