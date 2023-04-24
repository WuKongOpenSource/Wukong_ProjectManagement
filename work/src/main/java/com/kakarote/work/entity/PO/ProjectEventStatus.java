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
 * 事件属性表
 * </p>
 *
 * @author bai
 * @since 2022-09-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("wk_project_event_status")
@ApiModel(value = "ProjectEventStatus对象", description = "事件属性表")
public class ProjectEventStatus implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键ID")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;
    @TableField(exist = false)
    @ApiModelProperty(value = "状态名称")
    private String statusName;
    @ApiModelProperty(value = "状态id")
    private Long projectStatusId;
    @ApiModelProperty(value = "项目事件id")
    private Long projectEventId;
    @ApiModelProperty(value = "1 系统 2自定义")
    @TableField(exist = false)
    private Integer sysType;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @ApiModelProperty(value = "对应主键batchId")
    private String batchId;

    @ApiModelProperty(value = "创建人ID")
    @TableField(fill = FieldFill.INSERT)
    private Long createUserId;

    @ApiModelProperty(value = "更新时间")
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;

    @ApiModelProperty(value = "修改人ID")
    private Long updateUserId;
    @ApiModelProperty(value = "状态类型 1 未完成 2进行中 3已完成")
    @TableField(exist = false)
    private Integer statusType;


    @ApiModelProperty(value = "应用状态 0 未应用新增 1已应用 2未应用删除")
    private Integer useStatus;

    @ApiModelProperty(value = "是否初始状态(0不是 1是)")
    private Integer initStatus;
    @ApiModelProperty(value = "排序 从小到大")
    private Long sorting;

    @ApiModelProperty(value = "排序 从小到大")
    private Long projectId;

}
