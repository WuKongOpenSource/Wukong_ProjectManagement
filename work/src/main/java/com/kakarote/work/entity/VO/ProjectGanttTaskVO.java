package com.kakarote.work.entity.VO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "ProjectGanttTaskVO", description = "甘特图事项")
public class ProjectGanttTaskVO {
    @ApiModelProperty(value = "事项id")
    private String taskId;

    @ApiModelProperty(value = "事项名称")
    private String name;

    @ApiModelProperty(value = "开始时间")
    private Date startTime;

    @ApiModelProperty(value = "结束时间")
    private Date stopTime;
    @ApiModelProperty(value = "事项类型")
    private Integer type;
}
