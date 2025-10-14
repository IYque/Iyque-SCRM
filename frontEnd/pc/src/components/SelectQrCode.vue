<script setup lang="ts">
import { getList } from '@/views/groupCode/api.js'

defineProps({
  // 是否单选
  isSigleSelect: {
    type: Boolean,
    default: false,
  },
})

const dialogRef = ref()
const selected = ref([])
const $emit = defineEmits(['confirm'])

defineExpose({
  dialogRef,
})
</script>

<template>
  <BaDialog
    ref="dialogRef"
    title="选择活码"
    @confirm="
      ({ visible, loading }) => ((visible.value = false), (loading.value = false), $emit('confirm', { selected }))
    ">
    <RequestChartTable
      class="pad0"
      :isSigleSelect
      ref="rct"
      :request="getList"
      searchBtnType="icon"
      @selectionChange="(val) => (selected = isSigleSelect ? val?.[0] : val)">
      <template #table="{}">
        <el-table-column label="活码名称" prop="chatCodeName" show-overflow-tooltip />
        <el-table-column label="活码地址" prop="chatCodeUrl">
          <template #default="{ row }">
            <BaImage :src="row.chatCodeUrl" style="width: 100px"></BaImage>
          </template>
        </el-table-column>
        <el-table-column label="更新时间" prop="updateTime" />
      </template>
    </RequestChartTable>
  </BaDialog>
</template>

<style lang="scss" scoped></style>
