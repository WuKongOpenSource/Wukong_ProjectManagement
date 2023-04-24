import { isObject } from '@/utils/types'
export const itemTypeMap = {
  All: '',
  Planning: 0,
  Iterations: 0,
  Require: 2,
  Task: 3,
  Defects: 4
}

export const priorityList = [
  { label: '高', key: '3', icon: getPriorityIcon(3) },
  { label: '中', key: '2', icon: getPriorityIcon(2) },
  { label: '低', key: '1', icon: getPriorityIcon(1) },
  { label: '无', key: '0', icon: getPriorityIcon(0) }
]

export function getPriorityIcon(type) {
  return [
    require('@/assets/img/pm/no.png'),
    require('@/assets/img/pm/lower.png'),
    require('@/assets/img/pm/middle.png'),
    require('@/assets/img/pm/heigh.png')
  ][type]
}

export function convertPriority(type) {
  return ['无', '低', '中', '高'][type]
}

export function getItemImg(data) {
  let indexType = ''
  if (isObject(data)) {
    indexType = data.pid ? 'sub' : data.type
  } else {
    indexType = data
  }
  return {
    2: require('@/assets/img/pm/require.png'),
    3: require('@/assets/img/pm/task.png'),
    4: require('@/assets/img/pm/problem.png'),
    sub: require('@/assets/img/pm/child.png')
  }[indexType]
}

export const handleAuth = ({
  // coordination
  deletMatters,
  deleteIteration,
  editIteration,
  editMatters,
  read,

  // projectAnnouncement
  deleteAnnouncement,
  editAnnouncement,
  viewAnnouncement,

  // projectDescription
  editDescription,
  viewDescription,

  // set
  editCoordination,
  editProjectInfo,
  memberManage,
  memberPermissionsConfig,
  synergyConfig,

  set,
  projectAnnouncement,
  project,
  coordination,
  projectDescription
}) => ({
  coordination: {
    deletMatters,
    deleteIteration,
    editIteration,
    editMatters,
    read
  },
  projectAnnouncement: {
    deleteAnnouncement,
    editAnnouncement,
    viewAnnouncement
  },
  projectDescription: {
    editDescription,
    viewDescription
  },
  set: {
    editCoordination,
    editProjectInfo,
    memberManage,
    memberPermissionsConfig,
    synergyConfig
  }
})
