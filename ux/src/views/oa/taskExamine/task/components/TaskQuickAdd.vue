<template>
  <div
    v-loading="sendLoading"
    :class="['add', {
      'unfold': isUnfold,
      'is-close': !isUnfold,
      'hide-border': showStyle === 'hideBorder' && !isUnfold
    }]"
    class="task-quick-add">
    <i
      v-if="isUnfold"
      class="wk wk-close"
      @click="addClose" />
    <el-input
      ref="input"
      v-model="sendContent"
      :maxlength="100"
      :prefix-icon="isUnfold ? '' : 'el-icon-plus'"
      class="input"
      placeholder="添加任务"
      @focus="inputFocus" />

    <flexbox class="add-info">
      <el-date-picker
        ref="endTime"
        v-model="sendStopTime"
        :class="{ 'no-time-top': !sendStopTime }"
        type="date"
        value-format="yyyy-MM-dd"
        placeholder="" />
      <wk-user-select
        :value="mainUser ? mainUser.userId : ''"
        radio
        style="height: auto !important;"
        @change="selectMainUser">
        <div
          slot="reference"
          class="select-user">
          <i
            v-if="!createMainUser"
            class="wk wk-persons add-info__btn add-info__interval" />
          <xr-avatar
            v-else
            :name="createMainUser.realname"
            :size="24"
            :src="createMainUser.img"
            class="add-info__interval" />
        </div>
      </wk-user-select>

      <i
        class="el-icon-more add-info__btn add-info__interval"
        @click="moreClick" />
    </flexbox>

    <el-button
      v-if="showStyle === 'hideBorder'"
      v-debounce="send"
      class="send-btn"
      type="primary">保存</el-button>
    <el-button
      v-else
      v-debounce="send"
      icon="wk wk-top"
      class="send-btn"
      type="primary">发布</el-button>

    <task-create
      v-if="taskCreateShow"
      :action="createAction"
      @save="createSuccess"
      @close="taskCreateShow = false"
    />
  </div>
</template>

<script>
import WkUserSelect from '@/components/NewCom/WkUserSelect'
import { setTaskAPI } from '@/api/oa/task'

import TaskCreate from '../Create'

import merge from '@/utils/merge'

const DefaultTaskAddProps = {
  relatedObj: {}, // 关联业务信息
  relatedObjIds: {} // 关联业务Ids信息
}

export default {
  // 任务快捷添加
  name: 'TaskQuickAdd',
  components: {
    WkUserSelect,
    TaskCreate
  },
  props: {
    // default  hideBorder
    showStyle: {
      type: String,
      default: 'default'
    },
    // 管理业务参数
    params: Object,
    props: Object
  },
  data() {
    return {
      // 添加
      sendLoading: false,
      // 默认闭合
      isUnfold: false,
      sendContent: '',
      sendStopTime: '',
      mainUser: [],
      taskCreateShow: false,
      createAction: {
        type: 'save'
      }
    }
  },
  computed: {
    /**
     * 负责人
     */
    createMainUser() {
      return this.mainUser && this.mainUser.length ? this.mainUser[0] : null
    },
    config() {
      return merge({ ...DefaultTaskAddProps }, this.props || {})
    }
  },
  watch: {},
  mounted() {},

  beforeDestroy() {},
  methods: {
    /**
     * 任务添加
     */
    inputFocus() {
      this.$emit('focus')
      this.isUnfold = true
      this.$nextTick(() => {
        this.$refs.input.focus()
      })
    },

    /**
     * 选择负责人
     */
    selectMainUser(_, dataArray) {
      this.mainUser = dataArray
    },

    /**
     * 创建任务
     */
    send() {
      if (!this.sendContent.length) {
        this.$message.error('请填写任务标题')
      } else {
        this.sendLoading = true
        let params = {
          name: this.sendContent,
          stopTime: this.sendStopTime,
          mainUserId: this.createMainUser ? this.createMainUser.userId : ''
        }
        if (this.params) {
          params = { ...params, ...this.params }
        }
        setTaskAPI(params)
          .then(res => {
            this.sendLoading = false
            this.$message.success('新建成功')
            this.resetSendData()
            this.$emit('send')
          })
          .catch(() => {
            this.sendLoading = false
          })
      }
    },

    resetSendData() {
      this.sendContent = ''
      this.sendStopTime = ''
      this.mainUser = []
      this.isUnfold = false
    },

    /**
     * 创建成功
     */
    createSuccess() {
      this.resetSendData()
      this.$emit('send')
    },

    /**
     * 关闭
     */
    addClose() {
      this.isUnfold = false
    },

    /**
     * 更多点击
     */
    moreClick() {
      this.createAction = {
        type: 'save',
        data: {
          name: this.sendContent,
          stopTime: this.sendStopTime,
          mainUserId: this.createMainUser ? this.createMainUser.userId : '',
          relatedObj: this.config.relatedObj,
          relatedObjIds: this.config.relatedObjIds
        },
        params: this.params
      }
      this.taskCreateShow = true
    }
  }
}
</script>

<style lang="scss" scoped>
// 添加
.add {
  position: relative;
  padding: 5px;
  background-color: white;
  border: 1px solid $--border-color-base;
  border-radius: $--border-radius-base;

  .input {
    width: calc(100% - 50px);

    /deep/.el-input__inner {
      border: none;
    }
  }

  .add-info {
    padding: 0 8px;

    .el-date-editor {
      width: 110px;
      font-size: 12px;

      /deep/ .el-input__prefix {
        left: 0;

        .el-icon-date {
          width: 24px;
          line-height: 24px;
        }
      }

      /deep/ .el-input__suffix {
        top: -4px;
      }

      /deep/ .el-input__inner {
        height: 24px;
        padding-right: 5px;
        padding-left: 20px;
        line-height: 24px;
        background-color: #f0f0f0;
        border: none;
        border-radius: 12px;
      }
    }

    &__btn {
      display: inline-block;
      width: 24px;
      height: 24px;
      font-size: 12px;
      line-height: 24px;
      color: #c0c4cc;
      text-align: center;
      cursor: pointer;
      border: 1px solid #e6e6e6;
      border-radius: 12px;
    }

    &__btn:hover {
      color: $--color-primary;
      border-color: $--color-primary;
    }

    &__interval {
      margin-left: 8px;
    }

    .no-time-top {
      width: 24px;
      cursor: pointer;

      /deep/ .el-input__inner {
        height: 24px;
        padding: 0;
        line-height: 24px;
        background-color: white;
        border: 1px solid $--border-color-base;
      }
    }

    .no-time-top:hover {
      /deep/ .el-input__inner {
        border-color: $--color-primary;
      }

      /deep/ .el-icon-date {
        color: $--color-primary;
      }
    }
  }

  // 无边框风格
  &.hide-border {
    padding: 0;
    border-color: white;

    .send-btn {
      display: none;
    }

    /deep/ .el-input__icon {
      color: $--color-primary;
    }
  }

  &.hide-border:hover {
    border-color: white !important;
  }
}

.add.is-close {
  .add-info {
    display: none;
  }
}

// 闭合
.add.is-close:hover {
  cursor: pointer;
  border-color: #c0c4cc;

  .input {
    /deep/ .el-icon-plus {
      color: $--color-primary;
    }
  }
}

// 展开
.add.unfold {
  .input {
    margin-bottom: 15px;
  }
}

.send-btn {
  position: absolute;
  right: 10px;
  bottom: 5px;
  z-index: 1;

  /deep/ i {
    font-size: 13px;
  }
}

// 选择负责人
.select-user {
  display: inline-block;
  cursor: pointer;
}

// 关闭按钮
.wk-close {
  position: absolute;
  top: 0;
  right: 0;
  z-index: 1;
  padding: 4px;
  font-size: 22px;
  color: #d9d9d9;
  cursor: pointer;
  border: 0;
  transform: scale(0.8);
}

.wk-close:hover {
  color: #2362fb;
}
</style>
