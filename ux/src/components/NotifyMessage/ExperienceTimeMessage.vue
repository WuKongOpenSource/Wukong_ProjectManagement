<template>
  <div class="message-content">
    <div class="content">
      <span>{{ label }}试用期剩余天数</span>
      <span class="can-visit can-visit--bold">{{ day }}</span>
      <span>，到期后数据将为您保留1个月为避免数据丢失请及时购买</span>
    </div>
    <el-button
      v-if="userInfo && userInfo.isAdmin"
      type="primary"
      @click="handleClick()">
      <span v-if="goodsId ===1">立即购买</span>
      <span v-else>联系我们</span>
    </el-button>

    <!-- 电话提示 -->
    <contact-dialog
      title="联系悟空购买"
      append-to-body
      :visible.sync="phoneDialogVisible"
    />
  </div>
</template>

<script>
import ContactDialog from '@/components/ContactDialog'

export default {
  name: 'ExperienceTimeMessage',
  components: {
    ContactDialog
  },
  props: {
    label: String,
    day: {
      type: [String, Number],
      default: ''
    },
    userInfo: Object,
    goodsId: Number
  },
  data() {
    return {
      phoneDialogVisible: false
    }
  },
  methods: {
    handleClick() {
      this.$emit('pay', this.goodsId)
    }
  }
}
</script>

<style scoped lang="scss">
.message-content {
  .content {
    margin-bottom: 10px;
  }
}
</style>
