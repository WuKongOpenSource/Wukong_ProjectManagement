<template>
  <xr-create
    :loading="loading"
    :title="title"
    @close="handleClose"
    @save="saveClick">
    <create-sections title="基本信息">
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
            v-if="item.type == 'text'"
            v-model="ruleForm[item.field]"
            type="textarea"
            maxlength="100"
            show-word-limit
            :placeholder="item.placeholder" />
          <el-input
            v-else-if="item.type == 'number'"
            v-model="ruleForm[item.field]"
            type="number"
            maxlength="100"
            :placeholder="item.placeholder" />
          <wk-user-select
            v-else-if="item.type == 'user'"
            v-model="ruleForm[item.field]"
            :props="userSelectProps"
            radio
            style="width: 100%;"
          />
          <wk-user-select
            v-else-if="item.type == 'users'"
            v-model="ruleForm[item.field]"
            :props="userSelectProps"
            style="width: 100%;"
          />
          <el-date-picker
            v-else-if="item.type == 'date'"
            v-model="ruleForm[item.field]"
            style="width: 100%;"
            value-format="yyyy-MM-dd HH:mm:ss"
            type="date"
            :placeholder="item.placeholder" />
          <tinymce
            v-else-if="item.type === 'textarea'"
            ref="editor"
            v-model="ruleForm[item.field]"
            :height="200"
            :init="{
              menubar: false,
              toolbar_sticky: true,
              statusbar: false,
              contextmenu: '',
              content_style: 'p { margin: 5px 0; line-height: 1.5;}',
              plugins: 'paste',
              toolbar: 'paste',
              paste_data_images: true
            }"
            class="rich-txt" />
          <!-- 关联需求 -->
          <el-select
            v-else-if="item.type == 'item'"
            v-model="ruleForm[item.field]"
            style="width: 100%;"
            filterable
            remote
            clearable
            :remote-method="getItemData"
            :loading="itemLoading"
            @focus="getItemData('')">
            <el-option
              v-for="task in options[item.field]"
              :key="task.taskId"
              :label="task.name"
              :value="task.taskId" />
          </el-select>
          <el-select
            v-else-if="item.type == 'select'"
            v-model="ruleForm[item.field]"
            style="width: 100%;"
            clearable
            :placeholder="item.placeholder">
            <el-option
              v-for="items in options[item.field]"
              :key="items.key"
              :label="items.label"
              :value="items.key">
              <template v-if="item.field === 'boardStatusId'">
                <status-tag
                  :type="items.statusType"
                  :status-name="items.statusName" />
              </template>
            </el-option>
          </el-select>
          <!-- 附件 -->
          <div v-else-if="item.type == 'file'" class="file">
            <file-cell
              :file-list="ruleForm[item.field]"
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
          <!-- 子工作项 -->
          <template v-else-if="item.type == 'subItem'">
            <div class="sub-task-all">
              <div
                v-for="(sitem, sindex) in ruleForm[item.field]"
                :key="sindex"
                class="sub-task">
                <flexbox v-if="!sitem.showEdit" style="padding: 14px;">
                  <div @click.stop>
                    <el-checkbox
                      v-model="sitem.checked" />
                  </div>
                  <img class="item-img" src="@/assets/img/pm/child.png" alt="">
                  <div
                    :class="{ 'is-checked' : sitem.checked }"
                    class="sub-task__bd text-one-line">
                    {{ sitem.name }}
                  </div>
                  <div class="edit-del-dropdown">
                    <el-dropdown
                      :hide-on-click="false"
                      @command="handleSubItem($event, sindex)">
                      <span class="el-dropdown-link">
                        <i class="el-icon-more" style="cursor: pointer;" />
                      </span>
                      <el-dropdown-menu slot="dropdown">
                        <el-dropdown-item :command="{type:'update',data:item}">
                          <span>编辑</span>
                        </el-dropdown-item>
                        <el-dropdown-item :command="{type:'delete',data:item}">
                          <span>删除</span>
                        </el-dropdown-item>
                      </el-dropdown-menu>
                    </el-dropdown>
                  </div>
                </flexbox>
                <!-- 编辑时 -->
                <handle-sub-item
                  v-else
                  :name="sitem.name"
                  type="update"
                  @update="addOrUpdateSubItem($event, sindex, 'update')" />
              </div>
            </div>
            <!-- 添加时 -->
            <handle-sub-item
              v-if="addSubtasks"
              type="add"
              @add="addOrUpdateSubItem" />
            <span
              v-if="!addSubtasks"
              class="add-btn"
              @click="addSubtasks = true">
              <i class="wk wk-l-plus" />
              <span class="label">子工作项</span>
            </span>
          </template>
        </el-form-item>

      </el-form>
    </create-sections>
  </xr-create>
</template>

<script>
import { workSaveProjectItemAPI, workQueryIterationsItemListAPI, workQueryItemListAPI } from '@/api/pm/projectTask'
import { queryProjectUserListAPI } from '@/api/pm/manage'
import { getStatusListAPI } from '@/api/pm/setting'

import XrCreate from '@/components/XrCreate'
import CreateSections from '@/components/CreateSections'
import Tinymce from '@/components/Tinymce'
import WkUserSelect from '@/components/NewCom/WkUserSelect'
import FileCell from '@/components/FileCell'
import StatusTag from '@/views/pm/project/components/StatusTag'
import HandleSubItem from './/components/HandleSubItem'

import { itemTypeMap, priorityList } from '@/views/pm/data'
import { guid } from '@/utils'

import { debounce } from 'throttle-debounce'
import { mapGetters } from 'vuex'

export default {
  // 新建编辑
  name: 'CreateItem',

  components: {
    XrCreate,
    CreateSections,
    Tinymce,
    FileCell,
    WkUserSelect,
    StatusTag,
    HandleSubItem
  },
  props: {
    visible: {
      type: Boolean,
      required: true,
      default: false
    },
    itemType: String,
    isAgility: Boolean,
    iterationId: [String, Number]
  },

  data() {
    return {
      loading: false,
      dialogVisible: false,
      fieldList: [],
      itemLoading: false,
      ruleForm: {},
      rules: {
        name: [{ required: true, message: '请输入事项标题', trigger: 'change' }],
        priority: [{ required: true, message: '请选择优先级', trigger: 'change' }]
      },
      options: {
        // 优先级
        priority: priorityList,
        belongIterationId: [],
        boardStatusId: [], // 状态列表
        relatedDemandId: [],
        wrongType: [
          { label: '功能缺陷', key: 1 },
          { label: 'UI界面问题', key: 2 },
          { label: '易用性问题', key: 3 },
          { label: '安全问题', key: 4 },
          { label: '性能问题', key: 5 },
          { label: '代码错误', key: 6 }
        ]
      },
      batchId: guid(), // 批次ID
      addSubtasks: false,
      childSubList: []
    }
  },

  computed: {
    ...mapGetters(['userInfo']),
    title() {
      const label = {
        Require: '需求',
        Task: '任务',
        Defects: '缺陷'
      }[this.itemType]

      return `新建${label}`
    },

    eventId() {
      const eventId = {
        Require: 1,
        Task: 2,
        Defects: 3
      }[this.itemType]
      return eventId
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
        this.dialogVisible = val
        if (val) {
          this.getField()
          this.getIterationOption()
          this.getAllStatus()
        }
      },
      immediate: true
    }
  },

  created() {
    this.debouncedSaveField = debounce(300, this.saveClick)
  },

  mounted() {},

  beforeDestroy() {},

  methods: {
    /**
     * @description: 获取新建字段
     * @return {*}
     */
    getField() {
      const defalut = [
        { type: 'text', label: '事项标题', field: 'name', placeholder: '请输入事项标题', width: '100%' },
        { type: 'textarea', label: '事项描述', field: 'description', placeholder: '请输入事项描述', width: '100%' },
        { type: 'date', label: '开始时间', field: 'startTime', placeholder: '请选择开始时间', width: '50%' },
        { type: 'date', label: '结束时间', field: 'stopTime', placeholder: '请选择结束时间', width: '50%' },
        { type: 'select', label: '优先级', field: 'priority', placeholder: '请选择优先级', width: '50%' },
        { type: 'user', label: '处理人', field: 'mainUserId', width: '50%' },
        { type: 'users', label: '团队成员', field: 'ownerUserId', width: '50%' },
        { type: 'select', label: '状态', field: 'boardStatusId', placeholder: '请选择状态', width: '50%' },
        // 敏捷项目字段
        { type: 'select', label: '所属迭代', field: 'belongIterationId', placeholder: '请选择所属迭代', width: '50%' },
        { type: 'number', label: '进度', field: 'progress', placeholder: '请输入进度', width: '50%' },
        { type: 'number', label: '预估工时', field: 'estimatedManHours', placeholder: '请输入预估工时', width: '50%' },
        { type: 'number', label: '实际投入时长', field: 'actualHour', placeholder: '请输入实际投入时长', width: '50%' },
        { type: 'number', label: '剩余工时', field: 'surplusHours', placeholder: '请输入剩余工时', width: '50%' },
        // 缺陷
        { type: 'item', label: '关联需求', field: 'relatedDemandId', placeholder: '未关联需求', width: '50%' },
        { type: 'select', label: '缺陷类型', field: 'wrongType', placeholder: '请选择缺陷类型', width: '50%' },
        // 敏捷项目字段
        { type: 'subItem', label: '子工作项', field: 'childTaskList', width: '50%' },

        { type: 'file', label: '附件', field: 'file', width: '50%' },
        // { type: 'relatedBusiness', label: '关联业务', field: 'projectTaskRelation', width: '50%' }
      ]

      const result = []
      const filterCondition = item => (
        !this.isAgility &&
        [
          'belongIterationId',
          'progress',
          'estimatedManHours',
          'childTaskList'
        ].includes(item.field)
      ) || (
        this.itemType !== 'Defects' &&
        [
          'relatedDemandId',
          'wrongType'
        ].includes(item.field)
      )

      for (const item of defalut) {
        if (filterCondition(item)) continue
        result.push(item)
        this.$set(this.ruleForm, item.field, this.initData(item))
      }

      this.fieldList = result
    },

    /**
     * @description: 初始化数据
     * @return {*}
     */
    initData(item) {
      if (['ownerUserId', 'file', 'childTaskList', 'projectTaskRelation'].includes(item.field)) {
        return []
      } else if (item.field === 'priority') {
        return '2'
      }

      return ''
    },

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
    saveClick() {
      this.$refs.ruleForm.validate(valid => {
        if (valid) {
          this.loading = true
          const {
            actualHour,
            surplusHours,
            childTaskList = [],
            projectTaskRelation,
            ...rest
          } = this.ruleForm

          const relativeBusiness = {}
          for (const key in projectTaskRelation) {
            if (Object.hasOwnProperty.call(projectTaskRelation, key)) {
              const element = projectTaskRelation[key]
              relativeBusiness[key] = element.join()
            }
          }

          const params = {
            companyId: this.userInfo.companyId,
            type: itemTypeMap[this.itemType],
            projectId: this.$route.params.id,
            ...rest,
            ownerUserId: rest.ownerUserId.join(),
            projectTaskRelation: relativeBusiness,
            childTaskList: childTaskList.map(item => ({ name: item.name })),
            taskTimeList: [{ actualHour, surplusHours }]
          }

          if (this.iterationId) {
            params.belongIterationId = this.iterationId
          }

          workSaveProjectItemAPI(params).then(res => {
            this.$emit('save-success', { type: this.itemType })
            this.loading = false
          }).catch(() => {
            this.loading = false
          })
        }
      })
    },

    /**
     * @description: 获取需求数据
     * @return {*}
     */
    getItemData(query) {
      const params = {
        name: query,
        projectId: this.$route.params.id,
        pageType: 0,
        type: 2
      }
      this.itemLoading = true
      workQueryItemListAPI(params).then(res => {
        this.options.relatedDemandId = (res.data.list || [])
        this.itemLoading = false
      }).catch(() => {
        this.itemLoading = false
      })
    },

    /**
     * @description: 附件删除
     * @param {*} index
     * @param {*} item
     * @param {*} field
     * @return {*}
     */
    fileDelete(index, item, field) {
      this.ruleForm['file'].splice(index, 1)
    },

    /**
     * @description: 附件
     * @param {*} val
     * @return {*}
     */
    httpRequest(val) {
      this.$wkUploadFile.upload({
        file: val.file,
        params: {
          batchId: this.batchId
        }
      }).then(({ res }) => {
        const data = res.data || {}
        const list = this.ruleForm['file'] || []
        list.push(data)
        this.$set(this.ruleForm, 'file', list)
        this.$message.success('上传成功')
      }).catch(() => {})
    },

    /**
     * @description: 编辑或删除设置
     * @param {*} data
     * @param {*} index
     * @param {*} type
     * @return {*}
     */
    addOrUpdateSubItem(data, index, type) {
      if (!data.name) {
        return this.$message.warning('请输入子工作项标题！')
      }

      if (type === 'update') {
        this.ruleForm.childTaskList[index].showEdit = false
        this.ruleForm.childTaskList[index].checked = data.checked
        this.ruleForm.childTaskList[index].name = data.name
      } else {
        this.ruleForm.childTaskList.push(data)
        this.addSubtasks = false
      }
    },

    /**
     * @description: 子工作项编辑与删除
     * @param {*} value
     * @return {*}
     */
    handleSubItem({ type }, index) {
      if (type === 'update') {
        this.$set(this.ruleForm.childTaskList[index], 'showEdit', true)
      } else if (type === 'delete') {
        this.ruleForm.childTaskList.splice(index, 1)
      }
    },

    /**
     * 删除子任务
     */
    subTaskDelete(val, index, field) {
      this.ruleForm[field.field].splice(index, 1)
    },

    /**
     * 子任务添加
     */
    subTaskAdd(task, field) {
      const list = this.ruleForm[field.field] || []
      list.push(task)
      this.$set(this.ruleForm, field.field, list)
    },

    /**
     * @description: 查询事项列表
     * @return {*}
     */
    getIterationOption() {
      const params = {
        type: 0,
        projectId: this.$route.params.id,
        name: '',
        pageType: 0
      }
      workQueryIterationsItemListAPI(params).then(res => {
        const list = (res.data.list || []).map(item => {
          item.label = item.name
          item.key = item.taskId
          return item
        })
        this.$set(this.options, 'belongIterationId', list)
      }).catch(() => {

      })
    },

    /**
     * @description: 获取所有状态
     * @return {*}
     */
    getAllStatus() {
      const params = {
        projectId: this.$route.params.id,
        eventId: {
          Require: 1,
          Task: 2,
          Defects: 3
        }[this.itemType]
      }
      getStatusListAPI(params).then(res => {
        this.allStatusList = res.data || []
        const list = (res.data || []).map(item => {
          item.label = item.statusName
          item.key = item.id
          return item
        })
        this.$set(this.options, 'boardStatusId', list)
      })
    }
  }
}
</script>

<style lang="scss" scoped>
/deep/ .el-form {
  display: flex;
  flex-wrap: wrap;
  align-items: flex-start;
  justify-content: space-between;
  overflow-x: hidden;
  overflow-y: auto;

  .el-form-item {
    display: flex;
    flex-direction: column;
    align-items: flex-start;
    justify-content: flex-start;
    padding: 12px 12px 0;
    margin: 0;

    .el-form-item__label {
      line-height: 30px;
      text-align: left !important;
    }

    .el-form-item__content {
      width: 100%;
      margin-left: 0 !important;
      line-height: normal;
    }

    &:first-child {
      padding-top: 0;
    }
  }
}

.btn-wrap {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  margin-top: 20px;
}

// 添加btn
.add-btn {
  display: inline-block;
  min-width: 92px;
  padding: 3px 10px;
  margin-top: 8px;
  font-size: 12px;
  color: $--color-text-primary;
  text-align: center;
  cursor: pointer;
  background-color: $--button-default-background-color;
  border-radius: $--border-radius-base;

  .wk-l-plus {
    font-size: 12px;
  }
}

.upload-file /deep/ .el-upload-list--picture {
  display: none;
}

.add-btn:hover {
  background-color: $--button-hover-background-color;
}

// 子任务
.sub-task-all {
  background: $--color-white;
  border-radius: 4px;
  box-shadow: $--box-shadow-bottom-light;

  &:hover {
    box-shadow: $--box-shadow-hover-bottom-light;
  }
}

.item-img {
  display: inline-block;
  width: 16px;
  height: 16px;
  margin-left: 10px;
}

.sub-task {
  position: relative;
  height: 48px;
  font-size: 14px;
  border-bottom: $--border-base;

  &:nth-last-child(1) {
    border-bottom: none;
  }

  &:hover {
    background-color: $--color-n20;
  }

  &__hd {
    // 解决多选框样式
    .el-checkbox /deep/ .el-checkbox__inner {
      width: 16px;
      height: 16px;
    }

    .el-checkbox /deep/ .el-checkbox__inner::after {
      top: 0;
      left: 4px;
      width: 4px;
      height: 10px;
      border-width: 2px;
    }
  }

  &__bd {
    position: relative;
    flex: 1;
    margin-left: 16px;
  }

  &__bd.is-checked {
    color: #8f8f8f;
    text-decoration: line-through;
  }

  .edit-del-box {
    flex-shrink: 0;
    margin-left: 8px;
  }

  .bg-color {
    flex-shrink: 0;
    font-size: 12px;
  }

  .user-img {
    flex-shrink: 0;
    margin-left: 10px;
  }
}
</style>
