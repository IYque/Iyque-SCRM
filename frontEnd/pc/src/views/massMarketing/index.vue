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
    <el-tab-pane label="群发客户">
      <list v-if="opened.includes('0')" />
    </el-tab-pane>
    <el-tab-pane label="群发客群">
      <list v-if="opened.includes('1')" type="group" />
    </el-tab-pane>
  </CacheElTabs>
</template>

<style lang="scss" scoped></style>
