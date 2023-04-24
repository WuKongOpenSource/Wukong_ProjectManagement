<template>
  <div>
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

<script type="text/javascript">
import WkHeadSection from '@/components/NewCom/WkHeadSection'
export default {
  name: 'DetailInfo',
  components: {
    WkHeadSection
  },
  data() {
    return {
      detailList: [{
        label: '优先级',
        formType: 'priority',
        field: 'priority'
      }, {
        label: '负责人',
        formType: 'user',
        field: 'mainUser'
      }, {
        label: '开始时间',
        formType: 'date',
        field: 'startTime'
      }, {
        label: '结束时间',
        formType: 'date',
        field: 'stopTime'
      }]
    }
  },

  methods: {
    /**
     * @description: 获取权限
     * @param {*} key
     * @return {*}
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
     * @description: 紧急按钮
     * @param {*} value
     * @param {*} def
     * @return {*}
     */
    priorityBtn(value, def) {
      // this.taskData.priority = value.id
      // workTaskPrioritySetAPI({
      //   taskId: this.id,
      //   priority: value.id
      // })
      //   .then(res => {
      //     this.$emit('on-handle', {
      //       type: 'change-priority',
      //       value: value,
      //       index: this.detailIndex,
      //       section: this.detailSection
      //     })
      //     this.priorityVisible = false
      //   })
      //   .catch(() => {
      //     this.$message.error('编辑失败')
      //     this.taskData.priority = def
      //   })
    },

    /**
     * @description: 上传负责人信息
     * @param {*} mainUser
     * @return {*}
     */
    submiteMainUser(mainUser) {
      // workTaskMainUserSetAPI({
      //   taskId: this.id,
      //   userId: mainUser ? mainUser.userId : ''
      // })
      //   .then(res => {
      //     if (mainUser) {
      //       this.$set(this.taskData, 'mainUser', mainUser)
      //     } else {
      //       this.$set(this.taskData, 'mainUser', null)
      //     }

      //     this.$emit('on-handle', {
      //       type: 'change-main-user',
      //       value: mainUser,
      //       index: this.detailIndex,
      //       section: this.detailSection
      //     })
      //   })
      //   .catch(() => {})
    },

    /**
     * @description: 截至日期API
     * @param {*} type
     * @return {*}
     */
    deleteTime(type) {
      this.taskData[type] = ''
      this.timeChange(type)
    },

    timeChange(type) {
      // const params = {
      //   taskId: this.id,
      //   startTime: this.taskData.startTime || '',
      //   stopTime: this.taskData.stopTime || ''
      // }
      // params[type] = this.taskData[type] || ''
      // if (!params.startTime && !params.stopTime) {
      //   this.$message.error('开始和结束时间必须至少有一个不能为空')
      //   return
      // }
      // workTaskTimeSetAPI(params)
      //   .then(res => {
      //     // 停止时间回调
      //     if (type == 'stopTime') {
      //       this.$emit('on-handle', {
      //         type: 'change-stop-time',
      //         value: this.taskData[type],
      //         index: this.detailIndex,
      //         section: this.detailSection
      //       })
      //     }
      //   })
      //   .catch(() => {})
    }
  }
}
</script>
