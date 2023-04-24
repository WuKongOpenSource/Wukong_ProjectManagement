<template>
  <div v-loading="loading" class="gannt">
    <div class="gannt-header">
      <div>
        <el-input
          v-model="filterObj.search"
          placeholder="搜索迭代名称"
          @blur="getTaskData">
          <el-button
            slot="suffix"
            type="icon"
            icon="wk wk-sousuo"
            @click="getTaskData" />
        </el-input>
      </div>
      <div>
        <el-select
          v-model="filterObj.ganttMode"
          style="width: 100px;"
          @change="ganttModeChanged">
          <el-option
            v-for="item in modeOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value" />
        </el-select>
      </div>
    </div>
    <div class="gannt-body">
      <div class="gannt-left">
        <el-table
          :data="tableData"
          row-key="taskId"
          border
          class="gannt-table"
          default-expand-all
          :cell-class-name="cellClassName"
          :tree-props="{children: 'children', hasChildren: 'hasChildren'}"
          @expand-change="expandChange"
          @row-click="handleRowClick">
          <el-table-column
            prop="name"
            label="名称"
            sortable
            show-overflow-tooltip>
            <template slot-scope="{row}">
              <span class="icon-name">
                <img v-if="row.type" class="item-img" :src="getIconClass(row)" alt="">
                <span class="item-title">{{ row.name }}</span>
              </span>
            </template>
          </el-table-column>
          <el-table-column
            prop="startTime"
            label="开始时间"
            width="100"
            sortable
            show-overflow-tooltip />
          <el-table-column
            prop="stopTime"
            label="结束时间"
            width="100"
            show-overflow-tooltip />
          <div
            slot="append"
            :style="{
              height: tableData.length === 0 ? '62px' : '143px'
            }" />
        </el-table>
      </div>
      <div class="gannt-right">
        <main
          ref="gantt"
          class="gantt-target" />
      </div>
    </div>

    <item-detail
      v-if="taskDetailShow"
      :id="taskId"
      ref="particulars"
      :project-detail="projectDetail"
      :detail-index="detailIndex"
      :no-listener-class="['el-table__body']"
      @update-list="handleUpdateList"
      @close="taskDetailShow = false" />
  </div>
</template>

<script>
import { projectGetGanttAPI } from '@/api/pm/project'

import Gantt from 'frappe-gantt'
import ItemDetail from '@/views/pm/project/team/itemDetail'

import { getItemImg } from '@/views/pm/data'
import { timeToFormatTime } from '@/utils'

const InitTask = {
  id: 'initTask',
  start: timeToFormatTime(new Date()),
  end: timeToFormatTime(new Date()),
  name: ''
}

export default {
  name: 'Gannt',
  components: {
    ItemDetail
  },

  inject: ['rootTabs'],
  props: {
    projectDetail: Object
  },
  data() {
    return {
      loading: false,
      itemType: 'Gannt',
      ganttInstance: null,
      ganttData: [],

      tableData: [],

      filterObj: {
        search: '',
        ganttMode: 'Day'
      },
      modeOptions: [
        { label: '日视图', value: 'Day' },
        { label: '周视图', value: 'Week' },
        { label: '月视图', value: 'Month' },
        { label: '年视图', value: 'Year' }
      ],

      taskId: '',
      detailIndex: -1,
      taskDetailShow: false

    }
  },

  watch: {
    'rootTabs.currentName'(val) {
      if (val === 'Gannt') {
        this.firstLoad()
      }
    }
  },

  mounted() {
    this.initGantt()
    this.firstLoad()
  },

  methods: {
    /**
     * @description: 首次执行操作
     * @return {*}
     */
    firstLoad() {
      this.clearFilterCondition()
      this.getTaskData()
    },

    /**
     * @description: 清除筛选条件
     * @return {*}
     */
    clearFilterCondition() {
      this.filterObj.search = ''
    },

    /**
     * @description: 甘特图初始化
     * @return {*}
     */
    initGantt() {
      this.ganttInstance = new Gantt('.gantt-target', [InitTask], {
        padding: 20, // 保证和表格等高
        popup_trigger: 'mouseover',
        on_click: function(task) {
          console.log('on_click---', task)
        },
        on_date_change: function(task, start, end) {
          console.log('on_date_change---', task, start, end)
          console.log('timeToFormatTime(start)---', timeToFormatTime(start))
          // task.startTime = timeToFormatTime(start)
          // task.stopTime = timeToFormatTime(end)
        },
        on_progress_change: function(task, progress) {
          task.progress = progress
          console.log('on_progress_change---', task, progress)
        },
        on_view_change: function(mode) {
          console.log('on_view_change---', mode)
        },
        view_mode: this.filterObj.ganttMode,
        language: 'zh'
      })
      console.log(this.ganttInstance)

      document.querySelector(".bar-wrapper[data-id='initTask']").remove()
    },

    /**
     * @description: 标准刷新，供父组件调用使用
     * @return {*}
     */
    refresh(isClearFilter) {
      isClearFilter && this.clearFilterCondition()
      this.getTaskData()
    },

    /**
     * @description: 获取任务数据
     * @return {*}
     */
    getTaskData() {
      projectGetGanttAPI({
        projectId: this.$route.params.id,
        pageType: 1,
        search: this.filterObj.search
      }).then(res => {
        this.loading = false
        this.tableData = res.data
        this.tableData.forEach(item => {
          item.startTime = this.getTime(item.startTime)
          item.stopTime = this.getTime(item.stopTime)
          item.children.forEach(val => {
            val.startTime = this.getTime(val.startTime)
            val.stopTime = this.getTime(val.stopTime)
          })
        })
        this.refreshGanntData()
      }).catch(() => {
        this.loading = false
      })
    },

    /**
     * @description: 表格的展开闭合
     * @return {*}
     */
    expandChange(row, expand) {
      row.expand = expand

      this.refreshGanntData()
    },

    /**
     * @description: 刷新甘特图数据
     * @return {*}
     */
    refreshGanntData() {
      const tasks = this.handleTaskData()
      if (tasks.length === 0) {
        this.ganttInstance.refresh([InitTask])
        document.querySelector(".bar-wrapper[data-id='initTask']").remove()
      } else {
        this.ganttInstance.refresh(tasks)
      }
    },

    cellClassName({ row, column, rowIndex, columnIndex }) {
      if (column.property === 'name' && !('expand' in row)) {
        return 'can-visit--underline'
      } else {
        return ''
      }
    },

    /**
     * @description: 图标
     * @param {*} row
     * @return {*}
     */
    getIconClass(row) {
      return getItemImg(row)
    },

    /**
     * @description: 进入详情
     * @param {*} row
     * @param {*} column
     * @param {*} event
     * @return {*}
     */
    handleRowClick(row, column, event) {
      if (column.property == 'name' && !('expand' in row)) {
        this.taskId = row.taskId
        // this.detailIndex = this.getObjIndex(this.list, 'taskId', row.taskId)
        this.taskDetailShow = true
      }
    },

    /**
     * @description: 更新数据
     * @return {*}
     */
    handleUpdateList() {
      this.getTaskData()
    },

    /**
     * @description: 操作任务数据
     * @return {*}
     */
    handleTaskData() {
      const tasks = []

      this.tableData.forEach(item => {
        const copyItem = {
          name: item.name,
          id: item.taskId,
          start: this.getTime(item.startTime),
          end: this.getTime(item.stopTime),
          custom_class: 'cc-iteration'
        }

        if (!item.hasOwnProperty('expand')) {
          item.expand = true
        }

        // 时间无效 名称改为空
        if (!copyItem.start || !copyItem.end) {
          copyItem.name = ''
        }
        tasks.push(copyItem)

        if (item.children && item.expand) {
          item.children.forEach(child => {
            const copyChild = {
              name: child.name,
              id: child.taskId,
              start: this.getTime(child.startTime),
              end: this.getTime(child.stopTime)
            }

            if (!copyChild.start || !copyChild.end) {
              copyChild.name = ''
            }
            // 2需求 3任务 4缺陷
            if (child.type) {
              copyChild.custom_class = {
                2: 'cc-require-item',
                3: 'cc-task-item',
                4: 'cc-defects-item'
              }[child.type]
            }
            tasks.push(copyChild)
          })
        }
      })
      return tasks
    },

    /**
     * @description: 获取正确的时间
     * @return {*}
     */
    getTime(time) {
      if (time) {
        return time.split(' ')[0]
      }
      return ''
    },

    /**
     * @description: 甘特图展示调整
     * @return {*}
     */
    ganttModeChanged() {
      this.ganttInstance.change_view_mode(this.filterObj.ganttMode)
      document.querySelector(".bar-wrapper[data-id='initTask']").remove()
    }
  }
}
</script>
<style lang='scss' scoped>
@import "frappe-gantt/src/gantt";

.gannt {
  height: 100%;

  &-body {
    position: relative;
    display: flex;
    width: 100%;
    height: calc(100% - 55px);
    overflow-y: auto;
  }

  &-header {
    display: flex;
    justify-content: space-between;
    height: 40px;

    .el-input {
      width: 220px;
    }
  }

  &-left {
    flex-shrink: 0;
    width: 500px;
    height: 100%;

    .gannt-table {
      /deep/ .el-table__empty-block {
        height: calc(100% - 70px);
      }
    }
  }

  &-right {
    position: relative;
    flex: 1;
    width: 0;

    .gantt-target {
      width: 100%;
      height: 100%;
      border-right: $--border-base;
      border-bottom: $--border-base;
    }
  }

  /deep/ .el-table {
    th {
      height: 59.5px;
    }
  }
}

// 定义 bar 和 进度颜色
/deep/ .cc-iteration {
  .bar {
    fill: $--color-n300 !important;
  }
}

/deep/ .cc-task-item {
  .bar {
    fill: $--color-t300 !important;
  }
}

/deep/ .cc-require-item {
  .bar {
    fill: $--color-p300 !important;
  }
}

/deep/ .cc-defects-item {
  .bar {
    fill: $--color-r300 !important;
  }
}

/deep/ .bar-invalid {
  display: none;
}

/deep/ .el-table .cell {
  display: flex;
  align-items: center;
  justify-content: flex-start;
}

.icon-name {
  display: inline-flex;
  align-items: center;
  justify-content: flex-start;
  max-width: calc(100% - 40px);
}

.item-img {
  display: inline-block;
  width: 16px;
  height: 16px;
  margin-right: 5px;
}

.item-title {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style>
