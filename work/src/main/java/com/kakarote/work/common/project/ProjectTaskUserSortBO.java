package com.kakarote.work.common.project;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author zyh
 */
@Data
@ApiModel("项目任务排序")
public class ProjectTaskUserSortBO {

    @ApiModelProperty("项目id")
    private Long projectId;
    @ApiModelProperty("任务排序")
    private List<Long> sortList;
//    @ApiModelProperty("任务排序")
//    private List<TaskUserSortBO> sortList;

}
