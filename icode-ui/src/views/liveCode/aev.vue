<template>
  <div class="g-card">
    <el-form ref="form" :rules="rules" :model="form" label-position="right" label-width="100px">
      <el-form-item label="活码名称" prop="codeName">
        <el-input v-model="form.codeName" maxlength="15" show-word-limit clearable placeholder="请输入"></el-input>
        <!-- <div class="g-tip">活码名称创建完成后不可修改</div> -->
      </el-form-item>

      <el-form-item label="活码员工" prop="users" :error="userErrorTip">
        <el-select
          v-model="form.users"
          value-key="id"
          multiple
          collapse-tags
          collapse-tags-tooltip
          :max-collapse-tags="3"
          placeholder="请选择">
          <el-option v-for="item in userList" :key="item.id" :label="item.name" :value="item" />
        </el-select>
      </el-form-item>
      <el-form-item label="免验证">
        <el-switch v-model="form.skipVerify"></el-switch>
        <div class="g-tip">（注:勾选后,客户添加员工好友无需员工确认）</div>
      </el-form-item>
      <el-form-item label="重复添加">
        <el-switch v-model="form.isExclusive"></el-switch>
        <div class="g-tip">（注:开启后，同一个企业的客户会优先添加到同一个跟进人）</div>
      </el-form-item>
      <el-form-item label="新客标签" :error="tagErrorTip">
        <el-select
          v-model="form.tags"
          value-key="id"
          multiple
          collapse-tags
          collapse-tags-tooltip
          :max-collapse-tags="3"
          placeholder="请选择">
          <el-option v-for="item in tagList" :key="item.id" :label="item.name" :value="item" />
        </el-select>
      </el-form-item>
      <el-form-item label="欢迎语">
        <TextareaExtend
          v-model="form.weclomeMsg"
          :toolbar="['emoji', 'insertCustomerNickName']"
          maxlength="2000"
          show-word-limit
          placeholder="请输入"
          :autosize="{ minRows: 5, maxRows: 20 }"
          clearable
          :autofocus="false" />
      </el-form-item>
    </el-form>
  </div>
  <!-- <CommonTopRight>
      <el-button type="primary" size="default" @click="submit()">确定</el-button>
    </CommonTopRight> -->
</template>

<script>
import { getDetail, add, update } from './api'
import { getUserList, getTagList } from '@/api/common'

export default {
  props: { data: {} },
  data() {
    return {
      rules: {
        codeName: [
          {
            required: true,
            message: '请输入活码名称',
            trigger: 'blur',
          },
        ],
        users: [
          {
            required: true,
            message: '请选择',
            trigger: 'change',
            validator: (rule, value, callback) => {
              if (value.length == 0) {
                callback(new Error('请选择'))
              } else {
                callback()
              }
            },
          },
        ],
      },
      form: {
        codeName: '',
        skipVerify: 1, // 自动通过
        tags: [], // 标签
        users: [], // 标签
      },

      selectedUserList: [],
      selectedTagList: [],

      userList: [],
      userErrorTip: '',
      tagList: [],
      tagErrorTip: '',
    }
  },
  watch: {
    data: {
      deep: true,
      immediate: true,
      handler(val) {
        let element = JSON.parse(JSON.stringify(val))
        element.tags = []
        element.users = []

        if (element.tagId && element.tagName) {
          element.tagId = element.tagId.split(',')
          element.tagName = element.tagName.split(',')
          element.tagId.forEach((unit, index) => {
            element.tags.push({
              id: unit,
              name: element.tagName[index],
            })
          })
        }

        if (element.userId && element.userName) {
          element.userId = element.userId.split(',')
          element.userName = element.userName.split(',')
          element.userId.forEach((unit, index) => {
            element.users.push({
              id: unit,
              name: element.userName[index],
            })
          })
        }
        this.form = element
        setTimeout(() => {
          this.$refs.form.clearValidate()
        }, 0)
      },
    },
  },
  created() {
    this.getUserList()
    this.getTagList()
    // let id = this.$route.query.id
    // if (id) {
    //   this.getDetail(id)
    // }
  },
  methods: {
    getUserList() {
      getUserList().then((res) => {
        if (res.code == 301) {
          this.userErrorTip = res.msg
          return
        }
        this.userList = res.data || []
      })
    },
    getTagList() {
      getTagList().then((res) => {
        if (res.code == 301) {
          this.tagErrorTip = res.msg
          return
        }
        this.tagList = res.data || []
      })
    },
    /** 获取详情 */
    getDetail(id) {
      getDetail(id).then((res) => {
        res.data.forEach((element) => {
          if (element.tagId && element.tagName) {
            element.tagId = element.tagId.split(',')
            element.tagName = element.tagName.split(',')
            element.tags = []
            element.tagId.forEach((unit, index) => {
              element.tags.push({
                id: unit,
                name: element.tagName[index],
              })
            })
          }

          if (element.userId && element.userName) {
            element.userId = element.userId.split(',')
            element.userName = element.userName.split(',')
            element.users = []
            element.userId.forEach((unit, index) => {
              element.users.push({
                id: unit,
                name: element.userName[index],
              })
            })
          }
        })
        this.form = res.data
      })
    },
    async submit() {
      let valid = await this.$refs.form.validate()
      if (!valid) return
      this.form.tagId = this.form.tags.map((e) => e.id) + ''
      this.form.tagName = this.form.tags.map((e) => e.name) + ''
      this.form.userId = this.form.users.map((e) => e.id) + ''
      this.form.userName = this.form.users.map((e) => e.name) + ''
      this.$store.loading = true
      return (this.form.id ? update : add)(this.form)
        .then(({ data }) => {
          this.msgSuccess('操作成功')
          this.$router.back()
        })
        .finally(() => {
          this.$store.loading = false
        })
    },
  },
}
</script>

<style lang="scss" scoped>
.add-continue {
  cursor: pointer;
  font-size: 14px;

  font-weight: 400;
  color: var(--color);
  display: flex;
  align-items: center;
  justify-content: center;
  &:hover {
    opacity: 0.8;
  }
}

.crumb- {
  // 一级 页面标题
  &title {
    display: flex;
    flex-direction: column;
    justify-content: center;
    height: 90px; // line-height: 90px;
    font-size: 18px;
    font-weight: 500;
    color: var(--font-black);
    padding: 0 20px;
    background: var(--bg-white);
    border-top-left-radius: 4px;
    border-top-right-radius: 4px;
  }
}

.crumb {
  font-size: 12px;

  font-weight: 400;
  color: var(--font-black-4);
  display: flex;
}

.wrap {
  display: flex;
  margin-top: 24px;
  .el-form {
    margin-right: 10%;
  }
}

.preview-wrap {
  line-height: 26px;
}

.tip {
  color: var(--font-black-6);
}

.welcome-input {
  display: table;
  width: 80%;
  margin: 0 auto 20px;
}

.el-icon-error {
}

.my-cord {
  position: relative;
  .operation {
    z-index: 100;
    cursor: pointer;
    position: absolute;
    top: 0;
    right: 0;
    font-size: 12px;

    font-weight: 400;
    color: #1785ff;
    display: flex;
    align-items: center;
    &:hover {
      opacity: 0.8;
    }
  }
}
.roster-btn-delete {
  margin-top: -16%;
  margin-right: -8%;
}
.roster-card:not(:first-child) {
  margin-top: 20px;
}
.user-el-tag {
  margin-right: 50px;
  width: 160px;
  margin-left: 0px;
}
</style>
