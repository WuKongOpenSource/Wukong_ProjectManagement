package com.kakarote.work.common.project;

import com.kakarote.common.result.PageEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author JiaS
 * @date 2020/11/6
 */
@Data
@ApiModel("项目任务查询参数")
public class ProjectUserTaskQueryBO extends PageEntity {

    @ApiModelProperty("项目ID")
    private Long projectId;

    @ApiModelProperty("开始时间")
    private String startTime;

    @ApiModelProperty("结束时间")
    private String endTime;

    @ApiModelProperty("类型")
    private Integer type;

    @ApiModelProperty("任务名称")
    private String name;
    @ApiModelProperty("负责人")
    private Long mainUserId;

}
