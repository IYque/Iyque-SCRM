<script>
import * as api from './api'
import * as apiLink from './apiLink'
import { env } from '../../../sys.config'
import aev from './aev.vue'
import synch from './synch.vue'

let { getList,findAiAnalysisMsgAudits,synchMsg,buildAISessionWarning,findIYqueMsgRules,del,saveOrUpdateMsgRule,batchStartOrStop} = {}

export default {
	data() {
		// let isLink = location.href.includes('customerLink')
		let _ = ({ getList,findAiAnalysisMsgAudits,synchMsg,buildAISessionWarning,findIYqueMsgRules,del,saveOrUpdateMsgRule,batchStartOrStop} = api)

		return {
			activeName: 'first',
			list: '',
			aiList:'',
			aiRuleList:'',
			total: 0,
			aiTotal: 0,
			aiRuleTotal: 0,
			multipleSelection: [], // 多选数据
			loading: false,
			dialogVisible: false, // 弹窗显示控制
			dialogAiVisible: false,
			form: {},
			queryParm: {
				fromName: null,
				acceptName: null,
				time: null,
				acceptType: 2,
				page: 1,
				size: 10
			},
			aiQueryParm: {
				warning: null,
				employeeName: null,
				customerName: null,
				msgAuditType: 4,
				time: null,
				page: 1,
				size: 10
			},
			aiRuleParm: {
				ruleStatus: null,
				ruleType: 4,
				page: 1,
				size: 10
			},
			options: [],
			xData: [],
			series: [],
			tabCount: {},
			// 下拉选项
            options: [
                { key: '全部', val: null },
                { key: '意向群友', val: true },
                { key: '非意向群友', val: false },
            ],
			aiRuleOptions: [
                { key: '全部', val: null },
                { key: '启用', val: true },
                { key: '停用', val: false },
            ]

			
		}
	},
	components: { aev,synch },
	watch: {},
	created() {
		this.getList()
		this.getData()
		this.findAiAnalysisMsgAudits()
		this.findIYqueMsgRules()
	},
	mounted() {},
	methods: {
		openDialog() {
			this.dialogAiVisible = true;
         },
		getList(page) {
			if(page == null){
				page=this.queryParm;
			}
			// page && (this.query.page = page)
			this.$store.loading = true
		    console.log(page);
			
			getList(page)
				.then(response => {
					this.list = response.data
					this.total = response.count
					this.multipleSelection = []
				})
				.catch((e) => console.error(e))
				.finally(() => (this.$store.loading = false))
		},

		del(id) {
			let ids = id
			
			this.$confirm()
				.then(() => {
					this.$store.loading = true
					return del(ids).then((res) => {
						this.msgSuccess('删除成功')
						this.findIYqueMsgRules()
					})
				})
				.catch((e) => {
					console.error(e)
				})
		},



		findAiAnalysisMsgAudits(page) {
			if(page == null){
				page=this.aiQueryParm;
			}

			this.$store.loading = true
		    console.log(page);
			
			findAiAnalysisMsgAudits(page)
				.then(({ data, count }) => {
					this.aiList = data
					this.aiTotal = +count
					this.multipleSelection = []
				})
				.catch((e) => console.error(e))
				.finally(() => (this.$store.loading = false))
		},

		findIYqueMsgRules(page) {
			if(page == null){
				page=this.aiRuleParm;
			}

			this.$store.loading = true
		    console.log(page);
			
			findIYqueMsgRules(page)
				.then(({ data, count }) => {
					this.aiRuleList = data
					this.aiRuleTotal = +count
					this.multipleSelection = []
				})
				.catch((e) => console.error(e))
				.finally(() => (this.$store.loading = false))
		},


		submit() {
			this.loading = true
			this.$refs.aev
				.submit()
				.then(() => {
					this.dialogVisible = false
					return this.findIYqueMsgRules()
				})
				.catch((e) => console.error(e))
				.finally(() => (this.loading = false))
		},

		submitAiVisible(){

			this.loading = true
			this.$refs.synch
				.submit()
				.then(() => {
					this.dialogAiVisible = false
				})
				.catch((e) => console.error(e))
				.finally(() => (this.loading = false))
		},
		synchChatMsg() {
            synchMsg()
			 .then(response => {
			
				this.msgSuccess(response.msg)
			 })
			 .catch((e) => console.error(e))
			.finally(() => (this.$store.loading = false))
		},

		buildAISessionWarning(){
			buildAISessionWarning()
			.then(response => {
				this.msgSuccess(response.msg)
			})
			.catch((e) => console.error(e))
			.finally(() => (this.$store.loading = false))
		},


		getData() {
			var queryParm = {
				fromName: this.queryParm.fromName,
				acceptName: this.queryParm.acceptName,
				startTime: this.queryParm.time === null ? null : this.formatDate(this.queryParm.time?.[0]),
				endTime: this.queryParm.time === null ? null : this.formatDate(this.queryParm.time?.[1]),
				acceptType: 2,
				page: this.queryParm.page,
				size: this.queryParm.size
			}

	
			 this.getList(queryParm)
		},

		getAIData() {
			var queryParm = {
				warning: this.aiQueryParm.warning,
				employeeName: this.aiQueryParm.employeeName,
				customerName: this.aiQueryParm.customerName,
				startTime: this.aiQueryParm.time === null ? null : this.formatDate(this.aiQueryParm.time?.[0]),
				endTime: this.aiQueryParm.time === null ? null : this.formatDate(this.aiQueryParm.time?.[1]),
				msgAuditType: 4,
				page: this.aiQueryParm.page,
				size: this.aiQueryParm.size
			}

	
			 this.findAiAnalysisMsgAudits(queryParm)
		},

		getAIRuleData() {
			var queryParm = {
				ruleStatus: this.aiRuleParm.ruleStatus,
				ruleType: 4,
				page: this.aiRuleParm.page,
				size: this.aiRuleParm.size
			}

	
			 this.findIYqueMsgRules(queryParm)
		},

		restting() {
			this.queryParm.fromName = null
			this.queryParm.acceptName = null
			this.queryParm.time = null
			
			this.getData()
		},

		resttingAi(){
			this.aiQueryParm.warning = null
			this.aiQueryParm.employeeName = null
			this.aiQueryParm.customerName = null
			this.aiQueryParm.time=null

            this.getAIData()
		},


		resttingRuleAi(){
			this.aiRuleParm.ruleStatus = null
	

            this.getAIRuleData()
		},



		formatDate(date) {
			const year = date.getFullYear()
			const month = (date.getMonth() + 1).toString().padStart(2, '0')
			const day = date.getDate().toString().padStart(2, '0')
			return `${year}-${month}-${day}`
		},

		formatWarning(row, column, cellValue) {
            return cellValue ? '意向群友' : '非意向群友';
        },
		formatStartOrStop(row, column, cellValue) {
            return cellValue ? '启用' : '停用';
        },
		formatDefaultRule(row, column, cellValue) {
            return cellValue ? '默认' : '非默认';
        },
		formatDateTimeRange(startTime, endTime) {
			if (!startTime || !endTime) return '-';
			return `${startTime} 至 ${endTime}`;
        },
		handleStatusChange() {
			console.log(this.multipleSelection?.join?.(','))
			
        // 这里可以添加处理状态改变的逻辑，例如调用后端接口更新状态
		  this.$confirm('是否需要启用或停用该条规则')
				.then(() => {
					return batchStartOrStop(
						this.multipleSelection?.join?.(',')
					).then((res) => {
						this.msgSuccess('操作成功')
						this.findIYqueMsgRules()
					})
				})
				.catch((e) => {
					console.error(e)
				})
        }
	},
}
</script>
<template>
	<div>
		<div class="warning">
			<a href="https://www.iyque.cn?utm_source=iyquecode" target="_blank">
				<strong>
					源雀Scrm-是基于Java源码交付的企微SCRM,帮助企业构建高度自由安全的私域平台。:https://www.iyque.cn/
				</strong>
			</a>
		</div>

		<el-tabs v-model="activeName">

			<el-tab-pane label="AI意向群友" name="first">

				<div class="warning">
				<strong>
					依据分析规则,借助AI大模型对群成员的聊天内容进行语义分析,判断是否是潜在意向客户 。
				</strong>
		       </div>

				<div class="g-card">
					<el-form class="searchForm" ref="searchForm" :model="query2" label-width="" inline>

						<el-form-item label="是否意向群友:" prop="value3">
							<el-select
								v-model="aiQueryParm.warning"
								collapse-tags
								collapse-tags-tooltip
								:max-collapse-tags="2"
								placeholder="全部"
								style="width: 260px">
								<el-option
									v-for="item in options"
									:key="item.val"
									:label="item.key"
									:value="item.val" />
							</el-select>
						</el-form-item>

					<el-form-item label="群友名称:" prop="value3">
						<el-input v-model="aiQueryParm.employeeName" placeholder=""></el-input>
					</el-form-item>


					<el-form-item label="客群名称:" prop="value3">
						<el-input v-model="aiQueryParm.customerName" placeholder=""></el-input>
					</el-form-item>

					<el-form-item label="时间:">
						<el-date-picker
							v-model="aiQueryParm.time"
							type="daterange"
							range-separator="至"
							start-placeholder="开始日期"
							end-placeholder="结束日期"></el-date-picker>
					</el-form-item>

					<el-form-item>
						<el-button type="primary" @click="getAIData">查询</el-button>
						<el-button @click="resttingAi">重置</el-button>
					</el-form-item>
				</el-form>
					
					<div class="fxbw">
						<el-button type="primary" @click=";(form = {}), (dialogAiVisible = true)">AI意向群友分析</el-button>
					</div>

			

					<el-table
						:data="aiList"
						tooltip-effect="dark"
						highlight-current-row
						@selection-change="(selection) => (multipleSelection = selection.map((item) => item.id))">
						<!-- <el-table-column type="selection" width="50" align="center"></el-table-column> -->
						<el-table-column label="群友名称" prop="employeeName"/>
						<el-table-column label="所属客群" prop="customerName"/>

						<el-table-column label="是否意向客户" prop="warning" :formatter="formatWarning">
						
						</el-table-column>

						<!-- 新增的“数据分析时间范围”列 -->
						<el-table-column label="数据分析时间范围">
							<template #default="{ row }">
							{{ formatDateTimeRange(row.startTime, row.endTime) }}
							</template>
						</el-table-column>

						<el-table-column label="意向提示" prop="msg" />
						<el-table-column label="分析时间" prop="createTime" />
					</el-table>
					<pagination
						v-show="aiTotal > 0"
						:total="aiTotal"
						v-model:page="aiQueryParm.page"
						v-model:limit="aiQueryParm.size"
						@pagination="findAiAnalysisMsgAudits()" />

						<el-dialog title="AI会话预审" v-model="dialogAiVisible" width="80%" :close-on-click-modal="false">
							<synch v-if="dialogAiVisible" :data="form" ref="synch"></synch>
							<template #footer>
								<el-button @click="dialogAiVisible = false">取消</el-button>
								<el-button type="primary" @click="submitAiVisible" v-loading="loading">确定</el-button>
							</template>
						</el-dialog>
				</div>

			</el-tab-pane>

			<el-tab-pane label="AI分析规则" name="second">
				<div class="g-card">
					<el-form class="searchForm" ref="searchForm" :model="query2" label-width="" inline>

						<el-form-item label="规则状态:" prop="value3">
							<el-select
								v-model="aiRuleParm.ruleStatus"
								collapse-tags
								collapse-tags-tooltip
								:max-collapse-tags="2"
								placeholder="全部"
								style="width: 260px">
								<el-option
									v-for="item in aiRuleOptions"
									:key="item.val"
									:label="item.key"
									:value="item.val" />
							</el-select>
						</el-form-item>


					<el-form-item>
						<el-button type="primary" @click="getAIRuleData">查询</el-button>
						<el-button @click="resttingRuleAi">重置</el-button>
					</el-form-item>
				</el-form>
					

				   <div class="fxbw">
						<el-button type="primary" @click=";(form = {}), (dialogVisible = true)">新建</el-button>
						<el-button :disabled="!multipleSelection.length" @click="handleStatusChange()" type="danger">批量启用或停用</el-button>
					</div>
					<el-table
						:data="aiRuleList"
						tooltip-effect="dark"
						highlight-current-row
						@selection-change="(selection) => (multipleSelection = selection.map((item) => item.id))">
						<el-table-column type="selection" width="50" align="center"></el-table-column>
						<el-table-column label="规则内容" prop="ruleContent"/>
						<el-table-column label="状态" prop="ruleStatus">
						<template #default="{ row }">
							<el-switch
								v-model="row.ruleStatus" disabled
							></el-switch>
						</template>
					    </el-table-column>

				

						<el-table-column label="是否默认" prop="defaultRule" :formatter="formatDefaultRule">						
						</el-table-column>
						<el-table-column label="创建时间" prop="createTime" />

						<el-table-column label="操作" fixed="right">
							<template #default="{ row }">
								<el-button text  :disabled="row.defaultRule" @click=";(form = JSON.parse(JSON.stringify(row))), (dialogVisible = true)">编辑</el-button>
								<el-button text  :disabled="row.defaultRule" @click="del(row.id)">删除</el-button>
							</template>
						</el-table-column>

					</el-table>
					<pagination
						v-show="aiRuleTotal > 0"
						:total="aiRuleTotal"
						v-model:page="aiRuleParm.page"
						v-model:limit="aiRuleParm.size"
						@pagination="findIYqueMsgRules()" />

						<el-dialog :title="form.id ? '编辑' : '新建'" v-model="dialogVisible" width="80%" :close-on-click-modal="false">
							<aev v-if="dialogVisible" :data="form" ref="aev"></aev>
							<template #footer>
								<el-button @click="dialogVisible = false">取消</el-button>
								<el-button type="primary" @click="submit" v-loading="loading">确定</el-button>
							</template>
						</el-dialog>


					


				</div>

			</el-tab-pane>
		</el-tabs>


	</div>
</template>

<style scoped lang="scss">
.warning {
	padding: 8px 16px;
	background-color: #fff6f7;
	border-radius: 4px;
	border-left: 5px solid #fe6c6f;
	margin: 20px 0;
	line-height: 40px;
}

.like {
	cursor: pointer;
	font-size: 25px;
	display: inline-block;
}
</style>
