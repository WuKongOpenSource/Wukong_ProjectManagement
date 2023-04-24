<template>
  <div class="iteration-item">
    <div class="filter-wrapper">
      <wk-table-header
        :search.sync="search"
        :tabs="btnList"
        :props="tableHeaderProps.props"
        :filter-header-props="tableHeaderProps.filterHeaderProps"
        @tabs-change="sceneSelect"
        @event-change="tableHeaderHandle">
        <el-button
          type="subtle"
          class="filter-button"
          icon="wk wk-screening"
          @click="filterShow=true">
          高级筛选
        </el-button>
        <el-select
          v-model="layout"
          placeholder="请选择"
          style="margin-left: 12px;"
          @change="selectChanged">
          <el-option-group>
            <el-option
              v-for="item in showList"
              :key="item.value"
              :label="item.label"
              :value="item.value">
              {{ item.label }}
            </el-option>
          </el-option-group>
          <el-option-group
            v-if="showBoardSet">
            <el-option
              v-for="item in boardSetList"
              :key="item.value"
              :label="item.label"
              :value="item.value">
              <i :class="item.icon" style="margin-right: 5px;" />{{ item.label }}
            </el-option>
          </el-option-group>
        </el-select>
        <template v-if="createAuth">
          <el-popover
            placement="bottom"
            width="160"
            trigger="click"
            popper-class="no-padding-popover">
            <div class="drop-wrap is-small">
              <div class="drop-wrap__section">
                <div class="drop-wrap__content">
                  <flexbox
                    v-for="(item, index) in quickAddList"
                    :key="index"
                    class="drop-cell is-small"
                    @click.native="handleCreate(item)">
                    <div class="drop-cell__bd">{{ item.label }}</div>
                  </flexbox>
                </div>
              </div>
            </div>
            <el-button
              slot="reference"
              class="common-create-btn"
              type="primary">新建</el-button>
          </el-popover>
        </template>
      </wk-table-header>
    </div>
    <!-- <el-table
      v-loading="loading"
      :data="list"
      :row-height="rowHeight"
      :row-key="`taskId`"
      use-virtual
      :height="tableHeight"
      :cell-class-name="cellClassName"
      highlight-current-row
      style="width: 100%;"
      class="crm-table"
      @row-click="handleRowClick">
      <el-table-column
        v-for="(item,index) in fieldList"
        :key="index"
        :prop="item.prop"
        :label="item.label"
        :width="item.width"
        :fixed="item.isLock === 1"
        :min-width="item.minWidth"
        :class-name="item.width>60 ? 'column' : '' "
        show-overflow-tooltip>
        <template slot-scope="{ row }">
          <span v-if="item.prop == 'num'">#{{ row[item.prop] }}</span>
          <span v-else-if="item.prop == 'name'" class="icon-name">
            <img class="item-img" :src="getIconClass(row)" alt="">
            <span class="item-title can-visit--underline">{{ row[item.prop] }}</span>
          </span>
          <status-tag v-else-if="item.prop == 'status'" style="margin-right: 8px;" :type="row.status" />
          <span v-else-if="item.prop == 'priority'" class="icon-name">
            <img class="item-img" :src="getPriorityPic(row[item.prop])" alt="">
            <span>{{ getPriority(row[item.prop]) }}</span>
          </span>
          <el-tag
            v-else-if="item.prop == 'stopTime' && row.stopTime"
            disable-transitions
            :class="[row.status == 3 ? 'is-common' : getOverTimeStatus(row.stopTime)]"
            class="project-tag">
            {{ formatTime(row.stopTime) + '截止' }}
          </el-tag>
          <span v-else>{{ row[item.prop] }}</span>
        </template>
      </el-table-column>
    </el-table> -->
    <div v-loading="loading" :class="{'common-list-body': viewType !== 'table'}">
      <table-list
        v-if="viewType == 'table'"
        :list="list"
        :table-height="tableHeight"
        :row-height="rowHeight"
        :item-type="itemType"
        :project-detail="projectDetail"
        :iteration-id="iterationId"
        @get-list="refreshList"
        @handle="handleProcess" />

      <board-list
        v-else-if="viewType == 'board'"
        ref="board"
        :search="search"
        :list="list"
        :item-type="itemType"
        :iteration-id="iterationId" />
    </div>

    <div v-if="viewType == 'table'" class="p-contianer">
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

    <item-detail
      v-if="taskDetailShow"
      :id="taskId"
      ref="particulars"
      :detail-index="detailIndex"
      :no-listener-class="['el-table__body']"
      @update-list="handleUpdateList"
      @close="taskDetailShow = false" />

    <panel-setting
      v-if="panelSettingShow"
      :item-type="itemType"
      :visible.sync="panelSettingShow"
      @update="udpatePanelShow" />

    <!-- 筛选 -->
    <list-filter
      v-if="filterShow"
      list-type="itemDetail"
      :list="filterData"
      @change="handleChange"
      @close-filter="filterShow = false" />

    <!-- 创建事项 -->
    <create-item
      v-if="createItemVisible"
      :visible.sync="createItemVisible"
      :item-type="createType"
      :iteration-id="iterationId"
      :is-agility="isAgility"
      @save-success="handleRefresh"
      @close="createItemVisible = false" />

    <!-- 关联事项  -->
    <crm-relative
      v-if="showPopover"
      :radio="false"
      :visible.sync="showPopover"
      crm-type="item"
      @close="showPopover=false"
      @changeCheckout="selectItemInfos" />
  </div>
</template>

<script>
import { workQueryIterationAllItemAPI, relationIterationAPI } from '@/api/pm/projectTask'

import WkTableHeader from '@/components/Page/WkTableHeader'
import ItemDetail from '@/views/pm/project/team/itemDetail'
// import StatusTag from '@/views/pm/project/components/StatusTag'
import ListFilter from '@/views/pm/project/team/components/ListFilter'
import CreateItem from '../../CreateItem'
import CrmRelative from '@/components/CreateCom/CrmRelative'
import TableList from '@/views/pm/project/team/components/list/TableList'
import BoardList from '@/views/pm/project/team/components/list/BoardList'
import PanelSetting from '@/views/pm/project/team/components/PanelSetting'

import { mapGetters } from 'vuex'
import Lockr from 'lockr'
import {
  convertPriority,
  getItemImg,
  getPriorityIcon
} from '@/views/pm/data'

export default {
  name: 'IterationItem',
  components: {
    WkTableHeader,
    ItemDetail,
    // StatusTag,
    ListFilter,
    CreateItem,
    CrmRelative,
    TableList,
    BoardList,
    PanelSetting
  },
  provide() {
    return {
      iterationId: this.iterationId,
      'rootTabs': {}
    }
  },
  props: {
    iterationId: [String, Number]
    // projectDetail: Object
  },
  data() {
    return {
      loading: false,
      search: '',
      layout: '1',
      itemType: 'All',
      viewType: 'table',
      boardSetList: [
        { label: '看板视图设置', value: '4' }
      ],
      sceneLayout: '',
      btnList: [
        { label: '全部', value: '' },
        { label: '需求', value: '2' },
        { label: '任务', value: '3' },
        { label: '缺陷', value: '4' }
      ],
      tableHeaderProps: {
        props: {
          showFilterView: false, // 不展示高级筛选
          showExportFields: false
        },
        filterHeaderProps: {
          maxTabCount: 4,
          tabSetShow: false,
          searchPlaceholder: '搜索标题或ID'
        }
      },
      quickAddList: [
        {
          type: 'relative',
          label: '关联现有事项'
        },
        {
          type: 'Require',
          label: '需求'
        },
        {
          type: 'Task',
          label: '任务'
        },
        {
          type: 'Defects',
          label: '缺陷'
        }
      ],
      list: [],
      fieldList: [
        { prop: 'num', label: 'ID', width: '60', isLock: 1 },
        { prop: 'name', label: '标题', minWidth: '440', isLock: 1 },
        { prop: 'priority', label: '优先级', minWidth: '100' },
        { prop: 'status', label: '状态', minWidth: '110' },
        { prop: 'mainUserName', label: '处理人', minWidth: '100' },
        { prop: 'createUserName', label: '创建人', minWidth: '100' },
        { prop: 'belongIterationName', label: '所属迭代', minWidth: '160' },
        { prop: 'stopTime', label: '截止日期', minWidth: '160' },
        { prop: 'createTime', label: '创建日期', minWidth: '160' }
      ],

      tableHeight: 200,
      rowHeight: 44, // 行高

      currentPage: 1,
      pageSize: Lockr.get('crmPageSizes') || 15,
      pageSizes: [15, 30, 60, 100],
      total: 0,

      taskId: '',
      detailIndex: -1,
      taskDetailShow: false,
      panelSettingShow: false,

      // 高级筛选
      filterShow: false,
      filterData: [],

      createItemVisible: false,
      createType: '',

      showPopover: false
    }
  },
  computed: {
    ...mapGetters(['projectDetail']),
    createAuth() {
      return this.$auth('coordination.editIteration', 'PM')
    },

    isAgility() {
      return this.projectDetail?.type === 2
    },
    showList() {
      const arr = [{ label: '平铺视图', value: '1' }]
      if (this.sceneLayout) {
        arr.push({ label: '看板视图', value: '3' })
      }
      return arr
    },
    showBoardSet() {
      return this.layout == '3' // 看板视图
    }
  },
  watch: {
    itemType: {
      handler(val) {
        if (val === 'Defects' || val === 'Task' || val === 'Require') {
          this.viewType = 'board'
          this.layout = '3'
        } else {
          this.viewType = 'table'
          this.layout = '1'
        }
      }
    }
  },
  mounted() {
    window.onresize = () => {
      this.updateTableHeight()
    }
    this.firstLoad()
  },
  methods: {
    /**
     * @description: 首次执行操作
     * @return {*}
     */
    firstLoad() {
      this.clearFilterCondition()
      this.getList()
    },

    refreshList() {
      this.getList()
    },

    /**
     * @description: 清除筛选条件
     * @return {*}
     */
    clearFilterCondition() {
      this.search = ''
      this.filterData = []
    },

    /**
     * @description: 获取列表数据
     * @param {*} type
     * @return {*}
     */
    getList() {
      this.loading = true
      const params = {
        belongIterationId: this.iterationId,
        type: this.sceneLayout,
        projectId: this.$route.params.id,
        name: this.search,
        page: this.currentPage,
        limit: this.pageSize
      }

      if (this.filterData && this.filterData.length) {
        // 高级筛选
        if (this.filterData && this.filterData.length) {
        // eslint-disable-next-line
        for (const item of this.filterData) {
            const currentParams = []
            // eslint-disable-next-line
          for (const sitem of item.list) {
              if (sitem.checked) {
                currentParams.push(sitem.id)
              }
            }
            params[item.field] = currentParams
          }
        }
      }

      workQueryIterationAllItemAPI(params).then(res => {
        this.list = res.data.list

        if (res.data.totalRow && Math.ceil(res.data.totalRow / this.pageSize) < this.currentPage && this.currentPage > 1) {
          this.currentPage = this.currentPage - 1
          this.getList()
        } else {
          this.total = res.data.totalRow
          this.loading = false
        }

        this.$nextTick(() => {
          document.querySelector('.el-table__body-wrapper').scrollTop = 1
        })

        // 数据更新刷新列
        this.updateTableHeight()
      }).catch(() => {
        this.loading = false
      })
    },

    /**
     * @description: 更新表高
     * @return {*}
     */
    updateTableHeight() {
      const clientHeight = document.documentElement.clientHeight
      this.$nextTick(() => {
        const tableHead = this.getTableHead()

        const maxTableHeight = clientHeight - tableHead.$el.clientHeight - 275

        const dataHeight = this.rowHeight * this.list.length + 41 // 头高度
        if (dataHeight > maxTableHeight) {
          this.tableHeight = maxTableHeight
        } else {
          this.tableHeight = this.list.length === 0 ? 200 : dataHeight
        }

        this.tableHeight
      })
    },

    /**
     * @description: 获取table
     * @return {*}
     */
    getTableHead() {
      let table = null
      this.$children.forEach(item => {
        if (item.$options && item.$options.name === 'WkTableHeader') {
          table = item
        }
      })
      return table
    },

    /**
     * @description: 搜索
     * @param {*} type
     * @param {*} data
     * @return {*}
     */
    tableHeaderHandle(type, data) {
      if (type == 'search') {
        this.search = data
        this.getList()
      }
    },

    /**
     * @description: 场景
     * @param {*} data
     * @return {*}
     */
    sceneSelect(data) {
      if (!data.value) {
        this.viewType = 'table'
        this.layout = '1'
        this.itemType = 'All'
      } else {
        this.itemType = {
          2: 'Require',
          3: 'Task',
          4: 'Defects'
        }[data.value]
      }
      this.sceneLayout = data.value
      this.updateListOrBoard()
    },

    /**
     * @description: 更新列表或看板
     * @return {*}
     */
    updateListOrBoard(isClearFilter = false) {
      if (this.viewType == 'table') {
        isClearFilter ? this.firstLoad() : this.getList()
      } else if (this.viewType == 'board') {
        isClearFilter && (this.search = '')
        this.$nextTick(() => {
          this.$refs.board.getData('', isClearFilter)
        })
      }
      // if (this.viewType === 'board') {
      //   this.$nextTick(() => {
      //     this.$refs.board.getData(isClearFilter)
      //   })
      // } else {
      //   this.getList(isClearFilter)
      // }
    },

    /**
     * @description: 新建
     * @param {*} item
     * @return {*}
     */
    handleCreate(item) {
      if (item.type == 'relative') {
        this.createType = item.type
        this.showPopover = true
      } else if (['Require', 'Task', 'Defects'].includes(item.type)) {
        this.createType = item.type
        this.createItemVisible = true
      }
    },

    /**
     * @description: 关联事项到当前迭代
     * @param {*} data 勾选数据
     * @return {*}
     */
    selectItemInfos({ data = [] }) {
      if (data.length) {
        const params = {
          taskIds: data.map(item => item.taskId),
          belongIterationId: this.iterationId
        }

        relationIterationAPI(params).then(res => {
          this.$message.success('操作成功')
          this.getList()
        }).catch(() => {
          /** ignore */
        })
      }
    },

    /**
     * @description: 创建迭代事项保存回调
     * @return {*}
     */
    handleRefresh() {
      if (this.createType == 'relative') {
        this.showPopover = true
      } else {
        this.createItemVisible = false
        this.updateListOrBoard()
      }
    },

    cellClassName({ row, column, rowIndex, columnIndex }) {
      if (column.property === 'name') {
        return 'can-visit--underline'
      } else {
        return ''
      }
    },

    /**
     * @description: 表格点击
     * @param {*} row
     * @param {*} column
     * @param {*} event
     * @return {*}
     */
    handleRowClick(row, column, event) {
      if (column.property == 'name') {
        this.taskId = row.taskId
        this.detailIndex = this.getObjIndex(this.list, 'taskId', row.taskId)
        this.taskDetailShow = true
      }
    },

    /**
     * @description: 获取对象在所在数组的索引
     * @param {*} array
     * @param {*} filed
     * @param {*} value
     * @return {*}
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
     * @description: 筛选刷新
     * @param {*} data
     * @return {*}
     */
    handleChange(data) {
      this.filterData = data
      this.getList()
    },

    /**
     * @description: 刷新数据
     * @return {*}
     */
    handleUpdateList() {
      this.getList()
    },

    /**
     * 更改每页展示数量
     * @param {*} val
     */
    handleSizeChange(val) {
      Lockr.set('crmPageSizes', val)
      this.pageSize = val
      this.getList()
    },

    /**
     * 更改当前页数
     * @param {*} val
     */
    handleCurrentChange(val) {
      this.currentPage = val
      this.getList()
    },

    getPriority(val) {
      return convertPriority(val)
    },

    getIconClass(row) {
      return getItemImg(row)
    },

    getPriorityPic(val) {
      return getPriorityIcon(val)
    },

    /**
     * 超时状态
     * @param time
     * @returns {boolean}
     */
    getOverTimeStatus(time) {
      const data = this.handleTime(time)
      if (data === '今天' || data === '明天') {
        return 'is-near'
      } else {
        return this.$moment(time).isBefore(this.$moment()) ? 'is-over' : 'is-common'
      }
    },

    /**
     * 时间格式化
     * @param time
     * @returns {null|*}
     */
    formatTime(time) {
      if (!time) return null
      const data = this.handleTime(time)
      if (data) {
        return data
      } else {
        const flag = this.$moment().isSame(time, 'year')
        return flag
          ? this.$moment(time).format('M月D日')
          : this.$moment(time).format('YYYY年M月D日')
      }
    },

    /**
     * @description: 根据时间处理今天/明天/昨天的情况
     * @return {*}
     */
    handleTime(time) {
      const activeDataArr = this.$moment(time).format('YYYY-MM-DD').split('-')
      const todayDate = new Date() // 今天
      const nowDataArr = [todayDate.getFullYear(), todayDate.getMonth() + 1, todayDate.getDate()] // 今天的年月日
      const tomorrowData = new Date(todayDate.setTime(todayDate.getTime() + 24 * 60 * 60 * 1000)) // 明天
      const tomorrowDataArr = [tomorrowData.getFullYear(), tomorrowData.getMonth() + 1, tomorrowData.getDate()] // 明天的年月日
      const yesterdayDate = new Date(todayDate.setTime(todayDate.getTime() - 48 * 60 * 60 * 1000)) // 昨天
      const yesterdayDateArr = [yesterdayDate.getFullYear(), yesterdayDate.getMonth() + 1, yesterdayDate.getDate()] // 昨天的年月日
      console.log(nowDataArr, yesterdayDate, yesterdayDateArr)
      if (nowDataArr[0] == activeDataArr[0] && nowDataArr[1] == activeDataArr[1] && nowDataArr[2] == activeDataArr[2]) {
        return '今天'
      } else if (tomorrowDataArr[0] == activeDataArr[0] && tomorrowDataArr[1] == activeDataArr[1] && tomorrowDataArr[2] == activeDataArr[2]) {
        return '明天'
      } else if (yesterdayDateArr[0] == activeDataArr[0] && yesterdayDateArr[1] == activeDataArr[1] && yesterdayDateArr[2] == activeDataArr[2]) {
        return '昨天'
      } else {
        return false
      }
    },

    /**
     * @description: 选择框处理方法
     * @param {*} value
     * @return {*}
     */
    selectChanged(value) {
      if (value == '4') {
        this.layout = this.showIndex
        this.panelSettingShow = true
      } else {
        this.viewType = {
          1: 'table',
          3: 'board'
        }[value]
        this.layout = value
        this.showIndex = value

        this.changeViewList()
      }
    },

    /**
     * @description: 切换视图获取列表
     * @return {*}
     */
    changeViewList() {
      if (this.viewType == 'table') {
        this.getList()
      }
    },

    handleProcess(data) {
      const { type, row } = data
      if (type == 'detail') {
        if (row.type == 1) {
          // this.$emit('enter-detail', row)
        } else {
          this.taskId = row.taskId
          this.detailIndex = this.getObjIndex(this.list, 'taskId', row.taskId)
          this.taskDetailShow = true
        }
      }
    },

    udpatePanelShow() {
      this.$refs.board.getData()
      this.panelSettingShow = false
    }

  }
}
</script>
<style lang='scss' scoped>
@import "@/views/pm/styles/table.scss";
@import "@/views/layout/components/style.scss";

.wk-table-header {
  margin-top: 0;
  margin-bottom: 20px;
}

.iteration-item {
  height: calc(100% - 72px);

  .common-list-body {
    height: calc(100% - 54px);
  }
}

.filter-wrapper {
  margin-bottom: 20px;
  line-height: 32px;
}

.item-img {
  display: inline-block;
  width: 16px;
  height: 16px;
  margin-right: 5px;
}

.item-title {
  max-width: calc(100% - 21px);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.icon-name {
  display: flex;
  align-items: center;
  justify-content: flex-start;
}

// 高级筛选
.filter-button {
  &.has-values {
    background-color: $--color-n30;
  }
}

.common-create-btn {
  margin-left: 12px;
}

.project-tag {
  height: 22px;
  padding: 0 8px;
  font-size: 13px;
  line-height: 18px;

  &.is-over {
    color: white;
    background-color: $--color-r400;
  }

  &.is-near {
    color: white;
    background-color: $--color-y400;
  }

  &.is-common {
    color: $--color-black;
    background-color: $--background-color-base;
  }
}
</style>
