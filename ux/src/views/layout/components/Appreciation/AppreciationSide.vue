<template>
  <slide-view
    class="d-view"
    append-to-body
    :body-style="{padding: 0, height: '100%'}"
    @afterEnter="viewAfterEnter"
    @close="$emit('close')">
    <div class="help">
      <div class="help-menus">
        <div class="title">增值服务</div>
        <div class="menus-wrap">
          <menu-item
            v-for="(item, index) in menus"
            :key="index"
            :icon="item.iconClass"
            :title="item.name"
            :selectd="menuIndex === item.value"
            @click.native="menuClick(item, index)" />
        </div>
      </div>
      <div class="help-body">
        <component
          :is="menuIndex"
          :id="subMenuIndex"
          :class="`is-${menuIndex}`" />
      </div>
    </div>
  </slide-view>
</template>

<script>

import SlideView from '@/components/SlideView'
import MenuItem from '@/views/layout/components/Sidebar/Item'
import Signature from './Signature'
import Conversion from './Conversion'
import BusinessInformation from './BusinessInformation'
import Call from './Call'
import Scan from './Scan'
import Card from './Card'
import Bill from './Bill'

export default {
  // HelpSide
  name: 'AppreciationSide',

  components: {
    SlideView,
    MenuItem,
    Signature,
    Conversion,
    Call,
    BusinessInformation,
    Scan,
    Card,
    Bill
  },

  props: {
    props: Object
  },

  data() {
    return {
      menuIndex: 'BusinessInformation',
      menus: [{
        name: '工商信息',
        value: 'BusinessInformation',
        iconClass: 'wk wk-icon-business-info',
        tips: ''
      }, {
        name: '呼叫中心',
        value: 'Call',
        iconClass: 'wk wk-phone-radio',
        tips: ''
      }, {
        name: '语音转换',
        value: 'Conversion',
        iconClass: 'wk wk-icon-voice-convert',
        tips: ''
      }, {
        name: '电子签章',
        value: 'Signature',
        iconClass: 'wk wk-icon-seal',
        tips: ''
      }, {
        name: '扫码枪',
        value: 'Scan',
        iconClass: 'wk wk-icon-scanner',
        tips: ''
      }, {
        name: '名片扫描',
        value: 'Card',
        iconClass: 'wk wk-icon-business-card',
        tips: ''
      }, {
        name: '发票扫描',
        value: 'Bill',
        iconClass: 'wk wk-invoice-line',
        tips: ''
      }],
      subMenus: [],
      subMenuIndex: '',
      showPay: false,
      payGoodsId: null
    }
  },

  computed: {

  },

  watch: {},

  created() {
    this.getData()
  },

  mounted() {
  },

  beforeDestroy() {
  },

  methods: {
    /**
     * 动画结束页面展示
     */
    viewAfterEnter() {
    },

    /**
     * 菜单点击
     */
    menuClick(item) {
      this.menuIndex = item.value
    },
    /**
     * 返回
     */
    backClick() {
      this.menuIndex = 'Question'
    },

    /**
     * 数据获取
     */
    getData() {
      this.menuIndex = this.props.type
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
  min-width: 810px;
}

.menu-item-content {
  .wk-icon-circle-right {
    visibility: hidden;
  }

  &:hover {
    .wk-icon-circle-right {
      visibility: visible;
    }
  }
}

.wk-icon-circle-right {
  position: absolute;
  top: 2px;
  right: 0;
}

.help {
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
      height: calc(100% - 70px);
      padding: 0 16px;
      overflow-y: auto;

      &.is-sub {
        padding-right: 0;
      }
    }

    .menus-wrap-hd {
      position: relative;
      line-height: 40px;

      &::before {
        position: absolute;
        right: -16px;
        bottom: 0;
        left: -16px;
        height: 1px;
        content: " ";
        background-color: $--border-color-base;
      }
    }

    .menus-wrap-bd {
      height: calc(100% - 40px);
      padding-top: 8px;
      padding-right: 8px;
      overflow-y: auto;
    }
  }

  &-body {
    position: relative;
    height: 100%;
    margin-left: 240px;
    overflow: hidden;
    background-color: white;
    box-shadow: 0 0 3px rgba(0, 0, 0, 0.1);

    &.no-padding {
      padding: 0;
    }

    &.is-Question {
      padding-left: 16px;
    }
  }
}

.letter-block {
  font-size: $--font-size-base;

  > .header {
    position: relative;
    height: 21px;
    line-height: 21px;

    > .label {
      position: absolute;
      left: 0;
      z-index: 2;
      padding-right: 8px;
      color: $--color-n70;
      background-color: $--color-n10;
    }

    > .line {
      position: absolute;
      top: 10px;
      right: 0;
      left: 0;
      z-index: 1;
      height: 0.5px;
      background-color: $--border-color-base;
    }
  }
}
</style>
