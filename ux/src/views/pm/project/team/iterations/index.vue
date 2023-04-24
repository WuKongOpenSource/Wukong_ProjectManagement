<template>
  <div>
    <div
      v-empty="!indexAuth"
      xs-empty-icon="nopermission"
      xs-empty-text="暂无权限"
      class="pm-container">

      <list-view
        ref="Iterations"
        :item-type="itemType"
        :project-detail="projectDetail"
        @enter-detail="enterDetail" />

    </div>
  </div>
</template>

<script>
import ListView from '@/views/pm/project/team/components/list'

export default {
  name: 'Iterations',
  components: {
    ListView
  },
  props: {
    projectDetail: Object
  },
  data() {
    return {
      itemType: 'Iterations'
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
    },

    enterDetail(row) {
      this.$emit('change', { type: 'iteration-detail', row })
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
