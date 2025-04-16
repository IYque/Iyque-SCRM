<script>
export default {
  name: '',
  components: {
    list: defineAsyncComponent(() => import('./list')),
    statistics: defineAsyncComponent(() => import('./statistics')),
  },
  data() {
    return {}
  },
  computed: {},
  watch: {},
  created() {},
  mounted() {},
  methods: {},
}
</script>

<template>
  <CacheElTabs #="{ opened }">
    <el-tab-pane label="客服配置">
      <list v-if="opened.includes('0')" />
    </el-tab-pane>
    <el-tab-pane label="客服统计">
      <statistics v-if="opened.includes('1')" />
    </el-tab-pane>
  </CacheElTabs>
</template>

<style lang="scss" scoped></style>
