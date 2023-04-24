import { projectAuthAPI } from '@/api/pm/manage'
import { handleAuth } from '@/views/pm/data'

const pm = {
  state: {
    projectAuth: {},
    projectDetail: {}
  },

  mutations: {
    SET_PM_AUTH: (state, projectAuth) => {
      state.projectAuth = handleAuth(projectAuth || {})
    },
    SET_PROJECT_DETAIL: (state, projectDetail) => {
      state.projectDetail = projectDetail
    }
  },

  actions: {
    async GetPeojectAuth({
      commit,
      dispatch
    }, params) {
      try {
        const { id, isOpen, isMember } = params
        const res = await projectAuthAPI({ projectId: id })
        let data = res.data || {}
        if (isOpen && !isMember && !res.data) {
          data = {
            coordination: { read: true },
            projectAnnouncement: { viewAnnouncement: true },
            projectDescription: { viewDescription: true }
          }
        }
        data.loaded = true
        commit('SET_PM_AUTH', data)
        Promise.resolve(data)
      } catch (error) {
        commit('SET_PM_AUTH', {})
        Promise.reject(error)
      }
    }
  },

  getters: {
    projectAuth: state => state.projectAuth,
    projectDetail: state => state.projectDetail
  }
}

export default pm
