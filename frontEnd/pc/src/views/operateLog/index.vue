<script>
export default {
  name: '',
  components: {
    list: defineAsyncComponent(() => import('./list')),
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
    <el-tab-pane label="成员操作">
      <list v-if="opened.includes('0')" />
    </el-tab-pane>
    <el-tab-pane label="管理员操作">
      <list v-if="opened.includes('1')" type="2" />
    </el-tab-pane>
  </CacheElTabs>
</template>

<style lang="scss" scoped></style>
