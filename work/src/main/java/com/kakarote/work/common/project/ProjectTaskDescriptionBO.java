package com.kakarote.work.common.project;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author wyq
 */
@Data
@ApiModel("任务描述参数")
public class ProjectTaskDescriptionBO {

    @ApiModelProperty("任务id")
    private Long taskId;

    @ApiModelProperty("任务描述")
    private String description;

}
