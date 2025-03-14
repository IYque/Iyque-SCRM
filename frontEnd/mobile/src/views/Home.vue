<template>
  <div class="home">
    <div class="logo ac">yq</div>
    <template v-if="isShow">
      <router-link class="router-link" v-for="(item, index) in $router.options.routes" :to="item.path" :key="index">{{
        item.meta && item.meta.title
      }}</router-link>
    </template>
    <div v-else class="cc tips blod">
      {{ tips }}
    </div>
  </div>
</template>

<script>
// @ is an alias to /src

export default {
  name: 'Home',
  components: {},
  data() {
    return {
      isShow: process.env.NODE_ENV === 'development',
      tips: '',
    }
  },
  created() {
    if (location.href.includes('redPacketReceive') && !navigator.userAgent.includes('Mobile')) {
      this.tips = '该业务仅支持手机微信用户，请在手机微信端打开'
    } else {
    }
  },
}
</script>

<style scoped>
.logo {
  margin: 0 auto;
  width: 150px;
  height: 50px;
  line-height: 50px;
  border: 2px solid #6582ff;
  border-radius: 50px;
}
.router-link {
  display: block;
  text-align: center;
  line-height: 40px;
}
.tips {
  width: 90%;
  text-align: center;
  line-height: 1.5;
}
</style>
