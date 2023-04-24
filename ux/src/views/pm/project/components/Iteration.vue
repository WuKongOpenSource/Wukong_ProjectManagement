<template>
  <project-card
    v-loading="loading">
    <template slot="title-left">
      <div class="title">
        <span class="name">项目迭代</span>
        <i class="line" />
        <span class="mark">共{{ iterationList.length || '0' }}个迭代内容</span>
      </div>
    </template>
    <el-button
      slot="title-right"
      type="primary-text"
      style="font-size: 14px;"
      @click="check">
      查看更多
    </el-button>
    <div
      v-empty="!iterationList.length"
      class="content rc-cont">
      <div class="box">
        <div
          v-for="(item, index) in iterationList"
          :key="index"
          class="content-item"
          @click="enterIteration(item)">
          <h2 class="title">{{ item.name }}</h2>
          <div class="item-content">
            <p>{{ item.startTime|filterTimestampToFormatTime('YYYY-MM-DD') }}<span>{{ item.startTime && item.stopTime ? '至' : '--' }}</span>{{ item.stopTime|filterTimestampToFormatTime('YYYY-MM-DD') }}</p>
            <div class="tag">
              <el-tag disable-transitions type="info">{{ item.list ? item.list.length : '0' }}个事项</el-tag>
              <status-tag class="tag-content" :type="item.status" />
              <xr-avatar
                :id="item.mainUserId"
                trigger="hover"
                class="tag-content"
                :src="item.mainUserImg"
                :name="item.mainUserName"
                :size="24" />
            </div>
          </div>
        </div>
      </div>
    </div>
  </project-card>
</template>

<script>
import {
  workQueryIterationAllItemAPI,
  workQueryIterationsItemListAPI
} from '@/api/pm/projectTask'

import StatusTag from '@/views/pm/project/components/StatusTag'

import ProjectMixin from './ProjectMixin'

export default {
  name: 'Iteration', // 项目概况-迭代组件
  components: {
    StatusTag
  },
  mixins: [ProjectMixin],
  props: {
    detail: Object
  },
  data() {
    return {
      loading: false, // 加载
      num: 10,
      list: [
        { title: '09月份CRM升级内容', time: '2022-08-15至2022-09-15', matter: '3', status: '进行中' },
        { title: '2022商业汇演方案2', time: '2022-08-15至2022-09-15', matter: '3', status: '未开始' },
        { title: '09月份CRM升级内容', time: '2022-08-15至2022-09-15', matter: '3', status: '进行中' },
        { title: '2022商业汇演方案2', time: '2022-08-15至2022-09-15', matter: '3', status: '未开始' }
      ],
      iterationList: []
    }
  },
  // inject: ['rootTabs'],
  computed: {
    projectId() {
      return this.$route.params.id
    }
  },
  watch: {
    // 'rootTabs.currentName'(val) {
    //   if (val === this.itemType) {
    //     console.log('1111')
    //     this.getIterationsList()
    //   }
    // }
  },
  created() {
    this.getIterationsList()
  },
  mounted() {

  },
  methods: {
    /**
     * @description: 迭代列表
     * @return {*}
     */
    getIterationsList() {
      const params = {
        type: 0,
        pageType: 0,
        projectId: this.projectId,
        name: ''
      }
      workQueryIterationsItemListAPI(params).then(res => {
        const list = res.data.list?.filter(item => {
          return item.status !== 3
        })
        this.iterationList = (list || []).map((item, index) => {
          // if (index == 0) {
          this.getAllItem(item.taskId, index)
          //   item.collapse = true
          // } else {
          //   item.collapse = false
          // }
          // item.list = []
          return item
        })
      }).catch(() => {

      })
    },

    /**
     * @description: 获取迭代下所有事项
     * @param {*} id
     * @param {*} index
     * @return {*}
     */
    getAllItem(id, index) {
      const params = {
        belongIterationId: id,
        pageType: 0
      }
      workQueryIterationAllItemAPI(params).then(res => {
        this.$set(this.iterationList[index], 'list', res.data.list || [])
      }).catch(() => {

      })
    },

    /**
     * @description: 进入迭代详情
     * @param {*} item 迭代信息
     * @return {*}
     */
    enterIteration(item) {
      this.$router.push({
        name: 'projectTeam',
        params: {
          id: this.projectId
        },
        query: {
          type: 'iteration-detail',
          taskId: item.taskId
        }
      })
    },

    /**
     * @description: 查看更多
     * @return {*}
     */
    check() {
      this.$router.push({
        name: 'projectTeam',
        params: {
          id: this.projectId
        },
        query: {
          type: 'Iterations'
        }
      })
    }
  }
}
</script>
<style scoped lang='scss'>
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
  overflow: hidden;
  overflow-y: auto;

  /deep/ .empty-mask {
    .empty-content {
      top: 25%;
    }
  }

  .box {
    border: 1px solid $--border-color-base;
    border-radius: 3px;

    .content-item {
      padding: 12px 16px;
      cursor: pointer;

      &:hover {
        background-color: $--background-hover-color-base;
      }

      .title {
        font-size: 16px;
      }

      .item-content {
        display: flex;
        align-items: center;
        justify-content: space-between;

        p {
          color: $--color-text-secondary;
        }

        .tag {
          display: flex;
          align-items: center;

          .tag-content {
            margin-left: 8px;
          }
        }
      }
    }
  }

  .content-item + .content-item {
    border-top: $--border-base;
  }
}
</style>
