<template>
  <div v-loading="loading" class="container">
    <top-border-card
      :move="false">
      <template slot="title-left">
        申请
      </template>
      <div class="main">
        <div class="categorys">
          <flexbox
            v-for="(item, index) in categorys"
            :key="index"
            class="category-item"
            @click.native="selectCategorys(item)">
            <div
              :style="{ backgroundColor: item.iconColor }"
              class="category-icon">
              <i :class="item.iconClass" />
            </div>
            <div class="category-label ">{{ item.categoryTitle }}</div>
          </flexbox>
        </div>
        <el-empty
          v-if="!categorys.length"
          :image-size="240"
          :image="require('@/assets/img/empty/application.png')"
          description="暂无申请" />
        <el-button
          type="primary"
          class="other-btn"
          @click="handerClick">发起其他申请</el-button>
      </div>
    </top-border-card>

    <el-dialog
      v-if="visible"
      title="发起审批类型"
      :visible.sync="visible"
      :close-on-click-modal="false"
      :close-on-press-escape="false">
      <el-tabs
        v-model="activeType"
        nav-mode="more">
        <el-tab-pane
          v-for="(item, index) in moreCategorys"
          :key="index"
          :label="item.groupName"
          :name="item.groupId"
          lazy>
          <div class="categorys">
            <flexbox
              v-for="(sItem, sIndex) in item.superExamineVOList"
              :key="sIndex"
              class="category-item"
              @click.native="selectCategorys(sItem)">
              <div
                :style="{ backgroundColor: sItem.iconColor }"
                class="category-icon">
                <i :class="sItem.iconClass" />
              </div>
              <div class="category-label ">{{ sItem.examineName }}</div>
            </flexbox>
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-dialog>

    <examine-create-view
      v-if="createViewShow"
      :category-id="createInfo.categoryId"
      :type="createInfo.type"
      :category-title="createInfo.categoryTitle"
      :action="createAction"
      @hiden-view="createViewShow = false"
      @save-success="success" />
  </div>
</template>

<script>
import {
  superExaminesRecentlyUsedListAPI,
  superExaminesApplyListAPI
} from '@/api/oa/superExamine'

import ExamineCreateView from '../../../examine/components/ExamineCreateView'
import TopBorderCard from './TopBorderCard'
export default {
  name: 'ApplicationCard', // 申请卡片
  components: {
    TopBorderCard,
    ExamineCreateView
  },
  data() {
    return {
      loading: false,
      categorys: [],
      moreCategorys: [],
      visible: false,
      activeType: '',

      // 新建
      createViewShow: false,
      createInfo: {},
      createAction: { type: 'save' }
    }
  },
  created() {
    this.getDetail()
  },
  methods: {
    /**
     * 审批类型列表
     */
    getDetail() {
      this.loading = true
      superExaminesRecentlyUsedListAPI()
        .then(res => {
          const resData = res.data || []
          this.categorys = resData.map(item => {
            const temps = item.examineIcon ? item.examineIcon.split(',') : []
            item.categoryTitle = item.examineName
            item.categoryId = item.examineId
            if (temps.length > 1) {
              item.iconClass = temps[0]
              item.iconColor = temps[1]
            } else {
              item.iconClass = 'wk wk-approve'
              item.iconColor = '#9376FF'
            }
            return item
          })
          this.getMorecategorys()
        })
        .catch(() => {
          this.loading = false
        })
    },

    /**
     * 获取其他申请
     */
    getMorecategorys() {
      superExaminesApplyListAPI()
        .then(res => {
          this.loading = false
          const resData = res.data || []
          this.activeType = resData[0].groupId
          this.moreCategorys = resData
        })
        .catch(() => {
          this.loading = false
        })
    },

    /**
     * 审批类型选择
     */
    selectCategorys(item) {
      let ungrouped = []
      this.moreCategorys.forEach(item => {
        if (item.groupName == '未分组') {
          ungrouped = item.superExamineVOList.map(item => item.examineId)
        }
      })

      if (ungrouped.includes(item.examineId)) {
        this.$message.error('当前审批信息不完善，请前往后台进行配置')
        return
      }

      this.createInfo = {
        categoryTitle: item.categoryTitle || item.examineName,
        categoryId: item.categoryId || item.examineId
      }
      this.createViewShow = true
    },

    /**
     * 发起其他申请
     */
    handerClick() {
      this.visible = true
    },

    /**
     * @description: 新建成功
     * @return {*}
     */
    success() {
      this.visible = false
      this.$emit('refresh')
    }
  }
}
</script>

<style lang="scss" scoped>
.container {
  .statistics-card {
    min-height: 260px;
  }

  .main {
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    min-height: 192px;
    margin-top: 16px;

    /deep/ .el-empty {
      padding: 0;
    }

    .other-btn {
      display: block;
      margin: 0 auto;
      margin-top: 20px;
    }
  }

  .categorys {
    display: flex;
    flex-wrap: wrap;
    max-height: 40vh;
    overflow-y: auto;

    .category-item {
      flex: 0 0 31%;
      padding: 10px;
      margin: 5px;
      text-align: center;
      cursor: pointer;
      background-color: #f6f6f6;
      border-radius: $--border-radius-base;
    }

    .category-item:hover {
      background-color: #eef1f8;
    }

    .category-icon {
      width: 40px;
      height: 40px;
      line-height: 40px;
      border-radius: $--border-radius-base;

      .wk {
        font-size: 24px;
        color: white;
      }
    }

    .category-label {
      display: flex;
      flex: 1;
      padding-left: 10px;
      overflow: hidden;
      font-size: 14px;
      text-align: left;
      text-overflow: ellipsis;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
    }
  }
}

/deep/ .el-dialog__wrapper {
  .el-dialog {
    width: 700px;
    min-height: 360px;
  }
}

</style>
