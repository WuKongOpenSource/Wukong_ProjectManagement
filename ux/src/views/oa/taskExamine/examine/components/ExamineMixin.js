export default {
  methods: {
    /**
     * 获取图标
     */
    getCategoryIcon(icon) {
      const temps = icon && typeof icon == 'string' ? icon.split(',') : []
      if (temps.length > 1) {
        return {
          icon: temps[0],
          color: temps[1]
        }
      } else {
        return {
          icon: 'wk wk-approve',
          color: '#9376FF'
        }
      }
    }
  }
}
