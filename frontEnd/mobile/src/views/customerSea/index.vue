<script setup>
import List from './List.vue'
const status = ref({
  0: '待添加',
  1: '待通过',
  2: '已添加',
})
const num = ref({})
const listRef = ref()
async function refresh() {
  await listRef.value[1].loadListRef.getList(1)
}
</script>

<template>
  <van-tabs :lazy-render="false">
    <van-tab :title="`${item}(${num[index] || 0})`" v-for="(item, index) in status" :key="index">
      <List ref="listRef" :type="index" @change="(v) => (num[index] = v)" @refresh="refresh"></List>
    </van-tab>
  </van-tabs>
</template>

<style lang="less" scoped></style>
