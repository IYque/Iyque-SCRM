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
                              请选择指定时间段内客户的与员工的会话内容进行热词分析(受限于大模型的单词会话,每次分析建议时间间隔小一点)
                         </span>
					</el-form-item>
		        </el-form>
	</div>
</template>

<script>
import * as api from './api'
import { dictMsgType } from '@/utils/index'

let { aiHotWordAnalysis} = {}

export default {
	props: { data: {}, },
	data() {
		let _ = ({ aiHotWordAnalysis} = api)
		return {
			rules: {
				time: [{ required: true, message: '请输入客户与员工的聊天时间段', trigger: 'blur' }],
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
			aiHotWordAnalysis({
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
