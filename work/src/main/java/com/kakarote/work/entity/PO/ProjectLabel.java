package com.kakarote.work.entity.PO;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 任务标签表
 * </p>
 *
 * @author bai
 * @since 2022-09-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("wk_project_label")
@ApiModel(value="ProjectLabel对象", description="任务标签表")
public class ProjectLabel implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键ID")
    @TableId(value = "label_id", type = IdType.ASSIGN_ID)
    private Long labelId;

    @ApiModelProperty(value = "标签名")
    private String name;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    @ApiModelProperty(value = "创建人ID")
    @TableField(fill = FieldFill.INSERT)
    private Long createUserId;

    @ApiModelProperty(value = "状态")
    private Integer status;

    @ApiModelProperty(value = "颜色")
    private String color;

    @ApiModelProperty(value = "更新时间")
    @TableField(fill = FieldFill.UPDATE)
    private Date updateTime;

    @ApiModelProperty(value = "修改人ID")
    private Long updateUserId;


}
