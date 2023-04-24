<template>
  <div class="project-collaboration">
    <el-tabs v-model="activeName" @tab-click="handleClick">
      <el-tab-pane v-if="!backupShow" disabled>
        <template slot="label">
          项目协同
          <span class="divider" />
        </template>
      </el-tab-pane>
      <el-tab-pane
        v-if="!backupShow"
        label="协作配置"
        name="configuration">
        <configuration @backup="backupFn" />
      </el-tab-pane>
      <el-tab-pane v-else label="" name="backup">
        <span class="divider" />
        <backup
          ref="back"
          :event-id="eventId"
          :type="type"
          @show-btn="handleShowBtn" />
      </el-tab-pane>
    </el-tabs>
    <el-button v-if="backupShow" class="return" type="text" icon="wk wk-icon-circle-left" @click="backClick">协作配置</el-button>
    <div class="operate">
      <i v-if="showBtn" class="el-icon-warning warning">正在编辑配置方案</i>
      <el-button v-if="showBtn" type="primary-text" style="margin: 0 8px;" @click="handleDropEdit">
        放弃编辑
      </el-button>
      <el-button v-if="showBtn" type="primary" @click="handleUseStatus">
        应用方案
      </el-button>
    </div>

    <!-- 状态迁移 -->
    <map-status
      v-if="showStatusMove"
      :visible.sync="showStatusMove"
      :event-id="eventId"
      :all-list="allList"
      @save-success="handleSuccess"
      @close="showStatusMove = false" />
  </div>
</template>

<script>
import {
  moveStatusAPI,
  dropEditStatusAPI
} from '@/api/pm/setting'

import Configuration from './components/Configuration'
import Backup from './components/Backup'
import MapStatus from './components/MapStatus'

export default {
  name: 'ProjectTeam',
  components: {
    Configuration,
    Backup,
    MapStatus
  },
  data() {
    return {
      activeName: 'configuration',
      backupShow: false,
      rowData: null,

      eventId: '',
      type: '',

      useStatusList: [],
      hasAdd: false,

      showStatusMove: false,
      allList: []
    }
  },
  computed: {
    showBtn() {
      if (this.hasAdd && this.backupShow) {
        return true
      }
      return false
    }
  },
  created() {

  },
  methods: {
    handleClick() {},
    backupFn(data) {
      this.rowData = data
      this.eventId = data.id
      this.type = data.type
      this.backupShow = true
      this.activeName = 'backup'
    },
    backClick() {
      this.backupShow = false
      this.activeName = 'configuration'
    },

    /**
     * 是否显示应用按钮
     */
    handleShowBtn(data) {
      this.hasAdd = data
    },

    handleSuccess() {
      this.$refs.back.getList()
    },

    /**
     * 应用状态
     */
    handleUseStatus() {
      this.allList = this.$refs.back.allStatusList
      const needMapList = this.allList.filter(item => item.useStatus == 2)
      const params = {
        eventId: this.eventId,
        sourceStatus: [],
        transferStatus: []
      }

      if (!needMapList.length) {
        this.$confirm('确定应用方案？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          moveStatusAPI(params).then(res => {
            this.$message.success('应用成功')
            this.$refs.back.getList()
          }).catch(() => {

          })
        }).catch(() => {})
      } else {
        this.showStatusMove = true
      }
    },

    /**
     * 放弃编辑
     */
    handleDropEdit() {
      this.$confirm('确定放弃编辑？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        dropEditStatusAPI({ eventId: this.eventId }).then(() => {
          this.$message.success('操作成功')
          this.$refs.back.getList()
        })
      }).catch(() => {})
    }
  }
}
</script>
<style lang='scss' scoped>
@import "@/views/layout/components/style.scss";

.project-collaboration {
  position: relative;
  width: 100%;
  padding: 24px 40px 0;

  /deep/ .el-tabs__item.is-disabled {
    font-size: 18px;
    font-weight: bold;
    color: $--color-black;
  }

  /deep/ .el-tabs {
    .el-tabs__item {
      padding: 0;
      padding-right: 48px;
    }

    .is-active {
      font-size: 16px;
      font-weight: 700;
      color: $--color-primary;
    }

    .el-tabs__nav {
      z-index: 0;
    }
  }

  .return {
    position: absolute;
    top: 28px;
    left: 40px;
  }

  .operate {
    position: absolute;
    top: 28px;
    right: 40px;

    .warning {
      color: $--color-y400;
    }
  }
}

.divider {
  position: absolute;
  top: 13px;
  right: 0;
  display: inline-block;
  width: 1px;
  height: 16px;
  margin-right: 24px;
  background-color: #dfe1e6;
}
</style>
