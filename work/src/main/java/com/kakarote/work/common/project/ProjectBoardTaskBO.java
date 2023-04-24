package com.kakarote.work.common.project;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class ProjectBoardTaskBO {

    Long projectId;
    Integer taskType;

    String search;
    @ApiModelProperty("事项类型筛选")
    private List<Integer> typeQuery;
    @ApiModelProperty(value = "优先级筛选 从大到小 3高 2中 1低 0无")
    private List<Integer> priorityQuery;
    @ApiModelProperty(value = "状态筛选 1未开始 2进行中 3已完成")
    private List<Integer> statusQuery;
    @ApiModelProperty(value = "迭代筛选")
    private List<Long> belongIterationIdQuery;
    @ApiModelProperty(value = "标签筛选")
    private List<Long> labelQuery;
    @ApiModelProperty(value = "负责人")
    private List<Long> mainUserIdQuery;
}
