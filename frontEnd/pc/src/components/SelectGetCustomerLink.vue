<script setup lang="ts">
import { getList } from '@/views/liveCode/apiLink.js'

defineProps({
  isSigleSelect: { type: Boolean, default: false },
})

const dialogRef = ref()
const selected = ref(undefined)
const $emit = defineEmits(['confirm'])

defineExpose({
  dialogRef,
})
</script>

<template>
  <BaDialog
    append-to-body
    ref="dialogRef"
    title="选择获客链接"
    size="1000"
    @confirm="
      ({ visible, loading }) => ((visible.value = false), (loading.value = false), $emit('confirm', { selected }))
    ">
    <div class="g-card h100 !bg-(--bg-black-11)">
      <RequestChartTable
        ref="rctRef"
        :isSigleSelect
        :request="getList"
        @selectionChange="(val) => (selected = isSigleSelect ? val?.[0] : val)">
        <template #query="{ query }">
          <el-form-item label="链接名称" prop="codeName">
            <el-input v-model="query.codeName" placeholder="请输入" clearable />
          </el-form-item>
        </template>

        <template #table="{ data }">
          <el-table-column label="链接名称" min-width="100" prop="codeName" show-overflow-tooltip />
          <el-table-column label="获客链接" min-width="100" prop="codeUrl" show-overflow-tooltip />
        </template>
      </RequestChartTable>
    </div>
  </BaDialog>
</template>

<style lang="scss" scoped>
:deep() {
  .pagination-container {
    padding-bottom: inherit !important;
  }
}
</style>
