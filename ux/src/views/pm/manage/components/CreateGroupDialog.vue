<template>
  <el-dialog
    ref="wkDialog"
    append-to-body
    :visible.sync="dialogVisible"
    title="新建分组"
    width="500px"
    custom-class="no-padding-dialog"
    @close="handleCancel">

    <div class="group-manage">
      <el-input
        v-model.trim="groupName"
        :maxlength="20"
        placeholder="请输入分组名称" />
    </div>

    <div slot="footer">
      <el-button type="primary" @click="handleAdd">创建</el-button>
      <el-button @click="handleCancel">取消</el-button>
    </div>
  </el-dialog>
</template>

<script>
import {
  groupAddAPI
} from '@/api/pm/manage'
import ElDialogLoadingMixin from '@/mixins/ElDialogLoading'

export default {
  name: 'GroupManageDialog', // 新建分组弹层
  components: {
  },
  mixins: [ElDialogLoadingMixin],
  props: {
    visible: Boolean,
    listData: {
      type: Array,
      default: () => []
    }
  },
  data() {
    return {
      loading: false,
      dialogVisible: false,

      groupName: '',
      groupList: [],

      dragOptions: {
        sort: true,
        handle: '.grid',
        draggable: '.sortable'
      }
    }
  },
  watch: {
    visible: {
      handler() {
        this.dialogVisible = this.visible
      },
      immediate: true
    }
  },
  methods: {
    /**
     * @description: 创建
     * @return {*}
     */
    handleAdd() {
      if (!this.groupName) {
        this.$message.error('分组名称不能为空')
        return
      }
      const names = this.groupList.map(o => o.label)
      if (names.includes(this.groupName)) {
        this.$message.error('分组名称不能重复')
        return
      }
      const params = {
        name: this.groupName
      }
      this.loading = true
      groupAddAPI(params)
        .then(res => {
          console.log(res, '添加分组')
          this.$emit('addGroup')
          this.$emit('close')
          this.loading = false
          this.$message.success('添加成功')
        })
        .catch(() => {
          this.loading = false
        })
      this.groupName = ''
    },

    /**
     * @description: 取消
     * @return {*}
     */
    handleCancel() {
      this.dialogVisible = false
      this.$emit('close')
    }
  }
}
</script>

<style scoped lang="scss">
.group-manage {
  padding: 16px 20px;
}
</style>
