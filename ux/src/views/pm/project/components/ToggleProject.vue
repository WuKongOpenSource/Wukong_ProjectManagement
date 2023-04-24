<template>
  <div class="switch-p">
    <el-popover
      v-model="visible"
      placement="bottom"
      width="320"
      trigger="click">
      <div v-loading="loading" class="toggle-body" element-loading-spinner="el-icon-loading">
        <el-input
          v-model="searchInput"
          class="input-wrap"
          maxlength="8"
          placeholder="搜索项目">
          <el-button
            slot="suffix"
            type="icon"
            icon="wk wk-sousuo" />
        </el-input>
        <div class="p-list">
          <div v-for="(item,index) in list" :key="index" class="list-block">
            <div class="list-title">{{ item.title }}</div>
            <div class="sub-list">
              <div
                v-for="(sitem,sindex) in item.list"
                :key="sindex"
                class="sub-wrap"
                :class="{'isactive': showCurrentProject(sitem)}"
                @click="handleRowClick(sitem)">
                <div class="icon-wrap">
                  <i :class="sitem.icon" class="icon" />
                </div>
                <div class="sub-title">{{ sitem.name }}</div>
              </div>
              <div
                v-if="!item.list.length"
                v-empty="item.list"
                class="empty"
                xs-empty-text="暂无数据" />
            </div>
          </div>
        </div>
        <div class="line" />
        <div class="look-all" @click="enterAll">
          <div>
            <i class="wk wk-file" />
            查看全部项目
          </div>
          <i class="el-icon-arrow-right" />
        </div>

        <!-- <div class="add-btn">
          <el-button class="btn" type="text" @click="createClick">
            <i class="wk wk-l-plus" />
            新建项目
          </el-button>
        </div> -->
      </div>
      <div slot="reference" class="project-select">
        <span class="selected-project">{{ detail ? detail.name : '' }}</span>
        <i class="wk wk-icon-caret-bottom el-icon--right" />
      </div>

    </el-popover>
  </div>
</template>

<script>
import {
  projectCollectListAPI,
  projectQueryMyAPI
} from '@/api/pm/manage'

import PinyinMatch from 'pinyin-match'

export default {
  name: 'ToggleProject', // 切换项目
  props: {
    detail: Object
  },
  data() {
    return {
      loading: false,
      loadNum: 0,
      visible: false,
      searchInput: '',

      starList: [], // 标星的项目
      allProjectList: [] // 全部项目
    }
  },
  computed: {
    list() {
      let starList = this.starList
      let allProjectList = this.allProjectList
      if (this.searchInput) {
        starList = this.starList.filter(item => PinyinMatch.match(item.name || '', this.searchInput))
        allProjectList = this.allProjectList.filter(item => PinyinMatch.match(item.name || '', this.searchInput))
      }
      return [
        { title: '星标项目', list: starList },
        { title: '全部项目', list: allProjectList }
      ]
    }
  },
  watch: {
    visible(val) {
      if (val) {
        this.loading = true
        this.getProjectList()
      }
    }
  },
  methods: {
    createClick() {},

    showCurrentProject(item) {
      return item.projectId === this.detail.projectId
    },

    getProjectList() {
      this.getStarProjectList()
      this.getAllProjectList()
    },

    /**
     * @description: 处理loading状态
     * @return {*}
     */
    handleLoading() {
      this.loadNum++
      this.loadNum === 1 ? this.loading = true : this.loading = false
    },

    /**
     * @description: 获取参数
     * @return {*}
     */
    getParams() {
      return {
        name: this.searchInput,
        pageType: 0
      }
    },

    /**
     * @description: 星标项目列表
     * @return {*}
     */
    getStarProjectList() {
      projectCollectListAPI(this.getParams()).then(res => {
        this.starList = res.data?.list || []
        this.handleLoading()
      }).catch(() => {
        this.loading = false
      })
    },

    /**
     * @description: 全部项目列表
     * @return {*}
     */
    getAllProjectList() {
      projectQueryMyAPI(this.getParams()).then(res => {
        this.allProjectList = res.data.list || []
        this.handleLoading()
      }).catch(() => {
        this.loading = false
      })
    },

    /**
     * @description: 进入项目首页
     * @return {*}
     */
    enterAll() {
      this.visible = false
      this.$router.push({ name: 'allProjects' })
    },

    /**
     * @description: 进入项目详情
     * @param {*} sitem
     * @return {*}
     */
    handleRowClick(sitem) {
      this.visible = false
      this.$router.push({
        name: 'projectOverview',
        params: {
          id: sitem.projectId
        }
      })
    }
  }
}
</script>
<style lang='scss' scoped>
.project-select {
  display: flex;
  align-items: center;
  justify-content: flex-start;
  cursor: pointer;

  .selected-project {
    display: inline-block;
    max-width: 100px;
    overflow: hidden;
    font-size: 16px;
    color: $--color-black;
    text-overflow: ellipsis;
    white-space: nowrap;

    &:hover {
      color: $--color-primary;
    }
  }

  .wk-icon-caret-bottom {
    font-size: 12px;
    color: $--menu-item-font-color;
  }
}

.toggle-body {
  padding: 4px;
}

.input-wrap {
  margin-bottom: 16px;
}

.p-list {
  height: 375px;
  margin-bottom: 10px;
  overflow: auto;

  .list-block {
    .list-title {
      font-size: 12px;
      color: $--color-text-secondary;
    }

    .sub-list {
      margin-top: 8px;
      margin-bottom: 8px;

      .sub-wrap {
        display: flex;
        align-content: center;
        padding: 8px;
        cursor: pointer;
        border-radius: 3px;

        &:hover {
          background-color: $--button-default-background-color;

          .sub-title {
            color: #0052cc;
          }
        }

        .icon-wrap {
          display: flex;
          align-items: center;
          justify-content: center;
          width: 32px;
          height: 32px;
          margin-right: 10px;
          background-color: #f4f5f7;
          border: 1px solid #e2e4e9;
          border-radius: 3px;

          .icon {
            font-size: 18px;
            color: #0052cc;
          }
        }

        .sub-title {
          font-size: 14px;
          line-height: 32px;
          color: $--color-black;
        }
      }
    }

    .empty {
      height: 150px;

      /deep/ .empty-content {
        top: auto;
      }
    }
  }
}

.line {
  border-bottom: 1px solid $--border-color-base;
}

.isactive {
  background-color: $--button-default-background-color;

  .sub-title {
    color: #0052cc !important;
  }
}

.look-all {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: 8px;
  cursor: pointer;

  &:hover {
    color: $--color-primary;
  }
}

.add-btn {
  padding-right: 10px;

  .btn {
    padding: 0;
  }

  .wk-l-plus {
    font-size: 12px;
  }
}
</style>
