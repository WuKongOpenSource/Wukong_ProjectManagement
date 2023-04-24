<template>
  <el-dialog
    :loading="loading"
    :title="title"
    :visible.sync="visible"
    append-to-body
    width="700px"
    :before-close="close">
    <div class="el-dialog-div">
      <el-form ref="formData" :model="formData" :rules="rules" class="demo-ruleForm">
        <el-form-item label="公告内容" prop="content">
          <el-input v-model="formData.content" type="textarea" :rows="8" />
        </el-form-item>
      </el-form>
    </div>
    <span slot="footer" class="dialog-footer">
      <el-button @click="close">取消</el-button>
      <el-button type="primary" @click="onSubmit">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
import {
  addProjectAnnouncementAPI,
  setProjectAnnouncementAPI
} from '@/api/pm/notice'

export default {
  name: 'CreateNotice',
  components: {
  },
  props: {
    action: {
      type: Object,
      default: () => {
        return {
          type: 'create'
        }
      }
    },
    projectId: {
      type: [String, Number],
      required: true
    },
    visible: Boolean
  },

  data() {
    return {
      formData: {
        content: ''
      },
      rules: {
        content: [
          { required: true, message: '公告正文不能为空', trigger: 'blur' },
          { max: 1000, message: '最长不超过1000个字符', trigger: 'change' }
        ]
      },
      loading: false
    }
  },

  computed: {
    title() {
      return this.action.type == 'create' ? '新建公告' : '编辑公告'
    }
  },

  created() {
    if (this.action.type == 'edit') {
      const actionData = this.action.data
      this.formData = {
        // title: actionData.title,
        content: actionData.content
      }
    }
  },

  methods: {
    /**
     * @description: 保存
     * @return {*}
     */
    onSubmit() {
      this.$refs.formData.validate(valid => {
        if (valid) {
          this.loading = true
          const params = {
            projectId: this.projectId,
            content: this.formData.content
          }
          if (this.action.type == 'edit') {
            params.projectAnnouncementId = this.action.data.projectAnnouncementId
          }
          const request = {
            'create': addProjectAnnouncementAPI,
            'edit': setProjectAnnouncementAPI
          }[this.action.type]
          request(params)
            .then(res => {
              this.$message.success(`${this.title}成功`)
              this.$emit('onSubmit')
              this.close()
              this.loading = false
            })
            .catch(() => {
              this.$message.error(`${this.title}失败`)
              this.loading = false
            })
        } else {
          return false
        }
      })
    },

    /**
     * @description: 关闭
     * @return {*}
     */
    close() {
      this.$emit('close')
    }
  }
}
</script>

<style scoped lang="scss">
.el-dialog-div {
  height: 260px;
}

</style>
