<template>
  <el-popover
    v-model="tagShow"
    :placement="placement"
    :width="popoverWidth"
    :popper-class="noPadding ? 'no-padding-popover' : ''"
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
import NewTag from './NewTag'
import EditTag from './EditTag'
import {
  taskDeleteLabelAPI
} from '@/api/oa/task'
import {
  workTasklableIndexAPI,
  workTasklableDeleteAPI,
  workTasklableSaveAPI,
  workTasklableSetAPI
} from '@/api/pm/tag'
import { workTaskLabelSetAPI } from '@/api/pm/projectTask'

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
    },

    // 新建编辑无padding
    noPadding() {
      return [1, 2, 3].includes(this.tagContent)
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
     * 创建新标签
     */
    createTagClick() {
      this.newTagType = 'create'
      this.newTagInput = ''
      this.tagContent = 1
    },

    /**
     * 标签管理 -- 编辑
     */
    editTagClick(val) {
      this.editTagId = val.labelId
      this.newTagType = 'update'
      this.tagContent = 1
      this.bgColorProps = val.color
      this.newTagInput = val.name
    },

    /**
     * 标签管理弹出框
     */
    managementTag() {
      this.tagContent = 2
    },

    /**
     * 选择标签
     */
    tagSelectClick(value, index) {
      if (this.$listeners.change) {
        value.check = !value.check
        this.$emit('change', this.tagList.filter(item => item.check))
      } else {
        // 标签点击关联页面
        if (value.check) {
          taskDeleteLabelAPI({
            taskId: this.taskData.taskId,
            labelId: value.labelId
          }).then(res => {
            value.check = false
            this.updateDetailList(value, 'delete')
          }).catch(() => {
            value.check = true
          })
        } else {
          const labelIds = this.tagList.filter(item => {
            return item.check
          }).concat(value)

          workTaskLabelSetAPI({
            taskId: this.taskData.taskId,
            labelId: labelIds
              .map(item => {
                return item.labelId
              })
              .join(',')
          }).then(res => {
            value.check = true
            const labelList = []
            this.tagList.forEach(item => {
              if (item.check) {
                item.labelName = item.name
                labelList.push(item)
              }
            })
            this.taskData.labelList = labelList
          }).catch(() => {
            value.check = false
          })
        }
      }
    },

    /**
     * 更新/删除任务详情数据
     */
    updateDetailList(value, type) {
      if (this.taskData && this.taskData.labelList) {
        let changeIndex = -1
        for (let index = 0; index < this.taskData.labelList.length; index++) {
          const element = this.taskData.labelList[index]
          if (element.labelId == value.labelId) {
            changeIndex = index
            break
          }
        }
        if (changeIndex >= 0) {
          if (type == 'delete') {
            this.taskData.labelList.splice(changeIndex, 1)
          } else {
            this.taskData.labelList.splice(changeIndex, 1, value)
          }
        }
      }
    },

    // 标签点击变色
    changeColor(val) {
      this.bgColorProps = val
    },

    /**
     * 标签管理 -- 关闭按钮
     */
    tagClose() {
      this.tagShow = false
    },

    /**
     * 创建新标签 -- 提交
     */
    tagCreateSubmit(val, color) {
      if (this.newTagType == 'create') {
        workTasklableSaveAPI({
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
        workTasklableSetAPI({
          name: val,
          labelId: this.editTagId,
          color: color
        }).then(res => {
          // eslint-disable-next-line no-unused-vars
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
     * 创建新标签 -- 取消
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
     * 标签管理 ——— 返回上一页
     */
    back() {
      if (this.tagContent == 1) {
        this.tagContent = this.newTagType == 'create' ? 0 : 2
      } else if (this.tagContent == 2) {
        this.tagContent = 0
      }
    },

    /**
     * 标签管理 ——— 删除按钮
     */
    deleteBtn(val) {
      this.$confirm('确定删除?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
        customClass: 'is-particulars'
      })
        .then(() => {
          workTasklableDeleteAPI({
            labelId: val.labelId
          }).then(res => {
            // eslint-disable-next-line no-unused-vars
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
     * 获取列表数据
     */
    getTagList() {
      // 标签列表
      workTasklableIndexAPI().then(res => {
        const dataList = res.data || []
        // tagList 有数据后，后期以 tagList 的check 为准
        let selectIds = []
        if (this.tagList.length > 0) {
          selectIds = this.tagList.filter(item => item.check).map(item => item.labelId)
        } else if (this.taskData && this.taskData.labelList) {
          selectIds = this.taskData.labelList.map(item => item.labelId)
        }

        // eslint-disable-next-line no-unused-vars
        for (const item of dataList) {
          item.check = selectIds.includes(item.labelId)
        }
        // 标签管理数据
        this.tagList = dataList
      }).catch(() => {})
    },

    /**
     * 初始化刷新
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
