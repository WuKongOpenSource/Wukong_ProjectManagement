<template>
  <div>
    <div
      v-empty="!indexAuth"
      xs-empty-icon="nopermission"
      xs-empty-text="暂无权限"
      class="pm-container">

      <list-view
        ref="All"
        :item-type="itemType"
        :project-detail="projectDetail" />

    </div>
  </div>
</template>

<script>
import ListView from '@/views/pm/project/team/components/list'

export default {
  name: 'All',
  components: {
    ListView
  },
  props: {
    projectDetail: Object
  },
  data() {
    return {
      itemType: 'All'
    }
  },
  computed: {
    indexAuth() {
      return true
    }
  },
  watch: {},
  created() {

  },
  mounted() {

  },
  methods: {
    refresh(isClearFilter) {
      this.$refs[this.itemType].changeViewList(isClearFilter)
    }
  }
}
</script>
<style scoped lang='scss'>
.pm-container {
  padding: 0;
  padding-bottom: 16px;
}

.wk-table-header {
  margin-top: 0;
}
</style>
