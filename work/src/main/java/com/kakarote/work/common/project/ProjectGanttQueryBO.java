package com.kakarote.work.common.project;

import com.kakarote.common.result.PageEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@ApiModel("项目事项工时BO")
public class ProjectGanttQueryBO extends PageEntity {


    @ApiModelProperty(value = "项目ID")
    private Long projectId;
    @ApiModelProperty(value = "搜索条件")
    private String search;
    private LocalDateTime beginTime;
    private LocalDateTime endTime;


}
