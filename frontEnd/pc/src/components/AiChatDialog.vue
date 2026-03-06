<template>
  <el-dialog 
    v-model="visible" 
    :title="dialogType === 'create' ? '新建会话' : '编辑会话'" 
    width="550px" 
    :close-on-click-modal="true"
    @close="handleClose"
  >
    <div class="chat-dialog-form">
      <div class="form-section">
        <div class="form-section-title">基本信息</div>
        <div class="form-item">
          <label class="form-label">会话名称 <span class="required">*</span></label>
          <el-input 
            v-model="formData.title" 
            placeholder="请输入会话名称" 
            maxlength="20"
            show-word-limit
          />
        </div>
        <div class="form-item">
          <label class="form-label">会话模式</label>
          <div class="mode-list">
            <div 
              class="mode-card" 
              :class="{ 'selected': formData.mode === 'general', 'disabled': dialogType === 'edit' && formData.mode !== 'general' }"
              @click="selectMode('general')"
            >
              <div class="mode-icon general-icon">
                <svg viewBox="0 0 24 24" fill="currentColor">
                  <path d="M20 2H4c-1.1 0-2 .9-2 2v18l4-4h14c1.1 0 2-.9 2-2V4c0-1.1-.9-2-2-2zm0 14H6l-2 2V4h16v12z"/>
                </svg>
              </div>
              <div class="mode-info">
                <div class="mode-title">通用</div>
                <div class="mode-desc">使用大模型进行通用对话</div>
              </div>
              <div class="mode-check" v-if="formData.mode === 'general'">
                <el-icon><svg viewBox="0 0 24 24" fill="currentColor"><path d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z"/></svg></el-icon>
              </div>
            </div>
            <div 
              class="mode-card" 
              :class="{ 'selected': formData.mode === 'navigation', 'disabled': dialogType === 'edit' && formData.mode !== 'navigation' }"
              @click="selectMode('navigation')"
            >
              <div class="mode-icon navigation-icon">
                <svg viewBox="0 0 24 24" fill="currentColor">
                  <path d="M12 2C8.13 2 5 5.13 5 9c0 5.25 7 13 7 13s7-7.75 7-13c0-3.87-3.13-7-7-7zm0 9.5c-1.38 0-2.5-1.12-2.5-2.5s1.12-2.5 2.5-2.5 2.5 1.12 2.5 2.5-1.12 2.5-2.5 2.5z"/>
                </svg>
              </div>
              <div class="mode-info">
                <div class="mode-title">AI导航</div>
                <div class="mode-desc">智能推荐系统功能</div>
              </div>
              <div class="mode-check" v-if="formData.mode === 'navigation'">
                <el-icon><svg viewBox="0 0 24 24" fill="currentColor"><path d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z"/></svg></el-icon>
              </div>
            </div>
          </div>
          <div class="mode-hint-text" v-if="dialogType === 'edit'">会话模式不支持修改</div>
        </div>
        <div class="form-item">
          <label class="form-label">AI模型 <span class="required">*</span></label>
          <el-select 
            v-model="formData.modelName" 
            placeholder="选择模型" 
            style="width: 100%;"
            :teleported="false"
          >
            <el-option 
              v-for="(model, index) in availableModels" 
              :key="index" 
              :label="model" 
              :value="model" 
            />
          </el-select>
        </div>
      </div>
      
      <div class="form-section" v-if="formData.mode === 'general'">
        <div class="form-section-title">AI设置</div>
        <div class="form-item">
          <label class="form-label">AI角色设定</label>
          <el-input 
            v-model="formData.role" 
            type="textarea" 
            :rows="3"
            placeholder="请输入AI角色设定"
          />
        </div>
        
        <div class="form-item">
          <label class="form-label">温度参数 (temperature): {{ formData.temperature }}</label>
          <el-slider v-model="formData.temperature" :min="0" :max="1" :step="0.1" />
          <div class="slider-hint">值越大回答越随机，值越小回答越确定</div>
        </div>
        
        <div class="form-item">
          <label class="form-label">核采样 (topP): {{ formData.topP }}</label>
          <el-slider v-model="formData.topP" :min="0" :max="1" :step="0.1" />
          <div class="slider-hint">值越大回答越多样</div>
        </div>
        
        <div class="form-item">
          <label class="form-label">历史对话轮数: {{ formData.maxHistoryRounds }}轮</label>
          <el-slider v-model="formData.maxHistoryRounds" :min="1" :max="20" :step="1" />
          <div class="slider-hint">保留的历史对话轮数</div>
        </div>
      </div>
    </div>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="handleClose">取消</el-button>
        <el-button type="primary" @click="handleConfirm" :loading="loading">
          {{ dialogType === 'create' ? '创建会话' : '确定' }}
        </el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, watch, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getAvailableModels } from '@/api/ai'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  dialogType: {
    type: String,
    default: 'create',
    validator: (value) => ['create', 'edit'].includes(value)
  },
  chatData: {
    type: Object,
    default: () => null
  }
})

const emit = defineEmits(['update:modelValue', 'confirm'])

const visible = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

const loading = ref(false)
const availableModels = ref([])

const defaultFormData = {
  title: '',
  mode: 'general',
  modelName: '',
  role: '你是一个SCRM营销助手，专注于客户关系管理和营销策略。请用专业、友好的语言回答用户的问题，提供实用的营销建议和客户管理技巧。',
  temperature: 0.7,
  topP: 0.9,
  maxHistoryRounds: 10
}

const formData = reactive({ ...defaultFormData })

const loadAvailableModels = async () => {
  try {
    const response = await getAvailableModels()
    if (response && Array.isArray(response.data)) {
      availableModels.value = response.data
    }
  } catch (error) {
    console.error('加载模型列表失败:', error)
  }
}

const selectMode = (mode) => {
  if (props.dialogType === 'edit') return
  formData.mode = mode
}

const resetForm = () => {
  Object.assign(formData, defaultFormData)
}

const initFormData = () => {
  if (props.dialogType === 'edit' && props.chatData) {
    const settings = props.chatData.settings || {}
    Object.assign(formData, {
      title: props.chatData.title || '',
      mode: props.chatData.mode || 'general',
      modelName: settings.modelName || '',
      role: settings.role || defaultFormData.role,
      temperature: settings.temperature ?? defaultFormData.temperature,
      topP: settings.topP ?? defaultFormData.topP,
      maxHistoryRounds: settings.maxHistoryRounds ?? defaultFormData.maxHistoryRounds
    })
  } else {
    resetForm()
  }
}

watch(() => props.modelValue, async (val) => {
  if (val) {
    if (availableModels.value.length === 0) {
      await loadAvailableModels()
    }
    initFormData()
  }
})

const handleClose = () => {
  visible.value = false
}

const handleConfirm = async () => {
  if (!formData.title.trim()) {
    ElMessage.warning('请输入会话名称')
    return
  }
  if (!formData.modelName) {
    ElMessage.warning('请选择AI模型')
    return
  }
  
  loading.value = true
  try {
    emit('confirm', {
      type: props.dialogType,
      data: {
        title: formData.title.trim(),
        mode: formData.mode,
        modelName: formData.modelName,
        role: formData.role,
        temperature: formData.temperature,
        topP: formData.topP,
        maxHistoryRounds: formData.maxHistoryRounds
      }
    })
    handleClose()
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.chat-dialog-form {
  padding: 0 10px;
}

.form-section {
  margin-bottom: 24px;
}

.form-section-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 16px;
  padding-bottom: 8px;
  border-bottom: 1px solid #ebeef5;
}

.form-item {
  margin-bottom: 18px;
}

.form-label {
  display: block;
  font-size: 14px;
  color: #606266;
  margin-bottom: 8px;
}

.required {
  color: #f56c6c;
}

.mode-list {
  display: flex;
  gap: 12px;
}

.mode-card {
  flex: 1;
  padding: 16px;
  border-radius: 12px;
  border: 2px solid #e4e7ed;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  text-align: center;
}

.mode-card:hover {
  border-color: #409eff;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.15);
}

.mode-card.selected {
  border-color: #409eff;
  background: linear-gradient(135deg, #f0f7ff 0%, #e6f1ff 100%);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.2);
}

.mode-card.disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.mode-card.disabled:hover {
  transform: none;
  box-shadow: none;
}

.mode-icon {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 8px;
}

.mode-icon svg {
  width: 24px;
  height: 24px;
}

.mode-icon.general-icon {
  background: linear-gradient(135deg, #667eea20 0%, #764ba220 100%);
  color: #667eea;
}

.mode-icon.navigation-icon {
  background: linear-gradient(135deg, #f093fb20 0%, #f5576c20 100%);
  color: #f5576c;
}

.mode-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.mode-title {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
}

.mode-desc {
  font-size: 12px;
  color: #909399;
  line-height: 1.4;
}

.mode-check {
  margin-top: 8px;
  color: #409eff;
}

.mode-hint-text {
  font-size: 12px;
  color: #909399;
  margin-top: 8px;
  text-align: center;
}

.slider-hint {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}
</style>
