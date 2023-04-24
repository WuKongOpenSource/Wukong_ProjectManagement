<!-- pm-table模板 -->
<template>
  <div class="all-wrap">
    <!-- 头部 -->
    <div class="filter-wrapper">
      <wk-table-header
        :props="tableHeaderProps.props"
        :filter-header-props="tableHeaderProps.filterHeaderProps">
        <el-select v-model="layout" placeholder="请选择" @change="selectChanged">
          <el-option
            v-for="item in showList"
            :key="item.value"
            :label="item.label"
            :value="item.value">
            <i :class="item.icon" style="margin-right: 5px;" />{{ item.label }}
          </el-option>
        </el-select>
      </wk-table-header>
    </div>
    <!-- 树形 -->
    <el-table
      v-if="showIndex == '1'"
      :data="tableData"
      row-key="id"
      :tree-props="{children: 'children'}"
      :indent="32"
      class="table">
      <el-table-column
        label="ID"
        width="50"
        fixed="left"
        type="">
        <template slot-scope="scope">
          <span class="project-id text">{{ '#' + scope.row.id }}</span>
        </template>
      </el-table-column>

      <el-table-column
        prop="title"
        label="标题"
        width="280">
        <template slot-scope="scope">
          <flexbox>
            <flexbox
              align="center"
              justify="center"
              class="icon-box">
              <i :class="scope.row.icon" class="icon" />
            </flexbox>
            <span class="project-name text">{{ scope.row.title }}</span>
            <el-tag
              v-if="scope.row.children"
              disable-transitions
              :style="{
                color: '#00B8D9',
                backgroundColor: '#e6fcff',
              }"
              class="sub_num">
              {{ 0 + '/' + scope.row.children.length }}
            </el-tag>
          </flexbox>
        </template>
      </el-table-column>

      <el-table-column label="优先级">
        <template slot-scope="{ row }">
          <span :style="getStatusStyle(row.priority)" class="status-mark" />
          <span>{{ getPriorityTag(row.priority).label }}</span>
        </template>
      </el-table-column>

      <el-table-column label="状态">
        <template slot-scope="{ row }">
          <el-tag
            v-if="!isEmpty(row.checkStatus)"
            disable-transitions
            :style="{
              color: getStatusTag(row.checkStatus).color,
              backgroundColor: getStatusTag(row.checkStatus).bg
            }"
            class="project-tag">
            {{ getStatusTag(row.checkStatus).label }}
          </el-tag>
          <i />
        </template>
      </el-table-column>

      <el-table-column label="处理人" width="100">
        <template slot-scope="{ row }">
          <flexbox
            v-if="!isEmpty(row.manager)"
            align="center"
            justify="flex-start">
            <xr-avatar
              :id="row.manager.userId"
              :src="row.manager.img"
              :name="row.manager.realname"
              :size="24" />
            <div style="padding-left: 10px;">{{ row.manager.username }}</div>
          </flexbox>
        </template>
      </el-table-column>

      <el-table-column label="创建人" width="100">
        <template slot-scope="{ row }">
          <flexbox
            v-if="!isEmpty(row.founder)"
            align="center"
            justify="flex-start">
            <xr-avatar
              :id="row.founder.userId"
              :src="row.founder.img"
              :name="row.founder.realname"
              :size="24" />
            <div style="padding-left: 10px;">{{ row.founder.username }}</div>
          </flexbox>
        </template>
      </el-table-column>

      <el-table-column label="迭代" width="180">
        <template slot-scope="{ row }">
          {{ row.iterate }}
        </template>
      </el-table-column>

      <el-table-column label="截止日期" width="180">
        <template slot-scope="{ row }">
          <el-tag
            :class="[getOverTimeStatus(row.endTime) ? 'is-over' : 'is-common']"
            disable-transitions
            class="project-tag">
            {{ formatTime(row.endTime) }}截止
          </el-tag>
        </template>
      </el-table-column>

      <el-table-column label="创建日期" min-width="110">
        <template slot-scope="{ row }">
          <el-tag
            disable-transitions
            class="project-tag">
            {{ formatTime(row.startTime) }}创建
          </el-tag>
        </template>
      </el-table-column>
    </el-table>

    <!-- 平铺 -->
    <el-table
      v-else-if="showIndex == '2'"
      :data="tableList"
      row-key="id"
      class="table">
      <el-table-column
        label="ID"
        width="50"
        fixed="left"
        type="">
        <template slot-scope="scope">
          <span class="project-id text">{{ '#' + scope.row.id }}</span>
        </template>
      </el-table-column>

      <el-table-column
        prop="title"
        label="标题"
        width="280">
        <template slot-scope="scope">
          <flexbox>
            <flexbox
              align="center"
              justify="center"
              class="icon-box">
              <i :class="scope.row.icon" class="icon" />
            </flexbox>
            <span class="project-name text">{{ scope.row.title }}</span>
            <el-tag
              v-if="scope.row.hasChild"
              disable-transitions
              :style="{
                color: '#00B8D9',
                backgroundColor: '#e6fcff',
              }"
              class="sub_num">
              {{ 0 + '/' + 1 }}
            </el-tag>
          </flexbox>
        </template>
      </el-table-column>

      <el-table-column label="优先级">
        <template slot-scope="{ row }">
          <span :style="getStatusStyle(row.priority)" class="status-mark" />
          <span>{{ getPriorityTag(row.priority).label }}</span>
        </template>
      </el-table-column>

      <el-table-column label="状态">
        <template slot-scope="{ row }">
          <el-tag
            v-if="!isEmpty(row.checkStatus)"
            disable-transitions
            :style="{
              color: getStatusTag(row.checkStatus).color,
              backgroundColor: getStatusTag(row.checkStatus).bg
            }"
            class="project-tag">
            {{ getStatusTag(row.checkStatus).label }}
          </el-tag>
          <i />
        </template>
      </el-table-column>

      <el-table-column label="处理人" width="100">
        <template slot-scope="{ row }">
          <flexbox
            v-if="!isEmpty(row.manager)"
            align="center"
            justify="flex-start">
            <xr-avatar
              :id="row.manager.userId"
              :src="row.manager.img"
              :name="row.manager.realname"
              :size="24" />
            <div style="padding-left: 10px;">{{ row.manager.username }}</div>
          </flexbox>
        </template>
      </el-table-column>

      <el-table-column label="创建人" width="100">
        <template slot-scope="{ row }">
          <flexbox
            v-if="!isEmpty(row.founder)"
            align="center"
            justify="flex-start">
            <xr-avatar
              :id="row.founder.userId"
              :src="row.founder.img"
              :name="row.founder.realname"
              :size="24" />
            <div style="padding-left: 10px;">{{ row.founder.username }}</div>
          </flexbox>
        </template>
      </el-table-column>

      <el-table-column label="迭代" width="180">
        <template slot-scope="{ row }">
          {{ row.iterate }}
        </template>
      </el-table-column>

      <el-table-column label="截止日期" width="180">
        <template slot-scope="{ row }">
          <el-tag
            :class="[getOverTimeStatus(row.endTime) ? 'is-over' : 'is-common']"
            disable-transitions
            class="project-tag">
            {{ formatTime(row.endTime) }}截止
          </el-tag>
        </template>
      </el-table-column>

      <el-table-column label="创建日期" min-width="110">
        <template slot-scope="{ row }">
          <el-tag
            disable-transitions
            class="project-tag">
            {{ formatTime(row.startTime) }}创建
          </el-tag>
        </template>
      </el-table-column>
    </el-table>

    <!-- 看板视图 -->
    <!-- <task-board :table-data="tableData" /> -->

    <panel-setting
      v-if="panelSettingShow"
      :visible.sync="panelSettingShow"
      @update="udpatePanelShow" />
  </div>

</template>

<script>
import WkTableHeader from '@/components/Page/WkTableHeader'
import PanelSetting from './PanelSetting'

// import TaskBoard from './TaskBoard'
import { isEmpty } from '@/utils/types'
import { PRIORITY_ENUM, STATUS_ENUM } from '@/views/pm/enum'

export default {
  name: 'PmTable',
  components: {
    WkTableHeader,
    PanelSetting
    // TaskBoard
  },
  props: {
    tableData: {
      type: Array,
      default: () => {
        return []
      }
    }
  },
  data() {
    return {
      search: '',
      tableHeaderProps: {
        props: {
          showFilterView: true,
          showExportFields: false
        },
        filterHeaderProps: {
          maxTabCount: 0,
          tabSetShow: false,
          searchPlaceholder: '搜索事项'
        }
      },
      layout: '2',
      showIndex: '2',
      showList: [
        { label: '树状视图', value: '1', icon: 'wk wk-manage' },
        { label: '平铺视图', value: '2', icon: 'wk wk-manage' },
        { label: '看板视图', value: '3', icon: 'wk wk-manage' },
        { label: '看板视图设置', value: '4', icon: 'wk wk-manage' }
      ],
      panelSettingShow: false
    }
  },
  computed: {
    // 转化成同级数据
    tableList() {
      return this.treeToArray(this.tableData)
    }
  },
  watch: {},
  created() {

  },
  mounted() {

  },
  methods: {
    isEmpty(val) {
      return isEmpty(val)
    },

    /**
     * @description: 状态
     * @param {*} value
     * @return {*}
     */
    getStatusTag(value) {
      if (isEmpty(value)) return null
      return STATUS_ENUM.find(o => o.value == value) || {}
    },

    /**
     * 优先级
     * @param value
     * @returns {null|*|{}}
     */
    getPriorityTag(value) {
      if (isEmpty(value)) return null
      return PRIORITY_ENUM.find(o => o.value == value) || {}
    },

    /**
     * 超时状态
     * @param time
     * @returns {boolean}
     */
    getOverTimeStatus(time) {
      return this.$moment(time).isBefore(this.$moment())
    },

    /**
     * 时间格式化
     * @param time
     * @returns {null|*}
     */
    formatTime(time) {
      if (!time) return null
      const flag = this.$moment().isSame(time, 'year')
      return flag
        ? this.$moment(time).format('M月D日')
        : this.$moment(time).format('YYYY年M月D日')
    },

    /**
     * 状态颜色
     * @param {*} status
     */
    getStatusStyle(status) {
      return {
        backgroundColor: this.getStatusColor(status)
      }
    },
    /**
     * 状态颜色
     * @param {*} status
     */
    getStatusColor(status) {
      if (status == 1) {
        return '#DE350B'
      } else if (status == 2) {
        return '#FF991F'
      } else if (status == 3) {
        return '#00875A'
      }
    },

    /**
     * @description: 数据扁平处理
     * @param {*} tree
     * @return {*}
     */
    treeToArray(tree) {
      let res = []
      for (let i = 0; i < tree.length; i++) {
        const { children, ...item } = tree[i]
        if (children && children.length) {
          res = res.concat(this.treeToArray(children))
        }
        res.push(item)
      }
      return res
    },

    /**
     * @description: 选择展示类型
     * @param {*} value
     * @return {*}
     */
    selectChanged(value) {
      console.log(value, '值')
      if (value == '4') {
        this.layout = this.showIndex
        this.panelSettingShow = true
      } else {
        this.layout = value
        this.showIndex = value
      }
    },

    /**
     * @description: 更新看板显示
     * @param {*} data
     * @return {*}
     */
    udpatePanelShow(data) {
      this.panelSettingShow = data
    }
  }
}
</script>
<style scoped lang='scss'>
.all-wrap {
  .filter-wrapper {
    margin-bottom: 20px;
    line-height: 32px;
  }

  .el-table {
    /deep/ .cell {
      display: flex;
      align-items: center;
    }

    .status-mark {
      display: inline-block;
      width: 8px;
      height: 8px;
      border-radius: 4px;
    }

    .icon-box {
      width: 32px;
      height: 32px;
      background-color: $--color-n20;
      border-radius: $--border-radius-base;

      .icon {
        font-size: 18px;
        color: $--color-primary;
      }
    }

    .sub_num {
      position: absolute;
      right: 10px;
    }

    .project-tag {
      height: 22px;
      padding: 0 8px;
      font-size: 13px;
      line-height: 18px;

      &.is-over {
        color: white;
        background-color: $--color-r400;
      }

      &.is-common {
        color: $--color-black;
        background-color: $--background-color-base;
      }
    }

    .icon-header {
      color: $--color-n50;
    }

    .focus-btn {
      color: $--color-n50;

      /deep/ i {
        font-weight: bold;
      }

      &.active {
        color: $--color-y300;

        /deep/ i {
          font-weight: normal;
        }
      }
    }
  }
}

</style>
