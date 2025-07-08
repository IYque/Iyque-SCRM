<script setup>
import MaPage from '@/views/contentCenter/components/MaPage'
</script>

<template>
  <MaPage ref="page" type="0" v-slot="{ list }">
    <el-table-column label="标题" prop="title" :show-overflow-tooltip="true" />
    <el-table-column label="图片" prop="content">
      <template #default="{ row }">
        <el-popover placement="bottom" trigger="hover">
          <template #reference>
            <el-image class="--RadiusSmall w-[50px] h-[50px]" :src="row.image?.picUrl"></el-image>
          </template>
          <el-image class="--Radius w-[200px] h-[200px]" :src="row.image?.picUrl"></el-image>
        </el-popover>
      </template>
    </el-table-column>
  </MaPage>
</template>
<style lang="scss">
.el-tooltip__popper {
  max-width: 500px;
}
</style>
