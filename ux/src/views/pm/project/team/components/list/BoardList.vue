<template>
  <div
    class="board-list">
    <div
      v-loading="loading"
      class="board-column-content-parent">
      <div
        v-for="(item, index) in taskList"
        :key="index"
        class="board-column">
        <flexbox
          orient="vertical"
          align="stretch"
          class="board-column-wrapper ignoreClass">
          <div class="board-column-header">
            <div>
              <span class="text">{{ item[tableNameField] }}&nbsp;&nbsp;{{ item.children.length }} </span>
            </div>
          </div>
          <div
            v-show="draging"
            class="board-column-content is-flex">
            <div
              v-for="(element, i) in item.statusList"
              ref="taskRow"
              :key="i"
              class="target-item"
              :class="{'is-hover': element.hover}"
              @dragenter="targetDragenter($event, element)"
              @dragleave="targetDragleave($event, element)"
              @dragover="targetDragover($event, element)"
              @drop="targetDragDrop($event, element)">{{ element.statusName }}</div>
          </div>
          <div
            v-show="!draging"
            class="board-column-content">
            <div
              v-for="(element, i) in item.children"
              ref="taskRow"
              :key="i"
              draggable="true"
              class="board-item"
              @dragstart="dragstart($event, element)"
              @dragend="dragend">
              <!-- :style="{'border-color': getPriorityColor(element.priority).color}" -->
              <flexbox align="stretch">
                <draggable-item
                  :item="element"
                  @click.native="enterDetail(element, item.children, index, i)"
                />
              </flexbox>
            </div>
          </div>
        </flexbox>
      </div>
    </div>

    <!-- 事项详情 -->
    <item-detail
      v-if="taskDetailShow"
      :id="taskId"
      ref="particulars"
      :detail-index="detailIndex"
      :no-listener-class="['el-table__body']"
      @update-list="handleUpdateList"
      @close="taskDetailShow = false" />
  </div>
</template>

<script>
import {
  queryViewBoardListAPI,
  queryViewTaskChildBoardListAPI,
  projectBoardTaskSetStatusAPI
} from '@/api/pm/projectTask'

import DraggableItem from '@/views/pm/project/team/planning/components/DraggableItem'
import ItemDetail from '@/views/pm/project/team/itemDetail'

import BoardMixin from '../Board'
import { itemTypeMap } from '@/views/pm/data'

export default {
  name: 'BoardList',
  components: {
    DraggableItem,
    ItemDetail
  },
  mixins: [BoardMixin],
  inject: ['rootTabs'],
  props: {
    itemType: String,
    list: {
      type: Array,
      default() {
        return []
      }
    },
    search: String,
    iterationId: String
  },
  data() {
    return {
      loading: false,
      taskList: [],
      // 拖拽逻辑
      draging: false, // 标记是否拖拽中

      taskId: '',
      detailIndex: -1,
      taskDetailShow: false,

      filterList: [],

      // 缓存上一次已点击的事项
      cacheClickedInfo: new Map()
    }
  },
  watch: {
    'rootTabs.currentName'(val) {
      if (val === this.itemType) {
        this.firstLoad()
      }
    }
  },
  mounted() {
    this.firstLoad()
  },
  methods: {
    firstLoad() {
      this.clearCache()
      this.clearFilterCondition()
      this.getData()
    },

    /**
     * @description: 重置点击信息
     * @return {*}
     */
    clearCache() {
      this.cacheClickedInfo.clear()
    },

    /**
     * @description: 清除筛选条件
     * @return {*}
     */
    clearFilterCondition() {
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
     * @description: 数据获取
     * @param {*} id
     * @return {*}
     */
    getData(filterList, clearFilter) {
      this.loading = true
      clearFilter && this.clearFilterCondition()
      if (filterList && filterList.length) {
        this.filterList = filterList || []
      }
      const params = {
        projectId: this.$route.params.id,
        taskType: itemTypeMap[this.itemType],
        search: this.search,
        ...this.getFilterParams()
      }
      if (this.iterationId) {
        params.belongIterationId = this.iterationId
        params.type = itemTypeMap[this.itemType]
      }
      const request = this.iterationId ? queryViewTaskChildBoardListAPI : queryViewBoardListAPI
      request(params).then(res => {
        const resData = res.data
        resData.forEach(bigItem => {
          const children = []
          // 合并状态下的任务用于展示
          bigItem.statusList.forEach(statusItem => {
            (statusItem.taskVOList || []).forEach(item => {
              item.statusName = item.boardStatusName
              item.clicked = false
              children.push(item)
            })
          })
          bigItem.children = children
        })
        this.taskList = resData
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },

    // 拖拽
    /**
     * @description: 拖拽开始
     * @param {*} e
     * @param {*} element
     * @return {*}
     */
    dragstart(e, element) {
      this.draging = true
      e.dataTransfer.effectAllowed = 'move'
      e.dataTransfer.setData('dadaItem', JSON.stringify(element))
      console.log('dragstart----', arguments)
    },

    /**
     * @description: 拖拽结束
     * @return {*}
     */
    dragend() {
      this.draging = false
      this.clearCache()
      console.log('dragend----', arguments)
    },

    /**
     * @description: 拖入到目标
     * @param {*} event
     * @param {*} element
     * @return {*}
     */
    targetDragDrop(e, element) {
      const dragItem = JSON.parse(e.dataTransfer.getData('dadaItem'))
      console.log('targetDragDrop----', dragItem)
      this.setTaskStatus({
        statusId: element.statusId,
        taskId: dragItem.taskId
      })
      return false
    },

    /**
     * @description: 设置任务状态
     * @return {*}
     */
    setTaskStatus(params) {
      this.loading = true
      projectBoardTaskSetStatusAPI(params).then(res => {
        this.$message.success('操作成功')
        this.getData()
      }).catch(() => {
        this.loading = false
      })
    },

    /**
     * @description: 进入目标
     * @param {*} event
     * @param {*} element
     * @return {*}
     */
    targetDragenter(e, element) {
      this.$set(element, 'hover', true)
      console.log('targetDragenter----', element.hover)
    },

    /**
     * @description: 离开目标
     * @param {*} event
     * @param {*} element
     * @return {*}
     */
    targetDragleave(e, element) {
      this.$set(element, 'hover', false)
      console.log('targetDragleave----', element.hover)
    },

    /**
     * @description: 目标经过
     * @param {*} event
     * @param {*} element
     * @return {*}
     */
    targetDragover(e) {
      e.preventDefault()
      console.log('targetDragover----')
    },

    /**
     * @description: 更新数据
     * @return {*}
     */
    handleUpdateList() {
      this.getData()
    },

    /**
     * @description: 进入详情
     * @param {*} item
     * @return {*}
     */
    enterDetail(item, list, parentIndex, itemIndex) {
      if (this.cacheClickedInfo.has(parentIndex)) {
        const preIndex = this.cacheClickedInfo.get(parentIndex)
        list[preIndex].clicked = false
      }
      this.cacheClickedInfo.set(parentIndex, itemIndex)
      item.clicked = true

      this.taskId = item.taskId
      this.detailIndex = this.getObjIndex(list, 'taskId', item.taskId)
      this.taskDetailShow = true
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
    }
  }
}
</script>
<style lang='scss' scoped>
.board-list {
  position: relative;
  height: 100%;
  overflow-x: auto;
  overflow-y: hidden;
  white-space: nowrap;

  .board-column-content-parent {
    position: absolute;
    top: 0;
    right: 0;
    bottom: 0;
    left: 0;
    padding-bottom: 8px;
    overflow-x: auto;
    overflow-y: hidden;
  }

  .board-column {
    display: inline-block;
    width: 266px;
    height: 100%;
    margin-right: 8px;
    overflow: hidden;

    .board-column-wrapper {
      position: relative;
      height: 100%;
      padding: 4px 8px;
      margin-right: 8px;
      vertical-align: top;
      background: $--color-n20;
      border-radius: $--border-radius-base;

      .board-column-header {
        padding: 8px 4px;
        color: $--color-text-primary;

        .text {
          display: inline-block;
          font-size: $--font-size-base;
          color: $--color-black;
        }
      }

      .board-column-content {
        flex: 1;
        overflow: auto;

        .board-item {
          position: relative;
          margin-bottom: 4px;
          overflow: hidden;
          cursor: pointer;
          background-color: white;
          border-radius: $--border-radius-base;
        }

        &.is-flex {
          z-index: 1;
          display: flex;
          flex-direction: column;
        }

        .target-item {
          display: flex;
          flex: 1;
          align-items: center;
          justify-content: center;
          border-right: 2px dashed $--color-n40;
          border-bottom: 2px dashed $--color-n40;
          border-left: 2px dashed $--color-n40;
          border-radius: $--border-radius-base;

          &:first-child {
            border-top: 2px dashed $--color-n40;
            border-top-left-radius: $--border-radius-base;
            border-top-right-radius: $--border-radius-base;
          }

          &:last-child {
            border-bottom-right-radius: $--border-radius-base;
            border-bottom-left-radius: $--border-radius-base;
          }

          &.is-hover {
            color: $--color-b300;
            border: 2px dashed $--color-b300 !important;
          }
        }
      }
    }
  }
}
</style>
