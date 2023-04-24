<template>
  <div class="main">
    <flexbox class="main-header" justify="space-between">
      <div class="main-header__left">
        <i
          class="wk wk-icon-circle-left"
          @click="back" />
        <span>|</span>
        <span class="title">搜索结果</span>
      </div>
    </flexbox>

    <el-table
      v-loading="loading"
      class="table"
      :data="list"
      :row-height="rowHeight"
      :height="tableHeight"
      :cell-class-name="cellClassName"
      use-virtual
      highlight-current-row
      style="width: 100%;"
      @row-click="handleRowClick">
      <el-table-column
        label="序号"
        width="50"
        fixed="left"
        show-overflow-tooltip>
        <template slot-scope="scope">
          {{ scope.$index + 1 }}
        </template>
      </el-table-column>
      <el-table-column
        v-for="(item, index) in tableField"
        :key="index"
        :prop="item.prop"
        :formatter="fieldFormatter"
        :label="item.name"
        show-overflow-tooltip />
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

    <detail
      v-if="detailShow"
      :id="examineId"
      :page-list="list"
      :row-index.sync="rowIndex"
      @hide-view="detailShow = false"
    />
  </div>
</template>

<script>
import { superExamineByNameAPI } from '@/api/oa/superExamine'

import Detail from './Detail.vue'

import TableMixin from '@/mixins/Table'
import { isEmpty } from '@/utils/types'
export default {
  name: 'Search',
  components: {
    Detail
  },
  mixins: [TableMixin],
  data() {
    return {
      loading: false,
      search: '',
      list: [],
      tableField: [
        { name: '审批名称', prop: 'content', formType: 'text' },
        { name: '提交人', prop: 'submitUser', formType: 'text' },
        { name: '提交部门', prop: 'submitDeptName', formType: 'text' },
        { name: '文件类型', prop: 'examineType', formType: 'text' },
        { name: '上一步处理人', prop: 'preHandleUserList', formType: 'text' },
        { name: '文件已停留时间', prop: 'stayTime', formType: 'text' }
      ],

      // 详情
      detailShow: false,
      rowIndex: 0,
      examineId: ''
    }
  },
  watch: {
    search: {
      handler(val) {
        if (val) {
          this.getList()
        }
      },
      immediate: true
    },
    rowIndex(val) {
      this.examineId = this.list[val].oaExamineId
    }
  },
  created() {
    this.search = this.$route.query.search
    this.otherTableHeight = 210 // 出去表格后的剩余高度
  },
  methods: {
    /**
     * 获取数据
     */
    getList() {
      this.loading = true
      const params = {
        page: this.currentPage,
        limit: this.pageSize,
        search: this.search
      }
      superExamineByNameAPI(params)
        .then(res => {
          this.loading = false
          const data = res.data
          this.list = data.list || []
          this.total = data.totalRow
          this.updateTableHeight()
        })
        .catch(() => {
          this.loading = false
        })
    },

    /**
     * 格式化展示
     */
    fieldFormatter(row, column, cellValue, field) {
      if (column.property == 'submitUser') {
        return isEmpty(row['submitUser']) ? '--' : row['submitUser']['realname']
      } else if (column.property == 'submitdeptName') {
        return isEmpty(row['submitUser']) ? '--' : row['submitUser']['deptName']
      } else if (column.property == 'preHandleUserList') {
        return row[column.property]?.map(item => item.realname || item.outerUserEmail).join(';') || '--'
      }
      return isEmpty(row[column.property]) ? '--' : row[column.property]
    },

    /**
     * 返回
     */
    back() {
      this.$router.go(-1)
    },

    /**
     * 通过回调控制class
     */
    cellClassName({ row, column, rowIndex, columnIndex }) {
      if (column.property === 'content') {
        return 'can-visit--underline'
      } else {
        return ''
      }
    },

    /**
     * 列表操作
     * @param {*} row
     * @param {*} column
     * @param {*} event
     */
    handleRowClick(row, column, event) {
      if (column.property === 'content') {
        this.examineId = row.oaExamineId
        this.list.forEach((item, index) => {
          if (item.oaExamineId == row.oaExamineId) {
            this.rowIndex = index
          }
        })
        this.detailShow = true
      }
    }
  }
}
</script>

<style lang="scss" scoped>
@import "../style/index.scss";

.main {
  .main-header__left {
    display: flex;
    align-items: center;

    i {
      font-size: 24px;
      cursor: pointer;
    }

    span:nth-of-type(1) {
      margin: 0 8px;
      color: $--color-n100;
    }
  }

  .table {
    margin-top: 32px;
  }
}
</style>
