<template>
  <div class="iteration-detail">
    <div class="i-detail-head">
      <div class="i-left">
        <i
          class="wk wk-icon-circle-left back-pre"
          @click="backIterationList" />
        <span class="i-title">{{ newCurrentData.name }}</span>
        <span class="divider" />
        <span
          v-for="(item, index) in tabList"
          :key="index"
          class="tab-item"
          :class="{'iactive': menuIndex == index}"
          @click="handleClick(item, index)">
          {{ item.name }}
        </span>
      </div>
      <div class="i-right">
        <status-tag class="status-btn" :type="newCurrentData.status" />
        <el-tooltip
          effect="dark"
          placement="top"
          content="未规划进迭代并且未完成的事项">
          <i
            data-type="4"
            data-id="9"
            style="margin-right: 8px;"
            class="wk wk-icon-fill-help wk-help-tips" />
        </el-tooltip>
        <el-button
          v-if="newCurrentData.status != 3 && btnTxt && $auth('coordination.editIteration', 'PM')"
          type="primary"
          @click="handleIteration">
          {{ btnTxt }}
        </el-button>
        <el-dropdown
          v-if="dropdownsAuth && dropdownsAuth.length > 0"
          style="margin-left: 8px;"
          trigger="click"
          @command="dropdownCommand">
          <el-button class="dropdown-btn" icon="el-icon-more" />
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item
              v-for="(items, indexs) in dropdownsAuth"
              :key="indexs"
              :icon="items.icon"
              :command="items.command">
              {{ items.name }}
            </el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
      </div>
    </div>
    <component
      :is="tabName"
      :ref="tabName"
      :project-detail="projectDetail"
      :iteration-id="iterationId"
      @refresh="handleRefresh"
    />

    <!-- 创建迭代 -->
    <create-iteration
      v-if="createVisible"
      :visible.sync="createVisible"
      :detail-data="newCurrentData"
      type="edit"
      :item-type="itemType"
      @save-success="handleRefresh"
      @close="createVisible = false" />

    <!-- 开始迭代、完成迭代 -->
    <iteration-flow
      v-if="iterationVisible"
      :visible.sync="iterationVisible"
      :detail-data="newCurrentData"
      :flow-type="flowType"
      @save-success="handleRefresh"
      @close="iterationVisible = false" />
  </div>
</template>

<script>
import {
  delIterationAPI,
  detailIterationAPI
} from '@/api/pm/projectTask'

import StatusTag from '@/views/pm/project/components/StatusTag'
import Item from './item'
import Overview from './overview'
import CreateIteration from '@/views/pm/project/team/CreateIteration'
import IterationFlow from '@/views/pm/project/team/IterationFlow'
export default {
  name: 'IterationDetail',
  components: {
    Item,
    Overview,
    StatusTag,
    CreateIteration,
    IterationFlow
  },
  props: {
    iterationId: [String, Number],
    projectDetail: Object
  },
  data() {
    return {
      dropdowns: [
        { command: 'edit', name: '编辑', icon: 'wk wk-import', auth: 'editIteration' },
        { command: 'del', name: '删除', icon: 'wk wk-import', auth: 'deleteIteration' }
      ],
      tabName: 'Item',
      menuIndex: 0,
      tabList: [
        { name: '事项', tab: 'Item' },
        { name: '概览与统计', tab: 'Overview' }
      ],

      createVisible: false,
      itemType: 'iteration',

      newCurrentData: {},

      iterationVisible: false,
      flowType: ''

    }
  },
  computed: {
    btnTxt() {
      if (this.newCurrentData.status == 1) {
        return '开始迭代'
      } else if (this.newCurrentData.status == 2) {
        return '完成迭代'
      }
      return ''
    },
    // 更多操作权限
    dropdownsAuth() {
      return this.dropdowns.filter(item => {
        return this.$auth(item.auth, 'PM')
      })
    }
  },
  created() {
    this.getDetail()
  },
  mounted() {
    this.$bus.on('refresh-item-or-overview', () => {
      this.refreshItemOrOverview()
    })
  },
  destroyed() {
    this.$bus.off('refresh-item-or-overview')
  },
  methods: {
    /**
     * @description: 编辑与删除
     * @param {*} command
     * @return {*}
     */
    dropdownCommand(command) {
      if (command == 'edit') {
        this.createVisible = true
      } else if (command == 'del') {
        this.$confirm('确认删除么?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          const params = {
            taskId: this.newCurrentData.taskId
          }
          delIterationAPI(params).then(res => {
            this.$message.success('当前迭代已经被删除')
            this.backIterationList()
          }).catch(() => {
            this.$message.error('操作失败')
          })
        }).catch(() => {})
      }
    },

    /**
     * @description: 获取迭代详情
     * @return {*}
     */
    getDetail() {
      const params = {
        taskId: this.iterationId
      }
      detailIterationAPI(params).then(res => {
        this.newCurrentData = res.data
      }).catch(() => {

      })
    },

    /**
     * @description: 获取事项列表
     * @return {*}
     */
    refreshItemList() {
      this.$refs.item.getList()
    },

    /**
     * @description: 返回到迭代列表
     * @return {*}
     */
    backIterationList() {
      const { taskId } = this.$route.query
      if (taskId) {
        this.$router.push({
          query: {
            tab: 'Iterations'
          }
        })
      }

      this.$emit('change', { type: 'main' })
    },

    /**
     * @description: 开始迭代、完成迭代
     * @return {*}
     */
    handleIteration() {
      if (this.newCurrentData.status == 1) {
        this.flowType = 'start'
      } else if (this.newCurrentData.status == 2) {
        this.flowType = 'finish'
      }
      this.iterationVisible = true
    },

    /**
     * @description: 刷新详情
     * @param {*} data
     * @return {*}
     */
    handleRefresh(data) {
      const { type } = data
      if (type == 'flow') {
        this.iterationVisible = false
      } else {
        this.createVisible = false
      }
      this.getDetail()
      this.refreshItemList()
    },

    refreshItemOrOverview() {
      if (this.tabName === 'Item') {
        this.$refs.Item.updateListOrBoard(true)
      } else {
        this.$refs.Overview.getAllInfo()
      }
    },

    /**
     * @description: 切换选项卡
     * @param {*} item
     * @param {*} index
     * @return {*}
     */
    handleClick(item, index) {
      this.tabName = item.tab
      this.menuIndex = index
    }
  }
}
</script>
<style lang='scss' scoped>
.iteration-detail {
  height: calc(100% - 44px);
}

.i-detail-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 41px;
  margin-bottom: 16px;
  border-bottom: 2px solid $--border-color-base;

  .i-left {
    display: flex;
    align-items: center;
    justify-content: flex-start;
  }
}

.back-pre {
  margin-right: 10px;
  cursor: pointer;
}

.i-title {
  margin-right: 10px;
}

.tab-item {
  margin-right: 24px;
  font-size: 14px;
  cursor: pointer;
}

.iactive {
  font-size: 16px;
  font-weight: 700;
  color: $--color-primary;
}

.status-btn /deep/ .status-name {
  padding: 4px 8px;
  font-size: 14px;
}

.divider {
  display: inline-block;
  width: 1px;
  height: 16px;
  margin-right: 22px;
  margin-left: 16px;
  background-color: $--badge-background-color;
}
</style>
