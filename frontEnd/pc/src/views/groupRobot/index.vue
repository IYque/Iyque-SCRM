<script>
import { getList, save, remove, appMsg } from './api'

export default {
  components: {
    HistoryMsg: defineAsyncComponent(() => import('./HistoryMsg')),
    MsgForm: defineAsyncComponent(() => import('./MsgForm')),
  },
  props: {},
  data() {
    return {
      form: {},
      list: [],
      dialogVisible: false,
      disabled: false,
      loading: false,
      rules: Object.freeze({
        title: [{ required: true, message: '必填项', trigger: 'blur' }],
        webHookUrl: [{ required: true, message: '必填项', trigger: 'blur' }],
      }),

      formMsg: {},
      dialogVisibleHistoryMsg: false,
      dialogVisibleSendMsg: false,
    }
  },
  watch: {},
  computed: {},
  created() {
    this.getList()

    this.$store.setBusininessDesc(`<div>通过群机器人给内部群发送消息，通知员工</div> `)
  },
  mounted() {},
  methods: {
    getList(page) {
      this.loading = true
      getList()
        .then(({ rows, data, total }) => {
          this.list = rows || data || []
        })
        .finally(() => {
          this.loading = false
        })
    },
    edit(data, type) {
      this.form = Object.assign({}, data || {})
      this.dialogVisible = true
      type || !data ? (this.disabled = false) : (this.disabled = true)
      this.$nextTick(() => {
        this.$refs['form'].clearValidate()
      })
    },
    submit() {
      this.$refs['form'].validate((valid) => {
        if (valid) {
          save(this.form)
            .then(() => {
              this.msgSuccess('操作成功')
              this.dialogVisible = false
              this.getList()
              // this.getList(!this.form.id && 1)
            })
            .catch(() => {
              this.dialogVisible = false
            })
        }
      })
    },
    remove(id) {
      // const operIds = id || this.ids + ''
      this.$confirm('是否确认删除？该操作不可撤销，请谨慎操作。', {
        title: '警告',
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      })
        .then(function () {
          return remove(id)
        })
        .then(() => {
          this.getList()
          this.msgSuccess('删除成功')
        })
    },

    // 发送消息确认按钮
    submitSendMsg(form) {
      if (form) {
        form.robotSubList = [{ msgType: form.msgType, [form.msgType]: form[form.msgType] }]
        delete form[form.msgType]
        delete form.msgType
        appMsg
          .add(form)
          .then(() => {
            this.msgSuccess('操作成功')
            this.dialogVisibleSendMsg = false
          })
          .catch(() => {
            this.dialogVisibleSendMsg = false
          })
      } else {
        this.dialogVisibleSendMsg = false
      }
    },
    // goRoute(id, path) {
    //   this.$router.push({
    //     path: 'taskAev',
    //     query: { id },
    //   })
    // },

    // 选择素材确认按钮
    // submitSelectMaterial(text, image, file) {
    //   this.form.logoMediaid = image.id
    //   this.form.squareLogoUrl = image.materialUrl
    //   this.dialogVisibleSelectMaterial = false
    // },
  },
}
</script>

<template>
  <div>
    <ul v-loading="loading" class="list-wrap">
      <li v-for="(item, index) of list" :key="index" class="list">
        <div class="list-item">
          <div class="title blod toe">{{ item.title }}</div>
          <div class="desc mt10">
            {{ item.webHookUrl }}
          </div>
          <!-- <el-button  type="primary" plain @click="sync(item)">同步</el-button> -->
          <div class="list-action fxbw">
            <el-button
              text
              @click=";(formMsg = { robotId: item.id }), (disabledMsg = false), (dialogVisibleSendMsg = true)">
              发送消息
            </el-button>
            <el-button text @click=";(formMsg = { robotId: item.id }), (dialogVisibleHistoryMsg = true)">
              历史消息
            </el-button>
            <el-button text @click="edit(item)">编辑</el-button>
            <el-button text @click="remove(item.id)">删除</el-button>
          </div>
        </div>
      </li>
      <li class="list" @click="edit()">
        <div class="ac flex aic cp theme list-item" style="justify-content: center">
          <el-icon-plus class="el-icon-plus"></el-icon-plus>
          添加机器人
        </div>
      </li>
    </ul>

    <!-- 编辑，添加弹窗 -->
    <el-drawer :title="form.id ? '编辑' : '添加'" v-model="dialogVisible" :close-on-click-modal="false" size="580px">
      <el-form ref="form" label-position="right" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="群名称" prop="title">
          <el-input v-model="form.title" placeholder="请输入群名称"></el-input>
          <!-- <div class="dialog-tip">如何获取：企微后台->应用管理->自建应用详情->AgentId</div> -->
        </el-form-item>
        <el-form-item label="WebHook" prop="webHookUrl">
          <el-input v-model="form.webHookUrl" placeholder="请输入WebHook URL"></el-input>
          <div class="dialog-tip">如何获取：企业微信内部群 -> 开启群机器人 -> 复制地址</div>
        </el-form-item>
      </el-form>
      <template #footer>
        <div>
          <el-button @click="dialogVisible = false">取 消</el-button>
          <el-button type="primary" @click="submit">确 定</el-button>
        </div>
      </template>
    </el-drawer>

    <!-- 历史消息 弹窗 -->
    <el-drawer title="历史消息" v-model="dialogVisibleHistoryMsg" :close-on-click-modal="false">
      <HistoryMsg
        :id="formMsg.robotId"
        v-if="dialogVisibleHistoryMsg"
        ref="historyMsg"
        @edit="
          (data, type) => {
            dialogVisibleSendMsg = true
            disabledMsg = true
            formMsg = data
          }
        " />
    </el-drawer>

    <!-- 编辑/详情发送消息 弹窗 -->
    <el-drawer title="发送消息" v-model="dialogVisibleSendMsg" :close-on-click-modal="false">
      <MsgForm v-if="dialogVisibleSendMsg" :data="formMsg" :disabled="disabledMsg" @submit="submitSendMsg" />
    </el-drawer>
  </div>
</template>

<style lang="scss" scoped>
.theme {
  color: var(--color);
}
.list-wrap {
  display: flex;
  flex-wrap: wrap;
  line-height: 20px;
  .list {
    width: 33.3%;
    overflow: hidden;
  }
  .list-item {
    height: 128px;
    padding: 20px 20px;
    border: 1px solid var(--border-black-9);
    margin: 0 20px 20px 0;
    background: var(--bg-white);
    border-radius: 6px;
    .title {
      font-size: 16px;
    }
    .desc {
      color: var(--font-black-8);
      height: 40px;
      overflow: hidden;
      text-overflow: ellipsis;
      word-break: break-all;
    }
  }
}

.dialog-tip {
  color: var(--font-black-6);
  font-size: 12px;
}
</style>
