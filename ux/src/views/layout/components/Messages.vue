<template>
  <slide-view
    class="d-view"
    append-to-body
    :body-style="{padding: 0, height: '100%'}"
    @afterEnter="viewAfterEnter"
    @close="$emit('close')">
    <div class="messages">
      <div class="messages-menus">
        <div class="title">通知/待办</div>
        <div class="menus-wrap">
          <menu-item
            v-for="(item, index) in menus"
            :key="index"
            :icon="item.iconClass"
            :title="item.name"
            :num="item.num"
            :selectd="menuIndex === item.infoType"
            @click.native="menuClick(item, index)"
          />
        </div>
      </div>
      <div v-if="show" class="messages-body">
        <system-message
          :unread-nums="unreadNums"
          lazy
          :show="menuIndex == 'message'"
          @update-count="sendSystemUnreadNum"
        />

        <system-message
          :unread-nums="unreadNums"
          only-announcement
          lazy
          :show="menuIndex == 'announce'"
          @update-count="sendSystemUnreadNum"
        />
      </div>
    </div>
  </slide-view>
</template>

<script>
import SlideView from '@/components/SlideView'
import MenuItem from '@/views/layout/components/Sidebar/Item'
import SystemMessage from './SystemMessage'

// 待办事项
import { mapGetters } from 'vuex'

export default {
  // 系统消息
  name: 'Messages',

  components: {
    SlideView,
    MenuItem,
    SystemMessage,
  },

  props: {
    unreadNums: Object
  },

  data() {
    return {
      menuIndex: 'message',
      show: false
    }
  },

  computed: {
    ...mapGetters(['messageNum']),

    menus() {
      const menus = [{
        name: '消息提醒',
        infoType: 'message',
        iconClass: 'wk wk-bell-line',
        num: this.unreadNums ? this.unreadNums.allCount : 0,
        tips: ''
      }, {
        name: '公告',
        infoType: 'announce',
        iconClass: 'wk wk-announcement-line',
        num: this.unreadNums ? this.unreadNums.announceCount : 0,
        tips: ''
      }]
      return menus
    }
  },

  watch: {},

  created() {},

  mounted() {
    this.show = true
  },

  beforeDestroy() {},

  methods: {
    /**
     * 动画结束页面展示
     */
    viewAfterEnter() {
      this.requestNumCount()
    },

    /**
     * 待办事项
     */
    requestNumCount() {
      this.$store
        .dispatch('GetMessageNum')
    },

    /**
     * 菜单点击
     */
    menuClick(item) {
      this.menuIndex = item.infoType
    },

    /**
     * 更新未读数量
     */
    sendSystemUnreadNum() {
      this.$emit('update-unread')
    }
  }
}
</script>

<style lang="scss" scoped>
.d-view {
  position: fixed;
  top: 0;
  right: 0;
  bottom: 0;
  width: $--detail-width-base;
  min-width: 950px;
}

.messages {
  position: relative;
  height: 100%;
  overflow-y: auto;

  &-menus {
    position: absolute;
    top: 0;
    left: 0;
    width: 240px;
    height: 100%;
    overflow: auto;
    background-color: $--color-n10;

    > .title {
      padding: 24px 32px 8px;
      font-size: 18px;
    }

    > .menus-wrap {
      padding: 0 16px;
    }
  }

  &-body {
    position: relative;
    height: 100%;
    margin-left: 240px;
    overflow: hidden;
    background-color: white;
    box-shadow: 0 0 3px rgba(0, 0, 0, 0.1);
  }
}
</style>
