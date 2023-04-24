<template>
  <div class="main">
    <xr-header>
      <template slot="label">
        自定义角色设置
        <reminder
          class="project-reminder"
          content="为不同场景下的项目成员所需的权限设置匹配的项目、任务列表、任务的操作权限" />
      </template>
      <el-button
        slot="ft"
        type="primary"
        @click="addJurisdiction">新建角色</el-button>
    </xr-header>
    <div class="main-body">
      <el-table
        id="examine-table"
        v-loading="loading"
        :data="list"
        :height="tableHeight"
        :class="WKConfig.tableStyle.class"
        :stripe="WKConfig.tableStyle.stripe"
        class="main-table"
        highlight-current-row
        style="width: 100%;"
        @row-click="handleRowClick">
        <el-table-column
          show-overflow-tooltip
          prop="roleName"
          width="150"
          label="角色名称" />
        <el-table-column
          show-overflow-tooltip
          prop="remark"
          label="角色描述" />
        <el-table-column
          fixed="right"
          label="操作"
          width="100">
          <template slot-scope="scope">
            <el-button
              type="primary-text"
              style="padding: 0;"
              @click="handleClick('edit', scope)">编辑</el-button>
            <el-button
              type="primary-text"
              style="padding: 0;"
              @click="handleClick('delete', scope)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <jurisdiction-create
      :show.sync="jurisdictionCreateShow"
      :action="createAction"
      @submite="getList" />
  </div>
</template>

<script>
import {
// systemRoleQueryProjectRoleListAPI,
// systemRoleDeleteWorkRoleAPI
} from '@/api/admin/project'
import {
  roleDeleteAPI,
  systemRoleByTypeAPI
} from '@/api/admin/role'
// import {
//   getProjectRoleByTypeAPI
// } from '@/api/pm/manage'

import JurisdictionCreate from './components/JurisdictionCreate'
import Reminder from '@/components/Reminder'
import XrHeader from '@/components/XrHeader'

export default {
  /** 系统管理 的 项目管理 */
  name: 'SystemProject',
  components: {
    JurisdictionCreate,
    Reminder,
    XrHeader
  },
  mixins: [],
  data() {
    return {
      loading: false, // 加载动画
      tableHeight: document.documentElement.clientHeight - 180, // 表的高度
      list: [],
      createAction: {
        type: 'save'
      },
      jurisdictionCreateShow: false
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
    /**
     * 获取列表数据
     */
    getList() {
      this.loading = true
      systemRoleByTypeAPI()
      // systemRoleQueryProjectRoleListAPI()
        .then(res => {
          this.list = res.data
          this.loading = false
        })
        .catch(() => {
          this.loading = false
        })
    },

    /**
     *  添加权限
     */
    addJurisdiction() {
      this.createAction = {
        type: 'save'
      }
      this.jurisdictionCreateShow = true
    },

    /** 列表操作 */
    /**
     * 当某一行被点击时会触发该事件
     */
    handleRowClick(row, column, event) {},

    /**
     * 编辑删除
     */
    handleClick(type, scope) {
      if (type === 'edit') {
        this.createAction = {
          type: 'update',
          data: scope.row
        }
        this.jurisdictionCreateShow = true
      } else if (type === 'delete') {
        // 启用停用
        this.$confirm('您确定要删除吗?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
          .then(() => {
            // systemRoleDeleteWorkRoleAPI
            roleDeleteAPI({
              roleId: scope.row.roleId
            })
              .then(res => {
                this.list.splice(scope.$index, 1)
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
    }
  }
}
</script>

<style lang="scss" scoped>
@import "../styles/index.scss";
@import "../styles/table.scss";

.main-body {
  margin-top: #{$--interval-base * 2};
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
</style>
