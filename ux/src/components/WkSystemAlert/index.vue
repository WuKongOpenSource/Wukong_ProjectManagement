<template>
  <el-alert
    :title="systemAlertProps.title"
    :type="systemAlertProps.type"
    :description="systemAlertProps.description"
    :closable="systemAlertProps.closable"
    :center="systemAlertProps.center"
    :close-text="systemAlertProps.closeText"
    :show-icon="systemAlertProps.showIcon"
    :effect="systemAlertProps.effect"
    class="wk-system-alert"
    @close="close" />
</template>

<script>
import { mapGetters } from 'vuex'
import Lockr from 'lockr'

export default {
  // 顶部系统消息
  name: 'WkSystemAlert',

  components: {},

  props: {},

  data() {
    return {
    }
  },

  computed: {
    ...mapGetters([
      'systemAlertProps'
    ])
  },

  watch: {},

  created() {},

  mounted() {},

  beforeDestroy() {},

  methods: {
    /**
     * 系统消息关闭
     */
    close() {
      Lockr.set('wkUpdateTips', Date.now().toString())
    }
  }
}
</script>

<style lang="scss" scoped>
.wk-system-alert {
  border-bottom: 1px solid #e6e6e6;
}
</style>
