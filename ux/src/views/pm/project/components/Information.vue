<template>
  <project-card
    v-loading="loading">
    <template slot="title-left">项目基本信息</template>
    <div class="content">
      <div v-if="detail" class="content-body">
        <div v-for="(item, index) in info" :key="index" class="body-item">
          <h2 class="title">{{ item.title }}</h2>
          <p v-if="item.type == 'text'" class="item-content">{{ detail[item.name] }}</p>
          <p v-if="item.type == 'time'" class="item-content">{{ detail.startTime|filterTimestampToFormatTime('YYYY-MM-DD') }}<span>{{ detail.startTime && detail.stopTime ? '至' : '--' }}</span>{{ detail.stopTime|filterTimestampToFormatTime('YYYY-MM-DD') }}</p>
          <status-tag v-if="item.type=='tag'" :type="detail.status" />
          <div v-if="item.type == 'theProgress'">
            <flexbox>
              <flexbox-item style="display: flex; align-items: center;">
                <el-progress
                  :percentage="Math.round(detail.theProgress) || 0"
                  :show-text="false"
                  style="width: 80%;" />
                <span class="text" style="padding-left: 8px;">{{ Math.round(detail.theProgress) || 0 }}%</span>
              </flexbox-item>
            </flexbox>
          </div>
          <flexbox
            v-if="item.type == 'adminAvatar'"
            align="center"
            justify="flex-start">
            <!-- <xr-avatar
              v-for="user in adminList"
              :id="user.userId"
              :key="user.userId"
              :src="user.img"
              :name="user.realname"
              :size="20" /> -->
            <div class="member">
              <span v-for="(user, i) in adminList" :key="i" style="flex-shrink: 0;">
                {{ user.realname }}
                <i v-if="i < adminList.length - 1">，</i>
              </span>
            </div>
          </flexbox>
          <flexbox
            v-if="item.type == 'avatar'"
            align="center"
            justify="flex-start">
            <!-- <xr-avatar
              v-for="user in detail.projectOwnerRoleList"
              :id="user.userId"
              :key="user.userId"
              :src="user.img"
              :name="user.realname"
              :size="20" /> -->
            <div class="member">
              <span v-for="(user, i) in detail.projectOwnerRoleList" :key="i" style="flex-shrink: 0;">
                {{ user.realname }}
                <i v-if="i < detail.projectOwnerRoleList.length - 1">，</i>
              </span>
            </div>
          </flexbox>
        </div>
      </div>
    </div>
  </project-card>
</template>

<script>
import StatusTag from '@/views/pm/project/components/StatusTag'

import ProjectMixin from './ProjectMixin'

export default {
  name: 'Information', // 项目概况-项目基本信息组件
  components: {
    StatusTag
  },
  mixins: [ProjectMixin],
  props: {
    detail: Object
  },
  data() {
    return {
      loading: false, // 加载
      info: [
        { title: '项目名称', name: 'name', type: 'text' },
        // { title: '项目状态', name: '进行中', type: 'tag' },
        { title: '项目周期', name: '2022-08-15至2022-09-15', type: 'time' },
        { title: '管理员', name: '', type: 'adminAvatar' },
        { title: '项目进度', name: '这是一个要升级的项目', type: 'theProgress' },
        { title: '项目成员', name: '', type: 'avatar' },
        { title: '项目描述', name: 'description', type: 'text' }
      ],
      adminList: []
    }
  },
  computed: {},
  created() {
    this.getAdminList()
  },
  methods: {
    /**
     * @description: 取项目内管理员列表
     * @return {*}
     */
    getAdminList() {
      const list = []
      this.detail.projectOwnerRoleList.forEach(item => {
        item.adminRoles.forEach(val => {
          if (val.label == 1) list.push(item)
        })
      })
      this.adminList = list
    }
  }
}
</script>
<style scoped lang='scss'>
.content {
  position: relative;
  width: 100%;
  margin-top: 16px;

  .content-body {
    display: flex;
    flex-flow: wrap;

    .body-item {
      width: 50%;
      margin-bottom: 24px;

      .title {
        margin-bottom: 8px;
        font-size: 14px;
        font-weight: bold;
        color: $--color-text-secondary;
      }

      .item-content {
        font-size: 14px;
        font-weight: bold;
      }

      &:last-child {
        width: 100%;
      }

      .member {
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
      }
    }
  }
}
</style>
