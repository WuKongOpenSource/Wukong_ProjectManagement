package com.kakarote.work.entity.VO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "ProjectTaskTimeStatisticsVO对象", description = "项目事项工时VO")
public class ProjectTaskTimeStatisticsVO {
    @ApiModelProperty(value = "迭代id")
    private Long taskId;
    @ApiModelProperty(value = "迭代名称")
    private String name;
    @ApiModelProperty(value = "事项工时列表")
    private List<ProjectTaskTimeListVO> taskTimeListVOList;
}
