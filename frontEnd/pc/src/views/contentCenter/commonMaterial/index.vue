<template>
  <div>
    <!-- 基础素材 -->
    <CacheElTabs v-model="activeName" #="{ opened }">
      <el-tab-pane v-for="(item, index) in list" :key="index" :label="item.label" :name="item.name">
        <component v-if="opened.includes(item.name)" :is="item.component"></component>
      </el-tab-pane>
    </CacheElTabs>
  </div>
</template>

<script>
import Mtext from './text'
import Mimage from './image'
import MimageText from './imageText'
export default {
  name: '',
  components: {
    // Mtext,
    // Mimage,
    // MimageText,
    // Mapplet,
  },
  data() {
    return {
      activeName: 'text',
      list: [
        { label: '文本', name: 'text', component: Mtext },
        { label: '图片', name: 'image', component: Mimage },
        { label: '图文', name: 'imageText', component: MimageText },
      ],
    }
  },
  methods: {},
  created() {
    this.$store.setBusininessDesc(
      `
      <div>文本、图片等基础素材，可直接用于聊天话术、欢迎语等场景</div>
    `,
    )
  },
}
</script>

<style></style>
