<template>
  <slide-view
    ref="sideView"
    v-empty="!canShowDetail"
    :no-listener-class="noListenerClass"
    :body-style="{padding: 0, height: '100%'}"
    class="d-view"
    xs-empty-icon="nopermission"
    xs-empty-text="暂无权限"
    @afterEnter="viewAfterEnter"
    @close="hideView">

    <div
      v-if="loading !== null"
      v-loading="loading"
      class="main">
      <template v-if="taskData">
        <!-- 左 -->
        <div class="main__left left-padding">
          <flexbox
            class="main-top"
            justify="space-between">
            <span
              v-if="taskData.createUser">{{ taskData.createUser.realname }} 创建于 {{ taskData.createTime }}</span>
          </flexbox>

          <flexbox
            class="margin-top-base"
            align="stretch">
            <!-- <el-checkbox
              v-model="taskData.checked"
              :disabled="!getPermission('setTaskStatus')"
              @change="completeMainTask" /> -->
            <el-tooltip v-if="!nameVinput" :content="taskData.name" effect="dark" placement="top">
              <div
                :class="['task-name', 'backgroud-btn', { 'is-checked': taskData.checked }]"
                @click="editTaskName">{{ taskData.name }}</div>
            </el-tooltip>
            <wk-edit-wrap v-else @save="submiteTaskName(taskDataName)" @cancel="nameVinput = false">
              <el-input
                v-model="taskDataName"
                :maxlength="100"
                size="medium" />
            </wk-edit-wrap>
          </flexbox>
          <!-- 标签 -->
          <div v-if="labelList" class="label" style="margin-top: 4px;">
            <tag-view
              :value="tagLabelList"
              :max-length="Infinity"
              wrap="wrap"
              class="tag-view">
              <div v-if="getPermission('setTaskLabel')" class="add-tag">
                <tag-index
                  placement="bottom-start"
                  :task-data="taskData">
                  <el-button slot="editIndex" type="icon" size="small" icon="wk wk-icon-label-solid" />
                </tag-index>
              </div>
            </tag-view>
          </div>

          <!-- 参与人 -->
          <div class="participant margin-top-base">
            <flexbox
              class="participant-bd"
              wrap="wrap">
              <span
                v-for="(item, index) in ownerUserShowList"
                :key="index"
                :class="['owner-list',{'owner-list-fold':taskData.ownerUserList.length < 5 }]">
                <xr-avatar
                  :name="item.realname"
                  :size="32"
                  :src="item.img"
                  class="user-img" />
                <img
                  v-if="getPermission('setTaskOwnerUser')"
                  src="@/assets/img/delete_task.png"
                  class="el-icon-close"
                  @click="deleteOwnerList(item, index)">
              </span>
              <el-dropdown
                v-if="taskData.ownerUserList.length > 5"
                trigger="click"
                :hide-on-click="false">
                <el-button class="dropdown-btn owner-list-button">
                  +{{ taskData.ownerUserList.length - 5 }}
                </el-button>
                <el-dropdown-menu slot="dropdown" class="owner-list-dropdown">
                  <el-dropdown-item
                    v-for="(item,index) in ownerUserHideList"
                    :key="index"
                    class="owner-list-dropdown-list">
                    <xr-avatar
                      :name="item.realname"
                      :size="32"
                      :src="item.img"
                      style="margin-right: 10px;"
                      class="owner-list-dropdown-img" />
                    <p class="owner-list-dropdown-text">{{ item.realname }} </p>
                    <i
                      class="el-icon-close owner-list-dropdown-i"
                      @click="deleteOwnerList(item, index + 5)" />
                  </el-dropdown-item>
                </el-dropdown-menu>
              </el-dropdown>
              <members-dep
                v-if="getPermission('setTaskOwnerUser')"
                style="margin-left: 15px;"
                :close-dep="true"
                :content-block="false"
                :user-checked-data="taskData.ownerUserList"
                :user-request="ownerListRequest"
                :user-params="ownerListParams"
                @popoverSubmit="editOwnerList">
                <i
                  slot="membersDep"
                  class="wk wk-l-plus participant-add" />
              </members-dep>
            </flexbox>
          </div>

          <!-- 其他信息 -->
          <div class="secitons">
            <!-- 描述 -->
            <div class="section">
              <div class="section__hd">
                <span>描述</span>
              </div>
              <div class="section__bd description">
                <div v-show="!addDescriptionShow">
                  <div
                    v-if="taskData.description"
                    class="description-content"
                    @click="editTaskDescription">{{ taskData.description }}</div>
                  <div
                    v-else
                    class="description-empty">
                    <span class="color-label" @click="editTaskDescription">暂无描述</span>
                  </div>
                </div>
                <wk-edit-wrap v-show="addDescriptionShow" @save="submiteDescription" @cancel="addDescriptionTextarea = '';addDescriptionShow = false">
                  <el-input
                    v-model="addDescriptionTextarea"
                    :autosize="{ minRows: 2}"
                    :maxlength="2000"
                    show-word-limit
                    type="textarea"
                    placeholder="请输入内容" />
                </wk-edit-wrap>
              </div>
            </div>

            <!-- 子任务 -->
            <div class="section">
              <div class="section__hd">
                <span>子任务({{ subTaskDoneNum }}/{{ taskData.childTask.length }})</span>
                <i
                  style="float: right;"
                  class="wk wk-l-plus section-add"
                  @click="addSubtasks = false" />
              </div>
              <div class="section__bd">
                <div v-if="taskData.pid == 0">
                  <div
                    v-if="taskData.childTask.length !== 0 || !addSubtasks"
                    class="sub-task-all">
                    <div
                      v-for="(item, index) in taskData.childTask"
                      :key="index"
                      class="sub-task">
                      <flexbox
                        v-if="!item.showEdit"
                        style="padding: 14px;"
                        :class="{ 'is-checked-background' : item.checked }">
                        <div @click.stop>
                          <el-checkbox
                            v-model="item.checked"
                            :disabled="!getPermission('setChildTaskStatus')"
                            @change="subtasksCheckbox(item, $event)" />
                        </div>
                        <div
                          :class="{ 'is-checked' : item.checked }"
                          class="sub-task__bd text-one-line">
                          {{ item.name }}
                        </div>
                        <div
                          v-if="item.stopTime"
                          style="margin-left: 8px;"
                          class="bg-color task-bg-color">{{ item.stopTime | moment("YYYY-MM-DD") }} 截止</div>
                        <xr-avatar
                          v-if="item.mainUser"
                          :name="item.mainUser.realname"
                          :size="25"
                          :src="item.mainUser.img"
                          class="user-img" />

                        <div class="edit-del-dropdown">
                          <el-dropdown
                            :hide-on-click="false"
                            @command="handleSubTask">
                            <span class="el-dropdown-link">
                              <i class="el-icon-more" style="cursor: pointer;" />
                            </span>
                            <el-dropdown-menu slot="dropdown">
                              <el-dropdown-item :command="{type:'update',data:item}">
                                <span
                                  v-if="getPermission('updateChildTask')">
                                  编辑
                                </span>
                              </el-dropdown-item>
                              <el-dropdown-item :command="{type:'delete',data:item}">
                                <span
                                  v-if="getPermission('deleteChildTask')">
                                  删除
                                </span>
                              </el-dropdown-item>
                            </el-dropdown-menu>
                          </el-dropdown>
                        </div>
                      </flexbox>
                      <sub-task
                        v-else
                        :sub-task-com="'edit'"
                        :time="item.stopTime"
                        :sub-data="item"
                        :text="item.name"
                        :task-id="subTaskID"
                        :checkbox-data="item.checked"
                        :task-data="taskData"
                        @on-handle="handleSubTasksBlock($event, item)" />
                    </div>
                    <div v-if="!addSubtasks">
                      <sub-task
                        :sub-task-com="'new'"
                        :task-data="taskData"
                        @on-handle="handleSubTasksBlock" />
                    </div>
                  </div>
                  <template v-if="getPermission('addChildTask')">
                    <!-- <div v-if="addSubtasks">
                      <span
                        class="add-btn"
                        @click="addSubtasks = false">
                        <i class="wk wk-l-plus" />
                        <span class="label">子任务</span>
                      </span>
                    </div> -->
                    <!-- <sub-task
                      v-if="!addSubtasks"
                      :sub-task-com="'new'"
                      :task-data="taskData"
                      @on-handle="handleSubTasksBlock" /> -->
                  </template>
                </div>
              </div>
            </div>

            <!-- 附件 -->
            <div class="section">
              <div class="section__hd ">
                <span>附件</span>
                <span v-if="fileList.length">({{ fileList.length }})</span>
                <el-upload
                  v-if="getPermission('uploadTaskFile')"
                  style="float: right;"
                  :http-request="httpRequest"
                  class="upload-file"
                  action="https://jsonplaceholder.typicode.com/posts/"
                  multiple
                  list-type="picture">
                  <i
                    style="float: right;"
                    class="wk wk-l-plus section-add" />
                </el-upload>
              </div>
              <div style="margin-top: 20px;">
                <file-cell
                  :file-list="fileList"
                  :module-id="id"
                  :show-delete="getPermission('deleteTaskFile')"
                  @delete="accessoryDeleteFun" />
              </div>
            </div>

          </div>

          <div class="margin-top-base ">
            <div>活动</div>
            <div class="activity-title">
              显示：
              <el-button
                v-for="(item, index) in tabs"
                :key="index"
                :type="item.value === tabValue ? 'selected' : ''"
                @click="tabValue = item.value">{{ item.label }}</el-button>
            </div>

            <flexbox
              v-if="tabValue === 'comment'"
              v-loading="commentsLoading"
              direction="column"
              align="stretch"
              class="comment-cells">
              <comment-list
                v-if="replyList && replyList.length > 0"
                ref="comment_list"
                :props="commentListProps"
                :list="replyList"
                @delete="deleteComment"
                @close-other-reply="$refs.f_reply.toggleFocus(true)" />
              <flexbox align="stretch" class="reply-comment-wrap">
                <xr-avatar
                  :name="userInfo.realname"
                  :size="32"
                  :src="userInfo.img"
                  class="user-img" />
                <reply-comment
                  ref="f_reply"
                  @toggle="closeOtherReply"
                  @reply="handleReply" />
              </flexbox>
            </flexbox>

            <div v-else-if="tabValue === 'activity'" class="activity-cells">
              <flexbox
                v-for="(item, index) in activityList"
                :key="index"
                align="stretch"
                class="activity-cell">
                <xr-avatar
                  :name="item.realname"
                  :size="26"
                  :src="item.img"
                  class="user-img" />
                <div class="activity-cell__bd">
                  <div class="activity-info">
                    <span class="activity-info--name">{{ item.realname }}</span>
                    <span class="activity-info--time">{{ item.createTime }}</span>
                  </div>
                  <div class="activity-content">{{ item.content }}</div>
                </div>
              </flexbox>
            </div>
          </div>
        </div>

        <!-- 右 -->
        <div class="main__right right-padding">
          <div class="main-top align-right">
            <span
              v-if="isArchive"
              class="margin-right-base">该任务已于 {{ taskData.archiveTime }} 被归档</span>
            <el-button
              v-if="isArchive"
              type="primary"
              @click="activateTask">激活</el-button>
            <span
              v-if="isRecycle"
              class="margin-right-base">该任务已于 {{ taskData.hiddenTime }} 被放入回收站</span>
            <el-button
              v-if="isRecycle && getPermission('restoreTask')"
              type="primary"
              @click="recoverTask">恢复</el-button>
            <el-button
              v-if="isRecycle && getPermission('cleanTask')"
              type="danger"
              @click="thoroughDeleteTask">彻底删除</el-button>
            <el-button
              v-if="showArchiveBtn && getPermission('archiveTask')"
              type="primary"
              @click="moreArchive">归档</el-button>
            <el-dropdown
              v-if="!isRecycle && getPermission('deleteTask')"
              trigger="click"
              @command="morkDropdownClick">
              <el-button icon="el-icon-more" class="dropdown-btn" />
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item
                  command="delete">删除</el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </div>

          <el-dropdown :disabled="!getPermission('setTaskStatus')" class="dropdown-status-btn" @command="completeMainTask">
            <el-button :disabled="!getPermission('setTaskStatus')" :class="{ 'is-done': taskData.checked }" type="primary">
              {{ taskData.checked ? '已完成' : '进行中' }}<i class="el-icon-arrow-down el-icon--right" />
            </el-button>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item>{{ taskData.checked ? '进行中' : '已完成' }}</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>

          <wk-head-section label="详细信息">
            <div class="detail-form">
              <wk-section-item
                v-for="(item, index) in detailList"
                :key="index"
                :label="item.label">
                <!-- 优先级 -->
                <flexbox v-if="item.field === 'priority'" @click.native="priorityVisible = true">
                  <el-popover
                    v-model="priorityVisible"
                    :disabled="!getPermission('setTaskPriority')"
                    popper-class="no-padding-popover"
                    placement="bottom"
                    trigger="click">
                    <flexbox class="priority-select">
                      <div
                        v-for="(i, ind) in priorityList"
                        :key="ind"
                        class="priority-select-item"
                        @click="priorityBtn(i, taskData.priority)">
                        <span
                          :style="{ backgroundColor: i.color }"
                          class="priority--icon">
                          {{ i.label }}
                        </span>
                      </div>
                    </flexbox>
                    <div
                      slot="reference"
                      class="priority--slot">
                      <div
                        :style="{ backgroundColor: priority.color }"
                        class="priority--ball" />
                      <span class="priority--text">
                        {{ priority.label }}
                      </span>
                    </div>
                  </el-popover>
                  <!-- <div class="head-btn__bd">
                    <div class="head-btn__bd--des">优先级</div>
                  </div> -->
                </flexbox>
                <!-- 负责人 -->
                <wk-user-select
                  v-else-if="item.field === 'mainUser'"
                  :value="taskData.mainUser ? taskData.mainUser.userId : ''"
                  :request="ownerListRequest"
                  :params="ownerListParams"
                  :props="{isList: !!ownerListRequest}"
                  :disabled="!getPermission('setTaskMainUser')"
                  style="width: 100%;"
                  radio
                  @change="mainUserChange">
                  <flexbox
                    slot="reference"
                    class="head-btn">
                    <!-- <i
                      v-if="!taskData.mainUser"
                      class="wk wk-l-plus head-btn__icon is-null" /> -->
                    <span
                      v-if="!taskData.mainUser"
                      class="head-btn__text">
                      点击选择负责人
                    </span>
                    <xr-avatar
                      v-else
                      disabled
                      :name="taskData.mainUser ? taskData.mainUser.realname : ''"
                      :size="30"
                      :src="taskData.mainUser ? taskData.mainUser.img : ''"
                      class="user-img" />
                    <div class="head-btn__bd">
                      <div
                        v-if="taskData.mainUser"
                        class="head-btn__bd--name">
                        {{ taskData.mainUser.realname }}
                      </div>
                    </div>
                    <i
                      v-show="taskData.mainUser && getPermission('setTaskMainUser')"
                      class="el-icon-close head-btn__close"
                      @click.stop="submiteMainUser(null)" />
                  </flexbox>
                </wk-user-select>
                <!-- 开始时间 -->
                <flexbox v-else-if="item.field === 'startTime'" class="head-btn">
                  <!-- <i
                    :class="[ taskData.startTime ? 'is-valve' : 'is-null']"
                    class="wk wk-l-time head-btn__icon" /> -->
                  <span
                    v-if="!taskData.startTime"
                    class="head-btn__text">
                    点击选择开始时间
                  </span>
                  <el-date-picker
                    v-model="taskData.startTime"
                    :clearable="false"
                    :disabled="!getPermission('setTaskTime')"
                    :picker-options="startTimeOptions"
                    type="date"
                    value-format="yyyy-MM-dd"
                    @change="timeChange('startTime')" />
                  <div class="head-btn__bd">
                    <div
                      v-if="taskData.startTime"
                      class="head-btn__bd--title">{{ taskData.startTime | moment('YYYY年MM月DD日') }}</div>
                      <!-- <div class="head-btn__bd--des">开始时间</div> -->
                  </div>
                  <i
                    v-show="taskData.startTime"
                    class="el-icon-close head-btn__close"
                    @click="deleteTime('startTime')" />
                </flexbox>
                <!-- 结束时间 -->
                <flexbox v-else-if="item.field === 'stopTime'" class="head-btn">
                  <!-- <i
                    :class="[ taskData.stopTime ? 'is-valve' : 'is-null']"
                    class="wk wk-l-minus head-btn__icon" /> -->
                  <span
                    v-if="!taskData.stopTime"
                    class="head-btn__text">点击选择结束时间</span>
                  <el-date-picker
                    v-model="taskData.stopTime"
                    :clearable="false"
                    :disabled="!getPermission('setTaskTime')"
                    :picker-options="stopTimeOptions"
                    type="date"
                    value-format="yyyy-MM-dd"
                    @change="timeChange('stopTime')" />
                  <div class="head-btn__bd">
                    <div
                      v-if="taskData.stopTime"
                      class="head-btn__bd--title">{{ taskData.stopTime | moment('YYYY年MM月DD日') }}</div>
                      <!-- <div class="head-btn__bd--des">结束时间</div> -->
                  </div>
                  <i
                    v-show="taskData.stopTime"
                    class="el-icon-close head-btn__close"
                    @click="deleteTime('stopTime')" />
                </flexbox>
              </wk-section-item>
            </div>
          </wk-head-section>

        </div>
      </template>
    </div>

  </slide-view>
</template>

<script type="text/javascript">
import SlideView from '@/components/SlideView'
import xss from 'xss'
import {
  workTaskArchiveAPI,
  workTaskRecoverAPI // 激活任务
} from '@/api/pm/task'
import {
  detailsTaskAPI,
  detailsTrashTaskAPI,
  queryLogTaskAPI
} from '@/api/oa/task'
import {
  queryCommentListAPI,
  deleteCommentAPI,
  setCommentAPI
} from '@/api/oa/common'
import {
  workTaskStatusSetAPI,
  workTaskChildStatusSetAPI,
  workTaskTitleSetAPI,
  workTaskDescriptionSetAPI,
  workTaskMainUserSetAPI,
  workTaskTimeSetAPI,
  workTaskOwnerUserSetAPI,
  workTaskOwnerUserDeleteAPI,
  workTaskPrioritySetAPI,
  workTaskDeleteAPI,
  workSubTaskDeleteAPI
} from '@/api/pm/projectTask'
// 项目参与人
import { workWorkOwnerListAPI } from '@/api/pm/project'
import {
  workTrashRecoverAPI,
  workTrashDeleteAPI
} from '@/api/pm/recycle'

import MembersDep from '@/components/SelectEmployee/MembersDep'
import TagIndex from './Tag/TagIndex'
import SubTask from './SubTask'
import WkHeadSection from '@/components/NewCom/WkHeadSection'
import WkSectionItem from '@/components/NewCom/WkHeadSection/SectionItem'
import WkEditWrap from '@/components/EditWrap'
import TagView from '@/components/NewCom/WkTag/TagView'

import WkUserSelect from '@/components/NewCom/WkUserSelect'
import FileCell from '@/components/FileCell'
import { mapGetters } from 'vuex'
import CommentList from '@/components/CommentList'
import ReplyComment from '@/components/ReplyComment'
import moment from 'moment'
import TaskMixin from '../mixins/TaskMixin'

export default {
  name: 'TaskDetail',
  components: {
    SlideView,
    MembersDep,
    TagIndex,
    SubTask,
    FileCell,
    WkUserSelect,
    CommentList,
    ReplyComment,
    WkHeadSection,
    WkSectionItem,
    WkEditWrap,
    TagView
  },
  mixins: [TaskMixin],
  props: {
    id: [String, Number],
    isTrash: {
      type: Boolean,
      default: false
    },
    detailIndex: Number,
    detailSection: Number,
    noListenerClass: {
      type: Array,
      default: () => {
        return ['el-table__body']
      }
    }
  },
  data() {
    return {
      loading: null,
      canShowDetail: true,
      ownerUserUnfold: false, // 默认闭合
      // 详情List
      detailList: [{
        label: '优先级',
        formType: 'priority',
        field: 'priority'
      }, {
        label: '负责人',
        formType: 'user',
        field: 'mainUser'
      }, /* {
        label: '标签',
        formType: 'tag',
        field: 'labelList'
      },*/ {
        label: '开始时间',
        formType: 'date',
        field: 'startTime'
      }, {
        label: '结束时间',
        formType: 'date',
        field: 'stopTime'
      }],
      // 紧急弹出框
      priorityVisible: false,

      /**
     * 限制时间选择`
     */
      startTimeOptions: {
        disabledDate: (time) => {
          if (!this.taskData || !this.taskData.stopTime) {
            return false
          }
          return moment(time).isAfter(this.taskData.stopTime)
        }
      },
      stopTimeOptions: {
        disabledDate: (time) => {
          if (!this.taskData || !this.taskData.startTime) {
            return false
          }
          return moment(time).isBefore(this.taskData.startTime)
        }
      },
      // 是否显示子任务
      addSubtasks: true,
      // 任务名称和编辑切换
      nameVinput: false,
      // 任务名
      taskDataName: '',
      // 是否显示描述
      addDescriptionShow: false,
      // 描述内容
      addDescriptionTextarea: '',
      // 子任务进度
      subTaskDoneNum: 0,
      // 是否显示评论框
      addComments: false,
      commentsLoading: false,
      // 子任务ID
      subTaskID: null,
      // 任务详情
      taskData: null,
      activityList: [],
      fileList: [],
      // 评论列表
      replyList: [],
      // tabs
      tabValue: 'comment',
      tabs: [{
        label: '评论',
        value: 'comment'
      }, {
        label: '活动',
        value: 'activity'
      }]
    }
  },
  computed: {
    ...mapGetters(['userInfo']),
    priority() {
      if (this.taskData.priority == 0 || !this.taskData.priority) {
        return this.priorityList[3] // 默认读取 priorityList 返回
      }
      return this.getPriorityColor(this.taskData.priority)
    },

    /**
     * 展示的员工树
     */
    ownerUserShowList() {
      const ownerUserList = this.taskData.ownerUserList || []
      if (ownerUserList.length > 0) {
        return ownerUserList.slice(0, 5)
      }
      return ownerUserList
    },
    ownerUserHideList() {
      const ownerUserList = this.taskData.ownerUserList || []
      if (ownerUserList.length > 0) {
        return ownerUserList.slice(5)
      }
      return ownerUserList
    },

    /**
     * 是否能操作
     */
    isDisabled() {
      return this.isTrash
    },

    /**
     * 是归档
     */
    isArchive() {
      return this.taskData.isArchive == 1 && this.taskData.ishidden != 1
    },

    /**
     * 是回收站
     */
    isRecycle() {
      return this.taskData.ishidden == 1
    },

    /**
     * 展示归档
     */
    showArchiveBtn() {
      return this.workId && !this.isArchive && !this.isRecycle
    },

    /**
     * 子任务完成进度
     */
    subTaskProgress() {
      if (this.subTaskDoneNum == 0) {
        return 0
      }
      return parseInt(
        (this.subTaskDoneNum / this.taskData.childTask.length) * 100
      )
    },

    ownerListRequest() {
      return this.workId ? workWorkOwnerListAPI : null
    },

    ownerListParams() {
      return this.workId ? { workId: this.workId } : null
    },

    /**
     * 项目ID 说明是项目
     */
    workId() {
      return (!this.taskData || !this.taskData.workId || this.taskData.workId == 0) ? '' : this.taskData.workId
    },

    /**
     * 标签
     */
    labelList() {
      if (!this.taskData) {
        return null
      }
      return this.taskData.labelList || []
    },

    /**
     * tagview 展示数据
     */
    tagLabelList() {
      return (this.labelList || []).map(item => {
        return { name: item.labelName, value: item.color ? item.color : '#ccc' }
      })
    },

    // 评论列表配置
    commentListProps() {
      return {
        addRequest: setCommentAPI, // 添加请求和参数
        addParams: { typeId: this.taskData ? this.taskData.taskId : '', type: '1' }, // 3 知识库 2 日志 1 任务
        replyKey: 'pid', // 日志 任务 pid  阶段 replyId
        replyValueKey: 'userId', // 获取值的keys 日志 任务 userId  阶段 user.userId
        deleteRequest: deleteCommentAPI, // 删除请求和参数
        deleteParams: null
      }
    }
  },
  watch: {
    id: function(val) {
      this.initInfo()
      this.getDetail()
      this.getCommentList()
      this.getActivityList()
    },

    labelList(newValue, oldValue) {
      if (oldValue && newValue) {
        this.$emit('on-handle', {
          type: 'change-label',
          value: newValue,
          index: this.detailIndex,
          section: this.detailSection
        })
      }
    },

    addDescriptionTextarea(val) {
      this.$refs.sideView.isEditClose = !!(val && val.length > 0)
    }
  },
  mounted() {},

  beforeDestroy() {},
  methods: {
    /**
     * 动画完成方法
     */
    viewAfterEnter() {
      if (this.id) {
        this.getDetail()
        this.getCommentList()
        this.getActivityList()
      }
    },

    initInfo() {
      this.taskData = null
      this.subTaskDoneNum = 0
      this.addDescriptionShow = false
      this.priorityVisible = false
      this.addSubtasks = true
      this.nameVinput = false
      this.addDescriptionTextarea = ''
      this.addComments = false

      this.activityList = []
      this.fileList = []
      this.replyList = []
    },

    /**
     * 详情
     */
    getDetail() {
      this.loading = true
      const request = this.isTrash ? detailsTrashTaskAPI : detailsTaskAPI
      request({ taskId: this.id })
        .then(res => {
          const taskData = res.data || {}
          // 处理一下时间, 仅保留日期
          if (taskData.startTime) {
            taskData.startTime = taskData.startTime.split(' ')[0]
          }

          if (taskData.stopTime) {
            taskData.stopTime = taskData.stopTime.split(' ')[0]
          }

          taskData.checked = taskData.status == 5
          if (taskData.childTask) {
            //  eslint-disable-next-line
            for (const item of taskData.childTask) {
              if (item.status == 5) {
                item.checked = true
                this.subTaskDoneNum++
              } else {
                item.checked = false
              }
            }
          }
          this.fileList = taskData.file || []

          this.taskData = taskData
          this.loading = false
        })
        .catch(error => {
          this.loading = false
          if (error && error.code == 3007) { // 无权访问
            this.canShowDetail = false
          } else {
            this.closeBtn()
          }
        })
    },

    /**
     * 活动信息
     */
    getActivityList() {
      queryLogTaskAPI({
        taskId: this.id
      })
        .then(res => {
          this.activityList = res.data
        })
        .catch(() => {})
    },

    /**
     * 评论信息
     */
    getCommentList() {
      queryCommentListAPI({
        typeId: this.id,
        type: 1 // 1是任务 2 是日志
      })
        .then(res => {
          this.replyList = res.data
        })
        .catch(() => {})
    },

    /**
     * 完成任务
     */
    completeMainTask() {
      const newValue = !this.taskData.checked
      this.taskData.checked = newValue
      workTaskStatusSetAPI({
        taskId: this.id,
        status: this.taskData.checked ? 5 : 1
      })
        .then(res => {
          this.$emit('on-handle', {
            type: 'title-check',
            value: newValue,
            index: this.detailIndex,
            section: this.detailSection
          })
          // this.$store.dispatch('GetOAMessageNum', 'task')
        })
        .catch(() => {
          // this.$emit('on-handle', {
          //   type: 'title-check',
          //   value: !this.taskData.checked,
          //   index: this.detailIndex,
          //   section: this.detailSection
          // })
          this.taskData.checked = !this.taskData.checked
        })
    },

    /**
     * 编辑任务标题
     */
    editTaskName() {
      if (this.getPermission('setTaskTitle')) {
        this.nameVinput = true
        this.taskDataName = this.taskData.name
      }
    },

    /**
     * 编辑任务描述
     */
    editTaskDescription() {
      if (this.getPermission('setTaskDescription')) {
        this.addDescriptionShow = true
        this.addDescriptionTextarea = this.taskData.description
      }
    },

    /**
     * 获取权限
     */
    getPermission(key) {
      if (!this.workId) {
        return true
      }

      const permission = this.taskData && this.taskData.authList ? this.taskData.authList.project || {} : {}
      const hasPermission = !!permission[key]

      // 是回收站任务 限制操作
      if (this.isDisabled) {
        return ['restoreTask', 'cleanTask'].includes(key) && hasPermission
      } else {
        return hasPermission
      }
    },

    /**
     * 关闭
     */
    closeBtn() {
      this.$emit('close')
    },

    /**
     * 紧急按钮
     */
    priorityBtn(value, def) {
      this.taskData.priority = value.id
      workTaskPrioritySetAPI({
        taskId: this.id,
        priority: value.id
      })
        .then(res => {
          this.$emit('on-handle', {
            type: 'change-priority',
            value: value,
            index: this.detailIndex,
            section: this.detailSection
          })
          this.priorityVisible = false
        })
        .catch(() => {
          this.$message.error('编辑失败')
          this.taskData.priority = def
        })
    },
    // 更多 ———— 删除和规定按钮
    moreDelete() {
      this.$confirm('确定删除?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
        customClass: 'is-particulars'
      })
        .then(() => {
          workTaskDeleteAPI(this.id)
            .then(res => {
              this.$message.success('删除成功')
              this.$emit('on-handle', {
                type: 'delete',
                index: this.detailIndex,
                section: this.detailSection
              })
              this.$emit('close')
            })
            .catch(() => {})
        })
        .catch(() => {
          this.$message({
            type: 'info',
            message: '已取消删除'
          })
        })
    },
    moreArchive() {
      this.$confirm('此操作将归档该任务, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
        customClass: 'is-particulars'
      })
        .then(() => {
          workTaskArchiveAPI(this.id)
            .then(res => {
              this.$message.success('操作成功')
              this.$emit('on-handle', {
                type: 'delete',
                index: this.detailIndex,
                section: this.detailSection
              })
              this.$emit('close')
            })
            .catch(() => {})
        })
        .catch(() => {
          this.$message({
            type: 'info',
            message: '已取消删除'
          })
        })
    },
    // 附件 -- 上传
    httpRequest(val) {
      this.$wkUploadFile.upload({
        file: val.file,
        params: {
          batchId: this.taskData.batchId
        }
      }).then(({ res }) => {
        // const data = res.data || {}
        // this.fileList.push(data)
        this.getDetail()
        // this.$emit('httpRequest', this.taskData)
        this.$message.success('上传成功')
      }).catch(() => {})
    },
    // 附件删除
    accessoryDeleteFun(index, item) {
      this.fileList.splice(index, 1)
    },
    // 子任务 -- 勾选
    subtasksCheckbox(val, e) {
      if (e) {
        this.$set(val, 'checked', true)
        this.subTaskDoneNum++
      } else {
        this.$set(val, 'checked', false)
        this.subTaskDoneNum--
      }
      // this.$emit('on-handle', {
      //   type: 'change-sub-task',
      //   value: {
      //     subdonecount: this.subTaskDoneNum,
      //     allcount: this.taskData.childTask.length
      //   },
      //   index: this.detailIndex,
      //   section: this.detailSection
      // })

      workTaskChildStatusSetAPI({
        taskId: val.taskId,
        status: e ? 5 : 1
      })
        .then(res => {
          this.$emit('on-handle', {
            type: 'change-sub-task',
            value: {
              subdonecount: this.subTaskDoneNum,
              allcount: this.taskData.childTask.length
            },
            index: this.detailIndex,
            section: this.detailSection
          })
        })
        .catch(() => {
          this.$message.error('子任务标记失败')
          if (e) {
            this.$set(val, 'checked', false)
            this.subTaskDoneNum--
          } else {
            this.$set(val, 'checked', true)
            this.subTaskDoneNum++
          }
          // this.$emit('on-handle', {
          //   type: 'change-sub-task',
          //   value: {
          //     subdonecount: this.subTaskDoneNum,
          //     allcount: this.taskData.childTask.length
          //   },
          //   index: this.detailIndex,
          //   section: this.detailSection
          // })
          // this.$emit('', val, e)
        })
    },
    /**
     * 参与人操作
     */
    editOwnerList(users, dep) {
      workTaskOwnerUserSetAPI({
        taskId: this.id,
        ownerUserId: users
          .map(item => {
            return item.userId
          })
          .join(',')
      })
        .then(res => {
          this.taskData.ownerUserList = users
        })
        .catch(() => {})
    },

    /**
     * 参与人删除按钮
     */
    deleteOwnerList(item, index) {
      workTaskOwnerUserDeleteAPI({
        taskId: this.id,
        userId: item.userId
      })
        .then(res => {
          this.taskData.ownerUserList.splice(index, 1)
        })
        .catch(() => {})
    },

    /**
     * 编辑负责人
     */
    mainUserChange(data, dataArray) {
      const mainUser = dataArray.length > 0 ? dataArray[0] : null
      this.submiteMainUser(mainUser)
    },

    /**
     * 上传负责人信息
     */
    submiteMainUser(mainUser) {
      workTaskMainUserSetAPI({
        taskId: this.id,
        userId: mainUser ? mainUser.userId : ''
      })
        .then(res => {
          if (mainUser) {
            this.$set(this.taskData, 'mainUser', mainUser)
          } else {
            this.$set(this.taskData, 'mainUser', null)
          }

          this.$emit('on-handle', {
            type: 'change-main-user',
            value: mainUser,
            index: this.detailIndex,
            section: this.detailSection
          })
        })
        .catch(() => {})
    },

    /**
     * 编辑任务名
     */
    submiteTaskName(val) {
      workTaskTitleSetAPI({
        name: val,
        taskId: this.id
      })
        .then(res => {
          this.nameVinput = false
          // this.$emit('saveName', val)
          this.$emit('on-handle', {
            type: 'change-name',
            value: val,
            index: this.detailIndex,
            section: this.detailSection
          })
          this.taskData.name = val
        })
        .catch(() => {})
    },

    /**
     * 截至日期API
     */
    deleteTime(type) {
      this.taskData[type] = ''
      this.timeChange(type)
    },

    timeChange(type) {
      const params = {
        taskId: this.id,
        startTime: this.taskData.startTime || '',
        stopTime: this.taskData.stopTime || ''
      }
      params[type] = this.taskData[type] || ''
      if (!params.startTime && !params.stopTime) {
        this.$message.error('开始和结束时间必须至少有一个不能为空')
        return
      }
      workTaskTimeSetAPI(params)
        .then(res => {
          // 停止时间回调
          if (type == 'stopTime') {
            this.$emit('on-handle', {
              type: 'change-stop-time',
              value: this.taskData[type],
              index: this.detailIndex,
              section: this.detailSection
            })
          }
        })
        .catch(() => {})
    },

    /**
     * 描述提交按钮
     */
    submiteDescription() {
      workTaskDescriptionSetAPI({
        taskId: this.id,
        description: this.addDescriptionTextarea
      })
        .then(res => {
          this.addDescriptionShow = false
          this.$set(this.taskData, 'description', this.addDescriptionTextarea)
          this.addDescriptionTextarea = ''
        })
        .catch(() => {})
    },

    /**
     * 评论发布
     */
    handleReply(data) {
      if (data) {
        this.commentsLoading = true

        setCommentAPI({
          content: xss(data),
          ...this.commentListProps.addParams
        })
          .then(res => {
            res.data.childCommentList = []
            res.data.user = {
              userId: this.userInfo.id,
              realname: this.userInfo.realname,
              img: this.userInfo.img
            }
            this.$refs.f_reply.commentsTextarea = ''
            this.replyList.push(res.data)
            this.$emit('on-handle', {
              type: 'change-comments',
              value: 'add',
              index: this.detailIndex,
              section: this.detailSection
            })
            this.commentsLoading = false
          })
          .catch(() => {
            this.commentsLoading = false
          })
      }
    },

    deleteComment(index) {
      this.replyList.splice(index, 1)
    },

    // 删除评论
    discussDelete(val, items, index) {
      this.$confirm('确定删除?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
        customClass: 'is-particulars'
      })
        .then(() => {
          deleteCommentAPI({
            commentId: val.commentId
          })
            .then(res => {
              items.splice(index, 1)
              this.$emit('on-handle', {
                type: 'change-comments',
                value: 'delete',
                index: this.detailIndex,
                section: this.detailSection
              })
            })
            .catch(() => {})
        })
        .catch(() => {
          this.$message({
            type: 'info',
            message: '已取消删除'
          })
        })
    },

    /**
     * 删除任务
     */
    deleteSubTask(val) {
      this.$confirm('此操作将删除该任务, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
        customClass: 'is-particulars'
      })
        .then(() => {
          workSubTaskDeleteAPI(val.taskId)
            .then(res => {
              const subData = this.taskData.childTask
              //  eslint-disable-next-line
              for (const i in subData) {
                if (subData[i].taskId == val.taskId) {
                  subData.splice(i, 1)
                  break
                }
              }
              if (val.checked) {
                this.subTaskDoneNum--
              }

              this.$emit('on-handle', {
                type: 'change-sub-task',
                value: {
                  subdonecount: this.subTaskDoneNum,
                  allcount: this.taskData.childTask.length
                },
                index: this.detailIndex,
                section: this.detailSection
              })
              this.$message.success('子任务删除成功')
            })
            .catch(() => {
              this.$message.error('子任务删除失败')
            })
        })
        .catch(() => {
          this.$message({
            type: 'info',
            message: '已取消删除'
          })
        })
    },

    /**
     * 编辑子任务
     */
    editSubTask(val) {
      this.subTaskID = val.taskId
      const dataList = this.taskData.childTask
      //  eslint-disable-next-line
      for (const i in dataList) {
        if (dataList[i].taskId == val.taskId) {
          this.$set(dataList[i], 'showEdit', true)
        } else {
          this.$set(dataList[i], 'showEdit', false)
        }
      }
    },

    /**
     * 子任务回调
     */
    handleSubTasksBlock(data, item) {
      if (data.type == 'edit') {
        this.$set(item, 'showEdit', false)
      } else if (data.type == 'add') {
        this.addSubtasks = true
        if (data.result == 'success') {
          this.$emit('on-handle', {
            type: 'change-sub-task',
            value: {
              subdonecount: this.subTaskDoneNum,
              allcount: this.taskData.childTask.length
            },
            index: this.detailIndex,
            section: this.detailSection
          })
        } else {
          this.$emit('on-handle', {
            type: 'change-sub-task',
            value: {
              subdonecount: this.subTaskDoneNum,
              allcount: this.taskData.childTask.length
            },
            index: this.detailIndex,
            section: this.detailSection
          })
        }
      } else if (data.type == 'cancel') {
        if (item) {
          this.$set(item, 'showEdit', false)
        } else {
          this.addSubtasks = true
        }
      }
    },

    /**
     * 删除截止时间
     */
    deleteTimeTop() {
      workTaskTimeSetAPI({
        taskId: this.id,
        stopTime: ''
      })
        .then(res => {
          this.$set(this.taskData, 'stopTime', '')
          this.$emit('on-handle', {
            type: 'stop-time',
            index: this.detailIndex,
            section: this.detailSection
          })
        })
        .catch(() => {})
    },

    /**
     * 激活任务
     */
    activateTask() {
      this.loading = true
      workTaskRecoverAPI(this.taskData.taskId)
        .then(res => {
          this.loading = false
          this.$emit('on-handle', {
            type: 'activate-task',
            index: this.detailIndex,
            section: this.detailSection
          })
          this.closeBtn()
        })
        .catch(() => {
          this.loading = false
        })
    },

    /**
     * 激活任务
     */
    recoverTask() {
      this.loading = true
      workTrashRecoverAPI(this.taskData.taskId)
        .then(res => {
          this.loading = false
          this.$message.success('恢复成功')
          this.$emit('on-handle', {
            type: 'recover-task',
            index: this.detailIndex,
            section: this.detailSection
          })
          this.closeBtn()
        })
        .catch(() => {
          this.loading = false
        })
    },

    /**
     * 激活任务
     */
    thoroughDeleteTask() {
      this.$confirm('确定删除?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => {
          this.loading = true
          workTrashDeleteAPI(this.taskData.taskId)
            .then(res => {
              this.loading = false
              this.$emit('on-handle', {
                type: 'thorough-delete-task',
                index: this.detailIndex,
                section: this.detailSection
              })
              this.closeBtn()
            })
            .catch(() => {
              this.loading = false
            })
        })
        .catch(() => {
          this.$message({
            type: 'info',
            message: '已取消删除'
          })
        })
    },

    /**
     * 任务头部操作
     */
    morkDropdownClick(command) {
      this.moreDelete()
    },

    /**
     * 关闭页面
     */
    hideView() {
      this.$emit('close')
      this.$emit('hide-view')
    },

    /**
     * 日志评论
     */

    closeOtherReply(flag) {
      if (!flag && this.$refs.comment_list) {
        this.$refs.comment_list.closeReply()
      }
    },
    handleSubTask(value) {
      if (value.type === 'update') {
        this.editSubTask(value.data)
      } else if (value.type === 'delete') {
        this.deleteSubTask(value.data)
      }
    }
  }
}
</script>
<style lang="scss" scoped>
@import "./taskDetail";

$width: 100%;

// 框架
.main {
  position: relative;
  display: flex;
  flex-wrap: nowrap;
  align-items: initial;
  height: 100%;

  &__left {
    flex: 1;
    overflow: hidden auto;

    // width: calc(0.65 * calc(min(1680px, $width)) + -12px);
    // padding-left: calc(max(($width - 1680px) / 2, 0px));
  }

  &__right {
    flex-shrink: 0;
    width: 350px;
    overflow: hidden auto;

    // width: calc(0.35 * calc(min(1680px, $width)) + 12px);
    // padding-right: calc(max(($width - 1680px) / 2, 0px));
  }

  .left-padding {
    padding: #{$--interval-base * 3} #{$--interval-base * 3}  #{$--interval-base * 4} #{$--interval-base * 4};
  }

  .margin-right-base {
    margin-right: $--interval-base;
  }

  .margin-top-base {
    margin-top: $--interval-base;
  }

  .margin-top-base2 {
    margin-top: #{$--interval-base * 2};
  }

  &-top {
    color: $--color-n90;
  }

  .align-right {
    text-align: right;
  }

  .right-padding {
    padding: 24px 20px 32px 16px;
  }

  .dropdown-status-btn {
    margin: #{$--interval-base * 2} 0;
  }

  .activity {
    &-title {
      margin-top: $--interval-base;
      font-size: 12px;
    }
  }

  // 完成
  .el-button {
    &.is-done {
      background-color: $--color-g400;
    }
  }

  // &__hd {
  //   margin-bottom: 15px;
  // }

  // &__bd {
  //   flex: 1;
  //   overflow: hidden;

  //   &--left {
  //     flex: 1;
  //     box-shadow: none;
  //     position: relative;
  //     overflow-y: auto;
  //     background-color: white;
  //     border-right: 1px solid $--border-color-base;
  //     border-top: 1px solid $--border-color-base;
  //     border-bottom: 1px solid $--border-color-base;
  //     padding: 20px 30px;
  //     margin-bottom: 15px;
  //   }

  //   &--right {
  //     /deep/ .el-tabs__item {
  //       color: $--color-text-primary;
  //       font-size: 12px;
  //       top: 2px;
  //       margin-top: -2px;
  //     }

  //     /deep/ .el-tabs__nav-scroll {
  //       min-height: 39px;
  //     }

  //     /deep/ .el-tabs__item.is-active {
  //       border-top: 2px solid $--color-primary;
  //       color: $--color-text-primary;
  //     }

  //     /deep/ .el-tabs {
  //       box-shadow: none;
  //       border-right: none;
  //       height: 100%;
  //     }

  //     /deep/ .el-tabs__content {
  //       height: calc(100% - 40px) !important;
  //       overflow: auto;
  //       overflow-y: overlay;
  //       position: relative;
  //       .el-tab-pane {
  //         height: 100%;
  //       }
  //     }

  //     width: 300px;
  //     min-width: 300px;
  //     box-shadow: none;
  //     flex-shrink: 0;
  //     height: calc(100% - 15px);
  //     background-color: white;
  //     margin-left: 15px;
  //   }
  // }
}

// 详情form
.detail-form {
  /deep/ .wk-section-item {
    .item-label {
      padding-top: 8px;
    }
  }

  .wk-user-select.is_valid:hover,
  .wk-user-select:hover {
    background-color: transparent;
  }
}

// 任务详情头

.task-hd {
  &__top {
    font-size: 12px;

    &--create {
      color: $--color-text-regular;
    }

    &--info {
      margin-right: 8px;
      color: $--color-text-regular;
    }

    .xr-btn--green {
      margin-right: 9px;
    }
  }

  &__middle {
    margin-top: 5px;

    .el-checkbox {
      margin-top: 5px;
      margin-right: 8px;
    }
  }

  &__bottom {
    margin-top: 20px;
    color: $--color-text-regular;

    .vux-flexbox-item {
      padding-left: 20px;
      font-size: 12px;
      text-align: left;
      border-left: 1px solid #efefef;

      .priority--icon {
        margin-right: 8px;
        cursor: pointer;
      }

      .head-btn__icon {
        margin-right: 12px;
      }

      .vux-flex-row {
        position: relative;
      }
    }

    .vux-flexbox-item:first-child {
      border-left: none;
    }
  }
}

// 任务名称
.task-name {
  display: flex;
  overflow: hidden;

  // flex: 1;
  font-size: $--font-size-xxlarge;
  text-overflow: ellipsis;
  word-break: break-all;
  word-wrap: break-word;
  white-space: pre-wrap;
  cursor: pointer;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.task-name.is-checked {
  color: $--color-n90;
  text-decoration: line-through;
}

.wk-edit-wrap {
  z-index: 2;
  flex: 1;
}

// 优先级
.priority {
  cursor: pointer;

  &--icon {
    display: inline-block;
    width: 32px;
    height: 32px;
    font-size: 12px;
    line-height: 32px;
    color: white;
    text-align: center;
    border-radius: 16px;
  }

  &--text {
    display: inline-block;
    width: 32px;
    height: 32px;
    margin-left: 10px;
    font-size: 14px;
    line-height: 32px;
    color: $--color-n800;
    text-align: center;
  }

  &--ball {
    position: absolute;
    top: 50%;
    width: 12px;
    height: 12px;
    border-radius: 50%;
    transform: translateY(-50%);
  }

  &--slot {
    position: relative;
    padding-left: 8px;
    cursor: pointer;
  }
}

.priority-select {
  margin: 10px;

  &-item {
    padding: 5px;
    cursor: pointer;
  }

  &-item:hover {
    background-color: #ecf5ff;
  }
}

// 操作图标
.head-btn {
  position: relative;
  min-height: 34px;
  cursor: pointer;

  &__icon {
    display: inline-block;
    width: 32px;
    height: 32px;
    font-size: 15px;
    line-height: 32px;
    color: $--color-text-primary;
    text-align: center;
    background-color: $--button-default-background-color;
    border: 1px solid transparent;
    border-radius: 16px;
  }

  &__text {
    height: 32px;
    padding-top: 1px;
    padding-left: 8px;
    line-height: 32px;
    color: $--color-n200;
  }

  // 有值效果
  .wk-l-time.is-valve {
    color: white;
    background-color: #f7ad3d;
  }

  .wk-l-minus.is-valve {
    color: white;
    background-color: #f56c6c;
  }

  .el-date-editor {
    position: absolute;
    top: 0;
    right: 0;
    bottom: 0;
    left: 0;
    z-index: 1;
    overflow: hidden;

    /deep/ .el-input__inner {
      color: transparent;
      cursor: pointer;
      background-color: transparent;
      border: none;
    }

    /deep/ .el-input__prefix {
      display: none;
      cursor: pointer;
    }
  }

  &__bd {
    flex: 1;
    padding-right: 8px;
    margin-left: $--interval-base;

    &--title {
      font-size: $--font-size-large;
    }

    &--name {
      width: 100px;
      overflow: hidden;
      font-size: $--font-size-large;
      text-overflow: ellipsis;
      white-space: nowrap;
    }

    &--des {
      color: $--color-text-regular;
    }
  }

  &__close {
    z-index: 5;
    flex-shrink: 0;
    font-size: 16px;
    color: $--color-text-regular;
    opacity: 0;
  }

  &__close:hover {
    color: #fa6060;
  }
}

.head-btn:hover {
  .head-btn__icon.is-null {
    background-color: $--button-hover-background-color;
  }

  .head-btn__close {
    opacity: 1;
  }
}

// 详情其他模块
.section {
  padding: 10px 0;

  &__hd {
    span {
      font-weight: $--font-weight-semibold;
    }
  }

  &__bd {
    margin-top: #{$--interval-base * 2};
  }

  /deep/ &-add {
    width: 32px;
    height: 32px;
    line-height: 32px;
    color: $--color-n800;
    text-align: center;
    cursor: pointer;
    border-radius: $--border-radius-base;
    transform: translateY(-25%);

    &:hover {
      background-color: $--color-n20;
    }
  }
}

.secitons {
  position: relative;
  margin-top: #{$--interval-base * 2};
}

// 活动

.activity-cells {
  .activity-cell {
    padding: 10px 0;
    color: $--color-text-primary;

    .user-img {
      flex-shrink: 0;
      margin-right: 10px;
    }

    &__bd {
      flex: 1;
      margin-top: 3px;

      .activity-info {
        &--name {
          margin-right: 5px;
          font-size: 14px;
          font-weight: 600;
          color: $--color-text-primary;
        }

        &--time {
          font-size: 12px;
          color: $--color-text-regular;
        }
      }

      .activity-content {
        margin-top: 5px;
        font-size: 14px;
        line-height: 17px;
        color: $--color-text-regular;
        word-break: break-all;
        word-wrap: break-word;
        white-space: pre-wrap;
      }
    }
  }
}

.comment-cells {
  margin-top: #{$--interval-base * 2};

  .reply-comment-wrap {
    .user-img {
      margin-top: 5px;
      margin-right: $--interval-base;
    }

    .reply-comment {
      flex: 1;
    }
  }

  .comment-list + .reply-comment-wrap {
    margin-top: #{$--interval-base * 2};
  }
}

// // 评论
// .comment-cells {
//   height: 100%;
//   .comment-list {
//     flex: 1;
//     overflow: auto;
//     /deep/ .user-info {
//       .user-img {
//         width: 26px;
//         height: 26px;
//         border-radius: 13px;
//         margin-right: 10px;
//       }

//       .user {
//         font-size: 14px;
//         color: $--color-text-primary;
//         font-weight: 600;
//       }
//     }

//     /deep/ .reply {
//       border-bottom: none;
//       padding: 0 0 20px;
//       .content {
//         margin-left: 40px;
//       }

//       .child-content {
//         margin-top: 5px;
//         margin-left: 40px;
//       }
//     }

//     /deep/ .reply:hover {
//       background-color: white;
//     }
//   }

//   .reply-comment {
//     flex-shrink: 0;
//     margin-top: 15px;
//   }
// }

.d-view {
  position: fixed;
  top: $--detail-view-top;
  right: 0;
  bottom: 0;
  width: $--detail-width-base;
  min-width: 926px;
  background: white;
}
</style>

