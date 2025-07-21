<script setup>
import List from './List.vue'
const status = ref({
  0: '待添加',
  1: '待通过',
  2: '已添加',
})
const num = ref({})
</script>

<template>
  <van-tabs>
    <van-tab
      :title="`${item}(${num[index] || 0})`"
      v-for="(item, index) in status"
      :key="index"
      @updateNum="(num) => (num[index] = num)">
      <List ref="list" :type="index"></List>
    </van-tab>
  </van-tabs>
</template>

<style lang="less" scoped></style>
