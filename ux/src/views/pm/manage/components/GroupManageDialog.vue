<template>
  <el-dialog
    ref="wkDialog"
    append-to-body
    :visible.sync="dialogVisible"
    title="管理分组"
    width="500px"
    custom-class="no-padding-dialog"
    @close="handleCancel">

    <div class="group-manage">
      <draggable
        v-model="groupList"
        v-bind="dragOptions"
        class="group-list"
        @sort="handleSort">
        <flexbox
          v-for="item in groupList"
          :key="item.label"
          class="group-list-item sortable">
          <span class="grid">⋮⋮</span>
          <flexbox-item class="name">{{ item.label }}</flexbox-item>
          <span class="desc">{{ item.num || 0 }}个项目</span>
          <i v-if="item.type !== 1 && item.type !== 2" class="el-icon-delete delete-icon" @click="deleteGroup(item)" />
        </flexbox>
        <flexbox class="group-list-item">
          <i class="el-icon-plus" />
          <el-input
            v-model.trim="groupName"
            :maxlength="20"
            placeholder="添加项目分组" />
          <el-button
            type="primary"
            size="medium"
            @click="handleAdd">添加分组</el-button>
        </flexbox>
      </draggable>
    </div>

    <div slot="footer">
      <el-button type="primary" @click="handleConfirm">保存</el-button>
      <el-button @click="handleCancel">取消</el-button>
    </div>
  </el-dialog>
</template>

<script>
import {
  groupAddAPI,
  groupDeleteAPI,
  updateGroupBatchAPI
} from '@/api/pm/manage'

import draggable from 'vuedraggable'

import ElDialogLoadingMixin from '@/mixins/ElDialogLoading'
import { objDeepCopy } from '@/utils'

export default {
  name: 'GroupManageDialog', // 管理分组弹层
  components: {
    draggable
  },
  mixins: [ElDialogLoadingMixin],
  props: {
    visible: Boolean,
    listData: {
      type: Array,
      default: () => []
    }
  },
  data() {
    return {
      loading: false,
      dialogVisible: false,
      groupName: '',
      groupList: [],
      dragOptions: {
        sort: true,
        handle: '.grid',
        draggable: '.sortable'
      }
    }
  },
  watch: {
    visible: {
      handler() {
        this.dialogVisible = this.visible
        if (this.dialogVisible) {
          // this.groupList = objDeepCopy(this.listData.slice(2, this.listData.length))
          this.groupList = objDeepCopy(this.listData)
        }
      },
      immediate: true
    },
    listData: {
      handler() {
        // this.groupList = objDeepCopy(this.listData.slice(2, this.listData.length))
        this.groupList = objDeepCopy(this.listData)
      },
      deep: true
    }
  },
  methods: {
    /**
     * @description: 移动
     * @return {*}
     */
    handleSort() {
      console.log(this.groupList.map(o => o.label))
    },

    /**
     * @description: 添加分组
     * @return {*}
     */
    handleAdd() {
      if (!this.groupName) {
        this.$message.error('分组名称不能为空')
        return
      }
      const names = this.groupList.map(o => o.label)
      if (names.includes(this.groupName)) {
        this.$message.error('分组名称不能重复')
        return
      }
      const params = {
        name: this.groupName
      }
      this.loading = true
      groupAddAPI(params)
        .then(res => {
          console.log(res, '添加分组')
          this.$emit('addGroup')
          this.loading = false
        })
        .catch(() => {
          this.loading = false
        })
      this.groupName = ''
    },

    /**
     * @description: 删除分组
     * @param {*} data
     * @return {*}
     */
    deleteGroup(data) {
      this.$confirm('确定删除?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => {
          this.loading = true
          groupDeleteAPI({ groupId: data.value })
            .then(res => {
              console.log(res)
              this.$emit('addGroup')
              this.loading = false
              this.$message({
                type: 'success',
                message: '删除成功!'
              })
            })
            .catch(() => {
              this.loading = false
            })
        })
        .catch(() => {
          this.$message({
            type: 'info',
            message: '已取消删除'
          })
        })
    },

    /**
     * @description: 保存
     * @return {*}
     */
    handleConfirm() {
      const list = []
      this.groupList.forEach((item, index) => {
        list.push({
          groupId: item.value,
          name: item.label,
          sort: index
        })
      })
      console.log('save: ', list)
      this.loading = true
      updateGroupBatchAPI(list)
        .then(res => {
          console.log(res)
          this.$emit('close')
          this.$emit('addGroup')
          this.loading = false
          this.$message.success('操作成功')
        })
        .catch(() => {
          this.loaing = false
        })
    },

    /**
     * @description: 取消
     * @return {*}
     */
    handleCancel() {
      this.dialogVisible = false
      this.$emit('close')
    }
  }
}
</script>

<style scoped lang="scss">
.group-manage {
  padding: 0 20px;

  .group-list {
    border: $--border-base;
    border-radius: $--border-radius-base;

    .group-list-item {
      width: 100%;
      height: 38px;
      padding: 0 15px;
      font-size: $--font-size-base;
      background-color: white;
      border-bottom: $--border-base;

      &:last-child {
        border: 0 none;
      }

      .grid {
        font-weight: bold;
        color: $--color-n90;
        cursor: move;
      }

      .name {
        color: $--color-black;
      }

      .desc {
        color: $--color-n200;
      }

      .delete-icon {
        display: none;
        cursor: pointer;
      }

      .el-input {
        margin-left: 2px;

        /deep/ .el-input__inner {
          border: 0 none;
        }
      }

      .el-button {
        margin-left: 6px;
      }

      &:hover {
        .desc {
          display: none;
        }

        .delete-icon {
          display: block;
        }
      }
    }
  }
}
</style>
