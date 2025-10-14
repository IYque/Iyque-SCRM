<template>
  <!-- 空状态，没有数据的时候 -->
  <div class="BaEmpty">
    <el-empty v-bind="Object.assign({}, $attrs, { style: '', class: '', id: '' })" />
    <!-- <svg-icon class="right-icon" :icon="nodataObj[type].svgName"></svg-icon>
    <div class="nodata-text">{{ description}}</div> -->
  </div>
</template>

<script>
export default {
  props: {
    // 显示的提示文字
    // description: {
    //   type: String,
    //   default: null,
    // },
  },
  data() {
    return {}
  },
}
</script>

<style lang="scss" scoped>
.BaEmpty {
  // width: 100%;
  // height: 100%;
  display: flex;
  flex-direction: column;
  text-align: center;
  align-items: center;
  justify-content: center;
  // .right-icon {
  //   width: 80px;
  //   height: 64px;
  // }
  // .nodata-text {
  //   font-size: 14px;
  //   font-weight: 400;
  //   color: #4e5969;
  //   line-height: 22px;
  // }
}
</style>
