<!-- 标签省略 收起展开 组件 -->
<script>
export default {
  name: 'TagEllipsis',
  components: {},
  props: {
    // tag 列表
    // 示例数据 eg：['tag1',tag2] 或 [{name:'tag1'},{name:'tag1'}]
    list: {
      type: Array,
      default: () => []
    },
    // 显示多少个开始折叠
    limit: {
      type: [String, Number],
      default: 2
    },
    // list为对象数组时，tag对象显示的属性名  默认：name， {name:'***'}
    defaultProps: {
      type: String,
      default: 'name'
    },
    // 空数据时显示的文本内容
    emptyText: {
      type: String,
      default: ''
    }
  },
  data() {
    return {
      showMoreTag: false
    }
  },
  computed: {
    _list() {
      return this.list?.filter((e) => (typeof e === 'string' ? e : e[this.defaultProps]))
    }
  },
  watch: {},
  created() {
    // 可传入 van-tag 的props
    this.elTagProps = Object.assign({}, this.$attrs)
    delete this.elTagProps.style
  },
  mounted() {},
  methods: {}
}
</script>

<template>
  <div class="tag-ellipsis" v-if="_list?.length || emptyText">
    <div v-if="!_list?.length">{{ emptyText }}</div>

    <div class="tag-all">
      <van-tag
        v-bind="elTagProps"
        round
        v-for="(item, index) in _list.slice(0, showMoreTag ? undefined : +limit)"
        :key="index"
      >
        {{ item[defaultProps] || item }}
      </van-tag>
      <van-tag
        v-if="_list.length > +limit"
        v-bind="elTagProps"
        round
        type="primary"
        key="a"
        @click="showMoreTag = !showMoreTag"
      >
        {{ showMoreTag ? '收起' : `...展开全部${_list.length}个` }}
      </van-tag>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.tag-ellipsis {
  display: inline-block;
  line-height: 34px;
  .van-tag {
    vertical-align: middle;
  }
  .tag-all {
    display: flex;
    flex-wrap: wrap;
    gap: 10px;
    transition: all 0.3s;
    .van-tag {
      margin: 0;
    }
  }
  .van-tag {
    padding: 4px 8px;
  }
}
</style>
