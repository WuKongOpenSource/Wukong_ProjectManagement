<template>
  <div class="main">
    <flexbox class="main-header" justify="space-between">
      <div class="main-header__left">
        <span class="title">{{ title }}</span>
      </div>
      <div class="main-header__right">
        <el-button
          type="primary"
          @click="createClick">新建审批</el-button>
        <el-button
          @click="exportClick">导出</el-button>
      </div>
    </flexbox>

    <flexbox class="main-header is-filter-header" justify="space-between">
      <div class="main-header__left">
        <el-select
          v-model="selectId"
          class="tabs-head-select"
          mode="no-border"
          @change="refreshList">
          <el-option
            v-for="(item, index) in selectList"
            :key="index"
            :label="item.label"
            :value="item.command" />
        </el-select>

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
      :class="WKConfig.tableStyle.class"
      :stripe="WKConfig.tableStyle.stripe"
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
        <template slot-scope="{ row, column }">
          <span
            v-if="item.field === 'examineStatus'"
            :style="{
              backgroundColor: getStatusColor(row.examineStatus)
            }"
            class="status-mark" />
          {{ fieldFormatter(row, column, row[column.property], item) }}
        </template>
      </el-table-column>
      <wk-empty
        slot="empty"
        :props="{
          buttonTitle: '新建审批',
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

    <!-- 创建 -->
    <examine-category-select
      :show="showCategorySelect"
      @select="selcetExamineCategory"
      @close="showCategorySelect=false" />
    <examine-create-view
      v-if="isCreate"
      :category-id="createInfo.categoryId"
      :type="createInfo.type"
      :category-title="createInfo.categoryTitle"
      :action="createAction"
      @save-success="refreshList"
      @hiden-view="createHideView" />

    <!-- 列表操作 -->
    <examine-detail
      v-if="showDview"
      :id="rowID"
      :detail-index="detailIndex"
      :no-listener-class="['el-table__body']"
      class="d-view"
      @hide-view="showDview=false"
      @handle="getList" />

    <c-r-m-all-detail
      :id="relatedID"
      :visible.sync="showRelatedDetail"
      :crm-type="relatedCRMType"
      :no-listener-class="['el-table__body']"
      @handle="getList" />

    <examine-handle
      :id="rowID"
      :show="showExamineHandle"
      :record-id="rowData.examineRecordId"
      :detail="rowData"
      :status="examineStatus"
      examine-type="oa_examine"
      @close="showExamineHandle = false"
      @save="getList" />
  </div>
</template>

<script>
import {
  oaExamineMyCreateIndexAPI,
  oaExamineDeleteAPI,
  oaExamineExportAPI
} from '@/api/oa/examine'
import {
  examinesQueryPartListAPI,
  examineWaitingQueryOaExamineListAPI
} from '@/api/examine'
import ExamineMixin from './components/ExamineMixin'
import TableMixin from '@/mixins/Table'
import CheckStatus from '@/mixins/CheckStatusMixin'

import ExamineCategorySelect from '@/views/oa/examine/components/ExamineCategorySelect'
import ExamineCreateView from '@/views/oa/examine//components/ExamineCreateView'

import ExamineDetail from '@/views/oa/examine/components/ExamineDetail'
import CRMAllDetail from '@/views/crm/components/CRMAllDetail'
import ExamineHandle from '@/components/Examine/ExamineHandle'

import { downloadExcelWithResData } from '@/utils/index'
import { ExamineHeadsModel } from '../model'

export default {
  /** 审批 */
  name: 'Index',
  components: {
    ExamineCategorySelect,
    ExamineCreateView,

    ExamineDetail,
    CRMAllDetail,
    ExamineHandle
  },
  mixins: [ExamineMixin, TableMixin, CheckStatus],

  beforeRouteUpdate(to, from, next) {
    this.examineType = to.params.type
    this.selectId = ''
    this.tabsSelectValue = ''
    this.tabsSelectValue = this.examineType == 'my' ? '0' : '1'

    this.refreshList()
    next()
  },
  props: {},
  data() {
    return {
      tabsSelectValue: '',
      selectList: [],
      examineType: '',

      // 表头
      tableHeaderFields: ExamineHeadsModel,
      list: [],
      loading: false,
      // 空是全部
      selectId: '',

      // 新建
      showCategorySelect: false,
      isCreate: false, // 是创建
      createAction: { type: 'save' },
      createInfo: {}, // 创建所需要的id 标题名信息

      // 控制详情展示
      // 目前选中单元格(从cell 回调中 获取)
      detailIndex: 0,
      rowID: '',
      rowData: {}, // 行全部信息
      showDview: false,

      // 相关详情的查看
      relatedID: '',
      relatedCRMType: '',
      showRelatedDetail: false,

      // 撤回操作
      showExamineHandle: false,
      recordId: '',
      // 审核操作 1 审核通过 2 审核拒绝 4 已撤回
      examineStatus: 1
    }
  },
  computed: {
    title() {
      return this.examineType === 'my' ? '我发出的审批' : '待我审批'
    },

    tabs() {
      if (this.examineType == 'my') {
        // status
        return [
          {
            label: '全部',
            name: 'all'
          },
          {
            label: '待审批',
            name: '0'
          },
          {
            label: '审批通过',
            name: '1'
          },
          {
            label: '审批拒绝',
            name: '2'
          }
        ]
      } else if (this.examineType == 'wait') {
        // status
        return [
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
        ]
      }
      return []
    }
  },
  watch: {},
  mounted() {
    this.examineType = this.$route.params.type
    this.getSelectList()
    this.tabsSelectValue = this.examineType == 'my' ? '0' : '1'
    this.refreshList()
  },

  beforeDestroy() {},
  methods: {
    /**
     * 通过回调控制class
     */
    cellClassName({ row, column, rowIndex, columnIndex }) {
      if (column.property === 'content') {
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
      this.detailIndex = this.getObjIndex(this.list, 'examineId', row.examineId)
      this.showRelatedDetail = false
      this.rowID = row.examineId
      this.showDview = true
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
        limit: this.pageSize,
        categoryId: this.selectId
      }

      let request = null
      const status = this.tabsSelectValue == 'all' ? '' : this.tabsSelectValue
      if (this.examineType == 'my') {
        params.status = status
        request = oaExamineMyCreateIndexAPI
      } else if (this.examineType == 'wait') {
        params.status = status
        request = examineWaitingQueryOaExamineListAPI
      }

      request(params)
        .then(res => {
          this.loading = false

          const status = this.tabsSelectValue == 'all' ? '' : this.tabsSelectValue
          let pass = false
          if (this.examineType == 'my' && params.status == status) {
            pass = true
          } else if (this.examineType == 'wait' && params.status == status) {
            pass = true
          }

          if (pass) {
            const resData = res.data || {}
            this.list = resData.list
            this.total = resData.totalRow

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
      if (column.property === 'createUser') {
        return row.createUser ? row.createUser.realname : ''
      } else if (column.property === 'examineStatus') {
        return this.getStatusName(row.examineStatus)
      }
      return cellValue
    },

    /**
     * 获取审批类型
     */
    getSelectList() {
      examinesQueryPartListAPI({
        label: 0
      })
        .then(res => {
          const resData = res.data || {}
          const list = resData.list || []
          this.selectList = list.map(item => {
            const iconItem = this.getCategoryIcon(item.examineIcon)
            item.categoryTitle = item.examineName
            iconItem.label = item.examineName
            iconItem.command = item.examineId
            return iconItem
          })
          this.selectList.unshift({
            icon: 'wk wk-approve',
            color: '#9376FF',
            command: '',
            label: '全部审批'
          })
        })
        .catch(() => {
          this.loading = false
        })
    },

    /**
     * 中间tabs改变
     */
    tabsChange(type) {
      this.tabsSelectValue = type
      this.refreshList()
    },

    /**
     * 单选
     */
    handleSelectClick(item) {
      this.selectId = item.command
      this.refreshList()
    },

    /**
     * cell 操作
     */
    cellHandle(type, data, index) {
      this.detailIndex = index

      if (type == 'detail') {
        this.showRelatedDetail = false
        this.rowID = data.examineId
        this.showDview = true
      } else if (type == 'relate-detail') {
        this.showDview = false
        this.relatedID = data.id
        this.relatedCRMType = data.type
        this.showRelatedDetail = true
      } else if (type == 'edit') {
        data.title = data.categoryName
        this.createInfo = data
        this.createAction = { type: 'update', id: data.examineId, data: data }
        this.isCreate = true
      } else if (type == 'delete') {
        this.$confirm('确定删除?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
          .then(() => {
            oaExamineDeleteAPI({
              examineId: data.examineId
            }).then(res => {
              this.list.splice(index, 1)
              this.updateTableHeight()
              this.$message({
                type: 'success',
                message: '删除成功!'
              })
            })
          })
          .catch(() => {
            this.$message({
              type: 'info',
              message: '已取消删除'
            })
          })
      } else if (type == 'reject' || type == 'pass' || type == 'withdraw') {
        // 审核操作 1 审核通过 2 审核拒绝 4 已撤回
        this.rowID = data.examineId
        this.examineStatus = {
          pass: 1,
          reject: 2,
          withdraw: 4
        }[type]
        this.rowData = data
        this.showExamineHandle = true
      }
    },

    /**
     * 新建 审批类型选择
     */
    selcetExamineCategory(item) {
      item.type = item.oaType
      this.createInfo = item
      this.createAction = { type: 'save' }
      this.isCreate = true
    },

    /**
     * 创建
     */
    createClick() {
      this.showCategorySelect = true
    },

    /**
     * 创建关闭
     */
    createHideView() {
      this.isCreate = false
    },

    /**
     * 审批导出
     */
    exportClick() {
      if (this.selectId === '') {
        this.$message.error('请选择一种审批类型导出')
        return
      }

      this.loading = true
      const params = {
        categoryId: this.selectId
      }

      const status = this.tabsSelectValue == 'all' ? '' : this.tabsSelectValue
      if (this.examineType == 'my') {
        params.status = status
      } else if (this.examineType == 'wait') {
        params.status = status
      }

      params.queryType = this.examineType == 'my' ? 1 : 2 // 1我发出的  2待我审批的
      oaExamineExportAPI(params)
        .then(res => {
          downloadExcelWithResData(res)
          this.loading = false
        })
        .catch(() => {
          this.loading = false
        })
    }
  }
}
</script>

<style lang="scss" scoped>
@import "../style/index.scss";

.tabs-head-select {
  margin-right: $--interval-base;
}

.dropdown-icon {
  padding: 3px;
  margin-right: 5px;
  font-size: 12px;
  color: white;
  border-radius: $--border-radius-base;
}

.status-mark {
  display: inline-block;
  width: 8px;
  height: 8px;
  border-radius: 4px;
}
</style>
