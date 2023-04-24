<template>
  <div class="">
    <el-dialog
      v-if="dialogVisible"
      ref="wkDialog"
      :append-to-body="true"
      :close-on-click-modal="false"
      :visible.sync="dialogVisible"
      :title="title"
      width="800px"
      custom-class="no-padding-dialog"
      @close="handleClose">
      <div class="main">
        <el-form
          ref="ruleForm"
          :model="ruleForm"
          :rules="rules"
          label-width="100px"
          class="demo-ruleForm">
          <el-form-item
            v-for="(item, index) in fieldList"
            :key="index"
            :label="item.label"
            :style="{width: item.width}"
            :prop="item.field">
            <el-input
              v-if="item.type == 'txt'"
              v-model="ruleForm[item.field]"
              :placeholder="item.placeholder" />
            <wk-user-select
              v-else-if="item.type == 'user'"
              v-model="ruleForm[item.field]"
              :props="userSelectProps"
              radio
              style="width: 100%;"
            />
            <el-date-picker
              v-else-if="item.type == 'date'"
              v-model="ruleForm[item.field]"
              style="width: 100%;"
              type="date"
              value-format="yyyy-MM-dd HH:mm:ss"
              :placeholder="item.placeholder" />
            <el-input
              v-else-if="item.type == 'textarea'"
              v-model="ruleForm[item.field]"
              type="textarea"
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
import { workSaveProjectItemAPI, workUpdateProjectItemAPI } from '@/api/pm/projectTask'
import { queryProjectUserListAPI } from '@/api/pm/manage'

import WkUserSelect from '@/components/NewCom/WkUserSelect'

import ElDialogLoadingMixin from '@/mixins/ElDialogLoading'
import { mapGetters } from 'vuex'

export default {
  name: 'CreateIteration',
  components: {
    WkUserSelect
  },
  mixins: [ElDialogLoadingMixin],
  props: {
    visible: {
      type: Boolean,
      required: true,
      default: false
    },
    itemType: String,
    type: String,
    detailData: {
      type: Object,
      default() {
        return {}
      }
    }
  },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      fieldList: [
        { type: 'txt', label: '迭代名称', field: 'name', placeholder: '请输入迭代名称', width: '100%' },
        { type: 'date', label: '开始时间', field: 'startTime', placeholder: '请选择开始时间', width: '48%' },
        { type: 'date', label: '结束时间', field: 'stopTime', placeholder: '请选择结束时间', width: '48%' },
        { type: 'textarea', label: '迭代目标', field: 'target', placeholder: '请输入迭代目标', width: '100%' },
        { type: 'user', label: '负责人', field: 'mainUserId', width: '48%' }
      ],
      ruleForm: {
        name: '',
        mainUserId: '',
        startTime: '',
        stopTime: '',
        target: ''
      },
      rules: {
        name: [{ required: true, message: '请输入迭代名称', trigger: 'change' }]
      }
    }
  },
  computed: {
    ...mapGetters(['userInfo']),

    title() {
      if (this.type == 'edit') {
        return '编辑迭代'
      }
      return '新建迭代'
    },

    userSelectProps() {
      return {
        request: queryProjectUserListAPI,
        params: {
          projectId: this.$route.params.id
        },
        isList: true
      }
    }
  },
  watch: {
    visible: {
      handler(val) {
        if (val) {
          this.ruleForm = {
            name: this.detailData.name,
            mainUserId: this.detailData.mainUserId,
            startTime: this.detailData.startTime,
            stopTime: this.detailData.stopTime,
            target: this.detailData.target
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
            companyId: this.userInfo.companyId,
            type: 1,
            projectId: this.$route.params.id,
            ...this.ruleForm
          }
          if (this.type == 'edit') {
            params.taskId = this.detailData.taskId
          }

          const request = this.type == 'edit'
            ? workUpdateProjectItemAPI
            : workSaveProjectItemAPI

          request(params).then(res => {
            this.$emit('save-success', { type: this.itemType })
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
