<template>
  <div class="">
    <el-dialog
      v-if="dialogVisible"
      ref="wkDialog"
      :append-to-body="true"
      :close-on-click-modal="false"
      :visible.sync="dialogVisible"
      title="添加工时"
      width="800px"
      custom-class="no-padding-dialog"
      @close="handleClose">
      <div class="main">
        <el-form
          ref="ruleForm"
          :model="ruleForm"
          :rules="rules"
          label-width="150px"
          class="demo-ruleForm">
          <el-form-item
            v-for="(item, index) in fieldList"
            :key="index"
            :label="item.label"
            :style="{width: item.width}"
            :prop="item.field">
            <el-autocomplete
              v-if="item.type == 'item'"
              v-model="itemName"
              :disabled="item.disable"
              style="width: 100%;"
              :fetch-suggestions="querySearchAsync"
              :placeholder="item.placeholder"
              @select="handleSelect">
              <template slot-scope="{ item: searchItem }">
                <div class="name">{{ searchItem.name }}</div>
              </template>
            </el-autocomplete>
            <wk-user-select
              v-else-if="item.type == 'user'"
              v-model="ruleForm[item.field]"
              :disabled="item.disable"
              :props="userSelectProps"
              radio
              style="width: 100%;" />
            <el-date-picker
              v-else-if="item.type == 'date'"
              v-model="ruleForm[item.field]"
              style="width: 100%;"
              type="date"
              value-format="yyyy-MM-dd HH:mm"
              :placeholder="item.placeholder" />
            <el-input
              v-else-if="item.type == 'textarea'"
              v-model="ruleForm[item.field]"
              type="textarea"
              :placeholder="item.placeholder" />
            <el-input
              v-else-if="item.type == 'time'"
              v-model="ruleForm[item.field]"
              :placeholder="item.placeholder"
              class="input-with-select"
              @change="handleChange($event, item.field)">
              <el-button slot="append" type="text">小时</el-button>
            </el-input>
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
import { addWorkTimeAPI, workQueryItemListAPI } from '@/api/pm/projectTask'
import { queryProjectUserListAPI } from '@/api/pm/manage'

import WkUserSelect from '@/components/NewCom/WkUserSelect'

import ElDialogLoadingMixin from '@/mixins/ElDialogLoading'
import { mapGetters } from 'vuex'
// import moment from 'moment'

export default {
  name: 'CreateWorkTime',
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
    taskData: {
      type: Object,
      default() {
        return {}
      }
    },
    itemType: String,
    restHours: [String, Number]
  },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      fieldList: [
        // { type: 'item', label: '事项名称', field: 'taskId', placeholder: '请选择事项名称', width: '48%', disable: false },
        // { type: 'user', label: '项目成员', field: 'projectMember', placeholder: '请输入', width: '48%', disable: false },
        // { type: 'date', label: '开始时间', field: 'beginTime', placeholder: '请选择开始时间', width: '48%' },
        // { type: 'date', label: '结束时间', field: 'endTime', placeholder: '请选择结束时间', width: '48%' },
        // { type: 'time', label: '预估工时', field: 'estimatedManHours', placeholder: '请输入迭代目标', width: '48%' },
        { type: 'time', label: '实际投入时长', field: 'actualHour', placeholder: '0', width: '48%' },
        { type: 'time', label: '剩余工时', field: 'surplusHours', placeholder: '0', width: '48%' }
        // { type: 'textarea', label: '描述', field: 'description', placeholder: '请输入描述', width: '100%' }
      ],
      ruleForm: {
        taskId: '',
        // beginTime: '',
        // endTime: '',
        // estimatedManHours: '',
        actualHour: '',
        surplusHours: ''
        // description: ''
      },
      rules: {
        // taskId: [{ required: true, message: '请选择事项名称', trigger: 'change' }],
        // beginTime: [{ required: true, message: '请选择开始时间', trigger: 'change' }],
        // endTime: [{ required: true, message: '请选择结束时间', trigger: 'change' }],
        actualHour: [{ required: true, message: '请输入实际投入时长', trigger: 'blur' }]
      },

      selectedUserIds: [],
      selectUserList: [],

      currentItem: '',
      itemName: ''
    }
  },
  computed: {
    ...mapGetters(['userInfo']),
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
          if (this.taskData.taskId) {
            this.itemName = this.taskData.name
            this.ruleForm.taskId = this.taskData.taskId
            this.fieldList[1].placeholder = this.restHours

            // this.ruleForm.beginTime = ''
            this.ruleForm.actualHour = ''
            this.ruleForm.surplusHours = ''
            // this.ruleForm.description = ''
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

    handleChange(data, field) {
      if (field == 'actualHour') {
        if (!this.restHours || data >= this.restHours) {
          this.fieldList[1].placeholder = 0
        } else {
          this.fieldList[1].placeholder = this.restHours - data
        }
      }
    },

    querySearchAsync(queryString, cb) {
      if (!queryString) {
        cb([])
        return
      }
      const params = {
        name: queryString,
        projectId: this.$route.params.id,
        type: '',
        pageType: 0
      }
      workQueryItemListAPI(params).then(res => {
        const list = res.data.list
        var results = queryString ? list.filter(this.createStateFilter(queryString)) : list
        cb(results)
      }).catch(() => {
        cb([])
      })
    },

    createStateFilter(queryString) {
      return (state) => {
        return (state.name.toLowerCase().indexOf(queryString.toLowerCase()) === 0)
      }
    },

    handleSelect(item) {
      this.currentItem = item
      this.itemName = item.name
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
            // projectId: this.$route.params.id,
            ...this.ruleForm
          }

          params.surplusHours = params.surplusHours || this.fieldList[1].placeholder

          // params.endTime = moment(params.beginTime).add(params.actualHour, 'hours').format('YYYY-MM-DD HH:mm')

          addWorkTimeAPI(params).then(res => {
            this.$message.success('操作成功')
            this.$emit('save-success')
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

.input-with-select /deep/ .el-input-group__append {
  padding: 0 40px;
}

.add-user-btn {
  width: 26px;
  height: 26px;
  font-size: $--font-size-small;
  cursor: pointer;
  border: 1px dotted $--color-text-regular;
  border-radius: 50%;

  &:hover {
    border-color: $--color-primary;
  }
}

.xr-avatar,
.add-user-btn {
  margin: 0 5px;
}
</style>
