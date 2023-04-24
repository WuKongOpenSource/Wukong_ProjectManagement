<template>
  <div v-loading="loading" class="container">
    <top-border-card
      :move="false">
      <template slot="title-left">
        快捷入口
      </template>
      <div class="main">
        <flexbox
          v-for="(item,index) in list"
          :key="index"
          class="category-item vux-flex-col"
          @click.native="quickEntry(item.url)">
          <div class="category-icon"><i :class="item.icon" /></div>
          <div class="category-label ">{{ item.label }}</div>
        </flexbox>
      </div>
    </top-border-card>
  </div>
</template>

<script>
import TopBorderCard from './TopBorderCard'
export default {
  name: 'QuickEntryCard', // 快捷入口卡片
  components: {
    TopBorderCard
  },
  data() {
    return {
      loading: false,
      list: [
        { label: '归档', icon: 'wk wk-icon-draft2-b', url: '/oa/examine/subs/statistics/archive' },
        { label: '草稿', icon: 'wk wk-icon-archive-b', url: '/oa/examine/subs/statistics/draft' }
      ]
    }
  },
  created() {
  },
  methods: {
    quickEntry(url) {
      const { module } = this.$route.query
      this.$router.push({
        path: url,
        query: {
          module
        }
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.main {
  display: flex;
  margin-top: 16px;

  .category-item {
    flex: 0;
    padding: 10px;
    margin: 5px;
    text-align: center;
    cursor: pointer;

    // background-color: #f6f6f6;
    border-radius: $--border-radius-base;

    .category-icon {
      width: 40px;
      height: 40px;
      line-height: 40px;
      background-color: #deebff;
      border-radius: $--border-radius-base;

      .wk {
        font-size: 24px;
        color: #0052cc;
      }
    }

    .category-label {
      width: 40px;
      margin-top: 5px;
      overflow: hidden;
      font-size: 14px;
      text-overflow: ellipsis;
      white-space: nowrap;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
    }
  }
}
</style>
