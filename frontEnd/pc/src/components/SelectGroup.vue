<script setup lang="ts">
import { getList } from '@/views/groupChat/api.js'

defineProps({
  title: {
    type: String,
    default: '选择客群',
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
    :title="title"
    @confirm="({ visible, loading }) => $emit('confirm', { visible, loading, selected })">
    <RequestChartTable
      :isSigleSelect="isSigleSelect"
      ref="rct"
      :request="getList"
      searchBtnType="icon"
      @selectionChange="(val) => (selected = val)">
      <template #table="{}">
        <el-table-column label="群名称" prop="chatName" show-overflow-tooltip />
        <el-table-column label="群主" prop="ownerName">
          <!-- <template #default="{ row }">
								<el-image :src="row.codeUrl" style="width: 100px"></el-image>
							</template> -->
        </el-table-column>
        <el-table-column label="群创建时间" prop="createTime" />
      </template>
    </RequestChartTable>
  </BaseDialog>
</template>

<style lang="scss" scoped></style>
