<template>
  <slide-view
    ref="sideView"
    v-loading="loading"
    v-empty="!canShowDetail"
    :no-listener-class="noListenerClass"
    :body-style="{padding: 0, height: '100%'}"
    class="d-view"
    xs-empty-icon="nopermission"
    xs-empty-text="暂无权限"
    @afterEnter="viewAfterEnter"
    @close="hideView">
    <div
      ref="crmDetailMain"
      class="detail-main no-padding">
      <flexbox
        v-if="taskData"
        direction="column"
        align="stretch"
        class="d-container">

        <!-- 顶部 -->
        <wk-detail-header
          :subtitle="subTitle"
          :page-list="pageList"
          icon-class="wk wk-icon-task-line"
          class="detail-header"
          :dropdowns="handleAuthOperations"
          @command="headerRightClick">
          <div slot="title" class="icon-name">
            <img class="item-pic" :src="getIconClass(taskData)">
            <!-- <i class="wk wk-icon-task-line" /> -->
            <wk-edit-wrap
              v-if="showEditTitle && editAuth"
              class="edit-wrap"
              @save="saveItemTitle"
              @cancel="showEditTitle = false">
              <el-input
                v-model="taskData.name"
                maxlength="100"
                show-word-limit />
            </wk-edit-wrap>
            <el-tooltip
              v-else
              :disabled="!title"
              :content="title"
              effect="dark"
              placement="top-start">
              <span class="wk-detail-header-title" @click="showEditTitle=true">{{ title }}</span>
            </el-tooltip>
          </div>
        </wk-detail-header>

        <!-- 标签 -->
        <div v-if="labelList" class="label">
          <tag-view
            :value="tagLabelList"
            :max-length="Infinity"
            wrap="wrap"
            class="tag-view">
            <!-- v-if="getPermission('setTaskLabel')" -->
            <div class="add-tag">
              <tag-index
                placement="bottom-start"
                :task-data="taskData">
                <el-button v-if="editAuth" slot="editIndex" type="icon" size="small" icon="wk wk-icon-label-solid" />
              </tag-index>
            </div>
          </tag-view>
        </div>

        <!-- 快捷创建 -->
        <wk-quick-create-bar
          v-if="editAuth"
          :list="activityHandle"
          @command="quickCreateClick" />

        <div
          class="main">
          <!-- 左 -->
          <div class="main__left left-padding" :style="{height: calcHeight + 'px'}">
            <div
              v-if="descShow || childShow || relationShow || fileShow"
              style="margin-bottom: 32px;">
              <!-- 描述 -->
              <div v-if="descShow && editAuth" class="section is-header">
                <div class="section__bd description" style="margin-top: 0;cursor: auto;">
                  <!-- 富文本 -->
                  <template v-if="!editDesc">
                    <div class="html-wrap">
                      <div
                        ref="descWrap"
                        :class="['desc-box', expand ? 'full-content-style' : 'part-content-style']"
                        @click="previewImg"
                        v-html="addDescriptionTextarea" />
                      <template v-if="htmlHeght === 320">
                        <div v-if="!expand" class="desc-mask" />
                        <div class="toggle-btn" @click="expand = !expand">
                          <i
                            class="el-icon-arrow-down el-icon--right arrow"
                            :style="{transform: expand ? 'rotate(180deg)' : 'none'}" />
                          {{ expand ? '收起描述': '展开描述' }}
                        </div>
                      </template>
                    </div>
                  </template>
                  <template v-else>
                    <tinymce
                      ref="editor"
                      v-model="addDescriptionTextarea"
                      :height="200"
                      :init="{
                        menubar: false,
                        toolbar_sticky: true,
                        statusbar: false,
                        contextmenu: '',
                        content_style: 'p { margin: 5px 0; line-height: 1.5;}',
                        plugins: 'paste',
                        toolbar: 'paste',
                        paste_data_images: true,
                        automatic_uploads: autoUpload
                      }"
                      class="rich-txt" />
                    <div class="save-cancel">
                      <el-button
                        v-debounce="submiteDescription"
                        type="primary">保存</el-button>
                      <el-button @click.native="cancelEditDesc">取消</el-button>
                    </div>
                  </template>
                </div>
              </div>

              <!-- 子工作项 -->
              <div v-if="childShow" class="section">
                <div class="section__hd">
                  <span>子工作项</span>
                  <!-- ({{ subTaskDoneNum }}/{{ childTask.length }}) -->
                  <span v-if="editAuth" class="line" />
                  <i
                    v-if="editAuth"
                    style="float: right;"
                    class="wk wk-l-plus section-add"
                    @click="addSubtasks = false" />
                </div>
                <div class="section__bd">
                  <div v-if="taskData.taskId">
                    <div
                      v-if="childTask.length !== 0 || !addSubtasks"
                      class="sub-task-all">
                      <div
                        v-for="(item, index) in childTask"
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
                          <img class="item-img" src="@/assets/img/pm/child.png" alt="">
                          <span class="sub-num">#{{ item.num }}</span>
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
                              v-if="editAuth"
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
                        <sub-work-item
                          v-else
                          :name="item.name"
                          :sub-id="item.taskId"
                          :index="index"
                          @save="handleSave"
                          @close="handleCloseSub(item)" />
                      </div>
                      <div v-if="!addSubtasks">
                        <!-- 创建事项 -->
                        <sub-work-item
                          @save="handleSave"
                          @close="addSubtasks=true;console.log(`4444`);" />
                      </div>
                    </div>

                  </div>
                </div>
              </div>

              <!-- 附件 -->
              <div v-if="fileShow" class="section">
                <div class="section__hd ">
                  <span>附件</span>
                  <!-- <span v-if="fileList.length">({{ fileList.length }})</span> -->
                  <span v-if="editAuth" class="line" />
                  <el-upload
                    v-if="getPermission('uploadTaskFile')"
                    ref="fileUpload"
                    style="float: right;"
                    :http-request="httpRequest"
                    class="upload-file"
                    action="https://jsonplaceholder.typicode.com/posts/"
                    multiple
                    list-type="picture">
                    <i
                      v-if="editAuth"
                      style="float: right;"
                      class="wk wk-l-plus section-add" />
                  </el-upload>
                </div>
                <div style="margin-top: 6px;">
                  <file-cell
                    :file-list="fileList"
                    :module-id="id"
                    :show-delete="getPermission('deleteTaskFile')"
                    :is-exposed="true"
                    @delete="accessoryDeleteFun" />
                </div>
              </div>
            </div>

            <!-- 其他信息 -->
            <div class="margin-top-base ">
              <div style="font-size: 18px;font-weight: 600;color: #172b4d;">活动日志</div>
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
                  :show-control="editAuth"
                  @delete="deleteComment"
                  @close-other-reply="$refs.f_reply.toggleFocus(true)"
                  @query-comment="getCommentList" />
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

              <div v-else-if="tabValue === 'activity' || tabValue === 'hours'" class="activity-cells">
                <activity-list
                  :list="activityList"
                  :hours-list="hoursList"
                  :type="tabValue" />
              </div>
            </div>
          </div>

          <!-- 右 -->
          <div class="main__right right-padding" :style="{height: calcHeight + 'px'}">
            <el-dropdown
              :disabled="!getPermission('setTaskStatus')"
              class="dropdown-status-btn"
              @command="setItemStatus">
              <div>
                <el-button
                  :style="{
                    'background-color': statusColor.bgColor,
                    color: statusColor.color
                  }">
                  {{ taskData.boardStatusName || '未开始' }}
                  <i class="el-icon-arrow-down el-icon--right" />
                </el-button>
              </div>
              <el-dropdown-menu
                slot="dropdown"
                class="dropdown-menu"
                :style="{padding: allStatusList.length === 0 ? '0px' : 'auto'}">
                <el-dropdown-item
                  v-for="(item, index) in allStatusList"
                  :key="index"
                  :command="item.id">
                  <span style="padding: 5px;">
                    <status-tag
                      :type="item.statusType"
                      :status-name="item.statusName" />
                  </span>
                </el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>

            <wk-head-section label="基础信息">
              <div class="detail-form">
                <wk-section-item
                  v-for="(item, index) in detailList"
                  :key="index"
                  :label="item.label">

                  <!-- 所属迭代 -->
                  <flexbox
                    v-if="item.field === 'belongIterationName'"
                    class="head-btn">
                    <el-autocomplete
                      v-if="showSearchIteration"
                      v-model="itemName"
                      v-clickoutside="editChangeSave"
                      style="width: 100%;"
                      :fetch-suggestions="querySearchAsync"
                      placeholder="请输入迭代名称"
                      @select="handleSelect">
                      <!-- @blur="showSearchIteration = false" -->
                      <template slot-scope="{ item: option }">
                        <div v-if="option.type == 'btn'" style="color: #0052cc;">{{ option.name }}</div>
                        <div v-else class="name">{{ option.name }}</div>
                      </template>
                    </el-autocomplete>
                    <div
                      v-else
                      class="head-btn__bd"
                      @click="editOpen(item.field)">
                      <span v-if="taskData.belongIterationName">{{ taskData.belongIterationName }}</span>
                      <span v-else class="head-btn__text" style="padding-left: 0;">点击更改所属迭代</span>
                    </div>
                  </flexbox>

                  <!-- 关联需求 -->
                  <div v-else-if="item.formType == 'item'" class="head-btn text-one-line">
                    <el-select
                      v-if="editingObj[item.field]"
                      v-model="taskData[item.field]"
                      v-clickoutside="editChangeSave"
                      style="width: 100%;"
                      filterable
                      remote
                      clearable
                      :remote-method="getRelatedDemandData"
                      :loading="relatedDemandLoading"
                      @focus="getRelatedDemandData('')"
                      @change="relatedDemandChange">
                      <el-option
                        v-for="task in relatedDemandOptions"
                        :key="task.taskId"
                        :label="task.name"
                        :value="task.taskId" />
                    </el-select>
                    <div
                      v-else
                      class="head-btn__bd"
                      @click="editOpen(item.field)">
                      <span class="head-btn__text" style="padding-left: 0;">{{ taskData.relatedDemandName || '点击更改关联需求' }}</span>
                    </div>
                  </div>

                  <!-- 修改缺陷 -->
                  <div v-else-if="item.formType == 'wrongType'" class="head-btn text-one-line">
                    <el-select
                      v-if="editingObj[item.field]"
                      v-model="taskData[item.field]"
                      v-clickoutside="editChangeSave"
                      style="width: 100%;"
                      clearable
                      @change="wrongTypeChange">
                      <el-option
                        v-for="items in wrongTypes"
                        :key="items.key"
                        :label="items.label"
                        :value="items.key" />
                    </el-select>
                    <div
                      v-else
                      class="head-btn__bd"
                      @click="editOpen(item.field)">
                      <span class="head-btn__text" style="padding-left: 0;">{{ taskData.wrongType ? wrongTypes.find(item => item.key == taskData.wrongType).label : '点击更改缺陷' }}</span>
                    </div>
                  </div>

                  <!-- 团队成员 -->
                  <flexbox v-if="item.field === 'projectOwnerRoleList'" class="head-btn">
                    <div class="head-btn__bd" style="padding-top: 3px;white-space: normal;">
                      <wk-user-select
                        v-model="projectOwnerRoleList"
                        :disabled="!editAuth"
                        :request="ownerListRequest"
                        :params="ownerListParams"
                        :props="{isList: !!ownerListRequest}"
                        style="width: 100%;"
                        @change="taskUserChange"
                        @visible-change="setTaskUser">
                        <flexbox
                          slot="reference"
                          class="head-btn">
                          <span
                            v-if="!projectOwnerRoleList.length"
                            class="head-btn__text"
                            style="padding-left: 0;">
                            {{ editAuth ? '点击选择团队成员' : '--' }}
                          </span>
                          <div v-else-if="projectOwnerRoleList.length" style="display: flex;" @click.stop>
                            <span
                              v-for="(user, ind) in ownerUserShowList"
                              :key="ind"
                              :class="['owner-list',{'owner-list-fold':projectOwnerRoleList.length < 5 }]">
                              <xr-avatar
                                :id="user.userId"
                                :name="user.realname"
                                :size="24"
                                :src="user.img"
                                class="user-img" />
                            </span>
                            <el-dropdown
                              v-if="projectOwnerRoleList.length > 5"
                              trigger="click"
                              :hide-on-click="false">
                              <el-button class="dropdown-btn owner-list-button">
                                +{{ projectOwnerRoleList.length - 5 }}
                              </el-button>
                              <el-dropdown-menu slot="dropdown" class="owner-list-dropdown">
                                <el-dropdown-item
                                  v-for="(v,i) in ownerUserHideList"
                                  :key="i"
                                  class="owner-list-dropdown-list">
                                  <xr-avatar
                                    :id="v.userId"
                                    :name="v.realname"
                                    :size="24"
                                    :src="v.img"
                                    style="margin-right: 10px;"
                                    class="owner-list-dropdown-img" />
                                  <p class="owner-list-dropdown-text">{{ v.realname }} </p>
                                </el-dropdown-item>
                              </el-dropdown-menu>
                            </el-dropdown>
                          </div>
                        </flexbox>
                      </wk-user-select>
                    </div>
                  </flexbox>

                  <!-- 优先级 -->
                  <flexbox v-else-if="item.field === 'priority'" @click.native="priorityVisible = true">

                    <el-dropdown
                      :disabled="!getPermission('priority')"
                      class="priority-box"
                      @command="changePriority">
                      <div class="priority-wrap">
                        <img class="priority-icon" :src="handlePriorityIcon(taskData.priority)" alt="">
                        {{ ['无','低','中','高'][taskData.priority] }}
                      </div>
                      <el-dropdown-menu slot="dropdown">
                        <el-dropdown-item
                          v-for="(option, optionIndex) in priorityList"
                          :key="optionIndex"
                          :command="option.key">
                          <div class="priority-wrap">
                            <img :src="option.icon" class="priority-icon" alt="">{{ option.label }}
                          </div>
                        </el-dropdown-item>
                      </el-dropdown-menu>
                    </el-dropdown>
                  </flexbox>

                  <!-- 预估工时 -->
                  <flexbox
                    v-else-if="item.field === 'estimatedManHours'"
                    class="head-btn">
                    <span class="head-btn__bd">
                      <wk-edit-wrap
                        v-if="showEditWorkTime"
                        class="edit-wrap"
                        @save="handleWorkTime"
                        @cancel="showEditWorkTime = false">
                        <el-input
                          v-model="taskData.estimatedManHours" />
                      </wk-edit-wrap>
                      <span
                        v-else
                        @click="editAuth ? showEditWorkTime=true : ''">
                        <span v-if="taskData.estimatedManHours">{{ taskData.estimatedManHours }}小时</span>
                        <span v-else class="head-btn__text" style="padding-left: 0;">{{ editAuth ? '点击更改预估工时' : '--' }}</span>
                      </span>
                    </span>
                  </flexbox>

                  <!-- 工时进度 -->
                  <flexbox v-else-if="item.field === 'progress'" class="head-btn">
                    <span class="head-btn__bd">
                      <div class="progress-wrap">
                        <el-progress type="circle" :show-text="false" :width="28" :stroke-width="4" :percentage="Number(taskData.progress || 0)" />
                        <wk-edit-wrap
                          v-if="editProgress"
                          style="margin-left: 5px;"
                          class="edit-wrap"
                          @save="hideProgress"
                          @cancel="editProgress = false">
                          <el-input
                            v-model="taskData.progress" />
                        </wk-edit-wrap>
                        <span v-else style="margin-left: 5px;" @click="editAuth ? editProgress=true : ''">已完成{{ taskData.progress || 0 }}%</span>
                      </div>
                    </span>
                  </flexbox>

                  <!-- 工时记录 -->
                  <flexbox v-else-if="item.field === 'taskTimeList'" class="head-btn">
                    <span class="head-btn__bd">
                      <div class="record-wrap" @click="editAuth ? workTimeShow=true : ''">
                        <div class="record-desc">
                          <span>已记录{{ workTimeRecord.actualHour || 0 }}小时</span>
                          <span>剩余{{ workTimeRecord.surplusHours || 0 }}小时</span>
                        </div>
                        <el-progress
                          :percentage="recordPercentage || 0"
                          :show-text="false"
                          color="#409eff" />
                      </div>
                    </span>
                  </flexbox>

                  <!-- 创建人 -->
                  <flexbox v-else-if="item.field === 'createUserName'" class="head-btn">
                    <span class="head-btn__bd" style="padding-top: 3px;">
                      <xr-avatar
                        :id="taskData.createUserId"
                        :key="taskData.createUserId"
                        :size="26"
                        :name="taskData.createUserName"
                        :src="taskData.createUserImg" />
                        <!-- {{ taskData.createUserName }} -->
                    </span>
                  </flexbox>
                  <flexbox v-else-if="['updateTime', 'createTime'].includes(item.field)" class="head-btn">
                    <span class="head-btn__bd">{{ taskData[item.field] || '--' | moment('YYYY年MM月DD日') }}</span>
                  </flexbox>

                  <!-- 处理人 -->
                  <wk-user-select
                    v-else-if="item.field === 'mainUser'"
                    :disabled="!editAuth"
                    :value="taskData.mainUserId"
                    :request="ownerListRequest"
                    :params="ownerListParams"
                    :props="{isList: !!ownerListRequest}"
                    style="width: 100%;margin-left: 8px;"
                    radio
                    @change="mainUserChange">
                    <!-- :disabled="!getPermission('setTaskMainUser')" -->

                    <flexbox
                      slot="reference"
                      class="head-btn">
                      <span
                        v-if="!taskData.mainUserId"
                        class="head-btn__text"
                        style="padding-left: 0;">
                        {{ editAuth ? '点击选择处理人' : '--' }}
                      </span>
                      <xr-avatar
                        v-else
                        :id="taskData.mainUserId"
                        disabled
                        :name="taskData.mainUserName"
                        :size="26"
                        :src="taskData.mainUserImg"
                        class="user-img handle-user" />
                      <div class="head-btn__bd">
                        <div
                          v-if="taskData.mainUserId"
                          class="head-btn__bd--name"
                          style="font-size: 14px;">
                          {{ taskData.mainUserName }}
                        </div>
                      </div>
                      <!-- && getPermission('setTaskMainUser') -->
                      <i
                        v-show="taskData.mainUserId && editAuth"
                        class="el-icon-close head-btn__close"
                        @click.stop="submiteMainUser(null)" />
                    </flexbox>
                  </wk-user-select>

                  <!-- 结束时间、截止时间 -->
                  <flexbox v-else-if="['startTime','stopTime'].includes(item.field)" class="head-btn">
                    <span
                      v-if="!taskData[item.field]"
                      class="head-btn__text">
                      {{ !editAuth ? '--' : item.field=='startTime' ? '点击选择开始时间' : '点击选择截止时间' }}
                    </span>

                    <el-date-picker
                      v-model="taskData[item.field]"
                      :disabled="!getPermission('setTaskTime',item.field)"
                      :clearable="false"
                      :picker-options="item.field=='startTime'? startTimeOptions: stopTimeOptions"
                      type="date"
                      value-format="yyyy-MM-dd"
                      @change="timeChange(item.field)" />
                    <div class="head-btn__bd">
                      <div
                        v-if="taskData[item.field]"
                        class="head-btn__bd--title"
                        style="font-size: 14px;">{{ taskData[item.field] | moment('YYYY年MM月DD日') }}</div>
                      <!-- <div class="head-btn__bd--des">结束时间</div> -->
                    </div>
                    <i
                      v-if="editAuth"
                      v-show="taskData[item.field]"
                      class="el-icon-close head-btn__close"
                      @click="deleteTime(item.field)" />
                  </flexbox>
                </wk-section-item>
              </div>
            </wk-head-section>

          </div>

        </div>
      </flexbox>
    </div>

    <!-- 创建登记工时 -->
    <create-work-time
      :visible.sync="workTimeShow"
      :task-data="taskData"
      :item-type="itemType"
      :rest-hours="workTimeRecord.surplusHours"
      @save-success="handleRefresh"
      @close="workTimeShow = false" />

  </slide-view>
</template>

<script type="text/javascript">
import {
  queryProjectUserListAPI,
  projectQueryByIdAPI
} from '@/api/pm/manage'
import {
  workQueryItemDetailAPI,
  workDelProjectItemAPI,
  workQueryIterationAllItemAPI, // 子工作项
  // workSaveProjectItemAPI, // 新增子工作项
  workUpdateProjectItemAPI, // 更新子工作项
  replyCommentAPI,
  delCommentAPI,
  queryCommentActivityListAPI,
  queryActivityListAPI, // 活动
  projectBoardTaskSetStatusAPI, // 设置状态
  setHandlerAPI, // 设置处理人
  setItemStartStopTimeAPI,
  relationIterationAPI,
  workQueryItemListAPI, // 需求
  workSetPriorityAPI, // 修改优先级
  workSetProgressAPI, // 修改进度
  workSetEstimatedManHoursAPI, // 保存预估工时
  workSetProjectWrongTypeAPI, // 更新缺陷
  upLoadFileRelationLogAPI, // 上传/下载/删除附件后调用日志接口
  workSetProjectChildTaskAPI, // 新增子工作项
  setItemNameAPI,
  relatedProjectTaskUserAPI // 关联任务团队成员
  // 关联wiki
  // relativeWikiAPI
  // updateWorkTimeAPI
} from '@/api/pm/projectTask'
import { getStatusListAPI } from '@/api/pm/setting'
import {
  workQueryIterationsItemListAPI,
  setItemDesciptionAPI,
  projectTaskRelevancyRelatedDemandAPI, // 关联需求-移除需求
  projectTaskSetChildTaskStatusAPI // 子事项设置完成
} from '@/api/pm/projectTask'

import SlideView from '@/components/SlideView'
import WkDetailHeader from '@/components/Page/WkDetailHeader'
import Tinymce from '@/components/Tinymce'
import SubWorkItem from './components/SubWorkItem'
import WkHeadSection from '@/components/NewCom/WkHeadSection'
import WkSectionItem from '@/components/NewCom/WkHeadSection/SectionItem'
import WkEditWrap from '@/components/EditWrap'
import TagView from '@/components/NewCom/WkTag/TagView'
import TagIndex from './components/Tag/TagIndex'
import WkQuickCreateBar from '@/components/Page/WkQuickCreateBar'
import CreateWorkTime from '@/views/pm/project/team/CreateWorkTime'
import StatusTag from '@/views/pm/project/components/StatusTag'
import WkUserSelect from '@/components/NewCom/WkUserSelect'
import FileCell from '@/components/FileCell'
import CommentList from './components/CommentList'
import ActivityList from './components/ActivityList'
import ReplyComment from '@/components/ReplyComment'

import moment from 'moment'
import { itemTypeMap, priorityList, getPriorityIcon } from '@/views/pm/data'
import { mapGetters } from 'vuex'
import xss from 'xss'

export default {
  name: 'ItemDetail',
  components: {
    SlideView,
    WkDetailHeader,
    Tinymce,
    TagIndex,
    SubWorkItem,
    FileCell,
    WkUserSelect,
    CommentList,
    ActivityList,
    ReplyComment,
    WkHeadSection,
    WkSectionItem,
    WkEditWrap,
    TagView,
    WkQuickCreateBar,
    CreateWorkTime,
    StatusTag
  },
  props: {
    id: [String, Number],
    detailIndex: Number,
    detailSection: Number,
    noListenerClass: {
      type: Array,
      default: () => {
        return ['el-table__body']
      }
    },
    pageList: Array, // 用于详情切换
    projectAuth: {
      type: Object,
      default: () => {
        return null
      }
    }
  },
  data() {
    return {
      loading: null,
      projectDetail: null,
      canShowDetail: true,
      ownerUserUnfold: false, // 默认闭合
      // 详情List
      showSearchIteration: false,
      currentItem: '',
      itemName: '',

      // 紧急弹出框
      priorityVisible: false,
      priorityList,

      /**
       * 限制时间选择
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
      // 是否显示描述
      addDescriptionShow: false,
      expand: false,
      // 描述内容
      autoUpload: false, // 控制图片是否自动上传
      addDescriptionTextarea: '',
      htmlHeght: 0,
      // 子任务进度
      subTaskDoneNum: 0,

      allData: {},
      commentsLoading: false,
      // 子任务ID
      subTaskID: null,
      // 任务详情
      taskData: null,
      activityList: [],
      hoursList: [],
      fileList: [],
      // 评论列表
      replyList: [],
      // tabs
      tabValue: 'comment',

      showEditTitle: false,

      descShow: false,
      editDesc: false,
      childShow: false,
      wikiShow: false,
      fileShow: false,
      workTimeShow: false,

      editProgress: false,
      childTask: [],
      handleOperations: [{
        command: 'delete',
        name: '删除',
        icon: ''
      }],
      // 工时记录
      showEditWorkTime: false,
      workTimeRecord: {},

      itemType: '',

      allStatusList: [],

      calcHeight: 600,
      // 关联需求
      relatedDemandLoading: false,
      relatedDemandOptions: [], // 需求数据
      wrongTypes: [
        { label: '功能缺陷', key: 1 },
        { label: 'UI界面问题', key: 2 },
        { label: '易用性问题', key: 3 },
        { label: '安全问题', key: 4 },
        { label: '性能问题', key: 5 },
        { label: '代码错误', key: 6 }
      ],
      // 编辑状态记录
      editingObj: {
        relatedDemandId: false,
        wrongType: false
      },
      // 任务团队成员列表
      projectOwnerRoleList: []
    }
  },
  computed: {
    ...mapGetters(['userInfo']),

    isWorkbench() {
      return this.$route.name === 'workbench'
    },

    projectId() {
      return this.taskData?.projectId
    },

    priority() {
      return this.getPriorityColor(this.taskData.priority)
    },

    title() {
      return this.taskData.name
    },

    subTitle() {
      // return `${this.taskData.createUserName}创建于${moment(this.taskData.createTime).format('YYYY-MM-DD')}`
      return `${this.taskData.createUserName}创建于${this.taskData.createTime}`
    },

    isAgileProject() {
      return this.taskData?.projectType == 2 // 敏捷项目
    },

    activityHandle() {
      const btnList = [{
        command: 'desc',
        icon: 'wk wk-icon-edit-des',
        label: '编辑描述',
        auth: 'oa.taskExamine'
      }, {
        command: 'child',
        icon: 'wk wk-icon-add-sub',
        label: '添加子工作项',
        auth: 'crm.contacts.save'
      }, 
      {
        command: 'file',
        icon: 'wk wk-icon-file',
        label: '上传附件',
        auth: 'crm.contacts.save'
      }, {
        command: 'worktime',
        icon: 'wk wk-icon-time',
        label: '添加工时',
        auth: 'crm.contacts.save'
      }]

      if (!this.isAgileProject) {
        return btnList.filter(item => ['desc', 'relation', 'file'].includes(item.command))
      }

      return btnList
    },

    detailList() {
      const fieldList = [
        {
          label: '所属迭代',
          formType: 'follow',
          field: 'belongIterationName'
        }, {
          label: '关联需求',
          formType: 'item',
          field: 'relatedDemandId'
        }, {
          label: '缺陷类型',
          formType: 'wrongType',
          field: 'wrongType'
        }, {
          label: '处理人',
          formType: 'priority',
          field: 'mainUser'
        }, {
          label: '优先级',
          formType: 'priority',
          field: 'priority'
        }, {
          label: '创建者',
          formType: 'createUser',
          field: 'createUserName'
        },
        {
          label: '开始时间',
          formType: 'date',
          field: 'startTime'
        },
        {
          label: '截止时间',
          formType: 'date',
          field: 'stopTime'
        },
        {
          label: '团队成员',
          formType: 'memeber',
          field: 'projectOwnerRoleList'
        }, {
          label: '进度',
          formType: 'progress',
          field: 'progress'
        }, {
          label: '预估工时',
          formType: 'futureTime',
          field: 'estimatedManHours'
        }, {
          label: '工时记录',
          formType: 'timeRecord',
          field: 'taskTimeList'
        }, {
          label: '更新时间',
          formType: 'date',
          field: 'updateTime'
        }, {
          label: '创建时间',
          formType: 'date',
          field: 'createTime'
        }]

      // 是敏捷项目
      if (this.isAgileProject) {
        // 4 缺陷项目
        if (this.taskData?.type === 4) {
          return fieldList
        } else {
          return fieldList.filter(item => ![
            'item',
            'wrongType'
          ].includes(item.formType))
        }
      } else {
        return fieldList.filter(item => ![
          'item',
          'wrongType',
          'follow',
          'progress',
          'futureTime',
          'timeRecord'
        ].includes(item.formType))
      }
    },

    statusColor() {
      return [
        '',
        { color: '#0052CC', 'bgColor': '#DEEBFF' },
        { color: '#EC8205', 'bgColor': '#FFEACF' },
        { color: '#006644', 'bgColor': '#E3FCEF' }
      ][this.taskData.status]
    },

    recordPercentage() {
      return (this.workTimeRecord.actualHour / (this.workTimeRecord.actualHour + this.workTimeRecord.surplusHours)) * 100
    },

    ownerListRequest() {
      return this.projectId ? queryProjectUserListAPI : null
    },

    ownerListParams() {
      return { projectId: this.projectId }
    },

    /**
     * 标签
     */
    labelList() {
      if (!this.taskData) {
        return null
      }
      return this.taskData.labelTaskList || []
    },

    /**
     * tagview 展示数据
     */
    tagLabelList() {
      return (this.labelList || []).map(item => {
        return { name: item.name, value: item.color ? item.color : '#ccc' }
      })
    },
    /**
     * 关联业务数量
     */
    relatedData() {
      let num = 0
      // eslint-disable-next-line
      for (const i in this.allData) {
        num += this.allData[i].length
      }
      return num
    },

    // 评论列表配置
    commentListProps() {
      return {
        addRequest: replyCommentAPI, // 添加请求和参数
        addParams: { typeId: this.taskData ? this.taskData.taskId : '', type: '1' },
        replyKey: 'pid', // 日志 任务 pid  阶段 replyId
        replyValueKey: 'userId', // 获取值的keys 日志 任务 userId  阶段 user.userId
        deleteRequest: delCommentAPI, // 删除请求和参数
        deleteParams: null
      }
    },

    // 编辑事项权限
    editAuth() {
      if (this.isWorkbench) {
        return this.projectAuth?.coordination?.editMatters || false
      }
      return this.$auth('coordination.editMatters', 'PM')
    },

    // 删除事项权限
    delAuth() {
      if (this.isWorkbench) {
        return this.projectAuth?.coordination?.deletMatters || false
      }
      return this.$auth('coordination.deletMatters', 'PM')
    },

    // 顶部droupdown权限
    handleAuthOperations() {
      return this.delAuth ? this.handleOperations : []
    },

    // 活动日志
    tabs() {
      const arr = [
        { label: '评论', value: 'comment' },
        { label: '活动', value: 'activity' }
      ]
      if (this.projectDetail && this.projectDetail.type == 2) {
        arr.push({ label: '工时日志', value: 'hours' })
      }
      return arr
    },

    // 5个以内的任务成员列表
    ownerUserShowList() {
      const ownerUserList = this.projectDetail.projectOwnerRoleList || []
      if (ownerUserList.length > 0) {
        return ownerUserList.slice(0, 5)
      }
      return ownerUserList
    },
    // 超出5个之后的任务成员列表
    ownerUserHideList() {
      const ownerUserList = this.projectDetail.projectOwnerRoleList || []
      if (ownerUserList.length > 0) {
        return ownerUserList.slice(5)
      }
      return ownerUserList
    }
  },
  watch: {
    id: function(val) {
      this.initInfo()
      this.getDetail()
      // 查子任务
      this.getCommentList()
      this.getActivityList()
    },

    tabValue() {
      this.getActivityList()
    }
  },
  mounted() {
    this.calcHeight = document.documentElement.clientHeight - 165
    window.onresize = () => {
      this.calcHeight = document.documentElement.clientHeight - 165
    }
  },

  beforeDestroy() {},
  methods: {
    /**
     * @description: 获取项目详情
     * @return {*}
     */
    getProjectDetail() {
      this.loading = true
      projectQueryByIdAPI({
        projectId: this.projectId ? this.projectId : this.$route.params.id,
        taskId: this.id
      })
        .then(res => {
          this.projectDetail = res.data || {}
          this.projectOwnerRoleList = res.data.projectOwnerRoleList.map(item => {
            return item.userId
          }) || []
          this.loading = false
        }).catch(() => {
          this.loading = false
        })
    },

    /**
     * @description: 动画完成方法
     * @return {*}
     */
    viewAfterEnter() {
      if (this.id) {
        this.getDetail()
        // 查子任务
        this.getCommentList()
        this.getActivityList()
      }
    },

    /**
     * @description: 更新父组件列表
     * @return {*}
     */
    handleUpdateParentList() {
      this.$emit('update-list')
    },

    /**
     * @description: 初始化
     * @return {*}
     */
    initInfo() {
      this.taskData = null
      this.itemType = ''
      this.subTaskDoneNum = 0
      this.addDescriptionShow = false
      this.priorityVisible = false
      this.addSubtasks = true
      this.addDescriptionTextarea = ''
      // 设置关联项列表
      this.allData = {
        business: [],
        contacts: [],
        contract: [],
        customer: [],
        receivables: []
      }

      this.activityList = []
      this.fileList = []
      this.replyList = []
    },

    /**
     * @description: 事项详情
     * @return {*}
     */
    getDetail() {
      this.loading = true
      workQueryItemDetailAPI({ taskId: this.id })
        .then(res => {
          const taskData = res.data || {}
          taskData.checked = taskData.status == 3

          if (taskData.childTaskList?.length) {
            this.childTask = taskData.childTaskList.map(item => {
              if (item.status == 3) {
                item.checked = true
              } else {
                item.checked = false
              }
              return item
            })
            this.childShow = true
          }

          this.fileList = taskData.file || []
          if (this.fileList.length) {
            this.fileShow = true
          }

          if (taskData.description) {
            this.descShow = true
          }

          let totalHour = 0
          const timeList = taskData.taskTimeList
          // eslint-disable-next-line
          for (const item of timeList) {
            totalHour = totalHour + item.actualHour
          }
          this.workTimeRecord = {
            actualHour: totalHour,
            surplusHours: timeList.length ? timeList[timeList.length - 1].surplusHours : 0
          }

          this.itemType = ['Require', 'Task', 'Defects'][taskData.type - 2]

          this.allData = {
            business: (taskData.businessList || []).map(item => {
              if (item.id) {
                item.businessId = item.id
              }
              return item
            }),
            contacts: (taskData.contactsList || []).map(item => {
              if (item.id) {
                item.contactsId = item.id
              }
              return item
            }),
            contract: (taskData.contractList || []).map(item => {
              if (item.id) {
                item.contractId = item.id
              }
              return item
            }),
            customer: (taskData.customerList || []).map(item => {
              if (item.id) {
                item.customerId = item.id
              }
              return item
            }),
            receivables: (taskData.receivablesList || []).map(item => {
              if (item.id) {
                item.receivablesId = item.id
                item.name = item.customerName
              }
              return item
            })
          }
          this.taskData = taskData

          // 初始化关联需求数据
          if (taskData.relatedDemandId) {
            this.relatedDemandOptions = [{
              name: taskData.relatedDemandName,
              taskId: taskData.relatedDemandId
            }]
          }
          this.getAllStatus()
          this.getProjectDetail()
          this.loading = false

          this.$nextTick(() => {
            this.autoUpload = true
          })

          if (taskData.description) {
            this.updateDescHeight(taskData.description)
          }
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
     * @description: 更新描述高度
     * @return {*}
     */
    updateDescHeight(desc) {
      this.$nextTick(() => {
        this.addDescriptionTextarea = desc

        setTimeout(() => {
          if (desc.includes('<img')) {
            document.querySelectorAll('.desc-box img').forEach(item => {
              item.onload = () => {
                this.htmlHeght = this.$refs.descWrap.offsetHeight
              }
            })
          } else {
            this.htmlHeght = this.$refs.descWrap?.offsetHeight
          }
        }, 0)
      })
    },

    /**
     * 编辑标题
     */
    saveItemTitle() {
      this.showEditTitle = false
      const params = {
        name: this.taskData.name,
        taskId: this.taskData.taskId
      }
      setItemNameAPI(params).then(res => {
        this.$message.success('操作成功')
        this.handleUpdateParentList()
      }).catch(() => {

      })
    },

    /**
     * @description: 获取所有状态
     * @return {*}
     */
    getAllStatus() {
      const params = {
        eventId: this.taskData.eventId,
        projectId: this.projectId
      }
      getStatusListAPI(params).then(res => {
        this.allStatusList = res.data || []
      })
    },

    /**
     * @description: 关闭子工作项编辑状态
     * @param {*} item
     * @return {*}
     */
    handleCloseSub(item) {
      this.$set(item, 'showEdit', false)
    },

    /**
     * @description: 保存进度
     * @return {*}
     */
    hideProgress() {
      this.editProgress = false
      const params = {
        taskId: this.taskData.taskId,
        progress: this.taskData.progress
      }
      workSetProgressAPI(params).then(() => {
        this.$message.success('操作成功')
        this.handleUpdateParentList()
        this.getActivityList()
      }).catch(() => {
        this.$message.success('操作失败')
      })
    },

    /**
     * @description: 获取需求数据
     * @return {*}
     */
    getRelatedDemandData(query) {
      const params = {
        name: query,
        projectId: this.projectId,
        pageType: 0,
        type: 2
      }

      this.relatedDemandLoading = true
      workQueryItemListAPI(params).then(res => {
        const relatedDemandOptions = (res.data.list || [])
        if (this.taskData.relatedDemandId) {
          const hasItem = relatedDemandOptions.find(item => item.taskId === this.taskData.relatedDemandId)
          // 没有当前值数据，增加一下
          if (!hasItem) {
            relatedDemandOptions.push({
              name: this.taskData.relatedDemandName,
              taskId: this.taskData.relatedDemandId
            })
          }
        }
        this.relatedDemandOptions = relatedDemandOptions
        this.relatedDemandLoading = false
      }).catch(() => {
        this.relatedDemandLoading = false
      })
    },

    /**
     * @description: 子工作项保存
     * @param {*} data
     * @return {*}
     */
    handleSave(data) {
      const params = {
        companyId: this.userInfo.companyId,
        type: itemTypeMap[this.itemType],
        projectId: this.projectId,
        name: data.name,
        pid: this.taskData.taskId
      }

      if (data.type == 'edit') {
        params.taskId = data.subId
      }

      const request = {
        add: workSetProjectChildTaskAPI,
        edit: workUpdateProjectItemAPI
      }[data.type]
      request(params).then(res => {
        const way = data.type == 'edit' ? '编辑子工作项成功' : '添加子工作项成功'
        if (data.type == 'edit') {
          this.childTask[data.index].showEdit = false
        }
        this.$message.success(way)
        this.getChildTask()
      }).catch(() => {

      })
    },

    /**
     * @description: 获取子工作项列表
     * @return {*}
     */
    getChildTask() {
      const params = {
        taskId: this.id,
        pageType: 0,
        type: 3
      }
      workQueryIterationAllItemAPI(params).then(res => {
        this.childTask = (res.data.list || []).map(item => {
          if (item.status == 3) {
            item.checked = true
            this.subTaskDoneNum++
          } else {
            item.checked = false
          }
          return item
        })
        if (this.childTask.length) {
          this.childShow = true
        }
      }).catch(() => {

      })
    },

    /**
     * 保存单个字段
     */
    // updateItemField(params) {
    //   workUpdateProjectItemAPI(params).then(() => {
    //     this.$message.success('操作成功')
    //     this.handleUpdateParentList()
    //   }).catch(() => {
    //     this.$message.success('操作失败')
    //   })
    // },

    /**
     * @description: 处理优先级图标
     * @param {*} type
     * @return {*}
     */
    handlePriorityIcon(type) {
      return getPriorityIcon(type)
    },

    /**
     * @description: 查询迭代列表
     * @param {*} queryString
     * @param {*} cb
     * @return {*}
     */
    querySearchAsync(queryString, cb) {
      const btnItem = this.taskData.belongIterationName
        ? [{ type: 'btn', name: '移出迭代' }]
        : []
      if (!queryString) {
        cb([...btnItem])
        return
      }
      const params = {
        name: queryString,
        projectId: this.projectId,
        type: 0,
        pageType: 0
      }
      workQueryIterationsItemListAPI(params).then(res => {
        const list = res.data.list
        var results = queryString ? list.filter(this.createStateFilter(queryString)) : list
        cb([...btnItem, ...results])
      }).catch(() => {
        cb([...btnItem])
      })
    },

    /**
     * @description: 过滤搜索迭代
     * @param {*} queryString
     * @return {*}
     */
    createStateFilter(queryString) {
      return (state) => {
        return (state.name.toLowerCase().indexOf(queryString.toLowerCase()) === 0)
      }
    },

    /**
     * @description: 修改所属迭代
     * @param {*} item
     * @return {*}
     */
    handleSelect(item) {
      if (item.type == 'btn') {
        const params = {
          taskIds: [this.taskData.taskId],
          belongIterationId: ''
        }

        relationIterationAPI(params).then(res => {
          this.currentItem = ''
          this.itemName = ''
          this.taskData.belongIterationName = ''
          this.showSearchIteration = false
          this.getDetail()
          this.handleUpdateParentList()
        }).catch(() => {})
      } else {
        const params = {
          taskIds: [this.taskData.taskId],
          belongIterationId: item.taskId
        }
        relationIterationAPI(params).then(res => {
          this.currentItem = item
          this.itemName = item.name
          this.taskData.belongIterationName = item.name
          this.showSearchIteration = false
          this.getDetail()
          this.handleUpdateParentList()
        }).catch(() => {})
      }
    },

    /**
     * @description: 保存预估工时
     * @return {*}
     */
    handleWorkTime() {
      this.showEditWorkTime = false
      const params = {
        taskId: this.taskData.taskId,
        estimatedManHours: this.taskData.estimatedManHours
      }
      workSetEstimatedManHoursAPI(params).then(() => {
        this.$message.success('操作成功')
        this.handleUpdateParentList()
        this.getActivityList()
      }).catch(() => {
        this.$message.success('操作失败')
      })
    },

    /**
     * @description: 添加工时后 刷新详情
     * @return {*}
     */
    handleRefresh() {
      this.workTimeShow = false
      this.getDetail()
      this.getActivityList()
    },

    /**
     * @description: 更多操作 - 删除
     * @param {*} type
     * @return {*}
     */
    headerRightClick(type) {
      if (type == 'delete') {
        this.$confirm('确定删除?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        }).then(() => {
          const params = {
            taskId: this.id
          }
          workDelProjectItemAPI(params).then(res => {
            this.$message({
              type: 'success',
              message: '删除成功'
            })
            this.hideView()
            this.handleUpdateParentList()
          }).catch(() => {

          })
        }).catch(() => {})
      }
    },

    /**
     * @description: 快捷操作
     * @param {*} command
     * @return {*}
     */
    quickCreateClick(command) {
      switch (command) {
        case 'desc':
          this.addDescriptionShow = true
          this.addDescriptionTextarea = this.taskData.description
          this.descShow = true
          this.editDesc = true
          break
        case 'child':
          this.childShow = true
          this.$nextTick(() => {
            this.addSubtasks = false
          })
          break
        case 'wiki':
          this.wikiShow = true
          break
        case 'file':
          this.fileShow = true
          this.$nextTick(() => {
            if (this.$refs.fileUpload) {
              this.$refs.fileUpload.$refs['upload-inner'].$refs.input.click()
            }
          })
          break
        case 'worktime':
          this.workTimeShow = true
          break
      }
    },

    /**
     * @description: 获取事项类型图标
     * @param {*} row
     * @return {*}
     */
    getIconClass(row) {
      const indexType = row.pid ? 'sub' : row.type
      return {
        2: require('@/assets/img/pm/drequire.png'),
        3: require('@/assets/img/pm/dtask.png'),
        4: require('@/assets/img/pm/dproblem.png')
        // sub: require('@/assets/img/pm/dchild.png')
      }[indexType]
    },

    /**
     * @description: 活动信息
     * @return {*}
     */
    getActivityList() {
      const activeParams = {
        taskId: this.id,
        type: 1
      }
      const hoursParams = {
        taskId: this.id,
        type: 2
      }
      this.loading = true
      Promise.all([
        queryActivityListAPI(activeParams),
        queryActivityListAPI(hoursParams)
      ])
        .then(resArr => {
          this.activityList = resArr[0].data
          this.hoursList = resArr[1].data
          this.loading = false
        })
        .catch(() => {
          this.loading = false
        })
    },

    /**
     * @description: 评论信息
     * @return {*}
     */
    getCommentList() {
      queryCommentActivityListAPI({
        type: 1,
        taskId: this.id
      })
        .then(res => {
          this.replyList = res.data
        })
        .catch(() => {})
    },

    /**
     * @description: 修改事项状态
     * @param {*} statusId
     * @return {*}
     */
    setItemStatus(statusId) {
      const params = {
        statusId: statusId,
        taskId: this.id
      }
      projectBoardTaskSetStatusAPI(params).then(res => {
        this.$message.success('设置成功')
        this.getDetail()
        this.handleUpdateParentList()
        this.getActivityList
      }).catch(() => {

      })
    },

    /**
     * @description: 获取权限
     * @param {*} key
     * @return {*}
     */
    getPermission(key) {
      if (!this.editAuth) return false
      // if (!this.workId) {
      //   return true
      // }

      // const permission = this.taskData && this.taskData.authList ? this.taskData.authList.project || {} : {}
      // const hasPermission = !!permission[key]

      // // 是回收站任务 限制操作
      // if (this.isDisabled) {
      //   return ['restoreTask', 'cleanTask'].includes(key) && hasPermission
      // } else {
      //   return hasPermission
      // }
      return true
    },

    /**
     * @description: 关闭
     * @return {*}
     */
    closeBtn() {
      this.$emit('close')
    },

    /**
     * @description: 修改优先级
     * @param {*} type
     * @return {*}
     */
    changePriority(type) {
      workSetPriorityAPI({
        taskId: this.id,
        priority: type
      })
        .then(res => {
          this.taskData.priority = type
          this.priorityVisible = false
          this.handleUpdateParentList()
          this.getActivityList()
        })
        .catch(() => {
          this.$message.error('编辑失败')
        })
    },

    /**
     * @description: 上传附件
     * @param {*} val
     * @return {*}
     */
    httpRequest(val) {
      this.$wkUploadFile.upload({
        file: val.file,
        params: {
          batchId: this.taskData.batchId
        }
      }).then(({ res }) => {
        this.getDetail()
        this.$message.success('上传成功')
        this.refreshLog({ taskIds: this.taskData.taskId, operationType: 1, fileName: res.data.name })
      }).catch(() => {})
    },

    /**
     * @description: 删除附件
     * @param {*} index
     * @param {*} item
     * @return {*}
     */
    accessoryDeleteFun(index, item) {
      this.fileList.splice(index, 1)
      this.refreshLog({ taskIds: this.taskData.taskId, operationType: 3, fileName: item.name })
    },

    /**
       * @description: 上传/下载/删除附件后调用日志接口
       * @param {*} params
       * @return {*}
       */
    refreshLog(params) {
      upLoadFileRelationLogAPI(params)
        .then(res => {
          console.log(res)
          this.getActivityList()
        })
        .catch(() => {})
    },

    /**
     * @description: 子工作项 - 勾选
     * @param {*} val
     * @param {*} e
     * @return {*}
     */
    subtasksCheckbox(val, e) {
      if (e) {
        this.$set(val, 'checked', true)
        this.subTaskDoneNum++
      } else {
        this.$set(val, 'checked', false)
        this.subTaskDoneNum--
      }

      // 1未开始 2进行中 3完成
      projectTaskSetChildTaskStatusAPI({
        taskId: val.taskId,
        status: e ? 3 : 1
      })
        .then(res => {
          this.$emit('on-handle', {
            type: 'change-sub-task',
            value: {
              subdonecount: this.subTaskDoneNum,
              allcount: this.childTask.length
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
        })
    },

    /**
     * @description: 辑负责人
     * @param {*} data
     * @param {*} dataArray
     * @return {*}
     */
    mainUserChange(data, dataArray) {
      const mainUser = dataArray.length > 0 ? dataArray[0] : null
      this.submiteMainUser(mainUser)
    },

    /**
     * @description: 任务成员改动
     * @param {*} data
     * @param {*} dataArray
     * @return {*}
     */
    taskUserChange(data, dataArray) {
      this.projectOwnerRoleList = data
      this.projectDetail.projectOwnerRoleList = dataArray
    },

    /**
     * @description: 隐藏团队成员选择框发送保存请求
     * @return {*}
     */
    setTaskUser() {
      relatedProjectTaskUserAPI({
        projectId: this.projectId ? this.projectId : this.$route.params.id,
        taskId: this.id,
        userIds: this.projectOwnerRoleList
      })
        .then(res => {
          console.log(res)
        })
        .catch(() => {})
    },

    /**
     * @description: 上传负责人信息
     * @param {*} mainUser
     * @return {*}
     */
    submiteMainUser(mainUser) {
      setHandlerAPI({
        taskId: this.id,
        mainUserId: mainUser ? mainUser.userId : ''
      })
        .then(res => {
          this.getDetail()
          this.handleUpdateParentList()
          this.getActivityList()
        })
        .catch(() => {})
    },

    /**
     * @description: 日期删除
     * @param {*} type
     * @return {*}
     */
    deleteTime(type) {
      this.taskData[type] = ''
      this.timeChange(type, true)
    },

    timeChange(type, clearDate = false) {
      const params = {
        taskId: this.id,
        startTime: type == 'startTime' && clearDate ? '' : this.taskData.startTime || '',
        stopTime: type == 'endTime' && clearDate ? '' : this.taskData.stopTime || ''
      }

      setItemStartStopTimeAPI(params).then(() => {
        this.$message.success('操作成功')
        this.getDetail()
        this.handleUpdateParentList()
        this.getActivityList()
      })
    },

    /**
     * @description: 图片预览
     * @param {*} e
     * @return {*}
     */
    previewImg(e) {
      const { tagName, src } = e.target
      if (tagName === 'IMG') {
        const list = src.split('/')
        this.$wkPreviewFile.preview({
          index: 0,
          data: [{
            url: src,
            name: list[list.length - 1]
          }]
        })
      }
    },

    /**
     * @description: 描述更改提交
     * @return {*}
     */
    submiteDescription() {
      setItemDesciptionAPI({
        taskId: this.id,
        description: this.addDescriptionTextarea
      })
        .then(res => {
          this.addDescriptionShow = false
          this.editDesc = false
          this.$set(this.taskData, 'description', this.addDescriptionTextarea)
          this.updateDescHeight(this.addDescriptionTextarea)
          this.handleUpdateParentList()
          this.$message.success('保存成功')
        })
        .catch(() => {})
    },

    /**
     * @description: 取消编辑描述
     * @return {*}
     */
    cancelEditDesc() {
      this.addDescriptionTextarea = this.taskData.description
    },

    /**
     * @description:关联需求更改
     * @return {*}
     */
    relatedDemandChange() {
      projectTaskRelevancyRelatedDemandAPI({
        taskIds: [this.id],
        relatedDemandId: this.taskData.relatedDemandId
      })
        .then(res => {
          if (!this.taskData.relatedDemandId) {
            this.$set(this.taskData, 'relatedDemandId', '')
            this.$set(this.taskData, 'relatedDemandName', '')
          } else {
            const selectData = this.relatedDemandOptions.find(item => item.taskId === this.taskData.relatedDemandId)
            this.$set(this.taskData, 'relatedDemandId', selectData.taskId)
            this.$set(this.taskData, 'relatedDemandName', selectData.name)
          }
          this.handleUpdateParentList()
        })
        .catch(() => {})
    },

    /**
     * @description:关联缺陷更改
     * @return {*}
     */
    wrongTypeChange() {
      workSetProjectWrongTypeAPI({
        projectId: this.projectId,
        taskId: this.id,
        wrongType: this.taskData.wrongType
      })
        .then(res => {
          this.handleUpdateParentList()
          this.getActivityList()
        })
        .catch(() => {})
    },

    /**
     * @description: 关联需求编辑保存
     * @return {*}
     */
    editChangeSave() {
      for (var key in this.editingObj) {
        this.editingObj[key] = false
      }
      this.showSearchIteration = false
    },

    /**
     * @description: 打开编辑
     * @return {*}
     */
    editOpen(field) {
      // 先让点击空白时间执行
      if (!this.editAuth) return
      this.$nextTick(() => {
        if (field === 'belongIterationName') {
          this.showSearchIteration = true
        } else {
          this.editingObj[field] = true
        }
      })
    },

    /**
     * @description: 评论发布
     * @param {*} data
     * @return {*}
     */
    handleReply(data) {
      if (data) {
        this.commentsLoading = true

        replyCommentAPI({
          content: xss(data),
          ...this.commentListProps.addParams
        })
          .then(res => {
            this.getCommentList()

            this.$refs.f_reply.commentsTextarea = ''
            this.commentsLoading = false
          })
          .catch(() => {
            this.commentsLoading = false
          })
      }
    },

    /**
     * @description: 删除评论
     * @param {*} index
     * @return {*}
     */
    deleteComment(index) {
      this.replyList.splice(index, 1)
    },

    /**
     * @description: 取出name为数组
     * @param {*} arr
     * @param {*} nameStr
     * @return {*}
     */
    // joinName(arr, nameStr) {
    //   let str = ''
    //   arr.forEach(item => {
    //     if (item.name) {
    //       str = str + item.name + ','
    //     } else {
    //       str = str + item[nameStr] + ','
    //     }
    //   })
    //   return str
    // },

    /**
     * @description: 删除子工作项
     * @param {*} val
     * @return {*}
     */
    deleteSubTask(val) {
      this.$confirm('此操作将删除该任务, 是否继续?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
        customClass: 'is-particulars'
      })
        .then(() => {
          workDelProjectItemAPI(val)
            .then(res => {
              const subData = this.childTask
              // eslint-disable-next-line
              for (const i in subData) {
                if (subData[i].taskId == val.taskId) {
                  subData.splice(i, 1)
                  break
                }
              }
              if (val.checked) {
                this.subTaskDoneNum--
              }

              this.$message.success('子工作项删除成功')
            })
            .catch(() => {
              this.$message.error('子工作项删除失败')
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
     * @description: 编辑子工作项
     * @param {*} val
     * @return {*}
     */
    editSubTask(val) {
      this.subTaskID = val.taskId
      const dataList = this.childTask
      // eslint-disable-next-line
      for (const i in dataList) {
        if (dataList[i].taskId == val.taskId) {
          this.$set(dataList[i], 'showEdit', true)
        } else {
          this.$set(dataList[i], 'showEdit', false)
        }
      }
    },

    /**
     * @description: 关闭页面
     * @return {*}
     */
    hideView() {
      this.$emit('close')
      this.$emit('hide-view')
    },

    /**
     * @description: 日志评论
     * @param {*} flag
     * @return {*}
     */
    closeOtherReply(flag) {
      if (!flag && this.$refs.comment_list) {
        this.$refs.comment_list.closeReply()
      }
    },

    /**
     * @description: 子工作项编辑与删除
     * @param {*} value
     * @return {*}
     */
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
@import "./components/taskDetail";

$width: 100%;

.detail-main {
  height: 100%;
}

.d-container {
  padding: 24px 24px 0;
}

.wk-detail-header-title {
  max-width: calc(100% - 40px);
  overflow: hidden;
  font-size: $--font-size-xxlarge;
  font-weight: $--font-weight-bold;
  text-overflow: ellipsis;
  white-space: nowrap;
}

/deep/ .detail-header .wk-detail-header-left {
  width: calc(100% - 40px);
}

.icon-name {
  display: flex;
  align-items: center;
  justify-content: flex-start;
  width: 100%;

  .item-pic {
    display: inline-block;
    width: 24px;
    height: 24px;
    margin-right: 10px;
  }
}

.label {
  margin: 5px 0;
}

// 框架
.main {
  position: relative;
  display: flex;
  flex: 1;
  flex-wrap: nowrap;
  align-items: initial;

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
    // padding: #{$--interval-base * 3} #{$--interval-base * 3}  #{$--interval-base * 4} #{$--interval-base * 4};
    padding: 0 24px 24px 0;
    margin-top: 24px;
    overflow-y: auto;
  }

  .margin-right-base {
    margin-right: $--interval-base;
  }

  .margin-top-base {
    // margin-top: $--interval-base;
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
    margin-bottom: 16px;
  }

  .activity {
    &-title {
      margin-top: $--interval-base * 3;
      font-size: 12px;
    }
  }

  // 完成
  .el-button {
    &.is-done {
      background-color: $--color-g400;
    }
  }
}

// 详情form
.detail-form {
  /deep/ .wk-section-item {
    display: flex;
    align-items: center;
    justify-content: flex-start;
  }

  .wk-user-select.is_valid:hover,
  .wk-user-select:hover {
    background-color: transparent;
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
  margin-bottom: 10px;

  &__hd {
    display: flex;
    align-items: center;
    justify-content: flex-start;

    span {
      font-size: 16px;
      font-weight: $--font-weight-semibold;
    }
  }

  &__bd {
    // margin-top: #{$--interval-base * 2};
    margin-top: 6px;
  }

  /deep/ &-add {
    width: 32px;
    height: 32px;
    line-height: 32px;
    color: $--color-n800;
    text-align: center;
    cursor: pointer;
    border-radius: $--border-radius-base;

    // transform: translateY(-25%);

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
  margin-top: 24px;
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

.sub-task__bd {
  margin-left: 0;
}

.item-img {
  display: inline-block;
  width: 16px;
  height: 16px;
  margin-left: 10px;
}

.sub-num {
  margin-right: 5px;
  margin-left: 8px;
  color: $--color-text-secondary;
}

.line {
  display: inline-block;
  width: 1px;
  height: 16px;
  margin: 0 10px;
  border: 1px solid $--border-color-base;
}

.d-view {
  position: fixed;
  top: $--detail-view-top;
  right: 0;
  bottom: 0;
  width: $--detail-width-base;
  min-width: 926px;
  background: white;
}

.record-wrap {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  justify-content: flex-start;

  .record-desc {
    display: flex;
    align-items: center;
    justify-content: space-between;
    width: 100%;
    margin-bottom: 2px;
    font-size: 12px;
  }

  .el-progress {
    width: 100%;
  }
}

.progress-wrap {
  display: flex;
  align-items: center;
  justify-content: flex-start;
}

.handle-user {
  width: 26px !important;
  height: 26px !important;
}

.wk-quick-create-bar {
  margin-top: 8px;

  /deep/ .el-button {
    span {
      margin-left: 4px;
    }
  }
}

.sub-task-all {
  margin-left: 1px;
}

.priority-box {
  margin-left: 7px;
  line-height: 34px;
  cursor: pointer;
}

.priority-wrap {
  display: flex;
  align-items: center;
  justify-content: flex-start;

  .priority-icon {
    display: inline-block;
    width: 16px;
    height: 16px;
    margin-right: 5px;
  }
}

.edit-wrap {
  display: inline-block;
  height: 100%;
}

.save-cancel {
  margin-top: 10px;
}

.owner-list {
  position: relative;
  display: inline-block;

  // width: 38px;
  // height: 38px;
  margin-right: 10px;
  margin-right: -13px;
  border: 3px solid $--color-white;
  border-radius: 50%;
}

.owner-list-fold:nth-last-child(1) {
  margin-right: 0;
}

.owner-list-button {
  z-index: 3;
  width: 32px;
  height: 32px;
  padding: 0;
  color: $--color-white;
  background: $--color-n90;
  border: 3px solid $--color-white;
  border-radius: 50%;
}

.owner-list-dropdown {
  width: 120px;
  max-height: 300px;
  overflow-y: auto;

  &-list {
    display: flex;
    align-items: center;
    padding-top: 5px;
    padding-bottom: 5px;
    border-left: 2px solid $--color-white;

    &:hover {
      background: $--color-n20 !important;
      border-left: 2px solid $--color-b400;
    }

    &:hover .owner-list-dropdown-text {
      color: $--color-n500;
    }
  }

  &-img {
    height: 24px;
  }

  &-text {
    display: inline-block;
    width: 80px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }
}

.html-wrap {
  position: relative;
  width: 100%;
  overflow: hidden;

  .desc-mask {
    position: absolute;
    bottom: 0;
    left: 0;
    width: 100%;
    height: 64px;
    background: linear-gradient(hsla(0deg, 0%, 100%, 0), #fff);
  }

  .toggle-btn {
    position: absolute;
    bottom: 4px;
    left: 50%;
    box-sizing: border-box;
    display: flex;
    align-items: center;
    justify-content: center;
    width: 94px;
    height: 28px;
    margin-left: -47px;
    font-size: 13px;
    color: #06f;
    cursor: pointer;
    background-color: #fff;
    border-radius: 14px;
    box-shadow: 0 3px 8px #d4d4d4, 0 1px 4px #d4d4d4;
    -webkit-transform: translateY(-50%);
    transform: translateY(-50%);
  }

  /deep/ img {
    display: block;
    max-width: 100%;
    cursor: pointer;
  }
}

.arrow {
  margin-right: 5px;
  margin-left: 0;
}

.desc-box {
  text-align: justify;
  white-space: normal !important;
}

.full-content-style {
  max-height: none;
  padding-bottom: 60px;
}

.part-content-style {
  max-height: 320px;
}

.dropdown-menu {
  max-height: 400px;
  overflow: auto;
}
</style>
