package com.kakarote.work.common.project;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author JiaS
 * @date 2020/11/6
 */
@Data
@ApiModel("项目统计参数")
public class ProjectCountBO {

    @ApiModelProperty("项目ID")
    private Long projectId;

    @ApiModelProperty("收藏数")
    private Integer collCount;

    @ApiModelProperty("未开始任务数量")
    private Integer taskOneCount;

    @ApiModelProperty("进行中任务数量")
    private Integer taskTwoCount;

    @ApiModelProperty("已完成任务数量")
    private Integer taskThreeCount;


}
