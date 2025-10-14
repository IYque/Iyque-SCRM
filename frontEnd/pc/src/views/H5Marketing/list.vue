<script setup lang="ts">
import * as api from './api'

import CodeLink from './components/CodeLink'
const share = ref({})
</script>
<template>
  <div :_="$store.setBusininessDesc(`<div>一键创建H5营销落地页，满足跳转加微、客服咨询等营销场景</div>`)">
    <RequestChartTable ref="rct" :request="api.getList" searchBtnType="icon">
      <template #query="{ query }">
        <BaInput label="H5名称" prop="name" v-model="query.name"></BaInput>
      </template>

      <template #operation="{ goRoute, apiConfirm }">
        <CommonTopRight>
          <el-button type="primary" @click="goRoute()">新建H5</el-button>
        </CommonTopRight>
        <!-- <el-button type="primary" plain @click="apiConfirm(api.del)">删除</el-button> -->
      </template>

      <template #default="{ data, apiConfirm, goRoute }">
        <div class="flexWrap --Gap">
          <div class="g-card" v-for="(row, index) in data" :key="index">
            <BaImage class="size-[50px] --RadiusSmall" :src="row.backgroundUrl" fit="fill" :lazy="true"></BaImage>
            <div class="pad15">
              <div class="g-card-title truncate">{{ row.name }}</div>
              <div class="g-tip mt15 mb15">
                <span class="mr10">{{ row.updateBy }}</span>
                {{ row.updateTime }}
              </div>
              <div class="flex justify-between">
                <TableOperateBtn isText type="detail" @click="goRoute('detail', { id: row.id })"></TableOperateBtn>
                <TableOperateBtn isText type="edit" @click="goRoute('aev', { id: row.id })"></TableOperateBtn>
                <TableOperateBtn isText @click="$refs.shareDialogRef.visible = true">分享</TableOperateBtn>
                <TableOperateBtn isText type="delete" @click="apiConfirm(api.del, row.id)"></TableOperateBtn>
              </div>
            </div>
          </div>
        </div>
      </template>
    </RequestChartTable>

    <!-- H5分享 -->
    <BaDialog ref="shareDialogRef" title="H5分享" size="666px">
      <div class="--BgBlack8 --Padding --RadiusBig">
        <CodeLink :data="share" />
      </div>
    </BaDialog>
  </div>
</template>
