<script>
import * as api from './api'
import * as apiLink from './apiLink'
import { env } from '../../../sys.config'
import aev from './aev.vue'

let { getList,findAiAnalysisMsgAudits,synchMsg,buildAISessionWarning} = {}

export default {
	data() {
		// let isLink = location.href.includes('customerLink')
		let _ = ({ getList,findAiAnalysisMsgAudits,synchMsg,buildAISessionWarning} = api)

		return {
			activeName: 'first',
			list: '',
			aiList:'',
			total: 0,
			aiTotal: 0,
			multipleSelection: [], // 多选数据
			loading: false,
			dialogVisible: false, // 弹窗显示控制
			form: {},
			queryParm: {
				fromName: null,
				acceptName: null,
				time: null,
				page: 1,
				size: 10
			},
			aiQueryParm: {
				warning: null,
				employeeName: null,
				customerName: null,
				time: null,
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
                { key: '违规', val: true },
                { key: '不违规', val: false },
            ]

			
		}
	},
	components: { aev },
	watch: {},
	created() {
		this.getList()
		this.getData()
		this.findAiAnalysisMsgAudits()
		// this.initSelect()
		// this.selectCount()
	},
	mounted() {},
	methods: {
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


		findAiAnalysisMsgAudits(page) {
			if(page == null){
				page=this.queryParm;
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


		submit() {
			this.loading = true
			this.$refs.aev
				.submit()
				.then(() => {
					this.dialogVisible = false
					this.getList()
					this.initSelect()
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
				page: this.aiQueryParm.page,
				size: this.aiQueryParm.size
			}

	
			 this.findAiAnalysisMsgAudits(queryParm)
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



		formatDate(date) {
			const year = date.getFullYear()
			const month = (date.getMonth() + 1).toString().padStart(2, '0')
			const day = date.getDate().toString().padStart(2, '0')
			return `${year}-${month}-${day}`
		},

		formatWarning(row, column, cellValue) {
            return cellValue ? '违规' : '不违规';
        },
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
			<el-tab-pane label="会话内容" name="first">
				<div class="g-card">
					<el-form class="searchForm" ref="searchForm" :model="queryParm" label-width="" inline>
					<el-form-item label="发送人:" prop="value3">
						<el-input v-model="queryParm.fromName" placeholder=""></el-input>
					</el-form-item>


					<el-form-item label="接收人:" prop="value3">
						<el-input v-model="queryParm.acceptName" placeholder=""></el-input>
					</el-form-item>

					<el-form-item label="时间:">
						<el-date-picker
							v-model="queryParm.time"
							type="daterange"
							range-separator="至"
							start-placeholder="开始日期"
							end-placeholder="结束日期"></el-date-picker>
					</el-form-item>

					<el-form-item>
						<el-button type="primary" @click="getData">查询</el-button>
						<el-button @click="restting">重置</el-button>
					</el-form-item>
				</el-form>
					
					<div class="fxbw">
						<el-button type="primary" @click="synchChatMsg()">同步会话</el-button>
					</div>
					<el-table
						:data="list"
						tooltip-effect="dark"
						highlight-current-row
						@selection-change="(selection) => (multipleSelection = selection.map((item) => item.msgId))">
						<el-table-column label="发送人" prop="fromName" show-overflow-tooltip />
						<el-table-column label="接收人" prop="acceptName"/>
						<el-table-column label="发送内容" prop="content" show-overflow-tooltip />
						<el-table-column label="发送时间" prop="msgTime" />


					</el-table>
					<pagination
						v-show="total > 0"
						:total="total"
						v-model:page="queryParm.page"
						v-model:limit="queryParm.size"
						@pagination="getList()" />
				</div>
			</el-tab-pane>

			<el-tab-pane label="AI会话报告" name="second">
				<div class="g-card">
					<el-form class="searchForm" ref="searchForm" :model="query2" label-width="" inline>

						<el-form-item label="是否违规:" prop="value3">
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

					<el-form-item label="员工名称:" prop="value3">
						<el-input v-model="aiQueryParm.employeeName" placeholder=""></el-input>
					</el-form-item>


					<el-form-item label="客户名称:" prop="value3">
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
						<el-button type="primary" @click="buildAISessionWarning()">AI会话预审</el-button>
					</div>
					<el-table
						:data="aiList"
						tooltip-effect="dark"
						highlight-current-row
						@selection-change="(selection) => (multipleSelection = selection.map((item) => item.id))">
						<!-- <el-table-column type="selection" width="50" align="center"></el-table-column> -->
						<el-table-column label="员工名称" prop="employeeName"/>
						<el-table-column label="客户名称" prop="customerName"/>

						<el-table-column label="员工是否有违规" prop="warning" :formatter="formatWarning">
						
						</el-table-column>

						<el-table-column label="违规提示" prop="msg" />
						<el-table-column label="分析时间" prop="createTime" />
					</el-table>
					<pagination
						v-show="aiTotal > 0"
						:total="aiTotal"
						v-model:page="aiQueryParm.page"
						v-model:limit="aiQueryParm.size"
						@pagination="findAiAnalysisMsgAudits()" />
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
