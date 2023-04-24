package com.kakarote.work.entity.PO;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 任务日志表
 * </p>
 *
 * @author bai
 * @since 2022-09-14
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("wk_project_task_log")
@ApiModel(value = "ProjectTaskLog对象", description = "任务日志表")
public class ProjectTaskLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "项目日志表")
    @TableId(value = "log_id", type = IdType.AUTO)
    private Long logId;

    @ApiModelProperty(value = "操作人ID")
    private Long userId;

    @ApiModelProperty(value = "日志类型 1通用类型 2工时日志")
    private Integer type;

    @ApiModelProperty(value = "内容")
    private String content;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @ApiModelProperty(value = "状态 4删除")
    private Integer status;

    @ApiModelProperty(value = "任务ID")
    private Long taskId;

    @ApiModelProperty(value = "项目ID")
    private Long projectId;

    @ApiModelProperty(value = "创建人ID")
    @TableField(fill = FieldFill.INSERT)
    private Long createUserId;

    @ApiModelProperty(value = "更新时间")
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;

    @ApiModelProperty(value = "修改人ID")
    private Long updateUserId;


}
