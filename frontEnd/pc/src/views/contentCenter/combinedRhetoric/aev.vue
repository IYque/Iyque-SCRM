<script setup lang="">
import * as apiCategory from '@/api/category'
import { save } from './api'
import AddOrEditMaterialDialog from '../components/AddOrEditMaterialDialog'
import SelectMaterial from '../commonMaterial/SelectMaterial'
import stores from '@/stores'
const $store = stores()
const router = useRouter()
const route = useRoute()

const isDetail = route.query.isDetail === 'true' // 是否为详情页面

const maxlength = 9 // 最大素材数量
const form = ref({})
const formRef = ref()
const groupProps = {
  expandTrigger: 'hover',
  checkStrictly: true,
  children: 'children',
  label: 'name',
  value: 'id',
  emitPath: false,
}
const talkList = ref([]) // 话术素材列表
const fontType = ref('素材') // 素材类型
const type = ref('0') // 素材类型，0:图片；4:文本；9:图文
const tree = ref([])
// 获取类目树
function getTree() {
  apiCategory.getList('13').then(({ data }) => {
    // 删除每一项的children属性
    data.forEach((item) => {
      item.children = null
    })
    tree.value = data
  })
}
getTree()

async function submit(data) {
  await formRef.value.validate()
  $store.loading = true
  return save({
    ...form.value,
    scriptSubs: talkList.value,
  })
    .then(() => {
      $sdk.msgSuccess('保存成功')
      // router.back()
      $store.loading = false
    })
    .finally(() => {
      $store.loading = false
    })
}

defineExpose({
  form,
  formRef,
  groupProps,
  talkList,
  submit,
})
</script>

<template>
  <div class="flex --Gap">
    <div class="flex-auto">
      <el-form
        class="g-card"
        ref="formRef"
        :model="form"
        :rules="{ title: [$sdk.ruleRequiredBlur], categoryId: [$sdk.ruleRequiredChange] }"
        label-width="100px">
        <el-form-item label="选择分组" prop="categoryId">
          <el-cascader v-model="form.categoryId" :options="tree" :props="groupProps"></el-cascader>
        </el-form-item>
        <el-form-item label="话术标题" prop="title">
          <el-input
            v-model="form.title"
            type="text"
            :maxlength="32"
            show-word-limit
            placeholder="请输入话术标题"></el-input>
        </el-form-item>
      </el-form>

      <div class="g-card">
        <div class="g-card-title">
          话术素材
          <el-popover trigger="hover" :content="'最多添加' + maxlength + '个素材'" placement="top-start">
            <template #reference>
              <el-icon-QuestionFilled class="el-icon-QuestionFilled"></el-icon-QuestionFilled>
            </template>
          </el-popover>
        </div>
        <div>
          <div class="--MarginT flex">
            <el-popover
              trigger="hover"
              :content="'最多添加' + maxlength + '个' + fontType + '，如需修改请删除已有' + fontType + '后重新尝试'"
              placement="top-start"
              :disabled="talkList?.length < maxlength">
              <template #reference>
                <el-dropdown
                  @command="(e) => ((type = e), $refs.dialogRef.action())"
                  :disabled="talkList?.length > maxlength || talkList?.length === maxlength">
                  <el-button type="primary" v-show="!(moduleType === 4 && otherType === 3)">
                    {{ '新建' + fontType }}
                  </el-button>
                  <template #dropdown>
                    <el-dropdown-menu trigger="click">
                      <el-dropdown-item :command="'4'">
                        <el-button text>文本</el-button>
                      </el-dropdown-item>
                      <el-dropdown-item :command="'0'">
                        <el-button text>图片</el-button>
                      </el-dropdown-item>
                      <el-dropdown-item :command="'9'">
                        <el-button text>图文</el-button>
                      </el-dropdown-item>
                    </el-dropdown-menu>
                  </template>
                </el-dropdown>
              </template>
            </el-popover>

            <AddOrEditMaterialDialog
              ref="dialogRef"
              :type="type"
              :dynamicTitle="$sdk.dictMaterialType[type]?.name"
              @confirm="
                ({ visible, loading }) =>
                  $refs.dialogRef.$refs.form
                    .validate()
                    .then(() => (talkList.push($refs.dialogRef.form), (visible.value = false)))
                    .finally(() => (loading.value = false))
              "></AddOrEditMaterialDialog>

            <el-popover
              trigger="hover"
              :content="'最多添加' + maxlength + '个' + fontType + '，如需修改请删除已有' + fontType + '后重新尝试'"
              placement="top-start"
              :disabled="talkList?.length < maxlength">
              <template #reference>
                <div class="ml20">
                  <el-button
                    @click="$refs.dialogSelectMaterialRef.dialogRef.visible = true"
                    :disabled="talkList?.length >= maxlength">
                    从素材中心选择
                  </el-button>
                </div>
              </template>
            </el-popover>

            <SelectMaterial
              ref="dialogSelectMaterialRef"
              @confirm="
                ({ visible, loading, selected }) => {
                  talkList.push(...selected)
                  visible.value = false
                  loading.value = false
                }
              " />
          </div>
        </div>
      </div>
      <div class="g-card" v-if="talkList?.length">
        <DragTable
          :data="talkList"
          @change="talkList = $event"
          @edit="(row) => ((type = $sdk.dictMaterialType[row.msgtype].type), $refs.dialogRef.action(row))"
          :dargAble="true"
          :isDetail="isDetail" />
      </div>

      <!-- <CommonTopRight>
        <el-button @click="$router.back()">取消</el-button>
        <el-button type="primary" v-loading="$store.loading" :disabled="$store.loading" @click="submit">确定</el-button>
      </CommonTopRight> -->
    </div>
    <div class="g-card mt0">
      <PhoneChatList :list="talkList" />
    </div>
  </div>
</template>

<style lang="scss" scoped></style>
