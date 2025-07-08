<script setup>
import MaPage from '@/views/contentCenter/components/MaPage'
</script>

<template>
  <MaPage ref="page" type="4" v-slot="{ list }">
    <el-table-column label="文本标题" prop="title" :show-overflow-tooltip="true" />
    <el-table-column label="文本内容" prop="content">
      <template #default="{ row }">
        <el-tooltip effect="dark" placement="top">
          <template #content><p style="white-space: pre-line" v-html="row.text?.content"></p></template>
          <div class="truncate">{{ row.text?.content }}</div>
        </el-tooltip>
      </template>
    </el-table-column>
  </MaPage>
</template>
<style lang="scss">
.el-tooltip__popper {
  max-width: 500px;
}
</style>
