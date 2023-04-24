<template>
  <div v-loading="loading" class="base-info">
    <div class="form-info">
      <div class="info-left">
        <el-form
          ref="ruleForm"
          :model="ruleForm"
          :rules="rules"
          :disabled="!setAuth"
          label-position="top">
          <el-form-item label="项目名称" prop="name">
            <el-input v-model="ruleForm.name" />
          </el-form-item>
          <el-form-item label="项目时间">
            <el-date-picker
              v-model="ruleForm.time"
              align="left"
              format="yyyy-MM-dd"
              value-format="yyyy-MM-dd"
              type="daterange"
              start-placeholder="开始日期"
              end-placeholder="结束日期" />
          </el-form-item>
          <el-form-item label="项目描述">
            <el-input
              v-model="ruleForm.description"
              type="textarea"
              :rows="3"
              placeholder="请输入内容" />
          </el-form-item>
          <el-button v-if="setAuth" type="primary" @click="submit">保存</el-button>
        </el-form>
      </div>
      <div class="info_cover">
        <span class="title">项目封面</span>
        <div class="cover-photo">
          <wk-ico-select
            v-model="iconCover"
            :disabled="!setAuth"
            icon-color="#0052CC"
            title="项目图标"
            hidden-color
            placement="bottom"
            style="width: 100px; height: 100px;" />
        </div>
      </div>
    </div>
    <div v-if="setAuth">
      <div class="bottom">
        <h2 class="title">归档项目</h2>
        <p class="tips">归档项目后，将无法继续访问和操作该项目内的数据（里程碑、事项、事项关联关系等）， 关联项目可正常访问和操作</p>
        <el-button class="button" type="primary" @click="click('file')">归档</el-button>
      </div>
      <div class="bottom">
        <h2 class="title">删除项目</h2>
        <p class="tips">删除项目会删除项目内的数据（里程碑、事项、事项关联关系等数据），但不会删除关联项目数据</p>
        <el-button class="button" type="danger" @click="click('delete')">删除</el-button>
      </div>
    </div>
  </div>
</template>

<script>
import {
  projectUpdateAPI,
  projectArchiveAPI,
  projectQueryByIdAPI
} from '@/api/pm/manage'

import WkIcoSelect from '@/views/pm/components/WkIcoSelect'

// import moment from 'moment'

export default {
  name: 'BaseInfo', // 项目基本信息
  components: {
    WkIcoSelect
  },
  props: {},
  data() {
    return {
      loading: false,
      projectDetail: {}, // 项目详情
      ruleForm: {
        name: '',
        time: [],
        description: ''
      },
      rules: {
        name: [
          { required: true, message: '请输入项目名称', trigger: 'blur' },
          { min: 1, max: 31, message: '请输入1-31位的项目名称', trigger: 'blur' }
        ]
      },
      iconCover: {
        icon: '',
        color: '#EBECF0'
      }
    }
  },
  computed: {
    projectId() {
      return this.$route.params.setting
    },
    setAuth() {
      return this.$auth('set.editProjectInfo', 'PM')
    }
  },
  created() {
    this.getDetail()
  },
  methods: {
    /**
     * @description: 获取项目详情
     * @return {*}
     */
    getDetail() {
      this.loading = true
      projectQueryByIdAPI({ projectId: this.projectId })
        .then(res => {
          const resData = res.data || {}
          this.projectDetail = resData
          if (resData.startTime && resData.stopTime) {
            this.ruleForm.time = [resData.startTime.split(' ')[0], resData.stopTime.split(' ')[0]]
          }
          this.iconCover.icon = resData.icon
          this.ruleForm.name = resData.name || ''
          this.ruleForm.description = resData.description || ''
          this.loading = false
        }).catch(() => {
          this.loading = false
        })
    },

    /**
     * @description: 保存
     * @return {*}
     */
    submit() {
      this.loading = true
      const params = {
        projectId: this.projectId,
        name: this.ruleForm.name,
        description: this.ruleForm.description,
        startTime: this.ruleForm.time[0],
        stopTime: this.ruleForm.time[1],
        icon: this.iconCover.icon
      }
      projectUpdateAPI(params)
        .then(res => {
          this.loading = false
          this.getDetail()
          this.$message.success('保存成功')
        }).catch(() => {
          this.loading = false
        })
    },
    confirm(msg) {
      return new Promise((resolve, reject) => {
        this.$confirm(
          msg,
          '提示',
          {
            confirmButtonText: '确定',
            cancelButtonText: '取消'
          }
        )
          .then(() => {
            resolve()
          })
          .catch(() => {
            reject()
          })
      })
    },

    /**
     * @description: 跳转到主页
     * @return {*}
     */
    refresh() {
      this.$router.push({
        name: 'participate'
      })
    },

    /**
     * @description: 归档/删除
     * @param {*} type String
     * @return {*}
     */
    click(type) {
      if (type == 'file') {
        this.confirm(`确定要归档项目吗？`)
          .then(() => {
            this.loading = true
            projectArchiveAPI({
              projectId: this.projectId,
              setType: 2
            })
              .then(() => {
                this.$message.success('操作成功')
                this.loading = false
                this.refresh()
              })
              .catch(() => {
                this.loading = false
              })
          })
      } else if (type == 'delete') {
        this.confirm(`确定要删除“${this.projectDetail.name}”项目吗，删除后，项目下的所有数据也将删除，且不可恢复！`)
          .then(() => {
            this.loading = true
            projectArchiveAPI({
              projectId: this.projectId,
              setType: 3
            })
              .then(() => {
                this.loading = false
                this.$message.success('操作成功')
                this.refresh()
              })
              .catch(() => {
                this.loading = false
              })
          })
      }
    }
  }

}
</script>
<style scoped lang='scss'>
  .base-info {
    padding-bottom: 48px;

    .form-info {
      display: flex;

      .info-left {
        flex: 2;
        margin-right: 32px;

        /deep/ .el-form-item__label {
          padding-bottom: 4px;
          line-height: normal;
        }

        .el-date-editor.el-input,
        .el-date-editor.el-input__inner {
          width: 100%;
        }
      }

      .info_cover {
        flex: 1;

        .title {
          color: $--table-header-font-color;
        }

        .cover-photo {
          margin: 8px 0;

          /deep/ .wk-ico-select .wk-ico-preview {
            width: 100px !important;
            height: 100px !important;

            .icon {
              font-size: 56px;
              line-height: 100px;
            }
          }
        }
      }
    }

    .bottom {
      margin-top: 48px;

      .title {
        margin-bottom: 4px;
        font-size: 16px;
        font-weight: bold;
      }

      .tips {
        font-size: 14px;
        color: $--color-text-secondary;
      }

      .button {
        margin-top: 24px;
      }
    }
  }
</style>
