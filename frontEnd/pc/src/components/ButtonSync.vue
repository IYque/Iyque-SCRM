<script>
export default {
  name: 'ButtonSync',
  emits: ['click'],
  props: {
    timeVisible: {
      type: Boolean,
      default: false,
    },
    lastSyncTime: {
      type: [String, Number],
      default: '',
    },
  },
  components: {},
  data() {
    return {}
  },
  computed: {
    disabled() {
      // return this.lastSyncTime ? (+new Date() - +new Date(this.lastSyncTime)) / 3600000 < 2 : false
      return false
    },
  },
  watch: {},
  created() {},
  mounted() {},
  methods: {
    sync() {
      // if (this.disabled) {
      //   this.msgError('由于企业微信开放平台的限制，两小时内不得重复同步操作')
      //   return
      // }
      this.$emit('click')
    },
  },
}
</script>

<template>
  <div class="button-sync">
    <!-- <el-tooltip
      v-if="disabled"
      effect="light"
      class="item"
      content="由于企业微信开放平台的限制，两小时内不得重复同步操作"
      placement="top-start">
      <el-tag type="info" size="default"><slot></slot></el-tag>
    </el-tooltip> -->
    <div v-preventReClick class="cp" @click="sync">
      <slot v-if="$slots.button" name="button"></slot>
      <el-button v-else type="primary">
        <slot></slot>
      </el-button>

      <span class="desc" v-if="timeVisible">最近同步：{{ lastSyncTime }}</span>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.button-sync {
  display: inline-block;
}
.desc {
  margin: 0 5px;
  font-size: 12px;
  color: var(--font-black-5);
}
</style>
