import { isEmpty } from '@/utils/types'

export default {
  methods: {
    /**
     * 审批方式
     * @param {*} data
     */
    getWkWayShowStatus(data) {
      if (!data) {
        return false
      }
      return (data.examineType === 1 && data.userList.length > 1) ||
          (data.examineType === 3 && data.roleId) ||
          (data.examineType === 4 &&
          data.chooseType === 2 &&
          (data.rangeType === 1 ||
          (data.rangeType === 2 && data.userList.length > 1) ||
          (data.rangeType === 3 && data.roleId)
          ))
    },

    /**
     * 获取error状态
     */
    getWkNodeErrorStatus(data) {
      if (data.examineType === 1) {
        return data.userList.length === 0
      } else if (data.examineType === 2) {
        return false
      } else if (data.examineType === 3) {
        return isEmpty(data.roleId)
      } else if (data.examineType === 4) {
        if (data.rangeType === 2) {
          return data.userList.length === 0
        } else if (data.rangeType === 3) {
          return isEmpty(data.roleId)
        }
        return false
      } else if (data.examineType === 5) {
        if (data.type === 1) {
          return isEmpty(data.roleId)
        } else if (data.type === 2) {
          return isEmpty(data.tempParentLevel)
        }
        return false
      } else if (data.examineType === 7) {
        if (data.userList.length || data.parentLevelList.length || data.roleIdList.length) {
          return false
        }
        return true
      }
    }
  }
}
