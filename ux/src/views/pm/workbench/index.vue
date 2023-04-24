<template>
  <div v-loading="loading" class="pm-workbench">
    <!-- <flexbox class="crm-workbench__hd">
      <div class="title">项目协同</div>
    </flexbox> -->
    <div class="pm-workbench-main">
      <el-tabs
        v-model="activeName"
        nav-mode="more"
        @tab-click="tabClick">
        <el-tab-pane
          v-for="(item, index) in tabList"
          :key="index"
          lazy
          :label="item.label"
          :name="item.name"
          class="tab-item">
          <template slot="label">
            <el-badge
              :value="tabNum[item.name]"
              :hidden="tabNum[item.name] <= 0"
              type="primary">
              <span>{{ item.label }}</span>
            </el-badge>
          </template>
          <!-- <component
            :is="item.name"
            ref="list"
            :project-detail="projectDetail" /> -->
          <list-view
            ref="list"
            :item-type="item.name"
            :project-id="layout"
            :project-detail="projectDetail" />
        </el-tab-pane>
      </el-tabs>
      <div class="select-project">
        <!-- <el-button
          type="subtle"
          class="filter-button"
          icon="wk wk-screening"
          @click="filterShow=true">
          高级筛选
        </el-button> -->
        <el-select
          v-model="layout"
          style="margin-left: 12px;"
          @change="selectChanged">
          <el-option-group>
            <el-option
              v-for="item in showList"
              :key="item.value"
              :label="item.label"
              :value="item.value">
              <i :class="item.icon" style="margin-right: 5px;" />{{ item.label }}
            </el-option>
          </el-option-group>
          <!-- <el-option-group
            v-if="showBoardSet">
            <el-option
              v-for="item in boardSetList"
              :key="item.value"
              :label="item.label"
              :value="item.value">
              <i :class="item.icon" style="margin-right: 5px;" />{{ item.label }}
            </el-option>
          </el-option-group> -->
        </el-select>
      </div>
    </div>
  </div>
</template>

<script>
import { workbenchItemNumAPI } from '@/api/pm/projectTask'
import { projectParticipateListAPI } from '@/api/pm/manage'

import ListView from './components/List'

export default {
  name: 'WorkbenchIndex',
  components: {
    ListView
  },
  data() {
    return {
      loading: false,
      projectDetail: {
        type: 3
      }, // 项目详情
      activeName: 'All',

      layout: '1',
      showList: [
        { label: '全部项目', value: '1' }
      ],

      tabNum: {
        All: 0,
        Require: 0,
        Task: 0,
        Defects: 0,
        Iterations: 0
      }
    }
  },
  computed: {
    tabList() {
      return [
        { label: '全部事项', name: 'All' },
        { label: '需求', name: 'Require' },
        { label: '任务', name: 'Task' },
        { label: '缺陷', name: 'Defects' },
        { label: '迭代', name: 'Iterations' }
      ]
    }
  },
  watch: {
  },
  created() {
    const { tab } = this.$route.query
    if (tab) {
      this.tabClick({ name: tab })
    }

    this.$store.commit('SET_PM_AUTH', null)
    this.getTabNum()
    this.getProjectShowList()

    this.$bus.on('update-workbench-num', () => {
      this.getTabNum()
    })
  },
  beforeDestroy() {
    this.$bus.off('update-workbench-num')
  },
  methods: {
    /**
     * @description: 获取tab num
     * @return {*}
     */
    getTabNum() {
      workbenchItemNumAPI().then(res => {
        const { all, need, task, defect, iteration } = res.data
        this.tabNum.All = all
        this.tabNum.Require = need
        this.tabNum.Task = task
        this.tabNum.Defects = defect
        this.tabNum.Iterations = iteration
      }).catch(() => {

      })
    },

    /**
     * @description: 获取我参与的项目列表
     * @return {*}
     */
    getProjectShowList() {
      projectParticipateListAPI({
        pageType: 0
      }).then(res => {
        const list = res.data.list.map(item => {
          return {
            label: item.name,
            value: item.projectId
          }
        })
        this.showList.push(...list)
      })
    },

    /**
     * @description: 选择框处理方法
     * @param {*} value
     * @return {*}
     */
    selectChanged(val) {
      // this.$refs.list[0].getList()
    },

    tabClick({ name }) {
      this.$router.push({
        query: {
          tab: name
        }
      })

      this.activeName = name
    }
  }
}
</script>

<style scoped lang="scss">
@import "@/views/layout/components/style.scss";

// .crm-workbench__hd .title {
//   flex: 1;
//   margin-bottom: 16px;
//   font-size: 24px;
// }

.pm-workbench {
  position: relative;
  width: 100%;
  height: 100%;
  padding: 24px 40px 0;

  &-main {
    .el-tabs {
      height: 100%;
    }
  }

  .pm-workbench-main {
    position: relative;

    .tab-item {
      position: relative;
      height: 100%;
    }

    /deep/ .el-tabs {
      .el-tabs__header {
        margin: 0 0 16px;
      }

      .el-tabs__item {
        padding: 0 20px;

        .el-badge {
          .el-badge__content {
            top: 50%;
            right: -4px;
          }
        }
      }

      .el-tabs__item.is-top:nth-child(2) {
        padding-left: 0;
      }

      .is-active {
        font-size: 16px;
        font-weight: 700;
        color: $--color-primary;
      }

      .el-tabs__content {
        height: calc(100% - 56px);

        .el-tab-pane {
          overflow: hidden;
        }
      }
    }

    .select-project {
      position: absolute;
      top: 4px;
      right: 0;
    }
  }
}
</style>

