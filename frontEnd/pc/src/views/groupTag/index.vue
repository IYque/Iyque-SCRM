<script>
import * as api from './api'
import AddTag from '@/components/AddTag'
import { Edit, Delete, MagicStick } from '@element-plus/icons-vue'

export default {
  components: { AddTag, Edit, Delete, MagicStick },
  props: {},
  data() {
    return {
      api,
      // 遮罩层
      loading: false,
      dialogVisible: false,
      // 标签分组类型(1:客户企业标签;2:客群标签)
      groupTagType: 2,
      // 表单参数
      form: {
        groupName: '',
        weTags: [],
        groupTagType: 2,
      },
      // 添加标签输入框
      newInput: '',
      // 添加标签显隐
      newInputVisible: false,
      // 表单验证规则
      rules: Object.freeze({
        groupName: [{ required: true, message: '必填项', trigger: 'blur' }],
      }),
      // 非多个禁用
      multiple: true,
      // AI生成标签相关
      aiDialogVisible: false,
      aiPrompt: '',
      aiLoading: false,
      aiResult: [],
      // AI生成配置
      aiConfig: {
        groupCount: 2, // 默认生成2个标签组
        tagCountPerGroup: 3 // 默认每组3个标签
      }
    }
  },
  computed: {},
  created() {},
  methods: {
    edit(data, type) {
      this.form = JSON.parse(JSON.stringify(Object.assign({ groupTagType: this.groupTagType, weTags: [] }, data || {})))
      this.dialogVisible = true
    },
    // AI生成标签相关方法
    openAiDialog() {
      this.aiDialogVisible = true
      this.aiPrompt = ''
      this.aiResult = []
    },
    // 使用示例需求描述
    useExamplePrompt(example) {
      this.aiPrompt = example
      this.msgInfo('已填充示例需求描述，请根据实际情况修改')
    },
    // 编辑标签组名称
    editTagGroupName(groupIndex, currentName) {
      this.$prompt(
        '请输入新的标签组名称',
        '编辑标签组名称',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          inputValue: currentName || `标签组${groupIndex + 1}`,
          inputValidator: (value) => {
            if (!value) {
              return '标签组名称不能为空'
            }
            return true
          }
        }
      ).then(({ value }) => {
        if (this.aiResult[groupIndex]) {
          this.aiResult[groupIndex].groupName = value
          this.msgSuccess('标签组名称修改成功')
        }
      }).catch(() => {
        // 取消编辑
      })
    },
    // 编辑标签名称
    editTagName(groupIndex, tagIndex, currentName) {
      this.$prompt(
        '请输入新的标签名称',
        '编辑标签名称',
        {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          inputValue: currentName,
          inputValidator: (value) => {
            if (!value) {
              return '标签名称不能为空'
            }
            return true
          }
        }
      ).then(({ value }) => {
        if (this.aiResult[groupIndex] && this.aiResult[groupIndex].tags && this.aiResult[groupIndex].tags[tagIndex]) {
          this.aiResult[groupIndex].tags[tagIndex].name = value
          this.msgSuccess('标签名称修改成功')
        }
      }).catch(() => {
        // 取消编辑
      })
    },
    // 删除标签
    deleteTag(groupIndex, tagIndex) {
      this.$confirm('确定要删除这个标签吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        if (this.aiResult[groupIndex] && this.aiResult[groupIndex].tags) {
          this.aiResult[groupIndex].tags.splice(tagIndex, 1)
          this.msgSuccess('标签删除成功')
        }
      }).catch(() => {
        // 取消删除
      })
    },
    // 显示添加标签输入框
    showAddTagInput(groupIndex) {
      if (this.aiResult[groupIndex]) {
        this.aiResult[groupIndex].showAddInput = true
        this.aiResult[groupIndex].newTagName = ''
      }
    },
    // 添加新标签
    addTag(groupIndex) {
      if (this.aiResult[groupIndex]) {
        const tagGroup = this.aiResult[groupIndex]
        const newTagName = tagGroup.newTagName && tagGroup.newTagName.trim()
        
        if (!newTagName) {
          this.msgError('标签名称不能为空')
          return
        }
        
        // 检查标签是否已存在
        if (tagGroup.tags && Array.isArray(tagGroup.tags)) {
          const isExist = tagGroup.tags.some(tag => tag.name === newTagName)
          if (isExist) {
            this.msgError('标签名称已存在')
            return
          }
        } else {
          tagGroup.tags = []
        }
        
        tagGroup.tags.push({ name: newTagName })
        tagGroup.showAddInput = false
        tagGroup.newTagName = ''
        this.msgSuccess('标签添加成功')
      }
    },
    async generateTagsByAi() {
      if (!this.aiPrompt) {
        this.msgError('请输入生成标签的需求描述')
        return
      }
      
      this.aiLoading = true
      try {
        // 调用后端AI生成标签API，传递配置参数
        const response = await this.api.generateTagsByAi({
          prompt: this.aiPrompt,
          groupCount: this.aiConfig.groupCount,
          tagCountPerGroup: this.aiConfig.tagCountPerGroup
        })
        this.aiResult = response.data || []
        this.msgSuccess('AI生成标签成功')
      } catch (error) {
        this.msgError('AI生成标签失败，请重试')
        console.error('AI生成标签失败:', error)
      } finally {
        this.aiLoading = false
      }
    },
    async confirmAiResult() {
      if (this.aiResult.length === 0) {
        this.msgError('暂无生成的标签结果')
        return
      }
      
      this.loading = true
      try {
        // 遍历AI生成的标签组，创建每个标签组
        for (const tagGroup of this.aiResult) {
          if (tagGroup.tags && Array.isArray(tagGroup.tags)) {
            const formData = {
              groupName: tagGroup.groupName || `AI生成标签组_${Date.now()}`,
              weTags: tagGroup.tags.map(tag => ({ name: tag.name })),
              groupTagType: this.groupTagType
            }
            const response = await this.api.add(formData)
            // 检查响应状态
            if (response.code !== 200) {
              this.msgError(`创建标签组失败: ${response.msg || '未知错误'}`)
              return // 停止后续操作，保持弹框不消失
            }
          }
        }
        
        this.msgSuccess('AI生成标签组创建成功')
        this.aiDialogVisible = false
        // 刷新标签组列表
        this.$refs.rct.getList()
      } catch (error) {
        this.msgError(`创建标签组失败: ${error.response?.data?.msg || error.message || '未知错误'}`)
        console.error('创建标签组失败:', error)
      } finally {
        this.loading = false
      }
    },
  },
}
</script>
<template>
  <div class="groupTag">
    <RequestChartTable ref="rct" :request="api.getList" searchBtnType="icon">
      <template #query="{ query }">
        <BaInput label="标签组名称" prop="groupName" v-model="query.groupName"></BaInput>
      </template>

      <template #operate="{ goRoute }">
        <el-button type="primary" @click="edit()">新建标签组</el-button>
        <el-button
          type="success"
          style="margin-left: 10px; padding: 12px 20px; font-size: 14px;"
          @click="openAiDialog"
          class="ai-button">
          <template #icon>
            <MagicStick />
          </template>
          <template #default>
            <span>AI群标签生成</span>
          </template>
        </el-button>
      </template>

      <template #operation="{ goRoute, apiConfirm }">
        <div class="flex justify-end">
          <el-button type="danger" plain @click="apiConfirm(api.del)">批量删除</el-button>
        </div>
      </template>

      <template #table="{ apiConfirm, goRoute }">
        <el-table-column label="标签组" align="center" prop="groupName" />
        <el-table-column label="标签" align="center" prop="weTags">
          <template #default="{ row }">
            <div v-if="row.weTags">
              <TagEllipsis :list="row.weTags"></TagEllipsis>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center">
          <template #default="{ row, index }">
            <el-button text @click="edit(row, index)">编辑</el-button>
            <el-button text @click="apiConfirm(api.del, row.groupId)">删除</el-button>
          </template>
        </el-table-column>
      </template>
    </RequestChartTable>

    <!-- 弹窗 -->
    <AddTag
      v-model:visible="dialogVisible"
      :form="form"
      :key="dialogVisible"
      @success="$refs.rct.getList(!form.groupId && 1)"
      module="groupTag" />

    <!-- AI生成标签对话框 -->
    <el-dialog
      v-model="aiDialogVisible"
      title="AI群标签智能生成"
      width="600px"
      append-to-body
      :close-on-click-modal="false"
      :loading="loading">
      <el-form>
        <el-form-item label="需求描述">
          <el-input
            type="textarea"
            v-model="aiPrompt"
            :rows="4"
            placeholder="请描述您的标签需求，例如：我需要做母婴产品的客户分层"
            maxlength="200"
            show-word-limit></el-input>
          <div class="prompt-tips" style="margin-top: 8px; font-size: 12px; color: #909399;">
            <p>提示：请简洁描述您的需求，明确行业、用途和目标群体</p>
            <div class="prompt-examples" style="margin-top: 12px;">
              <p style="font-weight: 500; margin-bottom: 8px;">💡 推荐示例：</p>
              <div class="example-tags" style="display: flex; flex-wrap: wrap; gap: 10px;">
                <el-tag
                  size="small"
                  type="info"
                  @click="useExamplePrompt('我需要做母婴产品的客户分层')"
                  style="cursor: pointer; padding: 6px 12px; font-size: 13px; border-radius: 4px; transition: all 0.3s;">
                  母婴产品客户分层
                </el-tag>
                <el-tag
                  size="small"
                  type="info"
                  @click="useExamplePrompt('我需要为电子产品做用户画像标签')"
                  style="cursor: pointer; padding: 6px 12px; font-size: 13px; border-radius: 4px; transition: all 0.3s;">
                  电子产品用户画像
                </el-tag>
                <el-tag
                  size="small"
                  type="info"
                  @click="useExamplePrompt('我需要做电商平台的营销活动标签')"
                  style="cursor: pointer; padding: 6px 12px; font-size: 13px; border-radius: 4px; transition: all 0.3s;">
                  电商平台营销活动
                </el-tag>
                <el-tag
                  size="small"
                  type="info"
                  @click="useExamplePrompt('我需要为金融产品做高价值客户分层')"
                  style="cursor: pointer; padding: 6px 12px; font-size: 13px; border-radius: 4px; transition: all 0.3s;">
                  金融产品高价值客户
                </el-tag>
              </div>
            </div>
          </div>
        </el-form-item>
        <div class="flex flex-col gap-4" style="margin-top: 16px;">
          <el-form-item label="标签组数量" style="width: 100%;">
            <el-slider
              v-model="aiConfig.groupCount"
              :min="1"
              :max="100"
              :step="1"
              show-input
              style="width: 100%;">
            </el-slider>
          </el-form-item>
          <el-form-item label="每组标签数" style="width: 100%;">
            <el-slider
              v-model="aiConfig.tagCountPerGroup"
              :min="1"
              :max="100"
              :step="1"
              show-input
              style="width: 100%;">
            </el-slider>
          </el-form-item>
        </div>
        
        <el-form-item label="生成结果" v-if="aiResult.length > 0">
          <div class="ai-result-scroll" style="max-height: 400px; overflow-y: auto; padding-right: 8px;">
            <div v-for="(tagGroup, groupIndex) in aiResult" :key="groupIndex" class="ai-tag-group">
              <div class="flex justify-between items-center">
                <h4>{{ tagGroup.groupName || `标签组${groupIndex + 1}` }}</h4>
                <el-button
                  type="primary"
                  plain
                  size="small"
                  @click="editTagGroupName(groupIndex, tagGroup.groupName)"
                  title="编辑标签组名称">
                  <el-icon><Edit /></el-icon>
                </el-button>
              </div>
              <div class="ai-tags">
                <div v-for="(tag, tagIndex) in tagGroup.tags" :key="tagIndex" class="tag-item">
                  <el-tag
                    type="info"
                    size="small"
                    style="margin-right: 8px; margin-bottom: 8px;">
                    {{ tag.name }}
                    <el-button
                      type="text"
                      size="small"
                      @click.stop="editTagName(groupIndex, tagIndex, tag.name)"
                      style="margin-left: 4px;"
                      title="编辑标签名称">
                      <el-icon><Edit /></el-icon>
                    </el-button>
                    <el-button
                      type="text"
                      size="small"
                      @click.stop="deleteTag(groupIndex, tagIndex)"
                      style="margin-left: 4px; color: #F56C6C;"
                      title="删除标签">
                      <el-icon><Delete /></el-icon>
                    </el-button>
                  </el-tag>
                </div>
                <div class="add-tag-input" v-if="tagGroup.showAddInput">
                  <el-input
                    v-model="tagGroup.newTagName"
                    size="small"
                    placeholder="输入新标签名称"
                    @keyup.enter="addTag(groupIndex)"
                    @blur="addTag(groupIndex)"
                    style="width: 150px; margin-right: 8px;">
                  </el-input>
                  <el-button
                    type="primary"
                    size="small"
                    @click="addTag(groupIndex)">
                    添加
                  </el-button>
                </div>
                <el-button
                  type="success"
                  plain
                  size="small"
                  @click="showAddTagInput(groupIndex)"
                  v-if="!tagGroup.showAddInput">
                  添加标签
                </el-button>
              </div>
            </div>
          </div>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="aiDialogVisible = false">取消</el-button>
          <el-button
            type="primary"
            @click="confirmAiResult"
            :disabled="aiResult.length === 0">
            确认创建
          </el-button>
          <el-button
            type="success"
            @click="generateTagsByAi"
            :loading="aiLoading"
            v-preventReClick="1000">
            生成标签
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>
<style lang="scss" scoped>
.ai-tag-group {
  margin-bottom: 16px;
  padding: 12px;
  background-color: #f5f7fa;
  border-radius: 4px;
  
  h4 {
    margin: 0 0 12px 0;
    font-size: 14px;
    font-weight: 600;
    color: #303133;
  }
}

.ai-tags {
  display: flex;
  flex-wrap: wrap;
}

/* AI按钮现代化样式 */
.ai-button {
  position: relative;
  overflow: hidden;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(103, 194, 58, 0.3);
  border-radius: 8px;
}

.ai-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(103, 194, 58, 0.4);
  background-color: #67c23a;
  color: white;
}

.ai-button:active {
  transform: translateY(0);
  box-shadow: 0 2px 6px rgba(103, 194, 58, 0.3);
}

/* 魔法棒图标动画效果 */
.ai-button :deep(.el-icon) {
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0% {
    transform: scale(1);
    opacity: 1;
  }
  50% {
    transform: scale(1.1);
    opacity: 0.8;
  }
  100% {
    transform: scale(1);
    opacity: 1;
  }
}

</style>