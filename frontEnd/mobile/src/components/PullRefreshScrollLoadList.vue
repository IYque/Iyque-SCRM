<!-- 下拉刷新、上拉加载列表 组件 -->
<script>
export default {
  name: '',
  props: {
    // 加载请求接口
    request: {
      type: Function,
    },
    // 查询参数
    params: {
      type: Object,
      default: () => ({}),
    },
    // 是否显示搜索框
    isQuery: {
      type: Boolean,
      default: false,
    },
    // 是否禁用滚动加载
    disabled: {
      type: Boolean,
      default: false,
    },
    // 自定义的查询参数处理方法，可用于添加和修改查询参数
    dealQueryFun: {
      type: Function,
      default: null,
    },
    // 自定义的返回数据处理方法，可用于添加和修改查询参数
    dealDataFun: {
      type: Function,
      default: null,
    },
    finishedText: {
      type: String,
      default: '没有更多了',
    },
  },
  data() {
    return {
      loading: false,
      query: {
        pageNum: 1,
        pageSize: 10,
        type: '',
      },
      refreshing: false,
      finished: false,
      error: false,
      list: [
        // {
        //   state: '跟进',
        //   content: 'asdfasdf请求失败，点击重新加载d',
        //   customerName: 'aaa森岛帆高色',
        //   sendTime: '1010-12-12 32:32:23',
        // },
        // {
        //   title: 'ssdsdssfsdfs',
        //   content: 'ssdsdssfsdfs',
        //   notificationTime: 'ssdsdssfsdfs',
        // },
        // {
        //   title: 'ssdsdssfsdfs',
        //   content: 'ssdsdssfsdfs',
        //   notificationTime: 'ssdsdssfsdfs',
        // },
      ],
    }
  },
  computed: {},
  watch: {},
  created() {
    this.getList(1)
  },
  mounted() {},
  methods: {
    getList(page) {
      if (this.disabled) {
        return
      }
      this.loading = true
      this.finished = false
      page && (this.query.pageNum = page)

      Object.assign(this.query, this.params)

      this.dealQueryFun && this.dealQueryFun(this.query)

      this.request(this.query)
        .then(({ rows, data, total = 0 }) => {
          rows ??= data || []
          if (this.query.pageNum == 1) {
            this.list = []
          }
          this.dealDataFun && this.dealDataFun(rows)

          this.list.push(...rows)
          this.loading = false
          this.refreshing = false
          // 数据全部加载完成
          if (this.list.length >= +total) {
            if (this.list.length == 0) {
              this.query.pageNum = 1
            }
            this.finished = true
          } else {
            this.query.pageNum++
          }
        })
        .catch((error) => {
          console.log(error)
          this.error = true
          this.loading = false
          // this.finished = false
        })
    },
  },
}
</script>

<template>
  <div class="PullRefreshScrollLoadList">
    <div class="query g-margin-b" v-if="isQuery">
      <van-field v-model="query.keywords" placeholder="请输入关键词" clearable>
        <template #button>
          <van-button icon="search" @click="getList(1)" />
        </template>
      </van-field>
    </div>
    <van-pull-refresh v-model="refreshing" success-text="刷新成功" @refresh="getList(1)">
      <van-list
        ref="scroll"
        v-model:loading="loading"
        :disabled="disabled"
        :finished="finished"
        :finished-text="list?.length ? finishedText : ''"
        v-model:error="error"
        error-text="请求失败，点击重新加载"
        @load="getList()"
      >
        <template v-for="(item, i) in list" :key="i">
          <slot name="item" v-bind="{ item }"></slot>
        </template>

        <div>
          <slot name="list" v-bind="{ list }"> </slot>
        </div>

        <slot name="empty" v-if="!error && !loading && !list?.length">
          <Empty />
        </slot>
      </van-list>
    </van-pull-refresh>
  </div>
</template>

<style lang="scss" scoped>
.PullRefreshScrollLoadList {
  height: 100%;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}
.query {
  border: 1px solid var(--border-black-10);
  border-radius: 6px;
  overflow: hidden;
  flex: none;
}
.van-list {
  height: 100%;
  overflow: auto;
}
</style>
