<script setup lang="ts">
import { getUserList, getTagList, getRemarkList } from '@/api/common'
let userList = ref([])

function getUserListFn() {
  getUserList().then((res) => {
    if (res.code == 301) {
      // this.userErrorTip = res.msg
      return
    }
    userList.value = res.data || []
  })
}
onMounted(() => {
  getUserListFn()
})
</script>

<template>
  <el-select
    value-key="userId"
    multiple
    collapse-tags
    collapse-tags-tooltip
    :max-collapse-tags="3"
    placeholder="请选择"
    v-bind="$attrs">
    <el-option v-for="item in userList" :key="item.userId" :label="item.name" :value="item" />
  </el-select>
</template>

<style lang="scss" scoped></style>
