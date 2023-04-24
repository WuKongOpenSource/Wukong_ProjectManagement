<!-- Backlog -->
<template>

  <team-card
    v-loading="loading">
    <template slot="title-left">Backlog</template>
    <!-- <el-button slot="title-right" type="primary-text" icon="el-icon-plus">新建公告</el-button> -->
    <div class="content">
      <div v-for="(item, index) in list" :key="index" class="content_item">
        <i class="left_border" :style="'background-color:' + item.color" />
        <div class="item_right">
          <div class="item_content">
            <div class="content_left">
              <i class="el-icon-share" style="font-size: 16px;" />
              <h2 class="title">{{ item.title }}</h2>
            </div>
            <div class="tag">
              <xr-avatar
                class="tag_content"
                disabled
                :size="24" />
            </div>
          </div>
        </div>
      </div>
    </div>
  </team-card>
</template>

<script>

import TeamCard from './TeamCard'

export default {
  name: 'Responsible',
  components: {
    TeamCard
  },
  props: {},
  data() {
    return {
      loading: false, // 加载
      color: 'red',
      list: [
        { title: '09月份CRM升级内容', avatar: '', icon: '', color: '#ff5630' },
        { title: '2022商业汇演方案2', avatar: '', icon: '', color: '#36b37e' },
        { title: '这是一个迭代项目需求', avatar: '', icon: '', color: '#dfe1e6' },
        { title: '09月份CRM升级内容', avatar: '', icon: '', color: '#ffab00' },
        { title: '这是一个迭代项目需求', avatar: '', icon: '', color: '#dfe1e6' },
        { title: '2022商业汇演方案2', avatar: '', icon: '', color: '#36b37e' }
      ]
    }
  },
  computed: {},
  watch: {},
  created() {

  },
  mounted() {

  },
  methods: {

  }
}
</script>
<style scoped lang='less'>
.content {
    position: relative;
    width: 100%;
    height: 330px;
    margin-top: 10px;
    overflow: hidden;
    overflow-y:auto;
    .content_item {
        position: relative;
        height: 50px;
        border: 1px solid #e5e6ea;
        background-color: #fff;
        .left_border {
        position: absolute;
        top: 0;
        left: 0;
        height: 50px;
        width: 4px;
        transform: translate(-1px, -1px);
        // background-color: red;
        }
        .item_right {
        height: 100%;
        padding: 15px 15px 0 19px;
        .item_content {
            display: flex;
            justify-content: space-between;
            align-items: center;
            .content_left {
            display: flex;
            align-items: center;
            .title {
                margin-left:15px;
                font-size: 16px;
                font-weight: bold;
            }
            }
            .tag {
                display: flex;
                align-items: center;
                .tag_content {
                    margin-left: 10px;
                }
            }
        }
        }
    }
    &>.content_item:not(:last-child) {
        border-bottom: none;
    }
}
</style>
