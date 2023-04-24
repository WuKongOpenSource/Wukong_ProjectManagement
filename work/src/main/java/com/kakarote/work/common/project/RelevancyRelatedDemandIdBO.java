package com.kakarote.work.common.project;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("关联需求")
public class RelevancyRelatedDemandIdBO {
    @ApiModelProperty("任务集合")
    List<Long> taskIds;
    @ApiModelProperty("需求id")
    Long relatedDemandId;
}
