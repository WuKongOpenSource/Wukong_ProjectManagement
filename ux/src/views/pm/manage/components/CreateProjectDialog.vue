<template>
  <!-- 选择应用 -->
  <el-dialog
    v-if="chooseDialogVisible"
    ref="wkDialog"
    append-to-body
    :visible.sync="chooseDialogVisible"
    :close-on-click-modal="false"
    :close-on-press-escape="false"
    :title="title"
    width="810px"
    :custom-class="!createDialogVisible ? 'no-padding-dialog pm-create-dialog' : 'pm-create-dialog'"
    @close="handleCloseAll">

    <!-- 项目类型选择 -->
    <flexbox
      v-if="!createDialogVisible"
      justify="space-between"
      class="tpl-wrapper">
      <flexbox
        direction="column"
        align="flex-start"
        class="tpl-item"
        @click.native="handleChoose(1)">
        <img
          src="@/assets/img/project/common_project.png"
          alt=""
          class="tpl-item__pic">
        <flexbox class="tpl-item__title">
          <i class="wk wk-icon-general-project" />
          <span>通用项目</span>
        </flexbox>
        <p class="tpl-item__sub-title">简单易用的通用任务处理模板，适用于诸如个人安排等活动管理。</p>
        <p class="tpl-item__desc">包含组件（4个）<br>概览、任务、甘特图、成员</p>
        <el-button type="primary" style="font-size: 14px;">选择</el-button>
      </flexbox>

      <flexbox
        direction="column"
        align="flex-start"
        class="tpl-item"
        @click.native="handleChoose(2)">
        <img
          src="@/assets/img/project/developer_project.png"
          alt=""
          class="tpl-item__pic">
        <flexbox class="tpl-item__title">
          <i class="wk wk-icon-agility" />
          <span>敏捷开发项目</span>
        </flexbox>
        <p class="tpl-item__sub-title">通过内置的敏捷研发管理组件，可以轻松实现迭代管控、需求分配、缺陷管理等核心研发工作，通过各类报表实时掌控项目进度状况。</p>
        <p class="tpl-item__desc">包含组件（8个）<br>需求、迭代、缺陷、任务、概览、甘特图、成员、工时组件</p>
        <el-button type="primary" style="font-size: 14px;">选择</el-button>
      </flexbox>
    </flexbox>

    <!-- 项目创建 -->
    <template v-if="createDialogVisible">
      <el-form
        ref="elForm"
        :model="form"
        :rules="rules"
        class="create-wrapper">
        <el-row>
          <el-col :span="11">
            <el-form-item label="项目名称" prop="name">
              <el-input v-model="form.name" />
            </el-form-item>
          </el-col>
          <el-col :span="2">&nbsp;</el-col>
          <el-col :span="11">
            <el-form-item label="项目图标" prop="icon">
              <wk-ico-select
                v-model="form.icon"
                icon-color="#0052CC"
                title="项目图标"
                hidden-color
                placement="bottom" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row>
          <el-col :span="11">
            <el-form-item label="开始时间" prop="startTime">
              <el-date-picker
                v-model="form.startTime"
                style="width: 100%;"
                type="date"
                value-format="yyyy-MM-dd HH:mm:ss"
                placeholder="开始时间" />
            </el-form-item>
          </el-col>
          <el-col :span="2">&nbsp;</el-col>
          <el-col :span="11">
            <el-form-item label="截止时间" prop="stopTime">
              <el-date-picker
                v-model="form.stopTime"
                style="width: 100%;"
                type="date"
                value-format="yyyy-MM-dd HH:mm:ss"
                placeholder="截止时间" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="项目描述" prop="desc">
          <el-input
            v-model.trim="form.description"
            :rows="3"
            :maxlength="500"
            type="textarea"
            resize="none"
            show-word-limit />
        </el-form-item>

        <el-form-item label="可见范围" prop="desc">
          <el-select v-model="form.isOpen">
            <el-option
              v-for="item in openOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value" />
          </el-select>
        </el-form-item>

        <el-form-item
          v-if="form.isOpen === 0"
          label="项目成员">
          <flexbox align="flex-start" justify="flex-start">
            <xr-avatar
              v-for="user in selectUserList"
              :id="user.userId"
              :key="user.userId"
              :size="26"
              :name="user.realname"
              :src="user.img" />
            <flexbox
              align="center"
              justify="center"
              class="add-user-btn"
              @click.native="handleToChooseUser">
              <i class="el-icon-plus" />
            </flexbox>
          </flexbox>
        </el-form-item>
      </el-form>

      <div
        slot="footer"
        class="dialog-footer">
        <el-button
          type="primary"
          @click="handleConfirmForm">确定</el-button>
        <el-button @click="handleCloseAll">取消</el-button>
      </div>

      <wk-dep-user-dialog
        v-if="depUserViewDialogShow"
        :user-value.sync="selectedUserIds"
        :props="{ showDept: false }"
        :visible.sync="depUserViewDialogShow"
        @change="selectUserChange" />
    </template>
  </el-dialog>

</template>

<script>
import {
  projectAddAPI,
  projectUpdateAPI
} from '@/api/pm/manage'

import WkDepUserDialog from '@/components/NewCom/WkUserDialogSelect/Dialog'
import WkIcoSelect from '@/views/pm/components/WkIcoSelect'

import { objDeepCopy, formatTimeToTimestamp } from '@/utils'
import ElDialogLoadingMixin from '@/mixins/ElDialogLoading'

export default {
  name: 'CreateProjectDialog', // 创建项目
  components: {
    WkDepUserDialog,
    WkIcoSelect
  },
  mixins: [ElDialogLoadingMixin],
  props: {
    visible: {
      type: Boolean,
      required: true,
      default: false
    },
    itemData: {
      type: Object,
      default: null
    }
  },
  data() {
    return {
      loading: false,
      chooseDialogVisible: false,
      createDialogVisible: false,
      depUserViewDialogShow: false,
      openOptions: [
        { value: 0, label: '私有：只有加入的成员才能看见此项目' },
        { value: 1, label: '公开：企业所有成员都可以看见此项目（公开项目系统所有人都能查看，但是只有项目成员可以编辑）' }
      ],
      form: {
        type: null,
        name: '',
        description: '',
        isOpen: 0,
        status: 1,
        level: 3,
        ownerDeptIds: '',
        ownerUserIds: '',
        icon: {
          icon: '',
          color: '#EBECF0'
        },
        startTime: '',
        stopTime: ''
      },
      rules: {
        name: [{ required: true, message: '请输入项目名称', trigger: 'blur' },
          { min: 1, max: 31, message: '请输入1-31位的项目名称', trigger: 'blur' }],
        icon: [{ required: true, message: '请选择应用图标', trigger: 'blur' }]
      },
      selectedUserIds: [],
      selectUserList: []
    }
  },
  computed: {
    title() {
      if (this.createDialogVisible) {
        return this.itemData ? '编辑项目基本信息' : '填写项目基本信息'
      }
      return '新建项目'
    }
  },
  watch: {
    visible: {
      handler() {
        if (this.visible) {
          this.chooseDialogVisible = true
        } else {
          this.chooseDialogVisible = false
          this.createDialogVisible = false
        }
      },
      immediate: true
    }
  },
  mounted() {
    if (this.itemData) {
      // 编辑时初始化表单
    } else {
      this.selectUserList = []
      this.selectedUserIds = []
    }
  },
  methods: {
    /**
     * 选择项目类型
     * @param type
     */
    handleChoose(type) {
      this.form.type = type
      this.createDialogVisible = true
    },

    /**
     * 关闭
     */
    handleCloseAll() {
      this.createDialogVisible = false
      this.chooseDialogVisible = false
      this.$emit('update:visible', false)
      this.$emit('close')
      this.form = {
        type: null,
        name: '',
        description: '',
        isOpen: 0,
        status: 1,
        level: 3,
        ownerDeptIds: '',
        ownerUserIds: '',
        icon: {
          icon: '',
          color: '#EBECF0'
        },
        startTime: '',
        stopTime: ''
      }
      this.selectUserList = []
    },

    /**
     * 去选择项目成员
     */
    handleToChooseUser() {
      if (this.form.ownerUserIds) {
        this.selectedUserIds = this.form.ownerUserIds.split(',')
      } else {
        this.selectUserIds = []
      }
      this.depUserViewDialogShow = true
    },

    /**
     * 选择成员确定
     */
    selectUserChange(userIds, deptIds, userList, deptList) {
      this.form.ownerUserIds = userIds.join(',')
      this.selectUserList = userList
    },

    /**
     * @description: 点击确定
     * @return {*}
     */
    handleConfirmForm() {
      this.$refs.elForm.validate((valid) => {
        if (valid) {
          if (!this.form.icon.icon) {
            this.$message.error('请选择项目图标')
            return false
          } else if (this.form.startTime || this.form.stopTime) {
            if (
              (!this.form.startTime || !this.form.stopTime) || (formatTimeToTimestamp(this.form.startTime) >=
          formatTimeToTimestamp(this.form.stopTime))
            ) {
              this.$message.error('开始时间必须小于结束时间')
              return false
            }
          }

          this.submitData()
        } else {
          console.log('error submit!!')
          return false
        }
      })
    },

    /**
     * 提交数据
     */
    submitData() {
      if (this.loading) return
      const request = this.itemData ? projectUpdateAPI : projectAddAPI
      const params = objDeepCopy(this.form)
      params.icon = this.form.icon.icon
      params.workOwnerRoleList = this.selectUserList
      console.log('save: ', params)

      this.loading = true
      request(params)
        .then(() => {
          this.loading = false
          this.$emit('save-success')
          this.handleCloseAll()
          this.form = {
            type: null,
            name: '',
            description: '',
            isOpen: 0,
            status: 1,
            level: 3,
            ownerDeptIds: '',
            ownerUserIds: '',
            icon: {
              icon: '',
              color: '#EBECF0'
            },
            startTime: '',
            stopTime: ''
          }
          this.selectUserList = []
        })
        .catch(() => {
          this.loading = false
        })
    }
  }
}
</script>

<style lang="scss">
.pm-create-dialog {
  margin-top: 10vh !important;
}
</style>

<style lang="scss" scoped>
.el-form {
  /deep/ .el-form-item__label {
    padding-bottom: 5px;
    line-height: normal;
  }

  .el-select {
    width: 100%;
  }
}

/deep/ .el-form-item {
  .el-form-item__content {
    line-height: initial;
  }
}

.tpl-wrapper {
  width: 100%;
  padding: 10px 36px 36px;

  .tpl-item {
    position: relative;
    width: 48%;
    height: 460px;
    padding: 10px 36px;
    line-height: 1.5;
    cursor: pointer;
    border: 1px solid $--border-color-base;
    border-radius: $--border-radius-base;

    &__pic {
      width: 200px;
      margin: 25px 0 20px -12px;
    }

    &__title {
      font-size: 20px;
      color: $--color-black;

      .wk {
        display: flex;
        align-items: center;
        justify-content: center;
        width: 24px;
        height: 24px;
        margin-right: 5px;
        color: white;
        background-color: $--color-primary;
        border-radius: $--border-radius-base;
      }
    }

    &__sub-title {
      margin-top: 4px;
      font-size: $--font-size-base;
      color: $--color-n500;
    }

    &__desc {
      margin: 15px 0 20px;
      color: $--color-n200;
    }

    .el-button {
      position: absolute;
      bottom: 24px;
      left: 36px;
      padding: 8px 25px;
      font-size: $--font-size-large;
    }
  }
}

.create-wrapper {
  max-height: 60vh;
  padding: 0 16px;
  overflow-y: auto;

  .add-user-btn {
    width: 26px;
    height: 26px;
    font-size: $--font-size-small;
    cursor: pointer;
    border: 1px dotted $--color-text-regular;
    border-radius: 50%;

    &:hover {
      border-color: $--color-primary;
    }
  }

  .xr-avatar,
  .add-user-btn {
    margin: 0 5px;
  }
}
</style>
