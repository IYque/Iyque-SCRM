<template>
  <div>
    <!-- 表格拖拽组件 -->
    <el-table :data="tableData" ref="singleTable" highlight-current-row row-key="id" class="load_table">
      <!-- <el-table-column type="index" width="50" label="序号" align="center"></el-table-column> -->
      <el-table-column label="" width="50" align="left" v-if="dargAble">
        <svg-icon icon="dragIcon" />
      </el-table-column>

      <el-table-column prop="name" label="素材" align="left">
        <template #default="{ row }">
          <el-tooltip :content="row.title" placement="top" :disabled="row.title ? row.title.length < 12 : true">
            <span class="title">{{ coverContent(row.title, 12) }}</span>
          </el-tooltip>
          <!-- 文本 -->
          <div v-if="row.msgtype === $sdk.dictMaterialType['4'].msgtype">
            <el-tooltip placement="top" :disabled="row.text.content ? row.text.content.length < 50 : true">
              <template #content><p style="white-space: pre-line" v-html="row.text.content"></p></template>
              <span class="twosplice">{{ coverContent(row.text.content, 50) }}</span>
            </el-tooltip>
          </div>
          <!-- 图片 -->
          <div v-if="row.msgtype === $sdk.dictMaterialType['0'].msgtype">
            <el-image :src="row.image.picUrl" fit="contain" class="imgsize"></el-image>
          </div>
          <!-- 图文 -->
          <div v-if="row.msgtype === $sdk.dictMaterialType['9'].msgtype" style="display: flex">
            <el-image :src="row.link.picUrl" fit="contain" class="imgsize" v-if="row.link.picUrl"></el-image>
            <div class="icon-style" v-else>
              <svg-icon class="icon-style" icon="imgText"></svg-icon>
            </div>
            <el-tooltip
              :content="row.link.desc"
              placement="top"
              :disabled="row.link.desc ? row.link.desc.length < 50 : true">
              <span class="twosplice distStyle">{{ coverContent(row.link.desc, 50) }}</span>
            </el-tooltip>
          </div>
        </template>
      </el-table-column>

      <el-table-column prop="msgtype" width="" label="类型">
        <template #default="{ row }">
          <div>
            {{ Object.values($sdk.dictMaterialType).find((e) => e.msgtype === row.msgtype).name }}
          </div>
        </template>
      </el-table-column>

      <el-table-column prop="" label="操作">
        <template #default="{ row, $index }">
          <el-button text @click="$emit('edit', row)">编辑</el-button>
          <el-button text @click="remove($index)" v-if="!isDetail">移除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
import Sortable from 'sortablejs'
export default {
  props: {
    data: {
      type: Array,
      default: [],
    },
    // 是否可拖拽
    dargAble: {
      type: Boolean,
      default: false,
    },
    // 是否为详情页面
    isDetail: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {
      tableData: [],
    }
  },
  watch: {
    data: {
      handler(val) {
        this.tableData = JSON.parse(JSON.stringify(val))
        this.tableData.forEach((item, index) => {
          item._index = index + ''
        })
      },
      immediate: true,
      deep: true,
    },
  },

  mounted() {
    if (this.dargAble) {
      this.dragSort()
    }
  },

  methods: {
    remove(index) {
      this.tableData.splice(index, 1)
      this.$emit('change', this.tableData)
    },
    // 超过num个。。。展示
    coverContent(str, num) {
      if (str && str.length > num) {
        str = str.substr(0, num) + '...'
      }
      return str
    },
    //表格拖动排序
    dragSort() {
      const el = this.$refs.singleTable.$el.querySelectorAll('.el-table__body-wrapper  table > tbody')[0]

      this.sortable = Sortable.create(el, {
        ghostClass: 'sortable-ghost',

        setData: function (dataTransfer) {
          dataTransfer.setData('Text', '')
        },

        onEnd: (e) => {
          //e.oldIndex为拖动一行原来的位置，e.newIndex为拖动后新的位置

          const targetRow = this.tableData.splice(e.oldIndex, 1)[0]

          this.tableData.splice(e.newIndex, 0, targetRow)

          // console.log(this.tableData, '排序后的数据')
          this.$emit('setData', this.tableData)

          let dragId = this.tableData[e.newIndex].id //拖动行的id

          let oneId, twoId

          //拖动行的前一行

          if (this.tableData[e.newIndex - 1]) {
            oneId = this.tableData[e.newIndex - 1].id
          } else {
            oneId = ''
          }

          //拖动行的后一行

          if (this.tableData[e.newIndex + 1]) {
            twoId = this.tableData[e.newIndex + 1].id
          } else {
            twoId = ''
          }

          console.log(dragId, oneId, twoId)
          // debugger
          // this.postRequest({
          //   url: this.dragUrl,

          //   data: {
          //     dragId: dragId,

          //     oneId: oneId,

          //     twoId: twoId,

          //     projectId: '',
          //   },

          //   success: (result) => {
          //     if (result) {
          //       this.$message({
          //         message: '拖动排序成功!',

          //         type: 'success',
          //       })
          //     } else {
          //       this.$message({
          //         message: '拖动排序失败！',

          //         type: 'error',
          //       })
          //     }
          //   },
          // })
        },
      })
    },
  },
}
</script>

<style lang="scss" scoped>
.icon-style {
  width: 50px;
  height: 50px;
  align-items: center;
}
.title {
  width: 80%;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-weight: bold;
  color: var(--font-black-7);
  font-size: 12px;
}
.imgsize {
  width: 50px;
  height: 50px;
  margin-right: 10px;
}
.distStyle {
  align-items: center;
  margin-left: 10px;
}
::v-deep .el-button.is-text.is-disabled {
  color: inherit;
}
</style>
