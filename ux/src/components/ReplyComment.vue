<template>
  <div
    v-loading="commentLoading"
    class="reply-comment">
    <template v-if="!showNoFocus">
      <el-input
        v-model="commentsTextarea"
        :rows="3"
        :maxlength="2000"
        show-word-limit
        type="textarea"
        placeholder="请输入评论内容"
        @blur="blurFun" />
      <div class="btn-group">
        <!-- <el-popover
          v-model="showEmoji"
          placement="top"
          width="400"
          trigger="click">
          // 表情
          <i
            slot="reference"
            class="wk wk-expression smiling-img" />
        </el-popover> -->
        <div class="btn-box">
          <el-button
            type="primary"
            @click="commentSubmit">评论</el-button>
          <el-button @click="closeComment">取消</el-button>
        </div>
      </div>
    </template>

    <div
      v-else
      class="no-focus"
      @click="toggleFocus()">
      请输入评论内容
    </div>
  </div>
</template>

<script>
/**
 * 评论输入框
 * event:      close 关闭输入框
 *             reply 确认输入  参数： {String} 输入框值
 *             toggle 状态切换
 * public fn:  toggleFocus 切换输入框状态
 */
import xss from 'xss'

export default {
  name: 'ReplyComment',
  components: {
  },
  data() {
    return {
      commentLoading: false, // 评论loading
      blurIndex: 0, // 评论表情插入位置
      commentsTextarea: '', // 评论内容
      showEmoji: false, // emoji选择标志
      showNoFocus: false
    }
  },
  methods: {
    /**
     * 输入框失去焦点
     */
    blurFun(eve) {
      this.blurIndex = eve.target.selectionEnd
    },
    /**
     * emoji 表情选择
     */
    // selectEmoji(val) {
    //   if (this.commentsTextarea && this.commentsTextarea.length + val.length <= 2000 || !this.commentsTextarea) {
    //     const list = this.commentsTextarea.split('')
    //     list.splice(this.blurIndex, 0, val)
    //     this.commentsTextarea = list.join('')
    //     this.showEmoji = false
    //   }
    // },
    /**
     * 提交评论评论
     */
    commentSubmit() {
      if (!this.commentsTextarea) {
        this.$message.error('评论内容不能为空')
        return
      }
      this.$emit('reply', xss(this.commentsTextarea))
    },
    /**
     * 关闭评论评论框
     */
    closeComment() {
      // this.showEmoji = false
      this.$emit('close')
      this.toggleFocus()
    },

    /**
     * 切换输入框状态
     */
    toggleFocus(flag) {
      if (typeof flag === 'boolean') {
        this.showNoFocus = flag
      } else {
        this.showNoFocus = !this.showNoFocus
      }
      this.$emit('toggle', this.showNoFocus)
    }
  }
}
</script>

<style scoped lang="scss">
  .reply-comment {
    overflow: hidden;
    border: 1px solid $--border-color-base;
    border-radius: $--border-radius-base;

    .btn-group {
      display: flex;
      align-items: center;
      justify-content: flex-start;
      padding: 5px 10px;
      overflow: hidden;
      background-color: white;

      .btn-box {
        display: flex;
        flex: 1;
        align-items: center;
        justify-content: flex-end;
      }

      // .smiling-img {
      //   cursor: pointer;
      //   font-size: 17px;
      //   color: $--color-text-regular;
      // }
    }

    .el-textarea /deep/ .el-textarea__inner {
      resize: none;
      background-color: white;
      border: 0;
    }

    .no-focus {
      width: 100%;
      padding: 9px 8px;
      font-size: 13px;
      color: #c0c4cc;
      cursor: pointer;
      background-color: white;
      border-radius: $--border-radius-base;
    }
  }
</style>
