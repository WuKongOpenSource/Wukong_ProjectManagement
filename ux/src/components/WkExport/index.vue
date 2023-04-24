<template>
  <div
    v-loading="loading"
    class="main">
    <el-dialog
      :visible="showCRMExport"
      :title="'导出'+crmTypeName"
      :append-to-body="true"
      :close-on-click-modal="false"
      width="750px"
      @close="closeView">
      <div class="dialog-body">
        <el-steps :active="stepsActive" align-center>
          <el-step title="导出配置" />
          <el-step title="导出完成" />
        </el-steps>
        <div
          v-if="stepsActive==1 && !exportLoading"
          class="step-section">
          <flexbox
            direction="column"
            align="stretch"
            class="field">
            <div class="title">选择字段导出范围：</div>
            <div class="field-list">
              <el-checkbox
                v-model="checkAll"
                :indeterminate="isIndeterminate"
                @change="handleCheckAllChange">全选</el-checkbox>
              <el-checkbox-group
                v-model="selectionList"
                @change="handleChange">
                <el-checkbox
                  v-for="(item,index) in fieldList"
                  :key="index"
                  :label="item.sortId">{{ item.label }}</el-checkbox>
              </el-checkbox-group>
            </div>
            <div class="select-num">已选择{{ selectionList.length }}项</div>
          </flexbox>
          <div v-if="crmType != 'examine'" class="fileType">
            <div class="title">选择导出文件类型</div>
            <el-radio-group v-model="fileType">
              <el-radio :label="1">xls</el-radio>
              <el-radio :label="2">csv</el-radio>
            </el-radio-group>
          </div>
        </div>

        <div
          v-if="exportLoading"
          v-loading="exportLoading"
          element-loading-text="正在导出中"
          element-loading-spinner="el-icon-loading"
          class="step-section" />

        <div
          v-if="stepsActive==2"
          class="step-section">
          <div class="success">
            <img src="@/assets/img/success.png">
            <div>导出完成</div>
          </div>
        </div>

      </div>
      <div
        v-if="!exportLoading"
        slot="footer"
        class="dialog-footer">
        <el-button
          v-if="stepsActive==1"
          type="primary"
          @click="sureClick">立即导出</el-button>
        <el-button
          v-else
          type="primary"
          @click="closeView">确认</el-button>
        <el-button
          @click="closeView">取消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { downloadExcelWithResData } from '@/utils'
import merge from '@/utils/merge'

export default {
  name: 'WkExport',
  data() {
    return {
      loading: false,
      showCRMExport: false,
      crmProps: null,
      crmType: '',
      fieldList: [], // 字段数据
      selectionList: [], // 勾选字段数据
      fileType: 1, // 导出文件类型
      stepsActive: 1, // 步骤

      checkAll: false, // 选择所有状态
      isIndeterminate: false,

      exportLoading: false // 正在导出中
    }
  },
  computed: {
    crmTypeName() {
      if (this.crmProps && this.crmProps.typeName) {
        return this.crmProps.typeName
      }
      return (
        {
          customer: '客户',
          leads: '线索',
          contacts: '联系人',
          applet: '名片',
          business: '商机',
          contract: '合同',
          receivables: '回款',
          product: '产品',
          invoice: '发票',
          receivablesPlan: '回款计划',
          examine: '审批'
        }[this.crmType] || ''
      )
    }
  },
  methods: {
    /**
     * 展示导出弹窗
     */
    export(crmType, props) {
      this.crmType = crmType
      /**
       * typeName 标题
       * params 确定传参
       * request 确定请求
       * fieldRequest 字段请求
       * fieldParams 字段参数
       * idKey 默认id
       * selectsKey sortIds  用于提交最终选择 idKey 值的key
       */
      this.crmProps = merge({ idKey: 'id', selectsKey: 'sortIds' }, props || {})
      this.showCRMExport = true
      this.getFieldList()
    },
    /**
     * 确认导出
     */
    sureClick() {
      if (this.selectionList.length == 0) {
        this.$message.error('请选择字段导出范围')
        return
      }
      this.exportLoading = true
      const params = {
        isXls: this.fileType,
        ... this.crmProps.params
      }

      if (this.crmType === 'examine') {
        const data = []
        this.fieldList.forEach(item => {
          this.selectionList.forEach((sItem, sIndex) => {
            if (item.sortId == sItem) {
              data.push({
                name: item.label,
                fieldKey: item.sortId,
                sort: sIndex
              })
            }
          })
        })

        params['fieldList'] = data
      } else {
        params[this.crmProps.selectsKey] = this.selectionList
      }

      this.crmProps.request(params)
        .then(res => {
          downloadExcelWithResData(res)
          this.exportLoading = false
          this.stepsActive = 2
        })
        .catch(() => {
          this.exportLoading = false
        })
    },
    /**
     * 获取导出字段
     */
    getFieldList() {
      this.loading = true
      let params = {}
      if (this.crmProps.fieldParams) {
        params = this.crmProps.fieldParams
      } else if (this.crmProps.isSeas) {
        if (this.crmProps.poolId) {
          params.poolId = this.crmProps.poolId
        }
      }
      const request = this.crmProps.fieldRequest
      request(params)
        .then(res => {
          res.data = res.data.filter(o => {
            if (!o.hasOwnProperty('formType')) return true
            return o.formType &&
              ![
                'handwriting_sign',
                'desc_text'
              ].includes(o.formType)
          })
          const fieldList = []
          const selectionList = []

          for (let index = 0; index < res.data.length; index++) {
            const element = res.data[index]
            var width = 0
            if (!element.width) {
              if (element.name && element.name.length <= 6) {
                width = element.name.length * 15 + 45
              } else {
                width = 140
              }
            } else {
              width = element.width
            }
            fieldList.push({
              prop: element.fieldName,
              formType: element.formType,
              label: element.name,
              width: width,
              sortId: element[this.crmProps.idKey]
            })

            selectionList.push(element[this.crmProps.idKey])
          }
          // 审批导出特殊处理
          if (this.crmProps.hasOwnProperty('fieldList')) {
            fieldList.push(...this.crmProps.fieldList)
            selectionList.push(...this.crmProps.selectionList)
          }

          this.fieldList = fieldList
          this.selectionList = selectionList
          this.checkAll = true
          this.loading = false
        })
        .catch(() => {
          this.loading = false
        })
    },
    /**
     * 选中数据
     */
    handleSelectionChange(val) {
      this.selectionList = val
    },
    // 关闭操作
    closeView() {
      if (this.exportLoading) { // 正在导出 禁止关闭
        return
      }
      this.showCRMExport = false
      this.stepsActive = 1
      this.selectionList = []
    },
    /**
     * 选中所有
     */
    handleCheckAllChange(val) {
      this.selectionList = val ? this.fieldList.map(item => item.sortId) : []
      this.isIndeterminate = false
    },
    /**
     * 选择
     */
    handleChange(value) {
      const checkedCount = value.length
      this.checkAll = checkedCount === this.fieldList.length
      this.isIndeterminate = checkedCount > 0 && checkedCount < this.fieldList.length
    }
  }
}
</script>

<style lang="scss" scoped>
.step-section {
  height: 350px;
  margin-top: 10px;

  .field,
  .fileType {
    padding: 10px;
    font-size: 15px;

    .title {
      margin-bottom: 10px;
      font-weight: bold;
    }
  }

  .field {
    height: 300px;
    border: 1px solid #e6e6e6;
    border-radius: 4px;

    .field-list {
      flex: 1;
      overflow-y: auto;
    }

    .select-num {
      margin-top: 8px;
    }
  }

  .success {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    height: 100%;
    font-weight: 600;
    color: $--color-text-primary;
    text-align: center;

    img {
      width: 50px;
      height: 50px;
      margin-bottom: 10px;
    }
  }
}

.dialog-footer {
  text-align: right;
}

/deep/ .el-step__title {
  font-size: 17px;
}

/deep/ .el-dialog__body {
  padding: 20px 30px;
  border-top: 1px solid #e6e6e6;
}

/deep/ .el-checkbox {
  margin-right: 20px;
  margin-bottom: 8px;
}

/deep/ .el-loading-spinner {
  top: 45%;

  .el-icon-loading {
    font-size: 40px;
    color: #2362fb;
  }

  .el-loading-text {
    margin: 8px 0;
    color: #2362fb;
  }
}
</style>
