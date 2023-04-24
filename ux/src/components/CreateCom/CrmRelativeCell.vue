<template>
  <wk-tags-view
    class="crm-relative-cell"
    :selected="dataValue"
    :visible="showSelectView"
    :disabled="disabled"
    :placeholder="placeholder"
    :collapse-tags="collapseTags"
    :value-key="getShowNameKeys()"
    @remove-tag="removeTag"
    @click.native="contentClick">
    <crm-relative
      v-if="!disabled&&viewLoaded"
      ref="crmrelative"
      :visible.sync="showSelectView"
      :crm-type="crmType"
      :radio="radio"
      :action="relationAction"
      :selected-data="selectedData"
      @changeCheckout="checkInfos" />
  </wk-tags-view>
</template>
<script type="text/javascript">
import WkTagsView from '@/components/NewCom/WkTagsView'

import ArrayMixin from './ArrayMixin'

export default {
  name: 'CrmRelativeCell', // 相关模块CRMCell 单类型 自定义字段用
  components: {
    WkTagsView,
    CrmRelative: () =>
      import('./CrmRelative')
  },
  mixins: [ArrayMixin],
  props: {
    relation: {
      // 相关ID
      type: Object,
      default: () => {
        return {}
      }
    },
    collapseTags: Boolean,
    // 多选框 只能选一个
    radio: {
      type: Boolean,
      default: true
    },
    placeholder: {
      type: String,
      default() {
        return '请选择'
      }
    },
    relativeType: String
  },
  data() {
    return {
      showSelectView: false, // 内容
      viewLoaded: false,
      relationAction: { type: 'default' }
    }
  },
  computed: {
    // 如果有相关ID  展示相关效果 例如客户下的商机和合同
    isRelationShow() {
      return this.item && this.item.data && this.item.data.relation_id
    },
    selectedData() {
      const crmObj = {}
      crmObj[this.crmType] = this.dataValue
      return crmObj
    },
    crmType() {
      if (this.relativeType) {
        return this.relativeType
      }
      return this.item.data.formType
    }
  },
  watch: {
    relation: function(val) {
      if (val.moduleType) {
        this.relationAction = { type: 'condition', data: val }
      } else {
        this.relationAction = { type: 'default' }
      }
    }
  },
  mounted() {
    if (this.relation && this.relation.moduleType) {
      this.relationAction = { type: 'condition', data: this.relation }
    } else {
      this.relationAction = { type: 'default' }
    }
  },
  methods: {
    /**
     * 选中
     */
    checkInfos(data) {
      this.dataValue = data.data ? data.data : []
      this.$emit('value-change', {
        index: this.index,
        value: data.data
      })
    },

    /**
     * 删除
     */
    removeTag(index) {
      if (this.disabled) return
      if (this.radio && this.$refs.crmrelative) {
        // 如果单选告知删除
        this.$refs.crmrelative.setSelections([])
      }
      if (this.dataValue.length === 1) {
        this.dataValue = []
      } else {
        this.dataValue.splice(index, 1)
      }

      this.$emit('value-change', {
        index: this.index,
        value: this.dataValue
      })
    },

    contentClick() {
      if (this.disabled) return
      this.viewLoaded = true
      this.showSelectView = true
    },

    getShowNameKeys() {
      const keys = []
      if (this.crmType === 'receivables') {
        keys.push('number')
      } else if (this.crmType === 'leads') {
        keys.push('leadsName')
      } else if (this.crmType === 'customer') {
        keys.push('customerName')
      } else if (this.crmType === 'business') {
        keys.push('businessName')
      } else if (this.crmType === 'contract') {
        keys.push('contractNum')
        keys.push('num')
      } else if (this.crmType === 'contacts') {
        keys.push('contactsName')
      }
      keys.push('name')
      return keys
    }
  }
}
</script>
<style lang="scss" scoped>
.crm-relative-cell {
  width: 100%;
}
</style>
