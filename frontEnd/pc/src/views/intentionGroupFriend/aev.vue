<template>
	<div class="">
		<el-form
			ref="form"
			:rules="rules"
			:model="form"
			label-position="right"
			label-width="100px"
			:scroll-into-view-options="true">
			<el-form-item label="规则内容" prop="ruleContent">
				<el-input type="textarea" :rows="4" v-model="form.ruleContent" maxlength="100" show-word-limit clearable placeholder="请输入"></el-input>
				<br/>
							<span style="color: #f00; font-size: 12px;">
                               请清晰填写相关规则
                         </span>
			</el-form-item>
		</el-form>
	</div>
</template>

<script>
import * as api from './api'
import { dictMsgType } from '@/utils/index'

let { saveOrUpdateMsgRule} = {}

export default {
	props: { data: {}, },
	data() {
		let _ = ({ saveOrUpdateMsgRule } = api)
		return {
			rules: {
				ruleContent: [{ required: true, message: '请输入规则内容', trigger: 'blur' }],
			},
			
			form: {
				ruleType: 4,
				id:null,
				ruleContent: ''
			}
		}
	},
	created() {
	
		if (this.data != null){

			console.log(this.data.id)
			console.log(this.data.ruleContent)
			this.form.ruleContent=this.data.ruleContent
			this.form.id=this.data.id
		}
		
	},

	methods: {


		async submit() {
			let valid = await this.$refs.form.validate()
			if (!valid) return
			let form = JSON.parse(JSON.stringify(this.form))
			saveOrUpdateMsgRule(form).then(({ data }) => {
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
