import { workQueryItemListAPI, workQueryIterationsItemListAPI } from '@/api/pm/projectTask'

import ItemDetail from '@/views/pm/project/team/itemDetail'

import { itemTypeMap } from '@/views/pm/data'
import Lockr from 'lockr'

export default {
  components: {
    ItemDetail
  },
  props: {

  },
  data() {
    return {
      loading: false,
      list: [],
      tableHeight: 200,
      rowHeight: 44, // 行高

      search: '', // 搜索内容
      filterList: [],
      panelSettingShow: false,
      layout: '3',
      showIndex: '3',

      currentPage: 1,
      pageSize: Lockr.get('crmPageSizes') || 15,
      pageSizes: [15, 30, 60, 100],
      total: 0
    }
  },
  inject: ['rootTabs'],
  computed: {
    indexAuth() {
      return true
    }
  },
  watch: {
    'rootTabs.currentName'(val) {
      if (val === this.itemType) {
        this.viewType = {
          1: 'table',
          2: 'tree',
          3: 'board'
        }[this.layout]
        this.firstLoad()
      }
    }
  },
  mounted() {
    window.onresize = () => {
      this.updateTableHeight()
    }

    if (this.rootTabs.currentName == this.itemType) {
      this.firstLoad()
    }
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
      this.filterList = []
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
      if (this.viewType === 'board') return
      this.loading = true

      const params = {
        type: itemTypeMap[this.itemType],
        projectId: this.$route.params.id,
        name: this.search,
        page: this.currentPage,
        limit: this.pageSize,
        ...this.getFilterParams()
      }

      if (this.itemType == 'Iterations') {
        params.type = 1
      }

      const request = this.itemType == 'Iterations'
        ? workQueryIterationsItemListAPI
        : workQueryItemListAPI

      request(params).then(res => {
        this.list = res.data.list.map(item => {
          if (this.itemType == 'Iterations') {
            item.startTime = item.startTime ? item.startTime.slice(0, 10) : ''
            item.stopTime = item.stopTime ? item.stopTime.slice(0, 10) : ''
          }
          return item
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
        if (row.type == 1) {
          this.$emit('enter-detail', row)
        } else {
          this.taskId = row.taskId
          this.detailIndex = this.getObjIndex(this.list, 'taskId', row.taskId)
          this.taskDetailShow = true
        }
      }
    },

    handleUpdateList() {
      this.getList()
    },

    detailClose() {
      this.taskDetailShow = false
      this.getList()
    },

    tableHeaderHandle(type, data) {
      if (type == 'search') {
        this.search = data
        this.changeViewList()
      }
    },

    /**
     * @description: 切换视图获取列表
     * @return {*}
     */
    changeViewList(isClearFilter) {
      if (this.viewType == 'table') {
        isClearFilter ? this.firstLoad() : this.getList()
      } else if (this.viewType == 'board') {
        isClearFilter && (this.search = '')
        this.$nextTick(() => {
          this.$refs.board.getData('', isClearFilter)
        })
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
      if (value == '4') {
        this.layout = this.showIndex
        this.panelSettingShow = true
      } else {
        this.viewType = {
          1: 'table',
          2: 'tree',
          3: 'board'
        }[value]
        this.layout = value
        this.showIndex = value

        this.changeViewList()
      }
    },

    udpatePanelShow() {
      this.$refs.board.getData()
      this.panelSettingShow = false
    },

    /**
     * @description: 更新表高
     * @return {*}
     */
    updateTableHeight() {
      const clientHeight = document.documentElement.clientHeight
      this.$nextTick(() => {
        const tableHead = this.getTableHead()
        const maxTableHeight = clientHeight - tableHead.$el.clientHeight - 260

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
    }
  }
}
