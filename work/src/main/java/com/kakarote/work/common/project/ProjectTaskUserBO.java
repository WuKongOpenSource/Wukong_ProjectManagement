package com.kakarote.work.common.project;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author zyh
 */
@Data
@ApiModel("任务成员")
public class ProjectTaskUserBO {

    @ApiModelProperty("项目id")
    private Long projectId;

    @ApiModelProperty("任务id")
    private Long taskId;

    @ApiModelProperty("成员ids")
    private List<Long> userIds;

}
