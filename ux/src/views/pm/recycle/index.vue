<template>
  <div class="archive-project">
    <wk-page-header
      title="回收站" />
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
          prop="deleteTime"
          label="删除时间" />
        <el-table-column
          label="操作"
          width="200">
          <template slot-scope="scope">
            <el-button
              type="primary-text"
              @click.native="recoverProject(scope.row, scope.$index)">恢复项目</el-button>
            <el-button
              type="primary-text"
              class="is-delete"
              @click.native="thoroughDeleteTask(scope.row, scope.$index)">彻底删除</el-button>
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
import {
  projectArchiveAPI,
  projectDeleteAPI
} from '@/api/pm/manage'

import WkPageHeader from '@/components/Page/WkPageHeader'

export default {
  name: 'RecycleIndex', // 项目回收站
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
     * @description: 更改每页展示数量
     * @param {*} val
     * @return {*}
     */
    handleSizeChange(val) {
      this.pageSize = val
      this.getList()
    },

    /**
     * @description: 更改当前页数
     * @param {*} val
     * @return {*}
     */
    handleCurrentChange(val) {
      this.currentPage = val
      this.getList()
    },

    /**
     * @description: 获取列表
     * @return {*}
     */
    getList() {
      this.loading = true
      archiveProjectListAPI({
        page: this.currentPage,
        limit: this.pageSize,
        setType: 3
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
     * @description: 恢复项目
     * @param {*} val
     * @param {*} index
     * @return {*}
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
        .catch(() => {})
    },

    /**
      * @description: 彻底删除项目
      * @return {*}
      */
    thoroughDeleteTask(val, index) {
      this.$confirm('确定删除?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => {
          this.loading = true
          projectDeleteAPI(val.projectId)
            .then(res => {
              this.list.splice(index, 1)
              this.$message.success('删除成功')
              this.loading = false
            })
            .catch(() => {
              this.loading = false
            })
        })
        .catch(() => {})
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

.el-button--primary-text.is-delete {
  color: $--color-danger;
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
