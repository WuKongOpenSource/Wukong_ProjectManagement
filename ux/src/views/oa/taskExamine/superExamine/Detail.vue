<template>
  <slide-view
    v-loading="loading"
    :listener-ids="['workbench-main-container']"
    class="d-view"
    @close="hideView">
    <div
      orient="vertical"
      class="detail-main">
      <flexbox
        direction="column"
        align="stretch"
        class="d-container">
        <wk-detail-header
          :subtitle="titleType"
          class="is-shadow"
          :title="detailInfo.content"
          :page-list="pageList"
          :dropdowns="headerHandler"
          @pageChange="pageChange"
          @command="examineHandler">
          <template slot="left">
            <span style="margin-left: 5px;">
              {{ detailInfo.examineStatus == 8 ? '(已作废)' : detailInfo.examineStatus == 13 ? '(已终止)' : '' }}
            </span>
          </template>
        </wk-detail-header>
      </flexbox>
      <div class="d-container-body">
        <detail-head-base :list="headDetails" />
        <flexbox
          class="left-right-wrap is-hidden-right"
          align="stretch">
          <div class="left">
            <o-a-base-info
              :filed-list="fieldList" />
            <!-- 图片附件 -->
            <div class="img-accessory">
              <div class="img-box">
                <img
                  v-for="(item, index) in detailInfo.img"
                  :key="index"
                  v-src="item.url"
                  class="main-img"
                  @click="previewImage(detailInfo.img, index)">
              </div>
              <div class="accessory">
                <file-cell
                  :file-list="detailInfo.file"
                  :show-time="false" />
              </div>
            </div>
          </div>
          <div class="right">
            <examine-info-section
              :examine-type="examineType"
              :record-id="detailInfo.examineRecordId"
              @on-handle="examineHandle" />
          </div>
        </flexbox>
      </div>
    </div>
    <examine-create-view
      v-if="isEdit"
      :category-id="detailInfo.categoryId"
      :type="detailInfo.type"
      :examine-type="examineType"
      :category-title="detailInfo.categoryTitle"
      :action="createAction"
      @save-success="saveSuccess"
      @hiden-view="isEdit = false" />
  </slide-view>
</template>

<script>
import {
  oaExamineGetFieldAPI,
  oaExamineReadAPI,
  oaExamineDeleteAPI
} from '@/api/oa/examine'

import SlideView from '@/components/SlideView'
import WkDetailHeader from '@/components/Page/WkDetailHeader'
import DetailHeadBase from '@/views/oa/components/DetailHeadBase'
import OABaseInfo from '@/views/oa/components/OABaseInfo'
import ExamineInfoSection from '@/components/Examine/ExamineInfoSection'
import FileCell from '@/components/FileCell'
import ExamineCreateView from '../../examine/components/ExamineCreateView'
import CustomFieldsMixin from '@/mixins/CustomFields'
import { isEmpty } from '@/utils/types'

export default {
  /** 详情 */
  name: 'Detail',
  components: {
    SlideView,
    WkDetailHeader,
    DetailHeadBase,
    OABaseInfo,
    ExamineInfoSection,
    FileCell,
    ExamineCreateView
  },
  mixins: [CustomFieldsMixin],
  props: {
    // 详情信息id
    id: [String, Number],
    pageList: Array, // 用于详情切换
    rowIndex: [String, Number],
    examineType: {
      type: String,
      default: ''
    }
  },
  data() {
    return {
      loading: false,
      detail: null,
      fieldList: [], // 基本信息

      categoryName: '',
      titleObj: {
        upcoming: '待办',
        track: '跟踪',
        archive: '归档',
        draft: '草稿'
      },
      detailInfo: {},

      isEdit: false,
      createAction: { type: 'update' },

      headDetails: null
    }
  },
  computed: {
    titleType() {
      return this.titleObj[this.examineType]
    },
    headerHandler() {
      const moreHandles = []
      const permission = this.detailInfo ? this.detailInfo.permission : null
      if (permission) {
        if (permission.isUpdate === 1) {
          moreHandles.push({
            name: '编辑',
            command: 'edit'
          })
        }

        if (permission.isDelete === 1) {
          moreHandles.push({
            name: '删除',
            command: 'delete'
          })
        }
      }

      moreHandles.push({
        name: '打印',
        command: 'print'
      })
      return moreHandles
    }

  },
  watch: {
    id: {
      handler() {
        this.getDetail()
      },
      immediate: true
    }
  },
  created() {
  },
  mounted() {},
  methods: {
    /**
     * 获取基本信息
     */
    getBaseInfo() {
      this.loading = true
      const params = {
        examineId: this.detailInfo.examineId,
        isDetail: 1,
        type: 1
      }
      oaExamineGetFieldAPI(params)
        .then(res => {
          console.log(res.data)
          const list = res.data || []

          list.forEach(item => {
            this.getFormItemDefaultProperty(item, false)
            if (item.fieldName == 'submitDeptId' && item.value.length) {
              this.headDetails[1].value = item.value[0].name || ''
            }

            if (item.formType === 'detail_table') {
              if (!isEmpty(item.value)) {
                item.value = this.getItemValue(item, null, 'update')
              }
              // allForm[item.field] = item.value
            }
          })

          this.fieldList = [
            { name: '基本信息', list }
          ]
          this.loading = false
        })
        .catch(() => {
          this.loading = false
        })
    },

    /**
     * 获取详情信息
     */
    getDetail() {
      oaExamineReadAPI(this.id)
        .then(res => {
          this.detailInfo = res.data || {}
          this.headDetails = [
            { title: '审批名称', value: this.detailInfo.content },
            { title: '提交部门', value: '' },
            { title: '审批类型', value: this.detailInfo.categoryTitle },
            { title: '提交人', value: this.detailInfo.createUser.realname }
          ]
          this.getBaseInfo()
        })
        .catch(() => {
          this.loading = false
        })
    },

    /**
     * 详情页面切换
     */
    pageChange(type) {
      if (type === 'left') {
        if (this.rowIndex > 0) {
          let pageIndex = this.rowIndex
          this.$emit('update:rowIndex', --pageIndex)
        } else {
          this.$message.error('没有更多了')
        }
      } else {
        if (this.rowIndex < this.pageList.length - 1) {
          let pageIndex = this.rowIndex
          this.$emit('update:rowIndex', ++pageIndex)
        } else {
          this.$message.error('没有更多了')
        }
      }
    },

    /**
     * 预览图片
     */
    previewImage(list, index) {
      this.$wkPreviewFile.preview({
        index: index,
        data: list
      })
    },

    /**
     * @description: 编辑
     * @return {*}
     */
    examineHandler(type) {
      if (type == 'edit') {
        this.isEdit = true
        this.createAction.data = {
          batchId: this.detailInfo.batchId,
          img: this.detailInfo.img || [],
          file: this.detailInfo.file || [],
          businessList: this.detailInfo.businessList || [],
          contactsList: this.detailInfo.contactsList || [],
          customerList: this.detailInfo.customerList || [],
          contractList: this.detailInfo.contractList || [],
          receivablesList: this.detailInfo.receivablesList || []
        }
        this.createAction.id = this.detailInfo.examineId

        // 传递审批状态以及recordId
        this.createAction.examineInfo = {
          examineStatus: this.detailInfo.examineStatus,
          recordId: this.detailInfo.examineRecordId
        }
      } else if (type === 'delete') {
        this.$confirm('确定删除?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
          .then(() => {
            oaExamineDeleteAPI({
              examineId: this.id
            }).then(res => {
              this.$message({
                type: 'success',
                message: '删除成功!'
              })
              this.$emit('success')
              this.hideView()
            })
          })
          .catch(() => {
          })
      } else if (type === 'print') {
        this.$router.push({
          name: 'examinePrint',
          query: {
            id: this.detailInfo.examineId,
            templateId: this.detailInfo.categoryId
          }
        })
      }
    },

    /**
     * @description: 编辑保存成功
     * @return {*}
     */
    saveSuccess(data) {
      this.getDetail()
      this.$emit('success')
      if (data == 'save') {
        this.hideView()
      }
    },

    /**
     * 审核操作
     */
    examineHandle(data) {
      // this.detailHeadHandle({ type: 'examine' })
      if (data == 'archive' || data == 'cancellation') {
        this.hideView()
      }
      this.getDetail()
      this.$store.dispatch('GetBacklogNum')
      this.$emit('success')
    },

    /**
     * 点击关闭按钮隐藏视图
     */
    hideView() {
      this.$emit('hide-view')
    }
  }
}
</script>

<style lang="scss" scoped>
@import "../../styles/content.scss";

.detail-main {
  display: flex;
  flex-direction: column;
  height: 100%;
  overflow-y: auto;
  overflow-y: overlay;
}

.d-view {
  position: fixed;
  top: $--detail-view-top;
  right: 0;
  bottom: 0;
  width: $--detail-width-base;
  min-width: 950px;
  padding: 24px;
  background-color: white;

  /deep/ .el-card__body {
    height: 100%;
  }
}

.detail-header {
  .header-icon {
    width: 36px;
    height: 36px;
    margin-right: 8px;
    line-height: 36px;
    text-align: center;
    border-radius: $--border-radius-base;

    .wk {
      font-size: 26px;
      color: white;
    }
  }

  .header-name {
    flex: 1;
    font-size: 16px;
    font-weight: 600;
  }
}

.d-container-body {
  flex: 1;
  margin-top: 8px;
  overflow: auto;

  .detail-head-base {
    padding: 16px;
    background-color: $--color-n20;
    border-radius: 3px;

    /deep/ .base-item {
      flex: 0 0 25%;
    }
  }

  .left-right-wrap {
    margin-top: 15px;

    .left {
      flex: 1;
      padding-right: 40px;

      // 图片 附件
      .img-accessory {
        padding: 0 12px 0 20px;
        margin-bottom: 5px;
        margin-bottom: 10px;
        font-size: 12px;

        .img-box {
          position: relative;
          margin-top: 40px;

          img {
            width: 200px;
            cursor: pointer;
          }
        }

        .accessory {
          margin-top: 25px;
          margin-bottom: 20px;
          color: #2362fb;

          .wukong-file {
            font-size: 13px;
          }
        }
      }

      .b-cont {
        height: unset;
      }

      // 基本信息
      .detail-head-base {
        padding: #{$--interval-base * 2};
        margin-bottom: 16px;
        background-color: $--color-n20;
        border-radius: $--border-radius-base;

        /deep/ .base-item {
          flex: 0 0 25%;
        }
      }
    }

    .right {
      flex-shrink: 0;
      width: 280px;
    }
  }
}
</style>

