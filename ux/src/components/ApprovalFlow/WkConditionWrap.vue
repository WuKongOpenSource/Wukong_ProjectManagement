<template>
  <div class="wk-conditon-node">
    <div
      class="wk-conditon-node-wrap">
      <div class="conditon-wrap-body">
        <el-button class="add-btn" plain @click="addClick">添加条件</el-button>
        <div
          v-for="(item, itemIndex) in node.conditionList"
          :key="itemIndex"
          class="condition">
          <template v-if="itemIndex === 0">
            <div class="cover-line is-top-left" />
            <div class="cover-line is-bottom-left" />
          </template>
          <template v-if="itemIndex === node.conditionList.length - 1">
            <div class="cover-line is-top-right" />
            <div class="cover-line is-bottom-right" />
          </template>
          <wk-condition-node
            :index="itemIndex"
            :parent="item.examineDataList"
            :condition-parent="node.conditionList"
            :node="item"
            @delete="conditionDelete(itemIndex)"
            @node-click="nodeClick"
          />
          <template v-if="item.examineDataList && item.examineDataList.length > 0">
            <template
              v-for="(subItem, subIndex) in item.examineDataList">
              <wk-condition-wrap
                v-if="subItem.examineType === 0"
                :key="subIndex"
                :index="subIndex"
                :node="subItem"
                :parent="item.examineDataList" />
              <wk-node
                v-else
                :key="subIndex"
                :index="subIndex"
                :node="subItem"
                :header-color="getHeaderStyle(subItem).bgColor"
                :header-icon="getHeaderStyle(subItem).icon"
                :parent="item.examineDataList"
                @node-click="nodeClick" />
            </template>
          </template>
        </div>
      </div>
      <div class="add-node-btn-wrap">
        <add-node-btn @command="handleCommand" />
      </div>
    </div>
  </div>
</template>

<script>
import AddNodeBtn from './AddNodeBtn'
import WkNode from './WkNode'
import WkConditionNode from './WkConditionNode'

import { conditionModel, examineModel, conditionListModel, copyModel } from './flowModel'
import { objDeepCopy } from '@/utils'

export default {
  // 条件node
  name: 'WkConditionWrap',

  components: {
    AddNodeBtn,
    WkNode,
    WkConditionNode
  },

  props: {
    index: Number,
    parent: Array,
    node: {
      type: Object,
      default() {
        return {}
      }
    }
  },

  data() {
    return {
      tree: null
    }
  },

  computed: {},

  watch: {},

  created() {
    if (this.$parent.$options.name === 'WkConditionWrap') {
      this.tree = this.$parent.tree
    } else {
      this.tree = this
    }
  },

  mounted() {},

  beforeDestroy() {},

  methods: {
    /**
     * 条件添加
     */
    addClick() {
      this.node.conditionList.push(objDeepCopy(conditionModel))
    },

    /**
     * 条件删除
     */
    conditionDelete(index) {
      if (this.node.conditionList.length > 2) {
        this.node.conditionList.splice(index, 1)
      } else {
        this.parent.splice(this.index, 1)
      }
    },

    /**
     * 添加
     */
    handleCommand(command) {
      if (command === 'approve') {
        this.parent.splice(this.index + 1, 0, objDeepCopy(examineModel))
      } else if (command === 'condition') {
        this.parent.splice(this.index + 1, 0, objDeepCopy(conditionListModel))
      } else if (command === 'copy') {
        this.parent.splice(this.index + 1, 0, objDeepCopy(copyModel))
      }
    },

    /**
     * 点击
     */
    nodeClick(data) {
      this.tree.$emit('node-click', data)
    },

    /**
     * @description: 通过审批类型 获取对应头部信息
     * @param {*} item
     * @return {*}
     */
    getHeaderStyle(item) {
      const defaultStyle = {
        bgColor: '#0052cc',
        icon: ''
      }

      // examineType 1 审批人 7 抄送
      // 发起人：#0052cc（蓝色）
      // 审批节点：#FF991F（橙色）
      // 抄送节点：#6B778C（灰色）
      // 填写节点：#0052cc（蓝色）
      // 添加数据：#36B37E（绿色）
      // 更新数据：#00B8D9（浅蓝色）
      if (item.examineType >= 1 && item.examineType <= 5) {
        defaultStyle.bgColor = '#FF991F'
        defaultStyle.icon = 'wk wk-approve-line'
      } else if (item.examineType === 7) {
        defaultStyle.bgColor = '#6B778C'
        defaultStyle.icon = 'wk wk-source-line'
      }

      return defaultStyle
    }
  }
}
</script>

<style lang="scss">

</style>
