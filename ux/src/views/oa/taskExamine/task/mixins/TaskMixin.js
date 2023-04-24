// 任务用到公共样式方法
import XRTheme from '@/styles/xr-theme.scss'

export default {
  data() {
    return {
      // 优先级列表 详情 任务列表 用到了 index 3 元素
      priorityList: [
        { id: 3, label: '高', color: XRTheme.colorDanger },
        { id: 2, label: '中', color: XRTheme.colorWarning },
        { id: 1, label: '低', color: XRTheme.colorSuccess },
        { id: 0, label: '无', color: XRTheme.colorN40 }
      ]
    }
  },

  methods: {
    getPriorityColor(priority) {
      if (priority == 1) {
        return {
          color: XRTheme.colorSuccess,
          label: '低'
        }
      } else if (priority == 2) {
        return {
          color: XRTheme.colorWarning,
          label: '中'
        }
      } else if (priority == 3) {
        return {
          color: XRTheme.colorDanger,
          label: '高'
        }
      } else {
        return {
          color: XRTheme.colorN40,
          label: '无'
        }
      }
    }

  }
}
