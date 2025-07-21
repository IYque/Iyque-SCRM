<script>
let keys = {}
let dict = {
  [(keys.edit ??= 'edit')]: {
    icon: 'el-icon-edit',
    content: '编辑',
  },
  [(keys.delete ??= 'delete')]: {
    icon: 'el-icon-delete',
    content: '删除',
  },
  [(keys.detail ??= 'detail')]: {
    icon: 'el-icon-view',
    content: '详情',
  },
  [(keys.copy ??= 'copy')]: {
    icon: 'el-icon-CopyDocument',
    content: '复制',
  },
}
</script>
<script setup>
import { useSlots, useAttrs } from 'vue'

defineProps({
  icon: {
    type: String,
    default: '',
  },
  type: {
    type: String,
    default: keys.edit,
    validator(value) {
      // 这个值必须匹配下列字符串中的一个
      return Object.keys(dict).includes(value)
    },
  },
  content: {
    type: String,
    default: '',
  },
  disabled: {
    type: Boolean,
    default: false,
  },
  disabledTip: {
    type: Boolean,
    default: true,
  },
})

const slots = useSlots()
const attrs = useAttrs()
</script>

<template>
  <div class="TableOperateBtn">
    <el-tooltip
      effect="dark"
      :content="$slots.default?.()[0].children || dict[type]?.content || $attrs.content"
      placement="top"
      :disabled="disabledTip"
      v-bind="Object.assign({}, $attrs, { class: '', style: '' })">
      <el-button
        text
        :disabled="disabled"
        :icon="icon || dict[type]?.icon"
        :title="$slots.default?.()[0].children || dict[type].content || $attrs.content">
        {{ $slots.default?.()[0].children }}
      </el-button>
    </el-tooltip>
  </div>
</template>

<style lang="scss" scoped>
.TableOperateBtn {
  display: inline-block;
  + .TableOperateBtn {
    margin-left: 10px;
  }
  .el-button {
    // font-size: 18px;
  }
}
</style>
