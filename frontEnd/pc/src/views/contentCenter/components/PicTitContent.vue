<template>
  <!-- 图片标题与描述 -->
  <div class="cp flex aic">
    <el-popover placement="bottom" trigger="hover">
      <template #reference>
        <el-image
          class="--RadiusSmall w-[50px] h-[50px] flex-none"
          :src="data.link?.picUrl"
          fit="contain"
          v-if="data.link?.picUrl"></el-image>
        <svg-icon class="text-[50px]" v-else icon="imgText"></svg-icon>
        <!-- <svg-icon class="text-[50px]" v-else-if="data.svgName" :icon="data.svgName"></svg-icon> -->
      </template>
      <el-image class="--Radius w-[200px] h-[200px]" :src="data.link?.picUrl" fit="contain"></el-image>
    </el-popover>

    <div class="ml10" :style="{ 'max-width': width }">
      <el-tooltip :content="data.title" placement="top" :disabled="data.title ? data.title.length < titleNum : true">
        <p style="text-align: left">
          {{ coverContent(data.title, titleNum) }}
        </p>
      </el-tooltip>
      <el-tooltip placement="top" :disabled="data.link?.desc ? data.link?.desc.length < contentNum : true">
        <template #content><p style="white-space: pre-line" v-html="data.link?.desc"></p></template>
        <p style="color: var(--font-black-7); text-align: left">
          {{ coverContent(data.link?.desc, contentNum) }}
        </p>
      </el-tooltip>
    </div>
  </div>
</template>

<script>
export default {
  props: {
    data: {
      type: Object,
      default: {},
    },
    // 标题超过多少字符。。。展示
    titleNum: {
      type: Number,
      default: 12,
    },
    // 描述超过多少字符。。。展示
    contentNum: {
      type: Number,
      default: 50,
    },
    width: {
      type: String,
      default: '200px',
    },
    // svg图标名称
    // svgName: {
    //   type: String,
    //   default: null,
    // },
  },
  watch: {},
  data() {
    return {}
  },
  mounted() {},
  methods: {
    // 超过num个。。。展示
    coverContent(str, num) {
      if (str && str.length > num) {
        str = str.substr(0, num) + '...'
      }
      return str
    },
  },
}
</script>

<style lang="scss" scoped></style>
