<script setup lang="ts">
import { showImagePreview } from 'vant'
</script>

<template>
  <van-image class="BaImage" @click="showImagePreview([$attrs.src])"></van-image>
</template>

<style lang="scss" scoped></style>
