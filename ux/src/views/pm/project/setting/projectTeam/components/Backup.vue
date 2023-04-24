<template>
  <div class="backup">
    <div class="card">
      <flexbox
        class="favourite-list">
        <div class="favourite-list-item">
          <flexbox class="content">
            <flexbox
              align="center"
              justify="center"
              class="icon-box">
              <i class="wk wk-address-book" />
            </flexbox>
            <div class="info">
              <div class="name text-one-line">{{ title.btitle }}</div>
              <div class="desc text-one-line">{{ title.stitle }}</div>
            </div>
          </flexbox>
        </div>
      </flexbox>
    </div>
    <div class="workflow">
      <div class="title">事项状态</div>
      <div class="container">
        <div v-loading="loading" class="container-left">
          <draggable
            :list="useStatusList"
            @end="moveEndTask($event, useStatusList)">
            <div
              v-for="(item, index) in useStatusList"
              :key="index"
              class="container-item">
              <div>
                <span style=" margin-right: 8px;font-weight: bold; cursor: move;">⋮⋮</span>
                <status-tag
                  style="width: 150px;"
                  :type="item.statusType"
                  :status-name="item.statusName" />
                <span style="margin-left: 50px;">{{ initStatus(item) }}</span>
              </div>
              <el-dropdown
                trigger="click"
                placement="bottom-start"
                @command="handleCommand($event, item)">
                <el-button
                  type="text"
                  class="dropdown-btn"
                  icon="el-icon-more" />
                <el-dropdown-menu slot="dropdown">
                  <el-dropdown-item
                    :disabled="item.initStatus == 1"
                    command="initial">设为初始状态</el-dropdown-item>
                  <el-dropdown-item
                    v-if="item.sysType == 2"
                    :disabled="item.initStatus == 1"
                    command="delete">移除</el-dropdown-item>
                </el-dropdown-menu>
              </el-dropdown>
            </div>
          </draggable>
        </div>
      </div>
      <el-button type="text" icon="el-icon-plus" class="addbutton" @click="addClick">添加状态</el-button>
    </div>

    <add-status
      :visible.sync="createVisible"
      :action="action"
      @save-success="handleRefresh"
      @close="createVisible = false" />
  </div>
</template>

<script>
import {
  projectEventStatusListAPI,
  projectEventStatusDelAPI,
  setInitStatusAPI,
  statusUpdateSortingAPI
  // projectEventStatusInitAPI
} from '@/api/pm/setting'

import draggable from 'vuedraggable'
import AddStatus from './AddStatus'
import StatusTag from '@/views/pm/project/components/StatusTag'

export default {
  name: 'Backup',
  components: {
    draggable,
    AddStatus,
    StatusTag
  },
  props: {
    eventId: [String, Number],
    type: [String, Number]
  },
  data() {
    return {
      loading: false,
      useStatusList: [],
      allStatusList: [],
      createVisible: false,
      action: {
        type: 'save',
        id: this.eventId
      }
    }
  },
  computed: {
    title() {
      const currentTitle = [
        { btitle: '需求', stitle: '敏捷框架中最小的工作单元，是从用户角度描述软件如何为其带来特定的价值。' },
        { btitle: '任务', stitle: '任务是指为实现某个目标或需求所进行的具体活动。' },
        { btitle: '缺陷', stitle: '缺陷是指软件不符合最初定义的业务需求的现象，缺陷管理用于跟踪这些问题和错误。' },
        { btitle: '子工作项', stitle: '在敏捷模式下，将一个事项拆分成更小的块。' }
      ][this.type - 1]
      return currentTitle
    }
  },
  watch: {},
  created() {
    this.getList()
  },
  mounted() {

  },
  methods: {
    getList() {
      this.loading = true
      const params = {
        projectId: this.$route.params.setting,
        eventId: this.eventId
      }
      projectEventStatusListAPI(params).then(res => {
        const list = res.data || []
        this.allStatusList = list
        const hasAdd = list.some(item => [0, 2].includes(item.useStatus))
        this.$emit('show-btn', hasAdd)
        this.useStatusList = list.filter(item => [0, 1].includes(item.useStatus))
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    /**
     * @description: 拖拽结束
     * @return {*}
     */
    moveEndTask(evt, list) {
      console.log(evt, list)
      console.log('dragend----', arguments)
      const statusList = []
      list.forEach(item => {
        statusList.push(item.id)
      })
      this.loading = true
      statusUpdateSortingAPI(statusList)
        .then(res => {
          this.loading = false
          this.$message.success('操作成功')
          this.updateData()
        })
        .catch(() => {
          this.loading = false
        })
      console.log(statusList)
    },
    handleRefresh() {
      this.createVisible = false
      this.updateData()
    },
    updateData() {
      this.getList()
    },
    addClick() {
      this.createVisible = true
    },
    initStatus(row) {
      if (row.initStatus == 1) {
        return '是初始状态'
      }
      return '非初始状态'
    },
    handleCommand(command, row) {
      if (command == 'delete') {
        this.$confirm('确定移除?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
          .then(() => {
            const params = {
              eventStatusId: row.id
            }
            projectEventStatusDelAPI(params).then(res => {
              this.$message.success('操作成功')
              this.updateData()
            }).catch(() => {

            })
          })
          .catch(() => {})
      } else if (command == 'initial') {
        const params = {
          eventId: this.eventId,
          projectStatusId: row.id
        }
        setInitStatusAPI(params).then(res => {
          this.$message.success('操作成功')
          this.updateData()
        }).catch(() => {

        })
      }
    }
  }
}
</script>
<style scoped lang='scss'>
.backup {
  .card {
    margin: 8px -7px 0;

    .favourite-list {
      .favourite-list-item {
        position: relative;
        display: flex;
        align-items: center;
        justify-content: space-between;
        width: 100%;
        padding: 24px;
        margin: 8px;
        background-color: white;
        border-radius: $--border-radius-base;
        box-shadow: 0 1px 4px 0 rgba(122, 134, 154, 0.5);

        .content {
          flex: 1;
          width: 100%;
          overflow: hidden;

          .icon-box {
            flex-shrink: 0;
            width: 50px;
            height: 50px;
            margin-right: 16px;
            background-color: $--color-n30;
            border-radius: $--border-radius-base;

            .wk {
              font-size: 32px;
              color: $--color-primary;
            }
          }

          .info {
            overflow: hidden;

            .name {
              font-size: $--font-size-large;
              font-weight: bold;
              color: $--color-black;
            }

            .desc {
              margin-top: 8px;
              font-size: $--font-size-base;
              color: $--color-text-regular;
              white-space: normal;
            }
          }
        }
      }
    }
  }

  .workflow {
    .title {
      margin: 16px 0;
      font-size: $--font-size-large;
      font-weight: bold;
      color: $--color-black;
    }

    .addbutton {
      font-size: $--font-size-large;
      color: $--color-text-regular;
    }

    .container {
      display: flex;
      margin-bottom: 10px;

      .container-left {
        border: 1px solid $--border-color-base;

        .container-item {
          display: flex;
          align-items: center;
          justify-content: space-between;
          width: 550px;
          height: 52px;
          padding: 0 16px;
          background-color: $--form-field-default-bg-color;
          border-bottom: 1px solid $--border-color-base;

          .dropdown-btn {
            padding: 0;
          }
        }

        &:last-child {
          border-bottom: none !important;
        }
      }
    }
  }
}
</style>
