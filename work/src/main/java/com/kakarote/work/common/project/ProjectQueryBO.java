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
@ApiModel("项目查询参数")
public class ProjectQueryBO extends PageEntity {

    @ApiModelProperty("项目名称")
    private String name;

    @ApiModelProperty("开始时间")
    private String startTime;

    @ApiModelProperty("结束时间")
    private String endTime;

    @ApiModelProperty("分组ID")
    private Long groupId;

    @ApiModelProperty("排序方式 1访问时间 2创建时间")
    private Integer sortType;

    @ApiModelProperty("项目状态 2归档 3删除")
    private Integer setType;


}
