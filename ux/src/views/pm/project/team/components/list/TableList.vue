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
      @row-click="handleRowClick"
      @header-dragend="handleHeaderDragend">
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
          <template v-if="item.prop == 'num'">#{{ row[item.prop] }}</template>
          <span v-else-if="item.prop == 'name'" class="icon-name">
            <img v-if="itemType != 'Iterations'" class="item-img" :src="getIconClass(row)" alt="">
            <span class="item-title can-visit--underline">{{ row[item.prop] }}</span>
          </span>

          <!-- 迭代列表下状态 -->
          <status-tag
            v-else-if="item.prop == 'status' && itemType == 'Iterations'"
            :status-name="row.boardStatusName"
            style="margin-right: 8px;"
            :type="row.status" />

          <!-- 事项列表下状态 -->
          <el-dropdown
            v-else-if="item.prop == 'status' && itemType != 'Iterations'"
            :placement="placement"
            :disabled="getDisabledAuth(row.taskAuth)"
            class="priority-box"
            @command="setItemStatus($event, row.taskId)"
            @click.native="handleDropdown($event, row.type - 1)">
            <flexbox>
              <status-tag
                class="cell-tag"
                :status-name="row.boardStatusName"
                :type="row.status" />
              <i class="wk wk-icon-caret-bottom bottom-icon" />
            </flexbox>
            <!-- height: 118px; -->
            <el-dropdown-menu
              slot="dropdown"
              v-loading="loading"
              class="dropdown-menu"
              :style="{
                padding: allStatusList.length === 0 ? '0px' : 'auto'
              }"
              style="padding: 4px 0;overflow: auto;">
              <el-dropdown-item
                v-for="(stautsItem, statusIndex) in allStatusList"
                :key="statusIndex"
                :command="stautsItem.id"
                style="padding: 0 10px;">
                <div class="dorp-item">
                  <status-tag
                    :is-drop="true"
                    :type="stautsItem.statusType"
                    :status-name="stautsItem.statusName" />
                </div>
              </el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>

          <!-- 事项列表下处理人 -->
          <span v-else-if="item.prop == 'mainUserName' && itemType != 'Iterations'" class="icon-name">
            <wk-user-select
              :disabled="getDisabledAuth(row.taskAuth)"
              :value="row.mainUserId || ''"
              :request="ownerListRequest"
              :params="{ projectId: row.projectId }"
              :props="{isList: true}"
              style="width: 100%;"
              radio
              @change="mainUserChange(arguments, row.taskId)">
              <flexbox
                slot="reference"
                class="head-btn">
                <span
                  v-if="!row.mainUserId"
                  class="head-btn__text">
                  {{ getDisabledAuth(row.taskAuth) ? '' : '未指定' }}
                </span>
                <div class="head-btn__bd">
                  <div
                    v-if="row.mainUserId"
                    class="head-btn__bd--name"
                    style="font-size: 14px;">
                    {{ row.mainUserName }}
                  </div>
                </div>
                <i class="wk wk-icon-caret-bottom bottom-icon" />
              </flexbox>
            </wk-user-select>
          </span>

          <!-- 迭代列表下优先级 -->
          <span v-else-if="item.prop == 'priority' && itemType == 'Iterations'" class="icon-name">
            <img v-if="itemType != 'Iterations'" class="item-img" :src="getPriorityPic(row[item.prop])" alt="">
            <span>{{ getPriority(row[item.prop]) }}</span>
          </span>

          <!-- 事项列表下优先级 -->
          <span v-else-if="item.prop == 'priority' && itemType != 'Iterations'" class="icon-name">
            <el-dropdown
              placement="bottom-start"
              :disabled="getDisabledAuth(row.taskAuth)"
              class="priority-box"
              @command="changePriority($event, row.taskId)">
              <div class="priority-wrap">
                <img class="priority-icon" :src="getPriorityPic(row[item.prop])" alt="">
                <span style="padding-right: 8px;">{{ getPriority(row[item.prop]) }}</span>
                <i class="wk wk-icon-caret-bottom bottom-icon" />
              </div>
              <el-dropdown-menu slot="dropdown" style="min-width: 100px;padding: 4px 0;">
                <el-dropdown-item
                  v-for="(option, optionIndex) in priorityList"
                  :key="optionIndex"
                  :command="option.key"
                  style="padding: 0 10px;">
                  <div class="priority-wrap">
                    <img :src="option.icon" class="priority-icon" alt="">{{ option.label }}
                  </div>
                </el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </span>

          <!-- 所属项目 -->
          <flexbox v-else-if="item.prop == 'belongProjectName'">
            <flexbox
              align="center"
              justify="center"
              class="icon-box">
              <i :class="row.projectIcon" class="icon" />
            </flexbox>
            <span class="project-name text">
              {{ row[item.prop] }}
              <span v-if="row.projectType == 2" class="agile-sign">敏捷</span>
              <span v-else-if="row.projectType == 1" class="ordinary-sign">普通</span>
            </span>
          </flexbox>

          <span v-else-if="item.prop == 'belongIterationProgress'">{{ Math.round(row[item.prop]) || 0 }}%</span>
          <span v-else-if="item.prop == 'projectAdminRoleList'">{{ getAdminList(row[item.prop]) }}</span>
          <el-tag
            v-else-if="item.prop == 'stopTime' && itemType == 'Iterations' && row.stopTime"
            disable-transitions
            :class="[row.status == 3 ? 'is-common' : getOverTimeStatus(row.stopTime)]"
            class="project-tag">
            {{ formatTime(row.stopTime) }}
          </el-tag>
          <div v-else-if="item.prop == 'stopTime' && itemType != 'Iterations'" class="stop-time">
            <span
              v-if="!row.stopTime"
              class="head-btn__text">
              {{ getDisabledAuth(row.taskAuth) ? '' : '未选择' }}
            </span>
            <el-date-picker
              v-model="row.stopTime"
              :disabled="getDisabledAuth(row.taskAuth)"
              :clearable="false"
              type="date"
              value-format="yyyy-MM-dd"
              @change="timeChange(row.stopTime, row.taskId)" />
            <el-tag
              v-if="row.stopTime"
              disable-transitions
              :class="[row.status == 3 ? 'is-common' : getOverTimeStatus(row.stopTime)]"
              class="project-tag">
              {{ formatTime(row.stopTime) }}
            </el-tag>
            <i class="wk wk-icon-caret-bottom bottom-icon" />
          </div>
          <span v-else>{{ row[item.prop] }}</span>
        </template>
      </el-table-column>
      <el-table-column />
    </el-table>
  </div>
</template>

<script>
import {
  setItemStartStopTimeAPI,
  workSetPriorityAPI,
  projectBoardTaskSetStatusAPI,
  setHandlerAPI
} from '@/api/pm/projectTask'
import { getStatusListAPI } from '@/api/pm/setting'
import {
  queryProjectUserListAPI
} from '@/api/pm/manage'

import StatusTag from '@/views/pm/project/components/StatusTag'
import WkUserSelect from '@/components/NewCom/WkUserSelect'

import { convertPriority, getItemImg, getPriorityIcon, priorityList } from '@/views/pm/data'
import MinxinTable from './mixins/table'
import moment from 'moment'
export default {
  name: 'TableList',
  components: {
    StatusTag,
    WkUserSelect
  },
  mixins: [MinxinTable],
  props: {
    itemType: String,
    list: {
      type: Array,
      default() {
        return []
      }
    }
  },
  data() {
    return {
      loading: false,
      priorityList,
      allStatusList: [],
      placement: 'bottom-start'
    }
  },
  computed: {
    ownerListRequest() {
      return queryProjectUserListAPI
    }
  },
  methods: {
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
      return (data || this.getTime(time)) + '截止'
    },

    getTime(time) {
      const flag = this.$moment().isSame(time, 'year')
      return flag
        ? this.$moment(time).format('M月D日')
        : this.$moment(time).format('YYYY年M月D日')
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
      if (nowDataArr[0] == activeDataArr[0] && nowDataArr[1] == activeDataArr[1] && nowDataArr[2] == activeDataArr[2]) {
        return '今天'
      } else if (tomorrowDataArr[0] == activeDataArr[0] && tomorrowDataArr[1] == activeDataArr[1] && tomorrowDataArr[2] == activeDataArr[2]) {
        return '明天'
      } else if (yesterdayDateArr[0] == activeDataArr[0] && yesterdayDateArr[1] == activeDataArr[1] && yesterdayDateArr[2] == activeDataArr[2]) {
        return '昨天'
      } else {
        return false
      }
    },

    /**
     * @description: 编辑截止时间
     * @param {*} type
     * @return {*}
     */
    timeChange(time, taskId) {
      const params = {
        taskId: taskId,
        stopTime: time || ''
      }
      setItemStartStopTimeAPI(params).then(() => {
        this.$message.success('操作成功')
        this.$emit('get-list')
      }).catch(() => {
        this.$message.error('编辑失败')
      })
    },

    /**
     * @description: 修改优先级
     * @param {*} type
     * @return {*}
     */
    changePriority(type, taskId) {
      workSetPriorityAPI({
        taskId: taskId,
        priority: type
      }).then(res => {
        this.$message.success('操作成功')
        this.$emit('get-list')
      }).catch(() => {
        this.$message.error('编辑失败')
      })
    },

    /**
     * @description: 修改事项状态
     * @param {*} statusId
     * @return {*}
     */
    setItemStatus(statusId, taskId) {
      const params = {
        statusId: statusId,
        taskId: taskId
      }
      projectBoardTaskSetStatusAPI(params).then(res => {
        this.$message.success('设置成功')
        this.$emit('get-list')
      }).catch(() => {
        this.$message.error('编辑失败')
      })
    },

    /**
     * @description: 状态下拉
     * @param {*} e
     * @return {*}
     */
    handleDropdown(e) {
      const fullHeight = document.body.clientHeight
      const topHeight = e.clientY
      const diffHeight = fullHeight - topHeight
      if (diffHeight > 417) {
        this.placement = 'bottom-start'
      } else {
        this.placement = 'top-start'
      }
    },

    /**
     * @description: 获取所有事项状态
     * @return {*}
     */
    getStatusList(row) {
      const params = {
        projectId: row.projectId,
        eventId: row.type - 1
      }
      getStatusListAPI(params).then(res => {
        this.allStatusList = res.data || []
        if (!this.allStatusList.length) {
          this.$message.error('当前无可选的状态')
        }
      }).catch(() => {
        /** ignore */
      })
    },

    /**
     * @description: 编辑负责人
     * @param {*} data
     * @param {*} dataArray
     * @return {*}
     */
    mainUserChange(data, taskId) {
      const mainUser = data[1].length > 0 ? data[1][0] : null
      this.submiteMainUser(mainUser, taskId)
    },

    /**
     * @description: 上传负责人信息
     * @param {*} mainUser
     * @return {*}
     */
    submiteMainUser(mainUser, taskId) {
      setHandlerAPI({
        taskId: taskId,
        mainUserId: mainUser ? mainUser.userId : ''
      })
        .then(res => {
          this.$message.success('设置成功')
          this.$emit('get-list')

          if (this.$route.name === 'workbench') {
            this.$bus.emit('update-workbench-num')
          }
        })
        .catch(() => {})
    }
  }
}
</script>
<style lang='scss' scoped>
@import "@/views/pm/styles/table.scss";

.normal-list {
  .el-date-editor {
    position: absolute;
    top: 0;
    right: 0;
    bottom: 0;
    left: 0;
    z-index: 1;
    width: 100%;
    overflow: hidden;

    /deep/ .el-input__inner {
      height: 44px;
      color: transparent;
      cursor: pointer;
      background-color: transparent;
      border: none;
    }

    /deep/ .el-input__prefix {
      display: none;
    }
  }

  /deep/ .wk-user-select {
    height: 23px !important;
    background: transparent !important;
  }

  /deep/ .el-table {
    .el-table__cell {
      position: relative;
    }
  }

  /deep/ .el-dropdown {
    position: static;
  }

  // /deep/ .el-dropdown-selfdefine {
  //   position: absolute;
  //   top: 0;
  //   left: 0;
  //   display: flex;
  //   align-items: center;
  //   width: 100%;
  //   height: 100%;
  //   padding: 10px;
  //   overflow: hidden;
  // }
}

.item-img {
  display: inline-block;
  width: 16px;
  height: 16px;
  margin-right: 5px;
}

.priority-box {
  // margin-left: 7px;
  line-height: 23px;
}

.dorp-item {
  display: flex;
  align-items: center;
  padding: 0 8px;

  .desc-text {
    display: inline-block;
    min-width: 60px;
    font-size: 12px;
  }
}

.priority-wrap {
  display: flex;
  align-items: center;
  justify-content: flex-start;

  .priority-icon {
    display: inline-block;
    width: 16px;
    height: 16px;
    margin-right: 5px;
  }
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
  overflow: hidden;
  font-size: 13px;
  line-height: 18px;
  text-overflow: ellipsis;
  white-space: nowrap;

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

// 操作图标
.head-btn {
  position: absolute;
  top: 0;
  left: 0;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px;

  &__text {
    height: 23px;
    line-height: 23px;
    color: $--color-n200;
  }

  &__bd {
    &--name {
      overflow: hidden;
      font-size: $--font-size-large;
      text-overflow: ellipsis;
      white-space: nowrap;
    }
  }
}

.stop-time {
  display: flex;
  justify-content: space-between;
}

.bottom-icon {
  display: none;
  font-size: 12px;
}

/deep/ .td-hover:hover {
  cursor: pointer;
  background-color: $--background-hover-color-base !important;

  .bottom-icon {
    display: block;
  }
}

.cell-tag {
  width: 100%;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.icon-box {
  width: 32px;
  height: 32px;
  background-color: $--color-n20;
  border: 1px solid #e2e4e9;
  border-radius: $--border-radius-base;

  .icon {
    font-size: 18px;
    color: $--color-primary;
  }
}

.text {
  margin-left: 10px;

  .agile-sign {
    display: inline-block;
    width: 48px;
    height: 20px;
    margin-left: 4px;
    font-size: 12px;
    line-height: 20px;
    color: $--color-text-regular;
    text-align: center;
    background-color: $--background-color-base;
    border-bottom-right-radius: 10px;
  }

  .ordinary-sign {
    display: inline-block;
    width: 48px;
    height: 20px;
    margin-left: 4px;
    font-size: 12px;
    line-height: 20px;
    color: $--color-text-regular;
    text-align: center;
    background-color: $--background-color-base;
    border-bottom-right-radius: 10px;
  }
}

.dropdown-menu {
  max-height: 400px;
  overflow: auto;
}

</style>
