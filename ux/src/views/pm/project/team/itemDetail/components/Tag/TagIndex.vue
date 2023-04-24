<template>
  <el-popover
    v-model="tagShow"
    :placement="placement"
    :width="popoverWidth"
    trigger="click">
    <div
      v-if="tagContent == 0"
      class="tag-popover-box">
      <div class="tag-top">
        <span>选择标签</span>
        <span
          class="el-icon-close rt cursor-pointer"
          @click="tagShow = false" />
      </div>
      <el-input
        v-model="tagSearch"
        placeholder="搜索标签"
        prefix-icon="el-icon-search"
        size="small" />
      <div class="tag-content">
        <div
          v-for="(item, index) in showTagList"
          :key="index"
          class="tag-list"
          @click="tagSelectClick(item, index)">
          <i
            :style="{ 'color': item.color}"
            class="wukong wukong-black-label" />
          <span class="item-label">{{ item.name }}</span>
          <span
            v-if="item.check"
            class="el-icon-check rt" />
        </div>
      </div>
      <div class="tag-footer">
        <div>
          <el-button
            class="footer-row"
            type="text"
            @click="createTagClick">+ 创建标签</el-button>
        </div>
        <div>
          <el-button
            class="footer-row"
            type="text"
            @click="managementTag">+ 管理标签</el-button>
        </div>
      </div>
    </div>
    <!-- 新建标签页 -新建 - 编辑 -->
    <new-tag
      v-else-if="tagContent == 1 || tagContent == 3"
      :new-tag-title="newTagTitle"
      :new-tag-input="newTagInput"
      :bg-color-props="bgColorProps"
      @changeColor="changeColor"
      @close="tagClose"
      @tagCreateSubmit="tagCreateSubmit"
      @tagCancel="tagCancel"
      @back="back" />
    <!-- 标签管理 -->
    <editTag
      v-else-if="tagContent == 2"
      :edit-tag-list="tagList"
      @back="back"
      @close="tagClose"
      @editBtn="editTagClick"
      @deleteBtn="deleteBtn" />
    <span
      slot="reference"
      @click="referenceFun">
      <slot name="editIndex" />
    </span>
  </el-popover>
</template>

<script>
import {
  setTaskLabelAPI,
  addLabelAPI,
  delLabelAPI,
  queryLabelAPI,
  updateLabelAPI
} from '@/api/pm/projectTask'

import NewTag from './NewTag'
import EditTag from './EditTag'

export default {
  components: {
    NewTag,
    EditTag
  },
  props: {
    taskData: {
      type: Object,
      default: () => {
        return {}
      }
    },
    placement: String
  },
  data() {
    return {
      // 标签弹出框
      tagShow: false,
      // 显示tag页面
      tagContent: 0,
      // 标签筛选框
      tagSearch: '',
      // 标签数据
      tagList: [],
      // 新增、编辑标签标题
      newTagType: 'create', // create update
      // 创建-编辑标签的输入框
      newTagInput: '',
      // 标签颜色
      bgColorProps: '',
      // 标签编辑ID
      editTagId: ''
    }
  },
  computed: {
    showTagList() {
      const showList = []
      for (let index = 0; index < this.tagList.length; index++) {
        const element = this.tagList[index]
        if (element.name.indexOf(this.tagSearch) > -1) {
          showList.push(element)
        }
      }
      return showList
    },

    // 窗口宽度
    popoverWidth() {
      // 1 新建编辑 2 管理 0 列表
      if (this.tagContent == 1) {
        return 330
      } else if (this.tagContent == 2) {
        return 400
      }

      return 220
    },

    // 新建编辑弹框title
    newTagTitle() {
      return this.newTagType === 'create' ? '创建新标签' : '编辑标签'
    }

    // /**
    //  * 项目ID 说明是项目
    //  */
    // workId() {
    //   return this.taskData.workId
    // }
  },
  watch: {},
  mounted() {},
  methods: {
    /**
     * @description: 创建新标签
     * @return {*}
     */
    createTagClick() {
      this.newTagType = 'create'
      this.newTagInput = ''
      this.tagContent = 1
    },

    /**
     * @description: 标签管理 -- 编辑
     * @param {*} val
     * @return {*}
     */
    editTagClick(val) {
      this.editTagId = val.labelId
      this.newTagType = 'update'
      this.tagContent = 1
      this.bgColorProps = val.color
      this.newTagInput = val.name
    },

    /**
     * @description: 标签管理弹出框
     * @return {*}
     */
    managementTag() {
      this.tagContent = 2
    },

    /**
     * @description: 选择标签
     * @param {*} value
     * @param {*} index
     * @return {*}
     */
    tagSelectClick(value, index) {
      if (this.$listeners.change) {
        value.check = !value.check
        this.$emit('change', this.tagList.filter(item => item.check))
      } else {
        this.tagList[index].check = !value.check
        const labelIds = this.tagList
          .filter(item => {
            return item.check
          })

        setTaskLabelAPI({
          taskId: this.taskData.taskId,
          ids: labelIds
            .map(item => {
              return item.labelId
            })
            .join(',')
        }).then(res => {
          if (!value.check) {
            this.updateDetailList(value, 'delete')
          } else {
            const labelList = []
            this.tagList.forEach(item => {
              if (item.check) {
                item.labelName = item.name
                labelList.push(item)
              }
            })
            this.taskData.labelTaskList = labelList
          }
        }).catch(() => {
          value.check = true
        })
      }
    },

    /**
     * @description: 更新/删除任务详情数据
     * @param {*} value
     * @param {*} type
     * @return {*}
     */
    updateDetailList(value, type) {
      if (this.taskData && this.taskData.labelTaskList) {
        let changeIndex = -1
        for (let index = 0; index < this.taskData.labelTaskList.length; index++) {
          const element = this.taskData.labelTaskList[index]
          if (element.labelId == value.labelId) {
            changeIndex = index
            break
          }
        }
        if (changeIndex >= 0) {
          if (type == 'delete') {
            this.taskData.labelTaskList.splice(changeIndex, 1)
          } else {
            this.taskData.labelTaskList.splice(changeIndex, 1, value)
          }
        }
      }
    },

    /**
     * @description: 标签点击变色
     * @param {*} val
     * @return {*}
     */
    changeColor(val) {
      this.bgColorProps = val
    },

    /**
     * @description: 标签管理 -- 关闭按钮
     * @return {*}
     */
    tagClose() {
      this.tagShow = false
    },

    /**
     * @description: 创建新标签 -- 提交
     * @param {*} val
     * @param {*} color
     * @return {*}
     */
    tagCreateSubmit(val, color) {
      if (this.newTagType == 'create') {
        addLabelAPI({
          name: val,
          color: color
        }).then(res => {
          // 刷新标签列表
          this.getTagList()
          // 关闭标签页
          this.back()
          this.$message.success('创建成功')
        })
      } else {
        updateLabelAPI({
          name: val,
          labelId: this.editTagId,
          color: color
        }).then(res => {
          // eslint-disable-next-line
          for (const item of this.tagList) {
            if (item.labelId == this.editTagId) {
              item.name = val
              item.color = color
              item.labelName = item.name
              this.updateDetailList(item, 'update')
            }
          }
          this.tagContent = 2
        })
      }
    },

    /**
     * @description: 创建新标签 -- 取消
     * @return {*}
     */
    tagCancel() {
      if (this.newTagType == 'create') {
        // 关闭标签页
        this.tagClose()
        this.$message.info('已取消创建')
      } else {
        this.tagContent = 2
      }
    },

    /**
     * @description: 标签管理 ——— 返回上一页
     * @return {*}
     */
    back() {
      if (this.tagContent == 1) {
        this.tagContent = this.newTagType == 'create' ? 0 : 2
      } else if (this.tagContent == 2) {
        this.tagContent = 0
      }
    },

    /**
     * @description: 标签管理 ——— 删除按钮
     * @param {*} val
     * @return {*}
     */
    deleteBtn(val) {
      this.$confirm('确定删除?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
        customClass: 'is-particulars'
      })
        .then(() => {
          delLabelAPI({
            labelId: val.labelId
          }).then(res => {
            // eslint-disable-next-line
            for (const i in this.tagList) {
              if (this.tagList[i].labelId == val.labelId) {
                this.tagList.splice(i, 1)
                break
              }
            }
            this.$message({
              type: 'success',
              message: '删除成功!'
            })
            this.tagShow = true
          }).catch(() => {
            // 防止窗口关闭
            this.tagShow = true
          })
        })
        .catch(() => {
          this.$message({
            type: 'info',
            message: '已取消删除'
          })
          // 防止窗口关闭
          this.tagShow = true
        })
    },

    /**
     * @description: 获取列表数据
     * @return {*}
     */
    getTagList() {
      // 标签列表
      queryLabelAPI({ name: '' }).then(res => {
        const dataList = res.data || []
        // tagList 有数据后，后期以 tagList 的check 为准
        let selectIds = []
        if (this.tagList.length > 0) {
          selectIds = this.tagList.filter(item => item.check).map(item => item.labelId)
        } else if (this.taskData && this.taskData.labelTaskList) {
          selectIds = this.taskData.labelTaskList.map(item => item.labelId)
        }
        // eslint-disable-next-line
        for (const item of dataList) {
          item.check = selectIds.includes(item.labelId)
        }
        // 标签管理数据
        this.tagList = dataList
      }).catch(() => {})
    },

    /**
     * @description: 初始化刷新
     * @return {*}
     */
    referenceFun() {
      this.tagContent = 0
      if (this.tagList && !this.tagList.length) {
        this.getTagList()
      }
    }
  }
}
</script>

<style scoped lang="scss">
// 标签按钮
.tag-popover-box {
  margin: 0 -12px;

  .tag-top {
    margin-bottom: 10px;

    .el-icon-close {
      margin-right: 0;
    }
  }

  .el-input /deep/ .el-input__inner {
    border-radius: 15px;
  }

  .tag-content {
    height: 196px;
    margin-top: 10px;
    overflow: auto;
    border-bottom: $--border-base;

    .tag-list {
      padding: 10px;
      cursor: pointer;

      .el-icon-check {
        margin-right: 0;
      }
    }

    .tag-list:hover {
      background: #f7f8fa;
    }
  }

  .tag-footer {
    line-height: initial;
  }
}

.tag-top,
.tag-content,
.tag-footer {
  padding: 0 12px;

  .footer-row:hover {
    color: $--color-primary;
  }
}

.tag-popover-box > .el-input {
  width: auto;
  margin: 0 12px;
}

.cursor-pointer {
  cursor: pointer;
}

// 调整对勾和关闭按钮位置
.rt {
  margin-top: 5px;
}
</style>
