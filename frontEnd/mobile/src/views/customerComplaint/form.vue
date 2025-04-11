<script setup lang="ts">
import { findComplaint, addComplaint } from './api'
const form = ref({})
const type = ref([])
const step = ref(1)
const selectedType = ref([])

// 获取投诉类型列表
function findComplaintFn() {
  findComplaint().then((res) => {
    type.value = res.data
  })
}
findComplaintFn()

import { showToast, showLoadingToast, showSuccessToast, closeToast } from 'vant'
function onSubmit() {
  let _form = JSON.parse(JSON.stringify(form.value))

  if (!_form.complainUserPhone) {
    showToast('请填写联系方式')
    return
  }
  _form.complainAnnexList = _form.complainAnnexList?.map((item) => {
    return {
      // annexType: 2,
      annexUrl: item.url,
    }
  })
  _form.complainType = selectedType.value[1]?.subCode || selectedType.value[0].code
  // .map((item) => {
  //   return item.value
  // })
  // .join('-')
  showLoadingToast({
    // message: '加载中...',
    forbidClick: true,
    loadingType: 'spinner',
  })
  addComplaint(_form)
    .then((res) => {
      showSuccessToast('提交成功')
      step.value = 4
    })
    .finally(() => {
      closeToast()
    })
}
</script>

<template>
  <div>
    <div v-show="step == 1">
      <div class="title">请选择投诉该账号的原因：</div>
      <van-cell-group>
        <van-cell
          v-for="(item, index) in type"
          :key="index"
          :title="item.value"
          is-link
          @click="(item.subTypes?.length ? (step = 2) : (step = 3), (selectedType = [item]))" />
      </van-cell-group>
    </div>
    <div v-if="step == 2">
      <div class="title">请选择哪一类违法内容：</div>
      <van-cell-group>
        <van-cell
          v-for="(item, index) in selectedType[0].subTypes"
          :key="index"
          :title="item.value"
          is-link
          @click="(selectedType.push(item), (step = 3))" />
      </van-cell-group>
    </div>
    <div v-if="step == 3">
      <div class="title">{{ selectedType[0]?.value }}/{{ selectedType[1]?.value }}</div>
      <van-form @submit="onSubmit">
        <van-cell-group inset>
          <van-field
            v-model="form.complainUserPhone"
            label="联系方式(必填)"
            placeholder="请输入联系方式"
            label-align="top" />
          <van-field
            class="textarea"
            v-model="form.complainUserContent"
            type="textarea"
            :autosize="{ maxHeight: 100, minHeight: 50 }"
            label="投诉内容(选填)"
            placeholder="请输入投诉内容"
            label-align="top" />

          <van-field class="upload" label="投诉举证(选填)" placeholder="请选择" label-align="top">
            <!-- <template #label>
            <span>图片</span>
            <van-icon name="info ml10" @click.native.stop="showDialog('upload')" />
          </template> -->
            <template #input>
              <Upload ref="upload" v-model:fileList="form.complainAnnexList" multiple limit="9" result-type="file">
                <template #tip>
                  <div class="">最多上传9张图片</div>
                </template>
              </Upload>
            </template>
          </van-field>
        </van-cell-group>
        <div class="text-center mt-[30px]">
          <van-button type="success" native-type="submit" class="!px-[41px]" size="small">提交</van-button>
        </div>
      </van-form>
    </div>
    <div v-if="step == 4">
      <div class="text-center centerCenter">
        <van-icon name="checked" size="90" color="var(--ColorSuccess)" />
        <div class="font20 bold leading-[60px]">投诉已提交</div>
        <div class="--FontBlack5 bold leading-[30px] whitespace-nowrap">
          我们会尽快核实处理，并通知你结果。
          <br />
          感谢你的支持
        </div>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.title {
  // @apply font12 --FontBlack3;
  font-size: 12px;
  color: var(--FontBlack3);
  padding: 10px 0 10px 10px;
}
</style>
