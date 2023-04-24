package com.kakarote.work.entity.BO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value = "Project对象", description = "项目表")
public class ProjectTaskWikiBO {
    @ApiModelProperty(value = "任务id")
    private Long taskId;
    @ApiModelProperty(value = "逗号隔开")
    private String wiki;
}
