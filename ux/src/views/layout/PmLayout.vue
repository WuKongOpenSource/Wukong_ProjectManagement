<template>
  <el-container>
    <el-header class="nav-container">
      <navbar
        v-if="menus && menus.length > 0"
        :menus="menus"
        title="PM"
        @select="menuSelect"
        @change="menuChange" />
    </el-header>

    <el-container>
      <wk-side-menu
        v-if="authSideMenus.length"
        :menus="authSideMenus">
        <div v-if="showHeader" slot="header">
          <flexbox class="header-cell">
            <i
              v-if="!!showSetting.setting"
              class="wk wk-icon-circle-left back-main"
              @click="backToProject" />
            <flexbox
              v-else-if="!!showSetting.id"
              justify="center"
              class="header-cell__hd"
              :class="{'project-icon': projectIcon}">
              <i
                :class="[projectIcon || 'wk wk-log' ]"
                :style="{color: projectIcon ? '#0052cc' : '' }" />
            </flexbox>
            <flexbox v-else justify="center" class="header-cell__hd">
              <i class="wk wk-log" />
            </flexbox>

            <div class="header-cell__bd">
              <template v-if="!!showSetting.id">
                <div class="header-cell--label">
                  <toggle-project
                    v-if="currentProjectDetail"
                    :detail="currentProjectDetail"
                  />
                </div>
              </template>
              <template v-else-if="!!showSetting.setting">
                <div class="header-cell--label">
                  项目设置
                </div>
              </template>
              <template v-else>
                <div class="header-cell--label">{{ headerCellObj.label }}</div>
                <div class="header-cell--des">{{ headerCellObj.des }}</div>
              </template>
            </div>
          </flexbox>
        </div>

        <div
          v-if="showSetBtn"
          slot="footer"
          class="setting"
          @click="goSetting">
          <i class="wk wk-manage" />
          <span style="margin-left: 10px;">项目设置</span>
        </div>
      </wk-side-menu>
      <el-main id="project-container" style="padding: 0;">
        <app-main :key="currentProjectId" />
      </el-main>
    </el-container>
  </el-container>
</template>

<script>
import { projectQueryByIdAPI } from '@/api/pm/manage'

import { mapGetters } from 'vuex'
import { Navbar, AppMain } from './components'
import WkSideMenu from './components//Sidebar/Menu'
import ToggleProject from '@/views/pm/project/components/ToggleProject'

import { getNavMenus } from './components/utils'
import path from 'path'

export default {
  name: 'PmLayout',
  components: {
    Navbar,
    AppMain,
    WkSideMenu,
    ToggleProject
  },
  data() {
    return {
      isCreate: false,
      projectBoardShow: false,
      tagBoardShow: false,

      sideMenus: [],
      showHeader: false,
      headerCellObj: {},

      projectDetail: null,
      currentProjectDetail: null
    }
  },
  computed: {
    ...mapGetters(['project', 'projectRouters', 'projectAuth']),

    menus() {
      return getNavMenus(this.projectRouters || [], '/project')
    },

    permissonProject() {
      return (
        this.project &&
        this.project.projectManage &&
        this.project.projectManage.save
      )
    },

    projectIcon() {
      return this.currentProjectDetail?.icon
    },

    // 是否显示设置按钮
    showSetting() {
      return this.$route.params || {}
    },

    currentProjectId() {
      return this.showSetting.id || this.showSetting.setting
    },

    // 是否展示项目设置按钮
    showSetBtn() {
      return !!this.showSetting.id && this.$auth('set', 'PM')
    },

    // 项目内菜单权限
    authSideMenus() {
      return this.sideMenus.filter(item => {
        if (item.meta.pmAuth) {
          return this.$auth(item.meta.pmAuth, 'PM')
        } else {
          return true
        }
      })
    }
  },
  watch: {
    '$route.params.id': {
      handler(val) {
        if (val) {
          this.$store.commit('SET_PM_AUTH', {})
          this.getProjectDetail(val)
          this.getProjectAuth(val)
        }
      },
      immediate: true
    },
    '$route.params.setting': {
      handler(val) {
        if (val) {
          this.getProjectAuth(val)
        }
      },
      immediate: true
    }
  },

  methods: {
    /**
     * @description: 项目详情
     * @param {*} val
     * @return {*}
     */
    getProjectDetail(val) {
      projectQueryByIdAPI({ projectId: val }).then(res => {
        this.currentProjectDetail = res.data
        this.$store.commit('SET_PROJECT_DETAIL', res.data || {})
      }).catch(() => {
      })
    },

    /**
     * @description: 权限接口
     * @param {*} val
     * @return {*}
     */
    getProjectAuth(val) {
      const { setting } = this.$route.params
      const params = 'id' in this.$route.params ? this.$route.params : { id: setting }
      this.$store.dispatch('GetPeojectAuth', params)
    },

    /**
     * 菜单选择
     */
    menuSelect(menu) {
      const router = this.projectRouters[menu.index]
      if (router && router.children && router.children.length > 1) {
        const sideMenus = this.getSideMenus(router.path, router.children)
        if (sideMenus.length) {
          const { id: projectId, setting } = this.$route.params || {}
          if (projectId) {
            this.sideMenus = sideMenus.filter(o => !!o.meta.isDetail).map(item => {
              item.path = item.path.replace(':id', projectId)
              return item
            })
          } else if (setting) {
            this.sideMenus = sideMenus.filter(o => !!o.meta.isSetting).map(item => {
              item.path = item.path.replace(':setting', setting)
              return item
            })
          } else {
            this.sideMenus = sideMenus.filter(o => !o.meta.isDetail && !o.meta.isSetting)
          }
          this.setHeaderCell()
        }
      } else {
        this.sideMenus = []
        this.headerCellObj = {}
      }
    },

    /**
     * 获取siderMenus
     */
    getSideMenus(mainPath, children) {
      const sideMenus = []
      children.forEach(item => {
        let auth = true
        if (item.permissions) {
          auth = this.$auth(item.permissions.join('.'))
        }
        if (!item.hidden && auth) {
          sideMenus.push({
            ...item,
            path: path.resolve(mainPath, item.path)
          })
        }
      })
      return sideMenus
    },

    setHeaderCell() {
      const { first, isDetail, isSetting } = this.$route.meta || {}
      if (first || isDetail || isSetting) {
        if (first) {
          this.headerCellObj = {
            icon: 'wk wk-log',
            index: 'visit',
            label: '项目',
            des: '项目管理'
          }
        } else {
          this.headerCellObj = {}
        }
        this.showHeader = true
        return
      } else {
        this.showHeader = false
        this.headerCellObj = {}
      }
    },

    menuChange(menu) {
      // this.siderbarSelect(menu.path)
    },

    /**
     * 通过 id 查询项目详情
     * @param {string|number} projectId
     * @param {boolean} refresh 是否刷新缓存
     * @returns {Promise<unknown>}
     */
    getProjectById(projectId, refresh = false) {
      return new Promise((resolve, reject) => {
        if (!projectId) return reject()

        if (
          !refresh &&
          this.projectDetail &&
          this.projectDetail.projectId === projectId
        ) {
          this.projectDetail = {
            projectId: projectId,
            name: '测试项目开发',
            description: '我是项目的描述文字我是项目的描述文字我是项目的描述文字我是项目的描述文字'
          }
          return resolve()
        }

        // TODO 通过 id 查询项目详情
        this.projectDetail = {
          projectId: projectId,
          name: '测试项目开发',
          description: '我是项目的描述文字我是项目的描述文字我是项目的描述文字我是项目的描述文字'
        }
        resolve()
      })
    },

    backToProject() {
      this.$router.push(`/project/subs/overview/${this.$route.params.setting}`)
    },

    goSetting() {
      this.$router.push(`/project/subs/project-member/${this.$route.params.id}`)
    }
  }
}
</script>

<style lang="scss" scoped>
@import "./components/Sidebar/variables.scss";
@import "./styles/common.scss";
@import "./components/style.scss";

// 公共新建按钮
.common-create-btn {
  margin-left: #{$--interval-base * 2};
}

.aside-container {
  position: relative;
  box-sizing: border-box;
  background-color: #2d3037;
}

.nav-container {
  z-index: 100;
  min-width: 1200px;
  padding: 0;
  box-shadow: 0 1px 2px #dbdbdb;
}

#project-container {
  position: relative;
  max-height: 100%;
}

.el-container {
  height: 100%;
  overflow: hidden;
}

.setting {
  display: flex;
  align-items: center;
  justify-content: flex-start;
  padding: 0 16px;
  line-height: 60px;
  white-space: nowrap;
  border-top: 2px solid $--border-color-base;

  &:hover {
    color: $--color-primary;
    cursor: pointer;
  }
}

/deep/ .wk-side-menu {
  .wk-side__footer {
    height: 80px;

    &::before {
      height: 0;
    }
  }
}

.back-main {
  cursor: pointer;
}

.project-icon {
  width: 32px;
  height: 32px;
  background-color: #f4f5f7;
  border: 1px solid #e2e4e9;
  border-radius: 3px;
}
</style>
