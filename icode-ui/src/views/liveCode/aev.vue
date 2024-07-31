<template>
	<div class="">
		<el-form
			ref="form"
			:rules="rules"
			:model="form"
			label-position="right"
			label-width="100px"
			:scroll-into-view-options="true">
			<el-form-item label="渠道名称" prop="codeName">
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

			<el-form-item label="二维码logo" prop="logoUrl">
				<Upload v-model:fileUrl="form.logoUrl" :on-remove="handleRemove">
					<template #tip><div>图片大小不超过2M</div></template>
				</Upload>
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
			<el-form-item label="自动备注">
				<el-select v-model="form.remarkType" value-key="id" placeholder="请选择">
					<el-option v-for="item in remarkList" :key="item.key" :label="item.val" :value="item.key"></el-option>
				</el-select>
				<div class="g-tip">
					（注:选择后，添加的客户会根据所选自动备注如：【客户名-渠道名】,如果选择为标签类型,则新客标签需要存在）
				</div>
			</el-form-item>

			<el-form-item label="分时段">
				<el-switch v-model="form.startPeriodAnnex" @change="setChange"></el-switch>
				<div class="g-tip">开启后，根据添加时间发送当前时段欢迎语</div>
			</el-form-item>

			<template v-if="!form.startPeriodAnnex">
				<el-form-item label="欢迎语" prop="weclomeMsg" :required="annexLists?.length > 0">
					<TextareaExtend
						v-model="form.weclomeMsg"
						:toolbar="['emoji', 'insertCustomerNickName']"
						maxlength="200"
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
							<el-dropdown @command="add" :disabled="annexLists?.length >= max">
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
						type="error"
						:closable="false"></el-alert>
					<br />
					<el-tabs ref="tabs" v-model="active" type="card" class="" closable @tab-remove="remove">
						<el-tab-pane
							v-for="(item, index) in annexLists"
							:key="item.msgtype"
							:label="dictMsgType[item.msgtype].name"
							:name="index">
							<MessageContentForm :type="item.msgtype" ref="contentForm" :form="item[item.msgtype]" />
						</el-tab-pane>
					</el-tabs>
					<div ref="bottom"></div>
				</el-form-item>
			</template>

			<!-- 时段欢迎语 -->
			<el-form-item required v-if="form.startPeriodAnnex" label="欢迎语">
				<template v-for="(item, index) in form.periodAnnexLists" :key="index + 'welcom'">
					<el-card class="roster-card">
						<el-button
							class="fr"
							v-if="index !== 0"
							text
							icon="el-icon-delete"
							@click="
								$confirm().then(() => {
									form.periodAnnexLists.splice(index, 1)
								})
							">
							删除
						</el-button>
						<el-form-item label="工作周期">
							<el-checkbox-group v-model="item.workCycle">
								<el-checkbox label="1">周一</el-checkbox>
								<el-checkbox label="2">周二</el-checkbox>
								<el-checkbox label="3">周三</el-checkbox>
								<el-checkbox label="4">周四</el-checkbox>
								<el-checkbox label="5">周五</el-checkbox>
								<el-checkbox label="6">周六</el-checkbox>
								<el-checkbox label="7">周日</el-checkbox>
							</el-checkbox-group>
						</el-form-item>
						<el-form-item label="时间段">
							<el-time-select
								v-model="item.beginTime"
								v-bind="{
									start: '00:00',
									end: '23:59',
									step: '00:30',
								}"
								style="width: 160px"
								:max-time="item.endTime"
								placeholder="选择时间"></el-time-select>
							——
							<el-time-select
								v-bind="{
									start: '00:00',
									end: '23:59',
									step: '00:30',
								}"
								style="width: 160px"
								:min-time="item.beginTime || null"
								v-model="item.endTime"
								placeholder="选择时间"></el-time-select>
						</el-form-item>
						<el-form-item label="欢迎语">
							<TextareaExtend
								v-model="item.weclomeMsg"
								maxlength="200"
								show-word-limit
								placeholder="请输入欢迎语"
								:autosize="{ minRows: 5, maxRows: 20 }"
								clearable />
						</el-form-item>
						<el-form-item label="欢迎语附件" prop="" style="margin-bottom: 0">
							<el-popover
								trigger="hover"
								:content="'最多添加' + max + '个'"
								placement="top-start"
								:disabled="item.periodMsgAnnexList?.length < max">
								<template #reference>
									<el-dropdown
										@command="(msgtype) => add(msgtype, item, index)"
										:disabled="item.periodMsgAnnexList?.length >= max">
										<el-button type="primary" class="mb10">添加</el-button>
										<template #dropdown>
											<el-dropdown-menu trigger="click">
												<el-dropdown-item v-for="(unit, unique) in dictMsgType" :key="unique" :command="unit.type">
													<el-button text>{{ unit.name }}</el-button>
												</el-dropdown-item>
											</el-dropdown-menu>
										</template>
									</el-dropdown>
								</template>
							</el-popover>
							<el-alert
								title="注: 1.图片:10MB,支持JPG,PNG格式; 2.视频:10MB,支持MP4格式; 3.普通文件:20MB"
								type="error"
								:closable="false"></el-alert>
							<br />
							<el-tabs
								ref="tabs"
								v-model="item.active"
								type="card"
								class=""
								closable
								@tab-remove="(name) => remove(name, item, index)">
								<el-tab-pane
									v-for="(unit, unique) in item.periodMsgAnnexList"
									:key="unit.msgtype"
									:label="dictMsgType[unit.msgtype].name"
									:name="unique">
									<MessageContentForm :type="unit.msgtype" ref="contentForm" :form="unit[unit.msgtype]" />
								</el-tab-pane>
							</el-tabs>
							<div ref="bottom"></div>
						</el-form-item>
					</el-card>
				</template>
				<div class="mt20">
					<el-button type="primary" plain @click="form.periodAnnexLists.push(JSON.parse(JSON.stringify(originForm)))">
						+添加工作周期
					</el-button>
				</div>
			</el-form-item>
		</el-form>
	</div>
	<!-- <CommonTopRight>
      <el-button type="primary" size="default" @click="submit()">确定</el-button>
    </CommonTopRight> -->
</template>

<script>
import { findIYqueMsgAnnexByMsgId, add, update } from './api'
import { getUserList, getTagList, getRemarkList } from '@/api/common'
import { dictMsgType } from '@/utils/index'
export default {
	props: { data: {} },
	components: { MessageContentForm: defineAsyncComponent(() => import('../config/MessageContentForm.vue')) },
	data() {
		return {
			rules: {
				codeName: [{ required: true, message: '请输入活码名称', trigger: 'blur' }],
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
				weclomeMsg: [{ required: false, message: '必填项', trigger: 'blur' }],
			},
			originForm: {
				beginTime: '',
				endTime: '',
				workCycle: [],
				weclomeMsg: '您好，很高兴为您服务，请问有什么可以帮您？',
			},
			form: {
				codeName: '',
				skipVerify: 1, // 自动通过
				tags: [], // 标签
				users: [], // 标签
				remarkType: null, //客户备注
				logoUrl: null, //活码logo
				startPeriodAnnex: true,
			},

			selectedUserList: [],
			selectedTagList: [],

			userList: [],
			userErrorTip: '',
			tagList: [],
			tagErrorTip: '',
			remarkList: [],
			annexLists: [],
			max: 9,
			active: 0,
			dictMsgType,
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
				if (!element?.periodAnnexLists) {
					element.periodAnnexLists = [JSON.parse(JSON.stringify(this.originForm))]
				} else {
					element.periodAnnexLists.forEach((unit, index) => {
						unit.workCycle = unit.workCycle?.split(',') || []
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
		this.getRemarkList()
		// let id = this.$route.query.id
		// if (id) {
		//   this.getDetail(id)
		// }
		let id = this.form.id
		if (id) {
			this.getDetail(id)
		}
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

		getRemarkList() {
			getRemarkList().then((res) => {
				this.remarkList = res.data || []
			})
		},

		add(msgtype, item, index) {
			if (this.form.startPeriodAnnex) {
				item.periodMsgAnnexList ??= []
				item.active = item.periodMsgAnnexList.push({ msgtype, [msgtype]: {} }) - 1
				setTimeout(() => {
					this.$refs.bottom[index].scrollIntoView()
				}, 100)
			} else {
				this.active = this.annexLists.push({ msgtype, [msgtype]: {} }) - 1
				setTimeout(() => {
					this.$refs.bottom.scrollIntoView()
				}, 100)
			}
		},
		remove(nameIndex, item, index) {
			this.$confirm().then(() => {
				if (this.form.startPeriodAnnex) {
					item.periodMsgAnnexList.splice(nameIndex, 1)
					if (nameIndex <= item.active) {
						item.active = item.periodMsgAnnexList.length - 1
					}
					if (item.periodMsgAnnexList.length == 0) {
						this.$refs.form.clearValidate('weclomeMsg')
					}
				} else {
					this.annexLists.splice(nameIndex, 1)
					if (nameIndex <= this.active) {
						this.active = this.annexLists.length - 1
					}
					if (this.annexLists.length == 0) {
						this.$refs.form.clearValidate('weclomeMsg')
					}
				}
			})
		},

		/** 获取详情 */
		getDetail(id) {
			findIYqueMsgAnnexByMsgId(id).then((res) => {
				console.log(res.data)
				this.annexLists = res.data
			})
		},

		async submit() {
			let valid = await this.$refs.form.validate()
			if (!valid) return
			let form = JSON.parse(JSON.stringify(this.form))
			form.tagId = form.tags.map((e) => e.id) + ''
			form.tagName = form.tags.map((e) => e.name) + ''
			form.userId = form.users.map((e) => e.id) + ''
			form.userName = form.users.map((e) => e.name) + ''

			if (form.startPeriodAnnex) {
				let contentFormNum = 0
				let tasks = form.periodAnnexLists?.map(async (item, index) => {
					item.workCycle += ''
					let tasks1 = item.periodMsgAnnexList?.map(async (e, i) => {
						let contentForm = await this.$refs.contentForm[contentFormNum++].submit()
						if (contentForm) {
							e[e.msgtype] = Object.assign(e[e.msgtype] || {}, contentForm)
							return true
						} else {
							return false
						}
					})

					let validate1 = tasks1 ? await Promise.all(tasks1) : true
					return validate1
				})
				let validate1 = tasks ? await Promise.all(tasks) : true
			} else {
				let tasks = this.annexLists.map(async (e, i) => {
					let contentForm = await this.$refs.contentForm[i].submit()
					if (contentForm) {
						e[e.msgtype] = Object.assign(e[e.msgtype] || {}, contentForm)
						return true
					} else {
						return false
					}
				})
				let validate1 = tasks ? await Promise.all(tasks) : true
			}
			form.annexLists = this.annexLists

			return (form.id ? update : add)(form)
				.then(({ data }) => {
					this.msgSuccess('操作成功')
					// this.$router.back()
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
