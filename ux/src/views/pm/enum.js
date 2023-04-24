/**
 * 通用枚举
 * @author yxk
 * @date 2022/9/7
 */

/**
 * 优先级
 * @type {*}
 */
export const PRIORITY_ENUM = [
  { label: '最高', value: 1, color: '#DE350B', bg: '#FFEBE6' },
  { label: '较高', value: 2, color: '#EC8205', bg: '#FFEACF' },
  { label: '普通', value: 3, color: '#006644', bg: '#E3FCEF' }
]

/**
 * 状态(进行中/已完成)
 * @type {*}
 */
export const STATUS_ENUM = [
  { label: '未开始', value: 0, color: '#1761d1', bg: '#deebff' },
  { label: '已完成', value: 1, color: '#006644', bg: '#E3FCEF' },
  { label: '进行中', value: 2, color: '#EC8205', bg: '#FFEACF' }
]
