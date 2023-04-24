<!-- 待规划 -->
<template>
  <div class="plan-wrap">
    <div class="filter-wrapper">
      <wk-table-header
        :search.sync="search"
        :props="tableHeaderProps.props"
        :filter-header-props="tableHeaderProps.filterHeaderProps"
        @event-change="tableHeaderHandle">
        <el-select
          v-model="layout"
          placeholder="请选择"
          @change="changeLayout">
          <el-option
            v-for="item in showList"
            :key="item.value"
            :label="item.label"
            :value="item.value">
            {{ item.label }}
          </el-option>
        </el-select>
      </wk-table-header>

      <div class="my-task-body" :style="styleObj">
        <div
          ref="plan"
          v-scrollx="{ ignoreClass :['ignoreClass']}"
          class="content-box"
          :class="layout == '1' ? ['two', 'two-column'] : ['full-row']">
          <div v-loading="planLoading" class="back-log" :class="[layout == '1' ? 'two-width': 'one-width']">
            <div
              ref="wrap"
              class="board"
              :class="[
                heightClass,
                !createMatterAuth ? 'list-bottom' : ''
              ]">
              <div ref="header" class="first-header">
                <span class="title">Backlog</span>
                <span class="num">共{{ planList.length }}个事项</span>
                <span class="divider" />
                <el-tooltip
                  effect="dark"
                  placement="top"
                  content="未规划进迭代并且未完成的事项">
                  <i
                    data-type="4"
                    data-id="9"
                    class="wk wk-icon-fill-help wk-help-tips" />
                </el-tooltip>
              </div>
              <div
                class="drag-wrap"
                :style="{height: listHeight}">
                <draggable
                  :id="-1"
                  ref="list"
                  :list="planList"
                  :disabled="!createMatterAuth"
                  :options="{
                    group: 'mission',
                    forceFallback: false,
                    dragClass: 'sortable-drag'
                  }"
                  filter=".empty-log"
                  :class="{ 'is-empty': planList.length === 0 }"
                  class="board-column-content"
                  :move="draggableChange"
                  @end="moveEndTask($event, planList)"
                  @sort="handleBacklogSort">
                  <item
                    v-for="(item, index) in planList"
                    :key="index"
                    :item="item"
                    :iteration-list="iterationList"
                    @click.native="enterDetail(item, planList, 'plan', index)"
                    @save-success="getPlanIterationList"
                    @dragstart.native="dragstart($event, item)" />

                  <!-- 列表为空时 -->
                  <div v-if="planList.length == 0" class="empty-log">
                    <div style="text-align: center;">
                      <div class="sup-title">当前没有未完成的事项</div>
                      <div class="sub-title">创建事项，并调整事项的顺序，拖动事项将其规划进迭代。</div>
                    </div>
                  </div>
                </draggable>
              </div>
              <!-- 创建事项 -->
              <create-item-short
                v-if="createMatterAuth"
                ref="create"
                class="create-item float-bottom"
                :class="{'float-create': isPlanFloat}"
                iteration-id=""
                @save-success="handleSuccess"
                @change-height="changeHeight" />
            </div>
          </div>
          <div
            ref="right"
            v-loading="iterationLoading"
            class="iteration-wrap"
            :class="[layout == '1' ? 'two-width': 'one-width']">
            <div class="iteration-list" :style="{height: iterationHeight}">
              <div ref="iteration" class="real-wrap">
                <div
                  v-for="(item, index) in iterationList"
                  :key="index"
                  :class="[
                    'board',
                    index == iterationList.length - 1 ? 'last-iteration' : '',
                    !createMatterAuth && item.collapse ? 'list-bottom' : ''
                  ]">
                  <div
                    class="other-header"
                    :class="{'is-hover': item.hover && !item.collapse}"
                    @dragenter="iterationDragenter($event, item)"
                    @dragleave="iterationDragleave($event, item)"
                    @dragover="iterationDragover($event, item)"
                    @drop="iterationDragDrop($event, item)"
                    @click="handleCollapse(item, index)">
                    <div class="h-left">
                      <i :class="[!item.collapse ? 'el-icon-arrow-right': 'el-icon-arrow-down']" />
                      <span class="title">{{ item.name }}</span>
                      <span class="num">共{{ item.belongIterationTaskCount || 0 }}个事项</span>
                      <span class="divider" />
                      <span @click.stop>
                        <el-dropdown
                          v-if="dropdowns(item).length > 0"
                          trigger="click"
                          @command="dropdownCommand($event, item)">
                          <el-button class="dropdown-btn" icon="el-icon-more" />
                          <el-dropdown-menu slot="dropdown">
                            <el-dropdown-item
                              v-for="(items, indexs) in dropdowns(item)"
                              :key="indexs"
                              :command="items.command">
                              {{ items.name }}
                            </el-dropdown-item>
                          </el-dropdown-menu>
                        </el-dropdown>
                      </span>
                    </div>
                    <div class="h-left">
                      <span
                        v-if="item.startTime"
                        class="h-date">
                        {{ handleYearMonthDay(item.startTime) }}-{{ handleYearMonthDay(item.stopTime) }}
                      </span>
                      <status-tag
                        v-if="item.status"
                        :type="item.status" />
                    </div>
                  </div>
                  <div
                    v-if="item.collapse"
                    class="drag-wrap">
                    <draggable
                      :id="index"
                      v-model="item.list"
                      :disabled="!createMatterAuth"
                      :options="{
                        group: 'mission',
                        forceFallback: false,
                        dragClass: 'sortable-drag'
                      }"
                      filter=".empty-log"
                      :class="{ 'is-empty': item.list.length === 0 }"
                      class="board-column-content"
                      :data-set="item.taskId"
                      :move="draggableChange"
                      @end="moveEndTask($event, item.list)">
                      <item
                        v-for="(cItem, cIndex) in item.list"
                        :key="cIndex"
                        :item="cItem"
                        @click.native="enterDetail(cItem, item.list, `iteration${index}`, cIndex)"
                        @save-success="getPlanIterationList"
                        @dragstart.native="dragstart($event, cItem)" />
                      <!-- 列表为空时 -->
                      <div v-if="item.list.length == 0" class="empty-item">
                        从 Backlog 拖动事项到此处规划该迭代
                      </div>
                    </draggable>
                  </div>
                  <!-- 创建事项 -->
                  <create-item-short
                    v-if="item.collapse && createMatterAuth"
                    class="create-item float-bottom iteration-item-create"
                    :iteration-index="index"
                    :iteration-id="item.taskId"
                    @save-success="handleSuccess" />
                </div>
              </div>
            </div>
            <!-- 创建迭代 -->
            <create-iteration-short
              v-if="editAuth"
              ref="die"
              class="create-iteration"
              :class="{'float-create': isIterationFloat}"
              @save-success="handleSuccess"
              @change-height="changeHeight" />
          </div>
        </div>
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

    <!-- 编辑迭代 -->
    <create-iteration
      v-if="editIterationVisible"
      :visible.sync="editIterationVisible"
      :detail-data="iterationData"
      type="edit"
      :item-type="itemType"
      @save-success="handleRefresh"
      @close="editIterationVisible = false" />

    <!-- 开始迭代、完成迭代 -->
    <iteration-flow
      v-if="iterationVisible"
      :visible.sync="iterationVisible"
      :detail-data="iterationData"
      :flow-type="flowType"
      @save-success="handleRefresh"
      @close="iterationVisible = false" />
  </div>
</template>

<script>
import {
  workQueryIterationAllItemAPI,
  workQueryPlanItemListAPI,
  workQueryIterationsItemListAPI,
  delIterationAPI,
  detailIterationAPI,
  relationIterationAPI,
  backLogListSortAPI
} from '@/api/pm/projectTask'

import WkTableHeader from '@/components/Page/WkTableHeader'
import CreateItemShort from './components/CreateItemShort'
import CreateIterationShort from './components/CreateIterationShort'
import Item from './components/Item'
import StatusTag from '@/views/pm/project/components/StatusTag'
import ItemDetail from '@/views/pm/project/team/itemDetail'
import CreateIteration from '@/views/pm/project/team/CreateIteration'
import IterationFlow from '@/views/pm/project/team/IterationFlow'

import draggable from 'vuedraggable'
import scrollx from '@/directives/scrollx'
import moment from 'moment'

export default {
  name: 'Planning',
  components: {
    WkTableHeader,
    CreateItemShort,
    CreateIterationShort,
    Item,
    StatusTag,
    draggable,
    ItemDetail,
    CreateIteration,
    IterationFlow
  },
  directives: {
    scrollx
  },
  inject: ['rootTabs'],
  data() {
    return {
      loading: false, // 加载
      itemType: 'Planning',
      search: '',
      tableHeaderProps: {
        props: {
          showFilterView: false, // 不展示高级筛选
          showExportFields: false
        },
        filterHeaderProps: {
          maxTabCount: 0,
          tabSetShow: false,
          searchPlaceholder: '搜索事项'
        }
      },
      layout: '1',
      showList: [
        { label: '双栏展示', value: '1' },
        { label: '单栏展示', value: '2' }
      ],

      planLoading: false,
      iterationLoading: false,

      moveTarget: null,

      iterationList: [],

      planList: [],

      taskId: '',
      detailIndex: -1,
      taskDetailShow: false,

      editIterationVisible: false,
      iterationData: {},

      iterationVisible: false,
      flowType: '',

      calcHeight: 600,
      heightClass: '',
      listHeight: 'auto',
      isPlanFloat: false,

      isIterationFloat: false,
      iterationHeight: 'auto',
      // 缓存迭代列表折叠状态
      cacheCollapseStatus: new Map(),
      // 缓存上一次已点击的事项
      cacheClickedInfo: new Map()
    }
  },
  computed: {
    editAuth() {
      return this.$auth('coordination.editIteration', 'PM')
    },
    deleteAuth() {
      return this.$auth('coordination.deleteIteration', 'PM')
    },
    createMatterAuth() {
      return this.$auth('coordination.editMatters', 'PM')
    },

    styleObj() {
      if (this.layout == '1') {
        return { height: this.calcHeight + 'px' }
      }
      return { height: this.calcHeight + 'px' }
    }
  },
  watch: {
    'rootTabs.currentName'(val) {
      if (val === this.itemType) {
        this.resetCache()
        this.getPlanIterationList()
      }
    }
  },
  mounted() {
    this.calcHeight = document.documentElement.clientHeight - 246

    if (this.rootTabs.currentName == this.itemType) {
      this.resetCache()
      this.getPlanIterationList()
    }

    window.onresize = () => {
      this.calcHeight = document.documentElement.clientHeight - 246
      this.updatePlanListHeight()
      this.updateIterationListHeight()
    }
  },
  methods: {
    /**
     * @description: 重置点击和折叠信息
     * @return {*}
     */
    resetCache() {
      this.cacheCollapseStatus.clear()
      this.cacheClickedInfo.clear()
    },

    /**
     * @description: 全部列表
     * @return {*}
     */
    getPlanIterationList() {
      // 规划列表
      this.getPlanList()
      // 迭代列表
      this.getIterationsList()
    },

    /**
     * @description: 未完成事项列表
     * @return {*}
     */
    getPlanList() {
      this.planLoading = true
      const params = {
        type: 0,
        pageType: 0,
        projectId: this.$route.params.id,
        name: this.search
      }
      workQueryPlanItemListAPI(params).then(res => {
        this.planList = (res.data.list || []).map(item => {
          item.clicked = false
          return item
        })
        this.planLoading = false
        this.updatePlanListHeight()
      }).catch(() => {
        this.planLoading = false
      })
    },

    /**
     * @description: 更新待规划列表高度
     * @return {*}
     */
    updatePlanListHeight() {
      this.$nextTick(() => {
        if (this.layout == '1' && this.$refs.wrap?.offsetHeight == this.$refs.plan?.offsetHeight) {
          const parentHeight = this.$refs.wrap.offsetHeight
          const headerHeight = this.$refs.header.offsetHeight
          const listHeight = this.$refs.list?.$el.offsetHeight
          const createHeight = this.$refs.create?.$el.offsetHeight || 0
          const realHeight = headerHeight + listHeight + createHeight
          if (realHeight > parentHeight) {
            this.isPlanFloat = true
            const changeListHeight = parentHeight - (headerHeight + createHeight)
            this.listHeight = changeListHeight + 'px'
            this.heightClass = 'resize-height'
          } else {
            this.isPlanFloat = false
            this.listHeight = 'auto'
            this.heightClass = ''
          }
        } else {
          this.isPlanFloat = false
          this.listHeight = 'auto'
          this.heightClass = ''
        }
      })
    },

    /**
     * @description: 更新迭代列表高度
     * @return {*}
     */
    updateIterationListHeight() {
      this.$nextTick(() => {
        if (this.layout == '1' && this.$refs.right?.offsetHeight == this.$refs.plan?.offsetHeight) {
          const parentHeight = this.$refs.plan.offsetHeight
          const listHeight = this.$refs.iteration.offsetHeight
          const createHeight = this.$refs.die?.$el.offsetHeight || 0
          const realHeight = listHeight + createHeight
          if (realHeight > parentHeight) {
            this.isIterationFloat = true
            const changeListHeight = parentHeight - createHeight
            this.iterationHeight = changeListHeight + 'px'
          } else {
            this.isIterationFloat = false
            this.iterationHeight = 'auto'
          }
        } else {
          this.isIterationFloat = false
          this.iterationHeight = 'auto'
        }
      })
    },

    /**
     * @description: 迭代列表
     * @return {*}
     */
    getIterationsList() {
      this.iterationLoading = true
      const params = {
        type: 0,
        pageType: 0,
        projectId: this.$route.params.id
      }
      workQueryIterationsItemListAPI(params).then(res => {
        this.iterationList = (res.data.list || []).map((item, index) => {
          if (this.cacheCollapseStatus.has(item.taskId)) {
            this.getAllItem(item.taskId, index)
            item.collapse = true
          } else {
            item.collapse = false
          }
          item.list = []
          return item
        })
        this.iterationLoading = false
        this.updateIterationListHeight()
      }).catch(() => {
        this.iterationLoading = false
      })
    },

    /**
     * @description: 迭代操作选择项
     * @param {*} data
     * @return {*}
     */
    dropdowns(data) {
      if (!this.editAuth && !this.deleteAuth) return []
      if (!this.editAuth && this.deleteAuth) return [{ command: 'del', name: '删除迭代' }]
      const options = []
      if (data.status == 1) {
        options.push({ command: 'start', name: '开始迭代' })
      } else if (data.status == 2) {
        options.push({ command: 'finish', name: '完成迭代' })
      }
      let editDel = []
      if (!this.deleteAuth) {
        editDel = [
          { command: 'edit', name: '编辑迭代' }
        ]
      } else {
        editDel = [
          { command: 'edit', name: '编辑迭代' },
          { command: 'del', name: '删除迭代' }
        ]
      }
      options.push(...editDel)
      return options
    },

    /**
     * @description: 快捷新建结果
     * @param {*} data
     * @return {*}
     */
    handleSuccess(data) {
      const { createType, id, index } = data
      if (createType == 'log') {
        this.getPlanList()
      } else if (createType == 'iteration-item') {
        this.getAllItem(id, index)
      } else if (createType == 'iteration') {
        this.getIterationsList()
      }
    },

    /**
     * @description: 更改创建状态时更新列表高度
     * @param {*} type
     * @return {*}
     */
    changeHeight(type) {
      if (type === 'log') {
        this.updatePlanListHeight()
      } else if (['iteration', 'iteration-item'].includes(type)) {
        this.updateIterationListHeight()
      }
    },

    /**
     * @description: 获取迭代下所有事项
     * @param {*} id
     * @param {*} index
     * @return {*}
     */
    getAllItem(id, index) {
      const params = {
        belongIterationId: id,
        pageType: 0,
        name: this.search
      }
      workQueryIterationAllItemAPI(params).then(res => {
        const list = (res.data.list || []).map(item => {
          item.clicked = false
          return item
        })
        this.$set(this.iterationList[index], 'list', list)
        this.updateIterationListHeight()
      }).catch(() => {

      })
    },

    /**
     * @description: 更新数据
     * @return {*}
     */
    handleUpdateList() {
      this.getPlanIterationList()
    },

    /**
     * @description: 是否折叠
     * @param {*} item
     * @param {*} index
     * @return {*}
     */
    handleCollapse(item, index) {
      if (!item.collapse) {
        this.getAllItem(item.taskId, index)
      }

      this.iterationList[index].collapse = !item.collapse
      if (item.collapse) {
        this.cacheCollapseStatus.set(item.taskId, item.collapse)
      } else {
        this.cacheCollapseStatus.delete(item.taskId)
      }
    },

    /**
     * @description: 下拉操作
     * @param {*} command
     * @param {*} data
     * @return {*}
     */
    dropdownCommand(command, data) {
      if (command == 'edit') {
        this.iterationData = data
        this.editIterationVisible = true
      } else if (command == 'del') {
        this.$confirm('确认删除么?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          const params = {
            taskId: data.taskId
          }
          delIterationAPI(params).then(res => {
            this.$message.success('当前迭代已经被删除')
            this.getPlanIterationList()
          }).catch(() => {
            this.$message.error('操作失败')
          })
        }).catch(() => {})
      } else if (['start', 'finish'].includes(command)) {
        this.iterationData = data
        this.flowType = command
        this.iterationVisible = true
      }
    },

    /**
     * @description: 获取迭代详情
     * @param {*} iterationId
     * @return {*}
     */
    getIterationDetail(iterationId) {
      const params = {
        taskId: iterationId
      }
      detailIterationAPI(params).then(res => {
        // this.newCurrentData = res.data
      }).catch(() => {

      })
    },

    /**
     * @description: 标准刷新，供父组件调用使用
     * @return {*}
     */
    refresh(isClearFilter) {
      isClearFilter && (this.search = '')
      this.getPlanIterationList()
    },

    /**
     * @description: 刷新迭代
     * @param {*} data
     * @return {*}
     */
    handleRefresh(data) {
      const { type } = data
      if (type == 'flow') {
        this.iterationVisible = false
      } else {
        this.editIterationVisible = false
      }
      this.getPlanIterationList()
    },

    /**
     * @description: 处理时间年月日格式
     * @param {*} date
     * @return {*}
     */
    handleYearMonthDay(date) {
      if (date) {
        return moment(date).format('YYYY-MM-DD')
      }
      return ''
    },

    /**
     * @description: 头部操作
     * @param {*} type
     * @param {*} data
     * @return {*}
     */
    tableHeaderHandle(type, data) {
      if (type == 'search') {
        this.search = data
        this.getPlanIterationList()
      }
    },

    /**
     * @description: 更改布局时重新计算高度
     * @return {*}
     */
    changeLayout() {
      this.updatePlanListHeight()
      this.updateIterationListHeight()
    },

    /**
     * @description: 拖动
     * @param {*} evt
     * @return {*}
     */
    draggableChange(evt, iterationItem) {
      this.moveTarget = evt.draggedContext.element
      if (evt.related && (evt.related.className.includes('empty-log') ||
        evt.related.className.includes('empty-item'))) {
        // 插入到空数据布局之前，控制空数据隐藏
        return -1
      }
    },

    /**
     * @description: 移动
     * @param {*} evt
     * @param {*} fromList
     * @return {*}
     */
    moveEndTask(evt, fromList) {
      if (evt.pullMode) {
        const params = {
          taskIds: [this.moveTarget.taskId],
          belongIterationId: evt.to.dataset.set
        }

        relationIterationAPI(params).then(res => {
          this.$message.success('操作成功')
          this.getPlanIterationList()
        }).catch(() => {})
      }
    },

    /**
     * @description: backlog排序
     * @param {*} newIndex
     * @param {*} oldIndex
     * @return {*}
     */
    handleBacklogSort({ newIndex, oldIndex }) {
      const sortList = this.planList.map((item, index) => {
        // if ([newIndex, oldIndex].includes(index)) {
        //   return {
        //     taskId: item.taskId,
        //     sortNum: item.sortNum || '0'
        //   }
        // }
        return item.taskId
      })

      const params = {
        projectId: this.$route.params.id,
        sortList
      }

      backLogListSortAPI(params).then(res => {
        this.getPlanList()
      }).catch(() => {

      })
    },

    /**
     * @description: 进入详情
     * @param {*} item
     * @return {*}
     */
    enterDetail(item, list, type, index) {
      if (this.cacheClickedInfo.has(type)) {
        const preIndex = this.cacheClickedInfo.get(type)
        list[preIndex].clicked = false
      }
      this.cacheClickedInfo.set(type, index)
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
    },

    /**
     * @description: 拖入到迭代
     * @param {*} e
     * @param {*} item
     * @return {*}
     */
    iterationDragenter(e, element) {
      this.$set(element, 'hover', true)
    },

    /**
     * @description: 离开目标
     * @param {*} event
     * @param {*} element
     * @return {*}
     */
    iterationDragleave(e, element) {
      this.$set(element, 'hover', false)
    },

    /**
     * @description: 目标经过
     * @param {*} event
     * @param {*} element
     * @return {*}
     */
    iterationDragover(e) {
      e.preventDefault()
    },

    /**
     * @description: 拖拽开始
     * @param {*} e
     * @param {*} element
     * @return {*}
     */
    dragstart(e, element) {
      e.dataTransfer.effectAllowed = 'move'
      e.dataTransfer.setData('dadaItem', JSON.stringify(element))
    },

    /**
     * @description: 拖入到目标
     * @param {*} event
     * @param {*} element
     * @return {*}
     */
    iterationDragDrop(e, element) {
      const dragItem = JSON.parse(e.dataTransfer.getData('dadaItem'))
      const params = {
        taskIds: [dragItem.taskId],
        belongIterationId: element.taskId
      }

      relationIterationAPI(params).then(res => {
        this.$message.success('操作成功')
        this.getPlanIterationList()
      }).catch(() => {})
      return false
    }
  }
}
</script>
<style lang='scss' scoped>
.plan-wrap {
  height: 100%;

  .filter-wrapper {
    height: 100%;
  }
}

.my-task-body {
  position: relative;
  height: calc(100% - 60px);
  margin-top: 16px;
  overflow-x: auto;
  overflow-y: hidden;
  white-space: nowrap;
}

// 圆角块效果
.board-column-content {
  // height: calc(100% - 70px);
  // overflow-y: auto;
  background-color: #fff;
  border: $--border-base;
  border-radius: $--border-radius-base;

  &.is-empty {
    border: none;
  }
}

.item + .empty-item,
.item + .empty-log {
  display: none;
}

.two {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
}

.two-width {
  width: calc(50% - 8px);
}

.one-width {
  width: 100%;
}

.two-column {
  .back-log {
    height: 100%;

    .board {
      position: relative;
      max-height: 100%;

      .drag-wrap {
        max-height: calc(100% - 38px);
        overflow: hidden;
        overflow-y: auto;

        .board-column-content {
          // height: calc(100% - 26px);
          // max-height: calc(100% - 26px);
        }

        .create-item {
          position: sticky;
          bottom: 0;
          z-index: 13;
          margin-bottom: 10px;
        }
      }
    }
  }

  .iteration-wrap {
    position: relative;
    height: 100%;

    .iteration-list {
      overflow: hidden;
      overflow-y: auto;
    }

    .create-iteration {
      width: 100%;
    }
  }
}

.resize-height {
  height: 100%;
}

.full-row {
  overflow: hidden;
  overflow-y: auto;
}

.content-box {
  height: 100%;

  .board {
    position: relative;
    width: 100%;
    margin-bottom: 16px;
    background-color: $--form-pre-bg-color;
    border-radius: 3px;

    .first-header {
      position: sticky;
      top: 0;
      z-index: 10;
      display: flex;
      align-items: center;
      justify-content: flex-start;
      padding: 10px 16px;
      cursor: pointer;
      background-color: $--form-pre-bg-color;
    }

    .other-header {
      position: sticky;
      top: 0;
      z-index: 13;
      padding: 10px 16px;
      background-color: #f4f5f7;

      &.is-hover {
        color: $--color-b300;

        * {
          pointer-events: none;
        }

        border: 2px dashed $--color-b300;
        border-radius: 2px;
      }

      .h-left {
        display: flex;
        align-items: center;
        justify-content: flex-start;

        .h-date {
          margin-right: 8px;
        }
      }
    }
  }
}

.last-iteration {
  margin-bottom: 0 !important;
}

.title {
  margin-right: 5px;
  margin-left: 5px;
  font-size: 16px;
  font-weight: 700;
  color: $--color-black;
}

.num {
  font-size: 14px;
  color: $--color-text-regular;
}

.divider {
  display: inline-block;
  width: 1px;
  height: 16px;
  margin-right: 4px;
  margin-left: 8px;
  background-color: $--badge-background-color;
}

.dropdown-btn {
  padding: 2px 5px;
  margin-left: 5px;
  background-color: $--badge-background-color;
}

.other-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  cursor: pointer;
}

.h-date {
  font-size: 14px;
  color: $--color-black;
}

.sortable-drag {
  background-color: white;
}

.empty-log {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 150px;
  background: #edeff2;
  border: 2px dashed #cdd1da;
  border-radius: 3px;

  .sup-title {
    margin-bottom: 8px;
    font-size: 15px;
    color: #202d40;
  }

  .sub-title {
    font-size: 13px;
    color: #8592a6;
  }
}

.empty-item {
  font-size: 13px;
  line-height: 32px;
  color: #8592a6;
  text-align: center;
  background: #edeff2;
  border: 2px dashed #cdd1da;
  border-radius: 3px;
}

.drag-wrap {
  padding: 0 16px;
}

.float-bottom {
  width: 100%;
  padding: 10px 16px;
  margin-top: 0;
  background-color: #f4f5f7;
}

.iteration-item-create {
  position: sticky;
  bottom: -1px;
}

.float-create {
  position: absolute;
  bottom: 0;
  left: 0;
}

.list-bottom {
  padding-bottom: 16px;
}
</style>
