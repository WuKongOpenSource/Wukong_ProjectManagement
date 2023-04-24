<template>
  <base-tabs-head
    v-bind="$attrs"
    class="Task-tabs-head"
    v-on="$listeners">
    <span
      slot="left"
      class="task-title">
      <i class="wk wk-task" />{{ title }}
    </span>
    <el-button
      slot="right"
      class="export-btn"
      @click="exportClick">导出</el-button>
  </base-tabs-head>
</template>

<script>
import BaseTabsHead from '@/components/BaseTabsHead'

export default {
  /** 任务切换头 */
  name: 'TaskTabsHead',
  components: {
    BaseTabsHead
  },
  props: {
    title: String
  },
  data() {
    return {}
  },
  computed: {},
  watch: {},
  mounted() {},

  beforeDestroy() {},
  methods: {
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
.task-title {
  margin-left: 30px;
  color: $--color-text-primary;

  i {
    padding: 3px;
    margin-right: 5px;
    font-size: 12px;
    color: white;
    background-color: #1cbaf5;
    border-radius: $--border-radius-base;
  }
}

.export-btn {
  margin-right: 8px;
}
</style>
