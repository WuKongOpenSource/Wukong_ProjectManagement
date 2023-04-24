package com.kakarote.work.entity.PO;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * <p>
 * 项目事项工时表
 * </p>
 *
 * @author zhangyongjie
 * @since 2022-09-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("wk_project_task_time")
@ApiModel(value = "ProjectTaskTime对象", description = "项目事项工时表")
public class ProjectTaskTime implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "工时主键")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @ApiModelProperty(value = "项目事项ID")
    private Long taskId;

    @ApiModelProperty(value = "开始时间")
    private Date beginTime;

    @ApiModelProperty(value = "结束时间")
    private Date endTime;

    @ApiModelProperty(value = "批次")
    private String batchId;

    @ApiModelProperty(value = "预估工时")
    private Integer estimatedManHours;

    @ApiModelProperty(value = "实际工时")
    private Integer actualHour;
    @ApiModelProperty(value = "剩余工时")
    private Integer surplusHours;
    @ApiModelProperty(value = "描述")
    private String description;

    @ApiModelProperty(value = "创建人ID")
    @TableField(fill = FieldFill.INSERT)
    private Long createUserId;

    @ApiModelProperty(value = "新建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @ApiModelProperty(value = "修改人ID")
    @TableField(fill = FieldFill.UPDATE)
    private Long updateUserId;

    @ApiModelProperty(value = "项目成员")
    @TableField(fill = FieldFill.INSERT)
    private Long projectMember;
    @ApiModelProperty(value = "更新时间")
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;
}
