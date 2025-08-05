<script>
import { getList, save, remove, sync, appMsg } from './api'

export default {
  components: {
    HistoryMsg: defineAsyncComponent(() => import('./HistoryMsg')),
    MsgForm: defineAsyncComponent(() => import('../MsgForm')),
  },
  props: {},
  data() {
    return {
      query: {
        // pageNum: 1,
        // pageSize: 10,
        // name: '',
      },
      // dateRange: [], // 添加日期
      // total: 0,
      form: {},
      list: [],
      dialogVisible: false,
      disabled: false,
      loading: false,
      rules: Object.freeze({
        agentId: [{ required: true, message: '必填项', trigger: 'blur' }],
        name: [{ required: true, message: '必填项', trigger: 'blur' }],
        description: [{ required: true, message: '必填项', trigger: 'blur' }],
        logoUrl: [{ required: true, message: '必填项', trigger: 'change' }],
      }),

      formMsg: {},
      dialogVisibleHistoryMsg: false,
      dialogVisibleSendMsg: false,
      disabledMsg: false,
    }
  },
  watch: {},
  computed: {},
  created() {
    this.getList()

    this.$store.setBusininessDesc(`<div>企业微信自建应用管理，支持通过应用发送应用消息</div> `)
  },
  mounted() {},
  methods: {
    getList(page) {
      // if (this.dateRange) {
      //   this.query.beginTime = this.dateRange[0]
      //   this.query.endTime = this.dateRange[1]
      // } else {
      //   this.query.beginTime = ''
      //   this.query.endTime = ''
      // }
      // page && (this.query.pageNum = page)
      this.loading = true
      getList()
        .then(({ data, total }) => {
          data.forEach((element) => {
            element.allowPartyName = (element.allowPartyName || '').split(',')
            element.allowUserinfoName = (element.allowUserinfoName || '').split(',')
          })
          this.list = data
        })
        .finally(() => {
          this.loading = false
        })
    },
    sync(item) {
      sync(item.id).then(({ data }) => {
        Object.assign(item, data)
        this.getList()
        this.msgSuccess('操作成功')
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
          this.form.agentId = Number(this.form.agentId)
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
    // goRoute(id, path) {
    //   this.$router.push({
    //     path: 'taskAev',
    //     query: { id },
    //   })
    // },

    // 发送消息确认按钮
    submitSendMsg(form) {
      if (form) {
        form.agentSub = [Object.assign({}, form)]
        appMsg.add(form).then(() => {
          this.msgSuccess('操作成功')
          this.dialogVisibleSendMsg = false
          if (this.dialogVisibleHistoryMsg) {
            this.$refs.historyMsg.getList()
          }
          // this.getList()
          // this.getList(!this.form.id && 1)
        })
      } else {
        this.dialogVisibleSendMsg = false
      }
    },
  },
}
</script>

<template>
  <div>
    <ul v-loading="loading" class="list-wrap">
      <li v-for="(item, index) of list" :key="index" class="list">
        <div class="list-item">
          <div class="flex">
            <el-image :src="item.logoUrl" fit="fill"></el-image>
            <div class="">
              <div class="title blod toe">{{ item.name }}</div>
              <div class="desc">
                {{ item.description }}
              </div>
              <el-button type="primary" plain @click="sync(item)">同步</el-button>
            </div>
          </div>
          <div class="flex aic mt10">
            <div class="list-label">AgentId</div>
            {{ item.agentId }}
          </div>
          <div class="flex aic mt10">
            <div class="list-label">可见范围</div>
            <!-- <div class="flex">
              <div v-for="(item, index) of item.allowPartys.split(',')" :key="index">
                <el-icon-folder-opened class="el-icon-folder-opened" v-if="item"></el-icon-folder-opened>{{ item }}
              </div>
            </div> -->
            <TagEllipsis :list="item.allowPartyName"></TagEllipsis>
            <TagEllipsis :list="item.allowUserinfoName"></TagEllipsis>
          </div>
          <div class="list-action fxbw">
            <el-button
              text
              @click="
                ;(formMsg = { agentName: item.name, agentId: item.agentId }),
                  (disabledMsg = false),
                  (dialogVisibleSendMsg = true)
              ">
              发送消息
            </el-button>
            <el-button
              text
              @click=";(formMsg = { agentName: item.name, agentId: item.agentId }), (dialogVisibleHistoryMsg = true)">
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
          添加应用
        </div>
      </li>
    </ul>

    <!-- 编辑，添加弹窗 -->
    <el-drawer :title="form.id ? '编辑' : '添加'" v-model="dialogVisible" :close-on-click-modal="false" size="580px">
      <el-form ref="form" label-position="right" :model="form" :rules="rules" label-width="80px">
        <!-- 添加 -->
        <!-- <template v-if="!form.id"> -->
        <el-form-item label="应用ID" prop="agentId">
          <el-input v-model="form.agentId" placeholder="请输入应用 AgentId"></el-input>
          <div class="dialog-tip">如何获取：企微后台->应用管理->自建应用详情->AgentId</div>
        </el-form-item>
        <el-form-item
          label="应用密钥"
          prop="secret"
          :rules="{ required: !form.agentId, message: '必填项', trigger: 'blur' }">
          <el-input v-model="form.secret" placeholder="请输入应用 Secret"></el-input>
          <div class="dialog-tip">如何获取：企微后台->应用管理->自建应用详情->Secret</div>
        </el-form-item>
        <!-- </template> -->

        <!-- 编辑 -->
        <template v-if="form.id">
          <el-form-item label="应用名称" prop="name">
            <el-input v-model="form.name" maxlength="16" show-word-limit placeholder="请输入应用名称"></el-input>
          </el-form-item>
          <el-form-item label="应用描述" prop="description">
            <el-input
              type="textarea"
              v-model="form.description"
              maxlength="60"
              show-word-limit
              placeholder="请输入应用描述"></el-input>
          </el-form-item>
          <el-form-item label="应用头像" prop="logoUrl">
            <upload class="image-uploader" v-model:fileUrl="form.logoUrl" type="0"></upload>
          </el-form-item>
        </template>

        <!-- <el-row :gutter="10">
          <el-col :span="4">
            <div class="avatar-wrap ac" @click="dialogVisibleSelectMaterial = true">
              <img class="avatar" v-if="form.squareLogoUrl" :src="form.squareLogoUrl" />
              <el-icon-plus  v-else class="el-icon-plus avatar-uploader-icon cc"/>
            </div>
          </el-col>
          <el-col :span="20">
            <el-form-item label="应用Id" prop="agentId">
              <el-input
                v-model="form.agentId"
                :disabled="!!form.id"
                placeholder="请输入应用Id"></el-input>
            </el-form-item>
            <el-form-item label="应用Secret" prop="agentSecret">
              <el-input
                :disabled="!!form.id"
                v-model="form.agentSecret"
                placeholder="请输入应用Secret"></el-input>
            </el-form-item>
            <el-form-item label="可见范围" v-if="form.id">
              <div class="flex">
                <div v-for="(item, index) of form.allowPartys.split(',')" :key="index">
                  <el-icon-folder-opened class="el-icon-folder-opened" v-if="item"></el-icon-folder-opened>{{ item }}
                </div>
                <div v-for="(item, index) of form.allowUserinfos.split(',')" :key="'1' + index">
                  <el-icon-s-custom class="el-icon-s-custom" v-if="item"></el-icon-s-custom>{{ item }}
                </div>
              </div>
            </el-form-item>
          </el-col>
        </el-row> -->
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
        ref="historyMsg"
        :id="formMsg.agentId"
        v-if="dialogVisibleHistoryMsg"
        @edit="
          (data, type) => {
            dialogVisibleSendMsg = true
            disabledMsg = type === 'detail'
            formMsg = Object.assign({ agentId: formMsg.agentId, agentName: formMsg.agentName }, data)
          }
        " />
    </el-drawer>

    <!-- 选择素材弹窗 -->
    <!-- <SelectMaterial
       v-model:visible="dialogVisibleSelectMaterial"
      type="1"
      :showArr="[1]"
      @success="submitSelectMaterial"
    >
    </SelectMaterial> -->

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
    height: 218px;
    margin: 0 20px 20px 0;
    padding: 20px 20px 10px;
    border: 1px solid var(--border-black-9);
    background: var(--bg-white);
    border-radius: 6px;
    .el-image {
      margin-right: 10px;
      width: 80px;
      height: 80px;
      flex: none;
      border: 1px solid var(--border-black-9);
      border-radius: 5px;
      background: var(--bg-black-9);
    }
    .title {
      font-size: 16px;
    }
    .desc {
      color: var(--font-black-6);
      word-break: break-all;
      margin: 5px 0;
    }
    .list-label {
      color: var(--font-black-6);
      width: 90px;
    }
  }
}

.dialog-tip {
  color: var(--font-black-6);
  font-size: 12px;
}
// .avatar-wrap {
//   position: relative;
//   width: 120px;
//   height: 120px;
//   border: 1px solid var(--border-black-9);
//   border-radius: 5px;
// }
// .avatar {
//   width: 100%;
// }
// .avatar-uploader-icon {
//   font-size: 28px;
//   color: var(--font-black-8);
// }
</style>
