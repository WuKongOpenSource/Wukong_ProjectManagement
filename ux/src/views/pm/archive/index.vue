<template>
  <div class="archive-project">
    <wk-page-header
      title="归档项目统计" />
    <div
      v-loading="loading"
      class="content-body">
      <el-table
        id="crm-table"
        :data="list"
        :height="tableHeight"
        highlight-current-row
        style="width: 100%;">
        <el-table-column
          show-overflow-tooltip
          prop="name"
          label="项目名称" />
        <el-table-column
          show-overflow-tooltip
          width="200"
          prop="archiveTime"
          label="归档时间" />
        <el-table-column
          label="操作"
          width="100">
          <template slot-scope="scope">
            <el-button
              type="primary-text"
              style="padding: 0;"
              @click.native="recoverProject(scope.row, scope.$index)">恢复项目</el-button>
          </template>
        </el-table-column>

      </el-table>
      <div class="p-contianer">
        <el-pagination
          :current-page="currentPage"
          :page-sizes="pageSizes"
          :page-size.sync="pageSize"
          :total="total"
          class="p-bar"
          background
          layout="prev, pager, next, sizes, total, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange" />
      </div>
    </div>
  </div>
</template>

<script>
import { archiveProjectListAPI } from '@/api/pm/archive'
// import { workWorkUpdateAPI } from '@/api/pm/project'

import {
  projectArchiveAPI
} from '@/api/pm/manage'

import WkPageHeader from '@/components/Page/WkPageHeader'

export default {
  name: 'ArchiveIndex',
  components: {
    WkPageHeader
  },
  data() {
    return {
      loading: false,
      tableHeight: document.documentElement.clientHeight - 195,
      list: [],

      // 分页
      currentPage: 1,
      pageSize: 15,
      pageSizes: [15, 30, 45, 60],
      total: 0
    }
  },
  created() {
    window.onresize = () => {
      this.tableHeight = document.documentElement.clientHeight - 195
    }

    this.getList()
  },
  methods: {
    /**
     * 更改每页展示数量
     */
    handleSizeChange(val) {
      this.pageSize = val
      this.getList()
    },

    /**
     * 更改当前页数
     */
    handleCurrentChange(val) {
      this.currentPage = val
      this.getList()
    },

    /**
     * 获取列表
     */
    getList() {
      this.loading = true
      archiveProjectListAPI({
        page: this.currentPage,
        limit: this.pageSize,
        setType: 2
      })
        .then(res => {
          this.list = res.data.list
          this.loading = false
        })
        .catch(() => {
          this.loading = false
        })
    },

    /**
     * 恢复项目
     */
    recoverProject(val, index) {
      this.$confirm('确定恢复?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => {
          this.loading = true
          projectArchiveAPI({
            projectId: val.projectId,
            setType: 1
          })
            .then(res => {
              this.list.splice(index, 1)
              this.$message.success('恢复成功')
              this.loading = false
            })
            .catch(() => {
              this.loading = false
            })
        })
        .catch(() => {
          this.$message({
            type: 'info',
            message: '已取消'
          })
        })
    }
  }
}
</script>

<style scoped lang="scss">
.archive-project {
  position: relative;
  height: 100%;
  padding: 24px 40px;
  overflow: hidden;

  .content-body {
    position: absolute;
    top: 65px;
    right: 40px;
    bottom: 24px;
    left: 40px;
    overflow-y: auto;
  }
}

.p-contianer {
  position: relative;
  margin-top: 16px;
  text-align: right;

  .p-bar {
    display: inline-block;
    vertical-align: middle;
  }
}
</style>
