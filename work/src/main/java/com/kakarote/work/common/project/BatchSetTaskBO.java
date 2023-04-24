package com.kakarote.work.common.project;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;
@Data
@ApiModel("批量设置任务")
public class BatchSetTaskBO {
    List<Long> taskIds;
    @ApiModelProperty(value = "优先级 从大到小 3高 2中 1低 0无")
    private Integer priority;
    @ApiModelProperty(value = "负责人ID")
    private Long mainUserId;
    @ApiModelProperty(value = "看板状态id")
    private Long boardStatusId;
    @ApiModelProperty(value = "结束时间")
    private Date stopTime;
}
