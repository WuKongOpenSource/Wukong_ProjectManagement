<template>
  <div
    class="tag-view"
    :style="{ 'flex-wrap': wrap}">
    <template v-if="value && value.length <= maxLength">
      <div
        v-for="(item, index) in value"
        :key="index"
        :style="{'background': colorRgb(item.value,0.1), 'margin-bottom': `${itemBottom}px`,'color':item.value}"
        class="item-label">
        <span>{{ item.name }}</span>
      </div>
    </template>
    <template v-if="value && value.length > maxLength">
      <div
        v-for="(item, index) in value.slice(0, maxLength)"
        :key="index"
        :style="{'background':colorRgb(item.value,0.1), 'margin-bottom': `${itemBottom}px`,'color':item.value}"
        class="item-label">{{ item.name }}</div>
      <el-tooltip
        placement="top"
        effect="light"
        popper-class="tooltip-change-border">
        <div
          slot="content"
          class="tooltip-box">
          <span
            v-for="(item, index) in value"
            v-show="index >= maxLength"
            :key="index"
            class="tooltip-item"
            :style="{'background':colorRgb(item.value,0.1), 'margin-bottom': `${itemBottom}px`,'color': item.value}">
            {{ item.name }}
          </span>
        </div>
        <el-button
          size="mini"
          style="height: 20px;"
          icon="el-icon-more" />
      </el-tooltip>
    </template>

    <slot />
  </div>

</template>
<script>
import { convertHexByOpacity } from '@/utils'

export default {
  name: 'TagView',
  props: {
    value: null,
    maxLength: {
      type: Number,
      default: 2
    },
    wrap: {
      type: String,
      default: 'nowrap'
    },
    itemBottom: {
      type: Number,
      default: 4
    }
  },
  data() {
    return {
    }
  },
  methods: {
    /**
     * 转换颜色成rgb格式
     */
    colorRgb(hexCode, opacity) {
      return convertHexByOpacity(hexCode, opacity)
    }
  }
}
</script>
<style lang='scss' scoped>
.tag-view {
  display: flex;

  .item-label {
    display: inline-block;
    height: 20px;
    padding: 0 10px;
    margin-right: 4px;
    margin-bottom: 4px;
    font-size: 12px;
    line-height: 20px;
    color: #fff;
    white-space: nowrap;
    border-radius: $--border-radius-base;
  }

  .color-label-more {
    position: relative;
    display: inline-block;
    width: 34px;
    height: 20px;
    font-size: inherit;
    font-weight: 700;
    line-height: 20px;
    text-align: center;
    vertical-align: middle;
    background: #eee;
    border-radius: $--border-radius-base;

    // margin-bottom: 6px;
    i {
      position: absolute;
      top: 0%;
      left: 50%;
      height: 20px;
      line-height: 36px;
      transform: translate(-50%, -50%);
    }
  }
}

.tooltip-box {
  margin: 8px 0 4px;

  .tooltip-item {
    display: inline-block;
    padding: 3px 10px;
    margin-right: 10px;
    margin-bottom: 4px;
    color: #fff;
    border-radius: 4px;
  }
}
</style>
