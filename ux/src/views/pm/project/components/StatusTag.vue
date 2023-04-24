<template>
  <div class="status-tag">
    <span class="status-name" :style="getStyle">{{ name }}</span>
  </div>
</template>

<script>
// 1未开始 2进行中 3已完成
export default {
  name: 'StatusTag', // 公用tag组件
  props: {
    type: [String, Number],
    statusName: String
  },
  data() {
    return {
      group: [
        '',
        { color: '#0052CC', 'background-color': '#DEEBFF' },
        { color: '#EC8205', 'background-color': '#FFEACF' },
        { color: '#006644', 'background-color': '#E3FCEF' }
      ]
    }
  },
  computed: {
    convertType() {
      return Number(this.type)
    },
    name() {
      if (this.statusName) {
        return this.statusName
      } else {
        return ['', '未开始', '进行中', '已完成'][this.convertType]
      }
    },
    getStyle() {
      return this.group[this.convertType]
    }
  },
  methods: {

  }
}
</script>
<style lang='scss' scoped>
.status-tag {
  display: inline-block;
}

.status-name {
  padding: 2px 8px;
  font-size: 12px;
  border-radius: 2px;
}
</style>
