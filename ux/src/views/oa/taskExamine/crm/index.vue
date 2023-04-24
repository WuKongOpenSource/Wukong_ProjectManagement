<template>
  <div class="main">
    <flexbox class="main-header" justify="space-between">
      <div class="main-header__left">
        <span class="title">{{ title }}</span>
      </div>
    </flexbox>

    <flexbox class="main-header is-filter-header" justify="space-between">
      <div class="main-header__left">
        <span class="tabs">
          <span class="tabs-label">显示:</span>
          <el-button
            v-for="(item, index) in tabs"
            :key="index"
            :type="item.name === tabsSelectValue ? 'selected' : null"
            @click="tabsChange(item.name)">{{ item.label }}</el-button>
        </span>
      </div>
    </flexbox>

    <el-table
      :key="`${tableHeight || '0'}-${pageSize}`"
      v-loading="loading"
      :row-height="rowHeight"
      :data="list"
      :height="tableHeight"
      :cell-class-name="cellClassName"
      use-virtual
      row-key="taskId"
      highlight-current-row
      style="width: 100%;"
      @row-click="handleRowClick"
      @selection-change="handleSelectionChange">
      <!-- <el-table-column
        show-overflow-tooltip
        reserve-selection
        type="selection"
        align="center"
        width="55"/> -->
      <el-table-column
        v-for="(item, index) in tableHeaderFields"
        :key="index"
        :fixed="index==0"
        :prop="item.field"
        :label="item.name"
        :min-width="item.width"
        show-overflow-tooltip>
        <template slot-scope="{ row, column, $index }">
          <template>
            {{ fieldFormatter(row, column, row[column.property], item) }}
          </template>
        </template>
      </el-table-column>
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

    <!-- 查看详情 -->
    <c-r-m-all-detail
      :id="rowID"
      :visible.sync="showCrmDetail"
      :crm-type="detailCrmType"
      :no-listener-class="['relate-cell', 'examine-content']"
      @handle="getList" />

    <!-- 审批操作 -->
    <examine-handle
      :id="rowID"
      :show="showExamineHandle"
      :record-id="rowData.examineRecordId"
      :detail="rowData"
      :status="examineStatus"
      :examine-type="`crm_${crmType}`"
      @close="showExamineHandle = false"
      @save="getList" />
  </div>
</template>

<script>
import {
  examineWaitingQueryCrmExamineListAPI
} from '@/api/examine'

import CRMAllDetail from '@/views/crm/components/CRMAllDetail'
import ExamineHandle from '@/components/Examine/ExamineHandle'

import TableMixin from '@/mixins/Table'
import CheckStatus from '@/mixins/CheckStatusMixin'

export default {
  // 回款和合同审批
  name: 'Index',
  components: {
    CRMAllDetail,
    ExamineHandle
  },
  mixins: [TableMixin, CheckStatus],

  beforeRouteUpdate(to, from, next) {
    this.crmType = to.params.type
    this.tabsSelectValue = '1'

    this.refreshList()
    next()
  },
  props: {},
  data() {
    return {
      crmType: '',
      // 待我审批的
      tabsSelectValue: '1',
      tabs: [
        {
          label: '全部',
          name: 'all'
        },
        {
          label: '待我审批的',
          name: '1'
        },
        {
          label: '我已审批的',
          name: '2'
        }
      ],
      // 表头
      tableHeaderFields: [{
        name: '审批内容',
        field: 'category',
        width: 120,
        formType: 'text'
      }, {
        name: '创建人',
        field: 'createUser',
        width: 120,
        formType: 'user'
      }, {
        name: '状态',
        field: 'examineStatus',
        width: 80,
        formType: 'examineStatus'
      }, {
        name: '创建日期 ',
        field: 'createTime',
        width: 80
      }],

      list: [],
      loading: false,

      // 控制详情展示
      detailIndex: 0,

      // 相关详情的查看
      rowID: '',
      rowData: {}, // 行全部信息
      detailCrmType: '',
      showCrmDetail: false,

      // 撤回操作
      showExamineHandle: false,
      recordId: '',
      // 审核操作 1 审核通过 2 审核拒绝 4 已撤回
      examineStatus: 1
    }
  },
  computed: {
    title() {
      return {
        contract: '待我审批（合同）',
        receivables: '待我审批（回款）',
        invoice: '待我审批（发票）'
      }[this.crmType]
    }
  },
  watch: {},
  mounted() {
    this.crmType = this.$route.params.type

    this.refreshList()
  },

  beforeDestroy() {},
  methods: {
    /**
     * 通过回调控制class
     */
    cellClassName({ row, column, rowIndex, columnIndex }) {
      if (column.property === 'category') {
        return 'can-visit--underline'
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
      this.detailIndex = this.getObjIndex(this.list, 'categoryId', row.categoryId)
      this.detailCrmType = this.crmType
      this.rowID = row.categoryId
      this.showCrmDetail = true
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
     * 刷新列表
     */
    refreshList() {
      this.currentPage = 1
      this.list = []
      this.getList()
    },

    /**
     * 获取数据列表
     */
    getList() {
      this.loading = true
      const params = {
        page: this.currentPage,
        limit: 15,
        status: this.tabsSelectValue == 'all' ? '' : this.tabsSelectValue
      }

      // 1合同 2 回款 3 发票
      if (this.crmType == 'contract') {
        params.label = 1
      } else if (this.crmType == 'receivables') {
        params.label = 2
      } else if (this.crmType == 'invoice') {
        params.label = 3
      }

      examineWaitingQueryCrmExamineListAPI(params)
        .then(res => {
          this.loading = false
          const status = this.tabsSelectValue == 'all' ? '' : this.tabsSelectValue

          if (params.status == status) {
            const resData = res.data || {}
            this.list = resData.list
            this.total = resData.totalRow
            this.updateTableHeight()
          } else {
            this.refreshList()
          }
        })
        .catch(() => {
          this.loading = false
        })
    },

    /**
     * 格式化展示
     */
    fieldFormatter(row, column, cellValue, field) {
      if (column.property === 'createUser') {
        return row.createUser ? row.createUser.realname : ''
      } else if (column.property === 'examineStatus') {
        return this.getStatusName(row.examineStatus)
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
     * cell 操作
     */
    cellHandle(type, data, index) {
      this.detailIndex = index

      if (type == 'detail') {
        this.detailCrmType = this.crmType
        this.rowID = data.categoryId
        this.showCrmDetail = true
      } else if (type == 'relate-detail') {
        this.showDview = false
        this.rowID = data.id
        this.detailCrmType = data.type
        this.showCrmDetail = true
      } else if (type == 'reject' || type == 'pass' || type == 'withdraw') {
        // 审核操作 1 审核通过 2 审核拒绝 4 已撤回
        this.rowID = data.categoryId
        this.examineStatus = {
          pass: 1,
          reject: 2,
          withdraw: 4
        }[type]
        this.rowData = data
        this.showExamineHandle = true
      }
    }
  }
}
</script>

<style lang="scss" scoped>
@import "../style/index.scss";
</style>
