package com.kakarote.work.entity.BO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author wyq
 */
@Data
@ApiModel("移动任务列表参数")
public class UpdateClassOrderBO {
    @ApiModelProperty("项目成员")
    private Long workId;

    @ApiModelProperty("任务列表id数组")
    private List<Long> classIds;
}
