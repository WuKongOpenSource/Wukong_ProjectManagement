<template>
  <project-card
    v-loading="loading">
    <template slot="title-left">项目公告</template>
    <el-button
      v-if="editNoticeAuth"
      slot="title-right"
      type="primary-text"
      icon="el-icon-plus"
      style="font-size: 14px;"
      @click="createNotice">
      新建公告
    </el-button>
    <div
      v-empty="!listData.length"
      class="content rc-cont">
      <div
        v-if="listData.length"
        class="content_body">
        <div class="body_top">
          <div class="header">
            <xr-avatar
              :id="listData[0].createUserId"
              :src="listData[0].createUserImg"
              :name="listData[0].createUser"
              :size="32" />
            <div class="name">
              <h2>{{ listData[0].createUser }}</h2>
              <p>发布与{{ listData[0].createTime }}</p>
            </div>
          </div>
          <div v-if="!editInputShow" class="body-bottom" @click="editNoticeAuth ? editNotice('show') : ''">
            {{ listData[0].content }}
          </div>
          <el-input
            v-else-if="editInputShow"
            ref="edit"
            v-model="inputEditText"
            class="body-bottom"
            type="textarea"
            :rows="8"
            @blur="editNotice('hidden')" />
        </div>
        <el-button
          v-if="$auth('projectAnnouncement', 'PM')"
          type="primary-text"
          style="font-size: 14px;"
          @click="checkNotice">
          查看全部公告
        </el-button>
      </div>
    </div>
    <create-notice-dialog
      v-if="createVisible"
      :visible="createVisible"
      :project-id="projectId"
      @onSubmit="getData"
      @close="createVisible = false" />
  </project-card>
</template>

<script>
import {
  getProjectAnnouncementAPI,
  setProjectAnnouncementAPI
} from '@/api/pm/notice'

import createNoticeDialog from '@/views/pm/project/components/CreateNoticeDialog'

import ProjectMixin from './ProjectMixin'
import { objDeepCopy } from '@/utils'

export default {
  name: 'Announcement', // 项目概况-项目公告组件
  components: {
    createNoticeDialog
  },
  mixins: [ProjectMixin],
  props: {},
  data() {
    return {
      loading: false, // 加载
      list: false,
      listData: [],
      createVisible: false,
      editInputShow: false,
      inputEditText: ''
    }
  },
  computed: {
    projectId() {
      return this.$route.params.id
    },
    editNoticeAuth() {
      return this.$auth('projectAnnouncement.editAnnouncement', 'PM')
    }
  },
  watch: {
    editInputShow: {
      handler(val) {
        if (val) {
          this.$nextTick(() => {
            this.inputEditText = objDeepCopy(this.listData[0].content) || ''
            this.$refs.edit.focus()
          })
        }
      }
    }
  },
  created() {
    this.getData()
  },
  methods: {
    /**
     * @description: 获取公告数据
     * @return {*}
     */
    getData() {
      this.loading = true
      getProjectAnnouncementAPI({ projectId: this.projectId })
        .then(res => {
          this.listData = res.data
          this.loading = false
        })
        .catch(() => {
          this.loading = false
        })
    },

    /**
     * @description: 新建公告
     * @return {*}
     */
    createNotice() {
      this.createVisible = true
    },

    /**
     * @description: 编辑公告
     * @return {*}
     */
    editNotice(val) {
      if (val == 'show') {
        this.editInputShow = true
      } else {
        this.loading = true
        setProjectAnnouncementAPI({
          projectId: this.listData[0].projectId,
          projectAnnouncementId: this.listData[0].projectAnnouncementId,
          content: this.inputEditText
        })
          .then(res => {
            this.$message.success('操作成功')
            this.editInputShow = false
            this.loading = false
            this.getData()
          })
          .catch(() => {
            this.editInputShow = false
            this.loading = false
          })
      }
    },

    /**
     * @description: 查看全部公告
     * @return {*}
     */
    checkNotice() {
      this.$router.push({
        name: 'projectNotice',
        params: {
          id: this.projectId
        }
      })
    }
  }
}
</script>
<style scoped lang='scss'>
.content {
  position: relative;
  width: 100%;
  height: 330px;
  margin-top: 16px;

  /deep/ .empty-mask {
    .empty-content {
      top: 25%;
    }
  }

  .body_top {
    height: 295px;
    padding: 16px;
    margin-bottom: 8px;
    background-color: $--color-n20;
    border-radius: $--border-radius-base;

    .header {
      display: flex;
      align-items: center;
    }

    .name {
      margin-left: 16px;

      h2 {
        margin-bottom: 8px;
        font-size: 16px;
        font-weight: bold;
        color: $--color-black;
      }

      p {
        font-size: 12px;
        color: $--color-text-secondary;
      }
    }
  }

  .body-bottom {
    height: 205px;
    margin-top: 16px;
    overflow: auto;
    font-weight: bold;
    color: $--color-text-regular;
    text-align: justify;
  }

  .title {
    margin-bottom: 8px;
    font-size: 14px;
    font-weight: bold;
    text-align: center;
  }
}
</style>
