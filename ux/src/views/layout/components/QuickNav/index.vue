<template>
  <div class="main">
    <scroll-pane
      ref="scrollContainer"
      class="scroll-container quick-nav-list"
    >
      <router-link
        v-for="(tag, index) in financeVisitedViews"
        :key="tag.path"
        ref="tag"
        :class="{ active: tag.path == $route.path }"
        :to="{ path: tag.path, query: tag.query, fullPath: tag.fullPath }"
        tag="div"
        class="quick-nav-item"
      >{{ tag.meta.title }}
        <i
          v-if="index !== 0"
          class="el-icon-close"
          @click.stop="closeQuickNav(tag.active ? 'current' : 'assign', tag)"
        />
      </router-link>

      <div class="quick-nav-item">
        <el-dropdown placement="right-start">
          <i class="el-icon-arrow-down" />
          <el-dropdown-menu slot="dropdown" class="no-padding-popover quick-close">
            <el-dropdown-item :disabled="$route.path==='/fm/workbench'" @click.native="closeQuickNav('current')">关闭当前页</el-dropdown-item>
            <el-dropdown-item @click.native="closeQuickNav('all')">关闭全部</el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
      </div>
    </scroll-pane>
  </div>
</template>

<script>
import ScrollPane from './ScrollPane'

import { mapGetters } from 'vuex'
export default {
  name: 'QuickNav',
  components: {
    ScrollPane
  },
  data() {
    return {
      cache: false,
      quickNavVisible: false // 快捷导航关闭的popover弹窗
    }
  },

  computed: {
    ...mapGetters(['financeVisitedViews'])
  },
  watch: {
    $route(to, from) {
      console.log('路由对象', to, from)
      this.$store.dispatch('addVisitedView', to)
      this.moveToCurrentTag()
    }
  },
  created() {
    console.log(this.$store)
    if (this.$route.path !== '/fm/workbench') {
      this.$store.dispatch('addVisitedView', {
        path: '/fm/workbench',
        name: 'workbench',
        meta: {
          title: '仪表盘'
        }
      })
    }
    this.$store.dispatch('addVisitedView', this.$route)
  },
  methods: {
    /**
     * 快捷导航跳转
     */
    targetQuickNav(data) {
      this.$router.push({ path: data.path })
    },
    handleScroll(e) {
      console.log(e)
    },
    moveToCurrentTag() {
      const tags = this.$refs.tag
      this.$nextTick(() => {
        for (const tag of tags) {
          if (tag.to.path === this.$route.path) {
            console.log(tag)
            this.$refs.scrollContainer.moveToTarget(tag)
            break
          }
        }
      })
    },
    /**
     * 关闭快捷导航
     */
    closeQuickNav(type, data = this.$route) {
      if (type == 'all') {
        this.$store.dispatch('clearVisItedViews')
        this.$router.push({ path: '/fm/workbench' })
        this.$store.dispatch('addVisitedView', {
          path: '/fm/workbench',
          name: 'workbench',
          meta: {
            title: '仪表盘'
          }
        })
      } else {
        if (data.path == '/fm/workbench') return
        this.$store.dispatch('deleteVisItedView', data)
        if (data.path == this.$route.path) {
          if (this.financeVisitedViews.length) {
            this.$router.push({
              path: this.financeVisitedViews[
                this.financeVisitedViews.length - 1
              ].path
            })
          } else {
            this.$router.push({ path: '/fm/workbench' })
            this.$store.dispatch('addVisitedView', {
              path: '/fm/workbench',
              name: 'workbench',
              meta: {
                title: '仪表盘'
              }
            })
          }
        }
      }
      this.quickNavVisible = false
    }
  }
}
</script>

<style lang="scss" scoped>
.main {
  position: relative;
  height: 100%;

  .quick-nav-list,
  .scroll-container {
    max-width: calc(100vw - 400px);

    // overflow: scroll;
    /deep/.el-scrollbar__wrap {
      .el-scrollbar__view {
        display: flex;
        flex-wrap: nowrap;
        align-items: center;
        scrollbar-width: none;
        -ms-overflow-style: none;

        &::-webkit-scrollbar {
          display: none; /* Chrome Safari */
        }
      }
    }
  }

  .quick-nav-item {
    flex-shrink: 0;
    padding: 4px 8px;
    margin-left: 8px;
    cursor: pointer;
    border-left: 1px solid $--color-n30;
  }

  .quick-nav-item.active {
    color: $--color-b400;
  }
}

.close-box {
  .close-item {
    padding: 8px;
    line-height: 25px;
    cursor: pointer;
  }

  .close-item:hover {
    background-color: $--background-color-base;
  }
}

.quick-close {
  width: 160px;

  .el-dropdown-menu__item {
    line-height: 41px;
  }
}
</style>
