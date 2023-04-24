<template>
  <div
    class="item"
    :class="{'clicked': item.clicked}">
    <div ref="wrap" class="inner-wrap">
      <el-tooltip :content="item.name" :disabled="isShowTooltip" placement="top">
        <span ref="name" class="item-title">{{ item.name }}</span>
      </el-tooltip>
      <span @click.stop>
        <el-dropdown
          v-if="dropdowns && dropdowns.length > 0"
          trigger="click"
          @command="dropdownCommand($event, item)">
          <el-button class="dropdown-btn" icon="el-icon-more" />
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item
              v-for="(items, indexs) in dropdowns"
              :key="indexs"
              :command="items.command">
              <template v-if="items.command == 'plan'">
                <el-dropdown
                  v-if="iterationList && iterationList.length > 0"
                  trigger="hover"
                  @command="handleCommand">
                  <span>{{ items.name }}</span>
                  <el-dropdown-menu slot="dropdown">
                    <el-dropdown-item
                      v-for="(sitem, sindex) in iterationList"
                      :key="sindex"
                      :command="sitem">
                      {{ sitem.name }}
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </el-dropdown>
              </template>
              <template v-else>
                {{ items.name }}
              </template>

            </el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
      </span>
    </div>
    <div class="item-desc">
      <div class="item-left">
        <img class="item-pic" :src="getIconClass(item)">
        <span class="mark">#{{ item.num }}</span>
      </div>
      <div class="item-right">
        <span class="icon-name">
          <img class="item-img" :src="getPriorityPic(item.priority)" alt="">
          <span>{{ getPriority(item.priority) }}</span>
        </span>
        <status-tag
          :type="item.status"
          :status-name="item.boardStatusName" />
        <xr-avatar
          v-if="item.mainUserId"
          :id="item.mainUserId"
          class="user-photo"
          trigger="hover"
          :name="item.mainUserName"
          :src="item.mainUserImg"
          :size="24" />
      </div>
    </div>
  </div>
</template>

<script>
import {
  workDelProjectItemAPI,
  relationIterationAPI
} from '@/api/pm/projectTask'

import StatusTag from '@/views/pm/project/components/StatusTag'

import {
  convertPriority,
  getItemImg,
  getPriorityIcon
} from '@/views/pm/data'
export default {
  name: 'Item',
  components: {
    StatusTag
  },
  props: {
    item: Object,
    iterationList: Array
  },
  data() {
    return {
      isShowTooltip: true
    }
  },
  computed: {
    // 编辑事项权限
    editAuth() {
      return this.$auth('coordination.editMatters', 'PM')
    },

    // 删除事项权限
    delAuth() {
      return this.$auth('coordination.deletMatters', 'PM')
    },
    dropdowns() {
      if (!this.editAuth && !this.delAuth) return []
      const options = []
      if (this.editAuth) {
        if (this.item.belongIterationId) {
          options.push({ command: 'move', name: '移出迭代' })
        } else {
          options.push({ command: 'plan', name: '规划进迭代' })
        }
      }
      if (this.delAuth) {
        options.push({ command: 'del', name: '删除事项' })
      }
      return options
    }
  },
  mounted() {
    this.$nextTick(() => {
      this.isShowTooltip = this.$refs.name.offsetWidth < (this.$refs.wrap.offsetWidth - 35)
    })
  },
  methods: {
    /**
     * @description: 事项图标
     * @param {*} row
     * @return {*}
     */
    getIconClass(row) {
      return getItemImg(row)
    },

    /**
     * @description: 优先级文字
     * @param {*} val
     * @return {*}
     */
    getPriority(val) {
      return convertPriority(val)
    },

    /**
     * @description: 优先级图片
     * @param {*} val
     * @return {*}
     */
    getPriorityPic(val) {
      return getPriorityIcon(val)
    },

    /**
     * @description: 迭代中事项下拉选项
     * @param {*} command
     * @return {*}
     */
    dropdownCommand(command) {
      if (command == 'move') {
        this.$confirm('确定移出迭代?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          const params = { taskIds: [this.item.taskId] }
          relationIterationAPI(params).then(res => {
            this.$message.success('操作成功')
            this.$emit('save-success')
          })
        }).catch(() => {})
      } else if (command == 'del') {
        this.$confirm('确定删除?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          const params = {
            taskId: this.item.taskId
          }
          workDelProjectItemAPI(params).then(res => {
            this.$message({
              type: 'success',
              message: '删除成功'
            })
            this.$emit('save-success')
          }).catch(() => {

          })
        }).catch(() => {})
      }
    },

    /**
     * @description: 关联迭代下拉选择
     * @param {*} data
     * @return {*}
     */
    handleCommand(data) {
      const params = {
        taskIds: [this.item.taskId],
        belongIterationId: data.taskId
      }
      relationIterationAPI(params).then(res => {
        this.$message.success('操作成功')
        this.$emit('save-success')
      })
    }
  }
}
</script>
<style lang='scss' scoped>
.item {
  padding: 10px 16px;
  cursor: move;

  &:hover {
    background-color: #fafbfc;

    .item-title {
      text-decoration: underline;
    }
  }

  .inner-wrap {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 5px;
  }

  .item-title {
    max-width: calc(100% - 35px);
    overflow: hidden;
    font-size: 14px;
    color: $--color-text-primary;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  .dropdown-btn {
    padding: 2px 5px;
    margin-left: 5px;
    background-color: $--badge-background-color;
  }

  .item-desc {
    display: flex;
    align-items: flex-end;
    justify-content: space-between;

    .item-left {
      display: flex;
      align-items: center;
      justify-content: flex-start;

      .item-pic {
        display: inline-block;
        width: 16px;
        height: 16px;
        margin-right: 5px;
      }

      .mark {
        font-size: 14px;
        color: $--color-text-secondary;
      }
    }

    .item-right {
      display: flex;
      align-items: center;
      justify-content: flex-end;
    }
  }
}

.item + .item {
  border-top: $--border-base;
}

.item-img {
  display: inline-block;
  width: 16px;
  height: 16px;
  margin-right: 5px;
}

.icon-name {
  display: flex;
  align-items: center;
  justify-content: flex-start;
  margin-right: 10px;
}

.user-photo {
  margin-bottom: -3.09px;
  margin-left: 8px;
}

.clicked {
  background-color: #deebff !important;
}
</style>
