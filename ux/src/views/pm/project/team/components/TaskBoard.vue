<template>
  <div
    v-loading="loading"
    class="content-box">
    <draggable
      id="task-board-body"
      v-scrollx="{ ignoreClass :['ignoreClass']}"
      xs-empty-text="请将状态拖入此列 否则该列将不在看板显示"
      :list="list"
      :options="{
        group: 'board',
        dragClass: 'sortable-parent-drag',
        filter: '.ignore-elements'
      }"
      handle=".board-column-wrapper"
      class="board-column-content-parent">
      <div
        v-for="(item, index) in list"
        :key="item.projectBoardId"
        class="board-column">
        <flexbox
          orient="vertical"
          align="stretch"
          class="board-column-wrapper ignoreClass">
          <div class="board-column-header">
            <div
              v-if="item.renameShow"
              class="input-btn">
              <el-input
                ref="text"
                v-model="editBoardName"
                :maxlength="10"
                size="small"
                placeholder="列表名"
                @blur="renameBoardSubmit(item, index)" />
            </div>
            <div
              v-else-if="!item.renameShow"
              class="header-title">
              <span class="text">⋮⋮ {{ item.boardName }}&nbsp;&nbsp;{{ item.statusList.length }} </span>
              <span class="option-btn">
                <el-popover
                  v-model="item.taskHandleShow"
                  placement="bottom-end"
                  width="150"
                  trigger="click">
                  <div class="omit-popover-box">
                    <p @click.stop="renameBoardClick(item, index)">重命名</p>
                    <p @click="delectBoardClick(item, index)">删除列</p>
                  </div>
                  <i slot="reference" class="el-icon-more img-gd" />
                </el-popover>

              </span>
            </div>

          </div>
          <draggable
            :list="item.statusList"
            :options="{
              group: {
                name: 'status'
              },
              dragClass: 'sortable-drag'
            }"
            class="board-column-content">
            <div
              v-for="(element, i) in item.statusList"
              ref="taskRow"
              :key="i.taskId"
              class="board-item">
              <flexbox align="stretch">
                <div class="element-label">
                  <status-tag
                    v-if="element.statusType"
                    :status-name="element.statusName"
                    :type="element.statusType" />
                </div>
              </flexbox>
            </div>
          </draggable>
        </flexbox>
      </div>

      <!-- 新建列表 -->
      <div
        class="board-column-new-list">
        <div
          v-if="!createBoardShow && loading == false"
          class="new-list"
          @click="createBoardShow = true">
          <el-button icon="el-icon-plus">新建列</el-button>
          <!-- <span class="el-icon-plus" />
          <span>新建列表</span> -->
        </div>
        <div
          v-else-if="createBoardShow && loading == false"
          class="input-btn">
          <el-input
            v-model="boardName"
            :maxlength="10"
            size="small"
            placeholder="未命名列"
            @blur="createBoardSave(boardName)" />
        </div>
      </div>
    </draggable>
    <!-- 未在看板中的状态 -->
    <div
      class="bottom-label">
      <flexbox
        orient="vertical"
        align="stretch"
        class="board-column-wrapper ignoreClass">
        <div class="board-column-header">
          <span class="text">以下状态未在看板列中，如果希望特定状态下的事项不在看板显示,可将状态拖进该列</span>
        </div>
        <div
          v-empty="statusList.length == 0"
          xs-empty-icon="none"
          xs-empty-text="所有事项状态已添加到看板">
          <draggable
            :list="statusList"
            :options="{
              group: {
                name: 'status'
              },
              dragClass: 'sortable-drag'
            }"
            class="board-column-content">
            <div
              v-for="(element, i) in statusList"
              ref="taskRow"
              :key="i"
              class="board-item">
              <flexbox align="stretch">
                <div class="element-label">
                  <status-tag
                    v-if="element.statusType"
                    :status-name="element.statusName"
                    :type="element.statusType" />
                </div>
              </flexbox>
            </div>
          </draggable>
        </div>
      </flexbox>
    </div>
  </div>
</template>
<script>
import StatusTag from '@/views/pm/project/components/StatusTag.vue'

import draggable from 'vuedraggable'
import scrollx from '@/directives/scrollx'

export default {
  components: {
    draggable,
    StatusTag
  },

  directives: {
    scrollx
  },

  props: {
    showType: String,
    statusList: {
      type: Array,
      default() {
        return []
      }
    },
    list: {
      type: Array,
      default() {
        return []
      }
    }
  },

  data() {
    return {
      loading: false,
      createBoardShow: false,
      // 新建列表
      boardName: '',
      // 重命名
      editBoardName: ''
    }
  },

  computed: {},

  watch: {
    showType() {
      this.createBoardShow = false
    }
  },

  created() {
  },

  mounted() {
    // 为了防止火狐浏览器拖拽的时候以新标签打开
    document.body.ondrop = function(event) {
      event.preventDefault()
      event.stopPropagation()
    }
  },

  methods: {
    /**
     * @description: 重命名 -- 提交
     * @param {*} val
     * @param {*} index
     * @return {*}
     */
    renameBoardSubmit(val, index) {
      this.list[index].boardName = this.editBoardName
      val.renameShow = false
      this.editBoardName = ''
    },

    /**
     * @description: 重命名
     * @param {*} val
     * @param {*} index
     * @return {*}
     */
    renameBoardClick(val, index) {
      this.editBoardName = val.boardName
      val.taskHandleShow = false
      val.renameShow = true

      this.$nextTick(() => {
        this.$refs.text[0].focus()
      })
    },

    /**
     * @description: 删除列表
     * @param {*} val
     * @param {*} index
     * @return {*}
     */
    delectBoardClick(val, index) {
      this.$confirm('您确定要删除?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => {
          this.list.splice(index, 1)
        })
        .catch(() => {})
    },

    /**
     * @description: 新建列表提交
     * @param {*} name
     * @return {*}
     */
    createBoardSave(name) {
      this.list.push({
        boardName: name || '未命名列',
        eventId: this.eventId,
        statusList: [],
        sorting: this.list.length
      })
      this.createBoardShow = false
      this.boardName = ''
    }
  }
}
</script>

<style scoped lang="scss">
.content-box {
  // position: relative;
  // height: 100%;
  overflow-x: auto;
  overflow-y: hidden;
  white-space: nowrap;
  user-select: none;

  .board-column-content-parent {
    height: 304px;

    // position: absolute;
    // top: 0;
    // right: 0;
    // bottom: 0;
    // left: 0;
    overflow-x: auto;
    overflow-y: hidden;
  }
}

.omit-popover-box {
  margin-right: -12px;
  margin-left: -12px;

  p {
    height: 40px;
    padding-left: 20px;
    font-size: 13px;
    line-height: 40px;
    color: $--color-text-primary;
    cursor: pointer;
  }

  p:hover {
    color: $--color-primary;
    background: #f7f8fa;
  }
}

.task-board-rechristen-popover {
  padding: 0;

  .task-board-rechristen-box {
    .title {
      padding: 15px;
      border-bottom: 1px solid $--skeleton-to-color;

      .el-icon-close {
        margin-right: 0;
      }
    }

    .content {
      padding: 0 15px;

      .el-input {
        margin: 15px 0;
      }

      .btn-box {
        margin-bottom: 15px;
        text-align: right;
      }
    }
  }
}

.board-column {
  display: inline-block;
  width: 180px;
  height: 300px;
  margin-right: 8px;
  overflow: hidden;

  .board-column-wrapper {
    position: relative;
    height: 100%;
    padding: 0;
    margin-right: 14px;
    vertical-align: top;
    background: $--color-n20;
    border-radius: $--border-radius-base;

    .board-column-header {
      height: 40px;
      padding: 0 16px;
      color: $--color-text-primary;

      .header-title {
        display: flex;
        align-items: center;
        justify-content: space-between;
        margin-bottom: 5px;
        line-height: 40px;
        cursor: pointer;

        .option-btn {
          display: block;
        }
      }

      .text {
        display: inline-block;
        font-size: $--font-size-base;
        font-weight: bold;
        color: $--color-black;
      }

      .input-btn {
        width: 100%;
        margin-top: 5px;
      }
    }

    .board-column-content {
      flex: 1;
      padding: 0 16px;
      overflow: auto;

      .board-item {
        position: relative;
        padding: 12px;
        margin-bottom: 8px;
        overflow: hidden;
        cursor: pointer;
        background-color: white;
        border-radius: $--border-radius-base;

        .element-label {
          word-wrap: break-word;
          white-space: pre-wrap;
        }
      }
    }
  }

  &:hover {
    // .header-title .option-btn {
    //   display: block !important;
    // }
  }
}

.board-column-new-list {
  display: inline-block;
  width: 180px;
  vertical-align: top;
  background: #fff;
  border-radius: $--border-radius-base;

  .new-list {
    display: flex;
    align-items: center;
    justify-content: center;
    color: $--color-text-secondary;
    cursor: pointer;

    .el-button {
      width: 100%;
    }

    .el-icon-plus {
      margin-right: 8px;
      font-weight: bold;
      color: $--color-primary;
    }
  }

  .input-btn {
    width: 100%;

    .button-box {
      float: right;
      margin-top: 8px;
    }
  }
}

.sortable-drag {
  background-color: white;
}

.sortable-parent-drag {
  background-color: transparent;
}

.bottom-label {
  // display: inline-block;
  // margin-right: 8px;
  width: 100%;
  margin-top: 16px;
  overflow: hidden;

  .board-column-wrapper {
    position: relative;
    min-height: 112px;
    padding: 16px 16px 0;
    vertical-align: top;
    background: $--color-n20;
    border-radius: $--border-radius-base;

    .board-column-header {
      padding: 8px 16px;
      color: $--color-text-primary;
      text-align: center;

      .text {
        display: inline-block;
        padding-bottom: 4px;
        font-size: $--font-size-base;
        font-weight: bold;
        color: $--color-black;
      }

      .el-progress /deep/ .el-progress-bar {
        padding-right: 0;
      }

      .el-progress /deep/ .el-progress__text {
        display: none;
      }

      .img-gd {
        float: right;
        color: $--color-text-regular;
        cursor: pointer;
      }
    }

    .board-column-content {
      display: flex;
      overflow: hidden;
      overflow-x: auto;
      overflow-y: hidden;

      /deep/ .empty-mask {
        background-color: transparent !important;
      }

      .board-item {
        position: relative;
        flex-shrink: 0;
        width: 138px;
        padding: 12px;
        margin-right: 16px;
        margin-bottom: 8px;

        // margin-bottom: 8px;
        overflow: hidden;
        cursor: pointer;
        background-color: white;
        border-radius: $--border-radius-base;

        .element-label {
          word-wrap: break-word;
          white-space: pre-wrap;
        }
      }
    }
  }
}
</style>

