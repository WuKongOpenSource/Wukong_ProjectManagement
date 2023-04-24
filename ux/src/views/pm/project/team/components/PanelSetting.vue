<template>
  <el-dialog
    v-loading="loading"
    :append-to-body="true"
    :close-on-click-modal="false"
    title="看板设置"
    :visible="visible"
    width="960px"
    custom-class="no-padding-dialog"
    @close="close">
    <div class="main">
      <div class="describe">将不同状态下的事项放在特定看板列中</div>
      <div class="card">
        <task-board
          ref="board"
          :list="list"
          :status-list="statusList"
          show-type="board" />
      </div>
      <div slot="footer" class="btn-wrap">
        <el-button
          v-debounce="handleConfirm"
          type="primary">保存</el-button>
        <!-- <el-button @click.native="close">恢复默认</el-button> -->
        <el-button @click.native="close">取消</el-button>
      </div>
    </div>
  </el-dialog>
</template>

<script>
import {
  queryViewBoardSetListAPI,
  queryViewBoardSetSaveAPI,
  queryNoAddStatusList
} from '@/api/pm/projectTask'

import TaskBoard from './TaskBoard'

import { itemTypeMap } from '@/views/pm/data'

export default {
  name: 'PanelSetting',
  components: {
    TaskBoard
  },
  props: {
    visible: {
      type: Boolean,
      required: true,
      default: false
    },
    itemType: String
  },
  data() {
    return {
      loading: false,
      list: [],
      statusList: [],
      schemeRelationId: ''
    }
  },
  mounted() {
    this.getList(this.$route.params.id)
    this.getStatusList(this.$route.params.id)
  },
  methods: {
    /**
     * @description: 列获取
     * @param {*} id
     * @return {*}
     */
    getList(id) {
      queryViewBoardSetListAPI({
        projectId: id,
        taskType: itemTypeMap[this.itemType]
      }).then(res => {
        this.loading = false
        this.list = res.data.map(item => {
          item.renameShow = false
          return item
        })
      }).catch(() => {
        this.loading = false
      })
    },

    /**
     * @description: 事项状态
     * @param {*} id
     * @return {*}
     */
    getStatusList(id) {
      queryNoAddStatusList({
        projectId: id,
        taskType: itemTypeMap[this.itemType]
      }).then(res => {
        this.statusList = res.data || []
      }).catch(() => {

      })
    },

    /**
     * @description: 保存
     * @return {*}
     */
    handleConfirm() {
      const list = this.list || []
      const isEmptyStatusList = list.some(item => item.statusList.length == 0)
      if (isEmptyStatusList) {
        return this.$message.error('看板列表中必须存在状态')
      }

      const submitList = []
      list.forEach(element => {
        const sitem = {
          boardName: element.boardName,
          boardStatusBOList: []
        }
        element.statusList.forEach(item => {
          sitem.boardStatusBOList.push({
            statusId: item.id || item.statusId
          })
        })
        submitList.push(sitem)
      })

      if (!submitList.length) {
        return this.$message.error('看板不能为空')
      }
      const params = {
        boardBOS: submitList,
        projectId: this.$route.params.id,
        taskType: itemTypeMap[this.itemType]
      }
      this.loading = true
      queryViewBoardSetSaveAPI(params).then(res => {
        this.$emit('update')
        this.$message.success('操作成功')
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    close() {
      this.$emit('update:visible', false)
    }
  }
}
</script>
<style scoped lang='scss'>
.main {
  padding: 0 20px 20px;

  .describe {
    margin-bottom: 20px;
    color: $--color-text-secondary;

    // font-size: $--font-size-large;
    // position: absolute;
    // left: 20px;
    // top: 50px;
  }

  .card {
    position: relative;
    height: calc(100% - 90px);
    overflow-y: auto;
  }

  .btn-wrap {
    display: flex;
    align-items: center;
    justify-content: flex-end;
    margin-top: 24px;
  }
}
</style>
