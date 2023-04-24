<template>
  <div class="configuration">
    <div class="matter">
      <div class="title">事项类型</div>
      <el-table
        v-loading="loading"
        :data="list"
        :class="WKConfig.tableStyle.class"
        :stripe="WKConfig.tableStyle.stripe"
        class="main-table"
        highlight-current-row
        style="width: 100%;"
        @row-click="handleRowClick">
        <el-table-column
          prop="name"
          label="事项类型名称"
          width="200">
          <template slot-scope="{row}">
            <flexbox>
              <!-- <span class="move-icon">⋮⋮</span> -->
              <img class="item-pic" :src="getIconClass(row)">
              <span>{{ row.name }}</span>
            </flexbox>
          </template>
        </el-table-column>
        <el-table-column
          prop="describes"
          label="描述" />
        <el-table-column
          fixed="right"
          label="操作"
          width="200">
          <template slot-scope="{row}">
            <el-button
              :disabled="!$auth('set.synergyConfig', 'PM')"
              type="text"
              class="dropdown-btn"
              icon="el-icon-s-operation"
              @click="handleClick('a', row)" />
            <!-- <el-dropdown
              trigger="click"
              placement="bottom-start"
              @command="handleCommand(row)">
              <el-button
                type="text"
                class="dropdown-btn"
                icon="el-icon-more" />
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item
                  command="delete">移除</el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown> -->

          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script>
import {
  projectEventTypeListAPI
  // projectEventTypeDelAPI
} from '@/api/pm/setting'
import {
  projectQueryByIdAPI
} from '@/api/pm/manage'

import { getItemImg } from '@/views/pm/data'

export default {
  name: 'Configuration',
  components: {

  },
  props: {},
  data() {
    return {
      optionType: 'universal',
      list: [],
      loading: false,
      typelist: [],
      changeTypeShow: true
    }
  },
  computed: {
    projectId() {
      return this.$route.params.setting
    }
  },
  watch: {},
  created() {

  },
  mounted() {
    this.typelist = [{
      name: '敏捷开发项目',
      desc: '通过内置的敏捷研发管理组件，可以轻松实现迭代管控、需求分配、缺陷管理等核心研发工作，通过各类报表实时掌控项目进度状况。'
    }]
    this.getDetail()
  },
  methods: {
    /**
     * @description: 获取项目详情
     * @return {*}
     */
    getDetail() {
      this.loading = true
      projectQueryByIdAPI({ projectId: this.projectId })
        .then(res => {
          const resData = res.data || {}
          this.getList(resData.schemeId)
          this.loading = false
        }).catch(() => {
          this.loading = false
        })
    },

    /**
     * @description: 获取数据
     * @param {*} schemeId
     * @return {*}
     */
    getList(schemeId) {
      this.loading = true
      const params = {
        schemeId
      }
      projectEventTypeListAPI(params).then(res => {
        this.list = res.data || []
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },

    /**
     * @description: 当某一行被点击时会触发该事件
     * @param {*} row
     * @param {*} column
     * @param {*} event
     * @return {*}
     */
    handleRowClick(row, column, event) {},
    /**
     * @description: 操作
     * @param {*} type
     * @param {*} scope
     * @return {*}
     */
    handleClick(type, data) {
      if (type === 'a') {
        console.log('aaa')
        this.$emit('backup', data)
      } else if (type === 'b') {
        console.log('bbb')
      }
    },

    getIconClass(row) {
      const typeMap = [0, 2, 3, 4, 'sub'][row.type]
      const obj = {
        type: typeMap
      }
      return getItemImg(obj)
    },

    // handleCommand(data) {
    //   const params = {
    //     schemeId: this.schemeId
    //     // id:
    //   }
    //   projectEventTypeDelAPI(params).then(res => {

    //   }).catch(() => {

    //   })
    // },
    switchType(type) {
      this.optionType = type
      // 开启 doShow()
      // this.$refs.popover.doShow()
      // 关闭 doClose()
      this.$refs.popover.doClose()
    }
  }
}
</script>
<style scoped lang='scss'>
.configuration {
  .title {
    margin: 16px 0;
    font-size: $--font-size-large;
    font-weight: bold;
    color: $--color-black;
  }

  .el-table {
    .move-icon {
      font-weight: bold;
      cursor: move;
    }

    .item-pic {
      display: inline-block;
      width: 24px;
      height: 24px;
      margin-right: 6px;
      margin-left: 10px;
    }

    .text {
      margin-left: 10px;
    }

    .project-tag {
      height: 22px;
      padding: 0 8px;
      font-size: 13px;
      line-height: 18px;

      &.is-over {
        color: white;
        background-color: $--color-r400;
      }

      &.is-common {
        color: $--color-black;
        background-color: $--background-color-base;
      }
    }

    .xr-avatar {
      margin-right: 4px;
    }

    .icon-header {
      color: $--color-n50;
    }

    .focus-btn {
      color: $--color-n50;

      /deep/ i {
        font-weight: bold;
      }

      &.active {
        color: $--color-y300;

        /deep/ i {
          font-weight: normal;
        }
      }
    }
  }
}

.filter-popover {
  padding: 4px;

  .title {
    font-size: $--font-size-base;
    color: $--color-black;
  }

  .info-item {
    padding: 12px;
    margin-top: 8px;
    overflow: hidden;
    cursor: pointer;
    border: 2px solid transparent;
    border-radius: $--border-radius-base;

    .name {
      font-size: $--font-size-large;
      font-weight: bold;
      color: $--color-black;
    }

    .desc {
      margin-top: 8px;
      font-size: $--font-size-base;
      color: $--color-text-regular;
    }

    &:hover {
      border: 2px solid $--color-primary;
    }
  }

  .active {
    border: 2px solid $--color-primary;
  }
}
</style>
