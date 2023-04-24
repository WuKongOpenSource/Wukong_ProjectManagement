<template>
  <div class="head-section wrap">
    <div :class="{ 'is-unfold': unfold }" class="section-head" @click="unfold = !unfold">
      <div class="section-title text-one-line">{{ label }}</div>
      <slot name="otherLabel" />
      <i class="el-icon-arrow-down" />
    </div>

    <div v-if="unfold" class="section-body">
      <slot />
    </div>
  </div>
</template>

<script>
export default {
  // 带头部的section
  name: 'HeadSection',

  components: {},

  props: {
    label: [String, Number]
  },

  data() {
    return {
      unfold: true // 展开
    }
  },

  computed: {},

  watch: {},

  created() {},

  mounted() {},

  beforeDestroy() {},

  methods: {}
}
</script>

<style lang="scss" scoped>
.wrap {
  .section-head {
    position: relative;
    display: flex;
    align-items: center;
    height: 48px;
    padding: 12px;
    cursor: pointer;
    user-select: none;
    border: 1px solid $--border-color-base;
    border-radius: 4px;

    &.is-unfold {
      border-radius: 4px 4px 0 0;
    }

    &:hover {
      background-color: $--color-n20;
    }
  }

  .section-title {
    flex-shrink: 0;
    max-width: 60%;
    padding-right: 4px;
    font-size: 14px;
    font-weight: $--font-weight-semibold;
  }

  .el-icon-arrow-down {
    display: flex;
    margin-left: auto;
  }

  .section-body {
    box-sizing: border-box;
    padding: 8px 12px;
    border-width: 0 1px 1px;
    border-top-color: initial;
    border-top-style: initial;
    border-right-color: $--border-color-base;
    border-right-style: solid;
    border-bottom-color: $--border-color-base;
    border-bottom-style: solid;
    /* stylelint-disable-next-line declaration-block-no-redundant-longhand-properties */
    border-left-color: $--border-color-base;
    /* stylelint-disable-next-line declaration-block-no-redundant-longhand-properties */
    border-left-style: solid;
    border-bottom-right-radius: 4px;
    border-bottom-left-radius: 4px;
    border-image: initial;
  }
}
</style>
