<template>
  <div v-loading="loading" class="main">
    <div class="header">
      <flexbox class="main-header" justify="space-between">
        <div class="main-header__left">
          <span class="title">申请</span>
        </div>
      </flexbox>

      <wk-filter-header
        class="main-header is-filter-header"
        :props="wkHeaderProps">
        <template slot="left-start" />
        <!-- eslint-disable-next-line -->
        <template slot="left-start" slot-scope="scope">
          <el-input
            v-model="search"
            style="width: 210px; margin-right: 10px;"
            placeholder="请输入审批流名称"
            @change="handlerSearch">
            <el-button
              slot="suffix"
              type="icon"
              icon="wk wk-sousuo"
              @click="handlerSearch" />
          </el-input>
          <div class="custom-scene">
            <span>显示：</span>
            <el-button
              v-for="(item, index) in tabs.length > 5 ? 5 : tabs.length"
              :key="index"
              :type="tabs[index].name === tabsSelectValue ? 'selected' : null"
              @click="tabsChange(tabs[index].name,tabs[index].label)">{{ tabs[index].label }}</el-button>
            <el-dropdown
              v-if="moreTabs.length"
              style="margin-left: 4px;"
              @command="handlerMoreTab">
              <el-button class="dropdown-btn">
                <i
                  class="el-icon-arrow-down"
                  style="margin-left: 0;" />
              </el-button>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item
                  v-for="(item,index) in moreTabs"
                  :key="index"
                  :command="beforeHandlerCommand(item.label,item.name)">{{ item.label }}</el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </div>
        </template>

      </wk-filter-header>
    </div>

    <div class="main-container">
      <div
        v-for="(item,index) in categoryList"
        :key="index"
        class="categorys">
        <span>{{ item.groupName }}</span>
        <div class="category-list">
          <draggable
            v-model="item.superExamineVOList"
            :options="{ dragClass: 'sortable-drag', forceFallback: false }"
            style="flex-wrap: wrap;"
            class="vux-flexbox"
            @end="draggableEnd(item)">
            <flexbox
              v-for="(sItem, sIndex) in item.superExamineVOList"
              :key="sIndex"
              class="category-item"
              @click.native="selectCategorys(sItem,sItem.examineName)">
              <div
                :style="{ backgroundColor: sItem.iconColor }"
                class="category-icon">
                <i :class="sItem.iconClass" />
              </div>
              <div class="category-label ">{{ sItem.examineName }}</div>
            </flexbox>
          </draggable>
        </div>
      </div>
    </div>
    <examine-create-view
      v-if="isCreate"
      :category-id="createInfo.categoryId"
      :type="createInfo.type"
      :category-title="createInfo.categoryTitle"
      :action="createAction"
      @hiden-view="isCreate = false" />
  </div>
</template>

<script>
import {
  superExaminesApplyListAPI,
  superExaminesByNameAPI,
  saveOrUpdateOaExamineSortAPI
} from '@/api/oa/superExamine'

import WkFilterHeader from '@/components/NewCom/WkFilterHeader'
import ExamineCreateView from '../../examine/components/ExamineCreateView'

import { objDeepCopy } from '@/utils'
import Draggable from 'vuedraggable'
export default {
  name: 'Application', // 申请
  components: {
    ExamineCreateView,
    WkFilterHeader,
    Draggable
  },
  data() {
    return {
      loading: false,
      search: '',
      tabsSelectValue: 'all',
      wkHeaderProps: {
        showSearch: false
      },
      tabs: [
        { label: '全部', name: 'all' }
      ],

      categoryAllList: [],
      categoryList: [],

      // 新建
      isCreate: false,
      createInfo: {},
      createAction: { type: 'save' }
    }
  },
  computed: {
    moreTabs() {
      if (this.tabs.length > 5) {
        const tab = objDeepCopy(this.tabs)
        return tab.splice(5, this.tabs.length - 1)
      } else {
        return []
      }
    }
  },
  created() {
    this.getDetail()
  },
  methods: {
    /**
     * 点击tabs
     */
    tabsChange(type, label) {
      this.tabsSelectValue = type
      this.search = ''
      if (type == 'all') {
        // this.categoryList = objDeepCopy(this.categoryAllList)
        this.getDetail()
      } else {
        let superExamineVOList = null
        this.categoryAllList.forEach(item => {
          if (item.groupName == label) {
            superExamineVOList = item.superExamineVOList
          }
        })

        this.categoryList = [
          { groupName: label, superExamineVOList }
        ]
      }
    },

    /**
     * 动态设置Dropdown的command
     */
    beforeHandlerCommand(label, value) {
      return {
        label,
        value
      }
    },

    handlerMoreTab(command) {
      this.tabsChange(command.value, command.label)
    },

    /**
     * 审批类型列表
     */
    getDetail() {
      this.loading = true
      superExaminesApplyListAPI()
        .then(res => {
          this.loading = false
          this.tabsSelectValue = 'all'
          const resData = res.data || []
          this.categoryList = resData
          this.categoryAllList = resData
          this.tabs = [{ label: '全部', name: 'all' }]
          resData.forEach(item => {
            this.tabs.push({ label: item.groupName, name: item.groupId })
          })
        })
        .catch(() => {
          this.loading = false
        })
    },

    /**
     * 创建审批
     */
    selectCategorys(item, name) {
      let ungrouped = []
      this.categoryAllList.forEach(item => {
        if (item.groupName == '未分组') {
          ungrouped = item.superExamineVOList.map(item => item.examineId)
        }
      })

      if (ungrouped.includes(item.examineId)) {
        this.$message.error('当前审批信息不完善，请前往后台进行配置')
        return
      }
      this.createInfo = {
        categoryTitle: name,
        categoryId: item.examineId
      }
      this.isCreate = true
    },

    /**
     * 搜索
     */
    handlerSearch() {
      if (!this.search) {
        this.getDetail()
        return
      }
      this.loading = true
      superExaminesByNameAPI({ examineName: this.search })
        .then(res => {
          this.tabsSelectValue = 'all'
          this.loading = false
          const resData = res.data || []
          this.categoryList = [
            { groupName: '', superExamineVOList: resData }
          ]
          console.log(res.data)
        })
        .catch(() => {
          this.loading = false
        })
    },

    draggableEnd(item) {
      const oaExamineSortList = []
      item.superExamineVOList.forEach((item, index) => {
        oaExamineSortList.push({ categoryId: item.examineId, sort: index })
      })
      const params = {
        groupId: item.groupId,
        oaExamineSortList: oaExamineSortList
      }

      saveOrUpdateOaExamineSortAPI(params)
        .then(res => {
          this.$message.success('修改成功')
        })
    }
  }
}
</script>

<style lang="scss" scoped>
@import "../style/index.scss";

.main {
  display: flex;
  flex-direction: column;
  height: 100%;
  overflow: hidden;
}

.header {
  padding-bottom: 20px;
  background-color: #fff;
}

.main-container {
  flex: 1;
  overflow-y: auto;
  border-bottom: 1px solid transparent;

  .categorys {
    span {
      margin-left: 10px;
      font-size: 18px;
    }

    .category-list {
      display: flex;
      flex-wrap: wrap;
      margin-top: 8px;
      margin-bottom: 24px;
      overflow-y: auto;

      .category-item {
        flex: 0 0 23%;
        padding: 10px;
        margin: 5px;
        text-align: center;
        cursor: pointer;
        background-color: #f6f6f6;
        border-radius: $--border-radius-base;
      }

      .category-item-line {
        background-color: transparent;
        border-bottom: 1px solid $--border-color-base;
      }

      .category-item:hover {
        background-color: #eef1f8;
      }

      .category-icon {
        width: 40px;
        height: 40px;
        line-height: 40px;
        border-radius: $--border-radius-base;

        .wk {
          font-size: 24px;
          color: white;
        }
      }

      .category-label {
        display: flex;
        flex: 1;
        padding-left: 10px;
        overflow: hidden;
        font-size: 14px;
        text-align: left;
        text-overflow: ellipsis;
        -webkit-line-clamp: 2;
        -webkit-box-orient: vertical;
      }
    }
  }
}
</style>
