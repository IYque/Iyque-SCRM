<script>
import List from './List.vue'

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
    }
  },
  watch: {},
  computed: {},
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
      <van-tabs v-model:active="active">
        <van-tab :title="item.name" v-for="(item, index) in list" :key="index">
          <List :ref="'list' + index" :sideId="item.id" :mediaType="item.type + ''"></List>
        </van-tab>
      </van-tabs>
    </div>
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
