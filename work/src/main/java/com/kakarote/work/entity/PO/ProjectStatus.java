package com.kakarote.work.entity.PO;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 项目管理：状态表
 * </p>
 *
 * @author guole
 * @since 2022-09-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wk_project_status")
@ApiModel(value = "ProjectStatus对象", description = "项目管理：状态表")
public class ProjectStatus implements Serializable {

    private static final long serialVersionUID = 1L;
    @TableId(value = "project_status_id", type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "状态id")
    private Long projectStatusId;

    @ApiModelProperty(value = "状态名称")
    private String statusName;

    @ApiModelProperty(value = "描述")
    private String description;
    @ApiModelProperty(value = "状态类型 1 未完成 2进行中 3已完成")
    private Integer statusType;
    @ApiModelProperty(value = "排序 从小到大")
    private Long sorting;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @ApiModelProperty(value = "修改时间")
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;

    @ApiModelProperty(value = "修改人ID")
    private Long updateUserId;

    @ApiModelProperty(value = "1 系统 2自定义")
    private Integer sysType;

    @ApiModelProperty(value = "创建人ID")
    @TableField(fill = FieldFill.INSERT)
    private Long createUserId;

}
