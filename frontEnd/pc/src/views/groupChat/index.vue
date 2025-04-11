<script>
import * as api from './api'
import { env } from '../../../sys.config'
let { getList,synchIyqueChat} = {}

export default {
	data() {
		// let isLink = location.href.includes('customerLink')
		let _ = ({ getList,synchIyqueChat} = api)

		return {
			activeName: 'first',
			list: '',
			total: 0,
			multipleSelection: [], // 多选数据
			loading: false,
			dialogVisible: false, // 弹窗显示控制
			form: {},
			queryParm: {
				chatName: null,
				page: 1,
				size: 10
			},
			options: [],
			xData: [],
			series: [],
			tabCount: {}

			
		}
	},
	watch: {},
	created() {
		this.getList()
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

		getData() {
			var queryParm = {
				chatName: this.queryParm.chatName,
				page: this.queryParm.page,
				size: this.queryParm.size
			}

	
			 this.getList(queryParm)
		},



		synchIyqueChat() {
            synchIyqueChat()
			 .then(response => {
			
				this.msgSuccess(response.msg)
			 })
			 .catch((e) => console.error(e))
			.finally(() => (this.$store.loading = false))
		},



		restting() {
			this.queryParm.chatName = null
			this.getData()
		},

		 // 格式化状态值
		 formatStatus(row) {
			const statusMap = {
				1: '已激活',
				2: '已禁用',
				4: '未激活',
				5: '退出企业',
			};
			return statusMap[row.status] || '未知状态';
			},



		formatDate(date) {
			const year = date.getFullYear()
			const month = (date.getMonth() + 1).toString().padStart(2, '0')
			const day = date.getDate().toString().padStart(2, '0')
			return `${year}-${month}-${day}`
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


		<div class="g-card">
					<el-form class="searchForm" ref="searchForm" :model="queryParm" label-width="" inline>
					<el-form-item label="群名称:" prop="value3">
						<el-input v-model="queryParm.chatName" placeholder=""></el-input>
					</el-form-item>


					<el-form-item>
						<el-button type="primary" @click="getData">查询</el-button>
						<el-button @click="restting">重置</el-button>
					</el-form-item>
				</el-form>
					
					<div class="fxbw">
						<el-button type="primary" @click="synchIyqueChat()">同步客群</el-button>
					</div>
					<el-table
						:data="list"
						tooltip-effect="dark"
						highlight-current-row
						@selection-change="(selection) => (multipleSelection = selection.map((item) => item.msgId))">
						<el-table-column label="群名称" prop="chatName" />
						<el-table-column label="群主" prop="ownerName" ></el-table-column>
						<el-table-column label="创建时间" prop="createTime" />

					</el-table>
					<pagination
						v-show="total > 0"
						:total="total"
						v-model:page="queryParm.page"
						v-model:limit="queryParm.size"
						@pagination="getList()" />
				</div>


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
