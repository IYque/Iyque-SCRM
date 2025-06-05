<!-- 客户 头像、名称、姓名信息组合单元 -->
<script>
import profile from '@/assets/icons/svg/user.svg'
export default defineComponent({
  props: {
    data: { type: Object, default: () => ({}) },
  },
  components: {},
  data() {
    return { profile }
  },
  computed: {},
  watch: {},
  created() {},
  mounted() {},
  methods: {},
})
</script>

<template>
  <div
    class="CustomerInfoCell"
    @click="
      data.addUserId &&
        $router.push({
          name: 'customerDetail',
          query: { externalUserid: data.externalUserid, userId: data.addUserId },
        })
    ">
    <el-image class="avatar" :src="data.avatar || profile" fit="fill"></el-image>
    <div class="ml10">
      <p class="blod">{{ data.customerName }}</p>
      <el-icon-Avatar :class="['el-icon-Avatar', { 1: 'man', 2: 'woman' }[data.gender]]" />
      <span :style="{ color: data.type === 1 ? '#4bde03' : '#f9a90b' }">
        {{ { 1: '@微信', 2: '@企业微信' }[data.type] }}
      </span>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.CustomerInfoCell {
  display: flex;
  align-items: center;
  cursor: pointer;
  text-align: left;
}
.avatar {
  width: 56px;
  height: 56px;
  flex: none;
  border-radius: var(--border-radius-big);
}
.el-icon-Avatar {
  font-size: 16px;
  // margin-left: 4px;
  color: var(--font-black-6);
  &.man {
    color: #13a2e8;
  }
  &.woman {
    color: #f753b2;
  }
}
</style>
