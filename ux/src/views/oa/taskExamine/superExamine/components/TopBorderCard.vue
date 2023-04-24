<template>
  <div
    class="statistics-card card">
    <flexbox class="card-title">
      <span v-if="move" class="filter-handle">⋮⋮</span>
      <div class="card-title-center text-one-ellipsis"><slot name="title-left" /></div>
      <div class="card-title-right">
        <slot name="title-right" />
      </div>
    </flexbox>
    <flexbox v-if="screen" class="filter-bar">
      <flexbox class="left">
        <slot name="filter-left" />
      </flexbox>
      <div class="right">
        <slot name="filter-right" />
      </div>
    </flexbox>
    <slot />
  </div>
</template>

<script>

export default {
  // 统计卡片
  name: 'TopBorderCard',

  components: {},

  props: {
    icon: String,
    move: {
      type: Boolean,
      default: true
    },
    fixedToday: {
      type: String,
      default: ''
    },
    screen: {
      type: Boolean,
      default: false
    }
  },

  data() {
    return {
    }
  },

  computed: {},

  watch: {},

  created() {},

  mounted() {},

  beforeDestroy() {},

  methods: {
  }
}
</script>

<style lang="scss" scoped>
.card {
  // border: 1px solid $--border-color-base;
  position: relative;
  padding: #{$--interval-base * 2};
  background-color: white;
  border-radius: $--border-radius-base;
  box-shadow: rgba(9, 30, 66, 0.25) 0 1px 1px, rgba(9, 30, 66, 0.13) 0 0 1px 1px;

  &::before {
    position: absolute;
    top: 0;
    right: 0;
    left: 0;
    height: 4px;
    content: " ";
    background-color: $--color-primary;
    border-radius: 2px;
  }

  &-title {
    display: flex;

    .icon {
      padding: 3px;
      margin-right: $--interval-base;
      font-size: 12px;
      color: white;
      background-color: $--color-primary;
      border-radius: $--border-radius-base;
    }

    &-center {
      flex: 1;
      font-size: 16px;
      font-weight: bold;

      // margin-left: 5px;
    }

    &-right {
      flex-shrink: 0;

      i {
        cursor: pointer;
      }
    }
  }

  // &-desc {
  //   margin: #{$--interval-base * 2} 0 $--interval-base;
  // }

  &.is-close {
    height: auto !important;
  }

  /* 筛选条 */
  .filter-bar {
    > .left {
      flex: 1;
      width: 0;
      margin-top: 16px;
    }

    > .right {
      flex-shrink: 0;
    }
  }

  .filter-tag {
    display: inline-block;
    height: 32px;
    padding: 0 8px;
    line-height: 32px;
    color: $--color-n70;
    background-color: $--background-color-base;
    border-radius: $--border-radius-base;
  }

  .filter-tag + .filter-tag {
    margin-left: $--interval-base;
  }

  .wk-shrink,
  .wk-full {
    user-select: none;
  }

  .filter-handle {
    font-weight: bold;
    cursor: move;
  }

  .filter-handle + .card-title-center {
    margin-left: $--interval-base;
  }

  .card-title-center {
    display: flex;
  }
}
</style>
