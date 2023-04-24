<template>
  <div class="common-list">
    <wk-table-header
      :search.sync="search"
      :props="tableHeaderProps.props"
      :filter-header-props="tableHeaderProps.filterHeaderProps"
      @event-change="tableHeaderHandle">
      <el-button
        type="subtle"
        class="filter-button"
        icon="wk wk-screening"
        @click="filterShow=true">
        高级筛选
      </el-button>
      <el-select
        v-if="itemType != 'Iterations'"
        v-model="layout"
        style="margin-left: 12px;"
        @change="selectChanged">
        <el-option-group>
          <el-option
            v-for="item in showList"
            :key="item.value"
            :label="item.label"
            :value="item.value">
            <i :class="item.icon" style="margin-right: 5px;" />{{ item.label }}
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
    </wk-table-header>

    <div v-loading="loading" :class="{'common-list-body': viewType !== 'table'}">
      <table-list
        v-if="viewType == 'table'"
        :list="list"
        :table-height="tableHeight"
        :row-height="rowHeight"
        :item-type="itemType"
        :project-detail="projectDetail"
        @get-list="refreshList"
        @handle="handleProcess" />

      <tree-list
        v-else-if="viewType == 'tree'"
        :list="list"
        :item-type="itemType" />

      <board-list
        v-else-if="viewType == 'board'"
        ref="board"
        :search="search"
        :list="list"
        :item-type="itemType" />
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
      :project-detail="projectDetail"
      :detail-index="detailIndex"
      :no-listener-class="['el-table__body']"
      @update-list="handleUpdateList"
      @close="detailClose" />

    <panel-setting
      v-if="panelSettingShow"
      :item-type="itemType"
      :visible.sync="panelSettingShow"
      @update="udpatePanelShow" />

    <!-- 筛选 -->
    <list-filter
      v-if="filterShow"
      :list-type="listType"
      :list="filterList"
      @change="handleChange"
      @close-filter="filterShow = false" />
  </div>
</template>

<script>
import WkTableHeader from '@/components/Page/WkTableHeader'
import PanelSetting from '../PanelSetting'
import ListFilter from '@/views/pm/project/team/components/ListFilter'
import TableList from './TableList'
import TreeList from './TreeList'
import BoardList from './BoardList'

import MixinMain from './mixins/main'

export default {
  name: 'List',
  components: {
    WkTableHeader,
    PanelSetting,
    TableList,
    TreeList,
    BoardList,
    ListFilter
  },
  mixins: [MixinMain],
  props: {
    itemType: String,
    projectDetail: Object
  },
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
      filterShow: false,
      viewType: 'board',
      layout: '3',
      showList: [
        { label: '平铺视图', value: '1' },
        // { label: '树状视图', value: '2' },
        { label: '看板视图', value: '3' }
      ],
      boardSetList: [
        { label: '看板视图设置', value: '4' }
      ],
      taskId: '',
      detailIndex: -1,
      taskDetailShow: false
    }
  },
  computed: {
    showBoardSet() {
      return this.layout == '3' // 看板视图
    },

    listType() {
      if (this.itemType == 'All') {
        return 'all'
      } else if (this.itemType == 'Iterations') {
        return 'iteration'
      } else {
        return 'item'
      }
    }
  },

  created() {
    this.tableHeaderProps.filterHeaderProps.searchPlaceholder =
      this.itemType == 'Iterations' ? '搜索迭代' : '搜索标题或ID'

    if (this.itemType == 'All') {
      this.layout = '1'
      this.viewType = 'table'
      this.showList = [{ label: '平铺视图', value: '1' }]
    }

    if (this.itemType == 'Iterations') {
      this.layout = '1'
      this.viewType = 'table'
    }
  },
  methods: {
    /**
     * @description: 筛选更新
     * @param {*} list
     * @return {*}
     */
    handleChange(list) {
      this.filterList = list || []
      if (this.viewType == 'table') {
        this.getList()
      } else if (this.viewType == 'board') {
        this.$refs.board.getData(list)
      }
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
