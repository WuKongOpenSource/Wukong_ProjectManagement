<template>
  <div>
    <div class="main-head">
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
        <span style="margin: 0 15px;">角色</span>
        <el-select v-model="value" placeholder="请选择" clearable @change="selectList">
          <el-option
            v-for="item in groupOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value" />
        </el-select>
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
        style="width: 100%;"
        @row-click="handleRowClick">
        <el-table-column
          show-overflow-tooltip
          prop="realname"
          label="名称">
          <template slot-scope="{ row }">
            <flexbox
              v-if="!isEmpty(row.userId)"
              align="center"
              justify="flex-start">
              <xr-avatar
                :id="row.userId"
                :src="row.img"
                :name="row.realname"
                :size="24" />
              <div style="padding-left: 10px;">{{ row.realname }}</div>
            </flexbox>
          </template>
        </el-table-column>
        <el-table-column
          prop="group"
          label="所属角色"
          :formatter="formatter" />
        <el-table-column
          fixed="right"
          label="操作"
          width="300">
          <template slot-scope="scope">
            <el-button
              type="primary-text"
              style="padding: 0;"
              @click="handleClick('check', scope)">查看权限</el-button>
            <el-button
              v-if="manageAuth && editRoleAuth && scope.row.isPmCreater !== 1"
              type="primary-text"
              style="padding: 0;"
              @click="handleClick('associate', scope)">编辑角色</el-button>
            <el-button
              v-if="scope.row.userId == userInfo.userId"
              type="primary-text"
              style="padding: 0;"
              @click="handleClick('out', scope)">退出项目</el-button>
            <el-button
              v-if="scope.row.userId != userInfo.userId && remove && manageAuth"
              type="primary-text"
              style="padding: 0;"
              @click="handleClick('remove', scope)">移除项目</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <!-- 角色编辑 -->
    <relation-dialog
      v-if="editRoleDialogShow"
      :selection-list="selectionList"
      :visible.sync="editRoleDialogShow"
      :group-list="groupList"
      :project-id="projectId"
      :user-id="userId"
      @change="getUserList"
    />
    <!-- 查看成员权限 -->
    <check-role-dialog
      title="成员权限"
      type-name="成员"
      :show="groupAuthShow"
      :name="title"
      :rule-list="ruleList"
      @close="close" />
  </div>
</template>

<script>
import {
  queryProjectUserListAPI,
  deleteProjectRolesAPI,
  getAllRoleMenuAPI
} from '@/api/pm/manage'

import CheckRoleDialog from './CheckRoleDialog'
import RelationDialog from './RelationDialog' // 关联角色弹层

import { isEmpty } from '@/utils/types'
import { mapGetters } from 'vuex'
import merge from '@/utils/merge'

export default {
  name: 'Member', // 项目设置成员
  components: {
    RelationDialog,
    CheckRoleDialog
  },
  props: {
    groupList: {
      type: Array,
      default: () => {
        return []
      }
    },
    groupOptions: {
      type: Array,
      default: () => {
        return []
      }
    },
    projectId: {
      type: [String, Number]
    }
  },
  data() {
    return {
      searchInput: '',
      options: [],
      value: '',
      loading: false, // 加载动画
      tableHeight: document.documentElement.clientHeight - 180 - 48, // 表的高度
      list: [],
      dataValue: [],
      selectionList: [], // 批量勾选数据
      editRoleDialogShow: false,
      userId: '',
      groupAuthShow: false,
      title: '',
      ruleList: []
    }
  },
  computed: {
    config() {
      return merge({
        value: 'userId',
        label: 'realname',
        showDisableUser: false,
        disableUserList: null, // 用于单列固定数据的展示 会使 showUser false
        disableUserLabel: '停用员工'
      }, this.props || {})
    },
    ...mapGetters([
      'userInfo'
    ]),
    // 移除权限
    remove() {
      return this.list.find(item => item.userId == this.userInfo.userId)?.adminRoles.filter(val => val.label == 1).length
    },
    // 成员管理权限
    manageAuth() {
      return this.$auth('set.memberManage', 'PM')
    },
    // 成员权限配置权限
    editRoleAuth() {
      return this.$auth('set.memberPermissionsConfig', 'PM')
    }
  },
  watch: {},
  created() {

  },
  mounted() {
    var self = this
    /** 控制table的高度 */
    window.onresize = () => {
      self.tableHeight = document.documentElement.clientHeight - 180
    }
    this.getList()
  },
  methods: {
    isEmpty(val) {
      return isEmpty(val)
    },

    /**
     * @description: 关闭弹层
     * @return {*}
     */
    close() {
      this.groupAuthShow = false
    },

    /**
     * @description: 根据角色筛选列表
     * @return {*}
     */
    selectList() {
      this.getList()
    },

    /**
     * @description: 编辑角色
     * @return {*}
     */
    getUserList() {
      this.getList()
      this.editRoleDialogShow = false
    },

    /**
     * @description: 获取列表数据
     * @return {*}
     */
    getList() {
      this.loading = true
      queryProjectUserListAPI({
        projectId: this.projectId,
        userName: this.searchInput,
        roleId: this.value
      })
        .then(res => {
          this.list = res.data
          this.loading = false
        })
        .catch(() => {
          this.loading = false
        })
    },

    /**
     * @description: 格式化角色字段
     * @param {*} row
     * @param {*} column
     * @return {*}
     */
    formatter(row, column) {
      let str = ''
      row.adminRoles.forEach(item => {
        str = str + item.roleName + '，'
      })
      return str.substr(0, str.length - 1)
      // return row.address
    },

    /**
     * @description: 头部搜索
     * @return {*}
     */
    headerSearch() {
      this.refreshUserList()
    },

    /**
     * @description: 刷新列表
     * @return {*}
     */
    refreshUserList() {
      this.getList()
    },

    /**
     * @description: 当某一行被点击时会触发该事件
     * @param {*} row
     * @param {*} column
     * @param {*} event
     * @return {*}
     */
    handleRowClick(row, column, event) {},
    /**
     * @description: 操作
     * @param {*} type
     * @param {*} scope
     * @return {*}
     */

    /**
     * @description: 去重
     * @param {*} arr
     * @return {*}
     */
    unlink(arr) {
      for (var i = 0; i < arr.length; i++) { // 首次遍历数组
        for (var j = i + 1; j < arr.length; j++) { // 再次遍历数组
          if (arr[i] == arr[j]) { // 判断连个值是否相等
            arr.splice(j, 1) // 相等删除后者
            j--
          }
        }
      }
      return arr
    },

    /**
     * @description: 更多操作
     * @param {*} type
     * @param {*} scope
     * @return {*}
     */
    handleClick(type, scope) {
      if (type === 'check') {
        console.log('查看权限')
        getAllRoleMenuAPI({ projectId: this.projectId, userId: scope.row.userId })
          .then(res => {
            this.ruleList = this.unlink(res.data)
            this.title = scope.row.realname
            this.groupAuthShow = true
          })
      } else if (type === 'associate') {
        console.log('关联')
        this.userId = scope.row.userId
        this.selectionList = []
        scope.row.adminRoles.forEach(item => {
          this.selectionList.push(item.roleId)
        })
        this.editRoleDialogShow = true
      } else if (type === 'out' || type === 'remove') {
        // 启用停用
        let flag = null
        if (this.userInfo.userId == scope.row.userId) {
          flag = '退出项目后，您将不能查看任何关于该项目的信息，如果想重新加入，请重新联系该项目管理员。'
        // } else if (scope.row.adminRoles.filter(item => item.label == 1).length) {
        //   flag = '确认要退出项目吗? 作为该项目管理员，在退出项目前，您至少需要添加一名项目管理员。退出项目后，您将不能查看任何关于该项目的信息，如果想重新加入，请重新联系该项目管理员。'
        }
        this.$confirm(`您确定要${scope.row.userId == this.userInfo.userId ? '退出' : '移除该成员'}吗? ${flag || ''}`, '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
          .then(() => {
            this.loading = true
            deleteProjectRolesAPI({
              projectId: this.projectId,
              userId: scope.row.userId
            })
              .then(res => {
                this.$message({
                  type: 'success',
                  message: '操作成功'
                })
                if (type === 'out') {
                  this.$router.push({ name: 'participate' })
                }
                this.getList()
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
}
</script>
<style scoped lang='scss'>
.main-head {
  margin-top: #{$--interval-base * 2};

  .content-table-header {
    padding-bottom: 16px;
  }
}
</style>
