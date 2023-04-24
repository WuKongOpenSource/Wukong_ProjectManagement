<!-- 缺陷 -->
<template>
  <div
    v-empty="!indexAuth"
    xs-empty-icon="nopermission"
    xs-empty-text="暂无权限"
    class="pm-container">

    <list-view
      ref="Defects"
      :project-detail="projectDetail"
      :item-type="itemType" />

  </div>
</template>

<script>
import ListView from '@/views/pm/project/team/components/list'

export default {
  name: 'Defects',
  components: {
    ListView
  },
  props: {
    projectDetail: Object
  },
  data() {
    return {
      itemType: 'Defects'
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
  height: 100%;
  padding: 0;
  padding-bottom: 16px;
}

.wk-table-header {
  margin-top: 0;
}
</style>
