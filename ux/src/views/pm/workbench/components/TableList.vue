<template>
  <div class="normal-list">
    <el-table
      id="crm-table"
      :row-height="rowHeight"
      :data="list"
      :height="tableHeight"
      :cell-class-name="cellClassName"
      :row-key="`taskId`"
      use-virtual
      highlight-current-row
      style="width: 100%;"
      @row-click="handleRowClick">
      <el-table-column
        v-for="(item, index) in fieldList"
        :key="index"
        :fixed="item.isLock === 1"
        :prop="item.prop"
        :label="item.label"
        :width="item.width"
        :min-width="item.minWidth"
        :class-name="item.width>60 ? 'column' : '' "
        show-overflow-tooltip>
        <template slot-scope="{ row }">
          <span v-if="item.prop == 'num'">#{{ row[item.prop] }}</span>
          <span v-else-if="item.prop == 'name'" class="icon-name">
            <img v-if="itemType != 'Iterations'" class="item-img" :src="getIconClass(row)" alt="">
            <span class="item-title can-visit--underline">{{ row[item.prop] }}</span>
          </span>
          <status-tag v-else-if="item.prop == 'status'" :status-name="row.boardStatusName" style="margin-right: 8px;" :type="row.status" />
          <span v-else-if="item.prop == 'priority'" class="icon-name">
            <img v-if="itemType != 'Iterations'" class="item-img" :src="getPriorityPic(row[item.prop])" alt="">
            <span>{{ getPriority(row[item.prop]) }}</span>
          </span>
          <span v-else-if="item.prop == 'belongIterationProgress'">{{ Math.round(row[item.prop]) || 0 }}%</span>
          <span v-else-if="item.prop == 'projectAdminRoleList'">{{ getAdminList(row[item.prop]) }}</span>
          <!-- <span v-else-if="item.prop == 'stopTime'">{{ fomartTime(row[item.prop]) }}</span> -->
          <el-tag
            v-else-if="item.prop == 'stopTime'"
            disable-transitions
            :class="[row.status == 3 ? 'is-common' : getOverTimeStatus(row.stopTime)]"
            class="project-tag">
            {{ row.stopTime ? formatTime(row.stopTime) + '截止' : '' }}
          </el-tag>
          <span v-else>{{ row[item.prop] }}</span>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
import StatusTag from '@/views/pm/project/components/StatusTag'

import { convertPriority, getItemImg, getPriorityIcon } from '@/views/pm/data'
import moment from 'moment'

export default {
  name: 'TableList',
  components: {
    StatusTag
  },
  props: {
    itemType: String,
    tableHeight: Number,
    rowHeight: Number,
    projectDetail: Object,
    list: {
      type: Array,
      default() {
        return []
      }
    }
  },
  data() {
    return {
      // 列数据
      fieldList: [
        { prop: 'num', label: 'ID', width: '60', isLock: 1 },
        { prop: 'name', label: '标题', minWidth: '440', isLock: 1 },
        { prop: 'priority', label: '优先级', width: '100' },
        { prop: 'status', label: '状态', width: '100' },
        { prop: 'mainUserName', label: '处理人', minWidth: '100' },
        { prop: 'createUserName', label: '创建人', minWidth: '100' },
        { prop: 'belongIterationName', label: '所属迭代', minWidth: '160' },
        { prop: 'belongProjectName', label: '所属项目', minWidth: '160' },
        { prop: 'stopTime', label: '截止日期', width: '160' },
        { prop: 'createTime', label: '创建日期', width: '160' }
      ]
    }
  },
  methods: {
    cellClassName({ row, column, rowIndex, columnIndex }) {
      if (column.property === 'name') {
        return 'can-visit--underline'
      } else {
        return ''
      }
    },

    handleRowClick(row, column, event) {
      if (column.property == 'name') {
        this.$emit('handle', { type: 'detail', row })
      }
    },

    /**
     * @description: 优先级文字
     * @param {*} val
     * @return {*}
     */
    getPriority(val) {
      return convertPriority(val)
    },

    /**
     * @description: 优先级图片
     * @param {*} val
     * @return {*}
     */
    getPriorityPic(val) {
      return getPriorityIcon(val)
    },

    /**
     * @description: 负责人
     * @param {*} val
     * @return {*}
     */
    getAdminList(val) {
      return (val || []).map(item => item.realname).join()
    },

    /**
     * @description: 格式化时间
     * @return {*}
     */
    fomartTime(val) {
      return val ? moment(val).format('YYYY-MM-DD') : '--'
    },

    /**
     * @description: 图标
     * @param {*} row
     * @return {*}
     */
    getIconClass(row) {
      return getItemImg(row)
    },

    /**
     * 超时状态
     * @param time
     * @returns {boolean}
     */
    getOverTimeStatus(time) {
      const data = this.handleTime(time)
      if (data === '今天' || data === '明天') {
        return 'is-near'
      } else {
        return this.$moment(time).isBefore(this.$moment()) ? 'is-over' : 'is-common'
      }
    },

    /**
     * 时间格式化
     * @param time
     * @returns {null|*}
     */
    formatTime(time) {
      if (!time) return null
      const data = this.handleTime(time)
      if (data) {
        return data
      } else {
        const flag = this.$moment().isSame(time, 'year')
        return flag
          ? this.$moment(time).format('M月D日')
          : this.$moment(time).format('YYYY年M月D日')
      }
    },

    /**
     * @description: 根据时间处理今天/明天/昨天的情况
     * @return {*}
     */
    handleTime(time) {
      const activeDataArr = this.$moment(time).format('YYYY-MM-DD').split('-')
      const todayDate = new Date() // 今天
      const nowDataArr = [todayDate.getFullYear(), todayDate.getMonth() + 1, todayDate.getDate()] // 今天的年月日
      const tomorrowData = new Date(todayDate.setTime(todayDate.getTime() + 24 * 60 * 60 * 1000)) // 明天
      const tomorrowDataArr = [tomorrowData.getFullYear(), tomorrowData.getMonth() + 1, tomorrowData.getDate()] // 明天的年月日
      const yesterdayDate = new Date(todayDate.setTime(todayDate.getTime() - 48 * 60 * 60 * 1000)) // 昨天
      const yesterdayDateArr = [yesterdayDate.getFullYear(), yesterdayDate.getMonth() + 1, yesterdayDate.getDate()] // 昨天的年月日
      console.log(nowDataArr, yesterdayDate, yesterdayDateArr)
      if (nowDataArr[0] == activeDataArr[0] && nowDataArr[1] == activeDataArr[1] && nowDataArr[2] == activeDataArr[2]) {
        return '今天'
      } else if (tomorrowDataArr[0] == activeDataArr[0] && tomorrowDataArr[1] == activeDataArr[1] && tomorrowDataArr[2] == activeDataArr[2]) {
        return '明天'
      } else if (yesterdayDateArr[0] == activeDataArr[0] && yesterdayDateArr[1] == activeDataArr[1] && yesterdayDateArr[2] == activeDataArr[2]) {
        return '昨天'
      } else {
        return false
      }
    }
  }
}
</script>
<style lang='scss' scoped>
@import "@/views/pm/styles/table.scss";

.item-img {
  display: inline-block;
  width: 16px;
  height: 16px;
  margin-right: 5px;
}

.item-title {
  max-width: calc(100% - 21px);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.icon-name {
  display: flex;
  align-items: center;
  justify-content: flex-start;
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

  &.is-near {
    color: white;
    background-color: $--color-y400;
  }

  &.is-common {
    color: $--color-black;
    background-color: $--background-color-base;
  }
}
</style>
