<template>
  <div class="main">
    <flexbox class="main-header" justify="space-between">
      <div class="main-header__left">
        <span class="title">{{ title }}</span>
      </div>
      <div class="main-header__right">
        <el-button
          type="primary"
          @click="createClick">新建任务</el-button>
        <el-button
          @click="exportClick">导出</el-button>
      </div>
    </flexbox>

    <flexbox class="main-header is-filter-header" justify="space-between">
      <div class="main-header__left">
        <el-input
          v-model="search"
          placeholder="请输入任务名称"
          class="search-input"
          @input="debouncedRefreshList"
          @keyup.enter.native="debouncedRefreshList">
          <el-button
            slot="suffix"
            type="icon"
            icon="wk wk-sousuo"
            @click="debouncedRefreshList" />
        </el-input>

        <span class="tabs">
          <span class="tabs-label">显示:</span>
          <el-button
            v-for="(item, index) in tabs"
            :key="index"
            :type="item.name === tabsSelectValue ? 'selected' : null"
            @click="tabsChange(item.name)">{{ item.label }}</el-button>
        </span>

        <span class="progress">共<span>{{ progress.allTask }}</span>个,已完成<span>{{ progress.stopTask }}</span>个</span>
        <el-button
          class="refresh-btn"
          :class="[{'is-rotate': refreshRotate}]"
          type="icon"
          icon="el-icon-refresh"
          @click="refreshRotateClick" />
      </div>
      <div class="main-header__right">
        <el-popover
          v-model="taskFilterShow"
          popper-class="no-padding-popover"
          placement="bottom"
          width="300"
          trigger="click">
          <task-filter
            v-if="taskFilterShow"
            :due-date="dueDate"
            :priority="priority"
            :done="showDone"
            :users="userList"
            @close="taskFilterShow = false"
            @save="taskFilterSave"
          />
          <el-button
            slot="reference"
            v-model="taskFilterShow"
            :type="taskFilterShow ? 'selected' : 'subtle'"
            icon="wk wk-screening"
            @click="taskFilterShow = true">筛选</el-button>
        </el-popover>
      </div>
    </flexbox>

    <el-table
      :key="`${tableHeight || '0'}-${pageSize}`"
      v-loading="loading"
      :row-height="rowHeight"
      :data="list"
      :height="tableHeight"
      :cell-class-name="cellClassName"
      :class="WKConfig.tableStyle.class"
      :stripe="WKConfig.tableStyle.stripe"
      use-virtual
      row-key="taskId"
      highlight-current-row
      style="width: 100%;"
      @row-click="handleRowClick"
      @selection-change="handleSelectionChange">
      <el-table-column
        show-overflow-tooltip
        align="center"
        fixed
        width="55">
        <template slot-scope="{ row }">
          <el-checkbox
            v-model="row.checked"
            @click.native.stop=""
            @change="taskOverClick(row)" />
        </template>
      </el-table-column>
      <el-table-column
        v-for="(item, index) in tableHeaderFields"
        :key="index"
        :fixed="index==0"
        :prop="item.field"
        :label="item.name"
        :min-width="item.width"
        show-overflow-tooltip>
        <template slot-scope="{ row, column }">
          <template v-if="column.property === 'priority'">
            <span
              :style="{
                backgroundColor: getPriorityColor(row.priority).color
              }"
              class="status-mark" />{{ getPriorityColor(row.priority).label }}
          </template>
          <template v-else-if="column.property === 'labelId'">
            <tag-view
              :item-bottom="0"
              :value="(row.labelList || []).map(item => {
                return { name: item.labelName, value: item.color ? item.color : '#ccc' }
              })" />
          </template>
          <template v-else>
            <i v-if="item.iconClass && (row[column.property] !== '' && row[column.property] !== null)" :class="item.iconClass" />
            <span :class="{'can-visit--underline': item.canVisite}">{{ fieldFormatter(row, column, row[column.property], item) }}</span>
            <el-tag v-if="item.field === 'stopTime' && getIsOverdue(row)" type="danger" disable-transitions>已逾期</el-tag>
          </template>
        </template>
      </el-table-column>
      <wk-empty
        slot="empty"
        :props="{
          buttonTitle: '新建任务',
          showButton: true
        }"
        @click="createClick"
      />
    </el-table>
    <div class="p-contianer">
      <el-pagination
        :current-page="currentPage"
        :page-sizes="pageSizes"
        :page-size.sync="pageSize"
        :total="total"
        :pager-count="5"
        class="p-bar"
        layout="prev, pager, next, sizes, total, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange" />
    </div>

    <!-- 任务详情 -->
    <task-detail
      v-if="taskDetailShow"
      :id="taskId"
      ref="particulars"
      :detail-index="detailIndex"
      :no-listener-class="['el-table__body']"
      @on-handle="detailHandle"
      @close="taskDetailShow = false" />

    <!-- 任务新建 -->
    <task-create
      v-if="taskCreateShow"
      @save="getList"
      @close="taskCreateShow = false"
    />
  </div>
</template>

<script>
import { taskListAPI, taskOaExportAPI } from '@/api/oa/task'
import { workTaskStatusSetAPI } from '@/api/pm/projectTask'

import TaskDetail from './components/TaskDetail'
import TaskFilter from './components/TaskFilter'
import TaskCreate from './Create'
import TagView from '@/components/NewCom/WkTag/TagView' // 标签

import { mapGetters } from 'vuex'
import { downloadExcelWithResData } from '@/utils'
import { debounce } from 'throttle-debounce'

import TableMixin from '@/mixins/Table'
import TaskMixin from './mixins/TaskMixin'
import moment from 'moment'

export default {
  /** 任务列表 */
  name: 'Index',
  components: {
    TaskDetail,
    TaskFilter,
    TaskCreate,
    TagView
  },
  mixins: [TableMixin, TaskMixin],
  beforeRouteUpdate(to, from, next) {
    this.taskType = to.params.type
    this.showDone = true
    this.progress = {
      stopTask: 0,
      // 总数量
      allTask: 0
    }
    this.tabsSelectValue = '0'
    this.dueDate = ''
    this.priority = ''
    this.userList = []
    this.search = ''

    this.refreshList()
    next()
  },
  data() {
    return {
      tabsSelectValue: '0',
      // 任务类型 区分我的任务和下属任务
      taskType: '',
      list: [],
      refreshRotate: false, // 控制按钮转圈
      loading: false,
      dueDate: '',
      search: '',
      priority: '',
      showDone: true,
      userList: [],
      taskFilterShow: false,
      // 任务总进程
      progress: {
        stopTask: 0,
        // 总数量
        allTask: 0
      },

      // 表头
      tableHeaderFields: [{
        name: '任务名称',
        field: 'name',
        width: 360,
        formType: 'text'
      }, {
        name: '描述',
        field: 'description',
        width: 120,
        formType: 'textarea'
      }, {
        name: '优先级',
        field: 'priority',
        width: 80,
        formType: 'select'
      }, {
        name: '负责人',
        field: 'mainUserId',
        width: 80,
        formType: 'user'
      }, {
        name: '开始时间',
        field: 'startTime',
        width: 120,
        iconClass: 'wk wk-icon-datetime2',
        formType: 'date'
      }, {
        name: '结束时间',
        field: 'stopTime',
        width: 200,
        iconClass: 'wk wk-icon-datetime2',
        formType: 'date'
      }, {
        name: '参与人',
        field: 'ownerUserId',
        width: 80,
        formType: 'user'
      }, {
        name: '标签',
        field: 'labelId',
        width: 200,
        formType: 'tag'
      }, /*, {
        name: '相关信息数',
        field: 'relationCount',
        width: 100,
        formType: 'relatedBusiness'
      }*/ {
        name: '子任务数',
        field: 'childAllCount',
        width: 80,
        iconClass: 'wk wk-subtasks',
        formType: 'text',
        canVisite: true
      }, {
        name: '附件数',
        field: 'fileCount',
        width: 80,
        iconClass: 'wk wk-icon-file',
        formType: 'text',
        canVisite: true
      }, {
        name: '评论数',
        field: 'commentCount',
        width: 80,
        iconClass: 'wk wk-icon-message-line',
        formType: 'text',
        canVisite: true
      }],

      // 详情
      // 详情数据
      taskId: '',
      detailIndex: -1,
      taskDetailShow: false,

      // 新建
      taskCreateShow: false
    }
  },
  computed: {
    ...mapGetters(['userInfo']),

    tabs() {
      if (this.taskType == 1) {
        return [
          {
            label: '全部',
            name: '0'
          },
          {
            label: '我负责的',
            name: '1'
          },
          {
            label: '我参与的',
            name: '3'
          }
        ]
      } else {
        return [
          {
            label: '全部',
            name: '0'
          },
          {
            label: '下属负责的',
            name: '1'
          },
          {
            label: '下属参与的',
            name: '3'
          }
        ]
      }
    },

    title() {
      return this.taskType == 1 ? '我的任务' : '下属的任务'
    }
  },
  watch: {},
  created() {
    this.taskType = this.$route.params.type
    this.debouncedRefreshList = debounce(300, this.refreshList)
    this.refreshList()
  },
  mounted() {},

  beforeDestroy() {},
  methods: {
    /**
     * 新建
     */
    createClick() {
      this.taskCreateShow = true
    },

    /**
     * 通过回调控制class
     */
    cellClassName({ row, column, rowIndex, columnIndex }) {
      if (column.property === 'name') {
        let defaultClass = 'can-visit--underline'
        if (row.checked) {
          defaultClass += ' wk-task-name-finish'
        }

        return defaultClass
      } else {
        return ''
      }
    },

    /**
     * 列表操作
     * @param {*} row
     * @param {*} column
     * @param {*} event
     */
    handleRowClick(row, column, event) {
      this.taskId = row.taskId
      this.detailIndex = this.getObjIndex(this.list, 'taskId', row.taskId)
      this.taskDetailShow = true
    },

    /**
     * 获取对象在所在数组的索引
     */
    getObjIndex(array, filed, value) {
      for (let index = 0; index < array.length; index++) {
        const element = array[index]
        if (element[filed] === value) {
          return index
        }
      }
      return null
    },

    /**
     * 勾选操作
     * @param {*} val
     */
    handleSelectionChange(val) {

    },

    /**
     * 点击刷新按钮
     */
    refreshRotateClick() {
      this.getList()
      this.refreshRotate = true
      setTimeout(() => {
        this.refreshRotate = false
      }, 1000)
    },

    /**
     * 刷新列表
     */
    refreshList() {
      this.currentPage = 1
      this.list = []
      this.getList()
    },

    taskFilterSave(dueDate, priority, showDone, users) {
      this.priority = priority
      this.dueDate = dueDate
      this.showDone = showDone
      this.userList = users
      this.refreshList()
    },

    /**
     * 获取数据列表
     */
    getList() {
      this.loading = true
      const params = {
        page: this.currentPage,
        limit: this.pageSize,
        type: this.tabsSelectValue,
        priority: this.priority,
        dueDate: this.dueDate,
        status: this.showDone ? '' : '1',
        mainUserIds: this.userList && this.userList.length > 0 ? this.userList.map(item => item.userId) : []
      }

      if (this.search) {
        params.search = this.search
      }

      if (this.taskType != 1) {
        params.mold = 1 // 下属任务
      }

      taskListAPI(params)
        .then(res => {
          this.loading = false
          const data = res.data || {}
          if (this.tabsSelectValue == params.type) {
            const resData = data.page
            const list = resData.list
            for (const item of list) {
              if (item.status == 5) {
                item.checked = true
              }
            }
            this.total = resData.totalRow
            this.list = list
            this.progress = data

            this.updateTableHeight()
          } else {
            this.refreshList()
          }
        })
        .catch(() => {
          this.updateTableHeight()
          this.loading = false
        })
    },

    /**
     * 格式化展示
     */
    fieldFormatter(row, column, cellValue, field) {
      if (column.property === 'mainUserId') {
        return row.mainUser ? row.mainUser.realname : ''
      } else if (column.property === 'ownerUserId') {
        return row.ownerUserList ? row.ownerUserList.map(item => item.realname).join('、') : ''
      }

      return cellValue
    },

    /**
     * 中间tabs改变
     */
    tabsChange(type) {
      this.tabsSelectValue = type
      this.refreshList()
    },

    /**
     * 任务cell 操作
     */
    taskCellHandle(type, data, index) {
      if (type == 'view') {
        this.taskId = data.taskId
        this.detailIndex = index
        this.taskDetailShow = true
      } else if (type == 'complete') {
        this.progress.stopTask = data.checked
          ? ++this.progress.stopTask
          : --this.progress.stopTask
      }
    },

    /**
     * 详情操作
     */
    detailHandle(data) {
      if (data.type == 'delete') {
        this.list.splice(data.index, 1)
        this.updateTableHeight()
      } else {
        // 获取5条数据
        let page = 1
        if (data.index > 0) {
          page = Math.ceil(data.index / 5)
        }
        const params = {
          page: page,
          limit: 5,
          type: this.tabsSelectValue,
          priority: this.priority,
          dueDate: this.dueDate,
          status: this.showDone ? '' : '1'
        }

        if (this.taskType != 1) {
          params.mold = 1 // 下属任务
        }

        taskListAPI(params)
          .then(res => {
            const task = this.list[data.index]
            for (let index = 0; index < res.data.page.list.length; index++) {
              const element = res.data.page.list[index]
              if (element.taskId == task.taskId) {
                if (element.status == 5) {
                  element.checked = true
                }
                this.list.splice(data.index, 1, element)
                break
              }
            }
            this.progress = res.data
          })
          .catch(() => {})
      }
    },

    /**
     * 审批导出
     */
    exportClick() {
      this.loading = true
      const params = {
        type: this.tabsSelectValue,
        priority: this.priority,
        dueDate: this.dueDate,
        status: this.showDone ? '' : '1'
      }

      if (this.taskType != 1) {
        params.mold = 1 // 下属任务
      }

      taskOaExportAPI(params)
        .then(res => {
          downloadExcelWithResData(res)
          this.loading = false
        })
        .catch(() => {
          this.loading = false
        })
    },

    /**
     * 是否截止
     */
    getIsOverdue({ stopTime, status }) {
      // 5 完成任务
      if (status != 5 && stopTime) {
        // indexOf 简单判断是否有时分秒
        return moment().isAfter(stopTime.indexOf(' ') !== -1 ? stopTime : `${stopTime} 23:59:59`)
      } else {
        return false
      }
    },

    /**
     * @description: 列表标记任务
     * @param {*} val
     * @return {*}
     */
    taskOverClick(val) {
      const status = val.checked ? 5 : 1
      workTaskStatusSetAPI({
        taskId: val.taskId,
        status: status
      })
        .then(res => {
          val.status = status
        })
        .catch(() => {
          val.checked = false
        })
    }
  }
}
</script>

<style lang="scss">
.wk-task-name-finish {
  color: $--color-n90 !important;
  text-decoration: line-through;
}
</style>
<style lang="scss" scoped>
@import "../style/index.scss";

.main {
  .progress {
    margin-left: $--interval-base;
    color: $--color-text-secondary;

    > span {
      margin: 0 4px;
      color: $--color-black;
    }
  }

  /deep/ .refresh-btn {
    padding-left: 4px;

    i {
      color: $--color-black;
      transform-origin: center;
    }

    &.is-rotate {
      i {
        transition: all 1s;
        transform: rotate(360deg);
      }
    }
  }

  // 优先级
  .status-mark {
    display: inline-block;
    width: 8px;
    height: 8px;
    margin-right: 4px;
    border-radius: 4px;
  }
}

.content-wrapper {
  position: relative;
  height: calc(100% - 70px);
  padding: 70px 0 76px;
  margin-top: 15px;
  overflow: hidden;
  background-color: white;
  border: 1px solid $--border-color-base;
  border-radius: $--border-radius-base;

  &__hd {
    position: absolute;
    top: 0;
    right: 1px;
    left: 1px;
    z-index: 5;
    padding: 15px;

    .head-img {
      margin-right: 30px;
    }

    .el-date-editor {
      width: 150px;
    }

    .el-select {
      width: 80px;
    }

    .el-progress {
      width: 300px;
    }

    .label {
      margin: 0 10px 0 20px;
      font-size: 12px;
      color: $--color-text-secondary;
    }

    .label.margin-left {
      margin-left: 30px;
    }

    .header-right {
      .el-input {
        width: 200px;
        margin-right: 10px;
      }

      .el-button {
        /deep/ i {
          margin-right: 5px;
        }
      }
    }
  }
}

.cell-section {
  height: 100%;
  overflow: auto;
}

// 快捷添加
.task-add {
  position: absolute;
  right: 1px;
  bottom: 0;
  left: 1px;
  z-index: 5;
  padding: 15px;
  background-color: white;
}

.user-icon {
  padding: 8px 10px;
  font-size: 20px;
  color: white;
  background: $--color-primary;
  border-radius: 50%;
}
</style>
