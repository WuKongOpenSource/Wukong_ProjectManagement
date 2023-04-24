<template>
  <el-dialog
    v-loading="loading"
    :title="diaTitle"
    :visible="show"
    :modal-append-to-body="true"
    :append-to-body="true"
    :close-on-click-modal="false"
    class="create-dialog"
    width="700px"
    @close="closeView">
    <div class="label-input">
      <label class="label-title">角色名称</label>
      <el-input
        v-model="roleName"
        :maxlength="100"
        placeholder="请输入角色名称" />
    </div>
    <div class="label-input">
      <label class="label-title">角色描述</label>
      <el-input
        v-model="remark"
        :rows="2"
        :maxlength="300"
        type="textarea"
        placeholder="请输入角色描述" />
    </div>
    <div class="label-input">
      <label class="label-title">参考角色</label>
      <el-select v-model="labelValue" placeholder="请选择" style="width: 100%;" @change="selectChange">
        <el-option
          v-for="item in options"
          :key="item.value"
          :label="item.label"
          :value="item.value" />
      </el-select>
    </div>
    <label class="label-title">权限配置</label>
    <div class="jurisdiction-content-checkbox">
      <el-tree
        ref="tree"
        :data="showTreeData"
        :indent="0"
        :expand-on-click-node="false"
        :props="defaultProps"
        show-checkbox
        node-key="menuId"
        style="height: 0;"
        empty-text=""
        default-expand-all />
    </div>
    <span
      slot="footer"
      class="dialog-footer">
      <el-button
        type="primary"
        @click="sureClick">确定</el-button>
      <el-button @click="closeView">取消</el-button>
    </span>
  </el-dialog>
</template>

<script>
// import { systemRoleSetWorkRoleAPI } from '@/api/admin/project'
import {
  addProjectRoleAPI,
  updateRoleMenuAPI,
  systemRuleByTypeAPI
} from '@/api/admin/role'

export default {
  name: 'JurisdictionCreate', // 文件导入
  components: {},

  props: {
    show: {
      type: Boolean,
      default: false
    },

    action: {
      type: Object,
      default: () => {
        return {
          type: 'save'
        }
      }
    }
  },
  data() {
    return {
      loading: false,
      roleName: '',
      remark: '',
      showTreeData: [],
      defaultProps: {
        children: 'childMenu',
        label: 'menuName'
      },
      labelValue: '',
      // options: [{
      //   value: 0,
      //   label: '默认配置'
      // }, {
      //   value: 1,
      //   label: '系统项目管理员'
      // }, {
      //   value: 2,
      //   label: '开发'
      // }, {
      //   value: 3,
      //   label: '项目经理'
      // }, {
      //   value: 4,
      //   label: '测试'
      // }, {
      //   value: 5,
      //   label: '产品'
      // }, {
      //   value: 6,
      //   label: '运维'
      // }],
      options: [{
        value: 0,
        label: '默认配置'
      }, {
        value: 1,
        label: '系统项目管理员'
      }, {
        value: 5,
        label: '开发'
      }, {
        value: 8,
        label: '项目经理'
      }, {
        value: 7,
        label: '测试'
      }, {
        value: 6,
        label: '产品'
      }, {
        value: 9,
        label: '运维'
      }]
    }
  },

  computed: {
    diaTitle() {
      if (this.action.type == 'save') {
        return '新建'
      }

      return '编辑'
    }
  },

  watch: {
    show(value) {
      if (value) {
        this.labelValue = ''
        this.initInfo()
      }
    }
  },

  mounted() {},

  methods: {
    /**
     * 初始化数据
     */
    initInfo() {
      if (this.action.type == 'update') {
        this.roleName = this.action.data.roleName
        this.remark = this.action.data.remark
        this.labelValue = this.action.data.label
      } else {
        this.roleName = ''
        this.remark = ''
        if (this.$refs.tree) {
          this.$refs.tree.setCheckedKeys([])
        }
      }

      if (this.showTreeData.length == 0) {
        this.getRulesList()
      } else {
        this.checkTreeByUpdateInfo()
      }
    },

    /**
     * 获取规则
     */
    getRulesList() {
      this.loading = true
      systemRuleByTypeAPI(8)
        .then(res => {
          this.showTreeData = res.data.data ? [res.data.data] : []
          this.checkTreeByUpdateInfo()
          this.loading = false
        })
        .catch(() => {
          this.loading = false
        })
    },

    selectChange() {},

    /**
     * 标记数据
     */
    checkTreeByUpdateInfo() {
      this.$nextTick(() => {
        if (this.$refs.tree) {
          if (this.action.type == 'update') {
            this.$refs.tree.setCheckedKeys(
              this.getUserModuleRules(this.action.data.rules.data)
            )
          }

          let children = this.$refs.tree.$children && this.$refs.tree.$children.length ? this.$refs.tree.$children[0].$el : null
          if (children) {
            children = children.children && children.children.length ? children.children[0] : null
            if (children) {
              children.style.display = 'none'
            }
          }
        }
      })
    },

    /**
     * 确定
     */
    sureClick() {
      if (!this.roleName) {
        this.$message.error('请填写角色名称')
      } else {
        this.loading = true
        const menuIds = this.$refs.tree.getCheckedKeys()
        const params = {
          roleName: this.roleName,
          remark: this.remark,
          menuIds: menuIds,
          roleType: 8,
          label: this.labelValue
        }
        let request = addProjectRoleAPI

        if (this.action.type == 'update') {
          params.roleId = this.action.data.roleId
          request = updateRoleMenuAPI
        }
        request(params)
          .then(res => {
            this.loading = false
            this.$message.success('操作成功')
            this.$emit('submite')
            this.closeView()
          })
          .catch(() => {
            this.loading = false
          })
      }
    },

    closeView() {
      this.$emit('update:show', false)
    },

    /**
     * 获得check的实际数据
     */
    getUserModuleRules(array) {
      if (!array) {
        array = []
      }
      var firstTree = this.showTreeData[0]
      var hasRemove = false
      var copyArray = this.copyItem(array)
      for (
        let firstIndex = 0;
        firstIndex < firstTree.childMenu.length;
        firstIndex++
      ) {
        const firstItem = firstTree.childMenu[firstIndex]
        if (!firstItem.childMenu) {
          firstItem.childMenu = []
        }
        for (let index = 0; index < array.length; index++) {
          const element = array[index]
          var temps = []
          for (
            let secondIndex = 0;
            secondIndex < firstItem.childMenu.length;
            secondIndex++
          ) {
            const secondItem = firstItem.childMenu[secondIndex]
            if (secondItem.menuId == element) {
              temps.push(secondItem)
            }
          }
          if (temps.length != firstItem.childMenu.length) {
            hasRemove = true
            this.removeItem(copyArray, firstItem.menuId)
          }
        }
      }

      if (hasRemove) {
        this.removeItem(copyArray, firstTree.menuId)
      }

      var checkedKey = []
      for (let index = 0; index < copyArray.length; index++) {
        const element = copyArray[index]
        if (element) {
          checkedKey.push(parseInt(element))
        }
      }

      return checkedKey
    },
    copyItem(array) {
      var temps = []
      for (let index = 0; index < array.length; index++) {
        temps.push(array[index])
      }
      return temps
    },
    removeItem(array, item) {
      var removeIndex = -1
      for (let index = 0; index < array.length; index++) {
        if (item == array[index]) {
          removeIndex = index
          break
        }
      }
      if (removeIndex > 0) {
        array.splice(removeIndex, 1)
      }
    },
    containItem(array, item) {
      for (let index = 0; index < array.length; index++) {
        if (item == array[index]) {
          return true
        }
      }
      return false
    }
  }
}
</script>

<style scoped lang="scss">
.create-dialog /deep/ .el-dialog__body {
  padding-top: 0 !important;
}

.label-title {
  display: block;
  margin: 20px 0 10px;
  color: $--color-text-regular;
}

.label-input {
  position: relative;
}

.jurisdiction-content-checkbox {
  height: 200px;
  padding: 10px 0;
  overflow-y: auto;
  border: 1px solid #e6e6e6;
  border-radius: 2px;
}

.jurisdiction-content-checkbox .el-tree
/deep/
.el-tree-node > .el-tree-node__content {
  width: 160px;
  margin-bottom: 5px;
}

.jurisdiction-content-checkbox /deep/ .el-tree .el-tree-node {
  margin-bottom: 5px;
  white-space: inherit;
}

.jurisdiction-content-checkbox
/deep/
.el-tree > .el-tree-node > .el-tree-node__children > .is-expanded > .el-tree-node__children > .is-expanded {
  display: inline-block;
}
</style>
