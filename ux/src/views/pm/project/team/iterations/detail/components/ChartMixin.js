import * as echarts from 'echarts'
import xrTheme from '@/styles/xr-theme.scss'
import { objDeepCopy } from '@/utils'

export default {
  components: {
  },
  props: {
  },
  data() {
    return {
      charts: [],
      // investmentsWayData: [],
      // investmentsWayDataCount: [],
      // investmentsWayDataCounts: [],
      trendOption: {
        // title: {
        //   text: '折线图堆叠'
        // },
        tooltip: {
          trigger: 'axis'
        },
        legend: { // 图例
          orient: 'vertical',
          align: 'left',
          x: 'right',
          y: 'top',
          icon: 'roundRect',
          itemWidth: 12,
          itemHeight: 8,
          data: ['已完成', '进行中', '未开始']
        },
        grid: {
          top: '8px',
          left: '0',
          right: '80px',
          bottom: '24px',
          containLabel: true
        },
        // toolbox: {
        //   feature: {
        //     saveAsImage: {}
        //   }
        // },
        xAxis: {
          type: 'category',
          boundaryGap: ['20%', '20%'],
          axisTick: {
            alignWithLabel: true
          },
          axisLabel: {
            color: xrTheme.colorBlack,
            fontWeight: xrTheme.axisLabelFontWeight,
            minInterval: 1,
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
          minInterval: 1
        },
        series: [
          {
            name: '已完成',
            type: 'line',
            // stack: 'total',
            data: [0, 2, 3, 4, 2, 1, 2],
            itemStyle: {
              color: '#36B37E'
            }
          },
          {
            name: '进行中',
            type: 'line',
            // stack: 'total',
            data: [2, 2, 3, 3, 2, 4, 2],
            itemStyle: {
              color: '#FFAB00'
            }
          },
          {
            name: '未开始',
            type: 'line',
            // stack: 'total',
            data: [0, 0, 1, 3, 5, 4, 2],
            itemStyle: {
              color: '#0065FF'
            }
          }
        ]
      },
      chartOption: { // 折线图
        color: ['#0052CC'],
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            // 坐标轴指示器，坐标轴触发有效 默认为直线，可选为：'line' | 'shadow'
            type: 'shadow'
          }
        },
        legend: { // 图例
          orient: 'vertical',
          align: 'left',
          x: 'right',
          y: 'top',
          icon: 'roundRect',
          itemWidth: 12,
          itemHeight: 3,
          data: ['理想剩余', '实际剩余']
        },
        grid: {
          top: '8px',
          left: '0',
          right: '80px',
          bottom: '24px',
          containLabel: true
        },
        xAxis: {
          type: 'category',
          boundaryGap: ['20%', '20%'],
          axisTick: {
            alignWithLabel: true
          },
          axisLabel: {
            color: xrTheme.colorBlack,
            fontWeight: xrTheme.axisLabelFontWeight,
            minInterval: 2,
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
          // min: 0,
          // max: 30,
          minInterval: 5,
          axisTick: {
            show: false
          },
          axisLine: {
            lineStyle: { width: 0 }
          },
          axisLabel: {
            color: xrTheme.colorBlack,
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
            name: '理想剩余',
            data: [],
            // smooth: true,
            type: 'line',
            areaStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                {
                  offset: 0,
                  color: 'rgba(0, 82, 204, 0.2)'
                }
              ])
            },
            itemStyle: {
              normal: {
                color: '#C1C7D0',
                lineStyle: {
                  width: 1,
                  color: '#C1C7D0' // 改变折线颜色
                }
              }
            }
          },
          {
            name: '实际剩余',
            type: 'line',
            // smooth: true,
            data: [],
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
                color: '#0065FF',
                lineStyle: {
                  width: 1,
                  color: '#0065FF' // 改变折线颜色
                }
              }
            }
          }

        ]
      },
      barChartOption: { // 横向柱状图
        tooltip: {
          show: true,
          trigger: 'axis',
          axisPointer: {
            type: 'shadow'
          }
        },
        legend: { // 图例
          orient: 'horizontal',
          x: 'center',
          y: 'bottom',
          icon: 'roundRect',
          itemWidth: 12,
          itemHeight: 8,
          data: ['已完成', '进行中', '未开始']
        },
        grid: {
          top: '8px',
          left: '0',
          right: '80px',
          bottom: '32px',
          containLabel: true
        },
        xAxis: {
          type: 'value'
        },
        yAxis: [
          {
            type: 'category',
            axisLabel: {
              color: '#6B778C'
            },
            axisLine: {
              show: true,
              lineStyle: {
                color: '#DFE1E6',
                type: 'solid'
              }
            },
            data: ['需求', '任务', '缺陷']
          }
        ],
        series: [
          {
            name: '已完成',
            type: 'bar',
            barWidth: 5,
            stack: 'total',
            emphasis: {
              focus: 'series'
            },
            data: [1, 2, 1, 2],
            itemStyle: {
              color: '#36B37E'
            }
          },
          {
            name: '进行中',
            type: 'bar',
            barWidth: 5,
            stack: 'total',
            emphasis: {
              focus: 'series'
            },
            data: [2, 1, 2, 1],
            itemStyle: {
              color: '#FFAB00'
            }
          },
          {
            name: '未开始',
            type: 'bar',
            barWidth: 5,
            stack: 'total',
            // emphasis: {
            //   focus: 'series'
            // },
            data: [1, 1, 2, 2],
            itemStyle: {
              color: '#0065FF'
            }
          }
        ]
      }
    }
  },
  watch: {
  },
  created() {
  },
  mounted() {
  },
  beforeDestroy() {
  },
  methods: {
    /**
     * @description: 初始化echarts
     * @return {*}
     */
    initChart() {
      this.charts = []
      if (this.taskBurnout) {
        const BurnoutxAxisData = []
        const BurnoutseriesData1 = []
        const BurnoutseriesData2 = []
        this.taskBurnout.forEach(item => {
          // xAxisData.push(moment(item.updateTime).format('MM-DD'))
          BurnoutxAxisData.push(item.updateTime)
          BurnoutseriesData1.push(item.forecastTime || 0)
          BurnoutseriesData2.push(item.surplus || 0)
        })
        const elem = this.$refs.chart
        const chart = echarts.init(elem)
        const chartOptions = objDeepCopy(this.chartOption)
        // 燃尽图的x轴/数据
        chartOptions.xAxis.data = BurnoutxAxisData
        chartOptions.series[0].data = [[0, BurnoutseriesData1[0]], [BurnoutxAxisData.length - 1, 0]]
        chartOptions.series[1].data = BurnoutseriesData2
        chart.setOption(chartOptions, true)
        this.charts.push(chart)
      }

      if (this.taskTrend.projectTaskNumVOEnd) {
        const trendElem = this.$refs.trendChart
        const trendChart = echarts.init(trendElem)
        const trendOptions = objDeepCopy(this.trendOption)
        // 事项状态趋势图的数据
        const trendxAxisData = []
        const trendseriesData1 = []
        const trendseriesData2 = []
        const trendseriesData3 = []
        this.taskTrend.projectTaskNumVOEnd.forEach(item => {
          trendxAxisData.push(item.updateTime)
          trendseriesData1.push(item.num)
        })
        this.taskTrend.projectTaskNumVOsRun.forEach(item => {
          trendseriesData2.push(item.num)
        })
        this.taskTrend.projectTaskNumVOsNoStart.forEach(item => {
          trendseriesData3.push(item.num)
        })
        // trendOptions.xAxis.data = ['2022-10-01', '2022-10-02', '2022-10-03', '2022-10-01', '2022-10-02', '2022-10-03', '2022-10-01', '2022-10-02', '2022-10-03']
        trendOptions.xAxis.data = trendxAxisData
        trendOptions.series[0].data = trendseriesData1
        trendOptions.series[1].data = trendseriesData2
        trendOptions.series[2].data = trendseriesData3
        trendChart.setOption(trendOptions, true)
        this.charts.push(trendChart)
      }

      const barElem = this.$refs.barChart
      const barChart = echarts.init(barElem)
      const barChartOptions = objDeepCopy(this.barChartOption)
      // 事项分布图的数据
      const data = this.taskEvent
      const taskEcentDataArr = [
        [data.demandEndNum || 0, data.taskEndNum || 0, data.defectsEndNum || 0],
        [data.demandRunNum || 0, data.taskRunNum || 0, data.defectsRunNum || 0],
        [data.demandNoStartNum || 0, data.taskNoStartNum || 0, data.defectsNoStartNum || 0]
      ]
      barChartOptions.series.forEach((item, index) => {
        item.data = taskEcentDataArr[index]
      })
      barChart.setOption(barChartOptions, true)
      this.charts.push(barChart)

      window.addEventListener('resize', this.resizeCallback)
    },
    // initBarChart() {
    //   const barElem = this.$refs.barChart
    //   const barChart = echarts.init(barElem)
    //   const barChartOptions = objDeepCopy(this.barChartOption)
    //   barChart.setOption(barChartOptions, true)
    // },
    resizeCallback() {
      this.charts.forEach(chart => {
        chart.resize()
      })
    }
  }
}
