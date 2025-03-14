<script setup>
import List from './List.vue'
</script>

<template>
  <van-tabs>
    <van-tab title="待处理" name="0">
      <List type="1"></List>
    </van-tab>
    <van-tab title="已处理" name="1">
      <List type="2"></List>
    </van-tab>
  </van-tabs>
</template>

<style lang="scss" scoped></style>
