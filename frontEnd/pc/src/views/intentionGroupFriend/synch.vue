<template>
	<div class="">
				<el-form
					ref="form"
					:rules="rules"
					:model="form"
					label-position="right"
					label-width="100px"
					:scroll-into-view-options="true">
					<el-form-item label="时间:" prop="time" aria-required="true">
						<el-date-picker
							v-model="form.time"
							type="daterange"
							range-separator="至"
							start-placeholder="开始日期"
							end-placeholder="结束日期"></el-date-picker>
							<br/>
							<span style="color: #f00; font-size: 12px;">
                               请选择员工与客户指定时间段内的聊天内容进行预审
                         </span>
					</el-form-item>
		        </el-form>
	</div>
</template>

<script>
import * as api from './api'
import { dictMsgType } from '@/utils/index'

let { saveOrUpdateMsgRule,buildAISessionWarning} = {}

export default {
	props: { data: {}, },
	data() {
		let _ = ({ saveOrUpdateMsgRule,buildAISessionWarning} = api)
		return {
			rules: {
				time: [{ required: true, message: '请输入员工与客户的聊天时间段', trigger: 'blur' }],
              },
			  form: {
				time: null,
			}
		}
	},

	methods: {

		formatDate(date) {
			const year = date.getFullYear()
			const month = (date.getMonth() + 1).toString().padStart(2, '0')
			const day = date.getDate().toString().padStart(2, '0')
			return `${year}-${month}-${day}`
		},


		async submit() {

			
			let valid = await this.$refs.form.validate()
			console.log(valid)
			if (!valid) return
			// console.log(this.formatDate(this.query.time?.[0]))
			buildAISessionWarning({
				msgAuditType: 4,
				startTime: this.formatDate(this.form.time?.[0]),
				endTime: this.formatDate(this.form.time?.[1])
			}).then(({ data }) => {
					this.msgSuccess('操作成功')
				})
				.finally(() => {
					this.$store.loading = false
				})
		},
	},
}
</script>

<style lang="scss" scoped></style>
