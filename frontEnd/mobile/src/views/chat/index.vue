<script>
import * as apiCategory from '@/api/category'
import { getList } from './api'
import List from './List.vue'

import useStore from '@/stores/index.js'

export default {
  components: { List },
  props: {},
  data() {
    return {
      groupIndex: 0,
      keyword: '',
      active: 0,
      list: [
        { type: '4', name: '文本', sort: 1 },
        { type: '0', name: '图片', sort: 2 },
        { type: '9', name: '图文', sort: 3 },
      ],
      loading: false,
      finished: false,
      show: false,
      groupList: [], // 分组列表
      query: {},
      type: '', //分组类型
      // userId: useStore().userId,
    }
  },
  watch: {
    userId() {
      this.getList()
    },
  },
  computed: {
    userId() {
      return useStore().userId
    },
  },
  created() {
    this.getList()
  },
  mounted() {},
  methods: {
    search(pageNum) {
      // this.$refs['list'][this.active].search(pageNum, this.keyword)
      this.$refs['list' + this.active] &&
        this.$refs['list' + this.active][0].search(pageNum, this.keyword, this.query.categoryId)
    },
    reset() {
      this.keyword = ''
      // delete this.query.categoryId
      // this.groupIndex = 0
      this.$nextTick(() => this.search(1))
    },
    // refreshCollect() {
    //   this.userId && this.$refs['list'][0].getList(1)
    // },
    switchGroup(index, data) {
      this.groupIndex = index
      this.query.categoryId = data.id
      this.search(1)
    },
    // 获取当前类型下分组列表
    getList(item) {
      this.type = this.list[item?.name || 0].type

      apiCategory.getList(this.type).then((res) => {
        this.groupList = [{ name: '全部' }]
        res.data && this.groupList.push(...res.data)
        this.groupIndex = 0
      })
    },
  },
}
</script>

<template>
  <div>
    <div>
      <van-search v-model="keyword" show-action placeholder="请输入关键词，在当前分类分组下搜索" @search="search(1)">
        <template #action>
          <span @click="search(1)">搜索</span>
          <span class="ml5" @click="reset">重置</span>
        </template>
      </van-search>
    </div>
    <div class="tabs">
      <van-tabs v-model="active" @click-tab="getList">
        <van-tab :title="item.name" v-for="(item, index) in list" :key="index">
          <div
            style="
              display: flex;
              justify-content: space-between;
              align-items: stretch;
              width: 100%;
              height: calc(100vh - 120px);
            ">
            <div class="item-list">
              <div
                class="item"
                v-for="(group, key) in groupList"
                :class="{ active: groupIndex == key }"
                :key="group.id"
                @click="switchGroup(key, group)">
                <div class="name">{{ group.name }}</div>
              </div>
            </div>
            <!-- <List
            :ref="'list' + index"
            :sideId="item.id"
            :mediaType="item.type"
            @collect="refreshCollect"
            style="width:75%"
          ></List> -->
            <div style="width: 70%; overflow: auto">
              <List :ref="'list' + index" :sideId="item.id" :mediaType="item.type + ''"></List>
            </div>
          </div>
        </van-tab>
      </van-tabs>
    </div>

    <!-- <van-dialog
      v-model="show"
      :title="`添加&quot;我的&quot;${radio}`"
      show-cancel-button
    >
      <van-form @submit="onSubmit">
        <van-field name="radio" label="添加类型">
          <template #input>
            <van-radio-group v-model="radio" direction="horizontal">
              <van-radio name="1">文本</van-radio>
              <van-radio name="2">分类</van-radio>
            </van-radio-group>
          </template>
        </van-field>
        <van-field
          v-model="username"
          name="分类名称"
          label="分类名称"
          placeholder="分类名称"
          :rules="[{ required: true, message: '请填写分类名称' }]"
        />
        <template>
          <van-field
            readonly
            clickable
            name="picker"
            :value="value"
            label="文本分类"
            placeholder="点击选择文本分类"
            @click="showPicker = true"
          />
          <van-popup v-model="showPicker" position="bottom">
            <van-picker
              show-toolbar
              :columns="columns"
              @confirm="onConfirm"
              @cancel="showPicker = false"
            />
          </van-popup>

          <van-field
            v-model="message"
            rows="5"
            autosize
            label="文本信息"
            type="textarea"
            maxlength="150"
            placeholder="请输入文本信息"
            show-word-limit
          />
        </template>
      </van-form>
    </van-dialog> -->
  </div>
</template>

<style lang="scss" scoped>
.van-tab__pane {
  display: flex;
}
.search {
  width: 100%;
  position: fixed;
  top: 0px;
}
.tabs {
  position: relative;
  height: calc(100vh - 54px);
  overflow: auto;
}
.page {
  height: 100vh;
  background: #f6f6f6;
}
::deep() .list {
  padding: 10px;
  background: #fff;
  border-top: 1px solid #eee;
  .info {
    padding-top: 10px;
  }
  .action {
    padding-left: 5px;
  }
}
.item-list {
  margin-top: 10px;
  width: 30%;
  background-color: #fff;
  padding-top: 15px;
  display: flex;
  flex-direction: column;
  overflow-x: hidden;
  overflow-y: auto;
  .item {
    cursor: pointer;
    display: flex;
    justify-content: space-between;
    align-items: center;
    font-size: 14px;
    color: rgba(0, 0, 0, 0.6);
    height: 40px;
    line-height: 40px;
    width: 100%;
    padding-left: 20px;
    .name {
      overflow: hidden;
      white-space: nowrap;
      text-overflow: ellipsis;
    }
    .dropdown {
      // display: none;
      .dot {
        cursor: pointer;
        width: 15px;
        height: 15px;
        line-height: 15px;
        font-size: 14px;
        font-family: JMT-Font, JMT;
        font-weight: normal;
        color: rgba(0, 0, 0, 0.6);
        margin-right: 10px;
        margin-left: 5px;
        font-weight: 500;
        .content-icon {
          color: rgba(0, 0, 0, 0.6);
          font-size: 12px;
          transform: rotate(90deg);
        }
      }
    }
    &:hover {
      color: rgba(0, 0, 0, 0.9);
      background: #f5f8fe;
      opacity: 0.8;
      border-radius: 2px;
      .dropdown {
        // display: block;
      }
    }
  }

  .active {
    // border-left: 2px solid #3c88f0;
    color: rgba(0, 0, 0, 0.9);
    background: #f5f8fe;
    border-radius: 2px;
  }
}
</style>
