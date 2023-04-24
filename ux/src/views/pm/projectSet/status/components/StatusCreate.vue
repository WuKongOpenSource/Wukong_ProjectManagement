<template>
  <div class="">
    <el-dialog
      v-if="dialogVisible"
      v-loading="loading"
      :append-to-body="true"
      :close-on-click-modal="false"
      :visible.sync="dialogVisible"
      :title="title"
      width="480px"
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
            <template slot="label">
              {{ item.label }}
              <template v-if="item.type == 'select'">
                <el-tooltip
                  effect="dark"
                  placement="top">
                  <div slot="content">
                    <div>状态类型是事项的状态所处的大阶段。</div>
                    <div>所有的事项都会经历未开始、进行中和</div>
                    <div>已完成三个大阶段。</div>
                  </div>
                  <i
                    data-type="4"
                    data-id="9"
                    class="wk wk-icon-fill-help wk-help-tips" />
                </el-tooltip>
              </template>
            </template>

            <el-input
              v-if="item.type == 'txt'"
              v-model="ruleForm[item.field]"
              :placeholder="item.placeholder" />
            <el-select
              v-else-if="item.type == 'select'"
              v-model="ruleForm[item.field]"
              style="width: 100%;"
              :placeholder="item.placeholder">
              <el-option
                v-for="items in options"
                :key="items.key"
                :label="items.label"
                :value="items.key" />
            </el-select>
            <el-input
              v-else-if="item.type == 'textarea'"
              v-model="ruleForm[item.field]"
              :placeholder="item.placeholder"
              type="textarea" />
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
  projectStatusAddAPI,
  projectStatusUpdateAPI
} from '@/api/pm/setting'

export default {
  name: 'CreateItem',
  components: {},
  props: {
    visible: {
      type: Boolean,
      required: true,
      default: false
    },
    action: {
      type: Object,
      default() {
        return {
          type: 'save',
          data: null,
          id: ''
        }
      }
    }
  },
  data() {
    return {
      loading: false,
      dialogVisible: false,
      fieldList: [
        { type: 'txt', label: '状态名称', field: 'statusName', placeholder: '请输入状态名称', width: '100%' },
        { type: 'select', label: '状态类型', field: 'statusType', placeholder: '请选择', width: '100%' },
        { type: 'textarea', label: '描述', field: 'description', placeholder: '对该状态的简要描述', width: '100%' }
      ],
      ruleForm: {
        statusName: '',
        description: '',
        statusType: ''
      },
      rules: {
        statusName: [{ required: true, message: '请输入状态名称', trigger: 'blur' }],
        statusType: [{ required: true, message: '请输入状态名称', trigger: 'change' }]
      },
      options: [
        { label: '未开始', key: 1 },
        { label: '进行中', key: 2 },
        { label: '已完成', key: 3 }
      ]
    }
  },
  computed: {
    title() {
      return `${this.action.type == 'save' ? '新建' : '编辑'}事项状态`
    }
  },
  watch: {
    visible: {
      handler(val) {
        this.initData()
        this.dialogVisible = val
      },
      immediate: true
    }
  },
  created() {

  },
  methods: {
    initData() {
      if (this.action.type == 'update') {
        const data = this.action.data || {}
        this.ruleForm = {
          statusName: data.statusName,
          description: data.description,
          statusType: data.statusType
        }
      } else {
        this.ruleForm = {
          statusName: '',
          description: '',
          statusType: ''
        }
      }
    },
    /**
     * 关闭
     */
    handleClose() {
      this.dialogVisible = false
      this.$emit('update:visible', false)
      this.$emit('close')
    },

    handleConfirm() {
      this.$refs.ruleForm.validate(valid => {
        if (valid) {
          this.loading = true
          const params = {
            ...this.ruleForm
          }
          if (this.action.type == 'update') {
            params.projectStatusId = this.action.id
          }
          const request = {
            save: projectStatusAddAPI,
            update: projectStatusUpdateAPI
          }[this.action.type]
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
        line-height: normal;
      }
    }
  }
}

.btn-wrap {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  margin-top: 20px;
}
</style>
