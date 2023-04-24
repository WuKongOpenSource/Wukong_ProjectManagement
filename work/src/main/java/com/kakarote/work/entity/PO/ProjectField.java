package com.kakarote.work.entity.PO;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 自定义字段表
 * </p>
 *
 * @author bai
 * @since 2022-09-16
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@TableName("wk_project_field")
@ApiModel(value = "ProjectField对象", description = "自定义字段表")
public class ProjectField implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键ID")
    @TableId(value = "field_id", type = IdType.ASSIGN_ID)
    private Long fieldId;

    @ApiModelProperty(value = "自定义字段英文标识")
    private String fieldName;

    @ApiModelProperty(value = "字段名称")
    private String name;

    @ApiModelProperty(value = "字段类型 1 单行文本 2 多行文本 3 单选 4日期 5 数字 6 小数 7 手机  8 文件 9 多选 10 人员 11 附件 12 部门 13 日期时间 14 邮箱 15客户 16 商机 17 联系人 18 地图 19 产品类型")
    private Integer type;

    @ApiModelProperty(value = "字段说明")
    private String remark;

    @ApiModelProperty(value = "输入提示")
    private String inputTips;

    @ApiModelProperty(value = "最大长度")
    private Integer maxLength;

    @ApiModelProperty(value = "默认值")
    private Object defaultValue;

    @ApiModelProperty(value = "是否唯一 1 是 0 否")
    private Integer isUnique;

    @ApiModelProperty(value = "是否必填 1 是 0 否")
    private Integer isNull;

    @ApiModelProperty(value = "排序 从小到大")
    private Integer sorting;

    @ApiModelProperty(value = "如果类型是选项，此处不能为空，多个选项以，隔开")
    private String options;

    @ApiModelProperty(value = "是否允许编辑")
    private Integer operating;

    @ApiModelProperty(value = "是否隐藏  0不隐藏 1隐藏")
    private Integer isHidden;

    @ApiModelProperty(value = "最后修改时间")
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;

    @ApiModelProperty(value = "字段来源  0.自定义 1.原始固定 2原始字段但值存在扩展表中")
    private Integer fieldType;

    @ApiModelProperty(value = "只有线索需要，转换客户的自定义字段ID")
    private Long relevant;

    @ApiModelProperty(value = "样式百分比%")
    private Integer stylePercent;

    @ApiModelProperty(value = "精度，允许的最大小数位")
    private Integer precisions;

    @ApiModelProperty(value = "表单定位 坐标格式： 1,1")
    private String formPosition;

    @ApiModelProperty(value = "限制的最大数值")
    private String maxNumRestrict;

    @ApiModelProperty(value = "限制的最小数值")
    private String minNumRestrict;

    @ApiModelProperty(value = "表单辅助id，前端生成")
    private Long formAssistId;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @ApiModelProperty(value = "创建人ID")
    @TableField(fill = FieldFill.INSERT)
    private Long createUserId;

    @ApiModelProperty(value = "修改人ID")
    private Long updateUserId;

    @TableField(exist = false)
    @ApiModelProperty(value = "设置列表")
    private List<Object> setting;

    @TableField(exist = false)
    @ApiModelProperty(value = "类型")
    private String formType;

    /**
     * 坐标
     */
    @TableField(exist = false)
    @ApiModelProperty(value = "x轴")
    @JsonIgnore
    private Integer xAxis;

    @TableField(exist = false)
    @ApiModelProperty(value = "y轴")
    @JsonIgnore
    private Integer yAxis;
    @TableField(exist = false)
    @ApiModelProperty(value = "逻辑表单数据")
    private Map<String, Object> optionsData;

    @ApiModelProperty(value = "语言包map")
    @TableField(exist = false)
    private Map<String, String> languageKeyMap;

    @TableField(exist = false)
    @ApiModelProperty(value = "扩展字段")
    private List<ProjectFieldExtend> fieldExtendList;

    public void setFormPosition(String formPosition) {
        this.formPosition = formPosition;
        if (StrUtil.isNotEmpty(formPosition)) {
            if (formPosition.contains(com.kakarote.work.common.project.Const.SEPARATOR)) {
                String[] axisArr = formPosition.split(com.kakarote.work.common.project.Const.SEPARATOR);
                int two = 2;
                if (axisArr.length == two) {
                    String regex = "[0-9]+";
                    if (axisArr[0].matches(regex) && axisArr[1].matches(regex)) {
                        this.xAxis = Integer.valueOf(axisArr[0]);
                        this.yAxis = Integer.valueOf(axisArr[1]);
                        return;
                    }
                }
            }
        }
        this.xAxis = -1;
        this.yAxis = -1;
    }
}
