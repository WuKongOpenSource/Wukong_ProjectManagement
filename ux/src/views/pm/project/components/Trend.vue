<template>
  <project-card
    v-loading="loading">
    <template slot="title-left">
      <div class="title">
        <span class="name">工作项趋势</span>
        <i class="line" />
        <span class="mark">2周内已完成的工作项</span>
      </div>
    </template>
    <div
      class="chart-card content trend">
      <!-- <div class="title">工作项趋势</div> -->
      <!-- <span class="dec">2周内未完成的工作项</span> -->
      <div ref="chart" class="chart" />
    </div>
  </project-card>
</template>

<script>
import {
  getProjectByTimeAPI
} from '@/api/pm/projectTask'

import ProjectMixin from './ProjectMixin'

import * as echarts from 'echarts'
import xrTheme from '@/styles/xr-theme.scss'
import { objDeepCopy } from '@/utils'

export default {
  name: 'Trend', // Trend 概览趋势

  components: {},
  mixins: [ProjectMixin],
  props: {},

  data() {
    return {
      loading: false, // 加载
      charts: [],
      dataTime: [],
      dataNum: [],
      chartOption: {
        color: ['#0052CC'],
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            // 坐标轴指示器，坐标轴触发有效 默认为直线，可选为：'line' | 'shadow'
            type: 'shadow'
          },
          textStyle: {
            color: '#42526E' // 设置文字颜色
          }
        },
        legend: { // 图例
          orient: 'vertical',
          x: 'center',
          y: 'top',
          icon: 'roundRect',
          itemWidth: 16,
          itemHeight: 8,
          data: ['工作项数量'],
          textStyle: {
            color: '#42526E',
            fontWeight: 700
          }
        },
        grid: {
          top: '40px',
          left: '0',
          right: '0',
          bottom: '16px',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          boundaryGap: ['20%', '20%'],
          // data: ['10-01', '10-02', '10-03', '10-04', '10-05', '10-06', '10-01', '10-02', '10-03', '10-04', '10-05', '10-06', '10-01', '10-02', '10-03', '10-04', '10-05', '10-06', '10-05', '10-06'],
          axisTick: {
            alignWithLabel: true
          },
          axisLabel: {
            color: '#6B778C',
            fontWeight: xrTheme.axisLabelFontWeight,
            interval: 0,
            formatter: value => {
              return echarts.format.formatTime('MM-dd', new Date(value))
            }
          },
          axisLine: {
            lineStyle: {
              color: xrTheme.axisLineColor
            }
          }
        },
        yAxis: {
          type: 'value',
          min: 0,
          // max: 2,
          interval: 1,
          axisTick: {
            show: true
          },
          axisLine: {
            show: true,
            lineStyle: {
              color: xrTheme.axisLineColor
            }
          },
          axisLabel: {
            color: '#6B778C',
            fontWeight: xrTheme.axisLabelFontWeight
          },
          splitLine: {
            lineStyle: {
              color: xrTheme.axisLineColor
            }
          }
        },
        series: [
          {
            name: '工作项数量',
            type: 'line',
            smooth: true,
            // data: [1, 1, 1, 2, 3, 3, 3],
            areaStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                {
                  offset: 0,
                  color: 'rgba(193, 199, 208, 0.2)'
                }
              ])
            },
            itemStyle: {
              normal: {
                color: '#0052cc',
                lineStyle: {
                  width: 2,
                  color: '#0052cc' // 改变折线颜色
                }
              }
            }
          }

        ]
      }
    }
  },

  computed: {
    projectId() {
      return this.$route.params.id
    }
  },

  watch: {},

  created() {
    this.getTrendChart()
  },

  mounted() {},

  beforeDestroy() {},

  methods: {
    /**
     * @description: 获取趋势数据
     * @return {*}
     */
    getTrendChart() {
      getProjectByTimeAPI({ projectId: this.projectId })
        .then(res => {
          const timeList = []
          const numList = []
          res.data.projectTaskNumVOEnd.forEach(item => {
            timeList.push(item.updateTime)
            numList.push(item.num)
          })
          this.dataTime = timeList
          this.dataNum = numList
          this.initChart()
        }).catch(() => {})
    },

    /**
     * @description: 初始化echarts
     * @return {*}
     */
    initChart() {
      this.charts = []
      const elem = this.$refs.chart
      const chart = echarts.init(elem)
      const chartOptions = objDeepCopy(this.chartOption)
      chartOptions.xAxis.data = this.dataTime
      chartOptions.series[0].data = this.dataNum
      chart.setOption(chartOptions, true)
      this.charts.push(chart)
      window.addEventListener('resize', this.resizeCallback)
    },

    /**
     * @description: 更新大小
     * @return {*}
     */
    resizeCallback() {
      this.charts.forEach(chart => {
        chart.resize()
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.title {
  display: flex;
  align-items: center;

  .name {
    font-weight: 700;
  }
}

.line {
  display: inline-block;
  width: 1px;
  height: 16px;
  margin: 0 4px;
  background-color: $--border-color-base;
}

.mark {
  font-size: 14px;
  color: $--color-text-regular;
}

.content {
  position: relative;
  width: 100%;
  height: 330px;
  margin-top: 16px;
}

.chart-card {
  padding: 8px;

  .title {
    font-size: 16px;
    font-weight: bold;
  }

  .chart {
    width: 100%;
    height: calc(100% - 50px);
    padding-right: 8px;
  }
}

</style>
