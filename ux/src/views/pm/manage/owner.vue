<template>
  <div class="pm-project-manage">
    <wk-page-header
      title="我管理的">
      <template slot="right">
        <el-button
          type="primary"
          @click="createVisible = true">新建项目</el-button>
      </template>
    </wk-page-header>

    <project-table
      ref="table"
      type="owner"
      class="table-container" />

    <create-project-dialog
      :visible.sync="createVisible"
      @save-success="handleRefresh"
      @close="createVisible = false" />
  </div>
</template>

<script>
import WkPageHeader from '@/components/Page/WkPageHeader'
import ProjectTable from './components/ProjectTable'
import CreateProjectDialog from './components/CreateProjectDialog'

export default {
  name: 'ManageOwnerIndex', // 我管理的项目页面
  components: {
    WkPageHeader,
    ProjectTable,
    CreateProjectDialog
  },
  data() {
    return {
      createVisible: false
    }
  },
  methods: {
    /**
     * @description: 新建项目
     * @return {*}
     */
    handleRefresh() {
      this.$refs.table.refresh()
    }
  }
}
</script>

<style scoped lang="scss">
.pm-project-manage {
  width: 100%;

  .wk-page-header {
    padding: 24px 40px 0;
  }

  .table-container {
    padding: 0 40px 16px;
  }
}
</style>
