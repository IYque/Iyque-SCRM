<script>
import { CountTo } from 'vue3-count-to'
export default {
  // 指标卡片组
  components: { CountTo },
  props: {
    // 数据列表
    data: {
      type: Array,
      default: () => [],
    },
    isCardWrap: {
      type: Boolean,
      default: false,
    },
    defaultProps: {
      type: Object,
      default: () => ({
        title: 'title',
        value: 'value',
        tips: 'tips',
      }),
    },
  },
  data() {
    return {}
  },
  computed: {},
  watch: {},
  created() {},
  mounted() {},
  methods: {},
}
</script>

<template>
  <ul class="card-group-index" :class="{ 'card-wrap': isCardWrap }">
    <li v-for="(row, index) in data" :key="index" class="card-index-li">
      <div class="label">
        <span>{{ row[defaultProps.title] }}</span>

        <div class="tips toe mt10" v-if="row[defaultProps.tips] !== undefined">
          <el-popover trigger="hover" :content="row[defaultProps.tips]" placement="top-start">
            <template #reference>
              {{ row[defaultProps.tips] }}
            </template>
          </el-popover>
        </div>
      </div>
      <div class="mt10 value">
        <template v-if="+row[defaultProps.value] == row[defaultProps.value]">
          <CountTo class="view-item-num" :start-val="0" :end-val="+row[defaultProps.value] || 0"></CountTo>
        </template>
        <template v-else>
          {{ row[defaultProps.value] }}
        </template>
      </div>
    </li>
  </ul>
</template>

<style lang="scss" scoped>
.card-group-index {
  display: flex;
  flex-wrap: wrap;
  gap: var(--card-margin);
  margin-bottom: var(--card-margin);
  border-radius: var(--border-radius-big);
  &.card-wrap {
    display: grid;
    grid: auto/repeat(3, 1fr);
    background: var(--bg-white);
  }
}
// 指标卡片
.card-index-li {
  position: relative;
  flex: 1;
  min-width: 200px;
  // max-width: 240px;
  background: var(--bg-white);
  border-radius: var(--border-radius-big);
  // border: 1px solid var(--border-black-10);
  line-height: 1;
  padding: 22px 20px;
  .tips {
    font-size: 11px;
    color: var(--font-black-6);
  }
  .el-icon-ArrowRightBold {
    font-size: 12px;
    color: var(--font-black-6);
  }
  // // 6个以上的换行 需定宽并使用 media query
  // &:nth-child(n + 8) {
  //   margin-top: var(--card-margin);ç
  // }
  &:last-child {
    margin-right: 0;
  }
  .card-icon {
    position: absolute;
    top: 0;
    right: 0;
  }
  .label {
    position: relative;
    font-size: 14px;
  }
  .value,
  .value2 {
    font-size: 20px;
    font-weight: bold;
  }
  .value2 {
    font-size: 24px;
    line-height: 1.15;
  }
  .el-icon-warning {
    color: var(--font-black-6);
  }
  .icon-arrow {
    position: absolute;
  }

  .el-radio,
  .el-checkbox {
    position: relative;
    float: right;
    margin-right: 0;
    top: -8px;
  }

  .descend {
    color: green;
  }

  .ascend {
    color: red;
  }
}
</style>
