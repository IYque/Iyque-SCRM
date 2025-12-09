<script>
import * as customerTagApi from '@/views/customerTag/api.js'
import * as groupTagApi from '@/views/groupTag/api.js'

export default {
  name: 'AddTag',
  components: {},
  props: {
    // 添加标签显隐
    visible: {
      type: Boolean,
      default: false,
    },
    // 表单参数
    form: {
      type: Object,
      default: () => ({
        groupName: '',
        weTags: [],
      }),
    },
    // 模块类型（customerTag或groupTag）
    module: {
      type: String,
      default: 'customerTag'
    }
  },
  emits: ['update:visible'],
  data() {
    return {
      // 添加标签输入框
      newInput: '',
      // 表单验证规则
      rules: Object.freeze({
        groupName: [{ required: true, message: '必填项', trigger: 'blur' }],
        weTags: [{ required: true, message: '必填项', trigger: 'blur' }],
      }),
      // 添加便签按钮显隐
      visibleAdd: false,
    }
  },
  watch: {},
  computed: {
    Pvisible: {
      get() {
        if (this.visible) {
          this.$nextTick(() => {
            this.$refs['form'].clearValidate()
          })
        }
        return this.visible
      },
      set(val) {
        this.$emit('update:visible', val)
      },
    },
  },
  created() {},
  mounted() {},
  methods: {
    closeTag(tag, index) {
      // if (tag.id) {
      //   tag.status = 1
      //   // this.$forceUpdate()
      // } else {
      this.form.weTags.splice(index, 1)
      // }
    },

    showInput() {
      this.visibleAdd = true
      this.$nextTick((_) => {
        this.$refs.saveTagInput.$refs.input.focus()
      })
    },
    newInputConfirm() {
      let name = this.newInput
      if (name) {
        Array.isArray(this.form.weTags) || (this.form.weTags = [])
        let isExist = this.form.weTags.some((e) => {
          return e.name === name
        })
        if (isExist) {
          this.msgError('标签名已存在，不可重复添加')
          return
        }
        this.form.weTags.push({ name })
      }
      this.visibleAdd = false
      this.newInput = ''
    },
    submit() {
      if (!this.visibleAdd) {
        this.$refs['form'].validate((valid) => {
          this.newInput = ''
          let form = JSON.parse(JSON.stringify(this.form))
          if (!form.weTags.length) {
            return
          }
          // 根据模块类型选择对应的API
          const currentApi = this.module === 'groupTag' ? groupTagApi : customerTagApi
          currentApi[form.groupId ? 'update' : 'add'](form).then(() => {
            this.msgSuccess('操作成功')
            this.Pvisible = false
            this.$emit('success')
          })
        })
      }
    },
  },
}
</script>

<template>
  <el-drawer
    :title="(form.groupId ? '修改' : '添加') + '标签/组'"
    :model-value="Pvisible"
    @update:modelValue="(val) => (Pvisible = val)"
    width="500px"
    append-to-body
    :close-on-click-modal="false">
    <el-form ref="form" :model="form" :rules="rules" label-width="100px">
      <el-form-item label="标签组名称" prop="groupName">
        <el-input
          v-model.trim="form.groupName"
          maxlength="15"
          show-word-limit
          placeholder="请输入标签组名称，该名称不支持再次修改" />
      </el-form-item>
      <el-form-item label="标签" prop="weTags">
        <template v-for="(item, index) in form.weTags" :key="index">
          <el-tag v-if="item.status !== 1" type="primary" closable @close="closeTag(item, index)">
            {{ item.name }}
          </el-tag>
        </template>
        <div>
          <el-input
            class="input-new-tag"
            v-if="visibleAdd"
            v-model.trim="newInput"
            ref="saveTagInput"
            maxlength="10"
            show-word-limit
            @keyup.enter="newInputConfirm"
            @blur="newInputConfirm"></el-input>
          <el-button v-else type="primary" plain class="button-new-tag" @click="showInput">添加标签</el-button>
        </div>
      </el-form-item>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button type="primary" @click="submit" v-preventReClick="1000">确 定</el-button>
        <el-button @click="Pvisible = false">取 消</el-button>
      </div>
    </template>
  </el-drawer>
</template>

<style lang="scss" scoped>
.input-new-tag {
  width: auto;
  margin-right: 10px;
  vertical-align: bottom;
}
</style>
