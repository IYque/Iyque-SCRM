<script setup lang="ts">
import { getList, findGroupAll, summaryKfmsgByAi } from './api'

defineProps({
  title: {
    type: String,
    default: '选择客户',
  },
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
  <BaseDialog
    ref="dialogRef"
    width="900"
    append-to-body
    :title="title"
    @confirm="({ visible, loading }) => $emit('confirm', { visible, loading, selected })">
    <RequestChartTable
      :isSigleSelect="isSigleSelect"
      ref="rct"
      :request="findGroupAll"
      searchBtnType="icon"
      @selectionChange="(val) => (selected = val)">
      <template #table="{}">
        <el-table-column label="咨询客户" prop="nickname" show-overflow-tooltip>
          <template #default="{ row }">
            <div class="flex items-center">
              <el-image class="flex-none" :src="row.avatar" style="width: 50px"></el-image>
              <div class="ml10">{{ row.nickname }}</div>
            </div>
          </template>
        </el-table-column>
      </template>
    </RequestChartTable>
  </BaseDialog>
</template>

<style lang="scss" scoped></style>
