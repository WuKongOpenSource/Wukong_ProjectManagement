<template>
  <div class="o-wrap">
    <div class="o-right">
      <div class="title">
        <span class="name">事项状态趋势</span>
      </div>
      <div v-if="taskTrend && taskTrend.projectTaskNumVOEnd" ref="trendChart" class="chart" />
      <div v-if="taskTrend && !taskTrend.projectTaskNumVOEnd" class="tips">
        <span v-if="!detail.startTime || !detail.stopTime">迭代周期未设置，暂无数据</span>
        <span v-else-if="detail.status == 1">迭代未开始，暂无数据</span>
      </div>

      <div class="title">
        <span class="name">燃尽图</span>
      </div>
      <div v-if="taskBurnout" ref="chart" class="chart" />
      <div v-if="!taskBurnout" class="tips">
        <span v-if="!detail.startTime || !detail.stopTime">迭代周期未设置，暂无数据</span>
        <span v-else-if="detail.status == 1">迭代未开始，暂无数据</span>
      </div>

      <div class="title">
        <span class="name">
          事项分布
          <i
            data-type="4"
            data-id="9"
            style="margin-right: 8px;"
            class="wk wk-icon-fill-help wk-help-tips" />
        </span>
      </div>
      <div ref="barChart" class="chart" />

      <div style="min-height: 240px; margin-bottom: 48px;">
        <div class="title">
          <span class="name" style="margin-top: 48px;">活动日志</span>
        </div>
        <div
          v-if="list"
          v-empty="list"
          class="rc-cont"
          style="padding-right: 10%;">
          <flexbox
            v-for="(item, index) in list"
            :key="index"
            class="ha-cont"
            align="stretch"
            justify="flex-start">
            <div class="ha-week">{{ item.createTime|filterTimestampToFormatTime('MM-DD dddd') }}</div>
            <div class="ha-circle" />
            <div class="ha-time">{{ item.createTime|filterTimestampToFormatTime('HH:mm') }}</div>
            <xr-avatar
              v-if="item"
              :id="item.logId"
              :name="item.realname"
              :size="32"
              :src="item.img"
              :disabled="false"
              class="ha-img" />
            <div v-if="item.user" class="ha-name">{{ item.user.realname }}</div>
            <div class="ha-content">
              <p>{{ item.content }}</p>
            </div>
            <div class="ha-line" />
          </flexbox>
        </div>
      </div>
    </div>

    <div v-loading="loading" class="o-left">
      <div class="title">
        <span class="name">迭代信息</span>
        <el-button
          icon="el-icon-edit"
          :disabled="!$auth('coordination.editIteration', 'PM')"
          style="padding: 0; background-color: #fff;"
          @click="editIteration" />
      </div>
      <div class="info">
        <div v-for="(item, index) in infoList" :key="index" class="infoItem">
          <span>{{ item.title }}</span>
          <div v-if="item.type == 'status'">
            <status-tag :type="detail.status" />
          </div>
          <div v-else-if="item.type == 'progress'" style="flex: 1; text-align: right;">
            <div class="progress">
              <div class="strip">
                <span v-if="computeNum(3)" :style="{'width': computeNum(3)}" style="background-color: #36b37e;" />
                <span v-if="computeNum(2)" :style="{'width': computeNum(2)}" style="background-color: #ffab00;" />
                <span v-if="computeNum(1)" :style="{'width': computeNum(1)}" style="background-color: #0052cc;" />
              </div>
              <span class="text">
                {{ Math.round(((matterList.filter(val => val.status == 3).length || 0) * 100 / (matterList.length)) ) || 0 }}%
              </span>
            </div>
            <!-- <el-progress
              :percentage="" /> -->
          </div>
          <div v-else-if="item.type == 'director'">
            {{ detail.mainUserName }}
            <!-- <flexbox
              align="center"
              justify="flex-start">
              <xr-avatar
                :id="detail.createUserId"
                :src="detail.createUserImg"
                :name="detail.createUserName"
                :size="24" />
              <div style="padding-left: 10px;">{{ detail.createUserName }}</div>
            </flexbox> -->
          </div>
          <div v-else-if="item.type == 'member'">
            <flexbox
              align="center"
              justify="flex-end"
              wrap="wrap"
              class="participant-bd">
              <span
                v-for="(user, ind) in ownerUserShowList"
                :key="ind"
                :class="['owner-list',{'owner-list-fold':projectDetail.projectOwnerRoleList.length < 5 }]">
                <xr-avatar
                  :id="user.userId"
                  :name="user.realname"
                  :size="24"
                  :src="user.img"
                  class="user-img" />
              </span>
              <el-dropdown
                v-if="projectDetail.projectOwnerRoleList.length > 5"
                trigger="click"
                :hide-on-click="false">
                <el-button class="dropdown-btn owner-list-button">
                  +{{ projectDetail.projectOwnerRoleList.length - 5 }}
                </el-button>
                <el-dropdown-menu slot="dropdown" class="owner-list-dropdown">
                  <el-dropdown-item
                    v-for="(v,i) in ownerUserHideList"
                    :key="i"
                    class="owner-list-dropdown-list">
                    <xr-avatar
                      :id="v.userId"
                      :name="v.realname"
                      :size="24"
                      :src="v.img"
                      style="margin-right: 10px;"
                      class="owner-list-dropdown-img" />
                    <p class="owner-list-dropdown-text">{{ v.realname }} </p>
                  </el-dropdown-item>
                </el-dropdown-menu>
              </el-dropdown>
              <!-- <xr-avatar
                  v-for="user in projectDetail.projectOwnerRoleList"
                  :id="user.userId"
                  :key="user.userId"
                  :src="user.img"
                  :name="user.realname"
                  :size="24" /> -->
            </flexbox>
          </div>
          <div v-else>
            {{ time }}
          </div>
        </div>
      </div>
      <div class="title">
        <span class="name">总事项数</span>
        <span>{{ matterList.length || 0 }}</span>
      </div>
      <div class="info">
        <div v-for="(item, index) in statusList" :key="index" class="infoItem">
          <span>
            <i :class="item.icon" :style="{color: item.color}" />
            <span>{{ item.title }}</span>
          </span>
          <span>{{ matterList.filter(val => val.status == item.status).length || 0 }}</span>
        </div>
      </div>
      <div class="title">
        <span class="name">迭代目标</span>
      </div>
      <div class="target">
        <span>{{ detail.target || '--' }}</span>
      </div>
    </div>

    <!-- 编辑迭代 -->
    <create-iteration
      v-if="editIterationVisible"
      :visible.sync="editIterationVisible"
      :detail-data="detail"
      type="edit"
      @save-success="handleRefresh"
      @close="editIterationVisible = false" />
  </div>
</template>

<script>
import {
  workQueryIterationAllItemAPI,
  queryActivityListAPI,
  getTaskBurnoutAPI,
  getProjectTaskEventAPI,
  getTaskByTimeAPI,
  detailIterationAPI
} from '@/api/pm/projectTask'

import StatusTag from '@/views/pm/project/components/StatusTag'
import CreateIteration from '@/views/pm/project/team/CreateIteration'

import ChartMixin from './components/ChartMixin'
import moment from 'moment'
import { isObject } from '@/utils/types'

export default {
  name: 'IterationOverview', // 概览与统计
  components: {
    StatusTag,
    CreateIteration
  },
  mixins: [ChartMixin],
  props: {
    iterationId: [String, Number],
    projectDetail: Object
  },
  data() {
    return {
      infoList: [],
      statusList: [],
      loading: false,
      detail: {},
      list: [], // 活动列表
      matterList: [],
      taskTrend: null, // 趋势图
      taskBurnout: null, // 燃尽图
      taskEvent: null, // 事项分布图
      editIterationVisible: false // 编辑迭代
    }
  },
  computed: {
    time() {
      return (this.detail.startTime ? moment(this.detail.startTime).format('YYYY-MM-DD') : '--') + '至' + (this.detail.stopTime ? moment(this.detail.stopTime).format('YYYY-MM-DD') : '--')
    },
    ownerUserShowList() {
      const ownerUserList = this.projectDetail.projectOwnerRoleList || []
      if (ownerUserList.length > 0) {
        return ownerUserList.slice(0, 5)
      }
      return ownerUserList
    },
    ownerUserHideList() {
      const ownerUserList = this.projectDetail.projectOwnerRoleList || []
      if (ownerUserList.length > 0) {
        return ownerUserList.slice(5)
      }
      return ownerUserList
    }
  },
  created() {
    this.getAllInfo()
  },
  mounted() {
    // this.initBarChart()
  },
  methods: {
    getAllInfo() {
      this.getInfo()
      this.getMatterList()
      this.getList()
      this.getDetail()
      this.getChartData()
    },

    /**
     * @description: 迭代信息卡片字段名
     * @return {*}
     */
    getInfo() {
      this.infoList = [{
        title: '迭代状态',
        type: 'status',
        prop: 'status'
      }, {
        title: '迭代周期',
        type: '',
        prop: 'cycle'
      }, {
        title: '迭代进度',
        type: 'progress',
        prop: 'progress'
      }, {
        title: '负责人',
        type: 'director',
        prop: 'realname'
      }, {
        title: '团队成员',
        type: 'member',
        prop: 'users'
      }]
      this.statusList = [{
        title: '已完成',
        prop: 'finishedCount',
        icon: 'wk wk-icon-success',
        color: '#36B37E',
        status: 3
      }, {
        title: '进行中',
        prop: 'notStartCount',
        icon: 'wk wk-icon-in-review',
        color: '#FFAB00',
        status: 2
      }, {
        title: '未开始',
        prop: 'underwayCount',
        icon: 'wk wk-icon-uncommitted',
        color: '#0052CC',
        status: 1
      }]
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
        this.detail = res.data
      }).catch(() => {

      })
    },

    /**
     * @description: 计算分别状态的百分比
     * @param {*} val
     * @return {*}
     */
    computeNum(status) {
      const num = this.matterList.filter(val => val.status == status).length || 0
      const wid = Math.round((num * 150 / this.matterList.length) || 0)
      if (wid == 0) return false
      return wid + 'px'
    },

    /**
     * @description: 编辑迭代
     * @return {*}
     */
    editIteration() {
      this.editIterationVisible = true
    },

    /**
     * @description: 刷新迭代
     * @return {*}
     */
    handleRefresh() {
      this.editIterationVisible = false
      this.$emit('refresh', { type: 'edit' })
      this.getDetail()
      this.getChartData()
    },

    /**
     * @description: 获取燃尽图/事件分布图数据
     * @return {*}
     */
    getChartData() {
      const params = {
        projectId: this.$route.params.id,
        taskId: this.iterationId
      }
      Promise.all([
        getTaskByTimeAPI(params),
        getProjectTaskEventAPI(params),
        getTaskBurnoutAPI(params)
      ]).then(resArr => {
        console.log(resArr)
        this.taskTrend = resArr[0].data
        this.taskEvent = resArr[1].data
        this.taskBurnout = resArr[2].data
        this.$nextTick(() => {
          this.initChart()
        })
      })
    },

    /**
     * @description: 获取迭代下事项列表
     * @param {*} type
     * @return {*}
     */
    getMatterList(type) {
      this.loading = true
      const params = {
        belongIterationId: this.iterationId,
        type: '',
        projectId: this.$route.params.id,
        pageType: 0
      }
      workQueryIterationAllItemAPI(params).then(res => {
        this.matterList = res.data.list
        if (res.data.totalRow && Math.ceil(res.data.totalRow / this.pageSize) < this.currentPage && this.currentPage > 1) {
          this.currentPage = this.currentPage - 1
          this.getMatterList()
        } else {
          this.total = res.data.totalRow
          this.loading = false
        }
        this.$nextTick(() => {
          document.querySelector('.el-table__body-wrapper').scrollTop = 1
        })
        // 数据更新刷新列
        this.updateTableHeight()
      }).catch(() => {
        this.loading = false
      })
    },

    /**
     * @description: 获取活动列表
     * @return {*}
     */
    getList() {
      queryActivityListAPI({
        taskId: this.iterationId,
        type: 1
      })
        .then(res => {
          console.log(res)
          this.list = res.data || []
        })
        .catch(() => {})
    },

    /**
     * @description: 判断是否是对象
     * @param {*} data
     * @return {*}
     */
    isObject(data) {
      return isObject(data)
    }
  }
}
</script>
<style lang='scss' scoped>
.o-wrap {
  display: flex;
  align-items: flex-start;
  justify-content: flex-start;
  height: calc(100% - 61px);
  overflow: hidden;

  .title {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 8px 0;
    font-size: 16px;

    .name {
      font-weight: bold;
    }
  }

  .o-left {
    width: 300px;
    height: calc(100% - 30px);
    padding: 8px 16px;
    margin-left: 32px;
    overflow-y: auto;
    border: 1px solid $--border-color-base;
    border-radius: 4px;

    .info {
      padding-bottom: 16px;
      margin-bottom: 16px;
      font-size: 14px;
      color: $--color-text-regular;
      border-bottom: 1px solid $--border-color-base;

      .infoItem {
        display: flex;
        align-items: center;
        justify-content: space-between;
        padding: 8px 0;

        .owner-list {
          position: relative;
          display: inline-block;

          // width: 38px;
          // height: 38px;
          margin-right: 10px;
          margin-right: -13px;
          border: 3px solid $--color-white;
          border-radius: 50%;
        }

        .owner-list-fold:nth-last-child(1) {
          margin-right: 0;
        }

        .owner-list-button {
          z-index: 3;
          width: 32px;
          height: 32px;
          padding: 0;
          color: $--color-white;
          background: $--color-n90;
          border: 3px solid $--color-white;
          border-radius: 50%;
        }
      }

      .progress {
        display: flex;
        align-items: center;

        .strip {
          display: flex;
          flex: 1;
          justify-content: flex-end;
          padding-left: 20px;

          span {
            display: block;
            height: 4px;
          }
        }

        .strip span:first-child {
          border-top-left-radius: 2px;
          border-bottom-left-radius: 2px;
        }

        .strip span:last-child {
          border-top-right-radius: 2px;
          border-bottom-right-radius: 2px;
        }

        .text {
          // width: 32px;
          padding-left: 4px;
        }
      }
    }

    .target {
      font-size: 14px;
      color: $--color-text-regular;
    }
  }

  .o-right {
    width: calc(100% - 332px);
    height: calc(100% - 30px);
    overflow-y: auto;

    .chart {
      width: 100%;
      height: 200px;
      padding-right: 8px;
    }

    .tips {
      width: 100%;
      height: 200px;
      line-height: 200px;
      text-align: center;
    }

    .ha-cont {
      position: relative;
      min-height: 40px;
      padding-top: 3px;
      line-height: 20px;

      .ha-week {
        flex-shrink: 0;
        width: 90px;
        margin: 0 17px 0 10px;
      }

      .ha-time {
        flex-shrink: 0;
        width: 80px;
        padding: 0 10px 0 24px;
      }

      .ha-circle {
        z-index: 2;
        flex-shrink: 0;
        width: 18px;
        height: 18px;
        background-color: white;
        border: 5px solid $--color-primary;
        border-radius: 9px;
      }

      .ha-img {
        display: block;
        flex-shrink: 0;
        margin: -3px 10px 0;
      }

      .ha-name {
        flex-shrink: 0;
        padding: 0 10px;
      }

      .ha-content {
        flex: 1;
        padding: 0 10px 10px;
      }

      .ha-line {
        position: absolute;
        top: 3px;
        bottom: -3px;
        left: 125px;
        z-index: 1;
        width: 1px;
        background-color: $--border-color-base;
      }
    }
  }
}

.owner-list-dropdown {
  width: 120px;
  max-height: 300px;
  overflow-y: auto;

  &-list {
    display: flex;
    align-items: center;
    padding-top: 5px;
    padding-bottom: 5px;
    border-left: 2px solid $--color-white;

    &:hover {
      background: $--color-n20 !important;
      border-left: 2px solid $--color-b400;
    }

    &:hover .owner-list-dropdown-text {
      color: $--color-n500;
    }
  }

  &-img {
    height: 24px;
  }

  &-text {
    display: inline-block;
    width: 80px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
}
</style>
