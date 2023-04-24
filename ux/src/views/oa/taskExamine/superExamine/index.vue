<template>
  <div class="main">
    <flexbox class="main-header" justify="space-between">
      <div class="main-header__left">
        <span class="title">{{ getRemind() }}</span>
      </div>
    </flexbox>

    <flexbox class="main-header is-filter-header" justify="space-between">
      <div class="main-header__left">
        <span>本月审批（{{ formatTime }}）:</span>
        <span>审批次数(次) <span class="frequency">{{ headerData.examineNum }}</span></span>
        <span>审批时效(小时/次) <span class="frequency">{{ headerData.examineAvgNum }}</span></span>
        <el-button
          type="primary-text"
          @click="handlerSkip">查看更多></el-button>
      </div>
      <div class="main-header__right">
        <el-input
          v-model="search"
          placeholder="审批名称/审批编号"
          @change="handlerSearch">
          <el-button
            slot="suffix"
            type="icon"
            icon="wk wk-sousuo"
            @click="handlerSearch" />
        </el-input>
      </div>
    </flexbox>
    <div class="card-list">
      <div class="card-list-left">
        <application-card @refresh="refreshTrack" />
        <track-card ref="trackCard" class="track-card" />
      </div>
      <div class="card-list-right">
        <upcoming-card ref="upcomingCard" />
        <quick-entry-card class="quick-entry-card" />
      </div>
    </div>
  </div>
</template>

<script>
import {
  examineSuperExaminesGetExamineReportInfoAPI
} from '@/api/oa/superExamine'

import ApplicationCard from './components/ApplicationCard' // 申请
import UpcomingCard from './components/UpcomingCard' // 待办
import TrackCard from './components/TrackCard' // 跟踪
import QuickEntryCard from './components/QuickEntryCard' // 快捷入口

import { mapGetters } from 'vuex'
import moment from 'moment'
export default {
  name: 'Home',
  components: {
    ApplicationCard,
    UpcomingCard,
    TrackCard,
    QuickEntryCard
  },
  data() {
    return {
      search: '',
      headerData: {}
    }
  },
  computed: {
    ...mapGetters(['userInfo']),
    formatTime() {
      const startTime = moment(new Date()).startOf('month').format('MM.DD')
      const endTime = moment(new Date()).format('MM.DD')
      return startTime + '-' + endTime
    }
  },
  created() {
    this.getHeaderInfo()
  },
  methods: {
    /**
     * @description: 获取头部信息
     * @return {*}
     */
    getHeaderInfo() {
      examineSuperExaminesGetExamineReportInfoAPI({
        limit: 15,
        page: 1,
        pageType: 1,
        dataType: 2,
        dateFilter: 'month',
        searchStyle: 1
      })
        .then(res => {
          this.headerData = res.data || {}
        })
    },

    /**
     * @description: 刷新跟踪卡片
     * @return {*}
     */
    refreshTrack() {
      const trackCard = this.$refs.trackCard
      const upcomingCard = this.$refs.upcomingCard
      trackCard.getDetail()
      upcomingCard.getDetail()
    },

    /**
     * 欢迎语
     */
    getRemind() {
      const userName = this.userInfo.realname
      let time = ''

      const hour = moment().format('H')
      if (hour < 12) {
        time = '早上好, '
      } else if (hour < 18) {
        time = '下午好, '
      } else {
        time = '晚上好, '
      }

      return time + userName
    },

    /**
     * 搜索
     */
    handlerSearch() {
      if (!this.search) return
      const { module } = this.$route.query
      this.$router.push({
        path: '/oa/examine/search',
        query: {
          search: this.search,
          module
        }
      })
    },

    /**
     * @description: 查看更多
     * @return {*}
     */
    handlerSkip() {
      const { module } = this.$route.query
      this.$router.push({
        path: '/oa/examine/subs/report',
        query: {
          module
        }
      })
    }
  }
}
</script>

<style lang="scss" scoped>
@import "../style/index.scss";

.main {
  .main-header.is-filter-header {
    .main-header__left {
      span {
        &:nth-of-type(1) {
          color: $--color-text-secondary;
        }

        .frequency {
          margin-right: 10px;
          color: $--color-primary;
        }
      }
    }
  }

  .card-list {
    display: flex;
    align-items: stretch;

    &-left {
      width: calc(50% - 10px);

      // min-width: 780px;
      margin-right: 20px;

      .track-card {
        margin-top: 18px;
      }
    }

    &-right {
      width: calc(50% - 10px);

      // min-width: 780px;

      .quick-entry-card {
        margin-top: 18px;
      }
    }
  }
}
</style>
