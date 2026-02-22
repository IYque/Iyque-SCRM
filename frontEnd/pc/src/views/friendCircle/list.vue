<script setup lang="ts">
import * as api from './api'
import { dictMsgType } from '@/utils/index'

const share = ref({})
</script>
<template>
  <div :_="$store.setBusininessDesc(`<div>管理朋友圈内容，支持查看详情</div>`)">
    <RequestChartTable ref="rct" :request="api.getFriendCircleList" searchBtnType="icon">
      <template #query="{ query }">
        <BaInput label="朋友圈名称" prop="name" v-model="query.name"></BaInput>
      </template>

      <template #operate="{ goRoute, apiConfirm }">
        <el-button type="primary" @click="goRoute()">新建朋友圈</el-button>
      </template>

      <template #default="{ data, apiConfirm, goRoute }">
        <el-table :data="data" style="width: 100%">
          <el-table-column prop="name" label="标题" min-width="180"></el-table-column>
          <el-table-column prop="content" label="文本内容" min-width="300">
            <template #default="{ row }">
              <div class="truncate">{{ row.content }}</div>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="创建时间" min-width="180"></el-table-column>
          <el-table-column label="操作" min-width="120" fixed="right">
            <template #default="{ row }">
              <div class="flex justify-start">
                <TableOperateBtn isText type="detail" @click="goRoute('detail', { id: row.id })"></TableOperateBtn>
                <TableOperateBtn isText type="delete" @click="apiConfirm(api.deleteFriendCircle, row.id)"></TableOperateBtn>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </template>
    </RequestChartTable>
  </div>
</template>
