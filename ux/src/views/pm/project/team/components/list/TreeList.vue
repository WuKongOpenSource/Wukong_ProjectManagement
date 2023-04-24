<template>
  <div class="tree-list">
    树状列表
  </div>
</template>

<script>
export default {
  name: 'TreeList',
  props: {
    itemType: String,
    list: {
      type: Array,
      default() {
        return []
      }
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
<style lang='scss' scoped>

</style>
