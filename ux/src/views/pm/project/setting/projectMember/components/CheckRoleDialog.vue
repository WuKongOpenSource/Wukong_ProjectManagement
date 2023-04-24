<template>
  <el-dialog
    ref="wkDialog"
    :title="title"
    :visible.sync="show"
    :before-close="close"
    :append-to-body="true"
    :close-on-click-modal="false"
    width="700px">
    <label class="label-title">当前{{ typeName }}: {{ name }}</label>
    <div style="margin-top: 8px;">
      <el-table
        :data="ruleMenuList"
        :class="WKConfig.tableStyle.class"
        :stripe="WKConfig.tableStyle.stripe"
        highlight-current-row
        style="width: 100%;">
        <el-table-column
          v-for="(item, index) in tableList"
          :key="index"
          :prop="item.field"
          :label="item.label"
          show-overflow-tooltip>
          <template slot-scope="{row}">
            <div
              v-if="['visit', 'list', 'check'].includes(item.field)">
              <div v-for="(v, i) in row[item.field]" :key="i">
                <el-checkbox
                  v-model="v.check"
                  :disabled="true" />
                <span>{{ v.menuName }}</span>
              </div>
            </div>
            <span v-else-if="item.field== 'module'">{{ row[item.field] }}</span>
          </template>
        </el-table-column>
        <!-- <el-table-column
          prop="relationNum"
          label="全选">
          <template slot-scope="scope">
            <el-checkbox
              v-model="scope.row.check"
              :disabled="true"
              @change="selectAll(scope.row)" />
          </template>
        </el-table-column> -->
      </el-table>
    </div>
  </el-dialog>
</template>

<script>
import {
  systemRuleByTypeAPI
} from '@/api/admin/role'
import ElDialogLoadingMixin from '@/mixins/ElDialogLoading'

export default {
  name: 'CheckRoleDialog', // 查看角色权限
  components: {
  },
  mixins: [ElDialogLoadingMixin],
  props: {
    title: {
      type: String,
      default: () => {
        return '角色权限'
      }
    },
    typeName: {
      type: String,
      default: () => {
        return '角色'
      }
    },
    name: {
      type: String,
      default: () => {
        return ''
      }
    },
    show: Boolean,
    ruleList: {
      type: Array,
      default: () => {
        return []
      }
    }
  },
  data() {
    return {
      loading: false,
      ruleMenuList: [],
      tableList: [
        { label: '模块', field: 'module' },
        { label: '访问权限', field: 'visit' },
        { label: '功能权限', field: 'list' }
        // { label: '全选', field: 'check' }
      ]
    }
  },
  computed: {
  },
  watch: {
    show: {
      handler(val) {
        if (val) {
          this.getRulesList(this.ruleList)
        }
      }
    }
  },
  created() {},
  mounted() {
  },
  methods: {
    close() {
      this.$emit('close')
    },
    /**
     * @description: 获取权限规则信息
     * @param {*} ruleList Array
     * @return {*}
     */
    getRulesList(ruleList) {
      this.loading = true
      systemRuleByTypeAPI(8)
        .then(res => {
          const resData = res.data || {}
          const data = resData.data
          const dataList = data.childMenu
          const list = []
          // eslint-disable-next-line
          for (const item of dataList) {
            list.push({
              module: item.menuName,
              visit: this.getData(item.childMenu, 2, ruleList),
              list: this.getData(item.childMenu, 1, ruleList),
              check: false
            })
          }
          this.ruleMenuList = list
          console.log(this.ruleMenuList)
          this.loading = false
        })
        .catch(() => {
          this.loading = false
        })
    },

    getData(list, type, ruleList) {
      const newList = list.filter(item => item.projectType == type).map(item => {
        item.check = !!ruleList.includes(item.menuId)
        return item
      })
      return newList
    },

    /**
     * @description: 全选变化
     * @param {*} row
     * @return {*}
     */
    selectAll(row) {
      const arr = [...row.list, ...row.visit]
      arr.forEach(item => {
        item.check = row.check
      })
    }
  }
}
</script>
<style lang="scss" scoped>
</style>
