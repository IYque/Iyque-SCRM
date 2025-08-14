<!-- <BaseSelect class="flex-none w-[150px]" v-model="row.type" @change="(val) => saveOrUpdateFileFn(row)">
  <el-option
    v-for="(item, key) of customerStatusDict"
    :key="key"
    :label="item"
    :value="key"></el-option>
</BaseSelect> -->
<script>
import { defineComponent } from 'vue'
export default defineComponent({
  props: {
    placeholder: { type: String },
    allText: { type: String },
    isAll: { type: Boolean, default: false },
    options: { type: [Array, Object], default: () => [] },
    request: {
      type: Function,
      // default: () => () => {},
    },
    queryKeywordsKey: {
      type: String,
      default: '',
    },
    query: {
      type: Object,
      default: () => ({}),
    },
    defaultProps: {
      type: Object,
      default: () => ({ value: 'value', label: 'label' }),
    },
  },
  data() {
    return {
      loading: false,
      list: undefined,
    }
  },
  watch: {
    query() {
      this.getList()
    },
  },
  computed: {
    placeholderTransfer() {
      return (
        this.placeholder ??
        '请选择' +
          (this.$attrs.label || this.$.parent?.props?.label || '') +
          (this.queryKeywordsKey ? '，支持关键词搜索' : '')
      )
    },
    allTextTransfer() {
      return this.allText ?? '全部' + (this.$attrs.label || this.$.parent?.props?.label || '')
    },
  },
  created() {
    this.getList()
  },
  methods: {
    getList(keywords) {
      if (!this.request) {
        return
      }
      this.loading = true
      let query = Object.assign(
        { [this.queryKeywordsKey]: keywords },
        {
          pageNum: 1,
          pageSize: 1000,
        },
        this.query,
      )
      this.request(query)
        .then(({ data, rows }) => {
          let _data = data || rows
          _data.forEach((e) => {
            e.value = e[this.defaultProps.value]
            e.label = e[this.defaultProps.label]
          })
          this.list = Object.freeze(_data)
        })
        .finally(() => {
          this.loading = false
        })
    },
  },
})
</script>
<template>
  <component :is="$attrs.label || $attrs.prop ? 'BaFormItem' : 'div'" class="inline-block">
    <template #[item] v-for="(item, index) of Object.keys($slots).filter((e) => !['default'].includes(e))" :key="index">
      <slot :name="item"></slot>
    </template>

    <el-select
      class="BaSelect"
      clearable
      :placeholder="placeholderTransfer"
      :empty-values="isAll && ['', null]"
      collapse-tags
      :remote="!!request"
      reserve-keyword
      :remote-method="getList"
      :loading="loading"
      v-bind="Object.assign({}, $attrs, { style: '', class: '', id: '' })">
      <el-option v-if="isAll" :label="allTextTransfer" :value="undefined"></el-option>
      <el-option
        v-for="(item, index) in list || options"
        :key="item[defaultProps.value]"
        :label="item[defaultProps.label] || item"
        :value="item[defaultProps.value] || index"></el-option>
      <template #[item] v-for="(item, index) of Object.keys($slots)" :key="index">
        <slot :name="item"></slot>
      </template>
    </el-select>
    <slot></slot>
  </component>
</template>

<style lang="scss" scoped>
.BaseSelect {
  height: var(--heightBaseSelect);
}
:deep() .el-select__wrapper {
  border: 1px solid var(--BorderBlack10);
  background-color: transparent;
  height: var(--heightBaseSelect);
}
</style>
