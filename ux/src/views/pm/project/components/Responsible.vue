<template>
  <project-card
    v-loading="loading">
    <template slot="title-left">分配给我的</template>
    <el-button
      slot="title-right"
      type="primary-text"
      style="font-size: 14px;"
      @click="check">
      查看更多
    </el-button>
    <div
      v-empty="!list.length"
      class="content rc-cont">
      <div class="box">
        <item
          v-for="(cItem, cIndex) in list"
          :key="cIndex"
          :item="cItem"
          style="cursor: pointer;"
          @click.native="enterDetail(cItem, list)" />
      </div>
    </div>
    <!-- </div> -->
    <item-detail
      v-if="taskDetailShow"
      :id="taskId"
      ref="particulars"
      :detail-index="detailIndex"
      :no-listener-class="['el-table__body']"
      @update-list="handleUpdateList"
      @close="taskDetailShow = false" />
  </project-card>
</template>

<script>
import {
  workQueryItemListAPI
} from '@/api/pm/projectTask'

import Item from '@/views/pm/project/team/planning/components/Item'
import ItemDetail from '@/views/pm/project/team/itemDetail'

import ProjectMixin from './ProjectMixin'
import { mapGetters } from 'vuex'

export default {
  name: 'Responsible', // 项目概览-分配给我的
  components: {
    Item,
    ItemDetail
  },
  mixins: [ProjectMixin],
  props: {
    detail: Object
  },
  data() {
    return {
      loading: false, // 加载
      list: [],
      taskId: '',
      detailIndex: -1,
      taskDetailShow: false
    }
  },
  computed: {
    projectId() {
      return this.$route.params.id
    },
    ...mapGetters(['userInfo'])
  },
  watch: {},
  created() {
    this.getList()
  },
  methods: {
    /**
     * @description: 获取列表
     * @return {*}
     */
    getList() {
      const params = {
        type: this.detail.type == 1 ? 3 : '',
        pageType: 0,
        projectId: this.projectId,
        mainUserId: this.userInfo.userId
      }
      this.loading = true
      workQueryItemListAPI(params).then(res => {
        const list = res.data.list.filter(item => {
          return item.status !== 3
        })
        this.list = list
        this.loading = false
      }).catch(() => {
        this.loading = false
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
          type: 'All'
        }
      })
    },

    /**
     * @description: 进入详情
     * @param {*} item
     * @return {*}
     */
    enterDetail(item, list) {
      this.taskId = item.taskId
      this.detailIndex = this.getObjIndex(list, 'taskId', item.taskId)
      this.taskDetailShow = true
    },

    /**
     * @description: 获取对象在所在数组的索引
     * @param {*} array
     * @param {*} filed
     * @param {*} value
     * @return {*}
     */
    getObjIndex(array, filed, value) {
      for (let index = 0; index < array.length; index++) {
        const element = array[index]
        if (element[filed] === value) {
          return index
        }
      }
      return null
    },

    handleUpdateList() {
      this.getList()
    }
  }
}
</script>
<style scoped lang='scss'>
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
  }

  .item {
    cursor: pointer;

    &:hover {
      background-color: $--background-hover-color-base;
    }
  }
}
</style>
