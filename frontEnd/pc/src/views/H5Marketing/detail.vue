<script setup lang="ts">
const aev = defineAsyncComponent(() => import('./aev'))
const statistics = defineAsyncComponent(() => import('./statistics'))
</script>

<template>
  <CacheElTabs>
    <el-tab-pane lazy label="配置详情">
      <aev />
    </el-tab-pane>
    <el-tab-pane lazy label="执行统计">
      <statistics />
    </el-tab-pane>
  </CacheElTabs>
</template>
