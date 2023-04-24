<!--
 * @Author: chengya chengya@5kcrm.com
 * @Date: 2023-02-06 14:40:32
 * @LastEditors: yang
 * @LastEditTime: 2023-04-03 18:06:19
 * @FilePath: \72crm-standard-web\src\views\pm\project\team\FullItem.vue
 * @Description: 这是默认设置,请设置`customMade`, 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
-->
<template>
  <div class="common-list">
    <wk-table-header
      :search.sync="search"
      :props="tableHeaderProps.props"
      :filter-header-props="tableHeaderProps.filterHeaderProps"
      @event-change="tableHeaderHandle">
      <el-checkbox
        v-model="checked"
        style="margin-right: 15px;"
        @change="handleCheck">
        只显示未规划事项
      </el-checkbox>
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
    </wk-table-header>

    <el-table
      id="crm-table"
      v-loading="loading"
      :row-height="rowHeight"
      :data="list"
      :height="tableHeight"
      :cell-class-name="cellClassName"
      :row-key="`taskId`"
      use-virtual
      highlight-current-row
      style="width: 100%;"
      @row-click="handleRowClick"
      @selection-change="handleSelectionChange">
      <el-table-column
        show-overflow-tooltip
        reserve-selection
        type="selection"
        fixed
        align="center"
        width="55" />
      <el-table-column
        v-for="(item, index) in fieldList"
        :key="index"
        :fixed="item.isLock === 1"
        :prop="item.prop"
        :label="item.label"
        :width="item.width"
        :min-width="item.minWidth"
        :class-name="item.width>60 ? 'column' : '' "
        show-overflow-tooltip>
        <template slot-scope="{ row }">
          <span v-if="item.prop == 'num'">#{{ row[item.prop] }}</span>
          <span v-else-if="item.prop == 'name'" class="icon-name">
            <img v-if="itemType != 'Iterations'" class="item-img" :src="getIconClass(row)" alt="">
            <span class="item-title can-visit--underline">{{ row[item.prop] }}</span>
          </span>
          <status-tag v-else-if="item.prop == 'status'" :status-name="row.boardStatusName" style="margin-right: 8px;" :type="row.status" />
          <span v-else-if="item.prop == 'priority'" class="icon-name">
            <img v-if="itemType != 'Iterations'" class="item-img" :src="getPriorityPic(row[item.prop])" alt="">
            <span>{{ getPriority(row[item.prop]) }}</span>
          </span>
          <span v-else-if="item.prop == 'belongIterationProgress'">{{ Math.round(row[item.prop]) || 0 }}%</span>
          <span v-else-if="item.prop == 'projectAdminRoleList'">{{ getAdminList(row[item.prop]) }}</span>
          <span v-else-if="item.prop == 'stopTime'">{{ fomartTime(row[item.prop]) }}</span>
          <span v-else>{{ row[item.prop] }}</span>
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

    <!-- 创建事项 -->
    <create-item
      v-if="createItemVisible"
      :visible.sync="createItemVisible"
      :item-type="createType"
      :is-agility="isAgility"
      @save-success="handleRefresh"
      @close="createItemVisible = false" />

    <!-- <item-detail
      v-if="taskDetailShow"
      :id="taskId"
      ref="particulars"
      :project-detail="projectDetail"
      :detail-index="detailIndex"
      :no-listener-class="['el-table__body']"
      @update-list="handleUpdateList"
      @close="detailClose" /> -->
  </div>
</template>

<script>
import { workQueryItemListAPI, workQueryPlanItemListAPI } from '@/api/pm/projectTask'

import WkTableHeader from '@/components/Page/WkTableHeader'
import StatusTag from '@/views/pm/project/components/StatusTag'
import CreateItem from './CreateItem'

import { itemTypeMap, convertPriority, getItemImg, getPriorityIcon } from '@/views/pm/data'
import { mapGetters } from 'vuex'
import Lockr from 'lockr'
import moment from 'moment'

export default {
  name: 'List',
  components: {
    WkTableHeader,
    StatusTag,
    CreateItem
  },
  inject: ['iterationId'],
  data() {
    return {
      tableHeaderProps: {
        props: {
          showFilterView: false, // 不展示高级筛选
          showExportFields: false
        },
        filterHeaderProps: {
          maxTabCount: 0,
          tabSetShow: false,
          searchPlaceholder: ''
        }
      },
      checked: false,
      quickAddList: [
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

      loading: false,
      fieldList: [
        { prop: 'num', label: 'ID', width: '60', isLock: 1 },
        { prop: 'name', label: '标题', minWidth: '440', isLock: 1 },
        { prop: 'priority', label: '优先级', width: '100' },
        { prop: 'status', label: '状态', width: '100' },
        { prop: 'mainUserName', label: '处理人', minWidth: '100' },
        { prop: 'createUserName', label: '创建人', minWidth: '100' },
        { prop: 'belongIterationName', label: '所属迭代', minWidth: '160' },
        { prop: 'stopTime', label: '截止日期', width: '160' },
        { prop: 'createTime', label: '创建日期', width: '160' }
      ],
      list: [],
      tableHeight: 200,
      rowHeight: 44, // 行高
      search: '', // 搜索内容
      selectionList: [],

      currentPage: 1,
      pageSize: Lockr.get('crmPageSizes') || 15,
      pageSizes: [15, 30, 60, 100],
      total: 0,

      itemType: 'All',
      taskId: '',
      detailIndex: -1,
      taskDetailShow: false,

      createItemVisible: false,
      createType: ''
    }
  },
  computed: {
    ...mapGetters(['projectDetail']),
    isAgility() {
      return this.projectDetail?.type === 2
    }
  },
  created() {
    this.tableHeaderProps.filterHeaderProps.searchPlaceholder = '搜索标题或描述'
  },
  mounted() {
    window.onresize = () => {
      this.updateTableHeight()
    }
    this.getList()
  },
  methods: {
    tableHeaderHandle(type, data) {
      if (type == 'search') {
        this.search = data
        this.changeViewList()
      }
    },

    handleCheck() {
      this.getList()
    },

    /**
     * @description: 新建
     * @param {*} item
     * @return {*}
     */
    handleCreate(item) {
      this.createType = item.type
      this.createItemVisible = true
    },

    /**
     * @description: 创建事项保存回调
     * @return {*}
     */
    handleRefresh() {
      this.createItemVisible = false
      this.getList()
    },

    /**
     * @description: 获取列表数据
     * @return {*}
     */
    getList() {
      this.loading = true

      const params = {
        type: itemTypeMap[this.itemType],
        projectId: this.$route.params.id,
        name: this.search,
        page: this.currentPage,
        limit: this.pageSize
      }

      let request = workQueryItemListAPI
      if (this.checked) {
        params.type = 0
        request = workQueryPlanItemListAPI
      } else {
        params.needBelongIteration = 2
        params.iterationId = this.iterationId
      }

      request(params).then(res => {
        this.list = res.data.list || []

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
        this.loading = false
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
        const maxTableHeight = clientHeight - tableHead.$el.clientHeight - 280

        const dataHeight = this.rowHeight * this.list.length + 51 // 头高度
        if (dataHeight > maxTableHeight) {
          this.tableHeight = maxTableHeight
        } else {
          this.tableHeight = this.list.length === 0 ? 200 : dataHeight
        }
      })
    },

    cellClassName({ row, column, rowIndex, columnIndex }) {
      if (column.property === 'name') {
        return 'can-visit--underline'
      } else {
        return ''
      }
    },

    handleRowClick(row, column, event) {
      if (column.property == 'name') {
        this.$emit('handle', { type: 'detail', row })
      }
    },

    /**
     * @description:
     * @param {*} val
     * @return {*}
     */
    handleSelectionChange(val) {
      this.selectionList = val
      this.$emit('selection-change', val, 'item')
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
     * @description: 优先级文字
     * @param {*} val
     * @return {*}
     */
    getPriority(val) {
      return convertPriority(val)
    },

    /**
     * @description: 优先级图片
     * @param {*} val
     * @return {*}
     */
    getPriorityPic(val) {
      return getPriorityIcon(val)
    },

    /**
     * @description: 负责人
     * @param {*} val
     * @return {*}
     */
    getAdminList(val) {
      return (val || []).map(item => item.realname).join()
    },

    /**
     * @description: 格式化时间
     * @return {*}
     */
    fomartTime(val) {
      return val ? moment(val).format('YYYY-MM-DD') : '--'
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

.common-list {
  height: 100%;
  padding: 16px 40px 0;

  &-body {
    height: 100%;
  }
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
</style>
