<template>
  <flexbox class="wk-ico-select">
    <wk-ico-popover
      ref="icoPopover"
      v-bind="$attrs"
      :data-value="value"
      @change="icoChange">
      <div
        slot="reference"
        :style="{ 'background': value.color }"
        class="wk-ico-preview">
        <i
          v-if="value.icon"
          :style="{ color: iconColor }"
          :class="value.icon"
          class="icon" />

        <div class="wk wk-edit handle-hook" />
      </div>
    </wk-ico-popover>
  </flexbox>
</template>

<script>
import WkIcoPopover from './Popover'

export default {
  // 图标选择
  name: 'WkIcoSelect',

  components: {
    WkIcoPopover
  },

  props: {
    value: {
      type: Object
    },
    iconColor: {
      type: String,
      default: '#FFFFFF'
    }
  },

  methods: {
    icoChange(data) {
      this.$emit('input', data)
    }
  }
}
</script>

<style lang="scss" scoped>
.wk-ico-select {
  display: inline-flex;

  .wk-ico-preview {
    position: relative;
    width: 42px;
    height: 42px;
    margin-right: 25px;
    line-height: 42px;
    text-align: center;
    cursor: pointer;
    border-radius: $--border-radius-base;

    .icon {
      font-size: $--font-size-extra-large;
      color: white;
    }

    .handle-hook {
      position: absolute;
      right: 0;
      bottom: 0;
      width: 18px;
      height: 18px;
      font-size: $--font-size-base;
      line-height: 18px;
      color: white;
      text-align: center;
      background-color: $--color-n300A;
      border-top-left-radius: $--border-radius-base;
      border-bottom-right-radius: $--border-radius-base;
    }
  }
}
</style>
