<template>
  <transition name="slide-fade">
    <el-card
      :style="{ 'z-index': zIndex }"
      class="project-screening">
      <p class="header">
        <span class="label">高级筛选</span>
        <el-button
          type="text"
          @click="resetBtn">重置</el-button>
        <span
          class="rt el-icon-close"
          @click="close" />
      </p>
      <div class="content">
        <div
          v-for="(item, outIndex) in filterFieldList"
          :key="outIndex"
          class="menu-list">
          <p
            class="item-label"
            @click="rowFun(item)">
            {{ item.label }}
            <span :class="item.expand ? 'el-icon-arrow-right item-expand' : 'el-icon-arrow-down item-expand'" />
          </p>
          <div
            v-for="(val, index) in item.list"
            v-show="item.expand == false"
            :key="index"
            :class="[
              'item-list',
              val.checked ? 'item-list-active' : '',
              outIndex == 1 ? 'time-item-list' : ''
            ]"
            @click="rowChecked(val,outIndex,index, item.list)">
            <xr-avatar
              v-if="item.type == 'user'"
              :name="val.realname"
              :size="24"
              :src="val.img"
              class="user-img" />
            <span
              v-else-if="item.type == 'label'"
              :style="{ backgroundColor: val.color }"
              class="tag-icon">
              <i class="wk wk-label" />
            </span>
            <status-tag
              v-else-if="item.type == 'status' && val.statusType"
              :type="val.statusType"
              :status-name="val.statusName" />
            <span class="item-list-label">{{ val.name }}</span>
            <span class="el-icon-check rt" />
          </div>
        </div>
      </div>
    </el-card>
  </transition>
</template>

<script>
import { queryLabelAPI, workQueryIterationsItemListAPI } from '@/api/pm/projectTask'
import { queryProjectUserListAPI } from '@/api/pm/manage'

import StatusTag from '@/views/pm/project/components/StatusTag'

import { priorityList } from '@/views/pm/data'
import { getMaxIndex } from '@/utils/index'
import { objDeepCopy } from '@/utils'

export default {
  name: 'ListFilter',
  components: {
    StatusTag
  },
  props: {
    listType: String,
    list: Array
  },
  data() {
    return {
      zIndex: getMaxIndex(),
      filterFieldList: [],
      filterItems: [{
        type: 'status',
        field: 'statusQuery',
        label: '状态',
        id: '3',
        expand: false,
        list: [
          { name: '未开始', id: 1, checked: false },
          { name: '进行中', id: 2, checked: false },
          { name: '已完成', id: 3, checked: false }
        ]
      }, {
        type: 'iteration',
        field: 'belongIterationIdQuery',
        label: '所属迭代',
        id: '3',
        expand: false,
        list: []
      }, {
        type: 'priority',
        field: 'priorityQuery',
        label: '优先级',
        id: '3',
        expand: false,
        list: []
      }, {
        type: 'label',
        field: 'labelQuery',
        label: '标签',
        id: '3',
        expand: false,
        list: []
      },
      // {
      //   type: 'user',
      //   field: 'handleUser',
      //   label: '处理人',
      //   id: '3',
      //   expand: false,
      //   list: []
      // },
      {
        type: 'user',
        field: 'mainUserIdQuery',
        label: '负责人',
        id: '3',
        expand: false,
        list: []
      }],
      iteration: [{
        type: 'status',
        field: 'statusQuery',
        label: '状态',
        id: '3',
        expand: false,
        list: [
          { name: '未开始', id: 1, checked: false },
          { name: '进行中', id: 2, checked: false },
          { name: '已完成', id: 3, checked: false }
        ]
      }, {
        type: 'user',
        field: 'mainUserIdQuery',
        label: '负责人',
        id: '1',
        expand: false,
        list: []
      }]
    }
  },
  watch: {
    list: {
      handler(val) {
        this.getFilterField()
      },
      deep: true,
      immediate: true
    }
  },
  mounted() {
    document
      .getElementById('app')
      .addEventListener('click', this.taskShowHandle, false)
  },
  methods: {
    /**
     * @description: 获取筛选字段
     * @return {*}
     */
    getFilterField() {
      if (this.list && this.list.length) {
        this.filterFieldList = objDeepCopy(this.list)
      } else {
        const all = [
          {
            type: 'itemType',
            field: 'typeQuery',
            label: '事项类型',
            id: '3',
            expand: false,
            list: [
              { name: '需求', id: 2, checked: false },
              { name: '任务', id: 3, checked: false },
              { name: '缺陷', id: 4, checked: false }
            ]
          },
          ...this.filterItems
        ]

        const itemDetail = this.filterItems.filter(item => item.field != 'belongIterationIdQuery')

        this.filterFieldList = {
          all,
          item: this.filterItems,
          iteration: this.iteration,
          itemDetail: itemDetail
        }[this.listType]

        if (['all', 'item', 'itemDetail'].includes(this.listType)) {
          const priorityIndex = this.getFieldIndex('priority')
          this.filterFieldList[priorityIndex].list = priorityList.map(item => {
            item.name = item.label
            item.id = item.key
            item.checked = false
            return item
          })

          this.getLabelList()
          if (this.listType != 'itemDetail') {
            this.getIterationList()
          }
        }
        this.getUserList()
      }
    },

    /**
     * @description: 获取字段索引
     * @param {*} type
     * @return {*}
     */
    getFieldIndex(type) {
      const labelIndex = this.filterFieldList.findIndex(item => item.type == type)
      return labelIndex
    },

    /**
     * @description: 获取处理人、负责人
     * @return {*}
     */
    getUserList() {
      const params = { projectId: this.$route.params.id }
      queryProjectUserListAPI(params).then(res => {
        const list = res.data || []
        // eslint-disable-next-line
        for (const item of this.filterFieldList) {
          if (item.type == 'user') {
            item.list = objDeepCopy(list).map(sitem => {
              sitem.name = sitem.realname
              sitem.id = sitem.userId
              sitem.checked = false
              return sitem
            })
          }
        }
      })
    },

    /**
     * @description: 获取标签
     * @return {*}
     */
    getLabelList() {
      const labelIndex = this.getFieldIndex('label')
      this.filterFieldList[labelIndex].list = []
      queryLabelAPI({ name: '' })
        .then(res => {
          this.filterFieldList[labelIndex].list = res.data.map(item => {
            item.id = item.labelId
            item.checked = false
            return item
          })
        })
        .catch(() => {})
    },

    /**
     * @description: 查询事项列表
     * @return {*}
     */
    getIterationList() {
      const iterationIndex = this.getFieldIndex('iteration')
      this.filterFieldList[iterationIndex].list = []
      const params = {
        type: 0,
        pageType: 0,
        projectId: this.$route.params.id,
        name: ''
      }
      workQueryIterationsItemListAPI(params).then(res => {
        this.filterFieldList[iterationIndex].list = (res.data.list || []).map(item => {
          item.id = item.taskId
          item.checked = false
          return item
        })
      })
    },

    /**
     * @description: 关闭
     * @return {*}
     */
    close() {
      this.$emit('close-filter')
    },

    rowChecked(item, outIndex, index, list) {
      list[index].checked = !item.checked
      // this.$set(this.filterFieldList[outIndex].list[index], 'checked', !item.checked)
      this.$emit('change', this.filterFieldList)
    },

    resetBtn() {
      // eslint-disable-next-line no-unused-vars
      for (const item of this.filterFieldList) {
        // eslint-disable-next-line no-unused-vars
        for (const i of item.list) {
          i.checked = false
        }
      }
      this.$emit('change', this.filterFieldList)
    },

    rowFun(val) {
      val.expand ? (val.expand = false) : (val.expand = true)
    },

    /**
     * @description: 点击空白处关闭详情
     * @param {*} e
     * @return {*}
     */
    taskShowHandle(e) {
      if (!this.$el.contains(e.target)) {
        this.close()
      }
    }
  }
}
</script>

<style scoped lang="scss">
.project-screening {
  position: fixed;
  top: 56px;
  right: 0;
  bottom: 0;
  width: 300px;
  overflow: auto;

  /deep/ .el-card__body {
    height: 100%;
    padding: 0;
  }

  .header {
    padding: 8px 16px;
    margin-bottom: 10px;
    border-bottom: $--border-base;

    .label {
      margin-right: 10px;
      font-size: 16px;
      font-weight: 600;
    }

    .el-icon-close {
      margin-top: 8px;
      margin-right: 0;
      font-size: 18px;
      font-weight: 600;
      color: $--color-text-placeholder;
      cursor: pointer;
    }

    .el-icon-close:hover {
      color: $--color-primary;
    }
  }

  .content {
    .el-input {
      padding: 10px 15px;
    }

    height: calc(100% - 60px);
    overflow-y: auto;

    .menu-list {
      margin-bottom: 10px;

      .item-label {
        position: relative;
        height: 36px;
        padding: 0 30px;
        font-size: 14px;
        font-weight: 600;
        line-height: 36px;
        color: $--color-text-primary;
        cursor: pointer;

        .item-expand {
          position: absolute;
          top: 0;
          right: 12px;
          height: 36px;
          line-height: 36px;
        }
      }

      .item-label::before {
        position: absolute;
        top: 10px;
        bottom: 10px;
        left: 18px;
        width: 4px;
        content: "";
        background-color: $--color-primary;
        border-radius: 2px;
      }

      .time-item-list {
        padding: 8px 10px 8px 35px !important;
      }

      .item-list {
        padding: 4px 10px 4px 35px;
        margin: 5px 0;
        color: $--color-text-primary;

        .el-icon-check {
          margin-top: 3px;
          opacity: 0;
        }

        .user-img {
          margin-right: 5px;
          vertical-align: middle;
        }

        span {
          vertical-align: middle;
        }
      }

      .tag-icon {
        display: inline-block;
        flex-shrink: 0;
        width: 24px;
        height: 24px;
        margin-right: 10px;
        line-height: 24px;
        text-align: center;
        border-radius: $--border-radius-base;

        .wk {
          font-size: 12px;
          color: white;
        }
      }

      .item-list-label {
        font-size: 14px;
        color: $--color-text-primary;
      }

      .item-list-label.is-tag {
        font-size: 12px;
        font-weight: 600;
      }

      .item-list:hover {
        cursor: pointer;
        background: $--background-color-base;
      }

      .item-list-active {
        cursor: pointer;
        background: $--background-color-base;

        .el-icon-check {
          color: $--color-primary;
          opacity: 1;
        }
      }
    }
  }
}
</style>
