<template>
  <div
    class="list-wrap"
    :class="{'clicked': item.clicked}">
    <div class="item-title">{{ item.name }}</div>
    <div v-if="item.stopTime">
      <el-tag
        disable-transitions
        :class="item.status == 3 ? 'is-common' :[getOverTimeStatus(item.stopTime)]"
        class="project-tag">
        {{ formatTime(item.stopTime) + '截止' }}
      </el-tag>
    </div>
    <div class="item-desc">
      <div class="item-left">
        <img class="item-img" :src="getIconClass(item)" alt="">
        <span class="mark">#{{ item.num }}</span>
      </div>
      <div class="item-right">
        <span class="icon-name">
          <img class="item-img" :src="getPriorityPic(item.priority)" alt="">
          <span>{{ getPriority(item.priority) }}</span>
        </span>
        <status-tag
          :status-name="item.boardStatusName"
          :type="item.status" />
        <xr-avatar
          v-if="item.mainUserId"
          :id="item.mainUserId"
          class="user-photo"
          trigger="hover"
          :name="item.mainUserName"
          :size="24"
          :src="item.mainUserImg" />
      </div>
    </div>
  </div>
</template>

<script>
import StatusTag from '@/views/pm/project/components/StatusTag'

import {
  convertPriority,
  getItemImg,
  getPriorityIcon
} from '@/views/pm/data'

export default {
  name: 'DraggableItem',
  components: {
    StatusTag
  },
  props: {
    item: {
      type: Object,
      required: true
    }
  },
  data() {
    return {
      isShowTooltip: true
    }
  },
  computed: {},
  watch: {},
  created() {

  },
  mounted() {

  },
  methods: {
    /**
     * @description: 鼠标移动
     * @return {*}
     */
    moveEndTask() {
      console.log('移动')
    },

    /**
     * @description: 鼠标移至
     * @return {*}
     */
    onMouseOver() {
      if (this.$refs.title.offsetWidth > 224) {
        this.isShowTooltip = false
      } else {
        this.isShowTooltip = true
      }
    },

    /**
     * @description: 事项图标
     * @param {*} row
     * @return {*}
     */
    getIconClass(row) {
      return getItemImg(row)
    },

    /**
     * @description: 优先级文字
     * @param {*} val
     * @return {*}
     */
    getPriority(val) {
      return convertPriority(val)
    },

    /**
     * @description: 优先级图片
     * @param {*} val
     * @return {*}
     */
    getPriorityPic(val) {
      return getPriorityIcon(val)
    },

    /**
     * 超时状态
     * @param time
     * @returns {boolean}
     */
    getOverTimeStatus(time) {
      const data = this.handleTime(time)
      if (data === '今天' || data === '明天') {
        return 'is-near'
      } else {
        return this.$moment(time).isBefore(this.$moment()) ? 'is-over' : 'is-common'
      }
    },

    /**
     * 时间格式化
     * @param time
     * @returns {null|*}
     */
    formatTime(time) {
      if (!time) return null
      const data = this.handleTime(time)
      if (data) {
        return data
      } else {
        const flag = this.$moment().isSame(time, 'year')
        return flag
          ? this.$moment(time).format('M月D日')
          : this.$moment(time).format('YYYY年M月D日')
      }
    },

    /**
     * @description: 根据时间处理今天/明天/昨天的情况
     * @return {*}
     */
    handleTime(time) {
      const activeDataArr = this.$moment(time).format('YYYY-MM-DD').split('-')
      const todayDate = new Date() // 今天
      const nowDataArr = [todayDate.getFullYear(), todayDate.getMonth() + 1, todayDate.getDate()] // 今天的年月日
      const tomorrowData = new Date(todayDate.setTime(todayDate.getTime() + 24 * 60 * 60 * 1000)) // 明天
      const tomorrowDataArr = [tomorrowData.getFullYear(), tomorrowData.getMonth() + 1, tomorrowData.getDate()] // 明天的年月日
      const yesterdayDate = new Date(todayDate.setTime(todayDate.getTime() - 48 * 60 * 60 * 1000)) // 昨天
      const yesterdayDateArr = [yesterdayDate.getFullYear(), yesterdayDate.getMonth() + 1, yesterdayDate.getDate()] // 昨天的年月日
      if (nowDataArr[0] == activeDataArr[0] && nowDataArr[1] == activeDataArr[1] && nowDataArr[2] == activeDataArr[2]) {
        return '今天'
      } else if (tomorrowDataArr[0] == activeDataArr[0] && tomorrowDataArr[1] == activeDataArr[1] && tomorrowDataArr[2] == activeDataArr[2]) {
        return '明天'
      } else if (yesterdayDateArr[0] == activeDataArr[0] && yesterdayDateArr[1] == activeDataArr[1] && yesterdayDateArr[2] == activeDataArr[2]) {
        return '昨天'
      } else {
        return false
      }
    }
  }
}
</script>
<style lang='scss' scoped>
.list-wrap {
  width: 100%;
  padding: 12px;
  background-color: #fff;
  border: 1px solid $--border-color-base;
  border-radius: 4px;

  &:hover {
    background-color: #fafbfc;

    .item-title {
      text-decoration: underline;
    }
  }

  .item-title {
    width: 100%;
    margin-bottom: 5px;
    font-size: 14px;
    color: $--color-black;
    word-break: break-all;
    word-wrap: break-word;
    white-space: normal;
  }

  .item-desc {
    display: flex;
    align-items: center;
    justify-content: space-between;

    .item-left {
      display: flex;
      align-items: center;
      justify-content: flex-start;

      .item-img {
        display: inline-block;
        width: 16px;
        height: 16px;
        margin-right: 5px;
      }

      .mark {
        font-size: 14px;
        color: $--color-text-secondary;
      }
    }

    .item-right {
      display: flex;
      align-items: center;
      justify-content: flex-end;
    }
  }
}

.item-img {
  display: inline-block;
  width: 16px;
  height: 16px;
  margin-right: 5px;
}

.icon-name {
  display: flex;
  align-items: center;
  justify-content: flex-start;
  margin-right: 10px;
}

.underline {
  border-bottom: 1px solid $--border-color-base;
}

.user-photo {
  margin-bottom: -3.09px;
  margin-left: 8px;
}

.project-tag {
  height: 20px;
  padding: 0 8px;
  margin: 4px 0 8px;
  font-size: 12px;

  // line-height: 18px;

  &.is-over {
    color: white;
    background-color: $--color-r400;
  }

  &.is-near {
    color: white;
    background-color: $--color-y400;
  }

  &.is-common {
    color: $--color-black;
    background-color: $--background-color-base;
  }
}

.clicked {
  background-color: #deebff !important;
}
</style>
