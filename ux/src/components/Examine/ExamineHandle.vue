<template>
  <el-dialog
    ref="wkDialog"
    :title="title"
    :append-to-body="true"
    :close-on-click-modal="false"
    :visible.sync="showDialog"
    width="600px"
    @close="hiddenView">
    <el-input
      v-model="content"
      :rows="8"
      :maxlength="200"
      :placeholder="placeholder"
      type="textarea"
      resize="none"
      show-word-limit />
    <div
      slot="footer"
      class="dialog-footer">
      <el-button
        type="primary"
        @click="handleClick('confirm')">确定</el-button>
      <el-button @click="handleClick('cancel')">取消</el-button>
    </div>
  </el-dialog>
</template>
<script type="text/javascript">
import {
  crmExamineRecordAuditAPI,
  examineEndAPI
} from '@/api/examine'

import ElDialogLoadingMixin from '@/mixins/ElDialogLoading'
import { mapGetters } from 'vuex'

export default {
  name: 'ExamineHandle', // 合同审核操作
  components: {
  },
  mixins: [ElDialogLoadingMixin],
  props: {
    show: {
      type: Boolean,
      default: false
    },
    // 审核 1 审核通过 2 审核拒绝 4 已撤回 13 终止
    status: {
      type: [String, Number],
      default: 1
    },
    // 详情信息id
    id: [String, Number],
    recordId: [String, Number],
    // crm_contract crm_receivables oa_examine
    examineType: {
      type: String,
      default: ''
    },
    flowId: {
      type: [String, Number],
      default: ''
    }
  },
  data() {
    return {
      loading: false,
      showDialog: false,
      content: '' // 输入内容
    }
  },
  computed: {
    ...mapGetters(['userInfo']),
    title() {
      if (this.status == 1) {
        return '审批通过'
      } else if (this.status == 2) {
        return '审批拒绝'
      } else if (this.status == 4) {
        return '撤回审批'
      } else if (this.status == 13) {
        return '审批终止'
      } else if (this.status == 14) {
        return '归档'
      } else if (this.status == 8) {
        return '作废'
      }
      return ''
    },
    placeholder() {
      // 1 审核通过 2 审核拒绝 4 已撤回
      if (this.status == 1) {
        return '请输入审批意见（选填）'
      } else if (this.status == 2) {
        return '请输入审批意见（必填）'
      } else if (this.status == 4) {
        return '请输入撤回理由（必填）'
      } else if (this.status == 13) {
        return '请输入终止理由（必填）'
      } else if (this.status == 14) {
        return '请输入归档意见（选填）'
      } else if (this.status == 8) {
        return '请输入作废意见（选填）'
      }
      return ''
    }
  },
  watch: {
    show: {
      handler(val) {
        this.showDialog = val
      },
      deep: true,
      immediate: true
    }
  },
  mounted() {},
  methods: {
    /**
     * 提交数据
     */
    submitInfo() {
      if ((this.status == 2 || this.status == 4 || this.status == 13) && !this.content) {
        this.$message.error(this.placeholder)
      } else {
        if (this.$listeners.confirm) {
          this.$emit('confirm', this.content)
        } else {
          this.loading = true
          let params = null
          let request = null
          if (this.status == 13) { // 终止
            request = examineEndAPI
            params = {
              recordId: this.recordId,
              flowId: this.flowId,
              status: this.status,
              remarks: this.content,
              examineUserId: this.userInfo.userId
            }
          } else { // 通过/拒绝
            request = crmExamineRecordAuditAPI
            params = {
              typeId: this.id,
              recordId: this.recordId,
              status: this.status,
              remarks: this.content
            }
          }
          request(params)
            .then(res => {
              this.loading = false
              this.$message.success('操作成功')
              // 刷新待办
              if (
                this.examineType == 'crm_contract' ||
                  this.examineType == 'crm_invoice' ||
                  this.examineType == 'crm_receivables'
              ) {
                this.$store.dispatch('GetMessageNum')
              }

              this.resetInfo()

              this.$emit('save', { type: this.status })
              this.hiddenView()
            })
            .catch(() => {
              this.loading = false
            })
        }
      }
    },

    /**
     * 操作点击
     */
    handleClick(type) {
      if (type == 'cancel') {
        this.hiddenView()
        this.resetInfo()
      } else if (type == 'confirm') {
        if (!this.recordId && this.$route.name == 'link') {
          const flag = (this.status == 2 || this.status == 4 || this.status == 13) && !this.content
          this.$emit('emailExamine', this.content, flag)
          return
        }
        this.submitInfo()
      }
    },

    /**
     * 关闭
     */
    hiddenView() {
      this.$emit('close')
    },

    /**
     * 重置信息
     */
    resetInfo() {
      this.content = ''
    }
  }
}
</script>
<style lang="scss" scoped>
.el-dialog__wrapper /deep/ .el-dialog__body {
  padding: 10px 25px 20px;
}

.title {
  padding-bottom: 8px;
  font-size: 12px;
  color: $--color-text-regular;
}
</style>
