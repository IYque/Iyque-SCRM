<script setup lang="ts">
import { ref, defineAsyncComponent } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { createFriendCircle, updateFriendCircle, getFriendCircleDetail, aiGenerateFriendCircle } from './api'
import { dictMsgType } from '@/utils/index'
import stores from '@/stores'

const MessageContentForm = defineAsyncComponent(() => import('@/components/MessageContentForm'))

const contentForm = ref([])
const bottom = ref(null)
const tabs = ref(null)

const $router = useRouter()
const $route = useRoute()

const $store = stores()

const active = ref(0)
const max = ref(1)

const aiLoading = ref(false)
const aiPromptDialogVisible = ref(false)
const aiPrompt = ref('')

const info = ref({
  id: '',
  name: '',
  content: '',
  annexLists: []
})

;(function getDetailFun(id = $route.query?.id) {
  if (!id) {
    // 读取本地 JSON 文件
    fetch('/result.json')
      .then(response => response.json())
      .then(data => {
        if (data.code === 200) {
          const result = JSON.parse(data.data)
          info.value.name = result.name
          info.value.content = result.content
        }
      })
      .catch(error => {
        console.error('读取 result.json 失败:', error)
      })
    return
  }
  $store.loading = true
  getFriendCircleDetail(typeof id === 'string' ? id : id[0])
    .then(({ data }) => {
      Object.assign(info.value, data)
      if (!info.value.annexLists) {
        info.value.annexLists = []
      }
    })
    .finally(() => {
      $store.loading = false
    })
})()

async function submit(submitFn, formRef) {
  let form = JSON.parse(JSON.stringify(info.value))

  if (!form.name) {
    return $sdk.msgError('请输入朋友圈名称')
  }

  if (!form.content) {
    return $sdk.msgError('请输入文本内容')
  }

  if (form.annexLists && form.annexLists.length > 0) {
    let tasks = form.annexLists.map(async (e, i) => {
      if (contentForm[i]) {
        let contentFormData = await contentForm[i].submit()
        if (contentFormData) {
          e[e.msgtype] = Object.assign(e[e.msgtype] || {}, contentFormData)
          return true
        } else {
          return false
        }
      }
      return true
    })
    await Promise.all(tasks)
  }

  const saveFn = info.value.id ? updateFriendCircle : createFriendCircle
  submitFn(() => saveFn(form), $router.back)
}

function add(msgtype) {
  info.value.annexLists.push({ msgtype, [msgtype]: {} })
  active.value = info.value.annexLists.length - 1
  setTimeout(() => {
    scrollIntoView(bottom.value)
  }, 100)
}

function remove(nameIndex) {
  $sdk.confirm().then(() => {
    info.value.annexLists.splice(nameIndex, 1)
    if (nameIndex <= active.value) {
      active.value = info.value.annexLists.length - 1
    }
  })
}

function scrollIntoView(el) {
  if (el) {
    el.scrollIntoViewIfNeeded
      ? el.scrollIntoViewIfNeeded(false)
      : el.scrollIntoView({ behavior: 'smooth', block: 'end' })
  }
}

function openAiGenerateDialog() {
  aiPrompt.value = ''
  aiPromptDialogVisible.value = true
}

async function executeAiPrompt() {
  if (!aiPrompt.value.trim()) {
    return $sdk.msgError('请输入提示词')
  }

  aiLoading.value = true
  try {
    const response = await aiGenerateFriendCircle(aiPrompt.value)
    const result = JSON.parse(response.data)
    info.value.name = result.name
    info.value.content = result.content
    $sdk.msgSuccess('AI生成成功')
    aiPromptDialogVisible.value = false
  } catch (error: any) {
    $sdk.msgError(error.message || 'AI处理失败')
  } finally {
    aiLoading.value = false
  }
}
</script>
<template>
  <BaForm class="h100 flexCol" v-model="info" ref="BaFormRef">
    <template #default="{ form, isDetail, formRef }">
      <div class="flex-auto overflow-auto">
        <div class="page-layout">
          <!-- 左侧：AI生成功能 -->
          <div class="sidebar">
            <div class="g-card ai-sidebar">
              <div class="g-card-title">AI智能助手</div>
              <div class="ai-tips">
                <el-alert
                  title="输入提示词，AI帮您生成朋友圈文案"
                  type="success"
                  :closable="false"
                  class="mb15"
                ></el-alert>
                <el-button type="primary" @click="openAiGenerateDialog" :disabled="isDetail" class="ai-generate-btn">
                  <span class="ai-icon">✨</span> 智能生成
                </el-button>
                <div class="ai-desc">
                  <p>• 输入产品特点、活动信息等</p>
                  <p>• AI自动生成标题和内容</p>
                  <p>• 支持emoji表情</p>
                </div>
              </div>
            </div>
          </div>
          
          <!-- 右侧：基础信息和附件内容 -->
          <div class="main-content">
            <div class="g-card">
              <div class="g-card-title">基础信息</div>
              <BaInput label="朋友圈名称" prop="name" required v-model="form.name" maxlength="50"></BaInput>
              <BaFormItem label="文本内容" prop="content" required>
                <el-input
                  v-model="form.content"
                  type="textarea"
                  :rows="5"
                  placeholder="请输入朋友圈文本内容"
                  :disabled="isDetail"
                ></el-input>
              </BaFormItem>

              <BaFormItem label="附件内容">
                <el-popover
                  trigger="hover"
                  content="只能上传一个附件（图片、链接、视频三选一）"
                  placement="top-start"
                  :disabled="form.annexLists?.length < max"
                >
                  <template #reference>
                    <el-dropdown @command="add" :disabled="form.annexLists?.length >= max || isDetail">
                      <el-button type="primary" class="mb10">添加</el-button>
                      <template #dropdown>
                        <el-dropdown-menu trigger="click">
                          <el-dropdown-item v-for="type in ['image', 'link', 'video']" :key="type" :command="type">
                            <el-button text>{{ dictMsgType[type].name }}</el-button>
                          </el-dropdown-item>
                        </el-dropdown-menu>
                      </template>
                    </el-dropdown>
                  </template>
                </el-popover>
                <el-alert
                  title="注: 1.图片:10MB,支持JPG,PNG格式; 2.视频:10MB,支持MP4格式"
                  type="error"
                  :closable="false"
                ></el-alert>
                <br />
                <el-tabs
                  ref="tabs"
                  v-model="active"
                  type="card"
                  class=""
                  closable
                  @tab-remove="remove"
                  :disabled="isDetail"
                >
                  <el-tab-pane
                    v-for="(item, index) in form.annexLists"
                    :key="index"
                    :label="dictMsgType[item.msgtype].name"
                    :name="index"
                  >
                    <MessageContentForm :type="item.msgtype" :ref="el => contentForm[index] = el" :form="item[item.msgtype]" />
                  </el-tab-pane>
                </el-tabs>
                <div ref="bottom"></div>
              </BaFormItem>
            </div>
          </div>
        </div>
      </div>
    </template>
    <template #operate="{ form, isDetail, formRef, submitFn }">
      <BaFormBar>
        <el-form>
          <ElButton type="primary" plain @click="$router.back()">取消</ElButton>
          <ElButton
            type="primary"
            v-loading="$store.loading"
            :disabled="$store.loading"
            @click="submit(submitFn, formRef)"
          >
            确定
          </ElButton>
        </el-form>
      </BaFormBar>
    </template>
  </BaForm>

  <el-dialog
    v-model="aiPromptDialogVisible"
    title="✨ AI智能生成朋友圈"
    width="500px"
    append-to-body
  >
    <el-form>
      <el-form-item label="生成需求">
        <el-input
          v-model="aiPrompt"
          type="textarea"
          :rows="4"
          placeholder="请输入您想生成什么样的朋友圈内容，例如：生成一条关于新商品上市的朋友圈，要吸引人、有感染力"
        ></el-input>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="aiPromptDialogVisible = false">取消</el-button>
      <el-button type="primary" :loading="aiLoading" @click="executeAiPrompt">
        开始生成
      </el-button>
    </template>
  </el-dialog>
</template>

<style scoped lang="scss">
.ai-tips {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.ai-icon {
  margin-right: 6px;
  font-size: 16px;
}

.ai-generate-btn {
  font-size: 14px;
  padding: 10px 20px;
  border-radius: 6px;
  background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%);
  border: none;
  box-shadow: 0 2px 12px 0 rgba(64, 158, 255, 0.3);
  transition: all 0.3s ease;
  
  &:hover {
    box-shadow: 0 4px 16px 0 rgba(64, 158, 255, 0.4);
    transform: translateY(-2px);
  }
  
  &:active {
    transform: translateY(0);
  }
}

.ai-desc {
  background: #f0f9eb;
  border: 1px solid #e1f3d8;
  border-radius: 6px;
  padding: 15px;
  margin-top: 10px;
  
  p {
    margin: 5px 0;
    color: #67c23a;
    font-size: 13px;
    line-height: 1.5;
  }
}

/* 调整基础信息区域的间距 */
.g-card-title {
  margin-top: 20px;
  margin-bottom: 15px;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  border-bottom: 2px solid #409eff;
  padding-bottom: 8px;
}

/* 调整表单间距 */
BaInput,
BaFormItem {
  margin-bottom: 15px;
}

/* 调整文本域高度 */
.el-textarea {
  width: 100%;
  min-height: 120px;
}

/* 调整按钮样式 */
.el-button {
  border-radius: 4px;
  margin-right: 10px;
}

/* 调整卡片样式 */
.g-card {
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  background: white;
}

/* 调整整体布局 */
.flex-auto {
  padding: 20px;
}

/* 调整页面标题 */
.g-card-title:first-child {
  margin-top: 0;
  font-size: 18px;
  color: #409eff;
}

/* 页面布局 */
.page-layout {
  display: flex;
  gap: 20px;
  width: 100%;
}

.main-content {
  flex: 1;
  min-width: 0;
}

.sidebar {
  width: 320px;
  flex-shrink: 0;
}

.ai-sidebar {
  position: sticky;
  top: 20px;
  max-height: calc(100vh - 120px);
  overflow-y: auto;
}

/* 响应式布局 */
@media (max-width: 1024px) {
  .page-layout {
    flex-direction: column;
  }
  
  .sidebar {
    width: 100%;
  }
  
  .ai-sidebar {
    position: relative;
    top: 0;
    max-height: none;
  }
}
</style>