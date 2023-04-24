package com.kakarote.work.entity.PO;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 事件绑定属性表
 * </p>
 *
 * @author bai
 * @since 2022-09-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("wk_project_event_field")
@ApiModel(value = "ProjectEventField对象", description = "事件绑定属性表")
public class ProjectEventField implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键ID")
    private Long id;

    @ApiModelProperty(value = "方案ID")
    private Long schemeId;

    @ApiModelProperty(value = "事件ID")
    private Long eventId;

    @ApiModelProperty(value = "自定义字段ID")
    private Long fieId;

    @ApiModelProperty(value = "1系统 2自定义")
    private Integer sysType;

    @ApiModelProperty(value = "1需求 2任务 3缺陷 4子工作项目")
    private Integer type;

    @ApiModelProperty(value = "排序 从小到大")
    private Integer sorting;

    @ApiModelProperty(value = "创建人ID")
    @TableField(fill = FieldFill.INSERT)
    private Long createUserId;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty(value = "修改人ID")
    private Long updateUserId;

    @ApiModelProperty(value = "更新时间")
    @TableField(fill = FieldFill.UPDATE)
    private Date updateTime;

    @ApiModelProperty(value = "对应主键batchId")
    private String batchId;


}
