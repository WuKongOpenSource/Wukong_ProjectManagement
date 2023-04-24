<template>
  <div class="iteration-create">
    <div v-if="showCreate" class="create-box">
      <el-input v-model="title" class="input" placeholder="迭代标题" />
      <div>
        <el-button
          v-debounce="handleConfirm"
          type="primary">新建</el-button>
        <el-button @click.native="handleClose">取消</el-button>
      </div>
    </div>
    <div v-else class="add-btn">
      <el-button class="btn" type="text" @click="createClick">
        <i class="wk wk-l-plus" />
        新建迭代
      </el-button>
      <div class="line" />
    </div>
  </div>
</template>

<script>
import { workSaveProjectItemAPI } from '@/api/pm/projectTask'
import { mapGetters } from 'vuex'
export default {
  name: 'CreateIterationShort',
  data() {
    return {
      title: '',
      showCreate: false
    }
  },
  computed: {
    ...mapGetters(['userInfo'])
  },
  methods: {
    /**
     * @description: 新建迭代
     * @return {*}
     */
    createClick() {
      this.showCreate = true
      this.updateStatus()
    },

    updateStatus() {
      this.$emit('change-height', 'iteration')
    },

    /**
     * @description: 保存
     * @return {*}
     */
    handleConfirm() {
      if (!this.title) {
        return this.$message.error('请输入迭代名称')
      }
      const params = {
        companyId: this.userInfo.companyId,
        type: 1,
        projectId: this.$route.params.id,
        name: this.title
      }
      workSaveProjectItemAPI(params).then(res => {
        this.title = ''
        this.$emit('save-success', {
          createType: 'iteration'
        })
      }).catch(() => {

      })
    },

    /**
     * @description: 取消
     * @return {*}
     */
    handleClose() {
      this.showCreate = false
      this.updateStatus()
    }
  }
}
</script>
<style lang='scss' scoped>
  .iteration-create {
    background-color: #fff;

    .create-box {
      display: flex;
      align-items: center;
      justify-content: space-between;
      width: 100%;
      padding: 10px 16px;
      margin-top: 5px;
      background-color: $--form-pre-bg-color;
      border-radius: 3px;

      .input {
        width: calc(100% - 60px);

        /deep/ .el-input__inner {
          background-color: $--form-pre-bg-color;
          border: none;
        }
      }
    }

    .add-btn {
      position: relative;
      z-index: 14;
      padding: 18px 0;
      text-align: center;
      background-color: #fff;

      .el-button {
        position: absolute;
        top: calc(50% - 8px);
        z-index: 15;
      }

      .line {
        position: absolute;
        top: 50%;
        width: 100%;
        height: 1px;
        background-color: $--badge-background-color;
      }

      .btn {
        padding: 0 5px;
        background-color: #fff;
      }

      .wk-l-plus {
        font-size: 12px;
      }
    }
  }

</style>
