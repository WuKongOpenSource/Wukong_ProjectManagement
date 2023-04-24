<template>
  <div class="add-node-btn">
    <el-popover
      v-model="popoverShow"
      :visible-arrow="false"
      placement="right"
      trigger="click"
      popper-class="no-padding-popover">
      <div class="add-node-approve">
        <div class="add-node-approve-btn" @click="selectClick('approve')">
          <i class="wk wk-associate is-orange" />审批人
        </div>
        <div class="add-node-approve-btn" @click="selectClick('condition')">
          <i class="wk wk-approve is-green" />条件分支
        </div>
        <div class="add-node-approve-btn" @click="selectClick('copy')">
          <i class="wk wk-source-line" />抄送节点
        </div>
      </div>
      <el-button slot="reference" type="primary" icon="el-icon-plus" circle />
    </el-popover>
  </div>
</template>

<script>
export default {
  // 添加
  name: 'AddNodeBtn',

  components: {},

  props: {},

  data() {
    return {
      popoverShow: false
    }
  },

  computed: {},

  watch: {},

  created() {},

  mounted() {},

  beforeDestroy() {},

  methods: {
    /**
     * 选择
     */
    selectClick(command) {
      this.popoverShow = false
      this.$emit('command', command)
    }
  }
}
</script>
