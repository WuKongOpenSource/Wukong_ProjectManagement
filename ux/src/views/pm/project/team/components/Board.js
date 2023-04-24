import {
// workTaskStatusSetAPI,
// workTaskClassSaveAPI
// workTaskClassUpateAPI
} from '@/api/pm/projectTask'
import {
  // workTaskclassDeleteAPI,
  // workTaskIndexAPI,
  // workTaskOwnerIndexAPI,
  // workTaskArchiveTaskAPI,
  // workTaskArchiveOwnerTaskAPI,
  workTaskUpdateOrderAPI,
  workTaskUpdateClassOrderAPI
} from '@/api/pm/project'

import TaskQuickAdd from '@/views/oa/taskExamine/task/components/TaskQuickAdd'
import TaskCreate from '@/views/oa/taskExamine/task/Create'
import TaskDetail from '@/views/oa/taskExamine/task/components/TaskDetail'

import TaskMixin from '@/views/oa/taskExamine/task/mixins/TaskMixin'
import { throttle } from 'throttle-debounce'

export default {
  components: {
    TaskQuickAdd,
    TaskDetail,
    TaskCreate
  },

  directives: {
  },

  mixins: [TaskMixin],

  props: {
    workId: [String, Number],
    permission: {
      type: Object,
      default: () => {
        return {}
      }
    },
    conditionData: {
      type: Object,
      default: () => {
        return {
          userIds: [],
          timeId: [],
          tagIds: []
        }
      }
    } // 筛选条件
  },

  data() {
    return {
      loading: false,
      // 新建任务弹出框
      createTaskListShow: false,
      createSubTaskClassId: 'hidden',
      // 新建列表
      taskListName: '',
      // 重命名
      editTaskListName: '',
      // 主数据
      taskList: [],
      // 详情对应的任务对象数据 -- 用于更新数据
      // 详情数据
      taskID: '',
      detailIndex: -1,
      detailSection: -1,
      taskDetailShow: false,
      // 全屏新建任务
      taskCreateShow: false,
      createAction: {
        type: 'save'
      }
    }
  },

  computed: {
    // 展示更多操作按钮
    showMoreBtn() {
      return (this.permission.updateTaskClass && this.isBoardShow) ||
      this.permission.saveTask ||
      this.permission.archiveTask ||
      (this.permission.deleteTaskClass && this.isBoardShow)
    },
    // 可以移动任务分类
    orderTaskClassDisabled() {
      return false
    },
    // 可以移动任务
    orderTaskDisabled() {
      return false
    },
    // 表的展示字段
    tableNameField() {
      return {
        board: 'boardName',
        user: 'realname'
      }[this.showType || 'board']
    },
    // 表的字段
    tableField() {
      return {
        board: 'projectBoardId',
        user: 'userId'
      }[this.showType || 'board']
    },
    // 是面板展示
    isBoardShow() {
      return this.showType == 'board'
    }
  },

  watch: {

    workId() {
      // this.createTaskListShow = false
      // this.taskDetailShow = false
      // this.taskList = []
      // this.getList()
    },

    conditionData() {
      this.getList()
    }
  },

  created() {
    this.debouncedCheckboxChange = throttle(500, (element, item, i) => {
      this.checkboxChange(element, item, i)
    })
    this.getList()

    // 导入
    this.$bus.$on('work-task-import', (userIds, timeId, tagIds) => {
      this.getList()
    })
  },

  mounted() {
    document
      .getElementById('project-container')
      .addEventListener('click', this.taskShowHandle, false)
  },

  beforeDestroy() {
    this.$bus.$off('work-task-import')
  },

  methods: {
    /**
     * @description: 获取所有数据
     * @return {*}
     */
    getList() {
      const params = { workId: this.workId }
      if (this.conditionData) {
        params.mainUserId = this.conditionData.userIds
        params.stopTimeType = this.conditionData.timeId
        params.labelId = this.conditionData.tagIds
        params.taskName = this.conditionData.search
      }

      // this.loading = true
      // const request = {
      //   board: workTaskIndexAPI,
      //   user: workTaskOwnerIndexAPI
      // }[this.showType || 'board']

      // request(params)
      //   .then(res => {
      //     this.loading = false
      //     // eslint-disable-next-line no-unused-vars
      //     for (const item of res.data) {
      //       item.checkedNum = 0
      //       // eslint-disable-next-line no-unused-vars
      //       for (const i of item.list) {
      //         if (i.status == 5) {
      //           i.checked = true
      //           item.checkedNum += 1
      //         } else {
      //           i.checked = false
      //         }
      //       }
      //     }
      //     this.taskList = res.data
      //     this.getListRest()
      //   })
      //   .catch(() => {
      //     this.loading = false
      //   })
    },

    /**
     * @description: 覆盖改方法 获取反馈
     * @return {*}
     */
    getListRest() {},

    /**
     * @description: 列表拖拽
     * @param {*} evt
     * @return {*}
     */
    moveEndParentTask(evt) {
      document.dispatchEvent(new MouseEvent('mouseup'))
      if (evt && evt.oldIndex != evt.newIndex) {
        workTaskUpdateClassOrderAPI({
          workId: this.workId,
          classIds: this.taskList.map(item => item.classId)
        })
          .then(res => {})
          .catch(() => {})
      }
    },

    moveParentTask(evt) {
      if (
        evt.draggedContext.futureIndex == 0 &&
        this.taskList[0].classId == -1
      ) {
        return false
      }
    },

    /**
     * @description: 任务拖拽
     * @param {*} evt
     * @return {*}
     */
    moveEndSonTask(evt) {
      document.dispatchEvent(new MouseEvent('mouseup'))
      if (evt) {
        const fromId = evt.from.id
        const toId = evt.to.id

        // 如果没有进行移动 不做处理
        if (fromId == toId && evt.oldIndex == evt.newIndex) {
          return
        }

        const fromTask = this.taskList.filter(item => {
          return item.classId == fromId
        })[0]
        const fromList = fromTask.list
        this.updateTaskListCheckNum(fromTask)

        const toTask = this.taskList.filter(item => {
          return item.classId == toId
        })[0]
        const toList = toTask.list
        this.updateTaskListCheckNum(toTask)

        let params = {}
        if (fromId == toId) {
          params = {
            toList: toList.map(item => item.taskId),
            toId: toId
          }
        } else {
          params = {
            fromList: fromList.map(item => item.taskId),
            fromId: fromId,
            toList: toList.map(item => item.taskId),
            toId: toId
          }
        }
        workTaskUpdateOrderAPI(params)
          .then(res => {})
          .catch(() => {})
      }
    },

    /**
     * @description: 更新勾选数字
     * @param {*} task
     * @return {*}
     */
    updateTaskListCheckNum(task) {
      task.checkedNum = task.list.filter(item => {
        return item.checked
      }).length
    },

    /**
     * 勾选
     */
    // checkboxChange(element, value, fromIndex) {
    //   workTaskStatusSetAPI({
    //     taskId: element.taskId,
    //     status: element.checked ? 5 : 1
    //   })
    //     .then(res => {
    //       this.changeListCompleteOrder(element, value, fromIndex, this.showType == 'board')
    //       this.updateTaskListCheckNum(value)
    //     })
    //     .catch(() => {
    //       element.checked = !element.checked
    //       this.updateTaskListCheckNum(value)
    //     })
    // },

    /**
     * 更改已完成顺序
     * @param {*} val
     * @param {*} index
     * @param {*} upload 是否提交排序，负责人只排序不提交
     */
    changeListCompleteOrder(element, value, fromIndex, upload = true) {
      console.log(upload)
      let toIndex = null

      if (element.checked) {
        const newElement = value.list[fromIndex]
        if (newElement.taskId != element.taskId) {
          fromIndex = null
        }
        for (let index = value.list.length - 1; index >= 0; index--) {
          const taskItem = value.list[index]
          if (fromIndex === null) {
            if (element.taskId == taskItem.taskId) {
              fromIndex = index
            }
          }
          if (!taskItem.checked) {
            toIndex = index
          }

          if (fromIndex !== null && toIndex !== null) {
            break
          }
        }

        if (toIndex < fromIndex) {
          toIndex = null
        }
      } else {
        const newElement = value.list[fromIndex]
        if (newElement.taskId != element.taskId) {
          fromIndex = null
        }
        for (let index = value.list.length - 1; index >= 0; index--) {
          const taskItem = value.list[index]
          if (fromIndex === null) {
            if (element.taskId == taskItem.taskId) {
              fromIndex = index
            }
          }

          if (fromIndex !== null) {
            break
          }
        }
        toIndex = 0
      }

      if (toIndex != null && toIndex >= 0) {
        if (toIndex != fromIndex) {
          value.list.splice(fromIndex, 1)
          value.list.splice(toIndex, 0, element)
          if (upload) {
            workTaskUpdateOrderAPI({
              toList: value.list.map(item => item.taskId),
              toId: value.classId
            })
              .then(res => {})
              .catch(() => {})
          }
        }
      }
    },

    /**
     * @description: 删除列表
     * @param {*} val
     * @param {*} index
     * @return {*}
     */
    delectTaskListClick(val, index) {
      this.$confirm('您确定要删除?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => {
          this.taskList.splice(index, 1)
          // workTaskclassDeleteAPI({
          //   classId: val.classId,
          //   workId: this.workId
          // })
          //   .then(res => {
          //     this.taskList.splice(index, 1)
          //     this.$message.success('删除成功')
          //     val.taskHandleShow = false // 隐藏弹出框
          //   })
          //   .catch(() => {})
        })
        .catch(() => {
          this.$message({
            type: 'info',
            message: '已取消删除'
          })
        })
    },

    /**
     * 归档已完成任务
     */
    // archiveTaskListClick(val) {
    //   const request = {
    //     board: workTaskArchiveTaskAPI,
    //     user: workTaskArchiveOwnerTaskAPI
    //   }[this.showType || 'board']
    //   const params = {
    //     workId: this.workId
    //   }
    //   params[this.tableField] = val[this.tableField]
    //   request(params).then(res => {
    //     this.$message.success('归档成功')
    //     val.taskHandleShow = false
    //     this.getList()
    //   })
    // },

    /**
     * @description: 重命名
     * @param {*} val
     * @param {*} index
     * @return {*}
     */
    renameTaskListClick(val, index) {
      this.editTaskListName = val[this.tableNameField]
      val.taskHandleShow = false
      val.renameShow = true

      this.$nextTick(() => {
        this.$refs.text[0].focus()
      })
    },

    /**
     * @description: 重命名 -- 提交
     * @param {*} val
     * @param {*} index
     * @return {*}
     */
    renameTaskListSubmit(val, index) {
      this.taskList[index][this.tableNameField] = this.editTaskListName
      val.renameShow = false
      this.editTaskListName = ''
    },

    /**
     * @description: 新建列表提交
     * @param {*} name
     * @return {*}
     */
    createTaskListSave(name) {
      this.taskList.push({
        boardName: name || '未命名列',
        eventId: this.eventId,
        statusList: [],
        sorting: this.taskList.length
      })
      this.createTaskListShow = false
      this.taskListName = ''
    },

    /**
     * @description: 新建任务
     * @return {*}
     */
    addSubTaskSuc() {
      this.createSubTaskClassId = 'hidden'
      this.getList()
    },

    /**
     * @description: 创建新任务
     * @param {*} item
     * @return {*}
     */
    createSubTaskClick(item) {
      this.createAction = {
        type: 'save',
        params: {
          workId: this.workId,
          classId: item.classId
        }
      }
      this.taskCreateShow = true
    },

    /**
     * @description: 点击显示详情
     * @param {*} val
     * @param {*} section
     * @param {*} index
     * @return {*}
     */
    showDetailView(val, section, index) {
      this.taskID = val.taskId
      this.detailIndex = index
      this.detailSection = section
      this.taskDetailShow = true
    },

    /**
     * @description: 详情操作
     * @param {*} data
     * @return {*}
     */
    detailHandle(data) {
      if (data.index == 0 || data.index) {
        // 是否完成勾选
        if (data.type == 'title-check') {
          const sectionItem = this.taskList[data.section]
          this.$set(sectionItem.list[data.index], 'checked', data.value)
          if (data.value) {
            sectionItem.checkedNum++
          } else {
            sectionItem.checkedNum--
          }
          this.$set(sectionItem, 'checkedNum', sectionItem.checkedNum)
        } else if (data.type == 'delete') {
          this.taskList[data.section].list.splice(data.index, 1)
        } else if (data.type == 'change-stop-time') {
          const stopTime = new Date(data.value).getTime() / 1000 + 86399
          if (stopTime > new Date().getTime() / 1000) {
            this.taskList[data.section].list[data.index].isEnd = false
          } else {
            this.taskList[data.section].list[data.index].isEnd = true
          }
          this.taskList[data.section].list[data.index].stopTime = data.value
        } else if (data.type == 'change-priority') {
          this.taskList[data.section].list[data.index].priority = data.value.id
        } else if (data.type == 'change-name') {
          this.taskList[data.section].list[data.index].name = data.value
        } else if (data.type == 'change-comments') {
          const commentCount = this.taskList[data.section].list[data.index]
            .commentCount
          if (data.value == 'add') {
            this.taskList[data.section].list[data.index].commentCount =
              commentCount + 1
          } else {
            this.taskList[data.section].list[data.index].commentCount =
              commentCount - 1
          }
        } else if (data.type == 'change-sub-task') {
          this.taskList[data.section].list[data.index].childWCCount =
            data.value.subdonecount
          this.taskList[data.section].list[data.index].childAllCount =
            data.value.allcount
        } else if (data.type == 'change-main-user') {
          this.taskList[data.section].list[data.index].mainUser = data.value
        } else if (data.type == 'change-label') {
          this.taskList[data.section].list[data.index].labelList = data.value
        }
      }
    },

    /**
     * @description: 关闭详情页
     * @return {*}
     */
    closeBtn() {
      this.taskDetailShow = false
    },

    /**
     * @description: 点击空白处关闭详情
     * @param {*} e
     * @return {*}
     */
    taskShowHandle(e) {
      if (
        this.$refs.particulars &&
        !this.$refs.particulars.$el.contains(e.target)
      ) {
        let hidden = true
        const items = document.getElementsByClassName('board-item')
        for (let index = 0; index < items.length; index++) {
          const element = items[index]
          if (element.contains(e.target)) {
            hidden = false
            break
          }
        }
        this.taskDetailShow = !hidden
      }
    }
  }
}
