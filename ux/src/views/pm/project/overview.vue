<template>
  <div v-loading="loading" class="pm-project">
    <div v-if="defaultVal == 'main'">
      <flexbox class="crm-workbench__hd">
        <div class="title">项目概况</div>
      </flexbox>
      <div v-if="projectDetail" class="project__body">
        <flexbox
          class="section"
          align="stretch">
          <!-- 左侧card -->
          <draggable
            v-model="sortLeft"
            class="left"
            :group="{ name: 'sort'}"
            :options="{ forceFallback: false, handle: '.filter-handle' }">
            <template v-for="(item, index) in sortLeft">
              <div v-if="item.isHidden === 1" :key="index" style="display: none;" />
              <component
                :is="item.component"
                v-else
                :key="index"
                :detail="projectDetail"
                class="left-content component-item"
              />
            </template>
          </draggable>
          <!-- 右侧card -->
          <draggable
            v-model="sortRight"
            class="right"
            :group="{ name: 'sort'}"
            :options="{ forceFallback: false, handle: '.filter-handle' }">
            <template v-for="(item, index) in sortRight">
              <div v-if="item.isHidden === 1" :key="index" style="display: none;" />
              <component
                :is="item.component"
                v-else
                :key="index"
                :detail="projectDetail"
                class="right-content component-item"
                @change="getChangeInfo"
              />
            </template>
          </draggable>
        </flexbox>
      </div>
    </div>
    <iteration-detail
      v-else-if="defaultVal"
      :iteration-id="iterationDetail.taskId"
      @change="getChangeInfo"
    />
  </div>
</template>

<script>
import { projectQueryByIdAPI } from '@/api/pm/manage'

import Announcement from './components/Announcement' // 项目公告
import Iteration from './components/Iteration' // 项目迭代
import Information from './components/Information' // 项目基本信息
import Responsible from './components/Responsible' // 分配给我的
import Trend from './components/Trend' // 普通项目趋势
import IterationDetail from '@/views/pm/project/team/iterations/detail'

import draggable from 'vuedraggable'
import { mapGetters } from 'vuex'

export default {
  name: 'ProjectOverviewIndex', // 项目概况
  components: {
    Announcement,
    draggable,
    Information,
    Responsible,
    Trend,
    IterationDetail
  },
  data() {
    return {
      loading: false,
      projectDetail: null, // 项目详情
      sortLeft: [], // 左侧
      sortRight: [], // 右侧
      iterationDetail: '',
      defaultVal: 'main' // 控制迭代详情展示
    }
  },
  computed: {
    projectId() {
      return this.$route.params.id
    },
    ...mapGetters(['projectAuth'])
  },
  watch: {
    projectAuth: {
      handler(val) {
        if (val.loaded) {
          if (!this.$auth('projectDescription.viewDescription', 'PM')) {
            this.$router.replace({
              name: 'participate'
            })
          }
        }
      },
      immediate: true,
      deep: true
    }
  },
  created() {
    this.getDetail()
  },
  methods: {
    /**
     * @description: 获取项目详情
     * @return {*}
     */
    getDetail() {
      this.loading = true
      projectQueryByIdAPI({ projectId: this.projectId })
        .then(res => {
          this.projectDetail = res.data || {}
          this.getModelSort()
          this.loading = false
        }).catch(() => {
          this.loading = false
        })
    },

    /**
     * @description: 排序/区分普通与敏捷
     * @return {*}
     */
    getModelSort() {
      // 1 普通项目 2 敏捷项目
      if (this.projectDetail.type === 1) {
        this.sortLeft = [
          { component: Announcement },
          { component: Information }
        ]
        this.sortRight = [
          { component: Trend },
          { component: Responsible }
        ]
      } else {
        this.sortLeft = [
          { component: Announcement },
          { component: Information }
        ]
        this.sortRight = [
          { component: Iteration },
          { component: Responsible }
        ]
      }
    },

    /**
     * @description: 迭代详情展示
     * @param {*} val
     * @return {*}
     */
    getChangeInfo(val) {
      this.defaultVal = val.type
      if (val.type == 'iteration-detail') {
        this.iterationDetail = val.row
      }
    }
  }
}
</script>

<style scoped lang="scss">
.pm-project {
  width: 100%;
  padding: 24px 40px 0;

  .crm-workbench__hd .title {
    flex: 1;
    font-size: 24px;
  }

  .section {
    padding-bottom: 40px;
    margin-top: 18px;

    .left {
      width: calc(50% - 10px);
      margin-right: 20px;

      &-content {
        height: 410px;
      }
    }

    .right {
      width: calc(50% - 10px);

      &-content {
        height: 410px;
      }
    }

    .left-content + .left-content,
    .right-content + .right-content {
      margin-top: 18px;
    }
  }
}
</style>
