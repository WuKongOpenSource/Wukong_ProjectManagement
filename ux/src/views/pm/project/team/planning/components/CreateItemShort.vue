<template>
  <div class="item-wrap">
    <div v-if="showCreate" class="create-box">
      <el-dropdown
        trigger="click"
        @command="dropdownCommand">
        <span class="el-dropdown-link">
          <!-- <i :class="icon" /> -->
          <img class="item-img" :src="icon" alt="">
          <i class="el-icon-arrow-down el-icon--right" />
        </span>
        <el-dropdown-menu slot="dropdown">
          <el-dropdown-item
            v-for="(items, indexs) in dropdowns"
            :key="indexs"
            :command="items.command">
            <div class="option-item">
              <img :src="items.icon" class="option-pic" alt="">
              <span>{{ items.label }}</span>
            </div>
          </el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
      <el-input v-model="title" class="input" :placeholder="`${placeholder}标题`" />
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
        新建事项
      </el-button>
    </div>
  </div>
</template>

<script>
import { workSaveProjectItemAPI } from '@/api/pm/projectTask'
import { itemTypeMap, getItemImg } from '@/views/pm/data'
import { mapGetters } from 'vuex'
export default {
  name: 'CreateItemShort',
  props: {
    iterationId: [String, Number],
    iterationIndex: [String, Number]
  },
  data() {
    return {
      dropdowns: [
        { label: '需求', command: 'Require', icon: require('@/assets/img/pm/require.png') },
        { label: '任务', command: 'Task', icon: require('@/assets/img/pm/task.png') },
        { label: '缺陷', command: 'Defects', icon: require('@/assets/img/pm/problem.png') }
      ],
      icon: getItemImg(2),
      cuttentItem: 'Require',
      title: '',
      placeholder: '需求',
      showCreate: false
    }
  },
  computed: {
    ...mapGetters(['userInfo']),
    createType() {
      return this.iterationId ? 'iteration-item' : 'log'
    }
  },
  methods: {
    /**
     * @description: 新建事项
     * @return {*}
     */
    createClick() {
      this.showCreate = true
      this.updateStatus()
    },

    updateStatus() {
      this.$emit('change-height', this.createType)
    },

    /**
     * @description: 新建保存
     * @return {*}
     */
    handleConfirm() {
      if (!this.title) {
        return this.$message.error('请输入事项名称')
      }
      const params = {
        companyId: this.userInfo.companyId,
        type: itemTypeMap[this.cuttentItem],
        projectId: this.$route.params.id,
        name: this.title,
        priority: '2'
      }
      if (this.iterationId) {
        params.belongIterationId = this.iterationId
      }
      workSaveProjectItemAPI(params).then(res => {
        this.title = ''
        this.$emit('save-success', {
          createType: this.createType,
          index: this.iterationIndex,
          id: this.iterationId
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
    },

    /**
     * @description: 选择事项类型
     * @param {*} command
     * @return {*}
     */
    dropdownCommand(command) {
      this.cuttentItem = command
      this.icon = {
        'Require': getItemImg(2),
        'Task': getItemImg(3),
        'Defects': getItemImg(4)
      }[command]
      this.placeholder = {
        'Require': '需求',
        'Task': '任务',
        'Defects': '缺陷'
      }[command]
    }
  }
}
</script>
<style lang='scss' scoped>
  .item-wrap {
    margin-top: 10px;

    .create-box {
      display: flex;
      align-items: center;
      justify-content: space-between;
      width: 100%;
      padding: 10px 16px;
      background-color: #fff;
      border-radius: 3px;

      .item-img {
        display: inline-block;
        width: 16px;
        height: 16px;
        margin-right: 5px;
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

    .add-btn {
      padding-right: 10px;

      .btn {
        padding: 0;
      }

      .wk-l-plus {
        font-size: 12px;
      }
    }
  }

  .option-item {
    display: flex;
    align-items: center;
    justify-content: flex-start;

    .option-pic {
      width: 16px;
      height: 16px;
      margin-right: 5px;
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
