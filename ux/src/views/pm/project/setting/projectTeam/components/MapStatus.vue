<template>
  <div class="">
    <el-dialog
      v-if="dialogVisible"
      ref="wkDialog"
      :append-to-body="true"
      :close-on-click-modal="false"
      :visible.sync="dialogVisible"
      title="迁移事项的状态"
      width="500px"
      custom-class="no-padding-dialog"
      @close="handleClose">
      <div class="main">
        <div class="demo-rule-form">
          <div
            v-for="(item, index) in fieldList"
            :key="index"
            class="table-wrap">
            <template v-if="item.type == 'title'">
              <div class="row-wrap">
                <div class="item-left" style="padding: 8px 16px;">{{ item.old }}</div>
                <div class="item-right" style="padding: 8px 16px;">{{ item.new }}</div>
              </div>
            </template>
            <template v-else-if="item.type == 'select'">
              <div class="row-wrap">
                <div class="item-left">
                  <status-tag
                    :type="item.old.statusType"
                    :status-name="item.old.statusName" />
                </div>
                <div class="item-right">
                  <el-dropdown
                    class="dropdown-status-btn"
                    @command="handleSelectStatus($event, index)">
                    <div>
                      <status-tag
                        v-if="item.new"
                        :type="item.new.statusType"
                        :status-name="item.new.statusName" />
                      <span v-else style="color: #b8c0cb;">请选择事项状态</span>
                      <i class="el-icon-arrow-down el-icon--right" />
                    </div>
                    <el-dropdown-menu slot="dropdown">
                      <el-dropdown-item
                        v-for="(opt, optIndex) in options"
                        :key="optIndex"
                        :command="opt">
                        <span style="padding: 5px;">
                          <status-tag :type="opt.statusType" :status-name="opt.statusName" />
                        </span>
                      </el-dropdown-item>
                    </el-dropdown-menu>
                  </el-dropdown>
                </div>
              </div>
            </template>
          </div>
        </div>
        <div slot="footer" class="btn-wrap">
          <el-button
            v-debounce="handleConfirm"
            type="primary">保存</el-button>
          <el-button @click.native="handleClose">取消</el-button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  moveStatusAPI
} from '@/api/pm/setting'

import StatusTag from '@/views/pm/project/components/StatusTag'
import ElDialogLoadingMixin from '@/mixins/ElDialogLoading'
import { mapGetters } from 'vuex'

export default {
  name: 'MapStatus',
  components: {
    StatusTag
  },
  mixins: [ElDialogLoadingMixin],
  props: {
    visible: {
      type: Boolean,
      required: true,
      default: false
    },
    allList: {
      type: Array,
      default() {
        return []
      }
    },
    eventId: [String, Number]
  },
  data() {
    return {
      dialogVisible: false,
      loading: false,
      fieldList: [
        { type: 'title', old: '即将删除的状态', new: '请选择事项状态' }
      ],

      options: []

    }
  },
  computed: {
    ...mapGetters(['userInfo'])

  },
  watch: {
    visible: {
      handler(val) {
        if (val) {
          this.options = this.allList.filter(item => [0, 1].includes(item.useStatus))
          const needMapList = this.allList.filter(item => item.useStatus == 2)

          needMapList.forEach((item, index) => {
            this.fieldList.push({
              type: 'select', old: item, new: ''
            })
          })
        }
        this.dialogVisible = val
      },
      immediate: true
    }
  },
  methods: {
    /**
     * 关闭
     */
    handleClose() {
      this.dialogVisible = false
      this.$emit('update:visible', false)
      this.$emit('close')
    },

    handleSelectStatus(data, index) {
      this.$set(this.fieldList[index], 'new', data)
    },

    handleConfirm() {
      const params = {
        eventId: this.eventId,
        sourceStatus: [],
        transferStatus: []
      }

      // eslint-disable-next-line
      for (const item of this.fieldList.slice(1)) {
        if (!item.new) {
          return this.$message.error('请选择迁移状态')
        } else {
          params.sourceStatus.push(item.old.id)
          params.transferStatus.push(item.new.id)
        }
      }

      this.loading = true
      moveStatusAPI(params).then(res => {
        this.$message.success('迁移成功')
        this.$emit('save-success')
        this.handleClose()
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    }
  }
}
</script>
<style lang='scss' scoped>
.main {
  padding: 0 20px 20px;

  .demo-rule-form {
    display: flex;
    flex-direction: column;
    align-items: flex-start;
    justify-content: flex-start;
    width: 100%;
    border-radius: 3px;
    box-shadow: 0 0 0 0.5px rgba(0, 0, 0, 0.08), 0 1px 3px 0 rgba(0, 0, 0, 0.05);

    .table-wrap {
      width: 100%;
      border-bottom: 1px solid rgba(0, 0, 0, 0.04);

      &:last-child {
        border-bottom: none;
      }

      .row-wrap {
        display: flex;
        align-items: center;
        justify-content: flex-start;

        .item-left {
          width: 50%;
          padding: 12px 16px;
          border-right: 1px solid rgba(0, 0, 0, 0.04);
        }

        .item-right {
          width: 50%;
          padding: 12px 16px;
        }
      }
    }
  }
}

.btn-wrap {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  margin-top: 20px;
}

</style>
