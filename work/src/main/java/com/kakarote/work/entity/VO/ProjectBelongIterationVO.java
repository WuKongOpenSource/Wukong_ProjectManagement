package com.kakarote.work.entity.VO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "迭代详情", description = "迭代详情")
public class ProjectBelongIterationVO {
    @ApiModelProperty(value = "完成状态 1未开始 2进行中 3已完成")
    private Integer status;
    @ApiModelProperty(value = "迭代剩余天数")
    private Long surplusDay;
    @ApiModelProperty(value = "迭代id")
    private Long taskId;
    @ApiModelProperty(value = "迭代名称")
    private String name;

    @ApiModelProperty(value = "开始时间")
    private Date startTime;

    @ApiModelProperty(value = "结束时间")
    private Date stopTime;
    @ApiModelProperty(value = "完成时间")
    private LocalDateTime finishTime;
    @ApiModelProperty(value = "迭代目标")
    private String target;

    @ApiModelProperty(value = "描述")
    private String description;
    @ApiModelProperty(value = "负责人名称")
    private String mainUserName;

    @ApiModelProperty(value = "负责人ID")
    private Long mainUserId;

}
