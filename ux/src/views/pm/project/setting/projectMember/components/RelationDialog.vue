<template>
  <el-dialog
    ref="wkDialog"
    :visible="visible"
    :append-to-body="true"
    :close-on-click-modal="false"
    width="500px"
    @close="close">
    <div slot="title" class="el-dialog__title">
      编辑角色
    </div>
    <el-form
      ref="editRoleForm"
      :model="ruleForm"
      label-width="100px"
      label-position="top">
      <el-form-item label="设置角色" prop="roleList">
        <el-select v-model="selectList" multiple placeholder="请选择" style="width: 100%;">
          <el-option
            v-for="item in options"
            :key="item.value"
            :label="item.label"
            :value="item.value" />
        </el-select>
      </el-form-item>
    </el-form>
    <div class="" />
    <span slot="footer" class="dialog-footer">
      <el-button type="primary" @click="sureClick">确定</el-button>
      <el-button @click="close">取消</el-button>
    </span>
  </el-dialog>
</template>

<script>
import {
  editProjectUserAPI
} from '@/api/pm/manage'

export default {
  name: 'RelationDialog', // 编辑角色
  components: {
  },
  props: {
    selectionList: Array,
    groupList: Array,
    visible: {
      type: Boolean,
      required: true,
      default: false
    },
    projectId: [String, Number],
    userId: [String, Number]
  },
  data() {
    return {
      loading: false,
      ruleForm: {
        roleList: [],
        userIds: [],
        deptIds: []
      },
      options: [],
      selectList: []
    }
  },
  computed: {
  },
  watch: {
    visible: {
      handler(val) {
        this.selectList = this.selectionList
      },
      deep: true,
      immediate: true
    }
  },
  created() {
    this.getOptions()
  },
  methods: {
    /**
     * @description: 格式化列表数组
     * @return {*}
     */
    getOptions() {
      const list = []
      // eslint-disable-next-line
      for (const item of this.groupList) {
        list.push({
          value: item.roleId,
          label: item.roleName
        })
      }
      this.options = list
    },

    /**
     * @description: 关闭弹层
     * @return {*}
     */
    close() {
      this.$emit('update:visible', false)
    },

    /**
     * @description: 编辑保存
     * @return {*}
     */
    sureClick() {
      this.$refs.editRoleForm.validate((valid) => {
        if (valid) {
          const params = {
            projectId: this.projectId,
            roleIds: this.selectList,
            userId: this.userId
          }
          editProjectUserAPI(params)
            .then(res => {
              this.$emit('change')
              this.$message.success('编辑成功')
            })
            .catch(() => {})
        } else {
          return false
        }
      })
    }
  }
}
</script>
<style lang="scss" scoped>
.el-form {
  margin-top: 10px;

  /deep/ .el-form-item__label {
    padding-bottom: 0;
    line-height: 30px;
  }
}
</style>
