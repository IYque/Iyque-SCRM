<!-- 由透传属性，可直接使用 el-dialog 的所有props，
用法示例：<AddOrEditMaterialDialog ref="dialogRef" title="title"/> -->
<script>
import * as apiCategory from '@/api/category'

const validateHtml = (rule, value, callback) => {
  if (/\.html$/gi.test(value)) {
    callback()
  } else {
    callback(new Error('必须以 .html 作为后缀'))
  }
}
const validateHttp = (rule, value, callback) => {
  if (/^https?:\/\//gi.test(value)) {
    callback()
  } else {
    callback(new Error('必须以 http://或 https://开头'))
  }
}

export default {
  components: {},
  props: {
    // 0:图片；4:文本；7：热词分类；9：图文
    type: {
      type: String,
      default: '0',
    },
    // 是否为详情展示（不显示选择按钮）
    isDetail: {
      type: Boolean,
      default: false,
    },
    // 是否显示分组
    isGroup: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {
      // 表单校验
      rules: Object.freeze({
        title: [$sdk.ruleRequiredChange],
        categoryId: [$sdk.ruleRequiredChange],
        'text.content': [$sdk.ruleRequiredBlur],
        materialUrl: [$sdk.ruleRequiredChange],
        'image.picUrl': [{ type: 'array', ...$sdk.ruleRequiredChange }],
        digest: [$sdk.ruleRequiredBlur],
        coverUrl: [$sdk.ruleRequiredBlur],
        'link.url': [
          $sdk.ruleRequiredBlur,
          { validator: this.type == 11 ? validateHtml : validateHttp, trigger: 'blur' },
        ],
      }),
      treeData: [{}], // 树
      // 分组props
      groupProps: {
        expandTrigger: 'hover',
        checkStrictly: true,
        children: 'children',
        label: 'name',
        value: 'id',
        emitPath: false,
      },

      dialogVisibleSelectTag: false,
    }
  },
  computed: {},
  watch: {},
  created() {},
  mounted() {},
  methods: {
    // 获取类目树
    getTree() {
      apiCategory.getList(this.type).then(({ data }) => {
        // 删除每一项的children属性
        data.forEach((item) => {
          item.children = null
        })
        this.treeData = [
          {
            id: '',
            name: '全部',
            parentId: '0',
            hasParent: false,
            hasChildren: true,
            children: data || [],
          },
        ]
      })
    },
    action(data = {}, callback) {
      this.$nextTick(() => {
        this.getTree()
        data.msgtype = $sdk.dictMaterialType[this.type].msgtype
        data[$sdk.dictMaterialType[this.type].msgtype] ??= {}
        this.$refs.dialogRef.action(data)

        callback?.()
      })
    },
  },
}
</script>

<template>
  <!-- 添加或修改素材对话框 -->
  <BaseDialog ref="dialogRef" width="1000px" append-to-body>
    <template #="{ form }">
      <div class="flex --Gap">
        <div class="flex-auto">
          <el-form ref="form" :model="form" :rules="rules" label-width="100px">
            <el-form-item label="选择分组" prop="categoryId" v-if="isGroup">
              <el-cascader v-model="form.categoryId" :options="treeData[0].children" :props="groupProps"></el-cascader>
            </el-form-item>

            <!-- 文本 -->
            <template v-if="type === '4'">
              <el-form-item label="文本标题" prop="title">
                <el-input v-model="form.title" placeholder="请输入标题" maxlength="50" show-word-limit></el-input>
                <div class="g-tip">标题对客户不可见，仅用于查询场景</div>
              </el-form-item>
              <el-form-item label="文本内容" prop="text.content">
                <TextareaExtend
                  v-model="form.text.content"
                  :autosize="{ minRows: 5, maxRows: 50 }"
                  placeholder="请输入内容"
                  maxlength="1000"
                  show-word-limit></TextareaExtend>
              </el-form-item>
            </template>

            <!-- 图片 -->
            <template v-else-if="type === '0'">
              <el-form-item label="图片标题" prop="title">
                <el-input v-model="form.title" placeholder="请输入" :maxlength="50" show-word-limit></el-input>
                <div class="g-tip">标题对客户不可见，仅用于查询场景</div>
              </el-form-item>
              <el-form-item label="图片" prop="image.picUrl">
                <Upload v-model:fileUrl="form.image.picUrl" :maxSize="20" :multiple="false" :limit="10">
                  <template #tip><div>支持jpg/jpeg/png格式，图片大小不超过20M</div></template>
                </Upload>
              </el-form-item>
            </template>

            <!-- 图文 -->
            <template v-else-if="type === '9'">
              <el-form-item label="图文地址" prop="link.url">
                <el-input
                  v-model="form.link.url"
                  type="text"
                  placeholder="请输入图文地址，以http://或https://开头"></el-input>
              </el-form-item>
              <el-form-item label="图文标题" prop="title">
                <el-input
                  v-model="form.title"
                  type="text"
                  :maxlength="32"
                  show-word-limit
                  placeholder="请输入图文标题"></el-input>
              </el-form-item>
              <el-form-item label="图文描述">
                <el-input
                  v-model="form.link.desc"
                  type="textarea"
                  :maxlength="128"
                  show-word-limit
                  :autosize="{ minRows: 3, maxRows: 50 }"
                  placeholder="请输入图文描述"></el-input>
              </el-form-item>
              <el-form-item label="图文封面">
                <Upload v-model:fileUrl="form.link.picUrl">
                  <template #tip><div>支持jpg/jpeg/png格式，建议200*200</div></template>
                </Upload>
              </el-form-item>
            </template>
          </el-form>
        </div>
        <div class="ml10" :span="10" v-if="!(type === '0' && form.id)">
          <PhoneChatList :list="[form]" />
        </div>
      </div>
    </template>
  </BaseDialog>
</template>

<style lang="scss" scoped>
.sub-des {
  color: var(--color);
  font-size: 12px;
  line-height: 1.5;
  margin-top: 5px;
}
.iframe {
  width: 100%;
  height: 255px;
  border: 1px solid var(--border-black-9);
  border-radius: var(--border-radius-small);
}
:deep() .g-card {
  padding: 0;
  border-radius: 0;
}
</style>
