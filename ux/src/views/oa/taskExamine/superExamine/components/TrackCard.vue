<template>
  <div v-loading="loading" class="container">
    <top-border-card
      :move="false">
      <template slot="title-left">
        跟踪
      </template>
      <template slot="title-right">
        <el-button
          type="primary-text"
          @click="handlerSkip('statistics/track')">查看更多></el-button>
      </template>
      <div class="main">
        <span
          type="primary-text"
          class="main-title"
          @click="handlerClick">{{ examineName }}</span>
        <div class="step-container">
          <div
            v-if="approvalProcess.length"
            :style="approvalProcess.length == 2 ? 'width: 55%; margin: 0 auto' : ''"
            class="step-main">
            <el-steps>
              <el-step
                v-for="(item,index) in approvalProcess"
                :key="index">
                <template slot="icon">
                  <i
                    v-if="index === 0"
                    class="wk wk-icon-success" />
                  <i
                    v-if="index === 1 && item.userList[0].stageName != 'null'"
                    class="wk wk-icon-check-pending" />
                  <i
                    v-if="index === 1 && item.userList[0].stageName == 'null'"
                    style="color: #36b37e;"
                    class="wk wk-icon-success" />
                  <i
                    v-if="index === 2"
                    class="wk wk-icon-in-review" />
                </template>
                <template slot="title">
                  <div style="width: 150px;">
                    <text-tooltip
                      :class="index == 1 ? 'title-active' : ''"
                      :content="item.userList[0].stageName == 'null' ? '自动通过' : item.name ? item.name : item.userList[0].stageName"
                      :ref-name="'tooltipOver' + index" />
                  </div>
                  <span>{{ item.type != 1 && item.userList[0].stageName != 'null' && item.userList.length > 1 ? typeObj[item.type] : '' }}</span>
                </template>
              </el-step>
            </el-steps>
          </div>
          <div
            v-else
            class="empty-style">
            <p>您还没有跟踪信息~</p>
            <el-button
              type="primary"
              @click="handlerSkip('application')">去添加</el-button>
          </div>
        </div>
      </div>
    </top-border-card>

    <detail
      v-if="detailShow"
      :id="examineId"
      @hide-view="detailShow = false"
      @success="getDetail()"
    />
  </div>
</template>

<script>
import { superExaminesTraceInfoAPI } from '@/api/oa/superExamine'

import TopBorderCard from './TopBorderCard'
import TextTooltip from './TextTooltip'
import Detail from '../Detail.vue'

import moment from 'moment'

export default {
  name: 'TrackCard', // 跟踪卡片
  components: {
    TopBorderCard,
    TextTooltip,
    Detail
  },
  data() {
    return {
      loading: false,
      approvalProcess: [],
      examineName: '',
      typeObj: {
        1: '依次审批',
        2: '会签',
        3: '或签'
      },
      detailShow: false,
      examinId: ''

    }
  },
  created() {
    this.getDetail()
  },
  methods: {
    getDetail() {
      this.loading = true
      superExaminesTraceInfoAPI()
        .then(res => {
          const data = res.data.superExamineTraceInfoVO || {}
          this.examinId = data.oaExamineId
          this.examineName = data.content
          this.approvalProcess = data.allStepHandleUserList || []
          this.approvalProcess.forEach(item => {
            if (item.userList.length > 2 && (item.type == 2 || item.type == 3)) {
              item.name = item.userList.slice(0, 2).map(item => item.stageName).join('、') + `等${item.userList.length - 2}人`
            } else if (item.userList.length == 2 && (item.type == 2 || item.type == 3)) {
              item.name = item.userList.slice(0, 2).map(item => item.stageName).join('、')
            }
            item.userList.forEach(sItem => {
              if (sItem.time != 'null') {
                sItem.time = moment(sItem.time).format('MM-DD')
              }
            })
          })
          this.loading = false
        })
        .catch(() => {
          this.loading = false
        })
    },

    /**
     * @description:点击查看详情
     * @param {*}
     * @return {*}
     */
    handlerClick() {
      this.examineId = this.examinId
      this.detailShow = true
    },

    /**
     * @description: 查看更多/添加
     * @return {*}
     */
    handlerSkip(path) {
      const { module } = this.$route.query
      this.$router.push({
        path: '/oa/examine/subs/' + path,
        query: {
          module
        }
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.container {
  .main {
    margin-top: 16px;

    .main-title {
      color: $--color-b500;
      cursor: pointer;

      &:hover {
        border-bottom: 1px solid $--color-b500;
      }
    }

    .step-container {
      // overflow-x: auto;
      // overflow-y: hidden;
      display: flex;
      align-items: center;
      justify-content: space-between;
      overflow: hidden;

      .prev-btn,
      .next-btn {
        width: 5%;
      }

      .step-main {
        width: 100%;
        overflow: hidden;

        // 参与人
        .send-user {
          position: relative;
          display: inline-block;

          &.is-close {
            border: 3px solid #fff;
            border-radius: 50%;
          }
        }

        .is-close + .is-close {
          margin-left: -13px !important;
        }

        .step-title-main {
          display: flex;
          flex-direction: column;
          align-items: center;
          justify-content: space-between;
          height: 70px;

          .step-title {
            display: flex;
            flex-direction: column;
            align-items: center;
            justify-content: space-between;
            height: 100%;

            span {
              line-height: 20px;
              color: $--color-n200;

              &.step-active {
                color: $--color-n800;
              }
            }

            .el-tag {
              width: 60px;
              margin-top: 10px;
              animation-play-state: paused !important;
            }
          }
        }
      }

      .empty-style {
        padding: 10px 0;
        margin: 0 auto;
        text-align: center;

        p {
          color: $--color-text-secondary;
        }

        .el-button {
          margin-top: 20px;
        }
      }
    }

    /deep/ .el-steps {
      padding: 10px 90px;
      margin-top: 20px;

      .el-step {
        flex-shrink: 0;

        &:nth-of-type(1) {
          .el-step__icon {
            i {
              color: $--color-g300;
            }
          }
        }

        &:nth-of-type(2) {
          .el-step__icon {
            i {
              color: $--color-y300;
            }
          }
        }
      }

      .el-step__head.is-process {
        color: $--color-n200;
        border-color: $--border-color-base;
      }

      .el-step__head.is-success {
        color: $--color-primary;
        border-color: $--color-primary;
      }

      .el-step.is-horizontal {
        span {
          white-space: nowrap;
        }

        .el-step__main {
          transform: translate(-4px);
        }
      }

      .el-step__title {
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        margin-left: 10px;
        font-size: 14px;
        text-align: center;
        transform: translateX(-50%);

        .title-active {
          font-size: 16px;
          color: $--color-n800;
        }
      }

      .el-step__title.is-process {
        font-weight: normal;
        color: $--color-text-primary;
      }

      .el-step__title.is-finish,
      .el-step__title.is-success {
        color: $--color-text-primary;
      }

      .el-step__line {
        top: 4px;
        right: 30px;
        left: 30px;
        height: 2px;
        overflow: hidden;
        background-color: #ebecf0;
      }

      .el-step__icon {
        width: 10px;
        height: 10px;

        i {
          font-size: 24px;
        }
      }

      .el-step__icon.is-text {
        border: unset;
        border-color: unset;
        border-radius: unset;
      }

      div[class="el-step__icon-inner"] {
        display: none;
      }
    }
  }
}
</style>
