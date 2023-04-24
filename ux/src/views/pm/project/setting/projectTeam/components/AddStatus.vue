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
            <el-select
              v-model="ruleForm[item.field]"
              style="width: 100%;"
              :placeholder="item.placeholder">
              <el-option
                v-for="items in options"
                :key="items.projectStatusId"
                :label="items.statusName"
                :value="items.projectStatusId">
                <status-tag
                  :type="items.statusType"
                  :status-name="items.statusName" />
              </el-option>
            </el-select>
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
  projectStatusListAPI,
  projectEventStatusAddAPI
} from '@/api/pm/setting'

import StatusTag from '@/views/pm/project/components/StatusTag'

export default {
  name: 'CreateItem',
  components: {
    StatusTag
  },
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
        { type: 'select', label: '状态', field: 'projectStatusId', placeholder: '请选择状态', width: '100%' }
      ],
      ruleForm: {
        projectStatusId: ''
      },
      rules: {
        projectStatusId: [{ required: true, message: '请选择状态', trigger: 'change' }]
      },
      options: []
    }
  },
  computed: {
    title() {
      return `${this.action.type == 'save' ? '新建' : '编辑'}状态`
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
    this.getOptions()
  },
  methods: {
    initData() {
      if (this.action.type == 'update') {
        const data = this.action.data || {}
        this.ruleForm = {
          projectStatusId: data.projectStatusId
        }
      } else {
        this.ruleForm = {
          projectStatusId: ''
        }
      }
    },
    getOptions() {
      const params = {
        projectId: this.$route.params.setting,
        pageType: 0,
        search: ''
      }
      projectStatusListAPI(params).then(res => {
        this.options = (res.data.list || []).map(item => {
          item.label = item.statusName
          item.value = item.projectStatusId
          return item
        })
      }).catch(() => {

      })
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
            projectId: this.$route.params.setting,
            projectEventId: this.action.id,
            ...this.ruleForm
          }
          if (this.action.type == 'update') {
            params.id = this.action.id
          }
          const request = {
            save: projectEventStatusAddAPI,
            update: 'a'
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
