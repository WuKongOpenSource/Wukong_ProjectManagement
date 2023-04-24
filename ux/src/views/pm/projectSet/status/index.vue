<template>
  <div class="main">
    <xr-header>
      <template slot="label">
        状态设置
        <i
          slot="otherLabel"
          class="wk wk-icon-fill-help wk-help-tips"
          data-type="24"
          data-id="211" />
      </template>
      <el-button
        slot="ft"
        type="primary"
        @click="addStatus">新建状态</el-button>
    </xr-header>
    <div class="main-body">
      <flexbox class="content-table-header">
        <el-input
          v-model="searchInput"
          placeholder="请输入内容"
          style="width: 240px;"
          @keyup.enter.native="headerSearch"
          @blur="headerSearch">
          <el-button
            slot="suffix"
            type="icon"
            icon="wk wk-sousuo"
            @click.native="headerSearch" />
        </el-input>
      </flexbox>
      <el-table
        id="examine-table"
        v-loading="loading"
        :data="list"
        :height="tableHeight"
        :class="WKConfig.tableStyle.class"
        :stripe="WKConfig.tableStyle.stripe"
        class="main-table"
        highlight-current-row
        style="width: 100%;">
        <el-table-column
          v-for="(item, index) in fieldList"
          :key="index"
          :prop="item.prop"
          :label="item.label"
          :width="item.width"
          :class-name="item.width>60 ? 'column' : '' "
          show-overflow-tooltip>
          <template slot-scope="{row}">
            <status-tag v-if="item.prop=='statusType'" :type="row.statusType" />
            <span v-else>{{ row[item.prop] }}</span>
          </template>
        </el-table-column>

        <el-table-column
          fixed="right"
          label="操作"
          width="100">
          <template slot-scope="scope">
            <el-button
              :disabled="scope.row.label == 2"
              type="primary-text"
              style="padding: 0;"
              @click="handleClick('edit', scope.row)">编辑</el-button>
            <el-button
              :disabled="scope.row.roleType == 5"
              type="primary-text"
              style="padding: 0;"
              @click="handleClick('delete', scope.row)">删除</el-button>
          </template>
        </el-table-column>

      </el-table>
      <div class="p-contianer">
        <el-pagination
          :current-page="currentPage"
          :page-sizes="pageSizes"
          :page-size.sync="pageSize"
          :total="total"
          :pager-count="5"
          class="p-bar"
          layout="prev, pager, next, sizes, total, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange" />
      </div>
    </div>
    <status-create
      :visible.sync="createVisible"
      :action="action"
      @save-success="handleRefresh"
      @close="createVisible = false" />
  </div>
</template>

<script>

import { projectStatusListAPI, projectStatusDelAPI } from '@/api/pm/setting'

import StatusTag from '@/views/pm/project/components/StatusTag'
import StatusCreate from './components/StatusCreate'
import XrHeader from '@/components/XrHeader'

import { isEmpty } from '@/utils/types'
import Lockr from 'lockr'

export default {
  /** 系统管理 的 项目管理 */
  name: 'SystemProject',
  components: {
    StatusCreate,
    StatusTag,
    // Reminder,
    XrHeader
  },
  mixins: [],
  data() {
    return {
      loading: false, // 加载动画
      tableHeight: document.documentElement.clientHeight - 250, // 表的高度
      list: [],
      searchInput: '',

      currentPage: 1,
      pageSize: Lockr.get('crmPageSizes') || 15,
      pageSizes: [15, 30, 60, 100],
      total: 0,

      fieldList: [
        { prop: 'statusName', label: '转改名称' },
        { prop: 'statusType', label: '状态类型' },
        { prop: 'description', label: '描述' }
      ],

      action: {
        type: 'save'
      },
      createVisible: false
    }
  },
  computed: {},
  mounted() {
    var self = this
    /** 控制table的高度 */
    window.onresize = function() {
      self.tableHeight = document.documentElement.clientHeight - 180
    }

    this.getList()
  },
  methods: {
    isEmpty(val) {
      return isEmpty(val)
    },
    handleRefresh() {
      this.createVisible = false
      this.getList()
    },
    /**
     * 获取列表数据
     */
    getList() {
      this.loading = true
      const params = {
        search: this.searchInput,
        page: this.currentPage,
        limit: this.pageSize
      }
      projectStatusListAPI(params)
        .then(res => {
          this.list = res.data.list
          if (res.data.totalRow && Math.ceil(res.data.totalRow / this.pageSize) < this.currentPage && this.currentPage > 1) {
            this.currentPage = this.currentPage - 1
            this.getList()
          } else {
            this.total = res.data.totalRow
            this.loading = false
          }

          this.$nextTick(() => {
            document.querySelector('.el-table__body-wrapper').scrollTop = 1
          })
          this.loading = false
        })
        .catch(() => {
          this.loading = false
        })
    },

    /**
     * 更改每页展示数量
     * @param {*} val
     */
    handleSizeChange(val) {
      Lockr.set('crmPageSizes', val)
      this.pageSize = val
      this.getList()
    },

    /**
         * 更改当前页数
         * @param {*} val
         */
    handleCurrentChange(val) {
      this.currentPage = val
      this.getList()
    },

    /**
     *  添加权限
     */
    addStatus() {
      this.action = {
        type: 'save',
        date: null,
        id: ''
      }
      this.createVisible = true
    },

    /**
     * 编辑删除
     */
    handleClick(type, row) {
      if (type == 'edit') {
        this.action = {
          type: 'update',
          data: row,
          id: row.projectStatusId
        }
        this.createVisible = true
      } else if (type == 'delete') {
        // 启用停用
        this.$confirm('您确定要删除吗?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
          .then(() => {
            projectStatusDelAPI({
              projectStatusId: row.projectStatusId
            })
              .then(res => {
                this.getList()
                this.$message({
                  type: 'success',
                  message: '操作成功'
                })
              })
              .catch(() => {})
          })
          .catch(() => {
            this.$message({
              type: 'info',
              message: '已取消删除'
            })
          })
      }
    },
    /**
     * 头部搜索
     */
    headerSearch() {
      this.getList()
    }
  }
}
</script>

<style lang="scss" scoped>
@import "../../styles/index.scss";
@import "../../styles/table.scss";

.main-body {
  margin-top: #{$--interval-base * 2};

  .content-table-header {
    padding-bottom: 16px;
  }
}

.el-button--small {
  padding: 0;
}

.project-reminder {
  display: inline-block;
  width: auto;
  margin-left: #{$--interval-base * 3};
  vertical-align: middle;
}

.project-tag {
  height: 22px;
  padding: 0 8px;
  font-size: 13px;
  line-height: 18px;

  &.is-over {
    color: white;
    background-color: $--color-r400;
  }

  &.is-common {
    color: $--color-black;
    background-color: $--background-color-base;
  }
}
</style>
