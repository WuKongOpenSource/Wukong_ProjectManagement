<template>
  <div class="tag-list">
    <template v-if="labelList.length <= 2">
      <div
        v-for="(k, j) in labelList"
        :key="j"
        :style="{
          'background': colorRgb(k.color,0.1),
          color: k.color
        }"
        class="item-label">
        {{ k.labelName }}
      </div>
    </template>
    <template v-else>
      <div
        :style="{
          'background': colorRgb(labelList[0].color,0.1),
          color: labelList[0].color
        }"
        class="item-label">{{ labelList[0].labelName }}</div>
      <div
        :style="{
          'background': colorRgb(labelList[1].color,0.1),
          color: labelList[1].color
        }"
        class="item-label">{{ labelList[1].labelName }}</div>
      <el-tooltip
        placement="top"
        effect="light"
        popper-class="tooltip-change-border task-tooltip">
        <div
          slot="content"
          style="margin: 10px 10px 10px 0;">
          <div
            v-for="(k, j) in labelList"
            :key="j"
            style="display: inline-block; margin-right: 10px;">
            <span
              v-if="j >= 2"
              :style="{
                'background': colorRgb(k.color ? k.color: '#ccc',0.1),
                color: k.color ? k.color: '#ccc'
              }"
              class="k-name"
              style=" padding: 3px 10px; color: #fff;border-radius: 4px;">{{ k.labelName }}</span>
          </div>
        </div>
        <div class="color-label-more">
          <i>...</i>
        </div>
      </el-tooltip>

    </template>
  </div>
</template>

<script>
import { convertHexByOpacity } from '@/utils'

export default {
  name: 'TagList',
  props: {
    labelList: {
      type: Array,
      default() {
        return []
      }
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
.tag-list {
  display: flex;

  .item-label {
    display: inline-block;
    height: 20px;
    padding: 0 10px;
    margin-right: 4px;

    // margin-bottom: 4px;
    font-size: 12px;
    line-height: 20px;
    color: #fff;
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
