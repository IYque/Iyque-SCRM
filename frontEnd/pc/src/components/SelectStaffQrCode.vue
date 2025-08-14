<script setup lang="ts">
import { getList } from '@/views/liveCode/api.js'

defineProps({
  title: {
    type: String,
    default: '选择活码',
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
  <BaDialog
    ref="dialogRef"
    :title="title"
    @confirm="({ visible, loading }) => $emit('confirm', { visible, loading, selected })">
    <RequestChartTable
      isSigleSelect
      ref="rct"
      :request="getList"
      searchBtnType="icon"
      @selectionChange="(val) => (selected = val)">
      <template #table="{}">
        <el-table-column label="活码名称" prop="codeName" show-overflow-tooltip />
        <el-table-column label="活码地址" prop="codeUrl">
          <!-- <template #default="{ row }">
								<el-image :src="row.codeUrl" style="width: 100px"></el-image>
							</template> -->
        </el-table-column>
        <el-table-column label="使用员工">
          <template #default="{ row }">
            <TagEllipsis :list="row.userName"></TagEllipsis>
          </template>
        </el-table-column>
        <el-table-column label="标签">
          <template #default="{ row }">
            <TagEllipsis :list="row.tagName"></TagEllipsis>
          </template>
        </el-table-column>

        <el-table-column label="更新时间" prop="updateTime" />
      </template>
    </RequestChartTable>
  </BaDialog>
</template>

<style lang="scss" scoped></style>
