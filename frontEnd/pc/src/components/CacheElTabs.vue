<!-- 扩展<el-tabs></el-tabs> 缓存当前激活索引和组件 -->
<script>
export default {
  props: {
    modelValue: '',
  },
  data() {
    return {
      active: this.$route.query.index || this.modelValue || '0',
      opened: [this.$route.query.index || this.modelValue || '0'],
    }
  },
  computed: {},
  watch: {
    modelValue: {
      handler(val) {
        this.active = val
      },
    },
    active: {
      // immediate: true,
      handler(val) {
        // debugger
        this.opened.includes(val) || this.opened.push(val)
        this.$emit('update:modelValue', val)
      },
    },
  },
  created() {
    // this.active = this.$route.query.index || this.modelValue || '0'
  },
  mounted() {},
  methods: {
    tabClick(v) {
      this.active = v.paneName
      this.opened.includes(v.paneName) || this.opened.push(v.paneName)
      this.$router.replace({ path: this.$route.path, query: { ...this.$route.query, index: v.paneName } })
    },
  },
}
</script>

<template>
  <el-tabs class="CacheElTabs" v-model="active" @tab-click="tabClick">
    <slot v-bind="{ active, opened }"></slot>
  </el-tabs>
</template>

<style lang="scss" scoped>
.CacheElTabs.el-tabs--top {
  // z-index: 99;
  // position: sticky !important;
  // top: 0;
  ::v-deep > .el-tabs__header {
    z-index: 99;
    position: sticky !important;
    top: 0;
    background: var(--bg-white);
    border-radius: var(--border-radius);
    box-shadow: 0px 4px 20px 0px var(--bg-black-10);
    overflow: hidden;
    .el-tabs__item.is-top:nth-child(2) {
      margin-left: 20px;
    }
    .el-tabs__item.is-top:last-child {
      margin-right: 20px;
    }
  }
}
</style>
