<template>
  <wk-head-section
    v-if="examineFlowList && examineFlowList.length > 0"
    v-loading="loading"
    label="审批流信息">

    <el-popover
      slot="right"
      ref="popover"
      v-model="showFlowPopover"
      :placement="placement"
      width="300"
      trigger="click">
      <check-flow
        :id="recordId"
        ref="checkFlow"
        :show="showFlowPopover"
        :examine-type="examineType"
        @close="showFlowPopover=false" />
      <el-button
        slot="reference"
        style="margin-right: 8px;"
        type="text"
        @click.stop="">查看历史审批流程</el-button>
    </el-popover>

    <!-- 固定 -->
    <el-timeline>
      <el-timeline-item
        v-for="(item, index) in examineFlowList"
        :key="index"
        hide-timestamp>
        <i
          v-if="item.examineType != 7"
          slot="dot"
          :class="getStatusIcon(item.examineStatus)"
          :style="{
            color: getStatusColor(item.examineStatus)
          }"
          class="examine-item-icon" />
        <i
          v-else
          slot="dot"
          style="color: #5e6c84;"
          class="examine-item-icon wk wk-source-line" />
        <div class="examine-item">
          <!-- 审批节点只有一人 -->
          <div class="examine-item__hd ei">
            <div>
              <span class="ei-status">{{ item.name ? item.examineStatus == 12 ? item.name : item.name + '：' : '' }}{{ item.examineType == 7 ? '抄送' : getStatusName(item.examineStatus) }}</span>
              <el-tag
                v-if="examineCategory(item.type) && item.examineType != 7 && item.userList && item.userList.length > 1"
                :disable-transitions="true">{{ examineCategory(item.type) }}</el-tag>
              <flexbox
                v-if="singleNodeDisableUserShow(item)"
                style="margin-top: 10px;">
                <xr-avatar
                  :name="getDetailName(item)"
                  :size="20" />
                <span class="ei-name">
                  {{ getDetailName(item) }}
                  {{ item.examineType == 7 && item.userList[0].userStatus == 0 ? '(已停用)' : '' }}
                  <span v-if="item.userList[0].hasOwnProperty('seekUser') && item.userList[0].seekUser" class="other-people">
                    <i class="wk wk-new-employee" />
                    已征求他人意见</span>
                </span>
                <!-- examineStatus
                  11 转审  8 作废  13 终止
                  examineType
                  7 抄送
                -->
                <span
                  v-if="item.userList[0].examineStatus != 11 && item.examineType !=7 && item.userList[0].examineStatus != 13 && item.userList[0].examineStatus != 8"
                  class="ei-status">{{ getStatusName(item.userList[0].examineStatus) }}</span>
              </flexbox>
            </div>
            <div v-if="item.userList.length === 1" class="ei-time">
              <span>{{ item.userList[0].examineTime }}</span>

              <!-- 审批异常转交/自动同意 -->
              <div v-if="showOneUserExamine(item)" class="examine-error">
                <i class="el-icon-warning" />
                {{ showOneUserExamine(item) }}
              </div>

              <!-- 审批限时处理 -->
              <div
                v-if="(item.userList[0].duration || item.userList[0].passFlag) && (examineAdvancedConfigVO.advancedLimitTimeVO && examineAdvancedConfigVO.advancedLimitTimeVO.handleType != 1) && item.userList[0].userStatus != 0"
                class="ei-time-limit"
                :style="autoExamineText(item.userList[0].duration,item.userList[0].examineStatus,item.userList[0].passFlag,item.userList[0])['class']">{{ autoExamineText(item.userList[0].duration,item.userList[0].examineStatus,item.userList[0].passFlag,item.userList[0])['text'] }}
              </div>

              <!-- 限时禁用处理 -->
              <div
                v-if="(item.userList[0].duration && item.userList[0].userStatus == 0) && timeLimitStyleShow"
                class="examine-error">
                <i class="el-icon-warning" />
                {{ limitDisableShow() }}
              </div>

              <!-- 抄送提示文本 -->
              <!-- <div
                v-if="carbonCopy(item.examineType,item.userList)"
                class="examine-error">
                <i class="el-icon-warning" />
                {{ carbonCopy(item.examineType,item.userList) }}
              </div> -->
            </div>
          </div>
          <!-- 审批节点有多人人 -->
          <div v-if="item.userList.length > 1" class="examine-item__bd">
            <div
              v-for="(subItem, subIndex) in item.userList"
              :key="subIndex"
              class="examine-item__hd ei">
              <flexbox
                v-if="disableAvatarUserShow(subItem)"
                :class="subItem.hasOwnProperty('handlerType') && subItem.handlerType == 1 ? 'examine-care-of' : ''">
                <i
                  v-if="subItem.hasOwnProperty('handlerType') && subItem.handlerType == 1"
                  class="wk wk-icon-reply"
                  :class="subItem.hasOwnProperty('handlerType') && subItem.handlerType == 1 ? 'care-of' : ''" />
                <xr-avatar
                  :name="$getUserName(subItem)"
                  :size="20" />
                <span class="ei-name">
                  {{ $getUserName(subItem) || subItem.outerUserEmail }}
                  {{ item.examineType == 7 && subItem.userStatus == 0 ? '(已停用)' : '' }}
                  <span v-if="subItem.hasOwnProperty('seekUser') && subItem.seekUser" class="other-people">
                    <i class="wk wk-new-employee" />
                    已征求他人意见</span>
                </span>

                <!-- examineStatus
                  11 转审  8 作废  13 终止
                  examineType
                  7 抄送
                -->
                <span
                  v-if="subItem.examineStatus != 11 && item.examineType != 7 && subItem.examineStatus != 13 && subItem.examineStatus != 8"
                  class="ei-status">{{ getStatusName(subItem.examineStatus) }}</span>
                <!-- class="ei-status">{{ item.type != 3 ? getStatusName(subItem.examineStatus) : '' }}</span> -->
                <span
                  v-if="subItem.examineStatus == 11"
                  class="ei-status">已转审</span>
              </flexbox>
              <div v-if="subItem.userStatus != 0 && (subItem.passFlag != 102 && subItem.passFlag != 202)" class="ei-time">{{ subItem.examineTime }}</div>

              <!-- 限时 -->
              <div
                v-if="((subItem.duration || subItem.passFlag) && (examineAdvancedConfigVO.advancedLimitTimeVO && examineAdvancedConfigVO.advancedLimitTimeVO.handleType != 1) && subItem.userStatus != 0)"
                class="ei-time-limit"
                :style="autoExamineText(subItem.duration,subItem.examineStatus,subItem.passFlag,subItem)['class']">
                {{ autoExamineText(subItem.duration,subItem.examineStatus,subItem.passFlag,subItem)['text'] }}
              </div>

              <!-- 禁用账户异常展示 -->
              <div
                v-if="disableUserShow(subItem.passFlag, subItem.examineStatus,subItem)"
                class="examine-error">
                <i class="el-icon-warning" />
                {{ disableUserShow(subItem.passFlag, subItem.examineStatus,subItem) }}
              </div>
            </div>

            <!-- 终止异常展示 -->
            <div
              v-if="item.passFlag == 302"
              class="examine-error">
              <i class="el-icon-warning" />
              转交人不存在已自动终止
            </div>

            <!-- 节点异常展示 -->
            <div
              v-if="(showMultiplayerExamine(item.passFlag,item.userList, true) && item.examineType != 7)"
              class="examine-error">
              <i class="el-icon-warning" />
              {{ showMultiplayerExamine(item.passFlag,item.userList) }}
            </div>

            <!-- 抄送提示文本 -->
            <!-- <div
              v-if="carbonCopy(item.examineType,item.userList)"
              class="examine-error">
              <i class="el-icon-warning" />
              {{ carbonCopy(item.examineType,item.userList) }}
            </div> -->

          </div>
        </div>
      </el-timeline-item>
    </el-timeline>

    <div
      class="handle"
      :style="!examineMoreHandle.length || isStageFlow ? 'justify-content: start;' : ''">
      <el-button
        v-if="examineInfo.isCheck == 1"
        type="success"
        icon="wk wk-success"
        @click="examineHandle(1)">通过</el-button>
      <el-button
        v-if="examineInfo.isCheck == 1"
        type="danger"
        :style="!examineMoreHandle.length || isStageFlow ? 'margin-left: 10px;' : ''"
        icon="wk wk-close"
        @click="examineHandle(2)">拒绝</el-button>

      <el-dropdown
        v-if="examineMoreHandle.length"
        :style="isStageFlow ? 'margin-left: 10px;' : ''"
        class="more-handle"
        @command="moreHandler">
        <el-button>更多操作</el-button>
        <el-dropdown-menu slot="dropdown">
          <el-dropdown-item
            v-for="(item,index) in examineMoreHandle"
            :key="index"
            :command="item.value">
            <template v-if="item.value =='forwardOthers'">
              <el-dropdown
                v-if="!isStageFlow"
                class="more-handle"
                @command="moreHandler">
                <div>转他人处理</div>
                <el-dropdown-menu slot="dropdown">
                  <el-dropdown-item command="selectUser">选择员工</el-dropdown-item>
                  <el-dropdown-item command="external">选择外部联系人</el-dropdown-item>
                </el-dropdown-menu>
              </el-dropdown>
              <span
                v-else
                @click="moreHandler('selectUser')">转他人处理</span>
            </template>
            <template v-else>
              {{ item.label }}
            </template>
          </el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
    </div>

    <!-- 操作 -->
    <examine-handle
      :id="id"
      ref="examineHandleRef"
      :show="examineHandleShow"
      :record-id="recordId"
      :examine-type="examineType"
      :detail="examineInfo"
      :flow-id="flowId"
      :status="examineHandleInfo.status"
      @close="examineHandleShow = false"
      @emailExamine="emailExamineHandle"
      @save="examineHandleClick" />

    <!-- 转他人处理 -->
    <wk-dep-user-dialog
      v-if="userViewDialogShow"
      :props="{
        showUser: true,
        showDept: false,
        showDisableUser: false,
        disableUserLabel: '员工'
      }"
      radio
      :visible.sync="userViewDialogShow"
      @change="selectUserChange"
    />

    <!-- 征求他人意见 -->
    <solicit-others
      v-if="solicitOthersShow"
      v-model="solicitOthersShow"
      @submitParams="solicitOthersHandler" />

    <!-- 选择外部联系人 -->
    <el-dialog
      v-if="externalDialogShow"
      :visible.sync="externalDialogShow"
      :close-on-click-modal="false"
      :append-to-body="true"
      title="外部联系人"
      width="30%">
      <span>请输入外部联系人邮箱</span>
      <el-input
        v-model="email"
        style="margin-top: 10px;" />
      <span slot="footer" class="dialog-footer">
        <el-button @click="externalDialogShow = false">取消</el-button>
        <el-button type="primary" @click="verifyEmail">确定</el-button>
      </span>
    </el-dialog>
  </wk-head-section>
</template>
<script type="text/javascript">
import {
  superExamineRecordAPI,
  forwardOthersAPI,
  solicitOthersAPI,
  useExamineEmailSendAPI,
  actionExamineAPI
} from '@/api/oa/superExamine'

import { examineSuperExaminesUseExamineEmailTokenForAuditExamineAPI } from '@/api/examine/superExamine.js'
import ExamineHandle from './ExamineHandle' // 审批操作理由
import CheckFlow from './CheckFlow' // 审批流程
import WkHeadSection from '@/components/NewCom/WkHeadSection'
import WkDepUserDialog from '@/components/NewCom/WkUserDialogSelect/Dialog'
import SolicitOthers from './SolicitOthers' // 征求他人意见

import CheckStatusMixin from '@/mixins/CheckStatusMixin'
import { wayTypeObj } from '@/components/ApprovalFlow/nodeModel'
import { regexIsCRMEmail } from '@/utils'
import { isEmpty } from '@/utils/types'
import moment from 'moment'
import NP from 'number-precision'

// 审核信息 config 1 固定 0 自选
export default {
  name: 'ExamineInfoSection', // 合同审核操作
  components: {
    ExamineHandle,
    CheckFlow,
    WkHeadSection,
    WkDepUserDialog,
    SolicitOthers
  },
  filters: {},
  mixins: [CheckStatusMixin],
  props: {
    examineType: {
      type: String,
      default: ''
    },
    placement: {
      type: String,
      default: 'bottom'
    },
    id: [String, Number],
    // 审批流id
    recordId: [String, Number],
    ownerUserId: [String, Number],
    config: {
      type: Object,
      default: () => {}
    },
    examineRecord: {
      type: Object,
      default: () => {}
    },
    externalMailbox: {
      type: Boolean,
      default: false
    },
    isStageFlow: { // 是否为  阶段流程
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      loading: false,
      examineInfo: {}, // 审核信息
      showFlowPopover: false,
      examineHandleInfo: { status: 1 }, // 1 审核通过 2 审核拒绝 4 已撤回
      examineHandleShow: false, // 审核操作
      userViewDialogShow: false, // 转他人处理
      solicitOthersShow: false, // 征求他人意见
      externalDialogShow: false, // 选择外部联系人
      email: '', // 外部联系人邮箱
      flowId: '', // 当前审批节点flowId

      examineAdvancedConfigVO: {}
    }
  },
  computed: {
    examineFlowList() {
      return this.examineInfo.examineFlowList ? this.examineInfo.examineFlowList : []
    },

    examineMoreHandle() {
      let moreActions = [
        { label: '转他人处理', value: 'forwardOthers' },
        { label: '征求他人意见', value: 'solicitOthers' },
        { label: '终止', value: 'termination' }
      ]

      const isCheck = this.examineInfo.isCheck
      const isRecheck = this.examineInfo.isRecheck
      if (this.externalMailbox) {
        if (isCheck) {
          moreActions = [
            { label: '终止', value: 'termination' }
          ]
        } else {
          moreActions = []
        }
      } else if (isCheck && isRecheck) {
        moreActions.push(
          { label: '邮件催办', value: 'emailReminder' },
          { label: '归档', value: 'archive' },
          { label: '作废', value: 'cancellation' },
          { label: '撤回', value: 'retract' }
        )
      } else if (!isCheck && isRecheck) {
        moreActions = [
          { label: '邮件催办', value: 'emailReminder' },
          { label: '撤回', value: 'retract' },
          { label: '归档', value: 'archive' },
          { label: '作废', value: 'cancellation' }
        ]
      } else if (!isCheck && !isRecheck) {
        moreActions = []
      }

      // 人资去除操作
      if (this.examineType == 'hrm_salary') {
        const hrmHandler = []
        const hrmType = ['cancellation']
        moreActions.forEach((item, index) => {
          if (!hrmType.includes(item.value)) {
            hrmHandler.push(item)
          }
        })
        moreActions = hrmHandler
      }

      // 阶段流程去除操作
      if (this.isStageFlow) {
        const stageFlowHandler = []
        const isStageFlowType = ['termination', 'emailReminder', 'archive', 'cancellation']
        moreActions.forEach(item => {
          if (!isStageFlowType.includes(item.value)) {
            stageFlowHandler.push(item)
          }
        })

        moreActions = stageFlowHandler
      }

      return moreActions
    },

    examineCategory() {
      return function(type) {
        return wayTypeObj[type]
      }
    },

    /**
     * 审批异常返回文本
     * flag
     * 102 审批人不存在自动同意
     * 202 审批人不存在自动转交
     * 104 审批人禁用自动同意
     * 204 审批人禁用自动转交
     *
     * examineStatus  // 审批状态
     * 1 已通过 3 审核中
     *
     */
    passFlagNumText() {
      return function(flag, examineStatus, userList = []) {
        console.log(flag, examineStatus, userList)
        const leftText = {
          1: '审批人不存在，',
          2: '审批人不存在，',
          102: '未找到审批人，',
          202: '未找到审批人，',
          104: '审批人不存在，',
          204: '审批人不存在，'
        }

        // const joinText = {
        //   1: '已',
        //   11: '已',
        //   3: '将'
        // }

        const rightText = {
          1: '自动同意',
          2: `自动转交给 ${this.examineAdvancedConfigVO.nodeHandleUser?.map(item => this.$getUserName(item)).join()}`,
          102: '自动同意',
          202: `自动转交给 ${this.examineAdvancedConfigVO.nodeHandleUser?.map(item => this.$getUserName(item)).join()}`,
          104: '自动同意',
          204: `自动转交给 ${this.examineAdvancedConfigVO.nodeHandleUser?.map(item => this.$getUserName(item)).join()}`
        }

        // return leftText[flag] + joinText[examineStatus] + rightText[flag]
        if (examineStatus != 0) {
          return leftText[flag] + '已' + rightText[flag]
        } else {
          return leftText[flag] + '将' + rightText[flag]
        }
      }
    },

    /**
     * 单人审批异常是否展示
     * flag
     * 102 审批人不存在自动同意
     * 202 审批人不存在自动转交
     * 104 审批人禁用自动同意
     * 204 审批人禁用自动转交
     */
    showOneUserExamine() {
      return function(item) {
        const { flag, userList, examineType } = item
        const user = userList[0]
        if (examineType == 7) return false
        if ((!flag || [101, 201, 103, 203].includes(flag)) && !user.hasOwnProperty('isAdvance')) return ''

        const isAdvance = user.isAdvance || null // 审批人未找到情况下提前预览审批流

        if ([104, 204].includes(flag)) {
          return this.passFlagNumText(flag, user.examineStatus)
        } else if (flag == 102 || (isAdvance && isAdvance == 1)) {
          return this.passFlagNumText(102, user.examineStatus)
        } else if (flag == 202 || (isAdvance && isAdvance == 2)) {
          return this.passFlagNumText(202, 3, userList)
        }
      }
    },

    /**
     * 多人审批异常是否展示
     *
     * flag
     * 102 审批人不存在自动同意
     * 202 审批人不存在自动转交
     * 104 审批人禁用自动同意
     * 204 审批人禁用自动转交
     *
     * examineType 多人审批类型
     * 1 依次审批 2 会签 3 或签
     *
     */
    showMultiplayerExamine() {
      return function(flag, userList) {
        const isAdvance = userList.map(item => item.isAdvance).includes(2) // 预览审批流程
        if ([101, 201, 103, 203].includes(flag) || (!flag && !isAdvance)) return ''
        const forbiddenShow = userList.map(item => item.passFlag).includes(104) || userList.map(item => item.passFlag).includes(204) // 禁用员工是否展示
        const timeLimitShow = userList.map(item => item.hasOwnProperty('duration')).includes(true) // 限时是否展示
        if (isAdvance) {
          return this.passFlagNumText(202, 1, userList)
        } else if (timeLimitShow || forbiddenShow) {
          return ''
        } else {
          return this.passFlagNumText(flag, 1, userList)
        }
      }
    },

    /**
     * 禁用账户是否展示
     */
    disableUserShow() {
      return function(passFlag, status, item) {
        if (passFlag != 104 && passFlag != 204) return ''

        if (passFlag == 204 && item.examineStatus == 11) {
          return this.passFlagNumText(passFlag, status)
        } else if (passFlag == 104 && status == 1) {
          return this.passFlagNumText(passFlag, status)
        }
      }
    },

    /**
     * 限时禁用展示样式
     */
    limitDisableShow() {
      return function() {
        const flag = this.examineAdvancedConfigVO.nodeHandleType
        return this.passFlagNumText(flag, 3, 0)
      }
    },

    /**
     * 限时自动提醒不展示样式
     */
    timeLimitStyleShow() {
      const advancedLimitTimeVO = this.examineAdvancedConfigVO.advancedLimitTimeVO || {}
      if (isEmpty(advancedLimitTimeVO)) {
        return false
      } else {
        // handleType 1.自动提醒  2.自动转交  3.自动同意
        const { handleType } = advancedLimitTimeVO
        if (handleType == 1) {
          return false
        } else {
          return true
        }
      }
    }
  },
  watch: {
    recordId: {
      handler(val) {
        if (val) {
          this.examineInfo = {}
          this.getFlowStepOAList()
          if (this.$refs.checkFlow) {
            this.$refs.checkFlow.getDetail()
          }
        }
      },
      deep: true,
      immediate: true
    },
    examineFlowList: {
      handler(val) {
        val.forEach(item => {
          item.userList.forEach(sItem => {
            if (sItem.examineStatus == 3) {
              this.flowId = item.flowId
            }
          })
        })
      },
      immediate: true,
      deep: true
    },
    examineRecord: {
      handler(val) {
        if (!isEmpty(val)) {
          this.examineInfo = this.examineRecord
        }
      },
      deep: true,
      immediate: true
    }
  },
  methods: {
    emailExamineHandle(content, flag) {
      if (flag) {
        this.$message.error('请输入审批意见（必填）')
        return
      }
      examineSuperExaminesUseExamineEmailTokenForAuditExamineAPI({
        emailToken: this.$route.query.k,
        status: this.examineHandleInfo.status,
        remarks: content
      }).then(res => {
        this.$message.success('操作成功')
        this.$refs.examineHandleRef.resetInfo()
        this.examineHandleShow = false
        this.$emit('success')
      }).catch(e => {})
    },

    /**
     * 详情
     */
    getFlowStepOAList() {
      this.loading = true
      superExamineRecordAPI({ recordId: this.recordId })
        .then(res => {
          this.loading = false
          const resData = res.data || {}
          this.examineAdvancedConfigVO = resData.examineAdvancedConfigVO
          this.examineInfo = resData
        })
        .catch(() => {
          this.loading = false
        })
    },

    /**
     * 更多操作
     */
    moreHandler(command) {
      if (command == 'selectUser') { // 转他人处理(选择员工)
        this.userViewDialogShow = true
      } else if (command == 'external') { // 转他人处理(外部联系人)
        this.externalDialogShow = true
      } else if (command == 'solicitOthers') { // 征求他人意见
        this.solicitOthersShow = true
      } else if (command == 'termination') { // 终止
        this.examineHandle(13)
      } else if (command == 'emailReminder') { // 邮件催办
        this.emailReminder()
      } else if (command == 'archive') { // 归档
        this.examineHandle(14)
      } else if (command == 'cancellation') { // 作废
        this.examineHandle(8)
      } else if (command == 'retract') { // 撤回
        this.examineHandle(4)
      }
    },

    /**
     * 撤回审核 通过 拒绝 归档 作废
     */
    examineHandle(status) {
      this.examineHandleInfo.status = status
      this.examineHandleShow = true
    },

    /**
     * @description: 归档/作废
     * @param {*} status 3归档  4作废
     * @return {*}
     */
    archivedObsolete(status, type) {
      const params = {
        archiveOrVoidFlag: status,
        recordId: this.recordId
      }
      actionExamineAPI(params)
        .then(res => {
          this.$message.success('操作成功')
          this.getFlowStepOAList()
          this.$emit('on-handle', type)
        })
    },

    /**
     * @description: 邮件催办
     * @return {*}
     */
    emailReminder() {
      useExamineEmailSendAPI({ examineRecordId: this.recordId })
        .then(res => {
          this.$message.success('操作成功')
          this.getFlowStepOAList()
        })
    },

    /**
     * 审批操作点击
     */
    examineHandleClick(data) {
      this.getFlowStepOAList()
      if (this.$refs.checkFlow) {
        this.$refs.checkFlow.getDetail()
      }
      this.$emit('on-handle', data)
    },

    /**
     * 获取名称
     */
    getDetailName(data) {
      if (!data.userList || data.userList.length === 0) {
        return 'XX'
      } else if (data.userList.length === 1) {
        return this.$getUserName(data.userList[0]) ? this.$getUserName(data.userList[0]) : data.userList[0].outerUserEmail
      } else if (data.examineType === 5) {
        return `${data.userList.length}人${wayTypeObj[1]}`
      } else {
        return `${data.userList.length}人${wayTypeObj[data.type]}`
      }
    },

    /**
     * 转他人处理
     */
    selectUserChange(usersIds) {
      const params = {
        examineFlowFinalUser: {
          userId: usersIds[0]
        },
        flowFinalId: this.flowId,
        recordId: this.recordId
      }

      forwardOthersAPI(params)
        .then(res => {
          this.getFlowStepOAList()
        })
    },

    /**
     * 验证邮箱
     */
    verifyEmail() {
      if (regexIsCRMEmail(this.email)) {
        const params = {
          examineFlowFinalUser: {
            email: this.email
          },
          flowFinalId: this.flowId,
          recordId: this.recordId
        }

        forwardOthersAPI(params)
          .then(res => {
            this.getFlowStepOAList()
          })
        this.email = null
        this.externalDialogShow = false
      } else {
        this.$message.error('邮箱格式有误')
      }
    },

    /**
     * 征求他人意见
     */
    solicitOthersHandler(data) {
      const params = {
        ...data,
        insertTarget: this.flowId,
        recordId: this.recordId
      }
      solicitOthersAPI(params)
        .then(() => {
          this.getFlowStepOAList()
        })
    },

    /**
     * 自动审批提示文本
     * @param {*} millimeter 现在时间距离创建时间的毫秒数
     * @param {*} examineStatus 审核状态
     * @param {*} passFlag 103 限时自动同意   203 限时自动转交
     * @param {*} user
     */
    autoExamineText(millimeter, examineStatus, passFlag, user) {
      const time = Number(millimeter || 0)
      const limitTimeVO = this.examineAdvancedConfigVO?.advancedLimitTimeVO // 审批限时高级配置
      if (!limitTimeVO || user.userStatus == 0 || !time) return {}

      /**
       *  handleType 限时处理方式 2 自动同意  3 自动转交
       *  handleUserList 处理人
       *  limitTime 后台配置时间
       *  timeType 后台配置时间单位
       **/
      const { handleType, handleUserList, limitTime } = limitTimeVO
      const timeType = limitTimeVO.limitTimeUnit

      // 后台配置时间 - 距离当前时间 = 剩余时间
      const timeObj = {
        'minute': Number(NP.minus(NP.times(limitTime, 60, 1000), time)),
        'hour': Number(NP.minus(NP.times(limitTime, 60, 60, 1000), time)),
        'day': Number(NP.minus(NP.times(limitTime, 24, 60, 60, 1000), time))
      }

      const timeTypeObj = {
        'minute': '分钟',
        'hour': '小时',
        'day': '天'
      }

      if (passFlag == 103) {
        return {
          text: `【限时审批】流程在审批人处停留超过${limitTime}${timeTypeObj[timeType]}，系统已自动同意`,
          class: 'background-color: #EBECF0;color:#999'
        }
      } else if (passFlag == 203) {
        return {
          text: `【限时审批】流程在审批人处停留超过${limitTime}${timeTypeObj[timeType]}，系统已【自动转交】给${handleUserList.map(item => this.$getUserName(item)).join()}`,
          class: 'background-color: #EBECF0;color:#999'
        }
      }

      if (timeObj[timeType] > 0) { // 限时处理
        const day = moment.duration(timeObj[timeType]).days() ? moment.duration(timeObj[timeType]).days() + '天' : '' // 剩余时间是否包含天
        const hour = moment.duration(timeObj[timeType]).hours() ? moment.duration(timeObj[timeType]).hours() + '小时' : '' // 剩余时间是否包含小时
        const minuter = moment.duration(timeObj[timeType]).minutes() ? moment.duration(timeObj[timeType]).minutes() + '分钟' : '' // 剩余时间是否包含分钟
        if (handleType == 3) {
          return {
            text: !day && !hour && !minuter ? '1分钟后自动同意' : day + hour + minuter + '后自动同意',
            class: 'color: red'
          }
        } else if (handleType == 2) {
          return {
            text: !day && !hour && !minuter ? '1分钟后自动转交给' + handleUserList.map(item => this.$getUserName(item)).join() : day + hour + minuter + '后自动转交给' + handleUserList.map(item => this.$getUserName(item)).join(),
            class: 'color: red'
          }
        }
      } else { // 超时处理
        if (handleType == 2) {
          return {
            text: `【限时审批】流程在审批人处停留超过${limitTime}${timeTypeObj[timeType]}，系统${examineStatus == 1 ? '已' : '将'}【自动转交】给${handleUserList.map(item => this.$getUserName(item)).join()}`,
            class: 'background-color: #EBECF0;color:#999'
          }
        } else if (handleType == 3) {
          return {
            text: `【限时审批】流程在审批人处停留超过${limitTime}${timeTypeObj[timeType]}，系统${examineStatus == 1 ? '已' : '将'}自动同意`,
            class: 'background-color: #EBECF0;color:#999'
          }
        }
      }
    },

    // 单节点员工禁用是否展示
    singleNodeDisableUserShow(item) {
      const userList = item.userList
      if (userList.length !== 1) return false
      const user = userList[0]
      if ((this.$getUserName(user) || user.outerUserEmail) &&
          ((!user.hasOwnProperty('userStatus') || user.userStatus != 0) ||
          item.examineType == 7 || (![104, 204].includes(user.passFlag)))) {
        return true
      }
    },

    /**
     * 多人禁用员工是否展示
     */
    disableAvatarUserShow(item) {
      const { passFlag, examineStatus } = item

      if (passFlag == 104 && examineStatus == 1) {
        return false
      } else if (passFlag == 204 && examineStatus == 11) {
        return false
      } else {
        return true
      }
    }
  }
}
</script>
<style lang="scss" scoped>
/deep/ .el-timeline {
  .el-timeline-item {
    .el-timeline-item__dot {
      i {
        margin-top: -5px;
      }
    }
  }
}

.wk-head-section {
  /deep/ {
    .section-body {
      max-height: 60vh;
      overflow: auto;
    }
  }

  .handle {
    position: sticky;
    bottom: 0;
    display: flex;
    justify-content: space-between;
    padding: 16px 0;
    background-color: $--color-n0;

    .el-button + .el-button {
      margin-left: unset;
    }

    .more-handle {
      display: flex;

      /deep/ .el-button.el-button--default {
        display: flex;
        align-items: center;
        justify-content: center;
        width: 74px;
      }
    }

    .el-button.el-button--danger {
      margin-left: unset;
    }

    .xr-btn--green,
    .xr-btn--red {
      color: white;
    }
  }
}

/** 审核流程 */
.check-items {
  overflow-x: auto;
  line-height: 2;
}

.check-item {
  flex-shrink: 0;
  width: auto;

  .check-item-name {
    margin-right: $--interval-base;
    font-size: 16px;
  }

  .check-item-status {
    color: $--color-text-secondary;
  }

  .check-item-arrow {
    margin: 0 #{$--interval-base * 2};
    font-size: 13px;
  }
}

// 固定审批流详情
.examine-item {
  &-icon {
    margin-left: -4px;
    font-size: 18px;
    background-color: white;
  }

  .ei {
    padding: 2px 0;

    &-name {
      font-size: 14px;
      color: $--color-black;
    }

    &-status {
      font-size: 14px;
      color: $--color-text-secondary;
    }

    &-time {
      margin-top: 4px;
      font-size: 12px;
      color: $--color-n70;
    }

    &-time-limit {
      padding: 5px 0;
      font-size: $--font-size-small;
    }

    span + span {
      margin-left: 4px;
    }
  }

  .ei + .ei {
    margin-top: 4px;
  }

  &__bd {
    margin-top: 4px;

    .examine-care-of {
      margin-left: 10px;
    }

    .care-of {
      margin-right: 5px;
      color: $--color-n200;
      transform: rotate(180deg);
    }

    .ei-name {
      font-size: 14px;
    }

    .ei-status {
      font-size: 12px;
    }
  }

  .other-people {
    font-size: 14px;
    color: $--color-n200;

    i {
      margin-left: 4px;
      font-size: 14px;
      color: $--color-y300;
    }
  }

  .remarks {
    padding: 8px;
    margin: 10px 0;
    font-size: 12px;
    line-height: 18px;
    color: #6b778c;
    background-color: #f4f5f7;
    border-radius: 3px;
  }
}

.examine-error {
  padding: 5px;
  margin-top: 10px;
  font-size: 12px;
  color: #a5adba;
  background-color: $--color-n20;

  i {
    margin-right: 5px;
    color: #f56c6c;
  }
}
</style>
