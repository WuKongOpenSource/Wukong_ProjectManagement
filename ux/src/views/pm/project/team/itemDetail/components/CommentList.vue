<template>
  <div class="comment-list">
    <div
      v-for="(item, index) in list"
      :key="index"
      class="comment-list-item">
      <div class="parent-reply reply">
        <div class="user-info">
          <xr-avatar
            :id="item.user.userId"
            :name="item.user.realname"
            :size="32"
            :src="item.user.img"
            :disabled="false"
            class="user-img" />
          <div class="user-info__bd">
            <span class="user">{{ item.user.realname }}</span>
            <span class="time">
              {{ item.createTime | formatTime }}
            </span>
          </div>
        </div>
        <div class="content">{{ item.content }}</div>
        <div class="control">
          <el-button
            class="reply-btn"
            type="text"
            @click="handleToReply(index)">
            <i class="wk wk-icon-message-line" />
            回复
          </el-button>
          <el-button
            class="delete-btn"
            type="text"
            @click="handleToDelete(item, index)">
            <i class="wk wk-icon-bin" />
            删除
          </el-button>
        </div>
        <reply-comment
          v-if="replyIndex === String(index)"
          @reply="handleReply($event, item)"
          @close="replyIndex = null" />
      </div>
      <template v-if="item.childCommentList && item.childCommentList.length > 0">
        <div
          v-for="(child, childIndex) in item.childCommentList"
          :key="childIndex"
          class="child-reply-list">
          <div class="child-reply reply">
            <div class="user-info">
              <xr-avatar
                :id="child.user.userId"
                :name="child.user.realname"
                :size="34"
                :src="child.user.img"
                :disabled="false"
                class="user-img" />
              <div class="user-info__bd">
                <span>
                  {{ child.user.realname }}
                </span>
                <span class="time">
                  {{ child.createTime }}
                </span>
              </div>
            </div>
            <div class="child-content">
              <span>
                回复 @{{ child.replyUser.realname }}：
              </span>
              <span class="content">{{ child.content }}</span>
            </div>
            <div class="control">
              <el-button
                class="reply-btn"
                type="text"
                @click="handleToReply(index, childIndex)">
                <i class="wk wk-icon-message-line" />
                回复
              </el-button>
              <el-button
                class="delete-btn"
                type="text"
                @click="handleToDelete(child, index, childIndex)">
                <i class="wk wk-icon-bin" />
                删除
              </el-button>
            </div>
            <reply-comment
              v-if="replyIndex === `${index}-${childIndex}`"
              v-loading="commentLoading"
              @reply="handleReply($event, child)"
              @close="replyIndex = null" />
          </div>
        </div>
      </template>
    </div>
  </div>
</template>

<script>
import ReplyComment from '@/components/ReplyComment' // 评论列表

import xss from 'xss'
import merge from '@/utils/merge'
import { formatTime } from '@/utils'
import { mapGetters } from 'vuex'

const DefaultCommentList = {
  addRequest: null, // 添加请求和参数
  addParams: null,
  replyKey: 'pid', // 日志 任务 pid  阶段 replyId
  replyValueKey: 'userId', // 获取值的keys 日志 任务 userId  阶段 user.userId
  deleteRequest: null, // 删除请求和参数
  deleteParams: null
}

export default {
  name: 'CommentList',
  components: {
    ReplyComment
  },
  filters: {
    formatTime(time) {
      return formatTime(time)
    }
  },
  props: {
    // 取值字段 如果继续新增 改为 props 方案
    props: {
      type: Object,
      default: () => {
        return {

        }
      }
    },
    list: {
      type: Array,
      required: true
    },
    showControl: { // 是否展示操作按钮
      type: Boolean,
      default: true
    }
  },
  data() {
    return {
      replyIndex: null,
      commentLoading: false
    }
  },
  computed: {
    ...mapGetters([
      'userInfo'
    ]),

    // 合并 props
    config() {
      return merge({ ...DefaultCommentList }, this.props || {})
    }
  },
  methods: {
    /**
     * 显示回复框
     * @param index
     * @param childIndex
     */
    handleToReply(index, childIndex = null) {
      const str = `${index}${childIndex !== null ? ('-' + childIndex) : ''}`
      this.$emit('close-other-reply')
      if (str === this.replyIndex) return
      this.replyIndex = str
    },

    closeReply() {
      this.replyIndex = null
    },

    /**
     * 子评论排序
     */
    // sortChildComment(childList) {
    //   let arr = [].concat(childList || [])
    //   arr = arr.sort((a, b) => {
    //     return new Date(b.createTime) - new Date(a.createTime)
    //   }) || []
    //   return arr
    // },

    /**
     * 回复
     * @param data
     */
    handleReply(data, item) {
      const params = {
        content: xss(data)
      }

      let c_comment = null
      const arr = this.replyIndex.split('-')
      const f_comment = this.list[arr[0]]
      if (arr[1]) {
        c_comment = f_comment.childCommentList[arr[1]]
      }
      if (f_comment) {
        params.mainId = f_comment.commentId
        params[this.config.replyKey] = this.getReplyValue(f_comment, this.config.replyValueKey)
      }
      if (c_comment) {
        params[this.config.replyKey] = this.getReplyValue(c_comment, this.config.replyValueKey)
      }

      this.config.addRequest({ ...params, ...(this.config.addParams || {}) }).then(res => {
        res.data.user = {
          userId: this.userInfo.userId,
          realname: this.userInfo.realname,
          img: this.userInfo.img
        }
        if (c_comment) {
          res.data.replyUser = c_comment.user
        } else {
          res.data.replyUser = f_comment.user
        }
        this.list[arr[0]].childCommentList.push(res.data)
        this.replyIndex = null
        this.commentLoading = false
      }).catch(() => {
        this.commentLoading = false
      })
    },

    /**
     * @description: 获取回复对象
     * @param {*}
     * @return {*}
     */
    getReplyValue(data, keyStr) {
      if (keyStr.indexOf('.') < 0) {
        return data[keyStr]
      }
      const keys = keyStr.split('.')
      let current = data
      for (let i = 0; i < keys.length; i++) {
        current = current[keys[i]]
        if (current === undefined) {
          return '' // 如果找不到 返回空
        }
      }
      return current
    },

    /**
     * @description: 删除回复
     * @param {*} data
     * @param {*} index
     * @param {*} childIndex
     * @return {*}
     */
    handleToDelete(data, index, childIndex = -1) {
      this.$confirm('确定删除?', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
        .then(() => {
          this.config.deleteRequest({
            commentId: data.commentId,
            ...(this.config.deleteParams || {})
          }).then(res => {
            this.$message({
              type: 'success',
              message: '删除成功!'
            })

            if (childIndex >= 0) {
              this.list[index].childCommentList.splice(childIndex, 1)
            } else {
              this.$emit('delete', index)
            }
          })
        })
        .catch(() => {
          this.$message({
            type: 'info',
            message: '已取消删除'
          })
        })
    }
  }
}
</script>

<style scoped lang="scss">
  .comment-list {
    .comment-list-item {
      color: $--color-text-regular;

      .reply {
        margin-top: $--interval-base;

        .user-info {
          display: flex;
          align-items: center;
          justify-content: flex-start;
          line-height: 1;

          &__bd {
            flex: 1;
            margin-left: $--interval-base;

            .user {
              color: $--color-text-primary;
            }

            .time {
              margin-left: $--interval-base;
              color: $--color-text-regular;
            }
          }
        }

        .content {
          margin-top: 5px;
          margin-left: 40px;
          line-height: 1.5;
          color: $--color-text-regular;
          word-wrap: break-word;
          white-space: pre-wrap;
        }

        .control {
          margin-left: 40px;
          line-height: 32px;

          i {
            font-size: 14px;
          }

          .delete-btn,
          .reply-btn {
            padding: 0;
            color: $--color-black;

            &:hover {
              color: $--color-primary;
            }
          }
        }

        .child-content {
          margin-left: 40px;

          .content {
            margin-left: 0;
          }
        }
      }

      .reply-comment {
        margin-top: 10px;
      }
    }

    .child-reply-list {
      padding-left: 40px;
    }
  }
</style>
