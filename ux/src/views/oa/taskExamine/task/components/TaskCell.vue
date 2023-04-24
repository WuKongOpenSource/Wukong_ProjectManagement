<template>
  <div
    ref="taskRow"
    class="list task-cell"
    @click="rowFun(data)">
    <div
      ref="listLeft"
      class="list-left">
      <div
        :class="data.checked ? 'title title-active' : 'title'"
        @click.stop>
        <el-checkbox
          v-model="data.checked"
          :disabled="checkboxDisabled"
          @change="taskOverClick(data)" />
      </div>
      <span
        :style="{ backgroundColor: priority.color }"
        class="priority">{{ priority.label }}</span>
      <el-tooltip
        placement="bottom"
        effect="light"
        popper-class="task-tooltip tooltip-change-border">
        <div slot="content">
          <span>{{ data.name }}</span>
        </div>
        <span
          ref="itemSpan"
          :class="data.checked ? 'item-name-active' : 'item-name'">
          {{ data.name }}
        </span>
      </el-tooltip>
    </div>
    <div class="list-right">
      <div
        v-if="data.labelList && data.labelList.length > 0"
        class="tag-box">
        <span
          v-for="(item, index) in showLabels"
          :key="index"
          :style="{'background': item.color}"
          class="k-name">{{ item.labelName }}</span>
        <el-tooltip
          v-if="hideShowLabels.length"
          placement="top"
          effect="light"
          popper-class="tooltip-change-border">
          <div
            slot="content"
            class="tooltip-content">
            <div
              v-for="(item, index) in hideShowLabels"
              :key="index"
              class="item-label"
              style="display: inline-block; margin-right: 10px;">
              <span
                :style="{'background': item.color || '#ccc'}"
                class="k-name"
                style=" padding: 3px 10px; color: #fff;border-radius: 3px;">{{ item.labelName }}</span>
            </div>
          </div>
          <el-button class="more-btn" icon="el-icon-more" />
        </el-tooltip>
      </div>

      <span
        v-if="data.stopTime"
        :class="[ 'due-time', { 'is-past': data.isEnd == 1 } ]">
        截止时间{{ data.stopTime | moment("YYYY-MM-DD") }}
      </span>

      <div class="img-group">
        <div
          v-if="data.relationCount"
          class="img-box">
          <i class="wukong wukong-relevance" />
          <span>{{ data.relationCount }}</span>
        </div>
        <div
          v-if="data.childAllCount > 0"
          class="img-box">
          <i class="wukong wukong-sub-task" />
          <span>{{ data.childWCCount }}/{{ data.childAllCount }}</span>
        </div>
        <div
          v-if="data.fileCount"
          class="img-box">
          <i class="wukong wukong-file" />
          <span>{{ data.fileCount }}</span>
        </div>
        <div
          v-if="data.commentCount"
          class="img-box">
          <i class="wukong wukong-comment-task" />
          <span>{{ data.commentCount }}</span>
        </div>
      </div>

      <xr-avatar
        v-if="data.mainUser && data.mainUser.userId"
        :id="data.mainUser.userId"
        :name="data.mainUser.realname"
        :size="24"
        :src="data.mainUser.img"
        :disabled="false"
        trigger="hover"
        class="user-img"
        @click.stop="" />
    </div>
  </div>
</template>
<script type="text/javascript">
// API
import { workTaskStatusSetAPI } from '@/api/pm/projectTask'

import TaskMixin from '@/views/oa/taskExamine/task/mixins/TaskMixin'

export default {
  name: 'TaskCell', // 任务cell
  components: {},
  mixins: [TaskMixin],
  props: {
    checkboxDisabled: {
      type: Boolean,
      default: false
    },
    data: Object,
    dataIndex: Number,
    dataSection: Number
  },
  data() {
    return {}
  },
  computed: {
    priority() {
      if (this.data.priority == 0 || !this.data.priority) {
        return this.priorityList[3] // 默认读取 priorityList 返回
      }
      return this.getPriorityColor(this.data.priority)
    },

    showLabels() {
      if (this.data.labelList.length > 3) {
        return this.data.labelList.slice(0, 3)
      }
      return this.data.labelList
    },

    hideShowLabels() {
      if (this.data.labelList.length > 3) {
        return this.data.labelList.slice(3)
      }
      return []
    }
  },
  watch: {},
  mounted() {},
  methods: {
    // 列表标记任务
    taskOverClick(val) {
      workTaskStatusSetAPI({
        taskId: val.taskId,
        status: val.checked ? 5 : 1
      })
        .then(res => {
          // this.$store.dispatch('GetOAMessageNum', 'task')
          this.$emit('on-handle', 'complete', this.data, this.dataIndex, this.dataSection)
        })
        .catch(() => {
          val.checked = false
        })
    },
    // 点击显示详情
    rowFun(val) {
      this.$emit('on-handle', 'view', this.data, this.dataIndex, this.dataSection)
    },
    onmouseoverFun(item) {
      if (
        this.$refs.itemSpan.offsetWidth >
        this.$refs.listLeft.offsetWidth - 21
      ) {
        this.$set(item, 'show', true)
      } else {
        this.$set(item, 'show', false)
      }
    }
  }
}
</script>
<style lang="scss" scoped>
@mixin v-align {
  vertical-align: middle;
}

@mixin cursor {
  cursor: pointer;
}

@mixin color9 {
  color: $--color-text-secondary;
}

.popover-btn-group {
  padding: 10px 0;
  margin: -12px;

  p {
    height: 26px;
    padding-left: 20px;
    font-size: 13px;
    line-height: 26px;

    @include cursor;
  }
}

.popover-btn-group p:hover {
  color: #2362fb;
  background: #f7f8fa;
}

.list {
  position: relative;
  display: flex;
  height: 40px;
  padding: 0 10px;
  line-height: 40px;
  cursor: pointer;

  .header {
    margin-bottom: 15px;

    img {
      width: 32px;
      margin-right: 14px;
      vertical-align: middle;
    }

    .name-time {
      display: inline-block;
      vertical-align: middle;

      .time {
        margin-top: 5px;
        font-size: 12px;
        color: $--color-text-secondary;
      }
    }
  }

  .title {
    display: inline-block;
    color: $--color-text-primary;
    cursor: pointer;

    .el-checkbox {
      padding-right: 7px;
    }
  }

  .title-active {
    color: $--color-text-regular;
    text-decoration: line-through;
    text-decoration-color: $--color-text-secondary;
  }

  .img-group {
    display: inline-block;
    font-size: 12px;
    color: $--color-text-secondary;
    vertical-align: middle;

    .img-box {
      display: inline-block;
      margin-right: 6px;

      .wukong {
        font-size: 12px;
      }

      .priority-btn {
        display: inline-block;
        width: 68px;
        height: 16px;
        font-size: 12px;
        line-height: 16px;
        color: #fff;
        text-align: center;
        border-radius: 10px;
      }
    }
  }

  .item-name-active {
    color: #8f8f8f;
    text-decoration: line-through;
  }

  .list-left,
  .list-right {
    display: inline-block;
  }

  .list-left {
    flex: 1;
    padding-right: 10px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;

    .priority {
      padding: 2px 8px;
      margin-right: 20px;
      font-size: 12px;
      color: white;
      border-radius: $--border-radius-base;
    }
  }

  .list-right {
    flex-shrink: 0;

    .user-img {
      margin-left: 12px;
      vertical-align: text-top;
    }

    .tag-box {
      display: inline-block;
      margin-right: 20px;

      .item-label {
        display: inline-block;
      }

      .k-name {
        display: inline-block;
        height: 20px;
        padding: 0 10px;
        margin-right: 6px;
        font-size: 12px;
        line-height: 20px;
        color: #fff;
        border-radius: 3px;
      }
    }
  }
}

.more-btn {
  padding: 3px 8px;
  margin-right: 6px;
}

.list:hover {
  background: #fafafa;
}

.list::before {
  position: absolute;
  top: 0;
  right: 0;
  left: 0;
  z-index: 2;
  height: 1px;
  color: $--border-color-base;
  content: " ";
  border-top: 1px solid $--border-color-base;
  -webkit-transform: scaleY(0.5);
  transform: scaleY(0.5);
  -webkit-transform-origin: 0 0;
  transform-origin: 0 0;
}

.list:first-child::before {
  display: none;
}

// 截止时间
.due-time {
  padding: 2px 8px;
  margin-right: 10px;
  font-size: 12px;
  color: $--color-text-secondary;
  background-color: #f1f1f1;
  border-radius: $--border-radius-base;
}

.due-time.is-past {
  color: #f95a5a;
  background-color: #fff2f2;
}

.tooltip-content {
  margin: 10px 10px 10px 0;
}
</style>
