package com.kakarote.work.entity.BO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel("关联迭代")
public class RelevancyBelongIterationBO {
    @ApiModelProperty("任务集合")
    List<Long> taskIds;
    @ApiModelProperty("迭代id")
    Long belongIterationId;
}
