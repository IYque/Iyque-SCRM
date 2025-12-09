<template>
  <div class="search">
    <div class="item">
      <el-button-group>
        <el-button @click="setType(0)" :type="active === 0 ? 'primary' : ''" v-if="isToday">今日</el-button>
        <el-button
          v-for="(item, index) in dayList"
          :key="index"
          @click="setType(item)"
          :type="active === item ? 'primary' : ''">
          {{ item }} 天
        </el-button>
        <el-button @click="active = 'diy'" :type="active === 'diy' ? 'primary' : ''" v-if="isDiy">自定义</el-button>
      </el-button-group>
    </div>
    <div class="item" v-if="active === 'diy'">
      <el-date-picker
        style="width: 250px"
        @change="setTime"
        clearable
        v-model="value"
        type="daterange"
        range-separator="至"
        v-bind="pickerOptions"
        start-placeholder="开始日期"
        end-placeholder="结束日期"></el-date-picker>
    </div>
    <div>
      <slot></slot>
    </div>
  </div>
</template>
<script>
import dayjs from 'dayjs'
export default {
  props: {
    isToday: {
      type: Boolean,
      default: false,
    },
    isDiy: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {
      dayList: [7, 15, 30],
      active: 7,
      value: [],
      data: {
        beginTime: '',
        endTime: '',
      },
    }
  },
  methods: {
    submit() {
      this.$emit('search', this.data)
    },
    setTime(e) {
      if (e) {
        this.data.beginTime = dayjs(e[0]).format('YYYY-MM-DD')
        this.data.endTime = dayjs(e[1]).format('YYYY-MM-DD')
        this.submit()
      }
      //  else {
      //   this.data.beginTime = ''
      //   this.data.endTime = ''
      // }
    },
    setType(type) {
      this.active = type
      if (type === 0) {
        this.data.beginTime = dayjs().format('YYYY-MM-DD')
        this.data.endTime = dayjs().format('YYYY-MM-DD')
      } else {
        const weekStart = dayjs().subtract(type, 'days').format('YYYY-MM-DD')
        const weekEnd = dayjs().format('YYYY-MM-DD')
        this.data.beginTime = weekStart
        this.data.endTime = weekEnd
      }
      this.submit()
    },
  },
  created() {
    this.setType(this.isToday ? 0 : this.dayList[0])
  },
}
</script>
<style lang="scss" scoped>
.search {
  display: flex;
  align-items: center;
  .item {
    margin-right: var(--Margin);
  }
}
</style>
