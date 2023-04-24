<template>
  <div class="pm-project-manage">
    <wk-page-header
      title="我参与的">
      <template slot="right">
        <el-button
          type="primary"
          @click="handleToCreate">新建项目</el-button>
      </template>
    </wk-page-header>

    <div class="favourite-wrapper">
      <div class="title">星标项目</div>

      <div
        v-if="favouriteList"
        v-empty="favouriteList"
        xs-empty-text="暂未设置星标项目"
        class="container rc-cont">
        <flexbox
          align="center"
          justify="flex-start"
          class="favourite-list">
          <div
            v-for="(item, index) in favouriteList"
            :key="index"
            class="favourite-list-item"
            @click="pushDetail(item)">
            <flexbox class="content">
              <flexbox
                align="center"
                justify="center"
                class="icon-box">
                <i :class="item.icon" />
              </flexbox>
              <div class="info">
                <div class="name text-one-line">{{ item.name }}</div>
                <div class="desc text-one-line">{{ item.description || '--' }}</div>
              </div>
            </flexbox>

            <div ref="chart" class="chart" />

            <flexbox class="footer">
              <flexbox-item class="text">已完成工作趋势</flexbox-item>
              <span @click.stop>
                <el-dropdown @command="handleCommand($event, item)">
                  <el-button icon="el-icon-more" type="text" />
                  <el-dropdown-menu slot="dropdown">
                    <!-- trigger="hover" -->
                    <el-dropdown @command="handleCommand($event, item)">
                      <el-dropdown-item
                        v-for="i in commandAuthList(item)"
                        :key="i.value"
                        :command="i.value"
                        :style="{'color': i.color ? i.color : '' }">
                        {{ i.label }}
                      </el-dropdown-item>
                      <el-dropdown-menu slot="dropdown" class="groupCard">
                        <el-dropdown-item
                          v-for="(val,ind) in myGroupList.filter(item => item.value && item.value != 0)"
                          :key="ind"
                          :command="handlerCommand('move',val.value)">{{ val.label }}</el-dropdown-item>
                      </el-dropdown-menu>
                    </el-dropdown>
                  </el-dropdown-menu>
                </el-dropdown>
              </span>
            </flexbox>
            <el-tooltip content="取消星标" effect="dark" placement="top">
              <el-button
                type="text"
                icon="wk wk-focus-on"
                class="focus-btn"
                @click.stop="handleToggleFocus(item)" />
            </el-tooltip>
          </div>
        </flexbox>
      </div>
    </div>

    <project-table
      ref="table"
      type="participate"
      class="table-container"
      :my-group-list="myGroupList"
      @getGroup="getGroup"
      @getList="getFavouriteList" />

    <create-project-dialog
      v-if="createVisible"
      :visible.sync="createVisible"
      @save-success="handleRefresh"
      @close="createVisible = false" />
  </div>
</template>

<script>
import {
  projectCollectListAPI,
  searchGroupListAPI
} from '@/api/pm/manage'

import WkPageHeader from '@/components/Page/WkPageHeader'
import ProjectTable from './components/ProjectTable'
import CreateProjectDialog from './components/CreateProjectDialog'

import * as echarts from 'echarts'
import { objDeepCopy } from '@/utils'
import { getRowValueByKey } from '@/utils'

export default {
  name: 'ManageParticipateIndex', // 我参与的项目页面
  components: {
    WkPageHeader,
    ProjectTable,
    CreateProjectDialog
  },
  data() {
    return {
      favouriteList: [],
      myGroupList: [],
      createVisible: false,
      chartOption: {
        color: ['#0052CC'],
        grid: {
          left: 8,
          top: 5,
          right: 8,
          bottom: 0
        },
        xAxis: {
          type: 'category',
          // data: ['1', '2', '3', '4', '5', '6'],
          axisTick: {
            show: false
          },
          axisLine: {
            show: false
          },
          axisLabel: {
            show: false
          }
        },
        yAxis: {
          type: 'value',
          splitLine: {
            show: false
          }
        },
        series: [
          {
            data: [],
            symbol: 'none',
            smooth: true,
            type: 'line',
            areaStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                {
                  offset: 0,
                  color: 'rgba(0, 82, 204, 0.2)'
                },
                {
                  offset: 1,
                  color: 'rgba(0, 82, 204, 0)'
                }
              ])
            }
          }
        ]
      },
      charts: []
    }
  },
  created() {
    this.getFavouriteList()
    this.getGroup()
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.resizeCallback)
  },
  methods: {
    /**
     * @description: 查询星标项目
     * @return {*}
     */
    async getFavouriteList() {
      this.loading = true
      await projectCollectListAPI()
        .then(res => {
          if (res.data) {
            this.favouriteList = res.data.list || []
          } else {
            this.favouriteList = []
          }
          this.loading = false
        }).catch(() => {
          this.loading = false
        })
      this.$nextTick(() => {
        this.initChart()
      })
    },

    commandAuthList(obj) {
      const commandList = [
        { label: '移动到分组', value: 'group' },
        { label: '项目设置', value: 'setting', auth: 'set' },
        { label: '归档项目', value: 'archive', auth: 'set.editProjectInfo' },
        { label: '删除项目', value: 'delete', auth: 'set.editProjectInfo', color: '#de350b' }
      ]
      return commandList.filter(item => {
        if (item.auth) {
          return getRowValueByKey(obj.userProjectAuth, item.auth)
        }
        return true
      })
    },

    /**
     * @description: 星标跳转到详情
     * @return {*}
     */
    pushDetail(data) {
      this.$router.push({
        name: 'projectOverview',
        params: {
          id: data.projectId
        }
      })
    },

    /**
     * @description: 初始化echarts
     * @return {*}
     */
    initChart() {
      this.charts = []
      const elems = this.$refs.chart || []
      elems.forEach((el, index) => {
        const chart = echarts.init(el)
        const chartOptions = objDeepCopy(this.chartOption)
        const list = []
        this.favouriteList[index].projectTaskCountVO.projectTaskNumVOEnd.forEach(item => {
          list.push(item.num)
        })
        chartOptions.series[0].data = list
        chart.setOption(chartOptions, true)
        this.charts.push(chart)
      })
      window.addEventListener('resize', this.resizeCallback)
    },

    /**
     * @description: 改变图表大小
     * @return {*}
     */
    resizeCallback() {
      this.charts.forEach(chart => {
        chart.resize()
      })
    },

    /**
     * @description: 列表更多操作
     * @param {*} command
     * @param {*} item
     * @return {*}
     */
    handleCommand(command, item) {
      this.$refs.table.handleCommand(command, item)
    },

    /**
     * @description: 移动到分组更多操作
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
     * @description: 取消收藏
     * @param {*} item
     * @return {*}
     */
    handleToggleFocus(item) {
      this.$refs.table.handleToggleFocus(item)
    },

    /**
     * @description: 新建项目
     * @return {*}
     */
    handleToCreate() {
      this.createVisible = true
    },

    /**
     * @description: 保存新建
     * @return {*}
     */
    handleRefresh() {
      this.$refs.table.refresh()
    },

    /**
     * @description: 获取分组列表
     * @return {*}
     */
    getGroup() {
      searchGroupListAPI()
        .then(res => {
          // console.log(res, '分组')
          // const list = [{
          //   label: '全部项目',
          //   value: ''
          // }, {
          //   label: '未分组',
          //   value: 0
          // }]
          const list = []
          // eslint-disable-next-line
          for (const item of res.data || []) {
            list.push({
              label: item.name,
              value: item.groupId,
              num: item.num || 0,
              type: item.type
            })
          }
          this.myGroupList = list
        })
    }
  }
}
</script>

<style scoped lang="scss">
.pm-project-manage {
  width: 100%;

  .wk-page-header {
    padding: 24px 40px 0;
  }

  .favourite-wrapper {
    height: 200px;
    padding: 16px 40px 0;

    .title {
      font-size: $--font-size-large;
      font-weight: bold;
      color: $--color-black;
    }

    .container {
      height: 100%;
      margin: 8px -8px 0;
      overflow-x: auto;
      overflow-y: hidden;
      white-space: nowrap;
      user-select: none;

      /deep/ .empty-mask {
        .empty-content {
          top: 0;
        }
      }

      .favourite-list {
        overflow-x: auto;
        overflow-y: hidden;
        white-space: nowrap;

        .favourite-list-item {
          position: relative;
          flex: unset;
          flex-shrink: 0;
          width: calc(25% - 16px);
          padding: 16px 16px 4px;
          margin: 8px;
          cursor: pointer;
          background-color: white;
          border-radius: $--border-radius-base;
          box-shadow: 0 1px 4px 0 rgba(122, 134, 154, 0.5);

          .content {
            width: 100%;
            overflow: hidden;

            .icon-box {
              flex-shrink: 0;
              width: 50px;
              height: 50px;
              margin-right: 16px;
              background-color: $--color-n30;
              border-radius: $--border-radius-base;

              .wk {
                font-size: 32px;
                color: $--color-primary;
              }
            }

            .info {
              overflow: hidden;

              .name {
                padding-right: 20px;
                font-size: $--font-size-large;
                font-weight: bold;
                color: $--color-black;
              }

              .desc {
                margin-top: 6px;
                font-size: $--font-size-small;
                color: $--color-n200;
              }
            }
          }

          .chart {
            width: 120%;
            height: 36px;
            margin: 0 -10%;
          }

          .footer {
            .text {
              font-size: $--font-size-small;
              color: $--color-n200;
            }
          }

          .focus-btn {
            position: absolute;
            top: 16px;
            right: 16px;
            padding: 0;
            color: $--color-y300;
          }
        }
      }
    }
  }

  .table-container {
    padding: 0 40px 16px;
  }
}
</style>
