<script>
import * as api from '@/api/category'

export default {
  props: {
    // 0:图片；4:文本；7：热词分类；9：图文；13：组合话术
    type: {
      type: String,
      required: true,
    },
    // 选中的素材ids
    selected: {
      type: [Array, String],
      default: '',
    },
    isDisabledAction: {
      type: Boolean,
      default: false,
    },
    moduleType: {
      type: String,
      default: '1',
    },
  },
  data() {
    return {
      groupList: [],
      groupVisible: false,
      groupForm: {
        mediaType: this.type,
        name: '',
      },
      groupRules: {
        name: [
          {
            required: true,
            message: '请输入分组名称',
            trigger: 'blur',
          },
          {
            min: 1,
            max: 15,
            message: '长度在 1 到 15 个字符',
            trigger: 'blur',
          },
        ],
      },
      delObj: { updateOrDel: 1, moduleType: +this.moduleType, ids: [] },

      loading: true, // 遮罩层
      activeId: 0,
    }
  },
  watch: {},
  computed: {},
  created() {
    // this.query.mediaType = this.type
    this.getTree(true)
  },
  mounted() {},
  methods: {
    addGroup() {
      this.groupForm = {
        mediaType: this.type,
        name: '',
      }
      this.groupVisible = true
    },
    switchGroup(index, data) {
      this.activeId = data.id
      this.$emit('currentChange', data)
    },
    // 新增分组
    onAddOrUpdateGroup() {
      this.$refs.groupForm.validate((validate) => {
        if (this.groupForm.id) {
          let obj = {
            id: this.groupForm.id,
            name: this.groupForm.name,
            mediaType: this.type,
          }
          this.groupForm = obj
        }
        if (validate) {
          api.save(this.groupForm).then((res) => {
            this.groupVisible = false
            this.groupForm = {
              name: '',
              mediaType: this.type,
            }
            this.$refs.groupForm.clearValidate()
            // this.getCodeCategoryListFn()
            this.getTree()
          })
        }
      })
    },
    editGroup(group) {
      this.groupForm = JSON.parse(JSON.stringify(group))
      this.groupVisible = true
    },
    removeGroup(id) {
      this.$confirm('是否确认删除当前分组？删除后该分组下内容将移动到默认分组中，该操作不可撤销，请谨慎操作。')
        .then(() => {
          api.del(id).then((res) => {
            if (id == this.activeId) {
              this.activeId = undefined
              this.getTree(true)
            } else {
              this.getTree()
            }
          })
        })
        .catch(() => {})
    },
    // 获取类目树
    getTree(refresh) {
      api.getList(this.type).then(({ data }) => {
        // 删除每一项的children属性
        data.forEach((item) => {
          item.children = null
        })
        this.groupList = data
        if (refresh) {
          this.activeId = data[0].id
          this.$emit('currentChange', data[0])
        }
      })
    },
  },
}
</script>

<template>
  <div class="h100">
    <div class="leftRight">
      <div class="left g-card">
        <div class="title">
          <div class="title-name">分组</div>
          <IconBtn v-if="!isDisabledAction" icon="ElIconPlus" @click="addGroup">新增分组</IconBtn>
        </div>
        <div class="item-list">
          <div
            class="item"
            v-for="(group, index) in groupList"
            :class="{ active: activeId == group.id }"
            :key="group.id"
            @click="switchGroup(index, group)">
            <div class="name">{{ group.name }}</div>
            <div v-if="group.id != 0 && !isDisabledAction">
              <IconBtn icon="el-icon-edit" @click.stop="editGroup(group)">编辑</IconBtn>
              <IconBtn icon="el-icon-delete" @click.stop="removeGroup(group.id)">删除</IconBtn>
            </div>
          </div>
        </div>
      </div>

      <div class="right">
        <slot></slot>
      </div>
    </div>

    <!-- 分组弹框 -->
    <el-drawer :title="`${groupForm.id ? '修改' : '新建'}分组`" v-model="groupVisible" size="30%" v-if="groupVisible">
      <el-form :model="groupForm" :rules="groupRules" ref="groupForm">
        <el-form-item label="分组名称" prop="name" label-width="80px">
          <el-input v-model="groupForm.name" clearable autocomplete="off" maxlength="15" show-word-limit></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="groupVisible = false">取 消</el-button>
          <el-button type="primary" @click="onAddOrUpdateGroup">确 定</el-button>
        </div>
      </template>
    </el-drawer>
  </div>
</template>

<style lang="scss" scoped>
.leftRight {
  display: flex;
  justify-content: space-between;
  max-height: 100%;
  .left {
    flex: auto;
    display: flex;
    flex-direction: column;
    margin-right: var(--card-margin);
    .title {
      display: flex;
      justify-content: space-between;
      align-items: center;
      .title-name {
        color: var(--font-black);
        font-size: 14px;
        font-weight: 600;
        // color: var(--font-black);
        display: flex;
        align-items: center;
      }
      .title-btn {
        cursor: pointer;
        display: flex;
        align-items: center;
        font-size: 14px;
        font-weight: normal;
        color: var(--color);
        &:hover {
          opacity: 0.8;
        }
      }
    }

    .item-list {
      margin-top: 15px;
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
        color: var(--font-black-5);
        height: 40px;
        line-height: 40px;
        width: 100%;
        padding-left: 20px;
        .name {
          overflow: hidden;
          white-space: nowrap;
          text-overflow: ellipsis;
        }
        &.active,
        &:hover {
          .name {
            color: var(--color);
          }
        }
      }
    }
  }
  .right {
    width: 78%;
    display: flex;
    flex-direction: column;
    flex: none;
    overflow: auto !important;
  }
}
</style>
