<template>
  <el-popover
    v-model="visible"
    v-bind="$attrs"
    :width="width"
    popper-class="wk-ico-popover no-padding-popover"
    trigger="click">
    <template v-if="dataValue">
      <flexbox
        v-if="!hiddenColor"
        class="colors"
        justify="space-between">
        <div
          v-for="(item, index) in colorList"
          :key="index"
          :style="{ 'background': item.color }"
          class="color"
          @click="colorSelect(item)">
          <i v-if="item.color === dataValue.color" class="wk wk-success" />
        </div>
      </flexbox>

      <div class="line" />

      <div class="icons-wrapper">
        <flexbox
          class="icons"
          wrap="wrap">
          <div
            v-for="(item, index) in iconList"
            :key="index"
            :class="{'is-select': dataValue.icon === item}"
            class="icon"
            @click="iconSelect(item)">
            <i :class="item" />
          </div>
        </flexbox>
      </div>
    </template>

    <slot slot="reference" name="reference" />
  </el-popover>
</template>

<script>
import { isObject } from '@/utils/types'
import IconfontLib from '@/styles/iconfonts'

const icoDefaultValue = {
  color: '#0052CC',
  icon: ''
}

export default {
  // 图标选择
  name: 'WkIcoPopover',

  components: {},

  inheritAttrs: false,

  props: {
    width: {
      type: String,
      default: '445'
    },
    hiddenColor: {
      type: Boolean,
      default: false
    },
    dataValue: {
      type: Object,
      default: () => {
        return {
          ...icoDefaultValue
        }
      }
    }
  },

  data() {
    return {
      visible: false,
      color: '',
      colorList: [
        { color: '#0052CC' },
        { color: '#97A0AF' },
        { color: '#6554C0' },
        { color: '#FF5630' },
        { color: '#FFAB00' },
        { color: '#36B37E' },
        { color: '#00B8D9' },
        { color: '#42526E' }
      ],

      iconPrefix: '',
      iconList: []
    }
  },

  computed: {},

  watch: {},

  created() {
    if (!isObject(this.dataValue)) {
      this.$emit('change', {
        ...icoDefaultValue
      })
    }
    const arr = []
    const keys = Object.keys(IconfontLib)
    keys.forEach(key => {
      const iconPrefix = `${IconfontLib[key].font_family} ${IconfontLib[key].css_prefix_text}`
      IconfontLib[key].glyphs.forEach(item => {
        arr.push(`${iconPrefix}${item.font_class}`)
      })
    })
    this.iconList = arr
  },

  mounted() {},

  beforeDestroy() {},

  methods: {
    /**
     * 颜色选择
     */
    colorSelect(item) {
      this.dataValue.color = item.color
      this.$emit('change', this.dataValue)
      this.$emit('colorChange', item.color)
    },

    /**
     * 图标选择
     */
    iconSelect(icon) {
      this.dataValue.icon = icon
      this.$emit('change', this.dataValue)
      this.$emit('iconChange', icon)
    }
  }
}
</script>

<style lang="scss" scoped>
.wk-ico-popover {
  .colors {
    padding: 0 20px;
    margin-bottom: 12px;

    .color {
      flex-shrink: 0;
      width: 45px;
      height: 45px;
      text-align: right;
      cursor: pointer;
      border-radius: $--border-radius-base;

      i {
        margin-right: 3px;
        color: white;
      }
    }
  }

  .line {
    width: 401px;
    height: 1px;
    margin-left: 20px;
    border-top: 1px solid $--border-color-base;
  }

  .icons-wrapper {
    width: 100%;
    height: 360px;
    padding: 0 20px 20px;
    margin-top: -1px;
    overflow-y: auto;

    .icons {
      width: 401px;
      border-left: 1px solid $--border-color-base;

      .icon {
        flex-shrink: 0;
        width: 40px;
        height: 40px;
        font-size: 18px;
        line-height: 42px;
        text-align: center;
        cursor: pointer;
        border-right: 1px solid $--border-color-base;
        border-bottom: 1px solid $--border-color-base;

        &.is-select {
          background: #e9efff;
        }
      }
    }
  }
}
</style>

<style lang="scss">
.wk-ico-popover {
  .el-popover__title {
    padding: 20px 20px 0;
  }
}
</style>
