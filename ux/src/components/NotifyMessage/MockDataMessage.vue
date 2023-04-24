<template>
  <div class="message-content">
    <span>悟空为您添加了一些模拟数据，让您更好的体验系统，体验中请勿添加真实数据。您需要点击</span>
    <span
      class="can-visit"
      @click="handleClick">模拟数据清除</span>
    <span>来初始化并使用您的系统</span>
  </div>
</template>

<script>
export default {
  name: 'MockDataMessage',
  methods: {
    handleClick() {
      this.$emit('click')
    }
  }
}
</script>
