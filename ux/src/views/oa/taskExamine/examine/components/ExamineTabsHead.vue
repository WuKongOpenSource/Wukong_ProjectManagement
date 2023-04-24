<template>
  <base-tabs-head
    v-bind="$attrs"
    class="examine-tabs-head"
    v-on="$listeners">
    <el-dropdown
      slot="left"
      trigger="click"
      class="tabs-head-select"
      @command="handleSelectClick">
      <span class="el-dropdown-link">
        <i
          :class="['wk', 'dropdown-icon', 'wk-' + currentValue.icon]"
          :style="{ backgroundColor: currentValue.color }" />{{ currentValue.label }}<i class="el-icon-arrow-down el-icon--right" />
      </span>
      <el-dropdown-menu slot="dropdown">
        <el-dropdown-item
          v-for="(item, index) in selectList"
          :key="index"
          :command="item"> <i
            :class="['wk', 'dropdown-icon', item.icon]"
            :style="{ backgroundColor: item.color }" />{{ item.label }}</el-dropdown-item>
      </el-dropdown-menu>
    </el-dropdown>
    <el-button
      slot="right"
      class="xr-btn--orange"
      icon="el-icon-plus"
      type="primary"
      @click="createClick">新建审批</el-button>
    <el-button
      slot="right"
      class="export-btn"
      @click="exportClick">导出</el-button>
  </base-tabs-head>
</template>

<script>
import BaseTabsHead from '@/components/BaseTabsHead'

export default {
  /** 审批头 */
  name: 'ExamineTabsHead',
  components: {
    BaseTabsHead
  },
  inheritAttrs: false,
  props: {
    selectList: Array
  },
  data() {
    return {
      currentValue: {
        icon: 'approve',
        color: '#9376FF',
        command: 'all',
        label: '全部审批'
      }
    }
  },
  computed: {},
  watch: {
    selectList() {
      this.getDefaultSelectValue()
    }
  },
  mounted() {
    this.getDefaultSelectValue()
  },

  beforeDestroy() {},
  methods: {
    /**
     * 获取默认选中
     */
    getDefaultSelectValue() {
      this.currentValue =
        this.selectList && this.selectList.length
          ? this.selectList[0]
          : {
            icon: 'wk wk-approve',
            color: '#9376FF',
            command: 'all',
            label: '全部审批'
          }
    },

    handleSelectClick(value) {
      this.currentValue = value
      this.$emit('select', value.command)
    },

    /**
     * 创建
     */
    createClick() {
      this.$emit('add')
    },

    /**
     * 导出
     */
    exportClick() {
      this.$emit('export')
    }
  }
}
</script>

<style lang="scss" scoped>
.tabs-head-select {
  margin-left: 30px;
}

.el-dropdown-link {
  color: $--color-text-primary;
  cursor: pointer;

  .el-icon--right {
    color: $--color-text-regular;
  }
}

.dropdown-icon {
  padding: 3px;
  margin-right: 5px;
  font-size: 12px;
  color: white;
  border-radius: $--border-radius-base;
}

.export-btn,
.xr-btn--orange {
  margin-right: 8px;
}
</style>
