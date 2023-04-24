package com.kakarote.work.entity.BO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author wyq
 */
@Data
@ApiModel("任务移除标签参数")
public class ProjectTaskLabelBO {

    @ApiModelProperty("标签id")
    private Long labelId;

    @ApiModelProperty("任务id")
    private Long taskId;

    @ApiModelProperty("标签名称")
    private String labelName;

    @ApiModelProperty("颜色")
    private String color;
}
