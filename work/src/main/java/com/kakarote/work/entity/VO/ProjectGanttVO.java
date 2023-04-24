package com.kakarote.work.entity.VO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "ProjectTaskTimeListVO对象", description = "迭代甘特图")
public class ProjectGanttVO {


    @ApiModelProperty(value = "迭代主键ID")
    private Long taskId;

    @ApiModelProperty(value = "迭代名称")
    private String name;

    @ApiModelProperty(value = "开始时间")
    private Date startTime;

    @ApiModelProperty(value = "结束时间")
    private Date stopTime;
//    @ApiModelProperty(value = "总计")
//    private Integer totalTime;

    @ApiModelProperty(value = "事项集合")
    private List<ProjectGanttTaskVO> children;
}
