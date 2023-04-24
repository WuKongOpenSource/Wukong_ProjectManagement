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
 * 事件表
 * </p>
 *
 * @author bai
 * @since 2022-09-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("wk_project_event")
@ApiModel(value = "ProjectEvent对象", description = "事件表")
public class ProjectEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键ID")
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @ApiModelProperty(value = "事件名称")
    private String name;

    @ApiModelProperty(value = "事件图标")
    private String icon;

    @ApiModelProperty(value = "事件属性描述")
    private String describes;

    @ApiModelProperty(value = "1系统 2自定义")
    private Integer sysType;

    @ApiModelProperty(value = "1需求 2任务 3缺陷 4子工作项目 0自定义")
    private Integer type;

    @ApiModelProperty(value = "工作流ID")
    private Long workflowId;

    @ApiModelProperty(value = "描述模板:自动填充当前事项类型创建时的描述内容")
    private String describeModel;

    @ApiModelProperty(value = "创建时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @ApiModelProperty(value = "创建人ID")
    @TableField(fill = FieldFill.INSERT)
    private Long createUserId;

    @ApiModelProperty(value = "更新时间")
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;

    @ApiModelProperty(value = "修改人ID")
    private Long updateUserId;
    @ApiModelProperty(value = "项目id")
    @TableField(exist = false)
    private Long projectId;
    @ApiModelProperty(value = "对应主键batchId")
    private String batchId;


}
