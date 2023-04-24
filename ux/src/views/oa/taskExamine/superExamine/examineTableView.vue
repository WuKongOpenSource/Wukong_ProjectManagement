<template>
  <div class="main">
    <wk-page-header
      :title="title"
      :dropdowns="getDefaultHeaderHandes"
      @command="pageHeaderCommand" />

    <wk-table-header
      ref="wkTableHeader"
      class="main-header is-filter-header"
      :search.sync="search"
      :props="wkHeaderProps"
      :filter-header-props="wkHeaderProps.filterHeaderProps"
      :filter-form-props="wkHeaderProps.filterFormProps"
      :fields="tableField"
      @event-change="tableHeaderHandle"
      @filter-change="handleFilter">
      <!-- eslint-disable -->
      <template slot="custom" slot-scope="scope">
        <div class="custom-scene">
          <span  v-if="examineType != 'draft' && (props && props.examineType != 'report')">显示：</span>
          <el-button
            v-for="(item, index) in tabs"
            :key="index"
            :type="item.name === tabsSelectValue ? 'selected' : null"
            @click="tabsChange(item.name)">{{ item.label }}</el-button>
        </div>
      </template>
    </wk-table-header>

    <el-table
      :key="examineType + tabsSelectValue"
      v-loading="loading"
      :data="list"
      :row-height="rowHeight"
      :height="tableHeight"
      :cell-class-name="cellClassName"
      :class="WKConfig.tableStyle.class"
      :stripe="WKConfig.tableStyle.stripe"
      use-virtual
      row-key="examineId"
      highlight-current-row
      style="width: 100%;"
      @row-click="handleRowClick">
      <el-table-column
        label="序号"
        width="50"
        fixed="left"
        show-overflow-tooltip>
        <template slot-scope="{row}">
          {{ row.index }}
        </template>
      </el-table-column>
      <el-table-column
        v-if="examineType == 'track' || examineType == 'archive'"
        fixed="left"
        width="60">
        <template
          slot="header"
          slot-scope="slot">
          <i class="el-icon-star-off focus-icon is-disabled" />
        </template>
        <template slot-scope="scope">
          <el-tooltip :content="scope.row.followStatus == 0 ? '添加关注' : '取消关注'" effect="dark" placement="top">
            <i
              v-if="scope.row.followStatus == 0"
              class="el-icon-star-off focus-icon"
              @click="toggleStar(scope.row)" />
            <i
              v-else
              class="wk wk-focus-on focus-icon active"
              @click="toggleStar(scope.row)" />
          </el-tooltip>
        </template>
      </el-table-column>
      <el-table-column
        v-for="(item, index) in tableField"
        :key="index"
        :formatter="fieldFormatter"
        :prop="item.prop"
        :label="item.name"
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

    <detail
      v-if="detailShow"
      :page-list="list"
      :id="examineId"
      :examine-type="examineType"
      :row-index.sync="rowIndex"
      @hide-view="detailShow = false"
      @success="getList()"
    />
  </div>
</template>

<script>
import {
  superExamineCopyMeAPI, // 待办(抄送给我)
  superExamineTodoMeAPI, // 待办(待我审批)
  superExamineEndMeAPI, // 归档列表(我的申请)
  superExamineEndAuditAPI, // 归档列表(经我审批)
  superExamineEndFollowAPI, // 归档列表(我的关注)
  superExamineEndTovoidAPI, // 归档列表(作废)
  superExamineEndCopyAPI, // 归档列表(抄送给我的)
  superExamineDraftAPI, // 草稿
  superExamineTrackMeAPI, // 跟踪(我申请的)
  superExamineTrackDoAPI, // 跟踪(经我审批)
  superExamineTrackCopyAPI, // 跟踪(抄送给我的)
  changeFollowAPI, // 关注状态
  getExamineReportSubListAPI, // 报表列表

  exportExamineDoMeInfoAPI, // 代办待我审批导出
  exportExamineCopyMeInfoAPI, // 代办抄送给我导出
  exportExamineTrackMeInfoAPI, // 跟踪我的申请导出
  exportExamineTrackDoInfoAPI, // 跟踪经我审批导出
  exportExamineTrackCopyInfoAPI, // 跟踪抄送我的导出
  exportExamineEenMeInfoAPI, // 归档我申请的导出
  exportExamineEenAuditInfoAPI, // 归档经我审批导出
  exportExamineEenFollowInfoAPI, // 归档我的关注导出
  exportExamineEenTovoIdInfoAPI, // 归档作废导出
  exportExamineEenCopyInfoAPI, // 归档抄送我的导出

  getSuperExamineExportFieldAPI
} from '@/api/oa/superExamine'

import {
  examineSuperExaminesQueryExamineGroupAPI // 审批类型
} from '@/api/examine/superExamine'

import WkTableHeader from '@/components/Page/WkTableHeader'
import WkPageHeader from '@/components/Page/WkPageHeader'
import Detail from './Detail.vue'

import TableMixin from '@/mixins/Table'
import { mapGetters } from 'vuex'
import { isEmpty } from '@/utils/types'
import { objDeepCopy } from '@/utils'

export default {
  /** 审批表格视图 */
  name: 'ExamineTableView',
  components: {
    WkTableHeader,
    WkPageHeader,
    Detail
  },
  mixins: [TableMixin],
  beforeRouteUpdate(to, from, next) {
    this.examineType = to.params.type
    this.tabsSelectValue = '0'
    this.filterObj = []
    this.refreshList()
    next()
  },
  props: {
    // 是页面还是组件
    isPage: {
      type: Boolean,
      default: true
    },
    props: {
      type: Object,
      default() {
        return {}
      }
    }
  },
  data() {
    return {
      loading: false,
      tabsSelectValue: '0',
      selectList: [],
      examineType: '',

      titleObj: {
        upcoming: '待办',
        track: '跟踪',
        archive: '归档',
        draft: '草稿'
      },

      examineId: '', // 审核id

      search: '', // 搜索

      // 头部设置
      wkHeaderProps: {
        showFilterView: true,
        filterHeaderProps: {
          searchPlaceholder: '请输入审批内容',
          tabSetShow: false,
          showSearch: true
        },
        filterFormProps: {
          showExport: false,
          showSaveScene: false
        }
      },

      // 表头
      tableHeaderFields: [],
      list: [],
      rowIndex: 0,

      filterObj: [], // 高级筛选

      detailShow: false, // 详情展示

      options: [], // 审批类型
      categoryId: '', // 审批类型id
      params: {}
    }
  },
  computed: {
    ...mapGetters(['userInfo']),
    title() {
      return this.titleObj[this.examineType]
    },

    tabs() {
      if (this.examineType == 'upcoming') { // 待办
        return [
          {
            label: '待我审批',
            name: '0'
          }, {
            label: '抄送给我',
            name: '1'
          }
        ]
      } else if (this.examineType == 'track') { // 跟踪
        return [
          {
            label: '我的申请',
            name: '0'
          }, {
            label: '经我审批',
            name: '1'
          }, {
            label: '抄送给我的',
            name: '2'
          }
        ]
      } else if (this.examineType == 'archive') { // 归档
        return [
          {
            label: '我的申请',
            name: '0'
          }, {
            label: '经我审批',
            name: '1'
          }, {
            label: '我的关注',
            name: '2'
          }, {
            label: '作废',
            name: '3'
          }, {
            label: '抄送给我的',
            name: '4'
          }
        ]
      }
      return []
    },

    tableField() {
      if (this.examineType == 'upcoming') { // 待办
        return [
          { name: '审批内容', fieldName: 'content', prop: 'content', formType: 'text' },
          { name: '提交人', fieldName: 'createUserId', prop: 'submitUser', formType: 'user' },
          { name: '提交部门', fieldName: 'submitDeptId', prop: 'submitDeptName', formType: 'structure' },
          { name: '审批类型', fieldName: 'examineName', prop: 'categoryTitle', formType: 'checkStatus', setting: this.options },
          { name: '当前处理人', fieldName: 'examineUserId', prop: 'currentExamineUser', formType: 'user' }
        ]
      } else if (this.examineType == 'track' || (this.props && this.props.examineType == 'report')) { // 跟踪
        return [
          { name: '审批内容', fieldName: 'content', prop: 'content', formType: 'text' },
          { name: '提交人', fieldName: 'createUserId', prop: 'submitUser', formType: 'user' },
          { name: '提交时间', fieldName: 'submitTime', prop: 'createTime', formType: 'datetime' },
          { name: '提交部门', fieldName: 'submitDeptId', prop: 'submitDeptName', formType: 'structure' },
          { name: '审批类型', fieldName: 'examineName', prop: 'categoryTitle', formType: 'checkStatus', setting: this.options },
          { name: '当前处理人', fieldName: 'examineUserId', prop: 'currentExamineUser', formType: 'user' }
        ]
      } else if (this.examineType == 'archive') { // 归档
        return [
          { name: '审批内容', fieldName: 'content', prop: 'content', formType: 'text' },
          { name: '提交人', fieldName: 'createUserId', prop: 'submitUser', formType: 'user' },
          { name: '提交时间', fieldName: 'createTime', prop: 'createTime', formType: 'datetime' },
          { name: '审批类型', fieldName: 'examineName', prop: 'categoryTitle', formType: 'checkStatus', setting: this.options },
          { name: '提交部门', fieldName: 'submitDeptId', prop: 'submitDeptName', formType: 'structure' }
        ]
      } else if (this.examineType == 'draft') { // 草稿
        return [
          { name: '审批内容', fieldName: 'content', prop: 'content', formType: 'text' },
          { name: '保存时间', fieldName: 'createTime', prop: 'createTime', formType: 'datetime' },
          { name: '提交部门', fieldName: 'submitDeptId', prop: 'submitDeptName', formType: 'structure' },
          { name: '审批类型', fieldName: 'examineName', prop: 'categoryTitle', formType: 'checkStatus', setting: this.options }
        ]
      }
      return []
    },

    // 表头操作
    getDefaultHeaderHandes() {
      if (this.examineType != 'draft') {
        return [{ command: 'out', name: '导出', icon: 'wk wk-export' }]
      } else {
        return []
      }
    }
  },
  watch: {
    examineType: {
      handler() {
        this.$nextTick(() => {
          this.$refs.wkTableHeader.filterObj = { form: [], obj: [] }
        })
      },
      immediate: true
    },
    rowIndex(val) {
      this.examineId = this.list[val].examineId
    }
  },
  created() {
    this.rowHeight = 50
    if (!this.isPage) {
      this.otherTableHeight = 360
    }
    this.getExamineType()
  },
  mounted() {
    this.examineType = this.$route.params.type
    this.refreshList()
    window.onresize = () => {
      this.updateTableHeight()
    }
  },

  methods: {
    /**
     * 获取列表
     */
    getList() {
      this.loading = true
      let request
      let params = {
        page: this.currentPage,
        limit: this.pageSize,
        search: this.search
      }
      if (this.examineType == 'upcoming' && this.tabsSelectValue == '0') { // 待办(待我审批)
        request = superExamineTodoMeAPI
      } else if (this.examineType == 'upcoming' && this.tabsSelectValue == '1') { // 待办(抄送给我)
        request = superExamineCopyMeAPI
      } else if (this.examineType == 'archive' && this.tabsSelectValue == '0') { // 归档列表(我的申请)
        request = superExamineEndMeAPI
      } else if (this.examineType == 'archive' && this.tabsSelectValue == '1') { // 归档列表(经我审批)
        request = superExamineEndAuditAPI
      } else if (this.examineType == 'archive' && this.tabsSelectValue == '2') { // 归档列表(我的关注)
        request = superExamineEndFollowAPI
      } else if (this.examineType == 'archive' && this.tabsSelectValue == '3') { // 归档列表(作废)
        request = superExamineEndTovoidAPI
      } else if (this.examineType == 'archive' && this.tabsSelectValue == '4') { // 归档列表(抄送给我的)
        request = superExamineEndCopyAPI
      } else if (this.examineType == 'draft') { // 草稿
        request = superExamineDraftAPI
      } else if (this.examineType == 'track' && this.tabsSelectValue == '0') { // 跟踪(我申请的)
        request = superExamineTrackMeAPI
      } else if (this.examineType == 'track' && this.tabsSelectValue == '1') { // 跟踪(经我审批)
        request = superExamineTrackDoAPI
      } else if (this.examineType == 'track' && this.tabsSelectValue == '2') { // 跟踪(抄送给我的)
        request = superExamineTrackCopyAPI
      } else if (this.props && this.props.examineType == 'report') { // 报表列表
        request = getExamineReportSubListAPI
        params = {
          page: this.currentPage,
          limit: this.pageSize,
          searchKey: this.search,
          ...this.props.params
        }
      }

      // if (this.filterObj.length) params.searchList = this.filterObj
      if ((this.examineType == 'upcoming' || this.examineType == 'track' || this.examineType == 'archive') && this.filterObj.length) {
        // 高级筛选选择审批类型修改传参方式
        const filterObj = objDeepCopy(this.filterObj)
        const searchList = filterObj.filter(item => item.name != 'examineName')

        const categoryNameList = filterObj.filter(item => item.name == 'examineName')

        if (searchList.length) {
          params.searchBO = {
            searchList
          }
        }

        if (categoryNameList.length) {
          const name = categoryNameList[0].values.length ? categoryNameList[0].values[0] : []
          if (name.length) {
            this.categoryId = this.options.filter(item => item.value == name)[0].examineId
            if (categoryNameList.length && categoryNameList[0].type == 2) {
              params.searchBO = {
                searchList: this.filterObj
              }
            } else {
              params.categoryId = this.categoryId
            }
          }
        }
      } else if (this.examineType == 'draft' && this.filterObj.length) {
        params.searchList = this.filterObj
      }

      this.params = objDeepCopy(params)
      request(params)
        .then(res => {
          this.loading = false
          const resData = res.data
          const list = resData.list || []
          list.forEach((item, index) => {
            item.index = index + (this.currentPage - 1) * this.pageSize + 1
          })
          this.list = list
          this.total = resData.totalRow
          this.updateTableHeight()
        })
        .catch(() => {
          this.loading = false
        })
    },

    /**
     * 通过回调控制class
     */
    cellClassName({ row, column, rowIndex, columnIndex }) {
      if (column.property === 'content' || column.property === 'trialContent' || column.property === 'fileName') {
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
      if (column.property === 'content' || column.property === 'trialContent' || column.property === 'fileName') {
        this.detailShow = true
        this.examineId = row.examineId
        this.list.forEach((item, index) => {
          if (item.examineId == row.examineId) {
            this.rowIndex = index
          }
        })
      }
    },

    /**
     * @description: 切换关注状态
     * @param {*} data
     * @return {*}
     */
    toggleStar(data) {
      const msg = data.followStatus ? '取消关注成功' : '关注成功'
      const params = {
        followUserId: this.userInfo.userId,
        recordId: data.examineRecordId
      }
      changeFollowAPI(params)
        .then(res => {
          this.$message.success(msg)
          this.refreshList()
        })
    },

    /**
     * 搜索
     */
    tableHeaderHandle(type) {
      if (type == 'search') {
        this.refreshList()
      }
    },

    /**
     * 筛选操作
     * @param {*} data 高级筛选数据
     */
    handleFilter(data) {
      this.filterObj = data
      this.refreshList()
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
     * 格式化展示
     */
    fieldFormatter(row, column, cellValue, field) {
      if (column.property == 'submitUser') {
        return isEmpty(row['createUser']['realname']) ? '--' : row['createUser']['realname']
      }
      return isEmpty(row[column.property]) ? '--' : row[column.property]
    },

    /**
     * 中间tabs改变
     */
    tabsChange(type) {
      this.tabsSelectValue = type
      this.refreshList()
    },

    /**
     * 获取审批类型
     */
    getExamineType() {
      examineSuperExaminesQueryExamineGroupAPI({
        groupType: 0
      })
        .then(res => {
          const data = res.data || []
          console.log(data)
          const options = []
          data.forEach(item => {
            item?.examineVOList.forEach(sItem => {
              options.push({ label: sItem.examineName, value: sItem.examineName, examineId: sItem.examineId })
            })
          })
          this.options = options
        })
    },

    /**
     * @description: 头部更多按钮事件
     * @param {*}
     * @return {*}
     */
    pageHeaderCommand(command) {
      if (command == 'out') {
        console.log(this.examineType + this.tabsSelectValue)

        const filter = this.filterObj.filter(item => item.name == 'examineName')
        if (!filter.length) {
          this.$message.error('请在高级筛选中先选择审批类型')
          return
        } else {
          const { type } = filter[0]
          if (type == 2) {
            this.$message.error('请选择单个审批类型')
            return
          }
        }

        this.exportInfos()
      }
    },

    exportInfos() {
      const apiObj = {
        'upcoming0': exportExamineDoMeInfoAPI,
        'upcoming1': exportExamineCopyMeInfoAPI,
        'track0': exportExamineTrackMeInfoAPI,
        'track1': exportExamineTrackDoInfoAPI,
        'track2': exportExamineTrackCopyInfoAPI,
        'archive0': exportExamineEenMeInfoAPI,
        'archive1': exportExamineEenAuditInfoAPI,
        'archive2': exportExamineEenFollowInfoAPI,
        'archive3': exportExamineEenTovoIdInfoAPI,
        'archive4': exportExamineEenCopyInfoAPI
      }

      this.$wkExport.export('examine', {
        fieldParams: { categoryId: this.params.categoryId },
        fieldRequest: getSuperExamineExportFieldAPI,
        request: apiObj[this.examineType + this.tabsSelectValue],
        params: { categoryId: this.params.categoryId },
        fieldList: [
          { label: '相关客户', prop: 'customerNames', sortId: 'customerNames' },
          { label: '相关商机', prop: 'businessNames', sortId: 'businessNames' },
          { label: '相关联系人', prop: 'contactsNames', sortId: 'contactsNames' },
          { label: '相关合同', prop: 'contractNames', sortId: 'contractNames' },
          { label: '相关联回款', prop: 'receivablesNames', sortId: 'receivablesNames' },
          { label: '当前流程状态', prop: 'examineStatus', sortId: 'examineStatus' }
        ],
        selectionList: ['customerNames', 'businessNames', 'contactsNames', 'contractNames', 'receivablesNames', 'examineStatus']
      })
    }
  }
}
</script>

<style lang="scss" scoped>
@import "../style/index.scss";

// 关注
.focus-icon {
  font-size: 18px;
  font-weight: bold;
  color: $--color-n40;
  cursor: pointer;

  .wk-focus-on {
    font-size: 13px;
  }

  &.active {
    color: #fac23d;
  }

  &.is-disabled {
    color: $--color-n20;
    cursor: not-allowed;
  }
}

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

.main-header.is-filter-header {
  /deep/ .vux-flexbox.filter-wrap.vux-flex-row {
    div {
      flex-shrink: 0;
    }
  }

  .custom-scene {
    span {
      margin: 0 8px;
    }
  }

  .custom-filtrate {
    span {
      margin: 0 8px;
    }
  }
}
</style>
