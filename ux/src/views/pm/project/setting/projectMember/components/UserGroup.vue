<template>
  <div>
    <div class="main-head">
      <el-table
        id="examine-table"
        v-loading="loading"
        :data="groupList"
        :height="tableHeight"
        :class="WKConfig.tableStyle.class"
        :stripe="WKConfig.tableStyle.stripe"
        class="main-table"
        highlight-current-row
        style="width: 100%;">
        <el-table-column
          show-overflow-tooltip
          prop="roleName"
          label="角色名称" />
        <el-table-column
          prop="num"
          label="关联成员数" />
        <el-table-column
          prop="createTime"
          label="创建时间" />
        <el-table-column
          fixed="right"
          label="操作"
          width="300">
          <template slot-scope="scope">
            <el-button
              type="primary-text"
              style="padding: 0;"
              @click="handleClick('check', scope)">查看权限</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <!-- 查看角色权限 -->
    <check-role-dialog
      title="角色权限"
      :show="groupAuthShow"
      :name="title"
      :rule-list="ruleList"
      @close="close" />
  </div>
</template>

<script>
import CheckRoleDialog from './CheckRoleDialog'

import { isEmpty } from '@/utils/types'

export default {
  name: 'UserGroup', // 项目设置-角色
  components: {
    CheckRoleDialog
  },
  props: {
    groupList: {
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
      loading: false, // 加载动画
      groupAuthShow: false,
      tableHeight: document.documentElement.clientHeight - 180, // 表的高度
      title: '',
      ruleList: []
    }
  },
  computed: {
  },
  watch: {
  },
  created() {
  },
  mounted() {
    var self = this
    /** 控制table的高度 */
    window.onresize = () => {
      self.tableHeight = document.documentElement.clientHeight - 180
    }
  },
  methods: {
    isEmpty(val) {
      return isEmpty(val)
    },

    /**
     * @description: 操作
     * @param {*} type String
     * @param {*} scope
     * @return {*}
     */
    handleClick(type, scope) {
      if (type === 'check') {
        console.log('查看权限')
        this.ruleList = scope.row.rules || []
        this.title = scope.row.roleName
        this.groupAuthShow = true
      }
    },

    /**
     * @description: 关闭弹层
     * @return {*}
     */
    close() {
      this.groupAuthShow = false
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
