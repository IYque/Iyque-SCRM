<template>
  <div>
    <!-- 基础素材 -->
    <CacheElTabs v-model="activeName" #="{ opened }">
      <el-tab-pane v-for="(item, index) in list" :key="index" :label="item.label">
        <component
          v-if="opened.includes(index + '')"
          :is="item.component"
          :isSelect="isSelect"
          ref="maPage"
          @selectionChange="isSelect && selectionChange($event)"></component>
      </el-tab-pane>
    </CacheElTabs>
  </div>
</template>

<script>
import Mtext from './text'
import Mimage from './image'
import MimageText from './imageText'
export default {
  props: {
    isSelect: { type: Boolean, default: false },
  },
  components: {
    // Mtext,
    // Mimage,
    // MimageText,
    // Mapplet,
  },
  data() {
    return {
      activeName: '0',
      list: [
        { label: '文本', name: 'text', component: Mtext },
        { label: '图片', name: 'image', component: Mimage },
        { label: '图文', name: 'imageText', component: MimageText },
      ],
      selected: [],
    }
  },
  methods: {
    selectionChange(data) {
      let newSelect = data.filter((item) => {
        return !this.selected[this.activeName]?.some((e) => e.id === item.id)
      })
      let total = this.selected.reduce((total, item) => {
        return (total += item.length)
      }, 0)
      if (total + newSelect.length > 9) {
        $sdk.msgError('最多只能选择9个素材')
        newSelect.forEach((item) => {
          this.$refs.maPage[this.activeName].$refs.page.$refs.rctRef.toggleRowSelection(item, false)
        })
        return
      }

      this.selected[this.activeName] = data

      this.$emit(
        'selectionChange',
        this.selected.reduce((total, item) => {
          return total.concat(item)
        }, []),
      )
    },
  },
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
