<template>
  <div class="create-box">
    <el-checkbox
      v-if="subId"
      v-model="checked"
      class="checked"
      :disabled="true" />
    <img class="item-img" src="@/assets/img/pm/child.png" alt="">
    <el-input ref="subInput" v-model="title" class="input" placeholder="子工作项标题" />
    <div class="btn-group">
      <el-button
        v-debounce="handleConfirm"
        type="primary">保存</el-button>
      <el-button @click.native="handleClose">取消</el-button>
    </div>
  </div>
</template>

<script>
export default {
  name: 'SubWorkItem',
  components: {

  },
  props: {
    name: String,
    index: Number,
    subId: [String, Number]
  },
  data() {
    return {
      checked: false,
      title: ''
    }
  },
  computed: {

  },
  watch: {

  },
  created() {
    if (this.name) {
      this.title = this.name
    }
  },
  mounted() {
    // 默认聚焦
    this.$refs.subInput.focus()
  },
  methods: {
    /**
     * @description: 取消
     * @return {*}
     */
    handleClose() {
      this.$emit('close')
    },

    /**
     * @description: 保存
     * @return {*}
     */
    handleConfirm() {
      this.$emit('save', {
        type: this.name ? 'edit' : 'add',
        name: this.title,
        subId: this.subId,
        index: this.index
      })
      if (!this.name) {
        this.title = ''
      }
    }
  }
}
</script>

<style scoped lang="scss">
.create-box {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
  padding: 10px 16px;
  background-color: #fff;
  border-radius: 3px;

  .checked {
    margin-right: 10px;
  }

  .item-img {
    display: inline-block;
    width: 16px;
    height: 16px;
  }

  .input {
    width: calc(100% - 60px);
    border-radius: 4px 4px 2px 2px;

    /deep/ .el-input__inner {
      background-color: #fff;
      border: none;
    }
  }
}

.btn-group {
  display: flex;
  align-items: center;
  justify-content: flex-start;
}

.el-dropdown-link {
  display: flex;
  align-items: center;
  justify-content: flex-start;
  cursor: pointer;

  .el-icon--right {
    margin-left: 0;
  }
}
</style>
