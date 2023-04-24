<template>
  <el-dialog
    :visible.sync="showDialog"
    :append-to-body="true"
    :show-close="showCancel"
    :close-on-click-modal="false"
    title="批量导入"
    width="750px"
    @close="closeView">
    <div class="dialog-body">
      <el-steps
        :active="stepsActive"
        simple>
        <el-step
          v-for="(item, index) in stepList"
          :key="index"
          :title="item.title"
          :icon="item.icon"
          :status="item.status" />
      </el-steps>
      <div
        v-if="stepsActive == 1"
        class="sections">
        <div>请选择需要导入的文件</div>
        <div class="content">
          <flexbox class="file-select">
            <el-input
              v-model="file.name"
              :disabled="true" />
            <el-button
              type="primary"
              @click="selectFile">选择文件</el-button>
          </flexbox>
        </div>
        <div
          class="download"
          @click="download">
          点击下载《{{ itemTxt }}导入模板》</div>
        <div class="content content-tips">
          <div>操作步骤：</div>
          <div>1、下载《{{ itemTxt }}导入模板》</div>
          <div>2、打开《{{ itemTxt }}导入模板》将对应字段信息输入或粘贴进本表。为保障粘贴信息被有效导入，请使用纯文本或数字</div>
          <div>3、信息输入完毕，点击“选择文件”按钮，选择excel文件上传</div>
          <div>4、点击“确定”开始进行{{ itemTxt }}导入</div>
        </div>
      </div>

      <div
        v-else-if="stepsActive == 2"
        v-loading="loading"
        element-loading-text="数据导入中"
        element-loading-spinner="el-icon-loading"
        class="sections" />

      <div
        v-else-if="stepsActive == 3"
        v-loading="loading"
        class="sections">
        <div class="result-info">
          <i class="wk wk-success result-info__icon" />
          <p class="result-info__des">数据导入完成</p>
          <p class="result-info__detail">导入总数据<span class="result-info__detail--all">{{ resultData.totalSize }}</span>条，导入成功<span class="result-info__detail--suc"><template v-if="resultData">{{ resultData.totalSize - (resultData.errSize || 0) }}</template></span>条，导入失败<span class="result-info__detail--err">{{ resultData.errSize || 0 }}</span>条</p>
          <el-button
            v-if="resultData && resultData.errSize > 0"
            class="result-info__btn--err"
            type="primary-text"
            style="padding: 0;"
            @click="downloadErrData">下载错误数据</el-button>
        </div>
      </div>
      <input
        id="import-input-file"
        ref="userFileInput"
        type="file"
        @change="uploadFile">
    </div>
    <span
      slot="footer"
      class="dialog-footer">
      <el-button
        v-if="sureTitle"
        type="primary"
        @click="sureClick">{{ sureTitle }}</el-button>
      <el-button
        :class="{ 'is-hidden': !showCancel }"
        @click="closeView">取消</el-button>
    </span>
  </el-dialog>
</template>

<script>
import {
  itemImportTemplateAPI,
  itemExcelImportAPI,
  itemErrorExcelDownAPI
} from '@/api/pm/projectTask'

import { downloadExcelWithResData, verifyFileTypeWithFileName } from '@/utils'
import { itemTypeMap } from '@/views/pm/data'

export default {
  // 批量导入事项
  name: 'BulkImportItem',
  components: {},
  props: {
    show: {
      type: Boolean,
      default: false
    },
    // CRM类型
    itemType: {
      type: String,
      default: ''
    }
  },
  data() {
    return {
      loading: false,
      showDialog: false,
      file: { name: '' },
      stepsActive: 1,
      stepList: [
        {
          icon: 'wk wk-upload',
          title: '上传文件',
          status: 'wait'
        },
        {
          icon: 'wk wk-data-import',
          title: '导入数据',
          status: 'wait'
        },
        {
          icon: 'wk wk-success',
          title: '导入完成',
          status: 'wait'
        }
      ],
      resultData: null
    }
  },
  computed: {
    sureTitle() {
      return {
        1: '立即导入',
        2: '',
        3: '确定'
      }[this.stepsActive]
    },

    itemTxt() {
      return {
        Require: '需求',
        Task: '任务',
        Defects: '缺陷'
      }[this.itemType]
    },

    showCancel() {
      return this.stepsActive != 2
    }
  },
  watch: {
    show: function(val) {
      this.showDialog = val
      this.resetData()
    }
  },
  mounted() {},
  methods: {
    sureClick() {
      if (this.stepsActive == 1) {
        if (this.stepList[0].status == 'finish') {
          this.stepList[1].status = 'process'
          this.stepsActive = 2
          this.updateFile(res => {
            this.stepList[1].status = 'finish'
            this.stepsActive = 3
            if (res.data) {
              this.resultData = res.data
              if (res.data.errSize > 0) {
                this.stepList[2].status = 'error'
              } else {
                this.stepList[2].status = 'finish'
              }
            }
          })
        } else {
          this.$message.error('请选择导入文件')
        }
      } else if (this.stepsActive == 3) {
        this.closeView()
      }
    },

    updateFile(result) {
      if (!this.file.name) {
        this.$message.error('请选择导入文件')
      } else {
        this.loading = true
        itemExcelImportAPI({
          file: this.file,
          projectId: this.$route.params.id,
          taskType: itemTypeMap[this.itemType]
        })
          .then(res => {
            this.loading = false
            if (result) {
              result(res)
            }
            this.$emit('success')
          })
          .catch(() => {
            if (result) {
              result(false)
            }
            this.loading = false
          })
      }
    },

    /**
     * 下载错误
     */
    downloadErrData() {
      this.getImportError(this.resultData.token)
    },

    /**
     * 导入错误下载
     */
    getImportError(token) {
      this.loading = true
      itemErrorExcelDownAPI({
        token
      })
        .then(res => {
          downloadExcelWithResData(res)
          this.loading = false
        })
        .catch(() => {
          this.loading = false
        })
    },

    /**
     * 下载模板操作
     */
    download() {
      itemImportTemplateAPI({ taskType: itemTypeMap[this.itemType] })
        .then(res => {
          downloadExcelWithResData(res)
        })
        .catch(() => {})
    },

    /**
     * 选择文件选择文件
     */
    selectFile() {
      this.$refs.userFileInput.click()
    },

    /**
     * 选择触发
     */
    uploadFile(event) {
      var files = event.target.files
      const file = files[0]
      if (verifyFileTypeWithFileName(file.name)) {
        this.file = file
        this.stepList[0].status = 'finish'
      }
      event.target.value = ''
    },

    /**
     * 关闭
     */
    closeView() {
      this.$emit('close')
    },

    /**
     * 重置数据
     */
    resetData() {
      this.file = { name: '' }
      this.stepList = [
        {
          icon: 'wk wk-upload',
          title: '上传文件',
          status: 'wait'
        },
        {
          icon: 'wk wk-data-import',
          title: '导入数据',
          status: 'wait'
        },
        {
          icon: 'wk wk-success',
          title: '导入完成',
          status: 'wait'
        }
      ]
      this.stepsActive = 1
      this.resultData = null
    }
  }
}
</script>

<style scoped lang="scss">
.el-steps {
  margin-bottom: 16px;

  /deep/ .el-step__title {
    font-size: 14px;
  }

  /deep/ .el-step.is-simple .el-step__arrow::before,
  /deep/ .el-step.is-simple .el-step__arrow::after {
    width: 2px;
    height: 10px;
  }

  /deep/ .el-step.is-simple .el-step__arrow::after {
    transform: rotate(45deg) translateY(3px);
  }

  /deep/ .el-step.is-simple .el-step__arrow::before {
    transform: rotate(-45deg) translateY(-2px);
  }
}

.sections {
  min-height: 215px;
  font-size: 14px;

  .download {
    margin-bottom: 16px;
    color: $--color-primary;
    cursor: pointer;
  }

  /deep/ .el-loading-spinner {
    top: 45%;

    .el-icon-loading {
      font-size: 40px;
      color: $--color-text-secondary;
    }

    .el-loading-text {
    }
  }
}

.content {
  padding: 10px 0;
}

.content-tips {
  line-height: 1.5;
  color: $--color-text-secondary;
}

#import-input-file {
  display: none;
}

.file-select {
  .el-input {
    width: 400px;
  }

  button {
    margin-left: $--interval-base;
  }
}

.is-hidden {
  visibility: hidden;
}

// 结果信息
.result-info {
  padding-top: 30px;
  text-align: center;

  &__icon {
    font-size: 40px;
    color: $--color-primary;
  }

  &__des {
    margin-top: 16px;
    font-size: 14px;
    color: $--color-black;
  }

  &__detail {
    margin-top: 16px;
    color: $--color-text-regular;

    &--all {
      font-weight: 600;
      color: $--color-black;
    }

    &--suc {
      font-weight: 600;
      color: $--color-primary;
    }

    &--err {
      font-weight: 600;
      color: $--color-danger;
    }
  }

  &__btn--err {
    margin-top: 10px;
  }
}

.dialog-footer {
  font-size: 0;
}
</style>
