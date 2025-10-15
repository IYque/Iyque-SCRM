<script setup lang="ts">
import { save, getDetail } from './api'

const SelectStaffQrCode = defineAsyncComponent(() => import('@/components/SelectStaffQrCode'))
const SelectQrCode = defineAsyncComponent(() => import('@/components/SelectQrCode'))
const SelectGetCustomerLink = defineAsyncComponent(() => import('@/components/SelectGetCustomerLink'))

import CodeLink from './components/CodeLink'
const share = ref({})

const $router = useRouter()
const $route = useRoute()

import stores from '@/stores'
const $store = stores()

const active = ref(0)
const activeControl = ref('img')

const sopSubType = {
  1: {
    label: '图片H5',
    desc: '上传落地页图片，快捷添加获客或咨询按钮，一键跳转',
  },
  5: { label: '更多类型开发中…', desc: '敬请期待…' },
}

const controlType = {
  1: { label: '客服控件', desc: '客户一键快捷咨询' },
  2: { label: '活码控件', desc: '通过活码实现营销' },
  3: { label: '链接控件', desc: '通过跳转链接实现营销' },
}

const info = ref({
  type: 1,
})
// 编辑获取详情
;(function getDetailFun(id = $route.query?.id) {
  if (!id) return
  $store.loading = true
  getDetail(id)
    .then(({ data }) => {
      Object.assign(info.value, data)
    })
    .finally(() => {
      $store.loading = false
    })
})()

function clearFields() {
  ;['controlUrl', 'controlSubType', 'title', 'titleSub', 'guideTip'].forEach((e) => {
    info.value[e] = undefined
  })
}

function remove() {
  $sdk.confirm().then(() => {
    info.value.controlType = undefined
    activeControl.value = 'img'
    clearFields()
  })
}
async function submit(submitFn, formControlRef) {
  let form = JSON.parse(JSON.stringify(info.value))
  if (!form.backgroundUrl) {
    return $sdk.msgError('请上传H5底图')
  }

  let validate = await formControlRef?.formRef.validate()
  if (!validate) {
    return
  }
  if (activeControl.value != 'img' && form.controlType) {
    if (!form.controlUrl) {
      return $sdk.msgError('请设置控件链接')
    }
    if (!form.title) {
      return $sdk.msgError('请设置主标题')
    }
    if (!form.titleSub) {
      return $sdk.msgError('请设置副标题')
    }
    if ([3, 1].includes(+form.controlType) && !form.guideTip) {
      return $sdk.msgError('请设置按钮引导文案')
    }
  }

  submitFn(() => save(form), $router.back)
}
</script>
<template>
  <div>
    <BaForm v-model="info" ref="BaFormRef">
      <template #default="{ form, isDetail, formRef }">
        <div class="g-top-step --MarginB" v-if="!isDetail">
          <BaSteps class="w-[80%] mx-auto" :active="active">
            <el-step title="基础信息" description="设置H5基础信息" />
            <el-step title="内容配置" description="设置H5具体样式与内容" />
            <!-- <el-step title="内容配置" description="H5生成后可立即使用" /> -->
          </BaSteps>
        </div>

        <div class="flex --Gap">
          <div class="g-card flex-auto" v-if="active == 0 || isDetail">
            <div class="g-card-title justify-between">
              基础信息
              <CommonTopRight v-if="isDetail">
                <BaForm :disabled="false">
                  <el-button type="primary" plain @click="$router.push({ path: 'aev', query: { id: form.id } })">
                    编辑
                  </el-button>
                  <el-button type="primary" @click=";(share = info), ($refs.shareDialogRef.visible = true)">
                    分享
                  </el-button>
                  <!-- H5分享 -->
                  <BaDialog ref="shareDialogRef" title="H5分享" size="666px">
                    <div class="--BgBlack8 --Padding --RadiusBig">
                      <CodeLink :data="share" />
                    </div>
                  </BaDialog>
                </BaForm>
              </CommonTopRight>
            </div>
            <BaInput label="H5名称" prop="name" required v-model="form.name" maxlength="15"></BaInput>
            <BaFormItem label="H5类型" prop="type" required>
              <div class="flexWrap --Gap">
                <template v-for="(item, index) in sopSubType" :key="index">
                  <div
                    class="card w-[280px] g-card --RadiusSmall mt0 --BgBlack10 border --BorderBlack9 pointer"
                    :class="form.type == index && 'active'"
                    v-if="!isDetail || form.type == index"
                    @click="index != 5 && (form.type = index)">
                    <div class="g-card-title">{{ item.label }}</div>
                    <div class="g-tip">{{ item.desc }}</div>
                  </div>
                </template>
              </div>
            </BaFormItem>
          </div>

          <div :class="isDetail || 'flex-auto grid grid-cols-[auto_415px_auto] --Gap'" v-if="active == 1 || isDetail">
            <div class="g-card" v-if="active == 1">
              <div class="g-card-title">自定义控件</div>
              <div class="grid grid-cols-2 --Gap">
                <template v-for="(item, index) in controlType" :key="index">
                  <div
                    class="card g-card --RadiusSmall mt0 --BgBlack10 --BorderBlack9 pointer"
                    :class="form.controlType == index && 'active'"
                    v-if="!isDetail || form.controlType == index"
                    @click="form.controlType == +index || clearFields(), (form.controlType = activeControl = +index)">
                    <div class="g-card-title">{{ item.label }}</div>
                    <div class="g-tip">{{ item.desc }}</div>
                  </div>
                </template>
              </div>
              <div class="g-tip text-center !mt-[50px]">更多控件开发中…</div>
            </div>

            <div class="g-card w-[415px]">
              <div class="g-card-title">H5预览</div>
              <div class="g-tip text-center !mt-[180px]" v-if="!form.backgroundUrl">
                <div>暂无图片</div>
                <Upload v-model:fileUrl="form.backgroundUrl">
                  <el-button class="!mt-[60px]" type="primary" plain>上传H5底图</el-button>
                </Upload>
              </div>

              <div class="relative" v-else>
                <!-- H5底图 -->
                <img
                  class="size-[100%] --RadiusSmall --BorderBlack11"
                  :class="activeControl == 'img' && '!border-(--Color)'"
                  :src="form.backgroundUrl"
                  fit="fill"
                  @click="activeControl = 'img'" />

                <!-- 客服控件,链接控件 -->
                <div
                  class="absolute bottom-[20px] w-[90%] mx-[5%] g-card --BorderBlack9 --RadiusSmall --BgBlack10 !overflow-visible"
                  :class="activeControl == form.controlType && '!border-(--Color)'"
                  v-if="[3, 1].includes(form.controlType)"
                  @click="activeControl = form.controlType">
                  <el-iconCircleCloseFilled
                    class="font18 absolute top-[-9px] right-[-9px] --Color pointer"
                    v-if="[3, 1].includes(activeControl)"
                    @click="remove()" />
                  <div class="truncate weight5">{{ form.title || '主标题' }}</div>
                  <div class="truncate mt15 mb-[18px]">{{ form.titleSub || '副标题' }}</div>
                  <div class="bgWhite text-center text-[red] weight5 pad15 --RadiusSmall">点击立即咨询 →</div>
                </div>

                <!-- 活码控件 -->
                <div
                  class="absolute bottom-[20px] w-[90%] mx-[5%] g-card --BorderBlack9 --RadiusSmall --BgBlack10 !overflow-visible"
                  :class="activeControl == form.controlType && '!border-(--Color)'"
                  v-if="form.controlType == 2"
                  @click="activeControl = form.controlType">
                  <el-iconCircleCloseFilled
                    class="font18 absolute top-[-9px] right-[-9px] --Color pointer"
                    v-if="[2].includes(activeControl)"
                    @click="remove()" />
                  <div class="mr10 flex items-center gap10">
                    <BaImage class="size-[80px] flex-none" :src="form.controlUrl" alt="" />
                    <div>
                      <div class="truncate weight5">{{ form.title || '主标题' }}</div>
                      <div class="truncate mt15">{{ form.titleSub || '副标题' }}</div>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            <div class="g-card" v-if="active == 1">
              <div class="g-card-title">控件设置</div>
              <div class="g-tip text-center !mt-[180px]" v-if="!form.backgroundUrl">请上传图片并添加控件后设置</div>
              <div class="relative" v-else>
                <BaFormItem v-if="activeControl == 'img'" label="H5底图" prop="backgroundUrl" required>
                  <Upload v-model:fileUrl="form.backgroundUrl"></Upload>
                </BaFormItem>

                <BaForm v-model="info" ref="formControlRef" label-position="right" :isOperate="false">
                  <template #default="{ form, isDetail, formRef }">
                    <div v-if="activeControl == 1">
                      <BaFormItem label="选择客服" prop="controlUrl" required>
                        <div v-if="form.controlUrl" class="truncate">{{ form.controlUrl }}</div>
                        <!-- <div
                          class="mr10 !inline-flex items-center gap10 --RadiusSmall border --BorderBlack11 px-[10px] py-[5px]"
                          v-if="form.controlUrl">
                          <img class="size-[50px] flex-none" :src="form._kfHeadImg" alt="" />
                          <div class="truncate">{{ form._kfName }}</div>
                        </div> -->
                        <el-button
                          class="!align-bottom"
                          type="primary"
                          :text="!!form.controlUrl"
                          @click="$refs.SelectCustomerServiceRef.dialogRef.visible = true">
                          {{ form.controlUrl ? '编辑' : '选择客服' }}
                        </el-button>
                        <SelectCustomerService
                          ref="SelectCustomerServiceRef"
                          isSigleSelect
                          @confirm="
                            ({ selected }) => ((form.controlUrl = selected.kfUrl), formRef.validateField('controlUrl'))
                          "></SelectCustomerService>
                      </BaFormItem>
                    </div>
                    <div v-if="activeControl == 2">
                      <BaFormItem label="活码类型" prop="controlSubType" required>
                        <!-- <div class="g-card --BgBlack10"> -->
                        <BaRadioGroup
                          v-model="form.controlSubType"
                          :options="[
                            { label: '员工活码', value: 1, desc: '扫码添加企业员工' },
                            { label: '无限群码', value: 2, desc: '扫码进入企业客群' },
                          ]"
                          @change="form.controlUrl = ''"></BaRadioGroup>
                        <!-- </div> -->
                      </BaFormItem>

                      <BaFormItem label="选择活码" prop="controlUrl" required>
                        <BaImage
                          class="mr10 size-[80px] --RadiusSmall border --BorderBlack11"
                          v-if="form.controlUrl"
                          :src="form.controlUrl"
                          fit="fill"></BaImage>
                        <el-button
                          class="!align-bottom"
                          :disabled="!form.controlSubType"
                          type="primary"
                          :text="!!form.controlUrl"
                          @click="
                            $refs[
                              form.controlSubType == 1 ? 'SelectStaffQrCodeRef' : 'SelectQrCodeRef'
                            ].dialogRef.visible = true
                          ">
                          {{ form.controlUrl ? '编辑' : '选择活码' }}
                        </el-button>
                        <SelectStaffQrCode
                          v-if="form.controlSubType == 1"
                          ref="SelectStaffQrCodeRef"
                          isSigleSelect
                          @confirm="
                            ({ selected }) => {
                              form.controlUrl = selected.codeUrl
                              formRef.validateField('controlUrl')
                            }
                          "></SelectStaffQrCode>
                        <SelectQrCode
                          v-if="form.controlSubType == 2"
                          ref="SelectQrCodeRef"
                          isSigleSelect
                          @confirm="
                            ({ selected }) => {
                              form.controlUrl = selected.chatCodeUrl
                              formRef.validateField('controlUrl')
                            }
                          "></SelectQrCode>
                      </BaFormItem>
                    </div>
                    <div v-if="activeControl == 3">
                      <BaFormItem label="链接类型" prop="controlSubType" required>
                        <BaRadioGroup
                          v-model="form.controlSubType"
                          :options="[
                            { label: '获客链接', value: 1, desc: '点击链接添加员工' },
                            { label: '自定义链接', value: 4, desc: '手动填写跳转链接' },
                          ]"></BaRadioGroup>
                      </BaFormItem>

                      <BaFormItem v-if="form.controlSubType == 1" label="选择链接" prop="controlUrl" required>
                        <div v-if="form.controlUrl" class="truncate">{{ form.controlUrl }}</div>
                        <el-button
                          type="primary"
                          :text="!!form.controlUrl"
                          @click="$refs.SelectGetCustomerLinkRef.dialogRef.visible = true">
                          {{ form.controlUrl ? '编辑' : '选择链接' }}
                        </el-button>
                        <SelectGetCustomerLink
                          ref="SelectGetCustomerLinkRef"
                          isSigleSelect
                          @confirm="
                            ({ selected }) => {
                              form.controlUrl = selected.codeUrl
                              formRef.validateField('controlUrl')
                            }
                          "></SelectGetCustomerLink>
                      </BaFormItem>
                      <BaInput
                        v-if="form.controlSubType == 4"
                        label="设置链接"
                        prop="controlUrl"
                        required
                        v-model="form.controlUrl"></BaInput>
                    </div>
                    <div v-if="[1, 2, 3].includes(+activeControl)">
                      <BaInput label="主标题" prop="title" required v-model="form.title" maxlength="10"></BaInput>
                      <BaInput label="副标题" prop="titleSub" required v-model="form.titleSub" maxlength="15"></BaInput>
                    </div>
                    <BaInput
                      v-if="[3, 1].includes(activeControl)"
                      label="按钮引导文案"
                      prop="guideTip"
                      required
                      v-model="form.guideTip"
                      maxlength="10"></BaInput>
                  </template>
                </BaForm>
              </div>
            </div>
          </div>
        </div>
      </template>
      <template #operate="{ form, isDetail, formRef, submitFn }">
        <BaFormBar>
          <el-form>
            <ElButton type="primary" plain @click="$router.back()">取消</ElButton>
            <ElButton v-if="active > 0" type="primary" plain @click="active--">← 上一步</ElButton>
            <ElButton v-if="active < 1" type="primary" @click="formRef.validate().then(() => active++)">
              下一步 →
            </ElButton>
            <ElButton
              v-if="active == 1"
              type="primary"
              v-loading="$store.loading"
              :disabled="$store.loading"
              @click="submit(submitFn, $refs.formControlRef)">
              确定
            </ElButton>
          </el-form>
        </BaFormBar>
      </template>
    </BaForm>
  </div>
</template>

<style scoped lang="scss">
.card {
  &:hover,
  &.active {
    color: var(--Color);
    border-color: var(--Color);
    transition: all 0.3s;
    background: var(--ColorLight11);
    .g-card-title,
    .g-tip {
      color: inherit;
    }
  }
}
</style>
