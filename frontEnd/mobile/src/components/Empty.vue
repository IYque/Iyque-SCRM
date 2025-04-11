<template>
  <div class="empty">
    <!-- <img src="@/assets/empty.png" alt="" /> -->
    <svg-icon class="empty-icon" name="empty"></svg-icon>
    <span>{{ description }}</span>
  </div>
</template>

<script>
export default {
  props: {
    // 显示的提示文字
    description: {
      type: String,
      default: '暂无数据',
    },
  },
}
</script>

<style lang="scss" scoped>
.empty {
  width: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  flex-direction: column;
  padding: 10px 0 20px;
  // img {
  //   width: 100px;
  //   height: 100px;
  // }
  .empty-icon {
    width: 100px;
    height: 100px;
  }
  span {
    font-size: 12px;
    font-weight: normal;
    color: #cccccc;
    line-height: 12px;
  }
}
</style>
