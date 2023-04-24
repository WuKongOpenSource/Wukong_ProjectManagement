package com.kakarote.work.common.project;

import io.swagger.annotations.ApiModelProperty;

public class BatchSetTaskQuery {
    @ApiModelProperty(value = "优先级 从大到小 3高 2中 1低 0无")
    private Integer priority;
}
