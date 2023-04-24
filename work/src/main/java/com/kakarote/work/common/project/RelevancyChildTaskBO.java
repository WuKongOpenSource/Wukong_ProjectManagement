package com.kakarote.work.common.project;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
@Data
@ApiModel("绑定子任务")
public class RelevancyChildTaskBO {
    @ApiModelProperty("字任务id(需求，子任务)")
    List<Long> childIds;
    @ApiModelProperty("父任务id")
    Long parentId;
}
