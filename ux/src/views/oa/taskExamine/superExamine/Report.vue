<template>
  <div class="report main">
    <flexbox class="main-header" justify="space-between">
      <div class="main-header__left">
        <span class="title">报表</span>
      </div>
    </flexbox>
    <flexbox v-loading="loading" class="status-bar">
      <div v-for="item in statusList" :key="item.key" class="status-bar__item">
        <template v-if="item.key!='5'">
          <div><span class="value">{{ item.value }}</span> <span>{{ item.unit }}</span> </div>
          <div class="status-bar-label">{{ item.label }}</div>
        </template>
        <template v-else>
          <div>{{ item.value }} <span>{{ item.unit }}</span> </div>
          <div class="effect">
            <span>审批时效</span>
            <el-slider
              disabled
              :max="10"
              :value="Number(item.value)"
              :format-tooltip="formatTooltip" />
            <span>{{ item.extra }}</span>
          </div>
        </template>
      </div>
    </flexbox>

    <flexbox class="report-filter">
      <xr-radio-menu
        v-model="filterDataType"
        :options="rangeOptions"
        :user-checked-data="filterValue.users"
        :dep-checked-data="filterValue.strucs"
        :show-default="false"
        :width="250"
        @select="radioMenuSelect">
        <el-input
          slot="reference"
          v-model="avatarData.realname"
          class="el-input--no-bg"
          :readonly="true">
          <i
            slot="suffix"
            class="el-icon-arrow-up" />
        </el-input>
      </xr-radio-menu>

      <wk-time-type-Select
        ref="timeTypeSelect"
        mode="no-border"
        :show-default="false"
        :width="200"
        :is-default.sync="timeDefault"
        :default-type="filterTime"
        style="margin-left: 8px;"
        @change="timeTypeChange" />

      <!-- <div style="margin-left: 18px;">
        <span>显示：</span>
        <el-button :type="filterValue.searchStyle==0 ? 'selected' : null" @click="displayHandler(0)">我审批的</el-button>
        <el-button :type="filterValue.searchStyle==1 ? 'selected' : null" @click="displayHandler(1)">我申请的</el-button>
      </div> -->
      <div class="table-header">
        <el-button size="default" type="" @click="exportReport">导出报告</el-button>
      </div>
    </flexbox>

    <el-table
      v-loading="loading"
      :data="list"
      border
      stripe
      :cell-class-name="cellClassName"
      @row-click="handleRowClick">
      <el-table-column
        v-for="(item, index) in fieldList"
        :key="index"
        :prop="item.prop"
        :label="item.label"
        :width="item.width"
        :sortable="item.prop != 'poolDay' ? 'custom' : false"
        :class-name="item.width>60 ? 'column' : '' "
        show-overflow-tooltip />
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

    <el-dialog
      v-if="detailListShow"
      top="10vh"
      width="80%"
      :title="dialogTitle"
      :visible.sync="detailListShow">
      <examine-table-view
        :is-page="false"
        :props="reportData" />
    </el-dialog>
  </div>
</template>

<script>

import {
  examineSuperExaminesGetExamineReportInfoAPI,
  examineSuperExaminesGetExamineReportListAPI,
  examineSuperExaminesGetExamineReporExportAPI
} from '@/api/oa/superExamine'

import XrRadioMenu from '@/components/Menu/XrRadioMenu'
import WkTimeTypeSelect from '@/components/WkTimeTypeSelect'
import ExamineTableView from './examineTableView'

import { downloadExcelWithResData } from '@/utils'
import TableMixin from '@/mixins/Table'
import NP from 'number-precision'
import { mapGetters } from 'vuex'
export default {
  name: 'Report', // 报表
  components: {
    XrRadioMenu,
    WkTimeTypeSelect,
    ExamineTableView
  },
  mixins: [TableMixin],
  data() {
    return {
      loading: false,
      currentActiveTab: 0,
      rangeOptions: [
        { label: '仅本人', command: 1 },
        { label: '本人及下属', command: 2 },
        { label: '仅本部门', command: 3 },
        { label: '本部门及下属部门', command: 4 },
        { label: '自定义', command: 'custom' }
      ],
      filterTime: 'month',
      filterDataType: 2,
      dataTypeDefault: 0, // 是否权限筛选默认
      timeDefault: 0, // 是否时间筛选默认
      filterValue: {
        dataType: 2,
        users: [],
        strucs: [],
        timeLine: {
          type: 'default',
          value: 'month'
        }
        // searchStyle: 1
      },
      list: [],
      statusList: [
        { key: '1', label: '年', value: '', unit: '月' },
        { key: '2', label: '发起审批量', value: '', unit: '件' },
        { key: '3', label: '平均办结时间', value: '', unit: '小时' },
        { key: '4', label: '审批次数', value: '', unit: '次' },
        { key: '5', label: '审批时效', value: '', unit: '小时', extra: '10小时' }
      ],
      fieldList: [
        { prop: 'name', label: '姓名', width: '80' },
        { prop: 'deptName', label: '所属部门', width: '120' },
        { prop: 'examineApplyNum', label: '提交数', width: '120' },
        { prop: 'examineNum', label: '审批总次数', width: '120' },
        { prop: 'examineTimeAvgNum', label: '平均时效（小时）', width: '200' },
        { prop: 'examineAvgNum5', label: '5小时内完成审批次数', width: '200' },
        { prop: 'examineFiveAvgPercent', label: '5小时内完成审批次数占比', width: '220' },
        { prop: 'examineAvgNum512', label: '5-12小时内完成审批次数', width: '200' },
        { prop: 'examineFiveAvgPercent512', label: '5-12小时内完成审批次数占比', width: '250' },
        { prop: 'examineAvgNum1224', label: '12-24小时内完成审批次数', width: '220' },
        { prop: 'examineFiveAvgPercent1224', label: '12-24小时内完成审批次数占比', width: '250' },
        { prop: 'examineAvgNum24', label: '超24小时完成审批次数', width: '200' },
        { prop: 'examineFiveAvgPercent24', label: '超24小时完成审批次数占比', width: '220' },
        { prop: 'examineLongTime', label: '最长审批用时（小时）', width: '200' },
        { prop: 'examineTimeAvgByYear', label: '当年平均审批时效', width: '200' }
        // { prop: 'examineTypeIdByYear', label: '当年累计文件量', width: '200' }
      ],

      detailListShow: false, // 列表详情
      reportData: {},
      dialogTitle: ''
    }
  },
  computed: {
    ...mapGetters(['userInfo']),
    // 如果只筛选一个人则头像显示当前被筛选人的头像，否则显示默认错误头像
    avatarData() {
      if (this.filterValue.dataType === 'custom') {
        const userNames = (this.filterValue.users || [])
          .map(o => o.realname)
        const strucNames = (this.filterValue.strucs || [])
          .map(o => o.name)
        return {
          realname: userNames.concat(strucNames).join(','),
          img: ''
        }
      }

      if (this.filterValue.dataType == 1) {
        return this.userInfo
      }

      return {
        showIcon: true,
        realname: {
          1: '仅本人',
          2: '本人及下属',
          3: '仅本部门',
          4: '本部门及下属部门'
        }[this.filterValue.dataType]
      }
    },

    getTableHeight() {
      const clientHeight = document.documentElement.clientHeight
      const paddingHieght = clientHeight * 0.2

      return clientHeight - paddingHieght - 200
    }
  },
  watch: {
    filterValue: {
      handler() {
        this.getList()
        this.getInfo()
      },
      deep: true
    }
  },
  created() {
    this.getList()
    this.getInfo()
  },
  methods: {
    /**
     * @description: 获取头部详情
     * @return {*}
     */
    getInfo() {
      const params = {
        limit: this.pageSize,
        page: this.currentPage,
        pageType: 1,
        ...this.getBaseParams()
      }
      this.loading = true
      examineSuperExaminesGetExamineReportInfoAPI(params).then(res => {
        this.loading = false
        const { year, month, examineStartNum, examineNum, examineEndTimeAvgNum, examineAvgNum } = res.data
        const list = []
        list.push({ key: '1', label: year + '年', value: month, unit: '月' })
        list.push({ key: '2', label: '发起审批量', value: examineStartNum, unit: '件' })
        list.push({ key: '3', label: '平均办结时间', value: examineEndTimeAvgNum, unit: '小时' })
        list.push({ key: '4', label: '审批次数', value: examineNum, unit: '次' })
        list.push({ key: '5', label: '审批时效', value: examineAvgNum, unit: '小时', extra: '10小时' })
        this.statusList = list
      }).catch(e => {
        this.loading = false
      })
    },

    /**
     * @description: 获取列表
     * @return {*}
     */
    getList() {
      const params = {
        limit: this.pageSize,
        page: this.currentPage,
        pageType: 1,
        ...this.getBaseParams()
      }
      this.loading = true
      examineSuperExaminesGetExamineReportListAPI(params).then(res => {
        this.list = res.data.list || []
        this.total = res.data.totalRow
        this.loading = false
      }).catch(e => {
        this.loading = false
      })
    },

    /**
     * @description: 员工部门筛选
     * @param {*} val
     * @param {*} data
     * @return {*}
     */
    radioMenuSelect(val, data) {
      this.filterValue.dataType = this.filterDataType
      if (this.filterDataType != 'custom') {
        this.filterValue.users = []
        this.filterValue.strucs = []
        // this.savefilter('dataType')
      } else {
        this.filterValue.users = data.users
        this.filterValue.strucs = data.strucs
        // this.savefilter('dataType')
      }
    },

    /**
     * @description: 时间筛选
     * @param {*} data
     * @return {*}
     */
    timeTypeChange(data) {
      this.filterValue.timeLine = data
      // this.savefilter('time')
    },

    // displayHandler(type) {
    //   this.filterValue.searchStyle = type
    // },

    /**
     * @description: 获取基础参数
     * @return {*}
     */
    getBaseParams() {
      const params = {}
      if (this.filterDataType != 'custom') {
        params.dataType = this.filterDataType
      } else {
        params.deptList = (this.filterValue.strucs || [])
          .map(item => item.deptId)

        params.userList = (this.filterValue.users || [])
          .map(item => item.userId)
      }

      if (this.filterValue.timeLine.type) {
        if (this.filterValue.timeLine.type === 'custom') {
          params.dateFilter = 'custom'
          params.startDate = this.filterValue.timeLine.startTime.replace(/\./g, '-')
          params.endDate = this.filterValue.timeLine.endTime.replace(/\./g, '-')
        } else {
          params.dateFilter = this.filterValue.timeLine.value || ''
        }
      }

      // params.searchStyle = this.filterValue.searchStyle

      return params
    },

    /**
     * @description: 导出报告
     * @return {*}
     */
    exportReport() {
      const params = {
        limit: this.pageSize,
        page: this.currentPage,
        pageType: 1,
        ...this.getBaseParams()
      }
      this.loading = true
      examineSuperExaminesGetExamineReporExportAPI(params).then(res => {
        this.loading = false
        downloadExcelWithResData(res)
      }).catch(e => {
        this.loading = false
      })
    },

    /**
     * @description: 行样式
     * @param {*} row
     * @param {*} column
     * @param {*} rowIndex
     * @param {*} columnIndex
     * @return {*}
     */
    cellClassName({ row, column, rowIndex, columnIndex }) {
      if (['examineApplyNum', 'examineNum', 'examineAvgNum5', 'examineAvgNum512', 'examineAvgNum1224', 'examineAvgNum24', 'examineTypeIdByYear'].includes(column.property)) {
        if (row[column.property]) {
          return 'can-visit--underline no-children no-father'
        } else {
          return 'can-visit--underline'
        }
      }
    },

    /**
     * @description: 行点击
     * @param {*} row
     * @param {*} column
     * @param {*} event
     * @return {*}
     */
    handleRowClick(row, column, event) {
      if (['examineApplyNum', 'examineNum', 'examineAvgNum5', 'examineAvgNum512', 'examineAvgNum1224', 'examineAvgNum24', 'examineTypeIdByYear'].includes(column.property)) {
        const reportSubFlag = {
          'examineApplyNum': 1,
          'examineNum': 2,
          'examineAvgNum5': 3,
          'examineAvgNum512': 4,
          'examineAvgNum1224': 5,
          'examineAvgNum24': 6
        }

        this.fieldList.forEach(item => {
          if (item.prop == column.property) {
            this.dialogTitle = item.label
          }
        })

        this.reportData = {
          examineType: 'report',
          params: {
            reportSubFlag: reportSubFlag[column.property],
            userIdList: [row.userId],
            ...this.getBaseParams()
          }
        }
        this.detailListShow = true
      }
    },

    /**
     * @description: 审批时效提示
     * @param {*} val
     * @return {*}
     */
    formatTooltip(val) {
      const number = Number(val) || 0
      if (number) {
        return NP.times(number, 10) + '%'
      } else {
        return 0
      }
    }
  }
}
</script>

<style lang="scss" scoped>
@import "../style/index.scss";

.status-bar {
  margin-top: 24px;
  background-color: #f4f5f7;

  &-label {
    color: $--color-text-secondary;
  }

  &__item {
    position: relative;
    display: flex;
    flex: 1;
    flex-direction: column;
    align-content: center;
    justify-content: center;
    height: 82px;
    padding: 10px;
    text-align: center;

    &::before {
      position: absolute;
      top: 20px;
      bottom: 20px;
      left: 0;
      width: 1px;
      content: "";
      background-color: $--color-n30;
    }

    &:first-child::before {
      display: none;
    }

    .value {
      font-size: 16px;
    }

    .effect {
      display: flex;
      align-items: center;
      justify-content: center;

      span {
        color: $--color-text-secondary;
      }

      .el-slider {
        flex: 1;
        padding: 0 10px;
      }
    }

    div {
      flex: 1;
      line-height: 30px;
    }
  }
}

.report-filter {
  margin-top: 16px;

  /deep/.xr-radio-menu-wrap {
    .el-popover__reference-wrapper {
      .el-input--no-bg {
        input {
          background-color: $--color-n20;
          border: none;
        }
      }
    }
  }
}

.table-header {
  display: flex;
  flex: 1;
  align-items: center;
  justify-content: flex-end;
  margin: 16px 0;
}

.el-icon-arrow-up {
  position: absolute;
  top: 9px;
  right: 5px;
  font-size: $--input-font-size;
  font-weight: bold;
  color: $--color-black;
  cursor: pointer;
  transition: transform 0.3s;
  transform: rotate(180deg);
}

/deep/ .el-dialog {
  height: 80vh;

  .el-dialog__header {
    padding: 30px 40px 0;
  }

  .el-dialog__body {
    padding: 0;
    margin-top: -15px;
  }
}
</style>
