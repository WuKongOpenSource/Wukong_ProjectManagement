<template>
  <div class="comment-list">
    <div
      v-for="(item, index) in type === 'activity' ? list : hoursList"
      :key="index"
      class="comment-list-item">
      <div class="parent-reply reply">
        <div class="user-info">
          <xr-avatar
            :id="item.userId"
            :name="item.realname"
            :size="32"
            :src="item.img"
            :disabled="false"
            class="user-img" />
          <div class="user-info__bd">
            <span class="user">{{ item.realname }}</span>
            <span class="time">
              {{ item.createTime }}
            </span>
          </div>
        </div>
        <div class="content">{{ item.content }}</div>
      </div>
    </div>
  </div>
</template>

<script>

export default {
  name: 'CommentList',
  components: {

  },
  filters: {

  },
  props: {
    list: {
      type: Array,
      required: true
    },
    hoursList: {
      type: Array,
      default: () => {
        return []
      }
    },
    type: {
      type: String,
      required: true
    }
  },
  data() {
    return {

    }
  },

  methods: {

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
      }
    }
  }
</style>
