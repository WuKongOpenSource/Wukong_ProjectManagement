<template>
  <div v-loading="loading" class="worktime">
    <div class="worktime-header">
      <el-input
        v-model="filterObj.search"
        placeholder="搜索迭代名称"
        @blur="getData">
        <el-button
          slot="suffix"
          type="icon"
          icon="wk wk-sousuo"
          @click="getData" />
      </el-input>

      <el-date-picker
        v-model="filterObj.time"
        type="daterange"
        :clearable="false"
        :picker-options="pickerOptions"
        value-format="yyyy-MM-dd"
        format="yyyy-MM-dd"
        range-separator="至"
        start-placeholder="开始日期"
        end-placeholder="结束日期"
        align="right"
        @change="getData" />
    </div>

    <div class="worktime-body">
      <el-table
        :data="tableData"
        row-key="taskId"
        border
        default-expand-all
        :cell-class-name="cellClassName"
        :tree-props="{
          children: 'taskTimeListVOList',
          hasChildren: 'hasChildren'
        }"
        @row-click="handleRowClick">
        <el-table-column
          prop="name"
          label="名称"
          fixed
          width="250"
          show-overflow-tooltip>
          <template slot-scope="{row}">
            <span class="icon-name">
              <img v-if="row.type" class="item-img" :src="getIconClass(row)" alt="">
              <span class="item-title">{{ row.name }}</span>
            </span>
          </template>
        </el-table-column>
        <el-table-column
          prop="totalTime"
          label="总计"
          fixed
          show-overflow-tooltip />
        <el-table-column
          v-for="(item, index) in timeFields"
          :key="index"
          :prop="item.field"
          :label="item.label"
          :min-width="110"
          show-overflow-tooltip />
      </el-table>
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
import { projectTaskTimeListAPI } from '@/api/pm/project'

import ItemDetail from '@/views/pm/project/team/itemDetail'

import moment from 'moment'
import NP from 'number-precision'
import { getItemImg } from '@/views/pm/data'

export default {
  // Worktime 工时
  name: 'Worktime',

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
      height: document.documentElement.clientHeight - 200,
      tableData: [],

      filterObj: {
        search: '',
        time: []
      },
      pickerOptions: {
        disabledDate(time) {
          return time.getTime() > Date.now()
        },
        shortcuts: [{
          text: '最近一周',
          onClick(picker) {
            const end = new Date()
            const start = new Date()
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 7)
            picker.$emit('pick', [start, end])
          }
        }, {
          text: '最近一个月',
          onClick(picker) {
            const end = new Date()
            const start = new Date()
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 30)
            picker.$emit('pick', [start, end])
          }
        }, {
          text: '最近三个月',
          onClick(picker) {
            const end = new Date()
            const start = new Date()
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 90)
            picker.$emit('pick', [start, end])
          }
        }]
      },

      taskId: '',
      detailIndex: -1,
      taskDetailShow: false
    }
  },

  computed: {
    timeFields() {
      if (this.filterObj.time.length < 2) return []

      const daysList = []
      const SDate = moment(this.filterObj.time[0])
      const EDate = moment(this.filterObj.time[1])
      daysList.push({
        label: SDate.format('MM-DD/ddd'),
        field: SDate.format('YYYY-MM-DD')
      })
      while (SDate.add(1, 'days').isBefore(EDate)) { // 注意这里add方法处理后SDate对象已经改变。
        daysList.push({
          label: SDate.format('MM-DD/ddd'),
          field: SDate.format('YYYY-MM-DD')
        })
      }
      daysList.push({
        label: EDate.format('MM-DD/ddd'),
        field: EDate.format('YYYY-MM-DD')
      })
      return daysList
    }
  },

  watch: {
    'rootTabs.currentName'(val) {
      if (val === 'Worktime') {
        this.firstLoad()
      }
    }
  },

  created() {
    this.filterObj.time = [moment().startOf('month').format('YYYY-MM-DD'),
      moment().format('YYYY-MM-DD')]
  },

  mounted() {
    window.onresize = () => {
      this.height = document.documentElement.clientHeight - 200
    }
    this.firstLoad()
  },

  beforeDestroy() {},

  methods: {
    /**
     * @description: 首次执行操作
     * @return {*}
     */
    firstLoad() {
      this.clearFilterCondition()
      this.getData()
    },

    /**
     * @description: 清除筛选条件
     * @return {*}
     */
    clearFilterCondition() {
      this.filterObj.search = ''
    },

    /**
     * @description: 标准刷新，供父组件调用使用
     * @return {*}
     */
    refresh(isClearFilter) {
      isClearFilter && this.clearFilterCondition()
      this.getData()
    },

    /**
     * @description: 获取任务数据
     * @return {*}
     */
    getData() {
      this.loading = true
      projectTaskTimeListAPI({
        projectId: this.$route.params.id,
        pageType: 1,
        search: this.filterObj.search,
        beginTime: this.filterObj.time[0] + ' 00:00:00',
        endTime: this.filterObj.time[1] + ' 23:59:59'
      }).then(res => {
        this.loading = false
        const resData = res.data || []
        resData.forEach(iterate => {
          let totalTime = 0
          iterate.taskTimeListVOList.forEach(item => {
            // 迭代总计
            totalTime = NP.plus(totalTime, item.totalTime || 0)

            // 工时列表
            if (item.taskTimeVOS) {
              // 通过时间 缓存已有工时
              const timeVOS = {}
              item.taskTimeVOS.forEach(timeItem => {
                if (timeItem.beginTime) {
                  const timeKey = timeItem.beginTime.split(' ')[0]
                  const cacheIndex = timeVOS[timeKey]
                  if (cacheIndex !== undefined) {
                    // 获取到已有item
                    const cacheItem = timeVOS[timeKey]
                    // 更新 actualHour
                    cacheItem.actualHour = NP.plus(cacheItem.actualHour, timeItem.actualHour || 0)
                    // 更新事项值
                    item[timeKey] = cacheItem.actualHour
                    // 更新迭代值
                    iterate[timeKey] = NP.plus(iterate[timeKey] || 0, timeItem.actualHour || 0)
                  } else {
                    timeVOS[timeKey] = {
                      actualHour: timeItem.actualHour
                    }
                    // 更新事项值
                    item[timeKey] = timeItem.actualHour
                    // 更新迭代值
                    if (iterate[timeKey]) {
                      iterate[timeKey] = NP.plus(iterate[timeKey], timeItem.actualHour || 0)
                    } else {
                      iterate[timeKey] = timeItem.actualHour || 0
                    }
                  }
                }
              })
            }
          })

          iterate.totalTime = totalTime
        })
        this.tableData = resData
      }).catch(() => {
        this.loading = false
      })
    },

    cellClassName({ row, column, rowIndex, columnIndex }) {
      if (column.property === 'name' && !('taskTimeListVOList' in row)) {
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
      if (column.property == 'name' && !('taskTimeListVOList' in row)) {
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
    }
  }
}
</script>

<style lang="scss" scoped>
.worktime {
  height: 100%;

  &-body {
    position: relative;
    width: 100%;
    height: calc(100% - 55px);
    overflow-y: auto;
  }

  &-header {
    display: flex;
    height: 40px;

    .el-input {
      width: 220px;
    }

    .el-date-editor {
      width: 300px;
      margin-left: 16px;
    }
  }
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
