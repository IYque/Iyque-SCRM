<script setup>
import { RouterView } from 'vue-router'
import stores from '@/stores/index'
</script>

<template>
  <van-config-provider
    class="vh100 bg-[var(--BgBlack10)]"
    :theme="stores().isDark ? 'dark' : 'light'"
  >
    <RouterView />
  </van-config-provider>
</template>
