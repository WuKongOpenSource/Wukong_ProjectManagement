<template>
  <div class="project-member">
    <el-tabs v-model="activeName" @tab-click="handleClick">
      <el-tab-pane label="项目与成员" disabled />
      <el-tab-pane
        v-for="(item, index) in tabList"
        :key="index"
        :label="item.label"
        :name="item.name">
        <template slot="label">
          <span v-if="index == 0" class="divider" />
          {{ item.label }}
        </template>
        <component
          :is="item.name"
          ref="member"
          v-loading="loading"
          :group-list="groupList"
          :group-options="groupOptions"
          :project-id="projectId"
          @getList="getGroupList" />
      </el-tab-pane>
    </el-tabs>
    <el-button
      v-if="activeName == 'Member' && $auth('set.memberManage', 'PM')"
      type="primary"
      class="add-btn"
      @click="newMemberBtn">添加成员</el-button>
    <!-- 添加成员 -->
    <el-dialog
      ref="wkDialog"
      title="添加成员"
      :visible.sync="newMemberVisible"
      :before-close="newMemberClose"
      :close-on-click-modal="false"
      width="700px"
      append-to-body>
      <div>
        <el-button
          type="text"
          icon="el-icon-plus"
          @click="addMember">添加成员</el-button>
        <wk-dep-user-dialog
          :radio="false"
          :disabled="false"
          :user-value.sync="dataValue"
          :visible.sync="visible"
          :props="{
            showDept: false,
            showDisableUser: config.showDisableUser,
            showUser: !config.disableUserList,
            disableUserList: config.disableUserList,
            disableUserLabel: config.disableUserLabel
          }"
          @click.native.stop
          @change="depUserDialogChange"
        />
        <el-table
          v-loading="loading"
          :data="memberList"
          :class="WKConfig.tableStyle.class"
          :stripe="WKConfig.tableStyle.stripe"
          class="main-table"
          highlight-current-row
          style="width: 100%;">
          <el-table-column
            prop="realname"
            label="成员名称"
            width="120px" />
          <el-table-column
            label="设置角色">
            <template slot-scope="scope">
              <el-select v-model="scope.row.roleIds" multiple placeholder="请选择角色" style="width: 100%;">
                <el-option
                  v-for="item in groupOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value" />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column
            prop="role"
            label="操作"
            width="120px">
            <template slot-scope="scope">
              <el-button
                type="primary-text"
                style="padding: 0;"
                @click="deleteClick(scope.row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <span
        slot="footer"
        class="dialog-footer">
        <el-button
          type="primary"
          @click.native="handleConfirm">保存</el-button>
        <el-button @click.native="newMemberClose">取消</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import {
  relatedProjectUserAPI,
  getProjectRoleByTypeAPI
} from '@/api/pm/manage'

import BaseInfo from './components/BaseInfo'
import Member from './components/Member'
import UserGroup from './components/UserGroup'
import WkDepUserDialog from '@/components/NewCom/WkUserDialogSelect/Dialog'

import merge from '@/utils/merge'
import ElDialogLoadingMixin from '@/mixins/ElDialogLoading'

export default {
  name: 'ProjectMember', // 项目与成员
  components: {
    BaseInfo,
    Member,
    UserGroup,
    WkDepUserDialog
  },
  mixins: [ElDialogLoadingMixin],
  data() {
    return {
      loading: false,
      activeName: 'BaseInfo',
      tabList: [
        { label: '基本信息', name: 'BaseInfo' },
        { label: '成员', name: 'Member' },
        { label: '角色', name: 'UserGroup' }
      ],
      groupList: [],
      newMemberVisible: false,
      groupOptions: [],
      labelValue: '2',
      dataValue: [], // 当前选择值
      visible: false,
      memberList: []
    }
  },
  computed: {
    projectId() {
      return this.$route.params.setting
    },
    // 合并 props
    config() {
      return merge({
        value: 'userId',
        label: 'realname',
        showDisableUser: false,
        disableUserList: null, // 用于单列固定数据的展示 会使 showUser false
        disableUserLabel: '停用员工'
      }, this.props || {})
    }
  },
  mounted() {
    this.getGroupList()
  },
  methods: {
    handleClick() {},

    /**
     * @description: 点击添加成员
     * @return {*}
     */
    newMemberBtn() {
      if (this.activeName == 'Member') {
        this.dataValue = []
        this.memberList = []
        this.newMemberVisible = true
      }
    },

    /**
     * @description: 取消操作
     * @return {*}
     */
    newMemberClose() {
      this.newMemberVisible = false
    },

    /**
     * @description: 获取角色列表
     * @return {*}
     */
    getGroupList() {
      this.loading = true
      getProjectRoleByTypeAPI({
        projectId: this.projectId
      })
        .then(res => {
          const resData = res.data || []
          const arr = []
          this.groupList = resData
          this.groupList.forEach(item => {
            arr.push({
              label: item.roleName,
              value: item.roleId
            })
          })
          this.groupOptions = arr
          console.log(resData)
          this.loading = false
          // this.getRulesList()
        }).catch(() => {
          this.loading = false
        })
    },

    /**
     * @description: 添加弹层内点击添加成员按钮
     * @return {*}
     */
    addMember() {
      this.visible = true
    },

    /**
     * @description: 选择成员弹框确定
     * @param {*} currentUserValue
     * @param {*} currentDepValue
     * @param {*} userSelectData
     * @param {*} deptSelectData
     * @return {*}
     */
    depUserDialogChange(currentUserValue, currentDepValue, userSelectData, deptSelectData) {
      this.$nextTick(() => {
        const list = []
        // eslint-disable-next-line
        for (const item of userSelectData) {
          list.push({
            userId: item.userId,
            realname: item.realname,
            roleIds: [],
            projectId: this.projectId
          })
        }
        this.memberList = list
      })
    },

    /**
     * @description: 删除已选择的成员
     * @param {*} row
     * @return {*}
     */
    deleteClick(row) {
      this.memberList.splice(this.dataValue.indexOf(row.userId), 1)
      this.dataValue.splice(this.dataValue.indexOf(row.userId), 1)
    },

    /**
     * @description: 添加成员点击确定
     * @return {*}
     */
    handleConfirm() {
      if (this.memberList.length == 0) {
        this.$message.error('请选择员工')
      } else {
        const params = this.memberList
        relatedProjectUserAPI(params)
          .then(res => {
            this.newMemberClose()
            this.$message.success('操作成功')
            this.$nextTick(() => {
              this.$refs.member[1].getList()
            })
          })
          .catch(() => {})
      }
    }

  }
}
</script>

<style scoped lang="scss">
@import "@/views/layout/components/style.scss";

.project-member {
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
      padding-right: 24px;

      &:nth-of-type(2) {
        padding-right: 48px;
      }
    }

    .is-active {
      font-size: 16px;
      font-weight: 700;
      color: #0052cc;
    }
  }

  .add-btn {
    position: absolute;
    top: 22px;
    right: 40px;
  }

  /* 新建用户组 */
  .input-group {
    width: 100%;
    padding: 10px 0 20px;
  }
}

.handle-item {
  padding-bottom: 15px;

  .handle-item-name {
    flex-shrink: 0;
    width: 90px;
  }

  .handle-item-content {
    flex: 1;
  }
}

/deep/ .el-dialog {
  .el-dialog__body {
    padding: 8px 20px;
  }
}

.divider {
  position: absolute;
  top: 13px;
  left: 0;
  display: inline-block;
  width: 1px;
  height: 16px;
  margin-left: -24px;
  background-color: #dfe1e6;
}
</style>
