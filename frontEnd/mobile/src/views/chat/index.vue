<script>
import List from './List.vue'

import useStore from '@/stores/index.js'

export default {
  components: { List },
  props: {},
  data() {
    return {
      keyword: '',
      active: 0,
      list: [
        { type: '4', name: '文本', sort: 1 },
        { type: '0', name: '图片', sort: 2 },
        { type: '9', name: '图文', sort: 3 },
      ],
      loading: false,
      finished: false,
      show: false,
      query: {},
      type: '', //分组类型
      // userId: useStore().userId,
    }
  },
  watch: {
    userId() {
      this.getList()
    },
  },
  computed: {
    userId() {
      return useStore().userId
    },
  },
  created() {},
  mounted() {},
  methods: {
    search(pageNum) {
      this.$refs['list' + this.active]?.[0].search(pageNum, this.keyword)
    },
    reset() {
      this.keyword = ''
      this.$nextTick(() => this.search(1))
    },
  },
}
</script>

<template>
  <div>
    <van-search v-model="keyword" show-action placeholder="请输入关键词，在当前分类分组下搜索" @search="search(1)">
      <template #action>
        <span @click="search(1)">搜索</span>
        <span class="ml5" @click="reset">重置</span>
      </template>
    </van-search>
    <div class="tabs">
      <van-tabs v-model:active="active" @click-tab="getList">
        <van-tab :title="item.name" v-for="(item, index) in list" :key="index">
          <List :ref="'list' + index" :sideId="item.id" :mediaType="item.type + ''"></List>
        </van-tab>
      </van-tabs>
    </div>

    <!-- <van-dialog
      v-model="show"
      :title="`添加&quot;我的&quot;${radio}`"
      show-cancel-button
    >
      <van-form @submit="onSubmit">
        <van-field name="radio" label="添加类型">
          <template #input>
            <van-radio-group v-model="radio" direction="horizontal">
              <van-radio name="1">文本</van-radio>
              <van-radio name="2">分类</van-radio>
            </van-radio-group>
          </template>
        </van-field>
        <van-field
          v-model="username"
          name="分类名称"
          label="分类名称"
          placeholder="分类名称"
          :rules="[{ required: true, message: '请填写分类名称' }]"
        />
        <template>
          <van-field
            readonly
            clickable
            name="picker"
            :value="value"
            label="文本分类"
            placeholder="点击选择文本分类"
            @click="showPicker = true"
          />
          <van-popup v-model="showPicker" position="bottom">
            <van-picker
              show-toolbar
              :columns="columns"
              @confirm="onConfirm"
              @cancel="showPicker = false"
            />
          </van-popup>

          <van-field
            v-model="message"
            rows="5"
            autosize
            label="文本信息"
            type="textarea"
            maxlength="150"
            placeholder="请输入文本信息"
            show-word-limit
          />
        </template>
      </van-form>
    </van-dialog> -->
  </div>
</template>

<style lang="scss" scoped>
.van-tab__pane {
  display: flex;
}
.search {
  width: 100%;
  position: fixed;
  top: 0px;
}
.tabs {
  position: relative;
  height: calc(100vh - 54px);
  overflow: auto;
}
.page {
  height: 100vh;
  background: #f6f6f6;
}
::deep() .list {
  padding: 10px;
  background: #fff;
  border-top: 1px solid #eee;
  .info {
    padding-top: 10px;
  }
  .action {
    padding-left: 5px;
  }
}
</style>
