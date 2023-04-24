<template>
  <div class="project-table">
    <div class="filter-wrapper">
      <wk-filter-header
        :search.sync="keywords"
        :props="config"
        :tabs="myGroupList"
        @event-change="handleFilterEvt"
        @tabs-change="sceneSelect">
        <el-dropdown
          v-if="activeSortField"
          slot="right"
          @command="handleSort">
          <el-button>
            <i class="sort-icon wk wk-iphone-sorting" />
            {{ activeSortField.name }}
          </el-button>
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item
              v-for="item in sortFieldList"
              :key="item.fieldName"
              :command="item.fieldName">
              {{ item.name }}
            </el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>

        <template
          v-if="type == 'participate'"
          slot="dropdownMore"
        >
          <el-dropdown-item
            v-for="(item, index) in groupOptionsList"
            :key="item.value"
            :divided="index === 0"
            :command="item.value">
            {{ item.label }}
          </el-dropdown-item>
        </template>
      </wk-filter-header>
    </div>

    <el-table
      v-loading="loading"
      :data="tableData"
      :height="type == 'participate' ? participateHeight : tableHeight"
      class="table"
      @row-click="handleRowClick">
      <el-table-column
        label="项目名称"
        prop="name"
        width="280"
        fixed="left">
        <template slot-scope="scope">
          <flexbox>
            <flexbox
              align="center"
              justify="center"
              class="icon-box">
              <i :class="scope.row.icon" class="icon" />
            </flexbox>
            <span class="can-visit--underline project-name text">
              {{ scope.row.name }}
              <span v-if="scope.row.type == 2" class="agile-sign">敏捷</span>
              <span v-else-if="scope.row.type == 1" class="ordinary-sign">普通</span>
            </span>
          </flexbox>
        </template>
      </el-table-column>

      <el-table-column
        label="完成度"
        width="280">
        <template slot-scope="{row}">
          <flexbox>
            <flexbox-item>
              <el-progress
                :percentage="Math.round( row.finishedCount * 100 / (row.finishedCount + row.notStartCount + row.underwayCount)) || 0"
                :show-text="false" />
            </flexbox-item>
            <span class="text">{{ row.finishedCount }}/{{ row.notStartCount }}/{{ row.underwayCount }}</span>
          </flexbox>
        </template>
      </el-table-column>

      <!-- <el-table-column label="优先级">
        <template slot-scope="{ row }">
          <el-tag
            v-if="!isEmpty(row.level)"
            disable-transitions
            :style="{
              color: getPriorityTag(row.level).color,
              backgroundColor: getPriorityTag(row.level).bg
            }"
            class="project-tag">
            {{ getPriorityTag(row.level).label }}
          </el-tag>
        </template>
      </el-table-column> -->

      <el-table-column
        min-width="170"
        label="截止时间">
        <template v-if="row.stopTime" slot-scope="{ row }">
          <el-tag
            disable-transitions
            :class="[getOverTimeStatus(row.stopTime)]"
            class="project-tag">
            {{ formatTime(row.stopTime) + '截止' }}
          </el-tag>
        </template>
      </el-table-column>

      <el-table-column
        prop="createTime"
        label="创建时间"
        width="180" />

      <el-table-column
        min-width="170"
        label="管理员"
        :formatter="formatter">
        <!-- <template slot-scope="{ row }">
          <flexbox
            v-if="!isEmpty(row.projectOwnerRoleList)"
            align="center"
            justify="flex-start">
            <xr-avatar
              v-for="user in row.projectOwnerRoleList"
              :id="user.userId"
              :key="user.userId"
              :src="user.img"
              :name="user.realname"
              :size="20" />
          </flexbox>
        </template> -->
      </el-table-column>

      <el-table-column
        v-if="type !== 'all'"
        width="60"
        fixed="right"
        align="center">
        <!-- eslint-disable-next-line vue/no-unused-vars -->
        <template slot="header" slot-scope="scope">
          <i class="el-icon-star-off focus-icon is-disabled" />
        </template>
        <template slot-scope="scope">
          <el-tooltip :content="scope.row.collectStatus == 1 ? '添加星标' : '取消星标'" effect="dark" placement="top">
            <el-button
              type="text"
              :icon="scope.row.collectStatus == 2 ? 'wk wk-focus-on focus-icon active' : 'el-icon-star-off focus-icon'"
              :class="{ active: scope.row.collectStatus == 2 }"
              class="focus-btn"
              @click="handleToggleFocus(scope.row)" />
          </el-tooltip>
        </template>
      </el-table-column>

      <el-table-column
        width="90"
        fixed="right"
        align="center"
        label="操作">
        <template slot-scope="scope">
          <el-dropdown v-if="commandAuthList(scope.row).length" @command="handleCommand($event, scope.row)">
            <el-button icon="el-icon-more" type="text" />
            <el-dropdown-menu slot="dropdown">
              <!-- trigger="hover" -->
              <el-dropdown @command="handleCommand($event, scope.row)">
                <el-dropdown-item
                  v-for="(item, index) in commandAuthList(scope.row) || []"
                  :key="index"
                  :command="item.value"
                  :style="{'color': item.color ? item.color : '' }">
                  {{ item.label }}
                </el-dropdown-item>
                <el-dropdown-menu slot="dropdown" class="groupCard">
                  <el-dropdown-item
                    v-for="(item,index) in myGroupList.filter(item => item.value && item.value != 0)"
                    :key="index"
                    :command="handlerCommand('move',item.value)">{{ item.label }}</el-dropdown-item>
                </el-dropdown-menu>
              </el-dropdown>
            </el-dropdown-menu>
          </el-dropdown>
        </template>
      </el-table-column>
    </el-table>

    <div class="p-contianer">
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

    <group-manage-dialog
      v-if="groupDialogVisible"
      :list-data="myGroupList"
      :visible.sync="groupDialogVisible"
      @addGroup="addGroup"
      @close="groupDialogVisible = false" />

    <create-group-dialog
      v-if="createGroupDialogVisible"
      :visible.sync="createGroupDialogVisible"
      @addGroup="addGroup"
      @close="createGroupDialogVisible = false" />
  </div>
</template>

<script>
import {
  projectParticipateListAPI,
  projectOwnerListAPI,
  projectQueryAllAPI,
  projectArchiveAPI,
  projectCollectAPI,
  moveToGroupAPI
} from '@/api/pm/manage'

import WkFilterHeader from '@/components/NewCom/WkFilterHeader'
import CreateGroupDialog from './CreateGroupDialog'
import GroupManageDialog from './GroupManageDialog'

import { mapGetters } from 'vuex'
import { isEmpty } from '@/utils/types'
import { PRIORITY_ENUM } from '@/views/pm/enum'
import { getRowValueByKey } from '@/utils'
import Lockr from 'lockr'

export default {
  name: 'ProjectTable', // 项目列表公共表格组件
  components: {
    WkFilterHeader,
    CreateGroupDialog,
    GroupManageDialog
  },
  props: {
    type: {
      type: String,
      required: true,
      validator: val => {
        return [
          'all', // 全部
          'owner', // 我负责的
          'participate' // 我参与的
        ].includes(val)
      }
    },
    myGroupList: {
      type: Array,
      default: () => []
    }
  },
  data() {
    return {
      loading: false,
      keywords: '',
      config: {
        searchPlaceholder: '搜索项目'
      },
      sceneId: '', // 场景筛选ID
      sceneData: {}, // 场景对象
      groupOptionsList: [
        { label: '创建分组', value: 'create' },
        { label: '管理分组', value: 'manage' }
        // { label: '批量整理项目', value: 'batch', divided: true }
      ],
      groupCommand: null,
      groupDialogVisible: false,
      createGroupDialogVisible: false,
      activeSortField: null,
      sortFieldList: [
        { name: '按访问时间', fieldName: 'updateTime' },
        { name: '按创建时间', fieldName: 'createTime' }
      ],
      tableData: [],
      participateHeight: document.documentElement.clientHeight - 460,
      tableHeight: document.documentElement.clientHeight - 265,
      currentPage: 1,
      pageSize: Lockr.get('crmPageSizes') || 15,
      pageSizes: [15, 30, 60, 100],
      total: 0
    }
  },
  computed: {
    ...mapGetters([
      'userInfo'
    ])
  },
  mounted() {
    this.activeSortField = this.sortFieldList[0]
    this.getData()
  },
  methods: {
    isEmpty(val) {
      return isEmpty(val)
    },

    /**
     * @description: 根据权限过滤操作显示数据
     * @param {*} row
     * @return {*}
     */
    commandAuthList(row) {
      if (!row.userProjectAuth) return []
      const commandList = [
        { label: '项目设置', value: 'setting', auth: 'set' },
        { label: '归档项目', value: 'archive', auth: 'set.editProjectInfo' },
        { label: '删除项目', value: 'delete', auth: 'set.editProjectInfo', color: '#de350b' }
      ]
      if (this.type == 'participate') {
        commandList.unshift({ label: '移动到分组', value: 'group', auth: '' })
      }
      return commandList.filter(item => {
        if (item.auth) {
          return getRowValueByKey(row.userProjectAuth, item.auth)
        }
        return true
      })
    },

    /**
     * 优先级
     * @param value
     * @returns {null|*|{}}
     */
    getPriorityTag(value) {
      if (isEmpty(value)) return null
      return PRIORITY_ENUM.find(o => o.value === value) || {}
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
      const nowDataArr = [todayDate.getFullYear(), todayDate.getMonth() + 1, todayDate.getDate()] // 今天的 年 月 日
      const tomorrowData = new Date(todayDate.setTime(todayDate.getTime() + 24 * 60 * 60 * 1000)) // 明天
      const tomorrowDataArr = [tomorrowData.getFullYear(), tomorrowData.getMonth() + 1, tomorrowData.getDate()]// 明天的 年 月 日
      const yesterdayDate = new Date(todayDate.setTime(todayDate.getTime() - 48 * 60 * 60 * 1000)) // 昨天
      const yesterdayDateArr = [yesterdayDate.getFullYear(), yesterdayDate.getMonth() + 1, yesterdayDate.getDate()]// 昨天的 年 月 日
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
      * @description: 格式化管理员字段
      * @param {*} row
      * @param {*} column
      * @return {*}
      */
    formatter(row, column) {
      return (row.projectAdminList || []).join()
    },

    /** 列表操作 */
    /**
     * 当某一行被点击时会触发该事件
     */
    handleRowClick(row, column, event) {
      if (column.property && column.property == 'name') {
        let isMember = false
        const username = this.userInfo.realname
        if ((row.projectAdminList || []).includes(username)) {
          isMember = true
        }
        this.$router.push({
          name: 'projectTeam',
          params: {
            id: row.projectId,
            isOpen: row.isOpen,
            isMember
          }
        })
      }
    },

    /**
     * @description: 获取列表
     * @return {*}
     */
    getData() {
      this.loading = true
      const request = {
        'participate': projectParticipateListAPI,
        'owner': projectOwnerListAPI,
        'all': projectQueryAllAPI
      }[this.type]
      const params = {
        page: this.currentPage,
        limit: this.pageSize,
        name: this.keywords,
        sortType: this.activeSortField.fieldName == 'updateTime' ? 1 : 2
      }
      if (this.type == 'participate') {
        params.groupId = this.sceneId
      }
      request(params)
        .then(res => {
          this.tableData = res.data.list
          this.total = res.data.totalRow
          this.loading = false
        })
        .catch(() => {
          this.loading = false
        })
    },

    /**
     * @description: 创建分组
     * @return {*}
     */
    addGroup() {
      this.$emit('getGroup')
    },

    /**
     * 刷新列表
     */
    refresh() {
      this.getData()
      this.$emit('getList')
    },

    /**
     * 筛选操作
     * @param type
     * @param data
     */
    handleFilterEvt(type, data) {
      console.log(type, data)
      if (type == 'search') {
        this.keywords = data
        this.refresh()
      } else if (type == 'create') {
        this.groupCommand = type
        this.createGroupDialogVisible = true
      } else if (type == 'manage') {
        this.groupCommand = type
        this.groupDialogVisible = true
      }
    },

    /**
     * 场景操作
     * @param {*} data
     */
    sceneSelect(data) {
      this.sceneData = data
      this.sceneId = data.value
      this.refresh()
    },

    /**
     * 排序
     * @param fieldName
     */
    handleSort(fieldName) {
      this.activeSortField = this.sortFieldList.find(o => o.fieldName === fieldName)
      this.getData()
      // TODO 排序
    },

    /**
     * 修改关注状态
     */
    handleToggleFocus(data) {
      projectCollectAPI({ projectId: data.projectId })
        .then(res => {
          data.collectStatus = data.collectStatus == 2 ? 1 : 2
          this.refresh()
        })
        .catch(() => {})
    },

    /**
     * 操作
     * @param command
     * @param data
     */
    handleCommand(command, data) {
      if (command.flag == 'move') {
        console.log(command.command, data)
        const params = {
          groupId: command.command,
          projectId: data.projectId
        }
        moveToGroupAPI(params)
          .then(res => {
            console.log(res)
            this.$message.success('操作成功')
            this.refresh()
            this.$emit('getGroup')
          })
          .catch(() => {})
      }
      switch (command) {
        case 'group':
          break
        case 'setting':
          this.$router.push({
            name: 'projectSetting',
            params: {
              setting: data.projectId
            }
          })
          break
        case 'archive':
          this.archiveProject(data)
          break
        case 'delete':
          this.deleteProject(data)
          break
      }
    },

    /**
     * @description: 移动到分组-选项
     * @param {*} flag
     * @param {*} command
     * @return {*}
     */
    handlerCommand(flag, command) {
      return {
        flag,
        command
      }
    },

    /**
     * @description: 提示公共方法
     * @param {*} msg
     * @return {*}
     */
    confirm(msg) {
      return new Promise((resolve, reject) => {
        this.$confirm(
          msg,
          '提示',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消'
          }
        )
          .then(() => {
            resolve()
          })
          .catch(() => {
            reject()
          })
      })
    },

    /**
     * 删除项目
     * @param item
     */
    deleteProject(item) {
      this.confirm(`确定要删除“${item.name}”项目吗，删除后，项目下的所有数据也将删除，且不可恢复！`)
        .then(() => {
          projectArchiveAPI({
            projectId: item.projectId,
            setType: 3
          })
            .then(() => {
              this.$message.success('操作成功')
              this.refresh()
            })
            .catch(() => {})
        })
    },

    /**
     * 归档项目
     * @param item
     */
    archiveProject(item) {
      this.confirm(`点击归档项目操作后，将无法继续访问和操作该项目。若需解除归档，可在「归档项目」页进行操作`)
        .then(() => {
          projectArchiveAPI({
            projectId: item.projectId,
            setType: 2
          })
            .then(() => {
              this.$message.success('操作成功')
              this.refresh()
            })
            .catch(() => {})
        })
    },

    /**
     * 更改每页展示数量
     * @param {*} val
     */
    handleSizeChange(val) {
      Lockr.set('crmPageSizes', val)
      this.pageSize = val
      this.refresh()
    },

    /**
     * 更改当前页数
     * @param {*} val
     */
    handleCurrentChange(val) {
      this.currentPage = val
      this.refresh()
    }
  }
}
</script>

<style scoped lang="scss">
.project-table {
  width: 100%;

  .filter-wrapper {
    margin-top: 30px;
    margin-bottom: 20px;
    line-height: 32px;

    .group-manage {
      margin-left: 8px;
    }

    .sort-icon {
      font-size: $--font-size-small;
    }
  }

  .el-table {
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

    .xr-avatar {
      margin-right: 4px;
    }

    .icon-header {
      color: $--color-n50;
    }

    .focus-btn {
      font-size: 18px;
      font-weight: bold;
      color: $--color-n40;
      cursor: pointer;

      /deep/ i {
        font-weight: bold;
      }

      .wk-focus-on {
        font-size: 13px;
      }

      &.active {
        color: $--color-y300;

        /deep/ i {
          font-weight: normal;
        }
      }
    }

    // 关注
    .focus-icon {
      font-size: 18px;
      font-weight: bold;
      color: $--color-n40;
      cursor: pointer;

      &.is-disabled {
        color: $--color-n20;
        cursor: not-allowed;
      }
    }
  }
}
</style>
