package com.kakarote.work.common.project;

import com.kakarote.common.result.PageEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel("项目事项跟进记录/用户动态BO")
public class ProjectTaskFollowsQueryBO extends PageEntity {

    @ApiModelProperty(value = "跟进类型 0 跟进记录 1 客户动态")
    private Integer followType;

    @ApiModelProperty(value = "项目事项主键ID")
    private String taskId;
    @ApiModelProperty(value = "父级评论id")
    private String pid;
}
