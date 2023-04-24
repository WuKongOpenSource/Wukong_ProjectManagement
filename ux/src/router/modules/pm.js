import Layout from '@/views/layout/PmLayout'

const layout = function(meta = {}) {
  return {
    path: '/project',
    component: Layout,
    meta: {
      requiresAuth: true,
      permissions: ['project'],
      ...meta
    }
  }
}

export default [
  {
    ...layout(),
    children: [
      {
        path: 'workbench', // 工作台
        name: 'workbench',
        component: () => import('@/views/pm/workbench/index'),
        meta: {
          title: '工作台',
          icon: 'workbench'
        }
      }
    ]
  },
  {
    ...layout({
      title: '项目',
      icon: 'customer-line'
    }),
    children: [
      {
        path: 'subs/manage/participate', // 我参与的项目
        name: 'participate',
        component: () => import('@/views/pm/manage/participate'),
        meta: {
          title: '我参与的',
          icon: 'wk wk-icon-participate',
          first: true
        }
      },
      {
        path: 'subs/manage/owner', // 我管理的项目
        component: () => import('@/views/pm/manage/owner'),
        meta: {
          title: '我管理的',
          icon: 'wk wk-icon-manage',
          first: true
        }
      },
      {
        name: 'allProjects',
        path: 'subs/manage/all', // 全部项目
        component: () => import('@/views/pm/manage/all'),
        meta: {
          title: '全部项目',
          icon: 'wk wk-icon-project',
          first: true
        }
      },
      // 项目详情
      {
        name: 'projectOverview',
        path: 'subs/overview/:id', // 项目概况
        component: () => import('@/views/pm/project/overview'),
        meta: {
          pmAuth: 'projectDescription',
          title: '项目概况',
          icon: 'wk wk-icon-overview',
          isDetail: true
        }
      },
      {
        name: 'projectTeam',
        path: 'subs/team/:id', // 项目协同
        component: () => import('@/views/pm/project/team'),
        meta: {
          pmAuth: 'coordination',
          title: '项目协同',
          icon: 'wk wk-icon-synergy',
          isDetail: true
        }
      },
      {
        name: 'projectNotice',
        path: 'subs/notice/:id', // 项目公告
        component: () => import('@/views/pm/project/notice'),
        meta: {
          pmAuth: 'projectAnnouncement',
          title: '项目公告',
          icon: 'wk wk-icon-notice',
          isDetail: true
        }
      },
      // 项目设置
      {
        name: 'projectSetting',
        path: 'subs/project-member/:setting', // 项目与成员
        component: () => import('@/views/pm/project/setting/projectMember'),
        meta: {
          pmAuth: 'set',
          title: '项目与成员',
          icon: 'customer-line',
          isSetting: true
        }
      },
      {
        path: 'subs/project-team/:setting', // 项目协同
        component: () => import('@/views/pm/project/setting/projectTeam'),
        meta: {
          title: '项目协同',
          icon: 'wk wk-icon-synergy',
          isSetting: true
        }
      }

    ]
  },
  {
    ...layout(),
    children: [
      {
        path: 'archive', // 归档项目
        component: () => import('@/views/pm/archive/index'),
        meta: {
          title: '归档项目',
          icon: 'archive'
        }
      }
    ]
  },
  {
    ...layout(),
    children: [
      {
        path: 'recycle', // 回收站
        component: () => import('@/views/pm/recycle/index'),
        meta: {
          title: '回收站',
          icon: 'recycle-bin'
        }
      }
    ]
  },
  {
    ...layout({
      // permissions: ['manage', 'projectManger'],
      title: '项目管理',
      icon: 'icon-project'
    }, '/project/set'),
    alwaysShow: true,
    children: [{
      name: 'system-project',
      path: 'system-project', // 项目管理
      component: () => import('@/views/pm/projectSet'),
      meta: {
        title: '自定义角色设置',
        requiresAuth: true,
        // permissions: ['manage', 'projectManger', 'setRole']
      }
    }, {
      name: 'project-status',
      path: 'project-status',
      component: () => import('@/views/pm/projectSet/status'),
      meta: {
        title: '状态设置',
        requiresAuth: true,
        // permissions: ['manage', 'projectManger', 'setStatus']
      }
    }]
  }
]
