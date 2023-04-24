<template>
  <xr-create
    :loading="loading"
    :title="title"
    @close="close"
    @save="saveClick">
    <create-sections title="基本信息">
      <wk-form
        ref="crmForm"
        :model="fieldForm"
        :rules="fieldRules"
        :field-from="fieldForm"
        :field-list="fieldList"
        label-position="top"
        @change="formChange"
      >
        <template slot-scope="{ data }">
          <div v-if="data && data.formType == 'tag'" class="label">
            <span
              v-for="(item, index) in fieldForm[data.field]"
              :key="index"
              :style="[{'background': item.color ? colorRgb(item.color,0.1) : '#ccc'},{'color': item.color ? item.color : '#ccc'}]"
              class="item-color">
              {{ item.labelName || item.name }}
            </span>
            <div class="add-tag">
              <tag-index
                :placement="'right'"
                @change="otherChange($event, data)">
                <span
                  slot="editIndex"
                  class="add-btn">
                  <i class="wk wk-l-plus" />
                  <span class="label">标签</span>
                </span>
              </tag-index>
            </div>
          </div>

          <template v-if="data && data.formType == 'subTask'">
            <div
              v-if="fieldForm[data.field]"
              :class="{'sub-task-all':fieldForm[data.field] && fieldForm[data.field].length > 0}">
              <div
                v-for="(item, index) in fieldForm[data.field]"
                :key="index"
                class="sub-task">
                <flexbox style="padding: 14px;">
                  <div @click.stop>
                    <el-checkbox
                      v-model="item.checked" />
                  </div>
                  <div
                    :class="{ 'is-checked' : item.checked }"
                    class="sub-task__bd text-one-line">
                    {{ item.name }}
                  </div>

                  <div class="edit-del-box">
                    <el-dropdown
                      :hide-on-click="false"
                      @command="subTaskDelete(item,index, data)">
                      <i class="el-icon-more more" style="cursor: pointer;" />
                      <el-dropdown-menu slot="dropdown">
                        <el-dropdown-item>
                          <span type="text">删除</span>
                        </el-dropdown-item>
                      </el-dropdown-menu>
                    </el-dropdown>
                  </div>
                  <div
                    v-if="item.stopTime"
                    style="margin-left: 8px;"
                    class="bg-color task-bg-color">{{ item.stopTime | moment("YYYY-MM-DD") }} 截止</div>
                  <xr-avatar
                    v-if="item.mainUser"
                    :name="item.mainUser.realname"
                    :size="25"
                    :src="item.mainUser.img"
                    class="user-img" />

                </flexbox>
              </div>
            </div>
            <div v-if="addSubtasks">
              <span
                class="add-btn"
                @click="addSubtasks = false">
                <i class="wk wk-l-plus" />
                <span class="label">子任务</span>
              </span>
            </div>
            <sub-task
              v-else
              :sub-task-com="'new'"
              :check-disabled="false"
              @add="subTaskAdd($event, data)"
              @close="addSubtasks = true" />
          </template>

          <div v-if="data && data.formType == 'taskFile'">
            <file-cell
              :file-list="fieldForm[data.field]"
              :show-time="false"
              show-delete
              @delete="fileDelete(arguments[0],arguments[1], data)" />
            <el-upload
              :http-request="httpRequest"
              class="upload-file"
              action="https://jsonplaceholder.typicode.com/posts/"
              multiple
              list-type="picture">
              <span class="add-btn">
                <i class="wk wk-l-plus" />
                <span class="label">附件</span>
              </span>
            </el-upload>
          </div>
        </template>
      </wk-form>
    </create-sections>
  </xr-create>
</template>

<script>
import { setTaskAPI } from '@/api/oa/task'

import XrCreate from '@/components/XrCreate'
import CreateSections from '@/components/CreateSections'
import WkForm from '@/components/NewCom/WkForm'
import TagIndex from './components/Tag/TagIndex'
import SubTask from './components/SubTask'
import FileCell from '@/components/FileCell'

import TaskMixin from './mixins/TaskMixin'
import CustomFieldsMixin from '@/mixins/CustomFields'
import { guid, convertHexByOpacity } from '@/utils'
import { mapGetters } from 'vuex'
import moment from 'moment'

export default {
  // 新建编辑
  name: 'TaskCreate',

  components: {
    XrCreate,
    CreateSections,
    WkForm,
    TagIndex,
    SubTask,
    FileCell
  },
  mixins: [CustomFieldsMixin, TaskMixin],
  props: {
    action: {
      type: Object,
      default: () => {
        return {
          type: 'save',
          id: '',
          data: {}
        }
      }
    }
  },

  data() {
    return {
      loading: false,
      fieldList: [],
      fieldForm: {},
      fieldRules: {
        name: { required: true, message: '任务名称不能为空', trigger: ['blur', 'change'] }
      },
      addSubtasks: true,
      batchId: guid(), // 批次ID
    }
  },

  computed: {
    ...mapGetters([
      'userInfo'
    ]),
    title() {
      return this.action.type === 'update' ? '编辑任务' : '新建任务'
    }
  },

  watch: {},

  created() {
    const fieldForm = {
      priority: 0,
      mainUserId: this.userInfo.userId,
      ownerUserId: [this.userInfo.userId]
    }

    if (this.action.data) {
      const actionData = this.action.data
      if (actionData.name) {
        fieldForm.name = actionData.name
      }
      fieldForm.stopTime = actionData.stopTime || ''
      if (actionData.mainUserId) {
        fieldForm.mainUserId = actionData.mainUserId
      }
    }

    this.fieldForm = fieldForm

    this.getField()
  },

  mounted() {
  },

  beforeDestroy() {},

  methods: {
    /**
     * 获取数据
     */
    getField() {
      const list = [{
        name: '任务名称',
        field: 'name',
        formType: 'text',
        setting: []
      }, {
        name: '描述',
        field: 'description',
        formType: 'textarea',
        setting: []
      }, {
        name: '优先级',
        field: 'priority',
        formType: 'select',
        setting: this.getPrioritySetting()
      }, {
        name: '负责人',
        field: 'mainUserId',
        radio: true,
        formType: 'user',
        setting: []
      }, {
        name: '开始时间',
        field: 'startTime',
        formType: 'date',
        setting: []
      }, {
        name: '结束时间',
        field: 'stopTime',
        formType: 'date',
        setting: []
      }, {
        name: '参与人',
        field: 'ownerUserId',
        radio: false,
        formType: 'user',
        setting: []
      }, {
        name: '标签',
        field: 'labelId',
        formType: 'tag',
        setting: []
      }, {
        name: '子任务',
        field: 'taskInfoList',
        formType: 'subTask',
        setting: []
      }, {
        name: '附件',
        field: 'files',
        formType: 'taskFile',
        setting: []
      }]

      this.fieldList = list
    },

    /**
     * 获取优先级数据
     */
    getPrioritySetting() {
      return this.priorityList.map(item => {
        return { label: item.label, value: item.id }
      })
    },

    /**
     * 保存
     */
    saveClick() {
      this.loading = true
      const crmForm = this.$refs.crmForm.instance
      crmForm.validate(valid => {
        if (valid) {
          this.submiteParams()
        } else {
          this.loading = false
          // 提示第一个error
          this.getFormErrorMessage(crmForm)
          return false
        }
      })
    },

    /**
     * 提交上传
     */
    submiteParams() {
      let params = {
        batchId: this.batchId
      }
      if (this.action.params) {
        params = { ...params, ...this.action.params }
      }

      this.fieldList.forEach(item => {
        if (item.formType === 'user' && item.radio == false) {
          const value = this.fieldForm[item.field] ? this.fieldForm[item.field] : []
          params[item.field] = value.join(',')
        } else if (item.formType === 'tag') {
          const value = this.fieldForm[item.field] ? this.fieldForm[item.field] : []
          params[item.field] = value.map(label => label.labelId).join(',')
        } else if (item.formType === 'subTask') {
          const valueList = this.fieldForm[item.field] || []
          params[item.field] = valueList.map(item => {
            return {
              status: item.checked ? 5 : 1,
              mainUserId: item.mainUser ? item.mainUser.userId : '',
              name: item.name,
              stopTime: item.stopTime
            }
          })
        } else if (item.formType !== 'taskFile') {
          params[item.field] = this.fieldForm[item.field]
        }
      })

      if (params.startTime && params.stopTime && moment(params.stopTime).isBefore(moment(params.startTime))) {
        this.$message.error('开始时间必须小于结束时间')
        this.loading = false
        return
      }

      setTaskAPI(params)
        .then(res => {
          this.loading = false

          this.$message.success(
            this.action.type == 'update' ? '编辑成功' : '添加成功'
          )

          this.close()

          // 保存成功
          this.$emit('save')
        })
        .catch(() => {
          this.loading = false
        })
    },

    /**
     * change
     */
    formChange(item, index, value, valueList) {},

    /**
     * 地址change
     */
    otherChange(data, field) {
      this.$set(this.fieldForm, field.field, data.value || data)
      this.$refs.crmForm.instance.validateField(field.field)
    },

    /**
     * 删除子任务
     */
    subTaskDelete(val, index, field) {
      this.fieldForm[field.field].splice(index, 1)
    },

    /**
     * 子任务添加
     */
    subTaskAdd(task, field) {
      const list = this.fieldForm[field.field] || []
      list.push(task)
      this.$set(this.fieldForm, field.field, list)
    },

    /**
     * 附件
     */
    httpRequest(val) {
      this.$wkUploadFile.upload({
        file: val.file,
        params: {
          batchId: this.batchId
        }
      }).then(({ res }) => {
        const data = res.data || {}
        const list = this.fieldForm['files'] || []
        list.push(data)
        this.$set(this.fieldForm, 'files', list)
        this.$message.success('上传成功')
      }).catch(() => {})
    },

    /**
     * 附件删除
     */
    fileDelete(index, item, field) {
      this.fieldForm['files'].splice(index, 1)
    },

    /**
     * 关闭
     */
    close() {
      this.$emit('close')
    },
    /**
     * 转换颜色成rgb格式
     */
    colorRgb(hexCode, opacity) {
      return convertHexByOpacity(hexCode, opacity)
    }
  }
}
</script>

<style lang="scss" scoped>
@import "./components/taskDetail";

.wk-form {
  /deep/ .el-form-item {
    &.is-tag,
    &.is-subTask,
    &.is-taskFile {
      .el-form-item__content {
        line-height: normal;
      }
    }
  }
}

.related-business {
  margin: 0;
}
</style>
