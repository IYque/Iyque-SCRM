<script setup>
import { getList, del } from './api'
import * as apiCategory from './apiCategory'

defineProps({
  isSelect: { type: Boolean, default: false },
})

const query = ref({})
const rctRef = ref()
const categoryList = ref([])

function getCategoryList(isFresh) {
  apiCategory.getList().then(({ data }) => {
    categoryList.value = data
    isFresh && switchGroup(data[0])
  })
}
getCategoryList(true)

function switchGroup(data) {
  query.value.categoryId = data?.id
  $nextTick(() => {
    $refs.rctRef.getList(1)
  })
}

function delCategory(item) {
  $sdk.confirm().then(() => {
    this.loading = true
    apiCategory
      .del(item.id)
      .then(() => {
        if (item.id == query.value.categoryId) {
          query.value.categoryId = undefined
          getCategoryList(true)
        } else {
          getCategoryList()
        }
        $sdk.msgSuccess()
      })
      .finally(() => (this.loading = false))
  })
}
</script>

<template>
  <div>
    <div class="g-left-right">
      <div class="left g-card">
        <div class="title">
          <div class="title-name">热词分类</div>
          <el-icon-plus
            class="hoverColor"
            v-if="!isSelect"
            @click.stop="$refs.dialogRef.action()"
            title="新增"></el-icon-plus>
          <!-- <div class="title-btn" @click="addGroup">添加</div> -->
        </div>
        <el-scrollbar class="item-list">
          <div
            class="item"
            v-for="(item, key) in categoryList"
            :key="item.id"
            :class="{ active: item.id == categoryId }"
            @click="switchGroup(item)">
            <div class="name">{{ item.name }}</div>

            <div class="hidden item-hover:flex" v-if="!isSelect">
              <el-icon-EditPen class="hoverColor" @click="$refs.dialogRef.action(item)" title="编辑"></el-icon-EditPen>
              <el-icon-delete class="hoverColor" @click="delCategory(item)" title="删除"></el-icon-delete>
            </div>
          </div>
        </el-scrollbar>
        <BaseDialog
          ref="dialogRef"
          dynamicTitle="分类"
          width="500"
          :formProps="{ 'label-width': 'auto' }"
          :rules="{ name: [$sdk.ruleRequiredBlur] }"
          @confirm="({ form }) => $refs.dialogRef.confirm(apiCategory.save, getCategoryList)">
          <template #form="{ form }">
            <el-form-item prop="name" class="width100" label="分类名称" show-word-limit maxlength="10">
              <el-input v-model="form.name" placeholder="请输入" clearable />
              <div class="g-tip">分类名称不允许重复</div>
            </el-form-item>
          </template>
        </BaseDialog>
      </div>

      <div class="right">
        <RequestChartTable
          ref="rctRef"
          :isCreateRequest="false"
          :request="(pa) => (Object.assign(pa, query), getList(pa))"
          @selection-change="(val) => $emit('selectionChange', val)"
          @reset="qrUserName = query.qrUserIds = ''">
          <template #query="{ query }">
            <el-form-item label="热词名称" prop="hotWord">
              <el-input v-model="query.hotWord" placeholder="请输入" clearable />
            </el-form-item>
          </template>

          <template #operation="{ selectedIds }" v-if="!isSelect">
            <div class="mid-action mb0">
              <CommonTopRight>
                <el-button type="primary" @click="$refs.dialogHotWordRef.action()">新建热词</el-button>
              </CommonTopRight>

              <el-button
                type="primary"
                plain
                :disabled="!selectedIds?.length"
                @click="$refs.rctRef?.apiConfirm(remove)">
                删除
              </el-button>
            </div>
          </template>

          <template #table="{ data }">
            <el-table-column label="热词名称" min-width="100" prop="hotWord" show-overflow-tooltip />
            <el-table-column label="热词相近词" prop="nearHotWord" min-width="160px">
              <template #default="{ row }">
                <TagEllipsis :list="row.nearHotWord" emptyText />
              </template>
            </el-table-column>
            <el-table-column label="最近更新" prop="updateTime" width="180">
              <template #default="{ row }">
                {{ row.updateBy }}
                <br />
                {{ row.updateTime }}
              </template>
            </el-table-column>
            <el-table-column label="操作" v-if="!isSelect" fixed="right" width="220">
              <template #default="{ row }">
                <TableOperateBtn type="edit" @click="$refs.dialogHotWordRef.action(row)">编辑</TableOperateBtn>
                <TableOperateBtn type="delete" @click="$refs.rctRef.apiConfirm(del, row.id)">删除</TableOperateBtn>
              </template>
            </el-table-column>
          </template>
        </RequestChartTable>

        <BaseDialog
          ref="dialogHotWordRef"
          dynamicTitle="热词"
          width="500"
          :formProps="{ 'label-width': 'auto' }"
          :rules="{ categoryId: [$sdk.ruleRequiredChange], hotWord: [$sdk.ruleRequiredBlur] }"
          @confirm="({ form }) => $refs.dialogRef.confirm(apiCategory.save)">
          <template #form="{ form }">
            <el-form-item label="热词分类" prop="categoryId">
              <el-select v-model="query.categoryId" :popper-append-to-body="false">
                <el-option v-for="(item, key) in categoryList" :key="key" :label="item.name" :value="item.id" />
              </el-select>
            </el-form-item>
            <el-form-item prop="hotWord" class="width100" label="热词名称" show-word-limit maxlength="10">
              <el-input v-model="form.hotWord" placeholder="请输入" clearable />
              <div class="g-tip">分类名称不允许重复</div>
            </el-form-item>
            <el-form-item prop="nearHotWord" class="width100" label="热词相近词" show-word-limit maxlength="10">
              <el-input v-model="form.nearHotWord" placeholder="请输入" clearable />
              <div class="g-tip">多个相近词时用英文逗号隔开</div>
            </el-form-item>
          </template>
        </BaseDialog>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped></style>
