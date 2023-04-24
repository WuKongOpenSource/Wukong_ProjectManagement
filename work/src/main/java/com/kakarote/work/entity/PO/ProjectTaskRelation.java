package com.kakarote.work.entity.PO;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * <p>
 * 任务关联业务表
 * </p>
 *
 * @author zhangzhiwei
 * @since 2020-06-29
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("wk_project_task_relation")
@ApiModel(value = "ProjectTaskRelation对象", description = "任务关联业务表")
public class ProjectTaskRelation implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "任务ID")
    private Long taskId;

    @ApiModelProperty(value = "关联id")
    private Long relationId;

    @ApiModelProperty(value = "类型 1客户id 2联系人id 3商机id 4合同id 5回款ID")
    private Integer type;

    @ApiModelProperty(value = "状态1可用")
    private Integer status;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @ApiModelProperty(value = "创建人ID")
    @TableField(fill = FieldFill.INSERT)
    private Long createUserId;

    @ApiModelProperty(value = "修改人ID")
    @TableField(fill = FieldFill.UPDATE)
    private Long updateUserId;

    @ApiModelProperty(value = "更新时间")
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;


    @ApiModelProperty(value = "客户IDs")
    @TableField(exist = false)
    private String customerIds;

    @ApiModelProperty(value = "联系人IDs")
    @TableField(exist = false)
    private String contactsIds;

    @ApiModelProperty(value = "商机IDs")
    @TableField(exist = false)
    private String businessIds;

    @ApiModelProperty(value = "合同IDs")
    @TableField(exist = false)
    private String contractIds;

    @ApiModelProperty(value = "回款ids")
    @TableField(exist = false)
    private String receivablesIds;

    @ApiModelProperty(value = "任务名称")
    @TableField(exist = false)
    private String name;

    @ApiModelProperty(value = "负责人")
    @TableField(exist = false)
    private Long ownerUserId;

    @ApiModelProperty(value = "开始时间")
    @TableField(exist = false)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startTime;


    @ApiModelProperty(value = "结束时间")
    @TableField(exist = false)
    private LocalDate endTime;

    @ApiModelProperty(value = "1新增或更新 2删除")
    @TableField(exist = false)
    private Integer flag;


}
