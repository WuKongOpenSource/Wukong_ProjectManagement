<template>
  <div class="pm-project">
    <wk-page-header
      :title="'项目公告' + `${listData.length > 0 ? `（${listData.length}）` : ''}`">
      <template slot="right">
        <el-button
          v-if="$auth('projectAnnouncement.editAnnouncement', 'PM')"
          type="primary"
          @click="createNotice">新建公告</el-button>
      </template>
    </wk-page-header>

    <div v-loading="loading" class="container">
      <div
        v-if="!listData.length"
        v-empty="listData.length === 0" />
      <div class="notice-list">
        <div
          v-for="(item, index) in listData"
          :key="item.projectAnnouncementId"
          class="notice-list-item">
          <flexbox class="user-info">
            <xr-avatar
              :id="item.createUserId"
              :src="item.createUserImg"
              :name="item.createUser"
              :size="32" />
            <flexbox-item>
              <div class="username">{{ item.createUser }}</div>
              <div class="time">{{ item.createTime }}</div>
            </flexbox-item>
          </flexbox>
          <flexbox
            align="flex-start"
            justify="flex-start"
            class="content">
            <flexbox-item>
              {{ item.content }}
            </flexbox-item>
            <div class="control">
              <el-button
                v-if="$auth('projectAnnouncement.editAnnouncement', 'PM')"
                type="primary"
                size="medium"
                @click="handleUpdate(item)">编辑</el-button>
              <el-button
                v-if="$auth('projectAnnouncement.deleteAnnouncement', 'PM')"
                type="danger"
                size="medium"
                class="del-btn"
                @click="handleDelete(item, index)">删除</el-button>
            </div>
          </flexbox>
        </div>
      </div>
    </div>
    <create-notice-dialog
      v-if="createVisible"
      :visible="createVisible"
      :action="action"
      :project-id="projectId"
      @onSubmit="getList"
      @close="createVisible = false" />
  </div>
</template>

<script>
import {
  getProjectAnnouncementAPI,
  delProjectAnnouncementAPI
} from '@/api/pm/notice'

import WkPageHeader from '@/components/Page/WkPageHeader'
import createNoticeDialog from './components/CreateNoticeDialog'

import { mapGetters } from 'vuex'

export default {
  name: 'ProjectNoticeIndex', // 项目公告
  components: {
    WkPageHeader,
    createNoticeDialog
  },
  data() {
    return {
      loading: false,
      listData: [],
      action: {
        type: '',
        data: null
      },
      createVisible: false
    }
  },
  computed: {
    projectId() {
      return this.$route.params.id
    },
    ...mapGetters(['projectAuth'])
  },
  watch: {
    projectAuth: {
      handler(val) {
        if (val.loaded) {
          if (!this.$auth('projectAnnouncement.viewAnnouncement', 'PM')) {
            this.$router.replace({
              name: 'participate'
            })
          }
        }
      },
      immediate: true,
      deep: true
    }
  },
  created() {
    this.getList()
  },
  methods: {
    /**
     * @description: 获取公告列表
     * @return {*}
     */
    getList() {
      const params = {
        projectId: this.projectId
      }
      this.loading = true
      getProjectAnnouncementAPI(params)
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
      this.action.type = 'create'
      this.createVisible = true
    },

    /**
     * @description: 编辑公告
     * @param {*} item
     * @return {*}
     */
    handleUpdate(item = null) {
      this.action.data = item
      this.action.type = 'edit'
      this.createVisible = true
    },

    /**
     * @description: 删除公告
     * @param {*} item
     * @param {*} index
     * @return {*}
     */
    handleDelete(item, index) {
      this.$confirm(
        '您确定要删除这条数据吗？',
        '提示',
        {
          type: 'warning',
          confirmButtonText: '确定',
          cancelButtonText: '取消'
        }
      )
        .then(() => {
          this.loading = true
          console.log('delete item: ', item)
          const params = {
            projectAnnouncementId: []
          }
          params.projectAnnouncementId.push(item.projectAnnouncementId)
          delProjectAnnouncementAPI(params)
            .then(res => {
              this.$message.success('删除成功')
              this.getList()
              this.loading = false
            })
            .catch(() => {
              this.$message.error('删除失败')
              this.loading = false
            })
        })
        .catch(() => {})
    }
  }
}
</script>

<style scoped lang="scss">
.pm-project {
  width: 100%;
  height: 100%;
  padding: 0 40px;

  .wk-page-header {
    padding-top: 24px;
  }

  /deep/ .container {
    height: calc(100% - 57px);
    padding: 20px 0;
    overflow-y: auto;

    .xs-empty-parent--relative {
      height: 100%;

      .empty-content {
        transform: translateY(-50%);
      }
    }

    .notice-list {
      .notice-list-item {
        padding: 16px;
        border-top: $--border-base;

        .username,
        .time {
          font-size: $--font-size-small;
          color: $--color-n500;
        }

        .username {
          margin-bottom: 5px;
        }

        .content {
          padding-left: 40px;
          margin-top: 10px;
        }

        .control {
          margin-left: 15px;

          .del-btn {
            color: $--color-text-regular;
            background-color: $--slider-button-default-border-color;
          }

          .el-button {
            visibility: hidden;
          }
        }

        &:last-child {
          border-bottom: $--border-base;
        }

        &:hover {
          cursor: pointer;
          background-color: $--color-n20;

          .el-button {
            visibility: visible;
          }
        }
      }
    }
  }
}
</style>
