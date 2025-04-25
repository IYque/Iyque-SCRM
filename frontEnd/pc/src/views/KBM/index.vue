<script setup lang="ts">
import { getList, save, del, getListDetail, getListFragment, delAttach } from './api'
let kid = ref('')
let docId = ref('')
</script>

<template>
  <div :_="$store.setBusininessDesc(`<div>查看所有客服场景中客户咨询的详细记录</div>`)">
    <RequestChartTable ref="rctRef" :request="getList" searchBtnType="icon">
      <template #query="{ query }">
        <el-form-item label="知识库名称" prop="kname">
          <el-input v-model="query.kname" placeholder="请输入" clearable />
        </el-form-item>
      </template>
      <template #operation="{ selectedIds }">
        <div class="mid-action mb0">
          <el-button type="primary" @click="$refs.dialogRef.action()">新建</el-button>

          <el-button type="primary" plain :disabled="!selectedIds?.length" @click="$refs.rctRef?.apiConfirm(del)">
            批量删除
          </el-button>
        </div>

        <BaseDialog
          ref="dialogRef"
          dynamicTitle="知识库"
          width="500"
          :formProps="{ 'label-width': '100px' }"
          :rules="{ users: { required: true, message: '必选项', trigger: 'change' } }"
          @confirm="
            () => $refs.dialogRef.confirm(() => setIYQueComplaintTip(form.users?.map((e) => ({ userId: e.userId }))))
          ">
          <template #form="{ form }">
            <el-form-item prop="kname" label="知识库名称">
              <el-input v-model="form.kname" maxlength="15" placeholder="请输入" show-word-limit clearable></el-input>
            </el-form-item>
            <!-- <el-form-item prop="knowledgeSeparator" label="分隔符">
              <el-input
                v-model="form.knowledgeSeparator"
                maxlength="15"
                placeholder="请输入"
                show-word-limit
                clearable></el-input>
            </el-form-item> -->
            <!-- <el-form-item prop="retrieveLimit" class="" label="知识库中检索的条数">
              <el-input
                v-model="form.retrieveLimit"
                maxlength="15"
                placeholder="请输入"
                show-word-limit
                clearable></el-input>
            </el-form-item> -->
            <!-- <el-form-item prop="textBlockSize" label="文本块大小">
              <el-input
                v-model="form.textBlockSize"
                maxlength="15"
                placeholder="请输入"
                show-word-limit
                clearable></el-input>
            </el-form-item> -->
            <!-- <el-form-item prop="overlapChar" label="重叠字符">
              <el-input
                v-model="form.overlapChar"
                maxlength="15"
                placeholder="请输入"
                show-word-limit
                clearable></el-input>
            </el-form-item> -->
            <!-- <el-form-item prop="hotWord" label="向量库">
              <el-select v-model="query.handleState" :popper-append-to-body="false">
                <el-option v-for="(value, key) in handleState" :key="key" :label="value" :value="key" />
              </el-select>
            </el-form-item> -->
            <!-- <el-form-item prop="questionSeparator" label="提问分割符">
              <el-input
                v-model="form.questionSeparator"
                maxlength="15"
                placeholder="请输入"
                show-word-limit
                clearable></el-input>
            </el-form-item> -->
            <!-- <el-form-item prop="hotWord" label="向量模型">
              <el-select v-model="query.handleState" :popper-append-to-body="false">
                <el-option v-for="(value, key) in handleState" :key="key" :label="value" :value="key" />
              </el-select>
            </el-form-item> -->
            <el-form-item prop="description" label="知识库描述">
              <el-input
                v-model="form.description"
                maxlength="15"
                placeholder="请输入"
                show-word-limit
                clearable></el-input>
            </el-form-item>
            <!-- <el-form-item prop="hotWord" label="是否公开">
              <el-radio-group v-model="form.isActive">
                <el-radio
                  v-for="(value, key) of { 0: '否', 1: '是' }"
                  :key="key"
                  :label="value"
                  :value="key"></el-radio>
              </el-radio-group>
            </el-form-item> -->
          </template>
        </BaseDialog>
      </template>

      <template #table="{ data }">
        <!-- <el-table-column label="编号" prop="kid" min-width="140"></el-table-column> -->
        <el-table-column label="知识库名称" min-width="200" prop="kname"></el-table-column>
        <el-table-column label="知识库描述" min-width="100" prop="description">
          <!-- <template #default="{ row }">
            {{ row.userName ? row.userName : '-' }}
          </template> -->
        </el-table-column>
        <el-table-column label="创建时间" min-width="200" prop="createTime"></el-table-column>

        <el-table-column label="操作" fixed="right" width="130">
          <template #default="{ row }">
            <TableOperateBtn type="" @click="$refs.rctRef.apiConfirm(del, row.id)">删除</TableOperateBtn>
            <TableOperateBtn type="" @click=";($refs.dialogRefFile.visible = true), (kid = row.id)">
              附件
            </TableOperateBtn>
          </template>
        </el-table-column>
      </template>
    </RequestChartTable>

    <BaseDialog ref="dialogRefFile" title="知识库附件" width="1000">
      <RequestChartTable ref="rctRefFile" :request="getListDetail" :params="{ kid }" searchBtnType="icon">
        <template #operation="{ query }">
          <Upload v-model:fileUrl="form.coverUrl" v-model:imgSize="form.memorySize" type="0">文件上传</Upload>
        </template>

        <template #table="{ data }">
          <el-table-column label="文档编号" prop="id" min-width="140"></el-table-column>
          <el-table-column label="文档名称" min-width="200" prop="docName"></el-table-column>
          <el-table-column label="文档类型" min-width="100" prop="docType">
            <!-- <template #default="{ row }">
              {{ row.userName ? row.userName : '-' }}
            </template> -->
          </el-table-column>
          <el-table-column label="操作" fixed="right" width="130">
            <template #default="{ row }">
              <TableOperateBtn type="" @click="$refs.rctRefFile.apiConfirm(delAttach, row.id)">删除</TableOperateBtn>
              <TableOperateBtn type="" @click=";($refs.dialogRefFragments.visible = true), (docId = row.id)">
                知识片段
              </TableOperateBtn>
            </template>
          </el-table-column>
        </template>
      </RequestChartTable>
    </BaseDialog>

    <BaseDialog ref="dialogRefFragments" title="知识片段" width="1000">
      <RequestChartTable ref="rct" :request="getListFragment" :params="{ docId }" searchBtnType="icon">
        <template #table="{ data }">
          <el-table-column label="片段编号" prop="id" min-width="140"></el-table-column>
          <el-table-column label="片段内容" min-width="200" prop="content">
            <!-- <template #default="{ row }"></template> -->
          </el-table-column>
        </template>
      </RequestChartTable>
    </BaseDialog>
  </div>
</template>

<style scoped lang="scss"></style>
