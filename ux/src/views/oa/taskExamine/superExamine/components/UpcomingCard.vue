<template>
  <div v-loading="loading" class="container">
    <top-border-card
      :move="false">
      <template slot="title-left">
        待办
        <el-badge
          v-if="data.num"
          :value="data.num" />
      </template>
      <template slot="title-right">
        <el-button
          type="primary-text"
          @click="quickEntry">查看更多></el-button>
      </template>
      <div class="main">
        <div
          v-for="(item,index) in data.list"
          :key="index"
          class="main-item"
          @click="checkExamine(item)">
          <div class="main-item-title">
            <div class="circle" />
            <span>{{ item.content }}</span>
          </div>
          <span class="main-item-time">{{ item.stayTime }}</span>
        </div>
      </div>
      <el-empty
        v-if="data.list && !data.list.length"
        :image-size="240"
        :image="require('@/assets/img/empty/upcoming.png')"
        description="暂无待办" />
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
import { superExaminesBackLogAPI } from '@/api/oa/superExamine'

import TopBorderCard from './TopBorderCard'
import Detail from '../Detail.vue'
export default {
  name: 'UpcomingCard', // 待办卡片
  components: {
    TopBorderCard,
    Detail
  },
  data() {
    return {
      loading: false,
      data: {},
      detailShow: false,

      examineId: ''
    }
  },
  created() {
    this.getDetail()
  },
  methods: {
    getDetail() {
      this.loading = true
      superExaminesBackLogAPI()
        .then(res => {
          const data = res.data || {}
          this.data.num = data.num || 0
          this.data.list = data.superExamineBackLogInfoList || []
          this.loading = false
        })
        .catch(() => {
          this.loading = false
        })
    },

    /**
     * @description: 查看详情
     * @param {*} data
     * @return {*}
     */
    checkExamine(data) {
      this.examineId = data.examineId
      this.detailShow = true
    },

    quickEntry() {
      const { module } = this.$route.query
      this.$router.push({
        path: '/oa/examine/subs/statistics/upcoming',
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
  /deep/ .el-badge {
    .el-badge__content {
      color: $--color-text-primary;
      background-color: $--color-n40;
    }
  }

  .statistics-card {
    height: 410px;
    overflow: hidden;

    /deep/ .el-badge {
      margin-left: 5px;

      .el-badge__content {
        top: unset;
      }
    }

    .main {
      margin-top: 10px;

      &-item {
        display: flex;
        flex-direction: column;
        padding: 9px 0;
        padding-left: 4px;
        cursor: pointer;
        border-bottom: 1px solid $--border-color-base;

        &:hover {
          background-color: $--background-hover-color-base;
        }

        &-title {
          display: flex;
          align-items: center;

          .circle {
            width: 12px;
            height: 12px;
            margin-right: 8px;
            background-color: $--color-y300;
            border-radius: 50%;
          }
        }

        &-time {
          margin-top: 8px;
          margin-left: 20px;
          color: $--color-n100;
        }
      }
    }
  }
}
</style>
