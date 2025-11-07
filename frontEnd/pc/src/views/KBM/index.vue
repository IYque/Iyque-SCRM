<script setup lang="ts">
import { getList, save, upload, del, getListDetail, getListFragment, delAttach } from './api'
let kid = ref('')
let file = ref()
let docId = ref('')
let loading = ref(false)
let rctRefFile = ref()
function _upload() {
  let formData = new FormData()
  formData.append('file', file.value)
  formData.append('kid', kid.value)

  loading.value = true
  upload(formData)
    .then((dd) => {
      if (dd.code == 200) {
        $sdk.msgSuccess()
        rctRefFile.value?.getList()
      } else {
        loading.value = false
        $sdk.msgError('上传失败，请稍后再试')
      }
    })
    .catch((err) => {
      $sdk.msgError('上传失败，请稍后再试')
    })
    .finally(() => {
      loading.value = false
    })
}
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

          <!-- <el-button type="primary" plain :disabled="!selectedIds?.length" @click="$refs.rctRef?.apiConfirm(del)">
            批量删除
          </el-button> -->
        </div>

        <BaDialog ref="dialogRef" dynamicTitle="知识库" width="500" :formProps="{ 'label-width': '100px' }"
          :rules="{ users: { required: true, message: '必选项', trigger: 'change' } }"
          @confirm="() => $refs.dialogRef.confirm(save, $refs.rctRef.getList)">
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
              <el-input type="textarea" v-model="form.description" maxlength="200" placeholder="请输入" show-word-limit
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
        </BaDialog>
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
            <TableOperateBtn isText @click="$refs.rctRef.apiConfirm(del, row.id)">删除</TableOperateBtn>
            <TableOperateBtn isText @click="; ($refs.dialogRefFile.visible = true), (kid = row.id)">
              附件
            </TableOperateBtn>
          </template>
        </el-table-column>
      </template>
    </RequestChartTable>

    <BaDialog ref="dialogRefFile" title="知识库附件" width="1000" :isFooter="false">
      <RequestChartTable ref="rctRefFile" class="pad0" :request="getListDetail" :params="{ kid }" searchBtnType="icon">
        <template #operation="{ query }">
          <el-upload :http-request="_upload" :limit="1" :before-upload="(val) => (file = val)">
            <el-button v-loading="loading" type="primary">文件上传</el-button>
          </el-upload>
          <!--
          <el-upload
            v-model:file-list="fileList"
            class="upload-demo"
            action="https://run.mocky.io/v3/9d059bf9-4660-45f2-925d-ce80ad6c4d15"
            multiple
            :on-preview="handlePreview"
            :on-remove="handleRemove"
            :before-remove="beforeRemove"
            :limit="3"
            :on-exceed="handleExceed">
            <el-button type="primary">Click to upload</el-button>
            <template #tip>
              <div class="el-upload__tip">jpg/png files with a size less than 500KB.</div>
            </template>
    </el-upload> -->
          <!-- <Upload v-model:fileUrl="form.coverUrl" v-model:imgSize="form.memorySize" type="0">文件上传</Upload> -->
        </template>

        <template #table="{ data }">
          <el-table-column label="文档编号" prop="id"></el-table-column>
          <el-table-column label="文档名称" prop="docName"></el-table-column>
          <el-table-column label="文档类型" prop="docType">
            <!-- <template #default="{ row }">
              {{ row.userName ? row.userName : '-' }}
            </template> -->
          </el-table-column>
          <el-table-column label="创建时间" min-width="200" prop="createTime"></el-table-column>
          <el-table-column label="操作" fixed="right" width="130">
            <template #default="{ row }">
              <TableOperateBtn type="" @click="$refs.rctRefFile.apiConfirm(delAttach, row.id)">删除</TableOperateBtn>
              <TableOperateBtn type="" @click="; ($refs.dialogRefFragments.visible = true), (docId = row.id)">
                知识片段
              </TableOperateBtn>
            </template>
          </el-table-column>
        </template>
      </RequestChartTable>
    </BaDialog>

    <BaDialog ref="dialogRefFragments" title="知识片段" width="1000" :isFooter="false">
      <RequestChartTable ref="rct" :request="getListFragment" :params="{ docId }" searchBtnType="icon">
        <template #table="{ data }">
          <el-table-column label="片段编号" prop="id"></el-table-column>
          <el-table-column label="片段内容" prop="content">
            <!-- <template #default="{ row }"></template> -->
          </el-table-column>
        </template>
      </RequestChartTable>
    </BaDialog>
  </div>
</template>

<style scoped lang="scss"></style>
