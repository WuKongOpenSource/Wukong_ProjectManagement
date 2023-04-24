// TableList 的 混入
import {
  workQueryFieldAndSortAPI,
  workListSetFieldWidthAPI
} from '@/api/pm/projectTask'

import StatusTag from '@/views/pm/project/components/StatusTag'

import { debounce } from 'throttle-debounce'

export default {
  components: {
    StatusTag
  },
  props: {
    tableHeight: Number,
    rowHeight: Number,
    projectDetail: Object
  },
  data() {
    return {
      // 列数据
      fieldList: [],

      iterationsList: [
        { prop: 'num', label: '引用ID', width: '70', isLock: 1 },
        { prop: 'name', label: '迭代名称', width: '220', isLock: 1 },
        { prop: 'startTime', label: '开始时间', minWidth: '160' },
        { prop: 'stopTime', label: '结束时间', minWidth: '160' },
        { prop: 'status', label: '状态', minWidth: '100' },
        { prop: 'belongIterationProgress', label: '进度' },
        { prop: 'mainUserName', label: '负责人' }
      ],

      // 查表头字段时模块映射
      moduleMap: {
        All: 0,
        Iterations: 1,
        Require: 2,
        Task: 3,
        Defects: 4
      }

    }
  },
  computed: {
    // 是敏捷项目
    isAgility() {
      return this.projectDetail?.type === 2
    },
    // 编辑事项权限
    editAuth() {
      return this.$auth('coordination.editMatters', 'PM')
    },
    // 是否处于工作台列表
    isWorkbench() {
      return this.$route.name === 'workbench'
    }
  },
  created() {
    this.getField()

    this.debouncePostWidthChange = debounce(500, this.postWidthChange)
  },
  mounted() { },
  methods: {
    /**
     * @description: 获取事项列表字段
     * @return {*}
     */
    getField() {
      const params = {
        projectId: this.$route.params.id,
        moduleType: this.moduleMap[this.itemType]
      }
      workQueryFieldAndSortAPI(params).then(res => {
        const fieldList = []

        for (const { fieldName, name, width } of (res.data || [])) {
          // type=3代表工作台
          if (
            this.projectDetail.type !== 3 &&
            fieldName === 'belongProjectName'
          ) {
            continue
          }

          const fieldItem = {
            prop: fieldName,
            label: name,
            width
          }

          if (['num', 'name'].includes(fieldName)) {
            fieldItem.isLock = 1
          }

          fieldList.push(fieldItem)
        }

        this.fieldList = this.isAgility
          ? fieldList
          : fieldList.filter(item => !['belongIterationName'].includes(item.prop))
      }).catch(() => {

      })
    },

    /**
     * 当拖动表头改变了列的宽度的时候会触发该事件
     * @param {*} newWidth
     * @param {*} oldWidth
     * @param {*} column
     * @param {*} event
     */
    handleHeaderDragend(newWidth, oldWidth, column, event) {
      this.debouncePostWidthChange(newWidth, column)
    },

    /**
     * 上传宽change
     * @param {*} val
     */
    postWidthChange(newWidth, column) {
      if (column.property) {
        this.fieldList.forEach(item => {
          if (column.property === item.prop) {
            item.width = newWidth
          }
        })

        const params = {
          projectId: this.$route.params.id || 0,
          schemeId: this.projectDetail?.schemeId || 0,
          moduleType: this.moduleMap[this.itemType],
          fieldSorts: this.fieldList.map(item => ({
            fieldName: item.prop,
            name: item.label,
            width: item.width
          }))
        }

        workListSetFieldWidthAPI(params)
          .then(res => {
            /** ignore */
          })
          .catch(() => { })
      }
    },

    getList() {
      this.$emit('get-list')
    },

    cellClassName({ row, column, rowIndex, columnIndex }) {
      if (column.property === 'name') {
        return 'can-visit--underline'
      } else if (
        ['priority', 'status', 'mainUserName', 'stopTime'].includes(column.property) &&
        !this.getDisabledAuth(row.taskAuth) &&
        this.itemType != 'Iterations'
      ) {
        return 'td-hover'
      } else {
        return ''
      }
    },

    handleRowClick(row, column, event) {
      if (column.property == 'name') {
        if (this.isWorkbench && this.$route.query.tab === 'Iterations') {
          window.open(`${location.origin + location.pathname}#/project/subs/team/${row.projectId}?tab=Iterations&type=iteration-detail&taskId=${row.taskId}`)
        } else {
          this.$emit('handle', { type: 'detail', row })
        }
      } else if (column.property == 'status') {
        this.getStatusList(row)
      }
    },

    // 区分工作台和项目内列表的禁用权限
    getDisabledAuth(taskAuth) {
      if (this.projectDetail.type === 3) {
        return !taskAuth.coordination.editMatters
      }
      return !this.editAuth
    }
  }
}
