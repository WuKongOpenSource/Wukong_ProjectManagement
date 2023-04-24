<template>
  <div class="">
    <el-dialog
      v-if="dialogVisible"
      ref="wkDialog"
      :append-to-body="true"
      :close-on-click-modal="false"
      :visible.sync="dialogVisible"
      :title="title"
      width="400px"
      custom-class="no-padding-dialog"
      @close="handleClose">
      <div class="main">
        <el-form
          ref="ruleForm"
          :model="ruleForm"
          :rules="rules"
          label-width="130px"
          class="demo-ruleForm">
          <el-form-item
            v-for="(item, index) in fieldList"
            :key="index"
            :label="item.label"
            :style="{width: item.width}"
            :prop="item.field">
            <el-date-picker
              v-if="item.type == 'date'"
              v-model="ruleForm[item.field]"
              style="width: 100%;"
              type="date"
              value-format="yyyy-MM-dd HH:mm:ss"
              :placeholder="item.placeholder" />
          </el-form-item>

        </el-form>
        <div slot="footer" class="btn-wrap">
          <el-button
            v-debounce="handleConfirm"
            type="primary">保存</el-button>
          <el-button @click.native="handleClose">取消</el-button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  startIterationAPI,
  finishIterationAPI
} from '@/api/pm/projectTask'

import ElDialogLoadingMixin from '@/mixins/ElDialogLoading'
import { mapGetters } from 'vuex'

export default {
  name: 'CreateIteration',
  components: {},
  mixins: [ElDialogLoadingMixin],
  props: {
    visible: {
      type: Boolean,
      required: true,
      default: false
    },
    detailData: {
      type: Object,
      default() {
        return {}
      }
    },
    flowType: [String, Number]
  },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      fieldList: [
        { type: 'date', label: '迭代开始时间', field: 'startTime', placeholder: '请选择迭代开始时间', width: '100%' },
        { type: 'date', label: '迭代结束时间', field: 'stopTime', placeholder: '请选择迭代结束时间', width: '100%' }
      ],
      ruleForm: {
        startTime: '',
        stopTime: ''
      },
      rules: {
        startTime: [{ required: true, message: '请选择迭代开始时间', trigger: 'change' }],
        stopTime: [{ required: true, message: '请选择迭代结束时间', trigger: 'change' }]
      }
    }
  },
  computed: {
    ...mapGetters(['userInfo']),
    title() {
      return {
        start: '开始迭代',
        finish: '结束迭代'
      }[this.flowType]
    }

  },
  watch: {
    visible: {
      handler(val) {
        if (val) {
          this.ruleForm = {
            startTime: this.detailData.startTime,
            stopTime: this.detailData.stopTime
          }
        }
        this.dialogVisible = val
      },
      immediate: true
    }
  },
  methods: {
    /**
     * @description: 关闭
     * @return {*}
     */
    handleClose() {
      this.dialogVisible = false
      this.$emit('update:visible', false)
      this.$emit('close')
    },

    /**
     * @description: 保存
     * @return {*}
     */
    handleConfirm() {
      this.$refs.ruleForm.validate(valid => {
        if (valid) {
          this.loading = true

          const params = {
            ...this.ruleForm,
            taskId: this.detailData.taskId
          }

          const request = {
            start: startIterationAPI,
            finish: finishIterationAPI
          }[this.flowType]

          request(params).then(res => {
            this.$message.success('迭代成功')
            this.$emit('save-success', { type: 'flow' })
            this.loading = false
          }).catch(() => {
            this.loading = false
          })
        }
      })
    }
  }
}
</script>
<style lang='scss' scoped>
.main {
  padding: 0 20px 20px;

  /deep/ .el-form {
    display: flex;
    flex-wrap: wrap;
    align-items: flex-start;
    justify-content: space-between;

    .el-form-item {
      display: flex;
      flex-direction: column;
      align-items: flex-start;
      justify-content: flex-start;
      margin-bottom: 10px;

      .el-form-item__label {
        line-height: 30px;
        text-align: left !important;
      }

      .el-form-item__content {
        width: 100%;
        margin-left: 0 !important;
      }
    }
  }
}

.start {
  width: calc(50% - 10px);
  margin-right: 20px;
}

.end {
  width: calc(50% - 10px);
}

.btn-wrap {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  margin-top: 20px;
}
</style>
