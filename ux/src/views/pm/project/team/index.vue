<template>
  <div v-loading="loading" class="pm-project">
    <flexbox class="crm-workbench__hd">
      <div class="title">
        项目协同
        <el-button
          v-if="refreshBtnShow"
          type="icon"
          icon="wk wk-icon-reset2"
          :class="{ 'is-rotation': refreshRotation }"
          @click="titleRefreshClick" />
      </div>
    </flexbox>
    <div v-if="defaultVal == 'main' && projectDetail" class="pm-project-main">
      <el-tabs
        :value="activeName"
        nav-mode="more"
        @tab-click="tabClick">
        <el-tab-pane
          v-for="(item, index) in tabList"
          :key="index"
          lazy
          :label="item.label"
          :name="item.name"
          class="tab-item">
          <template slot="label">
            {{ item.label }}
            <span v-if="isAgility && index == 1" class="divider" />
          </template>
          <component
            :is="item.name"
            :ref="item.name"
            :project-detail="projectDetail"
            @change="getChangeInfo" />
        </el-tab-pane>
      </el-tabs>
      <!-- 新建 -->
      <div v-if="quickAddList.length && createAuth" class="create">
        <el-popover
          v-if="quickAddList.length > 1"
          slot="left"
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
        <el-button
          v-for="(item, index) in quickAddList"
          v-else
          :key="index"
          slot="reference"
          class="common-create-btn"
          type="primary"
          @click.native="handleCreate(item)">创建{{ item.label }}</el-button>
        <el-dropdown
          v-if="moreBtn.length"
          class="add-user-btn"
          @command="optionsClick">
          <el-button class="dropdown-btn" icon="el-icon-more" />
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item
              v-for="(item,index) in moreBtn"
              :key="index"
              :command="item.command">
              {{ item.btnTxt }}
            </el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>

      </div>
    </div>
    <iteration-detail
      v-else-if="defaultVal == 'iteration-detail'"
      :iteration-id="iterationDetail.taskId"
      :project-detail="projectDetail"
      @change="getChangeInfo"
    />

    <!-- 创建迭代 -->
    <create-iteration
      v-if="createVisible"
      :visible.sync="createVisible"
      :item-type="itemType"
      @save-success="handleRefresh"
      @close="createVisible = false" />
    <!-- 创建事项 -->
    <create-item
      v-if="createItemVisible"
      :visible.sync="createItemVisible"
      :item-type="itemType"
      :is-agility="isAgility"
      @save-success="handleRefresh"
      @close="createItemVisible = false" />
    <!-- 批量导入 -->
    <bulk-import-item
      :item-type="activeName"
      :show="bulkImportShow"
      @close="bulkImportShow=false"
      @success="addSuccess" />
  </div>
</template>

<script>
import { projectQueryByIdAPI } from '@/api/pm/manage'
import { itemExportFieldAPI, itemAllExportAPI } from '@/api/pm/projectTask'

import Planning from './planning'
import Iterations from './iterations'
import All from './All'
import Require from './Require'
import Task from './Task'
import Defects from './Defects'
import Gannt from './Gannt'
import Worktime from './Worktime'

import IterationDetail from './iterations/detail'

import CreateIteration from './CreateIteration'
import CreateItem from './CreateItem'
import BulkImportItem from './components/BulkImportItem'

import { mapGetters } from 'vuex'
import { itemTypeMap } from '@/views/pm/data'

export default {
  name: 'ProjectTeamIndex',
  components: {
    Planning,
    Iterations,
    All,
    Require,
    Task,
    Defects,
    Gannt,
    Worktime,
    IterationDetail,
    CreateIteration,
    CreateItem,
    BulkImportItem
  },

  beforeRouteUpdate(to, _, next) {
    const tab = to.query.tab
    if (tab) {
      this.activeName = tab
    }
    next()
  },

  data() {
    return {
      loading: false,
      projectDetail: null, // 项目详情
      activeName: 'Planning',
      refreshRotation: false, // 刷新旋转动画控制
      typeList: ['All', 'Require', 'Task', 'Defects'],

      createVisible: false,

      createItemVisible: false,
      itemType: '',

      defaultVal: 'main',

      iterationDetail: '',

      bulkImportShow: false
    }
  },
  computed: {
    ...mapGetters(['projectAuth']),
    projectId() {
      return this.$route.params.id
    },
    // 是敏捷项目
    isAgility() {
      return this.projectDetail?.type === 2
    },
    tabList() {
      if (!this.projectDetail) return []
      // 1 普通项目 2 敏捷项目
      if (this.projectDetail.type === 1) {
        return [
          { label: '任务', name: 'Task' },
          { label: '甘特图', name: 'Gannt' }
        ]
      } else {
        return [
          { label: '待规划', name: 'Planning' },
          { label: '迭代', name: 'Iterations' },
          { label: '全部事项', name: 'All' },
          { label: '需求', name: 'Require' },
          { label: '任务', name: 'Task' },
          { label: '缺陷', name: 'Defects' },
          { label: '甘特图', name: 'Gannt' },
          { label: '工时', name: 'Worktime' }
        ]
      }
    },
    refreshBtnShow() {
      const tabNames = this.tabList.map(item => item.name)
      return [
        'iteration-detail',
        ...tabNames
      ].includes(this.activeName)
    },
    moreBtn() {
      const options = [{
        command: 'import',
        btnTxt: '导入'
      }, {
        command: 'export',
        btnTxt: '导出'
      }]

      if (this.activeName === 'All') {
        return options.filter(item => item.command === 'export')
      } else if (this.typeList.includes(this.activeName)) {
        return options
      }
      return []
    },
    createAuth() {
      if (this.activeName == 'Iterations') {
        return this.$auth('coordination.editIteration', 'PM')
      } else {
        return this.$auth('coordination.editMatters', 'PM')
      }
    },
    quickAddList() {
      if (!this.createAuth) return []
      var addItems = []
      if (this.activeName == 'Iterations') {
        addItems.push({
          type: 'Iterations',
          label: '迭代'
        })
      } else if (this.activeName == 'All') {
        const itemList = [
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
        ]
        addItems.push(...itemList)
      } else if (this.activeName == 'Require') {
        addItems.push({
          type: 'Require',
          label: '需求'
        })
      } else if (this.activeName == 'Task') {
        addItems.push({
          type: 'Task',
          label: '任务'
        })
      } else if (this.activeName == 'Defects') {
        addItems.push({
          type: 'Defects',
          label: '缺陷'
        })
      }
      return addItems
    }
  },
  watch: {
    projectAuth: {
      handler(val) {
        if (val.loaded) {
          if (!this.$auth('coordination.read', 'PM')) {
            this.$router.replace({
              name: 'participate'
            })
          }
        }
      },
      immediate: true,
      deep: true
    }
  },

  created() {
    // tab 是切换的导航位置，获取详情后会二次校准
    const { tab } = this.$route.query
    if (tab) {
      this.activeName = tab
    }

    this.getDetail()
  },
  methods: {
    /**
     * @description: 获取项目详情
     * @return {*}
     */
    getDetail() {
      this.loading = true
      projectQueryByIdAPI({ projectId: this.projectId })
        .then(res => {
          this.projectDetail = res.data || {}
          const query = this.$route.query
          if (query.type === 'iteration-detail') {
            this.activeName = 'Iterations'
            this.iterationDetail = { taskId: query.taskId }
            this.defaultVal = query.type
          } else {
            this.activeName = this.isAgility
              ? (this.$route.query.type
                ? (this.$route.query.tab || this.$route.query.type) // 有tab优先展示tab
                : (this.$route.query.tab || 'Planning'))
              : 'Task'
          }

          this.loading = false
        }).catch(() => {
          this.loading = false
        })
    },

    /**
     * @description: 详情tab切换
     * @return {*}
     */
    tabClick(tab) {
      this.$router.push({
        name: this.$route.name,
        params: this.$route.params,
        query: {
          ...this.$route.query,
          tab: tab.name
        }
      })
    },

    /**
     * @description: 数据刷新
     * @return {*}
     */
    titleRefreshClick() {
      this.refreshRotation = true
      setTimeout(() => {
        this.refreshRotation = false
      }, 1000)
      this.handleRefresh(true)
    },

    /**
     * @description: 创建迭代事项保存回调
     * @return {*}
     */
    handleRefresh(isClearFilter) {
      if (this.defaultVal === 'iteration-detail') {
        this.$bus.emit('refresh-item-or-overview')
      } else {
        if (this.activeName == 'Iterations') {
          this.createVisible = false
        } else {
          this.createItemVisible = false
        }

        this.$refs[this.activeName][0].refresh(isClearFilter)
      }
    },

    /**
     * @description: 修改迭代
     * @param {*} val
     * @return {*}
     */
    getChangeInfo(val) {
      this.defaultVal = val.type
      if (val.type == 'iteration-detail') {
        this.iterationDetail = val.row
      }
    },

    /**
     * @description: 新建
     * @param {*} item
     * @return {*}
     */
    handleCreate(item) {
      if (item.type == 'Iterations') {
        this.itemType = item.type
        this.createVisible = true
      } else if (['Require', 'Task', 'Defects'].includes(item.type)) {
        this.itemType = item.type
        this.createItemVisible = true
      }
    },

    /**
     * @description: 导入导出
     * @param {*} command
     * @return {*}
     */
    optionsClick(command) {
      if (command === 'import') {
        this.bulkImportShow = true
      } else if (command === 'export') {
        let queryParams = {}
        const listInstance = this.$refs[this.activeName][0].$refs[this.activeName]
        const { layout, name, page, limit, getFilterParams } = listInstance
        if (layout == 1) {
          queryParams = {
            projectId: this.$route.params.id,
            name,
            page,
            limit,
            ...getFilterParams()
          }
        } else if (layout == 3) {
          const filterParams = listInstance.$refs.board.getFilterParams
          queryParams = {
            projectId: this.$route.params.id,
            search: name,
            ...filterParams()
          }
        }
        this.$wkExport.export('item', {
          typeName: '事项',
          idKey: 'fieldName',
          selectsKey: 'exportColumn',
          fieldParams: {
            taskType: itemTypeMap[this.activeName]
          },
          fieldRequest: itemExportFieldAPI,
          params: {
            taskType: itemTypeMap[this.activeName],
            ...queryParams
          },
          request: itemAllExportAPI
        })
      }
    },

    /**
     * 更新列表
     */
    addSuccess() {
      this.$refs[this.activeName][0].refresh()
    }

  }
}
</script>

<style scoped lang="scss">
@import "@/views/layout/components/style.scss";

.crm-workbench__hd .title {
  display: flex;
  align-items: center;
  margin-bottom: 16px;
  font-size: 24px;

  /deep/ .el-button {
    padding: 5.8px 12px;
  }
}

@keyframes rotation {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.is-rotation {
  animation: rotation 1s linear;
}

.pm-project {
  position: relative;
  width: 100%;
  height: 100%;
  padding: 24px 40px 0;

  &-main {
    height: calc(100% - 44px);

    .el-tabs {
      height: 100%;
    }
  }

  .pm-project-main {
    position: relative;

    .tab-item {
      position: relative;
      height: 100%;
    }

    /deep/ .el-tabs {
      .el-tabs__header {
        margin: 0 0 16px;
      }

      .el-tabs__item {
        padding: 0;
        padding-right: 24px;

        &:nth-of-type(3) {
          padding-right: 48px;
        }
      }

      .is-active {
        font-size: 16px;
        font-weight: 700;
        color: $--color-primary;
      }

      .el-tabs__content {
        height: calc(100% - 56px);
      }
    }

    .create {
      position: absolute;
      top: 4px;
      right: 0;
    }
  }

  // 公共新建按钮
  .common-create-btn {
    margin-left: #{$--interval-base * 2};
  }

  .add-user-btn {
    margin-left: 6px;
  }
}

.divider {
  position: absolute;
  top: 13px;
  right: 0;
  display: inline-block;
  width: 1px;
  height: 16px;
  margin-right: 24px;
  background-color: $--badge-background-color;
}
</style>
