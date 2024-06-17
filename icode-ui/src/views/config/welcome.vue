<script>
import { getDetailWel, addOrUpdateWel } from './api'
import { dictMsgType } from '@/utils/index'
import MessageContentForm from './MessageContentForm.vue'
export default {
  // components: { MessageContentForm: () => import('./MessageContentForm.vue') },
  components: { MessageContentForm },
  data() {
    return {
      loading: false,
      form: { defaultContent: '' },
      rules: {
        defaultContent: [{ required: true, message: '必填项', trigger: 'blur' }],
      },

      active: 0,
      annexList: [],
      max: 9,
      dictMsgType,
    }
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
          this.loading = false
        })
        .catch(() => {
          this.loading = false
        })
    },
    submit() {
      this.$refs['form'].validate((validate) => {
        if (validate) {
          addOrUpdateWel(this.form).then(() => {
            this.msgSuccess('操作成功')
            this.getDetail()
          })
        }
      })
    },
    add() {},
    remove(index) {
      this.$confirm().then(() => {
        this.annexList.splice(index, 1)
        if (index >= this.annexList.length) {
          this.active = this.annexList.length - 1
        }
      })
    },
  },
}
</script>
<template>
  <el-form ref="form" label-width="80" :model="form" :rules="rules">
    <div class="g-card">
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
      <el-form-item label="附件" prop="">
        <el-popover
          trigger="hover"
          :content="'最多添加' + max + '个'"
          placement="top-start"
          :disabled="annexList?.length < max">
          <template #reference>
            <el-dropdown
              @command="(msgtype) => (active = annexList.push({ msgtype }) - 1)"
              :disabled="annexList?.length >= max">
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
        <el-tabs v-model="active" type="card" class="" closable @tab-remove="remove">
          <el-tab-pane
            v-for="(item, index) in annexList"
            :key="item.msgtype"
            :label="dictMsgType[item.msgtype].name"
            :name="index">
            <MessageContentForm :type="item.msgtype" :data="item[item.msgtype]" />
          </el-tab-pane>
        </el-tabs>
      </el-form-item>
    </div>
  </el-form>
</template>

<style lang="scss" scoped>
:deep(.el-tabs__new-tab) {
  width: auto;
}
</style>
