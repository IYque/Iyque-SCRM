<script setup>
import MaPage from '@/views/contentCenter/components/MaPage'
import PicTitContent from '@/views/contentCenter/components/PicTitContent'
</script>

<template>
  <MaPage ref="page" type="9" v-slot="{ list }">
    <el-table-column label="图文" prop="materialName" width="260">
      <template #default="{ row }">
        <PicTitContent :data="row" />
      </template>
    </el-table-column>
  </MaPage>
</template>

<style lang="scss" scoped></style>
