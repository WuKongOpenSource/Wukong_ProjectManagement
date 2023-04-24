<template>
  <div class="common-list">
    <wk-table-header
      :search.sync="search"
      :props="tableHeaderProps.props"
      :filter-header-props="tableHeaderProps.filterHeaderProps"
      @event-change="tableHeaderHandle" />

    <div v-loading="loading">
      <table-list
        :list="list"
        :table-height="tableHeight"
        :row-height="rowHeight"
        :item-type="itemType"
        :project-detail="projectDetail"
        @get-list="refreshList"
        @handle="handleProcess" />
    </div>

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

    <item-detail
      v-if="taskDetailShow"
      :id="taskId"
      ref="particulars"
      :project-detail="projectDetail"
      :detail-index="detailIndex"
      :no-listener-class="['el-table__body']"
      :project-auth="projectAuth"
      @update-list="handleUpdateList"
      @close="detailClose" />

    <!-- 筛选 -->
    <!-- <list-filter
      v-if="filterShow"
      :list-type="listType"
      :list="filterList"
      @change="handleChange"
      @close-filter="filterShow = false" /> -->
  </div>
</template>

<script>
import {
  workQueryUserTaskListAPI,
  workQueryIterationsItemListAPI
} from '@/api/pm/projectTask'
import { projectAuthListAPI } from '@/api/pm/manage'

import WkTableHeader from '@/components/Page/WkTableHeader'
// import ListFilter from '@/views/pm/project/team/components/ListFilter'
// import TableList from './TableList'
import TableList from '@/views/pm/project/team/components/list/TableList'
import ItemDetail from '@/views/pm/project/team/itemDetail'

import Lockr from 'lockr'
import { itemTypeMap, handleAuth } from '@/views/pm/data'

export default {
  name: 'List',
  components: {
    WkTableHeader,
    TableList,
    // ListFilter,
    ItemDetail
  },
  inject: ['rootTabs'],
  props: {
    itemType: String,
    projectDetail: Object,
    projectId: String
  },
  data() {
    return {
      loading: false,
      list: [],
      tableHeight: 200,
      rowHeight: 49, // 行高

      search: '', // 搜索内容
      filterList: [],

      currentPage: 1,
      pageSize: Lockr.get('crmPageSizes') || 15,
      pageSizes: [15, 30, 60, 100],
      total: 0,

      tableHeaderProps: {
        props: {
          showFilterView: false, // 不展示高级筛选
          showExportFields: false
        },
        filterHeaderProps: {
          maxTabCount: 0,
          tabSetShow: false,
          searchPlaceholder: '搜索标题或ID'
        }
      },
      filterShow: false,
      taskId: '',
      detailIndex: -1,
      taskDetailShow: false,
      projectAuth: null
    }
  },
  computed: {
    // listType() {
    //   if (this.itemType == 'All') {
    //     return 'all'
    //   } else {
    //     return 'item'
    //   }
    // }
  },

  watch: {
    'rootTabs.currentName'(val) {
      if (val === this.itemType) {
        this.filterList = []
        this.getList()
      }
    },
    projectId: {
      handler(oldVal, newVal) {
        if (newVal != oldVal && this.rootTabs.currentName == this.itemType) {
          this.getList()
        }
      }
    }
  },

  created() {
  },
  mounted() {
    window.onresize = () => {
      this.updateTableHeight()
    }

    if (this.rootTabs.currentName == this.itemType) {
      this.filterList = []
      this.getList()
    }
  },
  methods: {
    refreshList() {
      this.getList()
    },

    getFilterParams() {
      const filterParams = {}
      // 高级筛选
      // eslint-disable-next-line
      for (const item of this.filterList) {
        const currentParams = []
        // eslint-disable-next-line
        for (const sitem of item.list) {
          if (sitem.checked) {
            currentParams.push(sitem.id)
          }
        }
        filterParams[item.field] = currentParams
      }
      return filterParams
    },

    /**
     * @description: 获取列表数据
     * @return {*}
     */
    getList() {
      this.loading = true

      const params = {
        type: itemTypeMap[this.itemType],
        projectId: this.projectId === '1' ? '' : this.projectId,
        name: this.search,
        page: this.currentPage,
        limit: this.pageSize,
        ...this.getFilterParams()
      }

      let request = workQueryUserTaskListAPI

      if (this.itemType == 'Iterations') {
        params.type = 1
        request = workQueryIterationsItemListAPI
      }
      // if (this.projectId !== '1') {
      //   params.projectId = this.projectId
      // }

      request(params).then(res => {
        const projectIdStr = res.data.list.map(item => item.projectId).join(',')
        projectAuthListAPI({ projectIds: projectIdStr }).then(authRes => {
          const authList = authRes.data.map(handleAuth)
          this.list = res.data.list.map((item, index) => {
            return {
              taskAuth: authList[index],
              ...item
            }
          })

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
      }).catch(() => {
        this.loading = false
      })
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

    handleProcess(data) {
      const { type, row } = data
      if (type == 'detail') {
        this.taskId = row.taskId
        this.projectAuth = row.taskAuth
        this.detailIndex = this.getObjIndex(this.list, 'taskId', row.taskId)
        this.taskDetailShow = true
      }
    },

    handleUpdateList() {
      this.getList()
      this.$bus.emit('update-workbench-num')
    },

    detailClose() {
      this.taskDetailShow = false
      this.getList()
    },

    tableHeaderHandle(type, data) {
      if (type == 'search') {
        this.search = data
        this.getList()
      }
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
    /**
     * @description: 选择框处理方法
     * @param {*} value
     * @return {*}
     */
    selectChanged(value) {
      this.getList()
    },

    /**
     * @description: 更新表高
     * @return {*}
     */
    updateTableHeight() {
      const clientHeight = document.documentElement.clientHeight
      this.$nextTick(() => {
        const tableHead = this.getTableHead()
        const maxTableHeight = clientHeight - tableHead.$el.clientHeight - 226

        const dataHeight = this.rowHeight * this.list.length + 51 // 头高度
        if (dataHeight > maxTableHeight) {
          this.tableHeight = maxTableHeight
        } else {
          this.tableHeight = this.list.length === 0 ? 200 : dataHeight
        }
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
       * @description: 筛选更新
       * @param {*} list
       * @return {*}
       */
    handleChange(list) {
      this.filterList = list || []
      this.getList()
    }
  }

}

</script>
<style lang='scss' scoped>
.wk-table-header {
  margin-top: 0;
  margin-bottom: 20px;
}

.common-list {
  height: 100%;

  &-body {
    height: calc(100% - 54px);
  }
}
</style>
