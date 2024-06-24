<script>
import { getDetailWel, addOrUpdateWel,findIYqueUserCodeDetail } from './api'
import { dictMsgType } from '@/utils/index'
export default {
  components: { MessageContentForm: defineAsyncComponent(() => import('./MessageContentForm.vue')) },
  data() {
    return {
      loading: false,
      form: { defaultContent: '' },
      rules: {
        defaultContent: [{ required: true, message: '必填项', trigger: 'blur' }],
      },

      active: 0,
      annexLists: [],
      max: 9,
      dictMsgType,
    }
  },
  computed: {
    chatList() {
      return [
        {
          text: this.form.defaultContent,
        },
        ...this.annexLists,
      ]
    },
  },
  created() {
    this.getDetail()
  },
  methods: {
    getDetail() {
      this.loading = true
      getDetailWel()
        .then(({ data }) => {
          this.form = data
          this.annexLists = data.annexLists || []
          this.loading = false
        })
        .catch(() => {
          this.loading = false
        })
    },
    async submit() {
      let validate = await this.$refs['form'].validate()
      if (validate) {
        let tasks = this.annexLists.map(async (e, i) => {
          let contentForm = await this.$refs.contentForm[i].submit()
          if (contentForm) {
            e[e.msgtype] = Object.assign(e[e.msgtype] || {}, contentForm)
            return true
          } else {
            return false
          }
        })
        let validate1 = await Promise.all(tasks)
        this.form.annexLists = this.annexLists
        // console.log(this.form)
        validate1 &&
          addOrUpdateWel(this.form).then(() => {
            this.msgSuccess('操作成功')
            this.getDetail()
          })
      }
    },
    add() {},
    remove(index) {
      this.$confirm().then(() => {
        this.annexLists.splice(index, 1)
        if (index >= this.annexLists.length) {
          this.active = this.annexLists.length - 1
        }
      })
    },
  },
}
</script>
<template>
  <div class="fxbw">
    <div class="g-card fxauto">
      <el-form ref="form" label-width="80" :model="form" :rules="rules">
        <el-form-item label="欢迎语" prop="defaultContent">
          <TextareaExtend
            v-model="form.defaultContent"
            :toolbar="['emoji', 'insertCustomerNickName']"
            maxlength="2000"
            show-word-limit
            placeholder="请输入"
            :autosize="{ minRows: 5, maxRows: 20 }"
            clearable
            :autofocus="false" />
        </el-form-item>
        <el-form-item label="欢迎语附件" prop="">
          <el-popover
            trigger="hover"
            :content="'最多添加' + max + '个'"
            placement="top-start"
            :disabled="annexLists?.length < max">
            <template #reference>
              <el-dropdown
                @command="(msgtype) => (active = annexLists.push({ msgtype, [msgtype]: {} }) - 1)"
                :disabled="annexLists?.length >= max">
                <el-button type="primary" class="mb10">添加</el-button> 
                <template #dropdown>
                  <el-dropdown-menu trigger="click">
                    <el-dropdown-item v-for="(item, index) in dictMsgType" :key="index" :command="item.type">
                      <el-button text>{{ item.name }}</el-button>
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
            </template>
          </el-popover>
          <el-alert
            title="注: 1.图片:10MB,支持JPG,PNG格式; 2.视频:10MB,支持MP4格式; 3.普通文件:20MB"
            type="error" :closable="false">
          </el-alert>
         <br/>
          <el-tabs v-model="active" type="card" class="" closable @tab-remove="remove">
            <el-tab-pane
              v-for="(item, index) in annexLists"
              :key="item.msgtype"
              :label="dictMsgType[item.msgtype].name"
              :name="index">
              <MessageContentForm :type="item.msgtype" ref="contentForm" :form="item[item.msgtype]" />
            </el-tab-pane>
          </el-tabs>
        </el-form-item>
      </el-form>
    </div>
    <PhoneChatList class="g-margin-l" :list="chatList"></PhoneChatList>
  </div>
</template>

<style lang="scss" scoped>
:deep(.el-tabs__new-tab) {
  width: auto;
}
</style>
