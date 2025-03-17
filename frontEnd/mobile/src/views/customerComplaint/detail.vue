<script setup lang="js">
import { getDetail, handleComplaint } from './api'
const route = useRoute()

const form = ref({})
const form2 = ref({})
const show = ref(false)

// 获取详情
function getDetailFn() {
  getDetail(route.query.id).then((res) => {
    form.value = res.data
  })
}
getDetailFn()

import { showToast, showLoadingToast, showSuccessToast, closeToast } from 'vant'

function onSubmit() {
  let _form2 = form2.value

  if (!_form2.handleContent) {
    showToast('请填写处理内容')
    return
  }
  _form2.complainAnnexList = _form2.complainAnnexList?.map((item) => {
    return {
      annexType: 2,
      annexUrl: item.url,
    }
  })
  showLoadingToast({
    // message: '加载中...',
    forbidClick: true,
    loadingType: 'spinner',
  })
  handleComplaint(_form2)
    .then((res) => {
      showSuccessToast('操作成功')
      show.value = false
      getDetailFn()
    })
    .finally(() => {
      closeToast()
    })
}
</script>

<template>
  <div class="pad15">
    <van-cell-group>
      <van-field v-model="form.complainTypeContent" label="投诉类型" placeholder="投诉类型" label-align="top" />
      <van-field v-model="form.complainTime" label="投诉时间" placeholder="投诉时间" label-align="top" />
      <van-field v-model="form.complainUserPhone" label="联系方式" placeholder="联系方式" label-align="top" />
      <van-field v-model="form.complainUserContent" label="投诉内容" placeholder="投诉内容" label-align="top" />
      <van-field v-model="form.value" label="投诉举证" placeholder="投诉举证" label-align="top">
        <template #input>
          <div class="flexGap10 gap5">
            <van-image
              v-for="(item, index) in form.complainAnnexList?.filter((e) => e.annexType == 1)"
              :key="index"
              width="80"
              height="80"
              :src="item.annexUrl"
              fit="fill"
              :lazy="true" />
          </div>
        </template>
      </van-field>
    </van-cell-group>

    <van-cell-group class="mt15" v-if="form.handleState == 2">
      <van-field v-model="form.complainUserPhone" label="处理人" placeholder="处理人" label-align="top" />
      <van-field v-model="form.complainUserContent" label="处理时间" placeholder="处理时间" label-align="top" />
      <van-field v-model="form.complainUserContent" label="处理内容" placeholder="处理内容" label-align="top" />
      <van-field v-model="form.value" label="处理证明" placeholder="处理证明" label-align="top">
        <template #input>
          <div class="flexGap10 gap5">
            <van-image
              v-for="(item, index) in form.complainAnnexList?.filter((e) => e.annexType == 2)"
              :key="index"
              width="80"
              height="80"
              :src="item.annexUrl"
              fit="fill"
              :lazy="true" />
          </div>
        </template>
      </van-field>
    </van-cell-group>

    <div class="text-center mt-[30px]" v-if="form.handleState == 1">
      <van-button type="success" class="!px-[41px]" size="small" @click="show = true">处理投诉</van-button>
    </div>

    <van-action-sheet
      v-model:show="show"
      title="处理投诉"
      round
      position="bottom"
      :closeable="false"
      :close-on-click-overlay="false">
      <van-form @submit="onSubmit">
        <van-field
          class="textarea"
          v-model="form2.handleContent"
          type="textarea"
          :autosize="{ maxHeight: 100, minHeight: 50 }"
          label="处理内容(必填)"
          placeholder="请输入处理内容"
          label-align="top" />

        <van-field class="upload" label="处理证明(选填)" placeholder="请选择" label-align="top">
          <!-- <template #label>
            <span>图片</span>
            <van-icon name="info ml10" @click.native.stop="showDialog('upload')" />
          </template> -->
          <template #input>
            <Upload ref="upload" v-model:fileList="form2.complainAnnexList" multiple limit="9" result-type="file">
              <template #tip>
                <div class="">最多上传9张图片</div>
              </template>
            </Upload>
          </template>
        </van-field>
        <div class="footer flex" style="margin: 20px">
          <van-button class="" native-type="button" round block plain @click="show = false">取消</van-button>
          <van-button type="success" native-type="submit" round block class="ml10">提交</van-button>
        </div>
      </van-form>
    </van-action-sheet>
  </div>
</template>

<style lang="scss" scoped></style>
