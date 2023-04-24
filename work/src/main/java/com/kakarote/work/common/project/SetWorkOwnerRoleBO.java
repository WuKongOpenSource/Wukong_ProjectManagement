package com.kakarote.work.common.project;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author wyq
 */
@Data
@ApiModel("设置项目成员角色参数")
public class SetWorkOwnerRoleBO {
    @ApiModelProperty("项目id")
    private Long workId;

//    @ApiModelProperty("成员角色列表")
//    private List<WorkUser> list;
}
