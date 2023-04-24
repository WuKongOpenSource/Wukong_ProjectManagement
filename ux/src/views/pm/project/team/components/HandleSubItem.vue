<template>
  <div v-clickoutside="handleBlur" class="create-box">
    <el-checkbox
      v-model="checked"
      class="checked" />
    <img class="item-img" src="@/assets/img/pm/child.png" alt="">
    <el-input ref="subInput" v-model="title" class="input" placeholder="子工作项标题" />
  </div>
</template>

<script>
export default {
  name: 'HandleSubItem',
  components: {},
  props: {
    type: String,
    name: String
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

    handleBlur(e) {
      this.$emit(this.type, {
        checked: this.checked,
        name: this.title
      })
      this.title = ''
    }
  }
}
</script>

<style scoped lang="scss">
.create-box {
  display: flex;
  align-items: center;
  justify-content: flex-start;
  width: 100%;
  height: 48px;
  padding: 10px 14px;
  background-color: #fff;
  border: 1px solid #4c9aff;
  border-radius: 3px;

  .item-img {
    display: inline-block;
    width: 16px;
    height: 16px;
    margin-left: 10px;
  }

  .input {
    width: calc(100% - 60px);
    margin-left: 10px;
    border-radius: 4px 4px 2px 2px;

    /deep/ .el-input__inner {
      background-color: #fff;
      border: none;
    }
  }
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
