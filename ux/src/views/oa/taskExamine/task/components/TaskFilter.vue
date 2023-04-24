<template>
  <div class="task-filter">
    <div class="task-filter__header">
      <span class="task-filter__title">筛选</span>
    </div>
    <div class="task-filter__body">
      <el-form ref="form" label-width="90px">
        <el-form-item label="负责人">
          <wk-user-dialog-select
            :value="users.map(item => item.userId)"
            class="handle-item-content"
            @change="userChage" />
        </el-form-item>
        <el-form-item label="截止时间">
          <el-date-picker
            ref="filterDatePicker"
            v-model="dueDateValue"
            v-elclickoutside="handleDateClose"
            type="date"
            placeholder="选择日期"
            value-format="yyyy-MM-dd" />
        </el-form-item>
        <el-form-item label="优先级">
          <el-select
            v-model="priorityValue">
            <el-option
              v-for="item in priorityOptions"
              :key="item.key"
              :label="item.label"
              :value="item.key" />
          </el-select>
        </el-form-item>
        <el-form-item label="显示已完成">
          <el-switch
            v-model="doneValue" />
        </el-form-item>
      </el-form>
    </div>
    <div class="task-filter__footer">
      <span
        slot="footer"
        class="dialog-footer">
        <el-button
          v-debounce="saveClick"
          type="primary">保存</el-button>
        <el-button @click.native="closeClick">取消</el-button>
      </span>
    </div>
  </div>
</template>

<script>
import WkUserDialogSelect from '@/components/NewCom/WkUserDialogSelect'

import { objDeepCopy } from '@/utils'

export default {
  // 任务筛选
  name: 'TaskFilter',
  components: {
    WkUserDialogSelect
  },
  props: {
    dueDate: String,
    priority: String,
    users: Array,
    done: Boolean
  },
  data() {
    return {
      dueDateValue: '',
      priorityValue: '',
      doneValue: false,
      usersList: [],
      // 优先级
      priorityOptions: [
        { label: '全部', key: '' },
        { label: '高', key: '3' },
        { label: '中', key: '2' },
        { label: '低', key: '1' },
        { label: '无', key: '0' }
      ]
    }
  },
  computed: {},
  watch: {},
  created() {
    this.dueDateValue = this.dueDate
    this.priorityValue = this.priority
    this.doneValue = this.done
    this.usersList = objDeepCopy(this.users)
  },

  beforeDestroy() {},
  methods: {
    closeClick() {
      this.$emit('close')
    },

    userChage(_, data) {
      this.usersList = data
    },

    saveClick() {
      this.$emit('save', this.dueDateValue, this.priorityValue, this.doneValue, this.usersList)
      this.$emit('close')
    },

    /**
     * datepicker 不关闭的问题
     */
    handleDateClose() {
      this.$refs.filterDatePicker.pickerVisible = false
    }
  }
}
</script>

<style lang="scss" scoped>
.task-filter {
  &__header {
    padding: 16px;
    padding-bottom: 8px;
  }

  &__title {
    font-size: 18px;
    font-weight: 600;
    line-height: 24px;
  }

  &__body {
    padding: 0 16px;
    word-break: break-all;

    .el-date-editor {
      width: 100%;
    }

    .el-form-item {
      margin-bottom: $--interval-base;
    }
  }

  &__footer {
    padding: 10px;
    text-align: right;
    background-color: #f7f8fa;
    border-top: $--border-base;
  }
}
</style>
