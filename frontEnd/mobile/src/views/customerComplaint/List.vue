<script setup>
import { getList } from './api'
defineProps({
  type: { type: String, default: '1' },
})
</script>

<template>
  <div class="list">
    <PullRefreshScrollLoadList :request="getList" :params="{ handleState: type }">
      <template #list="{ list }">
        <div
          class="list-item --Radius"
          v-for="(item, index) in list"
          :key="index"
          @click="
            $router.push({
              path: '/customerComplaintDetail',
              query: {
                id: item.id,
              },
            })
          ">
          <div class="list-title">{{ item.complainTypeContent }}</div>
          <div class="list-info">联系方式：{{ item.complainUserPhone }}</div>
          <div class="">投诉时间：{{ item.complainTime }}</div>
        </div>
      </template>
    </PullRefreshScrollLoadList>
  </div>
</template>

<style lang="scss" scoped>
.list {
  padding: 10px;
  overflow: auto;
  .list-item {
    padding: 10px 15px;
    background: var(--BgWhite);
    & + .list-item {
      margin-top: 10px;
    }
    .list-title {
      font-weight: 500;
      font-size: 16px;
    }
    .list-info {
      // color: #b5b8c0;
      margin: 30px 0 10px;
    }
    .list-time {
      color: #b5b8c0;
      font-size: 12px;
    }
  }
}
</style>
